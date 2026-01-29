package com.bcasekuritas.mybest.app.feature.dialog.bottom.calendar

import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.domain.dto.response.CalEvent
import com.bcasekuritas.mybest.databinding.DialogCalendarStockSplitBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.convertMillisToDate
import com.bcasekuritas.mybest.ext.converter.formatPriceWithoutDecimal
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
@FragmentScoped
@AndroidEntryPoint
class DialogCalendarStockSplit (
    private val calEvent: CalEvent
) : BaseBottomSheet<DialogCalendarStockSplitBinding>() {

    @FragmentScoped
    override val binding: DialogCalendarStockSplitBinding by autoCleaned {
        (DialogCalendarStockSplitBinding.inflate(
            layoutInflater
        ))
    }

    private var okButtonClickListener: ((Boolean) -> Unit)? = null

    override fun setupComponent() {
        super.setupComponent()

        binding.apply {
            tvTitle.text = "Stock Split ${calEvent.stockCode}"
            tvRatioVal.text = "${calEvent.oldRatio.toInt()} : ${calEvent.newRatio.toInt()}"
            tvDividendFactorVal.text = calEvent.splitFactor.toString().formatPriceWithoutDecimal()
            tvCumulDateVal.text = convertMillisToDate(calEvent.cumulativeDate, "dd MMM yyyy")
            tvDividendExDateVal.text = convertMillisToDate(calEvent.exDate, "dd MMM yyyy")
            tvDividendReceiveDateVal.text = convertMillisToDate(calEvent.recordingDate, "dd MMM yyyy")

        }
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.btnClose.setOnClickListener {
            dismiss()
        }


        binding.btnViewStock.setOnClickListener {
            okButtonClickListener?.invoke(true)
            dismiss()
        }
    }

    fun setOkButtonClickListener(listener: (Boolean) -> Unit) {
        okButtonClickListener = listener
    }
}
