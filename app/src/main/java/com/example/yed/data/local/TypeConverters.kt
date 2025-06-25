package com.example.yed.data.local

import androidx.room.TypeConverter
import com.example.yed.domain.entity.Meaning
import com.example.yed.domain.entity.Phonetic
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromPhoneticList(value: List<Phonetic>): String = gson.toJson(value)

    @TypeConverter
    fun toPhoneticList(value: String): List<Phonetic> =
        gson.fromJson(value, object : TypeToken<List<Phonetic>>() {}.type)

    @TypeConverter
    fun fromMeaningList(value: List<Meaning>): String = gson.toJson(value)

    @TypeConverter
    fun toMeaningList(value: String): List<Meaning> =
        gson.fromJson(value, object : TypeToken<List<Meaning>>() {}.type)
}