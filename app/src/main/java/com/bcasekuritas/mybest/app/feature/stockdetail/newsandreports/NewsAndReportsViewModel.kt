package com.bcasekuritas.mybest.app.feature.stockdetail.newsandreports

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.NewsFeedByStockRequest
import com.bcasekuritas.mybest.app.domain.dto.request.NewsFeedSearchRequest
import com.bcasekuritas.mybest.app.domain.interactors.GetNewsFeedByStockUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetResearchContentSearchUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoFeed
import com.bcasekuritas.rabbitmq.proto.news.NewsResearchContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsAndReportsViewModel @Inject constructor(
    private val getNewsFeedByStockUseCase: GetNewsFeedByStockUseCase,
    private val getResearchContentSearchUseCase: GetResearchContentSearchUseCase
): BaseViewModel(){
    val getNewsFeedByStockResult = MutableLiveData<List<NewsInfoFeed>>()
    val getResearchNewsResult = MutableLiveData<List<NewsResearchContent>>()

    fun getNewsFeed(userId: String, sessionId: String, stockCode: String, page: Int, size: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val listStock = listOf<String>(stockCode)
            val request = NewsFeedByStockRequest(userId, sessionId, page, size, listStock)

            getNewsFeedByStockUseCase.invoke(request).collect() {resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            val data = it.data
                            if (data != null) {
                                if (data.newsInfoFeedDataCount != 0) {
                                    getNewsFeedByStockResult.postValue(data.newsInfoFeedDataList)
                                }
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
            val researchNewsReq = NewsFeedSearchRequest(userId, sessionId, page, size, searchKey, true)

            getResearchContentSearchUseCase.invoke(researchNewsReq).collect() {resource ->
                resource.let {
                    when (resource) {
                        is Resource.Success -> {
                            if (resource.data != null) {
                                if (resource.data.newsResearchContentCount != 0) {
                                    getResearchNewsResult.postValue(resource.data.newsResearchContentList)
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