package com.example.fitlife

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSessionScreen(
    navController: NavController,
    viewModel: RoutineViewModel,
    routineId: Int
) {
    val context = LocalContext.current
    val routines by viewModel.routines.collectAsState(initial = emptyList())
    val routine = routines.firstOrNull { it.id == routineId }

    var currentIndex by remember { mutableStateOf(0) }
    var remainingSeconds by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var isCompleted by remember { mutableStateOf(false) }

    val currentExercise = routine?.exercises?.getOrNull(currentIndex)
    val totalExercises = routine?.exercises?.size ?: 0
    val isLastExercise = currentIndex == totalExercises - 1

    // Video player
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    // Update video when exercise changes
    LaunchedEffect(currentExercise?.videoUri) {
        currentExercise?.videoUri?.let { uri ->
            exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(uri)))
            exoPlayer.prepare()
            exoPlayer.playWhenReady = false
        } ?: run {
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Initialize timer when exercise changes
    LaunchedEffect(currentIndex, routine) {
        currentExercise?.let {
            remainingSeconds = it.durationSeconds
            isRunning = false
        }
    }

    // Timer countdown
    LaunchedEffect(isRunning, remainingSeconds) {
        if (isRunning && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
        } else if (remainingSeconds == 0 && isRunning) {
            isRunning = false
        }
    }

    if (routine == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Completed screen
    if (isCompleted) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(Modifier.height(32.dp))

                Text(
                    "Workout Complete!",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "🎉 Great job! 🎉",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            routine.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "$totalExercises",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text("Exercises", style = MaterialTheme.typography.bodySmall)
                            }
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
                        }
                    }
                }

                Spacer(Modifier.height(48.dp))

                Button(
                    onClick = {
                        viewModel.setDone(routine.id, true)
                        navController.popBackStack()
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Done", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Exit")
                    }
                },
                actions = {
                    Text(
                        "${currentIndex + 1} / $totalExercises",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Progress bar
            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / totalExercises },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
            )

            // Media section (Image or Video)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                when {
                    currentExercise?.videoUri != null -> {
                        // Video player
                        AndroidView(
                            factory = { ctx ->
                                PlayerView(ctx).apply {
                                    player = exoPlayer
                                    useController = true
                                    layoutParams = FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    currentExercise?.imageUri != null -> {
                        // Image
                        AsyncImage(
                            model = currentExercise.imageUri,
                            contentDescription = currentExercise.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                    else -> {
                        // Placeholder
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.FitnessCenter,
                                contentDescription = null,
                                modifier = Modifier.size(100.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            // Exercise info and controls
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Exercise name
                    Text(
                        currentExercise?.name ?: "",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    // Equipment chips
                    if (currentExercise?.equipment?.isNotEmpty() == true) {
                        Spacer(Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            currentExercise.equipment.take(3).forEach { eq ->
                                AssistChip(
                                    onClick = {},
                                    label = { Text(eq, style = MaterialTheme.typography.bodySmall) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.FitnessCenter,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Timer
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                formatTime(remainingSeconds),
                                fontSize = 44.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            if (!isRunning && remainingSeconds > 0) {
                                Text(
                                    "TAP TO START",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Control buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Reset
                        FilledTonalIconButton(
                            onClick = {
                                isRunning = false
                                remainingSeconds = currentExercise?.durationSeconds ?: 30
                            },
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset")
                        }

                        // Play/Pause
                        FilledIconButton(
                            onClick = {
                                isRunning = !isRunning
                                if (isRunning && currentExercise?.videoUri != null) {
                                    exoPlayer.play()
                                } else {
                                    exoPlayer.pause()
                                }
                            },
                            modifier = Modifier.size(80.dp)
                        ) {
                            Icon(
                                if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isRunning) "Pause" else "Play",
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        // Skip
                        FilledTonalIconButton(
                            onClick = {
                                isRunning = false
                                exoPlayer.stop()
                                if (isLastExercise) {
                                    isCompleted = true
                                } else {
                                    currentIndex++
                                }
                            },
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(Icons.Default.SkipNext, contentDescription = "Skip")
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Next/Complete button
                    Button(
                        onClick = {
                            isRunning = false
                            exoPlayer.stop()
                            if (isLastExercise) {
                                isCompleted = true
                            } else {
                                currentIndex++
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = if (isLastExercise)
                            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                        else
                            ButtonDefaults.buttonColors()
                    ) {
                        Text(
                            if (isLastExercise) "Complete Routine" else "Next Exercise",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            if (isLastExercise) Icons.Default.Check else Icons.Default.ArrowForward,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}
