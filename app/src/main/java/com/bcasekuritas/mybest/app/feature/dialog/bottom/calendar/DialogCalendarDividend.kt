package com.bcasekuritas.mybest.app.feature.dialog.bottom.calendar

import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.domain.dto.response.CalEvent
import com.bcasekuritas.mybest.databinding.DialogCalendarDividendBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.convertMillisToDate
import com.bcasekuritas.mybest.ext.other.formatPrice
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class DialogCalendarDividend (
    private val calEvent: CalEvent
) : BaseBottomSheet<DialogCalendarDividendBinding>() {

    @FragmentScoped
    override val binding: DialogCalendarDividendBinding by autoCleaned { (DialogCalendarDividendBinding.inflate(layoutInflater)) }

    private var okButtonClickListener: ((String) -> Unit)? = null

    override fun setupComponent() {
        super.setupComponent()

        binding.apply {
            tvTitle.text = "Dividend ${calEvent.stockCode}"
            tvDividendCashVal.text = "Rp${calEvent.cashDividend.formatPrice()}"
            tvDividendCumulDateVal.text = convertMillisToDate(calEvent.cumulativeDate, "dd MMM yyyy")
            tvDividendExDateVal.text = convertMillisToDate(calEvent.exDate, "dd MMM yyyy")
            tvDividendReceiveDateVal.text = convertMillisToDate(calEvent.recordingDate, "dd MMM yyyy")
            tvDividendPayDateVal.text = convertMillisToDate(calEvent.paymentDate, "dd MMM yyyy")

        }
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnDividendViewHistory.setOnClickListener {
            okButtonClickListener?.invoke("history")
            dismiss()
        }

        binding.btnDividendViewStock.setOnClickListener {
            okButtonClickListener?.invoke("stock")
            dismiss()
        }
    }

    fun setOkButtonClickListener(listener: (String) -> Unit) {
        okButtonClickListener = listener
    }
}