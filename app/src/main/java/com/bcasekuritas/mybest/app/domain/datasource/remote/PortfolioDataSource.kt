package com.bcasekuritas.mybest.app.domain.datasource.remote

import com.bcasekuritas.mybest.app.domain.dto.request.AccountInfoRequest
import com.bcasekuritas.mybest.app.domain.dto.request.CashPosRequest
import com.bcasekuritas.mybest.app.domain.dto.request.PublishAccPosReq
import com.bcasekuritas.mybest.app.domain.dto.request.RealizedGainLossMonthRequest
import com.bcasekuritas.mybest.app.domain.dto.request.RealizedGainLossYearRequest
import com.bcasekuritas.mybest.app.domain.dto.request.SessionRequest
import com.bcasekuritas.mybest.app.domain.dto.request.SettlementSchedReq
import com.bcasekuritas.mybest.app.domain.dto.request.StockPosRequest
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.bcas.AccStockPosResponse
import com.bcasekuritas.rabbitmq.proto.bcas.CIFCashPosResponse
import com.bcasekuritas.rabbitmq.proto.bcas.CakraMessage
import com.bcasekuritas.rabbitmq.proto.bcas.ClientInfoResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RGainLossDtlResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RGainLossResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SettlementScheduleResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleAccountInfoByCIFResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimplePortofolioResponse
import com.google.protobuf.GeneratedMessage

interface PortfolioDataSource {

    suspend fun getAccountInfo(clientInfoRequest: AccountInfoRequest): ClientInfoResponse?
    suspend fun getCashPos(cifCashPosRequest: CashPosRequest): CIFCashPosResponse?
    suspend fun getSettlementSched(cifCashPosRequest: SettlementSchedReq): SettlementScheduleResponse?
    suspend fun getStockPos(stockPosRequest: StockPosRequest): AccStockPosResponse?
    suspend fun getSimpleAccountInfo(sessionRequest: SessionRequest): SimpleAccountInfoByCIFResponse?
    suspend fun getSimplePortfolio(sessionRequest: SessionRequest): SimplePortofolioResponse?

    /** Realized Gain Loss */
    suspend fun getRealizedGainLossByYear(realizedGainLossYearRequest: RealizedGainLossYearRequest): RGainLossResponse?
    suspend fun getRealizedGainLossByMonth(realizedGainLossMonthRequest: RealizedGainLossMonthRequest): RGainLossDtlResponse?

    /** Subscribe */
    suspend fun publishAccPos(publishAccPosReq: PublishAccPosReq?)

    suspend fun startCIFStockPos()
    suspend fun setListenerCIFStockPos(miListener: MQMessageListener<CakraMessage>?)
    suspend fun subscribeCIFStockPos(routingKey: String?)
    suspend fun unSubscribeCIFStockPos(routingKey: String)
    suspend fun stopCifStockPos()
}