package com.bcasekuritas.mybest.app.feature.order.tabbuy

import android.graphics.Typeface
import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.request.AdvancedCriteriaRequest
import com.bcasekuritas.mybest.app.domain.dto.request.OrderAdapterData
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.order.OrderSharedViewModel
import com.bcasekuritas.mybest.databinding.FragmentOrderBuyBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.adjustFractionPrice
import com.bcasekuritas.mybest.ext.common.boardType
import com.bcasekuritas.mybest.ext.common.compareTimeToCurrentTime
import com.bcasekuritas.mybest.ext.common.getCurrentHourAndMinutes
import com.bcasekuritas.mybest.ext.common.getFractionPrice
import com.bcasekuritas.mybest.ext.common.getTomorrowTimeInMillis
import com.bcasekuritas.mybest.ext.common.initCalenderDialogPlus1
import com.bcasekuritas.mybest.ext.common.initOpenTimePicker
import com.bcasekuritas.mybest.ext.common.isToday
import com.bcasekuritas.mybest.ext.common.isValidTimeForTomorrow
import com.bcasekuritas.mybest.ext.common.validDateInMonth
import com.bcasekuritas.mybest.ext.common.validateSessionPin
import com.bcasekuritas.mybest.ext.converter.GET_COMPARE_SLTP
import com.bcasekuritas.mybest.ext.converter.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.converter.removeSeparator
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarInterface
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.other.roundedHalfDown
import com.bcasekuritas.mybest.widget.adjuster.CustomQtyAdjuster
import com.bcasekuritas.mybest.widget.textview.CustomTextView
import com.bcasekuritas.rabbitmq.proto.bcas.TriggerOrder
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import kotlin.math.ceil

@FragmentScoped
@AndroidEntryPoint
class OrderBuyFragment : BaseFragment<FragmentOrderBuyBinding, OrderBuyViewModel>(),
    AdapterView.OnItemSelectedListener,
    ShowDialog by ShowDialogImpl(),
    ShowSnackBarInterface by ShowSnackBarImpl(),
    ShowDropDown by ShowDropDownImpl() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmOrderBuy
    override val viewModel: OrderBuyViewModel by viewModels()
    override val binding: FragmentOrderBuyBinding by autoCleaned {
        (FragmentOrderBuyBinding.inflate(layoutInflater))
    }
    private val sharedViewModel: OrderSharedViewModel by lazy {
        ViewModelProvider(requireActivity()).get(OrderSharedViewModel::class.java)
    }

    private var isPinSuccess = false

    private var stockCodes = ""
    private var totalPrice = 0.0
    private var buyingPower = 0.0
    private var cashBalance = 0.0
    private var timeInForce = "0"
    private var orderPeriod = 0L

    private var compareType = "0"
    private var boardType = "RG"
    private var slicingType = 0
    private var endTimeSlice = 0L

    private var lastAmendPrice = 0.0
    private var lastAmendQty = 0.0
    private var closePrice = 0.0
    private var commisionFee = 0.0
    private var avgPrice = 0.0

    private var isAmendOrder = false
    private var isSliceOrder = false
    private var isSLTP = false
    private var isBtnOrder = false
    private var isAmendPartial = false
    private var isAutoOrder = false
    private var tpValue = 0.0
    private var minusValTp = 0.0
    private var minusValPctTp = 0.0
    private var slValue = 0.0
    private var minusValSl = 0.0
    private var minusValPctSl = 0.0

    private var bracketCriteria = AdvancedCriteriaRequest()
    private var takeProfitCriteria = AdvancedCriteriaRequest()
    private var stopLossCriteria = AdvancedCriteriaRequest()

    private var tpChecked = false
    private var slChecked = false

    private var selectedOrdType = "0"
    private var selectedCompareTp = "0"
    private var selectedCompareSl = "0"
    private var selectedOpr = 0

    private var cbSplit = false
    private var cbRepeat = false
    private var isNotation = false
    private var priceARAARB = 0.0

    private var relId = ""
    private var isSpecialStock = false

    private var priceAdjuster = 0.0
    private var isMarketOrder = false
    private var marketOrderType = ""
    private var orderType = "0"
    private var isMarketClosed = false

    private var priceMarketOrder = 0.0

    private var listOrderType = arrayListOf("Limit Order","Market Order", "Automatic Order")
    private val listBoard = arrayListOf("Regular")
    private val listSliceType = arrayListOf("At Once")
    private val listCompareSLTP = arrayListOf("Last Price")
    private val listOpr = arrayListOf("=", ">", "≥", "<", "≤")
    private var listMarketOrderType = arrayListOf("Fill and Kill (FAK)", "Fill or Kill (FOK)", "Market to Limit")

    companion object {
        fun newInstance() = OrderBuyFragment()
    }

    override fun setupObserver() {
        super.setupObserver()

        sharedViewModel.data.observe(viewLifecycleOwner) { value ->

            if (value.stockCode?.isNotEmpty() == true) {
                stockCodes = value.stockCode.toString()

                val isEtf = stockCodes.first() == 'X'
                val isWarrantOrRight = stockCodes.length > 4
                // special : etf/warrant/right stock

                isSpecialStock = isEtf || isWarrantOrRight
                binding.adjusterBuyPriceOrder.setIsSpecialStock(isSpecialStock)
                binding.adjusterSellPriceTp.setIsSpecialStock(isSpecialStock)
                binding.adjusterSellPriceSl.setIsSpecialStock(isSpecialStock)

                binding.swtchSliceOrder.isChecked = false
                binding.swtchStopLoss.isChecked = false
                binding.swtchGtc.isChecked = false
            }

            if (value.relId.isNotEmpty()) {
                relId = value.relId
            }

            if (value.avgPrice != null && value.avgPrice > 0.0) {
                this.avgPrice = value.avgPrice
            }

            val priceInt = value.price?.toInt()
            val lotInt = value.lot?.toInt()?.div(100)
            val bestBid = value.bestBid?.toInt()
            val amendPrice = value.amendPrice?.toInt()
            val lastPrice = value.lastPrice?.toInt()
            val closePrice = value.closePrice?.toInt()

            if (value.closePrice != null && value.closePrice > 0.0) {
                this.closePrice = value.closePrice
            }
            if (value.amendPrice != 0.0 || value.isAmendMarketOrder) {
                listOrderType = arrayListOf("Limit Order")
                isAmendOrder = true
                if (value.isAmendPartial == true) {
                    isAmendPartial = true
                    binding.btnMaxCash.visibility = View.GONE
                    binding.btnMaxLimit.visibility = View.GONE
                    binding.adjusterLotQtyOrder.disable()
                    binding.adjusterLotQtyOrder.changeColor(R.color.noChanges)
                }
                lastAmendPrice = value.amendPrice ?: 0.0
                lastAmendQty = value.lot ?: 0.0
                lastAmendQty /= 100

                binding.adjusterBuyPriceOrder.setEdt(amendPrice.toString())
                binding.groupSwitchOption.visibility = View.GONE
                binding.btnMaxCash.visibility = View.GONE
                binding.btnMaxLimit.visibility = View.GONE

                //setup ui amend market order
                if (value.isAmendMarketOrder) {
                    isMarketOrder = true
                    orderType = "5"
                    timeInForce = value.timeInForce?: "0"
                    listOrderType = arrayListOf("Market Order")
                    listMarketOrderType = arrayListOf("Fill and Kill (FAK)")

                    marketOrderType = when (value.timeInForce) {
                        "3" -> "Fill and Kill (FAK)"
                        "4" -> "Fill or Kill (FOK)"
                        "0" -> "Market to Limit"
                        else -> ""
                    }
                    binding.tvOrderTypeVal.text = listOrderType[0]
                    binding.tvMarketOrderTypeVal.text = listMarketOrderType[0]

                    binding.adjusterBuyPriceOrder.disable()
                    binding.adjusterBuyPriceOrder.changeColor(R.color.noChanges)
                    binding.groupMarketOrder.visibility = View.VISIBLE
                    binding.tvErrorInputAutoPrice.visibility = View.GONE
                }
            } else {
                if (!isAmendOrder) {
                    binding.groupSwitchOption.visibility = View.VISIBLE
                }
            }

            if (value.isSuccessOrder == true) {
                clearData()
            }

            if (!isAmendOrder && !isMarketOrder) {
                when {
                    priceInt != 0 -> {
                        binding.adjusterBuyPriceOrder.setEdt(priceInt.toString())
                    }
                    bestBid != 0 -> {
                        binding.adjusterBuyPriceOrder.setEdt(bestBid.toString())
                    }
                    lastPrice != 0 -> {
                        binding.adjusterBuyPriceOrder.setEdt(lastPrice.toString())
                    }
                    closePrice != 0 -> {
                        binding.adjusterBuyPriceOrder.setEdt(closePrice.toString())
                    }
                    else -> "0"
                }
                priceAdjuster = binding.adjusterBuyPriceOrder.getDouble()
            } else {
                if  (priceInt != 0 && !isMarketOrder) {
                    binding.adjusterBuyPriceOrder.setEdt(priceInt.toString())
                }
            }

            if (closePrice != 0) {
                priceARAARB = closePrice?.toDouble()?: 0.0
            }

            if (lotInt != 0) {
                binding.adjusterLotQtyOrder.setEdt(lotInt.toString())
            }

            if (value.timeInForce.toString() == "2") {
                if (value.orderPeriod != null && value.orderPeriod != 0L) {
                    orderPeriod = value.orderPeriod
                    binding.tvDateGtc.text = DateUtils.toStringDate(orderPeriod, "dd/MM/yyyy")
                }
            }

            getBuyingLimitInfo()
        }

        viewModel.getMaxOrderByStockResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }

                is Resource.Success -> {
                    when (it.data?.status) {
                        0 -> {
                            val maxLot = if (!isAmendOrder) it.data.maxLot.toLong() else it.data.maxLotWhenAmend.toLong()
                            if (maxLot < 0) {
                                binding.adjusterLotQtyOrder.setEdt("0")
                            } else {
                                binding.adjusterLotQtyOrder.setEdt(maxLot.toString())
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

        viewModel.getMaxOrderByStockBuyLimitResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }

                is Resource.Success -> {
                    when (it.data?.status) {
                        0 -> {
                            buyingPower = if (!isAmendOrder) it.data.buyingPower else it.data.buyingPowerWhenAmend
                            cashBalance = if (isAmendOrder) it.data.cashBalanceWhenAmend else it.data.potCashBalance
                            binding.tvOrderBuyingPower.text = "Rp${buyingPower.formatPriceWithoutDecimal()}"
                            binding.tvOrderCashBalance.text = "Rp${cashBalance.formatPriceWithoutDecimal()}"
                            priceMarketOrder = it.data.price

                            sendToParent()
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

        sharedViewModel.updateOrder.observe(viewLifecycleOwner) {
            if (it.equals("B")) {
                sendToParent()
            }
        }

        sharedViewModel.isNotation.observe(viewLifecycleOwner) {
            isNotation = it
        }

        sharedViewModel.isMarketClosed.observe(viewLifecycleOwner) {
            isMarketClosed = it
        }

        sharedViewModel.isPinSuccess.observe(viewLifecycleOwner) {
            isPinSuccess = it
        }
    }

    private fun getBuyingLimitInfo() {
        val price = binding.adjusterBuyPriceOrder.getDouble()
        if (price != 0.0 || isMarketOrder) {
            viewModel.getMaxOrderBuyLimit(
                prefManager.userId,
                prefManager.accno,
                prefManager.sessionId,
                stockCodes,
                price,
                boardType,
                relId
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun setupComponent() {
        super.setupComponent()

        binding.apply {

            val textWatcherPrice = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // Do something before text changes
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Do something when text changes
                }

                override fun afterTextChanged(s: Editable?) {
                    sendToParent()

                }
            }
            adjusterBuyPriceOrder.setTextWatcher(textWatcherPrice)

            val textWatcherQty = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // Do something before text changes
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Do something when text changes
                }

                override fun afterTextChanged(s: Editable?) {
                    sendToParent()
                    binding.adjusterSellQtyTp.setEdt(binding.adjusterLotQtyOrder.getString())
                    binding.adjusterSellQtySl.setEdt(binding.adjusterLotQtyOrder.getString())
                }
            }
            adjusterLotQtyOrder.setTextWatcher(textWatcherQty)

            val textWatcherSplitNumber = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // Do something before text changes
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Do something when text changes
                }

                override fun afterTextChanged(s: Editable?) {
                    sendToParent()
                }
            }
            adjusterNoSplit.setTextWatcher(textWatcherSplitNumber)

            val textWatcherSplitBlockSize = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // Do something before text changes
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Do something when text changes
                }

                override fun afterTextChanged(s: Editable?) {
                    sendToParent()
                }
            }
            adjusterBlockSize.setTextWatcher(textWatcherSplitBlockSize)
        }
        val textWatcherPriceTp = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // Do something before text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do something when text changes
            }

            override fun afterTextChanged(s: Editable?) {
                stopLossTakeProfit()
            }
        }
        binding.adjusterSellPriceTp.setTextWatcher(textWatcherPriceTp)

        val textWatcherlotTp = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // Do something before text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do something when text changes
            }

            override fun afterTextChanged(s: Editable?) {
                stopLossTakeProfit()
            }
        }
        binding.adjusterSellQtyTp.setTextWatcher(textWatcherlotTp)

        val textWatcherPriceSl = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // Do something before text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do something when text changes
            }

            override fun afterTextChanged(s: Editable?) {
                stopLossTakeProfit()
            }
        }
        binding.adjusterSellPriceSl.setTextWatcher(textWatcherPriceSl)

        val textWatcherlotSl = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // Do something before text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do something when text changes
            }

            override fun afterTextChanged(s: Editable?) {
                stopLossTakeProfit()
            }
        }
        binding.adjusterSellQtySl.setTextWatcher(textWatcherlotSl)

        val textWatcherCompareTp = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // Do something before text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do something when text changes
            }

            override fun afterTextChanged(s: Editable?) {
                binding.edtCompareTp.removeTextChangedListener(this)

                val removeSeparator = s.toString().removeSeparator()
                val formattedText = removeSeparator.formatPriceWithoutDecimal()

                binding.edtCompareTp.setText(formattedText)
                binding.edtCompareTp.setSelection(formattedText.length)
                binding.edtCompareTp.addTextChangedListener(this)
                stopLossTakeProfit()
            }
        }
        binding.edtCompareTp.addTextChangedListener(textWatcherCompareTp)

        val textWatcherCompareSl = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // Do something before text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do something when text changes
            }

            override fun afterTextChanged(s: Editable?) {
                binding.edtCompareSl.removeTextChangedListener(this)

                val removeSeparator = s.toString().removeSeparator()
                val formattedText = removeSeparator.formatPriceWithoutDecimal()

                binding.edtCompareSl.setText(formattedText)
                binding.edtCompareSl.setSelection(formattedText.length)
                binding.edtCompareSl.addTextChangedListener(this)
                stopLossTakeProfit()
            }
        }
        binding.edtCompareSl.addTextChangedListener(textWatcherCompareSl)

        val textWatcherCompareAuto = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // Do something before text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do something when text changes
            }

            override fun afterTextChanged(s: Editable?) {
                binding.edtCompareAuto.removeTextChangedListener(this)

                val removeSeparator = s.toString().removeSeparator()
                val formattedText = removeSeparator.formatPriceWithoutDecimal()

                binding.edtCompareAuto.setText(formattedText)
                binding.edtCompareAuto.setSelection(formattedText.length)
                binding.edtCompareAuto.addTextChangedListener(this)

                stopLossTakeProfit()
            }
        }
        binding.edtCompareAuto.addTextChangedListener(textWatcherCompareAuto)

        binding.adjusterSellQtyTp.disable() // disable lot SLTP
        binding.adjusterSellQtySl.disable()

        checkboxStopLossisEnable(false)
        checkboxTakeProfitisEnable(false)
        binding.adjusterBlockSize.disable()
        binding.adjusterNoSplit.disable()

        commisionFee = prefManager.buyCommission.ifEmpty { "0.0" }.toDouble()
    }

    override fun onResume() {
        super.onResume()
        if (isPinSuccess) {
            getBuyingLimitInfo()
        }
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.ivSymbolAuto.setOnClickListener {
            selectedOpr = if (selectedOpr == 4) 0 else selectedOpr + 1
            binding.ivSymbolAuto.text = listOpr.get(selectedOpr)
            stopLossTakeProfit()
        }

        binding.swtchSliceOrder.setOnCheckedChangeListener { _, isChecked ->
            binding.groupSliceOrder.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (isChecked) {
                binding.swtchStopLoss.isChecked = false
                binding.groupSltp.visibility = View.GONE
                binding.swtchGtc.isChecked = false

                binding.groupEndTimeSlice.visibility = if (slicingType.equals(0)) View.GONE else View.VISIBLE
            } else {
                binding.cbNoSplit.isChecked = false
                binding.cbBlockSize.isChecked = false
                slicingType = 0
                endTimeSlice = 0L
                binding.tvTypeSlicingVal.text = "At Once"
                binding.tvEndTimeVal.text = "00:00"

                binding.tvErrorSliceOrder.visibility = View.GONE
                binding.tvErrorSplitOrder.visibility = View.GONE
            }
            isSliceOrder = isChecked
            sendToParent()
        }

        binding.swtchStopLoss.setOnCheckedChangeListener { _, isChecked ->
            binding.groupSltp.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (isChecked) {
                binding.swtchSliceOrder.isChecked = false
                binding.groupSliceOrder.visibility = View.GONE
                binding.swtchGtc.isChecked = false
            } else {
                binding.cbStopLossSltp.isChecked = false
                binding.cbTakeProfitSltp.isChecked = false

                selectedCompareTp = "0"
                selectedCompareSl = "0"
                binding.tvCompareTpVal.text = "Last Price"
                binding.tvCompareSlVal.text = "Last Price"
            }
            isSLTP = isChecked
            sendToParent()
        }

        binding.swtchGtc.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                timeInForce = "2"
                binding.swtchSliceOrder.isChecked = false
                binding.groupSliceOrder.visibility = View.GONE
                binding.swtchStopLoss.isChecked = false
                binding.groupSltp.visibility = View.GONE
                binding.groupGtc.visibility = View.VISIBLE

                orderPeriod = getTomorrowTimeInMillis()
                binding.tvDateGtc.text = DateUtils.toStringDate(orderPeriod, "dd/MM/yyyy")

                sharedViewModel.setBottomScroll(true)
            } else {
                binding.groupGtc.visibility = View.GONE
                binding.tvDateGtc.text = ""
                timeInForce = "0"
                orderPeriod = 0L
            }
            sendToParent()
        }

        binding.tvOrderTypeVal.setOnClickListener {
            showSimpleDropDownOrderType(
                requireContext(),
                listOrderType,
                binding.tvOrderTypeVal,
                isMarketClosed
            ) { index, value ->
                if (!isAmendOrder) {
                    binding.tvOrderTypeVal.text = value

                    binding.groupCompareAutoPrice.visibility = if (index == 2) View.VISIBLE else View.GONE
                    binding.groupMarketOrder.visibility = if (index == 1) View.VISIBLE else View.GONE

                    isAutoOrder = index == 2
                    binding.swtchSliceOrder.isClickable = index != 2
                    binding.swtchSliceOrder.isChecked = false
                    binding.tvErrorInputAutoPrice.visibility = View.GONE

                    if (index == 1) {
                        isMarketOrder = true
                        binding.tvMarketOrderTypeVal.text = "Fill and Kill (FAK)"
                        marketOrderType = "Fill and Kill (FAK)"
                        orderType = "5"
                        timeInForce = "3"

                        binding.adjusterBuyPriceOrder.disable()
                        binding.adjusterBuyPriceOrder.changeColor(R.color.noChanges)
                        getBuyingLimitInfo()

                        enabledSwitchOption(true)
                    } else {
                        isMarketOrder = false
                        orderType = "0"
                        timeInForce = "0"
                        binding.adjusterBuyPriceOrder.setEdt(priceAdjuster.toString())
                        binding.adjusterBuyPriceOrder.changeColor(R.color.black)

                        binding.adjusterBuyPriceOrder.enable()

                        enabledSwitchOption(false)
                    }
                    sendToParent()
                }
            }
        }

        binding.tvMarketOrderTypeVal.setOnClickListener {
            showSimpleDropDownWidth80(
                requireContext(),
                listMarketOrderType,
                binding.tvMarketOrderTypeVal
            ) {index, value ->
                binding.tvMarketOrderTypeVal.text = value
                marketOrderType = value
                when (index) {
                    0 -> {
                        timeInForce = "3"
                    }
                    1 -> {
                        timeInForce = "4"
                    }
                    2 -> {
                        timeInForce = "0"
                    }
                }
                sendToParent()
            }
        }

        binding.tvCompareTpVal.setOnClickListener {
            if (tpChecked) {
                showSimpleDropDownWidth80(
                    requireContext(),
                    listCompareSLTP,
                    binding.tvCompareTpVal
                ) { index, value ->
                    binding.tvCompareTpVal.text = value
                    selectedCompareTp = index.GET_COMPARE_SLTP()
                    stopLossTakeProfit()
                }
            }
        }

        binding.tvCompareSlVal.setOnClickListener {
            if (slChecked) {
                showSimpleDropDownWidth80(
                    requireContext(),
                    listCompareSLTP,
                    binding.tvCompareSlVal
                ) { index, value ->
                    binding.tvCompareSlVal.text = value
                    selectedCompareSl = index.GET_COMPARE_SLTP()
                    stopLossTakeProfit()
                }
            }
        }

        binding.tvBoardVal.setOnClickListener {
            showSimpleDropDownWidth80(requireContext(), listBoard, binding.tvBoardVal) { index, value ->
                binding.tvBoardVal.text = value
                boardType = boardType(value)
                if (boardType == "TN"){
                    timeInForce = "S"
                }
                sendToParent()
            }
        }


        binding.tvInfoLearnMore.setOnClickListener {
            showDialogInfoBottomBuyingLimitOrder(parentFragmentManager)
        }

        binding.btnMaxCash.setOnClickListener {
            val price = binding.adjusterBuyPriceOrder.getDouble()
            viewModel.getMaxOrder(
                prefManager.userId,
                prefManager.accno,
                prefManager.sessionId,
                stockCodes,
                price,
                "C",
                boardType,
                relId
                )
        }

        binding.btnMaxLimit.setOnClickListener {
            val price = binding.adjusterBuyPriceOrder.getDouble()
            viewModel.getMaxOrder(
                prefManager.userId,
                prefManager.accno,
                prefManager.sessionId,
                stockCodes,
                price,
                "L",
                boardType,
                relId
            )
        }

        binding.cbStopLossSltp.setOnCheckedChangeListener { _, isChecked ->
            slChecked = isChecked
            checkboxStopLossisEnable(isChecked)
            stopLossTakeProfit()
            if (!isChecked) {
                binding.tvErrorSlPrice.visibility = View.GONE
                binding.adjusterSellPriceSl.setEdt("0")
                binding.edtCompareSl.setText("")
            } else {
                stopLossDefaultValue()
            }
        }

        binding.cbTakeProfitSltp.setOnCheckedChangeListener { _, isChecked ->
            tpChecked = isChecked
            checkboxTakeProfitisEnable(isChecked)
            stopLossTakeProfit()
            if (!isChecked) {
                binding.tvErrorTpPrice.visibility = View.GONE
                binding.adjusterSellPriceTp.setEdt("0")
                binding.edtCompareTp.setText("")
            } else {
                takeProfitDefaultValue()
            }
        }

        binding.tvTypeSlicingVal.setOnClickListener {
            showSimpleDropDownWidth80(
                requireContext(),
                listSliceType,
                binding.tvTypeSlicingVal
            ) { index, value ->
                binding.tvTypeSlicingVal.text = value
                slicingType = index
                if (index == 1) {
                    getCurrentHourAndMinutes(){hour, minutes, timeInMillis ->
                        binding.tvEndTimeVal.text = "$hour:$minutes"
                        endTimeSlice = timeInMillis
                    }
                    binding.groupEndTimeSlice.visibility = View.VISIBLE
                } else {
                    endTimeSlice = 0L
                    binding.tvEndTimeVal.text = "00:00"
                    binding.groupEndTimeSlice.visibility = View.GONE
                }
                sendToParent()
            }

        }

        binding.tvEndTimeVal.setOnClickListener {
            initOpenTimePicker(childFragmentManager, binding.tvEndTimeVal, "Select End Time"){
                endTimeSlice = it
                if (compareTimeToCurrentTime(it)) {
                    binding.tvErrorEndTime.visibility = View.GONE
                } else {
                    binding.tvErrorEndTime.visibility = View.VISIBLE
                }
                sendToParent()
            }
        }

        binding.cbNoSplit.setOnCheckedChangeListener { _, isChecked ->
            cbSplit = isChecked
            if (isChecked) {
                binding.cbBlockSize.isChecked = false
                binding.adjusterBlockSize.disable()
                binding.adjusterNoSplit.enable()
                binding.adjusterNoSplit.setEdt("2")
            } else {
                binding.adjusterNoSplit.disable()
                binding.tvErrorSplitOrder.visibility = View.GONE
            }
            cbSplit = isChecked
        }

        binding.cbBlockSize.setOnCheckedChangeListener { _, isChecked ->
            cbRepeat = isChecked
            if (isChecked) {
                binding.cbNoSplit.isChecked = false
                binding.adjusterNoSplit.disable()
                binding.adjusterBlockSize.enable()
                binding.adjusterBlockSize.setEdt("2")
            } else {
                binding.adjusterBlockSize.disable()
                binding.tvErrorSliceOrder.visibility = View.GONE
            }
        }

        binding.tvDateGtc.setOnClickListener {
            initCalenderDialogPlus1(
                requireContext(),
                binding.tvDateGtc.text.toString(),
                binding.tvDateGtc,
                System.currentTimeMillis()
            ) {
                orderPeriod = it
                sendToParent()
            }

        }

        binding.tvOrderType.setOnClickListener {
            showDialogOrderInfoOrderType(parentFragmentManager)
        }

        binding.tvStopLossTitle.setOnClickListener {
            showDialogOrderInfoSltp(parentFragmentManager)
        }

        binding.tvSliceOrderTitle.setOnClickListener {
            showDialogOrderInfoSliceOrder(parentFragmentManager)
        }

        binding.tvGtcTitle.setOnClickListener {
            showDialogOrderInfoGtc(parentFragmentManager)
        }

        binding.clErrorBalanceNotEnough.setOnClickListener {
            findNavController().navigate(R.id.top_up_fragment)
        }
    }

    fun checkboxTakeProfitisEnable(state: Boolean) {
        binding.apply {
            if (state) {
                edtCompareTp.isFocusableInTouchMode = true
            } else {
                edtCompareTp.isFocusable = false
            }
            if (state) adjusterSellPriceTp.enable() else adjusterSellPriceTp.disable()
        }
    }

    fun checkboxStopLossisEnable(state: Boolean) {
        binding.apply {
            if (state) {
                edtCompareSl.isFocusableInTouchMode = true
            } else {
                edtCompareSl.isFocusable = false
            }
            if (state) adjusterSellPriceSl.enable() else adjusterSellPriceSl.disable()
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val stringCompareRes = resources.getStringArray(R.array.spinner_order_buy_compare)
        val stringBoardRes = resources.getStringArray(R.array.spinner_order_buy_board)
        val stringSliceRes = resources.getStringArray(R.array.spinner_order_buy_slicing_type)
        val selectItem = p0?.selectedItem.toString()

        if (stringCompareRes.contains(selectItem)) {
            compareType = selectItem
        }
        if (stringBoardRes.contains(selectItem)) {
            boardType = selectItem
        }

        if (stringSliceRes.contains(selectItem)) {
            slicingType = 1
        } else {
            slicingType = 0
        }
        sendToParent()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    fun sendToParent() {
//        binding.adjusterLotQtyOrder.changeColor(R.color.txtBlackWhite)
//        binding.adjusterBuyPriceOrder.changeColor(R.color.txtBlackWhite)
        val price = if (isMarketOrder) priceMarketOrder else binding.adjusterBuyPriceOrder.getDouble()
        var proceedAmount = price * binding.adjusterLotQtyOrder.getDoubleTimes100()
        val comFee =  proceedAmount.times(commisionFee)
        totalPrice = proceedAmount + comFee
        val blockSize = binding.adjusterBlockSize.getInt()
        if (blockSize != 0) {
            totalPrice *= blockSize
            proceedAmount *= blockSize
        }
        checkCondition(totalPrice)

        if (!tpChecked) takeProfitCriteria = AdvancedCriteriaRequest()
        if (!slChecked) stopLossCriteria = AdvancedCriteriaRequest()

        sharedViewModel.setDataOrder(
            OrderAdapterData(
                stockCodes,
                binding.adjusterBuyPriceOrder.getDouble(),
                binding.adjusterLotQtyOrder.getDouble(),
                if (isMarketOrder) 0.0 else totalPrice,
                0.0,
                "B",
                isSliceOrder,
                isSLTP,
                slicingType,
                orderType,
                binding.adjusterNoSplit.getInt(),
                binding.adjusterBlockSize.getInt(),
                boardType = boardType,
                isBtnOrder = isBtnOrder,
                bracketCriteria = bracketCriteria,
                takeProfitCriteria = takeProfitCriteria,
                stopLossCriteria = stopLossCriteria,
                timeInForce = timeInForce,
                orderPeriod = orderPeriod,
                proceedAmount = proceedAmount,
                endTimeSliceOrder = endTimeSlice,
                isAutoOrder = isAutoOrder,
                isBalanceNotEnough = totalPrice > buyingPower,
                isMarketOrder = isMarketOrder,
                marketOrderType = marketOrderType
            )
        )
    }

    fun sliceOrderValidation(qty: Double): Boolean {
            binding.tvErrorSliceOrder.visibility = View.GONE
            binding.tvErrorSplitOrder.visibility = View.GONE
            binding.tvErrorInput.visibility = View.GONE
            binding.adjusterNoSplit.changeColor(R.color.txtBlackWhite)
            binding.adjusterBlockSize.changeColor(R.color.txtBlackWhite)
            binding.adjusterLotQtyOrder.changeColor(R.color.txtBlackWhite)

            return if ((binding.adjusterNoSplit.getInt() != 0 && binding.adjusterNoSplit.getInt() in 2..10) ||
                (binding.adjusterBlockSize.getInt() != 0 && binding.adjusterBlockSize.getInt() in 2..10) ) {
                if (cbSplit) {
                    if (sliceOrderValidation(qty, binding.adjusterNoSplit.getInt())) {
                        binding.tvErrorSplitOrder.visibility = View.GONE
                        binding.adjusterNoSplit.changeColor(R.color.txtBlackWhite)

                        val qtyOrder = binding.adjusterLotQtyOrder.getDouble()
                        val noSplit = binding.adjusterNoSplit.getDouble()

                        if (qtyOrder.div(noSplit) > 50000.0){
                            binding.tvErrorSplitOrder.text = "Lot quantity after split can’t exceed 50,000"
                            binding.tvErrorSplitOrder.visibility = View.VISIBLE
                            binding.adjusterNoSplit.changeColor(R.color.textRed)
                            binding.adjusterLotQtyOrder.changeColor(R.color.textRed)
                            false
                        } else {
                            binding.tvErrorSplitOrder.visibility = View.GONE
                            binding.adjusterNoSplit.changeColor(R.color.txtBlackWhite)
                            binding.adjusterLotQtyOrder.changeColor(R.color.txtBlackWhite)

                            if (slicingType.equals(1)) {
                                compareTimeToCurrentTime(endTimeSlice)
                            } else {
                                true
                            }
                        }
                    } else {
                        binding.tvErrorSplitOrder.text = "Mininum lot quantity after split is 2"
                        binding.tvErrorSplitOrder.visibility = View.VISIBLE
                        binding.adjusterLotQtyOrder.changeColor(R.color.textRed)
                        false
                    }
                } else {
                    if (cbRepeat) {
                        if (qty >= 2) {
                            if (slicingType.equals(1)) {
                                compareTimeToCurrentTime(endTimeSlice)
                            } else {
                                true
                            }
                        } else {
                            binding.adjusterLotQtyOrder.changeColor(R.color.textRed)
                            binding.tvErrorInput.text = "Minimum repeat order quantity is 2"
                            binding.tvErrorInput.visibility = View.VISIBLE
                            false
                        }

                    } else {
                        false
                    }

                }

            } else {
                if (cbSplit) {
                    binding.adjusterNoSplit.changeColor(R.color.textRed)
                    binding.tvErrorSplitOrder.visibility = View.VISIBLE
                    binding.tvErrorSplitOrder.text = when {
                        cbSplit && binding.adjusterNoSplit.getInt() < 2 -> "Minimum number of splits is 2"
                        cbSplit && binding.adjusterNoSplit.getInt() > 10 -> "Maximum number of splits is 10"
                        else -> {""}
                    }
                } else if (cbRepeat) {
                    binding.adjusterBlockSize.changeColor(R.color.textRed)
                    binding.tvErrorSliceOrder.visibility = View.VISIBLE
                    binding.tvErrorSliceOrder.text = when {
                        cbRepeat && binding.adjusterBlockSize.getInt() < 2 -> "Minimum number of repeats is 2"
                        cbRepeat && binding.adjusterBlockSize.getInt() > 10 -> "Maximum number of repeats is 10"
                        else -> {""}
                    }
                }
                false
            }

            // GTC

    }

    fun gtcOrderValidation(orderPeriod: Long): Boolean{
        return if (isValidTimeForTomorrow(orderPeriod)) {
            if (validDateInMonth(orderPeriod)){
                binding.tvDateGtc.setTextColor(ContextCompat.getColor(requireContext(), R.color.txtBlackWhite))
                binding.tvErrorExpiryDate.visibility = View.GONE
                true
            } else {
                binding.tvErrorExpiryDate.visibility = View.VISIBLE
                binding.tvDateGtc.setTextColor(ContextCompat.getColor(requireContext(), R.color.textRed))
                binding.tvErrorExpiryDate.text = "Date can’t exceed 30 days"
                sharedViewModel.setBottomScroll(true)
                false
            }
        } else {
            binding.tvErrorExpiryDate.visibility = View.VISIBLE
            binding.tvDateGtc.setTextColor(ContextCompat.getColor(requireContext(), R.color.textRed))
            binding.tvErrorExpiryDate.text = if (isToday(orderPeriod)) "Date can’t be today" else "Date can’t be before today"
            sharedViewModel.setBottomScroll(true)
            false
        }
    }

    fun sltpOrderValidation(price: Double): Boolean {
        if (!tpChecked && !slChecked) {
            return false
        } else
        {
            var validationTp = true
            var validationSl = true

            val compareTp = binding.edtCompareTp.text.toString().removeSeparator().toDoubleOrNull() ?: 0.0
            val priceTp = binding.adjusterSellPriceTp.getDouble()
            val compareSl = binding.edtCompareSl.text.toString().removeSeparator().toDoubleOrNull() ?: 0.0
            val priceSl = binding.adjusterSellPriceSl.getDouble()

            if (tpChecked) {
                if (priceTp != 0.0) {
                    if (checkARAandARBSltp(priceTp, priceARAARB, binding.tvErrorTpPrice, binding.adjusterSellPriceTp)) {
                        if (priceTp > price) {
                            binding.adjusterSellPriceTp.changeColor(R.color.txtBlackWhite)
                            binding.tvErrorTpPrice.visibility = View.GONE

                            if (slChecked && priceSl >= priceTp) {
                                binding.tvErrorTpPrice.visibility = View.VISIBLE
                                binding.adjusterSellPriceTp.changeColor(R.color.textRed)
                                binding.tvErrorTpPrice.text = "Take profit price should be higher than stop loss price"
                                validationTp = false
                            } else {
                                binding.tvErrorTpPrice.visibility = View.GONE
                                binding.adjusterSellPriceTp.changeColor(R.color.txtBlackWhite)

                                if (compareTp == 0.0) {
                                    binding.tvErrorTpPrice.visibility = View.VISIBLE
                                    binding.tvErrorTpPrice.text = "Compare price should be filled"
                                    binding.adjusterSellPriceTp.changeColor(R.color.textRed)
                                    validationTp = false
                                } else {
                                    binding.tvErrorTpPrice.visibility = View.GONE

                                    if (slChecked && compareSl >= compareTp) {
                                        validationTp = false
                                        binding.tvErrorTpPrice.visibility = View.VISIBLE
                                        binding.tvErrorTpPrice.text = "Compare take profit price should be higher than compare stop loss price"
                                    } else {
                                        binding.tvErrorTpPrice.visibility = View.GONE
                                        validationTp = true
                                    }
                                }
                            }
                        } else {
                            binding.tvErrorTpPrice.visibility = View.VISIBLE
                            binding.adjusterSellPriceTp.changeColor(R.color.textRed)
                            binding.tvErrorTpPrice.text = "Take profit price should be higher than buy price"
                            validationTp = false
                        }
                    } else {
                        validationTp = false
                    }
                } else {
                    binding.tvErrorTpPrice.visibility = View.VISIBLE
                    binding.adjusterSellPriceTp.changeColor(R.color.textRed)
                    binding.tvErrorTpPrice.text = "Price should be filled"
                    validationTp = false
                }
            }

            if (slChecked) {
                if (priceSl != 0.0) {
                    if (checkARAandARBSltp(priceSl, priceARAARB, binding.tvErrorSlPrice, binding.adjusterSellPriceSl)) {
                        if (priceSl < price) {
                            binding.adjusterSellPriceSl.changeColor(R.color.txtBlackWhite)
                            binding.tvErrorSlPrice.visibility = View.GONE

                            if (tpChecked && priceSl >= priceTp) {
                                binding.tvErrorSlPrice.visibility = View.VISIBLE
                                binding.adjusterSellPriceSl.changeColor(R.color.textRed)
                                binding.tvErrorSlPrice.text = "Stop loss price should be lower than take profit price"
                                validationSl = false
                            } else {
                                binding.tvErrorSlPrice.visibility = View.GONE
                                binding.adjusterSellPriceSl.changeColor(R.color.txtBlackWhite)

                                if (compareSl == 0.0) {
                                    binding.tvErrorSlPrice.visibility = View.VISIBLE
                                    binding.tvErrorSlPrice.text = "Compare price should be filled"
                                    binding.adjusterSellPriceSl.changeColor(R.color.textRed)
                                    validationSl = false
                                } else {
                                    binding.tvErrorSlPrice.visibility = View.GONE

                                    if (tpChecked && compareSl >= compareTp) {
                                        validationSl = false
                                        binding.tvErrorSlPrice.visibility = View.VISIBLE
                                        binding.tvErrorSlPrice.text = "Compare stop loss price should be lower than compare take profit price"
                                    } else {
                                        binding.tvErrorSlPrice.visibility = View.GONE
                                        validationSl = true
                                    }
                                }
                            }
                        } else {
                            binding.tvErrorSlPrice.visibility = View.VISIBLE
                            binding.adjusterSellPriceSl.changeColor(R.color.textRed)
                            binding.tvErrorSlPrice.text = "Stop loss price should be lower than buy price"
                            validationSl = false
                        }
                    } else {
                        validationSl = false
                    }
                } else {
                    binding.tvErrorSlPrice.visibility = View.VISIBLE
                    binding.adjusterSellPriceSl.changeColor(R.color.textRed)
                    binding.tvErrorSlPrice.text = "Price should be filled"
                    validationSl = false
                }
            }

            return validationTp && validationSl
        }
    }

    fun autoOrderValidation(): Boolean{

        val comparePrice = binding.edtCompareAuto.text.toString().removeSeparator().toDoubleOrNull() ?: 0.0
        if (comparePrice != 0.0) {
            binding.tvErrorInputAutoPrice.visibility = View.GONE
            binding.edtCompareAuto.setTextColor(ContextCompat.getColor(requireContext(), R.color.txtBlackWhite))
            return true
        } else {
            binding.tvErrorInputAutoPrice.visibility = View.VISIBLE
            binding.edtCompareAuto.setTextColor(ContextCompat.getColor(requireContext(), R.color.textRed))
            binding.tvErrorInputAutoPrice.text =  "Compare Price should be filled"
            return false
        }
    }

    fun orderValidation(qty: Double, price: Double, total: Double) {
        if (qty > 50000 && !cbSplit) {
            binding.tvErrorInput.visibility = View.VISIBLE
            binding.adjusterLotQtyOrder.changeColor(R.color.textRed)
            if (cbRepeat){
                binding.adjusterBlockSize.changeColor(R.color.textRed)
            } else {
                binding.adjusterBlockSize.changeColor(R.color.txtBlackWhite)
            }
            errorInput(true)
        } else {
            binding.adjusterLotQtyOrder.changeColor(R.color.txtBlackWhite)
            binding.tvErrorInput.visibility = View.GONE
        }

        if (!isMarketOrder && timeInForce != "2") {
            checkARAandARB(priceARAARB)
        } else {
            if (!checkFractionPrice(binding.adjusterBuyPriceOrder.getDouble().toInt())) {
                binding.tvErrorInputPrice.visibility = View.VISIBLE
                binding.adjusterBuyPriceOrder.changeColor(R.color.textRed)
                errorInput(false)
            } else {
                binding.adjusterBuyPriceOrder.changeColor(R.color.txtBlackWhite)
                binding.tvErrorInputPrice.visibility = View.GONE
            }
        }
    }

    private fun checkCondition(total: Double) {
        val qty = binding.adjusterLotQtyOrder.getDouble()
        val price = binding.adjusterBuyPriceOrder.getDouble()

        orderValidation(qty, price, total)
        when {
            isSliceOrder -> sliceOrderValidation(qty)
            isSLTP -> sltpOrderValidation(price)
            timeInForce == "2" -> gtcOrderValidation(orderPeriod)
            isAutoOrder -> autoOrderValidation()
        }

        if (isMarketOrder) {
            val conditionOne = total < cashBalance
            val conditionTwo = total > cashBalance && total < buyingPower
            val limitValidation = conditionOne || conditionTwo
            if (isAmendOrder) {
                isBtnOrder = when {
                    qty == lastAmendQty || qty == 0.0 -> {
                        binding.tvErrorInput.visibility = View.GONE
                        binding.adjusterLotQtyOrder.changeColor(R.color.textRed)
                        false
                    }
                    qty > 50000 -> false
                    else -> {
                        binding.adjusterLotQtyOrder.changeColor(R.color.txtBlackWhite)
                        limitValidation
                    }
                }
            } else {
                isBtnOrder = qty > 0.0 && qty <=50000 && limitValidation
            }
        } else {
            if (qty <= 0.0 || price == 0.0) {
                isBtnOrder = false
            } else {
                if (qty > 50000 && !cbSplit) {
                    binding.tvErrorInput.visibility = View.VISIBLE
                    binding.adjusterLotQtyOrder.changeColor(R.color.textRed)
                    if (cbRepeat){
                        binding.adjusterBlockSize.changeColor(R.color.textRed)
                    } else {
                        binding.adjusterBlockSize.changeColor(R.color.txtBlackWhite)
                    }
                    errorInput(true)
                    isBtnOrder = false
                } else {
                    binding.adjusterLotQtyOrder.changeColor(R.color.txtBlackWhite)
                    binding.tvErrorInput.visibility = View.GONE
                        val conditionOne = total < cashBalance
                        val conditionTwo = total > cashBalance && total < buyingPower
                        if (conditionOne || conditionTwo) {
                            // check if this order is gtc (for bypass ara arb validation)
                            if (timeInForce != "2") {
                                if (checkARAandARB(priceARAARB)) {
                                    if (checkFractionPrice(price.toInt())) {
                                        binding.adjusterBuyPriceOrder.changeColor(R.color.txtBlackWhite)
                                        binding.tvErrorInputPrice.visibility = View.GONE

                                        isBtnOrder = if (isAmendOrder) {
                                            if (isAmendPartial) {
                                                // Amend Partial order validation
                                                lastAmendPrice != price
                                            } else {
                                                // Amend order validation
                                                when {
                                                    qty < lastAmendQty -> {
                                                        binding.adjusterLotQtyOrder.changeColor(R.color.txtBlackWhite)
                                                        true
                                                    }
                                                    qty > lastAmendQty -> {
                                                        if (lastAmendPrice != price) {
                                                            binding.tvErrorInput.visibility = View.GONE
                                                            binding.adjusterLotQtyOrder.changeColor(R.color.txtBlackWhite)
                                                            true
                                                        } else {
                                                            binding.tvErrorInput.visibility = View.VISIBLE
                                                            binding.adjusterLotQtyOrder.changeColor(R.color.textRed)
                                                            binding.tvErrorInput.text =  "Cannot add quantity only"
                                                            false
                                                        }
                                                    }
                                                    qty == lastAmendQty -> {
                                                        if (lastAmendPrice != price) {
                                                            binding.adjusterLotQtyOrder.changeColor(R.color.txtBlackWhite)
                                                            binding.tvErrorInput.visibility = View.GONE
                                                            true
                                                        } else {
                                                            false
                                                        }
                                                    }

                                                    else -> false
                                                }
                                            }


                                        } else if (isSliceOrder) { // Slice Order
                                            sliceOrderValidation(qty)
                                        } else if (timeInForce.equals("2")) { // GTC Order

                                            gtcOrderValidation(orderPeriod)

                                        } else if (isSLTP) { // SLTP ORDER

                                            sltpOrderValidation(price)

                                        } else if (isAutoOrder) { // AUTO ORDER

                                            val comparePrice = binding.edtCompareAuto.text.toString().removeSeparator().toDoubleOrNull() ?: 0.0
                                            if (comparePrice != 0.0) {
                                                binding.tvErrorInputAutoPrice.visibility = View.GONE
                                                binding.edtCompareAuto.setTextColor(ContextCompat.getColor(requireContext(), R.color.txtBlackWhite))
                                                true
                                            } else {
                                                binding.tvErrorInputAutoPrice.visibility = View.VISIBLE
                                                binding.edtCompareAuto.setTextColor(ContextCompat.getColor(requireContext(), R.color.textRed))
                                                binding.tvErrorInputAutoPrice.text =  "Compare Price should be filled"
                                                false
                                            }

                                            // ORDER
                                        } else {
                                            true
                                        }
                                    } else {
                                        isBtnOrder = false
                                        binding.tvErrorInputPrice.visibility = View.VISIBLE
                                        binding.adjusterBuyPriceOrder.changeColor(R.color.textRed)
                                        errorInput(false)
                                    }
                                } else {
                                    isBtnOrder = false
                                }
                            } else {
                                // gtc validation
                                if (checkFractionPrice(price.toInt())) {
                                    binding.adjusterBuyPriceOrder.changeColor(R.color.txtBlackWhite)
                                    binding.tvErrorInputPrice.visibility = View.GONE

                                    isBtnOrder = gtcOrderValidation(orderPeriod)
                                } else {
                                    isBtnOrder = false
                                    binding.tvErrorInputPrice.visibility = View.VISIBLE
                                    binding.adjusterBuyPriceOrder.changeColor(R.color.textRed)
                                    errorInput(false)
                                }
                            }
                        } else {
                            isBtnOrder = false
                        }

                }

            }
        }

        if (isAmendPartial){
            binding.tvErrorInput.text = "Cannot change partial match quantity"
            binding.tvErrorInput.isGone = false
        }

        binding.clInfoMoreThanMaxCash.visibility = when {
            total > cashBalance && total < buyingPower -> View.VISIBLE
            else -> View.GONE
        }

        binding.clErrorBalanceNotEnough.visibility = when {
            total > buyingPower && total > cashBalance -> View.VISIBLE
            else -> View.GONE
        }

        binding.tvOrderBuyingPower.setTextColor(
            if (total > buyingPower) ContextCompat.getColor(requireContext(), R.color.textRed)
            else ContextCompat.getColor(requireContext(), R.color.txtBlackWhite)
        )

        binding.tvOrderCashBalance.setTextColor(
            if (total > buyingPower) ContextCompat.getColor(requireContext(), R.color.textRed)
            else ContextCompat.getColor(requireContext(), R.color.txtBlackWhite)
        )

        binding.adjusterLotQtyOrder.changeColor(
            when {
                total > buyingPower && total > cashBalance -> R.color.textRed
                qty > 50000 && !cbSplit -> R.color.textRed
                cbRepeat && qty < 2 && binding.adjusterBlockSize.getInt() >= 2 -> R.color.textRed
                binding.adjusterNoSplit.getDouble() != 0.0 && qty.div(binding.adjusterNoSplit.getDouble()) > 50000.0 -> R.color.textRed
                else -> R.color.txtBlackWhite
            }
        )
    }

    private fun checkARAandARBSltp(price: Double, closePrice:Double, errorTextView: TextView, qtyAdjuster: CustomQtyAdjuster): Boolean {
        if (closePrice != 0.0 && !isSpecialStock) {
            var top: Double
            var bottom: Double

            val percent  = when {
                closePrice < 50.0 -> 0.1
                closePrice in 50.0..200.0 -> 0.35
                closePrice in 200.0..5000.0 -> 0.25
                closePrice > 5000.0 -> 0.20
                else -> 0.0
            }

            when {
                isNotation && closePrice <= 10 -> {
                    top = closePrice + 1
                    bottom = closePrice - 1
                }
                isNotation && closePrice > 10 -> {
                    top = closePrice + (closePrice * 0.10)
                    bottom = closePrice - (closePrice * 0.10)
                }
                else -> {
                    top = closePrice + (closePrice * percent)
                    bottom = closePrice - (closePrice * 0.15)

                    if (closePrice >= 50) {
                        bottom = if (bottom < 50) 50.0 else bottom
                    } else {
                        bottom = if (bottom < 1) 1.0 else bottom
                    }
                }
            }
            val arb = ceil(bottom)
            when {
                price < arb -> {
                    errorTextView.visibility = View.VISIBLE
                    qtyAdjuster.changeColor(R.color.textRed)

                    if (!checkFractionPrice(arb.toInt())) {
                        val adjustFraction = adjustFractionPrice(arb.toInt(), "", isSpecialStock)
                        errorTextView.text = "Price can't be lower than ${adjustFraction}"
                    } else {
                        errorTextView.text = "Price can't be lower than ${arb.formatPriceWithoutDecimal()}"
                    }
                }
                price > top -> {
                    errorTextView.visibility = View.VISIBLE
                    qtyAdjuster.changeColor(R.color.textRed)

                    if (!checkFractionPrice(top.toInt())) {
                        val adjustFraction = adjustFractionPrice(top.toInt(), "-", isSpecialStock)
                        errorTextView.text = "Price can't be higher than ${adjustFraction}"
                    } else {
                        errorTextView.text = "Price can't be higher than ${top.toInt().formatPriceWithoutDecimal()}"
                    }
                }
                else -> {
                    errorTextView.visibility = View.GONE
                    qtyAdjuster.changeColor(R.color.txtBlackWhite)
                }
            }

            return price in bottom..top
        } else {
            return true
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



    private fun stopLossTakeProfit() {

        val totalOrder =
            binding.adjusterBuyPriceOrder.getDouble().times(binding.adjusterLotQtyOrder.getDoubleTimes100())

        /** BRACKET CRITERIA */

        val priceBuy = binding.adjusterBuyPriceOrder.getDouble().toLong()
        val qtyBuy = binding.adjusterLotQtyOrder.getDoubleTimes100().toLong()

        val triggerOrderBc = TriggerOrder.newBuilder()
            .setStockCode(stockCodes)
            .setBuySell("B")
            .setOrdType(0)
            .setTimeInForce("0")
            .setOrdQty(qtyBuy)
            .setOrdPrice(priceBuy)
            .build()

        bracketCriteria = if (isAutoOrder) {
            val triggerAutoPrice = binding.edtCompareAuto.text.toString().removeSeparator()
            val triggerValAuto = triggerAutoPrice.takeIf { it.isNotEmpty() }?.toLong() ?: 0L
            AdvancedCriteriaRequest(advType = 2, triggerOrder = triggerOrderBc, opr = selectedOpr, triggerVal = triggerValAuto, triggerCategory = 0)
        } else {
            AdvancedCriteriaRequest(advType = 100, triggerOrder = triggerOrderBc)
        }
        /** TAKE PROFIT*/
        val priceTp = binding.adjusterSellPriceTp.getDouble().toLong()
        val qtyTp = binding.adjusterSellQtyTp.getDoubleTimes100().toLong()
        val triggerTp = binding.edtCompareTp.text.toString().removeSeparator()
        val triggerValTp = triggerTp.takeIf { it.isNotEmpty() }?.toLong() ?: 0L

        tpValue = binding.adjusterSellPriceTp.getDouble()
            .times(binding.adjusterSellQtyTp.getDoubleTimes100())
        minusValTp = tpValue.minus(totalOrder)
        minusValPctTp = if (totalOrder != 0.0 && !minusValTp.isNaN() && !totalOrder.isNaN()) {
            (minusValTp / totalOrder * 100)
        } else {
            0.0
        }

        val compareTpText = binding.edtCompareTp.text.toString().removeSeparator()
        val compareTpDouble = compareTpText.toDoubleOrNull() ?: 0.0
        val tpPrice = "Rp${compareTpDouble.formatPriceWithoutDecimal()}"

        val tpProfit =
            "Rp${minusValTp.formatPriceWithoutDecimal()} (+${minusValPctTp.roundedHalfDown()}%)"

        setInfoSLTP(
            binding.tvInfoTakeProfitTp,
            tpPrice,
            tpProfit,
            true
        )

        val triggerOrderTp = TriggerOrder.newBuilder()
            .setStockCode(stockCodes)
            .setBuySell("S")
            .setOrdType(0)
            .setTimeInForce("0")
            .setOrdQty(qtyTp)
            .setOrdPrice(priceTp)
            .build()

        takeProfitCriteria = AdvancedCriteriaRequest(advType = 2, opr = 2, triggerVal = triggerValTp, triggerCategory = selectedCompareTp.toInt() ,triggerOrderTp)

        /** STOP LOSS*/
        val priceSl = binding.adjusterSellPriceSl.getDouble().toLong()
        val qtySl = binding.adjusterSellQtySl.getDoubleTimes100().toLong()
        val triggerSl = binding.edtCompareSl.text.toString().removeSeparator()
        val triggerValSl = triggerSl.takeIf { it.isNotEmpty() }?.toLong() ?: 0L

        slValue = binding.adjusterSellPriceSl.getDouble()
            .times(binding.adjusterSellQtySl.getDoubleTimes100())
        minusValSl = slValue.minus(totalOrder)
        minusValPctSl = if (totalOrder != 0.0 && !minusValSl.isNaN() && !totalOrder.isNaN()) {
            (minusValSl / totalOrder * 100)
        } else {
            0.0
        }

        val compareSlText = binding.edtCompareSl.text.toString().removeSeparator()
        val compareSlDouble = compareSlText.toDoubleOrNull() ?: 0.0
        val slPrice = "Rp${compareSlDouble.formatPriceWithoutDecimal()}"

        val slProfit =
            "Rp${minusValSl.formatPriceWithoutDecimal()} (${minusValPctSl.roundedHalfDown()}%)"

        val triggerOrderSl = TriggerOrder.newBuilder()
            .setStockCode(stockCodes)
            .setBuySell("S")
            .setOrdType(0)
            .setTimeInForce("0")
            .setOrdQty(qtySl)
            .setOrdPrice(priceSl)
            .build()

        stopLossCriteria = AdvancedCriteriaRequest(advType = 2, opr = 4, triggerVal = triggerValSl, triggerCategory = selectedCompareSl.toInt() ,triggerOrderSl)

        setInfoSLTP(
            binding.tvInfoTakeProfitSl,
            slPrice,
            slProfit,
            false
        )
        sendToParent()
    }

    private fun setInfoSLTP(
        binding: CustomTextView,
        sltpPrice: String,
        profitInfo: String,
        isProfit: Boolean
    ) {

        val staticText =
            "If stock price hit $sltpPrice, Sell order will be sent to take an approx. $profitInfo profit"

        val spannableStringBuilder = SpannableStringBuilder(staticText)

        val stockPriceStart = staticText.indexOf(sltpPrice)
        val stockPriceEnd = stockPriceStart + sltpPrice.length

        val profitInfoStart = staticText.indexOf(profitInfo)
        val profitInfoEnd = profitInfoStart + profitInfo.length

        spannableStringBuilder.setSpan(
            StyleSpan(Typeface.BOLD),
            stockPriceStart,
            stockPriceEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val color = if (isProfit) android.graphics.Color.GREEN else android.graphics.Color.RED
        spannableStringBuilder.setSpan(
            ForegroundColorSpan(color),
            profitInfoStart,
            profitInfoEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.text = spannableStringBuilder
    }

    private fun errorInput(isLimit: Boolean){
        if (isLimit){
            val warningText = getString(R.string.fob_warning_more_than_lot_limit)
            val formattedText = Html.fromHtml(warningText)
            binding.tvErrorInput.text = formattedText
        } else {
            binding.tvErrorInputPrice.text = "The price entered does not align with the fractional value"
        }
    }

    private fun sliceOrderValidation(value: Double, split: Int): Boolean {
        return value >= 2 * split
    }

    private fun checkARAandARB(price: Double): Boolean {
        if (price != 0.0 && !isSpecialStock) {
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

            val inputPrice = binding.adjusterBuyPriceOrder.getDouble()
            val arb = ceil(bottom)
            when {
                inputPrice < arb -> {
                    binding.adjusterBuyPriceOrder.changeColor(R.color.textRed)
                    binding.tvErrorInputPrice.visibility = View.VISIBLE
                    if (!checkFractionPrice(arb.toInt())) {
                        val adjustFraction = adjustFractionPrice(arb.toInt(), "", isSpecialStock)
                        binding.tvErrorInputPrice.text = "Price can't be lower than ${adjustFraction}"
                    } else {
                        binding.tvErrorInputPrice.text = "Price can't be lower than ${arb.toInt().formatPriceWithoutDecimal()}"
                    }
                }
                inputPrice > top -> {
                    binding.adjusterBuyPriceOrder.changeColor(R.color.textRed)
                    binding.tvErrorInputPrice.visibility = View.VISIBLE

                    if (!checkFractionPrice(top.toInt())) {
                        val adjustFraction = adjustFractionPrice(top.toInt(), "-", isSpecialStock)
                        binding.tvErrorInputPrice.text = "Price can't be higher than ${adjustFraction}"
                    } else {
                        binding.tvErrorInputPrice.text = "Price can't be higher than ${top.toInt().formatPriceWithoutDecimal()}"
                    }
                }
                else -> {
                    if (!checkFractionPrice(inputPrice.toInt())) {
                        binding.tvErrorInputPrice.visibility = View.VISIBLE
                        binding.adjusterBuyPriceOrder.changeColor(R.color.textRed)
                        errorInput(false)
                    } else {
                        binding.adjusterBuyPriceOrder.changeColor(R.color.txtBlackWhite)
                        binding.tvErrorInputPrice.visibility = View.GONE
                    }
                }
            }

            return inputPrice in bottom..top
        } else {
            return true
        }
    }

    private fun takeProfitDefaultValue() {
        if (avgPrice != 0.0 || closePrice != 0.0) {
            val price = if (avgPrice != 0.0) avgPrice else closePrice
            val changes = price.times(0.05)
            val priceTp = price.plus(changes)
            val fractionPrice = getFractionPrice(priceTp.toInt()).formatPriceWithoutDecimal()

            binding.adjusterSellPriceTp.setEdt(fractionPrice)
            binding.edtCompareTp.setText(fractionPrice)
        }
    }

    private fun stopLossDefaultValue() {
        if (avgPrice != 0.0 || closePrice != 0.0) {
            val price = if (avgPrice != 0.0) avgPrice else closePrice
            val changes = price.times(0.05)
            val priceSl = price.minus(changes)
            val fractionPrice = getFractionPrice(priceSl.toInt()).formatPriceWithoutDecimal()

            binding.adjusterSellPriceSl.setEdt(fractionPrice)
            binding.edtCompareSl.setText(fractionPrice)
        }
    }

    private fun enabledSwitchOption(state: Boolean) {
        if (state) {
            binding.swtchStopLoss.isClickable = false
            binding.swtchSliceOrder.isClickable = false
            binding.swtchGtc.isClickable = false

            binding.swtchStopLoss.isChecked = false
            binding.swtchSliceOrder.isChecked = false
            binding.swtchGtc.isChecked = false

            binding.tvStopLossTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.noChanges))
            binding.tvStopLossDesc.setTextColor(ContextCompat.getColor(requireContext(), R.color.noChanges))
            binding.tvSliceOrderTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.noChanges))
            binding.tvSliceOrderDesc.setTextColor(ContextCompat.getColor(requireContext(), R.color.noChanges))
            binding.tvGtcTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.noChanges))
            binding.tvGtcDesc.setTextColor(ContextCompat.getColor(requireContext(), R.color.noChanges))
        } else {
            binding.swtchStopLoss.isClickable = true
            binding.swtchSliceOrder.isClickable = true
            binding.swtchGtc.isClickable = true

            binding.tvStopLossTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.tvStopLossDesc.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.tvSliceOrderTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.tvSliceOrderDesc.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.tvGtcTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.tvGtcDesc.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }
    }

    private fun clearData() {
        stockCodes = ""
        totalPrice = 0.0
        buyingPower = 0.0
        cashBalance = 0.0
        timeInForce = "0"

        compareType = "0"
        boardType = "RG"
        slicingType = 0

        lastAmendPrice = 0.0
        lastAmendQty = 0.0

        isAmendOrder = false
        isSliceOrder = false
        isSLTP = false
        isBtnOrder = false
        isAutoOrder = false
        tpValue = 0.0
        minusValTp = 0.0
        minusValPctTp = 0.0
        slValue = 0.0
        minusValSl = 0.0
        minusValPctSl = 0.0

        bracketCriteria = AdvancedCriteriaRequest()
        takeProfitCriteria = AdvancedCriteriaRequest()
        stopLossCriteria = AdvancedCriteriaRequest()

        tpChecked = false
        slChecked = false

        selectedOrdType = "0"
        selectedCompareTp = "0"
        selectedCompareSl = "0"
        selectedOpr = 0
        orderType = "0"

        binding.tvOrderTypeVal.text = "Limit Order"
        binding.tvCompareTpVal.text = "Last Price"
        binding.tvCompareSlVal.text = "Last Price"
        binding.adjusterBuyPriceOrder.setEdt("")
        binding.adjusterLotQtyOrder.setEdt("")
        binding.adjusterSellPriceTp.setEdt("")
        binding.adjusterSellQtyTp.setEdt("")
        binding.adjusterSellPriceSl.setEdt("")
        binding.adjusterSellQtySl.setEdt("")
        binding.edtCompareAuto.setText("")
        binding.edtCompareTp.setText("")
        binding.edtCompareSl.setText("")

        binding.swtchGtc.isChecked = false
        binding.swtchStopLoss.isChecked = false
        binding.swtchSliceOrder.isChecked = false
        binding.swtchSliceOrder.isClickable = true

        binding.groupCompareAutoPrice.visibility = View.GONE
    }

}