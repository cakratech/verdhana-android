package com.bcasekuritas.mybest.app.feature.dialog.bottom.calendar

import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.domain.dto.response.CalEvent
import com.bcasekuritas.mybest.databinding.DialogCalendarIpoBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.convertMillisToDate
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class DialogCalendarIPO (
    private val calEvent: CalEvent
) : BaseBottomSheet<DialogCalendarIpoBinding>() {

    @FragmentScoped
    override val binding: DialogCalendarIpoBinding by autoCleaned { (DialogCalendarIpoBinding.inflate(layoutInflater)) }

    private var okButtonClickListener: ((Boolean) -> Unit)? = null

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
//            tvTitle.text = "E-IPO ${calEvent.stockCode}"
            tvIpoCompany.text = calEvent.companyName
            tvDateVal.text = convertMillisToDate(calEvent.listingDate, "EEE, dd MMM yyyy")
            tvIpoQty.text = calEvent.totalShareListed.formatPriceWithoutDecimal()
        }

    }

    override fun initOnClick() {
        super.initOnClick()

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnIpoViewIpo.setOnClickListener {
            okButtonClickListener?.invoke(true)
            dismiss()
        }
    }

    fun setOkButtonClickListener(listener: (Boolean) -> Unit) {
        okButtonClickListener = listener
    }
}