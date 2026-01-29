package com.bcasekuritas.mybest.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.bcasekuritas.mybest.app.base.db.DBEntity
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.app.data.entity.StockWithNotationObject

@Dao
interface StockParamDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockParam(stockParamObject: StockParamObject)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStockParam(stockParamObject: List<StockParamObject>)

    @Transaction
    @Query("SELECT * FROM ${DBEntity.STOCK_PARAM_SOURCE}")
    suspend fun getAllStockParam(): List<StockParamObject>

    @Transaction
    @Query("SELECT * FROM ${DBEntity.STOCK_PARAM_SOURCE} WHERE stockCode = :stockCode")
    suspend fun getStockParam(stockCode: String): StockParamObject?

    @Transaction
    @Query("SELECT * FROM ${DBEntity.STOCK_PARAM_SOURCE} WHERE stockCode IN (:stockCodes)")
    suspend fun getListStockParams(stockCodes: List<String>): List<StockParamObject>?

    @Transaction
    @Query("SELECT * FROM ${DBEntity.STOCK_PARAM_SOURCE} WHERE stockCode LIKE '%' || :value || '%' OR stockName LIKE '%' || :value || '%'")
    suspend fun searchStockParam(value: String): List<StockParamObject>?

    @Transaction
    @Query("SELECT * FROM ${DBEntity.STOCK_PARAM_SOURCE} WHERE stockCode IN (:stockCodes)")
    suspend fun getListStockWithNotationParams(stockCodes: List<String>): List<StockWithNotationObject>?

    @Query("DELETE FROM ${DBEntity.STOCK_PARAM_SOURCE} WHERE stockCode IN (:stockCodes)")
    suspend fun deleteStockByCodes(stockCodes: List<String>)

    @Query("DELETE FROM STOCK_PARAM")
    fun deleteStockParam()
}