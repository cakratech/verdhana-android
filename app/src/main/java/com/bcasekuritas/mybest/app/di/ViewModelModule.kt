package com.bcasekuritas.mybest.app.di

import androidx.lifecycle.ViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.ViewModelKey
import com.bcasekuritas.mybest.app.feature.activity.main.MainViewModel
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleViewModel
import com.bcasekuritas.mybest.app.feature.home.HomeViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import com.bcasekuritas.mybest.app.feature.brokersummary.BrokerSummaryViewModel
import com.bcasekuritas.mybest.app.feature.brokersummary.tabactivity.ActivityTabBrokerSummaryViewModel
import com.bcasekuritas.mybest.app.feature.brokersummary.tabbystock.ByStockTabBrokerSummaryViewModel
import com.bcasekuritas.mybest.app.feature.brokersummary.tabranking.RankingTabBrokerSummaryViewModel
import com.bcasekuritas.mybest.app.feature.categories.CategoriesViewModel
import com.bcasekuritas.mybest.app.feature.dialog.bottom.runningtrade.DialogFilterRunningTradeViewModel
import com.bcasekuritas.mybest.app.feature.dialog.coachmark.CoachmarkViewModel
import com.bcasekuritas.mybest.app.feature.dialog.order.viewmodel.DialogOrderBuyViewModel
import com.bcasekuritas.mybest.app.feature.dialog.order.viewmodel.DialogOrderSellViewModel
import com.bcasekuritas.mybest.app.feature.profile.profilelanding.ProfileViewModel
import com.bcasekuritas.mybest.app.feature.discover.DiscoverViewModel
import com.bcasekuritas.mybest.app.feature.e_ipo.EIPOViewModel
import com.bcasekuritas.mybest.app.feature.e_ipo.eipodetail.EIPODetailViewModel
import com.bcasekuritas.mybest.app.feature.e_ipo.eipodetail.about.EIPOAboutViewModel
import com.bcasekuritas.mybest.app.feature.e_ipo.eipodetail.stages.EIPOStagesViewModel
import com.bcasekuritas.mybest.app.feature.e_ipo.order.OrderEIPOViewModel
import com.bcasekuritas.mybest.app.feature.e_ipo.orderlist.EipoOrderListViewModel
import com.bcasekuritas.mybest.app.feature.help.HelpViewModel
import com.bcasekuritas.mybest.app.feature.help.searchquestion.SearchQuestionsViewModel
import com.bcasekuritas.mybest.app.feature.index.IndexViewModel
import com.bcasekuritas.mybest.app.feature.index.detail.IndexDetailViewModel
import com.bcasekuritas.mybest.app.feature.linesetting.LineSettingViewModel
import com.bcasekuritas.mybest.app.feature.news.NewsViewModel
import com.bcasekuritas.mybest.app.feature.news.tabnews.TabNewsViewModel
import com.bcasekuritas.mybest.app.feature.news.tabresearch.TabResearchViewModel
import com.bcasekuritas.mybest.app.feature.notification.NotificationViewModel
import com.bcasekuritas.mybest.app.feature.notification.generalnotification.GeneralNotificationViewModel
import com.bcasekuritas.mybest.app.feature.notification.transactionnotification.TransactionNotificationViewModel
import com.bcasekuritas.mybest.app.feature.order.conditionadvanced.ConditionAdvancedViewModel
import com.bcasekuritas.mybest.app.feature.order.orderdetail.OrderDetailViewModel
import com.bcasekuritas.mybest.app.feature.pin.accountdisable.AccountDisableViewModel
import com.bcasekuritas.mybest.app.feature.portfolio.portfoliocash.PortfolioCashViewModel
import com.bcasekuritas.mybest.app.feature.portfolio.portfoliodetail.PortfolioDetailViewModel
import com.bcasekuritas.mybest.app.feature.portfolio.portfoliodetail.tabhistory.HistoryTabPortfolioDetailViewModel
import com.bcasekuritas.mybest.app.feature.portfolio.portfoliodetail.tabportfolio.PortfolioTabPortfolioDetailViewModel
import com.bcasekuritas.mybest.app.feature.portfolio.portfolioreturn.PortfolioReturnViewModel
import com.bcasekuritas.mybest.app.feature.portfolio.realized.RealizedViewModel
import com.bcasekuritas.mybest.app.feature.portfolio.stoplosstakeprofit.StopLossTakeProfitViewModel
import com.bcasekuritas.mybest.app.feature.portfolio.tabhistory.HistoryTabPortfolioViewModel
import com.bcasekuritas.mybest.app.feature.portfolio.taborders.OrdersTabPortfolioViewModel
import com.bcasekuritas.mybest.app.feature.portfolio.tabportfolio.PortfolioTabViewModel
import com.bcasekuritas.mybest.app.feature.rdn.detailhistory.DetailRdnHistoryViewModel
import com.bcasekuritas.mybest.app.feature.rdn.history.RdnHistoryViewModel
import com.bcasekuritas.mybest.app.feature.rdn.topup.TopUpViewModel
import com.bcasekuritas.mybest.app.feature.rdn.withdraw.WithdrawViewModel
import com.bcasekuritas.mybest.app.feature.rightissue.RightIssueViewModel
import com.bcasekuritas.mybest.app.feature.rightissue.detail.ExerciseDetailViewModel
import com.bcasekuritas.mybest.app.feature.rightissue.exercise.ExerciseViewModel
import com.bcasekuritas.mybest.app.feature.rightissue.orderlist.ExerciseOrderListViewModel
import com.bcasekuritas.mybest.app.feature.runningtrade.RunningTradeViewModel
import com.bcasekuritas.mybest.app.feature.sectors.SectorViewModel
import com.bcasekuritas.mybest.app.feature.sectors.detail.SectorDetailViewModel
import com.bcasekuritas.mybest.app.feature.stockdetail.daily.StockDetailDailyViewModel
import com.bcasekuritas.mybest.app.feature.stockdetail.about.StockDetailAboutViewModel
import com.bcasekuritas.mybest.app.feature.stockdetail.analysis.StockDetailAnalysisViewModel
import com.bcasekuritas.mybest.app.feature.stockdetail.brokersummary.StockDetailBrokerSummaryViewModel
import com.bcasekuritas.mybest.app.feature.stockdetail.corporateaction.StockDetailCorporateActionViewModel
import com.bcasekuritas.mybest.app.feature.stockdetail.financial.balancesheet.BalanceSheetViewModel
import com.bcasekuritas.mybest.app.feature.stockdetail.financial.cashflow.CashflowViewModel
import com.bcasekuritas.mybest.app.feature.stockdetail.financial.incomestatement.IncomeStatementViewModel
import com.bcasekuritas.mybest.app.feature.stockdetail.keystats.KeyStatsViewModel
import com.bcasekuritas.mybest.app.feature.stockdetail.runningtrade.StockDetailRunningTradeViewModel
import com.bcasekuritas.mybest.app.feature.stockdetail.trade.TradeViewModel
import com.bcasekuritas.mybest.app.feature.stockpick.StockPickViewModel

@Module
@InstallIn(SingletonComponent::class)
abstract class ViewModelModule {

    /**
    ACTIVITY VIEW MODEL
     */

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(MainViewModel::class)
    abstract fun provideMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(MiddleViewModel::class)
    abstract fun provideMiddleViewModel(viewModel: MiddleViewModel): ViewModel

    /**
    FRAGMENT VIEW MODEL
     */

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(HomeViewModel::class)
    abstract fun provideHomeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(ProfileViewModel::class)
    abstract fun provideProfileViewModel(viewModel: ProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(TopUpViewModel::class)
    abstract fun provideTopUpViewModel(viewModel: TopUpViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(WithdrawViewModel::class)
    abstract fun provideWithdrawViewModel(viewModel: WithdrawViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(RdnHistoryViewModel::class)
    abstract fun provideRdnHistoryViewModel(viewModel: RdnHistoryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(DetailRdnHistoryViewModel::class)
    abstract fun provideDetailRdnHistoryViewModel(viewModel: DetailRdnHistoryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(CategoriesViewModel::class)
    abstract fun provideCategoriesViewModel(viewModel: CategoriesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped    
    @ViewModelKey(DiscoverViewModel::class)
    abstract fun provideDiscoverViewModelViewModel(viewModel: DiscoverViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(RunningTradeViewModel::class)
    abstract fun provideRunningTradeViewModelViewModel(viewModel: RunningTradeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(PortfolioTabViewModel::class)
    abstract fun provideTabPortfolioViewModelViewModel(viewModel: PortfolioTabViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(OrdersTabPortfolioViewModel::class)
    abstract fun provideOrdersTabPortfolioViewModelViewModel(viewModel: OrdersTabPortfolioViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(HistoryTabPortfolioViewModel::class)
    abstract fun provideHistoryTabPortfolioViewModelViewModel(viewModel: HistoryTabPortfolioViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(StockDetailRunningTradeViewModel::class)
    abstract fun provideStockDetailRunningTradeViewModel(viewModel: StockDetailRunningTradeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(OrderDetailViewModel::class)
    abstract fun provideOrderDetailViewModel(viewModel: OrderDetailViewModel): ViewModel
    
    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(KeyStatsViewModel::class)
    abstract fun provideKeyStatsViewModel(viewModel: KeyStatsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(TradeViewModel::class)
    abstract fun provideTradeViewModel(viewModel: TradeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(IncomeStatementViewModel::class)
    abstract fun provideIncomeStatementViewModel(viewModel: IncomeStatementViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(BalanceSheetViewModel::class)
    abstract fun provideBalanceSheetViewModel(viewModel: BalanceSheetViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(CashflowViewModel::class)
    abstract fun provideCashflowViewModel(viewModel: CashflowViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(OrdersTabPortfolioViewModel::class)
    abstract fun provideOrdersTabPortfolioViewModel(viewModel: OrdersTabPortfolioViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(StockDetailDailyViewModel::class)
    abstract fun provideStockDetailDailyViewModel(viewModel: StockDetailDailyViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(StockDetailAboutViewModel::class)
    abstract fun provideStockDetailAboutViewModel(viewModel: StockDetailAboutViewModel): ViewModel
  
    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(PortfolioDetailViewModel::class)
    abstract fun providePortfolioDetailViewModel(viewModel: PortfolioDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(PortfolioTabPortfolioDetailViewModel::class)
    abstract fun providePortfolioTabPortfolioDetailViewModel(viewModel: PortfolioTabPortfolioDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(HistoryTabPortfolioDetailViewModel::class)
    abstract fun provideHistoryTabPortfolioDetailViewModel(viewModel: HistoryTabPortfolioDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(StockDetailAnalysisViewModel::class)
    abstract fun provideStockDetailAnalysisViewModel(viewModel: StockDetailAnalysisViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(StockPickViewModel::class)
    abstract fun provideStockPickViewModel(viewModel: StockPickViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(StockDetailBrokerSummaryViewModel::class)
    abstract fun provideStockDetailBrokerSummaryViewModel(viewModel: StockDetailBrokerSummaryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(IndexViewModel::class)
    abstract fun provideIndexViewModel(viewModel: IndexViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(SectorViewModel::class)
    abstract fun provideSectorViewModel(viewModel: SectorViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(IndexDetailViewModel::class)
    abstract fun provideIndexDetailViewModel(viewModel: IndexDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(BrokerSummaryViewModel::class)
    abstract fun provideBrokerSummaryViewModel(viewModel: BrokerSummaryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(ByStockTabBrokerSummaryViewModel::class)
    abstract fun provideByStockTabBrokerSummaryViewModel(viewModel: ByStockTabBrokerSummaryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(ActivityTabBrokerSummaryViewModel::class)
    abstract fun provideActivityTabBrokerSummaryViewModel(viewModel: ActivityTabBrokerSummaryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(RankingTabBrokerSummaryViewModel::class)
    abstract fun provideRankingTabBrokerSummaryViewModel(viewModel: RankingTabBrokerSummaryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(NewsViewModel::class)
    abstract fun provideNewsViewModel(viewModel: NewsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(TabNewsViewModel::class)
    abstract fun provideTabNewsViewModel(viewModel: TabNewsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(TabResearchViewModel::class)
    abstract fun provideTabResearchViewModel(viewModel: TabResearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(PortfolioCashViewModel::class)
    abstract fun providePortfolioCashViewModel(viewModel: PortfolioCashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(StopLossTakeProfitViewModel::class)
    abstract fun provideStopLossTakeProfitViewModel(viewModel: StopLossTakeProfitViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(ConditionAdvancedViewModel::class)
    abstract fun provideConditionAdvancedViewModel(viewModel: ConditionAdvancedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(RightIssueViewModel::class)
    abstract fun provideRightIssueViewModel(viewModel: RightIssueViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(ExerciseViewModel::class)
    abstract fun provideExerciseViewModel(viewModel: ExerciseViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(LineSettingViewModel::class)
    abstract fun provideLineSettingViewModel(viewModel: LineSettingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(ExerciseOrderListViewModel::class)
    abstract fun provideExerciseOrderListViewModel(viewModel: ExerciseOrderListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(ExerciseDetailViewModel::class)
    abstract fun provideExerciseDetailViewModel(viewModel: ExerciseDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(DialogOrderBuyViewModel::class)
    abstract fun provideDialogOrderBuyViewModel(viewModel: DialogOrderBuyViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(DialogOrderSellViewModel::class)
    abstract fun provideDialogOrderSellViewModel(viewModel: DialogOrderSellViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(PortfolioReturnViewModel::class)
    abstract fun providerPortfolioReturnViewModel(viewModel: PortfolioReturnViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(RealizedViewModel::class)
    abstract fun providerRealizedViewModel(viewModel: RealizedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(AccountDisableViewModel::class)
    abstract fun providerAccountDisableViewModel(viewModel: AccountDisableViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(NotificationViewModel::class)
    abstract fun providerNotificationViewModel(viewModel: NotificationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(TransactionNotificationViewModel::class)
    abstract fun providerTransactionNotificationViewModel(viewModel: TransactionNotificationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(GeneralNotificationViewModel::class)
    abstract fun providerGeneralNotificationViewModel(viewModel: GeneralNotificationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(SectorDetailViewModel::class)
    abstract fun provideSectorDetailViewModel(viewModel: SectorDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(HelpViewModel::class)
    abstract fun provideHelpViewModel(viewModel: HelpViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(SearchQuestionsViewModel::class)
    abstract fun provideSearchQuestionsViewModel(viewModel: SearchQuestionsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(DiscoverViewModel::class)
    abstract fun provideDiscoverViewModel(viewModel: DiscoverViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(EIPOViewModel::class)
    abstract fun provideEIPOViewModel(viewModel: EIPOViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(EIPODetailViewModel::class)
    abstract fun provideEIPODetailViewModel(viewModel: EIPODetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(EIPOStagesViewModel::class)
    abstract fun provideEIPOStagesViewModel(viewModel: EIPOStagesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(EIPOAboutViewModel::class)
    abstract fun provideEIPOAboutViewModel(viewModel: EIPOAboutViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(OrderEIPOViewModel::class)
    abstract fun provideOrderEIPOViewModel(viewModel: OrderEIPOViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(EipoOrderListViewModel::class)
    abstract fun provideEipoOrderListViewModel(viewModel: EipoOrderListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(DialogFilterRunningTradeViewModel::class)
    abstract fun provideDialogFilterRunningTradeViewModel(viewModel: DialogFilterRunningTradeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(StockDetailCorporateActionViewModel::class)
    abstract fun provideStockDetailCorporateActionViewModel(viewModel: StockDetailCorporateActionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelScoped
    @ViewModelKey(CoachmarkViewModel::class)
    abstract fun provideCoachmarkViewModel(viewModel: CoachmarkViewModel): ViewModel

//    @Binds
//    @IntoMap
//    @ViewModelScoped
//    @ViewModelKey(AboutViewModel::class)
//    abstract fun provideAboutViewModel(viewModel: AboutViewModel): ViewModel


    /**
     DIALOG VIEW MODEL
     */

//    @Binds
//    @IntoMap
//    @ViewModelScoped
//    @ViewModelKey(DialogInfoBottomViewModel::class)
//    abstract fun provideDialogInfoBottomViewModel(viewModel: DialogInfoBottomViewModel): ViewModel

}