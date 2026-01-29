package com.bcasekuritas.mybest.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bcasekuritas.mybest.app.base.db.DBEntity
import com.bcasekuritas.mybest.ext.converter.Converters

@Entity(tableName = DBEntity.STOCK_NOTATION_SOURCE)
@TypeConverters(Converters::class)
data class StockNotationObject(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Int = 0,

    @ColumnInfo
    val notation: String,

    @ColumnInfo
    val stockCode: String,

    @ColumnInfo
    val description: String

)