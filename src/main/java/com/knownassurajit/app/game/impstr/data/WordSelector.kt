package com.knownassurajit.app.game.impstr.data

/**
 * Picks secret words for a match.
 *
 * Random mode uses everyday, widely-known words plus the player's regional
 * pack. Harder categories stay available when chosen on purpose.
 */
object WordSelector {
    val familiarCategories: Set<String> =
        setOf(
            "Animals",
            "Food & Drinks",
            "Sports & Activities",
            "Movies & TV Shows",
            "Famous Characters",
            "Famous Brands",
            "Vehicles & Transportation",
            "Nature & Landscapes",
            "At the Beach",
            "Superheroes & Villains",
            "Musical Instruments",
            "School & Education",
            "Christmas & Holidays",
            "Halloween & Horror",
            CatalogSnapshot.LOCAL_CATEGORY,
        )

    val hardCategories: Set<String> =
        setOf(
            "Historical Figures",
            "Mythology & Fantasy",
            "Science & Lab",
            "Trending & Viral",
            "Celebrities & Icons",
            "90s Nostalgia",
            "Space & Astronomy",
        )

    fun wordsFor(
        category: String,
        snapshot: CatalogSnapshot,
    ): List<String> {
        val merged = snapshot.mergedCategories()
        if (category == CatalogSnapshot.RANDOM_CATEGORY) {
            return randomPool(snapshot, merged)
        }
        val selected = merged[category]
        if (!selected.isNullOrEmpty()) return selected
        return randomPool(snapshot, merged)
    }

    fun pickWord(
        category: String,
        snapshot: CatalogSnapshot,
    ): String = wordsFor(category, snapshot).randomOrNull() ?: "Imposter"

    fun pickWordPair(
        category: String,
        snapshot: CatalogSnapshot,
    ): Pair<String, String> {
        val words = wordsFor(category, snapshot)
        if (words.size < 2) return Pair(words.firstOrNull() ?: "Imposter", "Decoy")
        val shuffled = words.shuffled()
        return Pair(shuffled[0], shuffled[1])
    }

    internal fun randomPool(
        snapshot: CatalogSnapshot,
        merged: Map<String, List<String>>,
    ): List<String> {
        val pool = ArrayList<String>()
        merged.forEach { (name, words) ->
            when {
                name == CatalogSnapshot.RANDOM_CATEGORY -> pool += words
                name in familiarCategories && name != CatalogSnapshot.LOCAL_CATEGORY -> pool += words
                name in hardCategories -> Unit
            }
        }
        val localWords =
            snapshot.overlay.extraCategories[CatalogSnapshot.LOCAL_CATEGORY].orEmpty() +
                snapshot.overlay.bonusEasyWords
        localWords.forEach { word ->
            pool += word
            pool += word
        }
        return pool.ifEmpty {
            merged[CatalogSnapshot.RANDOM_CATEGORY] ?: merged.values.flatten()
        }
    }
}
