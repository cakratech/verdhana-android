package com.bcasekuritas.mybest.app.domain.dto.response.source

data class StockDetRunningTradeRes(
    val id: Int,
    val time: String,
    val price: String,
    val lot: String,
    val buyer: String,
    val seller: String
)
