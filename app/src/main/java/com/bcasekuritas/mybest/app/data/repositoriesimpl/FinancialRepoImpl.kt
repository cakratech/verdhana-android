package com.bcasekuritas.mybest.app.data.repositoriesimpl

import com.bcasekuritas.mybest.app.domain.datasource.remote.FinancialDataSource
import com.bcasekuritas.mybest.app.domain.datasource.remote.NewsDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.DetailFinancialRequest
import com.bcasekuritas.mybest.app.domain.dto.request.FinancialRequest
import com.bcasekuritas.mybest.app.domain.dto.request.KeyStatRequest
import com.bcasekuritas.mybest.app.domain.repositories.FinancialRepo
import com.bcasekuritas.mybest.app.domain.repositories.NewsRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
import com.bcasekuritas.rabbitmq.proto.news.FinancialBalanceSheetResponse
import com.bcasekuritas.rabbitmq.proto.news.FinancialCashFlowResponse
import com.bcasekuritas.rabbitmq.proto.news.FinancialIncomeStatementResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewBalanceSheetResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewCashFlowResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewIncomeStatementResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewKeyStatResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FinancialRepoImpl @Inject constructor(
    private val remoteSource: FinancialDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) : FinancialRepo {
    override suspend fun getIncomeStatement(financialRequest: FinancialRequest): Flow<Resource<ViewIncomeStatementResponse?>> = flow{
        emit(Resource.Success(data = remoteSource.getIncomeStatement(financialRequest), DataSource.REMOTE))
    }

    override suspend fun getSheetBalance(financialRequest: FinancialRequest): Flow<Resource<ViewBalanceSheetResponse?>> = flow{
        emit(Resource.Success(data = remoteSource.getSheetBalance(financialRequest), DataSource.REMOTE))
    }

    override suspend fun getCashFlow(financialRequest: FinancialRequest): Flow<Resource<ViewCashFlowResponse?>> = flow{
        emit(Resource.Success(data = remoteSource.getCashFlow(financialRequest), DataSource.REMOTE))
    }

    override suspend fun getDetailIncomeStatement(detailFinancialRequest: DetailFinancialRequest): Flow<Resource<FinancialIncomeStatementResponse?>> = flow{
        emit(Resource.Success(data = remoteSource.getDetailIncomeStatement(detailFinancialRequest), DataSource.REMOTE))
    }

    override suspend fun getDetailBalanceSheet(detailFinancialRequest: DetailFinancialRequest): Flow<Resource<FinancialBalanceSheetResponse?>> = flow{
        emit(Resource.Success(data = remoteSource.getDetailBalanceSheet(detailFinancialRequest), DataSource.REMOTE))
    }

    override suspend fun getDetailCashFlow(detailFinancialRequest: DetailFinancialRequest): Flow<Resource<FinancialCashFlowResponse?>> = flow{
        emit(Resource.Success(data = remoteSource.getDetailCashFlow(detailFinancialRequest), DataSource.REMOTE))
    }


}