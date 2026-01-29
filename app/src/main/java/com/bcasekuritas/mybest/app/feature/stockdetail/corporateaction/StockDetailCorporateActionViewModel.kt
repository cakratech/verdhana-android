package com.bcasekuritas.mybest.app.feature.stockdetail.corporateaction

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.CorporateActionTabRequest
import com.bcasekuritas.mybest.app.domain.dto.response.CalDividenSaham
import com.bcasekuritas.mybest.app.domain.dto.response.CalIpo
import com.bcasekuritas.mybest.app.domain.dto.response.CalPubExp
import com.bcasekuritas.mybest.app.domain.dto.response.CalReverseStock
import com.bcasekuritas.mybest.app.domain.dto.response.CalRightIssue
import com.bcasekuritas.mybest.app.domain.dto.response.CalRups
import com.bcasekuritas.mybest.app.domain.dto.response.CalSahamBonus
import com.bcasekuritas.mybest.app.domain.dto.response.CalStockSplit
import com.bcasekuritas.mybest.app.domain.dto.response.CalWarrant
import com.bcasekuritas.mybest.app.domain.interactors.GetCorporateActionTabByStockCodeUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockDetailCorporateActionViewModel @Inject constructor(
    private val getCorporateActionTabByStockCodeUseCase: GetCorporateActionTabByStockCodeUseCase
): BaseViewModel() {
    val getDividendResult = MutableLiveData<List<CalDividenSaham>?>()
    val getWarrantResult = MutableLiveData<List<CalWarrant>?>()
    val getRightIssueResult = MutableLiveData<List<CalRightIssue>?>()
    val getStockSplitResult = MutableLiveData<List<CalStockSplit>?>()
    val getBonusResult = MutableLiveData<List<CalSahamBonus>?>()
    val getReverseStockResult = MutableLiveData<List<CalReverseStock>?>()
    val getPublicExposeResult = MutableLiveData<List<CalPubExp>?>()
    val getRupsResult = MutableLiveData<List<CalRups>?>()
    val getIpoResult = MutableLiveData<List<CalIpo>?>()

    fun getCorporateActionByStockCode(userId: String, sessionId: String, stockCode: String, calType: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = CorporateActionTabRequest(userId, sessionId, stockCode, calType)

            getCorporateActionTabByStockCodeUseCase.invoke(request).collect() {resource ->
                resource.let {
                    when (resource) {
                        is Resource.Success -> {
                            val data = resource.data?.calendar

                            when (calType) {
                                1 -> {
                                    val warrantData = data?.warrantList?.sortedByDescending{ it.excerciseEnd }?.map { warrant ->
                                        CalWarrant(
                                            stockCode = warrant.stockCode,
                                            excercisePrice = warrant.excercisePrice,
                                            tradingStart = warrant.tradingStart,
                                            tradingEnd = warrant.tradingEnd,
                                            excerciseStart = warrant.excerciseStart,
                                            excerciseEnd = warrant.excerciseEnd
                                        )
                                    }
                                    getWarrantResult.postValue(warrantData)
                                }

                                2 -> {
                                    val rightIssueData = data?.rightIsueList?.sortedByDescending{ it.cumulativeDate }?.map {rightIssue ->
                                        CalRightIssue(
                                            stockCode = rightIssue.stockCode,
                                            oldRatio = rightIssue.oldRatio,
                                            newRatio = rightIssue.newRatio,
                                            factor = rightIssue.factor,
                                            price = rightIssue.price,
                                            cumulativeDate = rightIssue.cumulativeDate,
                                            exDate = rightIssue.exDate,
                                            recordingDate = rightIssue.recordingDate,
                                            tradingStart = rightIssue.tradingStart,
                                            tradingEnd = rightIssue.tradingEnd
                                        )
                                    }
                                    getRightIssueResult.postValue(rightIssueData)
                                }
                                3 -> {
                                    val stockSplitData = data?.stockSplitList?.sortedByDescending{ it.cumulativeDate }?.map {stockSplit ->
                                        CalStockSplit(
                                            stockCode = stockSplit.stockCode,
                                            oldRatio = stockSplit.oldRatio,
                                            newRatio = stockSplit.newRatio,
                                            cumulativeDate = stockSplit.cumulativeDate,
                                            exDate = stockSplit.exDate,
                                            recordingDate = stockSplit.recDate,
                                            splitFactor = stockSplit.splitFactor
                                        )
                                    }
                                    getStockSplitResult.postValue(stockSplitData)

                                }
                                4 -> {
                                    val bonusData = data?.sahamBonusList?.sortedByDescending{ it.payDate }?.map {bonus ->
                                        CalSahamBonus(
                                            stockCode = bonus.stockCode,
                                            oldRatio = bonus.oldRatio,
                                            newRatio = bonus.newRatio,
                                            factor = bonus.factor,
                                            cumulativeDate = bonus.cumulativeDate,
                                            exDate = bonus.exDate,
                                            recordingDate = bonus.recordingDate,
                                            payDate = bonus.payDate
                                        )
                                    }
                                    getBonusResult.postValue(bonusData)

                                }
                                5 -> {
                                    val dividendData = data?.dividenList?.sortedByDescending{ it.paymentDate }?.map {dividen ->
                                        CalDividenSaham(
                                            stockCode = dividen.stockCode,
                                            cashDividend = dividen.cashDividend,
                                            cumulativeDate = dividen.cumulativeDate,
                                            exDate = dividen.exDate,
                                            recordingDate = dividen.recordingDate,
                                            paymentDate = dividen.paymentDate
                                        )
                                    }
                                    getDividendResult.postValue(dividendData)

                                }
                                6 -> {
                                    val publicExposeData = data?.pubExList?.sortedByDescending{ it.date }?.map {pubEx ->
                                        CalPubExp(
                                            stockCode = pubEx.stockCode,
                                            date = pubEx.date,
                                            time = pubEx.time,
                                            location = pubEx.location
                                        )
                                    }
                                    getPublicExposeResult.postValue(publicExposeData)

                                }
                                7 -> {
                                    val rupsData = data?.rupsList?.sortedByDescending{ it.date }?.map {rups ->
                                        CalRups(
                                            stockCode = rups.stockCode,
                                            date = rups.date,
                                            time = rups.time,
                                            location = rups.location
                                        )
                                    }
                                    getRupsResult.postValue(rupsData)

                                }
                                8 -> {
                                    val ipoData = data?.ipoList?.sortedByDescending{ it.listingDate }?.map {ipo ->
                                        CalIpo(
                                            stockCode = ipo.stockCode,
                                            companyName = ipo.companyName,
                                            totalShareListed = ipo.totalShareListed,
                                            listingDate = ipo.listingDate
                                        )
                                    }
                                    getIpoResult.postValue(ipoData)

                                }
                                9 -> {
                                    val reverseStockData = data?.rvsList?.sortedByDescending{ it.cumulativeDate }?.map {reverseSplit ->
                                        CalReverseStock(
                                            stockCode = reverseSplit.stockName,
                                            oldRatio = reverseSplit.oldRatio,
                                            newRatio = reverseSplit.newRatio,
                                            factor = reverseSplit.factor,
                                            cumulativeDate = reverseSplit.cumulativeDate,
                                            exDate = reverseSplit.exDate,
                                            paymentDate = reverseSplit.recDate
                                        )
                                    }
                                    getReverseStockResult.postValue(reverseStockData)
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}