package com.bcasekuritas.mybest.app.feature.news.tabnews

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.NewsFeedRequest
import com.bcasekuritas.mybest.app.domain.dto.request.NewsFeedSearchRequest
import com.bcasekuritas.mybest.app.domain.interactors.GetNewsFeedSearchUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetNewsFeedUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetNewsRssFeedUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.news.rssconverter.RssFeed
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoFeed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TabNewsViewModel @Inject constructor(
    private val getNewsRssFeedUseCase: GetNewsRssFeedUseCase,
    private val getNewsFeedUseCase: GetNewsFeedUseCase,
    private val getNewsFeedSearchUseCase: GetNewsFeedSearchUseCase,
    val imqConnectionListener: IMQConnectionListener
): BaseViewModel() {

    val getRssFeedResult = MutableLiveData<RssFeed?>()
    val getNewsFeedResult = MutableLiveData<List<NewsInfoFeed>>()

    fun getNewsFeed(userId: String, sessionId: String, page: Int, size: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = NewsFeedRequest(userId, sessionId, page, size, 1)

            getNewsFeedUseCase.invoke(request).collect() {resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            val data = it.data
                            if (data != null) {
                                if (data.newsInfoFeedDataCount != 0) {
                                    getNewsFeedResult.postValue(data.newsInfoFeedDataList)
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun getNewsFeedSearch(userId: String, sessionId: String, page: Int, size: Int, searchKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = NewsFeedSearchRequest(userId, sessionId, page, size, searchKey, false)

            getNewsFeedSearchUseCase.invoke(request).collect() {resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            val data = it.data
                            if (data != null) {
                                getNewsFeedResult.postValue(data.newsInfoFeedDataList)
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun getRssFeed(link: String) {
        viewModelScope.launch(Dispatchers.IO) {

            getNewsRssFeedUseCase.invoke(link).collect(){resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            if (it.data != null) {
                                getRssFeedResult.postValue(it.data)
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

}