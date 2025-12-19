package com.example.fitlife

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.LocalTime
import java.util.Locale

// Data for Fitness News
data class BlogPost(
    val id: Int,
    val title: String,
    val category: String,
    val duration: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    routineViewModel: RoutineViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val routines by routineViewModel.routines.collectAsState(initial = emptyList())
    val today = LocalDate.now()
    val dayName = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())

    // Load today's nutrition to drive "Today's Progress" cards
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val nutritionDao = remember { db.nutritionDao() }

    var todayCalories by remember { mutableStateOf(0) }
    var todayWater by remember { mutableStateOf(0) }
    var currentStreak by remember { mutableStateOf(0) }

    val completedWorkouts = remember(routines) { routines.count { it.isDone } }

    LaunchedEffect(currentUser?.id, today) {
        val userId = currentUser?.id
        if (userId != null) {
            val entry = nutritionDao.getForUserAndDate(userId, today.toString())
            if (entry != null) {
                todayCalories = entry.meals.sumOf { it.calories }
                todayWater = entry.waterGlasses
            } else {
                todayCalories = 0
                todayWater = 0
            }

            // Calculate streak: consecutive days with activity
            val allEntries = nutritionDao.getAllForUser(userId)
            val activityDates = allEntries
                .filter { it.meals.isNotEmpty() || it.waterGlasses > 0 }
                .mapNotNull { runCatching { LocalDate.parse(it.date) }.getOrNull() }
                .toSet()

            var streak = 0
            var day = LocalDate.now()
            while (activityDates.contains(day)) {
                streak++
                day = day.minusDays(1)
            }
            currentStreak = streak
        } else {
            todayCalories = 0
            todayWater = 0
            currentStreak = 0
        }
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 90.dp) // Extra padding for floating bottom bar
        ) {
            // Header with greeting and Search Bar
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp) // Increased height for search bar
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.background
                                )
                            ),
                            shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth()
                    ) {
                        // Top Row: Greeting & Profile
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    "Good ${getGreeting()}!",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                )
                                Text(
                                    currentUser?.name ?: "Athlete",
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            // Profile Image Placeholder
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(50.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = (currentUser?.name?.firstOrNull() ?: 'A').toString(),
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                        
                        Spacer(Modifier.height(24.dp))

                        // Search Bar (Visual Only)
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shadowElevation = 4.dp
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Search something...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        Spacer(Modifier.height(24.dp))
                        
                        // Date Badge
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "$dayName, ${today.dayOfMonth} ${today.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Your Progress / Overview
            item {
                Text(
                    "Your Progress",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        StatCard(
                            title = "Calories",
                            value = "$todayCalories",
                            target = "/ ${currentUser?.targetCalories ?: 2000}",
                            icon = Icons.Default.LocalFireDepartment,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    item {
                        StatCard(
                            title = "Water",
                            value = "$todayWater",
                            target = "/ ${currentUser?.targetWater ?: 8}",
                            icon = Icons.Default.WaterDrop,
                            color = Color(0xFF00E5FF) // Cyan
                        )
                    }
                    item {
                        StatCard(
                            title = "Streak",
                            value = "$currentStreak",
                            target = " days",
                            icon = Icons.Default.Whatshot,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(24.dp)) }

            // Quick Actions Grid
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        QuickActionCard(
                            title = "Start Workout",
                            subtitle = "Log your activity",
                            icon = Icons.Default.PlayArrow,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Screen.Manage.route) }
                        )
                        QuickActionCard(
                            title = "Add Meal",
                            subtitle = "Track calories",
                            icon = Icons.Default.Restaurant,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Screen.Nutrition.route) }
                        )
                    }
                }
            }

            // Latest News (Blogs)
            item {
                Text(
                    "What's New",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 16.dp)
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(getMockBlogPosts()) { post ->
                        BlogCard(post)
                    }
                }
            }

            // Weekly Overview
            item {
                Text(
                    "Weekly Activity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
                WeeklyOverviewCard()
            }
            
            item { Spacer(Modifier.height(16.dp)) }

            item {
                TipCard()
            }
        }
    }
}

// Mock Data for Blogs
fun getMockBlogPosts(): List<BlogPost> {
    return listOf(
        BlogPost(1, "Boosting Performance with Breath Work", "Sport & Activity", "5 min read", Color(0xFF81D4FA)),
        BlogPost(2, "The Ultimate Guide to HIIT", "Training", "8 min read", Color(0xFFCE93D8)),
        BlogPost(3, "Nutrition Myths Debunked", "Nutrition", "6 min read", Color(0xFFA5D6A7)),
        BlogPost(4, "Recovery: The Missing Link", "Health", "4 min read", Color(0xFFFFCC80))
    )
}

@Composable
fun BlogCard(post: BlogPost) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .height(180.dp)
            .clickable { /* Open Blog URL */ },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image Placeholder (Gradient)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(post.color.copy(alpha = 0.6f), post.color.copy(alpha = 0.2f))
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        post.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        post.duration,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Column {
                    Text(
                        post.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2
                    )
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.clickable { }
                    ) {
                        Text(
                            "Read Now",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    target: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier.width(140.dp).height(110.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(Modifier.width(12.dp))
            
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun WeeklyOverviewCard() {
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    val today = LocalDate.now().dayOfWeek.value - 1

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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEachIndexed { index, day ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        day,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    index == today -> MaterialTheme.colorScheme.primary
                                    index < today -> MaterialTheme.colorScheme.primaryContainer
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (index < today) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                        } else if (index == today) {
                            Text(
                                "${LocalDate.now().dayOfMonth}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TipCard() {
    val tips = listOf(
        "Consistency beats intensity. Show up every day!",
        "Eat protein within 30 minutes after your workout.",
        "Drink water before you feel thirsty.",
        "Quality sleep is when your muscles grow.",
        "Set small goals to achieve big results."
    )
    val tip = remember { tips.random() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Default.Lightbulb,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    "Tip of the Day",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    tip,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

private fun getGreeting(): String {
    val hour = LocalTime.now().hour
    return when (hour) {
        in 5..11 -> "Morning"
        in 12..16 -> "Afternoon"
        in 17..21 -> "Evening"
        else -> "Night"
    }
}
