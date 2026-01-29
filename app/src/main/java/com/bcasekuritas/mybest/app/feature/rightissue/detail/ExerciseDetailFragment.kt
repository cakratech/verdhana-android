package com.bcasekuritas.mybest.app.feature.rightissue.detail

import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.request.WithdrawExerciseOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.response.ExerciseOrderListItem
import com.bcasekuritas.mybest.databinding.FragmentExerciseDetailBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.getIpAddress
import com.bcasekuritas.mybest.ext.common.getRandomString
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.converter.GET_4_CHAR_STOCK_CODE
import com.bcasekuritas.mybest.ext.converter.GET_IDX_BOARD
import com.bcasekuritas.mybest.ext.converter.GET_STATUS_ORDER
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarInterface
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal

@FragmentScoped
@AndroidEntryPoint
class ExerciseDetailFragment: BaseFragment<FragmentExerciseDetailBinding, ExerciseDetailViewModel>(),
    ShowDialog by ShowDialogImpl(), ShowSnackBarInterface by ShowSnackBarImpl() {

    override val bindingVariable: Int = BR.vmExerciseDetail
    override val viewModel: ExerciseDetailViewModel by viewModels()
    override val binding: FragmentExerciseDetailBinding by autoCleaned { (FragmentExerciseDetailBinding.inflate(layoutInflater)) }

    var data: ExerciseOrderListItem? = ExerciseOrderListItem()

    private var ipAddress = ""

    override fun setupArguments() {
        super.setupArguments()
        arguments?.let {
            data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(Args.EXTRA_PARAM_OBJECT, ExerciseOrderListItem::class.java)
            } else {
                it.getParcelable(Args.EXTRA_PARAM_OBJECT)
            }
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            toolbar.tvLayoutToolbarMasterTitle.text = "Detail"
            toolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener {
                onBackPressed()
            }

            val url = prefManager.urlIcon+ GET_4_CHAR_STOCK_CODE(data?.stockCode?:"")
            Glide.with(requireContext())
                .load(url)
                .circleCrop()
                .placeholder(R.drawable.bg_circle)
                .error(R.drawable.bg_circle)
                .into(ivLogo)

            tvStockCode.text = data?.stockCode
            tvPrice.text = data?.orderPrice?.formatPriceWithoutDecimal()
            tvQty.text = data?.orderQty?.formatPriceWithoutDecimal()

            val total = data?.orderPrice.let {price->
                data?.orderQty?.let { qty -> price?.times(qty) }
            }
            tvTotal.text = total?.formatPriceWithoutDecimal()
            data?.status?.let { updateStatus(it) }


        }
    }

    private fun updateStatus(status: String) {
        binding.tvStatus.text = status.GET_STATUS_ORDER()

        when (status) {
            "0" -> {
                binding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.textDown))
            }
            "1" -> {
                binding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.alwaysBlue))
            }
            "9" -> {
                binding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.textUp))
            }
            else -> {
                binding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }
        }

        if (status == "O" || status == "P") {
            binding.btnWithdrawOrderDetail.visibility = View.VISIBLE
        } else {
            binding.btnWithdrawOrderDetail.visibility = View.GONE
        }
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            btnWithdrawOrderDetail.setOnClickListener {
                showDialogWithdrawOrderBottom(NavKeys.KEY_FM_EXERCISE_DETAIL, childFragmentManager)
            }
        }
    }

    override fun setupListener() {
        super.setupListener()
        childFragmentManager.setFragmentResultListener(NavKeys.KEY_FM_EXERCISE_DETAIL, viewLifecycleOwner) { _, result ->
            // withdraw dialog
            val confirmResultWithdraw = result.getString(NavKeys.CONST_RES_WITHDRAW_ORDER_CONFIRM)
            if (confirmResultWithdraw == "RESULT_OK") {
                if (result.getBoolean("confirm")) {
                    sendWithdraw()
                    showSnackBarTop(requireContext(), binding.root, "success", R.drawable.ic_success, "Withdraw Buy is done", "", requireActivity(), "")
                    updateStatus("C")
                }
            }
        }
    }

    override fun initAPI() {
        super.initAPI()
        data?.stockCode?.let { viewModel.getStockParam(it) }

        ipAddress = getIpAddress(requireContext())
        viewModel.getIpAddress()
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

    private fun sendWithdraw() {
        val accNo = prefManager.accno
        val userId = prefManager.userId
        val sessionId = prefManager.sessionId

        val withdrawExerciseOrderRequest = WithdrawExerciseOrderRequest(
            sessionId,
            data?.transCode?: "",
            getRandomString(),
            userId,
            ipAddress,
            accNo
        )

        viewModel.sendWithdraw(withdrawExerciseOrderRequest)
    }

}