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
fun RoutineExerciseScreen(
    navController: NavController,
    viewModel: RoutineViewModel,
    routineId: Int
) {
    val routines by viewModel.routines.collectAsState(initial = emptyList())
    val routine = routines.firstOrNull { it.id == routineId }

    // UI-only per-exercise timer (seconds)
    val timerMap = remember(routineId) { mutableStateMapOf<Int, String>() }

    // UI-only per-exercise equipment list
    val equipmentMap = remember(routineId) { mutableStateMapOf<Int, MutableList<String>>() }
    val equipmentInputMap = remember(routineId) { mutableStateMapOf<Int, String>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(routine?.name ?: "Routine") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->

        if (routine == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text("Routine not found.")
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                Text("Exercises", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
            }

            itemsIndexed(routine.exercises) { index, exerciseName ->
                // init defaults
                if (!timerMap.containsKey(index)) timerMap[index] = "30"
                if (!equipmentMap.containsKey(index)) equipmentMap[index] = mutableListOf()
                if (!equipmentInputMap.containsKey(index)) equipmentInputMap[index] = ""

                val timerValue = timerMap[index] ?: "30"
                val eqList = equipmentMap[index] ?: mutableListOf()
                val eqInput = equipmentInputMap[index] ?: ""

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {

                        Text(
                            text = "${index + 1}. $exerciseName",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(Modifier.height(10.dp))

                        // -------- Timer per exercise (UI only) --------
                        OutlinedTextField(
                            value = timerValue,
                            onValueChange = { newValue ->
                                // allow only digits (optional safety)
                                timerMap[index] = newValue.filter { it.isDigit() }.take(4)
                            },
                            label = { Text("Timer (seconds)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(Modifier.height(10.dp))

                        // -------- Equipment per exercise (UI only) --------
                        Text("Equipment", style = MaterialTheme.typography.titleSmall)

                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = eqInput,
                                onValueChange = { equipmentInputMap[index] = it },
                                label = { Text("Add equipment") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val item = eqInput.trim()
                                    if (item.isNotBlank()) {
                                        eqList.add(item)
                                        equipmentInputMap[index] = ""
                                        equipmentMap[index] = eqList
                                    }
                                }
                            ) { Text("Add") }
                        }

                        if (eqList.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "• " + eqList.joinToString("\n• "),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            Spacer(Modifier.height(6.dp))
                            Text("None", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
