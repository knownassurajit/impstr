package com.example.imposter.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.imposter.data.WordRepository
import com.example.imposter.ui.theme.CardPurple
import com.example.imposter.ui.theme.CardRed
import com.example.imposter.ui.theme.CardTeal
import com.example.imposter.ui.theme.CardYellow
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
    val isMe: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: com.example.imposter.ui.viewmodel.GameViewModel,
    onStartGame: () -> Unit,
    onHelpClick: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value
    var showPlayerConfig by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf<Int?>(null) }
    
    // Map ViewModel PlayerState to UI LobbyPlayer
    val players = uiState.players.mapIndexed { index, playerState ->
        LobbyPlayer(
            id = playerState.id,
            name = playerState.name,
            avatarColor = when(index % 4) {
                0 -> CardYellow
                1 -> CardPurple
                2 -> CardRed
                else -> CardTeal
            },
            isReady = playerState.isReady,
            isHost = index == 0,
            isMe = index == 0
        )
    }

    if (showPlayerConfig) {
        PlayerConfigBottomSheet(
            currentCount = players.size,
            currentImposterCount = uiState.imposterCount,
            onDismiss = { showPlayerConfig = false },
            onUpdateCount = { count -> viewModel.updatePlayerCount(count) },
            onUpdateImposterCount = { count -> viewModel.updateImposterCount(count) }
        )
    }

    if (showCategoryDialog) {
        CategoryBottomSheet(
            currentCategory = uiState.category,
            onDismiss = { showCategoryDialog = false },
            onCategorySelected = { category -> 
                viewModel.updateCategory(category)
                showCategoryDialog = false
            }
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
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "IMPSTR",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Black
                )
                Row {
                    IconButton(onClick = onHelpClick) {
                        Icon(
                            Icons.Rounded.Info,
                            contentDescription = "Help",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Info Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoCard(
                    modifier = Modifier.weight(1f),
                    color = CardYellow,
                    icon = Icons.Rounded.Person,
                    label = "Players",
                    value = "${uiState.imposterCount}/${players.size}",
                    subLabel = "Imposters",
                    onClick = { showPlayerConfig = true }
                )
                InfoCard(
                    modifier = Modifier.weight(1f),
                    color = CardTeal,
                    icon = Icons.Rounded.Info,
                    label = "Category",
                    value = uiState.category,
                    subLabel = "",
                    onClick = { showCategoryDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Players List Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Players",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = { 
                    viewModel.updatePlayerCount(players.size + 1)
                }) {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            // Players List (Reorderable)
            val state = rememberReorderableLazyListState(onMove = { from, to ->
                viewModel.reorderPlayers(from.index, to.index)
            })

            LazyColumn(
                state = state.listState,
                modifier = Modifier
                    .weight(1f)
                    .reorderable(state),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(players, key = { it.id }) { player ->
                    ReorderableItem(
                        reorderableState = state,
                        key = player.id
                    ) { isDragging ->
                        val elevation = animateDpAsState(if (isDragging) 8.dp else 0.dp)
                        val index = players.indexOfFirst { it.id == player.id }
                        
                        PlayerListItem(
                            player = player,
                            modifier = Modifier
                                .detectReorderAfterLongPress(state)
                                .shadow(elevation.value, RoundedCornerShape(16.dp)),
                            onEditClick = { if (index >= 0) showRenameDialog = index }
                        )
                    }
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { viewModel.updatePlayerCount(players.size - 1) },
                            enabled = players.size > 3
                        ) {
                            Icon(
                                Icons.Rounded.Remove,
                                contentDescription = "Remove Player",
                                modifier = Modifier.size(18.dp)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = androidx.compose.material3.ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Start Game",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.Rounded.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
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
    onUpdateImposterCount: (Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Configure Players",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Player Count
            Text(
                "Number of Players",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                IconButton(
                    // The button is disabled UI-wise if count is 3 or less
                    onClick = { if (currentCount > 3) onUpdateCount(currentCount - 1) }, 
                    enabled = currentCount > 3 
                ) {
                    // Ensure you have the 'material-icons-extended' dependency for .Remove
                    Icon(Icons.Rounded.Remove, contentDescription = "Decrease")
                }
                
                Text(
                    text = "$currentCount",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(
                    onClick = { onUpdateCount(currentCount + 1) }
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Increase")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Imposter Count
            Text(
                "Number of Imposters",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                IconButton(
                    onClick = { if (currentImposterCount > 1) onUpdateImposterCount(currentImposterCount - 1) },
                    enabled = currentImposterCount > 1
                ) {
                    Icon(Icons.Rounded.Remove, contentDescription = "Decrease Imposters")
                }
                
                Text(
                    text = "$currentImposterCount",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(
                    onClick = { if (currentImposterCount < currentCount - 1) onUpdateImposterCount(currentImposterCount + 1) },
                    enabled = currentImposterCount < currentCount - 1
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
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
    onCategorySelected: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val categories = WordRepository.categories.keys.toList()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Text(
                    "Select Category",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            items(categories) { category ->
                ListItem(
                    headlineContent = { Text(category) },
                    leadingContent = {
                        RadioButton(
                            selected = category == currentCategory,
                            onClick = { onCategorySelected(category) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onCategorySelected(category) },
                    colors = ListItemDefaults.colors(
                        containerColor = if (category == currentCategory) 
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        else Color.Transparent
                    )
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
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                "Rename Player",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Player Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
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
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
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
    onClick: () -> Unit = {}
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = color
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1
                )
                if (subLabel.isNotEmpty()) {
                    Text(
                        text = subLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black.copy(alpha = 0.5f)
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
    onEditClick: () -> Unit
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(player.avatarColor.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = player.name.firstOrNull()?.uppercase() ?: "?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black.copy(alpha = 0.8f)
                        )
                    }
                    // Host Badge
                    if (player.isHost) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 2.dp),
                            shape = RoundedCornerShape(4.dp),
                            color = CardYellow,
                            tonalElevation = 2.dp
                        ) {
                            Text(
                                "HOST",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
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
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                       text = "Hold to move",
                       style = MaterialTheme.typography.labelSmall,
                       color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Edit Button
            IconButton(onClick = onEditClick) {
                Icon(
                    Icons.Rounded.Edit,
                    contentDescription = "Edit Name",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
