package com.example.fitlife

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeeklyPlanDao {

    @Query("SELECT * FROM weekly_plans WHERE odayUserId = :userId")
    suspend fun getAllForUser(userId: Int): List<WeeklyPlanEntity>

    @Query("SELECT * FROM weekly_plans WHERE odayUserId = :userId AND odayOfWeek = :dayOfWeek LIMIT 1")
    suspend fun getForUserAndDay(userId: Int, dayOfWeek: Int): WeeklyPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(plan: WeeklyPlanEntity)

    @Query("DELETE FROM weekly_plans WHERE odayUserId = :userId AND odayOfWeek = :dayOfWeek")
    suspend fun delete(userId: Int, dayOfWeek: Int)
}



