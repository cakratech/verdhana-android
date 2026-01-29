package com.bcasekuritas.mybest.app.feature.dialog.bottom.order

import androidx.core.content.ContextCompat
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.databinding.DialogInfoOrderTypeBinding
import com.bcasekuritas.mybest.databinding.DialogPortfolioSummaryInfoBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned

class DialogOrderInfoOrderType: BaseBottomSheet<DialogInfoOrderTypeBinding>() {

    override val binding: DialogInfoOrderTypeBinding by autoCleaned { (DialogInfoOrderTypeBinding.inflate(layoutInflater)) }

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

            tvLimit.setOnClickListener {
                if (expandLimit.isExpanded) {
                    tvLimit.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconDown,null)
                    expandLimit.collapse()
                } else {
                    tvLimit.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconUp,null)
                    expandLimit.expand()
                }
            }

            tvMarket.setOnClickListener {
                if (expandMarket.isExpanded) {
                    tvMarket.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconDown,null)
                    expandMarket.collapse()
                } else {
                    tvMarket.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconUp,null)
                    expandMarket.expand()
                }
            }

            tvAutoOrder.setOnClickListener {
                if (expandAutoOrder.isExpanded) {
                    tvAutoOrder.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconDown,null)
                    expandAutoOrder.collapse()
                } else {
                    tvAutoOrder.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconUp,null)
                    expandAutoOrder.expand()
                }
            }
        }
    }
}