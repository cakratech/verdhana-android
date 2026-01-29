package com.bcasekuritas.mybest.app.feature.dialog.bottom.calendar

import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.domain.dto.response.CalEvent
import com.bcasekuritas.mybest.databinding.DialogCalendarRupsPublicBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.convertMillisToDate
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class DialogCalendarPublic(
    private val calEvent: CalEvent,
) : BaseBottomSheet<DialogCalendarRupsPublicBinding>() {

    @FragmentScoped
    override val binding: DialogCalendarRupsPublicBinding by autoCleaned {
        (DialogCalendarRupsPublicBinding.inflate(
            layoutInflater
        ))
    }

    private var okButtonClickListener: ((Boolean) -> Unit)? = null


    override fun setupComponent() {
        super.setupComponent()

        binding.apply {
            tvTitle.text = "Public Expose ${calEvent.stockCode}"
            tvDate.text = convertMillisToDate(calEvent.date, "EEE, dd MMM yyyy")
            tvTime.text = calEvent.time
            tvAddress.text = calEvent.location
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