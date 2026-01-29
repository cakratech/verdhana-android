package com.bcasekuritas.mybest.app.feature.dialog.bottom

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.data.layout.UIDialogPortfolioOrderModel
import com.bcasekuritas.mybest.databinding.DialogFilterPortfolioOrdersBinding
import com.bcasekuritas.mybest.databinding.DialogSortTabPortfolioBinding
import com.bcasekuritas.mybest.databinding.DialogWithdrawInfoBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl

@FragmentScoped
@AndroidEntryPoint
class DialogFilterPortfolioOrder(private val data: UIDialogPortfolioOrderModel, private val listStock: List<String>, private val currentTab: Int) : BaseBottomSheet<DialogFilterPortfolioOrdersBinding>(),
    ShowDropDown by ShowDropDownImpl() {

    @FragmentScoped
    override val binding: DialogFilterPortfolioOrdersBinding by autoCleaned { (DialogFilterPortfolioOrdersBinding.inflate(layoutInflater)) }

    private var type: String = data.type
    private var status: String = data.status
    private var stockCode: String = data.stockCode

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog?.setOnShowListener {
            Handler().post {
                val bottomSheet = (dialog as? BottomSheetDialog)?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout
                bottomSheet?.let {
                    BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }

        return dialog!!
    }

    override fun setupComponent() {
        super.setupComponent()

        binding.tvFilterStock.text = if (!data.stockCode.equals("")) data.stockCode else "All Stocks"
        binding.groupStatus.visibility = if (currentTab == 1) VISIBLE else GONE

        when (data.type) {
            "all" -> {
                binding.chipTypeAll.isChecked = true
            }
            "B" -> {
                binding.chipTypeBuy.isChecked = true
            }
            "S" -> {
                binding.chipTypeSell.isChecked = true
            }
        }

        when (data.status) {
            "all" -> {
                binding.chipStatusAll.isChecked = true
            }
            "O" -> {
                binding.chipStatusOpen.isChecked = true
            }
            "A" -> {
                binding.chipStatusAmend.isChecked = true
            }
            "C" -> {
                binding.chipStatusWithdrawn.isChecked = true
            }
        }

    }

    override fun initOnClick() {
        super.initOnClick()

        binding.apply {
            tvFilterStock.setOnClickListener {
                showDropDownStringSearchable(
                    requireContext(),
                    listStock,
                    binding.viewStockDropdownLine,
                    "Search stock code"
                ) { index, value ->
                    stockCode = value
                    tvFilterStock.text = value
                }
            }

            btnCancel.setOnClickListener {
                dismiss()
            }

            chipGroupType.setOnCheckedStateChangeListener { _, checkedIds ->
                for (chipId in checkedIds) {
                    val selectedChip = chipGroupType.findViewById(chipId) as Chip

                    type = when(selectedChip.text.toString().lowercase()) {
                        "all" -> ""
                        "buy" -> "B"
                        "sell" -> "S"
                        else -> ""
                    }
                }
            }

            chipGroupStatus.setOnCheckedStateChangeListener { _, checkedIds ->
                for (chipId in checkedIds) {
                    val selectedChip = chipGroupStatus.findViewById(chipId) as Chip

                    status = when (selectedChip.text.toString().lowercase()) {
                        "all" -> "all"
                        "open" -> "O"
                        "amend" -> "A"
                        "withdrawn" -> "C"
                        else -> ""
                    }
                }
            }

            btnApply.setOnClickListener {
                sendCallback()
                dismiss()
            }

            btnReset.setOnClickListener {
                resetFilter()
                sendCallback()
                dismiss()
            }
        }



    }

    private fun resetFilter() {
        binding.chipTypeAll.isChecked = true
        binding.chipStatusAll.isChecked = true
        stockCode = ""
        binding.tvFilterStock.text = "All Stocks"
    }

    private fun sendCallback(){
        val result = Bundle()
        result.putString(NavKeys.KEY_FM_TAB_PORTFOLIO_ORDER, NavKeys.CONST_RES_TAB_PORTFOLIO_ORDER_FILTER)
        result.putString(NavKeys.CONST_RES_TAB_PORTFOLIO_ORDER_FILTER, "RESULT_OK")
        result.putString("type", type)
        result.putString("status", status)
        result.putString("stockCode", stockCode)
        parentFragmentManager.setFragmentResult(NavKeys.KEY_FM_TAB_PORTFOLIO_ORDER, result)
    }

}