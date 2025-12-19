package com.example.fitlife

import android.net.Uri
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Manage : Screen("manage")
    object CreateRoutine : Screen("create_routine")
    object Checklist : Screen("checklist")
    object Profile : Screen("profile")
    object Nutrition : Screen("nutrition")
    object WeeklyPlan : Screen("weekly_plan")

    object Delegate : Screen("delegate/{message}") {
        fun route(message: String) = "delegate/${Uri.encode(message)}"
    }

    object RoutineExercises : Screen("routine_exercises/{routineId}") {
        fun route(routineId: Int) = "routine_exercises/$routineId"
    }

    object WorkoutSession : Screen("workout_session/{routineId}") {
        fun route(routineId: Int) = "workout_session/$routineId"
    }
}

@Composable
fun NavGraph(
    routineViewModel: RoutineViewModel,
    authViewModel: AuthViewModel,
    navController: NavHostController = rememberNavController()
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    LaunchedEffect(currentUser?.id) {
        routineViewModel.setUser(currentUser?.id)
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        composable(Screen.Splash.route) {
            SplashScreen(
                navController = navController,
                isLoggedIn = currentUser != null
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(navController, authViewModel)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController, authViewModel)
        }

        composable(Screen.Home.route) {
            HomeScreen(navController, authViewModel, routineViewModel)
        }

        composable(Screen.Manage.route) {
            ManageScreen(navController, routineViewModel, authViewModel)
        }

        composable(Screen.CreateRoutine.route) {
            CreateRoutineScreen(navController, routineViewModel, authViewModel)
        }

        composable(Screen.Checklist.route) {
            ChecklistScreen(navController, routineViewModel)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController, authViewModel, routineViewModel)
        }

        composable(Screen.Nutrition.route) {
            NutritionScreen(navController, authViewModel)
        }

        composable(Screen.WeeklyPlan.route) {
            WeeklyPlanScreen(navController, routineViewModel, authViewModel)
        }

        composable(
            route = Screen.Delegate.route,
            arguments = listOf(navArgument("message") { type = NavType.StringType })
        ) { backStackEntry ->
            val msg = backStackEntry.arguments?.getString("message") ?: ""
            DelegateScreen(navController, msg)
        }


        composable(
            route = Screen.RoutineExercises.route,
            arguments = listOf(navArgument("routineId") { type = NavType.IntType })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getInt("routineId") ?: 0
            RoutineExerciseScreen(navController, routineViewModel, routineId)
        }

        composable(
            route = Screen.WorkoutSession.route,
            arguments = listOf(navArgument("routineId") { type = NavType.IntType })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getInt("routineId") ?: 0
            WorkoutSessionScreen(navController, routineViewModel, routineId)
        }
    }
}
