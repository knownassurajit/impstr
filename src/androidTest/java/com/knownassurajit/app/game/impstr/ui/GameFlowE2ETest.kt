package com.knownassurajit.app.game.impstr.ui

import android.Manifest
import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToString
import androidx.compose.ui.test.waitUntilAtLeastOneExists
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.knownassurajit.app.game.impstr.E2EHostActivity
import com.knownassurajit.app.game.impstr.R
import com.knownassurajit.app.game.impstr.domain.usecase.ShufflePlayersUseCase
import com.knownassurajit.app.game.impstr.ui.theme.IMPSTRTheme
import com.knownassurajit.app.game.impstr.ui.theme.LightBackground
import com.knownassurajit.app.game.impstr.ui.theme.Primary
import com.knownassurajit.app.game.impstr.ui.theme.Typography
import com.knownassurajit.app.game.impstr.ui.viewmodel.GameViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DesignSystemUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun brandedDarkTheme_exposesMaterialRolesAndTypeScale() {
        var primary = androidx.compose.ui.graphics.Color.Unspecified
        var background = androidx.compose.ui.graphics.Color.Unspecified
        var bodySize = 0.sp
        composeRule.setContent {
            IMPSTRTheme(darkTheme = true, dynamicColor = false, isStealthMode = false) {
                primary = MaterialTheme.colorScheme.primary
                background = MaterialTheme.colorScheme.background
                bodySize = MaterialTheme.typography.bodyLarge.fontSize
            }
        }
        composeRule.waitForIdle()
        assertEquals(Primary, primary)
        assertNotEquals(LightBackground, background)
        assertEquals(Typography.bodyLarge.fontSize, bodySize)
    }

    @Test
    fun lightTheme_usesLightSurfacesNotDarkFallback() {
        var background = androidx.compose.ui.graphics.Color.Unspecified
        composeRule.setContent {
            IMPSTRTheme(darkTheme = false, dynamicColor = false) {
                background = MaterialTheme.colorScheme.background
            }
        }
        composeRule.waitForIdle()
        assertEquals(LightBackground, background)
    }
}

@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class GameFlowE2ETest {

    @get:Rule(order = 0)
    val permissions: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<E2EHostActivity>()

    @Before
    fun bringHostToForeground() {
        val automation = InstrumentationRegistry.getInstrumentation().uiAutomation
        runCatching { automation.executeShellCommand("input keyevent 224").close() }
        runCatching { automation.executeShellCommand("wm dismiss-keyguard").close() }
        composeRule.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        check(composeRule.activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            "E2E host is ${composeRule.activity.lifecycle.currentState}. Unlock the device and keep the display on."
        }
    }

    @Test
    fun hostActivity_exposesTaggedNode() {
        composeRule.setContent {
            Text(text = "probe", modifier = Modifier.testTag("e2e_probe"))
        }
        composeRule.onNodeWithTag("e2e_probe").assertExists()
    }

    @Test
    fun lobbyThroughResults_usesMaterialActions() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val viewModel = GameViewModel(context, SavedStateHandle(), ShufflePlayersUseCase())

        composeRule.setContent {
            val interactionTime = remember { mutableStateOf(System.currentTimeMillis()) }
            IMPSTRTheme(darkTheme = true, dynamicColor = false) {
                CompositionLocalProvider(LocalInteractionTime provides interactionTime) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        ImpstrGameFlow(viewModel)
                    }
                }
            }
        }

        composeRule.waitUntilAtLeastOneExists(hasTestTag(ImpstrTestTags.StartGame), 8_000)
        composeRule
            .onNodeWithTag(ImpstrTestTags.StartGame, useUnmergedTree = true)
            .assertIsDisplayed()
            .assertIsEnabled()
            .performClick()
        composeRule.waitForIdle()

        repeat(4) { index ->
            composeRule.waitUntilAtLeastOneExists(hasTestTag(ImpstrTestTags.RevealPrimary), 8_000)
            val reveal = context.getString(R.string.action_reveal_role)
            runCatching {
                composeRule.onNodeWithText(reveal).assertIsDisplayed().performClick()
                composeRule.waitForIdle()
            }
            val nextLabel =
                if (index == 3) {
                    context.getString(R.string.action_start_discussion)
                } else {
                    context.getString(R.string.action_next_player)
                }
            composeRule.waitUntilAtLeastOneExists(
                androidx.compose.ui.test.hasText(nextLabel),
                8_000,
            )
            composeRule.onNodeWithText(nextLabel).performClick()
            composeRule.waitForIdle()
        }

        composeRule.waitUntilAtLeastOneExists(hasTestTag(ImpstrTestTags.ProceedVoting), 8_000)
        composeRule.onNodeWithTag(ImpstrTestTags.ProceedVoting, useUnmergedTree = true).assertIsDisplayed().performClick()
        composeRule.waitForIdle()

        composeRule.waitUntilAtLeastOneExists(hasTestTag(ImpstrTestTags.SubmitVote), 8_000)
        composeRule.onNodeWithText("Player 1").performClick()
        composeRule.onNodeWithTag(ImpstrTestTags.SubmitVote, useUnmergedTree = true).assertIsEnabled().performClick()
        composeRule.waitForIdle()

        composeRule.waitUntilAtLeastOneExists(
            hasTestTag(ImpstrTestTags.PlayAgain)
                .or(hasTestTag(ImpstrTestTags.SeeResults))
                .or(hasTestTag(ImpstrTestTags.EndGame))
                .or(hasTestTag(ImpstrTestTags.NextRound)),
            10_000,
        )

        val resultsTree =
            runCatching { composeRule.onRoot(useUnmergedTree = true).printToString() }
                .getOrElse { "" }
        when {
            resultsTree.contains(ImpstrTestTags.SeeResults) -> {
                composeRule.onNodeWithTag(ImpstrTestTags.SeeResults, useUnmergedTree = true).performClick()
                composeRule.waitForIdle()
                composeRule.onNodeWithTag(ImpstrTestTags.PlayAgain, useUnmergedTree = true).assertIsDisplayed()
            }
            resultsTree.contains(ImpstrTestTags.PlayAgain) -> {
                composeRule.onNodeWithTag(ImpstrTestTags.PlayAgain, useUnmergedTree = true).assertIsDisplayed()
            }
            resultsTree.contains(ImpstrTestTags.EndGame) -> {
                composeRule.onNodeWithTag(ImpstrTestTags.EndGame, useUnmergedTree = true).performClick()
                composeRule.waitForIdle()
                composeRule.waitUntilAtLeastOneExists(hasTestTag(ImpstrTestTags.PlayAgain), 8_000)
                composeRule.onNodeWithTag(ImpstrTestTags.PlayAgain, useUnmergedTree = true).assertIsDisplayed()
            }
            else -> {
                composeRule.onNodeWithTag(ImpstrTestTags.NextRound, useUnmergedTree = true).assertIsDisplayed()
            }
        }
    }
}
