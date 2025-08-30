package com.sapan.restapp.collections.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sapan.restapp.collections.models.Collection
import com.sapan.restapp.collections.models.Request
import com.sapan.restapp.collections.respository.CollectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val repository: CollectionRepository
): ViewModel() {

    private val _collections = MutableStateFlow<List<Collection>>(emptyList())
    val collections: StateFlow<List<Collection>> = _collections.asStateFlow()

    private val _currentCollection = MutableStateFlow<Collection?>(null)
    val currentCollection: StateFlow<Collection?> = _currentCollection.asStateFlow()

    private val _requests = MutableStateFlow<List<Request>>(emptyList())
    val requests: StateFlow<List<Request>> = _requests.asStateFlow()

    init {
        loadRootCollections()
    }

    fun loadRootCollections() {
        viewModelScope.launch {
            repository.getRootCollections().collect { collections ->
                _collections.value = collections
            }
        }
    }

    fun loadSubCollections(parentId: String) {
        viewModelScope.launch {
            repository.getSubCollections(parentId).collect { collections ->
                _collections.value = collections
            }
        }
    }

    fun loadRequests(collectionId: String) {
        viewModelScope.launch {
            repository.getRequestByCollection(collectionId).collect { requests ->
                _requests.value = requests
            }
        }
    }

    fun createCollection(name: String, description: String = "", parentId: String? = null) {
        viewModelScope.launch {
            val collection = Collection(
                name = name,
                description = description,
                parentId = parentId
            )
            repository.createCollection(collection)
        }
    }

    fun updateCollection(collection: Collection) {
        viewModelScope.launch {
            repository.updateCollection(collection)
        }
    }

    fun deleteCollection(collection: Collection) {
        viewModelScope.launch {
            repository.deleteCollection(collection)
        }
    }

    fun saveRequest(
        collectionId: String,
        name: String,
        url: String,
        method: String,
        headers: Map<String, String>,
        queryParams: Map<String, String>,
        body: String?,
        bodyType: String
    ) {
        viewModelScope.launch {
            val request = Request(
                collectionId = collectionId,
                name = name,
                url = url,
                method = method,
                headers = headers,
                body = body,
                bodyType = bodyType
            )
            repository.createRequest(request)
        }
    }

    fun setCurrentCollection(collection: Collection?) {
        _currentCollection.value = collection
        collection?.let { loadRequests(it.id) }
    }
}

