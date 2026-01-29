package com.bcasekuritas.mybest.app.feature.activity.middle

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.BuildConfig
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseActivity
import com.bcasekuritas.mybest.app.data.entity.SessionObject
import com.bcasekuritas.mybest.app.data.layout.UIDialogModel
import com.bcasekuritas.mybest.app.domain.dto.response.PortfolioOrderItem
import com.bcasekuritas.mybest.app.domain.dto.response.PortfolioStockDataItem
import com.bcasekuritas.mybest.app.domain.dto.response.RdnHistoryItem
import com.bcasekuritas.mybest.app.feature.activity.main.MainViewModel
import com.bcasekuritas.mybest.app.feature.activity.main.SharedMainViewModel
import com.bcasekuritas.mybest.app.feature.dialog.center.DialogLoadingCenter
import com.bcasekuritas.mybest.databinding.ActivityMiddleBinding
import com.bcasekuritas.mybest.ext.common.RabbitMQForegroundService
import com.bcasekuritas.mybest.ext.common.validateSessionPin
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarInterface
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber

@AndroidEntryPoint
class MiddleActivity : BaseActivity<ActivityMiddleBinding, MainViewModel>(),
    ShowDialog by ShowDialogImpl(), ShowSnackBarInterface by ShowSnackBarImpl() {

    override val bindingVariable: Int = BR.vmActMiddle
    override val binding: ActivityMiddleBinding by lazy { ActivityMiddleBinding.inflate(layoutInflater) }
    override val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }
    private val sharedViewModel: SharedMainViewModel by lazy {
        ViewModelProvider(this).get(SharedMainViewModel::class.java)
    }

    //
    private var paramStrOne: String? = null
    private var paramStrTwo: String? = null

    private var paramBoolean: Boolean = false

    //
    private var paramIntOne: Int? = null
    private var paramIntTwo: Int? = null
    private var retryTimer: CountDownTimer? = null

    private var paramOrderDetail: PortfolioOrderItem? = PortfolioOrderItem()
    private var paramStockDetail: PortfolioStockDataItem? = PortfolioStockDataItem()
    private var paramRdnHistory: RdnHistoryItem? = RdnHistoryItem()

    private lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig

    companion object {
        fun startIntentParamWithFinish(activity: Activity, keyNavigate: String, strOne: String, strTwo: String) {
            val starter = Intent(activity, MiddleActivity::class.java)
                .putExtra(NavKeys.KEY_NAV_MIDDLE, keyNavigate)
                .putExtra(Args.EXTRA_PARAM_STR_ONE, strOne)
                .putExtra(Args.EXTRA_PARAM_STR_TWO, strTwo)
            activity.startActivity(starter)
            activity.finish()
        }

        fun startIntentWithFinish(activity: Activity, keyNavigate: String, paramOne: Any) {
            val starter = Intent(activity, MiddleActivity::class.java)
                .putExtra(NavKeys.KEY_NAV_MIDDLE, keyNavigate)
            when (paramOne) {
                is Boolean -> starter.putExtra(Args.EXTRA_PARAM_BOOLEAN, paramOne)
                else -> {}
            }
            if (keyNavigate == NavKeys.KEY_FM_LOGIN) {
                starter.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            activity.startActivity(starter)
            activity.finish()
        }

        fun startIntent(activity: Activity, keyNavigate: String) {
            val starter = Intent(activity, MiddleActivity::class.java)
                .putExtra(NavKeys.KEY_NAV_MIDDLE, keyNavigate)
            activity.startActivity(starter)
        }

        fun startIntentParam(activity: Activity, keyNav: String, paramOne: Any, paramTwo: Any) {
            val starter = Intent(activity, MiddleActivity::class.java)
                .putExtra(NavKeys.KEY_NAV_MIDDLE, keyNav)
            when (paramOne) {
                is String -> {
                    starter.putExtra(Args.EXTRA_PARAM_STR_ONE, paramOne)
                }

                is Int -> {
                    starter.putExtra(Args.EXTRA_PARAM_INT_ONE, paramOne)
                }

                is PortfolioOrderItem -> {
                    starter.putExtra(Args.EXTRA_PARAM_OBJECT, paramOne)
                }

                is PortfolioStockDataItem -> {
                    starter.putExtra(Args.EXTRA_PARAM_OBJECT, paramOne)
                }

                is RdnHistoryItem -> {
                    starter.putExtra(Args.EXTRA_PARAM_OBJECT, paramOne)
                }

                else -> {}
            }
            when (paramTwo) {
                is String -> {
                    starter.putExtra(Args.EXTRA_PARAM_STR_TWO, paramTwo)
                }

                is Int -> {
                    starter.putExtra(Args.EXTRA_PARAM_INT_TWO, paramTwo)
                }

                else -> {}
            }
            activity.startActivity(starter)
        }
    }

    override fun setupObserver() {
        viewModel.appNotificationLiveData.observe(this) {
            if (it != null){
                val currentFragmentNavigation = getNavHost(binding.fMiddleContainer).currentDestination?.id ?: 0
                val isLoginFragment = currentFragmentNavigation != 0 && currentFragmentNavigation == R.id.login_fragment
                when (it.category) {
                    1 -> {
                        if (!isLoginFragment) {
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

                    }

                    2 -> {
                        viewModel.deleteSessionPin()
                        if (!isLoginFragment) {
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

        viewModel.newOrderNotifLiveData.observe(this) {
            // show notif snackbar top after order
            if (it) {
                showSnackBarTop(
                    baseContext,
                    binding.root,
                    "success",
                    R.drawable.ic_success,
                    "Order Placed",
                    getString(R.string.desc_snackbar_order_success), this, ""
                )
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
                    RabbitMQForegroundService.stopService(this)
                    stopSubcribe()
                    startIntentWithFinish(
                        this,
                        NavKeys.KEY_FM_LOGIN,
                        ""
                    )
                }
                1 -> {
                    Timber.e(it.remarks)
                }
                else -> {
                    RabbitMQForegroundService.stopService(this)
                    stopSubcribe()
                    startIntentWithFinish(
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
                    viewModel.startSubsRecovered()
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
                val currentFragment = getNavHost(binding.fMiddleContainer).currentDestination?.id ?: 0
                if (currentFragment != R.id.login_fragment && currentFragment != 0) {
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
        }

        viewModel.imqConnectionListener.isPinExpiredLiveData.observe(this) { value ->
            if (value == true) {
                val lastEntry = getNavHost(binding.fMiddleContainer).backQueue.last().destination.id
                if (lastEntry != R.id.login_fragment) {
                    viewModel.validateSessionByPin(prefManager.userId, prefManager.sessionId)
                }
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
                        val lastEntry = getNavHost(binding.fMiddleContainer).backQueue.last().destination.id
                        if (lastEntry != R.id.stock_detail_fragment) {
                            showDialogPin()
                        }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        sharedViewModel.setStopSubs(false)
    }

    override fun onResume() {
        super.onResume()
        val lastEntry = getNavHost(binding.fMiddleContainer).backQueue.last().destination.id
        if (lastEntry != R.id.login_fragment) {
            viewModel.imqConnectionListener.connListenerLiveData.value?.let { conStatus ->
                if (conStatus == "Recovered") {
                    val userId = prefManager.userId
                    val sessionId = prefManager.sessionId
                    if (userId.isNotEmpty() && sessionId.isNotEmpty()) {
                        viewModel.validateSession(userId, sessionId)
                        viewModel.saveFCMToken(userId, sessionId, prefManager.fcmToken)
                    }
                }
            }
        }
//        fetchRemoteConfig()
    }

    private fun stopSubcribe() {
        viewModel.appNotificationLiveData.removeObservers(this)
        viewModel.orderReplyLiveData.removeObservers(this)
        viewModel.newOrderNotifLiveData.removeObservers(this)

        viewModel.clearAppNotification()
        viewModel.clearOrderReply()

        viewModel.unSubscribeAppNotification(prefManager.sessionId)
        viewModel.unsubscribeOrderReply(prefManager.accno)

        viewModel.stopAppNotification()
        viewModel.stopOrderReply()
    }

    override fun setupArguments() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.f_middle_container) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.navigation_mid)
        var bundle = Bundle()

        intent?.let {
            paramStrOne = intent.getStringExtra(Args.EXTRA_PARAM_STR_ONE)
            paramStrTwo = intent.getStringExtra(Args.EXTRA_PARAM_STR_TWO)
            paramIntOne = intent.getIntExtra(Args.EXTRA_PARAM_INT_ONE, 0)
            paramIntTwo = intent.getIntExtra(Args.EXTRA_PARAM_INT_TWO, 0)
            paramBoolean = intent.getBooleanExtra(Args.EXTRA_PARAM_BOOLEAN, false)

            when (intent.getStringExtra(NavKeys.KEY_NAV_MIDDLE)) {
                NavKeys.KEY_FM_LOGIN -> {
                    bundle = Bundle().apply {
                        putString(Args.EXTRA_PARAM_STR_ONE, paramStrOne)
                        putInt(Args.EXTRA_PARAM_INT_ONE, paramIntTwo!!)
                        putBoolean(Args.EXTRA_PARAM_BOOLEAN, paramBoolean)
                    }
                    graph.setStartDestination(R.id.login_fragment)
                    stopSubcribe()
                }
                NavKeys.KEY_FM_TOP_UP -> {
                    graph.setStartDestination(R.id.top_up_fragment)
                }
                NavKeys.KEY_FM_WITHDRAW -> {
                    graph.setStartDestination(R.id.withdraw_fragment)
                }
                NavKeys.KEY_FM_PROFILE_SECURITY -> {
                    graph.setStartDestination(R.id.security_fragment)
                }
                NavKeys.KEY_FM_E_STATEMENT -> {
                    graph.setStartDestination(R.id.e_statements_fragment)
                }
                NavKeys.KEY_FM_NOTIFICATION_SETTINGS -> {
                    graph.setStartDestination(R.id.notification_settings_fragment)
                }
                NavKeys.KEY_FM_RDN_DETAIL_HISTORY -> {
                    graph.setStartDestination(R.id.detail_rdn_history_fragment)
                    paramRdnHistory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(
                            Args.EXTRA_PARAM_OBJECT,
                            RdnHistoryItem::class.java
                        )
                    } else {
                        intent.getParcelableExtra(Args.EXTRA_PARAM_OBJECT)
                    }

                    bundle = Bundle().apply {
                        putParcelable(Args.EXTRA_PARAM_OBJECT, paramRdnHistory)
                    }
                }
                NavKeys.KEY_FM_RUNNING_TRADE -> {
                    graph.setStartDestination(R.id.running_trade_fragment)
                }
                NavKeys.KEY_FM_SEARCH_STOCK -> {
                    bundle = Bundle().apply {
                        putString(Args.EXTRA_PARAM_STR_ONE, paramStrOne)
                        putInt(Args.EXTRA_PARAM_INT_ONE, 1)
                    }
                    graph.setStartDestination(R.id.search_stock_fragment)
                }
                NavKeys.KEY_FM_MANAGE_WATCHLIST -> {
                    graph.setStartDestination(R.id.manage_watchlist_fragment)
                }
                NavKeys.KEY_FM_STOCK_DETAIL -> {
                    graph.setStartDestination(R.id.stock_detail_fragment)
                    bundle = Bundle().apply {
                        putString(Args.EXTRA_PARAM_STR_ONE, paramStrOne)
                    }
                }
                NavKeys.KEY_FM_FAST_ORDER-> {
                    graph.setStartDestination(R.id.fast_order_fragment)
                    bundle = Bundle().apply {
                        putString(Args.EXTRA_PARAM_STR_ONE, paramStrOne)
                        putString(Args.EXTRA_PARAM_STR_TWO, paramStrTwo)
                    }
                }

                NavKeys.KEY_FM_PORTFOLIO_DETAIL -> {
                    graph.setStartDestination(R.id.portfolio_detail_fragment)
                    paramStockDetail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(
                            Args.EXTRA_PARAM_OBJECT,
                            PortfolioStockDataItem::class.java
                        )
                    } else {
                        intent.getParcelableExtra(Args.EXTRA_PARAM_OBJECT)
                    }

                    bundle = Bundle().apply {
                        putParcelable(Args.EXTRA_PARAM_OBJECT, paramStockDetail)
                    }
                }

                NavKeys.KEY_FM_ORDER -> {
                    graph.setStartDestination(R.id.order_fragment)
                    paramOrderDetail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(
                            Args.EXTRA_PARAM_OBJECT,
                            PortfolioOrderItem::class.java
                        )
                    } else {
                        intent.getParcelableExtra(Args.EXTRA_PARAM_OBJECT)
                    }

                    bundle = Bundle().apply {
                        putInt(Args.EXTRA_PARAM_INT_ONE, paramIntTwo ?: 0)
                        putString(Args.EXTRA_PARAM_STR_ONE, paramStrOne)
                        putParcelable(Args.EXTRA_PARAM_OBJECT, paramOrderDetail)
                    }
                }

                NavKeys.KEY_FM_STOCK_PICK -> {
                    graph.setStartDestination(R.id.stock_pick_fragment)
                }
                NavKeys.KEY_FM_INPUT_PIN -> {
                    graph.setStartDestination(R.id.pin_fragment)
                }
                NavKeys.KEY_FM_INDEX -> {
                    graph.setStartDestination(R.id.index_fragment)
                }
                NavKeys.KEY_FM_SECTOR -> {
                    graph.setStartDestination(R.id.sector_fragment)
                }
                NavKeys.KEY_FM_INDEX_DETAIL -> {
                    graph.setStartDestination(R.id.index_detail_fragment)
                    bundle = Bundle().apply {
                        putString(Args.EXTRA_PARAM_STR_ONE, paramStrOne)
                        putInt(Args.EXTRA_PARAM_INT_ONE, paramIntTwo!!)
                    }
                }
                NavKeys.KEY_FM_CATEGORIES -> {
                    graph.setStartDestination(R.id.categories_fragment)
                }
                NavKeys.KEY_FM_BROKER_SUMMARY -> {
                    graph.setStartDestination(R.id.broker_summary_fragment)
                }
                NavKeys.KEY_FM_ACCOUNT_DISABLED -> {
                    graph.setStartDestination(R.id.account_disabled_fragment)
                }
                /*NavKeys.KEY_FM_PORTFOLIO_CASH -> {
                    graph.setStartDestination(R.id.portfolio_cash_fragment)
                }*/
                NavKeys.KEY_FM_STOP_LOSS_TAKE_PROFIT -> {
                    graph.setStartDestination(R.id.stop_loss_take_profit_fragment)
                }
                NavKeys.KEY_FM_CALENDAR -> {
                    graph.setStartDestination(R.id.calendar_fragment)
                }
                NavKeys.KEY_FM_TRADING_VIEW -> {
                    graph.setStartDestination(R.id.trading_view_fragment)
                    bundle = Bundle().apply {
                        putString(Args.EXTRA_PARAM_STR_ONE, paramStrOne)
                    }
                }
                NavKeys.KEY_FM_CONDITION_ADVANCED -> {
                    graph.setStartDestination(R.id.portfolio_detail_fragment)
                    paramStockDetail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(
                            Args.EXTRA_PARAM_OBJECT,
                            PortfolioStockDataItem::class.java
                        )
                    } else {
                        intent.getParcelableExtra(Args.EXTRA_PARAM_OBJECT)
                    }

                    bundle = Bundle().apply {
                        putParcelable(Args.EXTRA_PARAM_OBJECT, paramStockDetail)
                    }
                }
                NavKeys.KEY_FM_RIGHT_ISSUE -> {
                    graph.setStartDestination(R.id.right_issue_fragment)
                }
                NavKeys.KEY_FM_REALIZED -> {
                    graph.setStartDestination(R.id.realized_fragment)
                }
                NavKeys.KEY_FM_PRICE_ALERT -> {
                    graph.setStartDestination(R.id.price_alert_fragment)
                }
                NavKeys.KEY_FM_LINE_SETTING -> {
                    graph.setStartDestination(R.id.line_setting_fragment)
                }
                NavKeys.KEY_FM_NOTIFICATION -> {
                    graph.setStartDestination(R.id.notification_fragment)
                }
                NavKeys.KEY_FM_DISCLAIMER -> {
                    graph.setStartDestination(R.id.disclaimer_fragment)
                }
                NavKeys.KEY_FM_EIPO_DETAIL -> {
                    graph.setStartDestination(R.id.eipo_detail_fragment)
                    bundle = Bundle().apply {
                        putString(Args.EXTRA_PARAM_STR_ONE, paramStrOne)
                    }
                }
                NavKeys.KEY_FM_EIPO_ORDER_LIST -> {
                    graph.setStartDestination(R.id.eipo_order_list_fragment)
                }

                NavKeys.KEY_FM_MANAGE_DEVICE -> {
                    graph.setStartDestination(R.id.manage_device_fragment)
                }

            }
        }
        val navController = navHostFragment.navController
        navController.setGraph(graph, bundle)
    }

    override fun onInitViews() {
        setContentView(binding.root)

    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
//            val lastEntry = getNavHost(binding.fMiddleContainer).backQueue.last()   //NO QUEUE CAUSING AN ERROR

            if (getNavHost(binding.fMiddleContainer).backQueue.size > 2)
                getNavHost(binding.fMiddleContainer).popBackStack()
            else
                finish()
        }
    }

    internal fun showDialogSessionExpired() {
        viewModel.deleteSessionPin()
        val currentFragment = getNavHost(binding.fMiddleContainer).currentDestination?.id ?: 0
        if (currentFragment != R.id.login_fragment && currentFragment != 0) {
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
    }

    internal fun showDialogPin() {
        showDialogPin(supportFragmentManager, onSuccess = { isSuccess, isBlocked ->
            if (isSuccess) {
                viewModel.getSessionPin(prefManager.userId)
            } else {
                if (isBlocked) {
                    showDialogAccountDisable(supportFragmentManager)
                }
            }
        })
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