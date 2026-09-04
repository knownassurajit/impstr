package com.knownassurajit.app.game.impstr.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.HttpsURLConnection

/**
 * Fetches the public word catalog over HTTPS only.
 * Hosts are allow-listed; redirects that leave the list are rejected.
 */
@Singleton
class CatalogNetworkClient
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        fun isOnline(): Boolean {
            val manager = context.getSystemService(ConnectivityManager::class.java) ?: return false
            val network = manager.activeNetwork ?: return false
            val caps = manager.getNetworkCapabilities(network) ?: return false
            return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }

        fun fetchCatalog(): String? {
            if (!isOnline()) return null
            for (raw in CATALOG_URLS) {
                val body = runCatching { fetchUrl(raw) }.getOrNull()
                if (!body.isNullOrBlank()) return body
            }
            return null
        }

        internal fun fetchUrl(rawUrl: String): String? {
            var current = rawUrl
            var hops = 0
            while (hops <= MAX_REDIRECTS) {
                val url = URL(current)
                if (!isAllowed(url)) return null
                val connection =
                    (url.openConnection() as? HttpsURLConnection) ?: return null
                connection.instanceFollowRedirects = false
                connection.connectTimeout = TIMEOUT_MS
                connection.readTimeout = TIMEOUT_MS
                connection.requestMethod = "GET"
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty("User-Agent", USER_AGENT)
                connection.useCaches = true
                try {
                    val code = connection.responseCode
                    if (code in 300..399) {
                        val next = connection.getHeaderField("Location") ?: return null
                        current = resolveRedirect(url, next) ?: return null
                        hops++
                        continue
                    }
                    if (code != HttpURLConnection.HTTP_OK) return null
                    val contentLength = connection.contentLengthLong
                    if (contentLength > CatalogValidator.MaxBytes) return null
                    return readBounded(connection)
                } finally {
                    connection.disconnect()
                }
            }
            return null
        }

        private fun readBounded(connection: HttpsURLConnection): String? {
            connection.inputStream.use { input ->
                val output = ByteArrayOutputStream()
                val buffer = ByteArray(4096)
                var total = 0
                while (true) {
                    val read = input.read(buffer)
                    if (read <= 0) break
                    total += read
                    if (total > CatalogValidator.MaxBytes) return null
                    output.write(buffer, 0, read)
                }
                return output.toString(Charsets.UTF_8.name())
            }
        }

        private fun resolveRedirect(
            from: URL,
            location: String,
        ): String? {
            val next = URL(from, location)
            return if (isAllowed(next)) next.toString() else null
        }

        private fun isAllowed(url: URL): Boolean {
            if (url.protocol != "https") return false
            val host = url.host.lowercase()
            return ALLOWED_HOSTS.any { allowed ->
                host == allowed || host.endsWith(".$allowed")
            }
        }

        companion object {
            private const val TIMEOUT_MS = 8_000
            private const val MAX_REDIRECTS = 2
            private const val USER_AGENT = "IMPSTR/1.1 (Android; catalog-sync)"
            private val ALLOWED_HOSTS =
                setOf(
                    "raw.githubusercontent.com",
                    "cdn.jsdelivr.net",
                )
            val CATALOG_URLS =
                listOf(
                    "https://raw.githubusercontent.com/knownassurajit/impstr/develop/catalog/words.json",
                    "https://raw.githubusercontent.com/knownassurajit/impstr/master/catalog/words.json",
                    "https://cdn.jsdelivr.net/gh/knownassurajit/impstr@develop/catalog/words.json",
                )
        }
    }
