package com.bcasekuritas.mybest.app.data.repositoriesimpl

import com.bcasekuritas.mybest.app.data.entity.AccountObject
import com.bcasekuritas.mybest.app.domain.datasource.remote.ProfileDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.FaqReq
import com.bcasekuritas.mybest.app.domain.dto.request.HelpReq
import com.bcasekuritas.mybest.app.domain.dto.request.RdnHistoryRequest
import com.bcasekuritas.mybest.app.domain.dto.request.WithdrawCashReq
import com.bcasekuritas.mybest.app.domain.repositories.ProfileRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
import com.bcasekuritas.rabbitmq.proto.bcas.AccountCashMovementResponse
import com.bcasekuritas.rabbitmq.proto.bcas.WithdrawCashResponse
import com.bcasekuritas.rabbitmq.proto.news.GetNewsFrequentAskedQuestionByCategoryResponse
import com.bcasekuritas.rabbitmq.proto.news.GetNewsFrequentAskedQuestionResponse
import com.bcasekuritas.rabbitmq.proto.news.GetNewsTopFiveFrequentAskedQuestionResponse
import com.bcasekuritas.rabbitmq.proto.news.GetNewsTutorialVideoResponse
import com.bcasekuritas.rabbitmq.proto.news.SearchNewsFrequentAskedQuestionResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProfileRepoImpl @Inject constructor(
    private val remoteSource: ProfileDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) : ProfileRepo {
    override suspend fun insertAccountDao(accountRes: AccountObject) {
        remoteSource.insertAccount(accountRes)
    }

    override suspend fun getAccName(accNo: String): Flow<Resource<String>> = flow {
        emit(Resource.Success(data = remoteSource.getAccName(accNo), DataSource.REMOTE))
    }

    override suspend fun getAccountInfo(accNo: String): Flow<Resource<AccountObject>> = flow {
        emit(Resource.Success(data = remoteSource.getAccountInfo(accNo), DataSource.REMOTE))
    }

    override suspend fun getAllAccount(): Flow<Resource<List<AccountObject>>> = flow {
        emit(Resource.Success(data = remoteSource.getAllAccount(), DataSource.REMOTE))
    }

    override suspend fun getWithdrawCash(withdrawCashReq: WithdrawCashReq): Flow<Resource<WithdrawCashResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getWithdrawCash(withdrawCashReq), DataSource.REMOTE))
    }

    override suspend fun getRdnHistory(rdnHistoryRequest: RdnHistoryRequest): Flow<Resource<AccountCashMovementResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getRdnHistory(rdnHistoryRequest), DataSource.REMOTE))
    }

    override suspend fun getTopFiveFaq(helpReq: HelpReq): Flow<Resource<GetNewsTopFiveFrequentAskedQuestionResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getTopFiveFaq(helpReq), DataSource.REMOTE))
    }

    override suspend fun getFaqByCategory(faqReq: FaqReq): Flow<Resource<GetNewsFrequentAskedQuestionByCategoryResponse?>>  = flow {
        emit(Resource.Success(data = remoteSource.getFaqByCategory(faqReq), DataSource.REMOTE))
    }

    override suspend fun getSearchFaq(faqReq: FaqReq): Flow<Resource<SearchNewsFrequentAskedQuestionResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getSearchFaq(faqReq), DataSource.REMOTE))

    }

    override suspend fun getFaq(helpReq: HelpReq): Flow<Resource<GetNewsFrequentAskedQuestionResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getFaq(helpReq), DataSource.REMOTE))
    }

    override suspend fun getVideoTutorial(helpReq: HelpReq): Flow<Resource<GetNewsTutorialVideoResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getVideoTutorial(helpReq), DataSource.REMOTE))
    }

    override suspend fun clearAllAccount() {
        remoteSource.clearAccount()
    }

}