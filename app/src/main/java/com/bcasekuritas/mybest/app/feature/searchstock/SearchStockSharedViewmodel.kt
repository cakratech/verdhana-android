package com.bcasekuritas.mybest.app.feature.searchstock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SearchStockSharedViewmodel: ViewModel() {

    private val _sendStockParamData = MutableLiveData<List<String>>()
    val getCheckedStockParam: LiveData<List<String>>
        get() = _sendStockParamData

    fun setCheckedStockParam(value: List<String>) {
        viewModelScope.launch {
            _sendStockParamData.value = value
        }
    }
}