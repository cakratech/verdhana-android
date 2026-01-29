package com.bcasekuritas.mybest.app.feature.activity.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.ext.event.SingleLiveEvent
import kotlinx.coroutines.launch

class SharedMainViewModel : ViewModel() {

    val dataOrderReply = MutableLiveData<String>()
    val stopSubs = MutableLiveData<Boolean>()
    val isPinBackPressedResult = SingleLiveEvent<Boolean>()
    val isPinSuccess = SingleLiveEvent<Boolean>()

    fun setData(value: String) {
        viewModelScope.launch {
            dataOrderReply.value = value
        }
    }

    fun setStopSubs(value: Boolean) {
        viewModelScope.launch {
            stopSubs.value = value
        }
    }
}