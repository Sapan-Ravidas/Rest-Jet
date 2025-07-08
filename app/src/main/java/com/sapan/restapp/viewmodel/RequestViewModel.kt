package com.sapan.restapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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
}