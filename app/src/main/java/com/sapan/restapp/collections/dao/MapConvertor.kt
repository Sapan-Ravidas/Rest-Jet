package com.sapan.restapp.collections.dao

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MapConvertor {
    @TypeConverter
    fun toString(map: Map<String, String>?): String =
        if (map == null || map.isEmpty()) {
            ""
        } else {
            Gson().toJson(map)
        }

    @TypeConverter
    fun toMap(string: String?): Map<String, String> {
        if (string == null || string.isEmpty()) {
            return emptyMap()
        } else {
            val type = object : TypeToken<Map<String, String>>() {}.type
            return Gson().fromJson(string, type)
        }
    }
}