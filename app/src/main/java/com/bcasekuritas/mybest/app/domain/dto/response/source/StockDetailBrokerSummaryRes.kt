package com.bcasekuritas.mybest.app.domain.dto.response.source

data class StockDetailBrokerSummaryRes(
    var id: Int,
    var buy: String,
    var buyLot: String,
    var buyVal: String,
    var buyAvg: String,
    var tag: String,
    var sell: String,
    var sellLot: String,
    var sellVal: String,
    var sellAvg: String
)
