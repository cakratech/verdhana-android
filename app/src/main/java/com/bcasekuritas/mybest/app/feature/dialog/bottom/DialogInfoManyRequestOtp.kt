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
import com.bcasekuritas.mybest.databinding.DialogInfoManyRequestOtpBinding
import com.bcasekuritas.mybest.databinding.DialogSortTabPortfolioBinding
import com.bcasekuritas.mybest.databinding.DialogWithdrawInfoBinding
import com.bcasekuritas.mybest.databinding.DialogWithdrawOrderBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl

@FragmentScoped
@AndroidEntryPoint
class DialogInfoManyRequestOtp() : BaseBottomSheet<DialogInfoManyRequestOtpBinding>(){

    @FragmentScoped
    override val binding: DialogInfoManyRequestOtpBinding by autoCleaned { (DialogInfoManyRequestOtpBinding.inflate(layoutInflater)) }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            buttonOk.setOnClickListener {
                dismiss()
            }

            buttonClose.setOnClickListener {
                dismiss()
            }

        }
    }

}