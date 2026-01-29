package com.bcasekuritas.rabbitmq.network

import com.bcasekuritas.rabbitmq.MQModel
import com.bcasekuritas.rabbitmq.common.Util
import com.bcasekuritas.rabbitmq.connection.BasicMQConnection
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.constant.ConstantKeys
import com.bcasekuritas.rabbitmq.consumer.MQConsumer
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.pool.ChannelPooled
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
import com.bcasekuritas.rabbitmq.proto.datafeed.MIType
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
import com.bcasekuritas.rabbitmq.publisher.MQPublisher
import com.bcasekuritas.rabbitmq.rpc.MQClientRPC
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

class OLTServiceImpl(
    private val channelPooled: ChannelPooled?,
    private val connection: BasicMQConnection?,
    private val imqConnectionListener: IMQConnectionListener,
    private var consumerOLT: MQConsumer<CakraMessage> = MQConsumer(),
    private var consumerAppNotification: MQConsumer<CakraMessage> = MQConsumer(),
    private var consumerRunningTrade: MQConsumer<MIMessage> = MQConsumer(),
    private var consumerCIFStockPos: MQConsumer<CakraMessage> = MQConsumer(),
    private var consumerOrderBook: MQConsumer<MIMessage> = MQConsumer(),
    private var consumerIndiceData: MQConsumer<MIMessage> = MQConsumer(),
    private var consumerTradeSummary: MQConsumer<MIMessage> = MQConsumer(),
    private var mqPublisherOrder: MQPublisher = MQPublisher(),
    private var mqPublisherSubsInfo: MQPublisher = MQPublisher(),
    private var mqPublisherPing: MQPublisher = MQPublisher(),
) : OLTService {
    private val singleThread = Executors.newFixedThreadPool(1).asCoroutineDispatcher()

    override suspend fun logon(logonRequest: LogonRequest?): LogonResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var logonResponse: LogonResponse? = null
            try {
                val mqClientRPCLogon = MQClientRPC<LogonRequest?, LogonResponse>(
                    channelPooled,
                    ConstantKeys.LOGIN_RPC, LogonResponse::class.java
                )
                Timber.i("start call LogonRequest")
                logonResponse = mqClientRPCLogon.doCall(logonRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    logonResponse?.status?.let {
                        if (it == 2) {
                            imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                        } else if (it == 3) {
                            imqConnectionListener.isPinExpiredLiveData.postValue(true)
                        }
                    }
                    if (logonResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("LogonRequest")
                    }
                }
            } catch (e: Exception) {
                Timber.tag("logon").e(e)
            }
            logonResponse
        }
    }

    override suspend fun logout(logoutRequest: LogoutRequest): LogoutResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var logoutResponse: LogoutResponse? = null
            try {
                val mqClientRPC = MQClientRPC<LogoutRequest, LogoutResponse>(
                    channelPooled,
                    ConstantKeys.LOG_OUT_RPC,
                    LogoutResponse::class.java
                )
                Timber.i("start call LogoutRequest")
                logoutResponse = mqClientRPC.doCall(logoutRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    logoutResponse?.status?.let {
                        if (it == 2) {
                            imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                        } else if (it == 3) {
                            imqConnectionListener.isPinExpiredLiveData.postValue(true)
                        }
                    }
                    if (logoutResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("LogoutRequest")
                    }
                }
            } catch (e:Exception) {
                Timber.tag("logout").e(e)
            }
            logoutResponse
        }
    }

    override suspend fun getAccountInfo(accountRequest: ClientInfoRequest?): ClientInfoResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var clientInfoResponse: ClientInfoResponse? = null
            try {
                val mqClientRPC = MQClientRPC<ClientInfoRequest?, ClientInfoResponse>(
                    channelPooled,
                    ConstantKeys.ACCOUNT_INFO_RPC, ClientInfoResponse::class.java
                )
                Timber.i("start call AccountInfoRequest")
                clientInfoResponse = mqClientRPC.doCall(accountRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    clientInfoResponse?.status?.let {
                        if (it == 2) {
                            imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                        } else if (it == 3) {
                            imqConnectionListener.isPinExpiredLiveData.postValue(true)
                        }
                    }
                    if (clientInfoResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("AccountInfoRequest")
                    }
                }
            } catch (e: Exception) {
                Timber.tag("getAccountInfo").e(e)
            }
            clientInfoResponse
        }
    }

    override suspend fun getCIFPos(cifCashPosRequest: CIFCashPosRequest?): CIFCashPosResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var cifCashPosResponse: CIFCashPosResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                    val mqCifCasPos = MQClientRPC<CIFCashPosRequest?, CIFCashPosResponse>(
                        channelPooled,
                        ConstantKeys.CIF_LEVEL_POS_RPC, CIFCashPosResponse::class.java
                    )
                    Timber.i("start call CIFPosRequest")
                    cifCashPosResponse = mqCifCasPos.doCall(cifCashPosRequest, 10000, imqConnectionListener)

                    launch(Dispatchers.Main) {
                        cifCashPosResponse?.status?.let {
                            if (it == 2) {
                                imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                            } else if (it == 3) {
                                imqConnectionListener.isPinExpiredLiveData.postValue(true)
                            }
                        }
                        if (cifCashPosResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                            imqConnectionListener.timeOutLiveData.postValue("CIFPosRequest")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            cifCashPosResponse
        }

    }

    override suspend fun getStockPosition(accStockPosReq: AccStockPosRequest?): AccStockPosResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var accStockPosResponse: AccStockPosResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPCAccStockPos = MQClientRPC<AccStockPosRequest?, AccStockPosResponse>(
                    channelPooled,
                    ConstantKeys.ACC_STOCK_POS_RPC, AccStockPosResponse::class.java
                )
                Timber.i("start call AccStockPosRequest")
                accStockPosResponse = mqClientRPCAccStockPos.doCall(accStockPosReq, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    accStockPosResponse?.status?.let {
                        if (it == 2) {
                            imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                        } else if (it == 3) {
                            Timber.tag("pinexp").d("stockpos")
                            imqConnectionListener.isPinExpiredLiveData.postValue(true)
                        }
                    }
                    if (accStockPosResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("AccStockPosRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("getStockPosition").e(e)
            }
            accStockPosResponse
        }

    }

//    override fun logout(logoutRequest: LogoutRequest?): LogoutResponse? {
//        var logoutResponse: LogoutResponse? = null
//        try {
//            val mqClientRPCLogout = MQClientRPC<LogoutRequest?, LogoutResponse>(
//                channelPooled,
//                Util.generateRPCName(
//                    "olt",
//                    CakraMessage.Type.LOGOUT_REQUEST
//                ), LogoutResponse::class.java
//            )
//            Timber.i("start call LogoutRequest")
//            logoutResponse = mqClientRPCLogout.doCall(logoutRequest, 30000)
//        
//                if (companyProfileResponse.status.equals("2")) {
//                    imqConnectionListener.isSessionExpiredLiveData.postValue(true)
//                } else if (companyProfileResponse.status.equals("3")) {
//                    imqConnectionListener.isPinExpiredLiveData.postValue(true)
//                }
//            } catch (e: Exception) {
//            Timber.tag("").e(e)
//        }
//        return logoutResponse
//    }
//
//    override fun getSimpleAccStockPositions(simpleAccStockPosRequest: SimpleAccStockPosRequest?): SimpleAccStockPosResponse? {
//        var simpleAccStockPosResponse: SimpleAccStockPosResponse? = null
//        try {
//            val accStockPosReq = SimpleAccStockPosRequest
//                .newBuilder()
//                .setUserId(simpleAccStockPosRequest.userId)
//                .setAccNo(simpleAccStockPosRequest.accNo)
//                .setStockCode(simpleAccStockPosRequest.stockCode)
//                .setSessionId(simpleAccStockPosRequest.sessionId)
//                .build()
//            val mqClientRPCAccStockPos =
//                MQClientRPC<SimpleAccStockPosRequest, SimpleAccStockPosResponse>(
//                    channelPooled,
//                    Util.generateRPCName(
//                        "olt",
//                        CakraMessage.Type.SIMPLE_ACC_STOCK_POS_REQUEST
//                    ), SimpleAccStockPosResponse::class.java
//                )
//            simpleAccStockPosResponse = mqClientRPCAccStockPos.doCall(accStockPosReq, 10000, imqConnectionListener)
//        
//                if (companyProfileResponse.status.equals("2")) {
//                    imqConnectionListener.isSessionExpiredLiveData.postValue(true)
//                } else if (companyProfileResponse.status.equals("3")) {
//                    imqConnectionListener.isPinExpiredLiveData.postValue(true)
//                }
//            } catch (e: Exception) {
//            Timber.tag("").e(e)
//        }
//        return simpleAccStockPosResponse
//    }

//    override fun getStockPositions(
//        userName: String?,
//        sessionId: String?,
//        accNo: String?
//    ): AccStockPosResponse? {
//        var accStockPosResponse: AccStockPosResponse? = null
//        try {
//            val accStockPosReq = AccStockPosRequest
//                .newBuilder()
//                .setUserId(userName)
//                .setSessionId(sessionId)
//                .setAccNo(accNo)
//                .setStockCode("*") //All stocks
//                .build()
//            val mqClientRPCAccStockPos = MQClientRPC<AccStockPosRequest, AccStockPosResponse>(
//                channelPooled,
//                Util.generateRPCName(
//                    "olt",
//                    CakraMessage.Type.ACC_STOCK_POS_REQUEST
//                ), AccStockPosResponse::class.java
//            )
//            Timber.i("start call AccStockPosRequest")
//            accStockPosResponse = mqClientRPCAccStockPos.doCall(accStockPosReq, 10000, imqConnectionListener)
//        
//                if (companyProfileResponse.status.equals("2")) {
//                    imqConnectionListener.isSessionExpiredLiveData.postValue(true)
//                } else if (companyProfileResponse.status.equals("3")) {
//                    imqConnectionListener.isPinExpiredLiveData.postValue(true)
//                }
//            } catch (e: Exception) {
//            Timber.tag("").e(e)
//        }
//        return accStockPosResponse
//    }

    override suspend fun getStockTrade(stockTradeRequest: StockTradeRequest?): StockTradeResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var stockTradeResponse: StockTradeResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientStockTrade = MQClientRPC<StockTradeRequest?, StockTradeResponse>(
                    channelPooled, ConstantKeys.STOCK_TRADE_RPC, StockTradeResponse::class.java
                )
                Timber.i("start call StockTradeRequest")
                stockTradeResponse = mqClientStockTrade.doCall(stockTradeRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    if (stockTradeResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("StockTradeRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("getStockTrade").e(e)
            }
            stockTradeResponse
        }
    }


//    override fun getCashPositions(accCashPosReq: AccCashPosRequest?): AccCashPosResponse? {
//        var accCashPosResponse: AccCashPosResponse? = null
//        try {
//            val mqClientRPCAccCashPos = MQClientRPC<AccCashPosRequest?, AccCashPosResponse>(
//                channelPooled,
//                Util.generateRPCName(
//                    "olt",
//                    CakraMessage.Type.ACC_CASH_POS_REQUEST
//                ), AccCashPosResponse::class.java
//            )
//            Timber.i("start call AccCashPosRequest")
//            accCashPosResponse = mqClientRPCAccCashPos.doCall(accCashPosReq, 10000, imqConnectionListener)
//        
//                if (companyProfileResponse.status.equals("2")) {
//                    imqConnectionListener.isSessionExpiredLiveData.postValue(true)
//                } else if (companyProfileResponse.status.equals("3")) {
//                    imqConnectionListener.isPinExpiredLiveData.postValue(true)
//                }
//            } catch (e: Exception) {
//            Timber.tag("").e(e)
//        }
//        return accCashPosResponse
//    }
//
//    override fun getCashPosition(accCashPosReq: AccCashPosRequest?): AccCashPosResponse? {
//        var accCashPosResponse: AccCashPosResponse? = null
//        try {
//            val mqClientRPCAccCashPos = MQClientRPC<AccCashPosRequest?, AccCashPosResponse>(
//                channelPooled,
//                Util.generateRPCName(
//                    "olt",
//                    CakraMessage.Type.ACC_CASH_POS_REQUEST
//                ), AccCashPosResponse::class.java
//            )
//            accCashPosResponse = mqClientRPCAccCashPos.doCall(accCashPosReq, 10000, imqConnectionListener)
//        
//                if (companyProfileResponse.status.equals("2")) {
//                    imqConnectionListener.isSessionExpiredLiveData.postValue(true)
//                } else if (companyProfileResponse.status.equals("3")) {
//                    imqConnectionListener.isPinExpiredLiveData.postValue(true)
//                }
//            } catch (e: Exception) {
//            Timber.tag("").e(e)
//        }
//        return accCashPosResponse
//    }

//    override fun getStockPosition(accStockPosReq: com.bcasekuritas.rabbitmq.proto.bcas.AccStockPosRequest?): com.bcasekuritas.rabbitmq.proto.bcas.AccStockPosResponse? {
//        var accStockPosResponse: com.bcasekuritas.rabbitmq.proto.bcas.AccStockPosResponse? = null
//        try {
//            val mqClientRPCAccStockPos =
//                MQClientRPC<com.bcasekuritas.rabbitmq.proto.bcas.AccStockPosRequest?, com.bcasekuritas.rabbitmq.proto.bcas.AccStockPosResponse>(
//                    channelPooled,
//                    "cakra.acc_stock_pos_request-rpc",
//                    com.bcasekuritas.rabbitmq.proto.bcas.AccStockPosResponse::class.java
//                )
//
////            Timber.i("start call AccStockPosRequest");
//            accStockPosResponse = mqClientRPCAccStockPos.doCall(accStockPosReq, 10000, imqConnectionListener)
//        
//                if (companyProfileResponse.status.equals("2")) {
//                    imqConnectionListener.isSessionExpiredLiveData.postValue(true)
//                } else if (companyProfileResponse.status.equals("3")) {
//                    imqConnectionListener.isPinExpiredLiveData.postValue(true)
//                }
//            } catch (e: Exception) {
//            Timber.tag("").e(e)
//        }
//        return accStockPosResponse
//    }

//    override fun getCashPositions(userName: String?, sessionId: String?): AccCashPosResponse? {
//        var accCashPosResponse: AccCashPosResponse? = null
//        try {
//            val accCashPosReq = AccCashPosRequest
//                .newBuilder()
//                .setUserId(userName)
//                .setSessionId(sessionId)
//                .setAccNo("*")
//                .build()
//            val mqClientRPCAccCashPos = MQClientRPC<AccCashPosRequest, AccCashPosResponse>(
//                channelPooled,
//                Util.generateRPCName(
//                    "olt",
//                    CakraMessage.Type.ACC_CASH_POS_REQUEST
//                ), AccCashPosResponse::class.java
//            )
//            Timber.i("start call AccCashPosRequest")
//            accCashPosResponse = mqClientRPCAccCashPos.doCall(accCashPosReq, 10000, imqConnectionListener)
//        
//                if (companyProfileResponse.status.equals("2")) {
//                    imqConnectionListener.isSessionExpiredLiveData.postValue(true)
//                } else if (companyProfileResponse.status.equals("3")) {
//                    imqConnectionListener.isPinExpiredLiveData.postValue(true)
//                }
//            } catch (e: Exception) {
//            Timber.tag("").e(e)
//        }
//        return accCashPosResponse
//    }

    override suspend fun getTradeSummaryCurrentMessage(currentMessageRequest: CurrentMessageRequest?): CurrentMessageResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var currentMessageResponse: CurrentMessageResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                    val mqClientRPCLogon = MQClientRPC<CurrentMessageRequest?, CurrentMessageResponse>(
                        channelPooled,
                        ConstantKeys.TRADE_SUMMARY_CURRENT_MESSAGE_RPC, CurrentMessageResponse::class.java
                    )
                    Timber.i("start call CurrentMessageRequest")
                    currentMessageResponse = mqClientRPCLogon.doCall(currentMessageRequest, 10000, imqConnectionListener)

                    launch(Dispatchers.Main) {
                        if (currentMessageResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                            imqConnectionListener.timeOutLiveData.postValue("TradeSumCurrMessageRequest")
                        }
                    }}
            } catch (e: Exception) {
                Timber.tag("getCurrentMessage").e(e)
            }
            currentMessageResponse
        }
    }

    override suspend fun getOrderBookSummaryCurrentMessage(currentMessageRequest: CurrentMessageRequest?): CurrentMessageResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var currentMessageResponse: CurrentMessageResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                    val mqClientRPCLogon = MQClientRPC<CurrentMessageRequest?, CurrentMessageResponse>(
                        channelPooled,
                        ConstantKeys.ORDER_BOOK_SUMMARY_CURRENT_MESSAGE_RPC, CurrentMessageResponse::class.java
                    )
                    Timber.i("start call CurrentMessageRequest")
                    currentMessageResponse = mqClientRPCLogon.doCall(currentMessageRequest, 10000, imqConnectionListener)

                    launch(Dispatchers.Main) {
                        if (currentMessageResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                            imqConnectionListener.timeOutLiveData.postValue("OrderBookCurrMessageRequest")
                        }
                    }}
            } catch (e: Exception) {
                Timber.tag("getCurrentMessage").e(e)
            }
            currentMessageResponse
        }
    }

    override suspend fun getIndiceSummaryCurrentMessage(currentMessageRequest: CurrentMessageRequest?): CurrentMessageResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var currentMessageResponse: CurrentMessageResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                    val mqClientRPCLogon = MQClientRPC<CurrentMessageRequest?, CurrentMessageResponse>(
                        channelPooled,
                        ConstantKeys.INDICE_SUMMARY_CURRENT_MESSAGE_RPC, CurrentMessageResponse::class.java
                    )
                    Timber.i("start call CurrentMessageRequest")
                    currentMessageResponse = mqClientRPCLogon.doCall(currentMessageRequest, 10000, imqConnectionListener)

                    launch(Dispatchers.Main) {
                        if (currentMessageResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                            imqConnectionListener.timeOutLiveData.postValue("IndiceCurrMessageRequest")
                        }
                    }}
            } catch (e: Exception) {
                Timber.tag("getCurrentMessage").e(e)
            }
            currentMessageResponse
        }
    }

    override suspend fun getCurrentMessage(currentMessageRequest: CurrentMessageRequest?): CurrentMessageResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var currentMessageResponse: CurrentMessageResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPCLogon = MQClientRPC<CurrentMessageRequest?, CurrentMessageResponse>(
                    channelPooled,
                    Util.generateRPCName(
                        "mi",
                        MIType.CURRENT_MESSAGE_REQUEST
                    ), CurrentMessageResponse::class.java
                )
                Timber.i("start call CurrentMessageRequest")
                currentMessageResponse = mqClientRPCLogon.doCall(currentMessageRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    if (currentMessageResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("CurrentMessageRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("getCurrentMessage").e(e)
            }
            currentMessageResponse
        }
    }

    //
    override suspend fun getLatestTradeDetail(latestTradeDetailRequest: LatestTradeDetailRequest?): LatestTradeDetailResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var latestTradeDetailResponse: LatestTradeDetailResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val rpcName = Util.generateRPCName("mi", MIType.LATEST_TRADE_DETAIL_REQUEST)
                val mqClientRPCLogon =
                    MQClientRPC<LatestTradeDetailRequest?, LatestTradeDetailResponse>(
                        channelPooled,
                        rpcName,
                        LatestTradeDetailResponse::class.java
                    )

                Timber.i("start call LatestTradeDetailRequest")
                latestTradeDetailResponse = mqClientRPCLogon.doCall(latestTradeDetailRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    if (latestTradeDetailResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("LatestTradeDetailRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("getLatestTradeDetail").e(e)
            }

            latestTradeDetailResponse
        }

    }

    override suspend fun getTradeBook(tradeBookRequest: TradeBookRequest?): TradeBookResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var tradeBookResponse: TradeBookResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPCLogon = MQClientRPC<TradeBookRequest?, TradeBookResponse>(
                    channelPooled, ConstantKeys.TRADE_BOOK_RPC, TradeBookResponse::class.java
                )
                Timber.i("start call TradeBookRequest")
                tradeBookResponse = mqClientRPCLogon.doCall(tradeBookRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    if (tradeBookResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("TradeBookRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("getTradeBook").e(e)
            }

            tradeBookResponse
        }
    }

    override suspend fun getTradeBookTime(tradeBookTimeRequest: TradeBookTimeRequest?): TradeBookTimeResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var tradeBookTimeResponse: TradeBookTimeResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPCLogon = MQClientRPC<TradeBookTimeRequest?, TradeBookTimeResponse>(
                    channelPooled, ConstantKeys.TRADE_BOOK_TIME_RPC, TradeBookTimeResponse::class.java
                )
                Timber.i("start call TradeBookRequest")
                tradeBookTimeResponse = mqClientRPCLogon.doCall(tradeBookTimeRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    if (tradeBookTimeResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("TradeBookTimeRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("getTradeBookTime").e(e)
            }

            tradeBookTimeResponse
        }
    }

    override suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest?): ResetPasswordResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var resetPasswordResponse: ResetPasswordResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPCResetPwd = MQClientRPC<ResetPasswordRequest?, ResetPasswordResponse>(
                    channelPooled,
                    "cakra.reset_password_request-rpc", ResetPasswordResponse::class.java
                )
                Timber.i("start call ResetPasswordRequest")
                resetPasswordResponse = mqClientRPCResetPwd.doCall(resetPasswordRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    resetPasswordResponse?.status?.let {
                        if (it == 2) {
                            imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                        } else if (it == 3) {
                            imqConnectionListener.isPinExpiredLiveData.postValue(true)
                        }
                    }
                    if (resetPasswordResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("ResetPasswordRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("resetPassword").e(e)
            }
            resetPasswordResponse
        }
    }

    override suspend fun forgotPasswordPin(forgotPasswordPinRequest: ForgotPasswordPinRequest?): ForgotPasswordPinResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var resetPinResponse: ForgotPasswordPinResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPCResetPwd =
                    MQClientRPC<ForgotPasswordPinRequest?, ForgotPasswordPinResponse>(
                        channelPooled,
                        "cakra.forgot_password_pin_request-rpc",
                        ForgotPasswordPinResponse::class.java
                    )
                Timber.i("start call ForgotPasswordPinRequest")
                resetPinResponse = mqClientRPCResetPwd.doCall(forgotPasswordPinRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    resetPinResponse?.status?.let {
                        if (it == 2) {
                            imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                        } else if (it == 3) {
                            imqConnectionListener.isPinExpiredLiveData.postValue(true)
                        }
                    }
                    if (resetPinResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("ForgotPasswordPinRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("forgotPasswordPin").e(e)
            }
            resetPinResponse
        }
    }

//    override fun getRegenerateToken(regenerateTokenRequest: RegenerateTokenRequest?): RegenerateTokenResponse? {
//        var regenerateTokenResponse: RegenerateTokenResponse? = null
//        try {
//            val mqClientRPC = MQClientRPC<RegenerateTokenRequest?, RegenerateTokenResponse>(
//                channelPooled,
//                Util.generateRPCName("olt", CakraMessage.Type.REGENERATE_TOKEN_REQUEST),
//                RegenerateTokenResponse::class.java
//            )
//            regenerateTokenResponse = mqClientRPC.doCall(regenerateTokenRequest, 30000)
//        
//                if (companyProfileResponse.status.equals("2")) {
//                    imqConnectionListener.isSessionExpiredLiveData.postValue(true)
//                } else if (companyProfileResponse.status.equals("3")) {
//                    imqConnectionListener.isPinExpiredLiveData.postValue(true)
//                }
//            } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return regenerateTokenResponse
//    }
//
//    override fun validateTokenResponse(validateTokenRequest: ValidateTokenRequest?): ValidateTokenResponse? {
//        var validateTokenResponse: ValidateTokenResponse? = null
//        try {
//            val mqClientRPCValildateToken =
//                MQClientRPC<ValidateTokenRequest?, ValidateTokenResponse>(
//                    channelPooled,
//                    Util.generateRPCName(
//                        "olt",
//                        CakraMessage.Type.VALIDATE_TOKEN_REQUEST
//                    ), ValidateTokenResponse::class.java
//                )
//            validateTokenResponse = mqClientRPCValildateToken.doCall(validateTokenRequest, 10000, imqConnectionListener)
//            //            Timber.i("end call PINValidationRequest");
//        
//                if (companyProfileResponse.status.equals("2")) {
//                    imqConnectionListener.isSessionExpiredLiveData.postValue(true)
//                } else if (companyProfileResponse.status.equals("3")) {
//                    imqConnectionListener.isPinExpiredLiveData.postValue(true)
//                }
//            } catch (e: Exception) {
//            Timber.tag("").e(e)
//        }
//        return validateTokenResponse
//    }

    override suspend fun getChangePin(changePINRequest: ChangePinRequest?): ChangePinResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var changePINResponse: ChangePinResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<ChangePinRequest?, ChangePinResponse>(
                    channelPooled, ConstantKeys.CHANGE_PIN_RPC, ChangePinResponse::class.java
                )
                Timber.i("start call ChangePinRequest")
                changePINResponse = mqClientRPC.doCall(changePINRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    changePINResponse?.status?.let {
                        if (it == 2) {
                            imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                        } else if (it == 3) {
                            imqConnectionListener.isPinExpiredLiveData.postValue(true)
                        }
                    }
                    if (changePINResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("ChangePinRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("getChangePin").e(e)
            }
            changePINResponse
        }
    }

    override suspend fun saveDeviceToken(saveDeviceTokenRequest: SaveDeviceTokenRequest?): SaveDeviceTokenResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var saveDeviceTokenResponse: SaveDeviceTokenResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<SaveDeviceTokenRequest?, SaveDeviceTokenResponse>(
                    channelPooled, ConstantKeys.SAVE_DEVICE_TOKEN_RPC, SaveDeviceTokenResponse::class.java
                )
                Timber.i("start call SaveDeviceTokenRequest")
                saveDeviceTokenResponse = mqClientRPC.doCall(saveDeviceTokenRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    saveDeviceTokenResponse?.status?.let {
                        if (it == 2) {
                            imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                        } else if (it == 3) {
                            imqConnectionListener.isPinExpiredLiveData.postValue(true)
                        }
                    }
                    if (saveDeviceTokenResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("SaveDeviceTokenRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("saveDeviceToken").e(e)
            }
            saveDeviceTokenResponse
        }
    }

    override suspend fun getResetPin(resetPINRequest: ResetPinRequest?): ResetPinResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var resetPINResponse: ResetPinResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<ResetPinRequest?, ResetPinResponse>(
                    channelPooled, "cakra.reset_pin_request-rpc", ResetPinResponse::class.java
                )
                Timber.i("start call ResetPinRequest")
                resetPINResponse = mqClientRPC.doCall(resetPINRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    resetPINResponse?.status?.let {
                        if (it == 2) {
                            imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                        } else if (it == 3) {
                            imqConnectionListener.isPinExpiredLiveData.postValue(true)
                        }
                    }
                    if (resetPINResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("ResetPinRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("getResetPin").e(e)
            }
            resetPINResponse
        }
    }

    override suspend fun getChangePassword(changePasswordRequest: ChangePasswordRequest?): ChangePasswordResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var changePasswordResponse: ChangePasswordResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<ChangePasswordRequest?, ChangePasswordResponse>(
                    channelPooled,
                    ConstantKeys.CHANGE_PASSWORD_RPC, ChangePasswordResponse::class.java
                )
                Timber.i("start call ChangePasswordRequest")
                changePasswordResponse = mqClientRPC.doCall(changePasswordRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    changePasswordResponse?.status?.let {
                        if (it == 2) {
                            imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                        } else if (it == 3) {
                            imqConnectionListener.isPinExpiredLiveData.postValue(true)
                        }
                    }
                    if (changePasswordResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("ChangePasswordRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("getChangePassword").e(e)
            }
            changePasswordResponse
        }
    }

    override suspend fun getUserWatchList(simpleUserWatchListRequest: SimpleUserWatchListRequest): SimpleUserWatchListResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var orderListResponse: SimpleUserWatchListResponse? = null
            try {

                if(channelPooled?.connection?.isOpen == true){val mqClientRPC =
                    MQClientRPC<SimpleUserWatchListRequest?, SimpleUserWatchListResponse>(
                        channelPooled,
                        ConstantKeys.GET_USER_WATCHLIST_RPC, SimpleUserWatchListResponse::class.java
                    )
                    Timber.i("start call SimpleUserWatchListRequest")
                    orderListResponse = mqClientRPC.doCall(simpleUserWatchListRequest, 10000, imqConnectionListener)

                    launch(Dispatchers.Main) {
                        orderListResponse?.status?.let {
                            if (it == 2) {
                                imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                            } else if (it == 3) {
                                imqConnectionListener.isPinExpiredLiveData.postValue(true)
                            }
                        }
                        if (orderListResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                            imqConnectionListener.timeOutLiveData.postValue("SimpleUserWatchListRequest")
                        }
                    }
                }

            } catch (e: Exception) {
                Timber.tag("getUserWatchList").e(e)
            }
            orderListResponse
        }
    }

    override suspend fun getAllUserWatchlist(simpleAllUserWatchListRequest: SimpleAllUserWatchListRequest): SimpleAllUserWatchListResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var allWatchlistResponse: SimpleAllUserWatchListResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                    val mqClientRPC =
                        MQClientRPC<SimpleAllUserWatchListRequest?, SimpleAllUserWatchListResponse>(
                            channelPooled,
                            ConstantKeys.GET_ALL_USER_WATCHLIST,
                            SimpleAllUserWatchListResponse::class.java
                        )
                    Timber.i("start call SimpleAllUserWatchListRequest")
                    allWatchlistResponse = mqClientRPC.doCall(simpleAllUserWatchListRequest, 10000, imqConnectionListener)

                    launch(Dispatchers.Main) {
                        allWatchlistResponse?.status?.let {
                            if (it == 2) {
                                imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                            } else if (it == 3) {
                                imqConnectionListener.isPinExpiredLiveData.postValue(true)
                            }
                        }
                        if (allWatchlistResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                            imqConnectionListener.timeOutLiveData.postValue(
                                "SimpleAllUserWatchListRequest")
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.tag("getAllUserWatchlist").e(e)
            }
            allWatchlistResponse
        }
    }

    override suspend fun addWatchListCategory(addUserWatchListGroupRequest: AddUserWatchListGroupRequest): AddUserWatchListGroupResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var addUserWatchListGroupResponse: AddUserWatchListGroupResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                    val mqClientRPC =
                    MQClientRPC<AddUserWatchListGroupRequest?, AddUserWatchListGroupResponse>(
                        channelPooled,
                        ConstantKeys.ADD_WATCHLIST_CATEGORY_RPC,
                        AddUserWatchListGroupResponse::class.java
                    )
                    Timber.i("start call AddUserWatchListGroupRequest")
                    addUserWatchListGroupResponse =
                        mqClientRPC.doCall(addUserWatchListGroupRequest, 10000, imqConnectionListener)

                    launch(Dispatchers.Main) {
                        addUserWatchListGroupResponse?.status?.let {
                            if (it == 2) {
                                imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                            } else if (it == 3) {
                                imqConnectionListener.isPinExpiredLiveData.postValue(true)
                            }
                        }
                        if (addUserWatchListGroupResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                            imqConnectionListener.timeOutLiveData.postValue("AddUserWatchListGroupRequest")
                        }
                    }}

            } catch (e: Exception) {
                Timber.tag("addWatchListCategory").e(e)
            }
            addUserWatchListGroupResponse
        }
    }

    override suspend fun addItemCategory(addUserWatchListItemRequest: AddUserWatchListItemRequest): AddUserWatchListItemResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var addUserWatchListItemResponse: AddUserWatchListItemResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC =
                    MQClientRPC<AddUserWatchListItemRequest?, AddUserWatchListItemResponse>(
                        channelPooled,
                        ConstantKeys.ADD_ITEM_CATEGORY_RPC, AddUserWatchListItemResponse::class.java
                    )
                Timber.i("start call AddUserWatchListItemRequest")
                addUserWatchListItemResponse =
                    mqClientRPC.doCall(addUserWatchListItemRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    addUserWatchListItemResponse?.status?.let {
                        if (it == 2) {
                            imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                        } else if (it == 3) {
                            imqConnectionListener.isPinExpiredLiveData.postValue(true)
                        }
                    }
                    if (addUserWatchListItemResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("AddUserWatchListItemRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("addItemCategory").e(e)
            }
            addUserWatchListItemResponse
        }
    }

    override suspend fun removeWatchListCategory(addUserWatchListGroupRequest: RemoveUserWatchListGroupRequest): RemoveUserWatchListGroupResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var removeUserWatchListGroupResponse: RemoveUserWatchListGroupResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC =
                    MQClientRPC<RemoveUserWatchListGroupRequest?, RemoveUserWatchListGroupResponse>(
                        channelPooled,
                        ConstantKeys.REMOVE_WATCHLIST_CATEGORY_RPC,
                        RemoveUserWatchListGroupResponse::class.java
                    )
                Timber.i("start call RemoveUserWatchListGroupRequest")
                removeUserWatchListGroupResponse =
                    mqClientRPC.doCall(addUserWatchListGroupRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    removeUserWatchListGroupResponse?.status?.let {
                        if (it == 2) {
                            imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                        } else if (it == 3) {
                            imqConnectionListener.isPinExpiredLiveData.postValue(true)
                        }
                    }
                    if (removeUserWatchListGroupResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("RemoveUserWatchListGroupRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("removeWatchListCategory").e(e)
            }
            removeUserWatchListGroupResponse
        }
    }

    override suspend fun removeItemCategory(addUserWatchListGroupRequest: RemoveUserWatchListItemRequest): RemoveUserWatchListItemResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var removeUserWatchListItemResponse: RemoveUserWatchListItemResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC =
                    MQClientRPC<RemoveUserWatchListItemRequest?, RemoveUserWatchListItemResponse>(
                        channelPooled,
                        ConstantKeys.REMOVE_ITEM_CATEGORY_RPC,
                        RemoveUserWatchListItemResponse::class.java
                    )
                Timber.i("start call RemoveUserWatchListItemRequest")
                removeUserWatchListItemResponse =
                    mqClientRPC.doCall(addUserWatchListGroupRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    removeUserWatchListItemResponse?.status?.let {
                        if (it == 2) {
                            imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                        } else if (it == 3) {
                            imqConnectionListener.isPinExpiredLiveData.postValue(true)
                        }
                    }
                    if (removeUserWatchListItemResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("RemoveUserWatchListItemRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("removeItemCategory").e(e)
            }
            removeUserWatchListItemResponse
        }
    }

//    override fun getPINElapsedTime(elapsedTimeRequest: GetPINElapsedTimeRequest?): GetPINElapsedTimeResponse? {
//        var elapsedTimeResponse: GetPINElapsedTimeResponse? = null
//        try {
//            val mqClientRPC = MQClientRPC<GetPINElapsedTimeRequest?, GetPINElapsedTimeResponse>(
//                channelPooled,
//                Util.generateRPCName(
//                    "olt",
//                    CakraMessage.Type.GET_PIN_ELAPSED_TIME_REQUEST
//                ), GetPINElapsedTimeResponse::class.java
//            )
//            elapsedTimeResponse = mqClientRPC.doCall(elapsedTimeRequest, 10000, imqConnectionListener)
//        
//                if (companyProfileResponse.status.equals("2")) {
//                    imqConnectionListener.isSessionExpiredLiveData.postValue(true)
//                } else if (companyProfileResponse.status.equals("3")) {
//                    imqConnectionListener.isPinExpiredLiveData.postValue(true)
//                }
//            } catch (e: Exception) {
//            Timber.tag("").e(e)
//        }
//        return elapsedTimeResponse
//    }

    override suspend fun getOrderListInfo(orderListRequest: OrderListRequest?): OrderListResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var orderListResponse: OrderListResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<OrderListRequest?, OrderListResponse>(
                    channelPooled,
                    ConstantKeys.ORDER_LIST_RPC, OrderListResponse::class.java
                )
                Timber.i("start call OrderListRequest")
                orderListResponse = mqClientRPC.doCall(orderListRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    orderListResponse?.status?.let {
                        if (it == 2) {
                            imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                        } else if (it == 3) {
                            imqConnectionListener.isPinExpiredLiveData.postValue(true)
                        }
                    }
                    if (orderListResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("OrderListRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("getOrderListInfo").e(e)
            }
            orderListResponse
        }

    }

    override suspend fun getStockParamList(stockParamRequest: StockParamRequest?): StockParamResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var stockParamResponse: StockParamResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<StockParamRequest?, StockParamResponse>(
                    channelPooled,
                    ConstantKeys.STOCK_PARAM_LIST_RPC, StockParamResponse::class.java
                )
                Timber.i("start call StockParamRequest")
                stockParamResponse = mqClientRPC.doCall(stockParamRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    stockParamResponse?.status?.let {
                        if (it == 2) {
                            imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                        } else if (it == 3) {
                            imqConnectionListener.isPinExpiredLiveData.postValue(true)
                        }
                    }
                    if (stockParamResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("StockParamRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("getStockParamList").e(e)
            }
            stockParamResponse
        }

    }

    override suspend fun getTradeListInfo(tradeListRequest: TradeListRequest?): TradeListResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var orderListResponse: TradeListResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<TradeListRequest?, TradeListResponse>(
                    channelPooled,
                    "cakra.trade_list_request-rpc", TradeListResponse::class.java
                )
                Timber.i("start call TradeListRequest")
                orderListResponse = mqClientRPC.doCall(tradeListRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    orderListResponse?.status?.let {
                        if (it == 2) {
                            imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                        } else if (it == 3) {
                            imqConnectionListener.isPinExpiredLiveData.postValue(true)
                        }
                    }
                    if (orderListResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("TradeListRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("getTradeListInfo").e(e)
            }
            orderListResponse
        }
    }

    override suspend fun getSettlementSchedule(settlementScheduleRequest: SettlementScheduleRequest?): SettlementScheduleResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var settlementScheduleResponse: SettlementScheduleResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC =
                    MQClientRPC<SettlementScheduleRequest?, SettlementScheduleResponse>(
                        channelPooled,
                        ConstantKeys.SETTLEMENT_SCHEDULE_RPC,
                        SettlementScheduleResponse::class.java
                    )
                Timber.i("start call SettlementScheduleRequest")
                settlementScheduleResponse = mqClientRPC.doCall(settlementScheduleRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    settlementScheduleResponse?.status?.let {
                        if (it == 2) {
                            imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                        } else if (it == 3) {
                            imqConnectionListener.isPinExpiredLiveData.postValue(true)
                        }
                    }
                    if (settlementScheduleResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("SettlementScheduleRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("getSettlementSchedule").e(e)
            }
            settlementScheduleResponse
        }
    }

//    override fun getSettlementScheduleDetail(settlementScheduleDetailRequest: SettlementScheduleDetailRequest?): SettlementScheduleDetailResponse? {
//        var settlementScheduleDetailResponse: SettlementScheduleDetailResponse? = null
//        try {
//            val mqClientRPC =
//                MQClientRPC<SettlementScheduleDetailRequest?, SettlementScheduleDetailResponse>(
//                    channelPooled,
//                    "cakra.settlement_schedule_request-rpc",
//                    SettlementScheduleDetailResponse::class.java
//                )
//            settlementScheduleDetailResponse =
//                mqClientRPC.doCall(settlementScheduleDetailRequest, 10000, imqConnectionListener)
//        
//                if (companyProfileResponse.status.equals("2")) {
//                    imqConnectionListener.isSessionExpiredLiveData.postValue(true)
//                } else if (companyProfileResponse.status.equals("3")) {
//                    imqConnectionListener.isPinExpiredLiveData.postValue(true)
//                }
//            } catch (e: Exception) {
//            Timber.tag("").e(e)
//        }
//        return settlementScheduleDetailResponse
//    }

    override suspend fun getCashWithdraw(cashWithdrawRequest: WithdrawCashRequest?): WithdrawCashResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var cashWithdrawResponse: WithdrawCashResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<WithdrawCashRequest?, WithdrawCashResponse>(
                    channelPooled, "cakra.withdraw_cash_request-rpc", WithdrawCashResponse::class.java
                )
                Timber.i("start call WithdrawCashRequest")
                cashWithdrawResponse = mqClientRPC.doCall(cashWithdrawRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    cashWithdrawResponse?.status?.let {
                        if (it == 2) {
                            imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                        } else if (it == 3) {
                            imqConnectionListener.isPinExpiredLiveData.postValue(true)
                        }
                    }
                    if (cashWithdrawResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("WithdrawCashRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("getCashWithdraw").e(e)
            }
            cashWithdrawResponse
        }
    }

    override suspend fun getOrderInfoLIst(orderListRequest: OrderListRequest?): OrderListResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var orderListResponse: OrderListResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<OrderListRequest?, OrderListResponse>(
                    channelPooled,
                    "cakra.order_list_request-rpc", OrderListResponse::class.java
                )
                Timber.i("start call OrderListRequest")
                orderListResponse = mqClientRPC.doCall(orderListRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    orderListResponse?.status?.let {
                        if (it == 2) {
                            imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                        } else if (it == 3) {
                            imqConnectionListener.isPinExpiredLiveData.postValue(true)
                        }
                    }
                    if (orderListResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("OrderListRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("getOrderInfoLIst").e(e)
            }
            orderListResponse
        }
    }

    override suspend fun getFastOrderList(fastOrderListRequest: FastOrderListRequest?): FastOrderListResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var fastOrderListResponse: FastOrderListResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<FastOrderListRequest?, FastOrderListResponse>(
                    channelPooled,
                    ConstantKeys.FAST_ORDER_RPC, FastOrderListResponse::class.java
                )
                Timber.i("start call FastOrderListRequest")
                fastOrderListResponse =
                    mqClientRPC.doCall(fastOrderListRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    fastOrderListResponse?.status?.let {
                        if (it == 2) {
                            imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                        } else if (it == 3) {
                            imqConnectionListener.isPinExpiredLiveData.postValue(true)
                        }
                    }
                    if (fastOrderListResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("OrderListRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("getFastOrderList").e(e)
            }
            fastOrderListResponse
        }
    }

    override suspend fun getTradeSummary(tradeSummaryRequest: Cf.CFMessage.TradeSummaryRequest?): Cf.CFMessage.TradeSummaryResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var tradeSummaryResponse: Cf.CFMessage.TradeSummaryResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<Cf.CFMessage.TradeSummaryRequest?, Cf.CFMessage.TradeSummaryResponse>(
                    channelPooled, ConstantKeys.TRADE_SUMMARY_RPC, Cf.CFMessage.TradeSummaryResponse::class.java
                )
                Timber.i("start call TradeSummaryRequest")
                tradeSummaryResponse = mqClientRPC.doCall(tradeSummaryRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    tradeSummaryResponse?.status?.let {
                        if (it == 2) {
                            imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                        } else if (it == 3) {
                            imqConnectionListener.isPinExpiredLiveData.postValue(true)
                        }
                    }
                    if (tradeSummaryResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("TradeSummaryRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("getTradeSummary").e(e)
            }
            tradeSummaryResponse
        }
    }

    override suspend fun getValidatePin(validatePinRequest: ValidatePinRequest): ValidatePinResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var validatePinResponse: ValidatePinResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<ValidatePinRequest?, ValidatePinResponse>(
                    channelPooled, ConstantKeys.VALIDATE_PIN_RPC, ValidatePinResponse::class.java
                )
                Timber.i("start call ValidatePinRequest")
                validatePinResponse = mqClientRPC.doCall(validatePinRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    validatePinResponse?.status?.let {
                        if (it == 2) {
                            imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                        } else if (it == 3) {
                            imqConnectionListener.isPinExpiredLiveData.postValue(true)
                        }
                    }
                    if (validatePinResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("ValidatePinRequest")
                        throw Exception("Timeout occurred for ValidatePinRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("getValidatePin").e(e)
                if (validatePinResponse == null) {
                    throw Exception("Timeout occurred for ValidatePinRequest")
                }
            }
            validatePinResponse
        }
    }

    override suspend fun getCompanyProfile(companyProfileRequest: StockDetilCompanyProfileRequest?): StockDetilCompanyProfileResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var companyProfileResponse: StockDetilCompanyProfileResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC =
                    MQClientRPC<StockDetilCompanyProfileRequest?, StockDetilCompanyProfileResponse>(
                        channelPooled,
                        ConstantKeys.COMPANY_PROFILE_RPC,
                        StockDetilCompanyProfileResponse::class.java
                    )
                Timber.i("start call StockDetilCompanyProfileRequest")
                companyProfileResponse = mqClientRPC.doCall(companyProfileRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    if (companyProfileResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("StockDetilCompanyProfileRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("getCompanyProfile").e(e)
            }
            companyProfileResponse
        }
    }

    override suspend fun getNewsPromo(newsInfoPromoRequest: NewsInfoPromoRequest): NewsInfoResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var newsInfoResponse: NewsInfoResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<NewsInfoPromoRequest?, NewsInfoResponse>(
                    channelPooled, ConstantKeys.NEWS_PROMO_RPC, NewsInfoResponse::class.java
                )
                Timber.i("start call NewsInfoPromoRequest")
                newsInfoResponse = mqClientRPC.doCall(newsInfoPromoRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    if (newsInfoResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("NewsInfoPromoRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("getNewsPromo").e(e)
            }
            newsInfoResponse
        }
    }

    override suspend fun getPromoBanner(promotionBannerRequest: PromotionBannerRequest): PromotionBannerResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var promotionBannerResponse: PromotionBannerResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<PromotionBannerRequest?, PromotionBannerResponse>(
                    channelPooled, ConstantKeys.NEWS_BANNER_PROMOTION_RPC, PromotionBannerResponse::class.java
                )
                Timber.i("start call PromotionBannerRequest")
                promotionBannerResponse = mqClientRPC.doCall(promotionBannerRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    if (promotionBannerResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("PromotionBannerRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("getPromoBanner").e(e)
            }
            promotionBannerResponse
        }
    }

    override suspend fun getHistoryOrder(orderHistoryRequest: OrderListHistoryRequest): OrderListHistoryResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var orderHistoryResponse: OrderListHistoryResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientTC = MQClientRPC<OrderListHistoryRequest?, OrderListHistoryResponse>(
                    channelPooled,
                    ConstantKeys.ORDER_HISTORY_RPC,
                    OrderListHistoryResponse::class.java
                )
                Timber.i("start call OrderListHistoryRequest")
                orderHistoryResponse = mqClientTC.doCall(orderHistoryRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    orderHistoryResponse?.status?.let {
                        if (it == 2) {
                            imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                        } else if (it == 3) {
                            imqConnectionListener.isPinExpiredLiveData.postValue(true)
                        }
                    }
                    if (orderHistoryResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("OrderListHistoryRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("getHistoryOrder").e(e)
            }
            orderHistoryResponse
        }
    }

    override suspend fun getHistoryTrade(tradeListHistoryRequest: TradeListHistoryRequest?): TradeListHistoryResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var tradeListHistoryResponse: TradeListHistoryResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientTC = MQClientRPC<TradeListHistoryRequest?, TradeListHistoryResponse>(
                    channelPooled,
                    "cakra.trade_list_history_request-rpc",
                    TradeListHistoryResponse::class.java
                )
                Timber.i("start call TradeListHistoryRequest")
                tradeListHistoryResponse = mqClientTC.doCall(tradeListHistoryRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    tradeListHistoryResponse?.status?.let {
                        if (it == 2) {
                            imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                        } else if (it == 3) {
                            imqConnectionListener.isPinExpiredLiveData.postValue(true)
                        }
                    }
                    if (tradeListHistoryResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("TradeListHistoryRequest")
                    }
                }}
            } catch (e: Exception) {
                Timber.tag("getHistoryTrade").e(e)
            }
            tradeListHistoryResponse
        }
    }

    /*override suspend fun startTradeBook() {
        withContext(Dispatchers.IO) {
            try {
                consumerTradeBook = MQConsumer(
                    "mi.trade_detail_data-d", "topic", channelPooled,
                    MIMessage::class.java, false, false
                )
            } catch (e: Exception) {
                Timber.tag("").e(e)
            }
        }
    }

    override suspend fun setListenerTradeBook(miListener: MQMessageListener<MIMessage?>?) {
        withContext(Dispatchers.IO) {
            try {
                consumerTradeBook.consume(miListener)
            } catch (e: Exception) {
                Timber.tag("").e(e)
            }
        }
    }

    override suspend fun subscribeTradeBook(routingKey: String?) {
        withContext(Dispatchers.IO) {
            try {
                consumerTradeBook.subscribe(routingKey)
            } catch (e: Exception) {
                Timber.tag("").e(e)
            }
        }
    }

    override suspend fun unsubscribeTradeBook(stockCode: String?) {
        withContext(Dispatchers.IO) {
            try {
                consumerTradeBook.unsubscribe(stockCode)
            } catch (e: Exception) {
                Timber.tag("").e(e)
            }
        }
    }

    override suspend fun stopTradeBook() {
        withContext(Dispatchers.IO) {
            try {
                consumerTradeBook.stopConsume()
            } catch (e: Exception) {
                Timber.tag("").e(e)
            }
        }
    }*/

    override suspend fun startRunningTrade() {
        withContext(Dispatchers.IO) {
            try {
                consumerRunningTrade = MQConsumer(
                    "mi.trade_detail_data-d", "topic", channelPooled,
                    MIMessage::class.java, false, false
                )
                consumerRunningTrade.consume()
                Timber.d("Start RunningTrade")
            } catch (e: Exception) {
                Timber.tag("startRunningTrade").e(e)
            }
        }
    }

    override suspend fun setListenerRunningTrade(miListener: MQMessageListener<MIMessage>?) {
        withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            try {
                consumerRunningTrade.setMqMsgListener(miListener)
            } catch (e: Exception) {
                Timber.tag("setListenerRunningTrade").e(e)
            }
        }
    }

    override suspend fun subscribeRunningTrade(routingKey: String?) {
        withContext(singleThread) {
            imqConnectionListener.latchData?.await()
            try {
                consumerRunningTrade.subscribe(routingKey)
            } catch (e: Exception) {
                Timber.tag("subscribeRunningTrade").e(e)
            }
        }
    }

    override suspend fun unsubscribeRunningTrade(routingKey: String?) {
        withContext(singleThread) {
            imqConnectionListener.latchData?.await()
            try {
                consumerRunningTrade.unsubscribe(routingKey)
            } catch (e: Exception) {
                Timber.tag("unsubscribeRunningTrade").e(e)
            }
        }
    }

    override suspend fun stopRunningTrade() {
        withContext(Dispatchers.IO) {
            try {
                consumerRunningTrade.stopConsume()
                Timber.d("Stop RunningTrade")
            } catch (e: Exception) {
                Timber.tag("stopRunningTrade").e(e)
            }
        }
    }

    override suspend fun startCIFStockPos() {
        withContext(Dispatchers.IO) {
            try {
                consumerCIFStockPos = MQConsumer(
                    "cakra-info.message", "topic", channelPooled,
                    CakraMessage::class.java, true, false
                )
                consumerCIFStockPos.consume()
                Timber.d("Start CIFStockPos")
            } catch (e: Exception) {
                Timber.tag("startCIFStockPos").e(e)
            }
        }
    }

    override suspend fun setListenerCIFStockPos(mqMessageListener: MQMessageListener<CakraMessage>?) {
        withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            try {
                consumerCIFStockPos.setMqMsgListener(mqMessageListener)
            } catch (e: Exception) {
                Timber.tag("setListenerCIFStockPos").e(e)
            }
        }
    }

    override suspend fun subscribeCIFStockPos(routingKey: String?) {
        withContext(singleThread) {
            imqConnectionListener.latchData?.await()
            try {
                consumerCIFStockPos.subscribe(routingKey)
            } catch (e: Exception) {
                Timber.tag("subscribeCIFStockPos").e(e)
            }
        }
    }

    override suspend fun unSubscribeCIFStockPos(routingKey: String) {
        withContext(singleThread) {
            imqConnectionListener.latchData?.await()
            try {
                consumerCIFStockPos.unsubscribe(routingKey)
            } catch (e: Exception) {
                Timber.tag("unSubscribeCIFStockPos").e(e)
            }
        }
    }

    override suspend fun stopCifStockPos() {
        withContext(Dispatchers.IO) {
            try {
                consumerCIFStockPos.stopConsume()
                Timber.d("Stop CIFStockPos")
            } catch (e: Exception) {
                Timber.tag("stopCifStockPos").e(e)
            }
        }
    }

    override suspend fun startAppNotification(
        cakraListener: MQMessageListener<CakraMessage>?,
    ) {
        withContext(Dispatchers.IO) {
            try {
                consumerAppNotification = MQConsumer(
                    "cakra.app_info", "topic", channelPooled,
                    CakraMessage::class.java, true, false
                )
                consumerAppNotification.consume()
            } catch (e: Exception) {
                Timber.tag("startAppNotification").e(e)
            }
        }
    }

    override suspend fun startNewAppNotification(appNotificationFlow: (CakraMessage) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                consumerAppNotification = MQConsumer(
                    "cakra.app_info", "topic", channelPooled,
                    CakraMessage::class.java, true, false
                )
                consumerAppNotification.consume()
                consumerAppNotification.setMqMsgListener {
                    appNotificationFlow.invoke(it.protoMsg)
                }
            } catch (e: Exception) {
                Timber.tag("startNewAppNotification").e(e)
            }
        }
    }

    override suspend fun unsubscribeAppNotification(routingKey: String?) {
        withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            try {
                routingKey?.let {
                    if (it.isNotEmpty() && it.isNotBlank()) {
                        Timber.d("App Info Unsubscribe: $routingKey")
                        consumerAppNotification.unsubscribe(routingKey)
                    }
                }
            } catch (e: Exception) {
                Timber.tag("unsubscribeAppNotification").e(e)
            }
        }
    }

    override suspend fun subscribeAppNotification(routingKey: String?) {
        withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            try {
                routingKey?.let {
                    if (it.isNotEmpty() && it.isNotBlank()) {
                        Timber.d("App Info Subscribe: $routingKey")
                        consumerAppNotification.subscribe(routingKey)
                    }
                }
            } catch (e: Exception) {
                Timber.tag("subscribeAppNotification").e(e)
            }
        }
    }

    override suspend fun stopAppNotification() {
        withContext(Dispatchers.IO) {
            try {
                Timber.d("App Info Stop")
                consumerAppNotification.stopConsume()
            } catch (e: Exception) {
                Timber.tag("stopAppNotification").e(e)
            }
        }
    }

    override suspend fun startOrderBook() {
        withContext(Dispatchers.IO) {
            try {
                consumerOrderBook = MQConsumer(
                    "mi.orderbook_summary_compact-i", "topic", channelPooled,
                    MIMessage::class.java, false, false
                )
                consumerOrderBook.consume()
                Timber.d("Start OrderBook")
            } catch (e: Exception) {
                Timber.tag("startOrderBook").e(e)
            }
        }
    }

    override suspend fun setListenerOrderBook(mqMessageListener: MQMessageListener<MIMessage>?) {
        withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            try {
                consumerOrderBook.setMqMsgListener(mqMessageListener)
            } catch (e: Exception) {
                Timber.tag("setListenerOrderBook").e(e)
            }
        }
    }

    override suspend fun subscribeOrderBook(routingKey: String?) {
        withContext(singleThread) {
            imqConnectionListener.latchData?.await()
            try {
                if (routingKey != null) {
                    if (routingKey != "") {
                        consumerOrderBook.subscribe(routingKey)
                    }
                }
            } catch (e: Exception) {
                Timber.tag("subscribeOrderBook").e(e)
            }
        }
    }

    override suspend fun unsubscribeOrderBook(routingKey: String?) {
        withContext(singleThread) {
            imqConnectionListener.latchData?.await()
            try {
                consumerOrderBook.unsubscribe(routingKey)
            } catch (e: Exception) {
                Timber.tag("unsubscribeOrderBook").e(e)
            }

        }
    }

    override suspend fun stopOrderBook() {
        withContext(Dispatchers.IO) {
            try {
                consumerOrderBook.stopConsume()
                Timber.d("Stop OrderBook")
            } catch (e: Exception) {
                Timber.tag("stopOrderBook").e(e)
            }

        }
    }

    override suspend fun startTradeSum() {
        withContext(Dispatchers.IO) {
            try {
                consumerTradeSummary = MQConsumer(
                    "mi.trade_summary-i", "topic", channelPooled,
                    MIMessage::class.java, false, false
                )
                consumerTradeSummary.consume()
                Timber.d("Start TradeSum")
            } catch (e: Exception) {
                Timber.tag("startTradeSum").e(e)
            }
        }
    }

    override suspend fun setListenerTradeSum(mqMessageListener: MQMessageListener<MIMessage>?) {
        withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            try {
                consumerTradeSummary.setMqMsgListener(mqMessageListener)
            } catch (e: Exception) {
                Timber.tag("setListenerTradeSum").e(e)
            }
        }
    }

    override suspend fun subscribeAllTradeSum(routingKeys: List<String>) {
        return withContext(singleThread) {
            imqConnectionListener.latchData?.await()
            val jobs = routingKeys.map { stockParam ->
                // Your function that processes each stock item
                async { subscribeTradeSumAnsyc(stockParam) }
            }
            jobs.awaitAll() // Wait for all async tasks to complete
            Timber.d("Subscribe All routing keys completed")
        }
    }

    private suspend fun subscribeTradeSumAnsyc(routingKey: String?) {
        withContext(Dispatchers.IO) {
            try {
                consumerTradeSummary.subscribe(routingKey)
            } catch (e: Exception) {
                Timber.tag("subscribeTradeSum").e(e)
            }
        }
    }

    override suspend fun subscribeTradeSum(routingKey: String?) {
        withContext(singleThread) {
            imqConnectionListener.latchData?.await()
            try {
                consumerTradeSummary.subscribe(routingKey)
            } catch (e: Exception) {
                Timber.tag("subscribeTradeSum").e(e)
            }
        }
    }

    override suspend fun unsubscribeAllTradeSum(routingKeys: List<String>) {
        return withContext(singleThread) {
            imqConnectionListener.latchData?.await()
            val jobs = routingKeys.map { stockParam ->
                // Your function that processes each stock item
                async { unsubscribeTradeSumAsync(stockParam) }
            }
            jobs.awaitAll() // Wait for all async tasks to complete
            Timber.d("Unsubscribe All routing keys completed")
        }
    }

    private suspend fun unsubscribeTradeSumAsync(routingKey: String?) {
        withContext(Dispatchers.IO) {
            try {
                consumerTradeSummary.unsubscribe(routingKey)
            } catch (e: Exception) {
                Timber.tag("unsubscribeTradeSum").e(e)
            }
        }
    }

    override suspend fun unsubscribeTradeSum(routingKey: String?) {
        withContext(singleThread) {
            imqConnectionListener.latchData?.await()
            try {
                consumerTradeSummary.unsubscribe(routingKey)
            } catch (e: Exception) {
                Timber.tag("unsubscribeTradeSum").e(e)
            }
        }
    }

    override suspend fun stopTradeSum() {
        withContext(Dispatchers.IO) {
            try {
                consumerTradeSummary.stopConsume()
                Timber.d("Stop TradeSum")
            } catch (e: Exception) {
                Timber.tag("stopTradeSum").e(e)
            }
        }
    }

    override suspend fun startConsumeOrderReply(
        accNo: String?,
        oltListener: MQMessageListener<CakraMessage>?,
    ) {
        withContext(Dispatchers.IO) {
            try {
                consumerOLT = MQConsumer(
                    "cakra.order_reply", "topic", channelPooled,
                    CakraMessage::class.java, true, false
                )
                consumerOLT.consume()
            } catch (e: Exception) {
                Timber.tag("startConsumeOrderReply").e(e)
            }
        }
    }

    override suspend fun startNewOrderReply(orderReplyFlow: (CakraMessage) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                consumerOLT = MQConsumer(
                    "cakra.order_reply", "topic", channelPooled,
                    CakraMessage::class.java, true, false
                )
                consumerOLT.consume()
                consumerOLT.setMqMsgListener {
                    // Process the message and send updates through Flow
                    orderReplyFlow.invoke(it.protoMsg)
                }
            } catch (e: Exception) {
                Timber.tag("startNewOrderReply").e(e)
            }
        }
    }
    override suspend fun subscribeOrderReply(accNo: String?) {
        withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            try {
                accNo?.let {
                    if (it.isNotEmpty() && it.isNotBlank()) {
                        Timber.d("Order Reply Subscribe : $accNo")
                        consumerOLT.subscribe("*.*.$accNo")
                    }
                }
            } catch (e: Exception) {
                Timber.tag("subscribeOrderReply").e(e)
            }
        }
    }

    override suspend fun unsubscribeOrderReply(accNo: String?) {
        withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            try {
                accNo?.let {
                    if (it.isNotEmpty() && it.isNotBlank()) {
                        Timber.d("Order Reply Unsubscribe: $accNo")
                        consumerOLT.unsubscribe("*.*.$accNo")
                    }
                }
            } catch (e: Exception) {
                Timber.tag("unsubscribeOrderReply").e(e)
            }
        }
    }

    override suspend fun stopConsumeOrderReply() {
        withContext(Dispatchers.IO) {
            try {
                consumerOLT.stopConsume()
            } catch (e: Exception) {
                Timber.tag("stopConsumeOrderReply").e(e)
            }
        }
    }

    override suspend fun startAllConsumer(isFirstLaunch: Boolean): Boolean {
        return withContext(Dispatchers.IO) {
            //val appNotifJob = async { startNewAppNotification() }
            //val orderReplayJob = async { startNewOrderReply() }
            if (!isFirstLaunch) {
                imqConnectionListener.onListener("Recovered")
            } else {
                val tradeSumJob = async { startTradeSum() }
                val cifStockPosJob = async { startCIFStockPos() }
                val indiceDataJob = async { startIndiceData() }
                val orderBookJob = async { startOrderBook() }
                val runTradeJob = async { startRunningTrade() }
                val publishOrder = async { startPublishOrder() }
                val publishSubsInfo = async { startPublishSubsInfo() }
                // Waits for all to complete
                awaitAll(tradeSumJob, cifStockPosJob, indiceDataJob,
                    orderBookJob, runTradeJob, publishOrder, publishSubsInfo)
                Timber.d("Start All Consumer completed")
            }
            true
        }
    }

    override suspend fun startIndiceData() {
        withContext(Dispatchers.IO) {
            try {
                consumerIndiceData = MQConsumer(
                    "mi.indice_summary-i", "topic", channelPooled,
                    MIMessage::class.java, false, false
                )
                consumerIndiceData.consume()
                Timber.d("Start IndiceData")
            } catch (e: Exception) {
                Timber.tag("startIndiceData").e(e)
            }
        }
    }

    override suspend fun setListenerIndiceData(miListener: MQMessageListener<MIMessage>?) {
        withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            try {
                consumerIndiceData.setMqMsgListener(miListener)
            } catch (e: Exception) {
                Timber.tag("setListenerIndiceData").e(e)
            }
        }
    }

    override suspend fun subscribeAllIndiceData(routingKeys: List<String>) {
        return withContext(singleThread) {
            imqConnectionListener.latchData?.await()
            val jobs = routingKeys.map { stockParam ->
                // Your function that processes each stock item
                async { subscribeIndiceDataAsync(stockParam) }
            }
            jobs.awaitAll() // Wait for all async tasks to complete
            Timber.d("Subscribe All routing keys completed")
        }
    }

    private suspend fun subscribeIndiceDataAsync(routingKey: String?) {
        withContext(Dispatchers.IO) {
            try {
                consumerIndiceData.subscribe(routingKey)
            } catch (e: Exception) {
                Timber.tag("subscribeIndiceData").e(e)
            }
        }
    }

    override suspend fun subscribeIndiceData(routingKey: String?) {
        withContext(singleThread) {
            imqConnectionListener.latchData?.await()
            try {
                consumerIndiceData.subscribe(routingKey)
            } catch (e: Exception) {
                Timber.tag("subscribeIndiceData").e(e)
            }
        }
    }

    override suspend fun unsubscribeAllIndiceData(routingKeys: List<String>) {
        return withContext(singleThread) {
            imqConnectionListener.latchData?.await()
            val jobs = routingKeys.map { stockParam ->
                // Your function that processes each stock item
                async { unsubscribeIndiceDataAsync(stockParam) }
            }
            jobs.awaitAll() // Wait for all async tasks to complete
            Timber.d("Unsubscribe All routing keys completed")
        }
    }

    private suspend fun unsubscribeIndiceDataAsync(routingKey: String?) {
        withContext(Dispatchers.IO) {
            try {
                consumerIndiceData.unsubscribe(routingKey)
            } catch (e: Exception) {
                Timber.tag("unsubscribeIndiceData").e(e)
            }
        }
    }

    override suspend fun unsubscribeIndiceData(routingKey: String?) {
        withContext(singleThread) {
            imqConnectionListener.latchData?.await()
            try {
                consumerIndiceData.unsubscribe(routingKey)
            } catch (e: Exception) {
                Timber.tag("unsubscribeIndiceData").e(e)
            }
        }
    }

    override suspend fun stopIndiceData() {
        withContext(Dispatchers.IO) {
            try {
                consumerIndiceData.stopConsume()
                Timber.d("Stop IndiceData")
            } catch (e: Exception) {
                Timber.tag("stopIndiceData").e(e)
            }
        }
    }

    override suspend fun startPublishOrder() {
        withContext(Dispatchers.IO) {
            try {
                mqPublisherOrder.addMQModel(
                    MQModel("cakra.order_request", "simple", channelPooled)
                )
                mqPublisherOrder.startPublisher()
                Timber.d("Start PublishOrder")
            } catch (e: Exception) {
                Timber.tag("startPublishOrder").e(e)
            }
        }
    }

    override suspend fun sendOrder(cakraMessage: CakraMessage?) {
        withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            try {
                mqPublisherOrder.publishMessageP("", "cakra.order_request", cakraMessage)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun startPublishPing() {
        withContext(Dispatchers.IO) {
            try {
                mqPublisherPing.addMQModel(
                    MQModel("cakra.heartbeat", "simple", channelPooled?.obtain())
                )
                Timber.d("Start PublishPing")
            } catch (e: Exception) {
                Timber.tag("startPublishPing").e(e)
            }
        }
    }

    override suspend fun sendPing(cakraHeartbeat: CakraHeartbeat) {
        withContext(Dispatchers.IO) {
            try {
                mqPublisherPing.startPublisher()
                mqPublisherPing.publishMessage("", "cakra.heartbeat", cakraHeartbeat)

                Timber.d("sendPing Request after 30s")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun startPublishSubsInfo() {
        withContext(Dispatchers.IO) {
            try {
                mqPublisherSubsInfo.addMQModel(
                    MQModel("cakra.subscribe_info", "simple", channelPooled?.obtain())
                )
                Timber.d("Start PublishSubsInfo")
            } catch (e: Exception) {
                Timber.tag("startPublishSubsInfo").e(e)
            }
        }
    }

    override suspend fun publishSubsInfo(cakraMessage: CakraMessage?) {
        withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            try {
                mqPublisherSubsInfo.startPublisher()
                mqPublisherSubsInfo.publishMessage("", "cakra.subscribe_info", cakraMessage)
            } catch (e: Exception) {
                Timber.tag("publishFastOrder").e(e)
            }
        }
    }

    override suspend fun getKeyStat(keyStatRequest: ViewKeyStatRequest?): ViewKeyStatResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var viewKeyStatResponse: ViewKeyStatResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientTC = MQClientRPC<ViewKeyStatRequest?, ViewKeyStatResponse>(
                    channelPooled,
                    ConstantKeys.CURRENT_KEY_STAT_RPC,
                    ViewKeyStatResponse::class.java
                )
                Timber.i("start call ViewKeyStatRequest")
                viewKeyStatResponse = mqClientTC.doCall(keyStatRequest, 10000, imqConnectionListener)
                launch(Dispatchers.Main) {
                    if (viewKeyStatResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("ViewKeyStatRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            viewKeyStatResponse
        }
    }

    override suspend fun getKeyStatsRti(viewKeyStatsRTIRequest: ViewKeyStatsRTIRequest?): ViewKeyStatsRTIResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var viewKeyStatRtiResponse: ViewKeyStatsRTIResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientTC = MQClientRPC<ViewKeyStatsRTIRequest?, ViewKeyStatsRTIResponse>(
                    channelPooled,
                    ConstantKeys.KEY_STATS_RTI_RPC,
                    ViewKeyStatsRTIResponse::class.java
                )
                Timber.i("start call ViewKeyStatsRTIRequest")
                viewKeyStatRtiResponse = mqClientTC.doCall(viewKeyStatsRTIRequest, 10000, imqConnectionListener)
                launch(Dispatchers.Main) {
                    if (viewKeyStatRtiResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("ViewKeyStatsRTIRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            viewKeyStatRtiResponse
        }
    }

    override suspend fun getEarningPerShares(earningsPerShareRequest: EarningsPerShareRequest?): EarningsPerShareResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var earningsPerShareResponse: EarningsPerShareResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientTc = MQClientRPC<EarningsPerShareRequest?, EarningsPerShareResponse>(
                    channelPooled,
                    ConstantKeys.EARNING_PER_SHARES_RPC,
                    EarningsPerShareResponse::class.java
                )
                Timber.i("start call EarningsPerShareRequest")
                earningsPerShareResponse = mqClientTc.doCall(earningsPerShareRequest, 10000, imqConnectionListener)
                launch(Dispatchers.Main) {
                    if (earningsPerShareResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("EarningsPerShareRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }

            earningsPerShareResponse
        }
    }

    override suspend fun getIncomeStatement(incomeStatementRequest: ViewIncomeStatementRequest?): ViewIncomeStatementResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var incomeStatementResponse: ViewIncomeStatementResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientTC =
                    MQClientRPC<ViewIncomeStatementRequest?, ViewIncomeStatementResponse>(
                        channelPooled,
                        ConstantKeys.INCOME_STATEMENT_RPC,
                        ViewIncomeStatementResponse::class.java
                    )
                Timber.i("start call ViewIncomeStatementRequest")
                incomeStatementResponse = mqClientTC.doCall(incomeStatementRequest, 10000, imqConnectionListener)
                launch(Dispatchers.Main) {
                    if (incomeStatementResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("ViewIncomeStatementRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            incomeStatementResponse
        }
    }

    override suspend fun getBalanceSheet(balanceSheetRequest: ViewBalanceSheetRequest?): ViewBalanceSheetResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var balanceSheetResponse: ViewBalanceSheetResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientTC = MQClientRPC<ViewBalanceSheetRequest?, ViewBalanceSheetResponse>(
                    channelPooled,
                    ConstantKeys.BALANCE_SHEET_RPC,
                    ViewBalanceSheetResponse::class.java
                )
                Timber.i("start call ViewBalanceSheetRequest")
                balanceSheetResponse = mqClientTC.doCall(balanceSheetRequest, 10000, imqConnectionListener)
                launch(Dispatchers.Main) {
                    if (balanceSheetResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("ViewBalanceSheetRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            balanceSheetResponse
        }
    }

    override suspend fun getCashFlow(cashFlowRequest: ViewCashFlowRequest?): ViewCashFlowResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var cashFlowResponse: ViewCashFlowResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientTC = MQClientRPC<ViewCashFlowRequest?, ViewCashFlowResponse>(
                    channelPooled,
                    ConstantKeys.CASH_FLOW_RPC,
                    ViewCashFlowResponse::class.java
                )
                Timber.i("start call ViewCashFlowRequest")
                cashFlowResponse = mqClientTC.doCall(cashFlowRequest, 10000, imqConnectionListener)
                launch(Dispatchers.Main) {
                    if (cashFlowResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("ViewCashFlowRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            cashFlowResponse
        }
    }

    override suspend fun getDetailIncomeStatement(financiaIncomeStatementRequest: FinancialIncomeStatementRequest?): FinancialIncomeStatementResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var financialIncomeStatementResponse: FinancialIncomeStatementResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientTC = MQClientRPC<FinancialIncomeStatementRequest?, FinancialIncomeStatementResponse>(
                    channelPooled,
                    ConstantKeys.DETAIL_INCOME_STATEMENT_RPC,
                    FinancialIncomeStatementResponse::class.java
                )
                Timber.i("start call FinancialIncomeStatementRequest")
                financialIncomeStatementResponse = mqClientTC.doCall(financiaIncomeStatementRequest, 10000, imqConnectionListener)
                launch(Dispatchers.Main) {
                    if (financialIncomeStatementResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("FinancialIncomeStatementRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            financialIncomeStatementResponse
        }
    }

    override suspend fun getDetailBalanceSheet(financialBalanceSheetRequest: FinancialBalanceSheetRequest?): FinancialBalanceSheetResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var finacBalanceSheetResponse: FinancialBalanceSheetResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientTC = MQClientRPC<FinancialBalanceSheetRequest?, FinancialBalanceSheetResponse>(
                    channelPooled,
                    ConstantKeys.DETAIL_BALANCE_SHEET_RPC,
                    FinancialBalanceSheetResponse::class.java
                )
                Timber.i("start call FinancialBalanceSheetRequest")
                finacBalanceSheetResponse = mqClientTC.doCall(financialBalanceSheetRequest, 10000, imqConnectionListener)
                launch(Dispatchers.Main) {
                    if (finacBalanceSheetResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("FinancialBalanceSheetRequest")

                        throw Exception("Timeout occurred for FinancialBalanceSheetRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            finacBalanceSheetResponse
        }
    }

    override suspend fun getDetailCashFlow(financialCashFlowRequest: FinancialCashFlowRequest?): FinancialCashFlowResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var financialCashFlowResponse: FinancialCashFlowResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientTC = MQClientRPC<FinancialCashFlowRequest?, FinancialCashFlowResponse>(
                    channelPooled,
                    ConstantKeys.DETAIL_CASH_FLOW_RPC,
                    FinancialCashFlowResponse::class.java
                )
                Timber.i("start call FinancialCashFlowRequest")
                financialCashFlowResponse = mqClientTC.doCall(financialCashFlowRequest, 10000, imqConnectionListener)
                launch(Dispatchers.Main) {
                    if (financialCashFlowResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("FinancialCashFlowRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            financialCashFlowResponse
        }
    }

    override suspend fun getPerBand(getPerBandRequest: GetPerBandRequest?): GetPerBandResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var getPerBandResponse: GetPerBandResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientTC = MQClientRPC<GetPerBandRequest?, GetPerBandResponse>(
                    channelPooled,
                    ConstantKeys.GET_PER_BAND_RPC,
                    GetPerBandResponse::class.java
                )
                Timber.i("start call GetPerBandRequest")
                getPerBandResponse = mqClientTC.doCall(getPerBandRequest, 10000, imqConnectionListener)
                launch(Dispatchers.Main) {
                    if (getPerBandResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("GetPerBandRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            getPerBandResponse
        }
    }

    override suspend fun getPerData(getPerDataRequest: GetPerDataRequest?): GetPerDataResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var getPerDataResponse: GetPerDataResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientTC = MQClientRPC<GetPerDataRequest?, GetPerDataResponse>(
                    channelPooled,
                    ConstantKeys.GET_PER_DATA_RPC,
                    GetPerDataResponse::class.java
                )
                Timber.i("start call GetPerDataRequest")
                getPerDataResponse = mqClientTC.doCall(getPerDataRequest, 10000, imqConnectionListener)
                launch(Dispatchers.Main) {
                    if (getPerDataResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("GetPerDataRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            getPerDataResponse
        }
    }

    override suspend fun getPbvBand(getPbvBandRequest: GetPbvBandRequest?): GetPbvBandResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var getPbvBandResponse: GetPbvBandResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientTC = MQClientRPC<GetPbvBandRequest?, GetPbvBandResponse>(
                    channelPooled,
                    ConstantKeys.GET_PBV_BAND_RPC,
                    GetPbvBandResponse::class.java
                )
                Timber.i("start call GetPbvBandRequest")
                getPbvBandResponse = mqClientTC.doCall(getPbvBandRequest, 10000, imqConnectionListener)
                launch(Dispatchers.Main) {
                    if (getPbvBandResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("GetPbvBandRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            getPbvBandResponse
        }
    }

    override suspend fun getPbvData(getPbvDataRequest: GetPbvDataRequest?): GetPbvDataResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var getPbvDataResponse: GetPbvDataResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientTC = MQClientRPC<GetPbvDataRequest?, GetPbvDataResponse>(
                    channelPooled,
                    ConstantKeys.GET_PBV_DATA_RPC,
                    GetPbvDataResponse::class.java
                )
                Timber.i("start call GetPbvDataRequest")
                getPbvDataResponse = mqClientTC.doCall(getPbvDataRequest, 10000, imqConnectionListener)
                launch(Dispatchers.Main) {
                    if (getPbvDataResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("GetPbvDataRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            getPbvDataResponse
        }
    }

    override suspend fun getStockPick(stockPickRequest: NewsStockPickSingleRequest): NewsStockPickSingleResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var stockPickResponse: NewsStockPickSingleResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClient = MQClientRPC<NewsStockPickSingleRequest, NewsStockPickSingleResponse>(
                    channelPooled,
                    ConstantKeys.STOCK_PICK_RPC,
                    NewsStockPickSingleResponse::class.java
                )
                Timber.i("start call NewsStockPickSingleRequest")
                stockPickResponse = mqClient.doCall(stockPickRequest, 10000, imqConnectionListener)
                launch(Dispatchers.Main) {
                    if (stockPickResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("NewsStockPickSingleRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            stockPickResponse
        }
    }


    override suspend fun getStockAnalysisRating(stockAnalysisRatingRequest: StockAnalysisRatingRequest?): StockAnalysisRatingResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var stockAnalysisRatingResponse: StockAnalysisRatingResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClient = MQClientRPC<StockAnalysisRatingRequest, StockAnalysisRatingResponse>(
                    channelPooled,
                    ConstantKeys.STOCK_ANALYSIS_RATING_RPC,
                    StockAnalysisRatingResponse::class.java
                )
                Timber.i("start call StockAnalysisRatingRequest")
                stockAnalysisRatingResponse = mqClient.doCall(stockAnalysisRatingRequest, 10000, imqConnectionListener)
                launch(Dispatchers.Main) {
                    if (stockAnalysisRatingResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("StockAnalysisRatingRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            stockAnalysisRatingResponse
        }
    }

    override suspend fun getFibonacciPivotPoint(fibonacciPivotPointRequest: FibonacciPivotPointRequest?): FibonacciPivotPointResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var fibonacciPivotPointResponse: FibonacciPivotPointResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRpc = MQClientRPC<FibonacciPivotPointRequest, FibonacciPivotPointResponse>(
                    channelPooled,
                    ConstantKeys.FIBONACCI_PIVOT_POINT_RPC,
                    FibonacciPivotPointResponse::class.java
                )
                Timber.i("start call FibonacciPivotPointRequest")
                fibonacciPivotPointResponse = mqClientRpc.doCall(fibonacciPivotPointRequest, 10000, imqConnectionListener)
                launch(Dispatchers.Main) {
                    if (fibonacciPivotPointResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("FibonacciPivotPointRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            fibonacciPivotPointResponse
        }
    }

    override suspend fun getBrokerStockSummary(brokerStockSummaryRequest: BrokerStockSummaryRequest?): BrokerStockSummaryResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var brokerStockSummaryResponse: BrokerStockSummaryResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClient = MQClientRPC<BrokerStockSummaryRequest, BrokerStockSummaryResponse>(
                    channelPooled,
                    ConstantKeys.BROKER_STOCK_SUMMARY_RPC,
                    BrokerStockSummaryResponse::class.java
                )
                Timber.i("start call BrokerStockSummaryRequest")
                brokerStockSummaryResponse = mqClient.doCall(brokerStockSummaryRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    if (brokerStockSummaryResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("BrokerStockSummaryRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            brokerStockSummaryResponse
        }
    }

    override suspend fun getBrokerRankByStock(brokerRankByStockDiscoverRequest: BrokerRankByStockDiscoverRequest?): BrokerRankByStockDiscoverResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var brokerRankByStockDiscoverResponse: BrokerRankByStockDiscoverResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClient =
                    MQClientRPC<BrokerRankByStockDiscoverRequest, BrokerRankByStockDiscoverResponse>(
                        channelPooled,
                        ConstantKeys.BROKER_RANK_BY_STOCK_RPC,
                        BrokerRankByStockDiscoverResponse::class.java
                    )
                Timber.i("start call BrokerRankByStockDiscoverRequest")
                brokerRankByStockDiscoverResponse =
                    mqClient.doCall(brokerRankByStockDiscoverRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    if (brokerRankByStockDiscoverResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("BrokerRankByStockDiscoverRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            brokerRankByStockDiscoverResponse
        }
    }

    override suspend fun getBrokerSummaryByStockNet(brokerRankByStockNetDiscoverRequest: BrokerRankByStockNetDiscoverRequest): BrokerRankByStockNetDiscoverResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var brokerSummaryByStockNetResponse: BrokerRankByStockNetDiscoverResponse? = null
            try {
                if (channelPooled?.connection?.isOpen == true){
                    val mqClient = MQClientRPC<BrokerRankByStockNetDiscoverRequest, BrokerRankByStockNetDiscoverResponse>(
                        channelPooled,
                        ConstantKeys.BROKER_SUMMARY_BY_STOCK_NET_RPC,
                        BrokerRankByStockNetDiscoverResponse::class.java
                    )
                    brokerSummaryByStockNetResponse = mqClient.doCall(brokerRankByStockNetDiscoverRequest, 10000, imqConnectionListener)

                    launch(Dispatchers.Main) {
                        if (brokerSummaryByStockNetResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                            imqConnectionListener.timeOutLiveData.postValue("BrokerRankByStockNetDiscoverRequest")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            brokerSummaryByStockNetResponse
        }
    }

    override suspend fun getBrokerList(brokerListRequest: BrokerListRequest): BrokerListResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var brokerListResponse: BrokerListResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<BrokerListRequest,BrokerListResponse>(
                    channelPooled,
                    ConstantKeys.BROKER_LIST_RPC,
                    BrokerListResponse::class.java
                )
                Timber.i("start call BrokerListRequest")
                brokerListResponse = mqClientRPC.doCall(brokerListRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    if (brokerListResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("BrokerListRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            brokerListResponse
        }
    }

    override suspend fun getBrokerRankActivity(brokerRankActivityDiscoverRequest: BrokerRankActivityDiscoverRequest): BrokerRankActivityDiscoverResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var brokerRankActivityResponse: BrokerRankActivityDiscoverResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<BrokerRankActivityDiscoverRequest,BrokerRankActivityDiscoverResponse>(
                    channelPooled,
                    ConstantKeys.BROKER_RANK_ACTIVITY_RPC,
                    BrokerRankActivityDiscoverResponse::class.java
                )
                Timber.i("start call BrokerRankActivityDiscoverRequest")
                brokerRankActivityResponse = mqClientRPC.doCall(brokerRankActivityDiscoverRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    if (brokerRankActivityResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("BrokerRankActivityDiscoverRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            brokerRankActivityResponse
        }
    }

    override suspend fun getBrokerRankRanking(brokerRankingDiscoverRequest: BrokerRankingDiscoverRequest): BrokerRankingDiscoverResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var brokerRankingResponse: BrokerRankingDiscoverResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<BrokerRankingDiscoverRequest,BrokerRankingDiscoverResponse>(
                    channelPooled,
                    ConstantKeys.BROKER_RANK_RANKING_RPC,
                    BrokerRankingDiscoverResponse::class.java
                )
                Timber.i("start call BrokerRankingDiscoverRequest")
                brokerRankingResponse = mqClientRPC.doCall(brokerRankingDiscoverRequest, 20000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    if (brokerRankingResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("BrokerRankingDiscoverRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            brokerRankingResponse
        }
    }

    override suspend fun getIndexSector(indexSectorRequest: ViewIndexSectorRequest): ViewIndexSectorResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var indexSectorResponse: ViewIndexSectorResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClient = MQClientRPC<ViewIndexSectorRequest, ViewIndexSectorResponse>(
                        channelPooled,
                        ConstantKeys.INDEX_SECTOR_RPC,
                        ViewIndexSectorResponse::class.java
                    )
                Timber.i("start call ViewIndexSectorRequest")
                indexSectorResponse = mqClient.doCall(indexSectorRequest, 10000, imqConnectionListener)
                launch(Dispatchers.Main) {
                    if (indexSectorResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("ViewIndexSectorRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            indexSectorResponse
        }
    }

    override suspend fun getStockIndexSector(stockIndexSectorRequest: StockIndexMappingByStockIndexRequest): StockIndexMappingByStockIndexResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var stockIndexSectorResponse: StockIndexMappingByStockIndexResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClient = MQClientRPC<StockIndexMappingByStockIndexRequest, StockIndexMappingByStockIndexResponse>(
                    channelPooled,
                    ConstantKeys.STOCK_INDEX_SECTOR_RPC,
                    StockIndexMappingByStockIndexResponse::class.java
                )
                Timber.i("start call StockIndexMappingByStockIndexRequest")
                stockIndexSectorResponse = mqClient.doCall(stockIndexSectorRequest, 10000, imqConnectionListener)
                launch(Dispatchers.Main) {
                    if (stockIndexSectorResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("StockIndexMappingByStockIndexRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            stockIndexSectorResponse
        }
    }

    override suspend fun getMaxOrderByStock(maxOrderByStockRequest: MaxOrderByStockRequest?): MaxOrderByStockResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var maxOrderByStockResponse: MaxOrderByStockResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClient = MQClientRPC<MaxOrderByStockRequest, MaxOrderByStockResponse>(
                    channelPooled,
                    ConstantKeys.MAX_ORDER_BY_STOCK_RPC,
                    MaxOrderByStockResponse::class.java
                )
                Timber.i("start call MaxOrderByStockRequest")
                maxOrderByStockResponse = mqClient.doCall(maxOrderByStockRequest, 10000, imqConnectionListener)

                maxOrderByStockResponse?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    } else if (it == 3) {
                        imqConnectionListener.isPinExpiredLiveData.postValue(true)
                    }
                }
                launch(Dispatchers.Main) {
                    if (maxOrderByStockResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("MaxOrderByStockRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            maxOrderByStockResponse
        }
    }

    override suspend fun getSimpleAccountInfo(simpleAccountInfoByCIFRequest: SimpleAccountInfoByCIFRequest): SimpleAccountInfoByCIFResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var simpleAccountInfoByCIFResponse: SimpleAccountInfoByCIFResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClient = MQClientRPC<SimpleAccountInfoByCIFRequest, SimpleAccountInfoByCIFResponse>(
                    channelPooled,
                    ConstantKeys.GET_SIMPLE_ACCOUNT_INFO_RPC,
                    SimpleAccountInfoByCIFResponse::class.java
                )
                Timber.i("start call SimpleAccountInfoByCIFRequest")
                simpleAccountInfoByCIFResponse = mqClient.doCall(simpleAccountInfoByCIFRequest, 10000, imqConnectionListener)

                simpleAccountInfoByCIFResponse?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    } else if (it == 3) {
                        imqConnectionListener.isPinExpiredLiveData.postValue(true)
                    }
                }
                launch(Dispatchers.Main) {
                    if (simpleAccountInfoByCIFResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("SimpleAccountInfoByCIFRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            simpleAccountInfoByCIFResponse
        }
    }

    override suspend fun getSimplePortfolio(simplePortofolioRequest: SimplePortofolioRequest): SimplePortofolioResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var simplePortofolioResponse: SimplePortofolioResponse? = null
            try {
                if (channelPooled?.connection?.isOpen == true) {
                    val mqClient = MQClientRPC<SimplePortofolioRequest, SimplePortofolioResponse>(
                        channelPooled,
                        ConstantKeys.GET_SIMPLE_PORTFOLIO_RPC,
                        SimplePortofolioResponse::class.java
                    )
                    Timber.i("start call SimplePortofolioRequest")
                    simplePortofolioResponse =
                        mqClient.doCall(simplePortofolioRequest, 10000, imqConnectionListener)

                    simplePortofolioResponse?.status?.let {
                        if (it == 2) {
                            imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                        } else if (it == 3) {
                            imqConnectionListener.isPinExpiredLiveData.postValue(true)
                        }
                    }
                    launch(Dispatchers.Main) {
                        if (simplePortofolioResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                            imqConnectionListener.timeOutLiveData.postValue("SimplePortofolioRequest")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            simplePortofolioResponse
        }
    }

    override suspend fun getStockRankInfo(stockRankingRequest: StockRankingRequest): StockRankingResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var stockRankResponse: StockRankingResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<StockRankingRequest, StockRankingResponse>(
                    channelPooled,
                    ConstantKeys.STOCK_RANK_INFO_RPC,
                    StockRankingResponse::class.java
                )
                Timber.i("start call StockRankingRequest")
                stockRankResponse = mqClientRPC.doCall(stockRankingRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    if (stockRankResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("StockRankingRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            stockRankResponse
        }
    }

    override suspend fun getStockInfoDetail(viewStockInfoDetilRequest: ViewStockInfoDetilRequest): ViewStockInfoDetilResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var viewStockInfoDetilResponse: ViewStockInfoDetilResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<ViewStockInfoDetilRequest, ViewStockInfoDetilResponse>(
                    channelPooled,
                    ConstantKeys.STOCK_INFO_DETAIL_RPC,
                    ViewStockInfoDetilResponse::class.java
                )
                Timber.i("start call ViewStockInfoDetilRequest")
                viewStockInfoDetilResponse = mqClientRPC.doCall(viewStockInfoDetilRequest, 10000, imqConnectionListener)
                launch(Dispatchers.Main) {
                    if (viewStockInfoDetilResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("ViewStockInfoDetilRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            viewStockInfoDetilResponse
        }
    }

    override suspend fun getAdvanceOrderInfo(advancedOrderInfoRequest: AdvancedOrderInfoRequest): AdvancedOrderInfoResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var advancedOrderInfoResponse: AdvancedOrderInfoResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRpc = MQClientRPC<AdvancedOrderInfoRequest, AdvancedOrderInfoResponse>(
                    channelPooled,
                    ConstantKeys.ADVANCE_ORDER_INFO_RPC,
                    AdvancedOrderInfoResponse::class.java
                )
                Timber.i("start call AdvancedOrderInfoRequest")
                advancedOrderInfoResponse = mqClientRpc.doCall(advancedOrderInfoRequest, 10000, imqConnectionListener)

                advancedOrderInfoResponse?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    } else if (it == 3) {
                        imqConnectionListener.isPinExpiredLiveData.postValue(true)
                    }
                }
                launch(Dispatchers.Main) {
                    if (advancedOrderInfoResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("AdvancedOrderInfoRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            advancedOrderInfoResponse
        }
    }

    override suspend fun getChartIntradayPrice(intradayPriceRequest: IntradayPriceRequest): IntradayPriceResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var intradayPriceResponse: IntradayPriceResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<IntradayPriceRequest, IntradayPriceResponse>(
                    channelPooled,
                    ConstantKeys.INTRADAY_PRICE_RPC,
                    IntradayPriceResponse::class.java
                )
                Timber.i("start call IntradayPriceRequest")
                intradayPriceResponse = mqClientRPC.doCall(intradayPriceRequest, 10000, imqConnectionListener)

                intradayPriceResponse?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    } else if (it == 3) {
                        imqConnectionListener.isPinExpiredLiveData.postValue(true)
                    }
                }
                launch(Dispatchers.Main) {
                    if (intradayPriceResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("IntradayPriceRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            intradayPriceResponse
        }
    }

    override suspend fun getRightIssueInfo(exerciseInfoRequest: ExerciseInfoRequest): ExerciseInfoResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var exerciseInfoResponse: ExerciseInfoResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<ExerciseInfoRequest, ExerciseInfoResponse>(
                    channelPooled,
                    ConstantKeys.EXERCISE_INFO_RPC,
                    ExerciseInfoResponse::class.java
                )
                Timber.i("start call ExerciseInfoRequest")
                exerciseInfoResponse = mqClientRPC.doCall(exerciseInfoRequest, 10000, imqConnectionListener)

                exerciseInfoResponse?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    } else if (it == 3) {
                        imqConnectionListener.isPinExpiredLiveData.postValue(true)
                    }
                }
                launch(Dispatchers.Main) {
                    if (exerciseInfoResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("ExerciseInfoRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            exerciseInfoResponse
        }
    }

    override suspend fun getTradeList(tradeListRequest: TradeListRequest): TradeListResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var tradeListResponse: TradeListResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<TradeListRequest, TradeListResponse>(
                    channelPooled,
                    ConstantKeys.TRADE_LIST_RPC,
                    TradeListResponse::class.java
                )
                Timber.i("start call TradeListRequest")
                tradeListResponse = mqClientRPC.doCall(tradeListRequest, 10000, imqConnectionListener)

                tradeListResponse?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    } else if (it == 3) {
                        imqConnectionListener.isPinExpiredLiveData.postValue(true)
                    }
                }
                launch(Dispatchers.Main) {
                    if (tradeListResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("TradeListRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            tradeListResponse
        }
    }

    override suspend fun getExerciseOrderList(exerciseOrderListRequest: ExerciseOrderListRequest): ExerciseOrderListResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var exerciseOrderListResponse: ExerciseOrderListResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<ExerciseOrderListRequest, ExerciseOrderListResponse>(
                    channelPooled,
                    ConstantKeys.EXERCISE_ORDER_LIST_RPC,
                    ExerciseOrderListResponse::class.java
                )
                Timber.i("start call ExerciseOrderListRequest")
                exerciseOrderListResponse = mqClientRPC.doCall(exerciseOrderListRequest, 10000, imqConnectionListener)

                exerciseOrderListResponse?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    } else if (it == 3) {
                        imqConnectionListener.isPinExpiredLiveData.postValue(true)
                    }
                }
                launch(Dispatchers.Main) {
                    if (exerciseOrderListResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("ExerciseOrderListRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            exerciseOrderListResponse
        }
    }

    override suspend fun getMarketSession(marketSessionRequest: MarketSessionRequest): MarketSessionResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var marketSessionResponse: MarketSessionResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRpc = MQClientRPC<MarketSessionRequest, MarketSessionResponse>(
                    channelPooled,
                    ConstantKeys.MARKET_SESSION_RPC,
                    MarketSessionResponse::class.java
                )
                Timber.i("start call MarketSessionRequest")
                marketSessionResponse = mqClientRpc.doCall(marketSessionRequest, 10000, imqConnectionListener)

                marketSessionResponse?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    } else if (it == 3) {
                        imqConnectionListener.isPinExpiredLiveData.postValue(true)
                    }
                }
                launch(Dispatchers.Main) {
                    if (marketSessionResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("MarketSessionRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            marketSessionResponse
        }
    }

    override suspend fun getWithdrawCash(withdrawCashRequest: WithdrawCashRequest): WithdrawCashResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var withdrawCashResponse: WithdrawCashResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRpc = MQClientRPC<WithdrawCashRequest, WithdrawCashResponse>(
                    channelPooled,
                    ConstantKeys.WITHDRAW_CASH_RPC,
                    WithdrawCashResponse::class.java
                )
                Timber.i("start call WithdrawCashRequest")
                withdrawCashResponse = mqClientRpc.doCall(withdrawCashRequest, 10000, imqConnectionListener)

                withdrawCashResponse?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    } else if (it == 3) {
                        imqConnectionListener.isPinExpiredLiveData.postValue(true)
                    }
                }
                launch(Dispatchers.Main) {
                    if (withdrawCashResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("WithdrawCashRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            withdrawCashResponse
        }
    }

    override suspend fun getCalendarByDateInRangeRpc(caCalendarbyCaDateInRangeRequest: CorporateActionCalendarGetRequest): CorporateActionCalendarGetResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var corporateActionCalendarGetResponse: CorporateActionCalendarGetResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<CorporateActionCalendarGetRequest, CorporateActionCalendarGetResponse>(
                    channelPooled,
                    ConstantKeys.CALENDAR_BY_DATE_IN_RANGE_RPC,
                    CorporateActionCalendarGetResponse::class.java
                )
                Timber.i("start call CaCalendarbyCaDateInRangeRequest")
                corporateActionCalendarGetResponse = mqClientRPC.doCall(caCalendarbyCaDateInRangeRequest, 10000, imqConnectionListener)
                launch(Dispatchers.Main) {
                    if (corporateActionCalendarGetResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("corporateActionCalendarGetRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            corporateActionCalendarGetResponse
        }
    }

    override suspend fun getRdnHistory(cashMovementRequest: AccountCashMovementRequest): AccountCashMovementResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var rdnHistoryResponse: AccountCashMovementResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<AccountCashMovementRequest, AccountCashMovementResponse>(
                    channelPooled,
                    ConstantKeys.RDN_HISTORY_RPC,
                    AccountCashMovementResponse::class.java
                )
                Timber.i("start call AccountCashMovementRequest")
                rdnHistoryResponse = mqClientRPC.doCall(cashMovementRequest, 10000, imqConnectionListener)

                rdnHistoryResponse?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    } else if (it == 3) {
                        imqConnectionListener.isPinExpiredLiveData.postValue(true)
                    }
                }
                launch(Dispatchers.Main) {
                    if (rdnHistoryResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("AccountCashMovementRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            rdnHistoryResponse
        }
    }

    override suspend fun getNewsBannerLogin(loginBannerRequest: LoginBannerRequest): LoginBannerResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var loginBannerResponse: LoginBannerResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClient = MQClientRPC<LoginBannerRequest, LoginBannerResponse>(
                    channelPooled,
                    ConstantKeys.NEWS_BANNER_LOGIN_RPC,
                    LoginBannerResponse::class.java
                )
                Timber.i("start call LoginBannerRequest")
                loginBannerResponse = mqClient.doCall(loginBannerRequest, 10000, imqConnectionListener)
                launch(Dispatchers.Main) {
                    if (loginBannerResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("LoginBannerRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            loginBannerResponse
        }
    }

    override suspend fun getGlobalCommodities(latestComoditiesRequest: LatestComoditiesRequest): LatestComoditiesResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var latestComoditiesResponse: LatestComoditiesResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClient = MQClientRPC<LatestComoditiesRequest, LatestComoditiesResponse>(
                    channelPooled,
                    ConstantKeys.GLOBAL_COMODITIES_RPC,
                    LatestComoditiesResponse::class.java
                )

                latestComoditiesResponse = mqClient.doCall(latestComoditiesRequest, 10000, imqConnectionListener)
                launch(Dispatchers.Main) {
                    if (latestComoditiesResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("Global Comodities")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            latestComoditiesResponse
        }
    }

    override suspend fun getGlobalCurrency(latestCurrencyRequest: LatestCurrencyRequest): LatestCurrencyResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var latestCurrencyResponse: LatestCurrencyResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClient = MQClientRPC<LatestCurrencyRequest, LatestCurrencyResponse>(
                    channelPooled,
                    ConstantKeys.GLOBAL_CURRENCY_RPC,
                    LatestCurrencyResponse::class.java
                )

                latestCurrencyResponse = mqClient.doCall(latestCurrencyRequest, 10000, imqConnectionListener)
                launch(Dispatchers.Main) {
                    if (latestCurrencyResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("Global Currency")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            latestCurrencyResponse
        }
    }

    override suspend fun getGlobalIndex(latestIndexRequest: LatestIndexRequest): LatestIndexResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var latestIndexResponse: LatestIndexResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClient = MQClientRPC<LatestIndexRequest, LatestIndexResponse>(
                    channelPooled,
                    ConstantKeys.GLOBAL_INDEX_RPC,
                    LatestIndexResponse::class.java
                )

                latestIndexResponse = mqClient.doCall(latestIndexRequest, 10000, imqConnectionListener)
                launch(Dispatchers.Main) {
                    if (latestIndexResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("Global Index")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }

            latestIndexResponse
        }
    }

    override suspend fun getTradeListHistory(tradeListHistoryRequest: TradeListHistoryRequest): TradeListHistoryResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var tradeListHistoryResponse: TradeListHistoryResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<TradeListHistoryRequest, TradeListHistoryResponse>(
                    channelPooled,
                    ConstantKeys.TRADE_LIST_HISTORY_RPC,
                    TradeListHistoryResponse::class.java
                )
                Timber.i("start call TradeListHistoryRequest")
                tradeListHistoryResponse = mqClientRPC.doCall(tradeListHistoryRequest, 10000, imqConnectionListener)

                tradeListHistoryResponse?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    } else if (it == 3) {
                        imqConnectionListener.isPinExpiredLiveData.postValue(true)
                    }
                }
                launch(Dispatchers.Main) {
                    if (tradeListHistoryResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("TradeListHistoryRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            tradeListHistoryResponse
        }
    }

    override suspend fun getValidateSession(validateSessionRequest: ValidateSessionRequest): ValidateSessionResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var validateSessionResponse: ValidateSessionResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRpc = MQClientRPC<ValidateSessionRequest, ValidateSessionResponse>(
                    channelPooled,
                    ConstantKeys.VALIDATE_SESSION_RPC,
                    ValidateSessionResponse::class.java
                )
                validateSessionResponse = mqClientRpc.doCall(validateSessionRequest, 10000, imqConnectionListener)

                validateSessionResponse?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    } else if (it == 3) {
                        imqConnectionListener.isPinExpiredLiveData.postValue(true)
                    }
                }
                launch(Dispatchers.Main) {
                    if (validateSessionResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("ValidateSessionRequest")
                    }
                }}
            } catch (e:Exception) {
                e.printStackTrace()
            }
            validateSessionResponse
        }
    }

    override suspend fun getResearchNews(newsResearchContentRequest: NewsResearchContentRequest): NewsResearchContentResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var newsResearchResponse: NewsResearchContentResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<NewsResearchContentRequest, NewsResearchContentResponse>(
                    channelPooled,
                    ConstantKeys.NEWS_RESEARCH_CONTENT_RPC,
                    NewsResearchContentResponse::class.java
                )
                newsResearchResponse = mqClientRPC.doCall(newsResearchContentRequest, 10000, imqConnectionListener)

                newsResearchResponse?.status?.let {
                    if (it == "2") {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (newsResearchResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("StockPickResearchReportRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            newsResearchResponse
        }
    }

    override suspend fun getStockPickReport(stockPickResearchReportRequest: ViewStockPickResearchReportRequest): ViewStockPickResearchReportResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var viewStockPickResearchReportResponse: ViewStockPickResearchReportResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<ViewStockPickResearchReportRequest, ViewStockPickResearchReportResponse>(
                    channelPooled,
                    ConstantKeys.STOCK_PICK_RESEARCH_RPC,
                    ViewStockPickResearchReportResponse::class.java
                )
                    viewStockPickResearchReportResponse = mqClientRPC.doCall(stockPickResearchReportRequest, 10000, imqConnectionListener)

                    viewStockPickResearchReportResponse?.status?.let {
                    if (it == "2") {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (viewStockPickResearchReportResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("StockPickResearchReportRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            viewStockPickResearchReportResponse
        }
    }

    override suspend fun addPriceAlert(addPriceAlertRequest: AddPriceAlertRequest): AddPriceAlertResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var addPriceAlertResponse: AddPriceAlertResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<AddPriceAlertRequest, AddPriceAlertResponse>(
                    channelPooled,
                    ConstantKeys.ADD_PRICE_ALERT_RPC,
                    AddPriceAlertResponse::class.java
                )
                addPriceAlertResponse = mqClientRPC.doCall(addPriceAlertRequest, 10000, imqConnectionListener)

                addPriceAlertResponse?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (addPriceAlertResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("AddPriceAlertRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            addPriceAlertResponse
        }
    }

    override suspend fun getListPriceAlert(listPriceAlertRequest: ListPriceAlertRequest): ListPriceAlertResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var listPriceAlertResponse: ListPriceAlertResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<ListPriceAlertRequest, ListPriceAlertResponse>(
                    channelPooled,
                    ConstantKeys.LIST_PRICE_ALERT_RPC,
                    ListPriceAlertResponse::class.java
                )
                listPriceAlertResponse = mqClientRPC.doCall(listPriceAlertRequest, 10000, imqConnectionListener)

                listPriceAlertResponse?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (listPriceAlertResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("ListPriceAlertRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            listPriceAlertResponse
        }
    }

    override suspend fun removePriceAlert(removePriceAlertRequest: RemovePriceAlertRequest): RemovePriceAlertResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var removePriceAlertResponse: RemovePriceAlertResponse? = null
            try {
                if(channelPooled?.connection?.isOpen == true){
                val mqClientRPC = MQClientRPC<RemovePriceAlertRequest, RemovePriceAlertResponse>(
                    channelPooled,
                    ConstantKeys.REMOVE_PRICE_ALERT_RPC,
                    RemovePriceAlertResponse::class.java
                )
                removePriceAlertResponse = mqClientRPC.doCall(removePriceAlertRequest, 10000, imqConnectionListener)

                removePriceAlertResponse?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (removePriceAlertResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("RemovePriceAlertRequest")
                    }
                }}
            } catch (e: Exception) {
                e.printStackTrace()
            }
            removePriceAlertResponse
        }
    }

    override suspend fun getNotificationHistory(notificationHistoryRequest: NotificationHistoryRequest): NotificationHistoryResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var notificationHistoryResponse: NotificationHistoryResponse? = null

            try {
                val mqClientRPC = MQClientRPC<NotificationHistoryRequest, NotificationHistoryResponse>(
                    channelPooled,
                    ConstantKeys.NOTIFICATION_HISTORY_RPC,
                    NotificationHistoryResponse::class.java
                )
                notificationHistoryResponse = mqClientRPC.doCall(notificationHistoryRequest, 10000, imqConnectionListener)

                notificationHistoryResponse?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (notificationHistoryResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("NotificationHistoryRequest")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            notificationHistoryResponse
        }
    }

    override suspend fun getGlobalRank(brokerRankActivityByInvType2DiscoverRequest: BrokerRankActivityByInvType2DiscoverRequest): BrokerRankActivityByInvType2DiscoverResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var globalRankResponse: BrokerRankActivityByInvType2DiscoverResponse? = null
            try {
                val mqClientRPC = MQClientRPC<BrokerRankActivityByInvType2DiscoverRequest, BrokerRankActivityByInvType2DiscoverResponse>(
                    channelPooled,
                    ConstantKeys.GLOBAL_RANK_RPC,
                    BrokerRankActivityByInvType2DiscoverResponse::class.java
                )
                globalRankResponse = mqClientRPC.doCall(brokerRankActivityByInvType2DiscoverRequest, 10000, imqConnectionListener)

                launch(Dispatchers.Main) {
                    if (globalRankResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("GlobalRankRequest")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            globalRankResponse
        }
    }

    override suspend fun getTopFiveFaq(getNewsTopFiveFrequentAskedQuestionRequest: GetNewsTopFiveFrequentAskedQuestionRequest): GetNewsTopFiveFrequentAskedQuestionResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var topFiveFaqResponse: GetNewsTopFiveFrequentAskedQuestionResponse? = null
            try {
                val mqClientRPC = MQClientRPC<GetNewsTopFiveFrequentAskedQuestionRequest, GetNewsTopFiveFrequentAskedQuestionResponse>(
                    channelPooled,
                    ConstantKeys.TOP_FIVE_FAQ_RPC,
                    GetNewsTopFiveFrequentAskedQuestionResponse::class.java
                )
                topFiveFaqResponse = mqClientRPC.doCall(getNewsTopFiveFrequentAskedQuestionRequest, 10000, imqConnectionListener)

                topFiveFaqResponse?.status?.let {
                    if (it == "2") {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (topFiveFaqResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("GlobalRankRequest")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            topFiveFaqResponse
        }
    }

    override suspend fun getFaqByCategory(getNewsFrequentAskedQuestionByCategoryRequest: GetNewsFrequentAskedQuestionByCategoryRequest): GetNewsFrequentAskedQuestionByCategoryResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var faqByCategory: GetNewsFrequentAskedQuestionByCategoryResponse? = null
            try {
                val mqClientRPC = MQClientRPC<GetNewsFrequentAskedQuestionByCategoryRequest, GetNewsFrequentAskedQuestionByCategoryResponse>(
                    channelPooled,
                    ConstantKeys.FAQ_BY_CATEGORY_RPC,
                    GetNewsFrequentAskedQuestionByCategoryResponse::class.java
                )
                faqByCategory = mqClientRPC.doCall(getNewsFrequentAskedQuestionByCategoryRequest, 10000, imqConnectionListener)

                faqByCategory?.status?.let {
                    if (it == "2") {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (faqByCategory == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("GlobalRankRequest")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            faqByCategory
        }
    }

    override suspend fun getFaq(getNewsFrequentAskedQuestionRequest: GetNewsFrequentAskedQuestionRequest): GetNewsFrequentAskedQuestionResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var faqResponse: GetNewsFrequentAskedQuestionResponse? = null
            try {
                val mqClientRPC = MQClientRPC<GetNewsFrequentAskedQuestionRequest, GetNewsFrequentAskedQuestionResponse>(
                    channelPooled,
                    ConstantKeys.TOP_FIVE_FAQ_RPC,
                    GetNewsFrequentAskedQuestionResponse::class.java
                )
                faqResponse = mqClientRPC.doCall(getNewsFrequentAskedQuestionRequest, 10000, imqConnectionListener)

                faqResponse?.status?.let {
                    if (it == "2") {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (faqResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("GlobalRankRequest")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            faqResponse
        }
    }

    override suspend fun getSearchFaq(searchNewsFrequentAskedQuestionRequest: SearchNewsFrequentAskedQuestionRequest): SearchNewsFrequentAskedQuestionResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var searchFaqResponse: SearchNewsFrequentAskedQuestionResponse? = null
            try {
                val mqClientRPC = MQClientRPC<SearchNewsFrequentAskedQuestionRequest, SearchNewsFrequentAskedQuestionResponse>(
                    channelPooled,
                    ConstantKeys.SEARCH_FAQ_RPC,
                    SearchNewsFrequentAskedQuestionResponse::class.java
                )
                searchFaqResponse = mqClientRPC.doCall(searchNewsFrequentAskedQuestionRequest, 10000, imqConnectionListener)

                searchFaqResponse?.status?.let {
                    if (it == "2") {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (searchFaqResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("SearchFAQRequest")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            searchFaqResponse
        }
    }

    override suspend fun getHelpTutorialVideo(getNewsTutorialVideoRequest: GetNewsTutorialVideoRequest): GetNewsTutorialVideoResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var helpTutorialVideoResponse: GetNewsTutorialVideoResponse? = null
            try {
                val mqClientRPC = MQClientRPC<GetNewsTutorialVideoRequest, GetNewsTutorialVideoResponse>(
                    channelPooled,
                    ConstantKeys.HELP_TUTORIAL_VIDEO_RPC,
                    GetNewsTutorialVideoResponse::class.java
                )
                helpTutorialVideoResponse = mqClientRPC.doCall(getNewsTutorialVideoRequest, 10000, imqConnectionListener)

                helpTutorialVideoResponse?.status?.let {
                    if (it == "2") {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (helpTutorialVideoResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("HelpTutorialVideoRequest")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            helpTutorialVideoResponse
        }
    }

    override suspend fun getEIPOList(pipelinesIpoListRequest: PipelinesIpoListRequest): PipelinesIpoListResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var pipelinesIpoListResponse: PipelinesIpoListResponse? = null
            try {
                val mqClientRPC = MQClientRPC<PipelinesIpoListRequest, PipelinesIpoListResponse>(
                    channelPooled,
                    ConstantKeys.EIPO_LIST_RPC,
                    PipelinesIpoListResponse::class.java
                )
                pipelinesIpoListResponse = mqClientRPC.doCall(pipelinesIpoListRequest, 10000, imqConnectionListener)

                pipelinesIpoListResponse?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (pipelinesIpoListResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("ipoListRequest")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            pipelinesIpoListResponse
        }
    }

    override suspend fun getEIPOInfo(pipelinesIpoInfoRequest: PipelinesIpoInfoRequest): PipelinesIpoInfoResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var pipelinesIpoInfoResponse: PipelinesIpoInfoResponse? = null
            try {
                val mqClientRPC = MQClientRPC<PipelinesIpoInfoRequest, PipelinesIpoInfoResponse>(
                    channelPooled,
                    ConstantKeys.EIPO_INFO_RPC,
                    PipelinesIpoInfoResponse::class.java
                )
                pipelinesIpoInfoResponse = mqClientRPC.doCall(pipelinesIpoInfoRequest, 10000, imqConnectionListener)

                pipelinesIpoInfoResponse?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (pipelinesIpoInfoResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("ipoInfoRequest")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            pipelinesIpoInfoResponse
        }
    }

    override suspend fun getEIPOOrderList(ipoOrderListRequest: IpoOrderListRequest): IpoOrderListResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var ipoOrderListResponse: IpoOrderListResponse? = null
            try {
                val mqClientRPC = MQClientRPC<IpoOrderListRequest, IpoOrderListResponse>(
                    channelPooled,
                    ConstantKeys.EIPO_ORDER_LIST_RPC,
                    IpoOrderListResponse::class.java
                )
                ipoOrderListResponse = mqClientRPC.doCall(ipoOrderListRequest, 10000, imqConnectionListener)

                ipoOrderListResponse?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (ipoOrderListResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("ipoOrderListRequest")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            ipoOrderListResponse
        }
    }

    override suspend fun getEIPOOrderInfo(pipelinesIpoOrderListRequest: PipelinesIpoOrderListRequest): PipelinesIpoOrderListResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var pipelinesIpoOrderListResponse: PipelinesIpoOrderListResponse? = null
            try {
                val mqClientRPC = MQClientRPC<PipelinesIpoOrderListRequest, PipelinesIpoOrderListResponse>(
                    channelPooled,
                    ConstantKeys.EIPO_ORDER_INFO_RPC,
                    PipelinesIpoOrderListResponse::class.java
                )
                pipelinesIpoOrderListResponse = mqClientRPC.doCall(pipelinesIpoOrderListRequest, 10000, imqConnectionListener)

                pipelinesIpoOrderListResponse?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (pipelinesIpoOrderListResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("ipoOrderInfoRequest")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            pipelinesIpoOrderListResponse
        }
    }

    override suspend fun getNewsFeed(newsInfoFeedRequest: NewsInfoFeedRequest): NewsInfoFeedResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var newsResponse: NewsInfoFeedResponse? = null
            try {
                val mqClientRPC = MQClientRPC<NewsInfoFeedRequest, NewsInfoFeedResponse>(
                    channelPooled,
                    ConstantKeys.NEWS_FEED_RPC,
                    NewsInfoFeedResponse::class.java
                )
                newsResponse = mqClientRPC.doCall(newsInfoFeedRequest, 10000, imqConnectionListener)

                newsResponse?.status?.let {
                    if (it == "2") {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (newsResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("NewsFeedRequest")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            newsResponse
        }
    }

    override suspend fun getNewsFeedByStock(newsInfoFeedSearchByStockRequest: NewsInfoFeedSearchByStockRequest): NewsInfoFeedSearchByStockResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var newsResponse: NewsInfoFeedSearchByStockResponse? = null
            try {
                val mqClientRPC = MQClientRPC<NewsInfoFeedSearchByStockRequest, NewsInfoFeedSearchByStockResponse>(
                    channelPooled,
                    ConstantKeys.NEWS_FEED_BY_STOCK_RPC,
                    NewsInfoFeedSearchByStockResponse::class.java
                )
                newsResponse = mqClientRPC.doCall(newsInfoFeedSearchByStockRequest, 10000, imqConnectionListener)

                newsResponse?.status?.let {
                    if (it == "2") {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (newsResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("NewsFeedRequest")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            newsResponse
        }
    }

    override suspend fun getNewsFeedSearch(newsInfoFeedSearchRequest: NewsInfoFeedSearchRequest): NewsInfoFeedSearchResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var newsResponse: NewsInfoFeedSearchResponse? = null
            try {
                val mqClientRPC = MQClientRPC<NewsInfoFeedSearchRequest, NewsInfoFeedSearchResponse>(
                    channelPooled,
                    ConstantKeys.NEWS_FEED_SEARCH_RPC,
                    NewsInfoFeedSearchResponse::class.java
                )
                newsResponse = mqClientRPC.doCall(newsInfoFeedSearchRequest, 10000, imqConnectionListener)

                newsResponse?.status?.let {
                    if (it == "2") {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (newsResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("NewsFeedSearchRequest")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            newsResponse
        }
    }

    override suspend fun getResearchContentSearch(searchNewsResearchContentRequest: SearchNewsResearchContentRequest): SearchNewsResearchContentResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var newsResponse: SearchNewsResearchContentResponse? = null
            try {
                val mqClientRPC = MQClientRPC<SearchNewsResearchContentRequest, SearchNewsResearchContentResponse>(
                    channelPooled,
                    ConstantKeys.NEWS_RESEARCH_CONTENT_SEARCH_RPC,
                    SearchNewsResearchContentResponse::class.java
                )
                newsResponse = mqClientRPC.doCall(searchNewsResearchContentRequest, 10000, imqConnectionListener)

                newsResponse?.status?.let {
                    if (it == "2") {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (newsResponse == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("ResearchContentSearchRequest")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            newsResponse
        }
    }

    override suspend fun getCorporateActionCalendarByStockCode(
        corporateActionCalendarGetByStockRequest: CorporateActionCalendarGetByStockRequest,
    ): CorporateActionCalendarGetByStockResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var response: CorporateActionCalendarGetByStockResponse? = null
            try {
                val mqClientRPC = MQClientRPC<CorporateActionCalendarGetByStockRequest, CorporateActionCalendarGetByStockResponse>(
                    channelPooled,
                    ConstantKeys.NEWS_CORPORATE_ACTION_CALENDAR_BY_STOCK_CODE,
                    CorporateActionCalendarGetByStockResponse::class.java
                )
                response = mqClientRPC.doCall(corporateActionCalendarGetByStockRequest, 10000, imqConnectionListener)

                response?.status?.let {
                    if (it == "2") {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (response == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("CorporateActionTab")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            response
        }
    }

    override suspend fun getTradeHistoryGroup(tradeListHistoryGroupRequest: TradeListHistoryGroupRequest): TradeListHistoryGroupResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var response: TradeListHistoryGroupResponse? = null
            try {
                val mqClientRPC = MQClientRPC<TradeListHistoryGroupRequest, TradeListHistoryGroupResponse>(
                    channelPooled,
                    ConstantKeys.TRADE_LIST_HISTORY_GROUP_RPC,
                    TradeListHistoryGroupResponse::class.java
                )
                response = mqClientRPC.doCall(tradeListHistoryGroupRequest, 10000, imqConnectionListener)

                response?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (response == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("TradeListHistoryGroup")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            response
        }
    }

    override suspend fun getTradeHistoryGroupDetail(tradeListHistoryGroupDetailRequest: TradeListHistoryGroupDetailRequest): TradeListHistoryGroupDetailResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var response: TradeListHistoryGroupDetailResponse? = null
            try {
                val mqClientRPC = MQClientRPC<TradeListHistoryGroupDetailRequest, TradeListHistoryGroupDetailResponse>(
                    channelPooled,
                    ConstantKeys.TRADE_LIST_HISTORY_GROUP_DETAIL_RPC,
                    TradeListHistoryGroupDetailResponse::class.java
                )
                response = mqClientRPC.doCall(tradeListHistoryGroupDetailRequest, 10000, imqConnectionListener)

                response?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (response == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("TradeListHistoryGroupDetail")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            response
        }
    }

    override suspend fun getRealizedGainLossByYear(rGainLossRequest: RGainLossRequest): RGainLossResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var response: RGainLossResponse? = null
            try {
                val mqClientRPC = MQClientRPC<RGainLossRequest, RGainLossResponse>(
                    channelPooled,
                    ConstantKeys.REALIZED_GAIN_LOSS_BY_YEAR_RPC,
                    RGainLossResponse::class.java
                )
                response = mqClientRPC.doCall(rGainLossRequest, 10000, imqConnectionListener)

                response?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (response == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("RealizedGainLoss")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            response
        }
    }

    override suspend fun getRealizedGainLossByMonth(rGainLossDtlRequest: RGainLossDtlRequest): RGainLossDtlResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var response: RGainLossDtlResponse? = null
            try {
                val mqClientRPC = MQClientRPC<RGainLossDtlRequest, RGainLossDtlResponse>(
                    channelPooled,
                    ConstantKeys.REALIZED_GAIN_LOSS_BY_MONTH_RPC,
                    RGainLossDtlResponse::class.java
                )
                response = mqClientRPC.doCall(rGainLossDtlRequest, 10000, imqConnectionListener)

                response?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (response == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("RealizedGainLoss")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            response
        }
    }

    override suspend fun sendOtpTrustedDevice(sendOtpRequest: SendOtpRequest): SendOtpResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var response: SendOtpResponse? = null
            try {
                val mqClientRPC = MQClientRPC<SendOtpRequest, SendOtpResponse>(
                    channelPooled,
                    ConstantKeys.SEND_OTP_TRUSTED_DEVICE_MESSAGE_RPC,
                    SendOtpResponse::class.java
                )
                response = mqClientRPC.doCall(sendOtpRequest, 10000, imqConnectionListener)

                response?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    } else if (it == 3) {
                        imqConnectionListener.isPinExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (response == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("SendOtp")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            response
        }
    }

    override suspend fun verifyOtpTrustedDevice(verifyOtpRequest: VerifyOtpRequest): VerifyOtpResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var response: VerifyOtpResponse? = null
            try {
                val mqClientRPC = MQClientRPC<VerifyOtpRequest, VerifyOtpResponse>(
                    channelPooled,
                    ConstantKeys.VERIFY_OTP_DEVICE_MESSAGE_RPC,
                    VerifyOtpResponse::class.java
                )
                response = mqClientRPC.doCall(verifyOtpRequest, 10000, imqConnectionListener)

                response?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    } else if (it == 3) {
                        imqConnectionListener.isPinExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (response == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("VerifyOtp")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            response
        }
    }

    override suspend fun getTrustedDevice(trustedDeviceRequest: TrustedDeviceRequest): TrustedDeviceResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var response: TrustedDeviceResponse? = null
            try {
                val mqClientRPC = MQClientRPC<TrustedDeviceRequest, TrustedDeviceResponse>(
                    channelPooled,
                    ConstantKeys.TRUSTED_DEVICE_MESSAGE_RPC,
                    TrustedDeviceResponse::class.java
                )
                response = mqClientRPC.doCall(trustedDeviceRequest, 10000, imqConnectionListener)

                response?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    } else if (it == 3) {
                        imqConnectionListener.isPinExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (response == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("TrustedDevice")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            response
        }
    }

    override suspend fun deleteTrustedDevice(deleteDeviceRequest: DeleteDeviceRequest): DeleteDeviceResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var response: DeleteDeviceResponse? = null
            try {
                val mqClientRPC = MQClientRPC<DeleteDeviceRequest, DeleteDeviceResponse>(
                    channelPooled,
                    ConstantKeys.DELETE_TRUSTED_DEVICE_MESSAGE_RPC,
                    DeleteDeviceResponse::class.java
                )
                response = mqClientRPC.doCall(deleteDeviceRequest, 10000, imqConnectionListener)

                response?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    } else if (it == 3) {
                        imqConnectionListener.isPinExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (response == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("DeleteTrustedDevice")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            response
        }
    }

    override suspend fun getExerciseSession(exerciseSessionRequest: ExerciseSessionRequest): ExerciseSessionResponse? {
        return withContext(Dispatchers.IO) {
            imqConnectionListener.latchData?.await()
            var response: ExerciseSessionResponse? = null
            try {
                val mqClientRPC = MQClientRPC<ExerciseSessionRequest, ExerciseSessionResponse>(
                    channelPooled,
                    ConstantKeys.EXERCISE_SESSSION_MESSAGE_RPC,
                    ExerciseSessionResponse::class.java
                )
                response = mqClientRPC.doCall(exerciseSessionRequest, 10000, imqConnectionListener)

                response?.status?.let {
                    if (it == 2) {
                        imqConnectionListener.isSessionExpiredLiveData.postValue(true)
                    } else if (it == 3) {
                        imqConnectionListener.isPinExpiredLiveData.postValue(true)
                    }
                }

                launch(Dispatchers.Main) {
                    if (response == null && imqConnectionListener.connListenerLiveData.value.isNullOrEmpty()) {
                        imqConnectionListener.timeOutLiveData.postValue("ExerciseSession")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            response
        }
    }

    override suspend fun closeChannel() {
        return withContext(Dispatchers.IO) {
            channelPooled?.releaseChannel()
        }
    }
}