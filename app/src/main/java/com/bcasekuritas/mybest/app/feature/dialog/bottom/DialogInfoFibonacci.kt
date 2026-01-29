package com.bcasekuritas.mybest.app.feature.dialog.bottom

import androidx.core.content.ContextCompat
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.databinding.DialogInfoFibonacciBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned

class DialogInfoFibonacci: BaseBottomSheet<DialogInfoFibonacciBinding>() {

    override val binding: DialogInfoFibonacciBinding by autoCleaned { (DialogInfoFibonacciBinding.inflate(layoutInflater)) }

    override fun setupComponent() {
        super.setupComponent()

    }

    override fun initOnClick() {
        super.initOnClick()
        val iconUp = ContextCompat.getDrawable(requireContext(), R.drawable.ic_row_up)
        val iconDown = ContextCompat.getDrawable(requireContext(), R.drawable.ic_bot_row)

        binding.apply {

            btnOk.setOnClickListener {
                dismiss()
            }

            buttonInfoClose.setOnClickListener {
                dismiss()
            }

            tvPivot.setOnClickListener {
                if (expandPivot.isExpanded) {
                    tvPivot.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconDown,null)
                    expandPivot.collapse()
                } else {
                    tvPivot.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconUp,null)
                    expandPivot.expand()
                }
            }

            tvSupport.setOnClickListener {
                if (expandSupport.isExpanded) {
                    tvSupport.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconDown,null)
                    expandSupport.collapse()
                } else {
                    tvSupport.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconUp,null)
                    expandSupport.expand()
                }
            }

            tvResistance.setOnClickListener {
                if (expandResistance.isExpanded) {
                    tvResistance.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconDown,null)
                    expandResistance.collapse()
                } else {
                    tvResistance.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconUp,null)
                    expandResistance.expand()
                }
            }
        }
    }
}