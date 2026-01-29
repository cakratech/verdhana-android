package com.bcasekuritas.mybest.app.feature.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.CaCalendarbyCaDateInRangeReq
import com.bcasekuritas.mybest.app.domain.dto.response.CalDividenSaham
import com.bcasekuritas.mybest.app.domain.dto.response.CalIpo
import com.bcasekuritas.mybest.app.domain.dto.response.CalPubExp
import com.bcasekuritas.mybest.app.domain.dto.response.CalReverseStock
import com.bcasekuritas.mybest.app.domain.dto.response.CalRightIssue
import com.bcasekuritas.mybest.app.domain.dto.response.CalRups
import com.bcasekuritas.mybest.app.domain.dto.response.CalSahamBonus
import com.bcasekuritas.mybest.app.domain.dto.response.CalStockSplit
import com.bcasekuritas.mybest.app.domain.dto.response.CalWarrant
import com.bcasekuritas.mybest.app.domain.interactors.GetCalendarByDateInRangeUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.common.millisToDateOnly
import com.bcasekuritas.rabbitmq.proto.news.CorporateActionCalendarGetResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getCalendarByDateInRangeUseCase: GetCalendarByDateInRangeUseCase,
) : BaseViewModel() {

    val getCalendarDataResult = MutableLiveData<Resource<CorporateActionCalendarGetResponse?>>()
    val getCalendarDataResults = MutableLiveData<MutableMap<Long, MutableList<Any>>>()
    private var corpActionList = mutableListOf<Any>()
    private var corpActionMap = mutableMapOf<Long, MutableList<Any>>()

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCalendar(userId: String, sessionId: String, year: String, month: String) {
        viewModelScope.launch {
            getCalendarByDateInRangeUseCase.invoke(
                CaCalendarbyCaDateInRangeReq(
                    userId, sessionId, year, month
                )
            ).collect() { resource ->
                resource.let {
                    corpActionMap = mutableMapOf()
                    when (it) {
                        is Resource.Success -> {
                            val calData = it.data?.calendar

                            calData?.warrantList?.map { warrant ->
                                val date = millisToDateOnly(warrant.tradingStart)
                                corpActionMap.computeIfAbsent(date) { mutableListOf() }.apply {
                                    add(
                                        CalWarrant(
                                            stockCode = warrant.stockCode,
                                            excercisePrice = warrant.excercisePrice,
                                            tradingStart = warrant.tradingStart,
                                            tradingEnd = warrant.tradingEnd,
                                            excerciseStart = warrant.excerciseStart,
                                            excerciseEnd = warrant.excerciseEnd
                                        )
                                    )
                                }
                            }

                            calData?.rightIsueList?.map { rightIssue ->
                                val date = millisToDateOnly(rightIssue.tradingStart)
                                corpActionMap.computeIfAbsent(date) { mutableListOf() }.apply {
                                    add(
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
                                    )
                                }
                            }

                            calData?.stockSplitList?.map { stockSplit ->
                                val date = millisToDateOnly(stockSplit.cumulativeDate)
                                corpActionMap.computeIfAbsent(date) { mutableListOf() }.apply {
                                    add(
                                        CalStockSplit(
                                            stockCode = stockSplit.stockCode,
                                            oldRatio = stockSplit.oldRatio,
                                            newRatio = stockSplit.newRatio,
                                            cumulativeDate = stockSplit.cumulativeDate,
                                            exDate = stockSplit.exDate,
                                            recordingDate = stockSplit.recDate,
                                            splitFactor = stockSplit.splitFactor
                                        )
                                    )
                                }
                            }

                            calData?.sahamBonusList?.map { bonus ->
                                val date = millisToDateOnly(bonus.payDate)
                                corpActionMap.computeIfAbsent(date) { mutableListOf() }.apply {
                                    add(
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
                                    )
                                }
                            }

                            calData?.rvsList?.map { reverseSplit ->
                                val date = millisToDateOnly(reverseSplit.cumulativeDate)
                                corpActionMap.computeIfAbsent(date) { mutableListOf() }.apply {
                                    add(
                                        CalReverseStock(
                                            stockCode = reverseSplit.stockName,
                                            oldRatio = reverseSplit.oldRatio,
                                            newRatio = reverseSplit.newRatio,
                                            factor = reverseSplit.factor,
                                            cumulativeDate = reverseSplit.cumulativeDate,
                                            exDate = reverseSplit.exDate,
                                            paymentDate = reverseSplit.recDate
                                        )
                                    )
                                }
                            }

                            calData?.dividenList?.map { dividen ->
                                val date = millisToDateOnly(dividen.paymentDate)
                                corpActionMap.computeIfAbsent(date) { mutableListOf() }.apply {
                                    add(
                                        CalDividenSaham(
                                            stockCode = dividen.stockCode,
                                            cashDividend = dividen.cashDividend,
                                            cumulativeDate = dividen.cumulativeDate,
                                            exDate = dividen.exDate,
                                            recordingDate = dividen.recordingDate,
                                            paymentDate = dividen.paymentDate
                                        )
                                    )
                                }
                            }

                            calData?.pubExList?.map { pubEx ->
                                val date = millisToDateOnly(pubEx.date)
                                corpActionMap.computeIfAbsent(date) { mutableListOf() }.apply {
                                    add(
                                        CalPubExp(
                                            stockCode = pubEx.stockCode,
                                            date = pubEx.date,
                                            time = pubEx.time,
                                            location = pubEx.location
                                        )
                                    )
                                }
                            }

                            calData?.rupsList?.map { rups ->
                                val date = millisToDateOnly(rups.date)
                                corpActionMap.computeIfAbsent(date) { mutableListOf() }.apply {
                                    add(
                                        CalRups(
                                            stockCode = rups.stockCode,
                                            date = rups.date,
                                            time = rups.time,
                                            location = rups.location
                                        )
                                    )
                                }
                            }

                            calData?.ipoList?.map { ipo ->
                                val date = millisToDateOnly(ipo.listingDate)
                                corpActionMap.computeIfAbsent(date) { mutableListOf() }.apply {
                                    add(
                                        CalIpo(
                                            stockCode = ipo.stockCode,
                                            companyName = ipo.companyName,
                                            totalShareListed = ipo.totalShareListed,
                                            listingDate = ipo.listingDate
                                        )
                                    )
                                }
                            }

                            getCalendarDataResults.postValue(corpActionMap)
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}