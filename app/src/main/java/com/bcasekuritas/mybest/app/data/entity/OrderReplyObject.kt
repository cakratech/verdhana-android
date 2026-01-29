package com.bcasekuritas.mybest.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bcasekuritas.mybest.app.base.db.DBEntity
import com.bcasekuritas.mybest.ext.converter.Converters

@Entity(tableName = DBEntity.ORDER_REPLY_SOURCE)
@TypeConverters(Converters::class)
data class OrderReplyObject(
    @PrimaryKey
    @ColumnInfo
    var orderID: String,

    @ColumnInfo
    val clOrderRef: String? = null,

    @ColumnInfo
    var status: String? = null,

    @ColumnInfo
    var board: String? = null,

    @ColumnInfo
    var branch: String? = null,

    @ColumnInfo
    var orderTime: Long? = null,

    @ColumnInfo
    var buySell: String? = null,

    @ColumnInfo
    var stockCode: String? = null,

    @ColumnInfo
    var insvtType: String? = null,

    @ColumnInfo
    var ordQty: Double? = null,

    @ColumnInfo
    var matchQty: Double? = null,

    @ColumnInfo
    var ordPrice: Double? = null,

    @ColumnInfo
    var timeInForce: String? = null,

    @ColumnInfo
    var clientCode: String? = null,

    @ColumnInfo
    var clientSID: String? = null,

    @ColumnInfo
    var lotSize: Int? = null,

    @ColumnInfo
    var orderPeriod: Long? = null,

    @ColumnInfo
    var inputBy: String? = null,

    @ColumnInfo
    var remarks: String? = null,

    @ColumnInfo
    var rejectReason: String? = null,

    @ColumnInfo
    var exOrderId: String? = null,

    @ColumnInfo
    var accNo: String? = null,

    @ColumnInfo
    var accType: String? = null,

    @ColumnInfo
    var orderType: String? = null,

    @ColumnInfo
    var oldClOrderRef: String? = null,

    @ColumnInfo
    var oldOrderId: String? = null,

    @ColumnInfo
    var checkSameOrder: Boolean = false,

    @ColumnInfo
    var ordPeriod: Long? = null,

    @ColumnInfo
    var ordValue: Double? = null,

    @ColumnInfo
    var mValue: Double? = null,

    @ColumnInfo
    var channel: Int? = null,

    @ColumnInfo
    var accMQty: Double? = null,

    @ColumnInfo
    var isGtOrder: Boolean? = null,

    @ColumnInfo
    var advOrderId: String? = null,

    @ColumnInfo
    var isWdForToday: Boolean? = null

)