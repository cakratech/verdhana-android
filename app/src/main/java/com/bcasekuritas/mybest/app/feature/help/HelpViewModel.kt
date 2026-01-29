package com.bcasekuritas.mybest.app.feature.help

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.FaqReq
import com.bcasekuritas.mybest.app.domain.dto.request.HelpReq
import com.bcasekuritas.mybest.app.domain.dto.response.FaqHelpData
import com.bcasekuritas.mybest.app.domain.interactors.GetFaqByCategoryUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetTopFiveFaqUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetVideoTutorialUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.news.CMSNewsTutorialVideoDTO
import com.bcasekuritas.rabbitmq.proto.news.GetNewsTopFiveFrequentAskedQuestionResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HelpViewModel @Inject constructor(
    private val getTopFiveFaqUseCase: GetTopFiveFaqUseCase,
    private val getFaqByCategoryUseCase: GetFaqByCategoryUseCase,
    private val getVideoTutorialUseCase: GetVideoTutorialUseCase
) : BaseViewModel() {

    val getTopFiveFaqResult = MutableLiveData<List<FaqHelpData>?>()
    val getFaqByCategoryResult = MutableLiveData<List<FaqHelpData>?>()
    val getVideoTutorialResult = MutableLiveData<List<CMSNewsTutorialVideoDTO>>()

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

    fun getFaqByCategory(userId: String, sessionId: String, category: String){
        viewModelScope.launch(Dispatchers.IO) {
            val faqRequest = FaqReq(userId, sessionId, category, size = 9999, page = 0)

            getFaqByCategoryUseCase.invoke(faqRequest).collect() {resource ->
                resource.let{
                    when(it){
                        is Resource.Success -> {
                            val data = it.data?.faqDtoDataList?.map { item ->
                                FaqHelpData(
                                    id = item.id,
                                    questionName = item.questionName,
                                    answer = item.answer,
                                    category = item.category,
                                    activate = item.activate,
                                    createdDate = item.createdDateTime,
                                    lastActivateDate = item.lastActivateTime,
                                    isHelpful = false
                                )
                            }
                            getFaqByCategoryResult.postValue(data)
                        }
                        else -> {}
                    }

                }
            }
        }
    }

    fun getVideoTutorial(userId: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = HelpReq(userId, sessionId)

            getVideoTutorialUseCase.invoke(request).collect() {resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            val data = it.data?.tutorialVideoDataList
                            getVideoTutorialResult.postValue(data?.filterNotNull())

                        } else -> {}
                    }
                }
            }
        }
    }
    
}