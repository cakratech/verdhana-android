package com.bcasekuritas.mybest.app.feature.sectors

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.IndexSectorDataRequest
import com.bcasekuritas.mybest.app.domain.dto.request.IndexSectorRequest
import com.bcasekuritas.mybest.app.domain.dto.response.IndexSectorDetailData
import com.bcasekuritas.mybest.app.domain.interactors.GetIndexSectorDetailDataUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetIndexSectorUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SetListenerIndiceDataUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SubscribeAllIndiceDataUseCase
import com.bcasekuritas.mybest.app.domain.interactors.UnSubscribeAllIndiceDataUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.converter.GET_IMAGE_SECTOR
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import com.bcasekuritas.rabbitmq.proto.datafeed.MIType
import com.bcasekuritas.rabbitmq.proto.news.ViewIndexSector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SectorViewModel @Inject constructor(
    private val getIndexSectorUseCase: GetIndexSectorUseCase,
    private val getIndexSectorDetailDataUseCase: GetIndexSectorDetailDataUseCase,
    private val setListenerIndiceDataUseCase: SetListenerIndiceDataUseCase,
    private val subscribeAllIndiceDataUseCase: SubscribeAllIndiceDataUseCase,
    private val unSubscribeAllIndiceDataUseCase: UnSubscribeAllIndiceDataUseCase,
    val imqConnectionListener: IMQConnectionListener
): BaseViewModel() {

    val getSectorDataResult = MutableLiveData<List<ViewIndexSector?>?>()
    val getSectorDetailDataResult = MutableLiveData<List<IndexSectorDetailData?>?>()
    private val sectorMap = mutableMapOf<String , IndexSectorDetailData>()

    fun getSectorData(userId: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = IndexSectorRequest(userId, sessionId, 2)

            getIndexSectorUseCase.invoke(request).collect() {resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.map {
                            if (it != null) {
                                sectorMap[it.indexCode] = IndexSectorDetailData(
                                    id = it.id,
                                    indiceCode = it.indexCode,
                                    stockCount = it.stockCount,
                                    indexName = it.indexName,
                                    idImg = it.indexCode.GET_IMAGE_SECTOR()
                                )
                            }
                        }
                        val routingKey = sectorMap.keys.toList()
                        if (routingKey.isNotEmpty()) {
                            getSectorDetailData(userId, sessionId, sectorMap.keys.toList())
                        }
//                        getSectorDataResult.postValue(resource.data)
                    }
                    else -> {}
                }

            }


        }
    }

    fun getSectorDetailData(userId: String, sessionId: String, listItem: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = IndexSectorDataRequest(userId, sessionId, "RG", listItem)

            getIndexSectorDetailDataUseCase.invoke(request).collect() {
                when(it) {
                    is Resource.Success -> {
                        it.data?.map {
                            if (it != null) {
                                val id = sectorMap[it.indiceCode]?.id
                                val stockCount = sectorMap[it.indiceCode]?.stockCount
                                sectorMap[it.indiceCode] = IndexSectorDetailData(
                                    id!!,
                                    it.indiceCode,
                                    it.indiceVal,
                                    it.change,
                                    it.chgPercent,
                                    stockCount!!,
                                    it.indiceCode.GET_IMAGE_SECTOR()
                                )
                            }
                        }
                        getSectorDetailDataResult.postValue(sectorMap.values.toList())
                        delay(500)
                        subscribeIndiceData()
                    }
                    else -> {}
                }
            }
        }
    }

    fun setListenerIndice() {
        viewModelScope.launch(Dispatchers.IO) {
            setListenerIndiceDataUseCase.setListenerIndiceData(miListener)
        }
    }

    private fun subscribeIndiceData() {
        viewModelScope.launch(Dispatchers.IO) {
            val routingKey = sectorMap.keys.toList()
            subscribeAllIndiceDataUseCase.subscribe(routingKey)
        }
    }

    fun unSubscribeIndiceData() {
        viewModelScope.launch(Dispatchers.IO) {
            val routingKey = sectorMap.keys.toList()
            unSubscribeAllIndiceDataUseCase.unSubscribe(routingKey)
        }
    }

    private val miListener = MQMessageListener<MIMessage> { result ->
        viewModelScope.launch(Dispatchers.IO) {
            val parsedObject = result.protoMsg
            if (parsedObject.type == MIType.INDICE_SUMMARY) {
                Timber.tag("indiceSumRealtime").d( "get realtime data")
                val indiceSummaryProto = parsedObject.indiceSummary

                if (sectorMap.containsKey(indiceSummaryProto.indiceCode)) {
                    val id = sectorMap[indiceSummaryProto.indiceCode]?.id
                    val stockCount = sectorMap[indiceSummaryProto.indiceCode]?.stockCount
                    sectorMap[indiceSummaryProto.indiceCode] = IndexSectorDetailData(
                        id!!,
                        indiceSummaryProto.indiceCode,
                        indiceSummaryProto.indiceVal,
                        indiceSummaryProto.change,
                        indiceSummaryProto.chgPercent,
                        stockCount!!,
                        indiceSummaryProto.indiceCode.GET_IMAGE_SECTOR()
                    )

                    withContext(Dispatchers.Main) {
                        getSectorDetailDataResult.postValue(sectorMap.values.toList())
                    }
                }
            }
        }
    }

}