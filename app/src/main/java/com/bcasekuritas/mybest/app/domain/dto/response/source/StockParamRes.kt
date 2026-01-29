package com.bcasekuritas.mybest.app.domain.dto.response.source

data class StockParamRes(
    val stockCode: String? = "",
    val stockName: String? = "",
    val idxTrdBoard: String? = "",
    val stockNotasi: ByteArray? = null,
    val hairCut: Double? = 0.0
)
