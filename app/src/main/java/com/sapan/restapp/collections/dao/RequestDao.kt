package com.sapan.restapp.collections.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sapan.restapp.collections.models.Request
import kotlinx.coroutines.flow.Flow

@Dao
interface RequestDao {
    @Insert
    suspend fun insert(request: Request)

    @Update
    suspend fun update(request: Request)

    @Delete
    suspend fun delete(request: Request)

    @Query("SELECT * FROM requests WHERE collectionId = :collectionId")
    fun getRequestsByCollection(collectionId: String): Flow<List<Request>>

    @Query("SELECT * FROM requests WHERE id = :id")
    suspend fun getRequestById(id: String): Request?
}