package com.bcasekuritas.mybest.app.feature.rightissue.exercise

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.data.layout.UIDialogExerciseOrderModel
import com.bcasekuritas.mybest.app.domain.dto.request.SendExerciseOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.response.RightIssueParcelable
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.activity.main.SharedMainViewModel
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.databinding.FragmentRightIssueDetailBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.RabbitMQForegroundService
import com.bcasekuritas.mybest.ext.common.getIpAddress
import com.bcasekuritas.mybest.ext.common.getRandomString
import com.bcasekuritas.mybest.ext.common.validateSessionPin
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.converter.GET_4_CHAR_STOCK_CODE
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarInterface
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import timber.log.Timber
import java.util.Date

@FragmentScoped
@AndroidEntryPoint
class ExerciseFragment : BaseFragment<FragmentRightIssueDetailBinding, ExerciseViewModel>(), ShowDropDown by ShowDropDownImpl(), ShowSnackBarInterface by ShowSnackBarImpl() {

    override val viewModel: ExerciseViewModel by viewModels()
    override val binding: FragmentRightIssueDetailBinding by autoCleaned { (FragmentRightIssueDetailBinding.inflate(layoutInflater)) }
    override val bindingVariable: Int = BR.vmExercise

    private lateinit var sharedMainViewModel: SharedMainViewModel

    var data: RightIssueParcelable? = RightIssueParcelable()

    private var ipAddress = ""
    private var userId = ""
    private var sessionId = ""
    private var accNo = ""
    private var sessionPin: Long? = null
    private var tradingBalance: Double = 0.0

    private var totalPrice = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedMainViewModel = ViewModelProvider(requireActivity()).get(SharedMainViewModel::class.java)
    }

    override fun setupArguments() {
        super.setupArguments()
        arguments?.let {
            data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(Args.EXTRA_PARAM_OBJECT, RightIssueParcelable::class.java)
            } else {
                it.getParcelable(Args.EXTRA_PARAM_OBJECT)
            }
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            toolbar.tvLayoutToolbarMasterTitle.text = "Exercise"
            tvStockCode.text = data?.instrumentCode
            tvStockName.text = data?.stockName

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
                    setTotal()
                    validation()
                }
            }
            binding.adjusterBuyOrderShares.setTextWatcher(textWatcherCompareSl)
        }

        setupUIData()
    }

    private fun validation() {
        val qty = binding.adjusterBuyOrderShares.getDouble()
        val maxQty = data?.maxQty?: 0.0
        binding.btnExercise.isEnabled = when {
            binding.adjusterBuyOrderShares.getInt() == 0 -> {
                false
            }
            totalPrice > tradingBalance -> {
                setErrorText(state = true, isErrorQty = false)
                false
            }
            qty > maxQty -> {
                setErrorText(state = true, isErrorQty = true)
                false
            }
            else -> {
                setErrorText(state = false, isErrorQty = false)
                true
            }
        }
    }

    private fun setErrorText(state: Boolean, isErrorQty: Boolean) {
        binding.tvErrorValidation.visibility = if (state) View.VISIBLE else View.GONE
        binding.tvErrorValidation.text = if (isErrorQty) "Your available shares is not enough" else "Your balance is not enough"
    }

    private fun setTotal() {
        val qty = binding.adjusterBuyOrderShares.getDouble()
        totalPrice = data?.price?.times(qty) ?: 0.0
        binding.tvTotal.text = "Rp" + totalPrice?.formatPriceWithoutDecimal()
    }

    private fun setupUIData() {
        binding.apply {
            lyTotalValue.visibility = View.GONE
            lyCurrentPrice.visibility = View.GONE

            Glide.with(requireContext())
                .load(prefManager.urlIcon+ GET_4_CHAR_STOCK_CODE(data?.stockCode?:""))
                .circleCrop()
                .placeholder(R.drawable.bg_circle)
                .error(R.drawable.bg_circle)
                .into(ivLogo)

//            tvTotalValue.text = "Rp" + data?.totalValue?.formatPriceWithoutDecimal()
            tvOwnedShares.text = data?.stockPosQty?.formatPriceWithoutDecimal()
//            tvCurrentPrice.text = data?.currentPrice?.formatPriceWithoutDecimal()

            tvFirstTrade.text = data?.startDate?.let { DateUtils.convertLongToDate(it, "dd MMM yyyy") }
            tvEndOfTrade.text = data?.endDate?.let { DateUtils.convertLongToDate(it, "dd MMM yyyy") }

            tvAvailableShares.text = data?.maxQty?.formatPriceWithoutDecimal()
            tvBuyPrice.text = data?.price?.formatPriceWithoutDecimal()
        }
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            toolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener {
                onBackPressed()
            }
            btnExercise.setOnClickListener{
                showDialogConfirm()
            }
        }
    }

    private fun showDialogConfirm() {
        val uiConfirmationModel = UIDialogExerciseOrderModel(
            data?.instrumentCode,
            data?.stockName,
            data?.price?.formatPriceWithoutDecimal(),
            binding.adjusterBuyOrderShares.getString(),
            binding.tvTotal.text.toString()
        )

        showDialogExerciseOrderConfirm(uiConfirmationModel, parentFragmentManager)

        parentFragmentManager.setFragmentResultListener(
            NavKeys.KEY_FM_EXERCISE_ORDER, viewLifecycleOwner
        ){_, result ->
            val confirmResult = result.getString(NavKeys.CONST_RES_EXERCISE_ORDER)
            if (confirmResult == "RESULT_OK") {
                sendOrder()

                val bundle = Bundle().apply {
                    putBoolean(Args.EXTRA_PARAM_BOOLEAN, true)
                }

                val id = R.id.right_issue_fragment
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(id, false) // clear back stack queue up to start destination or in this case stock detail
                    .build()

                findNavController().navigate(R.id.exercise_order_list_fragment, bundle, navOptions)
            }
        }

    }

    override fun initAPI() {
        super.initAPI()

        userId = prefManager.userId
        sessionId = prefManager.sessionId
        accNo = prefManager.accno

        viewModel.getSessionPin(userId)

        ipAddress = getIpAddress(requireContext())
        viewModel.getIpAddress()
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.getIpAddressResult.observe(viewLifecycleOwner) {value ->
            ipAddress = value.ifEmpty { getIpAddress(requireContext()) }
        }

        viewModel.getSessionPinResult.observe(viewLifecycleOwner) {
            sessionPin = it
            if (sessionPin != null) {
                if (validateSessionPin(sessionPin ?: 0)) {
                    viewModel.getSimplePortfolio(userId, sessionId, accNo)
                } else {
                    showDialogPin()
                }
            }
        }

        viewModel.getSimplePortfolioResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }

                is Resource.Success -> {
                    when (it.data?.status) {
                        0 -> {
                            it.data.simplePortofolio.let { res ->
                                tradingBalance = res.cashT0
                                binding.tvTradingBalance.text = "Rp${res.cashT0.formatPriceWithoutDecimal()}"
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

        viewModel.getLogoutResult.observe(viewLifecycleOwner) {
            when (it?.status){
                0 -> {
                    RabbitMQForegroundService.stopService(requireContext())
                    sharedMainViewModel.setStopSubs(true)
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
    }

    private fun showDialogPin() {
        showDialogPin(parentFragmentManager, onSuccess = {isSuccess, isBlocked ->
            if (isSuccess) {
                if (isAdded) {
                    viewModel.getSimplePortfolio(userId, sessionId, accNo)
                }
            } else {
                if (isBlocked) {
                    viewModel.getLogout(userId, prefManager.sessionId)
                } else {
                    if (isAdded) {
                        onBackPressed()
                    }
                }

            }
        })
    }

    private fun sendOrder() {
        val accNo = prefManager.accno
        val userId = prefManager.userId
        val sessionId = prefManager.sessionId

        val amount = data?.price?.times(binding.adjusterBuyOrderShares.getInt())

        val request = SendExerciseOrderRequest(
            sessionId,
            getRandomString(),
            Date().time,
            0,
            accNo,
            data?.instrumentCode?: "",
            binding.adjusterBuyOrderShares.getDouble(),
            data?.price?:0.0,
            amount?:0.0,
            ipAddress,
            userId,
            channel = 0
        )

        viewModel.sendExerciseOrder(request)
    }
}