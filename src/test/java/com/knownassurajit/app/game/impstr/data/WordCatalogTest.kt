package com.knownassurajit.app.game.impstr.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WordSelectorTest {
    @Test
    fun randomPoolExcludesHardCategories() {
        val builtin =
            linkedMapOf(
                "Random Words" to listOf("Pizza"),
                "Animals" to listOf("Lion"),
                "Historical Figures" to listOf("Aristotle"),
                "Science & Lab" to listOf("Bunsen Burner"),
                "Trending & Viral" to listOf("Gig Economy"),
            )
        val snapshot = CatalogSnapshot(builtin)
        val pool = WordSelector.randomPool(snapshot, snapshot.mergedCategories())

        assertTrue(pool.contains("Pizza"))
        assertTrue(pool.contains("Lion"))
        assertFalse(pool.contains("Aristotle"))
        assertFalse(pool.contains("Bunsen Burner"))
        assertFalse(pool.contains("Gig Economy"))
    }

    @Test
    fun randomPoolWeightsLocalWords() {
        val overlay =
            WordOverlay(
                extraCategories = mapOf("Around You" to listOf("Biryani")),
                bonusEasyWords = listOf("Chai"),
            )
        val snapshot =
            CatalogSnapshot(
                builtin = mapOf("Random Words" to listOf("Pizza"), "Animals" to listOf("Lion")),
                overlay = overlay,
            )
        val pool = WordSelector.randomPool(snapshot, snapshot.mergedCategories())
        assertTrue(pool.contains("Biryani"))
        assertTrue(pool.contains("Chai"))
        assertTrue(pool.count { it == "Biryani" } >= 2)
    }

    @Test
    fun explicitHardCategoryStillAvailable() {
        val snapshot =
            CatalogSnapshot(
                builtin = mapOf("Science & Lab" to listOf("Microscope", "Atom")),
            )
        val words = WordSelector.wordsFor("Science & Lab", snapshot)
        assertEquals(listOf("Microscope", "Atom"), words)
    }
}

class CatalogValidatorTest {
    @Test
    fun buildsRegionalOverlay() {
        val overlay =
            CatalogValidator.buildOverlay(
                version = 1,
                regionCode = "IN",
                label = "India",
                suggested = listOf("Around You", "Food & Drinks"),
                categories =
                    mapOf(
                        "Around You" to listOf("Biryani", "Samosa"),
                        "Food & Drinks" to listOf("Dosa"),
                    ),
                easyWords = listOf("Chai"),
                globalEasy = listOf("Pizza"),
            )
        assertEquals("IN", overlay.regionCode)
        assertEquals("India", overlay.regionLabel)
        assertTrue(overlay.extraCategories["Around You"]!!.contains("Biryani"))
        assertTrue(overlay.extraWordsByCategory["Food & Drinks"]!!.contains("Dosa"))
        assertTrue(overlay.bonusEasyWords.contains("Pizza"))
        assertTrue(overlay.bonusEasyWords.contains("Chai"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsUnsupportedVersion() {
        CatalogValidator.requireSupportedVersion(99)
    }

    @Test
    fun dropsUnsafeWords() {
        assertEquals(null, CatalogValidator.sanitizeWord("<script>"))
        assertEquals(null, CatalogValidator.sanitizeWord(""))
        assertEquals("Butter Chicken", CatalogValidator.sanitizeWord("  Butter Chicken  "))
    }
}

class RegionalWordPacksTest {
    @Test
    fun indiaPackSuggestsLocalCategory() {
        val overlay = RegionalWordPacks.overlayFor("IN")
        assertEquals("India", overlay.regionLabel)
        assertTrue(overlay.suggestedCategories.contains("Around You"))
        assertTrue(overlay.extraCategories["Around You"]!!.contains("Biryani"))
    }

    @Test
    fun unknownCountryIsEmpty() {
        assertEquals(WordOverlay.Empty, RegionalWordPacks.overlayFor("ZZ"))
    }
}

class PlayerNameSanitizerTest {
    @Test
    fun trimsAndCapsLength() {
        assertEquals("Ada", PlayerNameSanitizer.sanitize("  Ada  "))
        assertEquals("Player", PlayerNameSanitizer.sanitize("@@@"))
        assertEquals(PlayerNameSanitizer.MaxLength, PlayerNameSanitizer.sanitize("A".repeat(80)).length)
    }
}
