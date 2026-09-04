package com.knownassurajit.app.game.impstr.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Keeps cache and leftover temp files from growing without bound.
 */
@Singleton
class StorageHygiene
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val catalogCache: CatalogCache,
    ) {
        fun prune() {
            catalogCache.prune()
            pruneDir(context.cacheDir, maxAgeMs = 14L * 24 * 60 * 60 * 1000, maxBytes = 2L * 1024 * 1024)
            pruneTemps(context.filesDir)
        }

        private fun pruneDir(
            dir: File,
            maxAgeMs: Long,
            maxBytes: Long,
        ) {
            if (!dir.exists()) return
            val files = dir.walkBottomUp().filter { it.isFile }.toList()
            val now = System.currentTimeMillis()
            files.forEach { file ->
                if (now - file.lastModified() > maxAgeMs) {
                    file.delete()
                }
            }
            val remaining = dir.walkBottomUp().filter { it.isFile }.toList()
            val total = remaining.sumOf { it.length() }
            if (total > maxBytes) {
                remaining.sortedBy { it.lastModified() }
                    .fold(total) { acc, file ->
                        if (acc <= maxBytes) acc
                        else {
                            val size = file.length()
                            file.delete()
                            acc - size
                        }
                    }
            }
        }

        private fun pruneTemps(dir: File) {
            if (!dir.exists()) return
            dir.listFiles()
                ?.filter { it.name.endsWith(".tmp") }
                ?.forEach { it.delete() }
        }
    }
