package com.sapan.restapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResponseViewModel @Inject constructor() : ViewModel() {

    private val _responseCode = MutableLiveData<Int>().apply {
        value = null
    }
    val responseCode: LiveData<Int?> = _responseCode

    private val _responseTime = MutableLiveData<Long>().apply {
        value = null
    }
    val responseTime: LiveData<Long?> = _responseTime

    private val _responseBody = MutableLiveData<String>().apply {
        value = null
    }
    val responseBody: LiveData<String?> = _responseBody

    private val _responseHeaders = MutableLiveData<Map<String, List<String>>>().apply {
        value = emptyMap()
    }
    val responseHeader: LiveData<Map<String, List<String>>> = _responseHeaders

    private val _error = MutableLiveData<String>().apply {
        value = null
    }
    val error: LiveData<String?> = _error

    fun updateResponse(
        code: Int,
        time: Long,
        body: String?,
        headers: Map<String, List<String>>
    ) {
        _responseCode.value = code
        _responseTime.value = time
        _responseBody.value = body
        _responseHeaders.value = headers
        _error.value = null
    }

    fun updateError(errorMessage: String) {
        _error.value = errorMessage
        _responseCode.value = null
        _responseTime.value = null
        _responseBody.value = null
        _responseHeaders.value = emptyMap()
    }

    fun clear() {
        _responseCode.value = null
        _responseTime.value = null
        _responseBody.value = null
        _responseHeaders.value = emptyMap()
        _error.value = null
    }
}