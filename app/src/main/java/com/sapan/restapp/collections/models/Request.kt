package com.sapan.restapp.collections.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sapan.restapp.collections.dao.MapConvertor
import java.util.UUID


@Entity(tableName = "requests")
data class Request (
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val collectionId: String,
    val name: String,
    val url: String,
    val method: String,

    @TypeConverters(MapConvertor::class)
    val headers: Map<String, String> = emptyMap(),

    @TypeConverters(MapConvertor::class)
    val queryParams: Map<String, String> = emptyMap(),
    val body: String? = null,
    val bodyType: String = "NONE",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
