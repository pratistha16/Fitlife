package com.example.fitlife

import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyPlanScreen(
    navController: NavController,
    routineViewModel: RoutineViewModel
) {
    val routines by routineViewModel.routines.collectAsState(initial = emptyList())
    var selectedDay by remember { mutableStateOf(LocalDate.now().dayOfWeek.value) }
    
    // Weekly plan state (in real app, this would be persisted)
    var weeklyPlan by remember {
        mutableStateOf(
            mapOf(
                1 to listOf<Int>(),  // Monday
                2 to listOf<Int>(),  // Tuesday
                3 to listOf<Int>(),  // Wednesday
                4 to listOf<Int>(),  // Thursday
                5 to listOf<Int>(),  // Friday
                6 to listOf<Int>(),  // Saturday
                7 to listOf<Int>()   // Sunday
            )
        )
    }
    
    var showAddRoutineDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weekly Plan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Week Overview Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            "This Week",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(Modifier.height(4.dp))
                        
                        val workoutDays = weeklyPlan.count { it.value.isNotEmpty() }
                        val restDays = 7 - workoutDays
                        
                        Text(
                            "$workoutDays workout days • $restDays rest days",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Day Selector
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(7) { index ->
                        val dayNum = index + 1
                        val dayName = DayOfWeek.of(dayNum).getDisplayName(TextStyle.SHORT, Locale.getDefault())
                        val isToday = LocalDate.now().dayOfWeek.value == dayNum
                        val isSelected = selectedDay == dayNum
                        val hasWorkout = weeklyPlan[dayNum]?.isNotEmpty() == true

                        Card(
                            modifier = Modifier
                                .width(60.dp)
                                .clickable { selectedDay = dayNum },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    isToday -> MaterialTheme.colorScheme.primaryContainer
                                    else -> MaterialTheme.colorScheme.surface
                                }
                            ),
                            border = if (isToday && !isSelected) 
                                androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) 
                            else null
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    dayName,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) 
                                        MaterialTheme.colorScheme.onPrimary 
                                    else 
                                        MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                hasWorkout && isSelected -> MaterialTheme.colorScheme.onPrimary
                                                hasWorkout -> Color(0xFF00B894)
                                                else -> Color.Transparent
                                            }
                                        )
                                )
                            }
                        }
                    }
                }
            }

            // Selected Day Details
            item {
                val dayName = DayOfWeek.of(selectedDay).getDisplayName(TextStyle.FULL, Locale.getDefault())
                val dayRoutines = weeklyPlan[selectedDay]?.mapNotNull { id -> 
                    routines.find { it.id == id } 
                } ?: emptyList()

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
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
                            Text(
                                dayName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            FilledTonalButton(
                                onClick = { showAddRoutineDialog = true }
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text("Add")
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        if (dayRoutines.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.SelfImprovement,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        "Rest Day",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                    Text(
                                        "No workouts scheduled",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                    )
                                }
                            }
                        } else {
                            dayRoutines.forEachIndexed { index, routine ->
                                if (index > 0) {
                                    Spacer(Modifier.height(12.dp))
                                }
                                
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            navController.navigate(Screen.RoutineExercises.route(routine.id))
                                        },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.FitnessCenter,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        Spacer(Modifier.width(16.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                routine.name,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                "${routine.exercises.size} exercises • ${routine.exercises.sumOf { it.durationSeconds } / 60} min",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                weeklyPlan = weeklyPlan.toMutableMap().apply {
                                                    this[selectedDay] = this[selectedDay]?.filter { it != routine.id } ?: emptyList()
                                                }
                                            }
                                        ) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Remove",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Weekly Summary
            item {
                Text(
                    "Weekly Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val totalWorkouts = weeklyPlan.values.sumOf { it.size }
                    val totalExercises = weeklyPlan.values.flatten().sumOf { id ->
                        routines.find { it.id == id }?.exercises?.size ?: 0
                    }
                    val totalMinutes = weeklyPlan.values.flatten().sumOf { id ->
                        routines.find { it.id == id }?.exercises?.sumOf { it.durationSeconds } ?: 0
                    } / 60

                    SummaryCard(
                        title = "Workouts",
                        value = "$totalWorkouts",
                        icon = Icons.Default.FitnessCenter,
                        color = Color(0xFF6C5CE7),
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = "Exercises",
                        value = "$totalExercises",
                        icon = Icons.Default.DirectionsRun,
                        color = Color(0xFF00B894),
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = "Minutes",
                        value = "$totalMinutes",
                        icon = Icons.Default.Timer,
                        color = Color(0xFFE17055),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }

        // Add Routine Dialog
        if (showAddRoutineDialog) {
            AlertDialog(
                onDismissRequest = { showAddRoutineDialog = false },
                title = { Text("Add Workout") },
                text = {
                    if (routines.isEmpty()) {
                        Text("No routines available. Create a routine first!")
                    } else {
                        LazyColumn {
                            items(routines) { routine ->
                                val isAdded = weeklyPlan[selectedDay]?.contains(routine.id) == true
                                
                                ListItem(
                                    headlineContent = { Text(routine.name) },
                                    supportingContent = { 
                                        Text("${routine.exercises.size} exercises") 
                                    },
                                    leadingContent = {
                                        Icon(
                                            Icons.Default.FitnessCenter,
                                            contentDescription = null
                                        )
                                    },
                                    trailingContent = {
                                        if (isAdded) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = "Added",
                                                tint = Color(0xFF00B894)
                                            )
                                        }
                                    },
                                    modifier = Modifier.clickable {
                                        if (!isAdded) {
                                            weeklyPlan = weeklyPlan.toMutableMap().apply {
                                                this[selectedDay] = (this[selectedDay] ?: emptyList()) + routine.id
                                            }
                                        }
                                        showAddRoutineDialog = false
                                    }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAddRoutineDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
                style = MaterialTheme.typography.headlineSmall,
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

