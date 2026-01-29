package com.bcasekuritas.mybest.app.feature.dialog.bottom

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.data.layout.UIDialogPortfolioOrderModel
import com.bcasekuritas.mybest.databinding.DialogFilterPortfolioOrdersBinding
import com.bcasekuritas.mybest.databinding.DialogRemovePriceAlertBinding
import com.bcasekuritas.mybest.databinding.DialogSortTabPortfolioBinding
import com.bcasekuritas.mybest.databinding.DialogWithdrawInfoBinding
import com.bcasekuritas.mybest.databinding.DialogWithdrawOrderBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl

@FragmentScoped
@AndroidEntryPoint
class DialogRemovePriceAlert(private val fromLayout: String) : BaseBottomSheet<DialogRemovePriceAlertBinding>(){

    @FragmentScoped
    override val binding: DialogRemovePriceAlertBinding by autoCleaned { (DialogRemovePriceAlertBinding.inflate(layoutInflater)) }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            btnDelete.setOnClickListener {
                sendCallback()
                dismiss()
            }

            btnCancel.setOnClickListener {
                dismiss()
            }

            btnClose.setOnClickListener {
                dismiss()
            }

        }
    }


    private fun sendCallback(){
        val result = Bundle()
        result.putString(fromLayout, NavKeys.CONST_RES_REMOVE_PRICE_ALERT)
        result.putString(NavKeys.CONST_RES_REMOVE_PRICE_ALERT, "RESULT_OK")
        result.putBoolean("confirm", true)
        parentFragmentManager.setFragmentResult(fromLayout, result)

    }

}