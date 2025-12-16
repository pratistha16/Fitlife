package com.example.fitlife

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RoutineViewModel(
    application: Application,
    private val repository: RoutineRepository
) : AndroidViewModel(application) {

    private val currentUserId = MutableStateFlow<Int?>(null)

    val routines: StateFlow<List<WorkoutRoutine>> =
        currentUserId
            .filterNotNull()
            .flatMapLatest { uid -> repository.getRoutinesForUser(uid) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    fun setUser(userId: Int) {
        currentUserId.value = userId
    }

    fun clearUser() {
        currentUserId.value = null
    }

    fun addRoutine(routine: WorkoutRoutine) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(routine)
        }
    }

    fun deleteRoutine(id: Int) {
        val uid = currentUserId.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(id, uid)
        }
    }

    fun setDone(id: Int, done: Boolean) {
        val uid = currentUserId.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.setDone(id, uid, done)
        }
    }
}
