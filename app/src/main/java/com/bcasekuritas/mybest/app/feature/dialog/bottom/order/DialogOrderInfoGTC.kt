package com.bcasekuritas.mybest.app.feature.dialog.bottom.order

import androidx.core.content.ContextCompat
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.databinding.DialogInfoGtcBinding
import com.bcasekuritas.mybest.databinding.DialogInfoOrderTypeBinding
import com.bcasekuritas.mybest.databinding.DialogPortfolioSummaryInfoBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned

class DialogOrderInfoGTC: BaseBottomSheet<DialogInfoGtcBinding>() {

    override val binding: DialogInfoGtcBinding by autoCleaned { (DialogInfoGtcBinding.inflate(layoutInflater)) }

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

            tvGtcDate.setOnClickListener {
                if (expandLimit.isExpanded) {
                    tvGtcDate.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconDown,null)
                    expandLimit.collapse()
                } else {
                    tvGtcDate.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconUp,null)
                    expandLimit.expand()
                }
            }
        }
    }
}