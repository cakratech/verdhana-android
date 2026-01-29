package com.bcasekuritas.mybest.ext.prefs

import android.content.Context
import com.bcasekuritas.rabbitmq.constant.ConstantKeys
import com.securepreferences.SecurePreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class SharedPreferenceManager @Inject constructor(context: Context) {

    private var securePreferences: SecurePreferences =
        SecurePreferences(context, ConstantKeys.SALT_KEY, ConstantKeys.PREFERENCE_NAME)

    companion object {
        private const val PREFS_KEY_USER_LOGIN_PLATFORM = "USER_LOGIN_PLATFORM"
        private const val PREFS_KEY_CIF_CODE = "KEY_CIF_CODE"
        private const val PREFS_KEY_USER_ID = "KEY_USER_ID"
        private const val PREFS_KEY_ACC_NO = "KEY_ACC_NO"
        private const val PREFS_KEY_BIOMETRIC_ACTIVE = "KEY_BIOMETRIC_ACTIVE"
        private const val PREFS_KEY_FAST_ORDER_SHOW_ORDERS = "KEY_FAST_ORDER_SHOW_ORDERS"
        private const val PREFS_KEY_FAST_ORDER_ORDER_COUNTS = "KEY_FAST_ORDER_ORDER_COUNTS"
        private const val PREFS_KEY_FAST_ORDER_PREVENT_ORDERS = "KEY_FAST_ORDER_PREVENT_ORDERS"
        private const val PREFS_KEY_FAST_ORDER_AMEND_ORDERS = "KEY_FAST_ORDER_AMEND_ORDERS"
        private const val PREFS_KEY_SESSION_ID = "KEY_SESSION_ID"
        private const val PREFS_KEY_STOCK_DETAIL_CODE = "KEY_STOCK_DETAIL_CODE"
        private const val PREFS_KEY_STOCK_DETAIL_LAST_PRICE = "KEY_STOCK_DETAIL_LAST_PRICE"
        private const val PREFS_KEY_URL_LOGO = "PREFS_KEY_URL_LOGO"
        private const val PREFS_KEY_URL_ICON = "PREFS_KEY_URL_ICON"
        private const val PREFS_KEY_BUY_COMM = "PREFS_KEY_BUY_COMM"
        private const val PREFS_KEY_SELL_COMM = "PREFS_KEY_SELL_COMM"
        private const val PREFS_KEY_LAST_COUNT_PROGRESS_BID = "PREFS_KEY_LAST_COUNT_PROGRESS_BID"
        private const val PREFS_KEY_LAST_COUNT_PROGRESS_OFFER = "PREFS_KEY_LAST_COUNT_PROGRESS_OFFER"
        private const val PREFS_KEY_ON_BOARDING_STATE = "PREFS_KEY_ON_BOARDING_STATE"
        private const val PREFS_KEY_CONNECTION_LINE = "PREFS_KEY_CONNECTION_LINE"
        private const val PREFS_KEY_CONNECTION_HTS = "PREFS_KEY_CONNECTION_HTS"
        private const val PREFS_KEY_CONNECTION_PORT = "PREFS_KEY_CONNECTION_PORT"
        private const val PREFS_KEY_DARK_MODE = "PREFS_KEY_DARK_MODE"
        private const val PREFS_KEY_FCM_TOKEN = "PREFS_KEY_FCM_TOKEN"
        private const val PREFS_KEY_BUY_COMM_OMS = "PREFS_KEY_BUY_COMM_OMS"
        private const val PREFS_KEY_SELL_COMM_OMS = "PREFS_KEY_SELL_COMM_OMS"
        private const val PREFS_KEY_DIALOG_AMEND_GTC = "PREFS_KEY_DIALOG_AMEND_GTC"
        private const val PREFS_KEY_DIALOG_FORCE_UPDATE = "PREFS_KEY_DIALOG_FORCE_UPDATE"
        private const val PREFS_KEY_COACHMARK_WATCHLIST = "PREFS_KEY_COACHMARK_WATCHLIST"
        private const val PREFS_KEY_COACHMARK_WATCHLIST_EMPTY = "PREFS_KEY_COACHMARK_WATCHLIST_EMPTY"
        private const val PREFS_KEY_COACHMARK_FAST_ORDER = "PREFS_KEY_COACHMARK_FAST_ORDER"
        private const val PREFS_KEY_COACHMARK_PORTFOLIO = "PREFS_KEY_COACHMARK_PORTFOLIO"
        private const val PREFS_KEY_SHOW_DIALOG_MANY_REQUEST_OTP = "PREFS_KEY_SHOW_DIALOG_MANY_REQUEST_OTP"

    }

    fun clearPreferences() {
        val allKeys = securePreferences.all.keys.toList()
        for (key in allKeys){
            if (key != PREFS_KEY_USER_ID && key != PREFS_KEY_ON_BOARDING_STATE && key != PREFS_KEY_COACHMARK_WATCHLIST && key != PREFS_KEY_COACHMARK_WATCHLIST_EMPTY
                && key != PREFS_KEY_COACHMARK_FAST_ORDER && key != PREFS_KEY_COACHMARK_PORTFOLIO){
                securePreferences.edit().remove(key).commit()
            }
        }
    }

    fun clearPreferencesUserId() {
        securePreferences.edit().remove(PREFS_KEY_USER_ID).commit()
    }

    fun removeStockDetailCodePreferences() {
        securePreferences.edit().remove(stockDetailCode).commit()
    }

    var userLoginPlatform: String
        get(): String {
            return securePreferences.getString(PREFS_KEY_USER_LOGIN_PLATFORM, "").toString()
        }
        set(value) {
            securePreferences.edit().putString(PREFS_KEY_USER_LOGIN_PLATFORM, value).apply()
        }

    var cifCode: String
        get(): String {
            return securePreferences.getString(PREFS_KEY_CIF_CODE, "").toString()
        }
        set(value) {
            securePreferences.edit().putString(PREFS_KEY_CIF_CODE, value).apply()
        }

    var userId: String
        get(): String {
            return securePreferences.getString(PREFS_KEY_USER_ID, "").toString()
        }
        set(value) {
            securePreferences.edit().putString(PREFS_KEY_USER_ID, value).apply()
        }

    var sessionId: String
        get(): String {
            return securePreferences.getString(PREFS_KEY_SESSION_ID, "").toString()
        }
        set(value) {
            securePreferences.edit().putString(PREFS_KEY_SESSION_ID, value).apply()
        }

    var accno: String
        get(): String {
            return securePreferences.getString(PREFS_KEY_ACC_NO, "").toString()
        }
        set(value) {
            securePreferences.edit().putString(PREFS_KEY_ACC_NO, value).apply()
        }

    var isBiometricActive: Boolean
        get(): Boolean {
            return securePreferences.getBoolean(PREFS_KEY_BIOMETRIC_ACTIVE, false)
        }
        set(value) {
            securePreferences.edit().putBoolean(PREFS_KEY_BIOMETRIC_ACTIVE, value).apply()
        }

    var isShowOrdersFO: Boolean
        get(): Boolean {
            return securePreferences.getBoolean(PREFS_KEY_FAST_ORDER_SHOW_ORDERS, true)
        }
        set(value) {
            securePreferences.edit().putBoolean(PREFS_KEY_FAST_ORDER_SHOW_ORDERS, value).apply()
        }

    var isPreventOrdersFO: Boolean
        get(): Boolean {
            return securePreferences.getBoolean(PREFS_KEY_FAST_ORDER_PREVENT_ORDERS, true)
        }
        set(value) {
            securePreferences.edit().putBoolean(PREFS_KEY_FAST_ORDER_PREVENT_ORDERS, value).apply()
        }

    var isOrderCountsFO: Boolean
        get(): Boolean {
            return securePreferences.getBoolean(PREFS_KEY_FAST_ORDER_ORDER_COUNTS, true)
        }
        set(value) {
            securePreferences.edit().putBoolean(PREFS_KEY_FAST_ORDER_ORDER_COUNTS, value).apply()
        }

    var isOnAmendFO: Boolean
        get(): Boolean {
            return securePreferences.getBoolean(PREFS_KEY_FAST_ORDER_AMEND_ORDERS, false)
        }
        set(value) {
            securePreferences.edit().putBoolean(PREFS_KEY_FAST_ORDER_AMEND_ORDERS, value).apply()
        }

    var stockDetailCode: String
        get(): String {
            return securePreferences.getString(PREFS_KEY_STOCK_DETAIL_CODE, "").toString()
        }
        set(value) {
            securePreferences.edit().putString(PREFS_KEY_STOCK_DETAIL_CODE, value).apply()
        }

    var lastPrice: String
        get(): String {
            return securePreferences.getString(PREFS_KEY_STOCK_DETAIL_LAST_PRICE, "").toString()
        }
        set(value) {
            securePreferences.edit().putString(PREFS_KEY_STOCK_DETAIL_LAST_PRICE, value).apply()
        }

    var urlIcon: String
        get(): String {
            return securePreferences.getString(PREFS_KEY_URL_ICON, "").toString()
        }
        set(value) {
            securePreferences.edit().putString(PREFS_KEY_URL_ICON, value).apply()
        }

    var urlLogo: String
        get(): String {
            return securePreferences.getString(PREFS_KEY_URL_LOGO, "").toString()
        }
        set(value) {
            securePreferences.edit().putString(PREFS_KEY_URL_LOGO, value).apply()
        }

    var connectionLine: Int
        get(): Int {
            return securePreferences.getInt(PREFS_KEY_CONNECTION_LINE, 0).toInt()
        }
        set(value) {
            securePreferences.edit().putInt(PREFS_KEY_CONNECTION_LINE, value).apply()
        }

    var connectionHts: String
        get(): String {
            return securePreferences.getString(PREFS_KEY_CONNECTION_HTS,"").toString()
        }
        set(value) {
            securePreferences.edit().putString(PREFS_KEY_CONNECTION_HTS, value).apply()
        }

    var connectionPort: Int
        get(): Int {
            return securePreferences.getInt(PREFS_KEY_CONNECTION_PORT, 0).toInt()
        }
        set(value) {
            securePreferences.edit().putInt(PREFS_KEY_CONNECTION_PORT, value).apply()
        }

    var isDarkMode: Boolean
        get(): Boolean {
            return securePreferences.getBoolean(PREFS_KEY_DARK_MODE, false)
        }
        set(value) {
            securePreferences.edit().putBoolean(PREFS_KEY_DARK_MODE, value).apply()
        }

    var lastCountProgressBid: Int
        get(): Int {
            return securePreferences.getInt(PREFS_KEY_LAST_COUNT_PROGRESS_BID, 0).toInt()
        }
        set(value) {
            securePreferences.edit().putInt(PREFS_KEY_LAST_COUNT_PROGRESS_BID, value).apply()
        }

    var lastCountProgressOffer: Int
        get(): Int {
            return securePreferences.getInt(PREFS_KEY_LAST_COUNT_PROGRESS_OFFER, 0).toInt()
        }
        set(value) {
            securePreferences.edit().putInt(PREFS_KEY_LAST_COUNT_PROGRESS_OFFER, value).apply()
        }

    var buyCommission: String
        get(): String {
            return securePreferences.getString(PREFS_KEY_BUY_COMM,"").toString()
        }
        set(value) {
            securePreferences.edit().putString(PREFS_KEY_BUY_COMM, value).apply()
        }

    var fcmToken: String
        get(): String {
            return securePreferences.getString(PREFS_KEY_FCM_TOKEN,"").toString()
        }
        set(value) {
            securePreferences.edit().putString(PREFS_KEY_FCM_TOKEN, value).apply()
        }

    var sellCommission: String
        get(): String {
            return securePreferences.getString(PREFS_KEY_SELL_COMM,"").toString()
        }
        set(value) {
            securePreferences.edit().putString(PREFS_KEY_SELL_COMM, value).apply()
        }

    var onBoardingState: Boolean
        get(): Boolean {
            return securePreferences.getBoolean(PREFS_KEY_ON_BOARDING_STATE, false)
        }
        set(value) {
            securePreferences.edit().putBoolean(PREFS_KEY_ON_BOARDING_STATE, value).apply()
        }

    var buyCommissionOms: String
        get(): String {
            return securePreferences.getString(PREFS_KEY_BUY_COMM_OMS,"").toString()
        }
        set(value) {
            securePreferences.edit().putString(PREFS_KEY_BUY_COMM_OMS, value).apply()
        }

    var sellCommissionOms: String
        get(): String {
            return securePreferences.getString(PREFS_KEY_SELL_COMM_OMS,"").toString()
        }
        set(value) {
            securePreferences.edit().putString(PREFS_KEY_SELL_COMM_OMS, value).apply()
        }

    var isAmendGtc: Boolean
        get(): Boolean {
            return securePreferences.getBoolean(PREFS_KEY_DIALOG_AMEND_GTC, false)
        }
        set(value) {
            securePreferences.edit().putBoolean(PREFS_KEY_DIALOG_AMEND_GTC, value).apply()
        }

    var isCoachmarkWatchListShow: Boolean
        get(): Boolean {
            return securePreferences.getBoolean(PREFS_KEY_COACHMARK_WATCHLIST, false)
        }
        set(value) {
            securePreferences.edit().putBoolean(PREFS_KEY_COACHMARK_WATCHLIST, value).apply()
        }

    var isCoachmarkWatchListEmptyShow: Boolean
        get(): Boolean {
            return securePreferences.getBoolean(PREFS_KEY_COACHMARK_WATCHLIST_EMPTY, false)
        }
        set(value) {
            securePreferences.edit().putBoolean(PREFS_KEY_COACHMARK_WATCHLIST_EMPTY, value).apply()
        }

    var isCoachmarkFastOrderShow: Boolean
        get(): Boolean {
            return securePreferences.getBoolean(PREFS_KEY_COACHMARK_FAST_ORDER, false)
        }
        set(value) {
            securePreferences.edit().putBoolean(PREFS_KEY_COACHMARK_FAST_ORDER, value).apply()
        }

    var isCoachmarkPortfolioShow: Boolean
        get(): Boolean {
            return securePreferences.getBoolean(PREFS_KEY_COACHMARK_PORTFOLIO, false)
        }
        set(value) {
            securePreferences.edit().putBoolean(PREFS_KEY_COACHMARK_PORTFOLIO, value).apply()
        }

    var isDialogForceUpdateShow: Boolean
        get(): Boolean {
            return securePreferences.getBoolean(PREFS_KEY_DIALOG_FORCE_UPDATE, false)
        }
        set(value) {
            securePreferences.edit().putBoolean(PREFS_KEY_DIALOG_FORCE_UPDATE, value).apply()
        }

    var isShowDialogManyRequestOtp: Boolean
        get(): Boolean {
            return securePreferences.getBoolean(PREFS_KEY_SHOW_DIALOG_MANY_REQUEST_OTP, false)
        }
        set(value) {
            securePreferences.edit().putBoolean(PREFS_KEY_SHOW_DIALOG_MANY_REQUEST_OTP, value).apply()
        }


    fun getString(key: String): String {
        return securePreferences.getString(key, "").toString()
    }

    fun setString(key: String, value: String) {
        securePreferences.edit().putString(key, value).apply()
    }

    fun getInt(key: String): Int {
        return securePreferences.getInt(key, 0)
    }

    fun setInt(key: String, value: Int) {
        securePreferences.edit().putInt(key, value).apply()
    }

}