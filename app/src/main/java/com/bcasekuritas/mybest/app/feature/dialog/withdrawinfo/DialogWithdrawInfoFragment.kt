package com.bcasekuritas.mybest.app.feature.dialog.withdrawinfo

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.data.layout.UIDialogModel
import com.bcasekuritas.mybest.databinding.DialogInfoBinding
import com.bcasekuritas.mybest.databinding.DialogWithdrawInfoBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.NavKeys

@FragmentScoped
@AndroidEntryPoint
class DialogWithdrawInfoFragment(private val strOne: String, private val strTwo: String) : BaseBottomSheet<DialogWithdrawInfoBinding>(){

    @FragmentScoped
    override val binding: DialogWithdrawInfoBinding by autoCleaned { (DialogWithdrawInfoBinding.inflate(layoutInflater)) }

    override fun setupComponent() {
        super.setupComponent()

        binding.tvWithdrawableAmount.text = strOne
        binding.tvProcessedAmount.text = strTwo
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.buttonOk.setOnClickListener {
            dismiss()
        }

        binding.buttonInfoClose.setOnClickListener {
            dismiss()
        }
    }
}