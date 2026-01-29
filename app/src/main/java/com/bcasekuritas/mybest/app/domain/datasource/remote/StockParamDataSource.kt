package com.bcasekuritas.mybest.app.domain.datasource.remote

import com.bcasekuritas.mybest.app.data.entity.StockNotationObject
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.app.data.entity.StockWithNotationObject
import com.bcasekuritas.mybest.app.domain.dto.request.StockParamListRequest
import com.bcasekuritas.mybest.app.domain.dto.response.source.StockNotationRes
import com.bcasekuritas.mybest.app.domain.dto.response.source.StockParamRes
import com.bcasekuritas.rabbitmq.proto.bcas.StockParamResponse

interface StockParamDataSource {
    suspend fun getStockParamList(stockParamListRequest: StockParamListRequest): StockParamResponse?

    suspend fun insertStockParam(stockParamRes: StockParamRes)
    suspend fun insertAllStockParam(stockParamObjectList: List<StockParamObject>): Boolean?

    suspend fun getAllStockParam(): List<StockParamObject>
    suspend fun searchStockParam(value: String): List<StockParamObject>?
    suspend fun getStockParam(value: String): StockParamObject?
    suspend fun getListStockParam(value: List<String>): List<StockWithNotationObject>?

    suspend fun insertStockNotation(stockNotationRes: StockNotationRes)
    suspend fun insertAllStockNotation(stockNotationList: List<StockNotationObject>): Boolean?
    suspend fun getAllStockNotation(): List<StockNotationObject>
    suspend fun getNotationByStockCode(stockCode: String): List<StockNotationObject>?

    suspend fun deleteStockByCodes(stockCodes: List<String>): Boolean?
    suspend fun deleteStockParamDao()
    suspend fun deleteStockNotationByCodes(stockCodes: List<String>): Boolean?
    suspend fun clearStockNotationDB()
}