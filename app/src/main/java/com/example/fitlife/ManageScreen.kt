package com.example.fitlife

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageScreen(
    navController: NavController,
    viewModel: RoutineViewModel,
    authViewModel: AuthViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    LaunchedEffect(currentUser?.id) {
        currentUser?.id?.let { uid ->
            viewModel.setUser(uid)
        }
    }

    val routines by viewModel.routines.collectAsState(initial = emptyList())

    val visibleRoutines = remember(currentUser?.id, routines) {
        val uid = currentUser?.id
        if (uid == null) emptyList() else routines.filter { it.ownerUserId == uid }
    }

    var selectedFilter by remember { mutableStateOf("All") }

    val filteredRoutines = remember(visibleRoutines, selectedFilter) {
        when (selectedFilter) {
            "Completed" -> visibleRoutines.filter { it.isDone }
            "Pending" -> visibleRoutines.filter { !it.isDone }
            else -> visibleRoutines
        }
    }

    val completedCount = visibleRoutines.count { it.isDone }
    val pendingCount = visibleRoutines.size - completedCount

    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Workouts") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(navController) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Screen.CreateRoutine.route) },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Routine") },
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    ) { paddingValues ->

        if (currentUser == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 90.dp, top = 16.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatChip(
                        label = "Total",
                        value = "${visibleRoutines.size}",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatChip(
                        label = "Completed",
                        value = "$completedCount",
                        color = Color(0xFF00B894),
                        modifier = Modifier.weight(1f)
                    )
                    StatChip(
                        label = "Pending",
                        value = "$pendingCount",
                        color = Color(0xFFE17055),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Filter chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listOf("All", "Completed", "Pending")) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) },
                            leadingIcon = if (selectedFilter == filter) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }
            }

            if (filteredRoutines.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.FitnessCenter,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "No routines yet",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Create your first workout routine",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                            Spacer(Modifier.height(24.dp))
                            Button(
                                onClick = { navController.navigate(Screen.CreateRoutine.route) }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Create Routine")
                            }
                        }
                    }
                }
            } else {
                items(filteredRoutines, key = { it.id }) { routine ->
                    val dismissState = rememberDismissState(
                        confirmValueChange = {
                            if (it == DismissValue.DismissedToStart) {
                                viewModel.deleteRoutine(routine.id)
                                true
                            } else if (it == DismissValue.DismissedToEnd) {
                                viewModel.setDone(routine.id, !routine.isDone)
                                false // Don't dismiss, just toggle
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismiss(
                        state = dismissState,
                        modifier = Modifier.animateContentSize(),
                        directions = setOf(DismissDirection.EndToStart, DismissDirection.StartToEnd),
                        background = {
                            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
                            val color by animateColorAsState(
                                when (dismissState.targetValue) {
                                    DismissValue.Default -> MaterialTheme.colorScheme.surface
                                    DismissValue.DismissedToEnd -> Color(0xFF00B894) // Green for Complete
                                    DismissValue.DismissedToStart -> MaterialTheme.colorScheme.error // Red for Delete
                                }
                            )
                            val alignment = when (direction) {
                                DismissDirection.StartToEnd -> Alignment.CenterStart
                                DismissDirection.EndToStart -> Alignment.CenterEnd
                            }
                            val icon = when (direction) {
                                DismissDirection.StartToEnd -> if (routine.isDone) Icons.Default.Undo else Icons.Default.Check
                                DismissDirection.EndToStart -> Icons.Default.Delete
                            }
                            val scale by animateFloatAsState(
                                if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
                            )

                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(color, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 20.dp),
                                contentAlignment = alignment
                            ) {
                                Icon(
                                    icon,
                                    contentDescription = null,
                                    modifier = Modifier.scale(scale),
                                    tint = Color.White
                                )
                            }
                        },
                        dismissContent = {
                            RoutineCard(
                                routine = routine,
                                onClick = {
                                    navController.navigate(Screen.RoutineExercises.route(routine.id))
                                },
                                onDelete = { viewModel.deleteRoutine(routine.id) },
                                onToggleDone = { viewModel.setDone(routine.id, !routine.isDone) }
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatChip(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoutineCard(
    routine: WorkoutRoutine,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onToggleDone: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    val totalDuration = routine.exercises.sumOf { it.durationSeconds }
    val totalCalories = routine.exercises.sumOf { 
        (it.durationSeconds / 60.0 * it.caloriesPerMinute).toInt() 
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Cover image or gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                if (routine.photoUri != null) {
                    AsyncImage(
                        model = routine.photoUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.secondaryContainer
                                    )
                                )
                            )
                    )
                }

                // Overlay gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.5f)
                                )
                            )
                        )
                )

                // Status badge
                if (routine.isDone) {
                    Box(
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        AssistChip(
                            onClick = {},
                            label = { Text("Completed") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color(0xFF00B894),
                                labelColor = Color.White,
                                leadingIconContentColor = Color.White
                            )
                        )
                    }
                }

                // Title overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        routine.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Details
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DetailItem(
                            icon = Icons.Default.FitnessCenter,
                            value = "${routine.exercises.size}",
                            label = "exercises"
                        )
                        DetailItem(
                            icon = Icons.Default.Timer,
                            value = "${totalDuration / 60}",
                            label = "min"
                        )
                        DetailItem(
                            icon = Icons.Default.LocalFireDepartment,
                            value = "$totalCalories",
                            label = "kcal"
                        )
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (routine.isDone) "Mark Incomplete" else "Mark Complete") },
                                onClick = {
                                    onToggleDone()
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        if (routine.isDone) Icons.Default.Undo else Icons.Default.Check,
                                        contentDescription = null
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = {
                                    onDelete()
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            )
                        }
                    }
                }

                routine.notes?.let { notes ->
                    if (notes.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(4.dp))
        Text(
            "$value $label",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}
