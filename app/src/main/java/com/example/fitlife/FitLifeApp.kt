package com.example.fitlife

import android.app.Application
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fitlife.ui.theme.FitlifeTheme

@Composable
fun FitLifeApp(application: Application) {

    // ✅ single DB instance
    val db = remember { AppDatabase.getDatabase(application) }

    // ✅ repositories
    val routineRepo = remember { RoutineRepository(db.routineDao()) }
    val userRepo = remember { UserRepository(db.userDao()) }

    // ✅ factories
    val routineVmFactory = remember {
        viewModelFactory {
            initializer {
                RoutineViewModel(application, routineRepo)
            }
        }
    }

    val authVmFactory = remember {
        viewModelFactory {
            initializer {
                AuthViewModel(application, userRepo)
            }
        }
    }

    val routineViewModel: RoutineViewModel = viewModel(factory = routineVmFactory)
    val authViewModel: AuthViewModel = viewModel(factory = authVmFactory)

    FitlifeTheme(darkTheme = true, dynamicColor = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavGraph(routineViewModel, authViewModel)
        }
    }
}
