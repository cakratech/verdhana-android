package com.bcasekuritas.mybest.app.base

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.ext.common.isAutomaticDate
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarInterface
import com.bcasekuritas.mybest.ext.prefs.SharedPreferenceManager
import com.bcasekuritas.mybest.widget.progressdialog.ProgressDialogHelper
import javax.inject.Inject

abstract class BaseActivity<VB : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity(),
    ShowDialog by ShowDialogImpl(), ShowSnackBarInterface by ShowSnackBarImpl() {

    abstract val binding: VB
    abstract val viewModel: VM
    abstract val bindingVariable: Int

    @Inject
    lateinit var prefManager: SharedPreferenceManager

    private val progressDialogHelper = ProgressDialogHelper()

//    private val baseActivityViewModel: BaseActivityViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Timber.i("Activity: Run onCreate")
        setupBinding()
        onInitViews()
        setupArguments()
        setupComponent()
        setupAdapter()
        setupViewPager()

        setupListener()
        setupObserver()

        initAPI()
        initOnClick()

        setupFMListener()
    }

    override fun onResume() {
        super.onResume()
        //Timber.i("Activity: Run onResume")
        isAutomaticDate(this, supportFragmentManager)
    }

    private fun setupBinding() {
        setContentView(binding.root)
        binding.setVariable(bindingVariable, viewModel)
        binding.executePendingBindings()
    }

//    override fun getResources(): Resources {
//        val res = super.getResources()
//        val config = res.configuration
//        if (config.fontScale != 1f) {
//            config.fontScale = 1f  // Prevent scaling
//            res.updateConfiguration(config, res.displayMetrics)
//        }
//        return res
//
//    }

    abstract fun onInitViews()

    protected open fun setupComponent() {}
    protected open fun setupArguments() {}
    protected open fun setupAdapter() {}
    protected open fun setupViewPager() {}

    protected open fun setupListener() {}
    protected open fun setupObserver() {}

    protected open fun initAPI() {}
    protected open fun initOnClick() {}


    protected open fun setupFMListener() {}

    override fun attachBaseContext(newBase: Context?) {
        if (newBase == null) return
        val config = Configuration(newBase.resources.configuration).apply {
            fontScale = 1.0f // cegah font scale berubah
            densityDpi = Resources.getSystem().displayMetrics.densityDpi
        }

        val context = newBase.createConfigurationContext(config)

        val metrics = context.resources.displayMetrics
        val systemMetrics = Resources.getSystem().displayMetrics

        // Patch ulang DisplayMetrics untuk hindari "Display Size" scaling dari Infinix XOS
        metrics.density = systemMetrics.density
        @Suppress("DEPRECATION")
        metrics.scaledDensity = systemMetrics.scaledDensity // masih dibutuhkan agar sp→px benar di Android < 15
        metrics.densityDpi = systemMetrics.densityDpi

        super.attachBaseContext(context)
    }

    fun showLoading() {
        this.progressDialogHelper.show(this, "")
    }

    fun hideLoading() {
        progressDialogHelper.dismiss()
    }

    fun getNavHost(fragmentContainer: FragmentContainerView): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(fragmentContainer.id) as NavHostFragment
        return navHostFragment.navController
    }

    open fun transparentStatusBar() {
        val window = window
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
    }

//    fun startRealtimeData(accNo: String, userId: String) {
//        baseActivityViewModel.startAppNotification()
//        baseActivityViewModel.startOrderReply(accNo)
//        baseActivityViewModel.data(userId, accNo)
//    }
//    fun subscribeAppNotification(sessionId: String){
//        baseActivityViewModel.subscribeAppNotification(sessionId)
//    }
//    fun unsubscribeAppNotification(sessionId: String){
//        baseActivityViewModel.UnSubscribeAppNotification(sessionId)
//    }
//
//    fun stopRealtimeData(sessionId: String, accNo: String){
//        baseActivityViewModel.stopAppNotification(sessionId)
//        baseActivityViewModel.stopOrderReply(accNo)
//    }
//
//    private fun baseObserver() {
//        baseActivityViewModel.getAppNotificationResult.observe(this) {
//            when(it?.category){
//                1 -> {
//                    showSnackBarTop(baseContext, binding.root, "", 0, "Force Logout", "Akun anda sedang dipakai pada device lain" )
//                    SharedPreferenceManager(baseContext).userId
//                }
//                2 -> {
//
//                }
//                3 -> {
//
//                }
//                4 -> {
//                    val timeSecond = System.currentTimeMillis() / 1000
//                    val remain = timeSecond + it.remain / 1000
//                    baseActivityViewModel.insertSession(
//                        SessionObject(
//                            SharedPreferenceManager(baseContext).userId,
//                            SharedPreferenceManager(baseContext).sessionId,
//                            remain
//                        )
//                    )
//
//                }
//            }
//        }
//
//        baseActivityViewModel.getOrderListResult.observe(this) {
//            if (it?.ordersList?.size != 0) {
//                val orderListItemMapper = ArrayList<PortfolioOrderItem>()
//                it?.ordersList?.map { data ->
//                    orderListItemMapper.add(
//                        PortfolioOrderItem(
//                            data.odId,
//                            data.odTime,
//                            data.ostatus,
//                            data.bs,
//                            data.ordertype,
//                            data.stockcode,
//                            data.remark,
//                            data.oprice,
//                            data.oqty,
//                            data.mqty
//                        )
//                    )
//                }
//                orderListItemMapper.sortBy { item -> item.time }
//            }
//        }
//
//        baseActivityViewModel.getOrderNotifResult.observe(this) {
//            it?.let {
//                showSnackBarOrder(baseContext, binding.root, it.buySell, it.stockCode, it.status, it.lotSize.toString(), it.ordPrice)
//            }
//        }
//    }

}