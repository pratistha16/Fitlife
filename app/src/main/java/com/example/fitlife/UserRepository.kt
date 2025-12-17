package com.example.fitlife

class UserRepository(private val dao: UserDao) {

    suspend fun insert(user: User) = dao.insert(user)

    suspend fun getByEmail(email: String): User? = dao.getByEmail(email)

    suspend fun login(email: String, password: String): User? = dao.login(email, password)

    suspend fun updateProfile(userId: Int, weight: Float?, height: Float?, age: Int?, gender: String?) =
        dao.updateProfile(userId, weight, height, age, gender)
}
