package com.bcasekuritas.mybest.app.di

import android.content.Context
import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.bcasekuritas.mybest.app.data.repositoriesimpl.*
import com.bcasekuritas.mybest.app.domain.datasource.remote.*
import com.bcasekuritas.mybest.app.domain.repositories.*
import com.bcasekuritas.mybest.ext.common.AppLifecycleObserver
import com.bcasekuritas.mybest.ext.common.ServiceLifecycleManager
import com.bcasekuritas.mybest.ext.constant.ConstKeys
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.prefs.SharedPreferenceManager
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
import com.bcasekuritas.rabbitmq.connection.BasicMQConnection
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.connection.MQConnectionListener
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.network.OLTServiceImpl
import com.bcasekuritas.rabbitmq.pool.ChannelPooled
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun provideIMQConnectionListener(): IMQConnectionListener {
        return MQConnectionListener()
    }

    @Singleton
    @Provides
    fun providesMQ(
        @ApplicationContext context: Context,
        imqConnectionListener: IMQConnectionListener,
    ): BasicMQConnection {
        val sharedPreferences = SharedPreferenceManager(context)

        val checkConLine = sharedPreferences.connectionLine
        val connectionHts = sharedPreferences.connectionHts
        val connectionPort = sharedPreferences.connectionPort
        return when(checkConLine) {
            2 -> {
                // Connection Custom Line
                Log.d("MQConnection", "providesMQ: Custom Line")
                BasicMQConnection()
                    .virtualHost(ConstKeys.VHOST)
                    .uri(connectionHts)
                    .port(connectionPort)
                    .dataPayload(ConstKeys.DATA_METRIC)
                    .sequenceBuffer(ConstKeys.MUTIPLE_DATA_METRIC)
                    .requestedHeartbeat(60)
                    .handshakeTimeout(10000)
                    .connectionTimeout(10000)
                    .shutdownTimeout(10000)
                    .connectionListener(imqConnectionListener)
//        // TODO : EM => add if use self sign mechanism
//            .selfSignCertificate(true)
            }
            1 -> {
                // Connection DRC
                Log.d("MQConnection", "providesMQ: DRC")
                BasicMQConnection()
                    .virtualHost(ConstKeys.VHOST)
                    .uri(ConstKeys.END_POINT_DRC)
                    .port(ConstKeys.PORT)
                    .dataPayload(ConstKeys.DATA_METRIC)
                    .sequenceBuffer(ConstKeys.MUTIPLE_DATA_METRIC)
                    .requestedHeartbeat(60)
                    .handshakeTimeout(10000)
                    .connectionTimeout(10000)
                    .shutdownTimeout(10000)
                    .connectionListener(imqConnectionListener)
//        // TODO : EM => add if use self sign mechanism
//            .selfSignCertificate(true)
            }
            else -> {
                // Connection Primary
                Log.d("MQConnection", "providesMQ: Primary")
                BasicMQConnection()
                    .virtualHost(ConstKeys.VHOST)
                    .uri(ConstKeys.END_POINT)
                    .port(ConstKeys.PORT)
                    .dataPayload(ConstKeys.DATA_METRIC)
                    .sequenceBuffer(ConstKeys.MUTIPLE_DATA_METRIC)
                    .requestedHeartbeat(60)
                    .handshakeTimeout(10000)
                    .connectionTimeout(10000)
                    .shutdownTimeout(10000)
                    .connectionListener(imqConnectionListener)
        // TODO : EM => add if use self sign mechanism
            .selfSignCertificate(true)
            }
        }
    }

    @Singleton
    @Provides
    fun provideChannelPooled(basicMQConnection: BasicMQConnection): ChannelPooled {
        Log.d("channelMq", "----Create Channel Pool----")
        return ChannelPooled(basicMQConnection)
    }

    @Singleton
    @Provides
    fun provideOLTService(channelPooled: ChannelPooled?, basicMQConnection: BasicMQConnection?, imqConnectionListener: IMQConnectionListener): OLTService {
        return OLTServiceImpl(channelPooled, basicMQConnection, imqConnectionListener)
    }

//    @Provides
//    @Singleton
//    fun provideNetworkMonitor(
//        @ApplicationContext context: Context
//    ): NetworkMonitor {
//        return NetworkMonitor(context)
//    }


    @Provides
    @Singleton
    fun provideServiceLifecycleManager(
        @ApplicationContext context: Context
    ): ServiceLifecycleManager {
        return ServiceLifecycleManager(context)
    }

    @Provides
    @Singleton
    fun provideAppLifecycleObserver(manager: ServiceLifecycleManager): AppLifecycleObserver {
        return AppLifecycleObserver(manager)
    }


    // REPO
    @Singleton
    @Provides
    fun provideAuthRepository(
        @ApplicationContext context: Context,
        source: AuthDataSource,
        schedulerProvider: SchedulerProvider,
    ): AuthRepo = AuthRepoImpl(source, schedulerProvider, ErrorCodesMapper(context))


    // REPO
    @Singleton
    @Provides
    fun provideFastOrderRepository(
        @ApplicationContext context: Context,
        source: FastOrderDataSource,
        schedulerProvider: SchedulerProvider,
    ): FastOrderRepo = FastOrderRepoImpl(source, schedulerProvider, ErrorCodesMapper(context))

    @Singleton
    @Provides
    fun provideStockParamRepository(
        @ApplicationContext context: Context,
        source: StockParamDataSource,
        schedulerProvider: SchedulerProvider,
    ): StockParamRepo = StockParamRepoImpl(source, schedulerProvider, ErrorCodesMapper(context))

    @Singleton
    @Provides
    fun providePortoRepository(
        @ApplicationContext context: Context,
        source: PortfolioDataSource,
        schedulerProvider: SchedulerProvider,
    ): PortfolioRepo = PortfolioRepoImpl(source, schedulerProvider, ErrorCodesMapper(context))

    @Singleton
    @Provides
    fun provideWatchListRepository(
        @ApplicationContext context: Context,
        source: WatchlistDataSource,
        schedulerProvider: SchedulerProvider,
    ): WatchlistRepo = WatchlistRepoImpl(source, schedulerProvider, ErrorCodesMapper(context))

    @Singleton
    @Provides
    fun provideRunningTradeRepository(
        @ApplicationContext context: Context,
        source: RunningTradeDataSource,
        schedulerProvider: SchedulerProvider,
    ): RunningTradeRepo = RunningTradeRepoImpl(source, schedulerProvider, ErrorCodesMapper(context))

    @Singleton
    @Provides
    fun provideStockRepository(
        @ApplicationContext context: Context,
        source: StockDetailDataSource,
        schedulerProvider: SchedulerProvider,
    ): StockDetailRepo = StockDetailRepoImpl(source, schedulerProvider, ErrorCodesMapper(context))

    @Singleton
    @Provides
    fun provideTradeBookRepository(
        @ApplicationContext context: Context,
        source: TradeBookDataSource,
        schedulerProvider: SchedulerProvider,
    ): TradeBookRepo = TradeBookRepoImpl(source, schedulerProvider, ErrorCodesMapper(context))

    @Singleton
    @Provides
    fun provideSendOrderRepository(
        @ApplicationContext context: Context,
        source: OrderDataSource,
        schedulerProvider: SchedulerProvider,
    ): OrderRepo = OrderRepoImpl(source, schedulerProvider, ErrorCodesMapper(context))

    @Singleton
    @Provides
    fun provideNewsRepository(
        @ApplicationContext context: Context,
        source: NewsDataSource,
        schedulerProvider: SchedulerProvider,
    ): NewsRepo = NewsRepoImpl(source, schedulerProvider, ErrorCodesMapper(context))

    @Singleton
    @Provides
    fun providePriceAlertRepository(
        @ApplicationContext context: Context,
        source: PriceAlertDataSource,
        schedulerProvider: SchedulerProvider,
    ): PriceAlertRepo = PriceAlertRepoImpl(source)

    @Singleton
    @Provides
    fun provideFinancialRepository(
        @ApplicationContext context: Context,
        source: FinancialDataSource,
        schedulerProvider: SchedulerProvider,
    ): FinancialRepo = FinancialRepoImpl(source, schedulerProvider, ErrorCodesMapper(context))

    @Singleton
    @Provides
    fun provideTradeSummaryRepository(
        @ApplicationContext context: Context,
        source: TradeSummaryDataSource,
        schedulerProvider: SchedulerProvider,
    ): TradeSummaryRepo = TradeSummaryRepoImpl(source, schedulerProvider, ErrorCodesMapper(context))

    @Singleton
    @Provides
    fun provideCompanyProfileRepository(
        @ApplicationContext context: Context,
        source: CompanyProfileDataSource,
        schedulerProvider: SchedulerProvider,
    ): CompanyProfileRepo = CompanyProfileRepoImpl(source, schedulerProvider, ErrorCodesMapper(context))

    @Singleton
    @Provides
    fun provideProfileRepository(
        @ApplicationContext context: Context,
        source: ProfileDataSource,
        schedulerProvider: SchedulerProvider,
    ): ProfileRepo = ProfileRepoImpl(source, schedulerProvider, ErrorCodesMapper(context))

    @Singleton
    @Provides
    fun provideAnalysisRepository(
        @ApplicationContext context: Context,
        source: AnalysisDataSource,
        schedulerProvider: SchedulerProvider,
    ): AnalysisRepo = AnalysisRepoImpl(source, schedulerProvider, ErrorCodesMapper(context))

    @Singleton
    @Provides
    fun provideBrokerSumRepository(
        @ApplicationContext context: Context,
        source: BrokerSumDataSource,
        schedulerProvider: SchedulerProvider,
    ): BrokerSumRepo = BrokerSumRepoImpl(source, schedulerProvider, ErrorCodesMapper(context))

    @Singleton
    @Provides
    fun provideStockTradeRepository(
        @ApplicationContext context: Context,
        source: StockTradeDataSource,
        schedulerProvider: SchedulerProvider,
    ): StockTradeRepo = StockTradeRepoImpl(source, schedulerProvider, ErrorCodesMapper(context))

    @Singleton
    @Provides
    fun provideGlobalMarketRepository(
        @ApplicationContext context: Context,
        source: GlobalMarketDataSource,
        schedulerProvider: SchedulerProvider,
    ): GlobalMarketRepo = GlobalMarketRepoImpl(source, schedulerProvider, ErrorCodesMapper(context))

    @Singleton
    @Provides
    fun provideSectorRepository(
        @ApplicationContext context: Context,
        source: SectorDataSource,
        schedulerProvider: SchedulerProvider,
    ): SectorRepo = SectorRepoImpl(source, schedulerProvider, ErrorCodesMapper(context))

    @Singleton
    @Provides
    fun provideCategoriesRepository(
        @ApplicationContext context: Context,
        source: CategoryDataSource,
        schedulerProvider: SchedulerProvider,
    ): CategoryRepo = CategoryRepoImpl(source, schedulerProvider, ErrorCodesMapper(context))

    @Singleton
    @Provides
    fun provideRightIssueRepository(
        @ApplicationContext context: Context,
        source: RightIssueDataSource,
        schedulerProvider: SchedulerProvider,
    ): RightIssueRepo = RightIssueRepoImpl(source, schedulerProvider, ErrorCodesMapper(context))

    @Singleton
    @Provides
    fun provideCalendarRepository(
        @ApplicationContext context: Context,
        source: CalendarDataSource,
        schedulerProvider: SchedulerProvider,
    ): CalendarRepo = CalendarRepoImpl(source, schedulerProvider, ErrorCodesMapper(context))

    @Singleton
    @Provides
    fun provideNotificationRepository(
        @ApplicationContext context: Context,
        source: NotificationDataSource,
        schedulerProvider: SchedulerProvider,
    ): NotificationRepo = NotificationRepoImpl(source, schedulerProvider, ErrorCodesMapper(context))

    @Singleton
    @Provides
    fun provideEIPORepository(
        @ApplicationContext context: Context,
        source: EIpoDataSource,
        schedulerProvider: SchedulerProvider,
    ): EIpoRepo = EIpoRepoImpl(source, schedulerProvider, ErrorCodesMapper(context))
}