package com.bcasekuritas.mybest.app.feature.dialog.bottom

import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.databinding.DialogAllocationEipoBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class DialogAllocationEipoBottom () : BaseBottomSheet<DialogAllocationEipoBinding>() {

    @FragmentScoped
    override val binding: DialogAllocationEipoBinding by autoCleaned { (DialogAllocationEipoBinding.inflate(layoutInflater)) }

    override fun initOnClick() {
        super.initOnClick()

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnOk.setOnClickListener {
            dismiss()
        }
    }
}