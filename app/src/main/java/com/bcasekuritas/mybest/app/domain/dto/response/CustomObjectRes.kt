package com.bcasekuritas.mybest.app.domain.dto.response

import android.os.Parcel
import android.os.Parcelable
import com.bcasekuritas.mybest.ext.date.DateUtils
import java.math.BigDecimal

data class TradeSummary(
    val boardCode: String = "",
    var secCode: String = "",
    var stockName: String = "",
    var last: Double = 0.0,
    val high: Double = 0.0,
    val low: Double = 0.0,
    var close: Double = 0.0,
    val open: Double = 0.0,
    var change: Double = 0.0,
    val bestBidQuantity: Int = 0,
    val bestBidPrice: Double = 0.0,
    val bestOfferQuantity: Int = 0,
    val bestOfferPrice: Double = 0.0,
    val sharesPerLot: Int = 0,
    val bestBidVolumeLot: Double = 0.0,
    val bestOfferVolumeLot: Double = 0.0,
    val sector: String = "",
    val tradeVolume: Double = 0.0,
    val tradeValue: Double = 0.0,
    val tradeFreq: Double = 0.0,
    val avgPrice: Double = 0.0,
    val tradeVolumeLot: Double = 0.0,
    val prevTradeVol: Double = 0.0,
    val prevTradeVolLot: Double = 0.0,
    val sectorId: Int= 0,
    var changePct: Double = 0.0,
    var type: Int = TradeSummaryItem.TYPE_CAT,
    var notation: String = "",
    var iep: Double = 0.0,
    var iev: Double = 0.0
)
interface TradeSummaryItem {
     val type: Int
     companion object {
          const val TYPE_ALL = 0
          const val TYPE_CAT = 1
          const val TYPE_PORT = 2
     }
}

data class TradeDetail(
     var rowId: Long = 0,
     val uniqueId: Long = 0,
     val boardCode: String = "",
     val secCode: String = "",
     val tradeTime: String = "",
     val price: Int? = 0,
     val closePrice: Double = 0.0,
     val change: Double = 0.0,
     val volume: Int = 0,
     val buyerCode: String = "",
     val sellerCode: String = "",
     val buyerType: String = "",
     val sellerType: String = ""
)

data class PortfolioStockDataItem (
     val stockcode: String = "",
     val reffprice: Double = 0.0,
     val avgprice: Double = 0.0,
     val value: Double = 0.0,
     val pct: Double = 0.0,
     val profitLoss: Double = 0.0,
     val qtyStock: Double = 0.0,
     val haircut: Double = 0.0,
     var notation: String = "",
     var idxBoard: String = "",
     var potentialLot: Double = 0.0,
     val totalAsset: Double = 0.0,
     val blockedLot: Double = 0.0
): Parcelable {
     constructor(parcel: Parcel) : this(
          parcel.readString()!!,
          parcel.readDouble(),
          parcel.readDouble(),
          parcel.readDouble(),
          parcel.readDouble(),
          parcel.readDouble(),
          parcel.readDouble(),
          parcel.readDouble(),
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readDouble(),
          parcel.readDouble(),
          parcel.readDouble()
     ) {
     }

     override fun writeToParcel(parcel: Parcel, flags: Int) {
          parcel.writeString(stockcode)
          parcel.writeDouble(reffprice)
          parcel.writeDouble(avgprice)
          parcel.writeDouble(value)
          parcel.writeDouble(pct)
          parcel.writeDouble(profitLoss)
          parcel.writeDouble(qtyStock)
          parcel.writeDouble(haircut)
          parcel.writeString(notation)
          parcel.writeString(idxBoard)
          parcel.writeDouble(potentialLot)
          parcel.writeDouble(totalAsset)
          parcel.writeDouble(blockedLot)
     }

     override fun describeContents(): Int {
          return 0
     }

     companion object CREATOR : Parcelable.Creator<PortfolioStockDataItem> {
          override fun createFromParcel(parcel: Parcel): PortfolioStockDataItem {
               return PortfolioStockDataItem(parcel)
          }

          override fun newArray(size: Int): Array<PortfolioStockDataItem?> {
               return arrayOfNulls(size)
          }
     }

}

data class OrderBookSummary(
     val boardCode: String = "",
     val secCode: String = "",
     val bestBidQuantity: Int = 0,
     val bestBidPrice: Double = 0.0,
     val totalBidQuantity: Int = 0,
     val bestOfferQuantity: Int = 0,
     val bestOfferPrice: Double = 0.0,
     val totalOfferQuantity: Int = 0,
     val last: Double = 0.0,
     val high: Double = 0.0,
     val low: Double = 0.0,
     val close: Double = 0.0,
     val open: Double = 0.0,
     val change: Double = 0.0,
     val changePct: Double = 0.0,
     val tradeValue: Double = 0.0,
     val tradeFreq: Double = 0.0,
     val avgPrice: Double = 0.0,
     val tradeVolumeLot: Double = 0.0,
     val theoreticalPrice: Double = 0.0,
     var buyOrderBook: BuyOrderBookRes? = null,
     var sellOrderBook: SellOrderBookRes? = null,
     val bestBidQuantityL: Int = 0,
     val totalBidQuantityL: Int = 0,
     val bestOfferQuantityL: Int = 0,
     val totalOfferQuantityL: Int = 0,
)

data class BuyOrderBookRes(
     var id: Int = 0,
     var quantity: Double = 0.0,
     var price: Double = 0.0,
     var quantityL: BigDecimal = BigDecimal(0),
     var totQuantityL: Int = 0,
     var progress: Int = 0,
     var prevPrice: Double = 0.0
)

data class TechnicalIndicator(
     var stockName: String? = "",
     var value: Double? = 0.0,
     var action: String? = ""
)

data class SellOrderBookRes(
     var id: Int = 0,
     var quantity: Double = 0.0,
     var price: Double = 0.0,
     var quantityL: BigDecimal = BigDecimal(0),
     var totQuantityL: Int = 0,
     var progress: Int = 0,
     var prevPrice: Double = 0.0
)



data class FastOrderBook(
//     var price: Double = 0.0,
//     var bid: Int = 0,
//     var qtyBid: Double = 0.0,
//     var qtyLBid: Int = 0,
//     var totalQtyLBid: Int = 0,
//     var progressBid: Int = 0,
//     var offer: Int = 0,
//     var qtyOffer: Double = 0.0,
//     var qtyLOffer: Int = 0,
//     var totalQtyLOffer: Int = 0,
//     var progressOffer: Int = 0,

     var index: Int = 0,
     var isBid: Boolean = false,
     var price: Double = 0.0,
     var closePrice: Double = 0.0,
     var quantity: Double = 0.0,
     var quantityL: Int = 0,
     var totQuantityL: Int = 0,
     var totOrdBid: Int = 0,
     var totOrdQtyBid: Long = 0L,
     var totOrdOffer: Int = 0,
     var totOrdQtyOffer: Long = 0L,
     var progress: Int = 0,
     var sameOrderList: ArrayList<Double> = arrayListOf()
)

data class IndCommCurrHelper(
     var code: String = "",
     var name: String = "",
     var value: Double = 0.0,
     var change: Double = 0.0,
     var changePct: Double = 0.0,
     var idImg: Int = 0
)

data class CurrencyGlobalData(
     var date: Long = 0L,
     var open: Double = 0.0,
     var high: Double = 0.0,
     var low: Double = 0.0,
     var value: Double = 0.0,
     var dailyReturn: Double = 0.0,
     var change: Double = 0.0,
     var macroId: Int = 0,
     var macroName: String = "",
     var basis: String = "",
     var nominalChange: Double = 0.0,
     var idImg: Int = 0
)

data class CommoditiesGlobalData(
     var date: Long = 0L,
     var open: Double = 0.0,
     var high: Double = 0.0,
     var low: Double = 0.0,
     var value: Double = 0.0,
     var dailyReturn: Double = 0.0,
     var weeklyReturn: Double = 0.0,
     var mtdReturn: Double = 0.0,
     var monthlyReturn: Double = 0.0,
     var threeMonthReturn: Double = 0.0,
     var sixMonthReturn: Double = 0.0,
     var ytdReturn: Double = 0.0,
     var annualReturn: Double = 0.0,
     var threeYearReturn: Double = 0.0,
     var fiveYearReturn: Double = 0.0,
     var tenYearReturn: Double = 0.0,
     var change: Double = 0.0,
     var macroId: Int = 0,
     var macroName: String = "",
     var yahooCode: String = "",
     var nominalChange: Double = 0.0,
     var isTrading: Boolean = false,
     var idImg: Int = 0
)

data class IndexGlobalData(
     var date: Long = 0L,
     var open: Double = 0.0,
     var high: Double = 0.0,
     var low: Double = 0.0,
     var value: Double = 0.0,
     var dailyReturn: Double = 0.0,
     var weeklyReturn: Double = 0.0,
     var mtdReturn: Double = 0.0,
     var monthlyReturn: Double = 0.0,
     var threeMonthReturn: Double = 0.0,
     var sixMonthReturn: Double = 0.0,
     var ytdReturn: Double = 0.0,
     var annualReturn: Double = 0.0,
     var threeYearReturn: Double = 0.0,
     var fiveYearReturn: Double = 0.0,
     var tenYearReturn: Double = 0.0,
     var change: Double = 0.0,
     var macroId: Int = 0,
     var macroName: String = "",
     var yahooCode: String = "",
     var nominalChange: Double = 0.0,
     var isTrading: Boolean = false,
     var idImg: Int = 0
)



data class QtyPriceItem(
     var qty: Double = 0.0,
     var price: Double = 0.0
)

data class PortfolioOrderItem(
     val orderId: String = "",
     val idxOrderId: String = "",
     val time: Double = 0.0,
     val status: String = "",
     val buySell: String = "",
     val orderType: String = "",
     val stockCode: String = "",
     val remark: String = "",
     val price: Double = 0.0,
     val orderQty: Double = 0.0,
     val matchQty: Double = 0.0,
     var isAmend: Boolean = false,
     val timeInForce: String = "",
     val ordPeriod: Long = 0L,
     val ordValue: Double = 0.0,
     val mValue: Double = 0.0,
     var notation: String = "",
     var idxBoard: String = "",
     var channel: Int = 0,
     var accMQty: Double = 0.0,
     var fee: Double = 0.0,
     var isHistory: Boolean = false,
     var isGtOrder: Boolean = false,
     val advOrderId: String = "",
     val isWdForToday: Boolean = false,
     val channelForFee: Int = 0,
     val matchPrice: Double = 0.0
) : Parcelable {
     constructor(parcel: Parcel) : this(
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readDouble(),
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readDouble(),
          parcel.readDouble(),
          parcel.readDouble(),
          parcel.readByte() != 0.toByte(),
          parcel.readString()!!,
          parcel.readLong(),
          parcel.readDouble(),
          parcel.readDouble(),
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readInt(),
          parcel.readDouble(),
          parcel.readDouble(),
          parcel.readByte() != 0.toByte(),
          parcel.readByte() != 0.toByte(),
          parcel.readString()!!,
          parcel.readByte() != 0.toByte(),
          parcel.readInt(),
          parcel.readDouble()
     ) {
     }

     override fun writeToParcel(parcel: Parcel, flags: Int) {
          parcel.writeString(orderId)
          parcel.writeString(idxOrderId)
          parcel.writeDouble(time)
          parcel.writeString(status)
          parcel.writeString(buySell)
          parcel.writeString(orderType)
          parcel.writeString(stockCode)
          parcel.writeString(remark)
          parcel.writeDouble(price)
          parcel.writeDouble(orderQty)
          parcel.writeDouble(matchQty)
          parcel.writeByte(if (isAmend) 1 else 0)
          parcel.writeString(timeInForce)
          parcel.writeLong(ordPeriod)
          parcel.writeDouble(ordValue)
          parcel.writeDouble(mValue)
          parcel.writeString(notation)
          parcel.writeString(idxBoard)
          parcel.writeInt(channel)
          parcel.writeDouble(accMQty)
          parcel.writeDouble(fee)
          parcel.writeByte(if (isHistory) 1 else 0)
          parcel.writeByte(if (isGtOrder) 1 else 0)
          parcel.writeString(advOrderId)
          parcel.writeByte(if (isWdForToday) 1 else 0)
          parcel.writeInt(channelForFee)
          parcel.writeDouble(matchPrice)
     }

     override fun describeContents(): Int {
          return 0
     }

     companion object CREATOR : Parcelable.Creator<PortfolioOrderItem> {
          override fun createFromParcel(parcel: Parcel): PortfolioOrderItem {
               return PortfolioOrderItem(parcel)
          }

          override fun newArray(size: Int): Array<PortfolioOrderItem?> {
               return arrayOfNulls(size)
          }
     }


}

data class OrderSuccessSnackBar(
     val isSuccess: Boolean = false,
     val orderType: String = "",
     val buySell: String = "",
     val stockCode: String = ""
): Parcelable {
     constructor(parcel: Parcel) : this(
          parcel.readByte() != 0.toByte(),
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!
     ) {
     }

     override fun writeToParcel(parcel: Parcel, flags: Int) {
          parcel.writeByte(if (isSuccess) 1 else 0)
          parcel.writeString(orderType)
          parcel.writeString(buySell)
          parcel.writeString(stockCode)
     }

     override fun describeContents(): Int {
          return 0
     }

     companion object CREATOR : Parcelable.Creator<OrderSuccessSnackBar> {
          override fun createFromParcel(parcel: Parcel): OrderSuccessSnackBar {
               return OrderSuccessSnackBar(parcel)
          }

          override fun newArray(size: Int): Array<OrderSuccessSnackBar?> {
               return arrayOfNulls(size)
          }
     }
}

data class OltOrder(
     var orderID: String = "",
     var clOrderRef: String = "",
     var status: String = "",
     var board: String = "",
     var branch: String = "",
     var orderTime: Long? = null,
     var buySell: String = "",
     var stockCode: String = "",
     var insvtType: String = "",
     var ordQty: Double = 0.0,
     var matchQty: Double = 0.0,
     var ordPrice: Double = 0.0,
     var timeInForce: String = "",
     var clientCode: String = "",
     var clientSID: String = "",
     var lotSize: Int? = null,
     var orderPeriod: Long? = null,
     var inputBy: String = "",
     var remarks: String = "",
     var rejectReason: String = "",
     var exOrderId: String = "",
     var sessionId: String = "",
     var ip: String = "",
     var accNo: String = "",
     var accType: String = "",
     var orderType: String = "",
     var oldClOrderRef: String = "",
     var oldOrderId: String = "",
     var checkSameOrder: Boolean = false
)

data class IndiceData(
     val indiceCode: String = "",
     val indiceVal: Double = 0.0,
     val close: Double = 0.0,
     val open: Double = 0.0,
     val high: Double = 0.0,
     val low: Double = 0.0,
     val change: Double = 0.0,
     val chgPercent: Double = 0.0,
     val marketVal: Double = 0.0,
     val marketVol: Double = 0.0,
     val marketFreq: Int? = 0,
     val totalUp: Int? = 0,
     val totalNoChange: Int? = 0,
     val totalDown: Int? = 0,
     val totalUnTrade: Int? = 0,
     val rowId: Int? = 0
)

data class IndexSectorDetailData(
     val id: Long = 0,
     val indiceCode:String= "",
     val indiceVal: Double = 0.0,
     val change: Double = 0.0,
     val chgPercent: Double = 0.0,
     val stockCount:Int = 0,
     val idImg:Int = 0,
     val indexName: String = ""
)

data class CategoriesItem(
    val stockCode: String = "",
    var stockName: String = "",
    val changePct: Double = 0.0,
    val change: Double = 0.0,
    val lastPrice: Double = 0.0,
)

data class StockCodeItem(
     val stockCode: String = "",
     val stockName: String = ""
)


data class RemoteConfigResponse(
     val version_name: String = "",
     val version_code: Long = 0
)

data class AdvancedOrderDetail(
     val orderId: String = "",
     val stockCode: String = "",
     val ordTime: Long = 0L,
     val ordStatus: String = "",
     val bracketStatus: String = "",
     val buysell: String = "",
     val ordPeriod: Long = 0L,
     val ordQty: Double = 0.0,
     val lotDone: Double = 0.0,
     val ordPrice: Double = 0.0,
     val advType: Int = 0,
     val blockQty: Int = 0,
     val splitSize: Int = 0,
     val remark: String = "",
     val stopLossOpr: Int = 0,
     val stopLossTriggerVal: Double = 0.0,
     val stopLossQty: Double = 0.0,
     val stopLossPrice:Double = 0.0,
     val takeProfitOpr: Int = 0,
     val takeProfitTriggerVal: Double = 0.0,
     val takeProfitQty: Double = 0.0,
     val takeProfitPrice:Double = 0.0,
     val opr: Int  = 0,
     val triggerPriceAutoOrder: Double = 0.0,
     val triggerCategory: Int = 0,
     val triggerCategoryStopLoss: Int = 0,
     val triggerCategoryTakeProfit: Int = 0,
     val takeProfitLotDone: Double = 0.0,
     val stopLossLotDone: Double = 0.0,
     val sltpLotDone: Double = 0.0
): Parcelable {
     constructor(parcel: Parcel) : this(
          parcel.readString()?: "",
          parcel.readString()?: "",
          parcel.readLong(),
          parcel.readString()?: "",
          parcel.readString()?: "",
          parcel.readString()?: "",
          parcel.readLong(),
          parcel.readDouble(),
          parcel.readDouble(),
          parcel.readDouble(),
          parcel.readInt(),
          parcel.readInt(),
          parcel.readInt(),
          parcel.readString()?: "",
          parcel.readInt(),
          parcel.readDouble(),
          parcel.readDouble(),
          parcel.readDouble(),
          parcel.readInt(),
          parcel.readDouble(),
          parcel.readDouble(),
          parcel.readDouble(),
          parcel.readInt(),
          parcel.readDouble(),
          parcel.readInt(),
          parcel.readInt(),
          parcel.readInt(),
          parcel.readDouble(),
          parcel.readDouble(),
          parcel.readDouble()
     ) {
     }

     override fun writeToParcel(parcel: Parcel, flags: Int) {
          parcel.writeString(orderId)
          parcel.writeString(stockCode)
          parcel.writeLong(ordTime)
          parcel.writeString(ordStatus)
          parcel.writeString(bracketStatus)
          parcel.writeString(buysell)
          parcel.writeLong(ordPeriod)
          parcel.writeDouble(ordQty)
          parcel.writeDouble(lotDone)
          parcel.writeDouble(ordPrice)
          parcel.writeInt(advType)
          parcel.writeInt(blockQty)
          parcel.writeInt(splitSize)
          parcel.writeString(remark)
          parcel.writeInt(stopLossOpr)
          parcel.writeDouble(stopLossTriggerVal)
          parcel.writeDouble(stopLossQty)
          parcel.writeDouble(stopLossPrice)
          parcel.writeInt(takeProfitOpr)
          parcel.writeDouble(takeProfitTriggerVal)
          parcel.writeDouble(takeProfitQty)
          parcel.writeDouble(takeProfitPrice)
          parcel.writeInt(opr)
          parcel.writeDouble(triggerPriceAutoOrder)
          parcel.writeInt(triggerCategory)
          parcel.writeInt(triggerCategoryStopLoss)
          parcel.writeInt(triggerCategoryTakeProfit)
          parcel.writeDouble(takeProfitLotDone)
          parcel.writeDouble(stopLossLotDone)
          parcel.writeDouble(sltpLotDone)
     }

     override fun describeContents(): Int {
          return 0
     }

     companion object CREATOR : Parcelable.Creator<AdvancedOrderDetail> {
          override fun createFromParcel(parcel: Parcel): AdvancedOrderDetail {
               return AdvancedOrderDetail(parcel)
          }

          override fun newArray(size: Int): Array<AdvancedOrderDetail?> {
               return arrayOfNulls(size)
          }
     }

}

data class FastOrderListInfo(
     val accNo: String = "",
     val stockCode: String = "",
     val buyFastOrderInfo: List<FastOrderInfo> = listOf(),
     val sellFastOrderInfo: List<FastOrderInfo> = listOf(),
     val stockInfo: FastOrderByStockInfo
)

data class FastOrderInfo(
     val price: Double = 0.0,
     val totalOrder: Int = 0,
     val totalOrdQty: Long = 0L,
     val relId: List<String> = listOf(),
     val detailQtyList: List<Double> = listOf()
)

data class FastOrderByStockInfo(
     val buyingPowerCash: Double = 0.0,
     val buyingPowerLimit: Double = 0.0,
     val maxLotCash: Double = 0.0,
     val maxLotLimit: Double = 0.0,
     val maxLotSell: Double = 0.0,
     val price: Double = 0.0,
)

data class CalendarItem(
     val caDate: Long? = 0L,
     val caType: String? = "",
     val caTypeDescr: String? = "",
     val caStock: String? = "",
     val caDateType: Int? = 0,
     val caDateTypeDescr: String? = "",
     val caId: Int? = 0,
)

data class CalendarData(
     val calItem: List<Any>,
     val calDate: Pair<Int, Int>? = null,
     val calDateData: Long? = 0L,
     val selectedMonth: Long? = 0L,
     var isSelected: Boolean? = false,
     var calType: String? = ""
)

data class RightIssueItem(
     val stockCode: String = "",
     var stockName: String = "",
     val price: Double = 0.0,
     val status: Int = 0,
     val instrumentCode: String = "",
     val instrumentType: String = "",
     val startDate: Long = 0L,
     val endDate: Long = 0L,
     val remarks: String = "",
     val stockPosQty: Double = 0.0,
     val maxQty: Double = 0.0,
     val currentPrice: Double = 0.0,
     val totalValue: Double = 0.0,
     val endTime: Long = 0L
)

data class CalWarrant(
     val stockCode: String = "",
     val excercisePrice: Double = 0.0,
     val tradingStart: Long = 0L,
     val tradingEnd: Long = 0L,
     val excerciseStart: Long = 0L,
     val excerciseEnd: Long = 0L,
     val type: String = "Warrant",
)

data class CalRightIssue(
     val stockCode: String = "",
     val oldRatio: Double = 0.0,
     val newRatio: Double = 0.0,
     val factor: Double = 0.0,
     val price: Double = 0.0,
     val cumulativeDate: Long = 0L,
     val exDate: Long = 0L,
     val recordingDate: Long = 0L,
     val tradingStart: Long = 0L,
     val tradingEnd: Long = 0L,
     val type: String = "Right Issue"
)

data class CalStockSplit(
     val stockCode: String = "",
     val oldRatio: Double = 0.0,
     val newRatio: Double = 0.0,
     val cumulativeDate: Long = 0L,
     val exDate: Long = 0L,
     val recordingDate: Long = 0L,
     val type: String = "Stock Split",
     val splitFactor: Double = 0.0
)

data class CalSahamBonus(
     val stockCode: String = "",
     val oldRatio: Double = 0.0,
     val newRatio: Double = 0.0,
     val factor: Double = 0.0,
     val cumulativeDate: Long = 0L,
     val exDate: Long = 0L,
     val recordingDate: Long = 0L,
     val payDate: Long = 0L,
     val type: String = "Bonus"
)

data class CalDividenSaham(
     val stockCode: String = "",
     val cashDividend: Double = 0.0,
     val cumulativeDate: Long = 0L,
     val exDate: Long = 0L,
     val recordingDate: Long = 0L,
     val paymentDate: Long = 0L,
     val type: String = "Dividend"
)

data class CalPubExp(
     val stockCode: String = "",
     val date: Long = 0L,
     val time: String = "",
     val location: String = "",
     val type: String = "Public Expose"
)

data class CalRups(
     val stockCode: String = "",
     val date: Long = 0L,
     val time: String = "",
     val location: String = "",
     val type: String = "RUPS"
)

data class CalIpo(
     val stockCode: String = "",
     val companyName: String = "",
     val totalShareListed: Double = 0.0,
     val listingDate: Long = 0L,
     val type: String = "E-IPO"
)

data class CalReverseStock(
     val oldRatio: Double = 0.0,
     val newRatio: Double = 0.0,
     val factor: Double = 0.0,
     val cumulativeDate: Long = 0L,
     val exDate: Long = 0L,
     val paymentDate: Long = 0L,
     val stockCode: String = "",
     val type: String = "Reverse Split"
)

data class CalEvent(
     val stockCode: String = "",
     val excercisePrice: Double = 0.0,
     val tradingStart: Long = 0L,
     val tradingEnd: Long = 0L,
     val excerciseStart: Long = 0L,
     val excerciseEnd: Long = 0L,
     val oldRatio: Double = 0.0,
     val newRatio: Double = 0.0,
     val factor: Double = 0.0,
     val price: Double = 0.0,
     val cumulativeDate: Long = 0L,
     val exDate: Long = 0L,
     val recordingDate: Long = 0L,
     val payDate: Long = 0L,
     val cashDividend: Double = 0.0,
     val paymentDate: Long = 0L,
     val date: Long = 0L,
     val time: String = "",
     val location: String = "",
     val companyName: String = "",
     val totalShareListed: Double = 0.0,
     val listingDate: Long = 0L,
     val splitDate: Long = 0L,
     val splitFactor: Double = 0.0,
     val type: String = ""
)



data class RightIssueParcelable(
     val stockCode: String = "",
     var stockName: String = "",
     val price: Double = 0.0,
     val status: Int = 0,
     val instrumentCode: String = "",
     val instrumentType: String = "",
     val startDate: Long = 0L,
     val endDate: Long = 0L,
     val remarks: String = "",
     val stockPosQty: Double = 0.0,
     val maxQty: Double = 0.0,
     val currentPrice: Double = 0.0,
     val totalValue: Double = 0.0
): Parcelable {
     constructor(parcel: Parcel) : this(
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readDouble(),
          parcel.readInt(),
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readLong(),
          parcel.readLong(),
          parcel.readString()!!,
          parcel.readDouble(),
          parcel.readDouble(),
          parcel.readDouble(),
          parcel.readDouble()
     ) {
     }

     override fun writeToParcel(parcel: Parcel, flags: Int) {
          parcel.writeString(stockCode)
          parcel.writeString(stockName)
          parcel.writeDouble(price)
          parcel.writeInt(status)
          parcel.writeString(instrumentCode)
          parcel.writeString(instrumentType)
          parcel.writeLong(startDate)
          parcel.writeLong(endDate)
          parcel.writeString(remarks)
          parcel.writeDouble(stockPosQty)
          parcel.writeDouble(maxQty)
          parcel.writeDouble(currentPrice)
          parcel.writeDouble(totalValue)
     }

     override fun describeContents(): Int {
          return 0
     }

     companion object CREATOR : Parcelable.Creator<RightIssueParcelable> {
          override fun createFromParcel(parcel: Parcel): RightIssueParcelable {
               return RightIssueParcelable(parcel)
          }

          override fun newArray(size: Int): Array<RightIssueParcelable?> {
               return arrayOfNulls(size)
          }
     }
}

data class RdnHistoryItem(
     val clCode: String= "",
     val transCode: String = "",
     val transType: String = "",
     val transAmount: Double = 0.0,
     val currencyCode: String = "",
     val bankCode: String = "",
     val bankAccountNo: String = "",
     val orderSource: String = "",
     val ordersourceRef: String = "",
     val status: String = "",
     val createdDate: Long = 0L,
     val lastModifiedDate: Long = 0L
): Parcelable {
     constructor(parcel: Parcel) : this(
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readDouble(),
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readLong(),
          parcel.readLong()
     ) {
     }

     override fun writeToParcel(parcel: Parcel, flags: Int) {
          parcel.writeString(clCode)
          parcel.writeString(transCode)
          parcel.writeString(transType)
          parcel.writeDouble(transAmount)
          parcel.writeString(currencyCode)
          parcel.writeString(bankCode)
          parcel.writeString(bankAccountNo)
          parcel.writeString(orderSource)
          parcel.writeString(ordersourceRef)
          parcel.writeString(status)
          parcel.writeLong(createdDate)
          parcel.writeLong(lastModifiedDate)
     }

     override fun describeContents(): Int {
          return 0
     }

     companion object CREATOR : Parcelable.Creator<RdnHistoryItem> {
          override fun createFromParcel(parcel: Parcel): RdnHistoryItem {
               return RdnHistoryItem(parcel)
          }

          override fun newArray(size: Int): Array<RdnHistoryItem?> {
               return arrayOfNulls(size)
          }
     }
}

data class WithdrawListItem(
     val accNo: String = "",
     val amount: Double = 0.0,
     val cifCode: String = "",
     val reference: String = "",
     val dateRequest: Long = 0L,
     val dateWithdraw: Long = 0L,
     val dateCancel: Long = 0L,
     val dateReceive: Long = 0L,
     val status: String = "",
     val refSas: String = "",
     val dateStatus: Long = 0L,
     val withdrawFrom: String = ""
): Parcelable {
     constructor(parcel: Parcel) : this(
          parcel.readString()!!,
          parcel.readDouble(),
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readLong(),
          parcel.readLong(),
          parcel.readLong(),
          parcel.readLong(),
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readLong(),
          parcel.readString()!!
     ) {
     }

     override fun writeToParcel(parcel: Parcel, flags: Int) {
          parcel.writeString(accNo)
          parcel.writeDouble(amount)
          parcel.writeString(cifCode)
          parcel.writeString(reference)
          parcel.writeLong(dateRequest)
          parcel.writeLong(dateWithdraw)
          parcel.writeLong(dateCancel)
          parcel.writeLong(dateReceive)
          parcel.writeString(status)
          parcel.writeString(refSas)
          parcel.writeLong(dateStatus)
          parcel.writeString(withdrawFrom)
     }

     override fun describeContents(): Int {
          return 0
     }

     companion object CREATOR : Parcelable.Creator<WithdrawListItem> {
          override fun createFromParcel(parcel: Parcel): WithdrawListItem {
               return WithdrawListItem(parcel)
          }

          override fun newArray(size: Int): Array<WithdrawListItem?> {
               return arrayOfNulls(size)
          }
     }
}

data class ExerciseOrderListItem(
     val transCode: String = "",
     val transDate: Long = 0L,
     val transType: String = "",
     val accno: String = "",
     val accInit: String = "",
     val accName: String = "",
     val stockCode: String = "",
     val orderPrice: Double = 0.0,
     val orderQty: Double = 0.0,
     val amount: Double = 0.0,
     val status: String = "",
     val clOrder_id: String = "",
     val sales_id: String = "",
     val aogroupid: String = "",
     val inputby: String = "",
     val input_ipaddress: String = "",
     val remarks: String = "",
     val reject_reason: String = "",
     val channel: String = "",
     val media_source: String = "",
     val flag: String = "",
     val is_read: String = "",
     val created_by: String = "",
     val created_date: String = "",
     val last_modified_by: String = "",
     val last_modified_date: Long = 0L
): Parcelable {
     constructor(parcel: Parcel) : this(
          parcel.readString()!!,
          parcel.readLong(),
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readDouble(),
          parcel.readDouble(),
          parcel.readDouble(),
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readLong()
     ) {
     }

     override fun writeToParcel(parcel: Parcel, flags: Int) {
          parcel.writeString(transCode)
          parcel.writeLong(transDate)
          parcel.writeString(transType)
          parcel.writeString(accno)
          parcel.writeString(accInit)
          parcel.writeString(accName)
          parcel.writeString(stockCode)
          parcel.writeDouble(orderPrice)
          parcel.writeDouble(orderQty)
          parcel.writeDouble(amount)
          parcel.writeString(status)
          parcel.writeString(clOrder_id)
          parcel.writeString(sales_id)
          parcel.writeString(aogroupid)
          parcel.writeString(inputby)
          parcel.writeString(input_ipaddress)
          parcel.writeString(remarks)
          parcel.writeString(reject_reason)
          parcel.writeString(channel)
          parcel.writeString(media_source)
          parcel.writeString(flag)
          parcel.writeString(is_read)
          parcel.writeString(created_by)
          parcel.writeString(created_date)
          parcel.writeString(last_modified_by)
          parcel.writeLong(last_modified_date)
     }

     override fun describeContents(): Int {
          return 0
     }

     companion object CREATOR : Parcelable.Creator<ExerciseOrderListItem> {
          override fun createFromParcel(parcel: Parcel): ExerciseOrderListItem {
               return ExerciseOrderListItem(parcel)
          }

          override fun newArray(size: Int): Array<ExerciseOrderListItem?> {
               return arrayOfNulls(size)
          }
     }
}

data class PriceAlertItem(
     val id: Long = 0L,
     val stockCode: String = "",
     var stockName: String = "",
     val operation: String = "",
     val price: Double = 0.0,
     val status: String = "",
     val triggerAt: Long = 0L
): Parcelable {
     constructor(parcel: Parcel) : this(
          parcel.readLong(),
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readDouble(),
          parcel.readString()!!,
          parcel.readLong()
     ) {
     }

     override fun writeToParcel(parcel: Parcel, flags: Int) {
          parcel.writeLong(id)
          parcel.writeString(stockCode)
          parcel.writeString(stockName)
          parcel.writeString(operation)
          parcel.writeDouble(price)
          parcel.writeString(status)
          parcel.writeLong(triggerAt)
     }

     override fun describeContents(): Int {
          return 0
     }

     companion object CREATOR : Parcelable.Creator<PriceAlertItem> {
          override fun createFromParcel(parcel: Parcel): PriceAlertItem {
               return PriceAlertItem(parcel)
          }

          override fun newArray(size: Int): Array<PriceAlertItem?> {
               return arrayOfNulls(size)
          }
     }
}

data class TradeDetailData (
     var secCode: String = "",
     var price: Double = 0.0,
     var totalFreq: Int = 0,
     var buyFreq: Int = 0,
     var sellFreq: Int = 0,
     var totalLot: Long = 0,
     var buyLot: Long = 0,
     var sellLot: Long = 0,
     var tradeTime: String = ""
)

data class BrokerSummaryByStock(
     val brokerCodeBuy: String,
     val brokerCodeSell: String,
     val buyLot: Double,
     val buyVal: Double,
     val buyAvg: Double,
     val sellLot: Double,
     val sellVal: Double,
     val sellAvg: Double
)

data class OrderBookSum(
     var totalBid: Long = 0L,
     var totalOffer: Long = 0L
)

data class SearchFaqData(
     val searchHit: Int,
     val faqDataList: List<FaqHelpData>
)

data class FaqHelpData(
     val id: Long,
     val questionName: String,
     val answer: String,
     val category: String,
     val activate: Boolean,
     val createdDate: Long,
     val lastActivateDate: Long,
     val isHelpful: Boolean,
     val searchHit: Int? = null,
)

data class DataChart(
     val price: Double,
     val date: Long,
     val isHoursFormat: Boolean
)

data class IpoData(
     val code: String,
     val companyName: String,
     val sector: String,
     val subSector: String,
     val address: String,
     val website: String,
     val businessLine: String,
     val targetLots: Double,
     val sharePercent: Double,
     val bookPriceFrom: Double,
     val bookPriceTo: Double,
     val bookPeriodStart: String,
     val bookPeriodEnd: String,
     val offeringPrice: Double,
     val offeringPeriodStart: String,
     val offeringPeriodEnd: String,
     val allotmentDate: String,
     val distDate: String,
     val listingDate: String,
     val logoLink: String,
     val statusId: String,
     val isBookBuilding: Boolean,
     val underWriters: String,
     val overview: String,
     val status: Int,
     val prospectusLink: String,
     val summaryProspectusLink: String,
     val participantAdmin: String,
     val deadlineAllocDate: Long,
     val deadlineBookDate: Long,
     val deadlineOfferDate: Long,
     val statusNew: StatusEipoList
) {
     fun statusDate(): Long {
          return when (statusNew) {
               StatusEipoList.BOOK_BUILDING -> deadlineBookDate ?: 0
               StatusEipoList.OFFERING -> deadlineOfferDate ?: 0
               StatusEipoList.ALLOTMENT -> DateUtils.getTimeInMillisFromDateStringFormat(allotmentDate, "")
               StatusEipoList.DISTRIBUTION -> DateUtils.getTimeInMillisFromDateStringFormat(distDate, "")
               StatusEipoList.IPO -> DateUtils.getTimeInMillisFromDateStringFormat(listingDate, "")
               StatusEipoList.UNKNOWN -> 0
          }
     }

}

enum class StatusEipoList(val priority: Int) {
     BOOK_BUILDING(0),
     OFFERING(1),
     ALLOTMENT(2),
     DISTRIBUTION(3),
     IPO(4),
     UNKNOWN(5);

     companion object {
          fun fromString(string: String): StatusEipoList? {
               val lowercased = string.lowercase().trim()

               return when (lowercased) {
                    "2" -> BOOK_BUILDING
                    "3" -> OFFERING
                    "4" -> ALLOTMENT
                    "distribution", "afterdistribution" -> DISTRIBUTION
                    "ipo", "afteripo" -> IPO
                    else -> null
               }
          }
     }
}

data class IpoStatusOrder(
     val isHasOrder: Boolean,
     val stages: Int,
     val status: String,
)

data class TradeListInfo(
     val orderId: String = "",
     val idxOrderId: String = "",
     val time: Double = 0.0,
     val status: String = "",
     val buySell: String = "",
     val orderType: String = "",
     val stockCode: String = "",
     val remark: String = "",
     val price: Double = 0.0,
     val orderQty: Double = 0.0,
     val matchQty: Double = 0.0,
     var isAmend: Boolean = false,
     val timeInForce: String = "",
     val ordPeriod: Long = 0L,
     val ordValue: Double = 0.0,
     val mValue: Double = 0.0,
     var notation: String = "",
     var idxBoard: String = "",
     var channel: Int = 0,
     var accMQty: Double = 0.0,
     var fee: Double = 0.0,
     var isHistory: Boolean = false,
     var isGtOrder: Boolean = false,
     val advOrderId: String = "",
     val isWdForToday: Boolean = false,
     val channelForFee: Int = 0,
     val listTradeInfo: List<TradeListInfo> = emptyList()
)

data class GroupKeyTradeInfo(
     val exOrderId: String,
     val stockCode: String,
     val price: Double,
     val bs: String // buy or sell
)

data class CoachMarkFastOrder(
     val buyValue: String,
     val sellValue: String,
     val bid: Double,
     val price: Double,
     val offer: Double
)

data class RealizedGainLossRes(
     val stockCode: String = "",
     val date: Long = 0L,
     val year: Int = 0,
     val month: Int = 0,
     val profitLoss: Double = 0.0,
     val totalCost: Double = 0.0,
     val profitLossPct: Double = 0.0,
     val isDateDivider: Boolean = false,
     var listStock: List<RealizedGainLossRes> = emptyList()
)
data class TrustedDeviceItem(
     val deviceId: String,
     val deviceName: String,
     val date: String,
     val ip: String,
     val platform: String
)

