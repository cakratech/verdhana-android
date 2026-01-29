package com.bcasekuritas.mybest.app.domain.dto.response.source

data class TradeTimeRes(
    var id: Int,
    var time: String,
    var buyLot: String,
    var SellLot: String,
    var buyPercent: String,
    var chart: Int,
    var sellPercent: String
)
