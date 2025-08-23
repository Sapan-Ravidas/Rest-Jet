package com.sapan.restapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sapan.restapp.services.NetWorkService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.net.URLEncoder
import javax.inject.Inject

@HiltViewModel
class RequestViewModel @Inject constructor(): ViewModel() {

    private val _requestUrl = MutableStateFlow<String>("https://192.168.1.7:8080/public/api/create-user")

    val requestUrl: StateFlow<String> = _requestUrl.asStateFlow()

    private val _selectedMethod = MutableStateFlow("POST")
    val selectedMethod: StateFlow<String> = _selectedMethod.asStateFlow()

    private val _headers = MutableLiveData<Map<String, String>>(emptyMap())
    val headers: LiveData<Map<String, String>> = _headers

    private val _queryParams = MutableLiveData<Map<String, String>>(emptyMap())
    val queryParams: LiveData<Map<String, String>> = _queryParams

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

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
        onResponse: (responseCode: Int, responseTime: Long, responseBody: String?, responseHeaders: Map<String, List<String>>) -> Unit
    ) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val startTime = System.currentTimeMillis()
                val networkService = NetWorkService()
                val fullUrl = buildUrlWithQueryParams(url, queryParams)

                val response: Response<ResponseBody> = when(method.uppercase()) {
                    "GET" -> networkService.apiService.get(fullUrl, headers)
                    "POST" -> handlePostRequest(networkService, fullUrl, headers, bodyType, bodyContent, file)
                    "PUT" -> handlePutRequest(networkService, fullUrl, headers, bodyType, bodyContent, file)
                    "DELETE" -> networkService.apiService.delete(fullUrl, headers)
                    "PATCH" -> handlePatchRequest(networkService, fullUrl, headers, bodyType, bodyContent, file)
                    else -> throw IllegalArgumentException("unsupported http method")
                }

                val responseTime = System.currentTimeMillis() - startTime
                val responseBody = response.body()?.string()
                val responseHeaders = mutableMapOf<String, List<String>>()
                response.headers().forEach {
                    val key = it.first
                    val value = it.second
                    if (responseHeaders.contains(key)) {
                        responseHeaders[key] = responseHeaders[key]!! + value
                    } else {
                        responseHeaders[key] = listOf(value)
                    }
                }
                onResponse(response.code(), responseTime, responseBody, responseHeaders)

            } catch (e: Exception) {
                _error.value = e.message ?: "unknown error occurred"
                onResponse(-1, 0, "error: ${e.message}", emptyMap())
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * build URL with query params
     */
    private fun buildUrlWithQueryParams(baseUrl: String, params: Map<String, String>): String {
        if (params.isEmpty()) return baseUrl

        val urlBuilder = StringBuilder(baseUrl)
        val hasExistingParams = baseUrl.contains("?")

        if (!hasExistingParams) {
            urlBuilder.append("?")
        } else if (!urlBuilder.endsWith("?") && !urlBuilder.endsWith("&")) {
            urlBuilder.append("&")
        }

        params.forEach { (key, value) ->
            if (urlBuilder.isEmpty() && !urlBuilder.endsWith("?") && !urlBuilder.endsWith("&")) {
                urlBuilder.append("&")
            }
            urlBuilder.append("$key=${URLEncoder.encode(value, "UTF-8")}")
        }

        return urlBuilder.toString()
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
            val requestBody = getRequestBody(bodyContent)
            netWorkService.apiService.post(url, headers, requestBody)
        }
        "FILE" -> {
            val part = getFileParts(file)
            netWorkService.apiService.uploadFile(url, headers, part)
        }
        "FORM" -> {
            val fields: Map<String, String> = getFormFields(bodyContent)
            netWorkService.apiService.postForm(url, headers, fields)
        }
        "NONE" -> netWorkService.apiService.post(url, headers, null)
        else -> throw  IllegalArgumentException("unsupported body part: $bodyType")
    }

    /**
     *
     */
    private suspend fun handlePutRequest(
        netWorkService: NetWorkService,
        url: String,
        headers: Map<String, String>,
        bodyType: String,
        bodyContent: String,
        file: File?
    ) = when (bodyType) {
        "TEXT" -> {
            val requestBody = getRequestBody(bodyContent)
            netWorkService.apiService.put(url, headers, requestBody)
        }
        "FILE" -> {
            val part = getFileParts(file)
            netWorkService.apiService.uploadFile(url, headers, part)
        }
        "FORM" -> {
            val fields = getFormFields(bodyContent)
            netWorkService.apiService.postForm(url, headers, fields)
        }
        "NONE" -> netWorkService.apiService.put(url, headers, null)
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
            val requestBody = getRequestBody(bodyContent)
            netWorkService.apiService.patch(url, headers, requestBody)
        }
        "FILE" -> {
            val part = getFileParts(file)
            netWorkService.apiService.uploadFile(url, headers, part)
        }
        "FORM" -> {
            val fields = getFormFields(bodyContent)
            netWorkService.apiService.postForm(url, headers, fields)
        }
        "NONE" -> netWorkService.apiService.patch(url, headers, null)
        else -> throw IllegalArgumentException("Unsupported body type $bodyType")
    }

    /**
     *
     */
    private fun getFileParts(file: File?): MultipartBody.Part {
        if (file == null || !file.exists()) {
            throw IllegalArgumentException("File not selected or doesn't exist")
        }
        return MultipartBody.Part.createFormData(
            "file",
            file.name,
            file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        )
    }

    /**
     *
     */
    private fun getRequestBody(bodyContent: String): RequestBody =
        bodyContent.toRequestBody("application/json".toMediaTypeOrNull())

    /**
     *
     */
    private fun getFormFields(bodyContent: String): Map<String, String> =
        bodyContent.split("&").associate {
            val parts = it.split("=")
            val key = parts[0]
            val value = if (parts.size > 1) parts[1] else ""
            key to value
    }

}