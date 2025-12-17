package com.example.fitlife

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
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
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _selectedChecklistItems = MutableStateFlow<List<String>>(emptyList())
    val selectedChecklistItems: StateFlow<List<String>> = _selectedChecklistItems.asStateFlow()

    fun setSelectedChecklistItems(items: List<String>) {
        _selectedChecklistItems.value = items
    }

    fun setUser(userId: Int?) {
        currentUserId.value = userId
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
