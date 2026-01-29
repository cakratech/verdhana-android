#include <jni.h>
#include <string>

using namespace std;

// ENVIRONMENT
const int ENV_PRODUCTION = 1;
const int ENV_STAGING = 2;
const int ENV_DEVELOPMENT = 3;

// environment development
const string DEV_SALT_KEY = "SECRET";
const string DEV_PREFERENCE_NAME = "bca-rabbit-dev.xml";

// environment staging
const string STAGING_SALT_KEY = "SECRET";
const string STAGING_PREFERENCE_NAME = "bca-rabbit-stg.xml";

// environment production
const string PROD_SALT_KEY = "SECRET";
const string PROD_PREFERENCE_NAME = "bca-rabbit.xml";

// RPC Name
const string LOGON_MESSAGE = "cakra.logon_request-rpc";
const string ACCOUNT_INFO_MESSAGE = "cakra.client_info_request-rpc";
const string CIF_LEVEL_POSITION = "cakra.cif_level_pos_request-rpc";
const string ACC_STOCK_POSITION = "cakra.acc_stock_pos_request-rpc";
const string CURRENT_MESSAGE = "mi.current_message_request-rpc";
const string ORDER_LIST_MESSAGE = "cakra.order_list_request-rpc";
const string NEWS_MESSAGE = "news.info_request-rpc";
const string COMPANY_PROFILE_MESSAGE = "news.stock_detil_company_profile_request-rpc";
const string TRADE_BOOK_MESSAGE = "mi.trade_book_request-rpc";
const string TRADE_BOOK_TIME_MESSAGE = "mi.trade_book_time_request-rpc";
const string INCOME_STATEMENT_MESSAGE = "news.view_income_statement_request-rpc";
const string BALANCE_SHEET_OVERVIEW_MESSAGE = "news.view_balance_sheet_request-rpc";
const string CASH_FLOW_MESSAGE = "news.view_cash_flow_request-rpc";
const string STOCK_TRADE_MESSAGE = "mi.stock_trade_request-rpc";
const string KEY_STAT_MESSAGE = "news.view_key_stat_request-rpc";
const string TRADE_SUMMARY_MESSAGE = "cf.trade_summary_request-rpc";
const string EARNING_PER_SHARES_MESSAGE = "news.earnings_per_share_request-rpc";
const string STOCK_PARAM_LIST = "cakra.stock_param_list-rpc";
const string GET_PER_BAND = "news.get_per_band_request-rpc";
const string GET_PER_DATA = "news.get_per_data_request-rpc";
const string GET_PBV_BAND = "news.get_pbv_band_request-rpc";
const string GET_PBV_DATA = "news.get_pbv_data_request-rpc";
const string NEWS_PROMO_MESSAGE = "news.news_info_promo_request-rpc";
const string ORDER_HISTORY_MESSAGE = "cakra.order_list_history_request-rpc";
const string STOCK_PICK_MESSAGE = "news.news_stock_pick_single_request-rpc";
const string GET_USER_WATCHLIST = "cakra.user_watchlist";
const string ADD_WATCHLIST_CATEGORY = "cakra.add_user_watchlist";
const string ADD_ITEM_CATEGORY = "cakra.add_user_watchlist_item";
const string REMOVE_WATCHLIST_CATEGORY = "cakra.remove_user_watchlist";
const string REMOVE_ITEM_CATEGORY = "cakra.remove_user_watchlist_item";
const string GET_ALL_USER_WATCHLIST = "cakra.user_watchlist_all";
const string STOCK_ANALYSIS_RATING = "news.stock_analysis_rating_request-rpc";
const string BROKER_STOCK_SUMMARY = "mi.broker_stock_summary_request-rpc";
const string VALIDATE_PIN = "cakra.validate_pin_request-rpc";
const string KEY_STATS_RTI = "news.view_keystats_rti_request-rpc";
const string SIMPLE_PORTOFOLIO_MESSAGE = "cakra.simple_portofolio_request-rpc";
const string SIMPLE_ACCOUNT_INFO_MESSAGE = "cakra.simple_account_info_request-rpc";
const string BROKER_RANK_BY_STOCK = "mi.broker_rank_by_stock_discover_request-rpc";
const string DETAIL_INCOME_STATEMENT = "news.financial_income_statement_request-rpc";
const string DETAIL_BALANCE_SHEET = "news.financial_balance_sheet_request-rpc";
const string DETAIL_CASH_FLOW = "news.financial_cash_flow_request-rpc";
const string INDEX_SECTOR_MESSAGE = "news.view_index_sector_request-rpc";
const string STOCK_INDEX_SECTOR_MESSAGE = "news.stock_index_mapping_by_stock_index_request-rpc";
const string STOCK_RANK_INFO_MESSAGE = "mi.stock_ranking_request-rpc";
const string MAX_ORDER_BY_STOCK = "cakra.max_order_by_stock-rpc";
const string BROKER_LIST_MESSAGE = "mi.broker_list_request-rpc";
const string BROKER_RANK_ACTIVITY_MESSAGE = "mi.broker_rank_discover_activity_request-rpc";
const string BROKER_RANK_RANKING_MESSAGE = "mi.broker_ranking_discover_request-rpc";
const string FAST_ORDER_LIST = "cakra.fast_order_list_request-rpc";
const string STOCK_INFO_DETAIL_MESSAGE = "news.view_stock_info_detil_request-rpc";
const string ADVANCE_ORDER_INFO_MESSAGE = "cakra.advanced_order_info_request-rpc";
const string FIBONACCI_PIVOT_POINT_MESSAGE = "news.fibonacci_pivot_point_request-rpc";
const string INTRADAY_PRICE_MESSAGE = "cf.intraday_price_request-rpc";
const string EXERCISE_INFO_MESSAGE = "cakra.exercise_info_request-rpc";
const string TRADE_LIST_MESSAGE = "cakra.trade_list_request-rpc";
const string LOG_OUT_MESSAGE = "cakra.logout_request-rpc";
const string SETTLEMENT_SCHEDULE = "cakra.settlement_schedule_request-rpc";
const string EXERCISE_ORDER_LIST_MESSAGE = "cakra.exercise_order_list_request-rpc";
const string MARKET_SESSION_MESSAGE = "cakra.market_session_request-rpc";
const string WITHDRAW_MESSAGE = "cakra.withdraw_cash_request-rpc";
const string CHANGE_PASSWORD_MESSAGE = "cakra.change_password_request-rpc";
const string CHANGE_PIN_MESSAGE = "cakra.change_pin_request-rpc";
const string CALENDAR_BY_DATE_IN_RANGE = "news.ca_calendar_by_ca_date_inrange_request-rpc";
const string CORPORATE_CALENDAR_MESSAGE = "news.corporate_action_calendar_get_request-rpc";
const string RDN_HISTORY_MESSAGE = "cakra.movement_cash_request-rpc";
const string NEWS_BANNER_LOGIN_MESSAGE = "news.login_banner_request-rpc";
const string NEWS_BANNER_PROMOTION_MESSAGE = "news.promotion_banner_request-rpc";
const string TRADE_LIST_HISTORY_MESSAGE = "cakra.trade_list_history_request-rpc";
const string VALIDATE_SESSION_MESSAGE = "cakra.validate_session_request-rpc";
const string STOCK_PICK_RESEARCH_MESSAGE = "news.view_stock_pick_research_report_request-rpc";
const string NEWS_RESEARCH_CONTENT_MESSAGE = "news.get_news_research_content_request-rpc";
const string GLOBAL_COMODITIES_MESSAGE = "news.comodities_latest_request-rpc";
const string GLOBAL_CURRENCY_MESSAGE = "news.currency_latest_request-rpc";
const string GLOBAL_INDEX_MESSAGE = "news.index_latest_request-rpc";
const string ADD_PRICE_ALERT_MESSAGE = "cakra.add_price_alert_request-rpc";
const string LIST_PRICE_ALERT_MESSAGE = "cakra.list_price_alert_request-rpc";
const string REMOVE_PRICE_ALERT_MESSAGE = "cakra.remove_price_alert_request-rpc";
const string SAVE_DEVICE_TOKEN_MESSAGE = "cakra.save_device_token_request-rpc";
const string NOTIFICATION_HISTORY_MESSAGE = "cakra.notification_history_request-rpc";
const string GLOBAL_RANK_MESSAGE = "mi.broker_rank_global_discover_activity_request-rpc";
const string BROKER_SUMMARY_BY_STOCK_NET_MESSAGE = "mi.broker_rank_by_stock_discover_net_request-rpc";
const string TOP_FIVE_FAQ_MESSAGE = "news.news_get_news_top_five_faq_request-rpc";
const string FAQ_BY_CATEGORY = "news.get_news_faq_by_category_request-rpc";
const string HELP_TUTORIAL_VIDEO_MESSAGE = "news.news_get_news_tutorial_video_request-rpc";
const string FAQ_MESSAGE = "news.news_get_news_faq_request-rpc";
const string SEARCH_FAQ_MESSAGE = "news.search_news_faq_request-rpc";
const string EIPO_LIST_MESSAGE = "cakra.pipelines_ipo_list_request-rpc";
const string EIPO_INFO_MESSAGE = "cakra.pipelines_ipo_info_request-rpc";
const string EIPO_ORDER_LIST_MESSAGE = "cakra.ipo_order_list_request-rpc";
const string EIPO_ORDER_INFO_MESSAGE = "cakra.pipelines_ipo_order_list_request-rpc";
const string NEWS_INFO_FEED_MESSAGE = "news.news_info_feed_request-rpc";
const string NEWS_INFO_FEED_BY_STOCK_MESSAGE = "news.news_info_feed_by_stock_request-rpc";
const string NEWS_INFO_FEED_SEARCH_MESSAGE = "news.news_info_feed_search_request-rpc";
const string NEWS_RESEARCH_CONTENT_SEARCH_MESSAGE = "news.search_news_research_content_request-rpc";
const string NEWS_CORPORATE_ACTION_CALENDAR_BY_STOCK_CODE = "news.corporate_action_calendar_get_by_stock_request-rpc";
const string TRADE_HISTORY_GROUP_MESSAGE = "cakra.trade_history_group_request-rpc";
const string TRADE_HISTORY_GROUP_DETAIL_MESSAGE = "cakra.trade_history_group_detail_request-rpc";
const string REALIZED_GAIN_LOSS_BY_YEAR_MESSAGE = "cakra.r_gain_loss_request-rpc";
const string REALIZED_GAIN_LOSS_BY_MONTH_MESSAGE = "cakra.r_gain_loss_dtl_request-rpc";
const string TRADE_SUMMARY_CURRENT_MESSAGE = "mi.trade_summary-rpc";
const string ORDER_BOOK_SUMMARY_CURRENT_MESSAGE = "mi.orderbook_summary_compact-rpc";
const string INDICE_SUMMARY_CURRENT_MESSAGE = "mi.indice_summary-rpc";
const string TRUSTED_DEVICE_MESSAGE = "cakra.trusted_device_list_request-rpc";
const string SEND_OTP_TRUSTED_DEVICE_MESSAGE = "cakra.send_otp_request-rpc";
const string VERIFY_OTP_DEVICE_MESSAGE = "cakra.verify_otp_request-rpc";
const string DELETE_TRUSTED_DEVICE_MESSAGE = "cakra.delete_device_request-rpc";
const string EXERCISE_SESSION_MESSAGE = "cakra.exercise_session_request-rpc";


extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getSaltKey(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = PROD_SALT_KEY;
    } else {
        s = DEV_SALT_KEY;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getPreferenceName(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = PROD_PREFERENCE_NAME;
    } else {
        s = DEV_PREFERENCE_NAME;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getLoginRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = LOGON_MESSAGE;
    } else {
        s = LOGON_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}
extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getAccountInfoRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = ACCOUNT_INFO_MESSAGE;
    } else {
        s = ACCOUNT_INFO_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getCIFLevelPosRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = CIF_LEVEL_POSITION;
    } else {
        s = CIF_LEVEL_POSITION;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getAccStockPosRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = ACC_STOCK_POSITION;
    } else {
        s = ACC_STOCK_POSITION;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getCurrentMessageRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = CURRENT_MESSAGE;
    } else {
        s = CURRENT_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getTradeBookRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = TRADE_BOOK_MESSAGE;
    } else {
        s = TRADE_BOOK_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getTradeBookTimeRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = TRADE_BOOK_TIME_MESSAGE;
    } else {
        s = TRADE_BOOK_TIME_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getStockTradeRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = STOCK_TRADE_MESSAGE;
    } else {
        s = STOCK_TRADE_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getNewsRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = NEWS_MESSAGE;
    } else {
        s = NEWS_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getCompanyProfileRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = COMPANY_PROFILE_MESSAGE;
    } else {
        s = COMPANY_PROFILE_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getOrderListRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = ORDER_LIST_MESSAGE;
    } else {
        s = ORDER_LIST_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getKeyStatRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = KEY_STAT_MESSAGE;
    } else {
        s = KEY_STAT_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getIncomeStatementRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = INCOME_STATEMENT_MESSAGE;
    } else {
        s = INCOME_STATEMENT_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getBalanceSheetRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = BALANCE_SHEET_OVERVIEW_MESSAGE;
    } else {
        s = BALANCE_SHEET_OVERVIEW_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getCashFlowRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = CASH_FLOW_MESSAGE;
    } else {
        s = CASH_FLOW_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getTradeSummaryRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = TRADE_SUMMARY_MESSAGE;
    } else {
        s = TRADE_SUMMARY_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}


extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getEarningPerSharesRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = EARNING_PER_SHARES_MESSAGE;
    } else {
        s = EARNING_PER_SHARES_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}


extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getStockParamList(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = STOCK_PARAM_LIST;
    } else {
        s = STOCK_PARAM_LIST;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_addWatchlistCategory(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = ADD_WATCHLIST_CATEGORY;
    } else {
        s = ADD_WATCHLIST_CATEGORY;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getPerBandRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
          s = GET_PER_BAND;
    } else {
        s = GET_PER_BAND;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getPerDataRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
          s = GET_PER_DATA;
    } else {
        s = GET_PER_DATA;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getNewsPromoRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
          s = NEWS_PROMO_MESSAGE;
    } else {
        s = NEWS_PROMO_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getPbvBandRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
           s = GET_PBV_BAND;
    } else {
        s = GET_PBV_BAND;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getOrderHistoryRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
          s = ORDER_HISTORY_MESSAGE;
    } else {
        s = ORDER_HISTORY_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getPbvDataRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
          s = GET_PBV_DATA;
    } else {
        s = GET_PBV_DATA;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getStockPickRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
         s = STOCK_PICK_MESSAGE;
    } else {
        s = STOCK_PICK_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getUserWatchList(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = GET_USER_WATCHLIST;
    } else {
        s = GET_USER_WATCHLIST;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_addItemCategoryRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = ADD_ITEM_CATEGORY;
    } else {
        s = ADD_ITEM_CATEGORY;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_removeWatchlistCategoryRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = REMOVE_WATCHLIST_CATEGORY;
    } else {
        s = REMOVE_WATCHLIST_CATEGORY;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_removeItemCategoryRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = REMOVE_ITEM_CATEGORY;
    } else {
        s = REMOVE_ITEM_CATEGORY;
    }

    return env->NewStringUTF(s.c_str());
}
extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getStockAnalysisRating(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = STOCK_ANALYSIS_RATING;
    } else {
        s = STOCK_ANALYSIS_RATING;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getValidatePinRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = VALIDATE_PIN;
    } else {
        s = VALIDATE_PIN;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getBrokerStockSummary(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
          s = BROKER_STOCK_SUMMARY;
    } else {
        s = BROKER_STOCK_SUMMARY;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getAllUserWatchlistRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = GET_ALL_USER_WATCHLIST;
    } else {
        s = GET_ALL_USER_WATCHLIST;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getKeyStatsRtiRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = KEY_STATS_RTI;
    } else {
        s = KEY_STATS_RTI;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getSimplePortfolioRpc(
          JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = SIMPLE_PORTOFOLIO_MESSAGE;
    } else {
        s = SIMPLE_PORTOFOLIO_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getSimpleAccountInfoRpc(
          JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = SIMPLE_ACCOUNT_INFO_MESSAGE;
    } else {
        s = SIMPLE_ACCOUNT_INFO_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getBrokerRankByStock(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = BROKER_RANK_BY_STOCK;
    } else {
        s = BROKER_RANK_BY_STOCK;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getDetailIncomeStatement(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = DETAIL_INCOME_STATEMENT;
    } else {
        s = DETAIL_INCOME_STATEMENT;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getDetailBalanceSheet(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = DETAIL_BALANCE_SHEET;
    } else {
        s = DETAIL_BALANCE_SHEET;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getBalanceSheetOverview(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = DETAIL_BALANCE_SHEET;
    } else {
        s = DETAIL_BALANCE_SHEET;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getDetailCashFlow(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = DETAIL_CASH_FLOW;
    } else {
        s = DETAIL_CASH_FLOW;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getIndexSectorRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = INDEX_SECTOR_MESSAGE;
    } else {
        s = INDEX_SECTOR_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getStockIndexSectorRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = STOCK_INDEX_SECTOR_MESSAGE;
    } else {
        s = STOCK_INDEX_SECTOR_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getStockRankInfoRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = STOCK_RANK_INFO_MESSAGE;
    } else {
        s = STOCK_RANK_INFO_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getMaxOrderByStockRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = MAX_ORDER_BY_STOCK;
    } else {
        s = MAX_ORDER_BY_STOCK;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getBrokerListRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = BROKER_LIST_MESSAGE;
    } else {
        s = BROKER_LIST_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getBrokerRankActivityRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = BROKER_RANK_ACTIVITY_MESSAGE;
    } else {
        s = BROKER_RANK_ACTIVITY_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getBrokerRankRankingRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = BROKER_RANK_RANKING_MESSAGE;
    } else {
        s = BROKER_RANK_RANKING_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getStockInfoDetailRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = STOCK_INFO_DETAIL_MESSAGE;
    } else {
        s = STOCK_INFO_DETAIL_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getAdvanceOrderInfoRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = ADVANCE_ORDER_INFO_MESSAGE;
    } else {
        s = ADVANCE_ORDER_INFO_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getFibonacciPivotPointRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = FIBONACCI_PIVOT_POINT_MESSAGE;
    } else {
        s = FIBONACCI_PIVOT_POINT_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getFastOrderListRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = FAST_ORDER_LIST;
    } else {
        s = FAST_ORDER_LIST;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getIntradayPriceRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = INTRADAY_PRICE_MESSAGE;
    } else {
        s = INTRADAY_PRICE_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getExerciseInfoRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = EXERCISE_INFO_MESSAGE;
    } else {
        s = EXERCISE_INFO_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getTradeListRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = TRADE_LIST_MESSAGE;
    } else {
        s = TRADE_LIST_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getLogoutRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = LOG_OUT_MESSAGE;
    } else {
        s = LOG_OUT_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getSettlementSchedRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = SETTLEMENT_SCHEDULE;
    } else {
        s = SETTLEMENT_SCHEDULE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getExerciseOrderListRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = EXERCISE_ORDER_LIST_MESSAGE;
    } else {
        s = EXERCISE_ORDER_LIST_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getMarketSessionRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = MARKET_SESSION_MESSAGE;
    } else {
        s = MARKET_SESSION_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getWithdrawCashRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = WITHDRAW_MESSAGE;
    } else {
        s = WITHDRAW_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getChangePasswordRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = CHANGE_PASSWORD_MESSAGE;
    } else {
        s = CHANGE_PASSWORD_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getChangePinRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = CHANGE_PIN_MESSAGE;
    } else {
        s = CHANGE_PIN_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getCalendarByDateInRangeRpc(JNIEnv *env,
                                                                                jclass instance,
                                                                                jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = CORPORATE_CALENDAR_MESSAGE ;
    } else {
        s = CORPORATE_CALENDAR_MESSAGE ;
    }

    return env->NewStringUTF(s.c_str());

}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getRdnHistoryRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = RDN_HISTORY_MESSAGE;
    } else {
        s = RDN_HISTORY_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getNewsBannerLoginRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = NEWS_BANNER_LOGIN_MESSAGE;
    } else {
        s = NEWS_BANNER_LOGIN_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getNewsBannerPromotionRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = NEWS_BANNER_PROMOTION_MESSAGE;
    } else {
        s = NEWS_BANNER_PROMOTION_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getTradeListHistoryRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = TRADE_LIST_HISTORY_MESSAGE;
    } else {
        s = TRADE_LIST_HISTORY_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getValidateSessionRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = VALIDATE_SESSION_MESSAGE;
    } else {
        s = VALIDATE_SESSION_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getStockPickResearchRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = STOCK_PICK_RESEARCH_MESSAGE;
    } else {
        s = STOCK_PICK_RESEARCH_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getNewsResearchContentRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = NEWS_RESEARCH_CONTENT_MESSAGE;
    } else {
        s = NEWS_RESEARCH_CONTENT_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getGlobalComoditiesRpc(JNIEnv *env,jclass clazz,jint envtype) {

    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = GLOBAL_COMODITIES_MESSAGE;
    } else {
        s = GLOBAL_COMODITIES_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getGlobalCurrencyRpc(JNIEnv *env, jclass clazz,jint envtype) {

    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = GLOBAL_CURRENCY_MESSAGE;
    } else {
        s = GLOBAL_CURRENCY_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getGlobalIndexRpc(JNIEnv *env, jclass clazz,jint envtype) {

    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = GLOBAL_INDEX_MESSAGE;
    } else {
        s = GLOBAL_INDEX_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_addPriceAlertRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = ADD_PRICE_ALERT_MESSAGE;
    } else {
        s = ADD_PRICE_ALERT_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getListPriceAlertRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = LIST_PRICE_ALERT_MESSAGE;
    } else {
        s = LIST_PRICE_ALERT_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_removePriceAlertRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = REMOVE_PRICE_ALERT_MESSAGE;
    } else {
        s = REMOVE_PRICE_ALERT_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_saveDeviceTokenRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = SAVE_DEVICE_TOKEN_MESSAGE;
    } else {
        s = SAVE_DEVICE_TOKEN_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_notificationHistoryRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = NOTIFICATION_HISTORY_MESSAGE;
    } else {
        s = NOTIFICATION_HISTORY_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getGlobalRankRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = GLOBAL_RANK_MESSAGE;
    } else {
        s = GLOBAL_RANK_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getBrokerSummaryByStockNetRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = BROKER_SUMMARY_BY_STOCK_NET_MESSAGE;
    } else {
        s = BROKER_SUMMARY_BY_STOCK_NET_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getTopFiveFaqRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = TOP_FIVE_FAQ_MESSAGE;
    } else {
        s = TOP_FIVE_FAQ_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getFaqByCategoryRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = FAQ_BY_CATEGORY;
    } else {
        s = FAQ_BY_CATEGORY;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getFaqRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = FAQ_MESSAGE;
    } else {
        s = FAQ_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getSearchFaqRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = SEARCH_FAQ_MESSAGE;
    } else {
        s = SEARCH_FAQ_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getHelpTutorialVideoRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = HELP_TUTORIAL_VIDEO_MESSAGE;
    } else {
        s = HELP_TUTORIAL_VIDEO_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getEIPOListRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = EIPO_LIST_MESSAGE;
    } else {
        s = EIPO_LIST_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getEIPOInfoRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = EIPO_INFO_MESSAGE;
    } else {
        s = EIPO_INFO_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getEipoOrderInfoRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = EIPO_ORDER_INFO_MESSAGE;
    } else {
        s = EIPO_ORDER_INFO_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getEipoOrderListRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = EIPO_ORDER_LIST_MESSAGE;
    } else {
        s = EIPO_ORDER_LIST_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getNewsFeedRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = NEWS_INFO_FEED_MESSAGE;
    } else {
        s = NEWS_INFO_FEED_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getNewsFeedByStockRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = NEWS_INFO_FEED_BY_STOCK_MESSAGE;
    } else {
        s = NEWS_INFO_FEED_BY_STOCK_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getNewsFeedSearchRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = NEWS_INFO_FEED_SEARCH_MESSAGE;
    } else {
        s = NEWS_INFO_FEED_SEARCH_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getNewsResearchContentSearchRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = NEWS_RESEARCH_CONTENT_SEARCH_MESSAGE;
    } else {
        s = NEWS_RESEARCH_CONTENT_SEARCH_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getNewsCorporateActionCalendarByStockCodeRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = NEWS_CORPORATE_ACTION_CALENDAR_BY_STOCK_CODE;
    } else {
        s = NEWS_CORPORATE_ACTION_CALENDAR_BY_STOCK_CODE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getTradeHistoryGroupRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = TRADE_HISTORY_GROUP_MESSAGE;
    } else {
        s = TRADE_HISTORY_GROUP_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getTradeHistoryGroupDetailRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = TRADE_HISTORY_GROUP_DETAIL_MESSAGE;
    } else {
        s = TRADE_HISTORY_GROUP_DETAIL_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getRealizedGainLossByYearRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = REALIZED_GAIN_LOSS_BY_YEAR_MESSAGE;
    } else {
        s = REALIZED_GAIN_LOSS_BY_YEAR_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getRealizedGainLossByMonthRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = REALIZED_GAIN_LOSS_BY_MONTH_MESSAGE;
    } else {
        s = REALIZED_GAIN_LOSS_BY_MONTH_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getTradeSummaryCurrentMessageRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = TRADE_SUMMARY_CURRENT_MESSAGE;
    } else {
        s = TRADE_SUMMARY_CURRENT_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getOrderBookSummaryCurrentMessageRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = ORDER_BOOK_SUMMARY_CURRENT_MESSAGE;
    } else {
        s = ORDER_BOOK_SUMMARY_CURRENT_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getIndiceSummaryCurrentMessageRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = INDICE_SUMMARY_CURRENT_MESSAGE;
    } else {
        s = INDICE_SUMMARY_CURRENT_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_getTrustedDeviceMessageRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = TRUSTED_DEVICE_MESSAGE;
    } else {
        s = TRUSTED_DEVICE_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_sendOtpTrustedDeviceMessageRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = SEND_OTP_TRUSTED_DEVICE_MESSAGE;
    } else {
        s = SEND_OTP_TRUSTED_DEVICE_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_verifyOtpTrustedDeviceMessageRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = VERIFY_OTP_DEVICE_MESSAGE;
    } else {
        s = VERIFY_OTP_DEVICE_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_deleteTrustedDeviceMessageRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = DELETE_TRUSTED_DEVICE_MESSAGE;
    } else {
        s = DELETE_TRUSTED_DEVICE_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}

extern "C" jstring JNICALL
Java_com_bcasekuritas_rabbitmq_constant_ConstantKeys_exerciseSessionMessageRpc(
        JNIEnv *env, jclass instance, jint envtype) {
    int type = (int) envtype;
    string s;

    if (type == ENV_PRODUCTION) {
        s = EXERCISE_SESSION_MESSAGE;
    } else {
        s = EXERCISE_SESSION_MESSAGE;
    }

    return env->NewStringUTF(s.c_str());
}