package com.bcasekuritas.mybest.app.feature.runningtrade

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Build
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.OptIn
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.data.layout.UIDialogRunningTradeModel
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.runningtrade.adapter.RunningTradeAdapter
import com.bcasekuritas.mybest.app.feature.runningtrade.adapter.SelectedStockChipAdapter
import com.bcasekuritas.mybest.databinding.FragmentRunningTradeBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class RunningTradeFragment : BaseFragment<FragmentRunningTradeBinding, RunningTradeViewModel>(), ShowDialog by ShowDialogImpl(), OnClickStr {

    override val bindingVariable: Int = BR.vmRunningTrade
    override val viewModel: RunningTradeViewModel by viewModels()
    override val binding: FragmentRunningTradeBinding by autoCleaned { (FragmentRunningTradeBinding.inflate(layoutInflater)) }

    private val runningTradeAdapter: RunningTradeAdapter by autoCleaned { RunningTradeAdapter(requireContext()) }
    private val selectedStockAdapter: SelectedStockChipAdapter by autoCleaned { SelectedStockChipAdapter(this) }

    private lateinit var badge: BadgeDrawable
    private lateinit var countDownTimer:CountDownTimer
    private var isCountDownTimerStart = false
    private var isMarketBreak = false
    private var isMarketOpen = false
    private var textMarketBreak = "resume."
    private var isFirstOpen = true

    private var searchHint = "Search code or name"
    private var selectedStockCodes: ArrayList<String> = arrayListOf()
    private var filter = UIDialogRunningTradeModel()
    private var defaultFilter = UIDialogRunningTradeModel()
    private var btnFilterClick = false

    companion object {
        fun newInstance() = RunningTradeFragment()
    }

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvRunningTrade.apply {
            adapter = runningTradeAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL, false)
        }
        binding.rcvSelectedStock.adapter = selectedStockAdapter
    }

    @OptIn(ExperimentalBadgeUtils::class) @SuppressLint("ClickableViewAccessibility")
    override fun setupComponent() {
        super.setupComponent()

        badge = BadgeDrawable.create(requireContext())
        // get height of rcv to set max item
        setMaxDataRunningTrade()


        binding.tvStart.setOnClickListener {
            viewModel.subscribeRunningTrade()
            isActiveRunningTrade(true)
        }

        binding.tvStop.setOnClickListener {
            viewModel.unSubscribeRunningTrade()
            isActiveRunningTrade(false)
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        // prevent trigger twice while on click on the background rcv
        var isRcvStockClicked = false
        binding.rcvSelectedStock.setOnTouchListener { view, motionEvent ->
            if (isRcvStockClicked) {
                return@setOnTouchListener false  // Ignore further clicks
            }
            isRcvStockClicked = true
            val childView = binding.rcvSelectedStock.findChildViewUnder(motionEvent.x, motionEvent.y)

            // If there's no child view under the touch, it's a background click
            if (childView == null) {
                Log.d("runnn", "click")
                // Handle click on RecyclerView's background
                showDialogSearchStock(parentFragmentManager, selectedStockCodes, onSelectedStock = { isSelected, stockCode ->
                    if (isSelected) {
                        if (stockCode.isNotEmpty()) {
                            selectedStockCodes.add(stockCode)
                            selectedStockAdapter.setData(selectedStockCodes)
                            binding.rcvSelectedStock.visibility = View.VISIBLE
                            BadgeUtils.attachBadgeDrawable(badge, binding.icFilter)

                            // override filter stock in bottomsheet
                            filter = UIDialogRunningTradeModel()
                            viewModel.setFilter(filter)
                            viewModel.setFilterStock(getFilterStock())
                        }
                        binding.etSearchRunningTrade.hint = if (stockCode.isNotEmpty()) "" else searchHint
                    }

                    isRcvStockClicked = false
                })
            } else {
                isRcvStockClicked = false
            }
            false
        }

        binding.etSearchRunningTrade.setOnClickListener {
            showDialogSearchStock(parentFragmentManager, selectedStockCodes, onSelectedStock = { isSelected, stockCode ->
                if (isSelected) {
                    if (stockCode.isNotEmpty()) {
                        BadgeUtils.attachBadgeDrawable(badge, binding.icFilter)
                        selectedStockCodes.add(stockCode)
                        selectedStockAdapter.setData(selectedStockCodes)
                        binding.rcvSelectedStock.visibility = View.VISIBLE

                        // override filter stock in bottomsheet
                        filter = UIDialogRunningTradeModel()
                        viewModel.setFilter(filter)
                        viewModel.setFilterStock(getFilterStock())
                    }
                    binding.etSearchRunningTrade.hint = if (stockCode.isNotEmpty()) "" else searchHint
                }
            })
        }

    }

    private fun getFilterStock(): List<String> {
        return (selectedStockCodes + filter.stockCodes).toSet().distinct().toList()
    }

    @OptIn(ExperimentalBadgeUtils::class) override fun setupListener() {
        super.setupListener()
        parentFragmentManager.setFragmentResultListener(
            NavKeys.KEY_FM_RUNNING_TRADE,
            viewLifecycleOwner
        ) { _, result ->
            //filter dialog
            val confirmResultFilter = result.getString(NavKeys.CONST_RES_RUNNING_TRADE)
            if (confirmResultFilter == "RESULT_OK") {
                filter = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) result.getParcelable(Args.EXTRA_PARAM_OBJECT, UIDialogRunningTradeModel::class.java)?: UIDialogRunningTradeModel()
                else result.getParcelable(Args.EXTRA_PARAM_OBJECT))?: UIDialogRunningTradeModel()

                if (filter != UIDialogRunningTradeModel()) {
                    BadgeUtils.attachBadgeDrawable(badge, binding.icFilter)
                } else {
                    BadgeUtils.detachBadgeDrawable(badge, binding.icFilter)
                }
                selectedStockCodes.clear()
                selectedStockAdapter.clearData()
                viewModel.setFilterStock(getFilterStock())
                viewModel.setFilter(filter)
            }
        }
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            btnFilter.setOnClickListener {
                btnFilterClick = true
                viewModel.getDefaultFilterRunningTrade(prefManager.userId)
            }
            btnBuy.setOnClickListener {
                navigateToOrderMenu(0)
            }

            btnSell.setOnClickListener {
                navigateToOrderMenu(1)
            }
        }
    }

    @OptIn(ExperimentalBadgeUtils::class) override fun setupObserver() {
        super.setupObserver()
        viewModel.imqConnectionListener.connListenerLiveData.observe(this) { value ->
            when (value) {
                "Recovered" -> {
                    viewModel.imqConnectionListener.onListener("")
                    viewModel.isRunningTradeStart = false
                    if (isCountDownTimerStart) {
                        countDownTimer.cancel()
                        isCountDownTimerStart = false
                    }
                }

                else -> {}
            }
        }

        viewModel.getRunningTradeData.observe(viewLifecycleOwner) { listResource ->
            if (listResource.isNotEmpty()) {
                if (isMarketBreak) {
                    runningTradeAdapter.setData(listResource, 1)
                } else {
                    runningTradeAdapter.setData(listResource, viewModel.getLatestUniqueId())
                }
            }
        }

        viewModel.getMarketSessionResult.observe(viewLifecycleOwner) {data ->
            if (data != null) {
                val nextMillis = data.nextMarketSessionDate
                val nowMillis = data.marketSessionDate
                val nowSession = data.marketSessionName
                val currentMillis = System.currentTimeMillis()
                val isHoliday = nowSession.contains("SESS_NONE") || nowSession.contains("SESS_BEFORE_OPEN") ||
                        nowSession.contains("SESS_MARKET_CLOSE") || nowSession.contains("SESS_NEGO_EXTEND") || nowSession.contains("SESS_HOLIDAY")
                isMarketOpen = nowSession.contains("SESS_1") || nowSession.contains("SESS_2")
                isMarketBreak = nowSession.contains("SESS_LUNCH_BREAK")

                textMarketBreak = if (data.nextMarketSessionName == "SESS_MARKET_CLOSE" || data.nextMarketSessionName == "SESS_NEGO_EXTEND") "close." else "resume."

                if (isMarketOpen) {
                    isActiveRunningTrade(true)
                    countdownTimer(nextMillis, false)

                    binding.boxMarketClose.visibility = View.GONE
                    setVisibilityLayout(true)

                    setMaxDataRunningTrade()
                } else {
                    hideButtonStartStop(true)
                    if (!isHoliday && currentMillis >= nowMillis && currentMillis < nextMillis) {
                        binding.boxMarketClose.visibility = View.VISIBLE
                        setVisibilityLayout(true)

                        setMaxDataRunningTrade()
                        countdownTimer(nextMillis, false)
                    } else {
                        // is holiday
                        binding.boxMarketClose.visibility = View.GONE
                        setVisibilityLayout(false)

                        countdownTimer(nextMillis, true)
                    }
                }

                viewModel.startRunningTrade(prefManager.userId, prefManager.sessionId, isMarketBreak)
            }
        }

        viewModel.getDefaultFilter.observe(viewLifecycleOwner) { res ->
            defaultFilter = res
            if (isFirstOpen) {
                filter = res
            }

            if (btnFilterClick) {
                showDialogFilterRunningTrade(filter, defaultFilter, isFirstOpen, parentFragmentManager)
                isFirstOpen = false
                btnFilterClick = !btnFilterClick
            } else {
                if (defaultFilter != UIDialogRunningTradeModel()) {
                    viewModel.setFilterStock(getFilterStock())
                    viewModel.setFilter(defaultFilter)
                    BadgeUtils.attachBadgeDrawable(badge, binding.icFilter)
                } else {
                    BadgeUtils.detachBadgeDrawable(badge, binding.icFilter)
                }
            }
        }
    }

    private fun setMaxDataRunningTrade() {

        binding.rcvRunningTrade.doOnNextLayout {recyclerView ->
            if (!isAdded || view == null) return@doOnNextLayout

            val recyclerViewHeight = recyclerView.height
            val density = resources.displayMetrics.density
            val heightInDP = recyclerViewHeight / density

            val totalItem = (heightInDP / 39).toInt()
            if (totalItem > 0) {
                viewModel.setMaxRunningTrade(totalItem)
            }
        }
    }

    private fun countdownTimer(startTime: Long , isHoliday: Boolean) {
        if (!isCountDownTimerStart) {
            val elapsedMillis = startTime - System.currentTimeMillis()
            isCountDownTimerStart = true
            countDownTimer = object: CountDownTimer(elapsedMillis, 1000) {
                override fun onTick(p0: Long) {
                    if (!isMarketOpen) {
                        var diff = p0
                        val secondsInMilli: Long = 1000
                        val minutesInMilli = secondsInMilli * 60
                        val hoursInMilli = minutesInMilli * 60
                        val daysInMilli = hoursInMilli * 24

                        val elapsedDays = diff / daysInMilli
                        if (elapsedDays >= 1) {
                            diff %= daysInMilli
                        }

                        val elapsedHours = diff / hoursInMilli
                        diff %= hoursInMilli

                        val elapsedMinutes = diff / minutesInMilli
                        diff %= minutesInMilli

                        val elapsedSeconds = diff / secondsInMilli

                        val hoursString = if (elapsedHours.toString().length == 1) "0${elapsedHours}H" else "${elapsedHours}H"
                        val minutesString = if (elapsedMinutes.toString().length == 1) "0${elapsedMinutes}M" else "${elapsedMinutes}M"
                        val secondString = if (elapsedSeconds.toString().length == 1) "0${elapsedSeconds}S" else "${elapsedSeconds}S"

                        if (isHoliday) {
                            if (elapsedDays >= 1) {
                                val daysString = "0${elapsedDays}D"
                                binding.tvTimer.text = daysString + ":" + hoursString + ":"+ minutesString + ":" + secondString
                            } else {
                                binding.tvTimer.text = hoursString + ":"+ minutesString + ":" + secondString
                            }
                        } else {
                            val countDown = hoursString + ":"+ minutesString + ":" + secondString
                            val text = countDown + " left until market $textMarketBreak"
                            val spannable = SpannableString(text)
                            spannable.setSpan(
                                StyleSpan(Typeface.BOLD),
                                0,
                                countDown.length - 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            binding.tvTimerBox.text = spannable
                        }
                    }
                }

                override fun onFinish() {
                    isCountDownTimerStart = false
                    viewModel.getMarketSession(prefManager.userId)
                }
            }.start()
        }
    }

    private fun hideButtonStartStop(isHide: Boolean) {
        binding.apply {
            tvStart.visibility = if (isHide) View.GONE else View.VISIBLE
            tvStop.visibility = if (isHide) View.GONE else View.VISIBLE
        }
    }

    override fun initAPI() {
        super.initAPI()
        viewModel.getDefaultFilterRunningTrade(prefManager.userId)
    }

    override fun onResume() {
        super.onResume()
        viewModel.setListenerRunningTrade()
        viewModel.subscribeRunningTrade()
        viewModel.getMarketSession(prefManager.userId)
    }

    override fun onPause() {
        super.onPause()
        viewModel.unSubscribeRunningTrade()
        if (isCountDownTimerStart) {
            countDownTimer.cancel()
            isCountDownTimerStart = false
        }
    }

    // remove selected stock click
    @OptIn(ExperimentalBadgeUtils::class) override fun onClickStr(value: String?) {
        selectedStockCodes.removeIf { it == value }
        binding.rcvSelectedStock.visibility = if (selectedStockCodes.isNotEmpty()) View.VISIBLE else View.GONE
        if (selectedStockCodes.isNotEmpty()) {
            selectedStockAdapter.setData(selectedStockCodes)
            binding.etSearchRunningTrade.hint = ""
        } else {
            binding.etSearchRunningTrade.hint = searchHint
            BadgeUtils.detachBadgeDrawable(badge, binding.icFilter)
        }
        viewModel.setFilterStock(getFilterStock())
    }

    private fun isActiveRunningTrade(state: Boolean) {
        when (state) {
            true -> {
                binding.tvStart.visibility = View.GONE
                binding.tvStop.visibility = View.VISIBLE

                viewModel.subscribeRunningTrade()
            }
            false -> {
                binding.tvStart.visibility = View.VISIBLE
                binding.tvStop.visibility = View.GONE

                viewModel.unSubscribeRunningTrade()
            }
        }
    }

    private fun setVisibilityLayout(isOpen: Boolean) {
        if (isOpen) {
            binding.marketOpen.visibility = View.VISIBLE
            binding.lyMarketClosed.visibility = View.GONE
            binding.etSearchRunningTrade.visibility = View.VISIBLE
            binding.btnFilter.visibility = View.VISIBLE
            binding.rcvSelectedStock.visibility = View.VISIBLE
            binding.lyButtonBuySell.visibility = View.VISIBLE
            binding.dropShadow.visibility = View.VISIBLE
        } else {
            binding.marketOpen.visibility = View.GONE
            binding.lyMarketClosed.visibility = View.VISIBLE
            binding.etSearchRunningTrade.visibility = View.GONE
            binding.btnFilter.visibility = View.GONE
            binding.rcvSelectedStock.visibility = View.GONE
            binding.lyButtonBuySell.visibility = View.GONE
            binding.dropShadow.visibility = View.GONE
        }
    }

    private fun navigateToOrderMenu(bs: Int) {
        // 0 for buy, 1 for sell
        val stockCode = runningTradeAdapter.getHighlightStock()
        if (stockCode.isNotEmpty()) {
            when (bs) {
                0 -> {
                    MiddleActivity.startIntentParam(
                        requireActivity(),
                        NavKeys.KEY_FM_ORDER,
                        stockCode,
                        bs
                    )
                }
                1 -> {
                    MiddleActivity.startIntentParam(
                        requireActivity(),
                        NavKeys.KEY_FM_ORDER,
                        stockCode,
                        1
                    ) // 0 for buy, 1 for sell
                }
            }
        }

    }
}