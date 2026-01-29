package com.bcasekuritas.mybest.app.domain.repositories

import com.bcasekuritas.mybest.app.domain.dto.request.DetailFinancialRequest
import com.bcasekuritas.mybest.app.domain.dto.request.FinancialRequest
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.news.FinancialBalanceSheetResponse
import com.bcasekuritas.rabbitmq.proto.news.FinancialCashFlowResponse
import com.bcasekuritas.rabbitmq.proto.news.FinancialIncomeStatementResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewBalanceSheetResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewCashFlowResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewIncomeStatementResponse
import kotlinx.coroutines.flow.Flow

interface FinancialRepo {
    suspend fun getIncomeStatement(financialRequest: FinancialRequest): Flow<Resource<ViewIncomeStatementResponse?>>
    suspend fun getSheetBalance(financialRequest: FinancialRequest): Flow<Resource<ViewBalanceSheetResponse?>>
    suspend fun getCashFlow(financialRequest: FinancialRequest): Flow<Resource<ViewCashFlowResponse?>>
    suspend fun getDetailIncomeStatement(detailFinancialRequest: DetailFinancialRequest): Flow<Resource<FinancialIncomeStatementResponse?>>
    suspend fun getDetailBalanceSheet(detailFinancialRequest: DetailFinancialRequest): Flow<Resource<FinancialBalanceSheetResponse?>>
    suspend fun getDetailCashFlow(detailFinancialRequest: DetailFinancialRequest): Flow<Resource<FinancialCashFlowResponse?>>
}