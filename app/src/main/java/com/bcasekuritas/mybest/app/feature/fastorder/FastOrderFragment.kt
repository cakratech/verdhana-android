package com.bcasekuritas.mybest.app.feature.fastorder

import android.graphics.PorterDuff
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.data.layout.UIDialogModel
import com.bcasekuritas.mybest.app.domain.dto.request.AmendFastOrderReq
import com.bcasekuritas.mybest.app.domain.dto.request.CancelFastOrderReq
import com.bcasekuritas.mybest.app.domain.dto.request.SendOrderReq
import com.bcasekuritas.mybest.app.domain.dto.response.FastOrderBook
import com.bcasekuritas.mybest.app.domain.dto.response.QtyPriceItem
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.fastorder.adapter.FastOrderAdapter
import com.bcasekuritas.mybest.databinding.FragmentFastOrderBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.getIpAddress
import com.bcasekuritas.mybest.ext.common.getRandomString
import com.bcasekuritas.mybest.ext.common.validateSessionPin
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.converter.edtToDouble
import com.bcasekuritas.mybest.ext.converter.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.converter.removeSeparator
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarInterface
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.textview.toDouble
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import java.util.Date

@FragmentScoped
@AndroidEntryPoint
class FastOrderFragment : BaseFragment<FragmentFastOrderBinding, FastOrderViewModel>(), OnClickAny,
    ShowSnackBarInterface by ShowSnackBarImpl() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmFastOrder
    override val viewModel: FastOrderViewModel by viewModels()
    override val binding: FragmentFastOrderBinding by autoCleaned {
        (FragmentFastOrderBinding.inflate(layoutInflater))
    }
    private var isShowOrders = false
    private var isOrderCounts = false
    private var isPreventOrders = false
    private val fastOrderAdapter: FastOrderAdapter by autoCleaned {
        FastOrderAdapter(this, requireContext(), prefManager)
    }

    private lateinit var userId: String
    private lateinit var sessionId: String
    private lateinit var accNo: String
    private var ipAddress = ""

    private var stockCodes = ""
    private var stockName = ""
    private var companyName = ""
    private var buyingLimit = 0.0
    private var sessionPin: Long? = null
    private var lastPrice = 0.0
    private var maxOrderResCount = 0
    private var fastOrderMap = mutableMapOf<Double, FastOrderBook>()
    private var listBuyInfo = listOf<QtyPriceItem>()
    private var listSellInfo = listOf<QtyPriceItem>()
    private var buyComFee = 0.0
    private var sellComFee = 0.0
    private var commFee = 0.0
    private var sendAmend = AmendFastOrderReq()

    override fun onResume() {
        super.onResume()
        viewModel.getMarketSession(userId)
        viewModel.setListenerOrderBook()
    }

    override fun onPause() {
        super.onPause()

        prefManager.isOnAmendFO = false

        viewModel.unSubscribeOrderbook(stockCodes)
        viewModel.clearOrderReply()
        viewModel.publishFastOrder(userId, sessionId, 1, accNo, stockCodes)
    }

    override fun setupArguments() {
        super.setupArguments()

        userId = prefManager.userId
        sessionId = prefManager.sessionId
        accNo = prefManager.accno

        arguments?.let {
            stockCodes = it.getString(Args.EXTRA_PARAM_STR_ONE).toString()
            stockName = it.getString(Args.EXTRA_PARAM_STR_TWO).toString()
            clearValueAfterSearch()
        }

        viewModel.getStockOrderbook(userId, sessionId, stockCodes)
        viewModel.publishFastOrder(userId, sessionId, 0, accNo, stockCodes)
        viewModel.getStockParam(stockCodes)
        viewModel.getStockPos(userId, accNo, sessionId, stockCodes)

        ipAddress = getIpAddress(requireContext())
        viewModel.getIpAddress()
//        viewModel.getSimplePortfolio(userId, sessionId, accNo)
        fastOrderMap = mutableMapOf<Double, FastOrderBook>()
    }

    private fun clearValueAfterSearch() {
        binding.tvMaxCashVal.text = "0"
        binding.tvMaxLimitVal.text = "0"
        binding.tvMaxSellVal.text = "0"
        binding.tvBuyingLimitVal.text = "0"
        binding.tvBuySum.text = "0(0)"
        binding.tvBidSum.text = "0(0)"
        binding.tvOfferSum.text = "0(0)"
        binding.tvSellSum.text = "0(0)"
        buyingLimit = 0.0
        lastPrice = 0.0
        binding.btnWithdrawAllBuy.isEnabled = false
        binding.btnWithdrawAllSell.isEnabled = false
        fastOrderAdapter.clearData()
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.lyToolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.ivSetupVolume.setOnClickListener {
//            val bundle = Bundle().apply {
//
//                putString(Args.EXTRA_PARAM_STR_ONE, stockCodes)
//                putDouble(Args.EXTRA_PARAM_DOUBLE_ONE, lastPrice)
//
//            }
//            findNavController().navigate(R.id.setup_fast_order_volume_fragment, bundle)

            val maxLimit = binding.tvMaxLimitVal.text.toString()
            val maxCash = binding.tvMaxCashVal.text.toString()

            showDialogSetupVolume(
                parentFragmentManager,
                lastPrice,
                stockCodes,
                maxLimit,
                maxCash,
                onConfirm = { volume ->

                    binding.edtVolume.setText(volume)

                })
        }

        binding.btnInfoBuyingLimit.setOnClickListener {
            showDialogPortfolioSummaryInfoBottom(
                "Buying Limit",
                "Buying limit adalah fasilitas untuk membeli saham dengan nilai melebihi dana yang tersedia, menggunakan cash dan valuasi portofolio sebagai kolateral, sesuai dengan perhitungan margin level.",
                "Buying limit is a facility to buy stocks exceeding your available cash, using your cash and portfolio valuation as collateral based on the margin level.",
                parentFragmentManager)
        }

        binding.lyToolbar.ivLayoutToolbarMasterIconRightOne.setOnClickListener {
            findNavController().navigate(R.id.search_fast_order_fragment)
        }

        binding.lyToolbar.ivLayoutToolbarMasterIconRightTwo.setOnClickListener {
            showDialogFastOrderSetting(
                parentFragmentManager,
                isShowOrders,
                isOrderCounts,
                isPreventOrders,
                onSave = { isShowOrder, isOrderCount, isPreventOrder ->
                    prefManager.isShowOrdersFO = isShowOrder
                    prefManager.isOrderCountsFO = isOrderCount
                    prefManager.isPreventOrdersFO = isPreventOrder

                    if (isOrderCounts != isOrderCount) fastOrderAdapter.updateOrderCounts(
                        isOrderCount
                    )

                    isShowOrders = isShowOrder
                    isOrderCounts = isOrderCount
                    isPreventOrders = isPreventOrder
                })
        }

        // Withdraw ALl
        binding.btnWithdrawAllBuy.setOnClickListener {

            val buyList = fastOrderMap.entries
                .filter { it.value.totOrdQtyBid != 0L }
                .map {
                    QtyPriceItem(
                        qty = it.value.totOrdQtyBid.toDouble(),
                        price = it.value.price
                    )
                }
                .toList()

            listBuyInfo = buyList

            val sendWithdrawAll = CancelFastOrderReq(
                stock = stockCodes,
                stockName = stockName,
                board = "RG",
                buySell = "B",
                price = 0.0,
                inputBy = userId,
                ipAddress = ipAddress,
                channel = 0,
                accNo = accNo
            )

            showDialogWithdrawAllFastOrder(
                parentFragmentManager,
                listBuyInfo,
                sendWithdrawAll,
                onConfirm = { cancelFastOrder ->
                    sendWithdrawAll(cancelFastOrder)
                })

        }

        binding.btnWithdrawAllSell.setOnClickListener {

            val sellList = fastOrderMap.entries
                .filter { it.value.totOrdQtyOffer != 0L }
                .map {
                    QtyPriceItem(
                        qty = it.value.totOrdQtyOffer.toDouble(),
                        price = it.value.price
                    )
                }
                .toList()

            listSellInfo = sellList

            val sendWithdrawAll = CancelFastOrderReq(
                stock = stockCodes,
                stockName = stockName,
                board = "RG",
                buySell = "S",
                price = 0.0,
                inputBy = userId,
                ipAddress = ipAddress,
                channel = 0,
                accNo = accNo
            )


            showDialogWithdrawAllFastOrder(
                parentFragmentManager,
                listSellInfo,
                sendWithdrawAll,
                onConfirm = { cancelFastOrder ->
                    sendWithdrawAll(cancelFastOrder)
                })
        }

        binding.btnInfo.setOnClickListener {
            showDialogCoachmarkFastOrder(parentFragmentManager)
        }

    }

    override fun initAPI() {
        super.initAPI()

        viewModel.getSessionPin(userId)

        buyComFee = prefManager.buyCommission.ifEmpty { "0.0" }.toDouble()
        sellComFee = prefManager.sellCommission.ifEmpty { "0.0" }.toDouble()
        isShowOrders = prefManager.isShowOrdersFO
        isOrderCounts = prefManager.isOrderCountsFO
        isPreventOrders = prefManager.isPreventOrdersFO

    }

    override fun setupComponent() {
        super.setupComponent()

        binding.lyToolbar.tvLayoutToolbarMasterTitle.text = stockCodes
        binding.lyToolbar.ivLayoutToolbarMasterIconLeft.visibility = View.VISIBLE
        binding.lyToolbar.ivLayoutToolbarMasterIconRightOne.visibility = View.VISIBLE
        binding.lyToolbar.ivLayoutToolbarMasterIconRightTwo.visibility = View.VISIBLE


        binding.lyToolbar.ivLayoutToolbarMasterIconRightOne.setImageResource(R.drawable.ic_search)
        binding.lyToolbar.ivLayoutToolbarMasterIconRightTwo.setImageResource(R.drawable.ic_setting)

        val tintColor = ContextCompat.getColor(requireContext(), R.color.black)
        binding.lyToolbar.ivLayoutToolbarMasterIconRightOne.setColorFilter(
            tintColor,
            PorterDuff.Mode.SRC_IN
        )
        binding.lyToolbar.ivLayoutToolbarMasterIconRightTwo.setColorFilter(
            tintColor,
            PorterDuff.Mode.SRC_IN
        )

        binding.edtVolume.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {

                binding.tvMaxSellVal.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                binding.edtVolume.removeTextChangedListener(this) // Remove listener to prevent infinite loop
                binding.edtVolume.setBackgroundResource(R.drawable.bg_ffffff_8_stroke_dae8f6)

                val removeSeparator = s.toString().removeSeparator()
                when {
                    removeSeparator.toIntOrNull() == null -> {
                        binding.edtVolume.setText("")
                    }

                    removeSeparator.toInt() <= 50000 -> {
                        val formattedText = removeSeparator.formatPriceWithoutDecimal()
                        binding.edtVolume.setText(formattedText)
                        binding.edtVolume.setSelection(formattedText.length) // Move cursor to the end
                    }

                    removeSeparator.toInt() > 50000 -> {
                        showSnackBarTop(
                            requireContext(),
                            binding.root,
                            "error",
                            R.drawable.ic_error,
                            "Can't buy or sell more than 50,000 lot",
                            "", requireActivity(), ""
                        )
                        val maxLot = 50000.0
                        binding.edtVolume.setText(maxLot.formatPriceWithoutDecimal())
                        binding.edtVolume.setSelection(maxLot.formatPriceWithoutDecimal().length)
                    }
                }
                binding.edtVolume.addTextChangedListener(this)
            }
        })
    }

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvFastOrderBook.apply {
            adapter = fastOrderAdapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.getIpAddressResult.observe(viewLifecycleOwner) {value ->
            ipAddress = value.ifEmpty { getIpAddress(requireContext()) }
        }

        viewModel.orderReplyLiveData.observe(viewLifecycleOwner) {
            if (it != null && it.stockCode == stockCodes) {
                when (it.status) {
                    "M", "O", "PS", "A" -> {
                        showSnackBarTop(
                            requireContext(),
                            binding.root,
                            "success",
                            R.drawable.ic_success,
                            "Order Placed",
                            "Check all of your active orders in your portfolio. Check Now",
                            requireActivity(),
                            ""
                        )
                    }

                    "R" -> {
                        showSnackBarTop(
                            requireContext(),
                            binding.root,
                            "error",
                            R.drawable.ic_success,
                            "Order Failed",
                            "",
                            requireActivity(),
                            ""
                        )
                    }
                }
            }
        }


        viewModel.fastOrderLiveData.observe(viewLifecycleOwner) {
            if (it.stockCode == stockCodes) {
                var sumQtyBuy = 0
                var sumQtySell = 0

                var sumOrdBuyList = arrayListOf<Int>()
                var sumOrdSellList = arrayListOf<Int>()

                var sumQtyBid = 0
                var sumQtyOffer = 0

                it.buyFastOrderInfo.forEach { buyList ->
                    fastOrderMap.computeIfAbsent(buyList.price) { FastOrderBook() }.apply {
                        price = if (price == 0.0) buyList.price else price
                        totOrdBid = buyList.totalOrder
                        totOrdQtyBid = buyList.totalOrdQty
                        sameOrderList = ArrayList(buyList.detailQtyList)
                    }
                    sumOrdBuyList.add(buyList.detailQtyList.size)
                    sumQtyBid += buyList.totalOrdQty.times(buyList.price).toInt()
                    sumQtyBuy += buyList.totalOrdQty.div(100).toInt()
                }

                it.sellFastOrderInfo.forEach { sellList ->
                    fastOrderMap.computeIfAbsent(sellList.price) { FastOrderBook() }.apply {
                        price = if (price == 0.0) sellList.price else price
                        totOrdOffer = sellList.totalOrder
                        totOrdQtyOffer = sellList.totalOrdQty
                        sameOrderList = ArrayList(sellList.detailQtyList)
                    }
                    sumOrdSellList.add(sellList.detailQtyList.size)
                    sumQtyOffer += sellList.totalOrdQty.times(sellList.price).toInt()
                    sumQtySell += sellList.totalOrdQty.div(100).toInt()
                }

                binding.btnWithdrawAllBuy.isEnabled = sumQtyBuy > 0
                binding.btnWithdrawAllSell.isEnabled = sumQtySell > 0

                binding.tvBuySum.text =
                    "${sumQtyBuy.formatPriceWithoutDecimal()} (${sumOrdBuyList.sum()})"
                binding.tvBidSum.text = sumQtyBid.formatPriceWithoutDecimal()

                binding.tvSellSum.text =
                    "${sumQtySell.formatPriceWithoutDecimal()} (${sumOrdSellList.sum()})"
                binding.tvOfferSum.text = sumQtyOffer.formatPriceWithoutDecimal()

                //fastOrderMap.entries.removeIf { item -> item.value.totOrdQtyBid == 0L && item.value.totOrdQtyOffer == 0L }

                fastOrderAdapter.updateBuySell(fastOrderMap.values.toList())

                binding.tvMaxCashVal.text =
                    if (it.stockInfo.maxLotCash > 0) it.stockInfo.maxLotCash.formatPriceWithoutDecimal() else "0"
                binding.tvMaxLimitVal.text =
                    if (it.stockInfo.maxLotLimit > 0) it.stockInfo.maxLotLimit.formatPriceWithoutDecimal() else "0"
                binding.tvMaxSellVal.text =
                    if (it.stockInfo.maxLotSell > 0) it.stockInfo.maxLotSell.formatPriceWithoutDecimal() else "0"
                binding.tvBuyingLimitVal.text =
                    "Rp${it.stockInfo.buyingPowerLimit.formatPriceWithoutDecimal()}"
                buyingLimit = it.stockInfo.buyingPowerLimit
            }
        }

        viewModel.getPinSessionResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if (it.data != null) {
                        sessionPin = it.data

                        if (validateSessionPin(sessionPin!!)) {
                            if (!prefManager.isCoachmarkFastOrderShow) {
                                prefManager.isCoachmarkFastOrderShow = true
                                showDialogCoachmarkFastOrder(parentFragmentManager)
                            }
                            viewModel.getStockPos(userId, accNo, sessionId, stockCodes)
                        } else {
                            showDialogPin()
                        }
                    } else {
                        showDialogPin()
                    }
                }

                else -> {}
            }
//
        }

        viewModel.getMarketSessionResult.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                val nowSession = data.marketSessionName
                val isMarketOpen = (nowSession.contains("SESS_1") || nowSession.contains("SESS_2") || nowSession.contains(
                    "SESS_LUNCH_BREAK"
                ))
                binding.rcvFastOrderBook.isGone = !isMarketOpen

                if (!isMarketOpen) {
                    binding.tvBuySum.text = "0(0)"
                    binding.tvBidSum.text = "0(0)"
                    binding.tvOfferSum.text = "0(0)"
                    binding.tvSellSum.text = "0(0)"
                    binding.btnWithdrawAllBuy.isEnabled = false
                    binding.btnWithdrawAllSell.isEnabled = false
                }

            }
        }

        viewModel.getStockOrderbookResult.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                it.first()?.let { firstData ->
                    if (firstData.secCode == stockCodes) {
                        // Orderbook
                        val totBidQtyL = firstData.totalBidQtyM.toDouble()
                        val totOfferQtyL = firstData.totalOfferQtyM.toDouble()
                        lastPrice = firstData.last

                        viewModel.getMaxOrder(
                            prefManager.userId,
                            prefManager.accno,
                            "B",
                            prefManager.sessionId,
                            stockCodes,
                            lastPrice,
                            "C",
                            "RG"
                        )
                        maxOrderResCount++

                        if (firstData.buyOrderBookList != null && firstData.buyOrderBookList.isNotEmpty()) {
                            firstData.buyOrderBookList.forEach { item ->
                                val itemQtyL = item.quantityL
                                val buyProgress = totBidQtyL / itemQtyL

                                fastOrderMap.computeIfAbsent(item.price) { FastOrderBook() }.apply {
                                    isBid = true
                                    price = item.price
                                    closePrice = firstData.close
                                    quantity = item.quantity.toDouble()
                                    quantityL = item.quantityL.toInt()
                                    totQuantityL = firstData.totalBidQtyM.toInt()
                                    progress = buyProgress.toInt()
                                }
                            }
                        }

                        if (firstData.sellOrderBookList != null && firstData.sellOrderBookList.isNotEmpty()) {
                            firstData.sellOrderBookList.forEach { item ->
                                val itemQtyL = item.quantityL
                                val sellProgress = totOfferQtyL / itemQtyL

                                fastOrderMap.computeIfAbsent(item.price) { FastOrderBook() }.apply {
                                    isBid = false
                                    price = item.price
                                    closePrice = firstData.close
                                    quantity = item.quantity.toDouble()
                                    quantityL = item.quantityL.toInt()
                                    totQuantityL = firstData.totalOfferQtyM.toInt()
                                    progress = sellProgress.toInt()
                                }
                            }
                        }

                        fastOrderAdapter.setData(fastOrderMap.values.toList(), prefManager.isOrderCountsFO)
//                        scrollToTopOfBuy(binding.rcvFastOrderBook, fastOrderMap.values.toList())
                    }
                }
            }
        }

        viewModel.getFastOrderListResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }

                is Resource.Success -> {
                    when (it.data?.status) {
                        0 -> {

                            it.data.fastOrderListInfo.buyFastOrderInfoList.map { buyList ->
                                fastOrderMap[buyList.price]?.let { fastOrderMap ->
                                    fastOrderMap.totOrdBid = buyList.totalOrder
                                    fastOrderMap.totOrdQtyBid = buyList.totalOrdQty
                                }
                            }

                            it.data.fastOrderListInfo.sellFastOrderInfoList.map { sellList ->
                                fastOrderMap[sellList.price]?.let { fastOrderMap ->
                                    fastOrderMap.totOrdBid = sellList.totalOrder
                                    fastOrderMap.totOrdQtyBid = sellList.totalOrdQty
                                }
                            }

                            //val sortFastOrder =
                            //    fastOrderMap.values.toList().sortedByDescending { it.price }

                            // fastOrderAdapter.setData(sortFastOrder, prefManager.isOrderCountsFO)

                        }

                        1 -> {
                        }
                    }
                }

                else -> {}
            }
        }

//        viewModel.getMaxOrderByStockResult.observe(viewLifecycleOwner) {
//            when (it) {
//                is Resource.Loading -> {
//                    showLoading()
//                }
//
//                is Resource.Success -> {
//                    when (it.data?.status) {
//                        0 -> {
//                            val maxLot = it.data.maxLot
//                            if (maxOrderResCount == 1) {
//                                binding.tvMaxCashVal.text = maxLot.formatPriceWithoutDecimal()
//                                maxOrderResCount++
//
//                                viewModel.getMaxOrder(
//                                    prefManager.userId,
//                                    prefManager.accno,
//                                    "B",
//                                    prefManager.sessionId,
//                                    stockCodes,
//                                    lastPrice,
//                                    "L",
//                                    "RG"
//                                )
//                            } else if (maxOrderResCount == 2) {
//                                binding.tvMaxLimitVal.text = maxLot.formatPriceWithoutDecimal()
//                                maxOrderResCount = 0
//                            }
//                        }
//                    }
//
//                }
//
//                is Resource.Failure -> {
//                    Toast.makeText(requireContext(), it.failureData.message, Toast.LENGTH_SHORT)
//                        .show()
//                }
//
//                else -> {}
//            }
//        }

        viewModel.getStockPosResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    it.data?.let { res ->
                        res.accstockposList.filter { it.stockcode == stockCodes }.map { data ->
                            binding.tvMaxSellVal.text = data.potStockAvailable.div(100)
                                .formatPriceWithoutDecimal()
                        }
                    }
                }

                else -> {}
            }
        }

        viewModel.getStockParamResult.observe(viewLifecycleOwner) {
            it?.stockName?.let {
                companyName = it
            }
        }
    }

    private fun showInsufficientBalance() {

        showDialogInfoBottomCallBack(
            parentFragmentManager, true, UIDialogModel(
                icon = R.drawable.ic_insufficient_balance,
                titleStr = "Insufficient Balance",
                btnPositiveStr = "Confirm"
            ),
            onOkClicked = { dismissDialogLoadingCenter() }
        )
    }

    private fun showInsufficientLots() {

        showDialogInfoBottomCallBack(
            parentFragmentManager, true, UIDialogModel(
                icon = R.drawable.ic_insufficient_lots,
                titleStr = "Insufficient Lots",
                btnPositiveStr = "Confirm"
            ),
            onOkClicked = { dismissDialogLoadingCenter() }
        )
    }

    private fun sendOrder(adapterValue: SendOrderReq) {
        val accNo = prefManager.accno
        val userId = prefManager.userId
        val sessionId = prefManager.sessionId
        val orderQty = binding.edtVolume.edtToDouble()
        val fastOrder = SendOrderReq(
            clOrderRef = getRandomString(),
            board = "RG",
            orderTime = Date().time,
            buySell = adapterValue.buySell,
            stockCode = stockCodes,
            stockName = stockName,
            orderType = "0",
            timeInForce = "0",
            orderPeriod = 0,
            sameOrderList = adapterValue.sameOrderList,
            accType = "I", //
            ordQty = orderQty * 100.0,
            ordPrice = adapterValue.ordPrice,
            accNo = accNo,
            inputBy = userId,
            clientCode = accNo,
            sessionId = sessionId,
            investType = "I",
            status = "PN",
            ip = ipAddress
        )

        viewModel.sendOrder(fastOrder)

    }

    private fun sendOrderAdapter(adapterValue: SendOrderReq): SendOrderReq {
        val accNo = prefManager.accno
        val userId = prefManager.userId
        val sessionId = prefManager.sessionId
        val orderQtyS = binding.edtVolume.removeSeparator()
        val orderQty = orderQtyS.toDouble()
        val fastOrder = SendOrderReq(
            clOrderRef = getRandomString(),
            board = "RG",
            orderTime = Date().time,
            buySell = adapterValue.buySell,
            stockCode = stockCodes,
            stockName = stockName,
            orderType = "0",
            timeInForce = "0",
            orderPeriod = null,
            accType = "I", //
            ordQty = orderQty,
            ordPrice = adapterValue.ordPrice,
            accNo = accNo,
            inputBy = userId,
            clientCode = accNo,
            sessionId = sessionId,
            investType = "I",
            status = "PN",
            ip = ipAddress,
            sameOrderList = adapterValue.sameOrderList
        )

        return fastOrder

    }

    private fun sendWithdraw(adapterValue: CancelFastOrderReq) {
        val accNo = prefManager.accno
        val userId = prefManager.userId
        val sessionId = prefManager.sessionId
        val sendWithdraw = CancelFastOrderReq(
            stock = stockCodes,
            stockName = stockName,
            board = "RG",
            buySell = adapterValue.buySell,
            price = adapterValue.price,
            inputBy = userId,
            ipAddress = ipAddress,
            sessionId = sessionId,
            channel = 0,
            accNo = accNo,
            sameOrderList = adapterValue.sameOrderList
        )

        viewModel.sendWithdraw(sendWithdraw)
    }

    private fun sendWithdrawAdapter(adapterValue: CancelFastOrderReq): CancelFastOrderReq {
        val accNo = prefManager.accno
        val userId = prefManager.userId
        val sessionId = prefManager.sessionId

        return CancelFastOrderReq(
            stock = stockCodes,
            stockName = stockName,
            board = "RG",
            buySell = adapterValue.buySell,
            price = adapterValue.price,
            inputBy = userId,
            ipAddress = ipAddress,
            channel = 0,
            qty = adapterValue.qty,
            sessionId = sessionId,
            accNo = accNo,
            sameOrderList = adapterValue.sameOrderList
        )
    }

    private fun sendWithdrawAll(adapterValue: CancelFastOrderReq) {
        val accNo = prefManager.accno
        val userId = prefManager.userId
        val sessionId = prefManager.sessionId
        val sendWithdrawAll = CancelFastOrderReq(
            stock = stockCodes,
            stockName = stockName,
            board = "RG",
            buySell = adapterValue.buySell,
            price = 0.0,
            inputBy = userId,
            ipAddress = ipAddress,
            channel = 0,
            sessionId = sessionId,
            accNo = accNo
        )

        viewModel.sendAllWithdraw(sendWithdrawAll)
    }

    private fun sendAmend(adapterValue: AmendFastOrderReq) {
        val accNo = prefManager.accno
        val userId = prefManager.userId
        val sessionId = prefManager.sessionId
        val sendAmend = AmendFastOrderReq(
            stock = stockCodes,
            stockName = stockName,
            board = "RG",
            buySell = adapterValue.buySell,
            oldPrice = adapterValue.oldPrice,
            newPrice = adapterValue.newPrice,
            inputBy = userId,
            sessionId = sessionId,
            ipAddress = ipAddress,
            channel = 0,
            accNo = accNo
        )

        viewModel.sendAmend(sendAmend)
    }

    private fun sendAmendAdapter(
        adapterValue: AmendFastOrderReq,
        newPrice: Double?,
    ): AmendFastOrderReq {
        val accNo = prefManager.accno
        val userId = prefManager.userId
        val sessionId = prefManager.sessionId

        return AmendFastOrderReq(
            stock = stockCodes,
            stockName = stockName,
            board = "RG",
            buySell = adapterValue.buySell,
            oldPrice = adapterValue.oldPrice,
            newPrice = newPrice,
            inputBy = userId,
            ipAddress = ipAddress,
            channel = 0,
            sessionId = sessionId,
            volume = adapterValue.volume,
            accNo = accNo,
            sameOrderList = adapterValue.sameOrderList
        )

    }

    override fun onClickAny(valueAny: Any?) {
        val volume = binding.edtVolume.removeSeparator().toIntOrNull() ?: 0

        when (valueAny) {
            is CancelFastOrderReq -> {
                val cancelFastOrderReq = valueAny as CancelFastOrderReq

                if (isShowOrders) {
                    // TODO RK: After tap open confirmation if there is same order, when confirm open the confirm dialog
                    val data = sendWithdrawAdapter(cancelFastOrderReq)
                    showDialogWithdrawFastOrder(
                        parentFragmentManager,
                        data,
                        onConfirm = { withdrawOrder ->
                            sendWithdraw(withdrawOrder)
                        })
                } else {
                    sendWithdraw(cancelFastOrderReq)
                }
            }

            is AmendFastOrderReq -> {
                val amendFastOrderReq = valueAny as AmendFastOrderReq

                if (prefManager.isOnAmendFO) {
                    sendAmend = sendAmendAdapter(amendFastOrderReq, null)
                    if (amendFastOrderReq.buySell == "B") {
                        binding.vwAmendBuy.isGone = false
                    } else {
                        binding.vwAmendSell.isGone = false
                    }
                } else {
                    binding.vwAmendSell.isGone = true
                    binding.vwAmendBuy.isGone = true

                    sendAmend = sendAmendAdapter(sendAmend, amendFastOrderReq.newPrice)

                    if (sendAmend.oldPrice != sendAmend.newPrice) {
                        showDialogAmendFastOrder(
                            parentFragmentManager,
                            sendAmend,
                            onConfirm = {
                                sendAmend(sendAmend)
                            }
                        )
                    }

                }
            }

            is SendOrderReq -> {
                val sendFastOrder = valueAny as SendOrderReq

                val maxSellLot = binding.tvMaxSellVal.text.toString().removeSeparator().toDouble()
                val maxBuyLimit = binding.tvMaxLimitVal.text.toString().removeSeparator().toDouble()
                val inputVolume = binding.edtVolume.edtToDouble()
                if (volume != 0) {

                    val data = sendOrderAdapter(sendFastOrder)
                    if (data.buySell == "S") {
                        if (inputVolume > maxSellLot) {
                            binding.tvMaxSellVal.setTextColor(ContextCompat.getColor(requireContext(), R.color.textDown))
                            showInsufficientLots()
                        } else {
                            when {
                                isPreventOrders && data.sameOrderList.isNotEmpty() -> {
                                    showDialogPreventFastOrder(
                                        parentFragmentManager,
                                        data,
                                        onConfirm = { sendOrder, isChecked ->
                                            sendOrder(sendOrder)
                                            if (isChecked) {
                                                prefManager.isPreventOrdersFO = false
                                                isPreventOrders = false
                                            }
                                        }
                                    )
                                }

                                isShowOrders -> {
                                    commFee = data.ordPrice?.times(data.ordQty?.times(100) ?: 0.0)
                                        ?.times(sellComFee) ?: 0.0
                                    showDialogConfirmFastOrder(
                                        parentFragmentManager,
                                        data.buySell,
                                        data,
                                        onConfirm = { sendOrder ->
                                            sendOrder(sendOrder)
                                        },
                                        commFee
                                    )
                                }

                                else -> {
                                    sendOrder(sendFastOrder)
                                }
                            }
                        }

                    } else {
                        val higherLot = maxOf(
                            binding.edtVolume.edtToDouble(),
                            binding.tvMaxLimitVal.toDouble()
                        )
                        if (binding.edtVolume.edtToDouble() > higherLot) {
                            showSnackBarTop(
                                requireContext(),
                                binding.root,
                                "error",
                                R.drawable.ic_success,
                                "Can't buy more than ${higherLot.formatPriceWithoutDecimal()}",
                                "", requireActivity(), ""
                            )
                        } else {
                            when {
                                isPreventOrders && data.sameOrderList.isNotEmpty() -> {
                                    showDialogPreventFastOrder(
                                        parentFragmentManager,
                                        data,
                                        onConfirm = { sendOrder, isChecked ->
                                            if (inputVolume > maxBuyLimit) {
                                                showInsufficientBalance()
                                            } else {
                                                sendOrder(sendOrder)
                                            }
                                            if (isChecked) {
                                                prefManager.isPreventOrdersFO = false
                                                isPreventOrders = false
                                            }
                                        }
                                    )
                                }

                                isShowOrders -> {
                                    commFee = data.ordPrice?.times(data.ordQty?.times(100) ?: 0.0)
                                        ?.times(buyComFee) ?: 0.0
                                    showDialogConfirmFastOrder(
                                        parentFragmentManager,
                                        data.buySell,
                                        data,
                                        onConfirm = { sendOrder ->
                                            if (inputVolume > maxBuyLimit) {
                                                showInsufficientBalance()
                                            } else {
                                                sendOrder(sendOrder)
                                            }
                                        },
                                        commFee
                                    )
                                }

                                else -> {
                                    if (inputVolume > maxBuyLimit) {
                                        showInsufficientBalance()
                                    } else {
                                        sendOrder(sendFastOrder)
                                    }
                                }
                            }
                        }

                    }

                } else {
                    binding.edtVolume.setBackgroundResource(R.drawable.bg_ffffff_8_stroke_e14343)
                    showSnackBarTop(
                        requireContext(),
                        binding.root,
                        "error",
                        R.drawable.ic_success,
                        "Please input volume",
                        "", requireActivity(), ""
                    )
                }

            }

        }

    }

    fun scrollToTopOfBuy(recyclerView: RecyclerView, orderList: List<FastOrderBook>) {
        val position = orderList.indexOfFirst { it.isBid } // Find first item with buyQty
        if (position != -1) {
            recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    recyclerView.scrollToPosition(position)
                }
            })
        }
    }

    private fun showDialogPin() {
        showDialogPin(parentFragmentManager, onSuccess = { isSuccess, isBlocked ->
            if (isSuccess) {
                if (!prefManager.isCoachmarkFastOrderShow) {
                    prefManager.isCoachmarkFastOrderShow = true
                    showDialogCoachmarkFastOrder(parentFragmentManager)
                }
                viewModel.getStockPos(userId, accNo, sessionId, stockCodes)
            } else {
                if (isBlocked) {
                    showDialogAccountDisable(parentFragmentManager)
                }
                onBackPressed()

            }
        })
    }
}
