package com.bcasekuritas.rabbitmq.constant;

import com.bcasekuritas.rabbitmq.BuildConfig;

public class ConstantKeys {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("rabbitmq");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */

    private static native String getSaltKey(int environment);

    private static native String getPreferenceName(int environment);

    private static native String getLoginRpc(int environment);

    private static native String getAccountInfoRpc(int environment);

    private static native String getTradeBookRpc(int environment);

    private static native String getTradeBookTimeRpc(int environment);

    private static native String getStockTradeRpc(int environment);

    private static native String getCIFLevelPosRpc(int environment);

    private static native String getAccStockPosRpc(int environment);

    private static native String getCurrentMessageRpc(int environment);

    private static native String getOrderListRpc(int environment);

    private static native String getNewsRpc(int environment);

    private static native String getCompanyProfileRpc(int environment);

    private static native String getIncomeStatementRpc(int environment);

    private static native String getBalanceSheetRpc(int environment);

    private static native String getCashFlowRpc(int environment);

    private static native String getKeyStatRpc(int environment);
    private static native String getStockParamList(int environment);
    private static native String getOrderHistoryRpc(int environment);
    private static native String getEarningPerSharesRpc(int environment);
    private static native String getUserWatchList(int environment);
    private static native String addWatchlistCategory(int environment);
    private static native String addItemCategoryRpc(int environment);
    private static native String removeWatchlistCategoryRpc(int environment);
    private static native String removeItemCategoryRpc(int environment);

    private static native String getStockPickRpc(int environment);

    private static native String getNewsPromoRpc(int environment);

    private static native String getPerBandRpc(int environment);


    private static native String getPerDataRpc(int environment);

    private static native String getPbvBandRpc(int environment);

    private static native String getPbvDataRpc(int environment);

    private static native String getTradeSummaryRpc(int environment);
    private static native String getValidatePinRpc(int environment);

    private static native String getStockAnalysisRating(int environment);

    private static native String getBrokerStockSummary(int environment);

    private static native String getAllUserWatchlistRpc(int environment);

    private static native String getKeyStatsRtiRpc(int environment);

    private static native String getSimplePortfolioRpc(int environment);
    private static native String getSimpleAccountInfoRpc(int environment);

    private static native String getBrokerRankByStock(int environment);

    private static native String getDetailIncomeStatement(int environment);

    private static native String getDetailBalanceSheet(int environment);

    private static native String getDetailCashFlow(int environment);

    private static native String getIndexSectorRpc(int environment);
    private static native String getStockRankInfoRpc(int environment);

    private static native String getStockIndexSectorRpc(int environment);

    private static native String getMaxOrderByStockRpc(int environment);

    private static native String getBrokerListRpc(int environment);

    private static native String getBrokerRankActivityRpc(int environment);

    private static native String getBrokerRankRankingRpc(int environment);
    private static native String getFastOrderListRpc(int environment);

    private static native String getStockInfoDetailRpc(int environment);

    private static native String getAdvanceOrderInfoRpc(int environment);

    private static native String getFibonacciPivotPointRpc(int environment);

    private static native String getIntradayPriceRpc(int environment);
    private static native String getExerciseInfoRpc(int environment);

    private static native String getTradeListRpc(int environment);
    private static native String getLogoutRpc(int environment);

    private static native String getSettlementSchedRpc(int environment);

    private static native String getExerciseOrderListRpc(int environment);
    private static native String getCalendarByDateInRangeRpc(int environment);

    private static native String getMarketSessionRpc(int environment);

    private static native String getWithdrawCashRpc(int environment);

    private static native String getChangePasswordRpc(int environment);

    private static native String getChangePinRpc(int environment);

    private static native String getRdnHistoryRpc(int environment);

    private static native String getNewsBannerLoginRpc(int environment);

    private static native String getNewsBannerPromotionRpc(int environment);

    private static native String getTradeListHistoryRpc(int environment);

    private static native String getValidateSessionRpc(int environment);

    private static native String getStockPickResearchRpc(int environment);

    private static native String getNewsResearchContentRpc(int environment);
    private static native String getGlobalComoditiesRpc(int environment);
    private static native String getGlobalCurrencyRpc(int environment);
    private static native String getGlobalIndexRpc(int environment);
    private static native String addPriceAlertRpc(int environment);
    private static native String getListPriceAlertRpc(int environment);
    private static native String removePriceAlertRpc(int environment);
    private static native String saveDeviceTokenRpc(int environment);
    private static native String notificationHistoryRpc(int environment);
    private static native String getGlobalRankRpc(int environment);
    private static native String getBrokerSummaryByStockNetRpc(int environment);
    private static native String getTopFiveFaqRpc(int environment);
    private static native String getFaqByCategoryRpc(int environment);
    private static native String getFaqRpc(int environment);
    private static native String getSearchFaqRpc(int environment);
    private static native String getHelpTutorialVideoRpc(int environment);
    private static native String getEIPOListRpc(int environment);
    private static native String getEIPOInfoRpc(int environment);
    private static native String getEipoOrderListRpc(int environment);
    private static native String getEipoOrderInfoRpc(int environment);
    private static native String getNewsFeedRpc(int environment);
    private static native String getNewsFeedByStockRpc(int environment);
    private static native String getNewsFeedSearchRpc(int environment);
    private static native String getNewsResearchContentSearchRpc(int environment);
    private static native String getNewsCorporateActionCalendarByStockCodeRpc(int environment);
    private static native String getTradeHistoryGroupRpc(int environment);
    private static native String getTradeHistoryGroupDetailRpc(int environment);
    private static native String getRealizedGainLossByYearRpc(int environment);
    private static native String getRealizedGainLossByMonthRpc(int environment);
    private static native String getTradeSummaryCurrentMessageRpc(int environment);
    private static native String getOrderBookSummaryCurrentMessageRpc(int environment);
    private static native String getIndiceSummaryCurrentMessageRpc(int environment);
    private static native String getTrustedDeviceMessageRpc(int environment);
    private static native String sendOtpTrustedDeviceMessageRpc(int environment);
    private static native String verifyOtpTrustedDeviceMessageRpc(int environment);
    private static native String deleteTrustedDeviceMessageRpc(int environment);
    private static native String exerciseSessionMessageRpc(int environment);


    private static final int ENVIRONMENT = BuildConfig.SERVER_ENV;
    public static final String SALT_KEY = getSaltKey(ENVIRONMENT);
    public static final String PREFERENCE_NAME = getPreferenceName(ENVIRONMENT);

    public static final String LOGIN_RPC= getLoginRpc(ENVIRONMENT);

    public static final String ACCOUNT_INFO_RPC = getAccountInfoRpc(ENVIRONMENT);

    public static final String TRADE_BOOK_RPC = getTradeBookRpc(ENVIRONMENT);
    public static final String TRADE_BOOK_TIME_RPC = getTradeBookTimeRpc(ENVIRONMENT);

    public static final String STOCK_TRADE_RPC = getStockTradeRpc(ENVIRONMENT);

    public static final String CIF_LEVEL_POS_RPC = getCIFLevelPosRpc(ENVIRONMENT);

    public static final String ACC_STOCK_POS_RPC = getAccStockPosRpc(ENVIRONMENT);

    public static final String CURRENT_MESSAGE_RPC = getCurrentMessageRpc(ENVIRONMENT);

    public static final String CURRENT_KEY_STAT_RPC = getKeyStatRpc(ENVIRONMENT);

    public static final String INCOME_STATEMENT_RPC = getIncomeStatementRpc(ENVIRONMENT);

    public static final String BALANCE_SHEET_RPC = getBalanceSheetRpc(ENVIRONMENT);

    public static final String CASH_FLOW_RPC = getCashFlowRpc(ENVIRONMENT);

    public static final String ORDER_LIST_RPC = getOrderListRpc(ENVIRONMENT);

    public static final String NEWS_RPC = getNewsRpc(ENVIRONMENT);

    public static final String COMPANY_PROFILE_RPC = getCompanyProfileRpc(ENVIRONMENT);
    public static final String STOCK_PARAM_LIST_RPC = getStockParamList(ENVIRONMENT);
    public static final String EARNING_PER_SHARES_RPC = getEarningPerSharesRpc(ENVIRONMENT);
    public static final String GET_USER_WATCHLIST_RPC = getUserWatchList(ENVIRONMENT);
    public static final String ADD_WATCHLIST_CATEGORY_RPC = addWatchlistCategory(ENVIRONMENT);
    public static final String ADD_ITEM_CATEGORY_RPC = addItemCategoryRpc(ENVIRONMENT);
    public static final String REMOVE_WATCHLIST_CATEGORY_RPC = removeWatchlistCategoryRpc(ENVIRONMENT);
    public static final String REMOVE_ITEM_CATEGORY_RPC = removeItemCategoryRpc(ENVIRONMENT);

    public static final String STOCK_PICK_RPC = getStockPickRpc(ENVIRONMENT);

    public static final String ORDER_HISTORY_RPC = getOrderHistoryRpc(ENVIRONMENT);

    public static final String NEWS_PROMO_RPC = getNewsPromoRpc(ENVIRONMENT);

    public static final String GET_PER_BAND_RPC = getPerBandRpc(ENVIRONMENT);

    public static final String GET_PER_DATA_RPC = getPerDataRpc(ENVIRONMENT);

    public static final String GET_PBV_BAND_RPC = getPbvBandRpc(ENVIRONMENT);

    public static final String GET_PBV_DATA_RPC = getPbvDataRpc(ENVIRONMENT);

    public static final String TRADE_SUMMARY_RPC = getTradeSummaryRpc(ENVIRONMENT);
    public static final String VALIDATE_PIN_RPC = getValidatePinRpc(ENVIRONMENT);

    public static final String STOCK_ANALYSIS_RATING_RPC = getStockAnalysisRating(ENVIRONMENT);

    public static final String BROKER_STOCK_SUMMARY_RPC = getBrokerStockSummary(ENVIRONMENT);

    public static final String GET_ALL_USER_WATCHLIST = getAllUserWatchlistRpc(ENVIRONMENT);

    public static final String KEY_STATS_RTI_RPC = getKeyStatsRtiRpc(ENVIRONMENT);


    public static final String GET_SIMPLE_PORTFOLIO_RPC = getSimplePortfolioRpc(ENVIRONMENT);
    public static final String GET_SIMPLE_ACCOUNT_INFO_RPC = getSimpleAccountInfoRpc(ENVIRONMENT);

    public static final String BROKER_RANK_BY_STOCK_RPC = getBrokerRankByStock(ENVIRONMENT);

    public static final String DETAIL_INCOME_STATEMENT_RPC = getDetailIncomeStatement(ENVIRONMENT);

    public static final String DETAIL_BALANCE_SHEET_RPC = getDetailBalanceSheet(ENVIRONMENT);

    public static final String DETAIL_CASH_FLOW_RPC = getDetailCashFlow(ENVIRONMENT);

    public static final String INDEX_SECTOR_RPC = getIndexSectorRpc(ENVIRONMENT);

    public static final String STOCK_RANK_INFO_RPC = getStockRankInfoRpc(ENVIRONMENT);

    public static final String STOCK_INDEX_SECTOR_RPC = getStockIndexSectorRpc(ENVIRONMENT);
    public static final String MAX_ORDER_BY_STOCK_RPC = getMaxOrderByStockRpc(ENVIRONMENT);

    public static final String BROKER_LIST_RPC = getBrokerListRpc(ENVIRONMENT);

    public static final String BROKER_RANK_ACTIVITY_RPC = getBrokerRankActivityRpc(ENVIRONMENT);

    public static final String BROKER_RANK_RANKING_RPC = getBrokerRankRankingRpc(ENVIRONMENT);
    public static final String FAST_ORDER_RPC = getFastOrderListRpc(ENVIRONMENT);

    public static final String STOCK_INFO_DETAIL_RPC = getStockInfoDetailRpc(ENVIRONMENT);

    public static final String ADVANCE_ORDER_INFO_RPC = getAdvanceOrderInfoRpc(ENVIRONMENT);

    public static final String FIBONACCI_PIVOT_POINT_RPC = getFibonacciPivotPointRpc(ENVIRONMENT);

    public static final String INTRADAY_PRICE_RPC = getIntradayPriceRpc(ENVIRONMENT);

    public static final String EXERCISE_INFO_RPC = getExerciseInfoRpc(ENVIRONMENT);

    public static final String TRADE_LIST_RPC = getTradeListRpc(ENVIRONMENT);
    public static final String LOG_OUT_RPC = getLogoutRpc(ENVIRONMENT);
    public static final String SETTLEMENT_SCHEDULE_RPC = getSettlementSchedRpc(ENVIRONMENT);

    public static final String EXERCISE_ORDER_LIST_RPC = getExerciseOrderListRpc(ENVIRONMENT);
    public static final String CALENDAR_BY_DATE_IN_RANGE_RPC = getCalendarByDateInRangeRpc(ENVIRONMENT);

    public static final String MARKET_SESSION_RPC = getMarketSessionRpc(ENVIRONMENT);

    public static final String WITHDRAW_CASH_RPC = getWithdrawCashRpc(ENVIRONMENT);

    public static final String CHANGE_PASSWORD_RPC = getChangePasswordRpc(ENVIRONMENT);
    public static final String CHANGE_PIN_RPC = getChangePinRpc(ENVIRONMENT);

    public static final String RDN_HISTORY_RPC = getRdnHistoryRpc(ENVIRONMENT);

    public static final String NEWS_BANNER_LOGIN_RPC = getNewsBannerLoginRpc(ENVIRONMENT);

    public static final String NEWS_BANNER_PROMOTION_RPC = getNewsBannerPromotionRpc(ENVIRONMENT);
    public static final String TRADE_LIST_HISTORY_RPC = getTradeListHistoryRpc(ENVIRONMENT);
    public static final String VALIDATE_SESSION_RPC = getValidateSessionRpc(ENVIRONMENT);
    public static final String STOCK_PICK_RESEARCH_RPC = getStockPickResearchRpc(ENVIRONMENT);
    public static final String NEWS_RESEARCH_CONTENT_RPC = getNewsResearchContentRpc(ENVIRONMENT);
    public static final String GLOBAL_COMODITIES_RPC = getGlobalComoditiesRpc(ENVIRONMENT);
    public static final String GLOBAL_CURRENCY_RPC = getGlobalCurrencyRpc(ENVIRONMENT);
    public static final String GLOBAL_INDEX_RPC = getGlobalIndexRpc(ENVIRONMENT);
    public static final String ADD_PRICE_ALERT_RPC = addPriceAlertRpc(ENVIRONMENT);
    public static final String LIST_PRICE_ALERT_RPC = getListPriceAlertRpc(ENVIRONMENT);
    public static final String REMOVE_PRICE_ALERT_RPC = removePriceAlertRpc(ENVIRONMENT);
    public static final String SAVE_DEVICE_TOKEN_RPC = saveDeviceTokenRpc(ENVIRONMENT);
    public static final String NOTIFICATION_HISTORY_RPC = notificationHistoryRpc(ENVIRONMENT);
    public static final String GLOBAL_RANK_RPC = getGlobalRankRpc(ENVIRONMENT);
    public static final String BROKER_SUMMARY_BY_STOCK_NET_RPC = getBrokerSummaryByStockNetRpc(ENVIRONMENT);
    public static final String TOP_FIVE_FAQ_RPC = getTopFiveFaqRpc(ENVIRONMENT);
    public static final String FAQ_BY_CATEGORY_RPC = getFaqByCategoryRpc(ENVIRONMENT);
    public static final String FAQ_RPC = getFaqRpc(ENVIRONMENT);
    public static final String SEARCH_FAQ_RPC = getSearchFaqRpc(ENVIRONMENT);
    public static final String HELP_TUTORIAL_VIDEO_RPC = getHelpTutorialVideoRpc(ENVIRONMENT);
    public static final String EIPO_LIST_RPC = getEIPOListRpc(ENVIRONMENT);
    public static final String EIPO_INFO_RPC = getEIPOInfoRpc(ENVIRONMENT);
    public static final String EIPO_ORDER_LIST_RPC = getEipoOrderListRpc(ENVIRONMENT);
    public static final String EIPO_ORDER_INFO_RPC = getEipoOrderInfoRpc(ENVIRONMENT);
    public static final String NEWS_FEED_RPC = getNewsFeedRpc(ENVIRONMENT);
    public static final String NEWS_FEED_BY_STOCK_RPC = getNewsFeedByStockRpc(ENVIRONMENT);
    public static final String NEWS_FEED_SEARCH_RPC = getNewsFeedSearchRpc(ENVIRONMENT);
    public static final String NEWS_RESEARCH_CONTENT_SEARCH_RPC = getNewsResearchContentSearchRpc(ENVIRONMENT);
    public static final String NEWS_CORPORATE_ACTION_CALENDAR_BY_STOCK_CODE = getNewsCorporateActionCalendarByStockCodeRpc(ENVIRONMENT);
    public static final String TRADE_LIST_HISTORY_GROUP_RPC = getTradeHistoryGroupRpc(ENVIRONMENT);
    public static final String TRADE_LIST_HISTORY_GROUP_DETAIL_RPC = getTradeHistoryGroupDetailRpc(ENVIRONMENT);
    public static final String REALIZED_GAIN_LOSS_BY_YEAR_RPC = getRealizedGainLossByYearRpc(ENVIRONMENT);
    public static final String REALIZED_GAIN_LOSS_BY_MONTH_RPC = getRealizedGainLossByMonthRpc(ENVIRONMENT);
    public static final String TRADE_SUMMARY_CURRENT_MESSAGE_RPC = getTradeSummaryCurrentMessageRpc(ENVIRONMENT);
    public static final String ORDER_BOOK_SUMMARY_CURRENT_MESSAGE_RPC = getOrderBookSummaryCurrentMessageRpc(ENVIRONMENT);
    public static final String INDICE_SUMMARY_CURRENT_MESSAGE_RPC = getIndiceSummaryCurrentMessageRpc(ENVIRONMENT);
    public static final String TRUSTED_DEVICE_MESSAGE_RPC = getTrustedDeviceMessageRpc(ENVIRONMENT);
    public static final String SEND_OTP_TRUSTED_DEVICE_MESSAGE_RPC = sendOtpTrustedDeviceMessageRpc(ENVIRONMENT);
    public static final String VERIFY_OTP_DEVICE_MESSAGE_RPC = verifyOtpTrustedDeviceMessageRpc(ENVIRONMENT);
    public static final String DELETE_TRUSTED_DEVICE_MESSAGE_RPC = deleteTrustedDeviceMessageRpc(ENVIRONMENT);
    public static final String EXERCISE_SESSSION_MESSAGE_RPC = exerciseSessionMessageRpc(ENVIRONMENT);


}
