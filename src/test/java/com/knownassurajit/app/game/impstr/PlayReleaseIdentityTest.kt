package com.knownassurajit.app.game.impstr

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class PlayReleaseIdentityTest {

    @Test
    fun releaseApplicationId_matchesPlayConsolePackage() {
        val gradle = releaseGradleFile().readText()
        assertTrue(
            gradle.contains("applicationId = \"com.knownassurajit.impstr_game.app\""),
        )
        assertFalse(gradle.contains("applicationId = \"com.knownassurajit.app.game.impstr\""))
        assertTrue(gradle.contains("compileSdk = 36"))
        assertTrue(gradle.contains("targetSdk = 36"))
        assertTrue(gradle.contains("val build = 1"))
        assertFalse(gradle.contains("compileSdk = 37"))
    }

    private fun releaseGradleFile(): File {
        var dir = File(System.getProperty("user.dir").orEmpty())
        repeat(6) {
            val candidate = File(dir, "build.gradle.kts")
            if (candidate.isFile && candidate.readText().contains("ciBuildNumber")) {
                return candidate
            }
            dir = dir.parentFile ?: error("build.gradle.kts not found")
        }
        val start = System.getProperty("user.dir").orEmpty()
        error("Release build.gradle.kts not found from $start")
    }
}
