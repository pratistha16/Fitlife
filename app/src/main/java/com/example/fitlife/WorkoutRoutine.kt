package com.example.fitlife

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class WorkoutRoutine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ownerUserId: Int,
    val name: String,
    val exercises: List<ExerciseItem>,
    val instructions: String? = null,
    val isDone: Boolean = false,
    val photoUri: String? = null,
    val notes: String? = null
)
