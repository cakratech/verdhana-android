package com.bcasekuritas.mybest.app.feature.e_ipo.eipodetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.domain.dto.response.IpoData
import com.bcasekuritas.mybest.app.domain.dto.response.IpoStatusOrder
import com.bcasekuritas.mybest.ext.event.SingleLiveEvent
import kotlinx.coroutines.launch
import timber.log.Timber

class EIPODetailSharedViewModel: ViewModel() {

    private val _ipoData = MutableLiveData<IpoData?>()
    val getIpoData: LiveData<IpoData?>
        get() = _ipoData

    fun setIpoData(value: IpoData?) {
        viewModelScope.launch {
            _ipoData.postValue(value)
        }
    }

    private val _isHasOrder = SingleLiveEvent<IpoStatusOrder>()
    val isHasOrder: SingleLiveEvent<IpoStatusOrder>
        get() = _isHasOrder

    fun setIsHasOrder(ipoStatusOrder: IpoStatusOrder) {
        Timber.tag("setIsHasOrder").d("${ipoStatusOrder.isHasOrder} - ${ipoStatusOrder.status} - ${ipoStatusOrder.stages}")
        viewModelScope.launch {
            _isHasOrder.postValue(ipoStatusOrder)
        }
    }

}