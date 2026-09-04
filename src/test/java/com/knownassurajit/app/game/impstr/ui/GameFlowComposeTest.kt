package com.knownassurajit.app.game.impstr.ui

import android.app.Application
import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.waitUntilAtLeastOneExists
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.knownassurajit.app.game.impstr.R
import com.knownassurajit.app.game.impstr.domain.usecase.ShufflePlayersUseCase
import com.knownassurajit.app.game.impstr.ui.theme.IMPSTRTheme
import com.knownassurajit.app.game.impstr.ui.viewmodel.GameViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], application = Application::class, qualifiers = "w411dp-h891dp-xxhdpi")
class GameFlowComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

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

        composeRule.onNodeWithTag(ImpstrTestTags.StartGame).assertIsDisplayed().assertIsEnabled().performClick()

        repeat(4) { index ->
            composeRule.waitUntilAtLeastOneExists(hasTestTag(ImpstrTestTags.RevealPrimary), 8_000)
            runCatching {
                composeRule.onNodeWithText(context.getString(R.string.action_reveal_role)).performClick()
            }
            val nextLabel =
                if (index == 3) {
                    context.getString(R.string.action_start_discussion)
                } else {
                    context.getString(R.string.action_next_player)
                }
            composeRule.waitUntilAtLeastOneExists(androidx.compose.ui.test.hasText(nextLabel), 8_000)
            composeRule.onNodeWithText(nextLabel).performClick()
        }

        composeRule.waitUntilAtLeastOneExists(hasTestTag(ImpstrTestTags.ProceedVoting), 8_000)
        composeRule.onNodeWithTag(ImpstrTestTags.ProceedVoting).performClick()

        composeRule.waitUntilAtLeastOneExists(hasTestTag(ImpstrTestTags.SubmitVote), 8_000)
        composeRule.onNodeWithText("Player 1").performClick()
        composeRule.onNodeWithTag(ImpstrTestTags.SubmitVote).assertIsEnabled().performClick()

        composeRule.waitUntilAtLeastOneExists(
            hasTestTag(ImpstrTestTags.PlayAgain)
                .or(hasTestTag(ImpstrTestTags.SeeResults))
                .or(hasTestTag(ImpstrTestTags.EndGame))
                .or(hasTestTag(ImpstrTestTags.NextRound)),
            10_000,
        )
    }
}
