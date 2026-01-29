package com.bcasekuritas.mybest.app.feature.stockdetail

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.DecimalFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.DataChart
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.activity.landscape.LandscapeActivity
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.stockdetail.adapter.StockDetailViewPagerAdapter
import com.bcasekuritas.mybest.databinding.FragmentStockDetailBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.RabbitMQForegroundService
import com.bcasekuritas.mybest.ext.common.adjustFractionPrice
import com.bcasekuritas.mybest.ext.common.formatLastNumberStartFromMillion
import com.bcasekuritas.mybest.ext.common.getCurrentTimeInMillis
import com.bcasekuritas.mybest.ext.common.initFormatThousandSeparator
import com.bcasekuritas.mybest.ext.common.initPercentFormatNumber
import com.bcasekuritas.mybest.ext.common.validateSessionPin
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.converter.GET_4_CHAR_STOCK_CODE
import com.bcasekuritas.mybest.ext.converter.GET_IDX_BOARD
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarInterface
import com.bcasekuritas.mybest.ext.other.formatPercent
import com.bcasekuritas.mybest.ext.other.formatPriceWithDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.other.roundedHalfUp
import com.bcasekuritas.mybest.ext.view.setSafeOnClickListener
import com.bcasekuritas.mybest.widget.view.CustomMarkerView
import com.bumptech.glide.Glide
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.ceil


@FragmentScoped
@AndroidEntryPoint
class StockDetailFragment : BaseFragment<FragmentStockDetailBinding, StockDetailViewModel>(), ShowDropDown by ShowDropDownImpl(), ShowSnackBarInterface by ShowSnackBarImpl() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmStockDetail
    override val viewModel: StockDetailViewModel by viewModels()
    override val binding: FragmentStockDetailBinding by autoCleaned {
        FragmentStockDetailBinding.inflate(
            layoutInflater
        )
    }

    lateinit var sharedViewModel: StockDetailSharedViewModel

    val sotckDetailTab = arrayOf(
        "Order Book",
        "Key Stats",
        "Trade",
        "Financial",
        "Analysis",
        "Broker Summary",
        "Daily",
        "Running Trade",
        "News & Reports",
        "Corporate Action",
        "About"
    )
    private var userId = ""
    private var sessionPin: Long? = null
    private var sessionId = ""
    private var accNo = ""

    private var selectChipId = 0
    private var stockCode = ""
    private var isStockOwned = false
    private var isBalanceHidden = false
    private var stockCodeList = arrayListOf<String>()
    private var isStockCodeChange = false
    private var endDate = 0L
    private var openPrice = 0.0
    private var isSession1 = false
    private var isChart1d = true
    private var isOpenPortfolio = false
    private var stockName = ""
    private var infoChange = ""
    private var stockPrice = ""

    private var isNotation = false
    private var isSpecialStock = false

    private var isScroll = false

    private lateinit var viewPager: ViewPager2

    private var pageChangeCallback: ViewPager2.OnPageChangeCallback? = null

    // for datamarker chart
    private var isHoursFormat = true

    // for timer subscribe acc pos
    private var subscribeJob: Job? = null
    private var hasSubscribeAccPos = false

    companion object {
        fun newInstance() = StockDetailFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel =
            ViewModelProvider(requireActivity()).get(StockDetailSharedViewModel::class.java)

        arguments?.let {
            stockCode = it.getString(Args.EXTRA_PARAM_STR_ONE).toString()
            prefManager.stockDetailCode = stockCode
        }
    }

    override fun setupComponent() {
        super.setupComponent()

        endDate = System.currentTimeMillis()
        binding.lyToolbarStockDetail.tvLayoutToolbarMasterTitle.visibility = View.GONE
        binding.lyToolbarStockDetail.ivLayoutToolbarMasterIconLeft.visibility = View.VISIBLE
        binding.lyToolbarStockDetail.ivLayoutToolbarMasterIconRightOne.visibility = View.VISIBLE
        binding.lyToolbarStockDetail.ivLayoutToolbarMasterIconRightTwo.visibility = View.VISIBLE
        binding.lyToolbarStockDetail.ivLayoutToolbarMasterIconRightThree.visibility = View.VISIBLE

        binding.lyToolbarStockDetail.ivLayoutToolbarMasterIconRightOne.setImageResource(R.drawable.ic_share_outline)
        binding.lyToolbarStockDetail.ivLayoutToolbarMasterIconRightTwo.setImageResource(R.drawable.ic_star_border)
        binding.lyToolbarStockDetail.ivLayoutToolbarMasterIconRightThree.setImageResource(R.drawable.ic_time)

        binding.tvStockDetailInfoCode.text = stockCode

        binding.scrollStockDetail.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            isScroll = scrollY > 115
            if (scrollY > 115) {
                binding.lyToolbarStockDetail.tvLayoutToolbarMasterTextLeft.visibility = View.VISIBLE
                binding.lyToolbarStockDetail.tvLayoutToolbarMasterTextLeft.text = stockCode
                binding.lyToolbarStockDetail.tvLayoutToolbarMasterTextLeft.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.textSecondaryBluey)
                )
            } else {
                binding.lyToolbarStockDetail.tvLayoutToolbarMasterTextLeft.visibility = View.GONE
            }
        }

        binding.swplStockDetail.setOnRefreshListener {
            sharedViewModel.setOnRefresh(true)
        }

        checkStockOwn()
        viewModel.setStockCode(stockCode)
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.lyToolbarStockDetail.ivLayoutToolbarMasterIconLeft.setOnClickListener {
            onBackPressed()
        }

        binding.lyToolbarStockDetail.ivLayoutToolbarMasterIconRightOne.setOnClickListener {
            if (infoChange.contains("-")){
                showDialogShareStock(false, stockCode, stockName, stockPrice, infoChange, parentFragmentManager)
            } else {
                showDialogShareStock(true, stockCode, stockName, stockPrice, infoChange, parentFragmentManager)
            }
        }

        binding.lyToolbarStockDetail.ivLayoutToolbarMasterIconRightTwo.setOnClickListener {
            val bundle = Bundle().apply {
                putStringArrayList(Args.EXTRA_PARAM_OBJECT, arrayListOf(stockCode))
                putString(Args.EXTRA_PARAM_STR_ONE, "stock_detail")
            }

            findNavController().navigate(R.id.select_watchlist_fragment, bundle)
        }

        binding.lyToolbarStockDetail.ivLayoutToolbarMasterIconRightThree.setOnClickListener {
            viewModel.isPriceAlertEmpty(userId, sessionId, stockCode)
        }

        binding.ivStockDetailInfoDown.setOnClickListener {
            showDropDownStringSearchable(
                requireContext(),
                stockCodeList.sorted(),
                binding.viewStockDetailLine,
                "Search stock code"
            ) { index, value ->
                sharedViewModel.unSubscribeOrderbook(stockCode)
                sharedViewModel.unSubscribeTradeSummary(stockCode)
                stockCode = value
                prefManager.stockDetailCode = stockCode
                viewModel.setStockCode(stockCode)
                isStockCodeChange = true
                filterChartOnClick(1)
                initAPI()
            }
        }

        binding.ivStockDetailInfoDownTwo.setOnClickListener {
            if (binding.expandStockDetail.isExpanded){
                binding.ivStockDetailInfoDownTwo.setImageResource(R.drawable.ic_chevron_down)
                binding.expandStockDetail.collapse()
            }else{
                binding.ivStockDetailInfoDownTwo.setImageResource(R.drawable.ic_chevron_up)
                binding.expandStockDetail.expand()
            }
        }

        binding.tvStockDetailInfoSpecialNotes.setOnClickListener {
            findNavController().navigate(R.id.special_notes_fragment)
        }

        binding.tvStockDetailInfoSpecialNotesAcceleration.setOnClickListener {
            val acceleration = binding.tvStockDetailInfoSpecialNotesAcceleration.text.toString()
            val bundle = Bundle().apply {
                putString(Args.EXTRA_PARAM_STR_ONE, acceleration)

            }
            findNavController().navigate(R.id.special_notes_fragment, bundle)
        }

        binding.buttonFast.setOnClickListener {
            val bundleBuy = Bundle().apply {
                putString(Args.EXTRA_PARAM_STR_ONE, stockCode)
                putString(Args.EXTRA_PARAM_STR_TWO, binding.tvStockDetailInfoName.text.toString())
            }
            findNavController().navigate(R.id.fast_order_fragment, bundleBuy)
        }

        binding.buttonBuy.setOnClickListener {
            val bundleBuy = Bundle().apply {
                putInt(Args.EXTRA_PARAM_INT_ONE, 0)  // tab buy = 0
                putString(Args.EXTRA_PARAM_STR_ONE, stockCode)
            }
            findNavController().navigate(R.id.order_fragment, bundleBuy)
        }

        binding.buttonSell.setOnClickListener {
            val bundleSell = Bundle().apply {
                putInt(Args.EXTRA_PARAM_INT_ONE, 1) // tab sell = 1
                putString(Args.EXTRA_PARAM_STR_ONE, stockCode)
            }
            findNavController().navigate(R.id.order_fragment, bundleSell)
        }

        binding.ivEyeStockOwned.setOnClickListener {
            isOpenPortfolio = true
            checkPin()
        }

        binding.tvStockDetailFilter1d.setOnClickListener {
            filterChartOnClick(1)
            viewModel.getChartIntraday(userId, sessionId, stockCode, getStartDate("1D"), endDate, 0)
        }

        binding.tvStockDetailFilter1w.setOnClickListener {
            filterChartOnClick(2)
            val startDay = Calendar.getInstance()
            startDay.add(Calendar.DAY_OF_YEAR, -7)
            startDay.set(Calendar.HOUR_OF_DAY, 0) // Set to midnight
            startDay.set(Calendar.MINUTE, 0)
            startDay.set(Calendar.SECOND, 0)
            startDay.set(Calendar.MILLISECOND, 0)

            val endDay = Calendar.getInstance()
            endDay.set(Calendar.HOUR_OF_DAY, 23) // Set to midnight
            endDay.set(Calendar.MINUTE, 59)
            endDay.set(Calendar.SECOND, 0)
            endDay.set(Calendar.MILLISECOND, 0)
            viewModel.getChartIntraday(userId, sessionId, stockCode, startDay.timeInMillis, endDay.timeInMillis, 1)
        }

        binding.tvStockDetailFilter1m.setOnClickListener {
            filterChartOnClick(3)
            viewModel.getChartIntraday(userId, sessionId, stockCode, getStartDate("1M"), endDate, 2)
        }

        binding.tvStockDetailFilter3m.setOnClickListener {
            filterChartOnClick(4)
            viewModel.getChartIntraday(userId, sessionId, stockCode, getStartDate("3M"), endDate, 2)
        }

        binding.tvStockDetailFilter1y.setOnClickListener {
            filterChartOnClick(5)
            viewModel.getChartIntraday(userId, sessionId, stockCode, getStartDate("1Y"), endDate, 2)
        }

        binding.tvStockDetailFilter5y.setOnClickListener {
            filterChartOnClick(6)
            viewModel.getChartIntraday(userId, sessionId, stockCode, getStartDate("5Y"), endDate, 2)
        }

        binding.tvStockDetailFilterAll.setOnClickListener {
            filterChartOnClick(7)
            viewModel.getChartIntraday(userId, sessionId, stockCode, getStartDate("10Y"), endDate, 2)
        }

        binding.ivFsdFilterExpand.setSafeOnClickListener {
//            MiddleActivity.startIntentParam(requireActivity(), NavKeys.KEY_FM_TRADING_VIEW, stockCode, "")
            LandscapeActivity.startIntentParam(requireActivity(), NavKeys.KEY_FM_TRADING_VIEW, stockCode, "")
        }
    }


    override fun setupViewPager() {
        super.setupViewPager()
        viewPager = binding.viewPagerStockDetail
        val tabLayout = binding.tabLayoutStockDetail
        val adapter = StockDetailViewPagerAdapter(this, stockCode)
        viewPager.isUserInputEnabled = false
//        viewPager.isNestedScrollingEnabled = false
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager, true, false) { tab, position ->
            tab.text = sotckDetailTab[position]
        }.attach()

        pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // This is called when a new page becomes selected.
                binding.lyOrderBookSum.visibility = if (position != 0) View.GONE else View.VISIBLE
            }
        }
        viewPager.registerOnPageChangeCallback(pageChangeCallback as ViewPager2.OnPageChangeCallback)
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        sessionId = prefManager.sessionId
        accNo = prefManager.accno

        checkPin()
        viewModel.getMarketSession(userId)
        viewModel.getChartIntraday(userId, sessionId, stockCode, getStartDate("1D"), endDate, 0)
        stockCodeList.clear()
        sharedViewModel.setStockCodeChange(isStockCodeChange)
        sharedViewModel.getStockOrderbook(userId, sessionId, stockCode)
        sharedViewModel.getStockDetailSummary(userId, sessionId, stockCode)
        viewModel.getStockParam(stockCode)
        viewModel.getAllStockParam("")
        viewModel.getStockNotation(stockCode)
        viewModel.getStockInfoDetail(userId, sessionId, stockCode)

        isStockCodeChange = false
    }

    @SuppressLint("SetTextI18n")
    override fun setupObserver() {
        super.setupObserver()
        viewModel.imqConnectionListener.connListenerLiveData.observe(this) { value ->
            when (value) {
                "Recovered" -> {
                    viewModel.imqConnectionListener.onListener("")
                    isStockCodeChange = true
                    initAPI()
                }

                else -> {}
            }
        }

        // from tab news&reports
        sharedViewModel.getChipNewsOnClick.observe(viewLifecycleOwner) {stock ->
            if (stock.isNotEmpty()) {
                sharedViewModel.unSubscribeOrderbook(stockCode)
                sharedViewModel.unSubscribeTradeSummary(stockCode)
                stockCode = stock
                prefManager.stockDetailCode = stockCode
                binding.lyToolbarStockDetail.tvLayoutToolbarMasterTextLeft.text = if (isScroll) stockCode else ""
                isStockCodeChange = true
                filterChartOnClick(1)
                initAPI()
            }
        }

        viewModel.isPriceAlertEmpty.observe(viewLifecycleOwner){
            it?.let {isEmpty ->

                val bundle = Bundle().apply {
                    putString(Args.EXTRA_PARAM_STR_ONE, stockCode)
                }

                if (isEmpty) {
                    bundle.putBoolean(Args.EXTRA_PARAM_BOOLEAN_TWO, true)
                    findNavController().navigate(R.id.create_edit_price_alert_fragment, bundle)
                } else {

                    findNavController().navigate(R.id.price_alert_fragment, bundle)
                }
                
                viewModel.clearisPriceAlertEmpty()
            }
        }

        sharedViewModel.getStockDetailSummary.observe(viewLifecycleOwner) {
            if (it != null) {
                val change = it.change

                binding.apply {
                    stockPrice = initFormatThousandSeparator(it.last)
                    tvStockDetailInfoChange.text =
                        "${change.formatPriceWithoutDecimal()} (${initPercentFormatNumber(it.changePct)})"
                    tvStockDetailInfoPrice.text = stockPrice
                    tvStockDetailInfoPrev.text = initFormatThousandSeparator(it.close)
                    tvStockDetailInfoOpen.text = initFormatThousandSeparator(it.open)
                    tvStockDetailInfoHigh.text = initFormatThousandSeparator(it.high)
                    tvStockDetailInfoLow.text = initFormatThousandSeparator(it.low)

                    tvStockDetailInfoIepVal.text = initFormatThousandSeparator(it.iep)
                    tvStockDetailInfoIevVal.text = initFormatThousandSeparator(it.iev)

                    tvStockDetailInfoVol.text = formatLastNumberStartFromMillion(it.tradeVolumeLot.toFloat())

                    // special : etf/warrant/right stock
                    val isEtf = stockCode.first() == 'X'
                    val isWarrantOrRight = stockCode.length > 4
                    val isSpecialStock = isEtf || isWarrantOrRight

                    if (!isSpecialStock) {
                        tvStockDetailInfoULimit.text = getAraArb(it.close, true)
                        tvStockDetailInfoLLimit.text = getAraArb(it.close, false)
                    }

                    tvStockDetailInfoTitleULimit.visibility = if (isSpecialStock) View.GONE else View.VISIBLE
                    tvStockDetailInfoULimit.visibility = if (isSpecialStock) View.GONE else View.VISIBLE
                    tvStockDetailInfoTitleLLimit.visibility = if (isSpecialStock) View.GONE else View.VISIBLE
                    tvStockDetailInfoLLimit.visibility = if (isSpecialStock) View.GONE else View.VISIBLE

                    openPrice = it.open

                    if (change > 0) {
                        infoChange = "+${change.formatPriceWithoutDecimal()} (+${initPercentFormatNumber(it.changePct)})"
                        tvStockDetailInfoChange.text =
                            "+${change.formatPriceWithoutDecimal()} (+${initPercentFormatNumber(it.changePct)})"
                        tvStockDetailInfoChange.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.textLightGreen
                            )
                        )
                    } else if (change < 0) {
                        infoChange = "${change.formatPriceWithoutDecimal()} (${initPercentFormatNumber(it.changePct)})"
                        tvStockDetailInfoChange.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.textDownHeader
                            )
                        )
                    } else {
                        infoChange = "${change.formatPriceWithoutDecimal()} (${initPercentFormatNumber(it.changePct)})"
                        tvStockDetailInfoChange.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.white
                            )
                        )
                    }
                }
            }
        }

        viewModel.getMarketSessionResult.observe(viewLifecycleOwner) {data ->
            if (data != null) {
                val nowSession = data.marketSessionName
                val isOpen = nowSession.contains("SESS_1") || nowSession.contains("SESS_2") || nowSession.contains("SESS_PRE_CLOSE")
                isSession1 = nowSession.contains("SESS_1") || nowSession.contains("SESS_PRICE_BUILD_OPEN")

                binding.clSdMarketClosed.visibility = if (isOpen) View.GONE else View.VISIBLE
            }
        }

        viewModel.getStockInfoDetailResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if (it.data != null) {
                        it.data.let { res ->
                            binding.apply {
                                tvStockDetailInfoWkHigh.text = formatLastNumberStartFromMillion(res.data.wkhigh52.toFloat())
                                tvStockDetailInfoWkLow.text = formatLastNumberStartFromMillion(res.data.wklow52.toFloat())
                                tvStockDetailInfoMktCap.text = formatLastNumberStartFromMillion(res.data.marketCap.toFloat())
                                tvStockDetailInfoTurnover.text = "${res.data.turnover.formatPercent()}%"
                            }
                        }
                    }
                }

                is Resource.Failure -> {
                    Toast.makeText(requireContext(), it.failureData.message, Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {}
            }
        }

        viewModel.getStockPosResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if (it.data != null) {
                        when (it.data.status) {
                            0 -> {
                                it.data.let { res ->
                                    if (res.accstockposList != null && res.accstockposList.size != 0) {
                                        val listStockPost = res.accstockposList[0]

                                        val marketValue =
                                            listStockPost.realStockAvailable * listStockPost.reffprice
                                        var value =
                                            listStockPost.avgprice * listStockPost.realStockAvailable
                                        val gainloss = marketValue - value
                                        val profitLoss =
                                            (listStockPost.reffprice - listStockPost.avgprice) * listStockPost.realStockAvailable
                                        val pct = (gainloss / value) * 100

                                        if (marketValue.equals(0.0)) {
                                            isStockOwned = false
                                        } else {
                                            isStockOwned = true
                                            val stockOwned = listStockPost.realStockAvailable / 100
                                            binding.apply {
                                                tvSdStockOwnedPrice.text =
                                                    initFormatThousandSeparator(marketValue)
                                                tvSdStockOwnedLot.text = stockOwned.toInt().formatPriceWithoutDecimal()
                                                tvSdStockOwnedAvg.text = listStockPost.avgprice.formatPriceWithDecimal()

                                                if (profitLoss > 0) {
                                                    tvSdStockOwnedProfitLoss.setTextColor(
                                                        ContextCompat.getColor(
                                                            requireContext(),
                                                            R.color.textUp
                                                        )
                                                    )
                                                    tvSdStockOwnedProfitLoss.text =
                                                        "+${initFormatThousandSeparator(profitLoss)} (+${
                                                            initPercentFormatNumber(pct)
                                                        })"
                                                } else if (profitLoss < 0) {
                                                    tvSdStockOwnedProfitLoss.setTextColor(
                                                        ContextCompat.getColor(
                                                            requireContext(),
                                                            R.color.textDown
                                                        )
                                                    )
                                                    tvSdStockOwnedProfitLoss.text =
                                                        "-${initFormatThousandSeparator(profitLoss)} (${
                                                            initPercentFormatNumber(pct)
                                                        })"
                                                } else {
                                                    tvSdStockOwnedProfitLoss.setTextColor(
                                                        ContextCompat.getColor(
                                                            requireContext(),
                                                            R.color.brandAccent
                                                        )
                                                    )
                                                    tvSdStockOwnedProfitLoss.text =
                                                        "${initFormatThousandSeparator(profitLoss)} (${
                                                            initPercentFormatNumber(pct)
                                                        })"
                                                }
                                            }
                                        }
                                    } else {
                                        unpublishRealtimePortfolio()
                                        isStockOwned = false
                                    }
                                }
                            }
                            2 -> (activity as MiddleActivity).showDialogSessionExpired()
                        }
                        checkStockOwn()
                    } else {
                        isStockOwned = false
                        checkStockOwn()
                    }
                }

                is Resource.Failure -> {
                    Toast.makeText(requireContext(), it.failureData.message, Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {}
            }
        }

        // realtime portfolio
        viewModel.getRealtimeStockPos.observe(viewLifecycleOwner) { data->
            if (data != null) {
                if (data.value.equals(0.0)) {
                    isStockOwned = false
                } else {
                    isStockOwned = true
                    binding.apply {
                        tvSdStockOwnedPrice.text = initFormatThousandSeparator(data.value)
                        tvSdStockOwnedLot.text = data.qtyStock.formatPriceWithoutDecimal()
                        tvSdStockOwnedAvg.text = data.avgprice.formatPriceWithDecimal()

                        if (data.profitLoss > 0) {
                            tvSdStockOwnedProfitLoss.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.textUp
                                )
                            )
                            tvSdStockOwnedProfitLoss.text =
                                "+${initFormatThousandSeparator(data.profitLoss)} (+${
                                    initPercentFormatNumber(data.pct)
                                })"
                        } else if (data.profitLoss < 0) {
                            tvSdStockOwnedProfitLoss.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.textDown
                                )
                            )
                            tvSdStockOwnedProfitLoss.text =
                                "-${initFormatThousandSeparator(data.profitLoss)} (${
                                    initPercentFormatNumber(data.pct)
                                })"
                        } else {
                            tvSdStockOwnedProfitLoss.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.brandAccent
                                )
                            )
                            tvSdStockOwnedProfitLoss.text =
                                "${initFormatThousandSeparator(data.profitLoss)} (${
                                    initPercentFormatNumber(data.pct)
                                })"
                        }
                    }
                }

                checkStockOwn()
            }
        }

        // Stock Param By Code
        viewModel.getStockParamResult.observe(viewLifecycleOwner) {
            val url = prefManager.urlIcon + GET_4_CHAR_STOCK_CODE(stockCode)

            Glide.with(requireActivity())
                .load(url)
                .override(300, 200)
                .circleCrop()
                .placeholder(R.drawable.bg_circle)
                .error(R.drawable.bg_circle)
                .into(binding.imageStock)

            stockName = it?.stockName.orEmpty()
            binding.tvStockDetailInfoName.text = stockName
            binding.tvStockDetailInfoCode.text = it?.stockCode
            binding.tvStockDetailInfoHaircut.text = "${it?.hairCut?.formatPriceWithoutDecimal()}%"
            binding.tvStockDetailInfoSpecialNotesAcceleration.text = it?.idxTrdBoard.GET_IDX_BOARD()
            binding.tvStockDetailInfoSpecialNotesAcceleration.visibility = if (binding.tvStockDetailInfoSpecialNotesAcceleration.text == "") View.GONE else View.VISIBLE
        }

        // All Stock Param
        viewModel.getAllStockParamResult.observe(viewLifecycleOwner) {
            stockCodeList.clear()
            it?.map { data -> data?.let { stockCodeList.add(data.stockCode) } }

//            if (allStockList){
//                for (checkedStock in allStockList) {
//                    val searchStock = allStockList.firstOrNull { searchStock -> searchStock.stockCode == checkedStock.stockCode }
//                    searchStock?.isChecked = true
//                }
//            }
//            searchStockAdapter.setData(searchStockList.sortedBy { it.stockCode }, isCheckbox )
        }

        // Get Session PIN
        viewModel.getSessionPinResult.observe(viewLifecycleOwner) {
            sessionPin = it
            if (it != null) {
                if (sessionPin != null) {
                    if (validateSessionPin(sessionPin!!)) {
                        Log.d("pinexp", "local stockdetail true")
                        if (isOpenPortfolio) {
                            binding.groupStockOwnoedShow.visibility =
                                if (isBalanceHidden) View.GONE else View.VISIBLE
                            binding.groupStockOwnoedHidden.visibility =
                                if (isBalanceHidden) View.VISIBLE else View.GONE
                            binding.ivEyeStockOwned.setImageResource(if (isBalanceHidden) R.drawable.ic_eye_outline_disabled else R.drawable.ic_eye_outline)

                            publishRealtimePortfolio()
                            isBalanceHidden = !isBalanceHidden
                            isOpenPortfolio = false
                        } else {
                            Log.d("pinexp", "hit stock pos")
                            viewModel.getStockPos(userId, prefManager.accno, sessionId, stockCode)
                        }
                    } else {
                        Log.d("pinexp", "local stockdetail false")
                        if (isOpenPortfolio) {
                            showDialogPin()
                        }
                    }
                } else {
                    if (isOpenPortfolio) {
                        showDialogPin()
                    }
                }
            }
        }

        // Check Notation
        viewModel.getStockNotationResult.observe(viewLifecycleOwner) {
            if (it.size != 0) {
                val listNotation = mutableListOf<String>()
                it.forEach {
                    if (it?.notation?.equals("X") == true) {
                        isNotation = true
                    }
                    if (!listNotation.contains(it?.notation)) {
                        it?.notation?.let { it1 -> listNotation.add(it1) }
                    }
                }
                binding.tvStockDetailInfoSpecialNotes.visibility = if (listNotation.size != 0) View.VISIBLE else View.GONE
                binding.tvStockDetailInfoSpecialNotes.text = listNotation.joinToString()
            } else {
                binding.tvStockDetailInfoSpecialNotes.visibility = View.GONE
            }
        }

        sharedViewModel.getRefreshFragmentResult.observe(viewLifecycleOwner){
            if (it == true){
                isStockCodeChange = true
                initAPI()
                binding.swplStockDetail.isRefreshing = false
            }
        }

        viewModel.getChartIntradayResult.observe(viewLifecycleOwner) {
            if (it != null) {
                val dataChart = it.map {item ->
                    DataChart(item.price, item.axisDate, isHoursFormat)
                }
                binding.chartStockDetail.setNoDataTextColor(ContextCompat.getColor(requireContext(), R.color.black))

                if (dataChart.isEmpty() && isSession1 && isChart1d) {
                    // draw open price if chart data is empty
                    val currentTime = getCurrentTimeInMillis()
                    val priceOpen = listOf(DataChart(openPrice, currentTime, isHoursFormat), DataChart(openPrice, currentTime, isHoursFormat))
                    setupChart(priceOpen)
                } else {
                    setupChart(dataChart)
                }
            } else {
                binding.chartStockDetail.clear()
            }
        }

        sharedViewModel.getPopUpSuccessPriceAlert.observe(viewLifecycleOwner) {isSuccess ->
            if (isSuccess == true) {
                showSnackBarTop(requireContext(), binding.root, "success", R.drawable.ic_success, "Price Alert Has Been Set", "You will receive a notification when stock price has reached your target.", requireActivity(), "")
                sharedViewModel.clearValuePopUpPriceAlert()
            }
        }

        viewModel.getLogoutResult.observe(viewLifecycleOwner) {
            when (it?.status){
                0 -> {
                    RabbitMQForegroundService.stopService(requireContext())
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

        sharedViewModel.getOrderBookSum.observe(viewLifecycleOwner) {sum ->
            if (sum != null) {
                binding.tvTotalBid.text = sum.totalBid.formatPriceWithoutDecimal()
                binding.tvTotalOffer.text = sum.totalOffer.formatPriceWithoutDecimal()
            }
        }
    }

    private fun setupChart(data: List<DataChart>) {
        //Part1
        val entries = ArrayList<Entry>()
        var index = 0f

        //Part2
        if (data.size != 0) {
            data.map {
                index++
                entries.add(Entry(index, it.price.roundedHalfUp().toFloat()))
            }
            val lineDataSet = LineDataSet(entries, "")
            val drawable =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_gradient_10c00d_ffffff)

            lineDataSet.fillDrawable = drawable
            lineDataSet.setDrawValues(false)
            lineDataSet.setDrawFilled(true)
            lineDataSet.lineWidth = 2f
            lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            lineDataSet.color = ContextCompat.getColor(requireContext(), R.color.textUp)
            lineDataSet.setDrawCircles(false)

            val dataMarker = data.toMutableList()
            val marker = CustomMarkerView(requireContext(), R.layout.custom_market_view, dataMarker)
            marker.chartView = binding.chartStockDetail

            binding.chartStockDetail.marker = marker
            binding.chartStockDetail.data = LineData(lineDataSet)
            binding.chartStockDetail.xAxis.setDrawGridLines(false)
            binding.chartStockDetail.axisLeft.setDrawGridLines(false)
            binding.chartStockDetail.axisRight.setDrawGridLines(false)
            binding.chartStockDetail.axisRight.textColor = ContextCompat.getColor(requireContext(), R.color.textPrimary)
            binding.chartStockDetail.description.text = ""
            binding.chartStockDetail.legend.isEnabled = false
            binding.chartStockDetail.axisLeft.isEnabled = false
            binding.chartStockDetail.xAxis.isEnabled = false
            binding.chartStockDetail.axisRight.axisLineColor = ContextCompat.getColor(requireContext(), R.color.white)
            binding.chartStockDetail.axisRight.isGranularityEnabled = true
            binding.chartStockDetail.setTouchEnabled(true)
            binding.chartStockDetail.setScaleEnabled(false)
            binding.chartStockDetail.isDoubleTapToZoomEnabled = false
            binding.chartStockDetail.setNoDataTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.chartStockDetail.invalidate()
            binding.chartStockDetail.axisRight.valueFormatter = object : ValueFormatter() {
                private val decimalFormat = DecimalFormat("#,###,###")
                override fun getPointLabel(entry: Entry?): String {
                    return decimalFormat.format(entry)
                }

                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return decimalFormat.format(value)
                }
            }
        } else {
            binding.chartStockDetail.clear()
            binding.chartStockDetail.setNoDataTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }


    }

    private fun getAraArb(price: Double, isForAra: Boolean): String {
        // true for ara, false for arb
        return if (price != 0.0) {
            var top: Double
            var bottom: Double

            val percent = when {
                price < 50.0 -> 0.1
                price in 50.0..200.0 -> 0.35
                price in 200.0..5000.0 -> 0.25
                price > 5000.0 -> 0.20
                else -> 0.0
            }

            when {
                isNotation && price <= 10 -> {
                    top = price + 1
                    bottom = price - 1
                }

                isNotation && price > 10 -> {
                    top = price + (price * 0.10)
                    bottom = price - (price * 0.10)
                }

                else -> {
                    top = price + (price * percent)
                    bottom = price - (price * 0.15)

                    if (price >= 50) {
                        bottom = if (bottom < 50) 50.0 else bottom
                    } else {
                        bottom = if (bottom < 1) 1.0 else bottom
                    }
                }

            }
            val arb = ceil(bottom) // rounding up bottom value
            val adjustBottom = if (!checkFractionPrice(arb.toInt())) adjustFractionPrice(arb.toInt(),"", false) else arb.toInt().formatPriceWithoutDecimal()
            val adjustTop = if (!checkFractionPrice(top.toInt())) adjustFractionPrice(top.toInt(), "-", false) else top.toInt().formatPriceWithoutDecimal()

            if (isForAra) adjustTop else adjustBottom
        } else {
            "0"
        }

    }

    private fun checkFractionPrice(price: Int): Boolean {
        val modVal = when {
            isSpecialStock -> 1
            price < 200 -> 1
            price < 500 -> 2
            price < 2000 -> 5
            price < 5000 -> 10
            else -> 25
        }

        return if (modVal == 1) {
            true
        } else {
            price % modVal == 0
        }
    }

    private fun getStartDate(state: String): Long {
        val calendar = Calendar.getInstance()
        when (state) {
            "1D" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            }
            "1W" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
            }
            "1M" -> {
                calendar.add(Calendar.MONTH, -1)
            }
            "3M" -> {
                calendar.add(Calendar.MONTH, -3)
            }
            "1Y" -> {
                calendar.add(Calendar.YEAR, -1)
            }
            "5Y" -> {
                calendar.add(Calendar.YEAR, -5)
            }
            "10Y" -> {
                calendar.add(Calendar.YEAR, -10)
            }
        }
        return calendar.timeInMillis
    }

    private fun checkStockOwn() {
        if (isStockOwned) {
            binding.clSdStockOwned.visibility = View.VISIBLE
        } else {
            binding.clSdStockOwned.visibility = View.GONE
        }
    }

    private fun checkPin() {
        viewModel.getSessionPin(userId)
    }

    private fun showDialogPin() {
        showDialogPin(parentFragmentManager, onSuccess = { isSuccess, isBlocked ->
            if (isSuccess) {
                binding.groupStockOwnoedShow.visibility = if (isBalanceHidden) View.GONE else View.VISIBLE
                binding.groupStockOwnoedHidden.visibility = if (isBalanceHidden) View.VISIBLE else View.GONE
                binding.ivEyeStockOwned.setImageResource(if (isBalanceHidden) R.drawable.ic_eye_outline_disabled else R.drawable.ic_eye_outline)

                publishRealtimePortfolio()
                isBalanceHidden = !isBalanceHidden
                isOpenPortfolio = false
            } else {
                if (isBlocked) {
                    viewModel.getLogout(prefManager.userId, prefManager.sessionId)
                }
            }
        })
    }

    private fun filterChartOnClick(state: Int) {
        isChart1d = state == 1
        binding.apply {
            // 1 = day, 2 = week, 3 = month, 4 = three month, 5 = year,6 = five years, 7 = all
            isHoursFormat = state == 1 || state == 2
            for (i in 1..7) {
                val textView = when (i) {
                    1 -> tvStockDetailFilter1d
                    2 -> tvStockDetailFilter1w
                    3 -> tvStockDetailFilter1m
                    4 -> tvStockDetailFilter3m
                    5 -> tvStockDetailFilter1y
                    6 -> tvStockDetailFilter5y
                    7 -> tvStockDetailFilterAll
                    else -> throw IllegalArgumentException("Invalid parameter: $i")
                }
                textView.setBackgroundResource(if (i == state) R.drawable.rounded_02b9cb_56 else 0)
                textView.setTextColor(if (i == state) ContextCompat.getColor(requireContext(), R.color.textChart) else ContextCompat.getColor(requireContext(), R.color.textSecondary))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sharedViewModel.setListenerOrderBookTradeSum()
        viewModel.startRealtimeDataPortofolio(accNo)
        viewPager.registerOnPageChangeCallback(pageChangeCallback as ViewPager2.OnPageChangeCallback)

        hasSubscribeAccPos = false
    }

    override fun onPause() {
        super.onPause()
        sharedViewModel.unSubscribeTradeSummary(stockCode)
        sharedViewModel.unSubscribeOrderbook(stockCode)
        viewModel.stopRealtimeDataPortofolio(accNo)
        pageChangeCallback?.let { viewPager.unregisterOnPageChangeCallback(it) }

        isBalanceHidden = false

        unpublishRealtimePortfolio()
    }

    private fun publishRealtimePortfolio() {
        if (!hasSubscribeAccPos && isStockOwned && !isBalanceHidden) {
            subscribeJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(3000)

                if (!isActive) return@launch

                // publish acc pos (subsOp = 0)
                hasSubscribeAccPos = true
                viewModel.publishAccPos(userId, sessionId, 0, accNo)
            }
        }
    }

    private fun unpublishRealtimePortfolio() {
        subscribeJob?.cancel()
        if (hasSubscribeAccPos) {
            // unpublish acc pos (subsOp = 1)
            viewModel.publishAccPos(userId, sessionId, 1, accNo)
            hasSubscribeAccPos = false
        }
    }
}
