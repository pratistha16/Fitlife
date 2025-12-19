package com.example.fitlife

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NutritionDao {

    @Query("SELECT * FROM daily_nutrition WHERE userId = :userId AND date = :date LIMIT 1")
    suspend fun getForUserAndDate(userId: Int, date: String): DailyNutritionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: DailyNutritionEntity)

    @Query("SELECT * FROM daily_nutrition WHERE userId = :userId ORDER BY date DESC")
    suspend fun getAllForUser(userId: Int): List<DailyNutritionEntity>
}


