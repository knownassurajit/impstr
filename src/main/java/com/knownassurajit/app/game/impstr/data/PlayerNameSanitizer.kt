package com.knownassurajit.app.game.impstr.data

object PlayerNameSanitizer {
    const val MaxLength = 24
    const val MaxPlayers = 10
    const val MinPlayers = 3

    fun sanitize(
        name: String,
        fallback: String = "Player",
    ): String {
        val cleaned =
            name.trim()
                .replace(Regex("\\s+"), " ")
                .filter { it.isLetterOrDigit() || it == ' ' || it in ".'-_" }
                .take(MaxLength)
        return cleaned.ifBlank { fallback }
    }
}
