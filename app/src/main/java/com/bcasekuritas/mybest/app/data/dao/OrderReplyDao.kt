package com.bcasekuritas.mybest.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bcasekuritas.mybest.app.base.db.DBEntity
import com.bcasekuritas.mybest.app.data.entity.OrderReplyObject
@Dao
interface OrderReplyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderReply(orderReplyObject: OrderReplyObject)

    @Query("SELECT * FROM ${DBEntity.ORDER_REPLY_SOURCE} WHERE clOrderRef = :clOrderRef")
    suspend fun getOrderReply(clOrderRef: String): OrderReplyObject
}