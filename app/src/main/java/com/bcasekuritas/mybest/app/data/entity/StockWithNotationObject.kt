package com.bcasekuritas.mybest.app.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class StockWithNotationObject(
    @Embedded val stockParam: StockParamObject,
    @Relation(
        parentColumn = "stockCode",
        entityColumn = "stockCode"
    )
    val stockNotation: List<StockNotationObject>
)
