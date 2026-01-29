package com.bcasekuritas.mybest.app.feature.global.tabindex

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.GlobalMarketReq
import com.bcasekuritas.mybest.app.domain.interactors.GetGlobalIndexUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.proto.news.LatestIndexResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TabIndexViewModel @Inject constructor(
    private val getGlobalIndexUseCase: GetGlobalIndexUseCase,
    val imqConnectionListener: IMQConnectionListener
) : BaseViewModel() {

    var getGlobalIndexResult = MutableLiveData<Resource<LatestIndexResponse?>>()

    fun getGlobalIndex(userId: String, sessionId: String){
        viewModelScope.launch(Dispatchers.IO) {
            val globalIndexReq = GlobalMarketReq(userId, sessionId)

            getGlobalIndexUseCase.invoke(globalIndexReq).collect() {resource ->
                resource.let {
                    getGlobalIndexResult.postValue(it)
                }
            }
        }
    }
}