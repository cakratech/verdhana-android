package com.bcasekuritas.mybest.app.feature.global

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GlobalSharedViewModel: ViewModel() {
    val getQuerySearch = MutableLiveData<String>()

    fun setQuery(query: String) {
            getQuerySearch.postValue(query)
    }
}