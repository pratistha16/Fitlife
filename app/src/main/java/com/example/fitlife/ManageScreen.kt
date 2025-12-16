// ManageScreen.kt
package com.example.fitlife

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageScreen(
    navController: NavController,
    viewModel: RoutineViewModel,
    authViewModel: AuthViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val routines by viewModel.routines.collectAsState(initial = emptyList())

    // ✅ UI-level safety filter (still do DB filtering too!)
    val visibleRoutines = remember(currentUser, routines) {
        val uid = currentUser?.id
        if (uid == null) emptyList() else routines.filter { it.ownerUserId == uid }
    }

    // ✅ If user becomes null (logout), go back to login
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
                title = { Text("Manage My Workouts") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { navController.navigate(Screen.CreateRoutine.route) }) {
                        Text("New")
                    }
                }
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
                Text("Please login first.")
            }
            return@Scaffold
        }

        if (visibleRoutines.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No routines yet. Tap 'New' to create one.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(visibleRoutines) { routine ->
                    val exerciseCount = routine.exercises.size
                    val approxMinutes = exerciseCount * 1 // simple estimate

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(
                                    Screen.RoutineExercises.route(routine.id)
                                )
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = routine.name,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "$exerciseCount exercises • ~${approxMinutes} min",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            if (routine.isDone) {
                                Text(
                                    text = "Completed",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                            }

                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                IconButton(
                                    onClick = { viewModel.deleteRoutine(routine.id) }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }

                                IconButton(
                                    onClick = {
                                        // ✅ correct param name: done
                                        viewModel.setDone(
                                            id = routine.id,
                                            done = !routine.isDone
                                        )
                                    }
                                ) {
                                    Icon(Icons.Default.Done, contentDescription = "Mark done")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
