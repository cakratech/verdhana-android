package com.bcasekuritas.mybest.app.domain.interactors

import com.bcasekuritas.mybest.app.data.entity.AccountObject
import com.bcasekuritas.mybest.app.data.entity.BiometricObject
import com.bcasekuritas.mybest.app.data.entity.FilterRunningTradeObject
import com.bcasekuritas.mybest.app.data.entity.OrderReplyObject
import com.bcasekuritas.mybest.app.data.entity.SessionObject
import com.bcasekuritas.mybest.app.data.entity.StockNotationObject
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.app.data.entity.StockWithNotationObject
import com.bcasekuritas.mybest.app.domain.FlowUseCase
import com.bcasekuritas.mybest.app.domain.dto.request.AccountInfoRequest
import com.bcasekuritas.mybest.app.domain.dto.request.AddPriceAlertReq
import com.bcasekuritas.mybest.app.domain.dto.request.AdvanceOrderListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.AdvanceOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.AllUserWatchListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.AmendFastOrderReq
import com.bcasekuritas.mybest.app.domain.dto.request.AmendOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.AutoOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerRankActivityReq
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerRankByStockReq
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerStockSumRequest
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerSummaryRankingReq
import com.bcasekuritas.mybest.app.domain.dto.request.CaCalendarbyCaDateInRangeReq
import com.bcasekuritas.mybest.app.domain.dto.request.CancelFastOrderReq
import com.bcasekuritas.mybest.app.domain.dto.request.CashPosRequest
import com.bcasekuritas.mybest.app.domain.dto.request.ChangePasswordReq
import com.bcasekuritas.mybest.app.domain.dto.request.ChangePinReq
import com.bcasekuritas.mybest.app.domain.dto.request.ChartIntradayRequest
import com.bcasekuritas.mybest.app.domain.dto.request.CompanyProfileRequest
import com.bcasekuritas.mybest.app.domain.dto.request.CorporateActionTabRequest
import com.bcasekuritas.mybest.app.domain.dto.request.DeleteTrustedDeviceRequest
import com.bcasekuritas.mybest.app.domain.dto.request.DetailFinancialRequest
import com.bcasekuritas.mybest.app.domain.dto.request.EarningsPerShareReq
import com.bcasekuritas.mybest.app.domain.dto.request.EipoOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.ExerciseOrderListReq
import com.bcasekuritas.mybest.app.domain.dto.request.ExerciseSessionReq
import com.bcasekuritas.mybest.app.domain.dto.request.FaqReq
import com.bcasekuritas.mybest.app.domain.dto.request.FastOrderListReq
import com.bcasekuritas.mybest.app.domain.dto.request.FibonacciPivotPointReq
import com.bcasekuritas.mybest.app.domain.dto.request.FinancialRequest
import com.bcasekuritas.mybest.app.domain.dto.request.GetPbvBandReq
import com.bcasekuritas.mybest.app.domain.dto.request.GetPerBandReq
import com.bcasekuritas.mybest.app.domain.dto.request.GlobalMarketReq
import com.bcasekuritas.mybest.app.domain.dto.request.GlobalRankReq
import com.bcasekuritas.mybest.app.domain.dto.request.HelpReq
import com.bcasekuritas.mybest.app.domain.dto.request.IPOInfoRequest
import com.bcasekuritas.mybest.app.domain.dto.request.IPOListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.IPOOrderListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.IndexSectorDataRequest
import com.bcasekuritas.mybest.app.domain.dto.request.IndexSectorRequest
import com.bcasekuritas.mybest.app.domain.dto.request.IndiceDataRequest
import com.bcasekuritas.mybest.app.domain.dto.request.KeyStatRequest
import com.bcasekuritas.mybest.app.domain.dto.request.LatestTradeDetailRequest
import com.bcasekuritas.mybest.app.domain.dto.request.LogonRequest
import com.bcasekuritas.mybest.app.domain.dto.request.LogoutReq
import com.bcasekuritas.mybest.app.domain.dto.request.MarketSessionReq
import com.bcasekuritas.mybest.app.domain.dto.request.MaxOrderByStockReq
import com.bcasekuritas.mybest.app.domain.dto.request.NewsFeedByStockRequest
import com.bcasekuritas.mybest.app.domain.dto.request.NewsFeedRequest
import com.bcasekuritas.mybest.app.domain.dto.request.NewsFeedSearchRequest
import com.bcasekuritas.mybest.app.domain.dto.request.NewsPromoRequest
import com.bcasekuritas.mybest.app.domain.dto.request.NewsResearchContentReq
import com.bcasekuritas.mybest.app.domain.dto.request.NotificationHistoryReq
import com.bcasekuritas.mybest.app.domain.dto.request.OrderHistoryRequest
import com.bcasekuritas.mybest.app.domain.dto.request.OrderListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.PriceAlertReq
import com.bcasekuritas.mybest.app.domain.dto.request.PromoBannerReq
import com.bcasekuritas.mybest.app.domain.dto.request.PublishAccPosReq
import com.bcasekuritas.mybest.app.domain.dto.request.PublishFastOrderReq
import com.bcasekuritas.mybest.app.domain.dto.request.RdnHistoryRequest
import com.bcasekuritas.mybest.app.domain.dto.request.RealizedGainLossMonthRequest
import com.bcasekuritas.mybest.app.domain.dto.request.RealizedGainLossYearRequest
import com.bcasekuritas.mybest.app.domain.dto.request.RemovePriceAlertReq
import com.bcasekuritas.mybest.app.domain.dto.request.RightIssueInfoReq
import com.bcasekuritas.mybest.app.domain.dto.request.SaveDeviceTokenReq
import com.bcasekuritas.mybest.app.domain.dto.request.SendExerciseOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.SendOrderReq
import com.bcasekuritas.mybest.app.domain.dto.request.SendOtpTrustedDeviceRequest
import com.bcasekuritas.mybest.app.domain.dto.request.SessionRequest
import com.bcasekuritas.mybest.app.domain.dto.request.SettlementSchedReq
import com.bcasekuritas.mybest.app.domain.dto.request.SliceOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockAnalysisRatingReq
import com.bcasekuritas.mybest.app.domain.dto.request.StockIndexSectorRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockInfoDetailRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockOrderbookRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockParamListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockPickReportReq
import com.bcasekuritas.mybest.app.domain.dto.request.StockPickRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockPosRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockRankInfoRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockTradeReq
import com.bcasekuritas.mybest.app.domain.dto.request.StockWatchListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.TradeBookRequest
import com.bcasekuritas.mybest.app.domain.dto.request.TradeListHistoryDetailReq
import com.bcasekuritas.mybest.app.domain.dto.request.TradeListHistoryReq
import com.bcasekuritas.mybest.app.domain.dto.request.TradeListReq
import com.bcasekuritas.mybest.app.domain.dto.request.TradeSumRequest
import com.bcasekuritas.mybest.app.domain.dto.request.TrustedDeviceReq
import com.bcasekuritas.mybest.app.domain.dto.request.UserWatchListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.ValidatePinReq
import com.bcasekuritas.mybest.app.domain.dto.request.ValidateSessionReq
import com.bcasekuritas.mybest.app.domain.dto.request.VerifyOtpTrustedDeviceRequest
import com.bcasekuritas.mybest.app.domain.dto.request.WithdrawCashReq
import com.bcasekuritas.mybest.app.domain.dto.request.WithdrawExerciseOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.WithdrawOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.response.IndexSectorDetailData
import com.bcasekuritas.mybest.app.domain.dto.response.source.StockNotationRes
import com.bcasekuritas.mybest.app.domain.dto.response.source.StockParamRes
import com.bcasekuritas.mybest.app.domain.repositories.AnalysisRepo
import com.bcasekuritas.mybest.app.domain.repositories.AuthRepo
import com.bcasekuritas.mybest.app.domain.repositories.BrokerSumRepo
import com.bcasekuritas.mybest.app.domain.repositories.CalendarRepo
import com.bcasekuritas.mybest.app.domain.repositories.CategoryRepo
import com.bcasekuritas.mybest.app.domain.repositories.CompanyProfileRepo
import com.bcasekuritas.mybest.app.domain.repositories.EIpoRepo
import com.bcasekuritas.mybest.app.domain.repositories.FastOrderRepo
import com.bcasekuritas.mybest.app.domain.repositories.FinancialRepo
import com.bcasekuritas.mybest.app.domain.repositories.GlobalMarketRepo
import com.bcasekuritas.mybest.app.domain.repositories.NewsRepo
import com.bcasekuritas.mybest.app.domain.repositories.NotificationRepo
import com.bcasekuritas.mybest.app.domain.repositories.OrderRepo
import com.bcasekuritas.mybest.app.domain.repositories.PortfolioRepo
import com.bcasekuritas.mybest.app.domain.repositories.PriceAlertRepo
import com.bcasekuritas.mybest.app.domain.repositories.ProfileRepo
import com.bcasekuritas.mybest.app.domain.repositories.RightIssueRepo
import com.bcasekuritas.mybest.app.domain.repositories.RunningTradeRepo
import com.bcasekuritas.mybest.app.domain.repositories.SectorRepo
import com.bcasekuritas.mybest.app.domain.repositories.StockDetailRepo
import com.bcasekuritas.mybest.app.domain.repositories.StockParamRepo
import com.bcasekuritas.mybest.app.domain.repositories.StockTradeRepo
import com.bcasekuritas.mybest.app.domain.repositories.TradeBookRepo
import com.bcasekuritas.mybest.app.domain.repositories.TradeSummaryRepo
import com.bcasekuritas.mybest.app.domain.repositories.WatchlistRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.news.rssconverter.RssFeed
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.bcas.AccStockPosResponse
import com.bcasekuritas.rabbitmq.proto.bcas.AccountCashMovementResponse
import com.bcasekuritas.rabbitmq.proto.bcas.AddPriceAlertResponse
import com.bcasekuritas.rabbitmq.proto.bcas.AddUserWatchListGroupResponse
import com.bcasekuritas.rabbitmq.proto.bcas.AddUserWatchListItemResponse
import com.bcasekuritas.rabbitmq.proto.bcas.AdvancedOrderInfoResponse
import com.bcasekuritas.rabbitmq.proto.bcas.CIFCashPosResponse
import com.bcasekuritas.rabbitmq.proto.bcas.CakraMessage
import com.bcasekuritas.rabbitmq.proto.bcas.ChangePasswordResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ChangePinResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ClientInfoResponse
import com.bcasekuritas.rabbitmq.proto.bcas.DeleteDeviceResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseInfoResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseOrderListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseSessionResponse
import com.bcasekuritas.rabbitmq.proto.bcas.FastOrderListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.IpoOrderListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ListPriceAlertResponse
import com.bcasekuritas.rabbitmq.proto.bcas.LogonResponse
import com.bcasekuritas.rabbitmq.proto.bcas.LogoutResponse
import com.bcasekuritas.rabbitmq.proto.bcas.MarketSessionResponse
import com.bcasekuritas.rabbitmq.proto.bcas.MaxOrderByStockResponse
import com.bcasekuritas.rabbitmq.proto.bcas.NotificationHistoryResponse
import com.bcasekuritas.rabbitmq.proto.bcas.OrderListHistoryResponse
import com.bcasekuritas.rabbitmq.proto.bcas.OrderListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.PipelinesIpoInfoResponse
import com.bcasekuritas.rabbitmq.proto.bcas.PipelinesIpoListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RGainLossDtlResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RGainLossResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RemovePriceAlertResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RemoveUserWatchListGroupResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RemoveUserWatchListItemResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SaveDeviceTokenResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SendOtpResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SettlementScheduleResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleAccountInfoByCIFResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleAllUserWatchListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimplePortofolioResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleUserWatchListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.StockParamResponse
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListHistoryGroupDetailResponse
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListHistoryGroupResponse
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListHistoryResponse
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.TrustedDeviceResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ValidatePinResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ValidateSessionResponse
import com.bcasekuritas.rabbitmq.proto.bcas.VerifyOtpResponse
import com.bcasekuritas.rabbitmq.proto.bcas.WithdrawCashResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerListResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankActivityByInvType2DiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankActivityDiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankByStockDiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankByStockNetDiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankingDiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerStockSummaryResponse
import com.bcasekuritas.rabbitmq.proto.chart.Cf
import com.bcasekuritas.rabbitmq.proto.chart.Cf.CFMessage.IntradayPriceResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.CurrentMessageResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import com.bcasekuritas.rabbitmq.proto.datafeed.OrderbookSummary
import com.bcasekuritas.rabbitmq.proto.datafeed.StockRankingResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.StockTradeResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.TradeBookResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.TradeBookTimeResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.TradeDetailData
import com.bcasekuritas.rabbitmq.proto.news.CorporateActionCalendarGetByStockResponse
import com.bcasekuritas.rabbitmq.proto.news.CorporateActionCalendarGetResponse
import com.bcasekuritas.rabbitmq.proto.news.EarningsPerShareResponse
import com.bcasekuritas.rabbitmq.proto.news.FibonacciPivotPointResponse
import com.bcasekuritas.rabbitmq.proto.news.FinancialBalanceSheetResponse
import com.bcasekuritas.rabbitmq.proto.news.FinancialCashFlowResponse
import com.bcasekuritas.rabbitmq.proto.news.FinancialIncomeStatementResponse
import com.bcasekuritas.rabbitmq.proto.news.GetNewsFrequentAskedQuestionByCategoryResponse
import com.bcasekuritas.rabbitmq.proto.news.GetNewsFrequentAskedQuestionResponse
import com.bcasekuritas.rabbitmq.proto.news.GetNewsTopFiveFrequentAskedQuestionResponse
import com.bcasekuritas.rabbitmq.proto.news.GetNewsTutorialVideoResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPbvBandResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPbvDataResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPerBandResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPerDataResponse
import com.bcasekuritas.rabbitmq.proto.news.LatestComoditiesResponse
import com.bcasekuritas.rabbitmq.proto.news.LatestCurrencyResponse
import com.bcasekuritas.rabbitmq.proto.news.LatestIndexResponse
import com.bcasekuritas.rabbitmq.proto.news.LoginBannerResponse
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoFeedResponse
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoFeedSearchByStockResponse
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoFeedSearchResponse
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoResponse
import com.bcasekuritas.rabbitmq.proto.news.NewsResearchContentResponse
import com.bcasekuritas.rabbitmq.proto.news.NewsStockPickSingleResponse
import com.bcasekuritas.rabbitmq.proto.news.PromotionBannerResponse
import com.bcasekuritas.rabbitmq.proto.news.SearchNewsFrequentAskedQuestionResponse
import com.bcasekuritas.rabbitmq.proto.news.SearchNewsResearchContentResponse
import com.bcasekuritas.rabbitmq.proto.news.StockAnalysisRatingResponse
import com.bcasekuritas.rabbitmq.proto.news.StockDetilCompanyProfileResponse
import com.bcasekuritas.rabbitmq.proto.news.StockIndexMappingByStockIndexResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewBalanceSheetResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewCashFlowResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewIncomeStatementResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewIndexSector
import com.bcasekuritas.rabbitmq.proto.news.ViewKeyStatResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewKeyStatsRTIResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewStockInfoDetilResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewStockPickResearchReportResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/*
class SourceByCategoryUseCase constructor(
    private val repository: SourceRepository
) : FlowUseCase<Nothing?, SourceRes>() {
    override suspend fun execute(parameters: Nothing?): Flow<Resource<SourceRes>> {
        return repository.getSourceByCategory()
    }
}

class SourceByCategoryLocalUseCase constructor(
    private val repository: SourceRepository
) : FlowUseCase<Nothing?, SourceRes>() {
    override suspend fun execute(parameters: Nothing?): Flow<Resource<SourceRes>> {
        return repository.getSourceByCategory()
    }
}*/

class GetLoginUseCase constructor(
    private val repository: AuthRepo
) : FlowUseCase<LogonRequest, LogonResponse?>() {
    override suspend fun execute(parameters: LogonRequest?): Flow<Resource<LogonResponse?>> {
        return repository.getLogin(parameters!!)
    }
}

class GetLogoutUseCase constructor(
    private val repository: AuthRepo
): FlowUseCase<LogoutReq, LogoutResponse?>() {
    override suspend fun execute(parameters: LogoutReq?): Flow<Resource<LogoutResponse?>> {
        return repository.getLogout(parameters!!)
    }
}

class GetValidatePinUseCase constructor(
    private val repository: AuthRepo
) : FlowUseCase<ValidatePinReq, ValidatePinResponse?>() {
    override suspend fun execute(parameters: ValidatePinReq?): Flow<Resource<ValidatePinResponse?>> {
        return repository.getValidatePin(parameters!!)
    }
}

class GetChangePasswordUsecase constructor(
    private val repository: AuthRepo
) : FlowUseCase<ChangePasswordReq, ChangePasswordResponse?>() {
    override suspend fun execute(parameters: ChangePasswordReq?): Flow<Resource<ChangePasswordResponse?>> {
        return repository.getChangePassword(parameters!!)
    }
}

class GetChangePinUsecase constructor(
    private val repository: AuthRepo
) : FlowUseCase<ChangePinReq, ChangePinResponse?>() {
    override suspend fun execute(parameters: ChangePinReq?): Flow<Resource<ChangePinResponse?>> {
        return repository.getChangePin(parameters!!)
    }
}

class SaveDeviceTokenUsecase constructor(
    private val repository: AuthRepo
) : FlowUseCase<SaveDeviceTokenReq, SaveDeviceTokenResponse?>() {
    override suspend fun execute(parameters: SaveDeviceTokenReq?): Flow<Resource<SaveDeviceTokenResponse?>> {
        return repository.saveDeviceToken(parameters!!)
    }
}

class GetAccountInfoUseCase constructor(
    private val repository: PortfolioRepo
) : FlowUseCase<AccountInfoRequest, ClientInfoResponse?>() {
    override suspend fun execute(parameters: AccountInfoRequest?): Flow<Resource<ClientInfoResponse?>> {
        return repository.getAccountInfo(parameters!!)
    }
}

class GetCashPosUseCase constructor(
    private val repository: PortfolioRepo
) : FlowUseCase<CashPosRequest, CIFCashPosResponse?>() {
    override suspend fun execute(parameters: CashPosRequest?): Flow<Resource<CIFCashPosResponse?>> {
        return repository.getCashPos(parameters!!)
    }
}

class GetSettlementSchedUseCase constructor(
    private val repository: PortfolioRepo
) : FlowUseCase<SettlementSchedReq, SettlementScheduleResponse?>() {
    override suspend fun execute(parameters: SettlementSchedReq?): Flow<Resource<SettlementScheduleResponse?>> {
        return repository.getSettlementSched(parameters!!)
    }
}

class GetStockPosUseCase constructor(
    private val repository: PortfolioRepo
): FlowUseCase<StockPosRequest, AccStockPosResponse?>() {
    override suspend fun execute(parameters: StockPosRequest?): Flow<Resource<AccStockPosResponse?>> {
        return repository.getStockPos(parameters!!)
    }
}

class GetSimpleAccountInfoUseCase constructor(
    private val repository: PortfolioRepo
): FlowUseCase<SessionRequest, SimpleAccountInfoByCIFResponse?>() {
    override suspend fun execute(parameters: SessionRequest?): Flow<Resource<SimpleAccountInfoByCIFResponse?>> {
        return repository.getSimpleAccountInfo(parameters!!)
    }
}

class GetSimplePortfolioUseCase constructor(
    private val repository: PortfolioRepo
): FlowUseCase<SessionRequest, SimplePortofolioResponse?>() {
    override suspend fun execute(parameters: SessionRequest?): Flow<Resource<SimplePortofolioResponse?>> {
        return repository.getSimplePortfolio(parameters!!)
    }
}

class PublishAccPosUseCase constructor(
    private val repository: PortfolioRepo
) {
    suspend fun publishFastOrderRepo(publishAccPosReq: PublishAccPosReq) {
        repository.publishAccPos(publishAccPosReq)
    }
}

class StartCIFStockPosUseCase constructor(
    private val repository: PortfolioRepo
) {
    suspend fun startCIFStockPos() {
        repository.startCIFStockPos()
    }
}

class SetListenerCIFStockPosUseCase constructor(
    private val repository: PortfolioRepo
) {
    suspend fun setListenerCIFStockPos(miListener: MQMessageListener<CakraMessage>?) {
        repository.setListenerCIFStockPos(miListener)
    }
}

class SubscribeCIFStockPosUseCase constructor(
    private val repository: PortfolioRepo
) {
    suspend fun subscribeCIFStockPos(accno: String?) {
        repository.subscribeCIFStockPos(accno)
    }
}

class UnsubscribeCIFStockPosUseCase constructor(
    private val repository: PortfolioRepo
) {
    suspend fun unsubscribeCIFStockPos(accno: String) {
        repository.unSubscribeCIFStockPos(accno)
    }
}

class StopCIFStockPosUseCase constructor(
    private val repository: PortfolioRepo
) {
    suspend fun stopCIFStockPos() {
        repository.stopCifStockPos()
    }
}

//class GetWatchListUseCase constructor(
//    private val repository: WatchlistRepo
//) : FlowUseCase<WatchListRequest, List<String>?>() {
//    override suspend fun execute(parameters: WatchListRequest?): Flow<Resource<List<String>>> {
//        return flow {
//            repository.getWatchList(parameters!!).collect() {resource ->
//                when (resource) {
//                    is Resource.Success -> {
//                        resource.data?.watchList?.bindingKeyList?.filter { it.length >3 }
//                            ?.map { it.split(".") }
//                            ?.map { it[1] }
//                    } else -> {}
//                }
//            }
//        }
//    }
//}

class GetStockDetailUseCase constructor(
    private val repository: WatchlistRepo
) : FlowUseCase<StockWatchListRequest, CurrentMessageResponse?>() {
    override suspend fun execute(parameters: StockWatchListRequest?): Flow<Resource<CurrentMessageResponse?>> {
        return flow {
            repository.getStockDetail(StockWatchListRequest(
                    parameters!!.userId, parameters.sessionId, "RG", parameters.stockCodeList
                )
            ).collect() { resource ->
                when (resource) {
                    is Resource.Success -> {
                         emit(Resource.Success(data = resource.data ,DataSource.REMOTE))
                    }
                    else -> {}
                }
            }

        }
    }
}

class GetIndexSectorDetailDataUseCase constructor(
    private val repository: SectorRepo
): FlowUseCase<IndexSectorDataRequest, List<IndexSectorDetailData?>>() {
    override suspend fun execute(parameters: IndexSectorDataRequest?): Flow<Resource<List<IndexSectorDetailData?>>> {
        return flow {
            repository.getIndexSectorDetailData(parameters!!).collect() {resource ->
                when(resource) {
                    is Resource.Success -> {
                        val data = resource.data?.curMsgInfoList?.map {
                            IndexSectorDetailData(
                                0,
                                it.inSummary.indiceCode,
                                it.inSummary.indiceVal,
                                it.inSummary.change,
                                it.inSummary.chgPercent
                            )
                        }

                        emit(Resource.Success(data = data, DataSource.REMOTE))
                    }
                    else -> {}
                }
            }
        }
    }
}

class GetLatestTradeDetailUseCase constructor(
    private val repository: RunningTradeRepo
): FlowUseCase<LatestTradeDetailRequest, List<TradeDetailData>?>() {
    override suspend fun execute(parameters: LatestTradeDetailRequest?): Flow<Resource<List<TradeDetailData>?>> {
        return flow {
            repository.getLatestTradeDetail(parameters!!).collect() {resources ->
                when (resources) {
                    is Resource.Success -> {
                        emit(Resource.Success(data = resources.data?.latestTradeDetailList , DataSource.REMOTE))
                    } else -> {}
                }
            }
        }
    }
}

class GetSimpleWatchlistUseCase constructor(
    private val repository: WatchlistRepo
): FlowUseCase<UserWatchListRequest, SimpleUserWatchListResponse?>() {
    override suspend fun execute(parameters: UserWatchListRequest?): Flow<Resource<SimpleUserWatchListResponse?>> {
        return flow {
            repository.getUserWatchList(parameters!!).collect() {resources ->
                when (resources) {
                    is Resource.Success -> {
                        emit(Resource.Success(data = resources.data , DataSource.REMOTE))
                    } else -> {}
                }
            }
        }
    }
}

class GetSimpleAllWatchlistUseCase constructor(
    private val repository: WatchlistRepo
): FlowUseCase<AllUserWatchListRequest, SimpleAllUserWatchListResponse?>() {
    override suspend fun execute(parameters: AllUserWatchListRequest?): Flow<Resource<SimpleAllUserWatchListResponse?>> {
        return repository.getAllUserWatchlist(parameters!!)
    }
}

class AddUserWatchlistUseCase constructor(
    private val repository: WatchlistRepo
): FlowUseCase<UserWatchListRequest, AddUserWatchListGroupResponse?>() {
    override suspend fun execute(parameters: UserWatchListRequest?): Flow<Resource<AddUserWatchListGroupResponse?>> {
        return repository.addUserWatchList(parameters!!)
    }
}

class AddItemCategoryUseCase constructor(
    private val repository: WatchlistRepo
): FlowUseCase<UserWatchListRequest, AddUserWatchListItemResponse?>() {
    override suspend fun execute(parameters: UserWatchListRequest?): Flow<Resource<AddUserWatchListItemResponse?>> {
        return repository.addItemCategory(parameters!!)
    }
}

class RemoveWatchListCategoryUseCase constructor(
    private val repository: WatchlistRepo
): FlowUseCase<UserWatchListRequest, RemoveUserWatchListGroupResponse?>() {
    override suspend fun execute(parameters: UserWatchListRequest?): Flow<Resource<RemoveUserWatchListGroupResponse?>> {
        return repository.removeWatchListCategory(parameters!!)
    }
}

class RemoveItemCategoryUseCase constructor(
    private val repository: WatchlistRepo
): FlowUseCase<UserWatchListRequest, RemoveUserWatchListItemResponse?>() {
    override suspend fun execute(parameters: UserWatchListRequest?): Flow<Resource<RemoveUserWatchListItemResponse?>> {
        return repository.removeItemCategory(parameters!!)
    }
}

class GetTradeBookUseCase constructor(
    private val repository: TradeBookRepo
) : FlowUseCase<TradeBookRequest, TradeBookResponse?>() {
    override suspend fun execute(parameters: TradeBookRequest?): Flow<Resource<TradeBookResponse?>> {
        return repository.getTradeBook(parameters!!)
    }
}

class GetTradeBookTimeUseCase constructor(
    private val repository: TradeBookRepo
) : FlowUseCase<TradeBookRequest, TradeBookTimeResponse?>() {
    override suspend fun execute(parameters: TradeBookRequest?): Flow<Resource<TradeBookTimeResponse?>> {
        return repository.getTradeBookTime(parameters!!)
    }
}

class GetStockOrderBookUseCase constructor(
    private val repository: StockDetailRepo
) : FlowUseCase<StockOrderbookRequest, List<OrderbookSummary?>>() {

    override suspend fun execute(parameters: StockOrderbookRequest?): Flow<Resource<List<OrderbookSummary?>>> {
        return flow {
            repository.getStockOrderbook(parameters!!).collect() { resource ->
                when (resource) {
                    is Resource.Success -> {
                        emit(Resource.Success(data = resource.data?.curMsgInfoList?.map {
                            it.obSummary
                        },DataSource.REMOTE))
                    }
                    else -> {}
                }
            }
        }
    }
}

class GetOrderListUseCase constructor(
    private val repository: OrderRepo
) : FlowUseCase<OrderListRequest, OrderListResponse?>() {

    override suspend fun execute(parameters: OrderListRequest?): Flow<Resource<OrderListResponse?>> {
        return repository.getOrderList(parameters!!)
    }
}

class GetStockTradeUseCase constructor(
    private val repository: StockTradeRepo
) : FlowUseCase<StockTradeReq, StockTradeResponse?>() {
    override suspend fun execute(parameters: StockTradeReq?): Flow<Resource<StockTradeResponse?>> {
        return repository.getStockTrade(parameters!!)
    }
}

class StartTradeSumUseCase constructor(
    private val repository: WatchlistRepo
) {
    suspend fun startTradeSum() {
        repository.startTradeSum()
    }
}

class SetListenerTradeSumUseCase constructor(
    private val repository: WatchlistRepo
) {
    suspend fun setListener(miListener: MQMessageListener<MIMessage>?) {
        repository.setListenerTradeSum(miListener)
    }
}

class SubscribeAllTradeSumUseCase constructor(
    private val repository: WatchlistRepo
) {
    suspend fun subscribe(parameters: List<String>?) {
        repository.subscribeAllTradeSum(parameters!!)
    }
}

class SubscribeTradeSumUseCase constructor(
    private val repository: WatchlistRepo
) {
    suspend fun subscribe(parameters: String?) {
        repository.subscribeTradeSum(parameters!!)
    }
}

class UnSubscribeAllTradeSumUseCase constructor(
    private val repository: WatchlistRepo
) {
    suspend fun unSubscribe(parameters: List<String>?) {
        repository.unSubscribeAllTradeSum(parameters!!)
    }
}

class UnSubscribeTradeSumUseCase constructor(
    private val repository: WatchlistRepo
) {
    suspend fun unSubscribe(parameters: String?) {
        repository.unSubscribeTradeSum(parameters!!)
    }
}

class StopTradeSumUserCase constructor(
    private val repository: WatchlistRepo
) {
    suspend fun stopTradeSum() {
        repository.stopTradeSum()
    }
}

class StartRunningTradeUserCase constructor(
    private val repository: RunningTradeRepo
) {
    suspend fun startRunningTrade() {
        repository.startRunningTrade()
    }
}

class SetListenerRunningTradeUseCase constructor(
    private val repository: RunningTradeRepo
) {
    suspend fun setListener(miListener: MQMessageListener<MIMessage>?) {
        repository.setListenerRunningTrade(miListener)
    }
}

class SubscribeRunningTradeUseCase constructor(
    private val repository: RunningTradeRepo
) {
    suspend fun subscribe(parameters: String?) {
        repository.subscribeRunningTrade(parameters!!)
    }
}

class UnSubscribeRunningTradeUseCase constructor(
    private val repository: RunningTradeRepo
) {
    suspend fun unSubscribe(parameters: String?) {
        repository.unsubscribeRunningTrade(parameters!!)
    }
}

class StopRunningTradeUseCase constructor(
    private val repository: RunningTradeRepo
) {
    suspend fun stopRunningTrade() {
        repository.stopRunningTrade()
    }
}


class InputPinUseCase {
    operator fun invoke(
            listPin: ArrayList<String>,
            pinInput: String
    ): Resource<List<String>> {

        if (pinInput.isNotEmpty()){
            if (listPin.size <= 19){
                listPin.add(pinInput)
            }
        } else {
            if (!listPin.isNullOrEmpty() || listPin.size != 0){
                listPin.removeAt(listPin.size - 1)
            }
        }
        return Resource.Success(listPin, DataSource.CACHE)
    }
}

class InputVolumeUseCase {
    operator fun invoke(
            listPin: ArrayList<String>,
            volumeInput: String
    ): Resource<List<String>> {

        if (volumeInput.isNotEmpty()){
                listPin.add(volumeInput)
        } else {
            if (!listPin.isNullOrEmpty() || listPin.size != 0){
                listPin.removeAt(listPin.size - 1)
            }
        }
        return Resource.Success(listPin, DataSource.CACHE)
    }
}

class StartOrderBookListUseCase constructor(
    private val repository: StockDetailRepo
){
    suspend fun startOrderBook(){
        repository.startOrderBook()
    }
}

class SetListenerOrderBookUseCase constructor(
    private val repository: StockDetailRepo
){
    suspend fun setListenerOrderBook(miListener: MQMessageListener<MIMessage>?){
        repository.setListenerOrderBook(miListener)
    }
}

class SubscribeOrderBookListUseCase constructor(
    private val repository: StockDetailRepo
){
    suspend fun subscribeOrderBook(parameters: String?){
        repository.subscribeOrderBook(parameters!!)
    }
}


class UnSubscribeOrderBookListUseCase constructor(
    private val repository: StockDetailRepo
){
    suspend fun unSubscribeOrderBook(parameters: String?){
        repository.unsubscribeOrderBook(parameters!!)
    }
}

class StopOrderBookListUseCase constructor(
    private val repository: StockDetailRepo
){
    suspend fun stopOrderBook(){
        repository.stopOrderBook()
    }
}

class SendOrderUseCase constructor(
    private val repository: OrderRepo
) {
    suspend fun sendOrder(newOLTOrder: SendOrderReq?) {
        repository.sendOrder(newOLTOrder)
    }
}

class SendWithdrawFastOrderUseCase constructor(
    private val repository: OrderRepo
) {
    suspend fun sendWithdrawFastOrder(cancelFastOrderReq: CancelFastOrderReq) {
        repository.sendWithdrawFastOrder(cancelFastOrderReq)
    }
}

class SendAllWithdrawFastOrderUseCase constructor(
    private val repository: OrderRepo
) {
    suspend fun sendAllWithdrawFastOrder(cancelFastOrderReq: CancelFastOrderReq) {
        repository.sendAllWithdrawFastOrder(cancelFastOrderReq)
    }
}

class SendAmendFastOrderUseCase constructor(
    private val repository: OrderRepo
) {
    suspend fun sendAmendFastOrder(amendFastOrderReq: AmendFastOrderReq) {
        repository.sendAmendFastOrder(amendFastOrderReq)
    }
}

class PublishFastOrderUseCase constructor(
    private val repository: FastOrderRepo
) {
    suspend fun publishFastOrderRepo(publishFastOrderReq: PublishFastOrderReq?) {
        repository.publishFastOrder(publishFastOrderReq)
    }
}

class SendAdvOrderBuyUseCase constructor(
    private val repository: OrderRepo
) {
    suspend fun sendAdvOrderBuy(advOrder: AdvanceOrderRequest?) {
        repository.sendAdvOrder(advOrder)
    }
}

class SendSliceOrderUseCase constructor(
    private val repository: OrderRepo
) {
    suspend fun SendSliceOrder(sliceOrderRequest: SliceOrderRequest?) {
        repository.sliceOrder(sliceOrderRequest)
    }
}

class SendAutoOrderUseCase constructor(
    private val repository: OrderRepo
) {
    suspend fun sendAutoOrder(autoOrderRequest: AutoOrderRequest) {
        repository.sendAutoOrder(autoOrderRequest)
    }
}

class SendAmendUseCase constructor(
    private val repository: OrderRepo
) {
    suspend fun SendAmend(amendOrder: AmendOrderRequest?) {
        repository.amendOrder(amendOrder)
    }
}

class SendWithdrawUseCase constructor(
    private val repository: OrderRepo
) {
    suspend fun sendWithdraw(withdrawOrder: WithdrawOrderRequest?) {
        repository.withdrawOrder(withdrawOrder)
    }
}

class SendWithdrawAdvancedOrderUseCase constructor(
    private val repository: OrderRepo
) {
    suspend fun sendWithdraw(withdrawOrder: WithdrawOrderRequest?) {
        repository.withdrawAdvancedOrder(withdrawOrder)
    }
}

//class GetNewsUseCase constructor(
//    private val repository: NewsRepo
//) : FlowUseCase<NewsRequest, List<NewsInfoResponse?>>() {
//    override suspend fun execute(parameters: NewsRequest?): Flow<Resource<List<NewsInfoResponse?>>> {
//        return repository.getNewsInfo(parameters!!)
//    }
//}

class GetCompanyProfileUseCase constructor(
    private val repository: CompanyProfileRepo
) : FlowUseCase<CompanyProfileRequest, StockDetilCompanyProfileResponse?>() {
    override suspend fun execute(parameters: CompanyProfileRequest?): Flow<Resource<StockDetilCompanyProfileResponse?>> {
        return repository.getCompanyProfile(parameters!!)
    }
}

class StartAppNotificationUseCase constructor(
    private val repository: AuthRepo
){
    suspend fun startAppNotification(miListener: MQMessageListener<CakraMessage>?){
        repository.startAppNotification(miListener)
    }
}

class UnsubscribeAppNotificationUseCase constructor(
    private val repository: AuthRepo
){
    suspend fun unsubscribeAppNotification(parameters: String?){
        repository.unsubscribeAppNotification(parameters!!)
    }
}

class SubscribeAppNotificationUseCase constructor(
    private val repository: AuthRepo
){
    suspend fun subscribeAppNotification(parameters: String?){
        repository.subscribeAppNotification(parameters!!)
    }
}

class StopAppNotificationUseCase constructor(
    private val repository: AuthRepo
){
    suspend fun stopAppNotification(){
        repository.stopAppNotification()
    }
}

class StartOrderReplyUseCase constructor(
    private val repository: OrderRepo
){
    suspend fun startOrderReply(miListener: MQMessageListener<CakraMessage>?, accno: String?){
        repository.startOrderReply(miListener, accno)
    }
}

class SubscribeOrderReplyUseCase constructor(
    private val repository: OrderRepo
){
    suspend fun subscribeOrderReply(accno: String?){
        repository.subscribeOrderReply(accno)
    }
}

class UnsubscribeOrderReplyUseCase constructor(
    private val repository: OrderRepo
){
    suspend fun unsubscribeOrderReply(accno: String?){
        repository.unsubscribeOrderReply(accno)
    }
}

class StopOrderReplyUseCase constructor(
    private val repository: OrderRepo
){
    suspend fun stopAppNotification(){
        repository.stopOrderReply()
    }
}

class GetKeyStatUseCase constructor(
    private val repository: StockDetailRepo
) : FlowUseCase<KeyStatRequest, ViewKeyStatResponse?>() {
    override suspend fun execute(parameters: KeyStatRequest?): Flow<Resource<ViewKeyStatResponse?>> {
        return repository.getKeyStat(parameters!!)
    }
}

class GetKeyStatRtiUseCase constructor(
    private val repository: StockDetailRepo
) : FlowUseCase<KeyStatRequest, ViewKeyStatsRTIResponse?>() {
    override suspend fun execute(parameters: KeyStatRequest?): Flow<Resource<ViewKeyStatsRTIResponse?>> {
        return repository.getKeyStatsRti(parameters!!)
    }
}

class GetEarningPerShareUseCase constructor(
    private val repository: StockDetailRepo
) : FlowUseCase<EarningsPerShareReq, EarningsPerShareResponse?>() {
    override suspend fun execute(parameters: EarningsPerShareReq?): Flow<Resource<EarningsPerShareResponse?>> {
        return repository.getEarningPerShare(parameters!!)
    }
}

class GetIncomeStatementUseCase constructor(
    private val repository: FinancialRepo
) : FlowUseCase<FinancialRequest, ViewIncomeStatementResponse?>() {
    override suspend fun execute(parameters: FinancialRequest?): Flow<Resource<ViewIncomeStatementResponse?>> {
        return repository.getIncomeStatement(parameters!!)
    }
}

class GetBalanceSheetUseCase constructor(
    private val repository: FinancialRepo
) : FlowUseCase<FinancialRequest, ViewBalanceSheetResponse?>() {
    override suspend fun execute(parameters: FinancialRequest?): Flow<Resource<ViewBalanceSheetResponse?>> {
        return repository.getSheetBalance(parameters!!)
    }
}

class GetCashFlowUseCase constructor(
    private val repository: FinancialRepo
) : FlowUseCase<FinancialRequest, ViewCashFlowResponse?>() {
    override suspend fun execute(parameters: FinancialRequest?): Flow<Resource<ViewCashFlowResponse?>> {
        return repository.getCashFlow(parameters!!)
    }
}

class GetDetailIncomeStatementUseCase constructor(
    private val repository: FinancialRepo
) : FlowUseCase<DetailFinancialRequest, FinancialIncomeStatementResponse?>() {
    override suspend fun execute(parameters: DetailFinancialRequest?): Flow<Resource<FinancialIncomeStatementResponse?>> {
        return repository.getDetailIncomeStatement(parameters!!)
    }
}

class GetDetailBalanceSheetUseCase constructor(
    private val repository: FinancialRepo
) : FlowUseCase<DetailFinancialRequest, FinancialBalanceSheetResponse?>() {
    override suspend fun execute(parameters: DetailFinancialRequest?): Flow<Resource<FinancialBalanceSheetResponse?>> {
        return repository.getDetailBalanceSheet(parameters!!)
    }
}

class GetDetailCashFlowUseCase constructor(
    private val repository: FinancialRepo
) : FlowUseCase<DetailFinancialRequest, FinancialCashFlowResponse?>() {
    override suspend fun execute(parameters: DetailFinancialRequest?): Flow<Resource<FinancialCashFlowResponse?>> {
        return repository.getDetailCashFlow(parameters!!)
    }
}


class GetTradeSummaryUseCase constructor(
    private val repository: TradeSummaryRepo
) : FlowUseCase<TradeSumRequest, Cf.CFMessage.TradeSummaryResponse?>() {
    override suspend fun execute(parameters: TradeSumRequest?): Flow<Resource<Cf.CFMessage.TradeSummaryResponse?>> {
        return repository.getTradeSummary(parameters!!)
    }
}

class GetStockParamListUseCase constructor(
    private val repository: StockParamRepo
) : FlowUseCase<StockParamListRequest, StockParamResponse?>() {
    override suspend fun execute(parameters: StockParamListRequest?): Flow<Resource<StockParamResponse?>> {
        return repository.getStockParamList(parameters!!)
    }
}

class GetPerBandUseCase constructor(
    private val repository: AnalysisRepo
) : FlowUseCase<GetPerBandReq, GetPerBandResponse?>() {
    override suspend fun execute(parameters: GetPerBandReq?): Flow<Resource<GetPerBandResponse?>> {
        return repository.getPerBand(parameters!!)
    }
}

class GetPerDataUseCase constructor(
    private val repository: AnalysisRepo
) : FlowUseCase<GetPerBandReq, GetPerDataResponse?>() {
    override suspend fun execute(parameters: GetPerBandReq?): Flow<Resource<GetPerDataResponse?>> {
        return repository.getPerData(parameters!!)
    }
}

class GetPbvBandUseCase constructor(
    private val repository: AnalysisRepo
) : FlowUseCase<GetPbvBandReq, GetPbvBandResponse?>() {
    override suspend fun execute(parameters: GetPbvBandReq?): Flow<Resource<GetPbvBandResponse?>> {
        return repository.getPbvBand(parameters!!)
    }
}

class GetPbvDataUseCase constructor(
    private val repository: AnalysisRepo
) : FlowUseCase<GetPbvBandReq, GetPbvDataResponse?>() {
    override suspend fun execute(parameters: GetPbvBandReq?): Flow<Resource<GetPbvDataResponse?>> {
        return repository.getPbvData(parameters!!)
    }
}
class GetNewsPromoInfoUseCase constructor(
    private val repository: NewsRepo
): FlowUseCase<NewsPromoRequest, NewsInfoResponse?>() {
    override suspend fun execute(parameters: NewsPromoRequest?): Flow<Resource<NewsInfoResponse?>> {
        return repository.getNewsPromoInfo(parameters!!)
    }
}
class GetPromoBannerUseCase constructor(
    private val repository: NewsRepo
): FlowUseCase<PromoBannerReq, PromotionBannerResponse?>() {
    override suspend fun execute(parameters: PromoBannerReq?): Flow<Resource<PromotionBannerResponse?>> {
        return repository.getPromoBanner(parameters!!)
    }
}

class GetOrderHistoryUseCase constructor(
    private val repository: OrderRepo
) : FlowUseCase<OrderHistoryRequest, OrderListHistoryResponse?>() {

    override suspend fun execute(parameters: OrderHistoryRequest?): Flow<Resource<OrderListHistoryResponse?>> {
        return repository.getOrderHistory(parameters!!)
    }
}

class GetMaxOrderByStockUseCase constructor(
    private val repository: OrderRepo
) : FlowUseCase<MaxOrderByStockReq, MaxOrderByStockResponse?>() {

    override suspend fun execute(parameters: MaxOrderByStockReq?): Flow<Resource<MaxOrderByStockResponse?>> {
        return repository.getMaxOrderByStock(parameters!!)
    }
}

class GetMaxOrderByStockForBuyingLimitUseCase constructor(
    private val repository: OrderRepo
) : FlowUseCase<MaxOrderByStockReq, MaxOrderByStockResponse?>() {

    override suspend fun execute(parameters: MaxOrderByStockReq?): Flow<Resource<MaxOrderByStockResponse?>> {
        return repository.getMaxOrderByStock(parameters!!)
    }
}

class GetStockPickUseCase constructor(
    private val repository: NewsRepo
) : FlowUseCase<StockPickRequest, NewsStockPickSingleResponse?>() {
    override suspend fun execute(parameters: StockPickRequest?): Flow<Resource<NewsStockPickSingleResponse?>> {
        return repository.getStockPick(parameters!!)
    }
}

class GetStockAnalysisRatingUseCase constructor(
    private val repository: AnalysisRepo
) : FlowUseCase<StockAnalysisRatingReq, StockAnalysisRatingResponse?>() {

    override suspend fun execute(parameters: StockAnalysisRatingReq?): Flow<Resource<StockAnalysisRatingResponse?>> {
        return repository.getStockAnalysisRating(parameters!!)
    }
}

class GetFibonacciPivotPointUseCase constructor(
    private val repository: AnalysisRepo
) : FlowUseCase<FibonacciPivotPointReq, FibonacciPivotPointResponse?>() {

    override suspend fun execute(parameters: FibonacciPivotPointReq?): Flow<Resource<FibonacciPivotPointResponse?>> {
        return repository.getFibonacciPivotPoint(parameters!!)
    }
}

class GetBrokerStockSummaryUseCase constructor(
    private val repository: BrokerSumRepo
) : FlowUseCase<BrokerStockSumRequest, BrokerStockSummaryResponse?>() {

    override suspend fun execute(parameters: BrokerStockSumRequest?): Flow<Resource<BrokerStockSummaryResponse?>> {
        return repository.getBrokerStockSummary(parameters!!)
    }
}

class GetBrokerRankByStockUseCase constructor(
    private val repository: BrokerSumRepo
) : FlowUseCase<BrokerRankByStockReq, BrokerRankByStockDiscoverResponse?>() {

    override suspend fun execute(parameters: BrokerRankByStockReq?): Flow<Resource<BrokerRankByStockDiscoverResponse?>> {
        return repository.getBrokerRankByStock(parameters!!)
    }
}

class GetBrokerSummaryByStockNetUseCase constructor(
    private val repository: BrokerSumRepo
): FlowUseCase<BrokerRankByStockReq, BrokerRankByStockNetDiscoverResponse?>() {
    override suspend fun execute(parameters: BrokerRankByStockReq?): Flow<Resource<BrokerRankByStockNetDiscoverResponse?>> {
        return repository.getBrokerSummaryByStockNet(parameters!!)
    }
}

class GetBrokerListUseCase constructor(
    private val repository: BrokerSumRepo
) : FlowUseCase<String, BrokerListResponse?>() {

    override suspend fun execute(parameters: String?): Flow<Resource<BrokerListResponse?>> {
        return repository.getBrokerList(parameters!!)
    }
}

class GetBrokerRankActivityUseCase constructor(
    private val repository: BrokerSumRepo
) : FlowUseCase<BrokerRankActivityReq, BrokerRankActivityDiscoverResponse?>() {

    override suspend fun execute(parameters: BrokerRankActivityReq?): Flow<Resource<BrokerRankActivityDiscoverResponse?>> {
        return repository.getBrokerRankActivity(parameters!!)
    }
}

class GetBrokerRankRankingUseCase constructor(
    private val repository: BrokerSumRepo
) : FlowUseCase<BrokerSummaryRankingReq, BrokerRankingDiscoverResponse?>() {

    override suspend fun execute(parameters: BrokerSummaryRankingReq?): Flow<Resource<BrokerRankingDiscoverResponse?>> {
        return repository.getBrokerRankRanking(parameters!!)
    }
}

// DAO

class InsertAccountDaoUseCase constructor(
    private val repository: ProfileRepo
) {
    suspend fun insertAccountDao(parameters: AccountObject?) {
        return repository.insertAccountDao(parameters!!)
    }
}

class GetAccNameDaoUseCase constructor(
    private val repository: ProfileRepo
) {
    suspend operator fun invoke(parameters: String?):Flow<Resource<String>>{
        return repository.getAccName(parameters!!)
    }
}

class GetAccountInfoDaoUseCase constructor(
    private val repository: ProfileRepo
) {
    suspend operator fun invoke(parameters: String?):Flow<Resource<AccountObject>>{
        return repository.getAccountInfo(parameters!!)
    }
}

class GetAllAccountDaoUseCase constructor(
    private val repository: ProfileRepo
) {
    suspend operator fun invoke():Flow<Resource<List<AccountObject>>>{
        return repository.getAllAccount()
    }
}

class InsertStockParamDaoUseCase constructor(
    private val repository: StockParamRepo
) {
    suspend fun insertStockParamDao(parameters: StockParamRes?) {
        return repository.insertStockParamDao(parameters!!)
    }
}

class InsertAllStockParamDaoUseCase constructor(
    private val repository: StockParamRepo
) {
    suspend fun insertAllStockParamDao(parameters: List<StockParamObject>?): Flow<Resource<Boolean?>> {
        return repository.insertAllStockParamDao(parameters!!)
    }
}

class DeleteStockByCodeDaoUseCase constructor(
    private val repository: StockParamRepo
) {
    suspend fun delete(stockCodes: List<String>): Flow<Resource<Boolean?>> {
        return repository.deleteStockByCodes(stockCodes)
    }
}

class DeleteStockParamDaoUseCase constructor(
    private val repository: StockParamRepo
) {
    suspend fun deleteStockParamDao() {
        return repository.deleteStockParamDao()
    }
}

class GetAllStockParamDaoUseCase constructor(
    private val repository: StockParamRepo
) {
    suspend operator fun invoke():Flow<Resource<List<StockParamObject>>>{
        return repository.getAllStockParam()
    }
}

class InsertOrderReplyDaoUseCase constructor(
    private val repository: OrderRepo
) {
    suspend fun insertOrderReplyDao(parameters: OrderReplyObject?) {
        return repository.insertOrderReply(parameters!!)
    }
}

class GetOrderReplyDaoUseCase constructor(
    private val repository: OrderRepo
) {
    suspend operator fun invoke(clOrderRef: String):Flow<Resource<OrderReplyObject>>{
        return repository.getOrderReplyDao(clOrderRef)
    }
}

class SearchStockParamDaoUseCase constructor(
    private val repository: StockParamRepo
) {
    suspend operator fun invoke(value: String):Flow<Resource<List<StockParamObject?>>>{
        return repository.searchStockParam(value)
    }
}

class GetStockParamDaoUseCase constructor(
    private val repository: StockParamRepo
) {
    suspend operator fun invoke(stockCode: String):Flow<Resource<StockParamObject?>>{
        return repository.getStockParam(stockCode)
    }
}

class GetFastOrderListUseCase constructor(
    private val repository: FastOrderRepo
) : FlowUseCase<FastOrderListReq, FastOrderListResponse?>() {

    override suspend fun execute(parameters: FastOrderListReq?): Flow<Resource<FastOrderListResponse?>> {
        return repository.getFastOrderList(parameters!!)
    }
}

class GetListStockParamDaoUseCase constructor(
    private val repository: StockParamRepo
) {
    suspend operator fun invoke(value: List<String>):Flow<Resource<List<StockWithNotationObject?>>>{
        return repository.getListStockParam(value)
    }
}

class GetSessionPinDaoUseCase constructor(
    private val repository: AuthRepo
) {
    suspend operator fun invoke(value: String):Flow<Resource<Long>>{
        return repository.getSessionPin(value)
    }
}

class GetSessionPinOrderListDaoUseCase constructor(
    private val repository: AuthRepo
) {
    suspend operator fun invoke(value: String):Flow<Resource<Long>>{
        return repository.getSessionPin(value)
    }
}

class InsertSessionDaoUseCase constructor(
    private val repository: AuthRepo
) {
    suspend fun insertSessionDao(parameters: SessionObject?) {
        return repository.insertSession(parameters!!)
    }
}
class DeleteSessionsDaoUseCase constructor(
    private val repository: AuthRepo
) {
    suspend fun deleteSessionsDao() {
        return repository.deleteSessions()
    }
}

class UpdateSessionPinDaoUseCase constructor(
    private val repository: AuthRepo
) {
    suspend fun updateSessionPinDao(userId: String, sessionPin: String) {
        return repository.updateSessionPin(userId, sessionPin)
    }
}

class GetIndiceDataUseCase constructor(
    private val repository: GlobalMarketRepo
) : FlowUseCase<IndiceDataRequest, CurrentMessageResponse?>() {
    override suspend fun execute(parameters: IndiceDataRequest?): Flow<Resource<CurrentMessageResponse?>> {
        return repository.getIndiceData(parameters!!)
    }
}

class StartIndiceDataUseCase constructor(
    private val repository: GlobalMarketRepo
) {
    suspend fun startIndiceData() {
        repository.startIndiceData()
    }
}

class SetListenerIndiceDataUseCase constructor(
    private val repository: GlobalMarketRepo
) {
    suspend fun setListenerIndiceData(miListener: MQMessageListener<MIMessage>?) {
        repository.setListenerIndiceData(miListener)
    }
}

class SubscribeAllIndiceDataUseCase constructor(
    private val repository: GlobalMarketRepo
) {
    suspend fun subscribe(parameters: List<String>?) {
        repository.subscribeAllIndiceData(parameters!!)
    }
}

class SubscribeIndiceDataUseCase constructor(
    private val repository: GlobalMarketRepo
) {
    suspend fun subscribe(parameters: String?) {
        repository.subscribeIndiceData(parameters!!)
    }
}

class UnSubscribeAllIndiceDataUseCase constructor(
    private val repository: GlobalMarketRepo
) {
    suspend fun unSubscribe(parameters: List<String>?) {
        repository.unsubscribeAllIndiceData(parameters!!)
    }
}

class UnSubscribeIndiceDataUseCase constructor(
    private val repository: GlobalMarketRepo
) {
    suspend fun unSubscribe(parameters: String?) {
        repository.unSubscribeIndiceData(parameters!!)
    }
}

class StopIndiceDataUserCase constructor(
    private val repository: GlobalMarketRepo
) {
    suspend fun stopIndiceData() {
        repository.stopIndiceData()
    }
}

class GetIndexSectorUseCase constructor(
    private val repository: SectorRepo
) : FlowUseCase<IndexSectorRequest, List<ViewIndexSector?>>() {
    override suspend fun execute(parameters: IndexSectorRequest?): Flow<Resource<List<ViewIndexSector?>>> {
        return flow {
            repository.getIndexSector(parameters!!).collect() {resource ->
                when (resource) {
                    is Resource.Success -> {
                        when (parameters.type) {
                            // 0: all, 1: index, 2: sector
                            0 -> emit(Resource.Success(data = resource.data?.dataList, DataSource.REMOTE))
                            1 -> {
                                val data = resource.data?.dataList?.filter { !it.sector }
                                emit(Resource.Success(data = data, DataSource.REMOTE))
                            }
                            2 -> {
                                val data = resource.data?.dataList?.filter { it.sector }
                                emit(Resource.Success(data = data, DataSource.REMOTE))
                            }
                        }

                    }
                    else -> {}
                }
            }
        }
    }
}

class GetStockIndexSectorUseCase constructor(
    private val repository: SectorRepo
): FlowUseCase<StockIndexSectorRequest, StockIndexMappingByStockIndexResponse?>() {
    override suspend fun execute(parameters: StockIndexSectorRequest?): Flow<Resource<StockIndexMappingByStockIndexResponse?>> {
        return repository.getStockIndexSector(parameters!!)
    }
}

class GetStockRankInfoUseCase constructor(
    private val repository: CategoryRepo
): FlowUseCase<StockRankInfoRequest, StockRankingResponse?>() {
    override suspend fun execute(parameters: StockRankInfoRequest?): Flow<Resource<StockRankingResponse?>> {
        return repository.getStockRankInfo(parameters!!)
    }
}

class InsertStockNotationDaoUseCase constructor(
    private val repository: StockParamRepo
) {
    suspend fun insertStockNotationDao(parameters: StockNotationRes?) {
        return repository.insertStockNotation(parameters!!)
    }
}

class GetAllStockNotationDaoUseCase constructor(
    private val repository: StockParamRepo
) {
    suspend operator fun invoke():Flow<Resource<List<StockNotationObject>>>{
        return repository.getAllStockNotation()
    }
}

class InsertAllStockNotationDaoUseCase constructor(
    private val repository: StockParamRepo
) {
    suspend fun insertAllStockNotationDao(parameters: List<StockNotationObject>?): Flow<Resource<Boolean?>> {
        return repository.insertAllStockNotation(parameters!!)
    }
}

class GetNotationByStockCodeDaoUseCase constructor(
    private val repository: StockParamRepo
) {
    suspend operator fun invoke(value: String):Flow<Resource<List<StockNotationObject?>>>{
        return repository.getStockNotationByStockCode(value)
    }
}

class DeleteNotationByCodeDaoUseCase constructor(
    private val paramRepo: StockParamRepo
) {
    suspend fun delete(stockCodes: List<String>): Flow<Resource<Boolean?>> {
        return paramRepo.deleteStockNotationByCodes(stockCodes)
    }
}

class ClearStockNotationDB constructor(
    private val paramRepo: StockParamRepo
) {
    suspend fun clearStockNotationDB() {
        paramRepo.clearStockNotationDB()
    }
}

class GetNewsRssFeedUseCase constructor(
    private val repository: NewsRepo
): FlowUseCase<String, RssFeed?>() {
    override suspend fun execute(parameters: String?): Flow<Resource<RssFeed?>> {
        return repository.getNewsRssFeed(parameters!!)
    }
}


class GetStockInfoDetailUseCase constructor(
    private val repository: StockDetailRepo
): FlowUseCase<StockInfoDetailRequest, ViewStockInfoDetilResponse?>() {

    override suspend fun execute(parameters: StockInfoDetailRequest?): Flow<Resource<ViewStockInfoDetilResponse?>> {
        return repository.getStockInfoDetail(parameters!!)
    }
}

class GetAdvanceOrderInfoUseCase constructor(
    private val repository: OrderRepo
) : FlowUseCase<AdvanceOrderListRequest, AdvancedOrderInfoResponse?>() {
    override suspend fun execute(parameters: AdvanceOrderListRequest?): Flow<Resource<AdvancedOrderInfoResponse?>> {
        return repository.getAdvanceOrderInfo(parameters!!)
    }
}

class GetChartIntradayPriceUseCase constructor(
    private val repository: AnalysisRepo
) : FlowUseCase<ChartIntradayRequest, IntradayPriceResponse?>() {

    override suspend fun execute(parameters: ChartIntradayRequest?): Flow<Resource<IntradayPriceResponse?>> {
        return repository.getChartIntradayPrice(parameters!!)
    }
}

class GetRightIssueInfoUseCase constructor(
    private val repository: RightIssueRepo
) : FlowUseCase<RightIssueInfoReq, ExerciseInfoResponse?>() {

    override suspend fun execute(parameters: RightIssueInfoReq?): Flow<Resource<ExerciseInfoResponse?>> {
        return repository.getRightIssueInfo(parameters!!)
    }
}

class GetCalendarByDateInRangeUseCase constructor(
    private val repository: CalendarRepo
) : FlowUseCase<CaCalendarbyCaDateInRangeReq, CorporateActionCalendarGetResponse?>() {
    override suspend fun execute(parameters: CaCalendarbyCaDateInRangeReq?): Flow<Resource<CorporateActionCalendarGetResponse?>> {
        return repository.getCalendarByDateInRangeRpc(parameters!!)
    }
}

class GetTradeListUseCase constructor(
    private val repository: OrderRepo
) : FlowUseCase<TradeListReq, TradeListResponse?>() {
    override suspend fun execute(parameters: TradeListReq?): Flow<Resource<TradeListResponse?>> {
        return repository.getTradeList(parameters!!)
    }
}

class GetExerciseOrderListUseCase constructor(
    private val repo: RightIssueRepo
): FlowUseCase<ExerciseOrderListReq, ExerciseOrderListResponse?>() {
    override suspend fun execute(parameters: ExerciseOrderListReq?): Flow<Resource<ExerciseOrderListResponse?>> {
        return repo.getExerciseOrderList(parameters!!)
    }
}

class SendExerciseOrderUseCase constructor(
    private val repository: RightIssueRepo
) {
    suspend fun sendOrder(sendExerciseOrder: SendExerciseOrderRequest?) {
        repository.sendExerciseOrder(sendExerciseOrder)
    }
}

class SendWithdrawExerciseOrderUseCase constructor(
    private val repository: RightIssueRepo
) {
    suspend fun sendWithdraw(withdrawExerciseOrder: WithdrawExerciseOrderRequest?) {
        repository.sendWithdrawExerciseOrder(withdrawExerciseOrder)
    }
}

class InsertBiometricDaoUseCase constructor(
    private val repository: AuthRepo
) {
    suspend fun insertToken(parameters: BiometricObject?) {
        return repository.insertToken(parameters!!)
    }
}

class UpdateTokenBiometricDaoUseCase constructor(
    private val repository: AuthRepo
) {
    suspend fun updateToken(userId: String, token: String) {
        return repository.updateToken(userId, token)
    }
}

class GetBiometricDaoUseCase constructor(
    private val repository: AuthRepo
) {
    suspend operator fun invoke(userId: String):Flow<Resource<BiometricObject>>{
        return repository.getToken(userId)
    }
}

class DeleteBiometricDaoUseCase constructor(
    private val repository: AuthRepo
) {
    suspend fun deleteBiometricDao() {
        return repository.deleteBiometric()
    }
}

class DeleteTokenDaoUseCase constructor(
    private val repository: AuthRepo
) {
    suspend fun deleteTokenDao(userId: String) {
        return repository.deleteToken(userId)
    }
}
class GetMarketSessionUseCase constructor(
    private val repository: GlobalMarketRepo
) : FlowUseCase<MarketSessionReq, MarketSessionResponse?>() {
    override suspend fun execute(parameters: MarketSessionReq?): Flow<Resource<MarketSessionResponse?>> {
        return repository.getMarketSession(parameters!!)
    }
}
class GetGlobalCommoditiesUseCase constructor(
    private val repository: GlobalMarketRepo
) : FlowUseCase<GlobalMarketReq, LatestComoditiesResponse?>() {
    override suspend fun execute(parameters: GlobalMarketReq?): Flow<Resource<LatestComoditiesResponse?>> {
        return repository.getGlobalCommodities(parameters!!)
    }
}
class GetGlobalCurrencyUseCase constructor(
    private val repository: GlobalMarketRepo
) : FlowUseCase<GlobalMarketReq, LatestCurrencyResponse?>() {
    override suspend fun execute(parameters: GlobalMarketReq?): Flow<Resource<LatestCurrencyResponse?>> {
        return repository.getGlobalCurrency(parameters!!)
    }
}
class GetGlobalIndexUseCase constructor(
    private val repository: GlobalMarketRepo
) : FlowUseCase<GlobalMarketReq, LatestIndexResponse?>() {
    override suspend fun execute(parameters: GlobalMarketReq?): Flow<Resource<LatestIndexResponse?>> {
        return repository.getGlobalIndex(parameters!!)
    }
}

class GetWithdrawCashUseCase constructor(
    private val repository: ProfileRepo
): FlowUseCase<WithdrawCashReq, WithdrawCashResponse?>() {
    override suspend fun execute(parameters: WithdrawCashReq?): Flow<Resource<WithdrawCashResponse?>> {
        return repository.getWithdrawCash(parameters!!)
    }
}

class GetRdnHistoryUseCase constructor(
    private val repository: ProfileRepo
): FlowUseCase<RdnHistoryRequest, AccountCashMovementResponse?>() {
    override suspend fun execute(parameters: RdnHistoryRequest?): Flow<Resource<AccountCashMovementResponse?>> {
        return repository.getRdnHistory(parameters!!)
    }
}

class GetBannerLoginUseCase constructor(
    private val repository: NewsRepo
): FlowUseCase<Nothing?, LoginBannerResponse?>() {
    override suspend fun execute(parameters: Nothing?): Flow<Resource<LoginBannerResponse?>> {
        return repository.getNewsBannerLogin()
    }
}

class GetTradeListHistoryUseCase constructor(
    private val repository: OrderRepo
): FlowUseCase<TradeListHistoryReq, TradeListHistoryResponse?>() {
    override suspend fun execute(parameters: TradeListHistoryReq?): Flow<Resource<TradeListHistoryResponse?>> {
        return repository.getTradeListHistory(parameters!!)
    }
}

class GetValidateSessionUseCase constructor(
    private val repository: AuthRepo
) : FlowUseCase<ValidateSessionReq, ValidateSessionResponse?>() {
    override suspend fun execute(parameters: ValidateSessionReq?): Flow<Resource<ValidateSessionResponse?>> {
        return repository.validateSession(parameters!!)
    }
}

class SendPingUseCase @Inject constructor(
    private val repository: AuthRepo
) {
    suspend fun sendPing(userId: String) {
        repository.sendPing(userId)
    }
}

class StartAllConsumerUseCase @Inject constructor(
    private val repository: AuthRepo
): FlowUseCase<Boolean, Boolean>()  {
    override suspend fun execute(parameters: Boolean?): Flow<Resource<Boolean>> {
        return repository.startAllConsumer(parameters!!)
    }
}

class GetValidateSessionByPinUseCase constructor(
    private val repository: AuthRepo
) : FlowUseCase<ValidateSessionReq, ValidateSessionResponse?>() {
    override suspend fun execute(parameters: ValidateSessionReq?): Flow<Resource<ValidateSessionResponse?>> {
        return repository.validateSession(parameters!!)
    }
}

class GetResearchNewsUseCase constructor(
    private val repository: NewsRepo
): FlowUseCase<NewsResearchContentReq, NewsResearchContentResponse?>() {
    override suspend fun execute(parameters: NewsResearchContentReq?): Flow<Resource<NewsResearchContentResponse?>> {
        return repository.getResearchNews(parameters!!)
    }
}

class GetStockPickReportUseCase constructor(
    private val repository: NewsRepo
): FlowUseCase<StockPickReportReq, ViewStockPickResearchReportResponse?>() {
    override suspend fun execute(parameters: StockPickReportReq?): Flow<Resource<ViewStockPickResearchReportResponse?>> {
        return repository.getStockPickReport(parameters!!)
    }
}

class AddPriceAlertUseCase constructor(
    private val repository: PriceAlertRepo
): FlowUseCase<AddPriceAlertReq, AddPriceAlertResponse?>() {
    override suspend fun execute(parameters: AddPriceAlertReq?): Flow<Resource<AddPriceAlertResponse?>> {
        return repository.addPriceAlert(parameters!!)
    }
}

class GetListPriceAlertUseCase constructor(
    private val repository: PriceAlertRepo
): FlowUseCase<PriceAlertReq, ListPriceAlertResponse?>() {
    override suspend fun execute(parameters: PriceAlertReq?): Flow<Resource<ListPriceAlertResponse?>> {
        return repository.getListPriceAlert(parameters!!)
    }
}

class RemovePriceAlertUseCase constructor(
    private val repository: PriceAlertRepo
): FlowUseCase<RemovePriceAlertReq, RemovePriceAlertResponse?>() {
    override suspend fun execute(parameters: RemovePriceAlertReq?): Flow<Resource<RemovePriceAlertResponse?>> {
        return repository.removePriceAlert(parameters!!)
    }
}

class GetNotificationHistoryUseCase constructor(
    private val repository: NotificationRepo
) : FlowUseCase<NotificationHistoryReq, NotificationHistoryResponse?>() {
    override suspend fun execute(parameters: NotificationHistoryReq?): Flow<Resource<NotificationHistoryResponse?>> {
        return repository.getNotificationHistory(parameters!!)
    }
}

class GetGlobalRankUseCase constructor(
    private val repository: GlobalMarketRepo
): FlowUseCase<GlobalRankReq, BrokerRankActivityByInvType2DiscoverResponse?>() {
    override suspend fun execute(parameters: GlobalRankReq?): Flow<Resource<BrokerRankActivityByInvType2DiscoverResponse?>> {
        return repository.getGlobalRank(parameters!!)
    }
}

class GetTopFiveFaqUseCase constructor(
    private val repository: ProfileRepo
): FlowUseCase<HelpReq, GetNewsTopFiveFrequentAskedQuestionResponse?>() {
    override suspend fun execute(parameters: HelpReq?): Flow<Resource<GetNewsTopFiveFrequentAskedQuestionResponse?>> {
        return repository.getTopFiveFaq(parameters!!)
    }
}

class GetFaqByCategoryUseCase constructor(
    private val repository: ProfileRepo
): FlowUseCase<FaqReq, GetNewsFrequentAskedQuestionByCategoryResponse?>() {
    override suspend fun execute(parameters: FaqReq?): Flow<Resource<GetNewsFrequentAskedQuestionByCategoryResponse?>> {
        return repository.getFaqByCategory(parameters!!)
    }
}

class GetSearchFaqUseCase constructor(
    private val repository: ProfileRepo
): FlowUseCase<FaqReq, SearchNewsFrequentAskedQuestionResponse?>() {
    override suspend fun execute(parameters: FaqReq?): Flow<Resource<SearchNewsFrequentAskedQuestionResponse?>> {
        return repository.getSearchFaq(parameters!!)
    }
}

class GetFaqUseCase constructor(
    private val repository: ProfileRepo
): FlowUseCase<HelpReq, GetNewsFrequentAskedQuestionResponse?>() {
    override suspend fun execute(parameters: HelpReq?): Flow<Resource<GetNewsFrequentAskedQuestionResponse?>> {
        return repository.getFaq(parameters!!)
    }
}

class GetVideoTutorialUseCase constructor(
    private val repository: ProfileRepo
): FlowUseCase<HelpReq, GetNewsTutorialVideoResponse?>() {
    override suspend fun execute(parameters: HelpReq?): Flow<Resource<GetNewsTutorialVideoResponse?>> {
        return repository.getVideoTutorial(parameters!!)
    }
}

class GetIPOListUseCase constructor(
    private val repository: EIpoRepo
): FlowUseCase<IPOListRequest, PipelinesIpoListResponse?>() {
    override suspend fun execute(parameters: IPOListRequest?): Flow<Resource<PipelinesIpoListResponse?>> {
        return repository.getIPOList(parameters!!)
    }
}

class GetIPOInfoUseCase constructor(
    private val repository: EIpoRepo
): FlowUseCase<IPOInfoRequest, PipelinesIpoInfoResponse?>() {
    override suspend fun execute(parameters: IPOInfoRequest?): Flow<Resource<PipelinesIpoInfoResponse?>> {
        return repository.getIPOInfo(parameters!!)
    }
}

class GetIPOOrderListUseCase constructor(
    private val repository: EIpoRepo
): FlowUseCase<IPOOrderListRequest, IpoOrderListResponse?>() {
    override suspend fun execute(parameters: IPOOrderListRequest?): Flow<Resource<IpoOrderListResponse?>> {
        return repository.getIPOOrderList(parameters!!)
    }
}

class SendEipoOrderUseCase constructor(
    private val repository: OrderRepo
) {
    suspend fun sendOrder(eipoOrderRequest: EipoOrderRequest) {
        repository.sendEipoOrder(eipoOrderRequest)
    }
}


class GetNewsFeedUseCase constructor(
    private val repository: NewsRepo
): FlowUseCase<NewsFeedRequest, NewsInfoFeedResponse?>() {
    override suspend fun execute(parameters: NewsFeedRequest?): Flow<Resource<NewsInfoFeedResponse?>> {
        return repository.getNewsFeed(parameters!!)
    }
}

class GetNewsFeedByStockUseCase constructor(
    private val repository: NewsRepo
): FlowUseCase<NewsFeedByStockRequest, NewsInfoFeedSearchByStockResponse?>() {
    override suspend fun execute(parameters: NewsFeedByStockRequest?): Flow<Resource<NewsInfoFeedSearchByStockResponse?>> {
        return repository.getNewsFeedByStock(parameters!!)
    }
}

class GetNewsFeedSearchUseCase constructor(
    private val repository: NewsRepo
): FlowUseCase<NewsFeedSearchRequest, NewsInfoFeedSearchResponse?>() {
    override suspend fun execute(parameters: NewsFeedSearchRequest?): Flow<Resource<NewsInfoFeedSearchResponse?>> {
        return repository.getNewsFeedSearch(parameters!!)
    }
}

class GetResearchContentSearchUseCase constructor(
    private val repository: NewsRepo
): FlowUseCase<NewsFeedSearchRequest, SearchNewsResearchContentResponse?>() {
    override suspend fun execute(parameters: NewsFeedSearchRequest?): Flow<Resource<SearchNewsResearchContentResponse?>> {
        return repository.getResearchContentSearch(parameters!!)
    }
}

class SetDefaultFilterRunningTradeUseCase constructor(
    private val repository: RunningTradeRepo
){
    suspend fun setDefaultFilter(filter: FilterRunningTradeObject?){
        repository.setDefaultFilter(filter!!)
    }
}

class ResetDefaultFilterRunningTradeUseCase constructor(
    private val repository: RunningTradeRepo
){
    suspend fun resetDefaultFilter(parameters: String?){
        repository.resetDefaultFilter(parameters!!)
    }
}

class GetFilterRunningTradeUseCase constructor(
    private val repository: RunningTradeRepo
) {
    suspend operator fun invoke(parameters: String?):Flow<Resource<FilterRunningTradeObject>>{
        return repository.getDefaultFilter(parameters!!)
    }
}

class GetCorporateActionTabByStockCodeUseCase constructor(
    private val repository: NewsRepo
): FlowUseCase<CorporateActionTabRequest, CorporateActionCalendarGetByStockResponse?>() {
    override suspend fun execute(parameters: CorporateActionTabRequest?): Flow<Resource<CorporateActionCalendarGetByStockResponse?>> {
        return repository.getCorporateActionTabByStockCode(parameters!!)
    }
}

class GetTradeListHistoryGroupUseCase constructor(
    private val repository: OrderRepo
): FlowUseCase<TradeListHistoryReq, TradeListHistoryGroupResponse?>() {
    override suspend fun execute(parameters: TradeListHistoryReq?): Flow<Resource<TradeListHistoryGroupResponse?>> {
        return repository.getTradeListHistoryGroup(parameters!!)
    }
}

class GetTradeListHistoryGroupDetailUseCase constructor(
    private val repository: OrderRepo
): FlowUseCase<TradeListHistoryDetailReq, TradeListHistoryGroupDetailResponse?>() {
    override suspend fun execute(parameters: TradeListHistoryDetailReq?): Flow<Resource<TradeListHistoryGroupDetailResponse?>> {
        return repository.getTradeListHistoryGroupDetail(parameters!!)
    }
}

class GetIpAddressUseCase constructor(
    private val repository: AuthRepo
) {
    suspend operator fun invoke():Flow<Resource<String>>{
        return repository.getIpAddress()
    }
}

class GetRealizedGainLossByYearUseCase constructor(
    private val repository: PortfolioRepo
): FlowUseCase<RealizedGainLossYearRequest, RGainLossResponse?>() {
    override suspend fun execute(parameters: RealizedGainLossYearRequest?): Flow<Resource<RGainLossResponse?>> {
        return repository.getRealizedGainLossByYear(parameters!!)
    }
}

class GetRealizedGainLossByMonthUseCase constructor(
    private val repository: PortfolioRepo
): FlowUseCase<RealizedGainLossMonthRequest, RGainLossDtlResponse?>() {
    override suspend fun execute(parameters: RealizedGainLossMonthRequest?): Flow<Resource<RGainLossDtlResponse?>> {
        return repository.getRealizedGainLossByMonth(parameters!!)
    }
}

class GetTrustedDeviceUsecase constructor(
    private val repository: AuthRepo
) : FlowUseCase<TrustedDeviceReq, TrustedDeviceResponse?>() {
    override suspend fun execute(parameters: TrustedDeviceReq?): Flow<Resource<TrustedDeviceResponse?>> {
        return repository.getTrustedDevice(parameters!!)
    }
}

class SendOtpTrustedDeviceUsecase constructor(
    private val repository: AuthRepo
) : FlowUseCase<SendOtpTrustedDeviceRequest, SendOtpResponse?>() {
    override suspend fun execute(parameters: SendOtpTrustedDeviceRequest?): Flow<Resource<SendOtpResponse?>> {
        return repository.sendOtpTrustedDevice(parameters!!)
    }
}

class VerifyOtpTrustedDeviceUsecase constructor(
    private val repository: AuthRepo
) : FlowUseCase<VerifyOtpTrustedDeviceRequest, VerifyOtpResponse?>() {
    override suspend fun execute(parameters: VerifyOtpTrustedDeviceRequest?): Flow<Resource<VerifyOtpResponse?>> {
        return repository.verifyOtpTrustedDevice(parameters!!)
    }
}

class DeleteTrustedDeviceUsecase constructor(
    private val repository: AuthRepo
) : FlowUseCase<DeleteTrustedDeviceRequest, DeleteDeviceResponse?>() {
    override suspend fun execute(parameters: DeleteTrustedDeviceRequest?): Flow<Resource<DeleteDeviceResponse?>> {
        return repository.deleteTrustedDevice(parameters!!)
    }
}

class GetExerciseSessionUseCase constructor(
    private val repository: RightIssueRepo
) : FlowUseCase<ExerciseSessionReq, ExerciseSessionResponse?>() {

    override suspend fun execute(parameters: ExerciseSessionReq?): Flow<Resource<ExerciseSessionResponse?>> {
        return repository.getExerciseSession(parameters!!)
    }
}


class CloseChannelUseCase constructor(
    private val repository: AuthRepo
) {
    suspend fun close(){
        return repository.closeChannel()
    }
}