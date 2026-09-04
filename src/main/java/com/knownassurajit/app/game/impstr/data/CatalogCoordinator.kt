package com.knownassurajit.app.game.impstr.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

data class PersonalizationState(
    val countryCode: String? = null,
    val regionLabel: String? = null,
    val locationConsent: Boolean = false,
    val hasLocationPermission: Boolean = false,
    val showLocationPrompt: Boolean = false,
    val suggestedCategories: List<String> = emptyList(),
    val allCategories: List<String> = WordRepository.categoryNames(),
    val catalogStatus: CatalogStatus = CatalogStatus.Idle,
    val usingLocalWords: Boolean = false,
)

@Singleton
class CatalogCoordinator
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val store: PersonalizationStore,
        private val location: LocationPersonalization,
        private val network: CatalogNetworkClient,
        private val cache: CatalogCache,
    ) {
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        private val mutex = Mutex()

        private val _state = MutableStateFlow(PersonalizationState())
        val state: StateFlow<PersonalizationState> = _state.asStateFlow()

        fun warmUp() {
            cache.prune()
            scope.launch { refresh(forceNetwork = false) }
        }

        fun refresh(forceNetwork: Boolean = false) {
            scope.launch { refreshInternal(forceNetwork) }
        }

        fun onLocationPermissionResult(granted: Boolean) {
            scope.launch {
                store.setLocationConsent(granted)
                refreshInternal(forceNetwork = true)
            }
        }

        fun dismissLocationPrompt() {
            scope.launch {
                store.setPromptDismissed()
                _state.update { it.copy(showLocationPrompt = false) }
            }
        }

        private suspend fun refreshInternal(forceNetwork: Boolean) {
            mutex.withLock {
                val prefs = store.snapshot()
                val hasPermission = location.hasCoarsePermission()
                val consent = prefs.locationConsent && hasPermission
                _state.update {
                    it.copy(
                        catalogStatus = CatalogStatus.Updating,
                        locationConsent = consent,
                        hasLocationPermission = hasPermission,
                    )
                }

                val country = resolveCountry(prefs, consent)
                if (country != prefs.countryCode) {
                    store.setCountryCode(country)
                }

                var overlay = RegionalWordPacks.overlayFor(country)

                val bundled = loadBundledCatalog(country)
                if (bundled != null) overlay = overlay.merge(bundled)

                val cachedJson = cache.readIfFresh()
                if (cachedJson != null) {
                    runCatching { CatalogValidator.parse(cachedJson, country) }
                        .getOrNull()
                        ?.let { overlay = overlay.merge(it) }
                }

                val shouldFetch =
                    forceNetwork ||
                        cachedJson == null ||
                        System.currentTimeMillis() - prefs.lastSyncAt > CatalogCache.CACHE_TTL_MS

                var status = if (network.isOnline()) CatalogStatus.Updated else CatalogStatus.Offline
                if (shouldFetch && network.isOnline()) {
                    val remote = network.fetchCatalog()
                    if (remote != null) {
                        val parsed = runCatching { CatalogValidator.parse(remote, country) }.getOrNull()
                        if (parsed != null) {
                            cache.writeAtomic(remote)
                            store.setLastSyncAt(System.currentTimeMillis())
                            overlay = overlay.merge(parsed)
                            status = CatalogStatus.Updated
                        } else {
                            status = CatalogStatus.Failed
                        }
                    } else if (cachedJson == null && bundled == null) {
                        status = CatalogStatus.Failed
                    }
                }

                WordRepository.applyOverlay(overlay)
                val snapshot = WordRepository.snapshot.value
                _state.value =
                    PersonalizationState(
                        countryCode = country,
                        regionLabel = overlay.regionLabel ?: RegionalWordPacks.labelFor(country),
                        locationConsent = consent,
                        hasLocationPermission = hasPermission,
                        showLocationPrompt = !prefs.promptDismissed && !consent,
                        suggestedCategories = snapshot.overlay.suggestedCategories,
                        allCategories = snapshot.categoryNames(),
                        catalogStatus = status,
                        usingLocalWords = overlay.extraCategories.isNotEmpty() || overlay.bonusEasyWords.isNotEmpty(),
                    )
            }
        }

        private suspend fun resolveCountry(
            prefs: PersonalizationPrefs,
            consent: Boolean,
        ): String? {
            val cachedFresh =
                !prefs.countryCode.isNullOrBlank() &&
                    System.currentTimeMillis() - prefs.countryCachedAt < COUNTRY_TTL_MS
            if (cachedFresh && (!consent || prefs.locationConsent)) {
                return prefs.countryCode
            }
            return location.resolveCountry(allowLocation = consent)
        }

        private fun loadBundledCatalog(country: String?): WordOverlay? =
            runCatching {
                context.assets.open(BUNDLED_CATALOG).bufferedReader().use { it.readText() }
            }.mapCatching { CatalogValidator.parse(it, country) }.getOrNull()

        companion object {
            private const val BUNDLED_CATALOG = "word_catalog.json"
            private const val COUNTRY_TTL_MS = 7L * 24 * 60 * 60 * 1000
        }
    }
