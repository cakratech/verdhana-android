package com.bcasekuritas.mybest.app.feature.global.tabcommodities

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.GlobalMarketReq
import com.bcasekuritas.mybest.app.domain.interactors.GetGlobalCommoditiesUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.proto.news.LatestComoditiesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TabCommoditiesViewModel @Inject constructor(
    private val getGlobalCommoditiesUseCase: GetGlobalCommoditiesUseCase,
    val imqConnectionListener: IMQConnectionListener
) : BaseViewModel() {

    var getGlobalCommoditiesResult = MutableLiveData<Resource<LatestComoditiesResponse?>>()


    fun getGlobalCommodities(userId: String, sessionId: String){
        viewModelScope.launch(Dispatchers.IO) {
            val globalCommoditiesReq = GlobalMarketReq(userId, sessionId)

            getGlobalCommoditiesUseCase.invoke(globalCommoditiesReq).collect() {resource ->
                resource.let {
                    getGlobalCommoditiesResult.postValue(it)
                }
            }
        }
    }
}