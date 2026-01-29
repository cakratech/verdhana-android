package com.bcasekuritas.mybest.app.feature.dialog.withdrawconfirm

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.data.layout.UIDialogWithdrawModel
import com.bcasekuritas.mybest.databinding.DialogWithdrawConfirmBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarInterface
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarImpl

@FragmentScoped
@AndroidEntryPoint
class DialogWithdrawConfirmFragment(private val dialogWithdrawModel: UIDialogWithdrawModel) :
    BaseBottomSheet<DialogWithdrawConfirmBinding>(){

    @FragmentScoped
    override val binding: DialogWithdrawConfirmBinding by autoCleaned {
        (DialogWithdrawConfirmBinding.inflate(
            layoutInflater
        ))
    }

    override fun setupComponent() {
        super.setupComponent()

        binding.tvTopupNumAccount.text = dialogWithdrawModel.bankName
        binding.tvProfileTopupAccount.text = dialogWithdrawModel.accName
        binding.tvProceeds.text = dialogWithdrawModel.amount
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.buttonOk.setOnClickListener {
            sendCallback()
            dismiss()
        }

        binding.buttonConfirmClose.setOnClickListener {
            dismiss()
        }
    }

    private fun sendCallback() {
        val result = Bundle()
        result.putString(NavKeys.KEY_FM_WITHDRAW, NavKeys.CONST_RES_WITHDRAW_CONFIRM)
        result.putString(NavKeys.CONST_RES_WITHDRAW_CONFIRM, "RESULT_OK")
        parentFragmentManager.setFragmentResult(NavKeys.KEY_FM_WITHDRAW, result)
    }
}