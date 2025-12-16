package com.example.fitlife

import android.app.Application
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

val RoutineViewModelFactory = viewModelFactory {
    initializer {
        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application

        val db = AppDatabase.getDatabase(app)
        val routineDao = db.routineDao()
        val repository = RoutineRepository(routineDao)

        RoutineViewModel(app, repository)
    }
}

val AuthViewModelFactory = viewModelFactory {
    initializer {
        // ✅ Your AuthViewModel has NO Application parameter (based on what you shared earlier)
        AuthViewModel()
    }
}

@Composable
fun FitLifeApp() {
    val routineViewModel: RoutineViewModel = viewModel(factory = RoutineViewModelFactory)
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory)

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavGraph(
                routineViewModel = routineViewModel,
                authViewModel = authViewModel
            )
        }
    }
}
