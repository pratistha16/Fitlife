package com.example.fitlife

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val userDao = AppDatabase.getDatabase(application).userDao()

    suspend fun register(name: String, email: String, password: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            val existing = userDao.getByEmail(email)
            if (existing != null) {
                return@withContext Result.failure(Exception("Email already registered."))
            }

            userDao.insert(User(name = name, email = email, password = password))
            Result.success(Unit)
        }
    }

    suspend fun loginWithEmail(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            val user = userDao.login(email, password)
            if (user != null) {
                _currentUser.value = user
                true
            } else false
        }
    }

    fun logout() {
        _currentUser.value = null
    }
}
