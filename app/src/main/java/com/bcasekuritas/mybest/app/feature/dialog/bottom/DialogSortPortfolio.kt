package com.bcasekuritas.mybest.app.feature.dialog.bottom

import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.databinding.DialogSortTabPortfolioBinding
import com.bcasekuritas.mybest.databinding.DialogWithdrawInfoBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.NavKeys

@FragmentScoped
@AndroidEntryPoint
class DialogSortPortfolio(private val selectedItem: Int) : BaseBottomSheet<DialogSortTabPortfolioBinding>(){

    @FragmentScoped
    override val binding: DialogSortTabPortfolioBinding by autoCleaned { (DialogSortTabPortfolioBinding.inflate(layoutInflater)) }

    override fun setupComponent() {
        super.setupComponent()

       checklistState(selectedItem)

    }

    override fun initOnClick() {
        super.initOnClick()

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.tvAZ.setOnClickListener {
            checklistState(0)
            sendCallback(0)
            dismiss()
        }

        binding.tvZA.setOnClickListener {
            checklistState(1)
            sendCallback(1)
            dismiss()
        }

        binding.tvHighestReturn.setOnClickListener {
            checklistState(2)
            sendCallback(2)
            dismiss()
        }

        binding.tvLowestReturn.setOnClickListener {
            checklistState(3)
            sendCallback(3)
            dismiss()
        }

        binding.tvHighestValue.setOnClickListener {
            checklistState(4)
            sendCallback(4)
            dismiss()
        }

        binding.tvLowestValue.setOnClickListener {
            checklistState(5)
            sendCallback(5)
            dismiss()
        }
    }

    private fun sendCallback(selectedItem: Int){
        val result = Bundle()
        result.putString(NavKeys.KEY_FM_TAB_PORTFOLIO, NavKeys.CONST_RES_TAB_PORTFOLIO_SORT)
        result.putString(NavKeys.CONST_RES_TAB_PORTFOLIO_SORT, "RESULT_OK")
        result.putInt("sortKey", selectedItem)
        parentFragmentManager.setFragmentResult(NavKeys.KEY_FM_TAB_PORTFOLIO, result)
    }

    private fun checklistState(state: Int) {
        binding.ivAZ.visibility = if (state == 0) View.VISIBLE else View.GONE
        binding.ivZA.visibility = if (state == 1) View.VISIBLE else View.GONE
        binding.ivCheckHighestReturn.visibility = if (state == 2) View.VISIBLE else View.GONE
        binding.ivCheckLowestReturn.visibility = if (state == 3) View.VISIBLE else View.GONE
        binding.ivCheckHighestValue.visibility = if (state == 4) View.VISIBLE else View.GONE
        binding.ivCheckLowestValue.visibility = if (state == 5) View.VISIBLE else View.GONE
    }

}