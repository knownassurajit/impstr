package com.game.impstr.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.impstr.data.WordRepository
import com.game.impstr.ui.components.SplitButton
import com.game.impstr.ui.theme.*
import com.game.impstr.ui.viewmodel.GameViewModel
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

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
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    var showPlayerConfig by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf<Int?>(null) }
    var isHelpVisible by remember { mutableStateOf(false) } // Local state for SideSheet

    // Map ViewModel PlayerState to UI LobbyPlayer
    val players =
        uiState.players.mapIndexed { index, playerState ->
            LobbyPlayer(
                id = playerState.id,
                name = playerState.name,
                avatarColor = PlayerColors[index % PlayerColors.size], // Use varied vibrant colors
                isReady = playerState.isReady,
                isHost = index == 0,
                isMe = index == 0,
            )
        }

    // Help Dialog
    if (isHelpVisible) {
        com.game.impstr.ui.components.HelpDialog(
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
                    .padding(horizontal = 20.dp),
        ) {
            // Header
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val infiniteTransition = rememberInfiniteTransition(label = "blink")
                    
                    val cursorAlpha by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 0f,
                        animationSpec = infiniteRepeatable(
                            animation = keyframes {
                                durationMillis = 1200
                                1f at 0
                                1f at 588
                                0f at 600
                                0f at 1200
                            },
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "cursorAlpha"
                    )
                    
                    val textStyle = MaterialTheme.typography.displayMedium.copy(
                        fontFamily = LogoFont,
                        letterSpacing = 2.sp,
                    )
                    
                    Text(
                        text = "I",
                        style = textStyle,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Black,
                    )
                    
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF3B82F6).copy(alpha = cursorAlpha))
                            .padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "M",
                            style = textStyle,
                            color = if (cursorAlpha > 0.5f) Color.Black else MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Black,
                        )
                    }
                    
                    Text(
                        text = "PSTR",
                        style = textStyle,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Black,
                    )
                }
                IconButton(onClick = { isHelpVisible = true }) { // Trigger side sheet
                    Icon(
                        Icons.Rounded.Info,
                        contentDescription = "Help",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                InfoCard(
                    modifier = Modifier.weight(1f),
                    color = CardYellow,
                    icon = Icons.Rounded.Person,
                    label = "Players",
                    value = "${uiState.imposterCount}/${players.size}",
                    subLabel = "Imposters",
                    onClick = { showPlayerConfig = true },
                )
                InfoCard(
                    modifier = Modifier.weight(1f),
                    color = CardTeal,
                    icon = Icons.Rounded.Category,
                    label = "Category",
                    value = uiState.category,
                    subLabel = "",
                    onClick = { showCategoryDialog = true },
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Players List Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Players",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    IconButton(onClick = { viewModel.shuffleLobbyPlayers() }) {
                        Icon(
                            androidx.compose.material.icons.Icons.Rounded.Shuffle,
                            contentDescription = "Shuffle Players",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                TextButton(onClick = {
                    viewModel.updatePlayerCount(players.size + 1)
                }) {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Players List (Reorderable)
            val state =
                rememberReorderableLazyListState(onMove = { from, to ->
                    viewModel.reorderPlayers(from.index, to.index)
                })

            LazyColumn(
                state = state.listState,
                modifier =
                    Modifier
                        .weight(1f)
                        .reorderable(state),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(players, key = { it.id }) { player ->
                    ReorderableItem(
                        reorderableState = state,
                        key = player.id,
                    ) { isDragging ->
                        val elevation = animateDpAsState(if (isDragging) 8.dp else 0.dp)
                        val index = players.indexOfFirst { it.id == player.id }

                        Box(
                            modifier =
                                Modifier
                                    .detectReorderAfterLongPress(state)
                                    .shadow(elevation.value, RoundedCornerShape(16.dp)),
                        ) {
                            // Use SplitButton for Player Item
                            SplitButton(
                                modifier = Modifier.fillMaxWidth(),
                                mainContent = {
                                    // Checkbox/Color Indicator
                                    Box(
                                        modifier =
                                            Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(player.avatarColor),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = player.name.firstOrNull()?.toString()?.uppercase() ?: "?",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black.copy(alpha = 0.7f),
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
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
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Surface(
                                                    color = MaterialTheme.colorScheme.primaryContainer,
                                                    shape = RoundedCornerShape(4.dp),
                                                ) {
                                                    Text(
                                                        "HOST",
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
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
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextButton(
                            onClick = { viewModel.updatePlayerCount(players.size - 1) },
                            enabled = players.size > 3,
                        ) {
                            Icon(
                                Icons.Rounded.Remove,
                                contentDescription = "Remove Player",
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Remove")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Start Game Button
            FilledTonalButton(
                onClick = onStartGame,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors =
                    ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
            ) {
                Text(
                    text = "Start Game",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
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
        containerColor = MaterialTheme.colorScheme.surface,
        scrimColor = Color.Black.copy(alpha = 0.8f), // Slight opacity difference
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Configure Players",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Player Count
            Text(
                "Number of Players",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                IconButton(
                    onClick = { if (currentCount > 3) onUpdateCount(currentCount - 1) },
                    enabled = currentCount > 3,
                ) {
                    Icon(Icons.Rounded.Remove, contentDescription = "Decrease")
                }

                Text(
                    text = "$currentCount",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                )

                IconButton(
                    onClick = { onUpdateCount(currentCount + 1) },
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Increase")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Imposter Count
            Text(
                "Number of Imposters",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                IconButton(
                    onClick = { if (currentImposterCount > 1) onUpdateImposterCount(currentImposterCount - 1) },
                    enabled = currentImposterCount > 1,
                ) {
                    Icon(Icons.Rounded.Remove, contentDescription = "Decrease Imposters")
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
                    Icon(Icons.Rounded.Add, contentDescription = "Increase Imposters")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

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
                        .height(48.dp),
            ) {
                Text("Done")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CategoryBottomSheet(
    currentCategory: String,
    onDismiss: () -> Unit,
    onCategorySelected: (String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val categories = WordRepository.categories.keys.toList()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            item {
                Text(
                    "Select Category",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
            }

            items(categories) { category ->
                ListItem(
                    headlineContent = { Text(category) },
                    leadingContent = {
                        RadioButton(
                            selected = category == currentCategory,
                            onClick = { onCategorySelected(category) },
                            colors =
                                RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                ),
                        )
                    },
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onCategorySelected(category) },
                    colors =
                        ListItemDefaults.colors(
                            containerColor =
                                if (category == currentCategory) {
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                } else {
                                    Color.Transparent
                                },
                        ),
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
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
                    .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            Text(
                "Rename Player",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Player Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
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
                            .height(48.dp),
                ) {
                    Text("Cancel")
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
                            .height(48.dp),
                ) {
                    Text("Save")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    subLabel: String,
    onClick: () -> Unit = {},
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.height(110.dp),
        shape = MaterialTheme.shapes.medium,
        colors =
            CardDefaults.elevatedCardColors(
                containerColor = color,
            ),
        elevation =
            CardDefaults.elevatedCardElevation(
                defaultElevation = 2.dp,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp),
                )
            }
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
                if (subLabel.isNotEmpty()) {
                    Text(
                        text = subLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black.copy(alpha = 0.5f),
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
                defaultElevation = if (player.isHost) 4.dp else 1.dp,
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
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
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(player.avatarColor.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = player.name.firstOrNull()?.uppercase() ?: "?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black.copy(alpha = 0.8f),
                        )
                    }
                    // Host Badge
                    if (player.isHost) {
                        Surface(
                            modifier =
                                Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(end = 2.dp),
                            shape = RoundedCornerShape(4.dp),
                            color = CardYellow,
                            tonalElevation = 2.dp,
                        ) {
                            Text(
                                "HOST",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
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
                        text = "Hold to move",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Edit Button
            IconButton(onClick = onEditClick) {
                Icon(
                    Icons.Rounded.Edit,
                    contentDescription = "Edit Name",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
