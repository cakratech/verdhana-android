package com.bcasekuritas.mybest.app.feature.stockdetail.about

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.CompanyProfileRequest
import com.bcasekuritas.mybest.app.domain.interactors.GetCompanyProfileUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.widget.progressdialog.ProgressDialogHelper
import com.bcasekuritas.rabbitmq.proto.news.StockDetilCompanyProfileResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StockDetailAboutViewModel @Inject constructor(
    private val getCompanyProfileUseCase: GetCompanyProfileUseCase
): BaseViewModel(){
    val getCompanyProfileResult = MutableLiveData<Resource<StockDetilCompanyProfileResponse?>>()
    private val progressDialogHelper = ProgressDialogHelper()

    fun getCompanyProfile(userId: String, userSession: String, stockCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val companyProfileRequest = CompanyProfileRequest(userId,userSession, stockCode)

            getCompanyProfileUseCase.invoke(companyProfileRequest).collect(){resource ->
                resource.let {
                    getCompanyProfileResult.postValue(it)
                }
            }
        }
    }
}