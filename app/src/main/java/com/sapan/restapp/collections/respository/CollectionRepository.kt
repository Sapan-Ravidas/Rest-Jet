package com.sapan.restapp.collections.respository

import com.sapan.restapp.collections.dao.CollectionDao
import com.sapan.restapp.collections.dao.RequestDao
import com.sapan.restapp.collections.models.Collection
import com.sapan.restapp.collections.models.Request
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CollectionRepository @Inject constructor(
    private val collectionDao: CollectionDao,
    private val requestDao: RequestDao
) {
    /**
     * collections
     */
    fun getRootCollections(): Flow<List<Collection>> = collectionDao.getRootCollections()

    fun getSubCollections(parentId: String): Flow<List<Collection>> = collectionDao.getSubCollections(parentId)

    suspend fun getCollectionById(id: String): Collection? = collectionDao.getCollectionById(id)

    suspend fun createCollection(collection: Collection) = collectionDao.insert(collection)

    suspend fun updateCollection(collection: Collection) = collectionDao.update(collection)

    suspend fun deleteCollection(collection: Collection) = collectionDao.delete(collection)

    /**
     * Requests
     */
    fun getRequestByCollection(collectionId: String): Flow<List<Request>> =
        requestDao.getRequestsByCollection(collectionId)

    suspend fun getRequestById(id: String): Request? = requestDao.getRequestById(id)

    suspend fun createRequest(request: Request) = requestDao.insert(request)

    suspend fun updateRequest(request: Request) = requestDao.update(request)

    suspend fun deleteRequest(request: Request) = requestDao.delete(request)

    suspend fun saveCurrentRequest(request: Request) = requestDao.insert(request)
}