package com.sapan.restapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sapan.restapp.services.NetWorkService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RequestViewModel @Inject constructor(): ViewModel() {

    private val _requestUrl = MutableLiveData<String>().apply {
        value = "https://192.168.1.7:8080/public/api/create-user"
    }
    val requestUrl: LiveData<String> = _requestUrl

    private val _selectedMethod = MutableLiveData<String>().apply {
        value = "POST"
    }
    val selectedMethod: LiveData<String> = _selectedMethod

    private val _headers = MutableLiveData<Map<String, String>>(emptyMap())
    val headers: LiveData<Map<String, String>> = _headers

    private val _queryParams = MutableLiveData<Map<String, String>>(emptyMap())
    val queryParams: LiveData<Map<String, String>> = _queryParams

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun updateUrl(url: String) {
        _requestUrl.value = url
    }

    fun updateMethod(method: String) {
        _selectedMethod.value = method
    }

    fun addHeader(key: String, value: String) {
        val current = _headers.value?.toMutableMap() ?: mutableMapOf()
        current[key] = value
        _headers.value = current
    }

    fun removeHeader(key: String) {
        val current = _headers.value?.toMutableMap() ?: mutableMapOf()
        current.remove(key)
        _headers.value = current
    }

    fun addQueryParam(key: String, value: String) {
        val current = _headers.value?.toMutableMap() ?: mutableMapOf()
        current[key] = value
    }

    fun removeQueryParam(key: String) {
        val current = _queryParams.value?.toMutableMap() ?: mutableMapOf()
        current.remove(key)
        _queryParams.value = current
    }

    fun makeRequest(
        url: String,
        method: String,
        headers: Map<String, String>,
        queryParams: Map<String, String>,
        bodyType: String,
        bodyContent: String,
        file: File?= null,
        onResponse: (responseCode: Int, responseTime: Long, responseBody: String?, responseHeaders: Map<String, String>) -> Unit
    ) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val startTime = System.currentTimeMillis()
                val networkService = NetWorkService(url)

            } catch (e: Exception) {
                _error.value = e.message ?: "unknown error occured"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     *
     */
    private fun buildUrlWithQueryParams(baseUrl: String, params: Map<String, String>): String {
        if (params.isEmpty()) return baseUrl

        val urlBuilder = StringBuilder(baseUrl)
        if (!baseUrl.contains("?")) {
            urlBuilder.append("?")
        } else if (!urlBuilder.endsWith("?")) {
            urlBuilder.append("?")
        }

        params.forEach { (key, value) ->
            urlBuilder.append("$key=$value")
        }

        return urlBuilder.toString().removeSuffix("&")
    }

    /**
     *
     */
    private suspend fun handlePostRequest(
        netWorkService: NetWorkService,
        url: String,
        headers: Map<String, String>,
        bodyType: String,
        bodyContent: String,
        file: File?
    ) = when (bodyType) {
        "TEXT" -> {
            val requestBody = bodyContent.toRequestBody("application/json".toMediaTypeOrNull())
            netWorkService.apiService.post(url, headers, requestBody)
        }
        "FILE" -> {
            val part = MultipartBody.Part.createFormData(
                "file",
                file?.name,
                file?.asRequestBody("multipart/form-data".toMediaTypeOrNull())!!
            )
            netWorkService.apiService.uploadFile(url, headers, part)
        }
        "FORM" -> {
            val fields = bodyContent.split("&").associate {
                val parts = it.split("=")
                parts[0] to parts.getOrElse(1) {""}
            }
            netWorkService.apiService.postForm(url, headers, fields)
        }
        else -> throw  IllegalArgumentException("unsupported body part")
    }

    private suspend fun handlePutRequest(
        netWorkService: NetWorkService,
        url: String,
        headers: Map<String, String>,
        bodyType: String,
        bodyContent: String,
        file: File?
    ) = when (bodyType) {
        "TEXT" -> {

        }
        "FILE" -> {

        }
        "FORM" -> {

        }
        else -> throw IllegalArgumentException("UnSupported body type")
    }

    /**
     *
     */
    private suspend fun handlePatchRequest(
        netWorkService: NetWorkService,
        url: String,
        headers: Map<String, String>,
        bodyType: String,
        bodyContent: String,
        file: File?
    ) = when (bodyType) {
        "TEXT" -> {

        }
        "FILE" -> {

        }
        "FORM" -> {

        }
        else -> throw IllegalArgumentException("Unsupported body type")
    }

}