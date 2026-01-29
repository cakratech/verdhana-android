package com.bcasekuritas.mybest.app.feature.dialog.bottom.order

import androidx.core.content.ContextCompat
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.databinding.DialogInfoOrderTypeBinding
import com.bcasekuritas.mybest.databinding.DialogInfoSltpBinding
import com.bcasekuritas.mybest.databinding.DialogPortfolioSummaryInfoBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned

class DialogOrderInfoSltp: BaseBottomSheet<DialogInfoSltpBinding>() {

    override val binding: DialogInfoSltpBinding by autoCleaned { (DialogInfoSltpBinding.inflate(layoutInflater)) }

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

            tvCompare.setOnClickListener {
                if (expandCompare.isExpanded) {
                    tvCompare.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconDown,null)
                    expandCompare.collapse()
                } else {
                    tvCompare.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconUp,null)
                    expandCompare.expand()
                }
            }

            tvSellPrice.setOnClickListener {
                if (expandSellPrice.isExpanded) {
                    tvSellPrice.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconDown,null)
                    expandSellPrice.collapse()
                } else {
                    tvSellPrice.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconUp,null)
                    expandSellPrice.expand()
                }
            }
        }
    }
}