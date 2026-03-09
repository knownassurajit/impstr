package com.game.impstr.data

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WordRepositoryTest {
    private lateinit var originalCategories: Map<String, List<String>>

    @Before
    fun setUp() {
        originalCategories = WordRepository.categories.toMap()
    }

    @After
    fun tearDown() {
        WordRepository.updateCategories(originalCategories)
    }

    @Test
    fun `getRandomWord returns word from specific category`() {
        WordRepository.updateCategories(
            mapOf(
                "Random Words" to listOf("ignored"),
                "Animals" to listOf("Tiger"),
            ),
        )

        val word = WordRepository.getRandomWord("Animals")

        assertEquals("Tiger", word)
    }

    @Test
    fun `getRandomWord in random mode uses aggregate pool from non-random categories`() {
        WordRepository.updateCategories(
            mapOf(
                "Random Words" to listOf("ignored"),
                "Animals" to listOf("Tiger"),
                "Countries" to listOf("Japan"),
            ),
        )

        val word = WordRepository.getRandomWord("Random Words")

        assertTrue(word in setOf("Tiger", "Japan"))
    }

    @Test
    fun `updateCategory rebuilds aggregate pool for random mode`() {
        WordRepository.updateCategories(
            mapOf(
                "Random Words" to listOf("ignored"),
                "Animals" to listOf("Tiger"),
            ),
        )

        WordRepository.updateCategory("Animals", listOf("Lion"))

        val word = WordRepository.getRandomWord("Random Words")

        assertEquals("Lion", word)
    }
}
