package com.example.fitlife

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoutineScreen(
    navController: NavController,
    routineViewModel: RoutineViewModel,
    authViewModel: AuthViewModel
) {
    // --- current user ---
    val currentUser by authViewModel.currentUser.collectAsState()
    val userId: Int? = currentUser?.id   // make sure User has "id: Int"

    // Routine info
    var routineName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    // Exercise input fields
    var exerciseName by remember { mutableStateOf("") }
    var durationText by remember { mutableStateOf("") } // seconds
    var setsText by remember { mutableStateOf("") }
    var repsText by remember { mutableStateOf("") }

    // Equipment input for CURRENT exercise
    var equipmentInput by remember { mutableStateOf("") }
    val currentEquipment = remember { mutableStateListOf<String>() }

    // Final exercise list for the routine
    val exerciseItems = remember { mutableStateListOf<ExerciseItem>() }

    // UI error message
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun clearExerciseInputs() {
        exerciseName = ""
        durationText = ""
        setsText = ""
        repsText = ""
        equipmentInput = ""
        currentEquipment.clear()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Routine") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ✅ If user not logged in
            if (userId == null) {
                item {
                    Text(
                        text = "You must be logged in to create routines.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Go to Login")
                    }
                }
                return@LazyColumn
            }

            // ---------------- Routine Name ----------------
            item {
                OutlinedTextField(
                    value = routineName,
                    onValueChange = { routineName = it },
                    label = { Text("Routine Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ---------------- Notes ----------------
            item {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Instructions (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Divider()
                Text(
                    "Add Exercise (timer + equipment per exercise)",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // ---------------- Exercise Name ----------------
            item {
                OutlinedTextField(
                    value = exerciseName,
                    onValueChange = { exerciseName = it },
                    label = { Text("Exercise Name (e.g. Push-ups)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ---------------- Duration ----------------
            item {
                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it.filter(Char::isDigit) },
                    label = { Text("Timer (seconds) e.g. 60") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ---------------- Sets + Reps ----------------
            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = setsText,
                        onValueChange = { setsText = it.filter(Char::isDigit) },
                        label = { Text("Sets (optional)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(
                        value = repsText,
                        onValueChange = { repsText = it.filter(Char::isDigit) },
                        label = { Text("Reps (optional)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ---------------- Equipment Add ----------------
            item {
                Text("Equipment for this exercise", style = MaterialTheme.typography.titleSmall)

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = equipmentInput,
                        onValueChange = { equipmentInput = it },
                        label = { Text("Add equipment (e.g. Dumbbells)") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val v = equipmentInput.trim()
                            if (v.isNotBlank()) {
                                currentEquipment.add(v)
                                equipmentInput = ""
                            }
                        }
                    ) {
                        Text("Add")
                    }
                }
            }

            // Show current equipment list (for current exercise)
            items(currentEquipment) { eq ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• $eq")
                        TextButton(onClick = { currentEquipment.remove(eq) }) {
                            Text("Remove")
                        }
                    }
                }
            }

            // ---------------- Add Exercise Button ----------------
            item {
                Button(
                    onClick = {
                        errorMsg = null

                        val name = exerciseName.trim()
                        val duration = durationText.toIntOrNull() ?: 0
                        val sets = setsText.toIntOrNull()
                        val reps = repsText.toIntOrNull()

                        if (name.isBlank()) {
                            errorMsg = "Exercise name is required."
                            return@Button
                        }
                        if (duration <= 0) {
                            errorMsg = "Timer must be greater than 0 seconds."
                            return@Button
                        }

                        exerciseItems.add(
                            ExerciseItem(
                                name = name,
                                durationSeconds = duration,
                                equipment = currentEquipment.toList(),
                                sets = sets,
                                reps = reps
                            )
                        )

                        clearExerciseInputs()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Exercise to Routine")
                }
            }

            // Error message
            item {
                errorMsg?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }

            item {
                Divider()
                Text("Exercises added:", style = MaterialTheme.typography.titleMedium)
            }

            // ---------------- Show added exercises ----------------
            items(exerciseItems) { ex ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("✅ ${ex.name}", style = MaterialTheme.typography.titleMedium)
                        Text("Timer: ${ex.durationSeconds} seconds")
                        ex.sets?.let { Text("Sets: $it") }
                        ex.reps?.let { Text("Reps: $it") }

                        Text(
                            text = if (ex.equipment.isNotEmpty())
                                "Equipment: ${ex.equipment.joinToString(", ")}"
                            else
                                "Equipment: None"
                        )

                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = { exerciseItems.remove(ex) }) {
                            Text("Remove Exercise")
                        }
                    }
                }
            }

            // ---------------- Save Routine ----------------
            item {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        errorMsg = null

                        val name = routineName.trim()
                        if (name.isBlank()) {
                            errorMsg = "Routine name is required."
                            return@Button
                        }
                        if (exerciseItems.isEmpty()) {
                            errorMsg = "Add at least 1 exercise before saving."
                            return@Button
                        }

                        val routine = WorkoutRoutine(
                            ownerUserId = userId, // ✅ IMPORTANT: filter routines per user
                            name = name,
                            exercises = exerciseItems.toList(),
                            notes = notes.trim().ifBlank { null }
                        )

                        routineViewModel.addRoutine(routine)
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SAVE ROUTINE")
                }
            }

            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}
