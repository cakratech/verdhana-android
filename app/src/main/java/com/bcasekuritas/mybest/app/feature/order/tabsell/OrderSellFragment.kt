package com.bcasekuritas.mybest.app.feature.order.tabsell

import android.graphics.Typeface
import android.text.Editable
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.request.AdvancedCriteriaRequest
import com.bcasekuritas.mybest.app.domain.dto.request.OrderAdapterData
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.order.OrderSharedViewModel
import com.bcasekuritas.mybest.databinding.FragmentOrderSellBinding
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
import com.bcasekuritas.mybest.ext.other.formatLotRoundingDown
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.other.unformatPriceToDouble
import com.bcasekuritas.mybest.widget.adjuster.CustomQtyAdjuster
import com.bcasekuritas.mybest.widget.textview.CustomTextView
import com.bcasekuritas.rabbitmq.proto.bcas.TriggerOrder
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import kotlin.math.ceil

@FragmentScoped
@AndroidEntryPoint
class OrderSellFragment : BaseFragment<FragmentOrderSellBinding, OrderSellViewModel>(),
    AdapterView.OnItemSelectedListener,
    ShowDialog by ShowDialogImpl(),
    ShowDropDown by ShowDropDownImpl(),
    ShowSnackBarInterface by ShowSnackBarImpl() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmOrderBuy
    override val viewModel: OrderSellViewModel by viewModels()
    override val binding: FragmentOrderSellBinding by autoCleaned {
        (FragmentOrderSellBinding.inflate(layoutInflater))
    }
    private val sharedViewModel: OrderSharedViewModel by lazy {
        ViewModelProvider(requireActivity()).get(OrderSharedViewModel::class.java)
    }

    private var totalPrice = 0.0

    private var compareType = "0"
    private var boardType = "RG"
    private var timeInForce = "0"
    private var stockCode = ""
    private var orderPeriod = 0L

    private var slicingType = 0
    private var endTimeSlice = 0L

    private var lastAmendPrice = 0.0
    private var lastAmendQty = 0.0
    private var tpValue = 0.0
    private var minusValTp = 0.0
    private var minusValPctTp = 0.0
    private var slValue = 0.0
    private var minusValSl = 0.0
    private var minusValPctSl = 0.0
    private var closePrice = 0.0
    private var comFee = 0.0
    private var avgPrice = 0.0

    private var amendLot = 0.0

    private var bracketCriteria = AdvancedCriteriaRequest()
    private var takeProfitCriteria = AdvancedCriteriaRequest()
    private var stopLossCriteria = AdvancedCriteriaRequest()

    private var isAmendOrder = false
    private var isAmendPartial = false
    private var isSliceOrder = false
    private var isBtnOrder = false
    private var isSLTP = false
    private var tpChecked = false
    private var slChecked = false
    private var cbSplit = false
    private var cbRepeat = false
    private var isNotation = false
    private var priceARAARB = 0.0

    // special stock for fraction 1
    private var isSpecialStock = false

    private var priceAdjuster = 0.0
    private var isMarketOrder = false
    private var marketOrderType = ""
    private var isMarketClosed = false
    private var listOrderType = arrayListOf("Limit Order", "Market Order")
    private var listMarketOrderType = arrayListOf("Fill and Kill (FAK)", "Fill or Kill (FOK)", "Market to Limit")
    private val listCompareSLTP = arrayListOf("Last Price")

    private val listBoard = arrayListOf("Regular")
    private var selectedOrdType = "0"
    private var selectedCompareTp = "0"
    private var selectedCompareSl = "0"
    private var selectedBoard = "RG"
    private val listSliceType = arrayListOf("At Once")

    private var lotOwned = 0.0
    private var relId = ""

    override fun setupObserver() {
        super.setupObserver()

        viewModel.getStockPosResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    it.data?.let { res ->
                        val stockPos = res.accstockposList.filter { it.stockcode == stockCode }
                        if (stockPos.isNotEmpty()) {
                            val lot = stockPos[0].potStockAvailable
                            lotOwned = if (isAmendOrder) lot.div(100) + lastAmendQty else lot.div(100)
                            binding.tvOwnedLotVal.text = lotOwned.formatLotRoundingDown() + " Lot"
                            binding.tvOwnedLotSltpVal.text = lotOwned.formatLotRoundingDown() + " Lot"
                        } else {
                            lotOwned = 0.0
                            binding.tvOwnedLotVal.text = lotOwned.formatLotRoundingDown() + " Lot"
                            binding.tvOwnedLotSltpVal.text = lotOwned.formatLotRoundingDown() + " Lot"
                        }
                        sendToParent()
                    }
                }

                else -> {}
            }
        }

        viewModel.getMaxOrderByStockResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }

                is Resource.Success -> {
                    when (it.data?.status) {
                        0 -> {
                            val maxLot = if (isAmendOrder) it.data.maxLotWhenAmend.toLong() else it.data.maxLot.toLong()
                            if (maxLot < 0) {
                                binding.adjusterLotQty.setEdt("0")
                            } else {
                                binding.adjusterLotQty.setEdt(maxLot.toString())
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

        sharedViewModel.updateOrder.observe(viewLifecycleOwner) {
            if (it.equals("S")) {
                sendToParent()
            }
        }

        sharedViewModel.data.observe(viewLifecycleOwner) { value ->
            if (value.stockCode?.isNotEmpty() == true) {
                stockCode = value.stockCode.toString()

                val isEtf = stockCode.first() == 'X'
                val isWarrantOrRight = stockCode.length > 4
                // special : etf/warrant/right stock
                isSpecialStock = isEtf || isWarrantOrRight
                binding.adjusterPrice.setIsSpecialStock(isSpecialStock)
                binding.adjusterSellPriceTp.setIsSpecialStock(isSpecialStock)
                binding.adjusterSellPriceSl.setIsSpecialStock(isSpecialStock)

                binding.swtchSliceOrder.isChecked = false
                binding.swtchStopLoss.isChecked = false
                binding.swtchGtc.isChecked = false
            }
            if (value.relId.isNotEmpty()) {
                relId = value.relId
            }
            var price = value.price?.toInt()
            var bestOffer = value.bestOffer?.toInt()
            val lotInt = value.lot?.toInt()?.div(100)
            var amendPrice = value.amendPrice?.toInt()
            val lastPrice = value.lastPrice?.toInt()
            val closePrice = value.closePrice?.toInt()

            if (value.closePrice != null && value.closePrice > 0.0) {
                this.closePrice = value.closePrice
            }

            if (value.avgPrice != null && value.avgPrice > 0.0) {
                this.avgPrice = value.avgPrice
            }

            if (value.amendPrice != 0.0 || value.isAmendMarketOrder) {
                listOrderType = arrayListOf("Limit Order")
                isAmendOrder = true
                if (value.isAmendPartial == true) {
                    isAmendPartial = true
                    binding.btnMaxCash.visibility = View.GONE
                    binding.adjusterLotQty.changeColor(R.color.noChanges)
                    binding.adjusterLotQty.disable()
                }
                lastAmendPrice = value.amendPrice ?: 0.0
                lastAmendQty = value.lot ?: 0.0
                lastAmendQty /= 100
                binding.adjusterPrice.setEdt(amendPrice.toString())
                binding.groupSwitchOption.visibility = View.GONE

                //setup ui amend market order
                if (value.isAmendMarketOrder) {
                    isMarketOrder = true
                    selectedOrdType = "5"
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

                    binding.adjusterPrice.disable()
                    binding.adjusterPrice.changeColor(R.color.noChanges)
                    binding.groupMarketOrder.visibility = View.VISIBLE
                    binding.tvErrorInputPrice.visibility = View.GONE
                }
            } else {
                if (!isAmendOrder) {
                    binding.groupSwitchOption.visibility = View.VISIBLE
                }
            }

            if (value.isSuccessOrder == true){
                clearData()
            }

            if (!isAmendOrder && !isMarketOrder) {
                when {
                    price != 0 -> {
                        binding.adjusterPrice.setEdt(price.toString())
                    }
                    bestOffer != 0 -> {
                        binding.adjusterPrice.setEdt(bestOffer.toString())
                    }
                    lastPrice != 0 -> {
                        binding.adjusterPrice.setEdt(lastPrice.toString())
                    }
                    closePrice != 0 -> {
                        binding.adjusterPrice.setEdt(closePrice.toString())
                    }
                    else -> "0"
                }
                priceAdjuster = binding.adjusterPrice.getDouble()
            } else {
                if  (price != 0 && !isMarketOrder) {
                    binding.adjusterPrice.setEdt(price.toString())
                }
            }

            if (closePrice != 0) {
                priceARAARB = closePrice?.toDouble()?: 0.0
            }

            if (lotInt != 0) {
                binding.adjusterLotQty.setEdt(lotInt.toString())
            }

            if (value.timeInForce.toString().equals("2")) {
                if (value.orderPeriod != null) {
                    orderPeriod = value.orderPeriod
                }
            }

            if (isAmendPartial){
                binding.tvErrorInput.text = "Cannot change partial match quantity"
                binding.tvErrorInput.isGone = false
            }


            val userId = prefManager.userId
            val accNo = prefManager.accno
            val sessionId = prefManager.sessionId
            if (stockCode.isNotEmpty()) {
                viewModel.getStockPos(userId, accNo, sessionId, stockCode)
            }
        }

        sharedViewModel.isNotation.observe(viewLifecycleOwner) {
            isNotation = it
        }

        sharedViewModel.isMarketClosed.observe(viewLifecycleOwner) {
            isMarketClosed = it
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val stringCompareRes = resources.getStringArray(R.array.spinner_order_buy_compare)
        val stringBoardRes = resources.getStringArray(R.array.spinner_order_buy_board)
        val stringSliceRes = resources.getStringArray(R.array.spinner_order_buy_slicing_type)
        val selectItem = parent?.selectedItem.toString()

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

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun setupComponent() {
        super.setupComponent()

        comFee = prefManager.sellCommission.ifEmpty { "0.0" }.toDouble()
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

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    // Do something when text changes
                }

                override fun afterTextChanged(s: Editable?) {
                    sendToParent()
                }
            }
            adjusterPrice.setTextWatcher(textWatcherPrice)

            val textWatcherQty = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // Do something before text changes
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    // Do something when text changes
                }

                override fun afterTextChanged(s: Editable?) {
                    if (!isSLTP) {
                        sendToParent()
                    }
                }
            }
            adjusterLotQty.setTextWatcher(textWatcherQty)

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
            adjusterSellPriceTp.setTextWatcher(textWatcherPriceTp)

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
                    binding.adjusterLotQty.setEdt(binding.adjusterSellQtyTp.getString())
                    if (!binding.adjusterSellQtySl.getString().equals(binding.adjusterSellQtyTp.getString()) && tpChecked) {
                        binding.adjusterSellQtySl.setEdt(binding.adjusterSellQtyTp.getString())
                    }
                }
            }
            adjusterSellQtyTp.setTextWatcher(textWatcherlotTp)

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
            adjusterSellPriceSl.setTextWatcher(textWatcherPriceSl)

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
                    binding.adjusterLotQty.setEdt(binding.adjusterSellQtySl.getString())
                    if (!binding.adjusterSellQtyTp.getString().equals(binding.adjusterSellQtySl.getString()) && slChecked){
                        binding.adjusterSellQtyTp.setEdt(binding.adjusterSellQtySl.getString())
                    }
                }
            }
            adjusterSellQtySl.setTextWatcher(textWatcherlotSl)

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
            edtCompareTp.addTextChangedListener(textWatcherCompareTp)

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
            edtCompareSl.addTextChangedListener(textWatcherCompareSl)

            checkboxStopLossisEnable(false)
            checkboxTakeProfitisEnable(false)
            binding.adjusterBlockSize.disable()
            binding.adjusterNoSplit.disable()
        }
    }

    override fun initOnClick() {
        super.initOnClick()

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

                binding.adjusterPrice.disable()
                binding.adjusterLotQty.disable()
            } else {
                binding.adjusterPrice.enable()
                binding.adjusterLotQty.enable()
                binding.adjusterPrice.setEdt(priceAdjuster.toString())
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
                timeInForce = "0"
                binding.tvDateGtc.text = ""
                binding.groupGtc.visibility = View.GONE
                orderPeriod = 0L
            }
            sendToParent()
        }

        binding.tvOrderTypeVal.setOnClickListener {
            if (!isSLTP) {
                showSimpleDropDownOrderType(
                    requireContext(),
                    listOrderType,
                    binding.tvOrderTypeVal,
                    isMarketClosed
                ) { index, value ->
                    if (!isAmendOrder) {
                        binding.tvOrderTypeVal.text = value

                        binding.groupMarketOrder.visibility = if (index == 1 || value == "Market Order") View.VISIBLE else View.GONE
                        if (index == 1 || value == "Market Order") {
                            isMarketOrder = true
                            binding.tvMarketOrderTypeVal.text = "Fill and Kill (FAK)"
                            marketOrderType = "Fill and Kill (FAK)"
                            selectedOrdType = "5"
                            timeInForce = "3"

                            binding.adjusterPrice.disable()
                            binding.adjusterPrice.changeColor(R.color.noChanges)

                            enabledSwitchOption(true)
                        } else {
                            isMarketOrder = false
                            binding.adjusterPrice.setEdt(priceAdjuster.toString())
                            binding.adjusterPrice.changeColor(R.color.black)

                            selectedOrdType = "0"
                            timeInForce = "0"

                            binding.adjusterPrice.enable()

                            enabledSwitchOption(false)
                        }

                        sendToParent()
                    }

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
            if (!isSLTP) {
                showSimpleDropDownWidth80(requireContext(), listBoard, binding.tvBoardVal) { _, value ->
                    binding.tvBoardVal.text = value
                    selectedBoard = boardType(value)
                    if (boardType == "TN") {
                        timeInForce = "S"
                    }
                    sendToParent()
                }
            }
        }

        binding.btnMaxCash.setOnClickListener {
            if (!isSLTP) {
                val price = if (!isMarketOrder) binding.adjusterPrice.getDouble() else priceAdjuster
                viewModel.getMaxOrder(
                    prefManager.userId,
                    prefManager.accno,
                    prefManager.sessionId,
                    stockCode,
                    price,
                    selectedOrdType,
                    selectedBoard,
                    relId
                )
            }
        }

        binding.cbStopLossSltp.setOnCheckedChangeListener { _, isChecked ->
            slChecked = isChecked
            checkboxStopLossisEnable(isChecked)
            if (!isChecked) {
                binding.tvErrorTpPrice.visibility = View.GONE
                binding.tvErrorSlPrice.visibility = View.GONE
                binding.tvErrorTpQty.visibility = View.GONE
                binding.tvErrorSlQty.visibility = View.GONE
                binding.adjusterSellPriceSl.setEdt("0")
                binding.edtCompareSl.setText("")
            } else {
                stopLossDefaultValue()
            }
            stopLossTakeProfit()
        }

        binding.cbTakeProfitSltp.setOnCheckedChangeListener { _, isChecked ->
            tpChecked = isChecked
            checkboxTakeProfitisEnable(isChecked)
            if (!isChecked) {
                binding.tvErrorSlPrice.visibility = View.GONE
                binding.tvErrorTpPrice.visibility = View.GONE
                binding.tvErrorTpQty.visibility = View.GONE
                binding.tvErrorSlQty.visibility = View.GONE
                binding.adjusterSellPriceTp.setEdt("0")
                binding.edtCompareTp.setText("")
            } else {
                takeProfitDefaultValue()
            }
            stopLossTakeProfit()
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
                binding.tvErrorSplitOrder.visibility = View.GONE
                binding.adjusterNoSplit.disable()
            }
        }

        binding.cbBlockSize.setOnCheckedChangeListener { _, isChecked ->
            cbRepeat = isChecked
            if (isChecked) {
                binding.cbNoSplit.isChecked = false
                binding.adjusterNoSplit.disable()
                binding.adjusterBlockSize.enable()
                binding.adjusterBlockSize.setEdt("2")
            } else {
                binding.tvErrorSliceOrder.visibility = View.GONE
                binding.adjusterBlockSize.disable()
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

    }

    fun checkboxTakeProfitisEnable(state: Boolean) {
        binding.apply {
            if (state) {
                edtCompareTp.isFocusableInTouchMode = true
            } else {
                edtCompareTp.isFocusable = false
            }
            if (state) adjusterSellPriceTp.enable() else adjusterSellPriceTp.disable()
            if (state) adjusterSellQtyTp.enable() else adjusterSellQtyTp.disable()
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
            if (state) adjusterSellQtySl.enable() else adjusterSellQtySl.disable()
        }
    }

    private fun stopLossTakeProfit() {

        val totalOrder =
            binding.adjusterPrice.getDouble().times(binding.adjusterLotQty.getDoubleTimes100())

        /** TAKE PROFIT*/
        val priceTp = binding.adjusterSellPriceTp.getDouble().toLong()
        val qtyTp = binding.adjusterSellQtyTp.getDoubleTimes100().toLong()
        val triggerTp = binding.edtCompareTp.text.toString().removeSeparator()
        val triggerValTp = triggerTp.takeIf { it.isNotEmpty() }?.toLong() ?: 0L

//        tpValue = binding.adjusterSellPriceTp.getDouble().times(binding.adjusterSellQtyTp.getDoubleTimes100())
//        minusValTp = totalOrder.minus(tpValue)
//        minusValPctTp = if (totalOrder != 0.0 && !minusValTp.isNaN() && !totalOrder.isNaN()) {
//            (minusValTp / totalOrder * 100)
//        } else {
//            0.0
//        }
//
//        val compareTpText = binding.edtCompareTp.text.toString().removeSeparator()
//        val compareTpDouble = compareTpText.toDoubleOrNull() ?: 0.0
//        val tpPrice = "Rp${compareTpDouble.formatPriceWithoutDecimal()}"
//
//        val tpProfit =
//            "Rp${minusValTp.formatPriceWithoutDecimal()} (+${minusValPctTp.roundedHalfDown()}%)"
//
//        setInfoSLTP(
//            binding.tvInfoTakeProfitTp,
//            tpPrice,
//            tpProfit,
//            true
//        )

        val triggerOrderTp = TriggerOrder.newBuilder()
            .setStockCode(stockCode)
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

//        slValue = binding.adjusterSellPriceSl.getDouble()
//            .times(binding.adjusterSellQtySl.getDoubleTimes100())
//        minusValSl = totalOrder.minus(slValue)
//        minusValPctSl = if (totalOrder != 0.0 && !minusValSl.isNaN() && !totalOrder.isNaN()) {
//            (minusValSl / totalOrder * 100)
//        } else {
//            0.0
//        }
//
//        val compareSlText = binding.edtCompareSl.text.toString().removeSeparator()
//        val compareSlDouble = compareSlText.toDoubleOrNull() ?: 0.0
//        val slPrice = "Rp${compareSlDouble.formatPriceWithoutDecimal()}"
//
//        val slProfit =
//            "Rp${minusValSl.formatPriceWithoutDecimal()} (-${minusValPctSl.roundedHalfDown()}%)"

        val triggerOrderSl = TriggerOrder.newBuilder()
            .setStockCode(stockCode)
            .setBuySell("S")
            .setOrdType(0)
            .setTimeInForce("0")
            .setOrdQty(qtySl)
            .setOrdPrice(priceSl)
            .build()

        stopLossCriteria = AdvancedCriteriaRequest(advType = 2, opr = 4, triggerVal = triggerValSl, triggerCategory = selectedCompareSl.toInt() ,triggerOrderSl)

//        setInfoSLTP(
//            binding.tvInfoTakeProfitSl,
//            slPrice,
//            slProfit,
//            false
//        )
        sendToParent()
    }

    fun setInfoSLTP(
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

    fun sendToParent() {
        binding.adjusterLotQty.changeColor(R.color.txtBlackWhite)
        if (!isMarketOrder) {
            binding.adjusterPrice.changeColor(R.color.txtBlackWhite)
        }

        var proceedAmount = binding.adjusterPrice.getDouble() * binding.adjusterLotQty.getDoubleTimes100()
        val comFee = proceedAmount.times(this.comFee)
        totalPrice = proceedAmount - comFee

        isBtnOrder = !(binding.adjusterPrice.getDouble() == 0.0 || binding.adjusterLotQty.getDouble() == 0.0)
        val blockSize = binding.adjusterBlockSize.getInt()
        if (blockSize != 0) {
            totalPrice *= blockSize
            proceedAmount *= blockSize
        }
        checkCondition(binding.adjusterLotQty.getDouble())

        if (!tpChecked) takeProfitCriteria = AdvancedCriteriaRequest()
        if (!slChecked) stopLossCriteria = AdvancedCriteriaRequest()

        sharedViewModel.setDataOrder(
            OrderAdapterData(
                stockCode,
                binding.adjusterPrice.getDouble(),
                binding.adjusterLotQty.getDouble(),
                totalPrice,
                0.0,
                "S",
                isSliceOrder,
                isSLTP,
                slicingType,
                selectedOrdType,
                binding.adjusterNoSplit.getInt(),
                binding.adjusterBlockSize.getInt(),
                boardType = selectedBoard,
                isBtnOrder = isBtnOrder,
                bracketCriteria = bracketCriteria,
                takeProfitCriteria = takeProfitCriteria,
                stopLossCriteria = stopLossCriteria,
                timeInForce = timeInForce,
                orderPeriod = orderPeriod,
                endTimeSliceOrder = endTimeSlice,
                proceedAmount = proceedAmount,
                isMarketOrder = isMarketOrder,
                marketOrderType = binding.tvMarketOrderTypeVal.text.toString()
            )
        )
    }

    private fun sliceOrderValidation(qty: Double, lotQty: Double): Boolean {
        binding.tvErrorSliceOrder.visibility = View.GONE
        binding.tvErrorSplitOrder.visibility = View.GONE
        binding.adjusterNoSplit.changeColor(R.color.txtBlackWhite)
        binding.adjusterBlockSize.changeColor(R.color.txtBlackWhite)
        binding.adjusterLotQty.changeColor(R.color.txtBlackWhite)

        return if ((binding.adjusterNoSplit.getInt() != 0 && binding.adjusterNoSplit.getInt() in 2..10) ||
            (binding.adjusterBlockSize.getInt() != 0 && binding.adjusterBlockSize.getInt() in 2..10) ) {

            if (cbSplit) {
                if (sliceOrderValidation(qty, binding.adjusterNoSplit.getInt())) {
                    binding.tvErrorSplitOrder.visibility = View.GONE
                    binding.adjusterNoSplit.changeColor(R.color.txtBlackWhite)

                    val noSplit = binding.adjusterNoSplit.getDouble()

                    if (lotQty.div(noSplit) > 50000.0){
                        binding.tvErrorSplitOrder.text = "Lot quantity after split can’t exceed 50,000"
                        binding.tvErrorSplitOrder.visibility = View.VISIBLE
                        binding.adjusterNoSplit.changeColor(R.color.textRed)
                        binding.adjusterLotQty.changeColor(R.color.textRed)
                        false
                    } else {
                        binding.tvErrorSplitOrder.visibility = View.GONE
                        binding.adjusterNoSplit.changeColor(R.color.txtBlackWhite)
                        binding.adjusterLotQty.changeColor(R.color.txtBlackWhite)

                        if (slicingType.equals(1)) {
                            compareTimeToCurrentTime(endTimeSlice)
                        } else {
                            true
                        }

                    }
                } else {
                    binding.tvErrorSplitOrder.text = "Mininum lot quantity after split is 2"
                    binding.adjusterLotQty.changeColor(R.color.textRed)
                    binding.tvErrorSplitOrder.visibility = View.VISIBLE
                    binding.adjusterNoSplit.changeColor(R.color.textDown)
                    false
                }
            } else {
                if (cbRepeat) {
                    if (qty >= 2) {
                        if (slicingType.equals(1)) {
                            compareTimeToCurrentTime(endTimeSlice)
                        } else {
                            val repeatsNumber = binding.adjusterBlockSize.getDouble().times(lotQty)
                            val maxRepeat = lotOwned/qty
                            if (repeatsNumber <= lotOwned) {
                                binding.adjusterBlockSize.changeColor(R.color.txtBlackWhite)
                                true
                            } else {
                                if (maxRepeat.toInt() != 0) {
                                    if (maxRepeat.toInt() == 1) {
                                        binding.tvErrorSliceOrder.text = "Minimum repeat order quantity is 2"
                                        binding.tvErrorSliceOrder.visibility = View.VISIBLE
                                        binding.adjusterBlockSize.changeColor(R.color.textDown)
                                    } else {
                                        binding.tvErrorSliceOrder.text =
                                            "Maximum number of repeats is ${maxRepeat.formatPriceWithoutDecimal()}"
                                        binding.tvErrorSliceOrder.visibility = View.VISIBLE
                                        binding.adjusterBlockSize.changeColor(R.color.textDown)
                                    }
                                } else {
                                    binding.tvErrorSliceOrder.visibility = View.GONE
                                    binding.adjusterBlockSize.changeColor(R.color.txtBlackWhite)
                                }
                                false
                            }
                        }
                    } else {
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
    }

    private fun gtcOrderValidation(): Boolean {
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

    private fun sltpValidation() {

        var validationTp = true
        var validationSl = true
        var validationQtyTp = true
        var validationQtySl = true

        val comparePriceTp = binding.edtCompareTp.text.toString().removeSeparator().toDoubleOrNull() ?: 0.0
        val priceTp = binding.adjusterSellPriceTp.getDouble()
        val comparePriceSl = binding.edtCompareSl.text.toString().removeSeparator().toDoubleOrNull() ?: 0.0
        val priceSl = binding.adjusterSellPriceSl.getDouble()

        if (tpChecked) {
            val qtyTp = binding.adjusterSellQtyTp.getDouble()

            if (priceTp != 0.0) {
                binding.tvErrorTpPrice.visibility = View.GONE
                binding.adjusterSellPriceTp.changeColor(R.color.textRed)

                if (checkARAandARBSltp(priceTp, priceARAARB, binding.tvErrorTpPrice, binding.adjusterSellPriceTp)) {
                    if (slChecked && priceSl >= priceTp) {
                        validationTp = false
                        binding.tvErrorTpPrice.visibility = View.VISIBLE
                        binding.adjusterSellPriceTp.changeColor(R.color.textRed)
                        binding.tvErrorTpPrice.text = "Take profit price should be higher than stop loss price"
                    } else {
                        binding.adjusterSellPriceTp.changeColor(R.color.txtBlackWhite)
                        binding.tvErrorTpPrice.visibility = View.GONE

                        if (comparePriceTp == 0.0) {
                            binding.tvErrorTpPrice.visibility = View.VISIBLE
                            binding.tvErrorTpPrice.text = "Compare price should be filled"
                            validationTp = false
                        } else {
                            if (slChecked && comparePriceSl >= comparePriceTp) {
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
                    validationTp = false
                }
            } else {
                binding.tvErrorTpPrice.visibility = View.VISIBLE
                binding.adjusterSellPriceTp.changeColor(R.color.textRed)
                binding.tvErrorTpPrice.text = "Price should be filled"
                validationTp = false
            }

            when {
                qtyTp == 0.0 -> {
                    binding.tvErrorTpQty.visibility = View.VISIBLE
                    binding.adjusterSellQtyTp.changeColor(R.color.textRed)
                    binding.tvErrorTpQty.text = "Quantity should be filled"
                    validationQtyTp = false
                }
                qtyTp > lotOwned -> {
                    binding.tvErrorTpQty.visibility = View.VISIBLE
                    binding.tvErrorTpQty.text = "Your owned lot is not enough"
                    binding.adjusterSellQtyTp.changeColor(R.color.textRed)
                    validationQtyTp = false
                }
                else -> {
                    binding.tvErrorTpQty.visibility = View.GONE
                    binding.adjusterSellQtyTp.changeColor(R.color.txtBlackWhite)
                    validationQtyTp = true
                }
            }
        }

        if (slChecked) {
            val qtySl = binding.adjusterSellQtySl.getDouble()

            if (priceSl != 0.0) {
                binding.tvErrorSlPrice.visibility = View.GONE
                binding.adjusterSellPriceSl.changeColor(R.color.txtBlackWhite)

                if (checkARAandARBSltp(priceSl, priceARAARB, binding.tvErrorSlPrice, binding.adjusterSellPriceSl)) {
                    if (tpChecked && priceSl >= priceTp) {
                        validationSl = false
                        binding.tvErrorSlPrice.visibility = View.VISIBLE
                        binding.adjusterSellPriceSl.changeColor(R.color.textRed)
                        binding.tvErrorSlPrice.text = "Stop loss price should be lower than take profit price"
                    } else {
                        binding.tvErrorSlPrice.visibility = View.GONE
                        binding.adjusterSellPriceSl.changeColor(R.color.txtBlackWhite)
                        if (comparePriceSl == 0.0) {
                            binding.tvErrorSlPrice.visibility = View.VISIBLE
                            binding.tvErrorSlPrice.text = "Compare price should be filled"
                            validationSl = false
                        } else {
                            if (tpChecked && comparePriceSl >= comparePriceTp) {
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
                    validationSl = false
                }
            } else {
                binding.tvErrorSlPrice.visibility = View.VISIBLE
                binding.adjusterSellPriceSl.changeColor(R.color.textRed)
                binding.tvErrorSlPrice.text = "Price should be filled"
                validationSl = false
            }

            when {
                qtySl == 0.0 -> {
                    binding.tvErrorSlQty.visibility = View.VISIBLE
                    binding.adjusterSellQtySl.changeColor(R.color.textRed)
                    binding.tvErrorSlQty.text = "Quantity should be filled"
                    validationQtySl = false
                }
                qtySl > lotOwned -> {
                    binding.tvErrorSlQty.text = "Your owned lot is not enough"
                    binding.tvErrorSlQty.visibility = View.VISIBLE
                    binding.adjusterSellQtySl.changeColor(R.color.textRed)
                    validationQtySl = false
                }
                else -> {
                    binding.tvErrorSlQty.visibility = View.GONE
                    binding.adjusterSellQtySl.changeColor(R.color.txtBlackWhite)
                    validationQtySl = true

                }
            }

        }

        isBtnOrder = if (!tpChecked && !slChecked) {
            false
        } else {
            validationTp && validationSl && validationQtySl && validationQtyTp
        }
    }

    private fun orderValidation(lotQty: Double, price: Double, qty: Double) {
        when{
            lotQty > lotOwned && !isAmendOrder -> {
                binding.tvErrorInput.visibility = View.VISIBLE
                binding.adjusterLotQty.changeColor(R.color.textRed)
                binding.tvErrorInput.text = "Your owned lot is not enough"
            }

            lotQty > 50000 && !cbSplit -> {
                binding.tvErrorInput.visibility = View.VISIBLE
                binding.adjusterLotQty.changeColor(R.color.textRed)
                if (cbRepeat){
                    binding.adjusterBlockSize.changeColor(R.color.textRed)
                } else {
                    binding.adjusterBlockSize.changeColor(R.color.txtBlackWhite)
                }
                errorInput(true)
            }
            else -> {
                binding.adjusterLotQty.changeColor(R.color.txtBlackWhite)
                binding.tvErrorInput.visibility = View.GONE
            }
        }

        if (timeInForce != "2") {
            if (checkARAandARB(priceARAARB)) {
                if (!checkFractionPrice(price.toInt())) {
                    binding.tvErrorInputPrice.visibility = View.VISIBLE
                    binding.adjusterPrice.changeColor(R.color.textRed)
                    binding.tvErrorInputPrice.text = "The price entered does not align with the fractional value"
                } else {
                    binding.adjusterPrice.changeColor(R.color.txtBlackWhite)
                    binding.tvErrorInputPrice.visibility = View.GONE
                }
            }
        } else {
            if (!checkFractionPrice(price.toInt())) {
                binding.tvErrorInputPrice.visibility = View.VISIBLE
                binding.adjusterPrice.changeColor(R.color.textRed)
                binding.tvErrorInputPrice.text = "The price entered does not align with the fractional value"
            } else {
                binding.adjusterPrice.changeColor(R.color.txtBlackWhite)
                binding.tvErrorInputPrice.visibility = View.GONE
            }
        }
    }

    private fun checkCondition(lotQty: Double) {
        val qty = binding.adjusterLotQty.getDouble()
        val price = binding.adjusterPrice.getDouble()
        val lotOwnDouble = binding.tvOwnedLotVal.text.toString().unformatPriceToDouble()
        if (isSLTP) {
            binding.tvErrorInput.visibility = View.GONE
            binding.tvErrorInputPrice.visibility = View.GONE

            sltpValidation()
        } else if (isMarketOrder) {
            if (isAmendOrder) {
                isBtnOrder = when {
                    qty == 0.0 || qty == lastAmendQty -> {
                        binding.tvErrorInput.visibility = View.GONE
                        binding.adjusterLotQty.changeColor(R.color.textRed)
                        false
                    }
                    qty > lotOwned -> {
                        binding.tvErrorInput.visibility = View.VISIBLE
                        binding.adjusterLotQty.changeColor(R.color.textRed)
                        binding.tvErrorInput.text = "Your owned lot is not enough"
                        false
                    }
                    qty > 50000 -> {
                        binding.tvErrorInput.visibility = View.VISIBLE
                        binding.adjusterLotQty.changeColor(R.color.textRed)
                        errorInput(true)
                        false
                    }
                    else -> {
                        binding.tvErrorInput.visibility = View.GONE
                        binding.adjusterLotQty.changeColor(R.color.txtBlackWhite)
                        true
                    }
                }
            } else {
                isBtnOrder = qty > 0.0 && qty <= lotOwned && qty <= 50000
                if (qty > lotOwned) {
                    binding.tvErrorInput.visibility = View.VISIBLE
                    binding.adjusterLotQty.changeColor(R.color.textRed)
                    binding.tvErrorInput.text = "Your owned lot is not enough"
                } else if (qty > 50000) {
                    binding.tvErrorInput.visibility = View.VISIBLE
                    binding.adjusterLotQty.changeColor(R.color.textRed)
                    errorInput(true)
                } else {
                    binding.adjusterLotQty.changeColor(R.color.txtBlackWhite)
                    binding.tvErrorInput.visibility = View.GONE
                }
            }
        } else {
            orderValidation(lotQty, price, qty)
            when {
                isSliceOrder -> sliceOrderValidation(qty, lotQty)
                timeInForce == "2" -> gtcOrderValidation()
            }
            if (qty <= 0.0 || price == 0.0) {
                isBtnOrder = false
            } else {
                when{
                    lotQty > lotOwned -> {
                        binding.tvErrorInput.visibility = View.VISIBLE
                        binding.adjusterLotQty.changeColor(R.color.textRed)
                        binding.tvErrorInput.text = "Your owned lot is not enough"
                        isBtnOrder = false
                    }

                    lotQty > 50000 && !cbSplit -> {
                        binding.tvErrorInput.visibility = View.VISIBLE
                        binding.adjusterLotQty.changeColor(R.color.textRed)
                        if (cbRepeat){
                            binding.adjusterBlockSize.changeColor(R.color.textRed)
                        } else {
                            binding.adjusterBlockSize.changeColor(R.color.txtBlackWhite)
                        }
                        errorInput(true)
                        isBtnOrder = false
                    }
                    else -> {
                        binding.adjusterLotQty.changeColor(R.color.txtBlackWhite)
                        binding.tvErrorInput.visibility = View.GONE
                        if (timeInForce != "2") {
                            if (checkARAandARB(priceARAARB)) {
                                if (!checkFractionPrice(price.toInt())) {
                                    isBtnOrder = false
                                    binding.tvErrorInputPrice.visibility = View.VISIBLE
                                    binding.adjusterPrice.changeColor(R.color.textRed)
                                    binding.tvErrorInputPrice.text = "The price entered does not align with the fractional value"
                                } else {
                                    binding.adjusterPrice.changeColor(R.color.txtBlackWhite)
                                    binding.tvErrorInputPrice.visibility = View.GONE
                                    // Amend
                                    isBtnOrder = if (isAmendOrder) {
                                        if (isAmendPartial) {
                                            lastAmendPrice != price
                                        } else {
                                            when {
                                                qty < lastAmendQty -> {
                                                    binding.adjusterLotQty.changeColor(R.color.txtBlackWhite)
                                                    true
                                                }
                                                qty > lastAmendQty -> {
                                                    if (lastAmendPrice != price) {
                                                        binding.adjusterLotQty.changeColor(R.color.txtBlackWhite)
                                                        binding.tvErrorInput.visibility = View.GONE
                                                        true
                                                    } else {
                                                        binding.tvErrorInput.visibility = View.VISIBLE
                                                        binding.adjusterLotQty.changeColor(R.color.textRed)
                                                        binding.tvErrorInput.text =  "Cannot add quantity only"
                                                        false
                                                    }
                                                }
                                                qty == lastAmendQty -> {
                                                    if (lastAmendPrice != price) {
                                                        binding.adjusterLotQty.changeColor(R.color.txtBlackWhite)
                                                        binding.tvErrorInput.visibility = View.GONE
                                                        true
                                                    } else {
                                                        false
                                                    }
                                                }
                                                else -> false
                                            }
                                        }

                                        // SLICE ORDER
                                    } else if (isSliceOrder) {
                                        sliceOrderValidation(qty, lotQty)
                                    } else if (timeInForce.equals("2")) {
                                        gtcOrderValidation()
                                    } else {
                                        true
                                    }
                                }
                            } else {
                                isBtnOrder = false
                            }
                        } else {
                            // gtc validation
                            if (checkFractionPrice(price.toInt())) {
                                binding.adjusterPrice.changeColor(R.color.txtBlackWhite)
                                binding.tvErrorInputPrice.visibility = View.GONE

                                isBtnOrder = gtcOrderValidation()
                            } else {
                                isBtnOrder = false
                                binding.tvErrorInputPrice.visibility = View.VISIBLE
                                binding.adjusterPrice.changeColor(R.color.textRed)
                                binding.tvErrorInputPrice.text = "The price entered does not align with the fractional value"
                            }
                        }

                        binding.adjusterSellQtyTp.changeColor(R.color.txtBlackWhite)
                        binding.adjusterSellQtySl.changeColor(R.color.txtBlackWhite)

                    }
                }
            }
        }


        binding.adjusterLotQty.changeColor(
            when {
                lotQty > lotOwned && !isAmendOrder -> R.color.textRed
                qty > 50000 && !cbSplit -> R.color.textRed
                cbRepeat && qty < 2 && binding.adjusterBlockSize.getInt() >= 2 -> R.color.textRed
                binding.adjusterNoSplit.getDouble() != 0.0 && qty.div(binding.adjusterNoSplit.getDouble()) > 50000.0 -> R.color.textRed
                else -> R.color.txtBlackWhite
            }
        )
    }

    fun sliceOrderValidation(value: Double, split: Int): Boolean {
        return value >= 2 * split
    }

    fun checkARAandARBSltp(price: Double, closePrice:Double, errorTextView: TextView, qtyAdjuster: CustomQtyAdjuster): Boolean {
        var top: Double
        var bottom: Double

        if (closePrice != 0.0 && !isSpecialStock) {
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
                        errorTextView.text = "Price can't be higher than ${top.formatPriceWithoutDecimal()}"
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

    fun checkARAandARB(price: Double): Boolean {
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

            val inputPrice = binding.adjusterPrice.getDouble()
            val arb = ceil(bottom)
            when {
                inputPrice < arb -> {
                    binding.adjusterPrice.changeColor(R.color.textRed)
                    binding.tvErrorInputPrice.visibility = View.VISIBLE

                    if (!checkFractionPrice(arb.toInt())) {
                        val adjustFraction = adjustFractionPrice(arb.toInt(), "", isSpecialStock)
                        binding.tvErrorInputPrice.text = "Price can't be lower than ${adjustFraction}"
                    } else {
                        binding.tvErrorInputPrice.text = "Price can't be lower than ${arb.formatPriceWithoutDecimal()}"
                    }
                }
                inputPrice > top -> {
                    binding.adjusterPrice.changeColor(R.color.textRed)
                    binding.tvErrorInputPrice.visibility = View.VISIBLE

                    if (!checkFractionPrice(top.toInt())) {
                        val adjustFraction = adjustFractionPrice(top.toInt(), "-", isSpecialStock)
                        binding.tvErrorInputPrice.text = "Price can't be higher than ${adjustFraction}"
                    } else {
                        binding.tvErrorInputPrice.text = "Price can't be higher than ${top.toInt().formatPriceWithoutDecimal()}"
                    }
                }
                else -> {
                    binding.tvErrorInputPrice.visibility = View.GONE
                    binding.adjusterPrice.changeColor(R.color.txtBlackWhite)
                }
            }

            return inputPrice in bottom..top
        } else {
            return true
        }
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

    fun checkFractionPrice(price: Int): Boolean {
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

    fun takeProfitDefaultValue() {
        if (avgPrice != 0.0 || closePrice != 0.0) {
            val price = if (avgPrice != 0.0) avgPrice else closePrice
            val changes = price.times(0.05)
            val priceTp = price.plus(changes)
            val fractionPrice = getFractionPrice(priceTp.toInt()).formatPriceWithoutDecimal()

            binding.adjusterSellPriceTp.setEdt(fractionPrice)
            binding.edtCompareTp.setText(fractionPrice)
        }
    }

    fun stopLossDefaultValue() {
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
        totalPrice = 0.0
        compareType = "0"
        boardType = "RG"
        timeInForce = "0"
        selectedOrdType = "0"
        stockCode = ""
        slicingType = 0
        lastAmendPrice = 0.0
        lastAmendQty = 0.0
        tpValue = 0.0
        minusValTp = 0.0
        minusValPctTp = 0.0
        slValue = 0.0
        minusValSl = 0.0
        minusValPctSl = 0.0
        bracketCriteria = AdvancedCriteriaRequest()
        takeProfitCriteria = AdvancedCriteriaRequest()
        stopLossCriteria = AdvancedCriteriaRequest()
        isAmendOrder = false
        isSliceOrder = false
        isBtnOrder = false
        isSLTP = false
        tpChecked = false
        slChecked = false



        binding.tvOrderTypeVal.text = "Limit Order"
        binding.tvCompareTpVal.text = "Last Price"
        binding.tvCompareSlVal.text = "Last Price"
        binding.adjusterPrice.setEdt("")
        binding.adjusterLotQty.setEdt("")
        binding.adjusterSellPriceTp.setEdt("")
        binding.adjusterSellQtyTp.setEdt("")
        binding.adjusterSellPriceSl.setEdt("")
        binding.adjusterSellQtySl.setEdt("")
        binding.edtCompareTp.setText("")
        binding.edtCompareSl.setText("")

        binding.swtchGtc.isChecked = false
        binding.swtchStopLoss.isChecked = false
        binding.swtchSliceOrder.isChecked = false
    }


}