package com.bcasekuritas.mybest.app.feature.dialog.bottom.calendar

import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.domain.dto.response.CalEvent
import com.bcasekuritas.mybest.databinding.DialogCalendarReverseSplitBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.convertMillisToDate
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimalOptional
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class DialogCalendarReverseSplit (
    private val calEvent: CalEvent
) : BaseBottomSheet<DialogCalendarReverseSplitBinding>() {

    @FragmentScoped
    override val binding: DialogCalendarReverseSplitBinding by autoCleaned {
        (DialogCalendarReverseSplitBinding.inflate(
            layoutInflater
        ))
    }

    private var okButtonClickListener: ((Boolean) -> Unit)? = null

    override fun setupComponent() {
        super.setupComponent()

        binding.apply {
            tvTitle.text = "Reverse Split ${calEvent.stockCode}"
            tvRatioVal.text = "${calEvent.oldRatio.toInt()} : ${calEvent.newRatio.toInt()}"
            tvFactorVal.text = calEvent.factor.formatPriceWithoutDecimalOptional()
            tvCumulativeDateVal.text = convertMillisToDate(calEvent.cumulativeDate, "dd MMM yyyy")
            tvExDateVal.text = convertMillisToDate(calEvent.exDate, "dd MMM yyyy")
            tvPayDateVal.text = convertMillisToDate(calEvent.paymentDate, "dd MMM yyyy")

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
