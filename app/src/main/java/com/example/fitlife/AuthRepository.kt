package com.example.fitlife

class AuthRepository(private val userDao: UserDao) {

    suspend fun register(fullName: String, email: String, password: String) {
        val existing = userDao.getByEmail(email)
        if (existing != null) throw IllegalStateException("Email already registered")
        userDao.insert(User(name = fullName, email = email, password = password))
    }

    suspend fun login(email: String, password: String): User {
        return userDao.login(email, password)
            ?: throw IllegalStateException("Invalid email or password")
    }
}
