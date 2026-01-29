package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.data.dao.StockNotationDao
import com.bcasekuritas.mybest.app.data.dao.StockParamDao
import com.bcasekuritas.mybest.app.data.entity.StockNotationObject
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.app.data.entity.StockWithNotationObject
import com.bcasekuritas.mybest.app.domain.datasource.remote.StockParamDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.StockParamListRequest
import com.bcasekuritas.mybest.app.domain.dto.response.source.StockNotationRes
import com.bcasekuritas.mybest.app.domain.dto.response.source.StockParamRes
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.proto.bcas.StockParamRequest
import com.bcasekuritas.rabbitmq.proto.bcas.StockParamResponse
import javax.inject.Inject

class StockParamDataSourceImpl @Inject constructor(
    private val oltService: OLTService,
    private val localSourceData: StockParamDao,
    private val localStockNotation: StockNotationDao,
) : StockParamDataSource {

    override suspend fun getStockParamList(stockParamListRequest: StockParamListRequest): StockParamResponse? {
        val stockParamListReq = StockParamRequest
            .newBuilder()
            .setUserId(stockParamListRequest.userId)
            .setSessionId(stockParamListRequest.sessionId)
            .setBoardcode(stockParamListRequest.boardcode)
            .setMktId(stockParamListRequest.mktId)
            .setLatestRetrieveStockParam(stockParamListRequest.latestRetrieveStockParam)
            .build()

        return oltService.getStockParamList(stockParamListReq)
    }

    override suspend fun insertStockParam(stockParamRes: StockParamRes) {
        stockParamRes.let {
            localSourceData.insertStockParam(
                StockParamObject(
                    stockCode = it.stockCode ?: "",
                    stockName = it.stockName ?: "",
                    idxTrdBoard = it.idxTrdBoard ?: "",
                    stockNotasi = it.stockNotasi ?: ByteArray(0),
                    hairCut = 100.0.minus(it.hairCut ?: 0.0)
                )
            )
        }
    }

    override suspend fun insertAllStockParam(stockParamObjectList: List<StockParamObject>): Boolean {
        localSourceData.insertAllStockParam(stockParamObjectList)
        return true
    }

    override suspend fun deleteStockByCodes(stockCodes: List<String>): Boolean {
        localSourceData.deleteStockByCodes(stockCodes)
        return true
    }

    override suspend fun deleteStockParamDao() {
        localSourceData.deleteStockParam()
    }

    override suspend fun getAllStockParam(): List<StockParamObject> {
        return localSourceData.getAllStockParam()
    }

    override suspend fun searchStockParam(value: String): List<StockParamObject>? {
        return localSourceData.searchStockParam(value)
    }

    override suspend fun getStockParam(stockCode: String): StockParamObject? {
        return localSourceData.getStockParam(stockCode)
    }

    override suspend fun getListStockParam(value: List<String>): List<StockWithNotationObject>? {
        return value.chunked(999).flatMap { localSourceData.getListStockWithNotationParams(it) ?: listOf() }
    }

    override suspend fun insertStockNotation(stockNotationRes: StockNotationRes) {
        stockNotationRes.let {
            localStockNotation.insertStockNotation(
                StockNotationObject(
                    stockCode = it.stockCode ?: "",
                    notation = it.notation ?: "",
                    description = it.description ?: ""
                )
            )
        }
    }

    override suspend fun insertAllStockNotation(stockNotationList: List<StockNotationObject>): Boolean {
        localStockNotation.insertAllStockNotation(stockNotationList)
        return true
    }

    override suspend fun getAllStockNotation(): List<StockNotationObject> {
        return localStockNotation.getAllStockNotation()
    }

    override suspend fun getNotationByStockCode(stockCode: String): List<StockNotationObject> {
        return localStockNotation.getStockNotation(stockCode)
    }

    override suspend fun deleteStockNotationByCodes(stockCodes: List<String>): Boolean {
        localStockNotation.deleteStockNotationByCodes(stockCodes)
        return true
    }

    override suspend fun clearStockNotationDB() {
        localStockNotation.clearStockNotationDB()
    }
}