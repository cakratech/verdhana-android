package com.bcasekuritas.mybest.app.domain.dto.response.source


class TradeDetailDataRes : Comparable<TradeDetailDataRes> {
    var secCode: String = ""
    var price: Double = 0.0
    var totalFreq: Int = 0
    var buyFreq: Int = 0
    var sellFreq: Int = 0
    var totalLot: Long = 0
    var buyLot: Long = 0
    var sellLot: Long = 0
    var tradeTime: String = ""

    override fun compareTo(other: TradeDetailDataRes): Int {
       return if (price == null || other.price == null) {
            0
        } else price.compareTo(other.price)
    }

    override fun toString(): String {
        return "TradeDetailDataRes(secCode='$secCode', price=$price, totalFreq=$totalFreq, buyFreq=$buyFreq, sellFreq=$sellFreq, totalLot=$totalLot, buyLot=$buyLot, sellLot=$sellLot, tradeTime='$tradeTime')"
    }


}
