package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.data.dao.AccountDao
import com.bcasekuritas.mybest.app.data.entity.AccountObject
import com.bcasekuritas.mybest.app.domain.datasource.remote.ProfileDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.FaqReq
import com.bcasekuritas.mybest.app.domain.dto.request.HelpReq
import com.bcasekuritas.mybest.app.domain.dto.request.RdnHistoryRequest
import com.bcasekuritas.mybest.app.domain.dto.request.WithdrawCashReq
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.proto.bcas.AccountCashMovementRequest
import com.bcasekuritas.rabbitmq.proto.bcas.AccountCashMovementResponse
import com.bcasekuritas.rabbitmq.proto.bcas.PageRequest
import com.bcasekuritas.rabbitmq.proto.bcas.WithdrawCashRequest
import com.bcasekuritas.rabbitmq.proto.bcas.WithdrawCashResponse
import com.bcasekuritas.rabbitmq.proto.news.GetNewsFrequentAskedQuestionByCategoryRequest
import com.bcasekuritas.rabbitmq.proto.news.GetNewsFrequentAskedQuestionByCategoryResponse
import com.bcasekuritas.rabbitmq.proto.news.GetNewsFrequentAskedQuestionRequest
import com.bcasekuritas.rabbitmq.proto.news.GetNewsFrequentAskedQuestionResponse
import com.bcasekuritas.rabbitmq.proto.news.GetNewsTopFiveFrequentAskedQuestionRequest
import com.bcasekuritas.rabbitmq.proto.news.GetNewsTopFiveFrequentAskedQuestionResponse
import com.bcasekuritas.rabbitmq.proto.news.GetNewsTutorialVideoRequest
import com.bcasekuritas.rabbitmq.proto.news.GetNewsTutorialVideoResponse
import com.bcasekuritas.rabbitmq.proto.news.SearchNewsFrequentAskedQuestionRequest
import com.bcasekuritas.rabbitmq.proto.news.SearchNewsFrequentAskedQuestionResponse
import javax.inject.Inject

class ProfileDataSourceImpl @Inject constructor(
    private val oltService: OLTService,
    private val localSourceData: AccountDao
) : ProfileDataSource {
    override suspend fun insertAccount(accountRes: AccountObject) {
        accountRes.let {
            localSourceData.insertAccount(AccountObject(
                accNo = it.accNo ?: "",
                accName = it.accName ?: "",
                productType = it.productType ?: "",
                userId = it.userId ?: "",
                clientName = it.clientName ?: "",
                cifCode = it.cifCode ?: ""
            ))
        }
    }

    override suspend fun getAccName(accNo: String): String {
        return localSourceData.getAccName(accNo)
    }

    override suspend fun getAccountInfo(accNo: String): AccountObject {
        return localSourceData.getAccountInfo(accNo)
    }

    override suspend fun getAllAccount(): List<AccountObject> {
        return localSourceData.getAllAccount()
    }

    override suspend fun getWithdrawCash(withdrawCashReq: WithdrawCashReq): WithdrawCashResponse? {
        val withdrawCashRequest = WithdrawCashRequest.newBuilder()
            .setUserId(withdrawCashReq.userId)
            .setSessionId(withdrawCashReq.sessionId)
            .setAccNo(withdrawCashReq.accNo)
            .setAmount(withdrawCashReq.amount)
            .setMediaSource(0)
            .setNotes(withdrawCashReq.notes)
            .setReference(withdrawCashReq.reference)
            .build()

        return oltService.getWithdrawCash(withdrawCashRequest)
    }

    override suspend fun getRdnHistory(rdnHistoryRequest: RdnHistoryRequest): AccountCashMovementResponse? {
        val pageRequest = PageRequest.newBuilder()
            .setPage(rdnHistoryRequest.pageRequest.page)
            .setSize(rdnHistoryRequest.pageRequest.size)
            .build()

        val cashMovementRequest = AccountCashMovementRequest.newBuilder()
            .setUserId(rdnHistoryRequest.userId)
            .setAccno(rdnHistoryRequest.accNo)
            .setMovementDate(rdnHistoryRequest.startDate)
            .setEndMovementDate(rdnHistoryRequest.endDate)
            .setSessionId(rdnHistoryRequest.sessionId)
            .setType(rdnHistoryRequest.type)
            .setPageRequest(pageRequest)
            .build()

        return oltService.getRdnHistory(cashMovementRequest)
    }

    override suspend fun getTopFiveFaq(helpReq: HelpReq): GetNewsTopFiveFrequentAskedQuestionResponse? {
        val topFiveFaqRequest = GetNewsTopFiveFrequentAskedQuestionRequest.newBuilder()
            .setUserId(helpReq.userId)
            .setSessionId(helpReq.sessionId)
            .build()

        return oltService.getTopFiveFaq(topFiveFaqRequest)
    }

    override suspend fun getFaqByCategory(faqReq: FaqReq): GetNewsFrequentAskedQuestionByCategoryResponse? {

        val faqByCategory = if (faqReq.size != null && faqReq.page != null){
            GetNewsFrequentAskedQuestionByCategoryRequest.newBuilder()
                .setUserId(faqReq.userId)
                .setSessionId(faqReq.sessionId)
                .setCategory(faqReq.category)
                .setSize(faqReq.size)
                .setPage(faqReq.page)
                .build()
        } else {
            GetNewsFrequentAskedQuestionByCategoryRequest.newBuilder()
                .setUserId(faqReq.userId)
                .setSessionId(faqReq.sessionId)
                .setCategory(faqReq.category)
                .build()
        }

        return oltService.getFaqByCategory(faqByCategory)
    }

    override suspend fun getSearchFaq(faqReq: FaqReq): SearchNewsFrequentAskedQuestionResponse? {
        val searchFaqRequest = SearchNewsFrequentAskedQuestionRequest.newBuilder()
            .setUserId(faqReq.userId)
            .setSessionId(faqReq.sessionId)
            .setQuery1(faqReq.query1)
            .setQuery2("")
            .setSize(10)
            .setPage(faqReq.page ?: 0)
            .build()

        return oltService.getSearchFaq(searchFaqRequest)
    }

    override suspend fun getFaq(helpReq: HelpReq): GetNewsFrequentAskedQuestionResponse? {
        val request = GetNewsFrequentAskedQuestionRequest.newBuilder()
            .setUserId(helpReq.userId)
            .setSessionId(helpReq.sessionId)
            .build()

        return oltService.getFaq(request)
    }

    override suspend fun getVideoTutorial(helpReq: HelpReq): GetNewsTutorialVideoResponse? {
        val request = GetNewsTutorialVideoRequest.newBuilder()
            .setUserId(helpReq.userId)
            .setSessionId(helpReq.sessionId)
            .build()

        return oltService.getHelpTutorialVideo(request)
    }

    override suspend fun clearAccount() {
        localSourceData.clearAllAccounts()
    }
}