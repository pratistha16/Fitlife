package com.example.fitlife

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")

    object Home : Screen("home")
    object Manage : Screen("manage")
    object CreateRoutine : Screen("create_routine")
    object Checklist : Screen("checklist")

    object Delegate : Screen("delegate/{message}") {
        fun route(message: String) = "delegate/${Uri.encode(message)}"
    }

    object RoutineExercises : Screen("routine_exercises/{routineId}") {
        fun route(routineId: Int) = "routine_exercises/$routineId"
    }
}

@Composable
fun NavGraph(
    routineViewModel: RoutineViewModel,
    authViewModel: AuthViewModel,
    navController: NavHostController = rememberNavController()
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    // ✅ update routines filter whenever login user changes
    LaunchedEffect(currentUser?.id) {
        currentUser?.id?.let { uid ->
            routineViewModel.setUser(uid)
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (currentUser == null) Screen.Login.route else Screen.Home.route
    ) {

        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Screen.Manage.route) {
            ManageScreen(
                navController = navController,
                viewModel = routineViewModel
            )
        }

        composable(Screen.CreateRoutine.route) {
            CreateRoutineScreen(
                navController = navController,
                routineViewModel = routineViewModel,
                authViewModel = authViewModel
            )
        }

        composable(Screen.Checklist.route) {
            ChecklistScreen(
                navController = navController,
                viewModel = routineViewModel
            )
        }

        composable(
            route = Screen.Delegate.route,
            arguments = listOf(
                navArgument("message") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val msg = backStackEntry.arguments?.getString("message") ?: ""
            DelegateScreen(
                navController = navController,
                prefilledMessage = msg
            )
        }

        composable(
            route = Screen.RoutineExercises.route,
            arguments = listOf(navArgument("routineId") { type = NavType.IntType })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getInt("routineId") ?: 0

            // ✅ IMPORTANT: use the parameter name your screen expects.
            // Here I assume RoutineExerciseScreen signature is:
            // RoutineExerciseScreen(navController, routineViewModel, routineId)
            RoutineExerciseScreen(
                navController = navController,
                routineViewModel = routineViewModel,
                routineId = routineId
            )
        }
    }
}
