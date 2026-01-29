package com.bcasekuritas.mybest.app.feature.pricealert.createedit

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.PriceAlertItem
import com.bcasekuritas.mybest.app.feature.stockdetail.StockDetailSharedViewModel
import com.bcasekuritas.mybest.databinding.FragmentCreateEditPriceAlertBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.adjustFractionPrice
import com.bcasekuritas.mybest.ext.common.getFractionNumber
import com.bcasekuritas.mybest.ext.common.initFormatThousandSeparator
import com.bcasekuritas.mybest.ext.common.initPercentFormatNumber
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.converter.GET_4_CHAR_STOCK_CODE
import com.bcasekuritas.mybest.ext.converter.GET_IDX_BOARD
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.widget.adjuster.qtyAdjusterClickListener
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class CreateEditPriceAlertFragment: BaseFragment<FragmentCreateEditPriceAlertBinding, CreateEditPriceAlertViewModel>(), ShowDropDown by ShowDropDownImpl() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmCreateEditPriceAlert
    override val viewModel: CreateEditPriceAlertViewModel by viewModels()
    override val binding: FragmentCreateEditPriceAlertBinding by autoCleaned {
        (FragmentCreateEditPriceAlertBinding.inflate(
            layoutInflater
        ))
    }

    lateinit var sharedViewModel: StockDetailSharedViewModel

    private var userId = ""
    private var sessionId = ""

    private var stockCode = "BBCA"
    private var percentSelection = 0.0
    private var lastPrice = 0.0
    private var fractionNumber = 1
    private var isNegative = false
    private var stockCodeList = arrayListOf<String>()
    private var isEditPriceAlert = false
    private var isFromStockDetail = false
    private var data: PriceAlertItem? = PriceAlertItem()

    companion object {
        fun newInstance() = CreateEditPriceAlertFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity()).get(StockDetailSharedViewModel::class.java)

        arguments?.let {
            stockCode = it.getString(Args.EXTRA_PARAM_STR_ONE).toString()
            isEditPriceAlert = it.getBoolean(Args.EXTRA_PARAM_BOOLEAN)
            isFromStockDetail = it.getBoolean(Args.EXTRA_PARAM_BOOLEAN_TWO)
            data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(Args.EXTRA_PARAM_OBJECT, PriceAlertItem::class.java)
            } else {
                it.getParcelable(Args.EXTRA_PARAM_OBJECT)
            }

        }
    }

    override fun setupListener() {
        super.setupListener()
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

            }

            override fun afterTextChanged(s: Editable?) {
                val price = binding.adjusterPriceTarget.getDouble()
                fractionNumber = getFractionNumber(price.toInt())

                when{
                    price == 0.0 -> binding.btnSubmit.isEnabled = false

                    !checkFractionPrice(price.toInt(), fractionNumber) -> {
                        binding.adjusterPriceTarget.changeColor(R.color.textRed)
                        binding.tvErrorInput.isGone = false
                        binding.btnSubmit.isEnabled = false
                    } else -> {
                        binding.adjusterPriceTarget.changeColor(R.color.txtBlackWhite)
                        binding.tvErrorInput.isGone = true
                        binding.btnSubmit.isEnabled = true
                    }
                }
            }
        }
        binding.adjusterPriceTarget.setTextWatcher(textWatcherPrice)
    }

    override fun onResume() {
        super.onResume()
        viewModel.setListenerTradeSummary()
    }

    override fun onPause() {
        super.onPause()
        viewModel.unSubscribeTradeSummary(stockCode)
    }

    override fun setupComponent() {
        super.setupComponent()

        chipOnClick()
        binding.apply {
            toolbar.tvLayoutToolbarMasterTitle.text = if (isEditPriceAlert) "Edit Price Alert" else "Create Price Alert"
            ivStockDetailInfoDown.visibility = if (isEditPriceAlert) View.GONE else View.VISIBLE

            if (data != null) {
                if (data?.stockCode != "") {
                    stockCode = data?.stockCode.toString()
                }

                data?.price?.formatPriceWithoutDecimal()?.let { price -> binding.adjusterPriceTarget.setEdt(price) }
            }

            val url = prefManager.urlIcon+ GET_4_CHAR_STOCK_CODE(stockCode)
            Glide.with(requireContext())
                .load(url)
                .circleCrop()
                .placeholder(R.drawable.bg_circle)
                .error(R.drawable.bg_circle)
                .into(imageStock)

            tvStockDetailInfoCode.text = stockCode

            if (isFromStockDetail){
                toolbar.ivLayoutToolbarMasterIconRightOne.visibility = View.VISIBLE
                toolbar.ivLayoutToolbarMasterIconRightOne.setImageResource(R.drawable.ic_file)
            }

            swplPriceAlertCreateEdit.setOnRefreshListener {
                viewModel.getStockDetailSummary(userId, sessionId, stockCode, true)
                viewModel.getStockParam(stockCode)
                viewModel.getStockNotation(stockCode)
            }
        }
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            toolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener {
                onBackPressed()
            }

            ivStockDetailInfoDown.setOnClickListener {
                showDropDownStringSearchable(
                    requireContext(),
                    stockCodeList.sorted(),
                    binding.viewDropdownStock,
                    "Search stock code"
                ) { index, value ->
                    viewModel.unSubscribeTradeSummary(stockCode)
                    stockCode = value
                    tvStockDetailInfoCode.text = value
                    resetChip()

                    viewModel.getStockDetailSummary(userId, sessionId, stockCode, false)
                    viewModel.getStockParam(stockCode)
                    viewModel.getStockNotation(stockCode)
                }
            }

            btnSubmit.setOnClickListener {
                val price = adjusterPriceTarget.getDouble()

                if (isEditPriceAlert) {
                    val id = data?.id
                    if (id != null && data?.id != 0L) {
                        viewModel.editPriceAlert(userId, sessionId, stockCode, price, id)
                    }
                } else {
                    viewModel.addPriceAlert(userId, sessionId, stockCode, price)
                }
            }

            toolbar.ivLayoutToolbarMasterIconRightOne.setOnClickListener {
                val bundle = Bundle().apply {
                    putString(Args.EXTRA_PARAM_STR_ONE, "")
                }
                val navController = findNavController()
                val id = R.id.stock_detail_fragment

                val navOptions = NavOptions.Builder()
                    .setPopUpTo(id, false) // clear back stack queue up to start destination or in this case stock detail
                    .build()

                navController.navigate(R.id.price_alert_fragment, bundle, navOptions)
            }

            adjusterPriceTarget.listener = object : qtyAdjusterClickListener{
                override fun onMinusClick() {
                    adjusterPriceTarget.setEdt(adjustFractionPrice(adjusterPriceTarget.getDouble().toInt(), fractionNumber, "-"))
                }

                override fun onPlusClick() {
                    adjusterPriceTarget.setEdt(adjustFractionPrice(adjusterPriceTarget.getDouble().toInt(), fractionNumber, "+"))
                }
            }
        }
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        sessionId = prefManager.sessionId

        viewModel.getAllStockParam("")
        viewModel.getStockDetailSummary(userId, sessionId, stockCode, false)
        viewModel.getStockParam(stockCode)
        viewModel.getStockNotation(stockCode)

    }

    override fun setupObserver() {
        super.setupObserver()

        viewModel.addPriceAlertResult.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it.status) {
                    0 -> {
                        sharedViewModel.setPopUpSuccessPriceAlert(true)
                        if (isFromStockDetail) {
                            val bundle = Bundle().apply {
                            putString(Args.EXTRA_PARAM_STR_ONE, stockCode)
                            }
                            val navController = findNavController()
                            val id = R.id.stock_detail_fragment

                            val navOptions = NavOptions.Builder()
                                .setPopUpTo(id, false) // clear back stack queue up to start destination or in this case stock detail
                                .build()

                            navController.navigate(R.id.price_alert_fragment, bundle, navOptions)
                        } else {
                            onBackPressed()
                        }
                    }
                    else -> {

                        sharedViewModel.setPopUpSuccessPriceAlert(false)
                        if (isFromStockDetail) {
                            val bundle = Bundle().apply {
                                putString(Args.EXTRA_PARAM_STR_ONE, stockCode)
                            }
                            val navController = findNavController()
                            val id = R.id.stock_detail_fragment

                            val navOptions = NavOptions.Builder()
                                .setPopUpTo(id, false) // clear back stack queue up to start destination or in this case stock detail
                                .build()

                            navController.navigate(R.id.price_alert_fragment, bundle, navOptions)
                        } else {
                            onBackPressed()
                        }
                    }
                }
            }
        }

        viewModel.getStockDetailResult.observe(viewLifecycleOwner) {
            if (it != null) {
                val change = it.change
                lastPrice = if(it.last != 0.0) it.last else it.close

                binding.apply {
                    swplPriceAlertCreateEdit.isRefreshing = false
                    if (data?.price == 0.0 || data == null) {
                        adjusterPriceTarget.setEdt(it.last.formatPriceWithoutDecimal())
                    }
                    tvStockDetailInfoPrice.text = initFormatThousandSeparator(lastPrice)
                    tvStockDetailInfoChange.text =
                        "${change.formatPriceWithoutDecimal()} (${initPercentFormatNumber(it.changePct)})"

                    if (change > 0) {
                        tvStockDetailInfoChange.text =
                            "+${change.formatPriceWithoutDecimal()} (+${initPercentFormatNumber(it.changePct)})"
                        tvStockDetailInfoChange.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.textLightGreen
                            )
                        )
                    } else if (change < 0) {
                        tvStockDetailInfoChange.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.textDownHeader
                            )
                        )
                    } else {
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

        viewModel.getStockParamResult.observe(viewLifecycleOwner) {
            val url = prefManager.urlIcon + GET_4_CHAR_STOCK_CODE(stockCode)

            Glide.with(requireActivity())
                .load(url)
                .override(300, 200)
                .circleCrop()
                .placeholder(R.drawable.bg_circle)
                .error(R.drawable.bg_circle)
                .into(binding.imageStock)

            binding.tvStockDetailInfoName.text = it?.stockName
            binding.tvStockDetailInfoHaircut.text = "${it?.hairCut?.formatPriceWithoutDecimal()}%"
            binding.tvStockDetailInfoSpecialNotesAcceleration.text = it?.idxTrdBoard.GET_IDX_BOARD()
            binding.tvStockDetailInfoSpecialNotesAcceleration.visibility = if (binding.tvStockDetailInfoSpecialNotesAcceleration.text == "") View.GONE else View.VISIBLE
        }

        viewModel.getStockNotationResult.observe(viewLifecycleOwner) {
            if (it.size != 0) {
                val listNotation = mutableListOf<String>()
                it.forEach {
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

        viewModel.getAllStockParamResult.observe(viewLifecycleOwner) {
            stockCodeList.clear()
            it?.map { data -> data?.let { stockCodeList.add(data.stockCode) } }
        }

        viewModel.getSubscribeTradeSummary.observe(viewLifecycleOwner) {
            if (it != null) {
                val change = it.change
                val price = if(it.last != 0.0) it.last else it.close

                binding.apply {
                    tvStockDetailInfoPrice.text = initFormatThousandSeparator(price)
                    tvStockDetailInfoChange.text = "${change.formatPriceWithoutDecimal()} (${initPercentFormatNumber(it.changePct)})"

                    if (change > 0) {
                        tvStockDetailInfoChange.text =
                            "+${change.formatPriceWithoutDecimal()} (+${initPercentFormatNumber(it.changePct)})"
                        tvStockDetailInfoChange.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.textLightGreen
                            )
                        )
                    } else if (change < 0) {
                        tvStockDetailInfoChange.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.textDownHeader
                            )
                        )
                    } else {
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
    }

    private fun calculatePercent() {
        if (lastPrice != 0.0) {
            val percent = lastPrice.times(percentSelection)
            var price = if (isNegative) lastPrice - percent else lastPrice + percent
            fractionNumber = getFractionNumber(price.toInt())
            if (checkFractionPrice(price.toInt(), fractionNumber)) {
                binding.adjusterPriceTarget.setEdt(price.toInt().formatPriceWithoutDecimal())
            } else {
                val fractionPrice = adjustFractionPrice(price.toInt(), fractionNumber, "-")
                binding.adjusterPriceTarget.setEdt(fractionPrice)
            }
        }
    }

    fun checkFractionPrice(price: Int, fractionNumber: Int): Boolean {
        return if (fractionNumber == 1) {
            true
        } else {
            if (fractionNumber != 0) {
                price % fractionNumber == 0
            } else {
                false
            }
        }
    }

    private fun chipOnClick() {
        binding.apply {
            chip1.setOnClickListener {
                percentSelection = 0.10
                isNegative = true
                calculatePercent()

                chip1.isChecked = true
                chip2.isChecked = false
                chip3.isChecked = false
                chip4.isChecked = false
                chip5.isChecked = false
                chip6.isChecked = false
            }

            chip2.setOnClickListener {
                percentSelection = 0.05
                isNegative = true
                calculatePercent()

                chip2.isChecked = true
                chip1.isChecked = false
                chip3.isChecked = false
                chip4.isChecked = false
                chip5.isChecked = false
                chip6.isChecked = false
            }

            chip3.setOnClickListener {
                percentSelection = 0.03
                isNegative = true
                calculatePercent()

                chip3.isChecked = true
                chip2.isChecked = false
                chip1.isChecked = false
                chip4.isChecked = false
                chip5.isChecked = false
                chip6.isChecked = false
            }

            chip4.setOnClickListener {
                percentSelection = 0.03
                isNegative = false
                calculatePercent()

                chip4.isChecked = true
                chip2.isChecked = false
                chip3.isChecked = false
                chip1.isChecked = false
                chip5.isChecked = false
                chip6.isChecked = false
            }

            chip5.setOnClickListener {
                percentSelection = 0.05
                isNegative = false
                calculatePercent()

                chip5.isChecked = true
                chip2.isChecked = false
                chip3.isChecked = false
                chip4.isChecked = false
                chip1.isChecked = false
                chip6.isChecked = false
            }

            chip6.setOnClickListener {
                percentSelection = 0.10
                isNegative = false
                calculatePercent()

                chip6.isChecked = true
                chip2.isChecked = false
                chip3.isChecked = false
                chip4.isChecked = false
                chip1.isChecked = false
                chip5.isChecked = false
            }


        }
    }

    private fun resetChip() {
        binding.apply {
            chip1.isChecked = false
            chip2.isChecked = false
            chip3.isChecked = false
            chip4.isChecked = false
            chip5.isChecked = false
            chip6.isChecked = false

            adjusterPriceTarget.setEdt("0")
        }
    }

}