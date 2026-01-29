package com.bcasekuritas.mybest.app.domain.dto.response.source

import java.math.BigDecimal

data class HistoryRdnRes(
    val date: String,
    val name: String,
    val status: String,
    val amount: String,
    val desc: String
)