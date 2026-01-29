package com.bcasekuritas.mybest.app.feature.activity.landscape

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.BuildConfig
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseActivity
import com.bcasekuritas.mybest.app.data.entity.SessionObject
import com.bcasekuritas.mybest.app.data.layout.UIDialogModel
import com.bcasekuritas.mybest.app.domain.dto.response.PortfolioOrderItem
import com.bcasekuritas.mybest.app.feature.activity.main.MainViewModel
import com.bcasekuritas.mybest.app.feature.activity.main.SharedMainViewModel
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.dialog.center.DialogLoadingCenter
import com.bcasekuritas.mybest.databinding.ActivityLandscapeBinding
import com.bcasekuritas.mybest.ext.common.RabbitMQForegroundService
import com.bcasekuritas.mybest.ext.common.validateSessionPin
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber

@AndroidEntryPoint
class LandscapeActivity: BaseActivity<ActivityLandscapeBinding, MainViewModel>(),
    ShowDialog by ShowDialogImpl()  {
    override val binding: ActivityLandscapeBinding by lazy { ActivityLandscapeBinding.inflate(layoutInflater) }
    override val viewModel: MainViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }
    override val bindingVariable: Int = BR.vmActLandscape

    private val sharedViewModel: SharedMainViewModel by lazy {
        ViewModelProvider(this).get(SharedMainViewModel::class.java)
    }

    private var paramStrOne: String? = null
    private var paramStrTwo: String? = null

    private var retryTimer: CountDownTimer? = null
    private lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig

    override fun onInitViews() {
        setContentView(binding.root)
    }

    companion object {
        fun startIntent(activity: Activity, keyNavigate: String) {
            val starter = Intent(activity, LandscapeActivity::class.java)
                .putExtra(NavKeys.KEY_NAV_LANDSCAPE, keyNavigate)
            activity.startActivity(starter)
        }

        fun startIntentParam(activity: Activity, keyNav: String, paramOne: Any, paramTwo: Any) {
            val starter = Intent(activity, LandscapeActivity::class.java)
                .putExtra(NavKeys.KEY_NAV_LANDSCAPE, keyNav)
            when (paramOne) {
                is String -> {
                    starter.putExtra(Args.EXTRA_PARAM_STR_ONE, paramOne)
                }

                else -> {}
            }
            when (paramTwo) {
                is String -> {
                    starter.putExtra(Args.EXTRA_PARAM_STR_TWO, paramTwo)
                }

                else -> {}
            }
            activity.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel.setStopSubs(false)
    }

    override fun onResume() {
        super.onResume()
//        fetchRemoteConfig()
    }

    override fun setupArguments() {
        super.setupArguments()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.f_landscape_container) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.navigation_landscape)
        var bundle = Bundle()

        intent?.let {
            paramStrOne = intent.getStringExtra(Args.EXTRA_PARAM_STR_ONE)
            paramStrTwo = intent.getStringExtra(Args.EXTRA_PARAM_STR_TWO)

            when (intent.getStringExtra(NavKeys.KEY_NAV_LANDSCAPE)) {
                NavKeys.KEY_FM_TRADING_VIEW -> {
                    graph.setStartDestination(R.id.trading_view_fragment)
                    bundle = Bundle().apply {
                        putString(Args.EXTRA_PARAM_STR_ONE, paramStrOne)
                    }
                }
            }
        }
        val navController = navHostFragment.navController
        navController.setGraph(graph, bundle)
    }

    override fun setupObserver() {
        viewModel.appNotificationLiveData.observe(this) {
            if (it != null){
                when (it.category) {
                    1 -> {
                        Timber.d("App Info Dialog Forced Logout")
                        showDialogInfoCenterCallBack(false,
                            supportFragmentManager, UIDialogModel(
                                titleStr = "Forced Logout",
                                descriptionStr = "Your account is in use on a different device",
                                btnPositive = R.string.logout_button_positive
                            ),
                            onOkClicked = {
                                it.let {
                                    viewModel.deleteSessionPin()
                                    viewModel.getLogout(prefManager.userId,prefManager.sessionId)
                                }
                            })
                    }

                    2 -> {
                        viewModel.deleteSessionPin()
                        Timber.d("App Info Dialog Session Expired")
                        showDialogInfoCenterCallBack(false,
                            supportFragmentManager, UIDialogModel(
                                titleStr = "Session Expired",
                                descriptionStr = "Please re-login to renew your session",
                                btnPositive = R.string.logout_button_positive
                            ),
                            onOkClicked = {
                                it.let {
                                    viewModel.getLogout(prefManager.userId,prefManager.sessionId)
                                }
                            })
                    }

                    3 -> {

                    }

                    4 -> {

                        Timber.d("App notif pin Middle")
                        Timber.d("Recive app info Session Pin")
                        val timeSecond = System.currentTimeMillis() / 1000
                        val remain = timeSecond + it.remain / 1000
                        viewModel.insertSession(
                            SessionObject(
                                prefManager.userId,
                                prefManager.sessionId,
                                remain
                            )
                        )

                    }
                }
                viewModel.clearAppNotification()
            }

        }

        viewModel.orderReplyLiveData.observe(this){
            //            showSnackBarOrder(
//                baseContext,
//                binding.root,
//                it.buySell,
//                it.stockCode,
//                it.status.GET_STATUS_ORDER() ?: "",
//                it.lotSize.toString(),
//                it.ordPrice
//            )
            it?.let {
                viewModel.clearOrderReply()

                viewModel.getOrderList(
                    prefManager.userId,
                    prefManager.accno,
                    prefManager.sessionId,
                    0
                )

            }
        }





        viewModel.getOrderListResult.observe(this) {
            if (it?.ordersList?.size != 0) {
                val orderListItemMapper = ArrayList<PortfolioOrderItem>()
                it?.ordersList?.map { data ->
                    orderListItemMapper.add(
                        PortfolioOrderItem(
                            data.odId,
                            data.exordid,
                            data.odTime,
                            data.ostatus,
                            data.bs,
                            data.ordertype,
                            data.stockcode,
                            data.remark,
                            data.oprice,
                            data.oqty,
                            data.mqty,
                            timeInForce = data.timeInForce
                        )
                    )
                }
                orderListItemMapper.sortBy { item -> item.time }
            }
        }

        sharedViewModel.stopSubs.observe(this) {
            if (it) {
                stopSubcribe()
            }
        }

        viewModel.getLogoutResult.observe(this){
            when (it?.status) {
                0 -> {
                    stopSubcribe()
                    RabbitMQForegroundService.stopService(this)
                    MiddleActivity.startIntentWithFinish(
                        this,
                        NavKeys.KEY_FM_LOGIN,
                        ""
                    )
                }
                1 -> {
                    Timber.e(it.remarks)
                }
                else -> {
                    stopSubcribe()
                    RabbitMQForegroundService.stopService(this)
                    MiddleActivity.startIntentWithFinish(
                        this,
                        NavKeys.KEY_FM_LOGIN,
                        ""
                    )
                }
            }
        }

        viewModel.imqConnectionListener.connListenerLiveData.observe(this) { value ->
            when (value) {
                "Shutdown", "Recovery" -> {
                    showLoading()
                    if (retryTimer != null) { return@observe }
                    retryTimer = object : CountDownTimer(15000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                        }

                        override fun onFinish() {
                            try {
                                if (!isFinishing || !isDestroyed && !supportFragmentManager.isStateSaved) {
                                    hideLoading()
                                    showDialogLoadingCenter(
                                        false,
                                        UIDialogModel(
                                            titleStr = "Connection Lost",
                                            descriptionStr = "Please check your internet connection \nand reconnect to the server"
                                        ), supportFragmentManager
                                    )
                                }
                            } catch (ignore: Exception) {}
                        }
                    }.also { it.start() }
                }

                "Done Recovered" -> {
                    retryTimer?.cancel()
                    retryTimer == null
//                    viewModel.startSubsRecovered()
                }

                "Recovered" -> {
                    viewModel.imqConnectionListener.onListener("")
                    retryTimer?.cancel()
                    try {
                        hideLoading()
                        dismissDialogLoadingCenter()
                        supportFragmentManager.fragments.forEach { fragment ->
                            if (fragment is DialogLoadingCenter) {
                                fragment.dismiss()
                            }
                        }
                    } catch (ignore: Exception) {}
                    //
                    val userId = prefManager.userId
                    val sessionId = prefManager.sessionId
                    if (userId.isNotEmpty() && sessionId.isNotEmpty()) {
                        viewModel.validateSession(userId, sessionId)
                    }
                }

                else -> {}
            }
        }

        viewModel.imqConnectionListener.isSessionExpiredLiveData.observe(this) { value ->
            if (value == true) {
                viewModel.deleteSessionPin()
                Timber.d("App Info Dialog Session Expired")
                showDialogInfoCenterCallBack(false,
                    supportFragmentManager, UIDialogModel(
                        titleStr = "Session Expired",
                        descriptionStr = "Please re-login to renew your session",
                        btnPositive = R.string.logout_button_positive
                    ),
                    onOkClicked = {
                        it.let {
                            viewModel.getLogout(prefManager.userId, prefManager.sessionId)
                        }
                    })
            }
        }

        viewModel.imqConnectionListener.isPinExpiredLiveData.observe(this) { value ->
            if (value == true) {
                viewModel.validateSessionByPin(prefManager.userId, prefManager.sessionId)
            }
        }

        viewModel.imqConnectionListener.timeOutLiveData.observe(this) { value ->
            value?.let {
                showSnackBarTop(
                    baseContext, binding.root,
                    "error",
                    R.drawable.ic_error,
                    value,
                    "Code: 408 - Message: Request time out.",
                    this, ""
                )
                viewModel.imqConnectionListener.timeOutLiveData.postValue(null)
            }
        }

        viewModel.getValidateSessionResult.observe(this) { data->
            if (data != null) {
                if (data.status == 1 || data.status == 2) {
                    showDialogSessionExpired()
                }
            }
        }

        viewModel.getValidateSessionByPinResult.observe(this) { data->
            if (data != null) {
                when (data.status) {
                    0 -> {
//                        val lastEntry = getNavHost(binding.fMiddleContainer).backQueue.last().destination.id
//                        if (lastEntry != R.id.stock_detail_fragment) {
//                            showDialogPin()
//                        }
                    }
                    1,2 -> showDialogSessionExpired()
                }
            }
        }

        viewModel.getSessionPinResult.observe(this) {
            if (it != null) {
                if (!validateSessionPin(it)) {
                    viewModel.validateSessionByPin(prefManager.userId, prefManager.sessionId)
                }
            } else {
                viewModel.validateSessionByPin(prefManager.userId, prefManager.sessionId)
            }
        }
    }

    internal fun showDialogSessionExpired() {
        viewModel.deleteSessionPin()
        showDialogInfoCenterCallBack(false,
            supportFragmentManager, UIDialogModel(
                titleStr = "Session Expired",
                descriptionStr = "Please re-login to renew your session",
                btnPositive = R.string.logout_button_positive
            ),
            onOkClicked = {
                it.let {
                    stopSubcribe()
                    MiddleActivity.startIntentWithFinish(
                        this,
                        NavKeys.KEY_FM_LOGIN,
                        ""
                    )
                }
            })
    }

    internal fun showDialogPin() {
        showDialogPin(supportFragmentManager, onSuccess = { isSuccess, isBlocked ->
            if (isSuccess) {

            } else {
                if (isBlocked) {
                    showDialogAccountDisable(supportFragmentManager)
                }
            }
        })
    }

    private fun stopSubcribe() {
        viewModel.appNotificationLiveData.removeObservers(this)
        viewModel.orderReplyLiveData.removeObservers(this)

        viewModel.clearAppNotification()
        viewModel.clearOrderReply()

        viewModel.unSubscribeAppNotification(prefManager.sessionId)
        viewModel.unsubscribeOrderReply(prefManager.accno)

        viewModel.stopAppNotification()
        viewModel.stopOrderReply()
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
            val versionName = androidConfig.getString("version_name")
            val versionCode = androidConfig.getInt("version_code")
            val isForceUpdate = androidConfig.getBoolean("is_force_update")

            if (versionCode > BuildConfig.VERSION_CODE) {
                showDialogForcedUpdate(supportFragmentManager, isForceUpdate, { onClick ->
                    if (onClick) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.bcasekuritas.mybest")))
                    }
                })
            }
            Timber.tag("SplashActivity").d("Version Name: $versionName, Version Code: $versionCode")
        } catch (e: Exception) {
            Timber.tag("SplashActivity").e("Failed to parse android_config JSON : $e")
        }
    }
}