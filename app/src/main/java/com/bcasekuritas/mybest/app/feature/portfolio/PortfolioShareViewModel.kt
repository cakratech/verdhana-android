package com.bcasekuritas.mybest.app.feature.portfolio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.domain.dto.response.OrderSuccessSnackBar
import com.bcasekuritas.mybest.ext.event.SingleLiveEvent
import kotlinx.coroutines.launch

class PortfolioShareViewModel  : ViewModel() {

    private val _isWithdrawSuccess = MutableLiveData<OrderSuccessSnackBar>()
    val isWithdrawSuccess: LiveData<OrderSuccessSnackBar>
        get() = _isWithdrawSuccess

    fun setWithdrawSuccess(value: OrderSuccessSnackBar) {
        viewModelScope.launch {
            _isWithdrawSuccess.value = value
        }
    }

    fun clearWithdrawSuccess() {
        _isWithdrawSuccess.value = OrderSuccessSnackBar()
    }

    private val _clientInfo = MutableLiveData<String>()

    val clientInfo: LiveData<String>
        get() = _clientInfo

    fun setClientInfo(clientName: String) {
        viewModelScope.launch {
            _clientInfo.value = clientName
        }
    }

    val showSessionExpired = MutableLiveData<Boolean>()

    fun setSessionExpired() {
        showSessionExpired.postValue(true)
    }

    val isPinSuccess = SingleLiveEvent<Boolean>()

    val isAccountChange = MutableLiveData<Boolean>()

    fun setIsAccountChange() {
        viewModelScope.launch {
            isAccountChange.postValue(true)
        }
    }

    fun clearIsAccountChange() {
        viewModelScope.launch {
            isAccountChange.postValue(false)
        }
    }

    val portfolioReturn = MutableLiveData<Double>()

    fun setPortfolioReturnPct(value: Double) {
        viewModelScope.launch {
            portfolioReturn.postValue(value)
        }
    }

}