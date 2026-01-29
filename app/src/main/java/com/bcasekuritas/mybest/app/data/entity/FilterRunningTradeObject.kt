package com.bcasekuritas.mybest.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bcasekuritas.mybest.app.base.db.DBEntity
import com.bcasekuritas.mybest.app.data.enumerator.Category
import com.bcasekuritas.mybest.ext.converter.Converters

@Entity(tableName = DBEntity.FILTER_SOURCE)
@TypeConverters(Converters::class)
data class FilterRunningTradeObject(
    @PrimaryKey
    @ColumnInfo
    val userId: String,

    @ColumnInfo
    val indexSectorId: Long,

    @ColumnInfo
    val category: Int,

    @ColumnInfo
    val minPrice: Double,

    @ColumnInfo
    val maxPrice: Double,

    @ColumnInfo
    val minChange: Double,

    @ColumnInfo
    val maxChange: Double,

    @ColumnInfo
    val minVolume: Double,

    @ColumnInfo
    val maxVolume: Double,

    @ColumnInfo
    val stockCodes: List<String> = emptyList()

)