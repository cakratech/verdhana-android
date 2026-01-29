package com.bcasekuritas.mybest.app.feature.portfolio.taborders

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.data.layout.UIDialogPortfolioOrderModel
import com.bcasekuritas.mybest.app.domain.dto.request.WithdrawOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.response.GroupKeyTradeInfo
import com.bcasekuritas.mybest.app.domain.dto.response.OrderSuccessSnackBar
import com.bcasekuritas.mybest.app.domain.dto.response.PortfolioOrderItem
import com.bcasekuritas.mybest.app.domain.dto.response.TradeListInfo
import com.bcasekuritas.mybest.app.feature.activity.main.MainViewModel
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.portfolio.PortfolioShareViewModel
import com.bcasekuritas.mybest.app.feature.portfolio.taborders.adapter.PortfolioOrderTabAdapter
import com.bcasekuritas.mybest.app.feature.portfolio.taborders.adapter.TradeListAdapter
import com.bcasekuritas.mybest.databinding.FragmentTabOrdersPortfolioBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.RabbitMQForegroundService
import com.bcasekuritas.mybest.ext.common.getIpAddress
import com.bcasekuritas.mybest.ext.common.getRandomString
import com.bcasekuritas.mybest.ext.common.validateSessionPin
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarInterface
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.mybest.ext.listener.OnClickAnyInt
import com.bcasekuritas.rabbitmq.proto.bcas.TradeInfo
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import timber.log.Timber

@FragmentScoped
@AndroidEntryPoint
class OrdersTabPortfolioFragment : BaseFragment<FragmentTabOrdersPortfolioBinding,
        OrdersTabPortfolioViewModel>(), ShowDialog by ShowDialogImpl(), OnClickAnyInt, OnClickAny,
    ShowSnackBarInterface by ShowSnackBarImpl() {
    override val viewModel: OrdersTabPortfolioViewModel by viewModels()
    override val binding: FragmentTabOrdersPortfolioBinding by autoCleaned {
        (FragmentTabOrdersPortfolioBinding.inflate(layoutInflater))
    }
    override val bindingVariable: Int = BR.vmTabOrders

    private val activeOrderAdapter: PortfolioOrderTabAdapter by autoCleaned {
        PortfolioOrderTabAdapter(
            requireContext(),
            this,
            this
        )
    }

    private val tradeListAdapter:  TradeListAdapter by autoCleaned { TradeListAdapter(requireContext(), this) }

    private var userId = ""
    private var accNo = ""
    private var sessionId = ""
    private var buySell = ""
    private var isWithdrawGtc = false
    private var advOrderId = ""
    private var isWithdrawGtcStatusPending = false

    private var currentTab = 1

    lateinit var sharedViewModel: PortfolioShareViewModel
    lateinit var mainViewModel: MainViewModel
    var filter = UIDialogPortfolioOrderModel()
    private var stockCodeWithdraw = ""
    private var orderId = ""

    private var stockCodeList = arrayListOf<String>()
    private val listOrder = arrayListOf<PortfolioOrderItem>()
    private val listTrade = arrayListOf<TradeListInfo>()
    private var amendGtc = PortfolioOrderItem()

    private var isWithdrawSuccess = false
    private var ipAddress = ""

    companion object {
        fun newInstance() = OrdersTabPortfolioFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(PortfolioShareViewModel::class.java)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun initAPI() {
        super.initAPI()
        viewModel.getAllStockParam("")

        ipAddress = getIpAddress(requireContext())
        viewModel.getIpAddress()
    }

    override fun onResume() {
        super.onResume()
        userId = prefManager.userId
        accNo = prefManager.accno
        sessionId = prefManager.sessionId

        filter = UIDialogPortfolioOrderModel("","", "")
        when (currentTab) {
            1 -> {
                viewModel.getOrderList(userId, accNo, sessionId, 0)
                viewModel.getAdvanceOrderList(userId, accNo)
            }
            2 -> {
                viewModel.getTradeList(userId, accNo, sessionId)
            }
        }
    }

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvActiveOrders.apply {
            adapter = activeOrderAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        binding.rcvTradeList.apply {
            adapter = tradeListAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        chipStateChange()

        binding.swipeRefresh.setOnRefreshListener {
            filter = UIDialogPortfolioOrderModel("","", "")
            when (currentTab) {
                1 -> {
                    viewModel.getOrderList(userId, accNo, sessionId, 0)
                    viewModel.getAdvanceOrderList(userId, accNo)
                    setEmptyData(true)
                }
                2 -> {
                    viewModel.getTradeList(userId, accNo, sessionId)
                    setEmptyData(false)
                }
                else -> {
                    binding.swipeRefresh.isRefreshing = false
                }
            }

        }
    }

    fun setEmptyData(isOrderList: Boolean){
        if (isOrderList){
            binding.tvNoData.text = "There is no active order"
            binding.tvDescNoData.text = "Choose a stock to make order, automatic order, or fast order"
        } else {
            binding.tvNoData.text = "There is no trade list"
            binding.tvDescNoData.text = "Today's completed orders will be shown here"

        }
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            btnFilter.setOnClickListener {
                val sortStockList = stockCodeList.sorted()
                showDialogFilterPortfolioOrderBottom(filter, sortStockList, childFragmentManager, currentTab)
            }

            cardStopLossTakeProfit.setOnClickListener {
                MiddleActivity.startIntent(requireActivity(), NavKeys.KEY_FM_STOP_LOSS_TAKE_PROFIT)
            }

            tvDiscoverStocks.setOnClickListener {
                findNavController().navigate(R.id.discover_fragment)
            }

            tvDiscoverStocksFilter.setOnClickListener {
                findNavController().navigate(R.id.discover_fragment)
            }
        }
    }

    private fun chipStateChange() {
        binding.apply {
            chipOrders.setOnCheckedStateChangeListener { _, checkedIds ->
                filter = UIDialogPortfolioOrderModel("","", "")
                for (chipId in checkedIds) {
                    val selectedChip = chipOrders.findViewById(chipId) as Chip
                    when (selectedChip.text.toString()) {
                        "Active Orders" -> {
                            currentTab = 1
                            cardStopLossTakeProfit.visibility = View.VISIBLE
                            lyPortfolioEmpty.visibility = View.GONE
                            rcvTradeList.visibility = View.GONE
                            rcvActiveOrders.visibility = View.VISIBLE
                            viewModel.getOrderList(userId, accNo, sessionId, 0)
                            viewModel.getAdvanceOrderList(userId, accNo)
                        }
                        "Trade List" -> {
                            cardStopLossTakeProfit.visibility = View.GONE
                            binding.lyPortfolioEmpty.visibility = View.GONE
                            rcvActiveOrders.visibility = View.GONE
                            rcvTradeList.visibility = View.VISIBLE
                            viewModel.getTradeList(userId, accNo, sessionId)
                            currentTab = 2
                        }
                    }
                }
            }
        }
    }

    override fun setupListener() {
        super.setupListener()
        childFragmentManager.setFragmentResultListener(
            NavKeys.KEY_FM_TAB_PORTFOLIO_ORDER,
            viewLifecycleOwner
        ) { _, result ->
            //filter dialog
            val confirmResultFilter = result.getString(NavKeys.CONST_RES_TAB_PORTFOLIO_ORDER_FILTER)
            if (confirmResultFilter == "RESULT_OK") {
                filter = UIDialogPortfolioOrderModel(
                    result.getString("type")!!,
                    result.getString("status")!!,
                    result.getString("stockCode")!!
                )

                setupFilter(filter, listOrder, listTrade)
            }

            // withdraw dialog
            val confirmResultWithdraw = result.getString(NavKeys.CONST_RES_WITHDRAW_ORDER_CONFIRM)
            if (confirmResultWithdraw == "RESULT_OK") {
                if (result.getBoolean("confirm")) {

                    if (isWithdrawGtc) {
                        sendWithdrawGtc(advOrderId)
                    } else {
                        sendWithdraw(orderId)
                    }
                    sharedViewModel.setWithdrawSuccess(
                        OrderSuccessSnackBar(
                            true,
                            buySell = buySell,
                            stockCode = stockCodeWithdraw
                        )
                    )
                    Handler(Looper.getMainLooper()).postDelayed({
                        viewModel.getOrderList(prefManager.userId, prefManager.accno, prefManager.sessionId, 0)
                        viewModel.getAdvanceOrderList(userId, accNo)
                    }, 500)

                    isWithdrawSuccess = true
                }
            }

            // withdraw GTC dialog
            val confirmResultWithdrawGtc = result.getString(NavKeys.CONST_RES_WITHDRAW_ORDER_GTC_CONFIRM)
            val withdrawSelection = result.getInt("selection")
            if (confirmResultWithdrawGtc == "RESULT_OK") {
                if (result.getBoolean("confirm")) {
                    if (withdrawSelection == 0) {
                        sendWithdraw(orderId)
                    } else {
                        sendWithdrawGtc(advOrderId)
                    }
                    sharedViewModel.setWithdrawSuccess(
                        OrderSuccessSnackBar(
                            true,
                            buySell = buySell,
                            stockCode = stockCodeWithdraw
                        )
                    )
                    Handler(Looper.getMainLooper()).postDelayed({
                        viewModel.getOrderList(prefManager.userId, prefManager.accno, prefManager.sessionId, 0)
                        viewModel.getAdvanceOrderList(userId, accNo)
                    }, 2000)

                    isWithdrawSuccess = true
                }
            }

            // amend GTC dialog
            val confirmResultAmendGtc = result.getString(NavKeys.CONST_RES_AMEND_ORDER_GTC_CONFIRM)
            if (confirmResultAmendGtc == "RESULT_OK") {
                if (result.getBoolean("confirm")) {
                    MiddleActivity.startIntentParam(
                        requireActivity(),
                        NavKeys.KEY_FM_ORDER,
                        amendGtc,
                        amendGtc.buySell
                    )
                }
            }
        }
    }

    private fun setupFilter(filter: UIDialogPortfolioOrderModel, listOrder: List<PortfolioOrderItem>, listTrade: List<TradeListInfo>) {
        val filterStock = filter.stockCode
        val filterType = filter.type
        val filterStatus = filter.status

        when (currentTab) {
            1 -> {
                if (listOrder.isNotEmpty()) {
                    val listByStock = if (filterStock == "") listOrder else listOrder.filter { it.stockCode == filterStock }
                    val listByType = if (filterType == "all" || filterType == "") listByStock else listByStock.filter { it.buySell == filterType }
                    val listByStatus = if (filterStatus == "all" || filterStatus == "") listByType else listByType.filter { it.status == filterStatus }

                    listByStatus.sortedByDescending { it.time ?: Double.MIN_VALUE }
                    if (listByStatus.isNotEmpty()) {
                        activeOrderAdapter.setData(listByStatus)
                        binding.lyFilterNotFound.visibility = View.GONE
                    } else {
                        activeOrderAdapter.clearData()
                        binding.lyFilterNotFound.visibility = View.VISIBLE
                    }
                } else {
                    binding.lyFilterNotFound.visibility = View.VISIBLE
                    binding.lyPortfolioEmpty.visibility = View.GONE
                }
            }
            2 -> {
                if (listTrade.isNotEmpty()) {
                    val listByStock = if (filterStock == "") listTrade else listTrade.filter { it.stockCode == filterStock }
                    val listByType = if (filterType == "all" || filterType == "") listByStock else listByStock.filter { it.buySell == filterType }

                    listByType.sortedByDescending { it.time ?: Double.MIN_VALUE }
                    if (listByType.isNotEmpty()) {
                        tradeListAdapter.setData(listByType)
                        binding.lyFilterNotFound.visibility = View.GONE
                    } else {
                        tradeListAdapter.cleardata()
                        binding.lyFilterNotFound.visibility = View.VISIBLE
                    }
                } else {
                    binding.lyFilterNotFound.visibility = View.VISIBLE
                    binding.lyPortfolioEmpty.visibility = View.GONE
                }
            }
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.imqConnectionListener.connListenerLiveData.observe(this) { value ->
            when (value) {
                "Recovered" -> {
                    viewModel.imqConnectionListener.onListener("")
                    when (currentTab) {
                        1 -> {
                            setEmptyData(true)
                        }
                        2 -> {
                            setEmptyData(false)
                        }
                    }
                }

                else -> {}
            }
        }

        viewModel.getIpAddressResult.observe(viewLifecycleOwner) {value ->
            ipAddress = value.ifEmpty { getIpAddress(requireContext()) }
        }

        viewModel.getOrderListResults.observe(viewLifecycleOwner) { result ->
            result?.let {
                try {
                    if (it.isNotEmpty()) {
                        val listItem = it.sortedByDescending { item -> item.time ?: Double.MIN_VALUE }
                        listOrder.clear()
                        listOrder.addAll(listItem)
                        activeOrderAdapter.setData(listItem)
                        binding.lyPortfolioEmpty.visibility = View.GONE
                        binding.lyFilterNotFound.visibility = View.GONE
                    } else {
                        binding.lyFilterNotFound.visibility = View.GONE
                        binding.tvNoData.text = "There is no active order"
                        binding.tvDescNoData.text = "Choose a stock to make order, automatic order, or fast order"
                        binding.lyPortfolioEmpty.visibility = View.VISIBLE
                    }
                } catch (ignore: Exception) {}
            }
            binding.swipeRefresh.isRefreshing = false
        }

        viewModel.isOrderListEmpty.observe(viewLifecycleOwner) {
            activeOrderAdapter.clearData()
            binding.lyPortfolioEmpty.visibility = if (it) View.VISIBLE else View.GONE
            binding.tvNoData.text = "There is no active order"
            binding.tvDescNoData.text = "Choose a stock to make order, automatic order, or fast order"
            binding.lyFilterNotFound.visibility = View.GONE
            binding.swipeRefresh.isRefreshing = false
        }

        viewModel.getTradeListResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                try {
                    if (it.isNotEmpty()) {
                        val mergeTradeList = mergedTradeList(it)
                        val sortedItem = mergeTradeList.sortedByDescending { item -> item.time ?: Double.MIN_VALUE }

                        listTrade.clear()
                        listTrade.addAll(sortedItem)
                        tradeListAdapter.setData(sortedItem)
                        binding.lyPortfolioEmpty.visibility = View.GONE
                        binding.lyFilterNotFound.visibility = View.GONE
                    } else {
                        tradeListAdapter.cleardata()
                        binding.lyFilterNotFound.visibility = View.GONE
                        binding.tvNoData.text = "There is no trade list"
                        binding.tvDescNoData.text = "Today's completed orders will be shown here"
                        binding.lyPortfolioEmpty.visibility = View.VISIBLE
                    }
                } catch (ignore: Exception) {}
            }
            binding.swipeRefresh.isRefreshing = false
        }

        mainViewModel.orderListData.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.getOrderList(prefManager.userId, prefManager.accno, prefManager.sessionId, 0)
                viewModel.getAdvanceOrderList(userId, accNo)
            }
        }

        viewModel.getSessionPinWithdrawResult.observe(viewLifecycleOwner) {
            val sessionPin = it
            if (sessionPin != null) {
                if (validateSessionPin(sessionPin)) {
                    if (!isWithdrawSuccess) {
                        if (isWithdrawGtc && !isWithdrawGtcStatusPending) {
                            showDialogWithdrawGTCOrderBottom(NavKeys.KEY_FM_TAB_PORTFOLIO_ORDER, childFragmentManager)
                        } else {
                            showDialogWithdrawOrderBottom(
                                NavKeys.KEY_FM_TAB_PORTFOLIO_ORDER,
                                childFragmentManager
                            )
                        }

                    }
                } else {
                    showDialogPin()
                }

                viewModel.clearSessionPin()
            }
        }

        viewModel.getAdvanceOrderListResult.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                val totalActive = it.filter { it.status == "PB" || it.status == "B2" || it.status == "O" || it.status == "O2" }
                if (totalActive.isNotEmpty()) {
                    binding.tvTotalAdvItem.visibility = View.VISIBLE
                    binding.tvTotalAdvItem.text = "You have ${totalActive.size} active condition(s)."
                } else {
                    binding.tvTotalAdvItem.visibility = View.GONE
                }
            } else {
                binding.tvTotalAdvItem.visibility = View.GONE
            }
        }

        viewModel.getAllStockParamResult.observe(viewLifecycleOwner) {
            if (stockCodeList.size != 0) {
                stockCodeList.clear()
            }
            it?.map { data -> data?.let { stockCodeList.add(data.stockCode) } }

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

        sharedViewModel.isPinSuccess.observe(viewLifecycleOwner) {isSuccess ->
            if (isSuccess) {
                filter = UIDialogPortfolioOrderModel("","", "")
                when (currentTab) {
                    1 -> {
                        viewModel.getOrderList(prefManager.userId, prefManager.accno, prefManager.sessionId, 0)
                        viewModel.getAdvanceOrderList(userId, accNo)
                        setEmptyData(true)
                    }
                    2 -> {
                        viewModel.getTradeList(userId, accNo, sessionId)
                        setEmptyData(false)
                    }
                    else -> {
                        binding.swipeRefresh.isRefreshing = false
                    }
                }
            }
        }
    }

    private fun showDialogPin() {
        showDialogPin(parentFragmentManager, onSuccess = {isSuccess, isBlocked ->
            if (isSuccess) {
                if (!isWithdrawSuccess) {
                    if (isWithdrawGtc && !isWithdrawGtcStatusPending) {
                        showDialogWithdrawGTCOrderBottom(NavKeys.KEY_FM_TAB_PORTFOLIO_ORDER, childFragmentManager)
                    } else {
                        showDialogWithdrawOrderBottom(
                            NavKeys.KEY_FM_TAB_PORTFOLIO_ORDER,
                            childFragmentManager
                        )
                    }
                }
            } else {
                if (isBlocked) {
                    viewModel.getLogout(prefManager.userId, prefManager.sessionId)
                }
            }
        })
    }

    override fun onClickAnyInt(valueAny: Any?, valueInt: Int) {
        if (valueAny is PortfolioOrderItem) {
            stockCodeWithdraw = valueAny.stockCode
            if (valueAny.status == "O" || valueAny.status == "PT"  || valueAny.status == "P" || valueAny.status == "PB") {

                //valueint: 1 = withdraw, 2 = amend
                when (valueInt) {
                    1 -> {
                        isWithdrawSuccess = false

                        orderId = valueAny.orderId
                        advOrderId = valueAny.advOrderId
                        buySell = if(valueAny.buySell == "B") "buy" else "sell"

                        isWithdrawGtc = valueAny.isGtOrder
                        isWithdrawGtcStatusPending = checkIsStatusPending(valueAny.status)

                        viewModel.getSessionPin(prefManager.userId)
                    }

                    2 -> {
                        if (valueAny.isGtOrder) {
                            if (!prefManager.isAmendGtc) {
                                showDialogAmendGtcOrderBottom(NavKeys.KEY_FM_TAB_PORTFOLIO_ORDER, childFragmentManager)
                                amendGtc = valueAny
                            } else {
                                MiddleActivity.startIntentParam(
                                    requireActivity(),
                                    NavKeys.KEY_FM_ORDER,
                                    valueAny,
                                    valueAny.buySell
                                )
                            }
                        } else {
                            MiddleActivity.startIntentParam(
                                requireActivity(),
                                NavKeys.KEY_FM_ORDER,
                                valueAny,
                                valueAny.buySell
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onClickAny(valueAny: Any?) {
        when{
            valueAny is PortfolioOrderItem -> {

                val bundle = Bundle().apply {
                    putParcelable(Args.EXTRA_PARAM_OBJECT, valueAny)
                }

                findNavController().navigate(R.id.order_detail_fragment, bundle)
            }
            valueAny is TradeListInfo -> {
                val value = PortfolioOrderItem(
                    orderId = valueAny.orderId,
                    time = valueAny.time,
                    status = "M",
                    buySell = valueAny.buySell,
                    orderType = valueAny.buySell,
                    idxBoard = valueAny.idxBoard,
                    timeInForce = "0",
                    stockCode = valueAny.stockCode,
                    price = valueAny.price,
                    orderQty = valueAny.orderQty,
                    matchQty = valueAny.orderQty,
                    ordValue = valueAny.orderQty.times(valueAny.price),
                    mValue = valueAny.orderQty.times(valueAny.price),
                    idxOrderId = valueAny.idxOrderId,
                    fee = valueAny.fee
                )

                val bundle = Bundle().apply {
                    putParcelable(Args.EXTRA_PARAM_OBJECT, value)
                }

                findNavController().navigate(R.id.order_detail_fragment, bundle)
            }
        }
    }

    private fun sendWithdraw(orderId: String) {
        val accNo = prefManager.accno
        val userId = prefManager.userId
        val sessionId = prefManager.sessionId

        val sendWithdraw = WithdrawOrderRequest(
            newCliOrderRef = getRandomString(),
            oldCliOrderRef = orderId,
            orderID = orderId,
            inputBy = userId,
            sessionId = sessionId,
            ip = ipAddress,
            accNo = accNo
        )

        viewModel.sendWithdraw(sendWithdraw)
    }

    private fun sendWithdrawGtc(orderId: String) {
        val accNo = prefManager.accno
        val userId = prefManager.userId
        val sessionId = prefManager.sessionId

        val sendWithdraw = WithdrawOrderRequest(
            oldCliOrderRef = orderId,
            orderID = orderId,
            inputBy = userId,
            sessionId = sessionId,
            accNo = accNo
        )

        viewModel.sendWithdrawGtc(sendWithdraw)
    }

    private fun checkIsStatusPending(status: String): Boolean {
        return when {
            status == "PN" || status == "PA" || status == "PS" || status == "PJ" || status == "PT" || status == "PB" -> true
            else -> false
        }
    }

    private fun mergedTradeList(data: List<TradeInfo>): List<TradeListInfo> {
        return data
            .groupBy { GroupKeyTradeInfo(it.exordid, it.stockcode, it.mprice, it.bs) }
            .map { (_, group) ->
                if (group.size == 1) {
                    val item = group.first()

                    TradeListInfo(
                        orderId = item.odId,
                        time = item.mtime,
                        status = "M",
                        buySell = item.bs,
                        orderType = item.bs,
                        idxBoard = item.boardcode,
                        timeInForce = "0",
                        stockCode = item.stockcode,
                        price = item.mprice,
                        orderQty = item.mqty,
                        matchQty = item.mqty,
                        ordValue = item.mqty.times(item.mprice),
                        mValue = item.mqty.times(item.mprice),
                        idxOrderId = item.exordid,
                        fee = item.fee
                    )
                } else {
                    val first = group.first()
                    val time = group.sortedByDescending { it.mtime ?: Double.MIN_VALUE  }[0]
                    TradeListInfo(
                        orderId = "",
                        time = time.mtime,
                        status = "M",
                        buySell = first.bs,
                        orderType = first.bs,
                        idxBoard = "",
                        timeInForce = "0",
                        stockCode = first.stockcode,
                        price = first.mprice,
                        orderQty = group.sumOf { it.mqty },
                        listTradeInfo = group.map {item ->
                            TradeListInfo(
                                orderId = item.odId,
                                time = item.mtime,
                                status = "M",
                                buySell = item.bs,
                                orderType = item.bs,
                                idxBoard = item.boardcode,
                                timeInForce = "0",
                                stockCode = item.stockcode,
                                price = item.mprice,
                                orderQty = item.mqty,
                                matchQty = item.mqty,
                                ordValue = item.mqty.times(item.mprice),
                                mValue = item.mqty.times(item.mprice),
                                idxOrderId = item.exordid,
                                fee = item.fee
                            )
                        }
                    )
                }
            }
    }

}