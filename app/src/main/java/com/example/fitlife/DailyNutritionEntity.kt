package com.example.fitlife

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Stores one day's nutrition for a single user.
 * Meals are stored as a list of [NutritionItem] via Room type converters.
 */
@Entity(
    tableName = "daily_nutrition",
    indices = [Index(value = ["userId", "date"], unique = true)]
)
data class DailyNutritionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val date: String, // yyyy-MM-dd
    val meals: List<NutritionItem> = emptyList(),
    val waterGlasses: Int = 0
)




