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
import com.bcasekuritas.mybest.databinding.DialogAmendGtcOrderBinding
import com.bcasekuritas.mybest.databinding.DialogFilterPortfolioOrdersBinding
import com.bcasekuritas.mybest.databinding.DialogSortTabPortfolioBinding
import com.bcasekuritas.mybest.databinding.DialogWithdrawInfoBinding
import com.bcasekuritas.mybest.databinding.DialogWithdrawOrderBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl

@FragmentScoped
@AndroidEntryPoint
class DialogAmendGtcOrder(private val fromLayout: String) : BaseBottomSheet<DialogAmendGtcOrderBinding>(){

    @FragmentScoped
    override val binding: DialogAmendGtcOrderBinding by autoCleaned { (DialogAmendGtcOrderBinding.inflate(layoutInflater)) }

    private var checkbox = false

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            checkboxConfirm.setOnCheckedChangeListener { _, isChecked ->
                checkbox = isChecked
            }

            btnAmend.setOnClickListener {
                if (checkbox) {
                    prefManager.isAmendGtc = true
                }
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
        result.putString(fromLayout, NavKeys.CONST_RES_AMEND_ORDER_GTC_CONFIRM)
        result.putString(NavKeys.CONST_RES_AMEND_ORDER_GTC_CONFIRM, "RESULT_OK")
        result.putBoolean("confirm", true)
        parentFragmentManager.setFragmentResult(fromLayout, result)

    }

}