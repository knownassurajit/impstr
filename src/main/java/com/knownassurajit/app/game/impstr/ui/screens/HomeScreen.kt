package com.knownassurajit.app.game.impstr.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.knownassurajit.app.game.impstr.R
import com.knownassurajit.app.game.impstr.data.CatalogSnapshot
import com.knownassurajit.app.game.impstr.data.PlayerNameSanitizer
import com.knownassurajit.app.game.impstr.ui.ImpstrTestTags
import com.knownassurajit.app.game.impstr.ui.components.ImpstrLogo
import com.knownassurajit.app.game.impstr.ui.components.ImpstrPrimaryButton
import com.knownassurajit.app.game.impstr.ui.components.LocationPersonalizationHost
import com.knownassurajit.app.game.impstr.ui.components.SplitButton
import com.knownassurajit.app.game.impstr.ui.theme.*
import com.knownassurajit.app.game.impstr.ui.viewmodel.GameViewModel
import com.knownassurajit.app.game.impstr.ui.viewmodel.PersonalizationViewModel
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

data class LobbyPlayer(
    val id: String,
    val name: String,
    val avatarColor: Color,
    val isReady: Boolean,
    val isHost: Boolean = false,
    val isMe: Boolean = false,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: GameViewModel,
    onStartGame: () -> Unit,
    personalizationViewModel: PersonalizationViewModel? = null,
) {
    if (personalizationViewModel == null) {
        HomeScreenContent(viewModel, onStartGame, com.knownassurajit.app.game.impstr.data.PersonalizationState(), null)
    } else {
        val personalization = personalizationViewModel.uiState.collectAsStateWithLifecycle().value
        HomeScreenContent(viewModel, onStartGame, personalization, personalizationViewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    viewModel: GameViewModel,
    onStartGame: () -> Unit,
    personalization: com.knownassurajit.app.game.impstr.data.PersonalizationState,
    personalizationViewModel: PersonalizationViewModel?,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    var showPlayerConfig by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf<Int?>(null) }
    var isHelpVisible by remember { mutableStateOf(false) } // Local state for HelpDialog

    // Map ViewModel PlayerState to UI LobbyPlayer
    val players =
        uiState.players.mapIndexed { index, playerState ->
            LobbyPlayer(
                id = playerState.id,
                name = playerState.name,
                avatarColor = if (uiState.isStealthMode) StealthPlayerColors[index % StealthPlayerColors.size] else PlayerColors[index % PlayerColors.size],
                isReady = playerState.isReady,
                isHost = index == 0,
                isMe = index == 0,
            )
        }

    // Help Dialog
    if (isHelpVisible) {
        com.knownassurajit.app.game.impstr.ui.components.HelpDialog(
            onDismiss = { isHelpVisible = false },
        )
    }

    if (showPlayerConfig) {
        PlayerConfigBottomSheet(
            currentCount = players.size,
            currentImposterCount = uiState.imposterCount,
            onDismiss = { showPlayerConfig = false },
            onUpdateCount = { count -> viewModel.updatePlayerCount(count) },
            onUpdateImposterCount = { count -> viewModel.updateImposterCount(count) },
        )
    }

    if (showCategoryDialog) {
        CategoryBottomSheet(
            currentCategory = uiState.category,
            categories = personalization.allCategories,
            suggested = personalization.suggestedCategories,
            regionLabel = personalization.regionLabel,
            onDismiss = { showCategoryDialog = false },
            onCategorySelected = { category ->
                viewModel.updateCategory(category)
                showCategoryDialog = false
            },
        )
    }

    if (showRenameDialog != null) {
        val index = showRenameDialog!!
        val currentName = players.getOrNull(index)?.name ?: ""
        RenamePlayerBottomSheet(
            currentName = currentName,
            onDismiss = { showRenameDialog = null },
            onConfirm = { newName ->
                viewModel.updatePlayerName(index, newName)
                showRenameDialog = null
            },
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimens.ScreenHorizontal),
        ) {
            // Header
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.SpacingLg, bottom = Dimens.SpacingSm),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ImpstrLogo(onClick = { isHelpVisible = true })
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSm),
                ) {
                    Text(
                        text = stringResource(if (uiState.isStealthMode) R.string.mode_stealth else R.string.mode_normal),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (uiState.isStealthMode) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Switch(
                        checked = uiState.isStealthMode,
                        onCheckedChange = { viewModel.setStealthMode(it) },
                        thumbContent = {
                            Icon(
                                imageVector = if (uiState.isStealthMode) Icons.Rounded.Check else Icons.Rounded.Close,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                            checkedIconColor = MaterialTheme.colorScheme.onPrimary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                            uncheckedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                }
            }

            if (personalizationViewModel != null) {
                LocationPersonalizationHost(
                    state = personalization,
                    viewModel = personalizationViewModel,
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SpacingLg))

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max)
                        .padding(horizontal = Dimens.SpacingXs),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingLg),
            ) {
                InfoCard(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    icon = Icons.Rounded.Person,
                    label = stringResource(R.string.label_players),
                    value = "${uiState.imposterCount}/${players.size}",
                    subLabel = stringResource(R.string.label_imposters),
                    onClick = { showPlayerConfig = true },
                )
                InfoCard(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    icon = Icons.Rounded.Category,
                    label = stringResource(R.string.label_category),
                    value = uiState.category,
                    subLabel =
                        when (uiState.category) {
                            CatalogSnapshot.RANDOM_CATEGORY -> stringResource(R.string.random_words_hint_short)
                            CatalogSnapshot.LOCAL_CATEGORY -> personalization.regionLabel.orEmpty()
                            else -> ""
                        },
                    onClick = { showCategoryDialog = true },
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SpacingXl))

            // Players List Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.label_players),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    IconButton(onClick = { viewModel.shuffleLobbyPlayers() }) {
                        Icon(
                            androidx.compose.material.icons.Icons.Rounded.Shuffle,
                            contentDescription = stringResource(R.string.action_shuffle_players),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                TextButton(
                    onClick = {
                    viewModel.updatePlayerCount(players.size + 1)
                },
                    enabled = players.size < PlayerNameSanitizer.MaxPlayers,
                ) {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.IconSizeSm),
                    )
                    Spacer(modifier = Modifier.width(Dimens.SpacingXs))
                    Text(stringResource(R.string.action_add))
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpacingMd))

            // Players List (Reorderable)
            val lazyListState = rememberLazyListState()
            val reorderableLazyListState =
                rememberReorderableLazyListState(lazyListState) { from, to ->
                    val fromIndex = from.index
                    val toIndex = to.index
                    if (fromIndex in players.indices && toIndex in players.indices) {
                        viewModel.reorderPlayers(fromIndex, toIndex)
                    }
                }
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.weight(1f).testTag(ImpstrTestTags.LobbyPlayers),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMd),
            ) {
                items(players, key = { it.id }) { player ->
                    ReorderableItem(
                        reorderableLazyListState,
                        key = player.id,
                    ) { isDragging ->
                        val elevation = animateDpAsState(if (isDragging) Dimens.ElevationMax else Dimens.ElevationNone, label = "playerDrag")
                        val index = players.indexOfFirst { it.id == player.id }

                        Box(
                            modifier =
                                Modifier
                                    .longPressDraggableHandle()
                                    .shadow(elevation.value, MaterialTheme.shapes.medium),
                        ) {
                            // Use SplitButton for Player Item
                            SplitButton(
                                modifier = Modifier.fillMaxWidth(),
                                mainContent = {
                                    // Checkbox/Color Indicator
                                    Box(
                                        modifier =
                                            Modifier
                                                .size(Dimens.AvatarSmall)
                                                .clip(CircleShape)
                                                .background(player.avatarColor),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = player.name.firstOrNull()?.toString()?.uppercase() ?: "?",
                                            fontWeight = FontWeight.Bold,
                                            color = GameColors.OnVibrant,
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(Dimens.SpacingLg))
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = player.name,
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                            )
                                            if (player.isHost) {
                                                Spacer(modifier = Modifier.width(Dimens.SpacingSm))
                                                Surface(
                                                    color = MaterialTheme.colorScheme.primaryContainer,
                                                    shape = Corners.Badge,
                                                ) {
                                                    Text(
                                                        stringResource(R.string.host_badge),
                                                        modifier = Modifier.padding(horizontal = Dimens.SpacingXs, vertical = Dimens.OpticalInset),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                },
                                onMainClick = { /* Maybe show player details? */ },
                                onSecondaryClick = { if (index >= 0) showRenameDialog = index },
                                secondaryIcon = Icons.Rounded.Edit,
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.SpacingXs),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextButton(
                            onClick = { viewModel.updatePlayerCount(players.size - 1) },
                            enabled = players.size > 3,
                        ) {
                            Icon(
                                Icons.Rounded.Remove,
                                contentDescription = stringResource(R.string.cd_remove_player),
                                modifier = Modifier.size(Dimens.IconSizeSm),
                            )
                            Spacer(modifier = Modifier.width(Dimens.SpacingXs))
                            Text(stringResource(R.string.action_remove))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpacingLg))

            ImpstrPrimaryButton(
                text = stringResource(R.string.start_game),
                onClick = onStartGame,
                modifier = Modifier.testTag(ImpstrTestTags.StartGame),
                icon = Icons.AutoMirrored.Rounded.ArrowForward,
            )

            Spacer(modifier = Modifier.height(Dimens.SpacingLg))
        }
    }
}

@Composable
fun PlayerConfigBottomSheet(
    currentCount: Int,
    currentImposterCount: Int,
    onDismiss: () -> Unit,
    onUpdateCount: (Int) -> Unit,
    onUpdateImposterCount: (Int) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = Alpha.Scrim + 0.18f),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.SheetPadding, vertical = Dimens.SpacingLg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                stringResource(R.string.configure_players),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(Dimens.SpacingXxl))

            Text(
                stringResource(R.string.number_of_players),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(Dimens.SpacingLg))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingXl),
            ) {
                IconButton(
                    onClick = { if (currentCount > 3) onUpdateCount(currentCount - 1) },
                    enabled = currentCount > 3,
                ) {
                    Icon(Icons.Rounded.Remove, contentDescription = stringResource(R.string.cd_decrease))
                }

                Text(
                    text = "$currentCount",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                )

                IconButton(
                    onClick = { if (currentCount < PlayerNameSanitizer.MaxPlayers) onUpdateCount(currentCount + 1) },
                    enabled = currentCount < PlayerNameSanitizer.MaxPlayers,
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = stringResource(R.string.cd_increase))
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpacingXxl))

            Text(
                stringResource(R.string.number_of_imposters),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(Dimens.SpacingLg))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingXl),
            ) {
                IconButton(
                    onClick = { if (currentImposterCount > 1) onUpdateImposterCount(currentImposterCount - 1) },
                    enabled = currentImposterCount > 1,
                ) {
                    Icon(Icons.Rounded.Remove, contentDescription = stringResource(R.string.cd_decrease_imposters))
                }

                Text(
                    text = "$currentImposterCount",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                )

                IconButton(
                    onClick = { if (currentImposterCount < currentCount - 1) onUpdateImposterCount(currentImposterCount + 1) },
                    enabled = currentImposterCount < currentCount - 1,
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = stringResource(R.string.cd_increase_imposters))
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpacingXxl))

            FilledTonalButton(
                onClick = {
                    scope.launch {
                        sheetState.hide()
                        onDismiss()
                    }
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(Dimens.TouchTargetMin),
            ) {
                Text(stringResource(R.string.action_done))
            }

            Spacer(modifier = Modifier.height(Dimens.SpacingLg))
        }
    }
}

@Composable
fun CategoryBottomSheet(
    currentCategory: String,
    categories: List<String>,
    suggested: List<String>,
    regionLabel: String?,
    onDismiss: () -> Unit,
    onCategorySelected: (String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val suggestedSet = suggested.toSet()
    val remaining = categories.filter { it !in suggestedSet }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.SheetPadding),
            contentPadding = PaddingValues(vertical = Dimens.SpacingLg),
        ) {
            item {
                Text(
                    stringResource(R.string.select_category),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = Dimens.SpacingSm),
                )
                if (regionLabel != null) {
                    Text(
                        stringResource(R.string.category_suggested) + " · $regionLabel",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = Dimens.SpacingMd),
                    )
                }
            }

            if (suggested.isNotEmpty()) {
                items(suggested.filter { it in categories.toSet() || it == currentCategory }.distinct()) { category ->
                    CategoryRow(
                        category = category,
                        selected = category == currentCategory,
                        supporting =
                            if (category == CatalogSnapshot.RANDOM_CATEGORY) {
                                stringResource(R.string.random_words_hint)
                            } else {
                                null
                            },
                        onCategorySelected = onCategorySelected,
                    )
                }
                item {
                    Text(
                        stringResource(R.string.category_all),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = Dimens.SpacingLg, bottom = Dimens.SpacingSm),
                    )
                }
            }

            items(remaining) { category ->
                CategoryRow(
                    category = category,
                    selected = category == currentCategory,
                    supporting =
                        if (category == CatalogSnapshot.RANDOM_CATEGORY) {
                            stringResource(R.string.random_words_hint)
                        } else {
                            null
                        },
                    onCategorySelected = onCategorySelected,
                )
            }

            item { Spacer(modifier = Modifier.height(Dimens.SpacingLg)) }
        }
    }
}

@Composable
private fun CategoryRow(
    category: String,
    selected: Boolean,
    supporting: String?,
    onCategorySelected: (String) -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(
                category,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent =
            supporting?.let {
                {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            },
        leadingContent = {
            RadioButton(
                selected = selected,
                onClick = { onCategorySelected(category) },
                colors =
                    RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary,
                    ),
            )
        },
        modifier =
            Modifier
                .clip(MaterialTheme.shapes.medium)
                .clickable { onCategorySelected(category) },
        colors =
            ListItemDefaults.colors(
                containerColor =
                    if (selected) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                    } else {
                        Color.Transparent
                    },
            ),
    )
}

@Composable
fun RenamePlayerBottomSheet(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var name by remember { mutableStateOf(currentName) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.SheetPadding, vertical = Dimens.SpacingLg),
        ) {
            Text(
                stringResource(R.string.rename_player),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(Dimens.SpacingXl))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it.take(PlayerNameSanitizer.MaxLength) },
                label = { Text(stringResource(R.string.player_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(Dimens.SpacingXl))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMd),
            ) {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    },
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(Dimens.TouchTargetMin),
                ) {
                    Text(stringResource(R.string.action_cancel))
                }

                FilledTonalButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            onConfirm(name)
                        }
                    },
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(Dimens.TouchTargetMin),
                ) {
                    Text(stringResource(R.string.action_save))
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpacingLg))
        }
    }
}

@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    color: Color,
    contentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    subLabel: String,
    onClick: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = Motion.SpatialSpring,
        label = "infoCardPress",
    )
    ElevatedCard(
        onClick = onClick,
        modifier =
            modifier
                .heightIn(min = Dimens.InfoCardHeight)
                .scale(scale),
        shape = MaterialTheme.shapes.large,
        interactionSource = interactionSource,
        colors =
            CardDefaults.elevatedCardColors(
                containerColor = color,
                contentColor = contentColor,
            ),
        elevation =
            CardDefaults.elevatedCardElevation(
                defaultElevation = Dimens.ElevationBase,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(Dimens.CardPadding),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = Alpha.Medium),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = MaterialTheme.typography.labelSmall.letterSpacing,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false).padding(end = Dimens.SpacingXs),
                )
                Icon(
                    icon,
                    contentDescription = null,
                    tint = contentColor.copy(alpha = Alpha.High),
                    modifier = Modifier.size(Dimens.IconSizeSm),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingXs)) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (subLabel.isNotEmpty()) {
                    Text(
                        text = subLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor.copy(alpha = Alpha.Medium),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerListItem(
    player: LobbyPlayer,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors =
            CardDefaults.elevatedCardColors(
                containerColor =
                    if (player.isHost) {
                        MaterialTheme.colorScheme.surfaceVariant
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
            ),
        elevation =
            CardDefaults.elevatedCardElevation(
                defaultElevation = if (player.isHost) Dimens.ElevationSlight else Dimens.ElevationBase,
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(Dimens.CardPaddingTight),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Box {
                    // Avatar
                    Box(
                        modifier =
                            Modifier
                                .size(Dimens.AvatarMedium)
                                .clip(CircleShape)
                                .background(player.avatarColor.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = player.name.firstOrNull()?.uppercase() ?: "?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GameColors.OnVibrant,
                        )
                    }
                    if (player.isHost) {
                        Surface(
                            modifier =
                                Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(end = Dimens.OpticalInset),
                            shape = Corners.Badge,
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            tonalElevation = Dimens.ElevationBase,
                        ) {
                            Text(
                                stringResource(R.string.host_badge),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.padding(horizontal = Dimens.SpacingXs, vertical = Dimens.OpticalInset),
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(Dimens.SpacingMd))
                Column {
                    Text(
                        text = player.name,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    )
                    Text(
                        text = stringResource(R.string.hold_to_move),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Edit Button
            IconButton(onClick = onEditClick) {
                Icon(
                    Icons.Rounded.Edit,
                    contentDescription = stringResource(R.string.cd_edit_name),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
