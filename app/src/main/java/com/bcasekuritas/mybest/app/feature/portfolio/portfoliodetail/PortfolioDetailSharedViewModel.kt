package com.bcasekuritas.mybest.app.feature.portfolio.portfoliodetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.domain.dto.response.PortfolioStockDataItem
import kotlinx.coroutines.launch

class PortfolioDetailSharedViewModel: ViewModel() {

    private val _portfolioDetailData = MutableLiveData<PortfolioStockDataItem>()
    val portfolioDetailData: LiveData<PortfolioStockDataItem>
        get() = _portfolioDetailData

    fun setDataPortfolioDetail(value: PortfolioStockDataItem) {
        viewModelScope.launch {
            _portfolioDetailData.value = value
        }
    }

}