package com.bcasekuritas.mybest.app.feature.index

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
class IndexViewModel @Inject constructor(
    private val getIndexSectorUseCase: GetIndexSectorUseCase,
    private val getIndexSectorDetailDataUseCase: GetIndexSectorDetailDataUseCase,
    private val setListenerIndiceDataUseCase: SetListenerIndiceDataUseCase,
    private val subscribeAllIndiceDataUseCase: SubscribeAllIndiceDataUseCase,
    private val unSubscribeAllIndiceDataUseCase: UnSubscribeAllIndiceDataUseCase,
    val imqConnectionListener: IMQConnectionListener
): BaseViewModel() {

    val getIndexDataResult = MutableLiveData<List<ViewIndexSector?>?>()
    val getIndexDetailDataResult = MutableLiveData<List<IndexSectorDetailData?>?>()
    private val indexMap = mutableMapOf<String, IndexSectorDetailData>()
    private var querySearch = ""

    fun setQuerySearch(query: String) {
        querySearch = query
    }

    fun getIndexData(userId: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = IndexSectorRequest(userId, sessionId, 1)

            getIndexSectorUseCase.invoke(request).collect() {resource ->
                when (resource) {
                    is Resource.Success -> {
                        val listIndex = resource.data?.sortedBy { it?.indexCode }
                        var indexImage = 0
                        listIndex?.map {
                            if (it != null) {
                                indexImage = if (indexImage > 7) 0 else indexImage
                                val indexCode = if (it.indexCode == "IHSG") "COMPOSITE" else it.indexCode
                                indexMap[indexCode] = IndexSectorDetailData(id = it.id, indiceCode = it.indexCode, stockCount = it.stockCount, indexName = it.indexName, idImg = indexImage)
                                indexImage += 1
                            }
                        }
                        val routingKey = indexMap.keys.toList()
                        if (routingKey.isNotEmpty()) {
                            getSectorDetailData(userId, sessionId, indexMap.keys.toList())
                        }
//                        getIndexDataResult.postValue(resource.data)
                    }
                    else -> {}
                }

            }
        }
    }

    private fun getSectorDetailData(userId: String, sessionId: String, listItem: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = IndexSectorDataRequest(userId, sessionId, "RG", listItem)

            getIndexSectorDetailDataUseCase.invoke(request).collect() {
                when(it) {
                    is Resource.Success -> {
                        it.data?.sortedBy { it?.indiceCode }?.map {
                            if (it != null) {
                                val id = indexMap[it.indiceCode]?.id
                                val stockCount = indexMap[it.indiceCode]?.stockCount
                                val idImg = indexMap[it.indiceCode]?.idImg?: 0
                                val indexName = indexMap[it.indiceCode]?.indexName?: ""
                                val indiceCode = if (it.indiceCode == "COMPOSITE") "IHSG" else it.indiceCode
                                indexMap[it.indiceCode] = IndexSectorDetailData(
                                    id!!,
                                    indiceCode,
                                    it.indiceVal,
                                    it.change,
                                    it.chgPercent,
                                    stockCount!!,
                                    idImg = idImg,
                                    indexName = indexName
                                )

                            }
                        }
                        getIndexDetailDataResult.postValue(indexMap.values.toList())
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
            val routingKey = indexMap.keys.toList()
            subscribeAllIndiceDataUseCase.subscribe(routingKey)
        }
    }

    fun unSubscribeIndiceData() {
        viewModelScope.launch(Dispatchers.IO) {
            val routingKey = indexMap.keys.toList()
            unSubscribeAllIndiceDataUseCase.unSubscribe(routingKey)
        }
    }

    private val miListener = MQMessageListener<MIMessage> { result ->
        viewModelScope.launch(Dispatchers.IO) {
            val parsedObject = result.protoMsg
            if (parsedObject.type == MIType.INDICE_SUMMARY) {
                Timber.tag("indiceSumRealtime").d( "get realtime data")
                val indiceSummaryProto = parsedObject.indiceSummary

                if (indexMap.containsKey(indiceSummaryProto.indiceCode)) {
                    val id = indexMap[indiceSummaryProto.indiceCode]?.id
                    val stockCount = indexMap[indiceSummaryProto.indiceCode]?.stockCount
                    val idImg = indexMap[indiceSummaryProto.indiceCode]?.idImg
                    val indexName = indexMap[indiceSummaryProto.indiceCode]?.indexName ?: ""
                    val indiceCode = if (indiceSummaryProto.indiceCode == "COMPOSITE") "IHSG" else indiceSummaryProto.indiceCode
                    indexMap[indiceSummaryProto.indiceCode] = IndexSectorDetailData(
                        id!!,
                        indiceCode,
                        indiceSummaryProto.indiceVal,
                        indiceSummaryProto.change,
                        indiceSummaryProto.chgPercent,
                        stockCount!!,
                        idImg!!,
                        indexName
                    )

                    if (querySearch != "") {
                        val filter = indexMap.filter { item ->
                            item.value.indiceCode.contains(querySearch, ignoreCase = true)
                        }
                        withContext(Dispatchers.Main) {
                            getIndexDetailDataResult.postValue(filter.values.toList())
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            getIndexDetailDataResult.postValue(indexMap.values.toList())
                        }
                    }
                }
            }
        }
    }

}