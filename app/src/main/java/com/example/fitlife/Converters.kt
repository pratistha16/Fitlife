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
}
