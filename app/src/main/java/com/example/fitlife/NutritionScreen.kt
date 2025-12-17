package com.example.fitlife

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    
    var meals by remember { mutableStateOf(listOf<NutritionItem>()) }
    var waterCount by remember { mutableStateOf(0) }
    var showAddMealDialog by remember { mutableStateOf(false) }
    var selectedMealType by remember { mutableStateOf(MealType.BREAKFAST) }

    val totalCalories = meals.sumOf { it.calories }
    val totalProtein = meals.sumOf { it.protein.toDouble() }.toFloat()
    val totalCarbs = meals.sumOf { it.carbs.toDouble() }.toFloat()
    val totalFat = meals.sumOf { it.fat.toDouble() }.toFloat()
    val targetCalories = currentUser?.targetCalories ?: 2000

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nutrition") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddMealDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Meal")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Calories Overview Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Today's Calories",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                "$totalCalories",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                " / $targetCalories kcal",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        LinearProgressIndicator(
                            progress = { (totalCalories.toFloat() / targetCalories).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (totalCalories > targetCalories) 
                                Color(0xFFFF6B6B) 
                            else 
                                MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "${targetCalories - totalCalories} kcal remaining",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Macros Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            "Macronutrients",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            MacroItem(
                                name = "Protein",
                                value = "${totalProtein.toInt()}g",
                                color = Color(0xFF6C5CE7),
                                target = 150
                            )
                            MacroItem(
                                name = "Carbs",
                                value = "${totalCarbs.toInt()}g",
                                color = Color(0xFFFFBE76),
                                target = 250
                            )
                            MacroItem(
                                name = "Fat",
                                value = "${totalFat.toInt()}g",
                                color = Color(0xFFFF7675),
                                target = 65
                            )
                        }
                    }
                }
            }

            // Water Tracker
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Water Intake",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "$waterCount / ${currentUser?.targetWater ?: 8} glasses",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            Row {
                                FilledTonalIconButton(
                                    onClick = { if (waterCount > 0) waterCount-- }
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Remove")
                                }
                                Spacer(Modifier.width(8.dp))
                                FilledIconButton(
                                    onClick = { waterCount++ }
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add")
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            repeat(currentUser?.targetWater ?: 8) { index ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            if (index < waterCount)
                                                Color(0xFF74B9FF)
                                            else
                                                MaterialTheme.colorScheme.surfaceVariant
                                        )
                                )
                            }
                        }
                    }
                }
            }

            // Meals by type
            MealType.values().forEach { mealType ->
                val mealsOfType = meals.filter { it.mealType == mealType }
                
                item {
                    MealSection(
                        mealType = mealType,
                        meals = mealsOfType,
                        onAddClick = {
                            selectedMealType = mealType
                            showAddMealDialog = true
                        },
                        onRemoveMeal = { meal ->
                            meals = meals - meal
                        }
                    )
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }

        // Add Meal Dialog
        if (showAddMealDialog) {
            AddMealDialog(
                mealType = selectedMealType,
                onDismiss = { showAddMealDialog = false },
                onAdd = { meal ->
                    meals = meals + meal
                    showAddMealDialog = false
                }
            )
        }
    }
}

@Composable
private fun MacroItem(
    name: String,
    value: String,
    color: Color,
    target: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            "/ ${target}g",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun MealSection(
    mealType: MealType,
    meals: List<NutritionItem>,
    onAddClick: () -> Unit,
    onRemoveMeal: (NutritionItem) -> Unit
) {
    val (icon, title) = when (mealType) {
        MealType.BREAKFAST -> Icons.Default.WbSunny to "Breakfast"
        MealType.LUNCH -> Icons.Default.LunchDining to "Lunch"
        MealType.DINNER -> Icons.Default.DinnerDining to "Dinner"
        MealType.SNACK -> Icons.Default.Cookie to "Snacks"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "${meals.sumOf { it.calories }} kcal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                IconButton(onClick = onAddClick) {
                    Icon(
                        Icons.Default.AddCircle,
                        contentDescription = "Add",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (meals.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                meals.forEach { meal ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                meal.name,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "P: ${meal.protein.toInt()}g • C: ${meal.carbs.toInt()}g • F: ${meal.fat.toInt()}g",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                        Text(
                            "${meal.calories} kcal",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        IconButton(onClick = { onRemoveMeal(meal) }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMealDialog(
    mealType: MealType,
    onDismiss: () -> Unit,
    onAdd: (NutritionItem) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add ${mealType.name.lowercase().replaceFirstChar { it.uppercase() }}") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Food Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it.filter(Char::isDigit) },
                    label = { Text("Calories") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = protein,
                        onValueChange = { protein = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Protein (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = carbs,
                        onValueChange = { carbs = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Carbs (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = fat,
                    onValueChange = { fat = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Fat (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && calories.isNotBlank()) {
                        onAdd(
                            NutritionItem(
                                name = name.trim(),
                                calories = calories.toIntOrNull() ?: 0,
                                protein = protein.toFloatOrNull() ?: 0f,
                                carbs = carbs.toFloatOrNull() ?: 0f,
                                fat = fat.toFloatOrNull() ?: 0f,
                                mealType = mealType
                            )
                        )
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

