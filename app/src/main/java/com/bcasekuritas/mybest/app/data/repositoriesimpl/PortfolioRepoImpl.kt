package com.bcasekuritas.mybest.app.data.repositoriesimpl

import com.bcasekuritas.mybest.app.domain.datasource.remote.PortfolioDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.AccountInfoRequest
import com.bcasekuritas.mybest.app.domain.dto.request.CashPosRequest
import com.bcasekuritas.mybest.app.domain.dto.request.PublishAccPosReq
import com.bcasekuritas.mybest.app.domain.dto.request.RealizedGainLossMonthRequest
import com.bcasekuritas.mybest.app.domain.dto.request.RealizedGainLossYearRequest
import com.bcasekuritas.mybest.app.domain.dto.request.SessionRequest
import com.bcasekuritas.mybest.app.domain.dto.request.SettlementSchedReq
import com.bcasekuritas.mybest.app.domain.dto.request.StockPosRequest
import com.bcasekuritas.mybest.app.domain.repositories.PortfolioRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
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
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PortfolioRepoImpl @Inject constructor(
    private val remoteSource: PortfolioDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) : PortfolioRepo {

    override suspend fun getAccountInfo(accountInfoRequest: AccountInfoRequest): Flow<Resource<ClientInfoResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getAccountInfo(accountInfoRequest), DataSource.REMOTE))
    }

    override suspend fun getCashPos(cifCashPosRequest: CashPosRequest): Flow<Resource<CIFCashPosResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getCashPos(cifCashPosRequest),DataSource.REMOTE))
    }

    override suspend fun getSettlementSched(settlementSchedReq: SettlementSchedReq): Flow<Resource<SettlementScheduleResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getSettlementSched(settlementSchedReq),DataSource.REMOTE))
    }

    override suspend fun getStockPos(stockPosRequest: StockPosRequest): Flow<Resource<AccStockPosResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getStockPos(stockPosRequest), DataSource.REMOTE))
    }

    override suspend fun getSimpleAccountInfo(sessionRequest: SessionRequest): Flow<Resource<SimpleAccountInfoByCIFResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getSimpleAccountInfo(sessionRequest), DataSource.REMOTE))
    }

    override suspend fun getSimplePortfolio(sessionRequest: SessionRequest): Flow<Resource<SimplePortofolioResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getSimplePortfolio(sessionRequest), DataSource.REMOTE))
    }

    override suspend fun getRealizedGainLossByYear(realizedGainLossYearRequest: RealizedGainLossYearRequest): Flow<Resource<RGainLossResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getRealizedGainLossByYear(realizedGainLossYearRequest), DataSource.REMOTE))
    }

    override suspend fun getRealizedGainLossByMonth(realizedGainLossMonthRequest: RealizedGainLossMonthRequest): Flow<Resource<RGainLossDtlResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getRealizedGainLossByMonth(realizedGainLossMonthRequest), DataSource.REMOTE))
    }

    override suspend fun publishAccPos(publishAccPosReq: PublishAccPosReq) {
        remoteSource.publishAccPos(publishAccPosReq)
    }

    override suspend fun startCIFStockPos() {
        remoteSource.startCIFStockPos()
    }

    override suspend fun setListenerCIFStockPos(miListener: MQMessageListener<CakraMessage>?) {
        remoteSource.setListenerCIFStockPos(miListener)
    }

    override suspend fun subscribeCIFStockPos(routingKey: String?) {
        remoteSource.subscribeCIFStockPos(routingKey)
    }

    override suspend fun unSubscribeCIFStockPos(routingKey: String) {
        remoteSource.unSubscribeCIFStockPos(routingKey)
    }

    override suspend fun stopCifStockPos() {
        remoteSource.stopCifStockPos()
    }
}