package com.bcasekuritas.mybest.app.domain.datasource.remote

import com.bcasekuritas.mybest.app.domain.dto.request.DetailFinancialRequest
import com.bcasekuritas.mybest.app.domain.dto.request.FinancialRequest
import com.bcasekuritas.mybest.app.domain.dto.request.KeyStatRequest
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.news.FinancialBalanceSheetRequest
import com.bcasekuritas.rabbitmq.proto.news.FinancialBalanceSheetResponse
import com.bcasekuritas.rabbitmq.proto.news.FinancialCashFlowRequest
import com.bcasekuritas.rabbitmq.proto.news.FinancialCashFlowResponse
import com.bcasekuritas.rabbitmq.proto.news.FinancialIncomeStatementRequest
import com.bcasekuritas.rabbitmq.proto.news.FinancialIncomeStatementResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewBalanceSheetResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewCashFlowResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewIncomeStatementResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewKeyStatResponse
import kotlinx.coroutines.flow.Flow

interface FinancialDataSource {
    suspend fun getIncomeStatement(financialRequest: FinancialRequest): ViewIncomeStatementResponse?
    suspend fun getSheetBalance(financialRequest: FinancialRequest): ViewBalanceSheetResponse?
    suspend fun getCashFlow(financialRequest: FinancialRequest): ViewCashFlowResponse?
    suspend fun getDetailIncomeStatement(detailFinancialRequest: DetailFinancialRequest): FinancialIncomeStatementResponse?
    suspend fun getDetailBalanceSheet(detailFinancialRequest: DetailFinancialRequest): FinancialBalanceSheetResponse?
    suspend fun getDetailCashFlow(detailFinancialRequest: DetailFinancialRequest): FinancialCashFlowResponse?
}