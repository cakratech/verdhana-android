package com.bcasekuritas.mybest.app.feature.news.tabresearch

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.NewsFeedSearchRequest
import com.bcasekuritas.mybest.app.domain.dto.request.NewsResearchContentReq
import com.bcasekuritas.mybest.app.domain.interactors.GetResearchContentSearchUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetResearchNewsUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.proto.news.NewsResearchContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TabResearchViewModel @Inject constructor(
    private val getResearchNewsUseCase: GetResearchNewsUseCase,
    private val getResearchContentSearchUseCase: GetResearchContentSearchUseCase,
    val imqConnectionListener: IMQConnectionListener
): BaseViewModel() {

    val getResearchNewsResult = MutableLiveData<List<NewsResearchContent>>()

    fun getResearchNews(userId: String, sessionId: String, page: Int, size: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val researchNewsReq = NewsResearchContentReq(userId, sessionId, page, size)

            getResearchNewsUseCase.invoke(researchNewsReq).collect() {resource ->
                resource.let {
                    when (resource) {
                        is Resource.Success -> {
                            if (resource.data != null) {
                                getResearchNewsResult.postValue(resource.data.newsResearchContentList)
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun getResearchNewsSearch(userId: String, sessionId: String, page: Int, size: Int, searchKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val researchNewsReq = NewsFeedSearchRequest(userId, sessionId, page, size, searchKey, false)

            getResearchContentSearchUseCase.invoke(researchNewsReq).collect() {resource ->
                resource.let {
                    when (resource) {
                        is Resource.Success -> {
                            if (resource.data != null) {
                                getResearchNewsResult.postValue(resource.data.newsResearchContentList)
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

}