package com.knownassurajit.app.game.impstr.data

import org.json.JSONArray
import org.json.JSONObject

/**
 * Strict parser for remote / bundled word catalogs.
 * Rejects oversized payloads, unknown shapes, and unsafe strings.
 */
object CatalogValidator {
    const val MaxBytes = 256_000
    const val MaxRegions = 60
    const val MaxCategoriesPerRegion = 16
    const val MaxWordsPerList = 80
    const val MaxWordLength = 40
    private val WordPattern = Regex("^[\\p{L}\\p{N}][\\p{L}\\p{N} .'&+\\-]*$")

    fun parse(
        json: String,
        regionCode: String?,
    ): WordOverlay {
        require(json.length <= MaxBytes) { "catalog too large" }
        val root = JSONObject(json)
        val version = root.optInt("version", 0)
        requireSupportedVersion(version)

        val globalEasy = sanitizeList(root.optJSONArray("globalEasyWords"))
        val regions = root.optJSONObject("regions") ?: JSONObject()
        require(regions.length() <= MaxRegions) { "too many regions" }

        val code = regionCode?.uppercase()?.take(2)
        val regionJson =
            if (!code.isNullOrBlank() && regions.has(code)) {
                regions.getJSONObject(code)
            } else {
                null
            }

        if (regionJson == null) {
            return buildOverlay(
                version = version,
                regionCode = code,
                label = null,
                suggested = emptyList(),
                categories = emptyMap(),
                easyWords = emptyList(),
                globalEasy = globalEasy,
            )
        }

        val categoriesJson = regionJson.optJSONObject("categories") ?: JSONObject()
        require(categoriesJson.length() <= MaxCategoriesPerRegion) { "too many categories" }
        val categories = linkedMapOf<String, List<String>>()
        categoriesJson.keys().forEach { key ->
            val name = sanitizeLabel(key) ?: return@forEach
            val words = sanitizeList(categoriesJson.optJSONArray(key))
            if (words.isNotEmpty()) categories[name] = words
        }

        return buildOverlay(
            version = version,
            regionCode = code,
            label = regionJson.optString("label").take(48).ifBlank { null },
            suggested = sanitizeList(regionJson.optJSONArray("suggestedCategories")).take(8),
            categories = categories,
            easyWords = sanitizeList(regionJson.optJSONArray("easyWords")),
            globalEasy = globalEasy,
        )
    }

    fun requireSupportedVersion(version: Int) {
        require(version == 1) { "unsupported catalog version" }
    }

    fun buildOverlay(
        version: Int,
        regionCode: String?,
        label: String?,
        suggested: List<String>,
        categories: Map<String, List<String>>,
        easyWords: List<String>,
        globalEasy: List<String>,
    ): WordOverlay {
        requireSupportedVersion(version)
        val extraCategories = linkedMapOf<String, List<String>>()
        val extraByCategory = linkedMapOf<String, List<String>>()
        val knownCategories = WordSelector.familiarCategories + WordSelector.hardCategories + KNOWN_GLOBAL
        categories.forEach { (rawName, rawWords) ->
            val name = sanitizeLabel(rawName) ?: return@forEach
            val words = rawWords.mapNotNull(::sanitizeWord).distinct()
            if (words.isEmpty()) return@forEach
            when {
                name == CatalogSnapshot.LOCAL_CATEGORY -> extraCategories[name] = words
                name in knownCategories -> extraByCategory[name] = words
                else -> extraCategories[name] = words
            }
        }
        return WordOverlay(
            version = version,
            regionCode = regionCode?.uppercase()?.take(2),
            regionLabel = label?.let { sanitizeLabel(it) ?: it.take(48) },
            suggestedCategories = suggested.mapNotNull(::sanitizeWord).distinct().take(8),
            extraCategories = extraCategories,
            extraWordsByCategory = extraByCategory,
            bonusEasyWords = (globalEasy + easyWords).mapNotNull(::sanitizeWord).distinct(),
        )
    }

    fun sanitizeWord(raw: String): String? {
        val word = raw.trim().replace(Regex("\\s+"), " ")
        if (word.length !in 1..MaxWordLength) return null
        if (!WordPattern.matches(word)) return null
        return word
    }

    private fun sanitizeLabel(raw: String): String? = sanitizeWord(raw)

    private val KNOWN_GLOBAL =
            setOf(
                "Random Words",
                "Countries",
                "World Cities",
                "Professions",
                "Technology & Gadgets",
            )

    private fun sanitizeList(array: JSONArray?): List<String> {
        if (array == null) return emptyList()
        require(array.length() <= MaxWordsPerList) { "word list too long" }
        val result = ArrayList<String>(array.length())
        for (i in 0 until array.length()) {
            val word = sanitizeWord(array.optString(i)) ?: continue
            result += word
        }
        return result.distinct()
    }
}
