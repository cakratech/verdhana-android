package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.domain.datasource.remote.PortfolioDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.AccountInfoRequest
import com.bcasekuritas.mybest.app.domain.dto.request.CashPosRequest
import com.bcasekuritas.mybest.app.domain.dto.request.PublishAccPosReq
import com.bcasekuritas.mybest.app.domain.dto.request.RealizedGainLossMonthRequest
import com.bcasekuritas.mybest.app.domain.dto.request.RealizedGainLossYearRequest
import com.bcasekuritas.mybest.app.domain.dto.request.SessionRequest
import com.bcasekuritas.mybest.app.domain.dto.request.SettlementSchedReq
import com.bcasekuritas.mybest.app.domain.dto.request.StockPosRequest
import com.bcasekuritas.mybest.ext.property.orOne
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.proto.bcas.AccStockPosRequest
import com.bcasekuritas.rabbitmq.proto.bcas.AccStockPosResponse
import com.bcasekuritas.rabbitmq.proto.bcas.CIFCashPosRequest
import com.bcasekuritas.rabbitmq.proto.bcas.CIFCashPosResponse
import com.bcasekuritas.rabbitmq.proto.bcas.CakraMessage
import com.bcasekuritas.rabbitmq.proto.bcas.ClientInfoRequest
import com.bcasekuritas.rabbitmq.proto.bcas.ClientInfoResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RGainLossDtlRequest
import com.bcasekuritas.rabbitmq.proto.bcas.RGainLossDtlResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RGainLossRequest
import com.bcasekuritas.rabbitmq.proto.bcas.RGainLossResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SettlementScheduleRequest
import com.bcasekuritas.rabbitmq.proto.bcas.SettlementScheduleResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleAccountInfoByCIFRequest
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleAccountInfoByCIFResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimplePortofolioRequest
import com.bcasekuritas.rabbitmq.proto.bcas.SimplePortofolioResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SubsAccPos
import com.google.protobuf.GeneratedMessage
import javax.inject.Inject

class PortfolioDataSourceImpl @Inject constructor(
    private val oltService: OLTService
) : PortfolioDataSource {
    override suspend fun getAccountInfo(accountInfoRequest: AccountInfoRequest): ClientInfoResponse? {
        val accountInfoReq = ClientInfoRequest
            .newBuilder()
            .setUserId(accountInfoRequest.userId)
            .setCifCode(accountInfoRequest.cifCode)
            .setLoginType(accountInfoRequest.loginType)
            .setSessionId(accountInfoRequest.sessionId)
            .build()

        return oltService.getAccountInfo(accountInfoReq)
    }

    override suspend fun getCashPos(cifCashPosRequest: CashPosRequest): CIFCashPosResponse? {
        val cifCashPosReq = CIFCashPosRequest
            .newBuilder()
            .setUserId(cifCashPosRequest.userId)
            .setCifCode(cifCashPosRequest.cifCode)
            .setLoginType(cifCashPosRequest.loginType)
            .setSessionId(cifCashPosRequest.sessionId)
            .build()

        return oltService.getCIFPos(cifCashPosReq)
    }

    override suspend fun getSettlementSched(cifCashPosRequest: SettlementSchedReq): SettlementScheduleResponse? {
        val settlementSchedReq = SettlementScheduleRequest
            .newBuilder()
            .setUserId(cifCashPosRequest.userId)
            .setAccNo(cifCashPosRequest.accNo)
            .setSessionId(cifCashPosRequest.sessionId)
            .build()

        return oltService.getSettlementSchedule(settlementSchedReq)
    }

    override suspend fun getStockPos(stockPosRequest: StockPosRequest): AccStockPosResponse? {
        val accStockPosReq = AccStockPosRequest
            .newBuilder()
            .setUserId(stockPosRequest.userId)
            .setAccno(stockPosRequest.accno)
            .setSessionId(stockPosRequest.sessionId)
            .setStockcode(stockPosRequest.stockCode)
            .build()

        return  oltService.getStockPosition(accStockPosReq)
    }

    override suspend fun getSimpleAccountInfo(sessionRequest: SessionRequest): SimpleAccountInfoByCIFResponse? {
        val request = SimpleAccountInfoByCIFRequest
            .newBuilder()
            .setUserId(sessionRequest.userId)
            .setSessionId(sessionRequest.sessionId)
            .setCifCode(sessionRequest.cifCode)
            .build()

        return  oltService.getSimpleAccountInfo(request)
    }

    override suspend fun getSimplePortfolio(sessionRequest: SessionRequest): SimplePortofolioResponse? {
        val request = SimplePortofolioRequest
            .newBuilder()
            .setUserId(sessionRequest.userId)
            .setSessionId(sessionRequest.sessionId)
            .setAccNo(sessionRequest.accNo)
            .build()

        return  oltService.getSimplePortfolio(request)
    }

    override suspend fun publishAccPos(publishAccPosReq: PublishAccPosReq?) {
        val subsAccPos = SubsAccPos
            .newBuilder()
            .setUserId(publishAccPosReq?.userId)
            .setSubsOp(publishAccPosReq?.subsOp.orOne())
            .setAccNo(publishAccPosReq?.accNo)
            .build()


        val cakraMessage = CakraMessage
            .newBuilder()
            .setType(CakraMessage.Type.SIMPLE_PORTOFOLIO_RESPONSE)
            .setSubsAccPos(subsAccPos)
            .setSessionId(publishAccPosReq?.sessionId)
            .build()

        return oltService.publishSubsInfo(cakraMessage)
    }

    override suspend fun getRealizedGainLossByYear(realizedGainLossYearRequest: RealizedGainLossYearRequest): RGainLossResponse? {
        val request = RGainLossRequest.newBuilder()
            .setUserId(realizedGainLossYearRequest.userId)
            .setAccNo(realizedGainLossYearRequest.accNo)
            .setSessionId(realizedGainLossYearRequest.sessionId)
            .setYear(realizedGainLossYearRequest.year)
            .setStockCode(realizedGainLossYearRequest.stockCode)
            .build()

        return oltService.getRealizedGainLossByYear(request)
    }

    override suspend fun getRealizedGainLossByMonth(realizedGainLossMonthRequest: RealizedGainLossMonthRequest): RGainLossDtlResponse? {
        val request = RGainLossDtlRequest.newBuilder()
            .setUserId(realizedGainLossMonthRequest.userId)
            .setAccNo(realizedGainLossMonthRequest.accNo)
            .setSessionId(realizedGainLossMonthRequest.sessionId)
            .setYear(realizedGainLossMonthRequest.year)
            .setMonth(realizedGainLossMonthRequest.month)
            .setStockCode(realizedGainLossMonthRequest.stockCode)
            .build()

        return oltService.getRealizedGainLossByMonth(request)
    }

    override suspend fun startCIFStockPos() {
        oltService.startCIFStockPos()
    }

    override suspend fun setListenerCIFStockPos(miListener: MQMessageListener<CakraMessage>?) {
        oltService.setListenerCIFStockPos(miListener)
    }

    override suspend fun subscribeCIFStockPos(routingKey: String?) {
        oltService.subscribeCIFStockPos(routingKey)
    }

    override suspend fun unSubscribeCIFStockPos(routingKey: String) {
       oltService.unSubscribeCIFStockPos(routingKey)
    }

    override suspend fun stopCifStockPos() {
        oltService.stopCifStockPos()
    }
}