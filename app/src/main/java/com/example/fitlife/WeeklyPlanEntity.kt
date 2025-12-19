package com.example.fitlife

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weekly_plans")
data class WeeklyPlanEntity(
    @PrimaryKey val odayOfWeek: Int,  // 1 = Monday, 7 = Sunday
    val odayUserId: Int,
    val routineIds: List<Int> = emptyList()
)



