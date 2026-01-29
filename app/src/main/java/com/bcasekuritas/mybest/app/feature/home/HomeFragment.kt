package com.bcasekuritas.mybest.app.feature.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.request.WatchListCategory
import com.bcasekuritas.mybest.app.domain.dto.response.TradeSummary
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.activity.main.MainActivity
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.home.adapter.WatchlistHomeAdapter
import com.bcasekuritas.mybest.databinding.FragmentHomeBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.RabbitMQForegroundService
import com.bcasekuritas.mybest.ext.common.validateSessionPin
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.bcasekuritas.mybest.ext.listener.OnClickAnyInt
import com.bcasekuritas.mybest.ext.listener.OnClickStrInt
import com.bcasekuritas.mybest.ext.other.formatPercent
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimalWithoutMinus
import com.bcasekuritas.mybest.widget.banner.BannerUtil
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

@FragmentScoped
@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(), OnClickStrInt, OnClickAnyInt, ShowDialog by ShowDialogImpl() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmHome
    override val viewModel: HomeViewModel by viewModels()
    override val binding: FragmentHomeBinding by autoCleaned {
        (FragmentHomeBinding.inflate( layoutInflater ))
    }

    //
    private val watchlistHomeAdapter: WatchlistHomeAdapter by autoCleaned {
        WatchlistHomeAdapter(prefManager.urlIcon, this, this)
    }

    private var isFromAuthDevice = false
    private var isFirstOpen = false

    private var userId = ""
    private var sessionId = ""
    private var accNo = ""

    private var balanceHidden = true
    private var wasSelectedChipWatchlist: Chip? = null
    private val chipWlExist = arrayListOf<String>()
    private var watchlistCategoryState = "All"
    private var deletedWatchlistItem = ""

    private val watchlistMap = mutableMapOf<String, WatchListCategory>()
    private val listWatchList = arrayListOf<TradeSummary>()

    private var isCheckPinForPortfolioWatchlist = false

    private var subscribeJob: Job? = null
    private var hasSubscribeAccPos = false

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun setupArguments() {
        super.setupArguments()
        arguments.let {
            if (it != null) {
                isFromAuthDevice = it.getBoolean(Args.EXTRA_PARAM_BOOLEAN)
                isFirstOpen = true
            }
        }
    }

    override fun setupAdapter() {
        super.setupAdapter()
        binding.apply {
            rcvWatchlistHome.apply {
                adapter = watchlistHomeAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            }
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
//            showSuccessAuthenticatedDevice()

            binding.bannerHome.setActivity(requireActivity())

            btnNotifHome.setOnClickListener {
                MiddleActivity.startIntent(requireActivity(),NavKeys.KEY_FM_NOTIFICATION)
            }

            menuStockPicks.setOnClickListener {
                findNavController().navigate(R.id.stock_pick_fragment)
            }

            menuFastOrder.setOnClickListener {
                MiddleActivity.startIntentParam(
                    requireActivity(),
                    NavKeys.KEY_FM_FAST_ORDER,
                    "BBCA",
                    "Bank Central Asia Tbk"
                )
            }

            chipGroupWatchlist.setOnCheckedStateChangeListener { _, checkedIds ->
                for (chipId in checkedIds) {
                    val selectedChip = chipGroupWatchlist.findViewById(chipId) as Chip
                    selectedChip.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    wasSelectedChipWatchlist?.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                    if (wasSelectedChipWatchlist?.text.toString()
                            .equals("All") && !selectedChip.text.toString().equals("All")
                    ) {
                        watchlistHomeAdapter.clearData()
                    }
                    wasSelectedChipWatchlist = selectedChip
                    val wlCode = selectedChip.text.toString()
                    watchlistCategoryState = wlCode
                    rcvWatchlistHome.closeOpenMenu()

                    if (wlCode.lowercase() == "portfolio") {
                        isCheckPinForPortfolioWatchlist = true
                        checkPin()
                    } else {
                        getWatchlistItem(wlCode)
                    }
                }
            }

            btnSearchHome.setOnClickListener {
                MiddleActivity.startIntent(requireActivity(), NavKeys.KEY_FM_SEARCH_STOCK)
            }

            tvManageWatchlist.setOnClickListener {
                MiddleActivity.startIntent(requireActivity(), NavKeys.KEY_FM_MANAGE_WATCHLIST)
            }

            boxNoWatchlistHome.setOnClickListener {
                MiddleActivity.startIntent(requireActivity(), NavKeys.KEY_FM_MANAGE_WATCHLIST)
            }
        }
        onClickChipNewInvestor()
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            btnEye.setOnClickListener {
                if (!balanceHidden) {
                    balanceHidden = true

                    groupBalanceHidden.visibility = View.VISIBLE
                    groupBalanceShow.visibility = View.GONE
                    btnEye.setImageResource(R.drawable.ic_eye_solid_disable)
                } else {
                    checkPin()
                }
            }

            menuMyOrder.setOnClickListener {
                val bundle = Bundle().apply {
                    putInt(Args.EXTRA_PARAM_INT_ONE, 1)
                }
                findNavController().navigate(R.id.portfolio_fragment, bundle)
            }

            btnViewPortfolio.setOnClickListener {
                findNavController().navigate(R.id.portfolio_fragment)
            }

            bannerHome.setImageClickListener {
                findNavController().navigate(R.id.news_fragment)
            }

            menuPriceAlert.setOnClickListener {
                MiddleActivity.startIntent(requireActivity(), NavKeys.KEY_FM_PRICE_ALERT)
            }

            menuRunningTrade.setOnClickListener {
                findNavController().navigate(R.id.running_trade_fragment)
            }

            menuCalendar.setOnClickListener {
                findNavController().navigate(R.id.calendar_fragment)
            }

            menuNews.setOnClickListener {
                findNavController().navigate(R.id.news_fragment)
            }
        }

    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        sessionId = prefManager.sessionId
        accNo = prefManager.accno

        viewModel.getPromoBannerUseCase(userId, sessionId)
        viewModel.getAccNameDao(accNo)

        Handler(Looper.getMainLooper()).postDelayed({
            if (isFirstOpen) {
                viewModel.getStockParamList(userId, sessionId)
                isFirstOpen = false
            }
        }, 3000)
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.setListenerRealtimeData()
            viewModel.subsCifStockPos(accNo)
        }, 200)

        resetChipWatchlist()
        binding.chip1Watchlist.isChecked = true
        viewModel.getAllWatchlist(userId, sessionId)

        hasSubscribeAccPos = false
        publishRealtimePortfolio()
    }

    override fun onPause() {
        super.onPause()
        viewModel.unSubscribeTradeSummary()
        viewModel.unSubsCifStockPos(accNo)
        isFromAuthDevice = false

        unpublishRealtimePortfolio()
    }

    private fun publishRealtimePortfolio() {
        if (!balanceHidden && !hasSubscribeAccPos) {
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
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setupObserver() {
        super.setupObserver()

        viewModel.getSimplePortfolioResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }

                is Resource.Success -> {
                    if (it.data != null) {
                        when (it.data?.status) {
                            0 -> {
                                it.data?.let { res ->
                                    binding.apply {
                                        tvTotalPortfolioVal.text = res.simplePortofolio.portfolio.formatPriceWithoutDecimal()
                                        tvTradingBalanceVal.text = res.simplePortofolio.eqBuyingPower.formatPriceWithoutDecimal()

                                        val returnValue = res.simplePortofolio.unrealizedGainLoss
                                        val percent = res.simplePortofolio.unrealizedGainLossPct * 100
                                        setValueUnrealizedGainLoss(returnValue, percent)

                                        balanceHidden = !balanceHidden
                                        groupBalanceHidden.visibility = if (balanceHidden) View.VISIBLE else View.GONE
                                        groupBalanceShow.visibility = if (balanceHidden) View.GONE else View.VISIBLE
                                        btnEye.setImageResource(if (balanceHidden) R.drawable.ic_eye_solid_disable else R.drawable.ic_eye_solid)

                                        publishRealtimePortfolio()
                                    }
                                }
                            }

                            2 -> (activity as MainActivity).showDialogSessionExpired()
                            else -> {
                                binding.apply {
                                    tvTotalPortfolioVal.text = "0"
                                    tvTradingBalanceVal.text = "0"

                                    val returnValue = 0.0
                                    val percent = 0.0
                                    setValueUnrealizedGainLoss(returnValue, percent)

                                    balanceHidden = !balanceHidden
                                    groupBalanceHidden.visibility = if (balanceHidden) View.VISIBLE else View.GONE
                                    groupBalanceShow.visibility = if (balanceHidden) View.GONE else View.VISIBLE
                                    btnEye.setImageResource(if (balanceHidden) R.drawable.ic_eye_solid_disable else R.drawable.ic_eye_solid)

                                    publishRealtimePortfolio()
                                }
                            }
                        }
                    }
                    hideLoading()

                }

                is Resource.Failure -> {
                    Toast.makeText(requireContext(), it.failureData.message, Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {}
            }
        }

        viewModel.getStockDetailWatchList.observe(viewLifecycleOwner) {
            binding.apply {
                if (it.isNotEmpty()) {
                    groupWatchlist.visibility = View.VISIBLE
                    rcvWatchlistHome.visibility = View.VISIBLE
                    groupNoWatchlist.visibility = View.GONE
                    lyNoDataPortfolio.visibility = View.GONE
                    groupFirstTimeLogin.visibility = View.GONE

                    rcvWatchlistHome.findItem()
                    listWatchList.clear()
                    listWatchList.addAll(it.filterNotNull())
                    watchlistHomeAdapter.setIsPortfolioCategory(watchlistCategoryState.lowercase() == "portfolio")
                    watchlistHomeAdapter.setData(it.filterNotNull())
                } else {
                    if (watchlistMap.size != 0) {
                        groupNoWatchlist.visibility = View.VISIBLE
                        rcvWatchlistHome.visibility = View.GONE
                        watchlistHomeAdapter.clearData()
                        listWatchList.clear()
                    } else {
//                    groupWatchlist.visibility = View.GONE
//                    groupNoWatchlist.visibility = View.VISIBLE
                    }
                }
            }
        }

        viewModel.getAccNameDaoResult.observe(viewLifecycleOwner) {
            it?.let {
                binding.apply {
                    tvUsername.text = it
                }
            }
        }

        viewModel.getPromoBannerResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    it.data?.let { res ->
                        if (res.promotionBannerCount != 0) {
                            val itemBanner = ArrayList<BannerUtil.BannerItemPromo>()
                            val sortedList = res.promotionBannerList.sortedBy {item-> item.priority }
                            sortedList.forEach { item ->
                                itemBanner.add(
                                    BannerUtil.BannerItemPromo(item.imagePath, item.link)
                                )
                            }

                            binding.bannerHome.initSlider(itemBanner)
                        }
                    }
                }

                else -> {}
            }
        }

        viewModel.getSimpleAllWatchlistResult.observe(viewLifecycleOwner) { res ->
            if (res != null) {
                binding.apply {
                    when (res.status) {
                        0 -> {
                            if (res.userWatchListItemList?.size != 0) {
                                val listStockParam = arrayListOf<String>()
                                res.userWatchListItemList?.map { listStockParam.add(it.itemCode) }
                                if (listStockParam.size != 0) {
                                    watchlistMap["All"] = WatchListCategory("All", stockListString = listStockParam)
                                    viewModel.getStockDetail(userId, sessionId, "All", listStockParam)
                                }

//                                if (!prefManager.isCoachmarkWatchListShow) {
//                                    prefManager.isCoachmarkWatchListShow = true
//                                    showDialogCoachmarkWatchlist(parentFragmentManager)
//                                }
                            } else {
                                if (watchlistMap.size != 0) {
                                    groupNoWatchlist.visibility = View.VISIBLE
                                    rcvWatchlistHome.visibility = View.GONE
                                }
//                                if (!prefManager.isCoachmarkWatchListEmptyShow) {
//                                    prefManager.isCoachmarkWatchListEmptyShow = true
//                                    showDialogCoachmarkNoWatchlist(parentFragmentManager)
//                                }
                            }
                        }

                        2 -> (activity as MainActivity).showDialogSessionExpired()
                    }
                }
            }
            viewModel.getSimpleWatchlist(userId, sessionId, "")
        }

        viewModel.getRealtimeWatchlistPortfolioResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }

                is Resource.Success -> {
                    val res = it.data
                    if (res != null) {
                        when (res.status) {
                            0 -> {
                                binding.apply {
                                    if (res.userWatchListList.isNotEmpty()) {
                                        val item = res.userWatchListList[0]
                                        val wlCode = item.userWatchListGroup.wlCode
                                        val listStockParam = arrayListOf<String>()
                                        item.userWatchListItemList.map { itemWl ->
                                            listStockParam.add(itemWl.itemCode)
                                        }
                                        watchlistMap[wlCode] = WatchListCategory(wlCode, stockListString = listStockParam)

                                        if (watchlistCategoryState == "Portfolio") {
                                            val listStock = watchlistMap[wlCode]?.stockListString
                                            if (!listStock.isNullOrEmpty()) {
                                                viewModel.getStockDetail(userId, sessionId, wlCode, listStock)
                                            } else {
                                                groupNoWatchlist.visibility = if (watchlistCategoryState.lowercase() == "portfolio") View.GONE else View.VISIBLE
                                                lyNoDataPortfolio.visibility = if (watchlistCategoryState.lowercase() == "portfolio") View.VISIBLE else View.GONE
                                                rcvWatchlistHome.visibility = View.GONE
                                                groupFirstTimeLogin.visibility = if (watchlistCategoryState.lowercase() == "all") View.VISIBLE else View.GONE
                                            }
                                        }
                                    }

                                }
                            }

                            2 -> (activity as MainActivity).showDialogSessionExpired()
                        }
                    }
                }

                else -> {}
            }
        }

        viewModel.getSimpleWatchlistResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }

                is Resource.Success -> {
                    val res = it.data
                    if (res != null) {
                        when (res.status) {
                            0 -> {
                                binding.apply {
                                    for (userWatchListList in res.userWatchListList) {
                                        val wlCode = userWatchListList.userWatchListGroup.wlCode
                                        val listStockParam = arrayListOf<String>()
                                        userWatchListList.userWatchListItemList.map {
                                            listStockParam.add(it.itemCode)
                                        }
                                        watchlistMap[wlCode] = WatchListCategory(wlCode, stockListString = listStockParam)
                                    }
                                    if (watchlistMap.size != 0) {
                                        for (wlGroup in watchlistMap.values) {
                                            if (!chipWlExist.contains(wlGroup.category) && wlGroup.category != "All") {
                                                val chip = layoutInflater.inflate(
                                                    R.layout.layout_chip_watchlist_home,
                                                    chipGroupWatchlist,
                                                    false
                                                ) as Chip
                                                val drawable = ChipDrawable.createFromAttributes(
                                                    requireContext(),
                                                    null,
                                                    0,
                                                    R.style.chipWatchlistHome
                                                )
                                                chip.text = wlGroup.category
                                                chip.setChipDrawable(drawable)
                                                chip.isCheckable = true
                                                chip.isClickable = true
                                                if (wlGroup.category == "Portfolio") {
                                                    chip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_chip_icon_portfolio)
                                                    chip.isChipIconVisible = true
                                                    val scale = resources.displayMetrics.density
                                                    chip.chipIconSize = 20*scale + 0.5f
                                                    chip.iconStartPadding = 8*scale + 0.5f
                                                }
                                                chip.setEnsureMinTouchTargetSize(false)
                                                chipGroupWatchlist.addView(chip)
                                                chipWlExist.add(wlGroup.category)

                                            }
                                        }
                                    }

                                    if (watchlistMap.containsKey("All")){
                                        groupNoWatchlist.visibility = View.GONE
                                        groupFirstTimeLogin.visibility = View.GONE
                                    } else {
                                        groupNoWatchlist.visibility = View.VISIBLE
                                        groupFirstTimeLogin.visibility = View.VISIBLE
                                    }
                                }
                            }

                            2 -> (activity as MainActivity).showDialogSessionExpired()
                        }
                    }
                }

                else -> {}
            }
        }

        viewModel.getSessionPinResult.observe(viewLifecycleOwner) {
            if (it != null) {
                if (validateSessionPin(it)) {
                    if (isCheckPinForPortfolioWatchlist) {
                        getWatchlistItem(watchlistCategoryState)
                        isCheckPinForPortfolioWatchlist = false
                    } else {
                        viewModel.getSimplePortfolio(userId, sessionId, accNo)
                    }
                } else {
                    showDialogPin()
                }
            } else {
                showDialogPin()
            }
        }

        viewModel.removeItemCategoryResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }

                is Resource.Success -> {
                    val res = it.data
                    if (res != null) {
                        when (res.status) {
                            0 -> {
                                // update stock category that item was delete
                                val listStockParam = arrayListOf<String>()
                                val wlCode = it.data.userWatchListGroup.wlCode
                                val sortItemSeq = it.data.userWatchListItemList.sortedBy { it.itemSeq }
                                sortItemSeq.map { listStockParam.add(it.itemCode) }
                                watchlistMap[wlCode] = WatchListCategory(wlCode, stockListString = listStockParam)

                                // check if the stocks still in another category
                                var isStillInAnothercategory = false
                                watchlistMap.mapValues {
                                    if (it.key != "All" && it.value.stockListString.contains(deletedWatchlistItem)) {
                                        isStillInAnothercategory = true
                                    }
                                }
                                if (!isStillInAnothercategory) {
                                    val listStock = watchlistMap["All"]?.stockListString?.toMutableList()
                                    listStock?.removeIf { it == deletedWatchlistItem }
                                    if (listStock != null) {
                                        watchlistMap["All"] = WatchListCategory("All", stockListString = listStock)
                                    } else {
                                        watchlistMap["All"] = WatchListCategory("All")
                                    }
                                }

                                // update ui
                                if (it.data.userWatchListItemList.size != 0) {
                                    viewModel.getStockDetail(userId, sessionId, wlCode, listStockParam)
                                } else {
                                    watchlistHomeAdapter.clearData()
                                    binding.groupNoWatchlist.visibility = View.VISIBLE
                                    binding.rcvWatchlistHome.visibility = View.GONE
                                }
                            }

                            2 -> (activity as MainActivity).showDialogSessionExpired()
                        }
                    }
                }

                else -> {}
            }
        }

        viewModel.showSessionExpired.observe(viewLifecycleOwner) {
            if (it) {
                (activity as MainActivity).showDialogSessionExpired()
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

        viewModel.getOrderMatchLiveData.observe(viewLifecycleOwner) {
            viewModel.getSimpleWatchlist(userId, sessionId, "Portfolio")
        }

        viewModel.getSubscribeStockWatchlist.observe(viewLifecycleOwner) {item ->
            if (item != null) {
                val itemIndex =  listWatchList.indexOfFirst { it.secCode == item.secCode }
                if (itemIndex != -1) {
                    watchlistHomeAdapter.updateData(itemIndex, item)
                }
            }
        }

        viewModel.getRealtimeStockDetailWatchList.observe(viewLifecycleOwner) {
            binding.apply {
                if (it.isNotEmpty()) {
                    groupWatchlist.visibility = View.VISIBLE
                    rcvWatchlistHome.visibility = View.VISIBLE
                    groupNoWatchlist.visibility = View.GONE
                    lyNoDataPortfolio.visibility = View.GONE
                    groupFirstTimeLogin.visibility = View.GONE

                    rcvWatchlistHome.findItem()
                    if (!rcvWatchlistHome.findItem()) {
                        watchlistHomeAdapter.setData(it as List<TradeSummary>)
                    }
                } else {
                    if (watchlistMap.size != 0) {
                        groupNoWatchlist.visibility = View.VISIBLE
                        rcvWatchlistHome.visibility = View.GONE
                        watchlistHomeAdapter.clearData()
                    } else {
//                    groupWatchlist.visibility = View.GONE
//                    groupNoWatchlist.visibility = View.VISIBLE
                    }
                }
            }
        }

        viewModel.getRealtimeSimplePortfolioResult.observe(viewLifecycleOwner) {portfolio ->
            if (portfolio != null) {
                binding.apply {
                    if (!balanceHidden) {
                        tvTotalPortfolioVal.text = portfolio.portfolio.formatPriceWithoutDecimal()
                        tvTradingBalanceVal.text = portfolio.eqBuyingPower.formatPriceWithoutDecimal()

                        val returnValue = portfolio.unrealizedGainLoss
                        val percent = portfolio.unrealizedGainLossPct * 100
                        setValueUnrealizedGainLoss(returnValue, percent)
                    }
                }

            }
        }
    }

    private fun checkPin() {
        viewModel.getSessionPin(userId)
    }

    private fun showDialogPin() {
        showDialogPin(parentFragmentManager, onSuccess = { isSuccess, isBlocked ->
            if (isSuccess) {
                if (isCheckPinForPortfolioWatchlist) {
                    isCheckPinForPortfolioWatchlist = false
                    getWatchlistItem(watchlistCategoryState)
                } else {
                    viewModel.getSimplePortfolio(userId, sessionId, accNo)
                }
            } else {
                if (isCheckPinForPortfolioWatchlist) {
                    isCheckPinForPortfolioWatchlist = false
                    val chipAll = binding.chipGroupWatchlist.children.first() as Chip
                    chipAll.isChecked = true
                }
                if (isBlocked) {
                    viewModel.getLogout(userId, sessionId)
                }
            }
        })
    }

    private fun getWatchlistItem(wlCode: String) {
        binding.apply {
            if (watchlistMap.containsKey(wlCode)) {
                val listStock = watchlistMap[wlCode]?.stockListString
                if (!listStock.isNullOrEmpty()) {
                    viewModel.getStockDetail(
                        userId,
                        sessionId,
                        wlCode,
                        listStock
                    )
                } else {
                    groupNoWatchlist.visibility = if (wlCode.lowercase() == "portfolio") View.GONE else View.VISIBLE
                    lyNoDataPortfolio.visibility = if (wlCode.lowercase() == "portfolio") View.VISIBLE else View.GONE
                    rcvWatchlistHome.visibility = View.GONE
                    groupFirstTimeLogin.visibility = if (wlCode.lowercase() == "all") View.VISIBLE else View.GONE
                }
            } else {
                groupWatchlist.visibility = View.VISIBLE
                groupNoWatchlist.visibility = if (wlCode.lowercase() == "portfolio") View.GONE else View.VISIBLE
                lyNoDataPortfolio.visibility = if (wlCode.lowercase() == "portfolio") View.VISIBLE else View.GONE
                rcvWatchlistHome.visibility = View.GONE
                groupFirstTimeLogin.visibility = if (wlCode.lowercase() == "all") View.VISIBLE else View.GONE
            }
        }
    }

    private fun navigateToIndexorSector(indiceCode: String, id: Int, isIndex: Boolean) {
        val bundle = Bundle().apply {
            putString(Args.EXTRA_PARAM_STR_ONE, indiceCode)
            putInt(Args.EXTRA_PARAM_INT_ONE, id)
        }
        if (isIndex) {
            findNavController().navigate(R.id.index_detail_fragment, bundle)
        }else {
            findNavController().navigate(R.id.sector_detail_fragment, bundle)
        }
    }

    private fun onClickChipNewInvestor() {
        binding.chipGroupNewInvestment.setOnCheckedStateChangeListener { _, checkedIds ->
            for (chipId in checkedIds) {
                val selectedChip: Chip = binding.chipGroupNewInvestment.findViewById(chipId)
                val selectedChipText = selectedChip.text.toString()
                when (selectedChipText) {
                    "LQ45 Stocks" -> {
                        navigateToIndexorSector("LQ45", 21, true)
                    }
                    "Shariah" -> {
                        navigateToIndexorSector("ISSI", 29, true)
                    }
                    "IDX30" -> {
                        navigateToIndexorSector("IDX30", 30, true)
                    }
                    "Kompas 100" -> {
                        navigateToIndexorSector("KOMPAS 100", 25, true)
                    }
                    "JII" -> {
                        navigateToIndexorSector("JII", 22, true)
                    }
                    "Rising This Week" -> {
                        findNavController().navigate(R.id.categories_fragment)
                    }
                    "Property" -> {
                        navigateToIndexorSector("IDXPROPERT", 62, false)
                    }
                    "Banking & Finance" -> {
                        navigateToIndexorSector("IDXFINANCE", 67, false)
                    }
                }
            }
        }
    }

    private fun showSuccessAuthenticatedDevice() {
        if (isFromAuthDevice) {
            showSnackBarTop(
                requireContext(),
                binding.root,
                "success",
                R.drawable.ic_success,
                "Device has been authenticated",
                "", requireActivity(), NavKeys.KEY_FM_TAB_PORTFOLIO
            )
            isFromAuthDevice = false
        }
    }

    private fun setValueUnrealizedGainLoss(returnValue: Double, percent: Double) {
        binding.apply {
            if (returnValue > 0) {
                tvPercent.text = "(+${percent.formatPercent()}%)"
                tvReturnVal.text = "+Rp${returnValue.formatPriceWithoutDecimal()}"

                tvReturnVal.setTextColor(ContextCompat.getColor(requireContext(), R.color.textUpHeader))
                tvPercent.setTextColor(ContextCompat.getColor(requireContext(), R.color.textUpHeader))
                val icReturnUp = ContextCompat.getDrawable(requireContext(), R.drawable.ic_trend_up)
                binding.tvReturnVal.setCompoundDrawablesRelativeWithIntrinsicBounds(icReturnUp, null, null, null)
            } else if (returnValue < 0) {
                tvPercent.text = "(${percent.formatPercent()}%)"
                tvReturnVal.text = "-Rp${returnValue.formatPriceWithoutDecimalWithoutMinus()}"

                tvReturnVal.setTextColor(ContextCompat.getColor(requireContext(), R.color.textDownHeader))
                tvPercent.setTextColor(ContextCompat.getColor(requireContext(), R.color.textDownHeader))
                val icReturnDown = ContextCompat.getDrawable(requireContext(), R.drawable.ic_trend_down)
                binding.tvReturnVal.setCompoundDrawablesRelativeWithIntrinsicBounds(icReturnDown, null, null, null)
            } else {
                tvPercent.text = "(${percent.formatPercent()}%)"
                tvReturnVal.text = "Rp${returnValue.formatPriceWithoutDecimal()}"

                tvReturnVal.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                binding.tvReturnVal.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
                tvPercent.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
        }
    }

    private fun resetChipWatchlist() {
        binding.apply {
            watchlistHomeAdapter.clearData()
            chipGroupWatchlist.removeAllViews()
            chipWlExist.clear()
            watchlistMap.clear()

            val chip = layoutInflater.inflate(
                R.layout.layout_chip_watchlist_home,
                chipGroupWatchlist,
                false
            ) as Chip
            val drawable = ChipDrawable.createFromAttributes(
                requireContext(), null, 0, R.style.chipWatchlistHome
            )
            chip.text = "All"
            chip.setChipDrawable(drawable)
            chip.isChecked = true
            chip.isCheckable = true
            chip.isClickable = true
            chip.setEnsureMinTouchTargetSize(false)
            chipGroupWatchlist.addView(chip)
            chipWlExist.add("All")
        }
    }

    override fun onClickStrInt(valueStr: String?, valueInt: Int?) {
        // value int: 1 = itemview, 2 = btn buy
        if (valueStr != null) {
            when (valueInt) {
                1 -> {
                    MiddleActivity.startIntentParam(
                        requireActivity(),
                        NavKeys.KEY_FM_STOCK_DETAIL,
                        valueStr.toString(),
                        ""
                    )
                }

                2 -> {
                    MiddleActivity.startIntentParam(
                        requireActivity(),
                        NavKeys.KEY_FM_ORDER,
                        valueStr,
                        0
                    ) // 0 for buy, 1 for sell
                }

                3 -> {

                }
            }
        }
    }

    // on swipe delete/sell click
    override fun onClickAnyInt(valueAny: Any?, valueInt: Int) {
        valueAny as String
        if (watchlistCategoryState.lowercase() == "portfolio") {

                MiddleActivity.startIntentParam(
                    requireActivity(),
                    NavKeys.KEY_FM_ORDER,
                    valueAny,
                    1
                ) // 0 for buy, 1 for sell
        } else {
            if (!valueAny.equals("") && valueInt != 0) {
                deletedWatchlistItem = valueAny
                viewModel.removeItemWatchlist(
                    userId,
                    watchlistCategoryState,
                    valueAny,
                    valueInt,
                    sessionId
                )
            }
        }
    }
}