package com.example.fitlife

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Prefer not to say") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var fitnessGoal by remember { mutableStateOf("Stay Fit") }
    var experienceLevel by remember { mutableStateOf("Beginner") }

    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Decorative background shape
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))

            // Logo/Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.FitnessCenter,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                "Join FitLife",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Text(
                "Start your transformation today",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )

            Spacer(Modifier.height(32.dp))

            // Register Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Create Account",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(24.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; error = null },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; error = null },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; error = null },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; error = null },
                        label = { Text("Confirm Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(Modifier.height(24.dp))

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(Modifier.height(16.dp))

                    Text(
                        "Your Fitness Profile",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(16.dp))

                    // Age, Height, Weight Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = age,
                            onValueChange = { age = it.filter { ch -> ch.isDigit() }; error = null },
                            label = { Text("Age") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = height,
                            onValueChange = { height = it.filter { ch -> ch.isDigit() || ch == '.' }; error = null },
                            label = { Text("Ht (cm)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = weight,
                            onValueChange = { weight = it.filter { ch -> ch.isDigit() || ch == '.' }; error = null },
                            label = { Text("Wt (kg)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Gender dropdown
                    Text(
                        "Gender",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                    GenderSelector(
                        selected = gender,
                        onSelectedChange = { gender = it }
                    )

                    Spacer(Modifier.height(12.dp))

                    // Fitness goal
                    Text(
                        "Fitness Goal",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                    ChoiceChipsRow(
                        options = listOf("Lose Weight", "Gain Muscle", "Stay Fit", "Rehab"),
                        selected = fitnessGoal,
                        onSelectedChange = { fitnessGoal = it }
                    )

                    Spacer(Modifier.height(12.dp))

                    // Experience level
                    Text(
                        "Experience Level",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                    ChoiceChipsRow(
                        options = listOf("Beginner", "Intermediate", "Advanced"),
                        selected = experienceLevel,
                        onSelectedChange = { experienceLevel = it }
                    )

                    error?.let { err ->
                        Spacer(Modifier.height(16.dp))
                        Text(
                            err,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                if (name.isBlank() || email.isBlank() || password.isBlank()) {
                                    error = "Please fill all fields"
                                    return@launch
                                }
                                if (password != confirmPassword) {
                                    error = "Passwords do not match"
                                    return@launch
                                }
                                if (password.length < 6) {
                                    error = "Password must be at least 6 characters"
                                    return@launch
                                }

                                val ageInt = age.toIntOrNull()
                                val heightFloat = height.toFloatOrNull()
                                val weightFloat = weight.toFloatOrNull()

                                val result = authViewModel.register(
                                    name = name.trim(),
                                    email = email.trim(),
                                    password = password,
                                    age = ageInt,
                                    gender = gender.takeIf { it != "Prefer not to say" },
                                    heightCm = heightFloat,
                                    weightKg = weightFloat,
                                    fitnessGoal = fitnessGoal,
                                    experienceLevel = experienceLevel
                                )

                                if (result.isSuccess) {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                } else {
                                    error = result.exceptionOrNull()?.message ?: "Registration failed"
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            "Create Account",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // Login link
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Already have an account?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = { navController.popBackStack() }) {
                    Text(
                        "Log In",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun GenderSelector(
    selected: String,
    onSelectedChange: (String) -> Unit
) {
    val options = listOf("Male", "Female", "Other", "Prefer not to say")
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        options.forEach { option ->
            FilterChip(
                selected = option == selected,
                onClick = { onSelectedChange(option) },
                label = { Text(option) }
            )
        }
    }
}

@Composable
private fun ChoiceChipsRow(
    options: List<String>,
    selected: String,
    onSelectedChange: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        options.forEach { option ->
            FilterChip(
                selected = option == selected,
                onClick = { onSelectedChange(option) },
                label = { Text(option) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}
