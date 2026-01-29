package com.bcasekuritas.mybest.app.feature.dialog.bottom.portfolio

import android.view.View
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.databinding.DialogPortfolioSummaryInfoBinding
import com.bcasekuritas.mybest.databinding.DialogRealizedGainLossInfoBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned

class DialogInfoRealizedGainLoss(): BaseBottomSheet<DialogRealizedGainLossInfoBinding>() {

    override val binding: DialogRealizedGainLossInfoBinding by autoCleaned { (DialogRealizedGainLossInfoBinding.inflate(layoutInflater)) }

    override fun setupComponent() {
        super.setupComponent()
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.btnOk.setOnClickListener {
            dismiss()
        }

        binding.buttonInfoClose.setOnClickListener {
            dismiss()
        }
    }
}