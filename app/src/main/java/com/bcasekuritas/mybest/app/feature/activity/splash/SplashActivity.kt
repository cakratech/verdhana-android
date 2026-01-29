package com.bcasekuritas.mybest.app.feature.activity.splash

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseActivity
import com.bcasekuritas.mybest.app.feature.activity.fullscreen.FullScreenActivity
import com.bcasekuritas.mybest.app.feature.activity.main.MainActivity
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.databinding.ActivitySplashBinding
import com.bcasekuritas.mybest.ext.common.RabbitMQForegroundService
import com.bcasekuritas.mybest.ext.common.isNetworkConnected
import com.bcasekuritas.mybest.ext.common.showToast
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {

    override val binding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(
            layoutInflater
        )
    }
    override val viewModel: SplashViewModel by viewModels()
    override val bindingVariable: Int = BR.vmActSplash

    private lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig
    private var versionCode = 0
    private var versionName = ""
    private var darkMode = 0
    private var isSplashVisible = true
    private var isForceUpdate = false

    companion object {
        fun startIntent(activity: Activity) {
            val starter = Intent(activity, SplashActivity::class.java)
            activity.startActivity(starter)
            activity.finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.FullScreenTheme)
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val splashScreen = installSplashScreen()
            splashScreen.setKeepOnScreenCondition { isSplashVisible }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            getFCMToken()
        }
        Timber.i("Splash onCreate")

    }

    private fun fetchRemoteConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)  // fetch every hour
            .build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)

        mFirebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Timber.tag("SplashActivity").d("Config params updated: $updated")
                    displayRemoteConfigValues()
                }
            }
    }

    private fun displayRemoteConfigValues() {
        val androidConfigJson = mFirebaseRemoteConfig.getString("android_config")
        try {
            val androidConfig = JSONObject(androidConfigJson)
            versionName = androidConfig.getString("version_name")
            versionCode = androidConfig.getInt("version_code")
            isForceUpdate = androidConfig.getBoolean("is_force_update")

            processFcmIntent()
            Timber.tag("SplashActivity").d("Version Name: $versionName, Version Code: $versionCode")
        } catch (e: Exception) {
            Timber.tag("SplashActivity").e("Failed to parse android_config JSON : $e")
        }
    }

    override fun onStart() {
        super.onStart()

        try {
            prefManager.clearPreferences()
        } catch (e: Exception) {
            Timber.d("noPrefferences")
        }


        darkMode = AppCompatDelegate.MODE_NIGHT_NO
//        binding.ivLogo.setBackgroundResource(R.drawable.ic_bca_sekuritas)
//        if (prefManager.isDarkMode){
//            darkMode = AppCompatDelegate.MODE_NIGHT_YES
//            binding.ivLogo.setBackgroundResource(R.drawable.ic_bca_logo)
//        } else {
//            darkMode = AppCompatDelegate.MODE_NIGHT_NO
//            binding.ivLogo.setBackgroundResource(R.drawable.logo_bcas_light)
//        }


        AppCompatDelegate.setDefaultNightMode(darkMode)
    }

    override fun onInitViews() {
        setContentView(binding.root)
    }

    private fun isIntentFromFcm(): Boolean {
        return intent.hasExtra("title") &&
                intent.hasExtra("body") &&
                intent.hasExtra("channel") &&
                intent.hasExtra("type")
    }

    private fun processFcmIntent() {
        if (isIntentFromFcm()) {
            // processFcmIntent()
        } else {
//            if (versionCode > BuildConfig.VERSION_CODE) {
//                if (isForceUpdate) {
//                    showDialogForcedUpdate(supportFragmentManager, onClickUpdated = { onClick ->
//                        if (onClick) {
//
//                        }
//                    })
//                }
//            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.deleteSession()
        fetchRemoteConfig()
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                prefManager.fcmToken = task.result.toString()

                Timber.d("FCM Token: ${task.result}")
                navigateNext()
            } else {
                showToast(this, "Fetching FCM token failed")
                Timber.w(task.exception, "Fetching FCM token failed")
                navigateNext()
            }
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.getValidateSessionResult.observe(this) { data ->
            if (data != null) {
                if (data.status == 0) {
                    Timber.d("App Info & Order Reply Start")
                    viewModel.startNewAppNotification()
                    viewModel.startNewOrderReply()
                    viewModel.startSubsAll(true)
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        MiddleActivity.startIntentWithFinish(this, NavKeys.KEY_FM_LOGIN, "")
                        isSplashVisible = false
                    }, 1000)
                }
            } else {
                Timber.d("App Info & Order Reply Start")
                viewModel.startNewAppNotification()
                viewModel.startNewOrderReply()
                viewModel.startSubsAll(false)
            }
        }

        viewModel.startAllConsumerResult.observe(this) { data ->
            data?.let {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (data) {
                        isSplashVisible = false

                        RabbitMQForegroundService.startService(this)
                        val appLinkData: Uri? = intent.data
                        val segment = appLinkData?.pathSegments
                        if (segment?.isNotEmpty() == true) {
                            navigateDeepLink(segment)
                        } else {
                            MainActivity.startIntentWithFinish(this, NavKeys.KEY_MAIN_HOME)
                        }
                    } else {
                        MiddleActivity.startIntentWithFinish(this, NavKeys.KEY_FM_LOGIN, "")
                        isSplashVisible = false
                    }
                }, 1000)
            }
        }
    }

    private fun navigateNext() {
            if (prefManager.onBoardingState) {
                if (prefManager.sessionId.isNotEmpty()) {
                    if (isNetworkConnected(this)) {
                        viewModel.validateSession(prefManager.userId, prefManager.sessionId)
                    } else {
                        Handler(Looper.getMainLooper()).postDelayed({
                            MiddleActivity.startIntentWithFinish(this, NavKeys.KEY_FM_LOGIN, "")
                            isSplashVisible = false
                        }, 1000)
                    }
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        MiddleActivity.startIntentWithFinish(this, NavKeys.KEY_FM_LOGIN, "")
                        isSplashVisible = false
                    }, 1000)
                }
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    FullScreenActivity.startIntentWithFinish(this, NavKeys.KEY_FM_DASHBOARD)
                    isSplashVisible = false
                }, 1000)
            }
    }

    private fun navigateDeepLink(path: List<String>) {
        val firstSegment = path[0]
        val twoSegment = if (path.size > 1) path[1] else ""

        when (firstSegment) {
            "home" -> MainActivity.startIntentWithFinish(this, NavKeys.KEY_MAIN_HOME)
            "discover" -> MainActivity.startIntentWithFinish(this, NavKeys.KEY_MAIN_DISCOVER)
            "portofolio" -> {
                val layoutPosition = if (twoSegment == "orderlist") 1 else 0
                MainActivity.startIntentParam(this, NavKeys.KEY_FM_TAB_PORTFOLIO, layoutPosition, "")
            }

            "order" -> {
                val main = Intent(this, MainActivity::class.java)
                main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

                val middle = Intent(this, MiddleActivity::class.java)
                    .putExtra(NavKeys.KEY_NAV_MIDDLE, NavKeys.KEY_FM_ORDER)
                    .putExtra(Args.EXTRA_PARAM_STR_ONE, "BBCA")


                startActivities(arrayOf(main, middle))
                finish()
            }

            "news" -> {
                val layoutPosition = if (twoSegment == "research") 1 else 0
                MainActivity.startIntentParam(this, NavKeys.KEY_MAIN_NEWS, layoutPosition, "")
            }

            "profile" -> MainActivity.startIntentWithFinish(this, NavKeys.KEY_MAIN_PROFILE)
            "stockpick" -> MainActivity.startIntentWithFinish(this, NavKeys.KEY_MAIN_STOCK_PICK)
            "calendar" -> MainActivity.startIntentWithFinish(this, NavKeys.KEY_FM_CALENDAR)
            "fast-order" -> {
                val main = Intent(this, MainActivity::class.java)
                main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

                val middle = Intent(this, MiddleActivity::class.java)
                    .putExtra(NavKeys.KEY_NAV_MIDDLE, NavKeys.KEY_FM_FAST_ORDER)
                    .putExtra(Args.EXTRA_PARAM_STR_ONE, "BBCA")
                    .putExtra(Args.EXTRA_PARAM_STR_TWO, "Bank Central Asia Tbk")


                startActivities(arrayOf(main, middle))
                finish()
            }

            "price-alert" -> {
                val main = Intent(this, MainActivity::class.java)
                main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

                val middle = Intent(this, MiddleActivity::class.java)
                    .putExtra(NavKeys.KEY_NAV_MIDDLE, NavKeys.KEY_FM_PRICE_ALERT)


                startActivities(arrayOf(main, middle))
                finish()
            }

            "broker-sum" -> MainActivity.startIntentWithFinish(this, NavKeys.KEY_FM_BROKER_SUMMARY)
            "run-trade" -> MainActivity.startIntentWithFinish(this, NavKeys.KEY_FM_RUNNING_TRADE)
            "global-market" -> MainActivity.startIntentWithFinish(this, NavKeys.KEY_FM_GLOBAL_MARKET)
            "right-issue" -> {
                val main = Intent(this, MainActivity::class.java)
                main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

                val middle = Intent(this, MiddleActivity::class.java)
                    .putExtra(NavKeys.KEY_NAV_MIDDLE, NavKeys.KEY_FM_RIGHT_ISSUE)


                startActivities(arrayOf(main, middle))
                finish()
            }
            "stock-detail" -> {
                val main = Intent(this, MainActivity::class.java)
                main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

                val middle = Intent(this, MiddleActivity::class.java)
                    .putExtra(NavKeys.KEY_NAV_MIDDLE, NavKeys.KEY_FM_STOCK_DETAIL)
                    .putExtra(Args.EXTRA_PARAM_STR_ONE, twoSegment)

                startActivities(arrayOf(main, middle))
                finish()
            }

            else -> MainActivity.startIntentWithFinish(this, NavKeys.KEY_MAIN_HOME)
        }
    }

}