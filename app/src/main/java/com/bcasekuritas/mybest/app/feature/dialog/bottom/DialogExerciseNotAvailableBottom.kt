package com.bcasekuritas.mybest.app.feature.dialog.bottom

import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.databinding.DialogAllocationEipoBinding
import com.bcasekuritas.mybest.databinding.DialogExerciseNotAvailableBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class DialogExerciseNotAvailableBottom (private val startHour: String) : BaseBottomSheet<DialogExerciseNotAvailableBinding>() {

    @FragmentScoped
    override val binding: DialogExerciseNotAvailableBinding by autoCleaned { (DialogExerciseNotAvailableBinding.inflate(layoutInflater)) }

    override fun initOnClick() {
        super.initOnClick()

        if (startHour.isNotEmpty()) {
            binding.tvDesc.text = "Exercise of rights issue available after $startHour WIB."
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnOk.setOnClickListener {
            dismiss()
        }
    }
}