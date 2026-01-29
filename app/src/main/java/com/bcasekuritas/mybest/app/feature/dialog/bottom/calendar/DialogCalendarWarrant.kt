package com.bcasekuritas.mybest.app.feature.dialog.bottom.calendar

import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.domain.dto.response.CalEvent
import com.bcasekuritas.mybest.databinding.DialogCalendarWarrantBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.convertMillisToDate
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
@FragmentScoped
@AndroidEntryPoint
class DialogCalendarWarrant (
    private val calEvent: CalEvent
) : BaseBottomSheet<DialogCalendarWarrantBinding>() {

    @FragmentScoped
    override val binding: DialogCalendarWarrantBinding by autoCleaned {
        (DialogCalendarWarrantBinding.inflate(
            layoutInflater
        ))
    }

    private var okButtonClickListener: ((Boolean) -> Unit)? = null

    override fun setupComponent() {
        super.setupComponent()

        binding.apply {
            tvTitle.text = "Warrant ${calEvent.stockCode}"
            tvExercisePriceVal.text = "Rp${calEvent.excercisePrice.formatPriceWithoutDecimal()}"
            tvTradingStartVal.text = convertMillisToDate(calEvent.tradingStart, "dd MMM yyyy")
            tvTradingEndVal.text = convertMillisToDate(calEvent.tradingEnd, "dd MMM yyyy")
            tvExerciseStartVal.text = convertMillisToDate(calEvent.excerciseStart, "dd MMM yyyy")
            tvExerciseEndVal.text = convertMillisToDate(calEvent.excerciseEnd, "dd MMM yyyy")

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