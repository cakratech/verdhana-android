package com.bcasekuritas.rabbitmq.network

import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.bcas.AccStockPosRequest
import com.bcasekuritas.rabbitmq.proto.bcas.AccStockPosResponse
import com.bcasekuritas.rabbitmq.proto.bcas.AccountCashMovementRequest
import com.bcasekuritas.rabbitmq.proto.bcas.AccountCashMovementResponse
import com.bcasekuritas.rabbitmq.proto.bcas.AddPriceAlertRequest
import com.bcasekuritas.rabbitmq.proto.bcas.AddPriceAlertResponse
import com.bcasekuritas.rabbitmq.proto.bcas.AddUserWatchListGroupRequest
import com.bcasekuritas.rabbitmq.proto.bcas.AddUserWatchListGroupResponse
import com.bcasekuritas.rabbitmq.proto.bcas.AddUserWatchListItemRequest
import com.bcasekuritas.rabbitmq.proto.bcas.AddUserWatchListItemResponse
import com.bcasekuritas.rabbitmq.proto.bcas.AdvancedOrderInfoRequest
import com.bcasekuritas.rabbitmq.proto.bcas.AdvancedOrderInfoResponse
import com.bcasekuritas.rabbitmq.proto.bcas.CIFCashPosRequest
import com.bcasekuritas.rabbitmq.proto.bcas.CIFCashPosResponse
import com.bcasekuritas.rabbitmq.proto.bcas.CakraHeartbeat
import com.bcasekuritas.rabbitmq.proto.bcas.CakraMessage
import com.bcasekuritas.rabbitmq.proto.bcas.ChangePasswordRequest
import com.bcasekuritas.rabbitmq.proto.bcas.ChangePasswordResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ChangePinRequest
import com.bcasekuritas.rabbitmq.proto.bcas.ChangePinResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ClientInfoRequest
import com.bcasekuritas.rabbitmq.proto.bcas.ClientInfoResponse
import com.bcasekuritas.rabbitmq.proto.bcas.DeleteDeviceRequest
import com.bcasekuritas.rabbitmq.proto.bcas.DeleteDeviceResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseInfoRequest
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseInfoResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseOrderListRequest
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseOrderListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseSessionRequest
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseSessionResponse
import com.bcasekuritas.rabbitmq.proto.bcas.FastOrderListRequest
import com.bcasekuritas.rabbitmq.proto.bcas.FastOrderListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ForgotPasswordPinRequest
import com.bcasekuritas.rabbitmq.proto.bcas.ForgotPasswordPinResponse
import com.bcasekuritas.rabbitmq.proto.bcas.IpoOrderListRequest
import com.bcasekuritas.rabbitmq.proto.bcas.IpoOrderListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ListPriceAlertRequest
import com.bcasekuritas.rabbitmq.proto.bcas.ListPriceAlertResponse
import com.bcasekuritas.rabbitmq.proto.bcas.LogonRequest
import com.bcasekuritas.rabbitmq.proto.bcas.LogonResponse
import com.bcasekuritas.rabbitmq.proto.bcas.LogoutRequest
import com.bcasekuritas.rabbitmq.proto.bcas.LogoutResponse
import com.bcasekuritas.rabbitmq.proto.bcas.MarketSessionRequest
import com.bcasekuritas.rabbitmq.proto.bcas.MarketSessionResponse
import com.bcasekuritas.rabbitmq.proto.bcas.MaxOrderByStockRequest
import com.bcasekuritas.rabbitmq.proto.bcas.MaxOrderByStockResponse
import com.bcasekuritas.rabbitmq.proto.bcas.NotificationHistoryRequest
import com.bcasekuritas.rabbitmq.proto.bcas.NotificationHistoryResponse
import com.bcasekuritas.rabbitmq.proto.bcas.OrderListHistoryRequest
import com.bcasekuritas.rabbitmq.proto.bcas.OrderListHistoryResponse
import com.bcasekuritas.rabbitmq.proto.bcas.OrderListRequest
import com.bcasekuritas.rabbitmq.proto.bcas.OrderListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.PipelinesIpoInfoRequest
import com.bcasekuritas.rabbitmq.proto.bcas.PipelinesIpoInfoResponse
import com.bcasekuritas.rabbitmq.proto.bcas.PipelinesIpoListRequest
import com.bcasekuritas.rabbitmq.proto.bcas.PipelinesIpoListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.PipelinesIpoOrderListRequest
import com.bcasekuritas.rabbitmq.proto.bcas.PipelinesIpoOrderListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RGainLossDtlRequest
import com.bcasekuritas.rabbitmq.proto.bcas.RGainLossDtlResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RGainLossRequest
import com.bcasekuritas.rabbitmq.proto.bcas.RGainLossResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RemovePriceAlertRequest
import com.bcasekuritas.rabbitmq.proto.bcas.RemovePriceAlertResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RemoveUserWatchListGroupRequest
import com.bcasekuritas.rabbitmq.proto.bcas.RemoveUserWatchListGroupResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RemoveUserWatchListItemRequest
import com.bcasekuritas.rabbitmq.proto.bcas.RemoveUserWatchListItemResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ResetPasswordRequest
import com.bcasekuritas.rabbitmq.proto.bcas.ResetPasswordResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ResetPinRequest
import com.bcasekuritas.rabbitmq.proto.bcas.ResetPinResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SaveDeviceTokenRequest
import com.bcasekuritas.rabbitmq.proto.bcas.SaveDeviceTokenResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SendOtpRequest
import com.bcasekuritas.rabbitmq.proto.bcas.SendOtpResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SettlementScheduleRequest
import com.bcasekuritas.rabbitmq.proto.bcas.SettlementScheduleResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleAccountInfoByCIFRequest
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleAccountInfoByCIFResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleAllUserWatchListRequest
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleAllUserWatchListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimplePortofolioRequest
import com.bcasekuritas.rabbitmq.proto.bcas.SimplePortofolioResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleUserWatchListRequest
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleUserWatchListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.StockParamRequest
import com.bcasekuritas.rabbitmq.proto.bcas.StockParamResponse
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListHistoryGroupDetailRequest
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListHistoryGroupDetailResponse
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListHistoryGroupRequest
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListHistoryGroupResponse
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListHistoryRequest
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListHistoryResponse
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListRequest
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.TrustedDeviceRequest
import com.bcasekuritas.rabbitmq.proto.bcas.TrustedDeviceResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ValidatePinRequest
import com.bcasekuritas.rabbitmq.proto.bcas.ValidatePinResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ValidateSessionRequest
import com.bcasekuritas.rabbitmq.proto.bcas.ValidateSessionResponse
import com.bcasekuritas.rabbitmq.proto.bcas.VerifyOtpRequest
import com.bcasekuritas.rabbitmq.proto.bcas.VerifyOtpResponse
import com.bcasekuritas.rabbitmq.proto.bcas.WithdrawCashRequest
import com.bcasekuritas.rabbitmq.proto.bcas.WithdrawCashResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerListRequest
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerListResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankActivityByInvType2DiscoverRequest
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankActivityByInvType2DiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankActivityDiscoverRequest
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankActivityDiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankByStockDiscoverRequest
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankByStockDiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankByStockNetDiscoverRequest
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankByStockNetDiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankingDiscoverRequest
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankingDiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerStockSummaryRequest
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerStockSummaryResponse
import com.bcasekuritas.rabbitmq.proto.chart.Cf
import com.bcasekuritas.rabbitmq.proto.chart.Cf.CFMessage.IntradayPriceRequest
import com.bcasekuritas.rabbitmq.proto.chart.Cf.CFMessage.IntradayPriceResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.CurrentMessageRequest
import com.bcasekuritas.rabbitmq.proto.datafeed.CurrentMessageResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.LatestTradeDetailRequest
import com.bcasekuritas.rabbitmq.proto.datafeed.LatestTradeDetailResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import com.bcasekuritas.rabbitmq.proto.datafeed.StockRankingRequest
import com.bcasekuritas.rabbitmq.proto.datafeed.StockRankingResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.StockTradeRequest
import com.bcasekuritas.rabbitmq.proto.datafeed.StockTradeResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.TradeBookRequest
import com.bcasekuritas.rabbitmq.proto.datafeed.TradeBookResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.TradeBookTimeRequest
import com.bcasekuritas.rabbitmq.proto.datafeed.TradeBookTimeResponse
import com.bcasekuritas.rabbitmq.proto.news.CorporateActionCalendarGetByStockRequest
import com.bcasekuritas.rabbitmq.proto.news.CorporateActionCalendarGetByStockResponse
import com.bcasekuritas.rabbitmq.proto.news.CorporateActionCalendarGetRequest
import com.bcasekuritas.rabbitmq.proto.news.CorporateActionCalendarGetResponse
import com.bcasekuritas.rabbitmq.proto.news.EarningsPerShareRequest
import com.bcasekuritas.rabbitmq.proto.news.EarningsPerShareResponse
import com.bcasekuritas.rabbitmq.proto.news.FibonacciPivotPointRequest
import com.bcasekuritas.rabbitmq.proto.news.FibonacciPivotPointResponse
import com.bcasekuritas.rabbitmq.proto.news.FinancialBalanceSheetRequest
import com.bcasekuritas.rabbitmq.proto.news.FinancialBalanceSheetResponse
import com.bcasekuritas.rabbitmq.proto.news.FinancialCashFlowRequest
import com.bcasekuritas.rabbitmq.proto.news.FinancialCashFlowResponse
import com.bcasekuritas.rabbitmq.proto.news.FinancialIncomeStatementRequest
import com.bcasekuritas.rabbitmq.proto.news.FinancialIncomeStatementResponse
import com.bcasekuritas.rabbitmq.proto.news.GetNewsFrequentAskedQuestionByCategoryRequest
import com.bcasekuritas.rabbitmq.proto.news.GetNewsFrequentAskedQuestionByCategoryResponse
import com.bcasekuritas.rabbitmq.proto.news.GetNewsFrequentAskedQuestionRequest
import com.bcasekuritas.rabbitmq.proto.news.GetNewsFrequentAskedQuestionResponse
import com.bcasekuritas.rabbitmq.proto.news.GetNewsTopFiveFrequentAskedQuestionRequest
import com.bcasekuritas.rabbitmq.proto.news.GetNewsTopFiveFrequentAskedQuestionResponse
import com.bcasekuritas.rabbitmq.proto.news.GetNewsTutorialVideoRequest
import com.bcasekuritas.rabbitmq.proto.news.GetNewsTutorialVideoResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPbvBandRequest
import com.bcasekuritas.rabbitmq.proto.news.GetPbvBandResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPbvDataRequest
import com.bcasekuritas.rabbitmq.proto.news.GetPbvDataResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPerBandRequest
import com.bcasekuritas.rabbitmq.proto.news.GetPerBandResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPerDataRequest
import com.bcasekuritas.rabbitmq.proto.news.GetPerDataResponse
import com.bcasekuritas.rabbitmq.proto.news.LatestComoditiesRequest
import com.bcasekuritas.rabbitmq.proto.news.LatestComoditiesResponse
import com.bcasekuritas.rabbitmq.proto.news.LatestCurrencyRequest
import com.bcasekuritas.rabbitmq.proto.news.LatestCurrencyResponse
import com.bcasekuritas.rabbitmq.proto.news.LatestIndexRequest
import com.bcasekuritas.rabbitmq.proto.news.LatestIndexResponse
import com.bcasekuritas.rabbitmq.proto.news.LoginBannerRequest
import com.bcasekuritas.rabbitmq.proto.news.LoginBannerResponse
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoFeedRequest
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoFeedResponse
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoFeedSearchByStockRequest
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoFeedSearchByStockResponse
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoFeedSearchRequest
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoFeedSearchResponse
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoPromoRequest
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoResponse
import com.bcasekuritas.rabbitmq.proto.news.NewsResearchContentRequest
import com.bcasekuritas.rabbitmq.proto.news.NewsResearchContentResponse
import com.bcasekuritas.rabbitmq.proto.news.NewsStockPickSingleRequest
import com.bcasekuritas.rabbitmq.proto.news.NewsStockPickSingleResponse
import com.bcasekuritas.rabbitmq.proto.news.PromotionBannerRequest
import com.bcasekuritas.rabbitmq.proto.news.PromotionBannerResponse
import com.bcasekuritas.rabbitmq.proto.news.SearchNewsFrequentAskedQuestionRequest
import com.bcasekuritas.rabbitmq.proto.news.SearchNewsFrequentAskedQuestionResponse
import com.bcasekuritas.rabbitmq.proto.news.SearchNewsResearchContentRequest
import com.bcasekuritas.rabbitmq.proto.news.SearchNewsResearchContentResponse
import com.bcasekuritas.rabbitmq.proto.news.StockAnalysisRatingRequest
import com.bcasekuritas.rabbitmq.proto.news.StockAnalysisRatingResponse
import com.bcasekuritas.rabbitmq.proto.news.StockDetilCompanyProfileRequest
import com.bcasekuritas.rabbitmq.proto.news.StockDetilCompanyProfileResponse
import com.bcasekuritas.rabbitmq.proto.news.StockIndexMappingByStockIndexRequest
import com.bcasekuritas.rabbitmq.proto.news.StockIndexMappingByStockIndexResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewBalanceSheetRequest
import com.bcasekuritas.rabbitmq.proto.news.ViewBalanceSheetResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewCashFlowRequest
import com.bcasekuritas.rabbitmq.proto.news.ViewCashFlowResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewIncomeStatementRequest
import com.bcasekuritas.rabbitmq.proto.news.ViewIncomeStatementResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewIndexSectorRequest
import com.bcasekuritas.rabbitmq.proto.news.ViewIndexSectorResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewKeyStatRequest
import com.bcasekuritas.rabbitmq.proto.news.ViewKeyStatResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewKeyStatsRTIRequest
import com.bcasekuritas.rabbitmq.proto.news.ViewKeyStatsRTIResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewStockInfoDetilRequest
import com.bcasekuritas.rabbitmq.proto.news.ViewStockInfoDetilResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewStockPickResearchReportRequest
import com.bcasekuritas.rabbitmq.proto.news.ViewStockPickResearchReportResponse

interface OLTService {
    suspend fun logon(logonRequest: LogonRequest?): LogonResponse?
    suspend fun logout(logoutRequest: LogoutRequest): LogoutResponse?
    suspend fun getStockPosition(accStockPosReq: AccStockPosRequest?): AccStockPosResponse?
    suspend fun getCIFPos(cifCashPosRequest: CIFCashPosRequest?): CIFCashPosResponse?
    suspend fun getStockTrade(stockTradeRequest: StockTradeRequest?): StockTradeResponse?
    suspend fun getCurrentMessage(currentMessageRequest: CurrentMessageRequest?): CurrentMessageResponse?
    suspend fun getLatestTradeDetail(latestTradeDetailRequest: LatestTradeDetailRequest?): LatestTradeDetailResponse?
    suspend fun getTradeBook(tradeBookRequest: TradeBookRequest?): TradeBookResponse?
    suspend fun getTradeBookTime(tradeBookTimeRequest: TradeBookTimeRequest?): TradeBookTimeResponse?
    suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest?): ResetPasswordResponse?
    suspend fun forgotPasswordPin(forgotPasswordPinRequest: ForgotPasswordPinRequest?): ForgotPasswordPinResponse?

    /** Current Message Rpc*/
    suspend fun getTradeSummaryCurrentMessage(currentMessageRequest: CurrentMessageRequest?): CurrentMessageResponse?
    suspend fun getOrderBookSummaryCurrentMessage(currentMessageRequest: CurrentMessageRequest?): CurrentMessageResponse?
    suspend fun getIndiceSummaryCurrentMessage(currentMessageRequest: CurrentMessageRequest?): CurrentMessageResponse?


    /** Watchlist Rpc*/
    suspend fun getUserWatchList(simpleUserWatchListRequest: SimpleUserWatchListRequest): SimpleUserWatchListResponse?
    suspend fun getAllUserWatchlist(simpleAllUserWatchListRequest: SimpleAllUserWatchListRequest): SimpleAllUserWatchListResponse?
    suspend fun addWatchListCategory(addUserWatchListGroupRequest: AddUserWatchListGroupRequest): AddUserWatchListGroupResponse?
    suspend fun addItemCategory(addUserWatchListItemRequest: AddUserWatchListItemRequest): AddUserWatchListItemResponse?
    suspend fun removeWatchListCategory(addUserWatchListGroupRequest: RemoveUserWatchListGroupRequest): RemoveUserWatchListGroupResponse?
    suspend fun removeItemCategory(addUserWatchListGroupRequest: RemoveUserWatchListItemRequest): RemoveUserWatchListItemResponse?

    suspend fun getChangePin(changePINRequest: ChangePinRequest?): ChangePinResponse?
    suspend fun saveDeviceToken(saveDeviceTokenRequest: SaveDeviceTokenRequest?): SaveDeviceTokenResponse?
    suspend fun getResetPin(resetPINRequest: ResetPinRequest?): ResetPinResponse?
    suspend fun getChangePassword(changePasswordRequest: ChangePasswordRequest?): ChangePasswordResponse?
    suspend fun getOrderListInfo(orderListRequest: OrderListRequest?): OrderListResponse?
    suspend fun getStockParamList(stockParamRequest: StockParamRequest?): StockParamResponse?
    suspend fun getTradeListInfo(tradeListRequest: TradeListRequest?): TradeListResponse?
    suspend fun getSettlementSchedule(settlementScheduleRequest: SettlementScheduleRequest?): SettlementScheduleResponse?
    suspend fun getAccountInfo(accountRequest: ClientInfoRequest?): ClientInfoResponse?
    suspend fun getCashWithdraw(cashWithdrawRequest: WithdrawCashRequest?): WithdrawCashResponse?
    suspend fun getOrderInfoLIst(orderListRequest: OrderListRequest?): OrderListResponse?
    suspend fun getFastOrderList(fastOrderListRequest: FastOrderListRequest?): FastOrderListResponse?
//    fun getBrokerRanking(brokerRankingRequest: BrokerRankingRequest?): BrokerRankingResponse?
//    fun sendRequestOnlineWithdraw(accOnlineCashWithdrawalRequest: AccOnlineCashWithdrawalRequest?): AccOnlineCashWithdrawalResponse?

//    fun getBrokerAnalyzer(brokerAnalyzerRequest: BrokerAnalyzerRequest?): BrokerAnalyzerResponse?
//    fun getStockRanking(stockRankingRequest: StockRankingRequest?): StockRankingResponse?
    suspend fun getTradeSummary(tradeSummaryRequest: Cf.CFMessage.TradeSummaryRequest?): Cf.CFMessage.TradeSummaryResponse?
    suspend fun getValidatePin(validatePinRequest: ValidatePinRequest): ValidatePinResponse?
//    suspend fun getNewsInfo(newsInfoRequest: NewsInfoRequest?): NewsInfoResponse?
    suspend fun getCompanyProfile(companyProfileRequest: StockDetilCompanyProfileRequest?): StockDetilCompanyProfileResponse?
//    fun getFinanceAnalysis(financialAnalysisRequest: FinancialAnalysisRequest?): FinancialAnalysisResponse?
//    fun getCorporateAction(corporateActionRequest: CorporateActionRequest?): CorporateActionResponse?
    suspend fun getHistoryOrder(orderHistoryRequest: OrderListHistoryRequest): OrderListHistoryResponse?
    suspend fun getHistoryTrade(tradeListHistoryRequest: TradeListHistoryRequest?): TradeListHistoryResponse?

    suspend fun getIndexSector(indexSectorRequest: ViewIndexSectorRequest): ViewIndexSectorResponse?

    suspend fun getStockIndexSector(stockIndexSectorRequest: StockIndexMappingByStockIndexRequest): StockIndexMappingByStockIndexResponse?
    suspend fun getMaxOrderByStock(maxOrderByStockRequest: MaxOrderByStockRequest?): MaxOrderByStockResponse?

    suspend fun getNewsPromo(newsInfoPromoRequest: NewsInfoPromoRequest): NewsInfoResponse?
    suspend fun getPromoBanner(promotionBannerRequest: PromotionBannerRequest): PromotionBannerResponse?
    suspend fun getStockPick(stockPickRequest: NewsStockPickSingleRequest): NewsStockPickSingleResponse?

    suspend fun getNewsBannerLogin(loginBannerRequest: LoginBannerRequest): LoginBannerResponse?

    suspend fun getStockRankInfo(stockRankingRequest: StockRankingRequest): StockRankingResponse?

    suspend fun getStockInfoDetail(viewStockInfoDetilRequest: ViewStockInfoDetilRequest): ViewStockInfoDetilResponse?

    suspend fun getAdvanceOrderInfo(advancedOrderInfoRequest: AdvancedOrderInfoRequest): AdvancedOrderInfoResponse?

    suspend fun getChartIntradayPrice(intradayPriceRequest: IntradayPriceRequest): IntradayPriceResponse?

    suspend fun getRightIssueInfo(exerciseInfoRequest: ExerciseInfoRequest): ExerciseInfoResponse?

    suspend fun getTradeList(tradeListRequest: TradeListRequest): TradeListResponse?

    suspend fun getExerciseOrderList(exerciseOrderListRequest: ExerciseOrderListRequest): ExerciseOrderListResponse?
    suspend fun getCalendarByDateInRangeRpc(caCalendarbyCaDateInRangeRequest: CorporateActionCalendarGetRequest): CorporateActionCalendarGetResponse?

    suspend fun getMarketSession(marketSessionRequest: MarketSessionRequest): MarketSessionResponse?

    suspend fun getRdnHistory(cashMovementRequest: AccountCashMovementRequest): AccountCashMovementResponse?
    suspend fun getTradeListHistory(tradeListHistoryRequest: TradeListHistoryRequest): TradeListHistoryResponse?
    suspend fun getValidateSession(validateSessionRequest: ValidateSessionRequest): ValidateSessionResponse?
    suspend fun getResearchNews(newsResearchContentRequest: NewsResearchContentRequest): NewsResearchContentResponse?
    suspend fun getStockPickReport(stockPickResearchReportRequest: ViewStockPickResearchReportRequest): ViewStockPickResearchReportResponse?
    suspend fun getCorporateActionCalendarByStockCode(corporateActionCalendarGetByStockRequest: CorporateActionCalendarGetByStockRequest): CorporateActionCalendarGetByStockResponse?
    suspend fun getExerciseSession(exerciseSessionRequest: ExerciseSessionRequest): ExerciseSessionResponse?

    /** Live Data TradeHistoryGroup */
    suspend fun getTradeHistoryGroup(tradeListHistoryGroupRequest: TradeListHistoryGroupRequest): TradeListHistoryGroupResponse?
    suspend fun getTradeHistoryGroupDetail(tradeListHistoryGroupDetailRequest: TradeListHistoryGroupDetailRequest): TradeListHistoryGroupDetailResponse?

    /** Live Data TradeBook */
//    suspend fun startTradeBook()
//    suspend fun setListenerTradeBook(miListener: MQMessageListener<MIMessage?>?)
//    suspend fun subscribeTradeBook(routingKey: String?)
//    suspend fun unsubscribeTradeBook(stockCode: String?)
//    suspend fun stopTradeBook()


    /** Live Data RunningTrade */
    suspend fun startRunningTrade()
    suspend fun setListenerRunningTrade(miListener: MQMessageListener<MIMessage>?)
    suspend fun subscribeRunningTrade(routingKey: String?)
    suspend fun unsubscribeRunningTrade(routingKey: String?)
    suspend fun stopRunningTrade()

    /** Live Data AppNotification */
    suspend fun startAppNotification(cakraListener: MQMessageListener<CakraMessage>?)
    suspend fun startNewAppNotification(appNotificationFlow: (CakraMessage) -> Unit)
    suspend fun unsubscribeAppNotification(routingKey: String?)
    suspend fun subscribeAppNotification(routingKey: String?)
    suspend fun stopAppNotification()
    suspend fun getNotificationHistory(notificationHistoryRequest: NotificationHistoryRequest): NotificationHistoryResponse?

    /** Live Data StockParam */
//    fun startStockParam(oltListener: MQMessageListener<CakraMessage?>?, sessionId: String?)
//    fun unsubscribeStockParam(sessionId: String?)
//    fun stopStockParam(routingKey: String?)

    /** Live Data CIF StockPos*/
    suspend fun startCIFStockPos()
    suspend fun setListenerCIFStockPos(mqMessageListener: MQMessageListener<CakraMessage>?)
    suspend fun subscribeCIFStockPos(routingKey: String?)
    suspend fun unSubscribeCIFStockPos(routingKey: String)
    suspend fun stopCifStockPos()

    /** Live Data OrderBook */
    suspend fun startOrderBook()
    suspend fun setListenerOrderBook(mqMessageListener: MQMessageListener<MIMessage>?)
    suspend fun subscribeOrderBook(routingKey: String?)
    suspend fun unsubscribeOrderBook(routingKey: String?)
    suspend fun stopOrderBook()

    /** Live Data LastPrice */
//    suspend fun startLatestPrice(miListener: MQMessageListener<MIMessage?>?)
//    suspend fun subscribeLatestPrice(routingKey: String?)
//    suspend fun unsubscribeLatestPrice(routingKey: String?)
//    suspend fun stopLatestPrice()

    /** Live Data OrderReplay By UserId */
    suspend fun startConsumeOrderReply(accNo: String?, oltListener: MQMessageListener<CakraMessage>?)
    suspend fun startNewOrderReply(orderReplyFlow: (CakraMessage) -> Unit)
    suspend fun subscribeOrderReply(accNo: String?)
    suspend fun unsubscribeOrderReply(accNo: String?)
    suspend fun stopConsumeOrderReply()

    /** Live Data RunningTrade By ExchangeKey */
//    fun startConsumeOrderReply(
//        exchangeKey: ExchangeKey?,
//        oltListener: MQMessageListener<CakraMessage?>?
//    )
//    fun stopConsumeOrderReply(exchangeKey: ExchangeKey?)

    /** Live Data AcquireConn */
    suspend fun startAllConsumer(isFirstLaunch: Boolean): Boolean

    /** Live Data IndiceData */
    suspend fun startIndiceData()
    suspend fun setListenerIndiceData(miListener: MQMessageListener<MIMessage>?)
    suspend fun subscribeAllIndiceData(routingKeys: List<String>)
    suspend fun subscribeIndiceData(routingKey: String?)
    suspend fun unsubscribeAllIndiceData(routingKeys: List<String>)
    suspend fun unsubscribeIndiceData(routingKey: String?)
    suspend fun stopIndiceData()

    /** Live Data Trade Summary */
    suspend fun startTradeSum()
    suspend fun setListenerTradeSum(mqMessageListener: MQMessageListener<MIMessage>?)
    suspend fun subscribeAllTradeSum(routingKeys: List<String>)
    suspend fun subscribeTradeSum(routingKey: String?)
    suspend fun unsubscribeAllTradeSum(routingKeys: List<String>)
    suspend fun unsubscribeTradeSum(routingKey: String?)
    suspend fun stopTradeSum()


    suspend fun startPublishOrder()
    suspend fun sendOrder(cakraMessage: CakraMessage?)
    suspend fun startPublishSubsInfo()
    suspend fun publishSubsInfo(cakraMessage: CakraMessage?)


    /** Live Data Key Stat */
    suspend fun getKeyStat(keyStatRequest: ViewKeyStatRequest?): ViewKeyStatResponse?
    suspend fun getKeyStatsRti(viewKeyStatsRTIRequest: ViewKeyStatsRTIRequest?): ViewKeyStatsRTIResponse?
    suspend fun getEarningPerShares(earningsPerShareRequest: EarningsPerShareRequest?): EarningsPerShareResponse?

    /** Financial */
    suspend fun getIncomeStatement(incomeStatementRequest: ViewIncomeStatementRequest?): ViewIncomeStatementResponse?
    suspend fun getBalanceSheet(balanceSheetRequest: ViewBalanceSheetRequest?): ViewBalanceSheetResponse?
    suspend fun getCashFlow(cashFlowRequest: ViewCashFlowRequest?): ViewCashFlowResponse?
    suspend fun getDetailIncomeStatement(financiaIncomeStatementRequest: FinancialIncomeStatementRequest?): FinancialIncomeStatementResponse?
    suspend fun getDetailBalanceSheet(financialBalanceSheetRequest: FinancialBalanceSheetRequest?): FinancialBalanceSheetResponse?
    suspend fun getDetailCashFlow(financialCashFlowRequest: FinancialCashFlowRequest?): FinancialCashFlowResponse?

    /** Analysis */
    suspend fun getPerBand(getPerBandRequest: GetPerBandRequest?): GetPerBandResponse?
    suspend fun getPerData(getPerDataRequest: GetPerDataRequest?): GetPerDataResponse?
    suspend fun getPbvBand(getPbvBandRequest: GetPbvBandRequest?): GetPbvBandResponse?
    suspend fun getPbvData(getPbvDataRequest: GetPbvDataRequest?): GetPbvDataResponse?
    suspend fun getStockAnalysisRating(stockAnalysisRatingRequest: StockAnalysisRatingRequest?): StockAnalysisRatingResponse?
    suspend fun getFibonacciPivotPoint(fibonacciPivotPointRequest: FibonacciPivotPointRequest?): FibonacciPivotPointResponse?

    /** Broker Summary */
    suspend fun getBrokerStockSummary(brokerStockSummaryRequest: BrokerStockSummaryRequest?): BrokerStockSummaryResponse?
    suspend fun getBrokerRankByStock(brokerRankByStockDiscoverRequest: BrokerRankByStockDiscoverRequest?): BrokerRankByStockDiscoverResponse?
    suspend fun getBrokerList(brokerListRequest: BrokerListRequest): BrokerListResponse?
    suspend fun getBrokerRankActivity(brokerRankActivityDiscoverRequest: BrokerRankActivityDiscoverRequest): BrokerRankActivityDiscoverResponse?
    suspend fun getBrokerRankRanking(brokerRankingDiscoverRequest: BrokerRankingDiscoverRequest): BrokerRankingDiscoverResponse?
    suspend fun getBrokerSummaryByStockNet(brokerRankByStockNetDiscoverRequest: BrokerRankByStockNetDiscoverRequest): BrokerRankByStockNetDiscoverResponse?

    /** Simple*/
    suspend fun getSimpleAccountInfo(simpleAccountInfoByCIFRequest: SimpleAccountInfoByCIFRequest): SimpleAccountInfoByCIFResponse?
    suspend fun getSimplePortfolio(simplePortofolioRequest: SimplePortofolioRequest): SimplePortofolioResponse?

    /** RDN */
    suspend fun getWithdrawCash(withdrawCashRequest: WithdrawCashRequest): WithdrawCashResponse?
    suspend fun startPublishPing()
    suspend fun sendPing(cakraHeartbeat: CakraHeartbeat)

    /** GLOBAL MARKET */
    suspend fun getGlobalCommodities(latestComoditiesRequest: LatestComoditiesRequest): LatestComoditiesResponse?
    suspend fun getGlobalCurrency(latestCurrencyRequest: LatestCurrencyRequest): LatestCurrencyResponse?
    suspend fun getGlobalIndex(latestIndexRequest: LatestIndexRequest): LatestIndexResponse?
    suspend fun getGlobalRank(brokerRankActivityByInvType2DiscoverRequest: BrokerRankActivityByInvType2DiscoverRequest): BrokerRankActivityByInvType2DiscoverResponse?

    /** PRICE ALERT */
    suspend fun addPriceAlert(addPriceAlertRequest: AddPriceAlertRequest): AddPriceAlertResponse?
    suspend fun getListPriceAlert(listPriceAlertRequest: ListPriceAlertRequest): ListPriceAlertResponse?
    suspend fun removePriceAlert(removePriceAlertRequest: RemovePriceAlertRequest): RemovePriceAlertResponse?

    /** HELP */
    suspend fun getTopFiveFaq(getNewsTopFiveFrequentAskedQuestionRequest: GetNewsTopFiveFrequentAskedQuestionRequest): GetNewsTopFiveFrequentAskedQuestionResponse?
    suspend fun getFaqByCategory(getNewsFrequentAskedQuestionByCategoryRequest: GetNewsFrequentAskedQuestionByCategoryRequest): GetNewsFrequentAskedQuestionByCategoryResponse?
    suspend fun getFaq(getNewsFrequentAskedQuestionRequest: GetNewsFrequentAskedQuestionRequest): GetNewsFrequentAskedQuestionResponse?
    suspend fun getSearchFaq(searchNewsFrequentAskedQuestionRequest: SearchNewsFrequentAskedQuestionRequest): SearchNewsFrequentAskedQuestionResponse?
    suspend fun getHelpTutorialVideo(getNewsTutorialVideoRequest: GetNewsTutorialVideoRequest): GetNewsTutorialVideoResponse?

    /** EIPO */
    suspend fun getEIPOList(pipelinesIpoListRequest: PipelinesIpoListRequest): PipelinesIpoListResponse?
    suspend fun getEIPOInfo(pipelinesIpoInfoRequest: PipelinesIpoInfoRequest): PipelinesIpoInfoResponse?
    suspend fun getEIPOOrderList(ipoOrderListRequest: IpoOrderListRequest): IpoOrderListResponse?
    suspend fun getEIPOOrderInfo(pipelinesIpoOrderListRequest: PipelinesIpoOrderListRequest): PipelinesIpoOrderListResponse?

    /** NEWS */
    suspend fun getNewsFeed(newsInfoFeedRequest: NewsInfoFeedRequest): NewsInfoFeedResponse?
    suspend fun getNewsFeedByStock(newsInfoFeedSearchByStockRequest: NewsInfoFeedSearchByStockRequest): NewsInfoFeedSearchByStockResponse?
    suspend fun getNewsFeedSearch(newsInfoFeedSearchRequest: NewsInfoFeedSearchRequest): NewsInfoFeedSearchResponse?
    suspend fun getResearchContentSearch(searchNewsResearchContentRequest: SearchNewsResearchContentRequest): SearchNewsResearchContentResponse?

    suspend fun getRealizedGainLossByYear(rGainLossRequest: RGainLossRequest): RGainLossResponse?
    suspend fun getRealizedGainLossByMonth(rGainLossDtlRequest: RGainLossDtlRequest): RGainLossDtlResponse?

    /** Manage Device Rpc*/
    suspend fun sendOtpTrustedDevice(sendOtpRequest: SendOtpRequest): SendOtpResponse?
    suspend fun verifyOtpTrustedDevice(verifyOtpRequest: VerifyOtpRequest): VerifyOtpResponse?
    suspend fun getTrustedDevice(trustedDeviceRequest: TrustedDeviceRequest): TrustedDeviceResponse?
    suspend fun deleteTrustedDevice(deleteDeviceRequest: DeleteDeviceRequest): DeleteDeviceResponse?

    suspend fun closeChannel()
}