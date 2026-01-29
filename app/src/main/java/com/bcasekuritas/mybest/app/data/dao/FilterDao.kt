package com.bcasekuritas.mybest.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bcasekuritas.mybest.app.data.entity.FilterRunningTradeObject
import com.bcasekuritas.mybest.app.data.entity.SessionObject

@Dao
interface FilterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilter(filterRunningTradeObject: FilterRunningTradeObject)

    @Query("SELECT * FROM FILTER WHERE userId = :userId")
    fun getDefaultFilter(userId: String): FilterRunningTradeObject

    @Query("DELETE FROM FILTER WHERE userId = :userId")
    fun deleteFilter(userId: String)
}