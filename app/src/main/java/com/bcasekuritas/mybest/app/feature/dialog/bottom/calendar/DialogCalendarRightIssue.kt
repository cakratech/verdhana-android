package com.bcasekuritas.mybest.app.feature.dialog.bottom.calendar

import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.domain.dto.response.CalEvent
import com.bcasekuritas.mybest.databinding.DialogCalendarRightIssueBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.convertMillisToDate
import com.bcasekuritas.mybest.ext.other.formatPriceWithDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
@FragmentScoped
@AndroidEntryPoint
class DialogCalendarRightIssue(
    private val calEvent: CalEvent,
) : BaseBottomSheet<DialogCalendarRightIssueBinding>() {

    @FragmentScoped
    override val binding: DialogCalendarRightIssueBinding by autoCleaned {
        (DialogCalendarRightIssueBinding.inflate(
            layoutInflater
        ))
    }

    private var okButtonClickListener: ((Boolean) -> Unit)? = null


    override fun setupComponent() {
        super.setupComponent()

        binding.apply {
            tvTitle.text = "Right Issue ${calEvent.stockCode}"
            tvRatioVal.text = "${calEvent.oldRatio.toInt()} : ${calEvent.newRatio.toInt()}"
            tvPriceVal.text = "Rp${calEvent.price.formatPriceWithoutDecimal()}"
            tvFactorVal.text = calEvent.factor.formatPriceWithDecimal()
            tvCumulativeDateVal.text = convertMillisToDate(calEvent.cumulativeDate, "dd MMM yyyy")
            tvExDateVal.text = convertMillisToDate(calEvent.exDate, "dd MMM yyyy")
            tvReceivingDateVal.text = convertMillisToDate(calEvent.recordingDate, "dd MMM yyyy")
            tvTradingStartVal.text = convertMillisToDate(calEvent.tradingStart, "dd MMM yyyy")
            tvTradingEndVal.text = convertMillisToDate(calEvent.tradingEnd, "dd MMM yyyy")
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