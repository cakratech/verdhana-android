package com.bcasekuritas.mybest.app.feature.e_ipo.order

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.data.layout.UIDialogExerciseOrderModel
import com.bcasekuritas.mybest.app.domain.dto.request.EipoOrderRequest
import com.bcasekuritas.mybest.app.feature.activity.main.SharedMainViewModel
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.e_ipo.eipodetail.EIPODetailSharedViewModel
import com.bcasekuritas.mybest.databinding.FragmentOrderEIPOBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.RabbitMQForegroundService
import com.bcasekuritas.mybest.ext.common.getCurrentTimeInMillis
import com.bcasekuritas.mybest.ext.common.getIpAddress
import com.bcasekuritas.mybest.ext.common.getRandomString
import com.bcasekuritas.mybest.ext.common.validateSessionPin
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.converter.CONVERT_PIPELINE_STATUS_NAME_EIPO
import com.bcasekuritas.mybest.ext.converter.GET_4_CHAR_STOCK_CODE
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import timber.log.Timber
import java.util.Date

@FragmentScoped
@AndroidEntryPoint
class OrderEIPOFragment : BaseFragment<FragmentOrderEIPOBinding, OrderEIPOViewModel>(), ShowDialog by ShowDialogImpl() {

    override val viewModel: OrderEIPOViewModel by viewModels()
    override val binding: FragmentOrderEIPOBinding by autoCleaned {
        (FragmentOrderEIPOBinding.inflate(
            layoutInflater
        ))
    }
    override val bindingVariable: Int = BR.vmOrderEIPO

    private lateinit var eipoSharedViewModel: EIPODetailSharedViewModel
    private lateinit var sharedMainViewModel: SharedMainViewModel

    private var stockCode = ""
    private var stockName = ""

    private var logoLink = ""

    private var offerPrice = 0.0
    private var totalPrice = 0.0
    private var priceFrom = 0.0
    private var priceTo = 0.0
    private var price = 0.0
    private var isEmployee = false
    private var isAffiliated = false
    private var isBenefical = false
    private var isAllocation = false
    private var validationPrice = false

    private var isBookBuilding = false

    private var ipAddress = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eipoSharedViewModel = ViewModelProvider(requireActivity()).get(EIPODetailSharedViewModel::class.java)
        sharedMainViewModel = ViewModelProvider(requireActivity()).get(SharedMainViewModel::class.java)
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            radioGroupEmployee.check(R.id.disabled_selected_employee)
            radioGroupAffiliated.check(R.id.disabled_selected_affiliated)
            radioGroupBeneficial.check(R.id.disabled_selected_beneficial)
            radioGroupAllocation.check(R.id.disabled_selected_allocation)
        }
    }

    override fun initAPI() {
        super.initAPI()
        ipAddress = getIpAddress(requireContext())
        viewModel.getIpAddress()
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            val textWatcherLot = object : TextWatcher {
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
                    validation()
                }
            }
            adjusterLot.setTextWatcher(textWatcherLot)

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
                    validation()
                }
            }
            adjusterPrice.setTextWatcher(textWatcherPrice)
        }

    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            btnBack.setOnClickListener {
                onBackPressed()
            }
            buttonBuy.setOnClickListener {
                if (isAllocation) {
                    showDialogAllocationEipoBottom(parentFragmentManager)
                } else {
                    showDialogConfirm()
                }
            }

            radioGroupEmployee.setOnCheckedChangeListener { _, selectedId ->
                isEmployee = selectedId == R.id.enabled_selected_employee
                groupAllocation.visibility = if (selectedId == R.id.enabled_selected_employee) View.VISIBLE else View.GONE

                validation()
            }
            radioGroupAffiliated.setOnCheckedChangeListener { _, selectedId ->
                isAffiliated = selectedId == R.id.enabled_selected_affiliated
                validation()
            }
            radioGroupBeneficial.setOnCheckedChangeListener { _, selectedId ->
                isBenefical = selectedId == R.id.enabled_selected_beneficial
                validation()
            }
            radioGroupAllocation.setOnCheckedChangeListener { _, selectedId ->
                isAllocation = selectedId == R.id.enabled_selected_allocation
                validation()
            }
        }
    }

    private fun validation() {
        binding.apply {
            price = if (isBookBuilding) binding.adjusterPrice.getDouble() else offerPrice
            val qty = binding.adjusterLot.getDouble().times(100)
            totalPrice = price.times(qty)

            tvPriceInfoVal.text = price.formatPriceWithoutDecimal()
            tvTotalVal.text = "Rp" + totalPrice?.formatPriceWithoutDecimal()

            if (price != 0.0) {
                if (isBookBuilding) {
                    when {
                        price < priceFrom -> {
                            validationPrice = false
                            binding.adjusterPrice.disableDecreaseBtn()
                            binding.adjusterPrice.changeColor(R.color.textRed)
                            tvErrorInputPrice.text =
                                "Input price only ranges from ${priceFrom.formatPriceWithoutDecimal()} to ${priceTo.formatPriceWithoutDecimal()}"
                            tvErrorInputPrice.isGone = false
                        }

                        price > priceTo -> {
                            validationPrice = false
                            binding.adjusterPrice.disableIncreaseBtn()
                            binding.adjusterPrice.changeColor(R.color.textRed)
                            tvErrorInputPrice.text =
                                "Input price only ranges from ${priceFrom.formatPriceWithoutDecimal()} to ${priceTo.formatPriceWithoutDecimal()}"
                            tvErrorInputPrice.isGone = false
                        }
                        !checkFractionPrice(price.toInt()) -> {
                            binding.adjusterPrice.changeColor(R.color.textRed)
                            tvErrorInputPrice.text = "The price entered does not align with the fractional value"
                            tvErrorInputPrice.isGone = false
                            validationPrice = false
                        }
                        else -> {
                            validationPrice = true
                            binding.adjusterPrice.enableIncreaseBtn()
                            binding.adjusterPrice.enableDecreaseBtn()
                            binding.adjusterPrice.changeColor(R.color.txtBlackWhite)
                            tvErrorInputPrice.isGone = true
                        }
                    }
                } else {
                    validationPrice = true
                }
            } else {
                validationPrice = false
            }

            buttonBuy.isEnabled = when {
                adjusterLot.getInt() == 0  -> false
                radioGroupEmployee.checkedRadioButtonId == -1 -> false
                radioGroupAffiliated.checkedRadioButtonId == -1 -> false
                radioGroupBeneficial.checkedRadioButtonId == -1 -> false
                radioGroupAllocation.checkedRadioButtonId == -1 -> false
                else -> validationPrice
            }
        }

    }

    private fun checkFractionPrice(price: Int): Boolean {
        val modVal = when {
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

    private fun showDialogConfirm() {
        val lot = binding.adjusterLot.getDouble().formatPriceWithoutDecimal()
        val total = binding.tvTotalVal.text.toString()
        val uiConfirmationModel = UIDialogExerciseOrderModel(
            stockCode,
            stockName,
            price.formatPriceWithoutDecimal(),
            lot,
            total,
            logoLink
        )

        showDialogEIPOOrderConfirm(uiConfirmationModel, parentFragmentManager)

        parentFragmentManager.setFragmentResultListener(
            NavKeys.KEY_FM_EIPO_ORDER, viewLifecycleOwner
        ) { _, result ->
            val confirmResult = result.getString(NavKeys.CONST_RES_EIPO_ORDER)
            if (confirmResult == "RESULT_OK") {
                viewModel.getSessionPin(prefManager.userId)
            }
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.getIpAddressResult.observe(viewLifecycleOwner) {value ->
            ipAddress = value.ifEmpty { getIpAddress(requireContext()) }
        }

        eipoSharedViewModel.getIpoData.observe(viewLifecycleOwner) {ipoData ->
            if (ipoData != null) {
                binding.tvStatus.text = CONVERT_PIPELINE_STATUS_NAME_EIPO(ipoData.status)

                isBookBuilding = ipoData.status == 1
                if (ipoData.status == 1) {
                    priceFrom = ipoData.bookPriceFrom
                    priceTo = ipoData.bookPriceTo

                    binding.adjusterPrice.setEdt(priceTo.toString())

                    binding.adjusterPrice.visibility = View.VISIBLE
                    binding.tvPriceVal.visibility = View.GONE

                    binding.tvStockPrice.text = "${ipoData.bookPriceFrom.formatPriceWithoutDecimal()} - ${ipoData.bookPriceTo.formatPriceWithoutDecimal()}"
                } else {
                    binding.tvPriceVal.text = ipoData.offeringPrice.formatPriceWithoutDecimal()
                    offerPrice = ipoData.offeringPrice

                    binding.adjusterPrice.visibility = View.GONE
                    binding.tvPriceVal.visibility = View.VISIBLE

                    binding.tvStockPrice.text = "${offerPrice.formatPriceWithoutDecimal()}"
                    binding.tvPriceInfoVal.text = "${offerPrice.formatPriceWithoutDecimal()}"
                }

                stockCode = ipoData.code
                binding.tvEmployee.text = "An employee of ${ipoData.code}?"
                stockName = ipoData.companyName
                binding.tvStockCode.text = stockCode
                binding.tvStockName.text = stockName
                logoLink = ipoData.logoLink

                val stockCodes = GET_4_CHAR_STOCK_CODE(stockCode)
                val url = if (ipoData.logoLink.isNotEmpty()) prefManager.urlIcon + ipoData.logoLink else prefManager.urlIcon + stockCodes
                Glide.with(requireActivity())
                    .load(url)
                    .override(300, 200)
                    .circleCrop()
                    .placeholder(R.drawable.bg_circle)
                    .error(R.drawable.bg_circle)
                    .into(binding.ivLogo)

            }
        }

        viewModel.getSessionPinResult.observe(viewLifecycleOwner) {
            val sessionPin = it
            if (sessionPin != null) {
                if (validateSessionPin(sessionPin)) {
                    viewModel.getIpoInfo(prefManager.userId, prefManager.sessionId, stockCode)
                } else {
                    showDialogPin()
                }
            }
        }

        viewModel.getIpoInfoResult.observe(viewLifecycleOwner) {ipoData ->
            if (ipoData != null) {
                val currentDate = getCurrentTimeInMillis()
                val deadlineBook = ipoData.deadlineBookDate
                val deadlineOffer = ipoData.deadlineOfferDate
                when (ipoData.statusId) {
                    "2" -> if (deadlineBook != 0L && currentDate >= deadlineBook) onBackPressed() else sendOrder()
                    "3" -> if (deadlineOffer != 0L && currentDate >= deadlineOffer) onBackPressed() else sendOrder()
                    else -> onBackPressed()
                }
            } else {
                onBackPressed()
            }
        }

        viewModel.getLogoutResult.observe(viewLifecycleOwner) {
            when (it?.status){
                0 -> {
                    RabbitMQForegroundService.stopService(requireContext())
                    sharedMainViewModel.setStopSubs(true)
                    viewModel.deleteSession()
                    prefManager.clearPreferences()
                    MiddleActivity.startIntentWithFinish(requireActivity(), NavKeys.KEY_FM_LOGIN, true)
                }
                else -> {
                    Timber.e("${it?.status} : ${it?.remarks}" )
                }
            }
        }
    }

    private fun showDialogPin() {
        showDialogPin(parentFragmentManager, onSuccess = { isSuccess, isBlocked ->
            if (isSuccess) {
                if (isAdded) {
                    viewModel.getIpoInfo(prefManager.userId, prefManager.sessionId, stockCode)
                }
            } else {
                if (isBlocked) {
                    viewModel.getLogout(prefManager.userId, prefManager.sessionId)
                }
            }
        })
    }

    private fun sendOrder() {
        val qty = binding.adjusterLot.getDouble()
        val eipoOrderRequest = EipoOrderRequest(
            clOrderRef = getRandomString(),
            prefManager.userId,
            prefManager.sessionId,
            accNo = prefManager.accno,
            price = price,
            qty = qty,
            eipoCode = stockCode,
            ipAddress = ipAddress,
            orderTime = Date().time,
            isEmployee = isEmployee,
            isBenefaciaries = isBenefical,
            isAffiliatedParty = isAffiliated,
            isSelfOrder = isAllocation
        )

        viewModel.sendOrder(eipoOrderRequest)
        Log.d("ordereip", "success")

        val bundle = Bundle().apply {
            putString(Args.EXTRA_PARAM_STR_ONE, stockCode)
            putBoolean(Args.EXTRA_PARAM_BOOLEAN, true)
        }
        eipoSharedViewModel.setIpoData(null)
        val navOptions = NavOptions.Builder()
            .setPopUpTo(
                R.id.eipo_detail_fragment,
                true
            ) // clear back stack queue up to start destination or in this case eipo detail
            .build()

        findNavController().navigate(R.id.eipo_detail_fragment, bundle, navOptions)
    }


}