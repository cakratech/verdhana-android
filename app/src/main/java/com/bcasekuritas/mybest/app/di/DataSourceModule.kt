package com.bcasekuritas.mybest.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.bcasekuritas.mybest.BcasApp
import com.bcasekuritas.mybest.app.data.dao.AccountDao
import com.bcasekuritas.mybest.app.data.dao.BiometricDao
import com.bcasekuritas.mybest.app.data.dao.FilterDao
import com.bcasekuritas.mybest.app.data.dao.OrderReplyDao
import com.bcasekuritas.mybest.app.data.dao.SessionDao
import com.bcasekuritas.mybest.app.data.dao.StockNotationDao
import com.bcasekuritas.mybest.app.data.dao.StockParamDao
import com.bcasekuritas.mybest.app.data.datasource.remote.*
import com.bcasekuritas.mybest.app.data.datasource.stub.*
import com.bcasekuritas.mybest.app.domain.datasource.remote.*
import com.bcasekuritas.mybest.app.domain.datasource.stub.*
import com.bcasekuritas.mybest.ext.stub.StubUtil
import com.bcasekuritas.rabbitmq.network.OLTService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    /** Source DataSource */
    @Singleton
    @Provides
    fun provideSourceStubDataSource(
        myApplication: BcasApp,
        stubUtil: StubUtil
    ): SourceStubDataSource = SourceStubDataSourceImpl(
        myApplication.applicationContext, stubUtil)

    @Singleton
    @Provides
    fun provideAuthDataSource (
        oltService: OLTService,
        sessionDao: SessionDao,
        biometricDao: BiometricDao
    ): AuthDataSource = AuthDataSourceImpl(oltService, sessionDao, biometricDao)

    @Singleton
    @Provides
    fun providePortfolioDataSource(
        oltService: OLTService
    ): PortfolioDataSource = PortfolioDataSourceImpl(oltService)

    @Singleton
    @Provides
    fun provideWatchlistDataSource(
        oltService: OLTService
    ): WatchlistDataSource = WatchlistDataSourceImpl(oltService)

    @Singleton
    @Provides
    fun provideRunningTradeDataSource(
        oltService: OLTService,
        filterDao: FilterDao
    ): RunningTradeDataSource = RunningTradeDataSourceImpl(oltService, filterDao)

    @Singleton
    @Provides
    fun provideStockDataSource (
        oltService: OLTService
    ): StockDetailDataSource = StockDetailDataSourceImpl(oltService)

    @Singleton
    @Provides
    fun provideOrderDataSource (
        oltService: OLTService,
        orderReplyDao: OrderReplyDao
    ): OrderDataSource = OrderDataSourceImpl(oltService, orderReplyDao)
    
    @Singleton
    @Provides
    fun provideTradeBookDataSource (
        oltService: OLTService
    ): TradeBookDataSource = TradeBookDataSourceImpl(oltService)

    @Singleton
    @Provides
    fun provideStockTradeDataSource (
        oltService: OLTService
    ): StockTradeDataSource = StockTradeDataSourceImpl(oltService)

    @Singleton
    @Provides
    fun provideFastOrderDataSource (
        oltService: OLTService
    ): FastOrderDataSource = FastOrderDataSourceImpl(oltService)

    @Singleton
    @Provides
    fun provideNewsDataSource (
        oltService: OLTService
    ): NewsDataSource = NewsDataSourceImpl(oltService)

    @Singleton
    @Provides
    fun provideFinancialDataSource (
        oltService: OLTService
    ): FinancialDataSource = FinancialDataSourceImpl(oltService)

    @Singleton
    @Provides
    fun provideTradeSummaryDataSource (
        oltService: OLTService
    ): TradeSummaryDataSource = TradeSummaryDataSourceImpl(oltService)

    @Singleton
    @Provides
    fun provideCompanyProfileDataSource (
        oltService: OLTService
    ): CompanyProfileDataSource = CompanyProfileDataSourceImpl(oltService)
    
    @Singleton
    @Provides
    fun provideProfileDataSource (
        oltService: OLTService,
        accountDao: AccountDao
    ): ProfileDataSource = ProfileDataSourceImpl(oltService, accountDao)

    @Singleton
    @Provides
    fun provideStockParamDataSource (
        oltService: OLTService,
        stockParamDao: StockParamDao,
        stockNotationDao: StockNotationDao
    ): StockParamDataSource = StockParamDataSourceImpl(oltService, stockParamDao, stockNotationDao)

    @Singleton
    @Provides
    fun provideAnalysisDataSource (
        oltService: OLTService
    ): AnalysisDataSource = AnalysisDataSourceImpl(oltService)

    @Singleton
    @Provides
    fun provideBrokerSumDataSource (
        oltService: OLTService
    ): BrokerSumDataSource = BrokerSumDataSourceImpl(oltService)

    @Singleton
    @Provides
    fun provideGlobalMarketDataSource (
        oltService: OLTService
    ): GlobalMarketDataSource = GlobalMarketDataSourceImpl(oltService)

    @Singleton
    @Provides
    fun provideSectorDataSource (
        oltService: OLTService
    ): SectorDataSource = SectorDataSourceImpl(oltService)

    @Singleton
    @Provides
    fun provideCalendarDataSource (
        oltService: OLTService
    ): CalendarDataSource = CalendarDataSourceImpl(oltService)

    @Singleton
    @Provides
    fun provideCategoriesDataSource (
        oltService: OLTService
    ): CategoryDataSource = CategoryDataSourceImpl(oltService)

    @Singleton
    @Provides
    fun provideRightIssueDataSource (
        oltService: OLTService
    ): RightIssueDataSource = RightIssueDataSourceImpl(oltService)

    @Singleton
    @Provides
    fun providePriceAlertDataSource (
        oltService: OLTService
    ): PriceAlertDataSource = PriceAlertDataSourceImpl(oltService)

    @Singleton
    @Provides
    fun provideNotificationDataSource (
        oltService: OLTService
    ): NotificationDataSource = NotificationDataSourceImpl(oltService)

    @Singleton
    @Provides
    fun provideEIPODataSource(
        oltService: OLTService
    ): EIpoDataSource = EIpoDataSourceImpl(oltService)
}