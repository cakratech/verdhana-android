package com.bcasekuritas.mybest.app.feature.portfolio

import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.OrderSuccessSnackBar
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.activity.main.MainActivity
import com.bcasekuritas.mybest.app.feature.activity.main.SharedMainViewModel
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.portfolio.adapter.PortfolioPagerAdapter
import com.bcasekuritas.mybest.databinding.FragmentPortfolioBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.RabbitMQForegroundService
import com.bcasekuritas.mybest.ext.common.validateSessionPin
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarInterface
import com.bcasekuritas.mybest.ext.other.formatPercentThousand
import timber.log.Timber

@FragmentScoped
@AndroidEntryPoint
class PortfolioFragment : BaseFragment<FragmentPortfolioBinding, PortfolioViewModel>(), ShowSnackBarInterface by ShowSnackBarImpl() {

    override val bindingVariable: Int = BR.vmPortfolio
    override val viewModel: PortfolioViewModel by viewModels()
    override val binding: FragmentPortfolioBinding by autoCleaned {
        (FragmentPortfolioBinding.inflate(layoutInflater))
    }
    private val sharedViewModel: PortfolioShareViewModel by lazy {
        ViewModelProvider(requireActivity()).get(PortfolioShareViewModel::class.java)
    }
    private lateinit var sharedMainViewModel: SharedMainViewModel

    private var userId = ""

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private var layoutPosition = 0
    private var isOrderSuccess = false
    private var portfolioReturn = ""

    private var sessionPin: Long? = null

    var orderSuccessSnackBar: OrderSuccessSnackBar? = OrderSuccessSnackBar()

    companion object {
        fun newInstance() = PortfolioFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedMainViewModel = ViewModelProvider(requireActivity()).get(SharedMainViewModel::class.java)
    }

    override fun setupArguments() {
        super.setupArguments()
        arguments.let {
            if (it != null) {
                layoutPosition = it.getInt(Args.EXTRA_PARAM_INT_ONE)
                isOrderSuccess = it.getBoolean(Args.EXTRA_PARAM_BOOLEAN)
                orderSuccessSnackBar = if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it.getParcelable(Args.EXTRA_PARAM_OBJECT, OrderSuccessSnackBar::class.java)
                } else {
                    it.getParcelable(Args.EXTRA_PARAM_OBJECT)
                }

                if (orderSuccessSnackBar?.isSuccess == true && !orderSuccessSnackBar?.orderType.equals("order")) {
                    val orderType: String = when(orderSuccessSnackBar?.orderType){
                        "withdraw" -> "Withdraw"
                        "amend" -> "Amend"
                        else -> {""}
                    }
                    showSnackBarTop(requireContext(), binding.root, "success", R.drawable.ic_success, "$orderType ${orderSuccessSnackBar?.buySell} ${orderSuccessSnackBar?.stockCode} is submitted", "", requireActivity(), "")
                }
            }
        }
    }

    override fun setupViewPager() {
        super.setupViewPager()
        viewPager = binding.viewPagerPortfolio
        tabLayout = binding.tabLayoutPortfolio

        val adapter = PortfolioPagerAdapter(this)
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position+1) {
                1 -> "Portfolio"
                2 -> "Orders"
                3 -> "History"
                else -> {""}
            }
        }.attach()

        if (layoutPosition != 0) {
            viewPager.setCurrentItem(layoutPosition, false)
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.ivSharePortfolio.isGone = position != 0
            }
        })
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        viewModel.getSessionPin(userId)
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.ivSharePortfolio.setOnClickListener {
            if (portfolioReturn.contains("-")){
                showDialogSharePortfolio(portfolioReturn, false, parentFragmentManager)
            } else {
                showDialogSharePortfolio(portfolioReturn, true, parentFragmentManager)
            }
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        sharedViewModel.isWithdrawSuccess.observe(viewLifecycleOwner){

            if (it.isSuccess == true) {
                showSnackBarTop(requireContext(), binding.root, "success", R.drawable.ic_success, "Withdraw ${it.buySell} ${it.stockCode} is submitted", "", requireActivity(), "")
            }
        }


        viewModel.getSimplePortfolioResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> { showLoading() }
                is Resource.Success -> {
                    Log.d("pinexp", "simple porto ${it.data?.status}")
                    when (it.data?.status) {
                        0 -> {
                            it.data.simplePortofolio.let { res ->
                                val unRealized = res.unrealizedGainLoss
                                val percent = res.unrealizedGainLossPct * 100

                                if (unRealized > 0) {
                                    portfolioReturn =  "+${percent.formatPercentThousand()}%"
                                } else if (unRealized < 0) {
                                    portfolioReturn =  "${percent.formatPercentThousand()}%"
                                } else {
                                    portfolioReturn = "0.00%"
                                }
                            }
                        }
                        2 -> sharedViewModel.setSessionExpired()
                        3 -> {
                            Log.d("pinexp", "simple porto input pin")
                            showDialogPin()
                        }
                    }

                    hideLoading()

                }
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), it.failureData.message, Toast.LENGTH_SHORT)
                        .show()
                } else -> {}
            }
        }

        // realtime for share portfolio
        sharedViewModel.portfolioReturn.observe(viewLifecycleOwner) { value ->
            Log.d("shareport", "get shareport")
            if (value != 0.0 && value != null) {
                if (value > 0) {
                    portfolioReturn =  "+${value.formatPercentThousand()}%"
                } else if (value < 0) {
                    portfolioReturn =  "${value.formatPercentThousand()}%"
                } else {
                    portfolioReturn = "0.00%"
                }
            }
        }

        viewModel.getAccNameDaoResult.observe(viewLifecycleOwner) {
            it?.let {
                sharedViewModel.setClientInfo(it)
            }
        }

        sharedViewModel.clientInfo.observe(viewLifecycleOwner) {
            if (!it.equals("") && it != null) {
                val accNo = prefManager.accno
                binding.tvClientName.text = "$accNo - $it"
            }
        }

        viewModel.getSessionPinResult.observe(viewLifecycleOwner) {
            sessionPin = it
            if (sessionPin != null) {
                if (validateSessionPin(sessionPin!!)) {
//                    if (!prefManager.isCoachmarkPortfolioShow) {
//                        prefManager.isCoachmarkPortfolioShow = true
//                        showDialogCoachmarkPortfolio(parentFragmentManager)
//                    }
                    binding.groupContent.visibility = View.VISIBLE
                    viewModel.getAccNameDao(prefManager.accno)
                    viewModel.getSimplePortfolio(userId, prefManager.sessionId, prefManager.accno)
                } else {
                    showDialogPin()
                }
            }
        }
        sharedViewModel.showSessionExpired.observe(viewLifecycleOwner) {
            if (it) {
                (activity as MainActivity).showDialogSessionExpired()
            }
        }

        viewModel.getLogoutResult.observe(viewLifecycleOwner) {
            when (it?.status){
                0 -> {
                    RabbitMQForegroundService.stopService(requireContext())
                    sharedMainViewModel.setStopSubs(true)
                    viewModel.deleteSession()
                    prefManager.clearPreferences()
                    MiddleActivity.startIntentWithFinish(requireActivity(), NavKeys.KEY_FM_LOGIN, true)
                    Timber.e(it?.remarks)
                }
                else -> {
                    Timber.e("${it?.status} : ${it?.remarks}" )
                }
            }
        }

        sharedMainViewModel.isPinBackPressedResult.observe(viewLifecycleOwner) { isPinBackPressed ->
            if (isPinBackPressed) {
                onBackPressed()
            }
        }

        sharedMainViewModel.isPinSuccess.observe(viewLifecycleOwner) {isPinSuccess ->
            if (isPinSuccess) {
                sharedViewModel.isPinSuccess.value = true
            }
        }
    }

    private fun showDialogPin() {
        showDialogPin(parentFragmentManager, onSuccess = {isSuccess, isBlocked ->
            if (isSuccess) {
                if (isAdded) {
//                    if (!prefManager.isCoachmarkPortfolioShow) {
//                        prefManager.isCoachmarkPortfolioShow = true
//                        showDialogCoachmarkPortfolio(parentFragmentManager)
//                    }
                    binding.groupContent.visibility = View.VISIBLE
                    viewModel.getAccNameDao(prefManager.accno)
                    viewModel.getSimplePortfolio(userId, prefManager.sessionId, prefManager.accno)
                }
            } else {
                if (isBlocked) {
                    viewModel.getLogout(userId, prefManager.sessionId)
                } else {
                    if (isAdded) {
                        onBackPressed()
                    }
                }

            }
        })
    }

    override fun onPause() {
        super.onPause()

        sharedViewModel.clearWithdrawSuccess()
        arguments?.clear()
        viewModel.clearSessionPin()
    }

}