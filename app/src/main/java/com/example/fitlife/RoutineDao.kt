package com.example.fitlife

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {

    // ✅ ADD THIS (so routines are filtered per logged-in user)
    @Query("SELECT * FROM routines WHERE ownerUserId = :userId ORDER BY id DESC")
    fun getRoutinesForUser(userId: Int): Flow<List<WorkoutRoutine>>

    @Insert
    suspend fun insert(routine: WorkoutRoutine): Long

    @Update
    suspend fun update(routine: WorkoutRoutine): Int

    @Query("DELETE FROM routines WHERE id = :id AND ownerUserId = :userId")
    suspend fun delete(id: Int, userId: Int): Int

    @Query("UPDATE routines SET isDone = :done WHERE id = :id AND ownerUserId = :userId")
    suspend fun setDone(id: Int, userId: Int, done: Boolean): Int
}
