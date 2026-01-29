package com.bcasekuritas.mybest.app.di

import com.bcasekuritas.mybest.app.domain.interactors.*
import com.bcasekuritas.mybest.app.domain.repositories.*
import com.bcasekuritas.rabbitmq.proto.bcas.DeleteDeviceResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseSessionResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RepoUseCaseModule {

    // REMOTE USE CASE
    /** Source Interact */
    /*    @Provides
        @ViewModelScoped
        fun provideGetSourceByCategory(
            sourceRepository: SourceRepository
        ): SourceByCategoryUseCase {
            return SourceByCategoryUseCase(sourceRepository)
        }*/


    // LOCAL USE CASE
    /** Source Interact */
    /*    @Provides
        @ViewModelScoped
        fun provideGetSavedSource(
            sourceRepository: SourceRepository
        ): SourceByCategoryLocalUseCase {
            return SourceByCategoryLocalUseCase(sourceRepository)
        }*/

    @Provides
    @ViewModelScoped
    fun provideGetSavedSource(
        authRepo: AuthRepo
    ): GetLoginUseCase {
        return GetLoginUseCase(authRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetValidatePinSource(
        authRepo: AuthRepo
    ): GetValidatePinUseCase {
        return GetValidatePinUseCase(authRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetChangePasswordUsecaseSource(
        authRepo: AuthRepo
    ): GetChangePasswordUsecase {
        return GetChangePasswordUsecase(authRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetChangePinUsecaseSource(
        authRepo: AuthRepo
    ): GetChangePinUsecase {
        return GetChangePinUsecase(authRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSaveDeviceTokenUsecaseSource(
        authRepo: AuthRepo
    ): SaveDeviceTokenUsecase {
        return SaveDeviceTokenUsecase(authRepo)
    }

    @Provides
    @ViewModelScoped
    fun providePortfolioSource(
        portfolioRepo: PortfolioRepo
    ): GetAccountInfoUseCase {
        return GetAccountInfoUseCase(portfolioRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideCashPosSource(
        portfolioRepo: PortfolioRepo
    ): GetCashPosUseCase {
        return GetCashPosUseCase(portfolioRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSettlementSchedSource(
        portfolioRepo: PortfolioRepo
    ): GetSettlementSchedUseCase {
        return GetSettlementSchedUseCase(portfolioRepo)
    }

//    @Provides
//    @ViewModelScoped
//    fun provideWatchListSource(
//        watchlistRepo: WatchlistRepo
//    ): GetWatchListUseCase {
//        return GetWatchListUseCase(watchlistRepo)
//    }


    @Provides
    @ViewModelScoped
    fun provideGetSimpleWatchlistSource(
        watchlistRepo: WatchlistRepo
    ): GetSimpleWatchlistUseCase {
        return GetSimpleWatchlistUseCase(watchlistRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideAddItemCategorySource(
        watchlistRepo: WatchlistRepo
    ): AddItemCategoryUseCase {
        return AddItemCategoryUseCase(watchlistRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideremoveWatchListCategorySource(
        watchlistRepo: WatchlistRepo
    ): RemoveWatchListCategoryUseCase {
        return RemoveWatchListCategoryUseCase(watchlistRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideremoveItemCategorySource(
        watchlistRepo: WatchlistRepo
    ): RemoveItemCategoryUseCase {
        return RemoveItemCategoryUseCase(watchlistRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideAddUserWatchlistSource(
        watchlistRepo: WatchlistRepo
    ): AddUserWatchlistUseCase {
        return AddUserWatchlistUseCase(watchlistRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideStockDetailWatchListSource(
        watchlistRepo: WatchlistRepo
    ): GetStockDetailUseCase {
        return GetStockDetailUseCase(watchlistRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideStartTradeSumSource(
        watchlistRepo: WatchlistRepo
    ): StartTradeSumUseCase {
        return StartTradeSumUseCase(watchlistRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSetListenerTradeSumSource(
        watchlistRepo: WatchlistRepo
    ): SetListenerTradeSumUseCase {
        return SetListenerTradeSumUseCase(watchlistRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSubscribeAllTradeSumSource(
        watchlistRepo: WatchlistRepo
    ): SubscribeAllTradeSumUseCase {
        return SubscribeAllTradeSumUseCase(watchlistRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSubscribeTradeSumSource(
        watchlistRepo: WatchlistRepo
    ): SubscribeTradeSumUseCase {
        return SubscribeTradeSumUseCase(watchlistRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideUnSubscribeAllTradeSumSource(
        watchlistRepo: WatchlistRepo
    ): UnSubscribeAllTradeSumUseCase {
        return UnSubscribeAllTradeSumUseCase(watchlistRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideUnSubscribeTradeSumSource(
        watchlistRepo: WatchlistRepo
    ): UnSubscribeTradeSumUseCase {
        return UnSubscribeTradeSumUseCase(watchlistRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideStopTradeSumSource(
        watchlistRepo: WatchlistRepo
    ): StopTradeSumUserCase {
        return StopTradeSumUserCase(watchlistRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideStartRunningTradeSource(
        runningTradeRepo: RunningTradeRepo
    ): StartRunningTradeUserCase {
        return StartRunningTradeUserCase(runningTradeRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSetListenerRunningTradeSource(
        watchlistRepo: RunningTradeRepo
    ): SetListenerRunningTradeUseCase {
        return SetListenerRunningTradeUseCase(watchlistRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSubscribeRunningTradeSource(
        watchlistRepo: RunningTradeRepo
    ): SubscribeRunningTradeUseCase {
        return SubscribeRunningTradeUseCase(watchlistRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideUnSubscribeRunningTradeSource(
        watchlistRepo: RunningTradeRepo
    ): UnSubscribeRunningTradeUseCase {
        return UnSubscribeRunningTradeUseCase(watchlistRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideStopRunningTradeSource(
        runningTradeRepo: RunningTradeRepo
    ): StopRunningTradeUseCase {
        return StopRunningTradeUseCase(runningTradeRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideLatestTradeDetailSource(
        runningTradeRepo: RunningTradeRepo
    ): GetLatestTradeDetailUseCase {
        return GetLatestTradeDetailUseCase(runningTradeRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideAccStockPosSource(
        portfolioRepo: PortfolioRepo
    ): GetStockPosUseCase {
        return GetStockPosUseCase(portfolioRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSimpleAccountInfoSource(
        portfolioRepo: PortfolioRepo
    ): GetSimpleAccountInfoUseCase {
        return GetSimpleAccountInfoUseCase(portfolioRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSimplePortfolioSource(
        portfolioRepo: PortfolioRepo
    ): GetSimplePortfolioUseCase {
        return GetSimplePortfolioUseCase(portfolioRepo)
    }

    @Provides
    @ViewModelScoped
    fun providePublishAccPosUseCaseSource(
        portfolioRepo: PortfolioRepo
    ): PublishAccPosUseCase {
        return PublishAccPosUseCase(portfolioRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideStartCIFStockPosUseCaseSource(
        portfolioRepo: PortfolioRepo
    ): StartCIFStockPosUseCase {
        return StartCIFStockPosUseCase(portfolioRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSetListenerCIFStockPosUseCaseSource(
        portfolioRepo: PortfolioRepo
    ): SetListenerCIFStockPosUseCase {
        return SetListenerCIFStockPosUseCase(portfolioRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSubscribeCIFStockPosUseCaseSource(
        portfolioRepo: PortfolioRepo
    ): SubscribeCIFStockPosUseCase {
        return SubscribeCIFStockPosUseCase(portfolioRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideUnsubscribeCIFStockPosUseCaseSource(
        portfolioRepo: PortfolioRepo
    ): UnsubscribeCIFStockPosUseCase {
        return UnsubscribeCIFStockPosUseCase(portfolioRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideStopCIFStockPosUseCaseSource(
        portfolioRepo: PortfolioRepo
    ): StopCIFStockPosUseCase {
        return StopCIFStockPosUseCase(portfolioRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideInputPinUseCase(): InputPinUseCase {
        return InputPinUseCase()
    }

    @Provides
    @ViewModelScoped
    fun provideInputVolumeUseCase(): InputVolumeUseCase {
        return InputVolumeUseCase()
    }

    @Provides
    @ViewModelScoped
    fun provideStockDetailSource(
        stockDetailRepo: StockDetailRepo
    ): GetStockOrderBookUseCase {
        return GetStockOrderBookUseCase(stockDetailRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideStartOrderBookSource(
        stockDetailRepo: StockDetailRepo
    ): StartOrderBookListUseCase {
        return StartOrderBookListUseCase(stockDetailRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSetListenerOrderBookSource(
        stockDetailRepo: StockDetailRepo
    ): SetListenerOrderBookUseCase {
        return SetListenerOrderBookUseCase(stockDetailRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSubscribeOrderbookSource(
        stockDetailRepo: StockDetailRepo
    ): SubscribeOrderBookListUseCase {
        return SubscribeOrderBookListUseCase(stockDetailRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideUnsubscribeOrderBookSource(
        stockDetailRepo: StockDetailRepo
    ): UnSubscribeOrderBookListUseCase {
        return UnSubscribeOrderBookListUseCase(stockDetailRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideStopOrderBookSource(
        stockDetailRepo: StockDetailRepo
    ): StopOrderBookListUseCase {
        return StopOrderBookListUseCase(stockDetailRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSendOrderSource(
        orderRepo: OrderRepo
    ): SendOrderUseCase {
        return SendOrderUseCase(orderRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSendWithdrawFastOrderSource(
        orderRepo: OrderRepo
    ): SendWithdrawFastOrderUseCase {
        return SendWithdrawFastOrderUseCase(orderRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSendAllWithdrawFastOrderSource(
        orderRepo: OrderRepo
    ): SendAllWithdrawFastOrderUseCase {
        return SendAllWithdrawFastOrderUseCase(orderRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSendAmendFastOrderSource(
        orderRepo: OrderRepo
    ): SendAmendFastOrderUseCase {
        return SendAmendFastOrderUseCase(orderRepo)
    }

    @Provides
    @ViewModelScoped
    fun providePublishFastOrderSource(
        orderRepo: FastOrderRepo
    ): PublishFastOrderUseCase {
        return PublishFastOrderUseCase(orderRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSendAdvOrderSource(
        orderRepo: OrderRepo
    ): SendAdvOrderBuyUseCase {
        return SendAdvOrderBuyUseCase(orderRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideAmendOrderSource(
        orderRepo: OrderRepo
    ): SendAmendUseCase {
        return SendAmendUseCase(orderRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideWithdrawOrderSource(
        orderRepo: OrderRepo
    ): SendWithdrawUseCase {
        return SendWithdrawUseCase(orderRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideOrderListSource(
        orderRepo: OrderRepo
    ): GetOrderListUseCase {
        return GetOrderListUseCase(orderRepo)
    }

//    fun provideNewsSource(
//        newsRepo: NewsRepo
//    ): GetNewsUseCase {
//        return GetNewsUseCase(newsRepo)
//    }

    @Provides
    @ViewModelScoped
    fun provideStockTradeSource(
        stockTradeRepo: StockTradeRepo
    ): GetStockTradeUseCase {
        return GetStockTradeUseCase(stockTradeRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideCompanyProfileSource(
        companyProfileRepo: CompanyProfileRepo
    ): GetCompanyProfileUseCase {
        return GetCompanyProfileUseCase(companyProfileRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideStartAppNotificationSource(
        authRepo: AuthRepo
    ): StartAppNotificationUseCase {
        return StartAppNotificationUseCase(authRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideUnsubscribeAppNotificationSource(
        authRepo: AuthRepo
    ): UnsubscribeAppNotificationUseCase {
        return UnsubscribeAppNotificationUseCase(authRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSubscribeAppNotificationSource(
        authRepo: AuthRepo
    ): SubscribeAppNotificationUseCase {
        return SubscribeAppNotificationUseCase(authRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideStopAppNotificationSource(
        authRepo: AuthRepo
    ): StopAppNotificationUseCase {
        return StopAppNotificationUseCase(authRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideStartOrderReplySource(
        authRepo: OrderRepo
    ): StartOrderReplyUseCase {
        return StartOrderReplyUseCase(authRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSubscribeOrderReplySource(
        authRepo: OrderRepo
    ): SubscribeOrderReplyUseCase {
        return SubscribeOrderReplyUseCase(authRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideUnsubscribeOrderReplySource(
        authRepo: OrderRepo
    ): UnsubscribeOrderReplyUseCase {
        return UnsubscribeOrderReplyUseCase(authRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideOrderReplyReplySource(
        authRepo: OrderRepo
    ): StopOrderReplyUseCase {
        return StopOrderReplyUseCase(authRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideTradeBookSource(
        tradeBookRepo: TradeBookRepo
    ): GetTradeBookUseCase {
        return GetTradeBookUseCase(tradeBookRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideTradeBookTimeSource(
        tradeBookRepo: TradeBookRepo
    ): GetTradeBookTimeUseCase {
        return GetTradeBookTimeUseCase(tradeBookRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideKeyStatSource(
        stockDetailRepo: StockDetailRepo
    ): GetKeyStatUseCase {
        return GetKeyStatUseCase(stockDetailRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideKeyStatRtSource(
        stockDetailRepo: StockDetailRepo
    ): GetKeyStatRtiUseCase {
        return GetKeyStatRtiUseCase(stockDetailRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideIncomeStatementSource(
        financialRepo: FinancialRepo
    ): GetIncomeStatementUseCase {
        return GetIncomeStatementUseCase(financialRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideBalanceSheetUseSource(
        financialRepo: FinancialRepo
    ): GetBalanceSheetUseCase {
        return GetBalanceSheetUseCase(financialRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideCashFlowSource(
        financialRepo: FinancialRepo
    ): GetCashFlowUseCase {
        return GetCashFlowUseCase(financialRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideDetailIncomeStatementSource(
        financialRepo: FinancialRepo
    ): GetDetailIncomeStatementUseCase {
        return GetDetailIncomeStatementUseCase(financialRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideDetailBalanceSheetSource(
        financialRepo: FinancialRepo
    ): GetDetailBalanceSheetUseCase {
        return GetDetailBalanceSheetUseCase(financialRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideDetailCashFlowSource(
        financialRepo: FinancialRepo
    ): GetDetailCashFlowUseCase {
        return GetDetailCashFlowUseCase(financialRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideTradeSummarySource(
        tradeSummaryRepo: TradeSummaryRepo
    ): GetTradeSummaryUseCase {
        return GetTradeSummaryUseCase(tradeSummaryRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideEarningPerShareSource(
        stockDetailRepo: StockDetailRepo
    ): GetEarningPerShareUseCase {
        return GetEarningPerShareUseCase(stockDetailRepo)
    }


    @Provides
    @ViewModelScoped
    fun provideStockParamListSource(
        stockParamRepo: StockParamRepo
    ): GetStockParamListUseCase {
        return GetStockParamListUseCase(stockParamRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideInsertAccountDaoSource(
        profileRepo: ProfileRepo
    ): InsertAccountDaoUseCase {
        return InsertAccountDaoUseCase(profileRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetAccNameDaoUseCaseSource(
        profileRepo: ProfileRepo
    ): GetAccNameDaoUseCase {
        return GetAccNameDaoUseCase(profileRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetAccountInfoDaoUseCaseSource(
        profileRepo: ProfileRepo
    ): GetAccountInfoDaoUseCase {
        return GetAccountInfoDaoUseCase(profileRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetAllAccountDaoUseCaseSource(
        profileRepo: ProfileRepo
    ): GetAllAccountDaoUseCase {
        return GetAllAccountDaoUseCase(profileRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideInsertStockParamDaoSource(
        stockParamRepo: StockParamRepo
    ): InsertStockParamDaoUseCase {
        return InsertStockParamDaoUseCase(stockParamRepo)
    }


    @Provides
    @ViewModelScoped
    fun provideInsertAllStockParamDaooUseCaseSource(
        stockParamRepo: StockParamRepo
    ): InsertAllStockParamDaoUseCase {
        return InsertAllStockParamDaoUseCase(stockParamRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteStockByCodeDaoUseCaseSource(
        stockParamRepo: StockParamRepo
    ): DeleteStockByCodeDaoUseCase {
        return DeleteStockByCodeDaoUseCase(stockParamRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteStockParamDaoUseCaseSource(
        stockParamRepo: StockParamRepo
    ): DeleteStockParamDaoUseCase {
        return DeleteStockParamDaoUseCase(stockParamRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetAllStockParamDaoSource(
        stockParamRepo: StockParamRepo
    ): GetAllStockParamDaoUseCase {
        return GetAllStockParamDaoUseCase(stockParamRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideInsertOrderReplyDaoSource(
        orderRepo: OrderRepo
    ): InsertOrderReplyDaoUseCase {
        return InsertOrderReplyDaoUseCase(orderRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideInsertBiometricDaoUseCaseSource(
        authRepo: AuthRepo
    ): InsertBiometricDaoUseCase {
        return InsertBiometricDaoUseCase(authRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideUpdateTokenBiometricDaoUseCaseeSource(
        authRepo: AuthRepo
    ): UpdateTokenBiometricDaoUseCase {
        return UpdateTokenBiometricDaoUseCase(authRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetBiometricDaoUseCaseSource(
        authRepo: AuthRepo
    ): GetBiometricDaoUseCase {
        return GetBiometricDaoUseCase(authRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteBiometricDaoUseCaseSource(
        authRepo: AuthRepo
    ): DeleteBiometricDaoUseCase {
        return DeleteBiometricDaoUseCase(authRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteTokenDaoUseCaseSource(
        authRepo: AuthRepo
    ): DeleteTokenDaoUseCase {
        return DeleteTokenDaoUseCase(authRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetOrderReplyDaoSource(
        orderRepo: OrderRepo
    ): GetOrderReplyDaoUseCase {
        return GetOrderReplyDaoUseCase(orderRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetPerBandUseCase(
        analysisRepo: AnalysisRepo
    ): GetPerBandUseCase {
        return GetPerBandUseCase(analysisRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetPerDataUseCase(
        analysisRepo: AnalysisRepo
    ): GetPerDataUseCase {
        return GetPerDataUseCase(analysisRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetPbvBandUseCase(
        analysisRepo: AnalysisRepo
    ): GetPbvBandUseCase {
        return GetPbvBandUseCase(analysisRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetPbvDataUseCase(
        analysisRepo: AnalysisRepo
    ): GetPbvDataUseCase {
        return GetPbvDataUseCase(analysisRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSearchStockParamDaoSource(
        stockParamRepo: StockParamRepo
    ): SearchStockParamDaoUseCase {
        return SearchStockParamDaoUseCase(stockParamRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetStockParamDaoSource(
        stockParamRepo: StockParamRepo
    ): GetStockParamDaoUseCase {
        return GetStockParamDaoUseCase(stockParamRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetFastOrderListSource(
        fastOrderRepo: FastOrderRepo
    ): GetFastOrderListUseCase {
        return GetFastOrderListUseCase(fastOrderRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSliceOrderSource(
        orderRepo: OrderRepo
    ): SendSliceOrderUseCase {
        return SendSliceOrderUseCase(orderRepo)
    }


    @Provides
    @ViewModelScoped
    fun provideGetSessionPinDaoUseCaseSource(
        authRepo: AuthRepo
    ): GetSessionPinDaoUseCase {
        return GetSessionPinDaoUseCase(authRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideNewsPromoSource(
        newsRepo: NewsRepo
    ): GetNewsPromoInfoUseCase {
        return GetNewsPromoInfoUseCase(newsRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetPromoBannerUseCaseSource(
        newsRepo: NewsRepo
    ): GetPromoBannerUseCase {
        return GetPromoBannerUseCase(newsRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideOrderHistorySource(
        orderRepo: OrderRepo
    ): GetOrderHistoryUseCase {
        return GetOrderHistoryUseCase(orderRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetMaxOrderByStockSource(
        orderRepo: OrderRepo
    ): GetMaxOrderByStockUseCase {
        return GetMaxOrderByStockUseCase(orderRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetMaxOrderByStockBuyingLimitSource(
        orderRepo: OrderRepo
    ): GetMaxOrderByStockForBuyingLimitUseCase {
        return GetMaxOrderByStockForBuyingLimitUseCase(orderRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideStockPickSource(
        newsRepo: NewsRepo
    ): GetStockPickUseCase {
        return GetStockPickUseCase(newsRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideAllWatchlistSource(
        watchlistRepo: WatchlistRepo
    ): GetSimpleAllWatchlistUseCase {
        return GetSimpleAllWatchlistUseCase(watchlistRepo)
    }
    @Provides
    @ViewModelScoped
    fun provideStockAnalysisRatingSource(
        analysisRepo: AnalysisRepo
    ): GetStockAnalysisRatingUseCase {
        return GetStockAnalysisRatingUseCase(analysisRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideInsertSessionDaoSource(
        authRepo: AuthRepo
    ): InsertSessionDaoUseCase {
        return InsertSessionDaoUseCase(authRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteSessionsnDaoSource(
        authRepo: AuthRepo
    ): DeleteSessionsDaoUseCase {
        return DeleteSessionsDaoUseCase(authRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideBrokerStockSummarySource(
        brokerSumRepo: BrokerSumRepo
    ): GetBrokerStockSummaryUseCase {
        return GetBrokerStockSummaryUseCase(brokerSumRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideBrokerRankByStockSource(
        brokerSumRepo: BrokerSumRepo
    ): GetBrokerRankByStockUseCase {
        return GetBrokerRankByStockUseCase(brokerSumRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideUpdateSessionDaoSource(
        authRepo: AuthRepo
    ): UpdateSessionPinDaoUseCase {
        return UpdateSessionPinDaoUseCase(authRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideListStockParamDaoSource(
        stockParamRepo: StockParamRepo
    ): GetListStockParamDaoUseCase {
        return GetListStockParamDaoUseCase(stockParamRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideIndiceDataSource(
        globalMarketRepo: GlobalMarketRepo
    ): GetIndiceDataUseCase {
        return GetIndiceDataUseCase(globalMarketRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideStartIndiceDataSource(
        globalMarketRepo: GlobalMarketRepo
    ): StartIndiceDataUseCase {
        return StartIndiceDataUseCase(globalMarketRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSetListenerIndiceDataUseCaseSource(
        globalMarketRepo: GlobalMarketRepo
    ): SetListenerIndiceDataUseCase {
        return SetListenerIndiceDataUseCase(globalMarketRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSubscribeAllIndiceDataSource(
        globalMarketRepo: GlobalMarketRepo
    ): SubscribeAllIndiceDataUseCase {
        return SubscribeAllIndiceDataUseCase(globalMarketRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSubscribeIndiceDataSource(
        globalMarketRepo: GlobalMarketRepo
    ): SubscribeIndiceDataUseCase {
        return SubscribeIndiceDataUseCase(globalMarketRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideUnSubscribeAllIndiceDataSource(
        globalMarketRepo: GlobalMarketRepo
    ): UnSubscribeAllIndiceDataUseCase {
        return UnSubscribeAllIndiceDataUseCase(globalMarketRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideUnSubscribeIndiceDataSource(
        globalMarketRepo: GlobalMarketRepo
    ): UnSubscribeIndiceDataUseCase {
        return UnSubscribeIndiceDataUseCase(globalMarketRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideStopIndiceDataSource(
        globalMarketRepo: GlobalMarketRepo
    ): StopIndiceDataUserCase {
        return StopIndiceDataUserCase(globalMarketRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideSectorDataSource(
        sectorRepo: SectorRepo
    ): GetIndexSectorUseCase {
        return GetIndexSectorUseCase(sectorRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideIndexSectorDetailDataSource(
        sectorRepo: SectorRepo
    ): GetIndexSectorDetailDataUseCase {
        return GetIndexSectorDetailDataUseCase(sectorRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideInsertStockNotationDaoSource(
        stockParamRepo: StockParamRepo
    ): InsertStockNotationDaoUseCase {
        return InsertStockNotationDaoUseCase(stockParamRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetAllStockNotationDaoUseCaseSource(
        stockParamRepo: StockParamRepo
    ): GetAllStockNotationDaoUseCase {
        return GetAllStockNotationDaoUseCase(stockParamRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideInsertAllStockNotationDaoUseCaseSource(
        stockParamRepo: StockParamRepo
    ): InsertAllStockNotationDaoUseCase {
        return InsertAllStockNotationDaoUseCase(stockParamRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetNotationByStockCodeDaoSource(
        stockParamRepo: StockParamRepo
    ): GetNotationByStockCodeDaoUseCase {
        return GetNotationByStockCodeDaoUseCase(stockParamRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideStockIndexSectorDataSource(
        sectorRepo: SectorRepo
    ): GetStockIndexSectorUseCase {
        return GetStockIndexSectorUseCase(sectorRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideStockRankInfoDaoSource(
        categoryRepo: CategoryRepo
    ): GetStockRankInfoUseCase {
        return GetStockRankInfoUseCase(categoryRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideBrokerListDataSource(
        brokerSumRepo: BrokerSumRepo
    ): GetBrokerListUseCase {
        return GetBrokerListUseCase(brokerSumRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideBrokerRankActivityDataSource(
        brokerSumRepo: BrokerSumRepo
    ): GetBrokerRankActivityUseCase {
        return GetBrokerRankActivityUseCase(brokerSumRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideBrokerRankRankingDataSource(
        brokerSumRepo: BrokerSumRepo
    ): GetBrokerRankRankingUseCase {
        return GetBrokerRankRankingUseCase(brokerSumRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideNewsRssFeedDataSource(
        newsRepo: NewsRepo
    ): GetNewsRssFeedUseCase {
        return GetNewsRssFeedUseCase(newsRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideStockInfoDetailDataSource(
        stockDetailRepo: StockDetailRepo
    ): GetStockInfoDetailUseCase {
        return GetStockInfoDetailUseCase(stockDetailRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideAdvanceOrderInfoDataSource(
        orderRepo: OrderRepo
    ): GetAdvanceOrderInfoUseCase {
        return GetAdvanceOrderInfoUseCase(orderRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideFibonacciPivotPointDataSource(
        analysisRepo: AnalysisRepo
    ): GetFibonacciPivotPointUseCase {
        return GetFibonacciPivotPointUseCase(analysisRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideChartIntradayPriceDataSource(
        analysisRepo: AnalysisRepo
    ): GetChartIntradayPriceUseCase {
        return GetChartIntradayPriceUseCase(analysisRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideRightIssueDataSource(
        rightIssueRepo: RightIssueRepo
    ): GetRightIssueInfoUseCase {
        return GetRightIssueInfoUseCase(rightIssueRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideCalendarDataSource(
        calendarRepo: CalendarRepo
    ): GetCalendarByDateInRangeUseCase {
        return GetCalendarByDateInRangeUseCase(calendarRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideWithdrawAdvancedOrderDataSource(
        orderRepo: OrderRepo
    ): SendWithdrawAdvancedOrderUseCase {
        return SendWithdrawAdvancedOrderUseCase(orderRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideTradeListDataSource(
        orderRepo: OrderRepo
    ): GetTradeListUseCase {
        return GetTradeListUseCase(orderRepo)
    }
    
    @Provides
    @ViewModelScoped
    fun provideAutoOrderDataSource(
        orderRepo: OrderRepo
    ): SendAutoOrderUseCase {
        return SendAutoOrderUseCase(orderRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteNotationByCodeDaoUseCaseSource(
        repository: StockParamRepo
    ): DeleteNotationByCodeDaoUseCase {
        return DeleteNotationByCodeDaoUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideClearStockNotationDB(
        repository: StockParamRepo
    ): ClearStockNotationDB {
        return ClearStockNotationDB(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideLogoutDataSource(
        repository: AuthRepo
    ): GetLogoutUseCase {
        return GetLogoutUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideExerciseOrderListDataSource(
        repository: RightIssueRepo
    ): GetExerciseOrderListUseCase {
        return GetExerciseOrderListUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideSendExerciseOrderDataSource(
        repository: RightIssueRepo
    ): SendExerciseOrderUseCase {
        return SendExerciseOrderUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideSendWithdrawExerciseOrderDataSource(
        repository: RightIssueRepo
    ): SendWithdrawExerciseOrderUseCase {
        return SendWithdrawExerciseOrderUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideMarketSessionDataSource(
        repo: GlobalMarketRepo
    ): GetMarketSessionUseCase {
        return GetMarketSessionUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGlobalCommoditiesUseCaseDataSource(
        repo: GlobalMarketRepo
    ): GetGlobalCommoditiesUseCase {
        return GetGlobalCommoditiesUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGlobalCurrencyUseCaseDataSource(
        repo: GlobalMarketRepo
    ): GetGlobalCurrencyUseCase {
        return GetGlobalCurrencyUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGlobalIndexUseCaseDataSource(
        repo: GlobalMarketRepo
    ): GetGlobalIndexUseCase {
        return GetGlobalIndexUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideWithdrawCashSource(
        repo: ProfileRepo
    ): GetWithdrawCashUseCase {
        return GetWithdrawCashUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideWithdrawCashListSource(
        repo: ProfileRepo
    ): GetRdnHistoryUseCase {
        return GetRdnHistoryUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetBannerLoginSource(
        repo: NewsRepo
    ): GetBannerLoginUseCase {
        return GetBannerLoginUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetTradeHistorySource(
        repo: OrderRepo
    ): GetTradeListHistoryUseCase {
        return GetTradeListHistoryUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetValidateSessionSource(
        repo: AuthRepo
    ): GetValidateSessionUseCase {
        return GetValidateSessionUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideSendPingUseCaseSource(
        repo: AuthRepo
    ): SendPingUseCase {
        return SendPingUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideStartAllConsumerUseCaseSource(
        repo: AuthRepo
    ): StartAllConsumerUseCase {
        return StartAllConsumerUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetValidateSessionByPinSource(
        repo: AuthRepo
    ): GetValidateSessionByPinUseCase {
        return GetValidateSessionByPinUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetResearchNewsSource(
        repo: NewsRepo
    ): GetResearchNewsUseCase {
        return GetResearchNewsUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetStockPickReportUseCaseSource(
        repo: NewsRepo
    ): GetStockPickReportUseCase {
        return GetStockPickReportUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideAddPriceAlertUseCaseSource(
        repo: PriceAlertRepo
    ): AddPriceAlertUseCase {
        return AddPriceAlertUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetListPriceAlertUseCaseSource(
        repo: PriceAlertRepo
    ): GetListPriceAlertUseCase {
        return GetListPriceAlertUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideRemovePriceAlertUseCaseSource(
        repo: PriceAlertRepo
    ): RemovePriceAlertUseCase {
        return RemovePriceAlertUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetSessionPinOrderListDaoUseCaseSource(
        authRepo: AuthRepo
    ): GetSessionPinOrderListDaoUseCase {
        return GetSessionPinOrderListDaoUseCase(authRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetNotificationHistoryUseCaseSource(
        repo: NotificationRepo
    ): GetNotificationHistoryUseCase {
        return GetNotificationHistoryUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetGlobalRankUseCaseSource(
        repo: GlobalMarketRepo
    ): GetGlobalRankUseCase {
        return GetGlobalRankUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetBrokerSummaryByStockNetUseCaseSource(
        repo: BrokerSumRepo
    ): GetBrokerSummaryByStockNetUseCase {
        return GetBrokerSummaryByStockNetUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetTopFiveFaqUseCaseSource(
        repo: ProfileRepo
    ): GetTopFiveFaqUseCase {
        return GetTopFiveFaqUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetFaqByCategoryUseCaseSource(
        repo: ProfileRepo
    ): GetFaqByCategoryUseCase {
        return GetFaqByCategoryUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetSearchFaqUseCaseCaseSource(
        repo: ProfileRepo
    ): GetSearchFaqUseCase {
        return GetSearchFaqUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetFaqUseCaseSource(
        repo: ProfileRepo
    ): GetFaqUseCase {
        return GetFaqUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetVideoTutorialUseCaseSource(
        repo: ProfileRepo
    ): GetVideoTutorialUseCase {
        return GetVideoTutorialUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetIPOListUseCase(
        repo: EIpoRepo
    ): GetIPOListUseCase {
        return GetIPOListUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetIPOInfoUseCase(
        repo: EIpoRepo
    ): GetIPOInfoUseCase {
        return GetIPOInfoUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetIPOOrderListUseCase(
        repo: EIpoRepo
    ): GetIPOOrderListUseCase {
        return GetIPOOrderListUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun providesendEipoOrderUseCase(
        repo: OrderRepo
    ): SendEipoOrderUseCase {
        return SendEipoOrderUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetNewsFeedUseCase(
        repo: NewsRepo
    ): GetNewsFeedUseCase {
        return GetNewsFeedUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetNewsFeedByStockUseCase(
        repo: NewsRepo
    ): GetNewsFeedByStockUseCase {
        return GetNewsFeedByStockUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetNewsFeedSearchUseCase(
        repo: NewsRepo
    ): GetNewsFeedSearchUseCase {
        return GetNewsFeedSearchUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetResearchContentSearchUseCase(
        repo: NewsRepo
    ): GetResearchContentSearchUseCase {
        return GetResearchContentSearchUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetDefaultFilterUseCase(
        repo: RunningTradeRepo
    ): GetFilterRunningTradeUseCase {
        return GetFilterRunningTradeUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideSetDefaultFilterUseCase(
        repo: RunningTradeRepo
    ): SetDefaultFilterRunningTradeUseCase {
        return SetDefaultFilterRunningTradeUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideResetDefaultFilterUseCase(
        repo: RunningTradeRepo
    ): ResetDefaultFilterRunningTradeUseCase {
        return ResetDefaultFilterRunningTradeUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetCorporateActionTabByStockCodeUseCase(
        repo: NewsRepo
    ): GetCorporateActionTabByStockCodeUseCase {
        return GetCorporateActionTabByStockCodeUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetTradeListHistoryGroupUseCase(
        repo: OrderRepo
    ): GetTradeListHistoryGroupUseCase {
        return GetTradeListHistoryGroupUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetTradeListHistoryGroupDetailUseCase(
        repo: OrderRepo
    ): GetTradeListHistoryGroupDetailUseCase {
        return GetTradeListHistoryGroupDetailUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetIpAddressUseCase(
        repo: AuthRepo
    ): GetIpAddressUseCase {
        return GetIpAddressUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideRealizedGainLossYearUseCase(
        portfolioRepo: PortfolioRepo
    ): GetRealizedGainLossByYearUseCase {
        return GetRealizedGainLossByYearUseCase(portfolioRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideRealizedGainLossMonthUseCase(
        portfolioRepo: PortfolioRepo
    ): GetRealizedGainLossByMonthUseCase {
        return GetRealizedGainLossByMonthUseCase(portfolioRepo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetTrustedDeviceUseCase(
        repo: AuthRepo
    ): GetTrustedDeviceUsecase {
        return GetTrustedDeviceUsecase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideSendOtpTrustedDeviceUseCase(
        repo: AuthRepo
    ): SendOtpTrustedDeviceUsecase {
        return SendOtpTrustedDeviceUsecase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideVerifyOtpTrustedDeviceUseCase(
        repo: AuthRepo
    ): VerifyOtpTrustedDeviceUsecase {
        return VerifyOtpTrustedDeviceUsecase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteTrustedDeviceUseCase(
        repo: AuthRepo
    ): DeleteTrustedDeviceUsecase {
        return DeleteTrustedDeviceUsecase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideExerciseSessionUseCase(
        repo: RightIssueRepo
    ): GetExerciseSessionUseCase {
        return GetExerciseSessionUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideCloseChannelUseCase(
        repo: AuthRepo
    ): CloseChannelUseCase {
        return CloseChannelUseCase(repo)
    }
}