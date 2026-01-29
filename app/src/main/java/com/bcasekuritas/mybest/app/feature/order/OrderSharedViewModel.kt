package com.bcasekuritas.mybest.app.feature.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.domain.dto.request.OrderAdapterData
import com.bcasekuritas.mybest.ext.event.SingleLiveEvent
import kotlinx.coroutines.launch

class OrderSharedViewModel : ViewModel() {

    private val _data = MutableLiveData<OrderAdapterData>()
    val data: LiveData<OrderAdapterData>
        get() = _data

    fun setData(value: OrderAdapterData) {
        viewModelScope.launch {
            _data.value = value
        }
    }

    private val _order = MutableLiveData<OrderAdapterData>()
    val order: LiveData<OrderAdapterData>
        get() = _order

    fun setDataOrder(value: OrderAdapterData) {
        viewModelScope.launch {
            _order.value = value
        }
    }

    private val _updateOrder = MutableLiveData<String>()
    val updateOrder: LiveData<String>
        get() = _updateOrder
    fun setUpdateOrder(buySell: String) {
        viewModelScope.launch {
            _updateOrder.value = buySell
        }
    }

    private val _bottomScroll = MutableLiveData<Boolean>()

    val bottomScroll: LiveData<Boolean>
        get() = _bottomScroll

    fun setBottomScroll(state: Boolean) {
        _bottomScroll.value = state
    }

    private val _isNotation = MutableLiveData<Boolean>()
    val isNotation: LiveData<Boolean>
        get() = _isNotation

    fun setIsNotation(state:Boolean) {
        _isNotation.value = state
    }

    private val _isMarketClosed = MutableLiveData<Boolean>()
    val isMarketClosed: LiveData<Boolean>
        get() = _isMarketClosed

    fun setIsMarketClosed(state:Boolean) {
        _isMarketClosed.value = state
    }

    val isPinSuccess = SingleLiveEvent<Boolean>()
}