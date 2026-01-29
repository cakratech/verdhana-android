package com.bcasekuritas.mybest.app.feature.help.searchquestion

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.FaqReq
import com.bcasekuritas.mybest.app.domain.dto.request.HelpReq
import com.bcasekuritas.mybest.app.domain.dto.response.FaqHelpData
import com.bcasekuritas.mybest.app.domain.dto.response.SearchFaqData
import com.bcasekuritas.mybest.app.domain.interactors.GetFaqUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSearchFaqUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetTopFiveFaqUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.property.orZero
import com.bcasekuritas.rabbitmq.proto.news.CMSNewsFrequentAskedQuestionDTO
import com.bcasekuritas.rabbitmq.proto.news.CMSNewsTutorialVideoDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchQuestionsViewModel @Inject constructor(
    private val getTopFiveFaqUseCase: GetTopFiveFaqUseCase,
    private val getSearchFaqUseCase: GetSearchFaqUseCase,
    private val faqUseCase: GetFaqUseCase
) : BaseViewModel() {

    val getFaqResult = MutableLiveData<List<FaqHelpData>?>()
    val getTopFiveFaqResult = MutableLiveData<List<FaqHelpData>?>()
    val getSearchFaqUseCaseResult = MutableLiveData<SearchFaqData?>()


    fun getFaq(userId: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = HelpReq(userId, sessionId)

            faqUseCase.invoke(request).collect() {resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            val data = it.data?.faqDtoDataList?.map { item ->
                                FaqHelpData(
                                    id = item.id,
                                    questionName = item.questionName,
                                    answer = item.answer,
                                    category = "",
                                    activate = item.activate,
                                    createdDate = item.createdDateTime,
                                    lastActivateDate = item.lastActivateTime,
                                    isHelpful = false
                                )
                            }
                            getFaqResult.postValue(data)

                        } else -> {}
                    }
                }
            }
        }
    }

    fun getSearchFaq(userId: String, sessionId: String, query: String, page: Int){
        val request = FaqReq(
            userId = userId,
            sessionId = sessionId,
            query1 = query,
            page = page)

        viewModelScope.launch(Dispatchers.IO) {

            getSearchFaqUseCase.invoke(request).collect() {resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            val listFaqData = it.data?.faqDtoDataList?.map { item ->
                                FaqHelpData(
                                    id = item.id,
                                    questionName = item.questionName,
                                    answer = item.answer,
                                    category = "",
                                    activate = item.activate,
                                    createdDate = item.createdDateTime,
                                    lastActivateDate = item.lastActivateTime,
                                    isHelpful = false
                                )
                            }

                            val searchFaqData = SearchFaqData(it.data?.searchHits.orZero(), listFaqData.orEmpty())
                            getSearchFaqUseCaseResult.postValue(searchFaqData)

                        } else -> {}
                    }
                }
            }
        }
    }

    fun getTopFiveFaq(userId: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val topFiveRequest = HelpReq(userId, sessionId)

            getTopFiveFaqUseCase.invoke(topFiveRequest).collect() { resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            val data = it.data?.faqTopFiveDtoDataList?.map { item ->
                                FaqHelpData(
                                    id = item.id,
                                    questionName = item.questionName,
                                    answer = item.answer,
                                    category = "",
                                    activate = item.activate,
                                    createdDate = item.createdDateTime,
                                    lastActivateDate = item.lastActivateTime,
                                    isHelpful = false
                                )
                            }
                            getTopFiveFaqResult.postValue(data)

                        } else -> {}
                    }
                }
            }
        }
    }

}