package com.bcasekuritas.mybest.app.feature.e_ipo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.mapper.toIpoListData
import com.bcasekuritas.mybest.app.domain.dto.request.IPOListRequest
import com.bcasekuritas.mybest.app.domain.dto.response.IpoData
import com.bcasekuritas.mybest.app.domain.interactors.GetIPOListUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EIPOViewModel @Inject constructor(
    private val getIPOListUseCase: GetIPOListUseCase
): BaseViewModel() {

    val getIpoListResult = MutableLiveData<List<IpoData>>()

    fun getIpoList(userId: String, sessionId: String, page: Int, size: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = IPOListRequest(userId, sessionId, false, 2, size, page)

            getIPOListUseCase.invoke(request).collect() {resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            val data = it.data?.pipelinesIpoListDataList?.toIpoListData()

                            getIpoListResult.postValue(data?.filterNotNull())
                        }
                        else -> {}
                    }
                }
            }
        }
    }

}