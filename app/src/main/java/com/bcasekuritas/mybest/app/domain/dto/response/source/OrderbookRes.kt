package com.bcasekuritas.mybest.app.domain.dto.response.source

import java.math.BigDecimal

data class OrderbookRes (
    var id: Int,
    var lotBid: String,
    var bid: String,
    var lotAsk: String,
    var ask: String,
    var progressBid: Int,
    var progressAsk: Int
)