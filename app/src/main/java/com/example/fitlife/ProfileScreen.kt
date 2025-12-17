package com.example.fitlife

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var showGoalsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                        MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    currentUser?.name?.firstOrNull()?.uppercase() ?: "U",
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            Text(
                                currentUser?.name ?: "User",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Text(
                                currentUser?.email ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )

                            currentUser?.fitnessGoal?.let { goal ->
                                Spacer(Modifier.height(8.dp))
                                AssistChip(
                                    onClick = {},
                                    label = { Text(goal) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Flag,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Stats Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileStatCard(
                        title = "Workouts",
                        value = "${currentUser?.totalWorkoutsCompleted ?: 0}",
                        icon = Icons.Default.FitnessCenter,
                        color = Color(0xFF6C5CE7),
                        modifier = Modifier.weight(1f)
                    )
                    ProfileStatCard(
                        title = "Calories",
                        value = "${currentUser?.totalCaloriesBurned ?: 0}",
                        icon = Icons.Default.LocalFireDepartment,
                        color = Color(0xFFFF6B6B),
                        modifier = Modifier.weight(1f)
                    )
                    ProfileStatCard(
                        title = "Streak",
                        value = "${currentUser?.currentStreak ?: 0}",
                        icon = Icons.Default.Whatshot,
                        color = Color(0xFFFFBE76),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Body Stats
            item {
                Text(
                    "Body Stats",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
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
                            Spacer(Modifier.height(16.dp))
                            Divider()
                            Spacer(Modifier.height(16.dp))

                            val bmi = currentUser!!.weight!! / ((currentUser!!.height!! / 100) * (currentUser!!.height!! / 100))
                            val bmiCategory = when {
                                bmi < 18.5 -> "Underweight"
                                bmi < 25 -> "Normal"
                                bmi < 30 -> "Overweight"
                                else -> "Obese"
                            }
                            val bmiColor = when {
                                bmi < 18.5 -> Color(0xFF74B9FF)
                                bmi < 25 -> Color(0xFF00B894)
                                bmi < 30 -> Color(0xFFFFBE76)
                                else -> Color(0xFFFF7675)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "BMI",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        String.format("%.1f", bmi),
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                AssistChip(
                                    onClick = {},
                                    label = { Text(bmiCategory) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = bmiColor.copy(alpha = 0.15f),
                                        labelColor = bmiColor
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Goals Section
            item {
                Text(
                    "Daily Goals",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        GoalItem(
                            icon = Icons.Default.LocalFireDepartment,
                            title = "Calories",
                            value = "${currentUser?.targetCalories ?: 2000} kcal",
                            color = Color(0xFFFF6B6B)
                        )
                        GoalItem(
                            icon = Icons.Default.WaterDrop,
                            title = "Water",
                            value = "${currentUser?.targetWater ?: 8} glasses",
                            color = Color(0xFF74B9FF)
                        )
                        GoalItem(
                            icon = Icons.Default.DirectionsWalk,
                            title = "Steps",
                            value = "${currentUser?.targetSteps ?: 10000}",
                            color = Color(0xFF00B894)
                        )

                        FilledTonalButton(
                            onClick = { showGoalsDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Edit Goals")
                        }
                    }
                }
            }

            // Activity Level
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Speed,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Activity Level",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    currentUser?.activityLevel ?: "Not set",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Logout
            item {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Logout")
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }

        // Edit Profile Dialog
        if (showEditDialog) {
            EditProfileDialog(
                currentUser = currentUser,
                onDismiss = { showEditDialog = false },
                onSave = { weight, height, age, gender, activityLevel, fitnessGoal ->
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
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
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
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
            value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(title, style = MaterialTheme.typography.bodyLarge)
        }
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileDialog(
    currentUser: User?,
    onDismiss: () -> Unit,
    onSave: (Float?, Float?, Int?, String?, String?, String?) -> Unit
) {
    var weight by remember { mutableStateOf(currentUser?.weight?.toString() ?: "") }
    var height by remember { mutableStateOf(currentUser?.height?.toString() ?: "") }
    var age by remember { mutableStateOf(currentUser?.age?.toString() ?: "") }
    var selectedGender by remember { mutableStateOf(currentUser?.gender ?: "") }
    var selectedActivity by remember { mutableStateOf(currentUser?.activityLevel ?: "") }
    var selectedGoal by remember { mutableStateOf(currentUser?.fitnessGoal ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Height (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it.filter(Char::isDigit) },
                    label = { Text("Age") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Gender dropdown
                var genderExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = genderExpanded,
                    onExpandedChange = { genderExpanded = !genderExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedGender,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gender") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false }
                    ) {
                        listOf("Male", "Female", "Other").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedGender = option
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }

                // Fitness Goal dropdown
                var goalExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = goalExpanded,
                    onExpandedChange = { goalExpanded = !goalExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedGoal,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fitness Goal") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = goalExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = goalExpanded,
                        onDismissRequest = { goalExpanded = false }
                    ) {
                        listOf("Lose Weight", "Build Muscle", "Stay Fit", "Gain Weight").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedGoal = option
                                    goalExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        weight.toFloatOrNull(),
                        height.toFloatOrNull(),
                        age.toIntOrNull(),
                        selectedGender.ifEmpty { null },
                        selectedActivity.ifEmpty { null },
                        selectedGoal.ifEmpty { null }
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
private fun EditGoalsDialog(
    currentUser: User?,
    onDismiss: () -> Unit,
    onSave: (Int, Int, Int) -> Unit
) {
    var calories by remember { mutableStateOf(currentUser?.targetCalories?.toString() ?: "2000") }
    var water by remember { mutableStateOf(currentUser?.targetWater?.toString() ?: "8") }
    var steps by remember { mutableStateOf(currentUser?.targetSteps?.toString() ?: "10000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Daily Goals") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it.filter(Char::isDigit) },
                    label = { Text("Target Calories") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = water,
                    onValueChange = { water = it.filter(Char::isDigit) },
                    label = { Text("Water (glasses)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = steps,
                    onValueChange = { steps = it.filter(Char::isDigit) },
                    label = { Text("Target Steps") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
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
