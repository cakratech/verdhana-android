package com.bcasekuritas.mybest.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.bcasekuritas.mybest.app.base.db.DBEntity
import com.bcasekuritas.mybest.app.data.entity.StockNotationObject

@Dao
interface StockNotationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStockNotation(stockNotationObject: StockNotationObject)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllStockNotation(stockNotationObjectList: List<StockNotationObject>)

    @Transaction
    @Query("SELECT * FROM ${DBEntity.STOCK_NOTATION_SOURCE}")
    suspend fun getAllStockNotation(): List<StockNotationObject>

    @Transaction
    @Query("SELECT DISTINCT * FROM ${DBEntity.STOCK_NOTATION_SOURCE} WHERE stockCode = :stockCode")
    suspend fun getStockNotation(stockCode: String): List<StockNotationObject>

    @Transaction
    @Query("DELETE FROM ${DBEntity.STOCK_NOTATION_SOURCE} WHERE stockCode IN (:stockCodes)")
    suspend fun deleteStockNotationByCodes(stockCodes: List<String>)

    @Transaction
    @Query("DELETE FROM ${DBEntity.STOCK_NOTATION_SOURCE}")
    suspend fun clearStockNotationDB()
}