package com.example.fitlife

import kotlinx.coroutines.flow.Flow

class RoutineRepository(private val dao: RoutineDao) {

    fun getRoutinesForUser(userId: Int): Flow<List<WorkoutRoutine>> =
        dao.getRoutinesForUser(userId)

    suspend fun insert(routine: WorkoutRoutine): Long =
        dao.insert(routine)

    suspend fun delete(id: Int, userId: Int): Int =
        dao.delete(id, userId)

    suspend fun setDone(id: Int, userId: Int, done: Boolean): Int =
        dao.setDone(id, userId, done)
}
