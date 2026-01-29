package com.bcasekuritas.mybest.app.feature.dialog.bottom

import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.databinding.DialogAllocationEipoBinding
import com.bcasekuritas.mybest.databinding.DialogTimeExpiredExerciseBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class DialogExerciseExpiredBottom () : BaseBottomSheet<DialogTimeExpiredExerciseBinding>() {

    @FragmentScoped
    override val binding: DialogTimeExpiredExerciseBinding by autoCleaned { (DialogTimeExpiredExerciseBinding.inflate(layoutInflater)) }

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