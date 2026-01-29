package com.bcasekuritas.mybest.app.domain.datasource.remote

import com.bcasekuritas.mybest.app.data.entity.AccountObject
import com.bcasekuritas.mybest.app.domain.dto.request.FaqReq
import com.bcasekuritas.mybest.app.domain.dto.request.HelpReq
import com.bcasekuritas.mybest.app.domain.dto.request.RdnHistoryRequest
import com.bcasekuritas.mybest.app.domain.dto.request.WithdrawCashReq
import com.bcasekuritas.rabbitmq.proto.bcas.AccountCashMovementResponse
import com.bcasekuritas.rabbitmq.proto.bcas.WithdrawCashResponse
import com.bcasekuritas.rabbitmq.proto.news.GetNewsFrequentAskedQuestionByCategoryResponse
import com.bcasekuritas.rabbitmq.proto.news.GetNewsFrequentAskedQuestionResponse
import com.bcasekuritas.rabbitmq.proto.news.GetNewsTopFiveFrequentAskedQuestionResponse
import com.bcasekuritas.rabbitmq.proto.news.GetNewsTutorialVideoResponse
import com.bcasekuritas.rabbitmq.proto.news.SearchNewsFrequentAskedQuestionResponse

interface ProfileDataSource {

    suspend fun insertAccount(accountRes: AccountObject)
    suspend fun getAccName(accNo: String): String
    suspend fun getAccountInfo(accNo: String): AccountObject
    suspend fun getAllAccount(): List<AccountObject>
    suspend fun clearAccount()

    suspend fun getWithdrawCash(withdrawCashReq: WithdrawCashReq): WithdrawCashResponse?
    suspend fun getRdnHistory(rdnHistoryRequest: RdnHistoryRequest): AccountCashMovementResponse?

    /** Help */
    suspend fun getTopFiveFaq(helpReq: HelpReq): GetNewsTopFiveFrequentAskedQuestionResponse?
    suspend fun getFaqByCategory(faqReq: FaqReq): GetNewsFrequentAskedQuestionByCategoryResponse?
    suspend fun getFaq(helpReq: HelpReq): GetNewsFrequentAskedQuestionResponse?
    suspend fun getSearchFaq(faqReq: FaqReq): SearchNewsFrequentAskedQuestionResponse?
    suspend fun getVideoTutorial(helpReq: HelpReq): GetNewsTutorialVideoResponse?
}