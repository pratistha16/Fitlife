package com.example.fitlife

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {

    // ✅ READ (this is what ManageScreen collects)
    @Query("SELECT * FROM routines WHERE ownerUserId = :userId ORDER BY id DESC")
    fun getRoutinesForUser(userId: Int): Flow<List<WorkoutRoutine>>

    // ✅ INSERT
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(routine: WorkoutRoutine): Long

    // ✅ UPDATE WHOLE ROUTINE (optional)
    @Update
    suspend fun update(routine: WorkoutRoutine): Int

    // ✅ DELETE (per user)
    @Query("DELETE FROM routines WHERE id = :id AND ownerUserId = :userId")
    suspend fun delete(id: Int, userId: Int): Int

    // ✅ DONE FLAG (per user)
    @Query("UPDATE routines SET isDone = :done WHERE id = :id AND ownerUserId = :userId")
    suspend fun setDone(id: Int, userId: Int, done: Boolean): Int
}
