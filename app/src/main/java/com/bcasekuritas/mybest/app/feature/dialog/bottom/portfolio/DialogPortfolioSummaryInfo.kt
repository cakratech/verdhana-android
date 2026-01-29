package com.bcasekuritas.mybest.app.feature.dialog.bottom.portfolio

import android.view.View
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.databinding.DialogPortfolioSummaryInfoBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned

class DialogPortfolioSummaryInfo(private val title: String, private val desc: String, private val footText: String): BaseBottomSheet<DialogPortfolioSummaryInfoBinding>() {

    override val binding: DialogPortfolioSummaryInfoBinding by autoCleaned { (DialogPortfolioSummaryInfoBinding.inflate(layoutInflater)) }

    override fun setupComponent() {
        super.setupComponent()

        binding.tvTitle.visibility = if (title.isNotEmpty()) View.VISIBLE else View.GONE
        binding.tvFootText.visibility = if (footText.isNotEmpty()) View.VISIBLE else View.GONE

        binding.tvTitle.text = title
        binding.tvDesc.text = desc
        binding.tvFootText.text = footText
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