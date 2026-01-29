package com.bcasekuritas.mybest.app.data.repositoriesimpl

import com.bcasekuritas.mybest.app.domain.datasource.remote.StockDetailDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.EarningsPerShareReq
import com.bcasekuritas.mybest.app.domain.dto.request.KeyStatRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockInfoDetailRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockOrderbookRequest
import com.bcasekuritas.mybest.app.domain.dto.request.TradeBookRequest
import com.bcasekuritas.mybest.app.domain.repositories.StockDetailRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.datafeed.CurrentMessageResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import com.bcasekuritas.rabbitmq.proto.datafeed.OrderbookSummary
import com.bcasekuritas.rabbitmq.proto.datafeed.TradeBookResponse
import com.bcasekuritas.rabbitmq.proto.news.EarningsPerShareResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewKeyStatResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewKeyStatsRTIResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewStockInfoDetilResponse
import com.google.protobuf.GeneratedMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StockDetailRepoImpl @Inject constructor(
    private val stockDetailDataSource: StockDetailDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) : StockDetailRepo{
    override suspend fun getStockOrderbook(stockOrderbookRequest: StockOrderbookRequest?): Flow<Resource<CurrentMessageResponse?>> = flow{
        emit(Resource.Success(data = stockDetailDataSource.getCurrentMessage(stockOrderbookRequest), DataSource.REMOTE))
    }

    override suspend fun startOrderBook() {
        stockDetailDataSource.startOrderBook()
    }

    override suspend fun setListenerOrderBook(miListener: MQMessageListener<MIMessage>?) {
        stockDetailDataSource.setListenerOrderBook(miListener)
    }

    override suspend fun subscribeOrderBook(routingKey: String?) {
        stockDetailDataSource.subscribeOrderBook(routingKey)
    }

    override suspend fun unsubscribeOrderBook(routingKey: String?) {
        stockDetailDataSource.unsubscribeOrderBook(routingKey)
    }

    override suspend fun stopOrderBook() {
        stockDetailDataSource.stopOrderBook()
    }

    override suspend fun getKeyStat(keyStatRequest: KeyStatRequest): Flow<Resource<ViewKeyStatResponse?>> = flow {
        emit(Resource.Success(data = stockDetailDataSource.getKeyStat(keyStatRequest), DataSource.REMOTE))
    }

    override suspend fun getKeyStatsRti(keyStatRequest: KeyStatRequest): Flow<Resource<ViewKeyStatsRTIResponse?>> = flow {
        emit(Resource.Success(data = stockDetailDataSource.getKeyStatRti(keyStatRequest), DataSource.REMOTE))
    }

    override suspend fun getEarningPerShare(earningsPerShareReq: EarningsPerShareReq): Flow<Resource<EarningsPerShareResponse?>> = flow {
        emit(Resource.Success(data = stockDetailDataSource.getEarningPerShares(earningsPerShareReq), DataSource.REMOTE))
    }

    override suspend fun getStockInfoDetail(stockInfoDetilRequest: StockInfoDetailRequest): Flow<Resource<ViewStockInfoDetilResponse?>> = flow {
        emit(Resource.Success(data = stockDetailDataSource.getStockInfoDetail(stockInfoDetilRequest), DataSource.REMOTE))
    }
}