package com.bcasekuritas.mybest.app.feature.stockdetail.keystats

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.EarningsPerShareReq
import com.bcasekuritas.mybest.app.domain.dto.request.KeyStatRequest
import com.bcasekuritas.mybest.app.domain.interactors.GetEarningPerShareUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetKeyStatRtiUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetKeyStatUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.news.EarningsPerShareResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewKeyStatResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewKeyStatsRTIResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class KeyStatsViewModel @Inject constructor(
    private val getKeyStatUseCase: GetKeyStatUseCase,
    private val getKeyStatRtiUseCase: GetKeyStatRtiUseCase,
    private val getEarningPerShareUseCase: GetEarningPerShareUseCase
): BaseViewModel(){
    val getKeyStatResult = MutableLiveData<Resource<ViewKeyStatResponse?>>()
    val getKeyStatRtiResult = MutableLiveData<Resource<ViewKeyStatsRTIResponse?>>()
    val getEarningPerShareResult = MutableLiveData<Resource<EarningsPerShareResponse?>>()

    fun getKeyStat(userId: String, sessionId: String, stockCode: String){
        viewModelScope.launch(Dispatchers.IO) {
            val keyStatRequest = KeyStatRequest(userId, sessionId, stockCode)

            getKeyStatUseCase.invoke(keyStatRequest).collect(){resource ->
                resource.let {
                    getKeyStatResult.postValue(it)
                }
            }
        }
    }

    fun getKeyStatRti(userId: String, sessionId: String, stockCode: String){
        viewModelScope.launch(Dispatchers.IO) {
            val keyStatRtiRequest = KeyStatRequest(userId, sessionId, stockCode)

            getKeyStatRtiUseCase.invoke(keyStatRtiRequest).collect(){resource ->
                resource.let {
                    getKeyStatRtiResult.postValue(it)
                }
            }
        }
    }

    fun getEarningPerShare(userId: String, sessionId: String, stockCode: String){
        viewModelScope.launch(Dispatchers.IO) {
            val earningsPerShareReq = EarningsPerShareReq(userId, sessionId, stockCode)

            getEarningPerShareUseCase.invoke(earningsPerShareReq).collect(){resource ->
                resource.let {
                    getEarningPerShareResult.postValue(it)
                }
            }
        }
    }
}