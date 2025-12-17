package com.example.fitlife

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val weight: Float? = null,           // in kg
    val height: Float? = null,           // in cm
    val age: Int? = null,
    val gender: String? = null,          // "Male", "Female", "Other"
    val targetCalories: Int = 2000,
    val targetWater: Int = 8,            // glasses per day
    val targetSteps: Int = 10000,
    val fitnessGoal: String? = null,     // "Lose Weight", "Build Muscle", "Stay Fit"
    val activityLevel: String? = null,   // "Sedentary", "Light", "Moderate", "Active", "Very Active"
    val totalWorkoutsCompleted: Int = 0,
    val totalCaloriesBurned: Int = 0,
    val currentStreak: Int = 0
)
