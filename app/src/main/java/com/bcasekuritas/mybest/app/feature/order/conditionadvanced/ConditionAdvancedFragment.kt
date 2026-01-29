package com.bcasekuritas.mybest.app.feature.order.conditionadvanced

import android.graphics.Typeface
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.request.AdvanceOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.AdvancedCriteriaRequest
import com.bcasekuritas.mybest.app.domain.dto.response.PortfolioStockDataItem
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.databinding.FragmentConditionAdvancedBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.RabbitMQForegroundService
import com.bcasekuritas.mybest.ext.common.adjustFractionPrice
import com.bcasekuritas.mybest.ext.common.generateTextImageProfile
import com.bcasekuritas.mybest.ext.common.getFractionPrice
import com.bcasekuritas.mybest.ext.common.getIpAddress
import com.bcasekuritas.mybest.ext.common.getRandomString
import com.bcasekuritas.mybest.ext.common.initPercentFormatNumber
import com.bcasekuritas.mybest.ext.common.validateSessionPin
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.converter.GET_4_CHAR_STOCK_CODE
import com.bcasekuritas.mybest.ext.converter.GET_COMPARE_SLTP
import com.bcasekuritas.mybest.ext.converter.GET_IDX_BOARD
import com.bcasekuritas.mybest.ext.converter.removeSeparator
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarInterface
import com.bcasekuritas.mybest.ext.other.formatLotRoundingDown
import com.bcasekuritas.mybest.ext.other.formatPriceWithDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.widget.adjuster.CustomQtyAdjuster
import com.bcasekuritas.mybest.widget.textview.CustomTextView
import com.bcasekuritas.rabbitmq.proto.bcas.TriggerOrder
import timber.log.Timber
import kotlin.math.ceil

@FragmentScoped
@AndroidEntryPoint
class ConditionAdvancedFragment: BaseFragment<FragmentConditionAdvancedBinding, ConditionAdvancedViewModel>(),
    ShowDialog by ShowDialogImpl(),
    ShowDropDown by ShowDropDownImpl(),
    ShowSnackBarInterface by ShowSnackBarImpl() {

    override val bindingVariable: Int = BR.vmConditionAdvance
    override val viewModel: ConditionAdvancedViewModel by viewModels()
    override val binding: FragmentConditionAdvancedBinding by autoCleaned { (FragmentConditionAdvancedBinding.inflate(layoutInflater)) }


    private var stockCode = ""
    private var comFee = 0.0
    private var closePrice = 0.0
    private var lastPrice = 0.0
    private var avgPrice = 0.0

    private var tpValue = 0.0
    private var minusValTp = 0.0
    private var minusValPctTp = 0.0
    private var slValue = 0.0
    private var minusValSl = 0.0
    private var minusValPctSl = 0.0

    private var selectedOrdType = "0"
    private var selectedCompareTp = "0"
    private var selectedCompareSl = "0"

    private var tpChecked = false
    private var slChecked = false

    private var bracketCriteria = AdvancedCriteriaRequest()
    private var takeProfitCriteria = AdvancedCriteriaRequest()
    private var stopLossCriteria = AdvancedCriteriaRequest()

    private var dataPortfolio: PortfolioStockDataItem? = PortfolioStockDataItem()
    private var lotOwned = 0.0

    // special stock for fraction 1
    private var isNotation = false
    private var isSpecialStock = false
    private var isWarrantOrRight = false

    private var userId = ""
    private var accNo = ""
    private var sessionId = ""
    private var ipAddress = ""

    private val listCompareSLTP =
        arrayListOf("Last Price", "Best Bid", "Best Offer")

    override fun setupArguments() {
        super.setupArguments()
        arguments?.let {
            dataPortfolio = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(Args.EXTRA_PARAM_OBJECT, PortfolioStockDataItem::class.java)
            } else {
                it.getParcelable(Args.EXTRA_PARAM_OBJECT)
            }
        }
        stockCode = dataPortfolio?.stockcode?: ""
    }


    override fun onResume() {
        super.onResume()
        viewModel.setListenerTradeSummary()
    }

    override fun onPause() {
        super.onPause()
        viewModel.unSubscribeTradeSummary(stockCode)
    }

    private fun checkSpecialStock() {
        val isEtf = stockCode.take(1) == "X"
        val isWarrantOrRight = stockCode.length > 4
        // special : etf/warrant/right stock
        isSpecialStock = isEtf || isWarrantOrRight

        binding.adjusterSellPriceTp.setIsSpecialStock(isSpecialStock)
        binding.adjusterSellPriceSl.setIsSpecialStock(isSpecialStock)
    }

    override fun setupComponent() {
        super.setupComponent()
        comFee = prefManager.sellCommission.ifEmpty { "0.0" }.toDouble()
        checkSpecialStock()

        binding.apply {
            toolbar.tvLayoutToolbarMasterTitle.text = "Change Condition"
            tvOrderBuySahamInfoCode.text = stockCode
            val url = prefManager.urlIcon+ GET_4_CHAR_STOCK_CODE(stockCode)

            Glide.with(requireContext())
                .load(url)
                .circleCrop()
                .placeholder(R.drawable.bg_circle)
                .error(R.drawable.bg_circle)
                .into(ivOrderBuySahamInfo)

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
                    if (!adjusterSellQtySl.getString().equals(adjusterSellQtyTp.getString())) {
                        adjusterSellQtySl.setEdt(adjusterSellQtyTp.getString())
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
                    if (!adjusterSellQtyTp.getString().equals(adjusterSellQtySl.getString())){
                        adjusterSellQtyTp.setEdt(adjusterSellQtySl.getString())
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
                    stopLossTakeProfit()
                }
            }
            edtCompareSl.addTextChangedListener(textWatcherCompareSl)
        }

        checkboxStopLossisEnable(false)
        checkboxTakeProfitisEnable(false)
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            cbStopLossSltp.setOnCheckedChangeListener { _, isChecked ->
                slChecked = isChecked
                checkboxStopLossisEnable(isChecked)
                if (!isChecked) {
                    binding.tvErrorSlPrice.visibility = View.GONE
                    binding.tvErrorSlQty.visibility = View.GONE
                    binding.adjusterSellPriceSl.setEdt("0")
                    binding.edtCompareSl.setText("")
                } else {
                    stopLossDefaultValue()
                }
                stopLossTakeProfit()
            }

            cbTakeProfitSltp.setOnCheckedChangeListener { _, isChecked ->
                tpChecked = isChecked
                checkboxTakeProfitisEnable(isChecked)
                if (!isChecked) {
                    binding.tvErrorTpQty.visibility = View.GONE
                    binding.tvErrorTpPrice.visibility = View.GONE
                    binding.adjusterSellPriceTp.setEdt("0")
                    binding.edtCompareTp.setText("")
                } else {
                    takeProfitDefaultValue()
                }
                stopLossTakeProfit()
            }

            tvCompareTpVal.setOnClickListener {
                if (tpChecked) {
                    showSimpleDropDownWidth80(
                        requireContext(),
                        listCompareSLTP,
                        binding.tvCompareTpVal
                    ) { index, value ->
                        binding.tvCompareTpVal.text = value
                        selectedCompareTp = index.GET_COMPARE_SLTP()
                    }
                }

            }

            tvCompareSlVal.setOnClickListener {
                if (slChecked) {
                    showSimpleDropDownWidth80(
                        requireContext(),
                        listCompareSLTP,
                        binding.tvCompareSlVal
                    ) { index, value ->
                        binding.tvCompareSlVal.text = value
                        selectedCompareSl = index.GET_COMPARE_SLTP()
                    }
                }
            }

            toolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener {
                onBackPressed()
            }

            btnBuy.setOnClickListener {
                sendAdvOrder()
                Handler(Looper.getMainLooper()).postDelayed({
                    findNavController().navigate(R.id.stop_loss_take_profit_fragment)
                }, 200)
            }
        }

    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        accNo = prefManager.accno
        sessionId = prefManager.sessionId

        ipAddress = getIpAddress(requireContext())
        viewModel.getIpAddress()

        viewModel.getSessionPin(userId)
        viewModel.getStockParam(stockCode)
        viewModel.getStockNotation(stockCode)
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.getPinSessionResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if (it.data != null) {
                        if (validateSessionPin(it.data)) {
                            viewModel.getStockPos(userId, accNo, sessionId, stockCode)
                            viewModel.getStockDetail(userId, sessionId, stockCode)
                            viewModel.getAccountInfo(accNo)
                        } else {
                            showDialogPin()
                        }
                    } else {
                        showDialogPin()
                    }
                }

                else -> {}
            }
        }

        viewModel.getStockPosResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    it.data?.let { res ->
                        res.accstockposList.filter { it.stockcode == stockCode }.map { data ->
                            lotOwned = data.potStockAvailable.div(100)
                            binding.tvOwnedLotVal.text = lotOwned.formatLotRoundingDown() + " Lot"
                        }
                    }
                }

                else -> {}
            }
        }

        viewModel.getStockDetailResult.observe(viewLifecycleOwner) {
            it?.let {
                closePrice = it.close
                lastPrice = it.last
                avgPrice = it.avgPrice
                binding.tvOrderBuySahamPrice.text = if (lastPrice != 0.0) lastPrice.formatPriceWithDecimal() else closePrice.formatPriceWithDecimal()

                binding.tvOrderBuySahamPercent.text = "${it.change.formatPriceWithoutDecimal()} (${initPercentFormatNumber(it.changePct)})"
                if (it.change > 0) {
                    binding.tvOrderBuySahamPercent.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.textLightGreen
                        )
                    )
                    binding.tvOrderBuySahamPercent.text = "+${it.change.formatPriceWithoutDecimal()} (+${initPercentFormatNumber(it.changePct)})"

                } else if (it.change < 0) {
                    binding.tvOrderBuySahamPercent.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.textDownHeader
                        )
                    )

                } else {
                    binding.tvOrderBuySahamPercent.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                }
            }
        }

        viewModel.getStockParamResult.observe(viewLifecycleOwner) {
            binding.tvOrderBuySahamInfoName.text = it?.stockName

            binding.tvInfoSpecialNotesAcceleration.text = it?.idxTrdBoard.GET_IDX_BOARD()
            binding.tvInfoSpecialNotesAcceleration.visibility = if (binding.tvInfoSpecialNotesAcceleration.text == "") View.GONE else View.VISIBLE
        }

        viewModel.getAccountInfoResult.observe(viewLifecycleOwner) {
            it?.let {
                binding.tvOrderBuyPersonalInfo.text = "${it.accNo} - ${it.accName}"
                binding.tvImageProfile.text = generateTextImageProfile(it.accName)
            }
        }

        viewModel.getSubscribeTradeSummary.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.tvOrderBuySahamPrice.text = if (it.last != 0.0) it.last.formatPriceWithDecimal() else it.close.formatPriceWithDecimal()

                binding.tvOrderBuySahamPercent.text =
                    "${it.change.formatPriceWithoutDecimal()} (${initPercentFormatNumber(it.changePct)})"
                if (it.change > 0) {
                    binding.tvOrderBuySahamPercent.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.textLightGreen
                        )
                    )
                    binding.tvOrderBuySahamPercent.text =
                        "+${it.change.formatPriceWithoutDecimal()} (+${initPercentFormatNumber(it.changePct)})"

                } else if (it.change < 0) {
                    binding.tvOrderBuySahamPercent.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.textDownHeader
                        )
                    )

                } else {
                    binding.tvOrderBuySahamPercent.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                }
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
        viewModel.getLogoutResult.observe(viewLifecycleOwner) {
            when (it?.status){
                0 -> {
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
            } else {
                binding.tvInfoSpecialNotes.text = ""
                binding.tvInfoSpecialNotes.visibility = View.GONE
            }
        }

        viewModel.getIpAddressResult.observe(viewLifecycleOwner) {value ->
            ipAddress = value.ifEmpty { getIpAddress(requireContext()) }
        }
    }

    private fun stopLossTakeProfit() {

        /** TAKE PROFIT*/
        val priceTp = binding.adjusterSellPriceTp.getDouble().toLong()
        val qtyTp = binding.adjusterSellQtyTp.getDoubleTimes100().toLong()
        val triggerTp = binding.edtCompareTp.text.toString().removeSeparator()
        val triggerValTp = triggerTp.takeIf { it.isNotEmpty() }?.toLong() ?: 0L

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

        val triggerOrderSl = TriggerOrder.newBuilder()
            .setStockCode(stockCode)
            .setBuySell("S")
            .setOrdType(0)
            .setTimeInForce("0")
            .setOrdQty(qtySl)
            .setOrdPrice(priceSl)
            .build()

        stopLossCriteria = AdvancedCriteriaRequest(advType = 2, opr = 4, triggerVal = triggerValSl, triggerCategory = selectedCompareSl.toInt() ,triggerOrderSl)

        sltpValidation()
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

                if (checkARAandARBSltp(priceTp, closePrice, binding.tvErrorTpPrice, binding.adjusterSellPriceTp)) {
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
                                binding.tvErrorTpPrice.text = "Compare take profit price should be higher than stop loss compare price"
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

                if (checkARAandARBSltp(priceSl, closePrice, binding.tvErrorSlPrice, binding.adjusterSellPriceSl)) {
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
                                binding.tvErrorSlPrice.text = "Compare stop loss price should be lower than take profit compare price"
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

        binding.btnBuy.isEnabled = if (!tpChecked && !slChecked) {
            false
        } else {
            validationTp && validationSl && validationQtySl && validationQtyTp
        }
    }

    private fun sendAdvOrder() {
        val accNo = prefManager.accno
        val userId = prefManager.userId
        val sessionId = prefManager.sessionId
        val orderQty = binding.adjusterSellQtyTp.getDoubleTimes100()
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
            ip = ipAddress
        )

        viewModel.sendAdvOrder(oltOrder)
    }

    private fun checkARAandARBSltp(price: Double, closePrice:Double, errorTextView: TextView, adjuster: CustomQtyAdjuster): Boolean {
        var top:Double
        var bottom:Double
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
                    adjuster.changeColor(R.color.textRed)

                    if (!checkFractionPrice(arb.toInt())) {
                        val adjustFraction = adjustFractionPrice(arb.toInt(), "", isSpecialStock)
                        errorTextView.text = "Price can't be lower than ${adjustFraction}"
                    } else {
                        errorTextView.text = "Price can't be lower than ${arb.formatPriceWithoutDecimal()}"
                    }
                }
                price > top -> {
                    errorTextView.visibility = View.VISIBLE
                    adjuster.changeColor(R.color.textRed)

                    if (!checkFractionPrice(top.toInt())) {
                        val adjustFraction = adjustFractionPrice(top.toInt(), "-", isSpecialStock)
                        errorTextView.text = "Price can't be higher than ${adjustFraction}"
                    } else {
                        errorTextView.text = "Price can't be higher than ${top.formatPriceWithoutDecimal()}"
                    }
                }
                else -> {
                    errorTextView.visibility = View.GONE
                    adjuster.changeColor(R.color.txtBlackWhite)
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

    private fun checkboxTakeProfitisEnable(state: Boolean) {
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

    private fun checkboxStopLossisEnable(state: Boolean) {
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

    private fun showDialogPin() {
        showDialogPin(parentFragmentManager, onSuccess = { isSuccess, isBlocked ->
            if (isSuccess) {
                viewModel.getStockPos(userId, accNo, sessionId, stockCode)
                viewModel.getStockDetail(userId, sessionId, stockCode)
                viewModel.getAccountInfo(accNo)
            } else {
                if (isBlocked) {
                    viewModel.getLogout(prefManager.userId, prefManager.sessionId)
                } else {
                    onBackPressed()
                }
            }
        })
    }

    private fun takeProfitDefaultValue() {
        if (avgPrice != 0.0 || closePrice != 0.0) {
            val price = if (avgPrice != 0.0) avgPrice else closePrice
            val changes = price.times(0.05)
            val tpPrice = price.plus(changes)
            val fractionPrice = getFractionPrice(tpPrice.toInt()).formatPriceWithoutDecimal()

            binding.adjusterSellPriceTp.setEdt(fractionPrice)
            binding.edtCompareTp.setText(fractionPrice)
        }
    }

    private fun stopLossDefaultValue() {
        if (avgPrice != 0.0 || closePrice != 0.0) {
            val price = if (avgPrice != 0.0) avgPrice else closePrice
            val changes = price.times(0.05)
            val slPrice = price.minus(changes)
            val fractionPrice = getFractionPrice(slPrice.toInt()).formatPriceWithoutDecimal()

            binding.adjusterSellPriceSl.setEdt(fractionPrice)
            binding.edtCompareSl.setText(fractionPrice)
        }
    }
}