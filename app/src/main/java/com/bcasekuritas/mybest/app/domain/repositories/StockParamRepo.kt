package com.bcasekuritas.mybest.app.domain.repositories

import com.bcasekuritas.mybest.app.data.entity.StockNotationObject
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.app.data.entity.StockWithNotationObject
import com.bcasekuritas.mybest.app.domain.dto.request.StockParamListRequest
import com.bcasekuritas.mybest.app.domain.dto.response.source.StockNotationRes
import com.bcasekuritas.mybest.app.domain.dto.response.source.StockParamRes
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.StockParamResponse
import kotlinx.coroutines.flow.Flow

interface StockParamRepo {
    suspend fun getStockParamList(stockParamListRequest: StockParamListRequest): Flow<Resource<StockParamResponse?>>

    suspend fun insertStockParamDao(stockParamRes: StockParamRes)
    suspend fun insertAllStockParamDao(stockParamObjectList: List<StockParamObject>): Flow<Resource<Boolean?>>
    suspend fun getAllStockParam(): Flow<Resource<List<StockParamObject>>>
    suspend fun searchStockParam(value: String): Flow<Resource<List<StockParamObject?>>>
    suspend fun getStockParam(stockCode: String): Flow<Resource<StockParamObject?>>
    suspend fun getListStockParam(value: List<String>): Flow<Resource<List<StockWithNotationObject?>>>

    suspend fun insertStockNotation(stockNotationRes: StockNotationRes)
    suspend fun insertAllStockNotation(stockNotationList: List<StockNotationObject>): Flow<Resource<Boolean?>>
    suspend fun getAllStockNotation(): Flow<Resource<List<StockNotationObject>>>
    suspend fun getStockNotationByStockCode(value: String): Flow<Resource<List<StockNotationObject?>>>

    suspend fun deleteStockNotationByCodes(stockCodes: List<String>): Flow<Resource<Boolean?>>
    suspend fun clearStockNotationDB()
    suspend fun deleteStockByCodes(stockCodes: List<String>): Flow<Resource<Boolean?>>
    suspend fun deleteStockParamDao()
}