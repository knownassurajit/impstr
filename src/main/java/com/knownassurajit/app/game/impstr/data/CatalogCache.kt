package com.knownassurajit.app.game.impstr.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Caches a validated catalog JSON in app cache, with size and age limits.
 */
@Singleton
class CatalogCache
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val catalogDir: File
            get() = File(context.cacheDir, "catalog").apply { mkdirs() }

        private val catalogFile: File
            get() = File(catalogDir, "words.json")

        fun readIfFresh(maxAgeMs: Long = CACHE_TTL_MS): String? {
            if (!catalogFile.isFile) return null
            val age = System.currentTimeMillis() - catalogFile.lastModified()
            if (age > maxAgeMs) return null
            if (catalogFile.length() > CatalogValidator.MaxBytes) {
                catalogFile.delete()
                return null
            }
            return catalogFile.readText(Charsets.UTF_8)
        }

        fun writeAtomic(json: String) {
            require(json.length <= CatalogValidator.MaxBytes)
            val temp = File(catalogDir, "words.json.tmp")
            temp.writeText(json, Charsets.UTF_8)
            if (!temp.renameTo(catalogFile)) {
                catalogFile.delete()
                temp.copyTo(catalogFile, overwrite = true)
                temp.delete()
            }
        }

        fun fingerprint(): String? {
            if (!catalogFile.isFile) return null
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(catalogFile.readBytes())
            return hash.joinToString("") { "%02x".format(it) }
        }

        fun prune(maxTotalBytes: Long = 512_000L) {
            val dir = catalogDir
            if (!dir.exists()) return
            val files = dir.listFiles() ?: return
            val total = files.sumOf { it.length() }
            val staleMs = 14L * 24 * 60 * 60 * 1000
            val now = System.currentTimeMillis()
            files.forEach { file ->
                if (now - file.lastModified() > staleMs || file.length() > CatalogValidator.MaxBytes) {
                    file.delete()
                }
            }
            if (total > maxTotalBytes) {
                files.sortedBy { it.lastModified() }.forEach { it.delete() }
            }
        }

        companion object {
            const val CACHE_TTL_MS = 24L * 60 * 60 * 1000
        }
    }
