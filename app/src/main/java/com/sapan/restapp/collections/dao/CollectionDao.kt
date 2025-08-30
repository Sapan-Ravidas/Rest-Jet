package com.sapan.restapp.collections.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sapan.restapp.collections.models.Collection
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    @Insert
    suspend fun insert(collection: Collection)

    @Update
    suspend fun update(collection: Collection)

    @Delete
    suspend fun delete(collection: Collection)

    @Query("SELECT * FROM collections WHERE parentId IS NULL ORDER BY NAME")
    fun getRootCollections(): Flow<List<Collection>>

    @Query("SELECT * FROM collections WHERE parentId = :parentId ORDER BY name")
    fun getSubCollections(parentId: String): Flow<List<Collection>>

    @Query("SELECT * FROM collections WHERE id = :id")
    suspend fun getCollectionById(id: String): Collection?
}