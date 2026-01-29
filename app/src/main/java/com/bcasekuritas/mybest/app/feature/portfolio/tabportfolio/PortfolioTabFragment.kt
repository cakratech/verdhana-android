package com.bcasekuritas.mybest.app.feature.portfolio.tabportfolio

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.PortfolioStockDataItem
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.portfolio.PortfolioShareViewModel
import com.bcasekuritas.mybest.app.feature.portfolio.tabportfolio.adapter.PortfolioBondsAdapter
import com.bcasekuritas.mybest.app.feature.portfolio.tabportfolio.adapter.PortfolioTabAdapter
import com.bcasekuritas.mybest.databinding.FragmentTabPortfolioBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.RabbitMQForegroundService
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.mybest.ext.listener.OnClickStrInt
import com.bcasekuritas.mybest.ext.other.formatPercent
import com.bcasekuritas.mybest.ext.other.formatPercentThousand
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleAccBondPos
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

@FragmentScoped
@AndroidEntryPoint
class PortfolioTabFragment: BaseFragment<FragmentTabPortfolioBinding, PortfolioTabViewModel>(),
    ShowDialog by ShowDialogImpl(), OnClickStrInt, OnClickAny {

    override val bindingVariable: Int = BR.vmTabPortfolio
    override val viewModel: PortfolioTabViewModel by viewModels()
    override val binding: FragmentTabPortfolioBinding by autoCleaned {
        (FragmentTabPortfolioBinding.inflate(layoutInflater))
    }

    private var userId = ""
    private var sessionId = ""
    private var accNo = ""

    lateinit var sharedViewModel: PortfolioShareViewModel

    private val mAdapter: PortfolioTabAdapter by autoCleaned { PortfolioTabAdapter(prefManager.urlIcon, this, this) }
    private val mBondsAdapter: PortfolioBondsAdapter by autoCleaned { PortfolioBondsAdapter() }
    private var sort = 4
    private val listPortfolio = arrayListOf<PortfolioStockDataItem>()
    private val listBonds = arrayListOf<SimpleAccBondPos>()
    private var layoutState = 0

    private var subscribeJob: Job? = null
    private var hasSubscribeAccPos = false

    companion object {
        fun newInstance() = PortfolioTabFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel =
            ViewModelProvider(requireActivity()).get(PortfolioShareViewModel::class.java)
    }

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvPortfolio.apply {
          adapter = mAdapter
          layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        binding.rcvBonds.apply {
            adapter = mBondsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.swipeRefresh.setOnRefreshListener {
//            viewModel.getStockPos(userId, accNo, sessionId)
            viewModel.getSimplePortfolio(userId, sessionId, accNo)
            sort = 4
        }

    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            btnSort.setOnClickListener {
                showDialogSortPortfolioBottom(sort, childFragmentManager)
            }

            lyTotalMarketValue.setOnClickListener {
                showDialogPortfolioSummaryInfoBottom(
                    "Total Market Value",
                    "Nilai portofolio nasabah berdasarkan jumlah lot yang dimiliki yang dikalikan dengan harga terakhir.",
                    "Total value of your portfolio based on the number of lots owned and the last price.",
                    parentFragmentManager)

            }

            lyUnrealizedGainLoss.setOnClickListener {
                showDialogPortfolioSummaryInfoBottom(
                    "Unrealized Gain/Loss",
                    "Keuntungan atau kerugian yang belum direalisasikan dari saham dalam portofolio, dihitung berdasarkan selisih antara harga beli rata-rata dan harga terakhir dikalikan dengan jumlah lot yang dimiliki.",
                    "Estimation of unrealized gains or losses of stocks in the portfolio, excluding fees, based on the last price.",
                    parentFragmentManager)

            }

            tvAvailableCash.setOnClickListener {
                showDialogPortfolioSummaryInfoBottom(
                    "Available Cash",
                    "Dana tersedia yang dapat digunakan untuk melakukan aktivitas pembelian saham.",
                    "The amount of cash available for stock purchases",
                    parentFragmentManager)
            }

            lyAvailableCash.setOnClickListener {
//                MiddleActivity.startIntent(requireActivity(), NavKeys.KEY_FM_PORTFOLIO_CASH)
                findNavController().navigate(R.id.portfolio_cash_fragment)
            }

            lyBuyingLimit.setOnClickListener {
                showDialogPortfolioSummaryInfoBottom(
                    "Buying Limit",
                    "Buying limit adalah fasilitas untuk membeli saham dengan nilai melebihi dana yang tersedia, menggunakan cash dan valuasi portofolio sebagai kolateral, sesuai dengan perhitungan margin level.",
                    "Buying limit is a facility to buy stocks exceeding your available cash, using your cash and portfolio valuation as collateral based on the margin level.",
                    parentFragmentManager)

            }

            btnPortfolioReturn.setOnClickListener{
                findNavController().navigate(R.id.portfolio_return_fragment)
            }
            tvDiscoverStocks.setOnClickListener {
                findNavController().navigate(R.id.discover_fragment)
            }

            chipStocks.setOnClickListener {
                layoutPosition(0)
            }
            chipBonds.setOnClickListener {
                layoutPosition(1)
            }



        }
    }

    override fun setupListener() {
        super.setupListener()
        // for filter
        childFragmentManager.setFragmentResultListener(NavKeys.KEY_FM_TAB_PORTFOLIO, viewLifecycleOwner) {_, result ->
            val confirmResult = result.getString(NavKeys.CONST_RES_TAB_PORTFOLIO_SORT)

            if (confirmResult == "RESULT_OK") {
                val sortResult = result.getInt("sortKey", 4)
                sort = sortResult
                if (listPortfolio.size != 0) {
                    val sortedItem = when (sort) {
                        0 -> listPortfolio.sortedBy { item -> item.stockcode }
                        1 -> listPortfolio.sortedByDescending { item -> item.stockcode }
                        2 -> listPortfolio.sortedByDescending {item-> item.pct }
                        3 -> listPortfolio.sortedBy {item-> item.pct }
                        4 -> listPortfolio.sortedByDescending {item-> item.value }
                        5 -> listPortfolio.sortedBy {item-> item.value }
                        else -> listPortfolio.sortedByDescending {item-> item.value }
                    }
                    mAdapter.setData(sortedItem)
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.startRealtimeData(userId, sessionId, accNo)

        hasSubscribeAccPos = false
        subscribeJob = viewLifecycleOwner.lifecycleScope.launch {
            delay(3000)

            if (!isActive) return@launch

            // publish acc pos (subsOp = 0)
            hasSubscribeAccPos = true
            viewModel.publishAccPos(userId, sessionId, 0, accNo)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopRealtimeData(userId, sessionId, accNo)
        subscribeJob?.cancel()
        if (hasSubscribeAccPos) {
            // unpublish acc pos (subsOp = 1)
            viewModel.publishAccPos(userId, sessionId, 1, accNo)
        }
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        accNo = prefManager.accno
        sessionId = prefManager.sessionId
        sort = 4

//        viewModel.getStockPos(userId, accNo, sessionId)
        viewModel.getSimplePortfolio(userId, sessionId, accNo)
    }

    @SuppressLint("SetTextI18n")
    override fun setupObserver() {
        super.setupObserver()
        viewModel.imqConnectionListener.connListenerLiveData.observe(this) { value ->
            when (value) {
                "Recovered" -> {
                    viewModel.imqConnectionListener.onListener("")
                    viewModel.getSimplePortfolio(userId, sessionId, prefManager.accno)
                }

                else -> {}
            }
        }

        viewModel.getSummaryResult.observe(viewLifecycleOwner){data ->
            if (data.isNotEmpty()) {
                val result = data[0]
                val availableCash = result.potCashBalance
                val buyingLimit = result.eqBuyingPower
                val unRealized = 11223344
                val percent = 11223344.0 * 100

                binding.tvTotalMarketVal.text = result.portfolio.formatPriceWithoutDecimal()
                binding.tvAvailableCashVal.text = availableCash.formatPriceWithoutDecimal()
                binding.tvBuyingLimitVal.text = buyingLimit.formatPriceWithoutDecimal()

                val formattedUnRealized = unRealized.formatPriceWithoutDecimal()
                if (unRealized > 0) {
                    binding.tvUnrealizedVal.setTextColor(ContextCompat.getColor(requireContext(), R.color.textUp))
                    binding.tvUnrealizedVal.text = "+$formattedUnRealized (+${percent.formatPercentThousand()}%)"
                } else if (unRealized < 0) {
                    binding.tvUnrealizedVal.setTextColor(ContextCompat.getColor(requireContext(), R.color.textDown))
                    binding.tvUnrealizedVal.text = "$formattedUnRealized (${percent.formatPercentThousand()}%)"
                } else {
                    binding.tvUnrealizedVal.setTextColor(ContextCompat.getColor(requireContext(), R.color.noChanges))
                    binding.tvUnrealizedVal.text = "0 (0.00%)"
                }
            }


        }

        viewModel.getSimplePortfolioRealtimeResult.observe(viewLifecycleOwner) {res ->
            Log.d("accPosRealtime", "get realtime data summary porto")
            if (res != null) {
                val availableCash = res.potCashBalance
                val buyingLimit = res.eqBuyingPower
                val unRealized = res.unrealizedGainLoss
                val percent = res.unrealizedGainLossPct * 100

                binding.tvTotalMarketVal.text = res.portfolio.formatPriceWithoutDecimal()
                binding.tvAvailableCashVal.text = availableCash.formatPriceWithoutDecimal()
                binding.tvBuyingLimitVal.text = buyingLimit.formatPriceWithoutDecimal()

                val formattedUnRealized = unRealized.formatPriceWithoutDecimal()
                if (unRealized > 0) {
                    binding.tvUnrealizedVal.setTextColor(ContextCompat.getColor(requireContext(), R.color.textUp))
                    binding.tvUnrealizedVal.text = "+$formattedUnRealized (+${percent.formatPercentThousand()}%)"
                } else if (unRealized < 0) {
                    binding.tvUnrealizedVal.setTextColor(ContextCompat.getColor(requireContext(), R.color.textDown))
                    binding.tvUnrealizedVal.text = "$formattedUnRealized (${percent.formatPercentThousand()}%)"
                } else {
                    binding.tvUnrealizedVal.setTextColor(ContextCompat.getColor(requireContext(), R.color.noChanges))
                    binding.tvUnrealizedVal.text = "0 (0.00%)"
                }

                sharedViewModel.setPortfolioReturnPct(percent)
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
                                val availableCash = res.potCashBalance
                                val buyingLimit = res.eqBuyingPower
                                val unRealized = res.unrealizedGainLoss
                                val percent = res.unrealizedGainLossPct * 100

                                binding.tvTotalMarketVal.text = res.portfolio.formatPriceWithoutDecimal()
                                binding.tvAvailableCashVal.text = availableCash.formatPriceWithoutDecimal()
                                binding.tvBuyingLimitVal.text = buyingLimit.formatPriceWithoutDecimal()

                                val formattedUnRealized = unRealized.formatPriceWithoutDecimal()
                                if (unRealized > 0) {
                                    binding.tvUnrealizedVal.setTextColor(ContextCompat.getColor(requireContext(), R.color.textUp))
                                    binding.tvUnrealizedVal.text = "+$formattedUnRealized (+${percent.formatPercentThousand()}%)"
                                } else if (unRealized < 0) {
                                    binding.tvUnrealizedVal.setTextColor(ContextCompat.getColor(requireContext(), R.color.textDown))
                                    binding.tvUnrealizedVal.text = "$formattedUnRealized (${percent.formatPercentThousand()}%)"
                                } else {
                                    binding.tvUnrealizedVal.setTextColor(ContextCompat.getColor(requireContext(), R.color.noChanges))
                                    binding.tvUnrealizedVal.text = "0 (0.00%)"
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

        viewModel.getListBondsResult.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                listBonds.clear()
                listBonds.addAll(it)

                binding.tvTotalBonds.text = "My Bonds (${it.size})"
                binding.chipBonds.text = "Bonds (${it.size})"

                showData(true)
                mBondsAdapter.setData(it)
                if (layoutState == 1) {
                    layoutPosition(1)
                }
            }
        }

        viewModel.getListPortfolio.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                listPortfolio.clear()
                listPortfolio.addAll(it)

                binding.tvTotalStock.text = "My Stocks (${it.size})"
                binding.chipStocks.text = "Stocks (${it.size})"

                val sortData = it.sortedByDescending { item -> item.value }

                mAdapter.setData(sortData)
                showData(true)
            } else {
                if (layoutState == 0) {
                    showData(false)
                }
            }
            binding.swipeRefresh.isRefreshing = false
        }

        viewModel.getLayoutState.observe(viewLifecycleOwner) {state ->
            binding.apply {
                layoutState = state
                when (state) {
                    0 -> {
                        chipGroup.visibility = View.GONE
                        layoutPosition(0)
                    }
                    1 -> {
                        chipGroup.visibility = View.GONE
                        layoutPosition(1)
                    }
                    2 -> {
                        chipGroup.visibility = View.VISIBLE
                        tvTotalStock.visibility = View.GONE
                        tvTotalBonds.visibility = View.GONE
                    }
                }
            }

        }

        viewModel.isEmptyPortfolio.observe(viewLifecycleOwner){
            if (it) {
                showData(false)
            }
            binding.swipeRefresh.isRefreshing = false
        }

        viewModel.getLogoutResult.observe(viewLifecycleOwner) {
            when (it?.status){
                0 -> {
                    RabbitMQForegroundService.stopService(requireContext())
                    viewModel.deleteSession()
                    prefManager.clearPreferences()
                    MiddleActivity.startIntentWithFinish(requireActivity(), NavKeys.KEY_FM_LOGIN, true)
                }
                else -> {
                    Timber.e("${it?.status} : ${it?.remarks}" )
                }
            }
        }

    }

    override fun onClickStrInt(valueStr: String?, valueInt: Int?) {
        if (valueStr != null && valueInt != null) {
            MiddleActivity.startIntentParam(requireActivity(), NavKeys.KEY_FM_ORDER, valueStr, valueInt)
        }
    }

    override fun onClickAny(valueAny: Any?) {
        MiddleActivity.startIntentParam(requireActivity(), NavKeys.KEY_FM_PORTFOLIO_DETAIL, valueAny!!, "")
    }

    private fun showData(state: Boolean) {
        binding.lyContent.visibility = if (state) View.VISIBLE else View.GONE
        binding.lyNoData.visibility = if (!state) View.VISIBLE else View.GONE
    }

    private fun layoutPosition(state: Int) {
        binding.apply {
            when (state) {
                // 0: stocks, 1: Bonds
                0 -> {
                    rcvPortfolio.visibility = View.VISIBLE
                    btnSort.visibility = View.VISIBLE

                    rcvBonds.visibility = View.GONE
                    tvBondsInfo.visibility = View.GONE

                    if (layoutState == 0) {
                        tvTotalStock.visibility = View.VISIBLE
                        tvTotalBonds.visibility = View.GONE
                    }
                }
                1 -> {
                    rcvBonds.visibility = View.VISIBLE
                    tvBondsInfo.visibility = View.VISIBLE

                    rcvPortfolio.visibility = View.GONE
                    btnSort.visibility = View.GONE

                    if (layoutState == 1) {
                        tvTotalBonds.visibility = View.VISIBLE
                        tvTotalStock.visibility = View.GONE
                    }
                }
            }
        }

    }

    private fun showDialogPin() {
        showDialogPin(parentFragmentManager, onSuccess = {isSuccess, isBlocked ->
            if (isSuccess) {
                if (isAdded) {
                    viewModel.getSimplePortfolio(userId, sessionId, accNo)
                    sort = 4
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

}