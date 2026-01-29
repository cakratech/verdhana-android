package com.bcasekuritas.mybest.app.feature.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bcasekuritas.mybest.ext.event.SingleLiveEvent

class NewsShareViewModel: ViewModel() {
    val getQuerySearch = MutableLiveData<String>()

    fun setQuery(query: String) {
        if (!query.equals("")) {
            getQuerySearch.postValue(query)
        }
    }

    val getOnChangeTab = SingleLiveEvent<Boolean>()

    fun setOnChangeTab(isOnChange: Boolean) {
        getOnChangeTab.postValue(isOnChange)
    }
}