package com.bcasekuritas.mybest.app.feature.activity.main

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.BuildConfig
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseActivity
import com.bcasekuritas.mybest.app.data.entity.SessionObject
import com.bcasekuritas.mybest.app.data.layout.UIDialogModel
import com.bcasekuritas.mybest.app.domain.dto.response.AdvancedOrderDetail
import com.bcasekuritas.mybest.app.domain.dto.response.OrderSuccessSnackBar
import com.bcasekuritas.mybest.app.domain.dto.response.PortfolioOrderItem
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.dialog.center.DialogLoadingCenter
import com.bcasekuritas.mybest.databinding.ActivityMainBinding
import com.bcasekuritas.mybest.ext.common.RabbitMQForegroundService
import com.bcasekuritas.mybest.ext.common.validateSessionPin
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(),
    ShowDialog by ShowDialogImpl() {

    override val bindingVariable: Int = BR.vmActMain
    override val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }
    private val sharedViewModel: SharedMainViewModel by lazy {
        ViewModelProvider(this).get(SharedMainViewModel::class.java)
    }

    private lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig

    private var activeNavigation: Int = 0
    private var currentFragmentNavigation: Int = 0

    private var isStartAppNotif = false

    private var paramOrderDetail: PortfolioOrderItem? = PortfolioOrderItem()

    //
    private var paramStrOne: String? = null
    private var paramStrTwo: String? = null

    //
    private var paramIntOne: Int? = null
    private var paramIntTwo: Int? = null
    private var paramBoolean: Boolean = false
    private var navId: Int? = null
    private var retryTimer: CountDownTimer? = null


    private var currentNavItem: Int = R.id.navigation_home

    private var paramSnackbarWithdraw: OrderSuccessSnackBar? =
        OrderSuccessSnackBar()

    companion object {
        fun startIntent(activity: Activity) {
            val starter = Intent(activity, MainActivity::class.java)
            activity.startActivity(starter)
            activity.finish()
        }

        fun startIntentWithFinish(activity: Activity, keyNavigate: String) {
            val starter = Intent(activity, MainActivity::class.java)
                .putExtra(NavKeys.KEY_NAV_MAIN, keyNavigate)
            activity.startActivity(starter)
            activity.finish()
        }

        fun startIntentParam(activity: Activity, keyNav: String, paramOne: Any, paramTwo: Any) {
            val starter = Intent(activity, MainActivity::class.java)
                .putExtra(NavKeys.KEY_NAV_MAIN, keyNav)
            if (keyNav == NavKeys.KEY_FM_TAB_PORTFOLIO && paramOne == 1) {
                starter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            if (keyNav == NavKeys.KEY_MAIN_DISCOVER && paramOne == 1) {
                starter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            when (paramOne) {
                is String -> {
                    starter.putExtra(Args.EXTRA_PARAM_STR_ONE, paramOne)
                }

                is Int -> {
                    starter.putExtra(Args.EXTRA_PARAM_INT_ONE, paramOne)
                }

                is Boolean -> {
                    starter.putExtra(Args.EXTRA_PARAM_BOOLEAN, paramOne)
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

                is Boolean -> {
                    starter.putExtra(Args.EXTRA_PARAM_BOOLEAN, paramTwo)
                }

                is OrderSuccessSnackBar -> {
                    starter.putExtra(Args.EXTRA_PARAM_OBJECT, paramTwo)
                }

                is AdvancedOrderDetail -> {
                    starter.putExtra(Args.EXTRA_PARAM_OBJECT_TWO, paramTwo)
                }

                else -> {}
            }
            activity.startActivity(starter)
            activity.finish()
        }

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setupArgs(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) { // Android 15
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        sharedViewModel.setStopSubs(false)
    }

    override fun onInitViews() {
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) { // Android 15
            ViewCompat.setOnApplyWindowInsetsListener(binding.root.rootView) { view, insets ->
                val topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
                view.setPadding(0, topInset, 0, 0)
                insets
            }
        }
    }

    override fun initAPI() {
        super.initAPI()

        if (!isStartAppNotif) {

            viewModel.subscribeAppNotification(prefManager.sessionId)
            viewModel.subscribeOrderReply(prefManager.accno)

            isStartAppNotif = true
        }
    }

    override fun onResume() {
        super.onResume()
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
        fetchRemoteConfig()
    }
    override fun onDestroy() {
        super.onDestroy()
        Timber.d("sendPing stop timer")
    }

    override fun setupObserver() {
        viewModel.appNotificationLiveData.observe(this) {
            if (it != null) {
                when (it.category) {
                    1 -> {
                        viewModel.deleteSessionPin()
                        Timber.d("App Info Dialog Forced Logout")
                        showDialogInfoCenterCallBack(false,
                            supportFragmentManager, UIDialogModel(
                                titleStr = "Forced Logout",
                                descriptionStr = "Your account is in use on a different device",
                                btnPositive = R.string.logout_button_positive
                            ),
                            onOkClicked = {
                                it.let {
                                    viewModel.getLogout(prefManager.userId, prefManager.sessionId)
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
                                    viewModel.getLogout(prefManager.userId, prefManager.sessionId)
                                }
                            })
                    }

                    3 -> {

                    }

                    4 -> {
                        Timber.d("App notif pin Main")
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

        viewModel.orderReplyLiveData.observe(this) {
            it?.let {

                /*showSnackBarOrder(
                    baseContext,
                    binding.root,
                    it.buySell,
                    it.stockCode,
                    it.status.GET_STATUS_ORDER() ?: "",
                    it.lotSize.toString(),
                    it.ordPrice
                )*/
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

        viewModel.getLogoutResult.observe(this) {
            when (it?.status) {
                0 -> {
                    stopSubcribe()
                    prefManager.clearPreferences()
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
                    RabbitMQForegroundService.stopService(this)
                    stopSubcribe()
                    MiddleActivity.startIntentWithFinish(
                        this,
                        NavKeys.KEY_FM_LOGIN,
                        ""
                    )
                }
            }
        }

        viewModel.getLogoutForceUpdateResult.observe(this) {
            when (it?.status) {
                0 -> {
                    stopSubcribe()
                    viewModel.deleteSessionPin()
                    prefManager.clearPreferences()
                }

                1 -> {
                    Timber.e(it.remarks)
                }

                else -> {
                    stopSubcribe()
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
                            // You can update UI or perform actions every tick if needed
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
                    retryTimer?.cancel() // Cancel the timer if it's running
                    retryTimer == null
                    viewModel.startSubsRecovered()
                }

                "Recovered" -> {
                    viewModel.imqConnectionListener.onListener("")
                    retryTimer?.cancel() // Cancel the timer if it's running
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
                Handler(Looper.getMainLooper()).post {
                    if (!supportFragmentManager.isStateSaved) {
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
        }

        viewModel.imqConnectionListener.isPinExpiredLiveData.observe(this) {
            if (it == true) {
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
                    0 ->  {
                        Log.d("pinexp", "main input pin")
                        showDialogPin()
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

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            bottomNavigation.itemIconTintList = null

            bottomNavigation.setOnItemSelectedListener {
                if (currentNavItem != it.itemId) {
                    when (it.itemId) {
                        R.id.navigation_home -> {
                            activeNavigation = 0
                            getNavHost(fMainContainer).navigate(
                                R.id.home_fragment,
                                null,
                                getNavOptions()
                            )
                            invalidateOptionsMenu()
                        }

                        R.id.navigation_discover -> {
                            activeNavigation = 1
                            getNavHost(fMainContainer).navigate(
                                R.id.discover_fragment,
                                null,
                                getNavOptions()
                            )
                            invalidateOptionsMenu()
                        }

                        R.id.navigation_portfolio -> {
                            activeNavigation = 2
                            getNavHost(fMainContainer).navigate(
                                R.id.portfolio_fragment,
                                null,
                                getNavOptions()
                            )
                            invalidateOptionsMenu()
                        }

                        R.id.navigation_news -> {
                            activeNavigation = 3
                            getNavHost(fMainContainer).navigate(
                                R.id.news_fragment,
                                null,
                                getNavOptions()
                            )
                            invalidateOptionsMenu()
                        }

                        R.id.navigation_profile -> {
                            activeNavigation = 4
                            getNavHost(fMainContainer).navigate(
                                R.id.profile_fragment,
                                null,
                                getNavOptions()
                            )
                            invalidateOptionsMenu()
                        }
                    }
                    currentNavItem = it.itemId
                }
                //animateIcon(bottomNavigation, it)
                true
            }

            getNavHost(fMainContainer).addOnDestinationChangedListener { _, destination, _ ->
                navId = destination.id
                when (destination.id) {
                    R.id.home_fragment -> {
                        updateBottomNavigation(R.id.navigation_home, 0)
                    }
                    R.id.portfolio_fragment -> {
                        updateBottomNavigation(R.id.navigation_portfolio, 2)
                    }

                    R.id.news_fragment -> {
                        updateBottomNavigation(R.id.navigation_news, 3)
                    }

                    R.id.discover_fragment -> {
                        updateBottomNavigation(R.id.navigation_discover, 1)
                    }

                    R.id.profile_fragment -> {
                        updateBottomNavigation(R.id.navigation_profile, 4)
                    }
                    else -> currentNavItem = destination.id
                }
            }
        }
    }

    private fun animateIcon(bottomNavigationView: BottomNavigationView, item: MenuItem) {
        val view = bottomNavigationView.findViewById<View>(item.itemId)
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.1f, 1.0f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.1f, 1.0f)

        val animator = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY)
        animator.duration = 300
        animator.interpolator = OvershootInterpolator()
        animator.start()
    }

    private fun updateBottomNavigation(itemId: Int, activeNav: Int) {
        if (currentNavItem != itemId) {
            binding.bottomNavigation.menu.findItem(itemId)?.isChecked = true
            currentNavItem = itemId
            activeNavigation = activeNav

            // Reset background color if not already set to bgWhite
            if (binding.llMainContainer.background !is ColorDrawable ||
                (binding.llMainContainer.background as ColorDrawable).color != ContextCompat.getColor(
                    this@MainActivity,
                    R.color.bgWhite
                )
            ) {
                binding.llMainContainer.setBackgroundColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.bgWhite
                    )
                )
            }
        }
    }

    private fun getNavOptions(): NavOptions {
        return NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setRestoreState(true)
            .setPopUpTo(getNavHost(binding.fMainContainer).graph.startDestinationId, false)
            .build()
    }

    override fun setupArguments() {
        super.setupArguments()
        setupArgs(intent)
    }

    private fun setupArgs(intent: Intent?) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.f_main_container) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.navigation_main)
        var bundle = Bundle()

        intent?.let {
            paramStrOne = intent.getStringExtra(Args.EXTRA_PARAM_STR_ONE)
            paramStrTwo = intent.getStringExtra(Args.EXTRA_PARAM_STR_TWO)
            paramIntOne = intent.getIntExtra(Args.EXTRA_PARAM_INT_ONE, 0)
            paramIntTwo = intent.getIntExtra(Args.EXTRA_PARAM_INT_TWO, 0)
            paramBoolean = intent.getBooleanExtra(Args.EXTRA_PARAM_BOOLEAN, false)

            when (intent.getStringExtra(NavKeys.KEY_NAV_MAIN)) {
                NavKeys.KEY_FM_TAB_PORTFOLIO -> {
                    paramSnackbarWithdraw =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(
                                Args.EXTRA_PARAM_OBJECT,
                                OrderSuccessSnackBar::class.java
                            )
                        } else {
                            intent.getParcelableExtra(Args.EXTRA_PARAM_OBJECT)
                        }

                    bundle = Bundle().apply {
                        putInt(Args.EXTRA_PARAM_INT_ONE, paramIntOne!!)
                        putBoolean(Args.EXTRA_PARAM_BOOLEAN, paramBoolean)
                        putParcelable(Args.EXTRA_PARAM_OBJECT, paramSnackbarWithdraw)
                    }
                    graph.setStartDestination(R.id.portfolio_fragment)
                }



                NavKeys.KEY_FM_ORDER_DETAIL -> {
                    graph.setStartDestination(R.id.order_detail_fragment)
                    paramOrderDetail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(
                            Args.EXTRA_PARAM_OBJECT,
                            PortfolioOrderItem::class.java
                        )
                    } else {
                        intent.getParcelableExtra(Args.EXTRA_PARAM_OBJECT)
                    }

                    bundle = Bundle().apply {
                        putParcelable(Args.EXTRA_PARAM_OBJECT, paramOrderDetail)
                    }
                }

                NavKeys.KEY_MAIN_DISCOVER -> {
                    graph.setStartDestination(R.id.discover_fragment)
                }

                NavKeys.KEY_MAIN_NEWS -> {
                    bundle = Bundle().apply {
                        putInt(Args.EXTRA_PARAM_INT_ONE, paramIntOne?: 0)
                    }
                    graph.setStartDestination(R.id.news_fragment)
                }

                NavKeys.KEY_MAIN_PROFILE -> {
                    graph.setStartDestination(R.id.profile_fragment)
                }

                NavKeys.KEY_MAIN_STOCK_PICK -> {
                    graph.setStartDestination(R.id.stock_pick_fragment)
                }

                NavKeys.KEY_MAIN_HOME -> {
                    bundle = Bundle().apply {
                        putBoolean(Args.EXTRA_PARAM_BOOLEAN, paramBoolean)
                    }
                    graph.setStartDestination(R.id.home_fragment)
                }

                NavKeys.KEY_FM_CALENDAR -> {
                    graph.setStartDestination(R.id.calendar_fragment)
                }

                NavKeys.KEY_FM_BROKER_SUMMARY -> {
                    graph.setStartDestination(R.id.broker_summary_fragment)
                }

                NavKeys.KEY_FM_RUNNING_TRADE -> {
                    graph.setStartDestination(R.id.running_trade_fragment)
                }

                NavKeys.KEY_FM_GLOBAL_MARKET -> {
                    graph.setStartDestination(R.id.global_market_fragment)
                }

                else -> {
                    bundle = Bundle().apply {
                        putBoolean(Args.EXTRA_PARAM_BOOLEAN, paramBoolean)
                    }
                    graph.setStartDestination(R.id.home_fragment)
                }
            }
        }
        val navController = navHostFragment.navController
        navController.setGraph(graph, bundle)
    }

    override fun setupFMListener() {
        super.setupFMListener()
        supportFragmentManager.setFragmentResultListener(
            NavKeys.KEY_FM_MAIN_ACTIVITY, this
        ) { _, bundle ->
            val result = bundle.getString(NavKeys.KEY_FM_MAIN_ACTIVITY)
            val valueResultStr: String?
            val valueResultInt: Int
//            when (result) {
//                NavKeys.CONST_RES_NOT_PREMIUM -> {
//                    valueResultStr = bundle.getString(NavKeys.CONST_RES_NOT_PREMIUM)
//                    if (!valueResultStr.isNullOrEmpty()) { /* Go to Premium Package */ }
//                }
//            }
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val previousStack = getNavHost(binding.fMainContainer).previousBackStackEntry?.destination?.label
            currentFragmentNavigation = getNavHost(binding.fMainContainer).currentDestination?.id!!
            if (currentFragmentNavigation != R.id.home_fragment) {
                when (currentFragmentNavigation) {
                    R.id.discover_fragment, R.id.profile_fragment,
                    R.id.portfolio_fragment, R.id.news_fragment,
                    -> {
                        activeNavigation = 0
                        binding.bottomNavigation.selectedItemId = R.id.navigation_home
                    }

                    else -> {
                        if (previousStack == "fragment_home" || previousStack == null) {
                            activeNavigation = 0
                            binding.bottomNavigation.selectedItemId = R.id.navigation_home
                        } else {
                            getNavHost(binding.fMainContainer).popBackStack()
                        }
                    }
                }
            }
//            else {
//                finish()
//            }
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
                sharedViewModel.isPinSuccess.value = true
                viewModel.getSessionPin(prefManager.userId)
            } else {
                if (isBlocked) {
                    showDialogAccountDisable(supportFragmentManager)
                } else {
                    sharedViewModel.isPinBackPressedResult.value = true
                }
            }
        })
    }

    private fun fetchRemoteConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(30)  // fetch every hour
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
                val shouldShowDialog = isForceUpdate || !prefManager.isDialogForceUpdateShow

                if (shouldShowDialog) {
                    if (!isForceUpdate) {
                        prefManager.isDialogForceUpdateShow = true
                    }

                    showDialogForcedUpdate(supportFragmentManager, isForceUpdate, { onClick ->
                        if (onClick) {
                            viewModel.getLogoutForceUpdate(prefManager.userId, prefManager.sessionId)

                            MiddleActivity.startIntentWithFinish(
                                this,
                                NavKeys.KEY_FM_LOGIN,
                                ""
                            )
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.bcasekuritas.mybest")))
                        }
                    })
                }
            }
            Timber.tag("SplashActivity").d("Version Name: $versionName, Version Code: $versionCode")
        } catch (e: Exception) {
            Timber.tag("SplashActivity").e("Failed to parse android_config JSON : $e")
        }
    }

}