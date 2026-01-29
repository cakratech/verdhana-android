package com.bcasekuritas.mybest.ext.constant

import com.bcasekuritas.mybest.BuildConfig

object ConstKeys {
    // Used to load the 'native-lib' library on application startup.
    init {
        System.loadLibrary("app");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    private external fun getEndPoint(environment: Int, variants: Int): String
    private external fun getEndPointDrc(environment: Int, variants: Int): String
    private external fun getPort(environment: Int, variants: Int): String
    private external fun getVHost(environment: Int, variants: Int): String
    private external fun getTradingViewUrl(environment: Int, variants: Int): String
    private external fun getForgotPassUrl(environment: Int, variants: Int): String
    private external fun getPreLoginUrl(environment: Int, variants: Int): String
    private external fun getPdfUrl(environment: Int, variants: Int): String
    private external fun getPromoBannerUrl(environment: Int, variants: Int): String
    private external fun getDataMetric(environment: Int, variants: Int): String
    private external fun getMutipleMetric(environment: Int, variants: Int): String

    const val LOGIN_BIOMETRICS = "Biometrics"

    private const  val ENVIRONMENT = BuildConfig.SERVER_ENV
    private const  val VARIANTS = BuildConfig.VARIANT_ENV

    val END_POINT = getEndPoint(ENVIRONMENT, VARIANTS)
    val END_POINT_DRC = getEndPointDrc(ENVIRONMENT, VARIANTS)
    val PORT = getPort(ENVIRONMENT, VARIANTS).toInt()
    val VHOST = getVHost(ENVIRONMENT, VARIANTS)
    val TRADING_VIEW_URL = getTradingViewUrl(ENVIRONMENT, VARIANTS)
    val FORGOT_PASS_URL = getForgotPassUrl(ENVIRONMENT, VARIANTS)
    val PRE_LOGIN_URL = getPreLoginUrl(ENVIRONMENT, VARIANTS)
    val PROMO_BANNER_URL = getPromoBannerUrl(ENVIRONMENT, VARIANTS)
    val PDF_URL = getPdfUrl(ENVIRONMENT, VARIANTS)
    val DATA_METRIC = getDataMetric(ENVIRONMENT, VARIANTS)
    val MUTIPLE_DATA_METRIC = getMutipleMetric(ENVIRONMENT, VARIANTS)

}