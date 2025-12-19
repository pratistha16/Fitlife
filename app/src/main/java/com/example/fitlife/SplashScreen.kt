package com.example.fitlife

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

/**
 * Full-screen splash / welcome experience shown before auth.
 */
@Composable
fun SplashScreen(
    navController: NavController,
    isLoggedIn: Boolean
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        // Show splash for ~2.5s, then route based on login state
        delay(2500)
        val target = if (isLoggedIn) Screen.Home.route else Screen.Login.route
        navController.navigate(target) {
            popUpTo(Screen.Splash.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(32.dp)
                ) {
                    // Icon cluster (gym themed)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .alpha(0.95f)
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = "FitLife",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Train Smart. Stay Strong.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )

                    Spacer(Modifier.height(32.dp))

                    // Small row of themed icons
                    RowOfGymIcons()
                }
            }
        }
    }
}

@Composable
private fun RowOfGymIcons() {
    androidx.compose.foundation.layout.Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.MonitorHeart,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.alpha(0.9f)
        )
        Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.alpha(0.9f)
        )
        Text(
            text = "Build Your Best Self",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            letterSpacing = 0.5.sp
        )
    }
}





