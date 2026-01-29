package com.bcasekuritas.mybest.app.feature.dialog.bottom.calendar

import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.domain.dto.response.CalEvent
import com.bcasekuritas.mybest.databinding.DialogCalendarBonusBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.convertMillisToDate
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
@FragmentScoped
@AndroidEntryPoint
class DialogCalendarBonus (
    private val calEvent: CalEvent
) : BaseBottomSheet<DialogCalendarBonusBinding>() {

    @FragmentScoped
    override val binding: DialogCalendarBonusBinding by autoCleaned {
        (DialogCalendarBonusBinding.inflate(
            layoutInflater
        ))
    }

    private var okButtonClickListener: ((Boolean) -> Unit)? = null

    override fun setupComponent() {
        super.setupComponent()

        binding.apply {
            tvTitle.text = "Bonus ${calEvent.stockCode}"
            tvRatioVal.text = "${calEvent.oldRatio.toInt()} : ${calEvent.newRatio.toInt()}"
            tvFactorVal.text = calEvent.factor.toString()
            tvCumulativeDateVal.text = convertMillisToDate(calEvent.cumulativeDate, "dd MMM yyyy")
            tvExDateVal.text = convertMillisToDate(calEvent.exDate, "dd MMM yyyy")
            tvRecordingDateVal.text = convertMillisToDate(calEvent.recordingDate, "dd MMM yyyy")
            tvPaydateVal.text = convertMillisToDate(calEvent.payDate, "dd MMM yyyy")

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
