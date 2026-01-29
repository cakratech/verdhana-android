package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.domain.datasource.remote.FinancialDataSource
import com.bcasekuritas.mybest.app.domain.datasource.remote.NewsDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.DetailFinancialRequest
import com.bcasekuritas.mybest.app.domain.dto.request.FinancialRequest
import com.bcasekuritas.mybest.app.domain.dto.request.KeyStatRequest
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.proto.news.FinancialBalanceSheetRequest
import com.bcasekuritas.rabbitmq.proto.news.FinancialBalanceSheetResponse
import com.bcasekuritas.rabbitmq.proto.news.FinancialCashFlowRequest
import com.bcasekuritas.rabbitmq.proto.news.FinancialCashFlowResponse
import com.bcasekuritas.rabbitmq.proto.news.FinancialIncomeStatementRequest
import com.bcasekuritas.rabbitmq.proto.news.FinancialIncomeStatementResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewBalanceSheetRequest
import com.bcasekuritas.rabbitmq.proto.news.ViewBalanceSheetResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewCashFlowRequest
import com.bcasekuritas.rabbitmq.proto.news.ViewCashFlowResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewIncomeStatementRequest
import com.bcasekuritas.rabbitmq.proto.news.ViewIncomeStatementResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewKeyStatRequest
import com.bcasekuritas.rabbitmq.proto.news.ViewKeyStatResponse
import javax.inject.Inject

class FinancialDataSourceImpl @Inject constructor(
    private val oltService: OLTService
) : FinancialDataSource {

    override suspend fun getIncomeStatement(financialRequest: FinancialRequest): ViewIncomeStatementResponse? {
        val viewIncomeStatementRequest = ViewIncomeStatementRequest
            .newBuilder()
            .setUserId(financialRequest.userId)
            .setSessionId(financialRequest.sessionId)
            .setStockCode(financialRequest.stockCode)
            .build()

        return oltService.getIncomeStatement(viewIncomeStatementRequest)
    }

    override suspend fun getSheetBalance(financialRequest: FinancialRequest): ViewBalanceSheetResponse? {
        val viewBalanceSheetRequest = ViewBalanceSheetRequest
            .newBuilder()
            .setUserId(financialRequest.userId)
            .setSessionId(financialRequest.sessionId)
            .setStockCode(financialRequest.stockCode)
            .build()

        return oltService.getBalanceSheet(viewBalanceSheetRequest)
    }

    override suspend fun getCashFlow(financialRequest: FinancialRequest): ViewCashFlowResponse? {
        val viewCashFlowRequest = ViewCashFlowRequest
            .newBuilder()
            .setUserId(financialRequest.userId)
            .setSessionId(financialRequest.sessionId)
            .setStockCode(financialRequest.stockCode)
            .build()

        return oltService.getCashFlow(viewCashFlowRequest)
    }

    override suspend fun getDetailIncomeStatement(detailFinancialRequest: DetailFinancialRequest): FinancialIncomeStatementResponse? {
        val financialIncomeStatementRequest = FinancialIncomeStatementRequest
            .newBuilder()
            .setUserId(detailFinancialRequest.userId)
            .setSessionId(detailFinancialRequest.sessionId)
            .setStockCode(detailFinancialRequest.stockCode)
            .setPeriodRange(detailFinancialRequest.periodRange)
            .setPeriodType(detailFinancialRequest.PeriodType)
            .build()

        return oltService.getDetailIncomeStatement(financialIncomeStatementRequest)
    }

    override suspend fun getDetailBalanceSheet(detailFinancialRequest: DetailFinancialRequest): FinancialBalanceSheetResponse? {
        val financialBalanceSheetRequest = FinancialBalanceSheetRequest
            .newBuilder()
            .setUserId(detailFinancialRequest.userId)
            .setSessionId(detailFinancialRequest.sessionId)
            .setStockCode(detailFinancialRequest.stockCode)
            .setPeriodRange(detailFinancialRequest.periodRange)
            .setPeriodType(detailFinancialRequest.PeriodType)
            .build()

        return oltService.getDetailBalanceSheet(financialBalanceSheetRequest)
    }

    override suspend fun getDetailCashFlow(detailFinancialRequest: DetailFinancialRequest): FinancialCashFlowResponse? {
        val financialCashFlowRequest = FinancialCashFlowRequest
            .newBuilder()
            .setUserId(detailFinancialRequest.userId)
            .setSessionId(detailFinancialRequest.sessionId)
            .setStockCode(detailFinancialRequest.stockCode)
            .setPeriodRange(detailFinancialRequest.periodRange)
            .setPeriodType(detailFinancialRequest.PeriodType)
            .build()

        return oltService.getDetailCashFlow(financialCashFlowRequest)
    }
}