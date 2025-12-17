package com.example.fitlife

data class ExerciseItem(
    val name: String,
    val durationSeconds: Int,
    val equipment: List<String> = emptyList(),
    val sets: Int? = null,
    val reps: Int? = null,
    val isDone: Boolean = false,
    val imageUri: String? = null,
    val videoUri: String? = null,
    val caloriesPerMinute: Int = 8  // Average calories burned per minute
)

data class NutritionItem(
    val name: String,
    val calories: Int,
    val protein: Float = 0f,      // grams
    val carbs: Float = 0f,        // grams
    val fat: Float = 0f,          // grams
    val mealType: MealType = MealType.SNACK
)

enum class MealType {
    BREAKFAST, LUNCH, DINNER, SNACK
}

data class DailyNutrition(
    val date: String,  // yyyy-MM-dd
    val meals: List<NutritionItem> = emptyList(),
    val waterGlasses: Int = 0,
    val targetCalories: Int = 2000
)

data class WeeklyPlan(
    val dayOfWeek: Int,  // 1 = Monday, 7 = Sunday
    val routineIds: List<Int> = emptyList(),
    val isRestDay: Boolean = false
)
