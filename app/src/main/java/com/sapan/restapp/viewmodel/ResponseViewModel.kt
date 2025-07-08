package com.sapan.restapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResponseViewModel @Inject constructor() : ViewModel() {

    private val _responseCode = MutableLiveData<Int>().apply {
        value = 201
    }
    val responseCode: LiveData<Int> = _responseCode

    private val _responseTime = MutableLiveData<Long>().apply {
        value = 910L
    }
    val responseTime: LiveData<Long> = _responseTime


}