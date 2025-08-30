package com.sapan.restapp.collections

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.sapan.restapp.collections.dao.CollectionDao
import com.sapan.restapp.collections.dao.MapConvertor
import com.sapan.restapp.collections.dao.RequestDao
import com.sapan.restapp.collections.models.Collection
import com.sapan.restapp.collections.models.Request

@Database(entities = [Collection::class, Request::class], version = 1)
@TypeConverters(MapConvertor::class)
abstract class RestDatabase: RoomDatabase() {
    abstract fun collectionDao(): CollectionDao
    abstract fun requestDao(): RequestDao
}