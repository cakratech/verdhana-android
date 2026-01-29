package com.bcasekuritas.mybest.app.feature.order

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.data.layout.UIDialogOrderConfirmationModel
import com.bcasekuritas.mybest.app.data.layout.UIDialogSellModel
import com.bcasekuritas.mybest.app.domain.dto.request.AdvanceOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.AdvancedCriteriaRequest
import com.bcasekuritas.mybest.app.domain.dto.request.AmendOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.AutoOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.OrderAdapterData
import com.bcasekuritas.mybest.app.domain.dto.request.SendOrderReq
import com.bcasekuritas.mybest.app.domain.dto.request.SliceOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.response.BuyOrderBookRes
import com.bcasekuritas.mybest.app.domain.dto.response.OrderSuccessSnackBar
import com.bcasekuritas.mybest.app.domain.dto.response.PortfolioOrderItem
import com.bcasekuritas.mybest.app.domain.dto.response.SellOrderBookRes
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.activity.main.MainActivity
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.order.adapter.OrderViewPagerAdapter
import com.bcasekuritas.mybest.app.feature.stockdetail.adapter.OrderBookBuyAdapter
import com.bcasekuritas.mybest.app.feature.stockdetail.adapter.OrderBookSellAdapter
import com.bcasekuritas.mybest.databinding.FragmentOrderBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.RabbitMQForegroundService
import com.bcasekuritas.mybest.ext.common.generateTextImageProfile
import com.bcasekuritas.mybest.ext.common.getIpAddress
import com.bcasekuritas.mybest.ext.common.getRandomString
import com.bcasekuritas.mybest.ext.common.initFormatThousandSeparator
import com.bcasekuritas.mybest.ext.common.initPercentFormatNumber
import com.bcasekuritas.mybest.ext.common.validateSessionPin
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.converter.GET_4_CHAR_STOCK_CODE
import com.bcasekuritas.mybest.ext.converter.GET_COMPARE_VALUE
import com.bcasekuritas.mybest.ext.converter.GET_IDX_BOARD
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarInterface
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimalWithoutMinus
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import timber.log.Timber
import java.math.BigDecimal
import java.util.Date

@FragmentScoped
@AndroidEntryPoint
class OrderFragment : BaseFragment<FragmentOrderBinding, OrderViewModel>(), OnClickStr,
    ShowSnackBarInterface by ShowSnackBarImpl(), ShowDropDown by ShowDropDownImpl() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmOrder
    override val viewModel: OrderViewModel by viewModels()
    override val binding: FragmentOrderBinding by autoCleaned {
        (FragmentOrderBinding.inflate(layoutInflater))
    }

    private lateinit var sharedViewModel: OrderSharedViewModel
    private var sessionPin: Long? = null
    private var userId = ""
    private var sessionId = ""

    val tabOrder = arrayOf(
        "Buy",
        "Sell"
    )

    private val orderBookBuyAdapter: OrderBookBuyAdapter by autoCleaned {
        OrderBookBuyAdapter(
            this,
            requireContext()
        )
    }
    private val orderBookSellAdapter: OrderBookSellAdapter by autoCleaned {
        OrderBookSellAdapter(
            this
        )
    }
    private var buySellInt = 0
    private var stockCode = ""
    private var stockName = ""
    private var buyPrice = 0.0
    private var amendBuyPrice = 0.0
    private var totalPrice = 0.0
    private var lotQty = 0.0
    private var amendLotQty = 0.0
    private var buySell = ""
    private var orderId = ""
    private var orderType = "0"
    private var isAmend = false
    private var isSliceOrder = false
    private var isAdvOrder = false
    private var isAutoOder = false
    private var slicingType = 0
    private var splitNumber = 0
    private var blockSize = 0
    private var compareType = "0"
    private var boardType = "RG"
    private var commfee = 0.0
    private var totalWithFee = 0.0
    private var timeInForce = "0"
    private var orderPeriod = 0L
    private var proceedAmount = 0.0
    private var buyComFee = 0.0
    private var sellComFee = 0.0
    private var endTimeSlice = 0L
    private var isAmendPartial = false
    private lateinit var amendData: PortfolioOrderItem
    private var stockCodeList = arrayListOf<String>()
    private var bracketCriteria = AdvancedCriteriaRequest()
    private var takeProfitCriteria = AdvancedCriteriaRequest()
    private var stopLossCriteria = AdvancedCriteriaRequest()
    private var isNotation = false
    private var isMarketOpen = false
    private var isAmendGtc = false
    private var isMarketOrder = false
    private var marketOrderType = ""
    private var isMarketOpenForMarketOrder = false
    private var channelForFee = 0

    private var ipAddress = ""

    // for validate pin
    private var isFromButtonBuy = false

    private lateinit var orderBookLayoutListener: ViewTreeObserver.OnGlobalLayoutListener
    private var isOrderBookListenerRemoved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(OrderSharedViewModel::class.java)
    }

    override fun setupArguments() {
        super.setupArguments()

        arguments?.let {
            val data = it.getParcelable(Args.EXTRA_PARAM_OBJECT) ?: PortfolioOrderItem()

            buySellInt = it.getInt(Args.EXTRA_PARAM_INT_ONE)
            buySell = if (buySellInt == 0) "B" else "S"
            stockCode = it.getString(Args.EXTRA_PARAM_STR_ONE) ?: ""

            if (data.buySell != "") {
                buySellInt = if (data.buySell == "B") 0 else 1
            }

            if (stockCode == "") {
                amendData = it.getParcelable(Args.EXTRA_PARAM_OBJECT) ?: PortfolioOrderItem()
                amendData.let { amendOrder ->
                    stockCode = amendOrder.stockCode
                    lotQty = amendOrder.orderQty
                    orderId = amendOrder.orderId
                    orderType = amendOrder.orderType
                    isAmend = amendOrder.isAmend
                    buySell = amendOrder.buySell
                    buyPrice = amendOrder.price
                    totalPrice = buyPrice.times(lotQty.times(100))
                    amendBuyPrice = amendOrder.price
                    amendLotQty = amendOrder.orderQty
                    isAmendGtc = amendOrder.isGtOrder
                    val isMarketOrder = amendOrder.orderType == "5"
                    timeInForce = if (isMarketOrder) amendOrder.timeInForce else "0"
                    channelForFee = amendOrder.channelForFee

//                    binding.groupOrderBuyTotal.visibility =
//                        if (totalPrice.toInt() == 0) View.GONE else View.VISIBLE

                    if (amendData.isAmend) {
                        binding.tvOrderBuySahamInfoCode.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            null,
                            null,
                            null,
                            null
                        )
                    }

                    if (amendOrder.status == "P") {
                        isAmendPartial = true
                    }

                    sharedViewModel.setData(
                        OrderAdapterData(
                            stockCode = stockCode,
                            amendPrice = buyPrice,
                            lot = lotQty,
                            totalPrice = totalPrice,
                            buySell = buySell,
                            timeInForce = timeInForce,
                            isAmendPartial = isAmendPartial,
                            relId = orderId,
                            isAmendMarketOrder = isMarketOrder
                        )
                    )
                }
            } else {
                sharedViewModel.setData(OrderAdapterData(stockCode))
            }
            setIconStock(stockCode)
        }
    }

    private fun setIconStock(stockCode: String) {
        val stock = GET_4_CHAR_STOCK_CODE(stockCode)
        val url = prefManager.urlIcon + stock

        Glide.with(requireActivity())
            .load(url)
            .override(300, 300)
            .circleCrop()
            .placeholder(R.drawable.bg_circle)
            .error(R.drawable.bg_circle)
            .into(binding.ivOrderBuySahamInfo)
    }

    override fun setupObserver() {
        super.setupObserver()
        orderBookLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (orderBookBuyAdapter.itemCount > 0 || orderBookSellAdapter.itemCount > 0) {
                    removeOrderBookLayoutListener()
                    val heightOrderBook = binding.viewPager2.top
                    binding.scrollView.smoothScrollTo(0, heightOrderBook)
                }
            }
        }

        binding.lyTableOrderbook.viewTreeObserver.addOnGlobalLayoutListener(orderBookLayoutListener) // for get height of order book

        viewModel.imqConnectionListener.connListenerLiveData.observe(this) { value ->
            when (value) {
                "Recovered" -> {
                    viewModel.imqConnectionListener.onListener("")
                    viewModel.getSessionPin(userId)
                }

                else -> {}
            }
        }

        viewModel.getOrderBookSum.observe(viewLifecycleOwner) {sum ->
            if (sum != null) {
                binding.tvTotalBid.text = sum.totalBid.formatPriceWithoutDecimal()
                binding.tvTotalOffer.text = sum.totalOffer.formatPriceWithoutDecimal()
            }
        }

        viewModel.getStockOrderbookResult.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                binding.apply {
                    val buyOrderbook = mutableListOf<BuyOrderBookRes>()
                    val sellOrderbook = mutableListOf<SellOrderBookRes>()
                    var totBidQtyL = it[0]?.totalBidQuantityL?.toDouble() ?: 0.0
                    var totOfferQtyL = it[0]?.totalOfferQuantityL?.toDouble() ?: 0.0
                    val prevPrice = it[0]?.close ?: 0.0

                    if (it[0]!!.buyOrderBookList != null && it[0]!!.buyOrderBookList.isNotEmpty()) {
                        it[0]!!.buyOrderBookList.take(10).forEachIndexed { i, item ->
                            val itemQtyL = item.quantityL
                            val buyProgress = totBidQtyL / itemQtyL

                            val buyOrderBookItem = BuyOrderBookRes(
                                id = i,
                                price = item.price,
                                quantity = item.quantity.toDouble(),
                                quantityL = BigDecimal(item.quantityL),
                                totQuantityL = it[0]!!.totalBidQtyM.toInt(),
                                progress = buyProgress.toInt(),
                                prevPrice =prevPrice
                            )
                            buyOrderbook.add(buyOrderBookItem)
                        }
                    }

                    if (it[0]!!.sellOrderBookList != null && it[0]!!.sellOrderBookList.isNotEmpty()) {
                        it[0]!!.sellOrderBookList.take(10).forEachIndexed { i, item ->
                            val itemQtyL = item.quantityL
                            val sellProgress = totOfferQtyL / itemQtyL

                            val sellOrderBookItem = SellOrderBookRes(
                                id = i,
                                price = item.price,
                                quantity = item.quantity.toDouble(),
                                quantityL = BigDecimal(item.quantityL),
                                totQuantityL = it[0]!!.totalOfferQtyM.toInt(),
                                progress = sellProgress.toInt(),
                                prevPrice =prevPrice
                            )
                            sellOrderbook.add(sellOrderBookItem)
                        }
                    }

                    orderBookBuyAdapter.setData(buyOrderbook)
                    orderBookSellAdapter.setData(sellOrderbook)
                }
            }
        }

        viewModel.getStockParamResult.observe(viewLifecycleOwner) {
            it?.let { item ->
                stockName = it.stockName
                binding.tvOrderBuySahamInfoCode.text = it.stockCode
                binding.tvOrderBuySahamInfoName.text = stockName
                binding.tvInfoHaircut.text = "Haircut ${it.hairCut?.formatPriceWithoutDecimal()}%"
                binding.tvInfoSpecialNotesAcceleration.text = it.idxTrdBoard.GET_IDX_BOARD()
                binding.tvInfoSpecialNotesAcceleration.visibility = if (binding.tvInfoSpecialNotesAcceleration.text == "") View.GONE else View.VISIBLE

            }
        }

        viewModel.getSubscribeTradeSummary.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.apply {
                    val price = if (it.last != 0.0) it.last else it.close
                    tvOrderBuySahamPrice.text = initFormatThousandSeparator(price)
                    tvOrderBuySahamPercent.text = "${it.change.formatPriceWithoutDecimal()} (${initPercentFormatNumber(it.changePct)})"

                    if (it.change > 0) {
                        tvOrderBuySahamPercent.text = "+${it.change.formatPriceWithoutDecimal()} (+${initPercentFormatNumber(it.changePct)})"
                        tvOrderBuySahamPercent.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.textLightGreen
                            )
                        )
                    } else if (it.change < 0) {
                        tvOrderBuySahamPercent.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.textDownHeader
                            )
                        )
                    } else {
                        tvOrderBuySahamPercent.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.white
                            )
                        )
                    }
                }
            }
        }

        viewModel.getStockDetailResult.observe(viewLifecycleOwner) {
            it?.let {
                binding.apply {
                    val price = if (it.last != 0.0) it.last else it.close
                    tvOrderBuySahamPrice.text = initFormatThousandSeparator(price)
                    tvOrderBuySahamPercent.text = "${it.change.formatPriceWithoutDecimal()} (${initPercentFormatNumber(it.changePct)})"

                    if (it.change > 0) {
                        tvOrderBuySahamPercent.text = "+${it.change.formatPriceWithoutDecimal()} (+${initPercentFormatNumber(it.changePct)})"
                        tvOrderBuySahamPercent.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.textLightGreen
                            )
                        )
                    } else if (it.change < 0) {
                        tvOrderBuySahamPercent.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.textDownHeader
                            )
                        )
                    } else {
                        tvOrderBuySahamPercent.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.white
                            )
                        )
                    }
                }
                if (!isAmend) {
                    sharedViewModel.setData(
                        OrderAdapterData(
                            stockCode = it.secCode,
                            bestBid = it.bestBidPrice,
                            bestOffer = it.bestOfferPrice,
                            lastPrice = it.last,
                            closePrice = it.close,
                            avgPrice = it.avgPrice
                        )
                    )
                } else {
                    val orderData = sharedViewModel.data.value
                    orderData?.closePrice = it.close
                    sharedViewModel.setData(orderData!!)
                }
            }
        }

        viewModel.getAccountInfoResult.observe(viewLifecycleOwner) {
            it?.let {
                binding.tvOrderBuyPersonalInfo.text = "${it.accNo} - ${it.accName}"
                binding.tvImageProfile.text = generateTextImageProfile(it.accName)
            }
        }

        sharedViewModel.order.observe(viewLifecycleOwner) { value ->
            buyPrice = value.price ?: 0.0
            lotQty = value.lot ?: 0.0
            totalPrice = value.totalPrice ?: 0.0
            binding.tvTotalOrderBuy.text = "Rp" + value.totalPrice?.formatPriceWithoutDecimal()
            proceedAmount = value.proceedAmount ?: 0.0
            buySell = value.buySell.toString()
            isSliceOrder = value.isSliceOrder
            isAdvOrder = value.isAdvOrder
            isAutoOder = value.isAutoOrder
            slicingType = value.slicingType!!
            splitNumber = value.splitNumber ?: 0
            blockSize = value.splitBlockSize ?: 0
            boardType = value.boardType ?: ""
            compareType = value.compareType ?: "0"
            orderType = value.orderType ?: "0"
            bracketCriteria = value.bracketCriteria ?: AdvancedCriteriaRequest()
            takeProfitCriteria = value.takeProfitCriteria ?: AdvancedCriteriaRequest()
            stopLossCriteria = value.stopLossCriteria ?: AdvancedCriteriaRequest()
            timeInForce = value.timeInForce.toString()
            orderPeriod = value.orderPeriod ?: 0L
            endTimeSlice = value.endTimeSliceOrder ?: 0L
            if (isAmend) {
                binding.btnBuy.isEnabled = when {
                    lotQty == amendLotQty.div(100) && buyPrice == amendBuyPrice -> false
                    else -> value.isBtnOrder ?: true
                }
            } else {
                value.isBtnOrder?.let { binding.btnBuy.isEnabled = it }
            }
//
//            binding.groupOrderBuyTotal.visibility =
//                if (totalPrice == 0.0) View.GONE else View.VISIBLE

            if (value.isBalanceNotEnough){
                binding.tvTotalOrderBuy.setTextColor(ContextCompat.getColor(requireContext(), R.color.textRed))
            } else {
                binding.tvTotalOrderBuy.setTextColor(ContextCompat.getColor(requireContext(), R.color.txtBlackWhite))
            }

            isMarketOrder = value.isMarketOrder
            marketOrderType = value.marketOrderType
        }

        viewModel.getPinSessionResult.observe(viewLifecycleOwner) {
            if (it != null) {
                sessionPin = it

                if (validateSessionPin(sessionPin!!)) {
                    Log.d("pinexp", "local order true")
                    sharedViewModel.isPinSuccess.value = true
                    if (isFromButtonBuy) {
                        if (isAdvOrder || isMarketOrder || (buyPrice != 0.0 && lotQty != 0.0)) {
                            showDialogOrder()
                        }
                        isFromButtonBuy = false
                    } else {
                        userId = prefManager.userId
                        val accNo = prefManager.accno
                        val sessionId = prefManager.sessionId
                        viewModel.getStockOrderbook(userId, sessionId, stockCode)
                        viewModel.getAllStockParam("")
                        viewModel.getAccountInfo(accNo)

                        viewModel.getStockDetail(
                            prefManager.userId,
                            prefManager.sessionId,
                            stockCode
                        )
                    }
                } else {
                    Log.d("pinexp", "local order false")
                    showDialogPin()
                }
            } else {
                showDialogPin()
            }
//
        }



        viewModel.getMaxOrderByStockResult.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        when (it.data?.status) {
                            0 -> {
//                                commfee = it.data.commFee
                                totalWithFee = it.data.orderValueWithFee
                                showDialogOrder()
                            }

                            2 -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Invalid Session",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }

                            else -> {}
                        }

                    }

                    is Resource.Failure -> {
                        Toast.makeText(requireContext(), it.failureData.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {}
                }
            }
        }

        viewModel.getAllStockParamResult.observe(viewLifecycleOwner) {
            stockCodeList.clear()
            it?.map { data -> data?.let { stockCodeList.add(data.stockCode) } }

        }

        sharedViewModel.bottomScroll.observe(viewLifecycleOwner) {
            if (it) {
                binding.scrollView.post(Runnable { binding.scrollView.fullScroll(View.FOCUS_DOWN) })
                sharedViewModel.setBottomScroll(false)
            }
        }

        // Check Notation
        viewModel.getStockNotationResult.observe(viewLifecycleOwner) {
            if (it.size != 0) {
                val listNotation = arrayListOf<String>()
                it.forEach {
                    if (it?.notation?.equals("X") == true) {
                        isNotation = true
                    }
                    if (!listNotation.contains(it?.notation)) {
                        it?.notation?.let { it1 -> listNotation.add(it1) }
                    }

                }
                binding.tvInfoSpecialNotes.visibility = if (listNotation.isNotEmpty()) View.VISIBLE else View.GONE
                binding.tvInfoSpecialNotes.text = listNotation.joinToString()
                sharedViewModel.setIsNotation(isNotation)
            } else {
                binding.tvInfoSpecialNotes.text = ""
                binding.tvInfoSpecialNotes.visibility = View.GONE
            }
        }

        viewModel.getMarketSessionResult.observe(viewLifecycleOwner) {data ->
            if (data != null) {
                val nowSession = data.marketSessionName
                val isOpen = nowSession.contains("SESS_1") || nowSession.contains("SESS_2") || nowSession.contains("SESS_PRE_CLOSE")
                val isOpenForMarketOrder = nowSession.contains("SESS_1") || nowSession.contains("SESS_2")
                        || nowSession.contains("SESS_PRE_OPEN") || nowSession.contains("SESS_PRICE_BUILD_OPEN") || nowSession.contains("SESS_PRE_CLOSE")
                        || nowSession.contains("SESS_PRICE_BUILD_CLOSE") || nowSession.contains("SESS_POST_CLOSE") || nowSession.contains("SESS_LUNCH_BREAK")

                isMarketOpenForMarketOrder = isOpenForMarketOrder
                isMarketOpen = isOpen

                sharedViewModel.setIsMarketClosed(!isOpenForMarketOrder)
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

        viewModel.getIpAddressResult.observe(viewLifecycleOwner) {value ->
            ipAddress = value.ifEmpty { getIpAddress(requireContext()) }
        }
    }

    override fun setupComponent() {
        super.setupComponent()

        binding.apply {
            lyToolbarOrder.ivLayoutToolbarMasterIconLeft.visibility = View.VISIBLE
            lyToolbarOrder.ivLayoutToolbarMasterIconLeft.setImageResource(R.drawable.ic_back)
            lyToolbarOrder.ivLayoutToolbarMasterIconRightOne.visibility = View.VISIBLE
            lyToolbarOrder.ivLayoutToolbarMasterIconRightOne.setImageResource(R.drawable.ic_file)

            binding.scrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (scrollY > 115) {
                    binding.lyToolbarOrder.tvLayoutToolbarMasterTextLeft.visibility = View.VISIBLE
                    binding.lyToolbarOrder.tvLayoutToolbarMasterTextLeft.text = stockCode
                    binding.lyToolbarOrder.tvLayoutToolbarMasterTextLeft.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.textSecondaryBluey)
                    )
                } else {
                    binding.lyToolbarOrder.tvLayoutToolbarMasterTextLeft.visibility = View.GONE
                }
            }

            if (isAmend) {
                tabLayoutBuySell.visibility = View.GONE
                lyToolbarOrder.tvLayoutToolbarMasterTitle.text = if (buySell == "B") "Buy" else "Sell"
                binding.btnBuy.text = "Amend Order"
            } else {
                binding.btnBuy.text = if (buySell == "B") "Buy" else "Sell"
            }

            tabLayoutBuySell.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val currentTabString = tabOrder[tab?.position ?: 0]
                    buySell = if (tab?.position == 0) "B" else "S"
                    sharedViewModel.setUpdateOrder(buySell)
                    binding.btnBuy.text = currentTabString
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })
        }


    }

    private fun showDialogOrder() {
        val lotInt = lotQty.toInt()
        val isMarketOpened = if (isMarketOrder) isMarketOpenForMarketOrder else isMarketOpen
        if (buySell == "B") {
            val comFee = proceedAmount.times(buyComFee)
            val uiDialogOrderConfirmationModel = UIDialogOrderConfirmationModel(
                stockCode = stockCode,
                companyName = stockName,
                orderType = orderType,
                orderExpiry = timeInForce,
                buyPrice = buyPrice.formatPriceWithoutDecimal(),
                lot = lotInt.toString(),
                total = totalPrice.formatPriceWithoutDecimal(),
                isSliceOrder = isSliceOrder,
                isAdvOrder = isAdvOrder,
                slicingType = slicingType,
                splitNumber = splitNumber,
                blockSize = blockSize,
                commissionFee = comFee.formatPriceWithoutDecimal(),
                proceedAmount = proceedAmount.formatPriceWithoutDecimal(),
                takeProfitCompare = takeProfitCriteria.triggerCategory.GET_COMPARE_VALUE(),
                takeProfitTriggerPrice = takeProfitCriteria.triggerVal.formatPriceWithoutDecimal(),
                takeProfitSellPrice = takeProfitCriteria.triggerOrder?.ordPrice?.formatPriceWithoutDecimal(),
                takeProfirSellLot = takeProfitCriteria.triggerOrder?.ordQty?.toString(),
                stopLossCompare = stopLossCriteria.triggerCategory.GET_COMPARE_VALUE(),
                stopLossTriggerPrice = stopLossCriteria.triggerVal.formatPriceWithoutDecimal(),
                stopLossSellPrice = stopLossCriteria.triggerOrder?.ordPrice?.formatPriceWithoutDecimal(),
                stopLossSellLot = stopLossCriteria.triggerOrder?.ordQty?.toString(),
                orderPeriod = orderPeriod,
                endTimeSliceOrder = endTimeSlice,
                isAutoOrder = isAutoOder,
                autoOrderComparePrice = bracketCriteria.triggerVal.formatPriceWithoutDecimal(),
                autoOrderOpr = bracketCriteria.opr,
                notation = binding.tvInfoSpecialNotes.text.toString(),
                idxBoard = binding.tvInfoSpecialNotesAcceleration.text.toString(),
                isAmend = isAmend,
                isMarketOpen = isMarketOpened,
                isAmendGtc = isAmendGtc,
                isMarketOrder = isMarketOrder,
                marketOrderType = marketOrderType
            )

            showDialogBuyConfirm(uiDialogOrderConfirmationModel, parentFragmentManager)

            parentFragmentManager.setFragmentResultListener(
                NavKeys.KEY_FM_ORDER, viewLifecycleOwner
            ) { _, result ->
                var sendSnackBarData = OrderSuccessSnackBar()
                val confirmResult = result.getString(NavKeys.CONST_RES_ORDER_BUY)
                if (confirmResult == "RESULT_OK") {
                    if (!isAmend) {
                        when {
                            isSliceOrder -> sendSliceOrder()
                            isAdvOrder && isAutoOder -> sendAdvOrder()
                            isAdvOrder -> sendAdvOrder()
                            isAutoOder -> sendAutoOrder()
                            else -> sendOrder()
                        }
                        sendSnackBarData = OrderSuccessSnackBar(true, "order")
                    } else {
                        sendAmend()
                        val sendBuySell = if (buySell == "B") "buy" else "sell"
                        sendSnackBarData = OrderSuccessSnackBar(true, "amend" , sendBuySell, stockCode)
                    }

                    MainActivity.startIntentParam(
                        requireActivity(),
                        NavKeys.KEY_FM_TAB_PORTFOLIO,
                        1,
                        sendSnackBarData
                    )


                    clearValues()
                    viewModel.getStockDetail(
                        prefManager.userId,
                        prefManager.sessionId,
                        stockCode
                    )
                    sharedViewModel.setData(OrderAdapterData(isSuccessOrder = true))
                }
            }
        } else {
            val commFeeVal = proceedAmount.times(sellComFee)
            val uiDialogSellModel = UIDialogSellModel(
                stockCode = stockCode,
                companyName = stockName,
                buyPrice = buyPrice.formatPriceWithoutDecimal(),
                lot = lotInt.toString(),
                proceedAmount = proceedAmount.formatPriceWithoutDecimal(),
                brokerFee = commFeeVal.formatPriceWithoutDecimalWithoutMinus(),
                exchangeFee = "0",
                profitLoss = "0",
                isAdvOrder = isAdvOrder,
                total = totalPrice.formatPriceWithoutDecimal(), // TODO RK: brokerFee + exchangeFee
                isSliceOrder = isSliceOrder,
                slicingType = slicingType,
                splitNumber = splitNumber,
                blockSize = blockSize,
                orderType = orderType,
                takeProfitCompare = takeProfitCriteria.triggerCategory.GET_COMPARE_VALUE(),
                takeProfitTriggerPrice = takeProfitCriteria.triggerVal.formatPriceWithoutDecimal(),
                takeProfitSellPrice = takeProfitCriteria.triggerOrder?.ordPrice?.formatPriceWithoutDecimal(),
                takeProfirSellLot = takeProfitCriteria.triggerOrder?.ordQty?.toString(),
                stopLossCompare = stopLossCriteria.triggerCategory.GET_COMPARE_VALUE(),
                stopLossTriggerPrice = stopLossCriteria.triggerVal.formatPriceWithoutDecimal(),
                stopLossSellPrice = stopLossCriteria.triggerOrder?.ordPrice?.formatPriceWithoutDecimal(),
                stopLossSellLot = stopLossCriteria.triggerOrder?.ordQty?.toString(),
                timeInForce = timeInForce,
                orderPeriod = orderPeriod,
                endTimeSliceOrder = endTimeSlice,
                notation = binding.tvInfoSpecialNotes.text.toString(),
                idxBoard = binding.tvInfoSpecialNotesAcceleration.text.toString(),
                isAmend = isAmend,
                isMarketOrder = isMarketOrder,
                isAmendGtc = isAmendGtc,
                isMarketOpen = isMarketOpened,
                marketOrderType = marketOrderType
            )

            showDialogSellConfirm(uiDialogSellModel, parentFragmentManager)

            parentFragmentManager.setFragmentResultListener(
                NavKeys.KEY_FM_ORDER,
                viewLifecycleOwner
            ) { _, result ->
                var sendSnackBarData = OrderSuccessSnackBar()
                val confirmResult = result.getString(NavKeys.CONST_RES_ORDER_SELL)
                if (confirmResult == "RESULT_OK") {
                    var message = "Order Placed"
                    if (!isAmend) {
                        when {
                            isSliceOrder -> sendSliceOrder()
                            isAdvOrder -> sendAdvOrder()
                            else -> sendOrder()
                        }
                        sendSnackBarData = OrderSuccessSnackBar(true, "order")
                        message = "Order Placed"
                    } else {
                        sendAmend()
                        val sendBuySell = if (buySell == "B") "buy" else "sell"
                        sendSnackBarData = OrderSuccessSnackBar(true, "amend" , sendBuySell, stockCode)

                        message = "Order Amended"
                    }

                    MainActivity.startIntentParam(
                        requireActivity(),
                        NavKeys.KEY_FM_TAB_PORTFOLIO,
                        1,
                        sendSnackBarData
                    )

                    clearValues()
                    viewModel.getStockDetail(
                        prefManager.userId,
                        prefManager.sessionId,
                        stockCode
                    )
                    sharedViewModel.setData(OrderAdapterData(isSuccessOrder = true))
                }
            }
        }
    }

    private fun sendAmend() {
        val accNo = prefManager.accno
        val userId = prefManager.userId
        val sessionId = prefManager.sessionId
        val orderQty = lotQty.toInt()

        val sendAmend = AmendOrderRequest(
            newCliOrderRef = getRandomString(),
            oldCliOrderRef = amendData.orderId,
            orderID = amendData.orderId,
            newQty = orderQty * 100.0,
            newPrice = buyPrice,
            stockCode = stockCode,
            newTimeInForce = timeInForce,
            newOrdPeriod = orderPeriod,
            inputBy = userId,
            sessionId = sessionId,
            ip = ipAddress,
            accNo = accNo
        )

        viewModel.sendAmend(sendAmend)
    }

    private fun sendSliceOrder() {
        val accNo = prefManager.accno
        val userId = prefManager.userId
        val sessionId = prefManager.sessionId

        val advType = if (blockSize != 0) 10 else 0
        val ordQty = if (blockSize != 0) 0L else lotQty.toInt().times(100).toLong()
        val splitNumber = if (blockSize != 0) blockSize else splitNumber
        val blockSize = if (blockSize != 0) lotQty.toInt().times(100) else 0

        val sliceOrder = SliceOrderRequest(
            clOrderRef = getRandomString(),
            accNo = accNo,
            advType = advType,
            stockCode = stockCode,
            buySell = buySell,
            ordType = 0,
            ordQty = ordQty,
            ordPrice = buyPrice.toLong(),
            splitNumber = splitNumber,
            inputBy = userId,
            ipAddress = ipAddress,
            accType = "I",
            channel = 0,
            splitBlockSize = blockSize,
            mediaSource = 0,
            sessionId = sessionId,
            endTriggerTime = endTimeSlice
        )
        viewModel.sendSliceOrder(sliceOrder)
    }

    private fun sendOrder() {
        val accNo = prefManager.accno
        val userId = prefManager.userId
        val clientCode = prefManager.accno
        val sessionId = prefManager.sessionId
        val orderQty = lotQty.toInt()
        val oltOrder = SendOrderReq(
            clOrderRef = getRandomString(),
            board = boardType,
            orderTime = Date().time,
            buySell = buySell,
            stockCode = stockCode,
            orderType = orderType,
            timeInForce = timeInForce,
            orderPeriod = orderPeriod,
            accType = "I", //
            ordQty = orderQty * 100.0,
            ordPrice = buyPrice,
            accNo = "$accNo",
            inputBy = userId,
            clientCode = accNo,
            sessionId = sessionId,
            investType = "I",
            status = "PN",
            ip = ipAddress
        )

        viewModel.sendOrder(oltOrder)
    }

    private fun sendAdvOrder() {
        val accNo = prefManager.accno
        val userId = prefManager.userId
        val clientCode = prefManager.accno
        val sessionId = prefManager.sessionId
        val orderQty = lotQty.toInt().times(100)
        val oltOrder = AdvanceOrderRequest(
            clOrderRef = getRandomString(),
            accNo = accNo,
            accType = "I",
            advType = 9,
            stockCode = stockCode,
            ordQty = orderQty.toLong(),
            bracketCriteria = bracketCriteria,
            takeProfitCriteria = takeProfitCriteria,
            stopLossCriteria = stopLossCriteria,
            inputBy = userId,
            sessionId = sessionId,
            validUntil = orderPeriod,
            ip = ipAddress
        )

        viewModel.sendAdvOrder(oltOrder)

    }

    private fun sendAutoOrder() {
        val userId = prefManager.userId
        val sessionId = prefManager.sessionId
        val accNo = prefManager.accno
        val orderQty = lotQty.toInt().times(100)
        val oltOrder = AutoOrderRequest(
            clOrderRef = getRandomString(),
            accNo = accNo,
            stockCode = stockCode,
            ordQty = orderQty.toLong(),
            sessionId = sessionId,
            validUntil = orderPeriod,
            timeInForce = timeInForce,
            userId = userId,
            ordPrice = buyPrice.toLong(),
            opr = bracketCriteria.opr,
            triggerval = bracketCriteria.triggerVal,
            inputBy = userId,
            ipAddress = ipAddress,
            accType = "I",
            channel = 0,
        )
        viewModel.sendAutoOrder(oltOrder)
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.lyToolbarOrder.ivLayoutToolbarMasterIconLeft.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.tvOrderBuyHideTable.setOnClickListener {
            if (binding.groupOrderBuyTable.visibility == View.VISIBLE) {
                binding.tvOrderBuyHideTable.text = getString(R.string.text_show_table)
                binding.groupOrderBuyTable.visibility = View.GONE
            } else {
                binding.tvOrderBuyHideTable.text = getString(R.string.text_hide_table)
                binding.groupOrderBuyTable.visibility = View.VISIBLE
            }
        }

        binding.btnBuy.setOnClickListener {
            if (validateSessionPin(sessionPin!!)){
                if (isAdvOrder || isMarketOrder || (buyPrice != 0.0 && lotQty != 0.0)) {
                    showDialogOrder()
                }
            } else {
                isFromButtonBuy = true
                viewModel.getSessionPin(userId)
            }
        }

        binding.tvOrderBuySahamInfoCode.setOnClickListener {
            val icArrowDown =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_chevron_down)
            if (isAmend) {
                binding.tvOrderBuySahamInfoCode.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    null,
                    null,
                    null
                )
            } else {
                binding.viewStockDropdownLine.visibility = View.VISIBLE
                binding.tvOrderBuySahamInfoCode.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    null,
                    icArrowDown,
                    null
                )
                showDropDownStringSearchable(
                    requireContext(),
                    stockCodeList.sorted(),
                    binding.viewStockDropdownLine,
                    "Search stock code"
                ) { index, value ->
                    isNotation = false
                    viewModel.unSubscribeOrderbook(stockCode)
                    viewModel.unSubscribeTradeSummary(stockCode)
                    stockCode = value
                    viewModel.getStockOrderbook(userId, sessionId, stockCode)
                    viewModel.getStockNotation(stockCode)
                    viewModel.getStockParam(stockCode)
                    setIconStock(stockCode)

                    viewModel.getStockDetail(
                        prefManager.userId,
                        prefManager.sessionId,
                        stockCode
                    )
                    binding.viewStockDropdownLine.visibility = View.GONE
                }
            }
        }

        binding.lyToolbarOrder.ivLayoutToolbarMasterIconRightOne.setOnClickListener {
            MainActivity.startIntentParam(
                requireActivity(),
                NavKeys.KEY_FM_TAB_PORTFOLIO,
                1,
                ""
            )
        }
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        buyComFee = if (channelForFee == 0) prefManager.buyCommission.ifEmpty { "0.0" }.toDouble() else prefManager.buyCommissionOms.ifEmpty { "0.0" }.toDouble()
        sellComFee =  if (channelForFee == 0) prefManager.sellCommission.ifEmpty { "0.0" }.toDouble() else prefManager.sellCommissionOms.ifEmpty { "0.0" }.toDouble()

        stockCodeList.clear()

        ipAddress = getIpAddress(requireContext())
        viewModel.getIpAddress()

        viewModel.getSessionPin(userId)
        viewModel.getStockParam(stockCode)
        viewModel.getStockNotation(stockCode)
        viewModel.getMarketSession(prefManager.userId)
    }

    override fun setupAdapter() {
        super.setupAdapter()

        binding.rcvOrderBookTableBuy.setHasFixedSize(true)
        binding.rcvOrderBookTableBuy.adapter = orderBookBuyAdapter

        binding.rcvOrderBookTableSell.setHasFixedSize(true)
        binding.rcvOrderBookTableSell.adapter = orderBookSellAdapter

        val viewPager = binding.viewPager2
        val tabLayout = binding.tabLayoutBuySell
        val adapter = OrderViewPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabOrder[position]
        }.attach()

        viewPager.setCurrentItem(buySellInt, false)

    }

    override fun onResume() {
        super.onResume()
        viewModel.setListenerOrderBookTradeSum()
    }

    override fun onPause() {
        super.onPause()
        viewModel.unSubscribeOrderbook(stockCode)
        viewModel.unSubscribeTradeSummary(stockCode)

        removeOrderBookLayoutListener()
        isOrderBookListenerRemoved = false
    }

    override fun onClickStr(value: String?) {
        sharedViewModel.setData(OrderAdapterData(stockCode, value?.toDouble()))
        val heightOrderBook = binding.lyTableOrderbook.bottom
        binding.scrollView.smoothScrollTo(0, heightOrderBook)
    }

    private fun showDialogPin() {
        showDialogPin(parentFragmentManager, onSuccess = { isSuccess, isBlocked ->
            if (isSuccess) {
                sharedViewModel.isPinSuccess.value = true
                if (isFromButtonBuy) {
                    if (isAdvOrder) {
                        showDialogOrder()
                    } else if (buyPrice != 0.0 && lotQty != 0.0) {
                        showDialogOrder()
                    }
                    isFromButtonBuy = false
                } else {
                    userId = prefManager.userId
                    val accNo = prefManager.accno
                    val sessionId = prefManager.sessionId
                    viewModel.getStockOrderbook(userId, sessionId, stockCode)
                    viewModel.getAllStockParam("")
                    viewModel.getAccountInfo(accNo)

                    viewModel.getStockDetail(
                        prefManager.userId,
                        prefManager.sessionId,
                        stockCode
                    )
                }
            } else {
                if (isBlocked) {
                    viewModel.getLogout(prefManager.userId, prefManager.sessionId)
                } else {
                    onBackPressed()
                }
            }
        })
    }

    private fun clearValues() {
        buySellInt = 0
        stockName = ""
        buyPrice = 0.0
        amendBuyPrice = 0.0
        totalPrice = 0.0
        lotQty = 0.0
        amendLotQty = 0.0
        buySell = ""
        orderId = ""
        orderType = "0"
        isAmend = false
        isSliceOrder = false
        isAdvOrder = false
        slicingType = 0
        splitNumber = 0
        blockSize = 0
        compareType = "0"
        boardType = "RG"
        commfee = 0.0
        totalWithFee = 0.0
        timeInForce = "0"
        amendData = PortfolioOrderItem()
        bracketCriteria = AdvancedCriteriaRequest()
        takeProfitCriteria = AdvancedCriteriaRequest()
        stopLossCriteria = AdvancedCriteriaRequest()
    }

    fun removeOrderBookLayoutListener() {
        if (!isOrderBookListenerRemoved) {
            binding.lyTableOrderbook.viewTreeObserver.removeOnGlobalLayoutListener(orderBookLayoutListener)
            isOrderBookListenerRemoved = true
        }
    }

}