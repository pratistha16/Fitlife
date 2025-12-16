package com.example.fitlife

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    // ✅ Observe logged-in user
    val currentUser by authViewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(40.dp))

        // ✅ Welcome message
        Text(
            text = "Welcome, ${currentUser?.name ?: "User"} 👋",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { navController.navigate(Screen.Manage.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Manage My Workouts")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate(Screen.CreateRoutine.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Workout Routine")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate(Screen.Checklist.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Checklist")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 🚪 LOGOUT BUTTON
        OutlinedButton(
            onClick = {
                authViewModel.logout()
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}
