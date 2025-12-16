package com.example.fitlife

data class ExerciseItem(
    val name: String,
    val durationSeconds: Int,
    val equipment: List<String> = emptyList(),
    val sets: Int? = null,
    val reps: Int? = null,
    val isDone: Boolean = false
)
