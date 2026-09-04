package com.knownassurajit.app.game.impstr.ui.components

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import com.knownassurajit.app.game.impstr.R
import com.knownassurajit.app.game.impstr.data.CatalogStatus
import com.knownassurajit.app.game.impstr.data.PersonalizationState
import com.knownassurajit.app.game.impstr.ui.theme.Dimens
import com.knownassurajit.app.game.impstr.ui.theme.Motion
import com.knownassurajit.app.game.impstr.ui.viewmodel.PersonalizationViewModel

@Composable
fun LocationAssistChip(
    state: PersonalizationState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label =
        when {
            state.regionLabel != null -> stringResource(R.string.location_chip_local, state.regionLabel)
            state.usingLocalWords -> stringResource(R.string.location_chip_locale)
            else -> stringResource(R.string.location_chip_off)
        }
    AssistChip(
        onClick = onClick,
        modifier = modifier,
        label = {
            Text(
                label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = if (state.usingLocalWords) Icons.Rounded.Place else Icons.Rounded.Public,
                contentDescription = null,
            )
        },
        colors =
            AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                labelColor = MaterialTheme.colorScheme.onSurface,
                leadingIconContentColor = MaterialTheme.colorScheme.primary,
            ),
    )
}

@Composable
fun LocationPersonalizationHost(
    state: PersonalizationState,
    viewModel: PersonalizationViewModel,
) {
    var showDetails by remember { mutableStateOf(false) }
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            viewModel.onLocationPermissionResult(granted)
        }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(Motion.enterTween()) + slideInVertically(Motion.enterTween<IntOffset>()) { -it / 2 },
        exit = fadeOut(Motion.exitTween()) + slideOutVertically(Motion.exitTween<IntOffset>()) { -it / 2 },
    ) {
        LocationAssistChip(
            state = state,
            onClick = { showDetails = true },
        )
    }

    if (state.showLocationPrompt) {
        LocationPromptSheet(
            onAllow = { permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION) },
            onSkip = { viewModel.dismissLocationPrompt() },
        )
    }

    if (showDetails) {
        LocationDetailsSheet(
            state = state,
            onDismiss = { showDetails = false },
            onEnableLocation = { permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION) },
            onRefresh = { viewModel.refresh() },
        )
    }
}

@Composable
private fun LocationPromptSheet(
    onAllow: () -> Unit,
    onSkip: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onSkip,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.SheetPadding, vertical = Dimens.SpacingSm),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                Icons.Rounded.Place,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(Dimens.SpacingMd))
            Text(
                stringResource(R.string.location_prompt_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(Dimens.SpacingSm))
            Text(
                stringResource(R.string.location_prompt_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(Dimens.SpacingXl))
            FilledTonalButton(
                onClick = onAllow,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(Dimens.ButtonHeight),
            ) {
                Text(stringResource(R.string.location_prompt_allow), fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(Dimens.SpacingSm))
            OutlinedButton(
                onClick = onSkip,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(Dimens.TouchTargetMin),
            ) {
                Text(stringResource(R.string.location_prompt_skip))
            }
            Spacer(Modifier.height(Dimens.SpacingLg))
        }
    }
}

@Composable
private fun LocationDetailsSheet(
    state: PersonalizationState,
    onDismiss: () -> Unit,
    onEnableLocation: () -> Unit,
    onRefresh: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.SheetPadding, vertical = Dimens.SpacingSm),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMd),
        ) {
            Text(
                stringResource(R.string.location_sheet_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text =
                    when {
                        state.regionLabel != null ->
                            stringResource(R.string.location_chip_local, state.regionLabel)
                        else -> stringResource(R.string.location_chip_locale)
                    },
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                stringResource(R.string.location_prompt_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (state.catalogStatus == CatalogStatus.Updating) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text(
                    stringResource(R.string.catalog_updating),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            if (!state.locationConsent) {
                FilledTonalButton(
                    onClick = onEnableLocation,
                    modifier = Modifier.fillMaxWidth().height(Dimens.ButtonHeight),
                ) {
                    Text(stringResource(R.string.location_prompt_allow), fontWeight = FontWeight.Bold)
                }
            }
            OutlinedButton(
                onClick = onRefresh,
                modifier = Modifier.fillMaxWidth().height(Dimens.TouchTargetMin),
            ) {
                Icon(Icons.Rounded.Refresh, contentDescription = null)
                Spacer(Modifier.padding(Dimens.SpacingXs))
                Text(stringResource(R.string.location_refresh))
            }
            Spacer(Modifier.height(Dimens.SpacingSm))
        }
    }
}
