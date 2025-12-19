package com.example.fitlife

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fitlife.ui.theme.BrandPrimary
import com.example.fitlife.ui.theme.BrandSecondary
import com.example.fitlife.ui.theme.BrandTertiary

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home, Screen.Home.route),
        BottomNavItem("Routines", Icons.Default.FitnessCenter, Screen.Manage.route),
        BottomNavItem("Plan", Icons.Default.CalendarMonth, Screen.WeeklyPlan.route),
        BottomNavItem("Profile", Icons.Default.Person, Screen.Profile.route)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Floating Bottom Bar Container
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(72.dp) // Slightly taller for the floating effect
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .shadow(16.dp, RoundedCornerShape(32.dp))
                .clip(RoundedCornerShape(32.dp)),
            color = MaterialTheme.colorScheme.surfaceVariant, // Dark Grey
        ) {
            // Gradient Background for the bar (optional, or just solid dark)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEach { item ->
                        val isSelected = currentRoute == item.route
                        
                        // Icon Container
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable {
                                    if (currentRoute != item.route) {
                                        navController.navigate(item.route) {
                                            popUpTo(Screen.Home.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                // Selected Indicator (Glowing Dot or Background)
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(BrandPrimary, BrandTertiary)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        tint = Color.Black, // Dark icon on bright background
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
