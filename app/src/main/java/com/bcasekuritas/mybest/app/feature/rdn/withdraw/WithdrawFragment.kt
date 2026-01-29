package com.bcasekuritas.mybest.app.feature.rdn.withdraw

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.data.layout.UIDialogWithdrawModel
import com.bcasekuritas.mybest.app.feature.rdn.topup.TopUpFragment
import com.bcasekuritas.mybest.databinding.FragmentWithdrawBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.converter.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.converter.removeSeparator
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarInterface
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.view.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import kotlin.math.floor

@FragmentScoped
@AndroidEntryPoint
class WithdrawFragment : BaseFragment<FragmentWithdrawBinding, WithdrawViewModel>(), ShowDialog by ShowDialogImpl(), ShowSnackBarInterface by ShowSnackBarImpl()  {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmWithdraw
    override val viewModel: WithdrawViewModel by viewModels()
    override val binding: FragmentWithdrawBinding by autoCleaned {(FragmentWithdrawBinding.inflate(layoutInflater))}

    private var userId = ""
    private var sessionId = ""
    private var accNo = ""
    private var cifCode = ""

    private var bankName = ""
    private var bankCode = ""
    private var accName = ""

    private var withdrawableAmount = 0.0
    private var processedAmount = 0.0


    companion object {
        fun newInstance() = TopUpFragment()
    }

    override fun setupComponent() {
        binding.lyToolbarWithdraw.tvLayoutToolbarMasterTitle.text = getString(R.string.text_withdraw)
        binding.lyToolbarWithdraw.ivLayoutToolbarMasterIconLeft.visibility = View.VISIBLE

        binding.swipeRefreshLayoutWithdraw.isEnabled = false

        binding.lyToolbarWithdraw.ivLayoutToolbarMasterIconLeft.setOnClickListener {
            requireActivity().finish()
        }

        binding.ivWithdrawAmountInfo.setOnClickListener {
            showDialogWithdrawInfoBottom("Rp${withdrawableAmount.formatPriceWithoutDecimal()}","Rp${processedAmount.formatPriceWithoutDecimal()}", parentFragmentManager)
        }

        binding.ivProcessedAmountInfo.setOnClickListener {
            showDialogWithdrawInfoBottom("Rp${withdrawableAmount.formatPriceWithoutDecimal()}", "Rp${processedAmount.formatPriceWithoutDecimal()}", parentFragmentManager)
        }

        binding.tvMaxAmount.setOnClickListener {
            binding.etAmount.setText(withdrawableAmount.formatPriceWithoutDecimal())
        }

        val textWatcherAmount = object : TextWatcher {
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
                binding.etAmount.removeTextChangedListener(this)

                val removeSeparator = s.toString().removeSeparator()
                val formattedText = removeSeparator.formatPriceWithoutDecimal()

                binding.etAmount.setText(formattedText)
                binding.etAmount.setSelection(formattedText.length)
                binding.etAmount.addTextChangedListener(this)

                val amount = removeSeparator.toDoubleOrNull() ?: 0.0

                binding.buttonNext.isEnabled = if (amount < 50000.0) {
                    binding.tvErrorAmount.text = if (amount == 0.0) "Withdrawable amount should be filled" else "Minimum withdraw 50.000"
                    binding.rlAmount.setBackgroundResource(R.drawable.rounded_ffffff_stroke_e14343_8)
                    binding.tvErrorAmount.visibility = View.VISIBLE
                    binding.tvAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.textDown))
                    false
                } else if(amount > withdrawableAmount){
                    binding.tvErrorAmount.text = if (amount == 0.0) "Withdrawable amount should be filled" else "Maximum amount to withdraw ${withdrawableAmount.formatPriceWithoutDecimal()}"
                    binding.rlAmount.setBackgroundResource(R.drawable.rounded_ffffff_stroke_e14343_8)
                    binding.tvErrorAmount.visibility = View.VISIBLE
                    binding.tvAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.textDown))
                    false
                } else {
                    binding.rlAmount.setBackgroundResource(R.drawable.rounded_dae8f6_8)
                    binding.tvErrorAmount.visibility = View.GONE
                    binding.tvAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    true
                }
            }
        }
        binding.etAmount.addTextChangedListener(textWatcherAmount)

        binding.buttonNext.setSafeOnClickListener {
            val amount = binding.etAmount.text.toString().removeSeparator().toDoubleOrNull() ?: 0.0

            val uiDialogWithdrawModel = UIDialogWithdrawModel(
                bankName,
                accName,
                amount.formatPriceWithoutDecimal(),
                "",
                ""
            )

            showDialogWithdrawConfirmBottom(uiDialogWithdrawModel, parentFragmentManager)

            parentFragmentManager.setFragmentResultListener(NavKeys.KEY_FM_WITHDRAW, viewLifecycleOwner) { _, result ->
                val confirmResult = result.getString(NavKeys.CONST_RES_WITHDRAW_CONFIRM)
                if (confirmResult == "RESULT_OK"){
                    binding.buttonNext.isEnabled = false
                    viewModel.sendWithdrawCash(userId, sessionId, accNo, amount)
                }
            }
        }

    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        sessionId = prefManager.sessionId
        accNo = prefManager.accno
        cifCode = prefManager.cifCode

        viewModel.getAccountInfo(userId, cifCode, sessionId)
        viewModel.getCashPos(userId, cifCode, sessionId)
    }

    override fun setupObserver() {
        super.setupObserver()

        viewModel.getCashPosResult.observe(viewLifecycleOwner){
            if (it != null) {
                it.groupCashPosList?.let { item ->
                    if (item.isNotEmpty()) {
                        item.forEach { cashPos ->
                            if (accNo == cashPos.ccGroupCode) {
                                val amount = cashPos.cashAvailForWithdraw
                                withdrawableAmount = if (amount > 0.0) floor(amount).minus(1.0) else amount
                                val processedAmounts = cashPos.cashoutstandAp?.plus(cashPos.tlsellmatch)
                                processedAmount = processedAmounts ?: 0.0

                                binding.tvWithdrawableAmount.text = "Rp${withdrawableAmount.formatPriceWithoutDecimal()}"
                                binding.tvProcessedAmount.text = "Rp${processedAmount.formatPriceWithoutDecimal()}"
                            }
                        }
                    }
                }
            }
        }


        viewModel.clientInfoResult.observe(viewLifecycleOwner){
            it?.let {
                if (it.accountGroupList.isNotEmpty()) {
                    val data = it.accountGroupList[0]

                    bankName = data.bankName.trim() + " - " + maskString(data.bankAccno.trim())
                    accName = data.bankAccname

                    binding.tvTopupNumAccount.text = bankName
                    binding.tvProfileTopupAccount.text = accName

                }
            }
        }

        viewModel.withdrawCashResult.observe(viewLifecycleOwner) {

            viewModel.getCashPos(userId, cifCode, sessionId)
            val remarks = it?.remarks ?: ""
            binding.etAmount.setText("0")
            when (it?.status) {
                0 -> {
                    showSnackBarTop(requireContext(), binding.root, "success", R.drawable.ic_success, "Withdraw is in progress", "", requireActivity(), "")
                    binding.etAmount.setText("0")
                }

                1 -> {
                    showSnackBarTop(requireContext(), binding.root, "error", R.drawable.ic_error, "Withdraw is failed", remarks, requireActivity(), "")
                    binding.etAmount.setText("0")
                }
            }
        }

    }

    private fun maskString(input: String): String {
        var result = ""
        if (input.length > 4) {
            val lastChars = input.takeLast(4)
            val maskingChar = "*".repeat(input.length - 4)

            result = maskingChar + lastChars
        }
        return result
    }
}