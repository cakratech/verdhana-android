package com.bcasekuritas.mybest.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bcasekuritas.mybest.app.base.db.DBEntity
import com.bcasekuritas.mybest.ext.converter.Converters

@Entity(tableName = DBEntity.STOCK_PARAM_SOURCE)
@TypeConverters(Converters::class)
data class StockParamObject(
    @PrimaryKey
    @ColumnInfo
    val stockCode: String,

    @ColumnInfo
    val stockName: String,

    @ColumnInfo
    val idxTrdBoard: String,

    @ColumnInfo
    val stockNotasi: ByteArray,

    @ColumnInfo
    var isChecked: Boolean? = false,

    @ColumnInfo
    var hairCut: Double? = 0.0

)