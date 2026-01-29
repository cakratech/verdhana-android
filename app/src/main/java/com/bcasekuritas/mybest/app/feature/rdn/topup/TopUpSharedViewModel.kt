package com.bcasekuritas.mybest.app.feature.rdn.topup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.rabbitmq.proto.bcas.AccountGroupInfo
import kotlinx.coroutines.launch

class TopUpSharedViewModel: ViewModel() {

    private val _getAccountGroupInfo = MutableLiveData<AccountGroupInfo>()
    val getAccountGroupInfo: MutableLiveData<AccountGroupInfo>
        get() = _getAccountGroupInfo

    fun setAccountGroupInfo(value: AccountGroupInfo) {
        viewModelScope.launch {
            _getAccountGroupInfo.value = value
        }
    }

}
