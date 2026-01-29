package com.bcasekuritas.mybest.app.domain.dto.request

import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.rabbitmq.proto.bcas.TriggerOrder

class SourceParam {
}

data class LogonRequest(
    val userId: String,
    val passowrd: String,
    val type: Int,
    val deviceType: String,
    val ip: String,
    val deviceId: String,
    val deviceModel: String,
    val deviceManufacture: String,
    val useOtp: Boolean,
    val appVersion: String
)

data class LogoutReq(
    val userId: String,
    val sessionId: String
)

data class AccountInfoRequest(
    val userId: String,
    val cifCode: String,
    val loginType: Int,
    val sessionId: String
)

data class CashPosRequest(
    val userId: String,
    val cifCode: String,
    val loginType: Int,
    val sessionId: String
)

data class SettlementSchedReq(
    val userId: String,
    val accNo: String,
    val sessionId: String
)

data class IndexSectorRequest(
    val userId: String,
    val sessionId: String,
    val type: Int // 0: all, 1: index, 2:sector
)

data class StockIndexSectorRequest(
    val userId: String,
    val sessionId: String,
    val indexSectorId: Long,
    val page: Int,
    val size: Int,
    val isPaging: Boolean
)

data class IndexSectorDataRequest(
    val userId: String,
    val sessionId: String,
    val board: String,
    val listItemCode: List<String>// 0: all, 1: index, 2:sector
)

data class StockWatchListRequest(
    val userId: String,
    val sessionId: String,
    val board: String,
    val stockCodeList: List<String>
)

data class LatestTradeDetailRequest(
    val userId: String,
    val sessionId: String
)

data class TradeBookRequest(
    val userId: String,
    val sessionId: String,
    val secCode: String
)

data class StockPosRequest(
    val userId: String,
    val accno: String,
    val sessionId: String,
    val stockCode: String
)

data class StockOrderbookRequest(
    val userId: String,
    val sessionId: String,
    val boardCode: String,
    val stockCodeList: List<String>
)

data class StockCompactOrderbookRequest(
    val userId: String,
    val sessionId: String,
    val secCode: String
)data class SessionRequest(
    val userId: String? = "",
    val sessionId: String? = "",
    val accNo: String? = "",
    val cifCode: String? = ""
)

data class SendOrderReq(
    val clOrderRef: String? = "",
    val status: String? = "",
    val board: String? = "",
    val orderTime: Long? = 0,
    val buySell: String? = "",
    val stockCode: String? = "",
    val stockName: String? = "",
    val investType: String? = "",
    val ordQty: Double? = 0.0,
    val ordPrice: Double? = 0.0,
    val timeInForce: String? = "",
    val clientCode: String? = "",
    val orderPeriod: Long? = 0,
    val inputBy: String? = "",
    val sessionId: String? = "",
    val ip: String? = "",
    val accNo: String? = "",
    val accType: String? = "",
    val orderType: String? = "",
    val sameOrderList: List<Double> = listOf()
)

data class AmendFastOrderReq(
    val stock: String? = "",
    val stockName: String? = "",
    val board: String? = "",
    val sessionId: String? = "",
    val buySell: String? = "",
    val oldPrice: Double? = 0.0,
    val newPrice: Double? = 0.0,
    val inputBy: String? = "",
    val ipAddress: String? = "",
    val channel: Int? = 0, // 0=OLT, 1=OMS, 7=CNA
    val accNo: String? = "",
    val volume: String? = "",
    val sameOrderList: List<Double> = listOf()
)

data class CancelFastOrderReq(
    val stock: String? = "",
    val sessionId: String? = "",
    val stockName: String? = "",
    val board: String? = "",
    val buySell: String? = "",
    val price: Double? = 0.0, // if not set, cancel all orders by order side (Buy/Sell)
    val qty: Double? = 0.0,
    val inputBy: String? = "",
    val ipAddress: String? = "",
    val channel: Int? = 0, // 0=OLT, 1=OMS, 7=CNA
    val accNo: String? = "",
    val sameOrderList: List<Double> = listOf()
)

data class PublishFastOrderReq(
    val userId: String? = "",
    val sessionId: String? = "",
    val subsOp: Int? = 1,
    val accNo: String? = "",
    val stockCode: String? = ""
)

data class PublishAccPosReq(
    val userId: String? = "",
    val sessionId: String? = "",
    val subsOp: Int? = 1,
    val accNo: String? = ""
)

data class AmendOrderRequest(
    val newCliOrderRef: String? = "",
    val oldCliOrderRef: String? = "",
    val stockCode: String? = "",
    val orderID: String? = "",
    val newQty: Double? = 0.0,
    val newPrice: Double? = 0.0,
    val newTimeInForce: String? = "",
    val newOrdPeriod: Long? = 0,
    val inputBy: String? = "",
    val sessionId: String? = "",
    val ip: String? = "",
    val accNo: String? = ""
)

data class WithdrawOrderRequest(
    val newCliOrderRef: String? = "",
    val oldCliOrderRef: String? = "",
    val orderID: String? = "",
    val inputBy: String? = "",
    val sessionId: String? = "",
    val ip: String? = "",
    val accNo: String? = ""
)

data class AdvanceOrderRequest(
    val clOrderRef: String = "",
    val accNo: String = "",
    val accType: String = "",
    val advType: Int = 0,
    val stockCode: String = "",
    val ordQty: Long = 0,
    val bracketCriteria: AdvancedCriteriaRequest? = AdvancedCriteriaRequest(),
    val takeProfitCriteria: AdvancedCriteriaRequest? = AdvancedCriteriaRequest(),
    val stopLossCriteria: AdvancedCriteriaRequest? = AdvancedCriteriaRequest(),
    val validUntil: Long = 0,
    val inputBy: String = "",
    val sessionId: String = "",
    val ip: String = ""
)

data class AdvancedCriteriaRequest(
    val advType: Int = 0,
    val opr: Int = 0,
    val triggerVal: Long = 0,
    val triggerCategory: Int = 0,
    val triggerOrder: TriggerOrder? = TriggerOrder.newBuilder().build()
)

data class TriggerOrderRequest(
    val stockCode: String? = null,
    val buySell: String? = null,
    val ordType: Int = 0,
    val timeInForce: String? = null,
    val ordQty: Long = 0,
    val ordPrice: Long = 0
)

data class OrderAdapterData(
    val stockCode: String? = "",
    val price: Double? = 0.0,
    val lot: Double? = 0.0,
    val totalPrice: Double? = 0.0,
    val lastPrice: Double = 0.0,
    val buySell: String? = "",
    val isSliceOrder: Boolean = false,
    val isAdvOrder: Boolean = false,
    val slicingType: Int? = 0,
    val orderType: String? = "",
    val splitNumber: Int? = 0,
    val splitBlockSize: Int? = 0,
    val bestBid: Double? = 0.0,
    val bestOffer: Double? = 0.0,
    val amendPrice: Double? = 0.0,
    val boardType: String? = "",
    val compareType: String? = "",
    val isBtnOrder: Boolean? = false,
    val isSuccessOrder: Boolean? = false,
    val timeInForce: String? = "",
    val orderPeriod: Long? = 0L,
    var closePrice: Double = 0.0,
    val proceedAmount: Double? = 0.0,
    val endTimeSliceOrder: Long? = 0L,
    val isAmendPartial: Boolean? = false,
    val isAutoOrder: Boolean = false,
    val bracketCriteria: AdvancedCriteriaRequest? = AdvancedCriteriaRequest(),
    val takeProfitCriteria: AdvancedCriteriaRequest? = AdvancedCriteriaRequest(),
    val stopLossCriteria: AdvancedCriteriaRequest? = AdvancedCriteriaRequest(),
    val stockNotation: Boolean? = false,
    val avgPrice: Double? = 0.0,
    val isBalanceNotEnough: Boolean = false,
    val relId: String = "",
    val isMarketOrder: Boolean = false,
    val marketOrderType: String = "",
    val isMarketClosed: Boolean = false,
    val isAmendMarketOrder: Boolean = false
)

data class OrderListRequest(
    val userId: String,
    val accNo: String,
    val sessionId: String,
    val includeTrade: Int
)

data class CompanyProfileRequest(
    val userId: String,
    val sessionId: String,
    val stockCode: String
)

data class NewsRequest(
    val userId: String,
    val sessionId: String,
    val tags: String,
    val publishDateStart: Long,
    val publishDateEnd: Long
)

data class StockTradeReq(
    val userId: String,
    val sessionId: String,
    val secCode: String,
    val action: Int,
    val tradeNo: Double,
    val limit: Int,
    val tradeNoNew: Long
)

data class KeyStatRequest(
    val userId: String,
    val sessionId: String,
    val stockCode: String
)

data class FinancialRequest(
    val userId: String,
    val sessionId: String,
    val stockCode: String
)

data class DetailFinancialRequest(
    val userId: String,
    val sessionId: String,
    val stockCode: String,
    val periodRange: Int,
    val PeriodType: Int
)

data class TradeSumRequest(
    val userId: String = "",
    val sessionId: String = "",
    val secCode: String = "",
    val startDate: Long = 0,
    val endDate: Long = 0
)
data class EarningsPerShareReq(
    val userId: String = "",
    val sessionId: String = "",
    val stockCode: String = ""
)

data class StockParamListRequest(
    val userId: String,
    val sessionId: String,
    val mktId: String,
    val boardcode: String,
    val latestRetrieveStockParam: Long
)

data class SliceOrderRequest(
    val clOrderRef: String? = "",
    val accNo: String? = "",
    val sessionId: String,
    val advType: Int? = 0, // 0 = split
    val stockCode: String? = "",
    val buySell: String? = "",
    val ordType: Int? = 0, // 0 = limit
    val ordQty: Long? = 0,
    val ordPrice: Long? = 0,
    val splitNumber: Int? = 0, // For No. of split number only
    val inputBy: String? = "",
    val ipAddress: String? = "",
    val accType: String? = "",
    val channel: Int? = 0, // 0=OLT
    val splitBlockSize: Int? = 0, // For Block size only
    val mediaSource: Int = 0, // 0=Android, 1=iOS, 2=HTS, 3=WTS
    val endTriggerTime: Long? = 0L // if slicing type = 1 / execution time
)

data class MaxOrderByStockReq(
    val userId: String? = "",
    val sessionId: String,
    val accNo: String? = "",
    val buySell: String? = "", //B="Buy", S="Sell"
    val stockCode: String? = "",
    val price: Double? = 0.0, // if price =0, harus diisi dengan nilai last
    val buyType: String? = "", //C="Cash", L="Limit"
    val boardCode: String? = "",
    val relId: String? = ""
)

data class FastOrderListReq(
    val userId: String? = "",
    val sessionId: String,
    val accNo: String? = "",
    val stockCode: String? = ""
)

data class TradeSummaryReq (
    val userId: String,
    val sessionId: String,
    val boardCode: String,
    val stockCodeList: List<String>)

data class GetPerBandReq(
    val userId: String = "",
    val sessionId: String = "",
    val stockCode: String = "",
    val period: Int = 0
)

data class GetPbvBandReq(
    val userId: String = "",
    val sessionId: String = "",
    val stockCode: String = "",
    val period: Int = 0
)
data class NewsPromoRequest (
    val userId: String,
    val sessionId: String,
    val promo: String
)
data class PromoBannerReq (
    val userId: String,
    val sessionId: String,
)

data class OrderHistoryRequest (
    val userId: String,
    val sessionId: String,
    val accNo: String,
    val includeTrade: Int,
    val startDate: Long,
    val endDate: Long,
    val stockCode: String
)


data class StockPickRequest (
    val userId: String,
    val sessionId: String,
    val status: Boolean,
)

data class UserWatchListRequest(
    val userId: String,
    val sessionId: String? = "",
    val wlgCode: String? = "",
    val includeWLItem: Boolean? = false,
    val wlCode: String? = "",
    val itemCode: String? = "",
    val itemSeq: Int? = 0,
    val userWlListItem: List<String>? = listOf(),
    val userWlListCat: List<String>? = listOf(),
    val newWlCode: String = ""
)


data class WatchListCategory(
    val category: String,
    val stockList: List<StockParamObject> = listOf(),
    val stockListString: List<String> = listOf(),
    var isChecked: Boolean = false
)

data class AllUserWatchListRequest (
    val userId: String,
    val sessionId: String
)

data class StockAnalysisRatingReq(
    val userId: String = "",
    val sessionId: String = "",
    val stockCode: String = ""
)

data class BrokerStockSumRequest(
    val userId: String = "",
    val sessionId: String,
    val trxDate: Long = 0,
    val stockCode: String = ""
)
data class ValidatePinReq(
    val userId: String,
    val pinValue: String,
    val sessionId: String,
    val rememberedPin: Boolean
)
data class ChangePasswordReq(
    val userId: String,
    val oldPass: String,
    val newPass: String,
    val confirmPass: String
)
data class ChangePinReq(
    val userId: String,
    val oldPin: String,
    val newPin: String,
    val confirmPin: String
)

data class ManageWatchlistItem(
    val stockCode: String = "",
    var stockName: String = ""
)

data class SaveDeviceTokenReq(
    val userId: String = "",
    var sessionId: String = "",
    var token: String = ""
)

data class BrokerRankByStockReq(
    val userId: String = "",
    val startDate: Long = 0,
    val endDate: Long = 0,
    val boardCode: String = "",
    val stockCode: String = "",
    val brokerType: Int = 0,
    val sessionId: String = "",
    val isNet: Boolean = false
)

data class BrokerRankActivityReq(
    val userId: String = "",
    val startDate: Long = 0,
    val endDate: Long = 0,
    val boardCode: String = "",
    val brokerCode: String = "",
    val sessionId: String = "",
)

data class BrokerSummaryRankingReq(
    val userId: String = "",
    val startDate: Long = 0,
    val endDate: Long = 0,
    val sessionId: String = "",
    val sortType: Int = 0
)

data class IndiceDataRequest(
    val userId: String,
    val sessionId: String,
    val indiceCode:String
)

data class StockRankInfoRequest(
    val userId: String,
    val sessionId: String,
    val sortAscending: Int,
    val sortType: Int,
    val seqNo: Int,
    val maxData: Int
)

data class StockInfoDetailRequest (
    val userId: String,
    val sessionId: String,
    val stockCode: String
)

data class AdvanceOrderListRequest(
    val userId: String,
    val accNo: String,
    val includeOrderInfo: Boolean
)

data class FibonacciPivotPointReq(
    val userId: String,
    val sessionId: String,
    val stockCode: String
)

data class ChartIntradayRequest(
    val userId: String,
    val sessionId: String,
    val itemCode: String,
    val boardCode: String,
    val timeUnit: Int,
    val ssDateFrom: Long,
    val ssDateTo: Long
)

data class RightIssueInfoReq(
    val userId: String,
    val sessionId: String,
    val accNo: String
)

data class TradeListReq(
    val userId: String,
    val sessionId: String,
    val accNo: String
)

data class AutoOrderRequest(
    val userId: String = "",
    val clOrderRef: String? = "",
    val accNo: String? = "",
    val sessionId: String,
    val stockCode: String = "",
    val ordQty: Long = 0,
    val ordPrice: Long = 0,
    val triggerCategory: Int = 0,
    val timeInForce: String = "0",
    val validUntil: Long = 0L,
    val opr: Int = 2,
    val triggerval: Long = 0L,
    val inputBy: String? = "",
    val ipAddress: String? = "",
    val accType: String? = "",
    val channel: Int? = 0, // 0=OLT
)

data class ExerciseOrderListReq(
    val reqType: String,
    val reqInfo: List<String>,
    val userId: String,
    val sessionId: String
)

data class CaCalendarbyCaDateInRangeReq(
    val userId: String,
    val sessionId: String,
    val year: String,
    val month: String,
)

data class SendExerciseOrderRequest(
    val sessionId: String,
    val trxCode: String,
    val trxDate: Long,
    val trxType: Int,
    val accNo: String,
    val stockCode: String,
    val qty: Double,
    val price: Double,
    val amount: Double,
    val ipAddress: String,
    val inputBy: String,
    val channel: Int
)

data class WithdrawExerciseOrderRequest(
    val sessionId: String,
    val trxCode: String,
    val orderId: String,
    val inputBy: String,
    val ipAddress: String,
    val accNo: String
)

data class MarketSessionReq(
    val userId: String
)

data class GlobalMarketReq(
    val userId: String,
    val sessionId: String
)

data class WithdrawCashReq(
    val userId: String,
    val sessionId: String,
    val accNo: String,
    val amount: Double,
    val notes: String,
    val reference: String
)

data class RdnHistoryRequest(
    val userId: String,
    val accNo: String,
    val startDate: Long,
    val endDate: Long,
    val type: String, // W = withdraw, D = deposit, * = all
    val sessionId: String,
    val pageRequest: PageRequest
)

data class TradeListHistoryReq(
    val userId: String,
    val sessionId: String,
    val accNo: String,
    val startDate: Long,
    val endDate: Long,
    val pageRequest: PageRequest,
    val stockCode: String
)

data class PageRequest(
    val page: Int,
    val size: Int
)

data class ValidateSessionReq(
    val userId: String,
    val sessionId: String
)

data class NewsResearchContentReq(
    val userId: String,
    val sessionId: String,
    val page: Int,
    val size: Int
)

data class StockPickReportReq(
    val userId: String,
    val sessionId: String
)

data class PriceAlertReq(
    val userId: String = "",
    val sessionId: String = "",
    val stockCode: String = ""
)

data class AddPriceAlertReq(
    val userId: String = "",
    val sessionId: String = "",
    val stockCode: String = "",
    val operation: String = "",
    val price: Double = 0.0,
    val id: Long = 0
)

data class RemovePriceAlertReq(
    val userId: String,
    val sessionId: String,
    val id: Long
)

data class NotificationHistoryReq(
    val userId: String,
    val sessionId: String,
    val page: Int,
    val size: Int
)

data class GlobalRankReq(
    val userId: String,
    val sessionId: String,
    val sortField: Int,
    val board: String,
    val activity: Int,
    val startDate: Long,
    val endDate: Long
)

data class HelpReq(
    val userId: String,
    val sessionId: String
)

data class FaqReq(
    val userId: String,
    val sessionId: String,
    val category: String? = null,
    val query1: String? = null,
    val query2: String? = null,
    val size: Int? = null,
    val page: Int? = null,
)

data class IPOListRequest(
    val userId: String,
    val sessionId: String,
    val isBookBuilding: Boolean,
    val sort: Int,
    val sizeItem: Int,
    val page:Int
)

data class IPOInfoRequest(
    val userId: String,
    val sessionId: String,
    val ipoCode: String
)

data class IPOOrderListRequest(
    val userId: String,
    val sessionId: String,
    val accNo: String,
    val ipoCode: String,
    val sort: Int,
    val sizeItem: Int,
    val page:Int
)


data class EipoOrderRequest(
    val clOrderRef: String? = "",
    val userId: String? = "",
    val sessionId: String? = "",
    val accNo: String? = "",
    val price: Double? = 0.0,
    val qty: Double? = 0.0,
    val eipoCode: String? = "",
    val ipAddress: String? ="",
    val orderTime: Long? = 0L,
    val isAffiliatedParty: Boolean = false,
    val isEmployee: Boolean = false,
    val isBenefaciaries: Boolean = false,
    val isSelfOrder: Boolean = false
)

data class NewsFeedRequest(
    val userId: String,
    val sessionId: String,
    val page: Int,
    val size: Int,
    val newsStatus: Int
)

data class NewsFeedByStockRequest(
    val userId: String,
    val sessionId: String,
    val page: Int,
    val size: Int,
    val stockCode: List<String>
)

data class NewsFeedSearchRequest(
    val userId: String,
    val sessionId: String,
    val page: Int,
    val size: Int,
    val searchKey: String,
    val report: Boolean
)

data class CorporateActionTabRequest(
    val userId: String,
    val sessionId: String,
    val stockCode: String,
    val calType: Int
)

data class TradeListHistoryDetailReq(
    val userId: String,
    val sessionId: String,
    val accNo: String,
    val exchordid: String,
    val price: Double
)

data class RealizedGainLossYearRequest(
    val userId: String,
    val accNo: String,
    val sessionId: String,
    val year: Int,
    val stockCode: String
)

data class RealizedGainLossMonthRequest(
    val userId: String,
    val accNo: String,
    val sessionId: String,
    val year: Int,
    val month: Int,
    val stockCode: String
)


data class TrustedDeviceReq(
    val userId: String,
    val sessionId: String
)

data class SendOtpTrustedDeviceRequest(
    val userId: String,
    val channel: String,
    val deviceId: String,
    val deviceModel: String,
    val manufacture: String,
    val appVersion: String
)

data class VerifyOtpTrustedDeviceRequest(
    val userId: String,
    val otp: String,
    val deviceId: String
)

data class DeleteTrustedDeviceRequest(
    val userId: String,
    val sessionId: String,
    val deviceId: String
)

data class ExerciseSessionReq(
    val userId: String,
    val sessionId: String
)




