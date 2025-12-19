package com.example.fitlife

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    routineViewModel: RoutineViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val nutritionDao = remember { db.nutritionDao() }
    val routines by routineViewModel.routines.collectAsState(initial = emptyList())

    var totalWorkouts by remember { mutableStateOf(currentUser?.totalWorkoutsCompleted ?: 0) }
    var totalCalories by remember { mutableStateOf(currentUser?.totalCaloriesBurned ?: 0) }
    var currentStreakState by remember { mutableStateOf(currentUser?.currentStreak ?: 0) }

    LaunchedEffect(currentUser?.id, routines) {
        val userId = currentUser?.id ?: return@LaunchedEffect

        // Workouts: count completed routines
        totalWorkouts = routines.count { it.isDone }

        // Nutrition: sum all calories across all saved days
        val entries = nutritionDao.getAllForUser(userId)
        totalCalories = entries.sumOf { entry ->
            entry.meals.sumOf { it.calories }
        }

        // Streak: consecutive days with any nutrition activity
        val activityDates = entries
            .filter { it.meals.isNotEmpty() || it.waterGlasses > 0 }
            .mapNotNull { runCatching { LocalDate.parse(it.date) }.getOrNull() }
            .toSet()

        var streak = 0
        var day = LocalDate.now()
        while (activityDates.contains(day)) {
            streak++
            day = day.minusDays(1)
        }
        currentStreakState = streak
    }

    var showEditDialog by remember { mutableStateOf(false) }
    var showGoalsDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Profile Header with Curve
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                ) {
                    // Background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.background
                                    )
                                ),
                                shape = RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp)
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "My Profile",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(Modifier.height(32.dp))

                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                currentUser?.name?.firstOrNull()?.uppercase() ?: "U",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Spacer(Modifier.height(16.dp))
                        
                        Text(
                            currentUser?.name ?: "User",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        Text(
                            currentUser?.email ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                    
                    // Edit Button (Top Right)
                    IconButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            // Stats Cards
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ProfileStatCard(
                        title = "Workouts",
                        value = "$totalWorkouts",
                        icon = Icons.Default.FitnessCenter,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                    ProfileStatCard(
                        title = "Streak",
                        value = "$currentStreakState",
                        icon = Icons.Default.Whatshot,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item { Spacer(Modifier.height(24.dp)) }

            // Body Stats
            item {
                Text(
                    "Body Stats",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            BodyStatItem(
                                label = "Weight",
                                value = currentUser?.weight?.let { "${it.toInt()} kg" } ?: "-- kg"
                            )
                            BodyStatItem(
                                label = "Height",
                                value = currentUser?.height?.let { "${it.toInt()} cm" } ?: "-- cm"
                            )
                            BodyStatItem(
                                label = "Age",
                                value = currentUser?.age?.let { "$it yrs" } ?: "-- yrs"
                            )
                        }

                        if (currentUser?.weight != null && currentUser?.height != null) {
                            Spacer(Modifier.height(24.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Spacer(Modifier.height(24.dp))

                            val bmi = currentUser!!.weight!! / ((currentUser!!.height!! / 100) * (currentUser!!.height!! / 100))
                            val bmiCategory = when {
                                bmi < 18.5 -> "Underweight"
                                bmi < 25 -> "Normal"
                                bmi < 30 -> "Overweight"
                                else -> "Obese"
                            }
                            val bmiColor = when {
                                bmi < 18.5 -> Color(0xFF60A5FA)
                                bmi < 25 -> Color(0xFF34D399)
                                bmi < 30 -> Color(0xFFFBBF24)
                                else -> Color(0xFFF87171)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "BMI Score",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        String.format("%.1f", bmi),
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Surface(
                                    color = bmiColor.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = bmiCategory,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = bmiColor.copy(alpha = 1f) // Darker shade for text usually, but simple here
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            item { Spacer(Modifier.height(24.dp)) }

            // Goals Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Daily Goals",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { showGoalsDialog = true }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Goals",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        GoalItem(
                            icon = Icons.Default.LocalFireDepartment,
                            title = "Calories",
                            value = "${currentUser?.targetCalories ?: 2000} kcal",
                            color = MaterialTheme.colorScheme.secondary
                        )
                        GoalItem(
                            icon = Icons.Default.WaterDrop,
                            title = "Water",
                            value = "${currentUser?.targetWater ?: 8} cups",
                            color = Color(0xFF3B82F6)
                        )
                        GoalItem(
                            icon = Icons.Default.DirectionsWalk,
                            title = "Steps",
                            value = "${currentUser?.targetSteps ?: 10000}",
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            // Logout
            item {
                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Log Out",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Edit Profile Dialog
        if (showEditDialog) {
            EditProfileDialog(
                currentUser = currentUser,
                onDismiss = { showEditDialog = false },
                onSave = { weight, height, age, gender ->
                    authViewModel.updateProfile(weight, height, age, gender)
                    showEditDialog = false
                }
            )
        }

        // Edit Goals Dialog
        if (showGoalsDialog) {
            EditGoalsDialog(
                currentUser = currentUser,
                onDismiss = { showGoalsDialog = false },
                onSave = { calories, water, steps ->
                    // In real app, save to ViewModel
                    // For now assuming the viewmodel updates or we just dismiss
                    showGoalsDialog = false
                }
            )
        }
    }
}

@Composable
private fun ProfileStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                title,
                style = MaterialTheme.typography.labelSmall,
                color = color.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun BodyStatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun GoalItem(
    icon: ImageVector,
    title: String,
    value: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun EditProfileDialog(
    currentUser: User?,
    onDismiss: () -> Unit,
    onSave: (Float?, Float?, Int?, String?) -> Unit
) {
    var weight by remember { mutableStateOf(currentUser?.weight?.toString() ?: "") }
    var height by remember { mutableStateOf(currentUser?.height?.toString() ?: "") }
    var age by remember { mutableStateOf(currentUser?.age?.toString() ?: "") }
    var gender by remember { mutableStateOf(currentUser?.gender ?: "Prefer not to say") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Height (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                // Simplified gender input for dialog
                OutlinedTextField(
                    value = gender,
                    onValueChange = { gender = it },
                    label = { Text("Gender") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        weight.toFloatOrNull(),
                        height.toFloatOrNull(),
                        age.toIntOrNull(),
                        gender
                    )
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditGoalsDialog(
    currentUser: User?,
    onDismiss: () -> Unit,
    onSave: (Int, Int, Int) -> Unit
) {
    var calories by remember { mutableStateOf(currentUser?.targetCalories?.toString() ?: "2000") }
    var water by remember { mutableStateOf(currentUser?.targetWater?.toString() ?: "8") }
    var steps by remember { mutableStateOf(currentUser?.targetSteps?.toString() ?: "10000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Goals") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it },
                    label = { Text("Daily Calories") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = water,
                    onValueChange = { water = it },
                    label = { Text("Daily Water (cups)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = steps,
                    onValueChange = { steps = it },
                    label = { Text("Daily Steps") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        calories.toIntOrNull() ?: 2000,
                        water.toIntOrNull() ?: 8,
                        steps.toIntOrNull() ?: 10000
                    )
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
