package com.knownassurajit.impstr_game.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WordRepositoryTest {

    @Test
    fun `getRandomWord returns word from selected category`() {
        val category = "Animals"
        repeat(50) {
            val word = WordRepository.getRandomWord(category)
            assertTrue(
                "expected '$word' to be in $category",
                WordRepository.categories.getValue(category).contains(word),
            )
        }
    }

    @Test
    fun `getRandomWordPair returns two distinct words from same category`() {
        val category = "Food & Drinks"
        val pool = WordRepository.categories.getValue(category)
        repeat(50) {
            val (a, b) = WordRepository.getRandomWordPair(category)
            assertNotEquals("pair words must differ", a, b)
            assertTrue("first word must be in pool", pool.contains(a))
            assertTrue("second word must be in pool", pool.contains(b))
        }
    }

    @Test
    fun `getRandomWordPair with RANDOM_CATEGORY picks both words from same sub-category`() {
        // Both words must come from the same actual sub-category so the imposter
        // sees a thematically related decoy in stealth mode.
        val realCategories = WordRepository.categories.filterKeys { it != WordRepository.RANDOM_CATEGORY }
        repeat(100) {
            val (a, b) = WordRepository.getRandomWordPair(WordRepository.RANDOM_CATEGORY)
            assertNotEquals(a, b)
            val sharedCategory = realCategories.entries.any { (_, words) ->
                words.contains(a) && words.contains(b)
            }
            assertTrue(
                "expected '$a' and '$b' to share a sub-category",
                sharedCategory,
            )
        }
    }

    @Test
    fun `every category exposes at least two words for stealth mode`() {
        WordRepository.categories.forEach { (name, words) ->
            assertTrue("category '$name' must have >=2 words for stealth pairs", words.size >= 2)
        }
    }

    @Test
    fun `RANDOM_CATEGORY key is present in categories map`() {
        assertEquals(true, WordRepository.categories.containsKey(WordRepository.RANDOM_CATEGORY))
    }
}
