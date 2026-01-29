package com.bcasekuritas.mybest.app.base.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bcasekuritas.mybest.app.data.layout.UISnackModel
import com.bcasekuritas.mybest.ext.event.SingleLiveEvent

abstract class BaseViewModel : ViewModel() {



    // RxJava
    enum class UiMode { INITIATE, ON_PROGRESS, SUCCESS, ERROR }

    private val mUiMode = MutableLiveData(UiMode.INITIATE)
    val uiMode: LiveData<UiMode> = mUiMode

    val isLoading: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val showToast: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnack: SingleLiveEvent<UISnackModel> = SingleLiveEvent()

    val forceLogout: MutableLiveData<Int> = MutableLiveData()

}