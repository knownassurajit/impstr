package com.knownassurajit.app.game.impstr.data

/**
 * Extra words and categories layered on top of the built-in library.
 * Built from the player's region (locale or coarse location) and optional
 * HTTPS catalog updates. Never contains coordinates or other personal data.
 */
data class WordOverlay(
    val version: Int = 0,
    val regionCode: String? = null,
    val regionLabel: String? = null,
    val suggestedCategories: List<String> = emptyList(),
    val extraCategories: Map<String, List<String>> = emptyMap(),
    val extraWordsByCategory: Map<String, List<String>> = emptyMap(),
    val bonusEasyWords: List<String> = emptyList(),
) {
    fun merge(other: WordOverlay): WordOverlay {
        if (other == Empty) return this
        if (this == Empty) return other
        return WordOverlay(
            version = maxOf(version, other.version),
            regionCode = other.regionCode ?: regionCode,
            regionLabel = other.regionLabel ?: regionLabel,
            suggestedCategories =
                (other.suggestedCategories + suggestedCategories)
                    .distinct()
                    .take(8),
            extraCategories = mergeWordMaps(extraCategories, other.extraCategories),
            extraWordsByCategory = mergeWordMaps(extraWordsByCategory, other.extraWordsByCategory),
            bonusEasyWords = (bonusEasyWords + other.bonusEasyWords).distinct(),
        )
    }

    companion object {
        val Empty = WordOverlay()
    }
}

data class CatalogSnapshot(
    val builtin: Map<String, List<String>>,
    val overlay: WordOverlay = WordOverlay.Empty,
) {
    fun categoryNames(): List<String> {
        val merged = mergedCategories()
        val ordered = linkedSetOf<String>()
        if (RANDOM_CATEGORY in merged) ordered += RANDOM_CATEGORY
        overlay.extraCategories.keys.forEach { ordered += it }
        overlay.suggestedCategories.forEach { if (it in merged) ordered += it }
        merged.keys.forEach { ordered += it }
        return ordered.toList()
    }

    fun mergedCategories(): Map<String, List<String>> {
        val result = linkedMapOf<String, List<String>>()
        builtin.forEach { (name, words) ->
            val extras = overlay.extraWordsByCategory[name].orEmpty()
            result[name] = (words + extras).distinct()
        }
        overlay.extraCategories.forEach { (name, words) ->
            result[name] = ((result[name] ?: emptyList()) + words).distinct()
        }
        return result
    }

    companion object {
        const val RANDOM_CATEGORY = "Random Words"
        const val LOCAL_CATEGORY = "Around You"
    }
}

enum class CatalogStatus {
    Idle,
    Updating,
    Updated,
    Offline,
    Failed,
}

internal fun mergeWordMaps(
    first: Map<String, List<String>>,
    second: Map<String, List<String>>,
): Map<String, List<String>> {
    val keys = first.keys + second.keys
    return keys.associateWith { key ->
        ((first[key] ?: emptyList()) + (second[key] ?: emptyList())).distinct()
    }
}
