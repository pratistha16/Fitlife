package com.example.fitlife

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(
    navController: NavController,
    routineViewModel: RoutineViewModel
) {
    // Make sure routines is a LIST
    val routines: List<WorkoutRoutine> by routineViewModel.routines.collectAsState(initial = emptyList())

    // Build checklist from routines
    val checklistItems: List<String> = remember(routines) {
        val list = mutableListOf<String>()

        for (routine in routines) {
            for (exercise in routine.exercises) {

                // Exercise line
                list.add("${routine.name} – Exercise: ${exercise.name} (${exercise.durationSeconds}s)")

                // Equipment lines
                if (exercise.equipment.isNotEmpty()) {
                    for (eq in exercise.equipment) {
                        list.add("${routine.name} – Equipment: $eq")
                    }
                }
            }
        }

        list
    }

    // Checkbox states
    val checkedStates = remember(checklistItems) {
        mutableStateListOf<Boolean>().apply {
            clear()
            addAll(List(checklistItems.size) { false })
        }
    }

    // Selected lines for sending
    val selected: List<String> = remember(checklistItems, checkedStates) {
        checklistItems.filterIndexed { index, _ ->
            checkedStates.getOrNull(index) == true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workout Checklist") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Button(
                    onClick = {
                        val msg = if (selected.isEmpty()) {
                            "No items selected."
                        } else {
                            selected.joinToString("\n")
                        }
                        navController.navigate(Screen.Delegate.route(msg))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    enabled = checklistItems.isNotEmpty()
                ) {
                    Text("Send Selected via SMS")
                }
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            if (checklistItems.isEmpty()) {
                item { Text("No items yet. Create a routine first.") }
            } else {
                itemsIndexed(checklistItems) { index, text ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = text, modifier = Modifier.weight(1f))

                        Checkbox(
                            checked = checkedStates.getOrNull(index) ?: false,
                            onCheckedChange = { checked ->
                                if (index < checkedStates.size) checkedStates[index] = checked
                            }
                        )
                    }
                }
            }
        }
    }
}
