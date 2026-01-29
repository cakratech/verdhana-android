package com.bcasekuritas.mybest.app.feature.rdn.detailhistory

import android.os.Build
import android.os.Build.VERSION
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.RdnHistoryItem
import com.bcasekuritas.mybest.databinding.FragmentDetailRdnHistoryBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.converter.GET_COLOR_STATUS_CASH_WITHDRAW
import com.bcasekuritas.mybest.ext.converter.GET_STATUS_CASH_WITHDRAW
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import java.text.SimpleDateFormat

@AndroidEntryPoint
class DetailRdnHistoryFragment : BaseFragment<FragmentDetailRdnHistoryBinding, DetailRdnHistoryViewModel>() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmDetailRdnHistory
    override val viewModel: DetailRdnHistoryViewModel by viewModels()
    override val binding: FragmentDetailRdnHistoryBinding by autoCleaned {(FragmentDetailRdnHistoryBinding.inflate(layoutInflater))}

    companion object {
        fun newInstance() = DetailRdnHistoryFragment()
    }

    var data: RdnHistoryItem? = RdnHistoryItem()

    override fun setupArguments() {
        super.setupArguments()
        arguments?.let {
            data = if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(Args.EXTRA_PARAM_OBJECT, RdnHistoryItem::class.java)
            } else {
                it.getParcelable(Args.EXTRA_PARAM_OBJECT)
            }
        }
    }

    override fun setupComponent() {
        binding.lyToolbarDetailRdnHistory.ivLayoutToolbarMasterIconLeft.visibility = View.VISIBLE
        binding.lyToolbarDetailRdnHistory.ivLayoutToolbarMasterIconLeft.setImageResource(R.drawable.ic_back)

        binding.lyToolbarDetailRdnHistory.ivLayoutToolbarMasterIconLeft.setOnClickListener {
            requireActivity().finish()
        }

        binding.tvDetailHistoryStatus.text = data?.status.GET_STATUS_CASH_WITHDRAW()
        val textColor = data?.status.GET_COLOR_STATUS_CASH_WITHDRAW()
        binding.tvDetailHistoryStatus.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        binding.tvDetailHistoryDate.text = data?.createdDate.let { SimpleDateFormat("dd MMM yyyy, HH:mm").format(it) }
        binding.tvDetailHistoryAmount.text = "Rp${data?.transAmount?.formatPriceWithoutDecimal()}"

        when (data?.transType) {
            "C" -> {
                binding.lyToolbarDetailRdnHistory.tvLayoutToolbarMasterTitle.text = "Deposit"
                binding.tvTitleDetailHistoryAmount.text = "Deposit Amount"
                binding.lyBank.visibility = View.GONE
                binding.lyAccountNumber.visibility = View.GONE
            }
            "W", "D" -> {
                binding.lyToolbarDetailRdnHistory.tvLayoutToolbarMasterTitle.text = "Withdrawal"
                binding.tvTitleDetailHistoryAmount.text = "Withdrawal Amount"
                binding.lyBank.visibility = View.VISIBLE
                val accountNumber = data?.bankAccountNo?.let { maskString(it.trim()) }
                val bankCode = data?.bankCode
                binding.tvDetailHistoryAccountNumber.text = if (accountNumber?.isNotEmpty() == true) accountNumber else "-"
                binding.tvDetailHistoryBank.text = if (bankCode?.isNotEmpty() == true) bankCode else "-"

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