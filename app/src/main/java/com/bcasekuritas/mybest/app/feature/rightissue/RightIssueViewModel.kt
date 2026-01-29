package com.bcasekuritas.mybest.app.feature.rightissue

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.ExerciseSessionReq
import com.bcasekuritas.mybest.app.domain.dto.request.RightIssueInfoReq
import com.bcasekuritas.mybest.app.domain.dto.response.RightIssueItem
import com.bcasekuritas.mybest.app.domain.dto.response.TradeSummary
import com.bcasekuritas.mybest.app.domain.interactors.GetExerciseSessionUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetListStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetRightIssueInfoUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.common.getEndTimeExercise
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseInfo
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseSessionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RightIssueViewModel @Inject constructor(
    private val rightIssueInfoUseCase: GetRightIssueInfoUseCase,
    private val getListStockParamDaoUseCase: GetListStockParamDaoUseCase,
    val imqConnectionListener: IMQConnectionListener,
    private val getExerciseSessionUseCase: GetExerciseSessionUseCase
): BaseViewModel() {

    val rightIssueInfoResult = MutableLiveData<List<RightIssueItem?>>()
    private val rightIssueMap = mutableMapOf<String, RightIssueItem>()
    val exerciseSessionResult = MutableLiveData<ExerciseSessionResponse?>()

    fun getRightIssueInfo(userId: String, accNo: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val rightIssueReq = RightIssueInfoReq(userId, sessionId, accNo)

            rightIssueInfoUseCase.invoke(rightIssueReq).collect(){resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.exerciseInfoList?.map {
                            rightIssueMap[it.stockCode] = RightIssueItem(
                                it.stockCode,
                                instrumentCode = it.instrumentCode,
                                instrumentType = it.instrumentType,
                                price = it.price,
                                status = it.status,
                                maxQty = it.maxQty,
                                stockPosQty = it.stockPosExerciseQty,
                                currentPrice = it.currentPrice,
                                totalValue = it.totalValue,
                                startDate = it.startDate,
                                endDate = it.endDate,
                                endTime = if (it.endTime != 0L) it.endTime else getEndTimeExercise(it.endDate),
                                remarks = it.remarks,
                                )
                        }
                        val listStockParam = rightIssueMap.keys.map { it.substring(0,4) }
                        getListStockParam(listStockParam)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun getListStockParam(value: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            getListStockParamDaoUseCase(value).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { data ->
                            data.map {item ->
                                rightIssueMap[item?.stockParam?.stockCode]?.let {
                                    if (item != null) {
                                        it.stockName = item.stockParam.stockName
                                    }
                                }
                            }
                            withContext(Dispatchers.Main) {
                                rightIssueInfoResult.postValue(rightIssueMap.values.toList())
                            }
                        }
                    }

                    else -> {
                        // Handle other cases if needed
                    }
                }
            }
        }
    }

    fun getExerciseSession(userId: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = ExerciseSessionReq(userId, sessionId)

            getExerciseSessionUseCase.invoke(request).collect() {resource ->
                when (resource) {
                    is Resource.Success -> {
                        exerciseSessionResult.postValue(resource.data)
                    }
                    else -> {}
                }
            }
        }
    }

}