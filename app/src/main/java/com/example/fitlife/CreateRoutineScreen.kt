package com.example.fitlife

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoutineScreen(
    navController: NavController,
    routineViewModel: RoutineViewModel,
    authViewModel: AuthViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val userId: Int? = currentUser?.id

    // Routine info
    var routineName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var routineImageUri by remember { mutableStateOf<String?>(null) }

    // Exercise input fields
    var exerciseName by remember { mutableStateOf("") }
    var durationText by remember { mutableStateOf("30") }
    var setsText by remember { mutableStateOf("") }
    var repsText by remember { mutableStateOf("") }
    var exerciseImageUri by remember { mutableStateOf<String?>(null) }
    var exerciseVideoUri by remember { mutableStateOf<String?>(null) }

    // Equipment input
    var equipmentInput by remember { mutableStateOf("") }
    val currentEquipment = remember { mutableStateListOf<String>() }

    // Final exercise list
    val exerciseItems = remember { mutableStateListOf<ExerciseItem>() }

    var errorMsg by remember { mutableStateOf<String?>(null) }
    var showAddExerciseDialog by remember { mutableStateOf(false) }

    // Image pickers
    val routineImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        routineImageUri = uri?.toString()
    }

    val exerciseImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        exerciseImageUri = uri?.toString()
    }

    val exerciseVideoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        exerciseVideoUri = uri?.toString()
    }

    fun clearExerciseInputs() {
        exerciseName = ""
        durationText = "30"
        setsText = ""
        repsText = ""
        equipmentInput = ""
        currentEquipment.clear()
        exerciseImageUri = null
        exerciseVideoUri = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Routine") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->

        if (userId == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Please login to create routines")
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { navController.navigate(Screen.Login.route) }) {
                        Text("Go to Login")
                    }
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(Modifier.height(8.dp)) }

            // Routine Cover Image
            item {
                Text(
                    "Routine Cover",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (routineImageUri == null)
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.secondaryContainer
                                    )
                                )
                            else
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.surface,
                                        MaterialTheme.colorScheme.surface
                                    )
                                )
                        )
                        .clickable { routineImagePicker.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (routineImageUri != null) {
                        AsyncImage(
                            model = routineImageUri,
                            contentDescription = "Routine cover",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        // Overlay for change button
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            FilledTonalIconButton(onClick = { routineImagePicker.launch("image/*") }) {
                                Icon(Icons.Default.Edit, contentDescription = "Change")
                            }
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Add Cover Image",
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // Routine Name
            item {
                OutlinedTextField(
                    value = routineName,
                    onValueChange = { routineName = it },
                    label = { Text("Routine Name") },
                    placeholder = { Text("e.g., Morning HIIT") },
                    leadingIcon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Notes
            item {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Description (optional)") },
                    placeholder = { Text("Add notes or instructions...") },
                    leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Exercises Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Exercises",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    FilledTonalButton(
                        onClick = { showAddExerciseDialog = true }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Add")
                    }
                }
            }

            // Exercise Cards
            if (exerciseItems.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.DirectionsRun,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "No exercises yet",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Tap 'Add' to create your first exercise",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            items(exerciseItems) { exercise ->
                ExerciseCard(
                    exercise = exercise,
                    onRemove = { exerciseItems.remove(exercise) }
                )
            }

            // Error message
            errorMsg?.let { msg ->
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(msg, color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }
            }

            // Save Button
            item {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        errorMsg = null
                        val name = routineName.trim()
                        if (name.isBlank()) {
                            errorMsg = "Routine name is required"
                            return@Button
                        }
                        if (exerciseItems.isEmpty()) {
                            errorMsg = "Add at least one exercise"
                            return@Button
                        }

                        val routine = WorkoutRoutine(
                            ownerUserId = userId,
                            name = name,
                            exercises = exerciseItems.toList(),
                            notes = notes.trim().ifBlank { null },
                            photoUri = routineImageUri
                        )

                        routineViewModel.addRoutine(routine)
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Save Routine", style = MaterialTheme.typography.titleMedium)
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }

        // Add Exercise Dialog
        if (showAddExerciseDialog) {
            AlertDialog(
                onDismissRequest = {
                    showAddExerciseDialog = false
                    clearExerciseInputs()
                },
                title = { Text("Add Exercise") },
                text = {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Exercise media preview
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Image picker
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { exerciseImagePicker.launch("image/*") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (exerciseImageUri != null) {
                                        AsyncImage(
                                            model = exerciseImageUri,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(Icons.Default.Image, contentDescription = null)
                                            Text("Image", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }

                                // Video picker
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { exerciseVideoPicker.launch("video/*") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (exerciseVideoUri != null) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(MaterialTheme.colorScheme.primaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.PlayCircle,
                                                contentDescription = null,
                                                modifier = Modifier.size(40.dp)
                                            )
                                        }
                                    } else {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(Icons.Default.VideoLibrary, contentDescription = null)
                                            Text("Video", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            OutlinedTextField(
                                value = exerciseName,
                                onValueChange = { exerciseName = it },
                                label = { Text("Exercise Name") },
                                placeholder = { Text("e.g., Push-ups") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = durationText,
                                onValueChange = { durationText = it.filter(Char::isDigit) },
                                label = { Text("Duration (seconds)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = setsText,
                                    onValueChange = { setsText = it.filter(Char::isDigit) },
                                    label = { Text("Sets") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = repsText,
                                    onValueChange = { repsText = it.filter(Char::isDigit) },
                                    label = { Text("Reps") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = equipmentInput,
                                    onValueChange = { equipmentInput = it },
                                    label = { Text("Equipment") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                FilledTonalIconButton(
                                    onClick = {
                                        val v = equipmentInput.trim()
                                        if (v.isNotBlank()) {
                                            currentEquipment.add(v)
                                            equipmentInput = ""
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add")
                                }
                            }
                        }

                        if (currentEquipment.isNotEmpty()) {
                            item {
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(currentEquipment.toList()) { eq ->
                                        InputChip(
                                            selected = false,
                                            onClick = { currentEquipment.remove(eq) },
                                            label = { Text(eq) },
                                            trailingIcon = {
                                                Icon(
                                                    Icons.Default.Close,
                                                    contentDescription = "Remove",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val name = exerciseName.trim()
                            val duration = durationText.toIntOrNull() ?: 0

                            if (name.isBlank() || duration <= 0) return@Button

                            exerciseItems.add(
                                ExerciseItem(
                                    name = name,
                                    durationSeconds = duration,
                                    equipment = currentEquipment.toList(),
                                    sets = setsText.toIntOrNull(),
                                    reps = repsText.toIntOrNull(),
                                    imageUri = exerciseImageUri,
                                    videoUri = exerciseVideoUri
                                )
                            )

                            showAddExerciseDialog = false
                            clearExerciseInputs()
                        }
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showAddExerciseDialog = false
                        clearExerciseInputs()
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: ExerciseItem,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (exercise.imageUri != null) {
                    AsyncImage(
                        model = exercise.imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (exercise.videoUri != null) {
                    Icon(
                        Icons.Default.PlayCircle,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                } else {
                    Icon(
                        Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    buildString {
                        append("${exercise.durationSeconds}s")
                        exercise.sets?.let { append(" • $it sets") }
                        exercise.reps?.let { append(" • $it reps") }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (exercise.equipment.isNotEmpty()) {
                    Text(
                        exercise.equipment.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
