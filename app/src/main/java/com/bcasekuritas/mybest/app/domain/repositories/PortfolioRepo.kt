package com.bcasekuritas.mybest.app.domain.repositories

import com.bcasekuritas.mybest.app.domain.dto.request.AccountInfoRequest
import com.bcasekuritas.mybest.app.domain.dto.request.CashPosRequest
import com.bcasekuritas.mybest.app.domain.dto.request.PublishAccPosReq
import com.bcasekuritas.mybest.app.domain.dto.request.RealizedGainLossMonthRequest
import com.bcasekuritas.mybest.app.domain.dto.request.RealizedGainLossYearRequest
import com.bcasekuritas.mybest.app.domain.dto.request.SessionRequest
import com.bcasekuritas.mybest.app.domain.dto.request.SettlementSchedReq
import com.bcasekuritas.mybest.app.domain.dto.request.StockPosRequest
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
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
import kotlinx.coroutines.flow.Flow

interface PortfolioRepo {

    suspend fun getAccountInfo(accountInfoRequest: AccountInfoRequest): Flow<Resource<ClientInfoResponse?>>

    suspend fun getCashPos(cifCashPosRequest: CashPosRequest): Flow<Resource<CIFCashPosResponse?>>
    suspend fun getSettlementSched(settlementSchedReq: SettlementSchedReq): Flow<Resource<SettlementScheduleResponse?>>

    suspend fun getStockPos(stockPosRequest: StockPosRequest): Flow<Resource<AccStockPosResponse?>>
    suspend fun getSimpleAccountInfo(sessionRequest: SessionRequest): Flow<Resource<SimpleAccountInfoByCIFResponse?>>
    suspend fun getSimplePortfolio(sessionRequest: SessionRequest): Flow<Resource<SimplePortofolioResponse?>>

    /** Realized Gain Loss */
    suspend fun getRealizedGainLossByYear(realizedGainLossYearRequest: RealizedGainLossYearRequest): Flow<Resource<RGainLossResponse?>>
    suspend fun getRealizedGainLossByMonth(realizedGainLossMonthRequest: RealizedGainLossMonthRequest): Flow<Resource<RGainLossDtlResponse?>>

    /** Subscribe */
    suspend fun publishAccPos(publishAccPosReq: PublishAccPosReq)

    suspend fun startCIFStockPos()
    suspend fun setListenerCIFStockPos(miListener: MQMessageListener<CakraMessage>?)
    suspend fun subscribeCIFStockPos(routingKey: String?)
    suspend fun unSubscribeCIFStockPos(routingKey: String)
    suspend fun stopCifStockPos()



}