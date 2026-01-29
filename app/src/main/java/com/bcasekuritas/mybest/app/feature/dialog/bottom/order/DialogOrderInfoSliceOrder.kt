package com.bcasekuritas.mybest.app.feature.dialog.bottom.order

import android.view.View
import androidx.core.content.ContextCompat
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.databinding.DialogInfoOrderTypeBinding
import com.bcasekuritas.mybest.databinding.DialogInfoSliceOrderBinding
import com.bcasekuritas.mybest.databinding.DialogPortfolioSummaryInfoBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned

class DialogOrderInfoSliceOrder: BaseBottomSheet<DialogInfoSliceOrderBinding>() {

    override val binding: DialogInfoSliceOrderBinding by autoCleaned { (DialogInfoSliceOrderBinding.inflate(layoutInflater)) }

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

            tvSplit.setOnClickListener {
                if (expandSplit.isExpanded) {
                    tvSplit.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconDown,null)
                    expandSplit.collapse()
                } else {
                    tvSplit.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconUp,null)
                    expandSplit.expand()
                }
            }

            tvNoSplit.setOnClickListener {
                if (expandNoSplit.isExpanded) {
                    tvNoSplit.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconDown,null)
                    expandNoSplit.collapse()
                } else {
                    tvNoSplit.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconUp,null)
                    expandNoSplit.expand()
                }
            }

            tvRepeat.setOnClickListener {
                if (expandRepeat.isExpanded) {
                    tvRepeat.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconDown,null)
                    expandRepeat.collapse()
                } else {
                    tvRepeat.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconUp,null)
                    expandRepeat.expand()
                }
            }

            tvNoRepeat.setOnClickListener {
                if (expandNoRepeat.isExpanded) {
                    tvNoRepeat.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconDown,null)
                    expandNoRepeat.collapse()
                } else {
                    tvNoRepeat.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconUp,null)
                    expandNoRepeat.expand()
                }
            }

            tvAtOnce.setOnClickListener {
                if (expandAtOnce.isExpanded) {
                    tvAtOnce.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconDown,null)
                    expandAtOnce.collapse()
                    binding.scrollView.post(Runnable { binding.scrollView.fullScroll(View.FOCUS_DOWN) })
                } else {
                    tvAtOnce.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, iconUp,null)
                    expandAtOnce.expand()
                }
            }
        }
    }
}