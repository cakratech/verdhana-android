package com.bcasekuritas.mybest.app.feature.portfolio.portfoliodetail.tabportfolio

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.StockPosRequest
import com.bcasekuritas.mybest.app.domain.dto.response.PortfolioStockDataItem
import com.bcasekuritas.mybest.app.domain.interactors.GetStockPosUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortfolioTabPortfolioDetailViewModel @Inject constructor(
    private val getStockPosUseCase: GetStockPosUseCase,
): BaseViewModel() {

    val getPortfolioDetailResult = MutableLiveData<PortfolioStockDataItem>()

    fun getPortfolioDetail(userId: String, sessionId: String, accNo: String, stockCode:String) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = StockPosRequest(userId, accNo, sessionId, stockCode)

            getStockPosUseCase.invoke(request).collect() { resource->
                resource.let {
                    when (resource) {
                        is Resource.Success -> {
                            val data = resource.data?.accstockposList?.getOrNull(0)
                            if (data != null) {
                                val marketValue = data.realStockAvailable * data.reffprice
                                val profitLoss = (data.reffprice - data.avgprice) * data.realStockAvailable
                                val value = data.avgprice * data.realStockAvailable
                                val gainloss = marketValue - value
                                val pct = (gainloss / value) * 100
                                val qty = data.realStockAvailable / 100
                                val haircut = 100.minus(data.pctStockVal)

                                val res = PortfolioStockDataItem(
                                    data.stockcode,
                                    data.reffprice,
                                    data.avgprice,
                                    marketValue,
                                    pct,
                                    profitLoss,
                                    qty,
                                    haircut,
                                    potentialLot = data.potStockAvailable / 100,
                                    totalAsset = data.totalAsset,
                                    blockedLot = data.blockQty
                                )

                                getPortfolioDetailResult.postValue(res)
                            }
                        }
                        else -> {}
                    }
                }
            }
        }

    }

}