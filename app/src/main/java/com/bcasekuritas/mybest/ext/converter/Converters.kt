package com.bcasekuritas.mybest.ext.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.math.BigDecimal
import java.util.*

object Converters {
    private var gson = Gson()

    @TypeConverter
    fun stringToIntList(data: String?): List<Int> {
        if (data == null || data.isEmpty()) {
            return Collections.emptyList()
        }

        val listType = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun intListToString(ints: List<Int>?): String? {
        if (ints == null || ints.isEmpty()) {
            return null
        }
        return gson.toJson(ints)
    }

    @TypeConverter
    fun stringToStringList(data: String?): List<String> {
        if (data == null || data.isEmpty()) {
            return Collections.emptyList()
        }

        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun stringListToString(string: List<String>?): String? {
        if (string == null || string.isEmpty()) {
            return ""
        }
        return gson.toJson(string)
    }

    @TypeConverter
    fun toDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun fromDate(value: Date?): Long? {
        return value?.let { value.time }
    }

    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal? {
        return value?.let { BigDecimal(value) }
    }

    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String? {
        return value?.let { value.toString() }
    }
}