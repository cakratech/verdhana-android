package com.bcasekuritas.mybest.app.domain.repositories

import com.bcasekuritas.mybest.app.data.entity.AccountObject
import com.bcasekuritas.mybest.app.domain.dto.request.FaqReq
import com.bcasekuritas.mybest.app.domain.dto.request.HelpReq
import com.bcasekuritas.mybest.app.domain.dto.request.RdnHistoryRequest
import com.bcasekuritas.mybest.app.domain.dto.request.WithdrawCashReq
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.AccountCashMovementResponse
import com.bcasekuritas.rabbitmq.proto.bcas.WithdrawCashResponse
import com.bcasekuritas.rabbitmq.proto.news.GetNewsFrequentAskedQuestionByCategoryResponse
import com.bcasekuritas.rabbitmq.proto.news.GetNewsFrequentAskedQuestionResponse
import com.bcasekuritas.rabbitmq.proto.news.GetNewsTopFiveFrequentAskedQuestionResponse
import com.bcasekuritas.rabbitmq.proto.news.GetNewsTutorialVideoResponse
import com.bcasekuritas.rabbitmq.proto.news.SearchNewsFrequentAskedQuestionResponse
import kotlinx.coroutines.flow.Flow

interface ProfileRepo {
    suspend fun insertAccountDao(accountRes: AccountObject)
    suspend fun getAccName(accNo: String): Flow<Resource<String>>
    suspend fun getAccountInfo(accNo: String): Flow<Resource<AccountObject>>
    suspend fun getAllAccount(): Flow<Resource<List<AccountObject>>>
    suspend fun clearAllAccount()

    suspend fun getWithdrawCash(withdrawCashReq: WithdrawCashReq): Flow<Resource<WithdrawCashResponse?>>
    suspend fun getRdnHistory(rdnHistoryRequest: RdnHistoryRequest): Flow<Resource<AccountCashMovementResponse?>>

    /** Help */
    suspend fun getTopFiveFaq(helpReq: HelpReq): Flow<Resource<GetNewsTopFiveFrequentAskedQuestionResponse?>>
    suspend fun getFaqByCategory(faqReq: FaqReq): Flow<Resource<GetNewsFrequentAskedQuestionByCategoryResponse?>>
    suspend fun getSearchFaq(faqReq: FaqReq): Flow<Resource<SearchNewsFrequentAskedQuestionResponse?>>
    suspend fun getFaq(helpReq: HelpReq): Flow<Resource<GetNewsFrequentAskedQuestionResponse?>>
    suspend fun getVideoTutorial(helpReq: HelpReq): Flow<Resource<GetNewsTutorialVideoResponse?>>
}