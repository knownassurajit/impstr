package com.knownassurajit.app.game.impstr.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Resolves an ISO country code using approximate location when the player
 * has opted in. Coordinates are never written to disk.
 */
@Singleton
class LocationPersonalization
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        fun hasCoarsePermission(): Boolean =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

        fun localeCountry(): String? =
            Locale.getDefault().country.takeIf { it.length == 2 }?.uppercase()

        suspend fun resolveCountry(allowLocation: Boolean): String? {
            if (allowLocation && hasCoarsePermission()) {
                val fromLocation = countryFromLocation()
                if (!fromLocation.isNullOrBlank()) return fromLocation
            }
            return localeCountry()
        }

        @SuppressLint("MissingPermission")
        private suspend fun countryFromLocation(): String? =
            withContext(Dispatchers.IO) {
                if (!hasCoarsePermission()) return@withContext null
                val manager = context.getSystemService(LocationManager::class.java) ?: return@withContext null
                val cached =
                    listOf(LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)
                        .filter { manager.isProviderEnabled(it) }
                        .mapNotNull { provider ->
                            runCatching { manager.getLastKnownLocation(provider) }.getOrNull()
                        }
                        .maxByOrNull { it.time }

                val location =
                    cached
                        ?: withTimeoutOrNull(5_000) { requestSingleUpdate(manager) }
                        ?: return@withContext null

                countryFromCoordinates(location.latitude, location.longitude)
            }

        @SuppressLint("MissingPermission")
        private suspend fun requestSingleUpdate(manager: LocationManager): Location? {
            val provider =
                when {
                    manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
                    manager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER) -> LocationManager.PASSIVE_PROVIDER
                    else -> return null
                }
            return suspendCancellableCoroutine { continuation ->
                val resumed = AtomicBoolean(false)
                val listener =
                    object : android.location.LocationListener {
                        override fun onLocationChanged(location: Location) {
                            if (resumed.compareAndSet(false, true)) {
                                manager.removeUpdates(this)
                                continuation.resume(location)
                            }
                        }
                    }
                runCatching {
                    manager.requestLocationUpdates(provider, 0L, 0f, listener, Looper.getMainLooper())
                }.onFailure {
                    if (resumed.compareAndSet(false, true)) {
                        continuation.resume(null)
                    }
                    return@suspendCancellableCoroutine
                }
                continuation.invokeOnCancellation {
                    manager.removeUpdates(listener)
                }
            }
        }

        private suspend fun countryFromCoordinates(
            latitude: Double,
            longitude: Double,
        ): String? {
            if (!Geocoder.isPresent()) return null
            val geocoder = Geocoder(context, Locale.ENGLISH)
            return withTimeoutOrNull(3_000) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    suspendCancellableCoroutine { continuation ->
                        geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                            continuation.resume(addresses.firstOrNull()?.countryCode?.uppercase())
                        }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    runCatching {
                        geocoder.getFromLocation(latitude, longitude, 1)
                            ?.firstOrNull()
                            ?.countryCode
                            ?.uppercase()
                    }.getOrNull()
                }
            }
        }
    }
