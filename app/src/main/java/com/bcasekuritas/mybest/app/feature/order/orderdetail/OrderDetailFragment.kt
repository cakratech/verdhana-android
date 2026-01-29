package com.bcasekuritas.mybest.app.feature.order.orderdetail

import android.annotation.SuppressLint
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.request.WithdrawOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.response.AdvancedOrderDetail
import com.bcasekuritas.mybest.app.domain.dto.response.OrderSuccessSnackBar
import com.bcasekuritas.mybest.app.domain.dto.response.PortfolioOrderItem
import com.bcasekuritas.mybest.app.feature.activity.main.MainActivity
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.databinding.FragmentOrderDetailBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.getIpAddress
import com.bcasekuritas.mybest.ext.common.getRandomString
import com.bcasekuritas.mybest.ext.common.oprConvert
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.converter.GET_4_CHAR_STOCK_CODE
import com.bcasekuritas.mybest.ext.converter.GET_IDX_BOARD
import com.bcasekuritas.mybest.ext.converter.GET_OPERATOR_COMPARE_STR
import com.bcasekuritas.mybest.ext.converter.GET_STATUS_ADVANCE_ORDER
import com.bcasekuritas.mybest.ext.converter.GET_STATUS_BUY_SELL
import com.bcasekuritas.mybest.ext.converter.GET_STATUS_ORDER
import com.bcasekuritas.mybest.ext.converter.GET_STATUS_TRIGGER_CATEGORY
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarInterface
import com.bcasekuritas.mybest.ext.other.formatPriceWithDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimalWithoutMinus
import com.bcasekuritas.mybest.ext.view.setSafeOnClickListener
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.abs

@FragmentScoped
@AndroidEntryPoint
class OrderDetailFragment: BaseFragment<FragmentOrderDetailBinding, OrderDetailViewModel>(),
    ShowDialog by ShowDialogImpl(), ShowSnackBarInterface by ShowSnackBarImpl() {

    override val viewModel: OrderDetailViewModel by viewModels()
    override val binding: FragmentOrderDetailBinding by autoCleaned { (FragmentOrderDetailBinding.inflate(layoutInflater)) }
    override val bindingVariable: Int = BR.vmOrderDetail

    private var ipAddress = ""

    var data: PortfolioOrderItem? = PortfolioOrderItem()
    var advanceOrderData: AdvancedOrderDetail? = AdvancedOrderDetail()
    private var orderId = ""
    private var advancedOrderId = ""

    var buyCommision = ""
    var sellCommision = ""

    var isWithdrawGTC = false

    override fun setupArguments() {
        super.setupArguments()

        arguments?.let {
            data = if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(Args.EXTRA_PARAM_OBJECT, PortfolioOrderItem::class.java)
            } else {
                it.getParcelable(Args.EXTRA_PARAM_OBJECT)
            }

            advanceOrderData = if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(Args.EXTRA_PARAM_OBJECT_TWO, AdvancedOrderDetail::class.java)
            } else {
                it.getParcelable(Args.EXTRA_PARAM_OBJECT_TWO)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun setupComponent() {
        super.setupComponent()

        buyCommision = prefManager.buyCommission
        sellCommision = prefManager.sellCommission

        when {
            data != null -> {
                setBuySellComission(true)
                setUIData()
                viewModel.getStockParam(data?.stockCode?: "")
            }
            advanceOrderData != null -> {
                setAdvanceOrderUIData()
                viewModel.getStockParam(advanceOrderData?.stockCode?:"")
            }
        }
    }

    override fun initAPI() {
        super.initAPI()
        ipAddress = getIpAddress(requireContext())
        viewModel.getIpAddress()
    }

    private fun setBuySellComission(isOrderData: Boolean) {
        if (isOrderData) {
            buyCommision = if (data?.channelForFee == 1) prefManager.buyCommissionOms else prefManager.buyCommission
            sellCommision = if (data?.channelForFee == 1) prefManager.sellCommissionOms else prefManager.sellCommission
        }
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {

            toolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener{
                onBackPressed()
            }

            btnWithdrawOrderDetail.setOnClickListener {
                orderId = data?.orderId?: ""

                if (data?.isGtOrder == true) {
                    isWithdrawGTC = true
                    advancedOrderId = data?.advOrderId?: ""

                    if (checkIsStatusPending(data?.status ?: "")) {
                        showDialogWithdrawOrderBottom(NavKeys.KEY_FM_ORDER_DETAIL, childFragmentManager)
                    } else {
                        showDialogWithdrawGTCOrderBottom(NavKeys.KEY_FM_ORDER_DETAIL, childFragmentManager)
                    }

                } else {
                    showDialogWithdrawOrderBottom(NavKeys.KEY_FM_ORDER_DETAIL, childFragmentManager)
                }
            }

            btnWithdrawAdvanceOrder.setOnClickListener {
                advancedOrderId = advanceOrderData?.orderId ?: ""
                showDialogWithdrawOrderBottom(NavKeys.KEY_FM_ORDER_DETAIL, childFragmentManager)
            }

            btnAmendOrderDetail.setOnClickListener {
                data?.isAmend = true
                val buySell = if (data?.buySell == "B") 0 else 1

                if (data?.isGtOrder == true) {
                    if (!prefManager.isAmendGtc) {
                        showDialogAmendGtcOrderBottom(NavKeys.KEY_FM_ORDER_DETAIL, childFragmentManager)
                    } else {

                        data?.let {
                            MiddleActivity.startIntentParam(
                                requireActivity(),
                                NavKeys.KEY_FM_ORDER,
                                it,
                                buySell
                            )
                        }
                    }
                } else {

                    data?.let {
                        MiddleActivity.startIntentParam(
                            requireActivity(),
                            NavKeys.KEY_FM_ORDER,
                            it,
                            buySell
                        )
                    }
                }

            }

            lyStockInfo.setSafeOnClickListener {
                val stockCode = if (data != null) data?.stockCode else advanceOrderData?.stockCode

                if (data != null){
                    val stockCodeAny: Any = stockCode ?: ""
                    MiddleActivity.startIntentParam(
                        requireActivity(),
                        NavKeys.KEY_FM_STOCK_DETAIL,
                        stockCodeAny,
                        ""
                    )

                } else {
                    val bundle = Bundle().apply {
                        putString(Args.EXTRA_PARAM_STR_ONE, stockCode)
                    }
                    findNavController().navigate(R.id.stock_detail_fragment, bundle)
                }
            }
        }
    }

    private fun sendWithdraw(orderId: String){
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

    private fun sendWithdrawAdvancedOrder(orderId: String) {
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

        viewModel.sendWithdrawAdvancedOrder(sendWithdraw)
    }

    override fun setupListener() {
        super.setupListener()
        childFragmentManager.setFragmentResultListener(NavKeys.KEY_FM_ORDER_DETAIL, viewLifecycleOwner) { _, result ->
            // withdraw dialog
            val confirmResultWithdraw = result.getString(NavKeys.CONST_RES_WITHDRAW_ORDER_CONFIRM)
            if (confirmResultWithdraw == "RESULT_OK") {
                if (result.getBoolean("confirm")) {
                    var buysell = ""
                    var stockCode = ""
                    when {
                        // for withdraw gtc when status pending from order detail
                        isWithdrawGTC -> {
                            buysell = data?.buySell?:""
                            stockCode = data?.stockCode?:""
                            sendWithdrawAdvancedOrder(advancedOrderId)
                        }
                        !orderId.equals("") -> {
                            buysell = data?.buySell?:""
                            stockCode = data?.stockCode?:""
                            sendWithdraw(orderId)
                        }
                        !advancedOrderId.equals("") -> {
                            buysell = advanceOrderData?.buysell?:""
                            stockCode = advanceOrderData?.stockCode?:""
                            sendWithdrawAdvancedOrder(advancedOrderId)
                        }
                    }
                    buysell = buysell.uppercase().GET_STATUS_BUY_SELL()
                    val withdrawData = OrderSuccessSnackBar(true, "withdraw" ,buysell, stockCode)

                    if (data != null){
                        val bundle = Bundle().apply {
                            putInt(Args.EXTRA_PARAM_INT_ONE, 1)
                            putParcelable(Args.EXTRA_PARAM_OBJECT, withdrawData)
                        }

                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.order_detail_fragment, false) // clear back stack queue up to start destination or in this case stock detail
                            .build()

                        findNavController().navigate(R.id.portfolio_fragment, bundle, navOptions)

                    } else {
                        MainActivity.startIntentParam(
                            requireActivity(),
                            NavKeys.KEY_FM_TAB_PORTFOLIO,
                            1,
                            withdrawData
                        )
                    }
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
                        sendWithdrawAdvancedOrder(advancedOrderId)
                    }
                    var buySell = data?.buySell?:""
                    val stockCode = data?.stockCode?:""

                    buySell = buySell.uppercase().GET_STATUS_BUY_SELL()
                    val withdrawData = OrderSuccessSnackBar(true, "withdraw" ,buySell, stockCode)

                    val bundle = Bundle().apply {
                        putInt(Args.EXTRA_PARAM_INT_ONE, 1)
                        putParcelable(Args.EXTRA_PARAM_OBJECT, withdrawData)
                    }

                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.order_detail_fragment, false) // clear back stack queue up to start destination or in this case stock detail
                        .build()

                    findNavController().navigate(R.id.portfolio_fragment, bundle, navOptions)

                }
            }

            // amend GTC dialog
            val confirmResultAmendGtc = result.getString(NavKeys.CONST_RES_AMEND_ORDER_GTC_CONFIRM)
            if (confirmResultAmendGtc == "RESULT_OK") {
                if (result.getBoolean("confirm")) {
                    val dataAmend = data
                    if (dataAmend != null) {
                        MiddleActivity.startIntentParam(
                            requireActivity(),
                            NavKeys.KEY_FM_ORDER,
                            dataAmend,
                            data?.buySell?:""
                        )
                    }
                }
            }

        }

    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.getIpAddressResult.observe(viewLifecycleOwner) {value ->
            ipAddress = value.ifEmpty { getIpAddress(requireContext()) }
        }

        viewModel.getStockParamResult.observe(viewLifecycleOwner) {
            binding.tvStockName.text = it?.stockParam?.stockName

            val listNotation = it?.stockNotation?.map { it.notation }

            binding.tvNotation.visibility = if (listNotation?.isNotEmpty() == true) View.VISIBLE else View.GONE
            binding.tvNotation.text = listNotation?.joinToString()
            binding.tvInfoSpecialNotesAcceleration.text = it?.stockParam?.idxTrdBoard.GET_IDX_BOARD()
            binding.tvInfoSpecialNotesAcceleration.visibility = if (binding.tvInfoSpecialNotesAcceleration.text != "") View.VISIBLE else View.GONE
        }
    }

    private fun setAdvanceOrderUIData() {
        binding.toolbar.tvLayoutToolbarMasterTitle.text = "Advance Order Detail"
        binding.tvIdxOrderId.text = "Order ID"
        binding.llOrderId.isGone = true
//        binding.tvOrderChannel.visibility = if (data?.channel == 1) View.VISIBLE else View.GONE
        val lot = advanceOrderData?.ordQty?.div(100)
        val lotDone = (advanceOrderData?.lotDone ?: 0.0) / 100
        val sltpLotDone = advanceOrderData?.sltpLotDone?.div(100)
        val takeProfitLotDone = advanceOrderData?.takeProfitLotDone?.div(100)
        val stopLossLotDone = advanceOrderData?.stopLossLotDone?.div(100)
        val amount = advanceOrderData?.ordPrice?.times(advanceOrderData?.ordQty?:0.0) ?: 0.0
        var total = 0.0
        var fee = 0.0
        val buyCommission = if (buyCommision != "") buyCommision.toDouble() else 0.0
        val sellCommission = if (sellCommision != "") sellCommision.toDouble() else 0.0
        if (advanceOrderData?.buysell == "B") {
            fee = amount.times(buyCommission)
            total = amount.plus(fee)
        } else {
            fee = amount.times(sellCommission)
            total = amount.minus(fee)
        }
        val orderDate: Date? = advanceOrderData?.ordTime?.let { Date(it) }
        val orderExpiry = advanceOrderData?.ordPeriod

        binding.apply {
            val url = prefManager.urlIcon+ GET_4_CHAR_STOCK_CODE(advanceOrderData?.stockCode?:"")

            Glide.with(requireContext())
                .load(url)
                .circleCrop()
                .placeholder(R.drawable.bg_circle)
                .error(R.drawable.bg_circle)
                .into(ivLogo)

            when (advanceOrderData?.advType) {
                0,10 -> {
                    //slice order
                    groupWithCondition.visibility = View.GONE
                    groupSliceOrder.visibility = View.VISIBLE
                    lyAutoOrder.visibility = View.GONE
                }
                2 -> {
                    // auto order
                    lyAutoOrder.visibility = View.VISIBLE
                    groupSliceOrder.visibility = View.GONE
                    groupWithCondition.visibility = View.GONE

                }
                6 -> {
                    // gtc
                    groupSliceOrder.visibility = View.GONE
                    groupWithCondition.visibility = View.GONE
                    lyAutoOrder.visibility = View.GONE
                }
                9 -> {
                    // sltp
                    lyAutoOrder.visibility = View.GONE
                    groupSliceOrder.visibility = View.GONE
                    groupWithCondition.visibility = View.VISIBLE
                    groupSummary.visibility = View.VISIBLE
                    lyPrice.visibility = View.VISIBLE
                    lyLot.visibility = View.VISIBLE
                    lyLotDone.visibility = View.VISIBLE

                    if (advanceOrderData?.triggerPriceAutoOrder != 0.0) {
                        lyAutoOrder.visibility = View.VISIBLE
                        tvCompareAutoVal.text = GET_STATUS_TRIGGER_CATEGORY(advanceOrderData?.triggerCategory?:0) + " " + advanceOrderData?.opr?.let { oprConvert(it) } + " ${advanceOrderData?.triggerPriceAutoOrder?.formatPriceWithoutDecimal()}"
                    }

                    tvSellOrderLotDoneLoss.text = stopLossLotDone?.formatPriceWithoutDecimal()
                    tvSellOrderLotDoneProfit.text = takeProfitLotDone?.formatPriceWithoutDecimal()
                }
            }


            tvLotDoneOrder.text = if (advanceOrderData?.advType == 9) sltpLotDone?.formatPriceWithoutDecimal() else lotDone.formatPriceWithoutDecimal()
            tvOrderTime.text = orderDate?.let { SimpleDateFormat("dd MMM yyyy, HH:mm").format(it) }
            tvStockCode.text = advanceOrderData?.stockCode
            tvIdxOrderId.text = advanceOrderData?.orderId
            tvBuySell.text = advanceOrderData?.buysell?.GET_STATUS_BUY_SELL()
            tvBuySell.text = when {
                advanceOrderData?.buysell == "B" && advanceOrderData?.advType == 9 -> "BUY & SELL"
                advanceOrderData?.buysell == "B" -> "BUY"
                advanceOrderData?.buysell == "S" -> "SELL"
                advanceOrderData?.buysell == "" && advanceOrderData?.advType == 9 -> "SELL"
                else -> ""
            }
            tvLotOrder.text = lot?.formatPriceWithoutDecimal() +" Lot"

            when {
                advanceOrderData?.advType?.equals(0) == true || advanceOrderData?.advType?.equals(10) == true -> {
                    tvStatus.text = if (advanceOrderData?.ordStatus.equals("PB") || advanceOrderData?.ordStatus.equals("B2")) "PENDING" else advanceOrderData?.ordStatus.GET_STATUS_ADVANCE_ORDER()?.uppercase()
                }
                advanceOrderData?.advType?.equals(6) == true -> {
                    tvStatus.text = if (advanceOrderData?.ordStatus.equals("PB")) "PENDING TIMER" else advanceOrderData?.ordStatus.GET_STATUS_ADVANCE_ORDER()?.uppercase()
                }
                advanceOrderData?.advType?.equals(9) == true -> tvStatus.text = advanceOrderData?.bracketStatus
                else -> tvStatus.text = advanceOrderData?.ordStatus.GET_STATUS_ADVANCE_ORDER()?.uppercase()
            }

            tvPrice.text = advanceOrderData?.ordPrice?.formatPriceWithoutDecimal()

            if (advanceOrderData?.advType?.equals(2) == true ){
                tvCompareAutoVal.text = GET_STATUS_TRIGGER_CATEGORY(advanceOrderData?.triggerCategory?:0) + " " + advanceOrderData?.opr?.let { oprConvert(it) } + " ${advanceOrderData?.triggerPriceAutoOrder?.formatPriceWithoutDecimal()}"
            }

            if (advanceOrderData?.advType?.equals(6) == true) {
                tvExpOrder.text = "Valid Until : ${orderExpiry?.let { DateUtils.toStringDate(it, "dd-MM-yyyy") }}"
            } else {
                tvExpOrder.text = "Today"
            }

            if (advanceOrderData?.advType?.equals(0) == true ||advanceOrderData?.advType?.equals(10) == true) {
                val splitSize = advanceOrderData?.splitSize?: 0
                val blockQty = advanceOrderData?.blockQty?: 0
                when {
                    blockQty != 0 -> {
                        lyNoOfSplit.visibility = View.GONE
                        tvBlockSize.text = splitSize.toString()
                    }
                    splitSize != 0 -> {
                        lyBlockSize.visibility = View.GONE
                        tvNoOfSplit.text = splitSize.toString()
                    }
                }
            }

            if (advanceOrderData?.advType?.equals(9) == true) {
                if (advanceOrderData?.takeProfitTriggerVal?.equals(0.0) == false) {
                    val opr = advanceOrderData?.takeProfitOpr ?: 5
                    tvTriggerPriceProfit.text = GET_STATUS_TRIGGER_CATEGORY(advanceOrderData?.triggerCategoryTakeProfit?:0) + " " + GET_OPERATOR_COMPARE_STR(opr) + " " + advanceOrderData?.takeProfitTriggerVal?.formatPriceWithoutDecimal()
                    tvSellPriceProfit.text = advanceOrderData?.takeProfitPrice?.formatPriceWithoutDecimal()
                    tvSellOrderLotProfit.text = advanceOrderData?.takeProfitQty?.div(100)?.formatPriceWithoutDecimal()
                } else {
                    lyTakeProfit.visibility = View.GONE
                    divider4.visibility = View.GONE
                }

                if (advanceOrderData?.stopLossTriggerVal?.equals(0.0) == false) {
                    val opr = advanceOrderData?.stopLossOpr?: 5
                    tvTriggerPriceLoss.text = GET_STATUS_TRIGGER_CATEGORY(advanceOrderData?.triggerCategoryStopLoss?:0) + " " + GET_OPERATOR_COMPARE_STR(opr) + " " + advanceOrderData?.stopLossTriggerVal?.formatPriceWithoutDecimal()
                    tvSellPriceLoss.text = advanceOrderData?.stopLossPrice?.formatPriceWithoutDecimal()
                    tvSellOrderLotLoss.text = advanceOrderData?.stopLossQty?.div(100)?.formatPriceWithoutDecimal()
                } else {
                    lyStopLoss.visibility = View.GONE
                    divider4.visibility = View.GONE
                }
            }

            if (advanceOrderData?.ordStatus.equals("A") || data?.status.equals("R") || data?.status.equals("C")) {
                tvInvestmentOrder.text = "0"
                tvTotal.text = "Rp0"
                tvBrokerFee.text = "0"
            } else {
                tvSellAmount.text = amount.formatPriceWithoutDecimal()
                tvInvestmentOrder.text = amount.formatPriceWithoutDecimal()
                tvTotal.text = "Rp" + total.formatPriceWithoutDecimal()
                tvBrokerFee.text = fee.formatPriceWithoutDecimalWithoutMinus()
            }

            if (advanceOrderData?.buysell == "B") {
                tvBuySell.setBackgroundResource(R.drawable.rounded_25cbd6_16)
                groupSummaryBuy.visibility = View.VISIBLE
                groupSummarySell.visibility = View.GONE
            } else {
                tvBuySell.setBackgroundResource(R.drawable.rounded_e14343_16)
                lySellAmount.visibility = View.VISIBLE
                groupSummaryBuy.visibility = View.GONE

                if (advanceOrderData?.advType?.equals(9) == true) {
                    lyPrice.visibility = View.GONE
                    lyLot.visibility = View.GONE
                    lyLotDone.visibility = View.GONE
                    groupSummary.visibility = View.GONE
                }
            }

            advanceOrderData?.remark?.takeIf { it.isNotEmpty() }?.let {
                tvRemark.text = it
                lyRemark.visibility = View.VISIBLE
            }

            lyButtonOrderDetail.visibility = View.GONE

            if (advanceOrderData?.ordStatus == "O" || advanceOrderData?.ordStatus == "O2" || advanceOrderData?.ordStatus == "PB" || advanceOrderData?.ordStatus == "B2") {
                btnWithdrawAdvanceOrder.visibility = View.VISIBLE
            } else {
                btnWithdrawAdvanceOrder.visibility = View.GONE
            }
        }

    }

    private fun setUIData() {
        binding.toolbar.tvLayoutToolbarMasterTitle.text = "Detail"
        binding.groupSliceOrder.visibility = View.GONE
        binding.groupWithCondition.visibility = View.GONE
        binding.tvOrderChannel.visibility = if (data?.channelForFee == 1) View.VISIBLE else View.GONE
        binding.llOrderId.isGone = false

        val lot = (data?.orderQty ?: 0.0) / 100
        val lotDone = (data?.matchQty ?: 0.0) / 100
        val amountOrder = if (data?.status == "M") data?.mValue else data?.ordValue
        val amountHistory = data?.price?.times(data?.orderQty?: 0.0)?: 0.0
        val amount = if (data?.isHistory == true) amountHistory else amountOrder
        var total = 0.0
        var fee = data?.fee ?: 0.0
        val buyCommission = if (buyCommision != "") buyCommision.toDouble() else 0.0
        val sellCommission = if (sellCommision != "") sellCommision.toDouble() else 0.0
        if (data?.buySell == "B") {
            fee = if (data?.fee != 0.0) abs(fee) else amount?.times(buyCommission) ?: 0.0
            total = amount?.plus(fee) ?: 0.0
        } else {
            fee = if (data?.fee != 0.0) abs(fee) else amount?.times(sellCommission) ?: 0.0
            total = amount?.minus(fee) ?: 0.0
        }

        val date: Date? = data?.time?.toLong()?.let { Date(it) }

        binding.apply {
            val url = prefManager.urlIcon+ GET_4_CHAR_STOCK_CODE(data?.stockCode?:"")
            Glide.with(requireContext())
                .load(url)
                .circleCrop()
                .placeholder(R.drawable.bg_circle)
                .error(R.drawable.bg_circle)
                .into(ivLogo)

            tvExpOrder.text = "Good Till Cancelled"
            tvLotDoneOrder.text = lotDone.formatPriceWithoutDecimal()
            tvOrderId.text = data?.orderId
            tvOrderTime.text = date?.let { SimpleDateFormat("dd MMM yyyy, HH:mm").format(it) }
            tvStockCode.text = data?.stockCode
            tvIdxOrderId.text = data?.idxOrderId
            tvBuySell.text = data?.buySell?.GET_STATUS_BUY_SELL()
            tvLotOrder.text = lot?.formatPriceWithoutDecimal() +" Lot"
            tvStatus.text = data?.status.GET_STATUS_ORDER()
            tvPrice.text = data?.price?.formatPriceWithoutDecimal()
            if (data?.isHistory == true) {
                tvSellAmount.text = amount?.formatPriceWithDecimal()
                tvSellAmountDone.text = amount?.formatPriceWithoutDecimal()
            } else {
                tvSellAmount.text = data?.ordValue?.formatPriceWithoutDecimal()
                tvSellAmountDone.text = data?.mValue?.formatPriceWithoutDecimal()
            }

            if (data?.status.equals("A") || data?.status.equals("R") || data?.status.equals("C")) {
                tvInvestmentOrder.text = "0"
                tvTotal.text = "Rp0"
                tvBrokerFee.text = "0"
            } else {
                tvInvestmentOrder.text = amount?.formatPriceWithoutDecimal()
                tvTotal.text = "Rp" + total.formatPriceWithoutDecimal()
                if (data?.fee != 0.0) {
                    tvBrokerFee.text = data?.fee?.formatPriceWithoutDecimalWithoutMinus()
                } else {
                    tvBrokerFee.text = fee.formatPriceWithoutDecimalWithoutMinus()
                }
            }

            data?.remark?.takeIf { it.isNotEmpty() }?.let {
                tvRemark.text = it
                lyRemark.visibility = View.VISIBLE
            }

            when (data?.timeInForce) {
                "0","3","4" -> {
                    tvExpOrder.text = "Today"
                }
                "S" -> tvExpOrder.text = "Session"
                "1", "2" -> {
                    if (data?.ordPeriod != 0L) {
                        val date = DateUtils.toStringDate(data?.ordPeriod?: 0L, "dd/MM/yyyy")
                        tvExpOrder.text = "GTC ($date)"
                    } else {
                        tvExpOrder.text = "Good Till Cancel"
                    }
                }
                else -> tvExpOrder.text = "Today"
            }

            // change text color status order
            when (data?.status) {
                "M" -> {
                    tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.textUp))
                }
                "O" -> {
                    tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.alwaysBlue))
                }
                "A" -> {
                    tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.action_cyan))
                }
                "C" -> {
                    tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.textDown))
                }
                else -> {
                    tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
            }

            if (data?.buySell == "B") {
                tvBuySell.setBackgroundResource(R.drawable.rounded_25cbd6_16)
                groupSummaryBuy.visibility = View.VISIBLE
                groupSummarySell.visibility = View.GONE
            } else {
                tvBuySell.setBackgroundResource(R.drawable.rounded_e14343_16)
                groupSummarySell.visibility = View.VISIBLE
                groupSummaryBuy.visibility = View.GONE
            }

            if (data?.status == "O" || data?.status == "P" || data?.status == "PT" || data?.status == "PB") {
                binding.lyButtonOrderDetail.visibility = View.VISIBLE
            } else {
                binding.lyButtonOrderDetail.visibility = View.GONE
            }

            tvWithdrawInfo.visibility = if (data?.isWdForToday == true && (data?.status == "C" || data?.status == "A")) View.VISIBLE else View.GONE
            tvWithdrawInfo.text = if (data?.status == "A") resources.getString(R.string.withdraw_info_amend_order_detail) else resources.getString(R.string.withdraw_info_order_detail)

            // set value of order type
            lyOrderType.visibility = if (data?.orderType == "5") View.VISIBLE else View.GONE
            val marketOrderType = when (data?.timeInForce) {
                "3" -> "(FAK)"
                "4" -> "(FOK)"
                "0" -> "(MTL)"
                else -> ""
            }
            tvOrderType.text = "Market Order $marketOrderType"


        }

    }

    private fun checkIsStatusPending(status: String): Boolean {
        return when {
            status == "PN" || status == "PA" || status == "PS" || status == "PJ" || status == "PT" || status == "PB" -> true
            else -> false
        }
    }

}