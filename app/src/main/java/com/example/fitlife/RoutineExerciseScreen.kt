package com.example.fitlife

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineExerciseScreen(
    navController: NavController,
    viewModel: RoutineViewModel,
    routineId: Int
) {
    val routines by viewModel.routines.collectAsState(initial = emptyList())
    val routine = routines.firstOrNull { it.id == routineId }
    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        floatingActionButton = {
            routine?.let {
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate(Screen.WorkoutSession.route(routineId)) },
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                    text = { Text("Start Workout") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                )
            }
        }
    ) { paddingValues ->

        if (routine == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Routine not found", style = MaterialTheme.typography.titleMedium)
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.background
                            )
                        ),
                        shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Spacer(Modifier.height(24.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        routine.name,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Text(
                        "${routine.exercises.size} exercises",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Summary card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "${routine.exercises.size}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text("Exercises", style = MaterialTheme.typography.bodySmall)
                            }

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(40.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant)
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                val totalTime = routine.exercises.sumOf { it.durationSeconds }
                                Text(
                                    "${totalTime / 60}:${String.format("%02d", totalTime % 60)}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text("Duration", style = MaterialTheme.typography.bodySmall)
                            }

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(40.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant)
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    if (routine.isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp),
                                    tint = if (routine.isDone) Color(0xFF00B894) else MaterialTheme.colorScheme.outline
                                )
                                Text(
                                    if (routine.isDone) "Done" else "Pending",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Location Info (if available)
                if (routine.latitude != null && routine.longitude != null) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val uri =
                                        Uri.parse("geo:${routine.latitude},${routine.longitude}?q=${routine.latitude},${routine.longitude}(Workout Location)")
                                    val intent = Intent(Intent.ACTION_VIEW, uri)
                                    intent.setPackage("com.google.android.apps.maps")
                                    if (intent.resolveActivity(context.packageManager) != null) {
                                        context.startActivity(intent)
                                    } else {
                                        val browserIntent = Intent(Intent.ACTION_VIEW, uri)
                                        context.startActivity(browserIntent)
                                    }
                                },
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(
                                    alpha = 0.5f
                                )
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            MaterialTheme.colorScheme.tertiary,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onTertiary
                                    )
                                }
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text(
                                        routine.locationName ?: "Tagged Location",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        "View on Map",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Exercises",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Exercise list
                itemsIndexed(routine.exercises) { index, exercise ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Number badge
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer.copy(
                                            alpha = 0.5f
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${index + 1}",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    exercise.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Timer,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        "${exercise.durationSeconds}s",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    if (exercise.equipment.isNotEmpty()) {
                                        Spacer(Modifier.width(12.dp))
                                        Icon(
                                            Icons.Default.FitnessCenter,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            exercise.equipment.joinToString(", "),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom spacer for FAB
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

