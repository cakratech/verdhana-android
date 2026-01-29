package com.bcasekuritas.mybest.app.data.repositoriesimpl

import com.bcasekuritas.mybest.app.data.entity.StockNotationObject
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.app.data.entity.StockWithNotationObject
import com.bcasekuritas.mybest.app.domain.datasource.remote.StockParamDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.StockParamListRequest
import com.bcasekuritas.mybest.app.domain.dto.response.source.StockNotationRes
import com.bcasekuritas.mybest.app.domain.dto.response.source.StockParamRes
import com.bcasekuritas.mybest.app.domain.repositories.StockParamRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
import com.bcasekuritas.rabbitmq.proto.bcas.StockParamResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
class StockParamRepoImpl @Inject constructor(
    private val remoteSource: StockParamDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) : StockParamRepo {
    override suspend fun getStockParamList(stockParamListRequest: StockParamListRequest): Flow<Resource<StockParamResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getStockParamList(stockParamListRequest), DataSource.REMOTE))
    }

    override suspend fun insertStockParamDao(stockParamRes: StockParamRes) {
        remoteSource.insertStockParam(stockParamRes)
    }

    override suspend fun insertAllStockParamDao(stockParamObjectList: List<StockParamObject>): Flow<Resource<Boolean?>> = flow {
        emit(Resource.Success(data = remoteSource.insertAllStockParam(stockParamObjectList), DataSource.REMOTE))
    }

    override suspend fun deleteStockByCodes(stockCodes: List<String>): Flow<Resource<Boolean?>> = flow {
        emit(Resource.Success(data = remoteSource.deleteStockByCodes(stockCodes), DataSource.REMOTE))
    }

    override suspend fun deleteStockParamDao() {
        remoteSource.deleteStockParamDao()
    }

    override suspend fun getAllStockParam(): Flow<Resource<List<StockParamObject>>> = flow {
        emit(Resource.Success(data = remoteSource.getAllStockParam(), DataSource.REMOTE))
    }

    override suspend fun searchStockParam(value: String): Flow<Resource<List<StockParamObject?>>> = flow {
        emit(Resource.Success(data = remoteSource.searchStockParam(value), DataSource.REMOTE))
    }

    override suspend fun getStockParam(stockCode: String): Flow<Resource<StockParamObject?>> = flow {
        emit(Resource.Success(data = remoteSource.getStockParam(stockCode), DataSource.REMOTE))
    }

    override suspend fun getListStockParam(value: List<String>): Flow<Resource<List<StockWithNotationObject?>>> = flow {
        emit(Resource.Success(data = remoteSource.getListStockParam(value), DataSource.REMOTE))
    }

    override suspend fun insertStockNotation(stockNotationRes: StockNotationRes) {
        remoteSource.insertStockNotation(stockNotationRes)
    }

    override suspend fun insertAllStockNotation(stockNotationList: List<StockNotationObject>): Flow<Resource<Boolean?>> = flow {
        emit(Resource.Success(data = remoteSource.insertAllStockNotation(stockNotationList), DataSource.REMOTE))
    }

    override suspend fun getAllStockNotation(): Flow<Resource<List<StockNotationObject>>> = flow {
        emit(Resource.Success(data = remoteSource.getAllStockNotation(), DataSource.REMOTE))
    }

    override suspend fun getStockNotationByStockCode(value: String): Flow<Resource<List<StockNotationObject?>>> = flow {
        emit(Resource.Success(data = remoteSource.getNotationByStockCode(value), DataSource.REMOTE))
    }

    override suspend fun deleteStockNotationByCodes(stockCodes: List<String>): Flow<Resource<Boolean?>> = flow {
        emit(Resource.Success(data = remoteSource.deleteStockNotationByCodes(stockCodes), DataSource.REMOTE))
    }

    override suspend fun clearStockNotationDB() {
        remoteSource.clearStockNotationDB()
    }
}