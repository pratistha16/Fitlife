package com.example.fitlife

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    application: Application,
    private val repository: UserRepository
) : AndroidViewModel(application) {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val user = repository.login(email, password)
            if (user != null) {
                _currentUser.value = user
                _loginError.value = null
            } else {
                _loginError.value = "Invalid email or password"
            }
        }
    }

    fun clearLoginError() {
        _loginError.value = null
    }

    suspend fun register(name: String, email: String, password: String): Result<Unit> {
        return runCatching {
            repository.insert(User(name = name, email = email, password = password))
            Unit // Indicate success
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun updateProfile(weight: Float?, height: Float?, age: Int?, gender: String?) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.updateProfile(user.id, weight, height, age, gender)
            // Update local state
            _currentUser.value = user.copy(weight = weight, height = height, age = age, gender = gender)
        }
    }
}
