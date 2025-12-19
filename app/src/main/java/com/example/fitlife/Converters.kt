package com.example.fitlife

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converters {

    private val gson = Gson()

    // ---------- List<String> ----------
    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun toStringList(list: List<String>?): String {
        return gson.toJson(list ?: emptyList<String>())
    }

    // ---------- List<ExerciseItem> ----------
    @TypeConverter
    fun fromExerciseItemList(value: String?): List<ExerciseItem> {
        if (value.isNullOrBlank()) return emptyList()
        val type = object : TypeToken<List<ExerciseItem>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun toExerciseItemList(list: List<ExerciseItem>?): String {
        return gson.toJson(list ?: emptyList<ExerciseItem>())
    }

    // ---------- List<NutritionItem> ----------
    @TypeConverter
    fun fromNutritionItemList(value: String?): List<NutritionItem> {
        if (value.isNullOrBlank()) return emptyList()
        val type = object : TypeToken<List<NutritionItem>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun toNutritionItemList(list: List<NutritionItem>?): String {
        return gson.toJson(list ?: emptyList<NutritionItem>())
    }

    // ---------- List<Int> ----------
    @TypeConverter
    fun fromIntList(value: String?): List<Int> {
        if (value.isNullOrBlank()) return emptyList()
        val type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun toIntList(list: List<Int>?): String {
        return gson.toJson(list ?: emptyList<Int>())
    }
    
}
