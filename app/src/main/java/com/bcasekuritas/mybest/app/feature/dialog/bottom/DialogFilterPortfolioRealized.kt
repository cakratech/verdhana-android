package com.bcasekuritas.mybest.app.feature.dialog.bottom

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.data.layout.UIDialogPortfolioHistoryModel
import com.bcasekuritas.mybest.app.data.layout.UIDialogPortfolioOrderModel
import com.bcasekuritas.mybest.app.data.layout.UIDialogPortfolioRealizedModel
import com.bcasekuritas.mybest.databinding.DialogFilterHistoryPortfolioBinding
import com.bcasekuritas.mybest.databinding.DialogFilterHistoryRealizedPortfolioBinding
import com.bcasekuritas.mybest.databinding.DialogFilterPortfolioOrdersBinding
import com.bcasekuritas.mybest.databinding.DialogSortTabPortfolioBinding
import com.bcasekuritas.mybest.databinding.DialogWithdrawInfoBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.date.DateUtils
import java.text.SimpleDateFormat
import java.util.Date

@FragmentScoped
@AndroidEntryPoint
class DialogFilterPortfolioRealized(private val data: UIDialogPortfolioRealizedModel) : BaseBottomSheet<DialogFilterHistoryRealizedPortfolioBinding>(){

    @FragmentScoped
    override val binding: DialogFilterHistoryRealizedPortfolioBinding by autoCleaned { (DialogFilterHistoryRealizedPortfolioBinding.inflate(layoutInflater)) }

    private var time: String = data.time
    private var dateFrom: Long = data.dateFrom
    private var dateTo: Long = data.dateTo

    private val datePattern = "dd/MM/yyyy"

    override fun setupComponent() {
        super.setupComponent()
        timeState(data.time)

        if (time.equals("Custom")) {
            binding.tvFromDate.text = DateUtils.convertLongToDate(dateFrom, datePattern)
            binding.tvToDate.text = DateUtils.convertLongToDate(dateTo, datePattern)
        }

    }

    override fun initOnClick() {
        super.initOnClick()

        binding.apply {
            tvAllTime.setOnClickListener {
                timeState("All Time")
                time = "All Time"
            }

            tvLatestWeek.setOnClickListener {
                timeState("Latest 1 week")
                time = "Latest 1 week"
            }

            tvLatestMonth.setOnClickListener {
                timeState("Latest 1 month")
                time = "Latest 1 month"
            }

            tvLatestThreeMonths.setOnClickListener {
                timeState("Latest 3 month(s)")
                time = "Latest 3 month(s)"
            }

            tvCustom.setOnClickListener {
                timeState("Custom")
                time = "Custom"
            }

            btnApply.setOnClickListener {
                sendCallback()
                dismiss()
            }

            btnReset.setOnClickListener {
                resetFilter()
            }

            lyFromDate.setOnClickListener {
                datePicker(1)
            }

            lyToDate.setOnClickListener {
                datePicker(2)
            }

        }



    }

    private fun datePicker(state: Int) {
        // 1: from, 2: to
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTheme(R.style.App_DatePicker)
                .build()

        datePicker.show(parentFragmentManager, "tag")

        datePicker.addOnPositiveButtonClickListener {
            when (state) {
                1 -> {
                    dateFrom = it
                    binding.tvFromDate.text = DateUtils.convertLongToDate(it, datePattern)
                }
                2 -> {
                    dateTo = it
                    binding.tvToDate.text = DateUtils.convertLongToDate(it, datePattern)
                }
            }
        }

    }

    private fun resetFilter() {
        timeState("All Time")
        time = "All Time"
    }

    private fun sendCallback(){
        val result = Bundle()
        result.putString(NavKeys.KEY_FM_REALIZED, NavKeys.CONST_RES_PORTFOLIO_REALIZED)
        result.putString(NavKeys.CONST_RES_PORTFOLIO_REALIZED, "RESULT_OK")
        result.putString("time", time)
        result.putLong("dateFrom", dateFrom)
        result.putLong("dateTo", dateTo)
        parentFragmentManager.setFragmentResult(NavKeys.KEY_FM_REALIZED, result)
    }

    private val checklistVisibilityMap = mapOf(
        "All Time" to listOf(VISIBLE, GONE, GONE, GONE, GONE, GONE),
        "Latest 1 week" to listOf(GONE, VISIBLE, GONE, GONE, GONE, GONE),
        "Latest 1 month" to listOf(GONE, GONE, VISIBLE, GONE, GONE, GONE),
        "Latest 3 month(s)" to listOf(GONE, GONE, GONE, VISIBLE, GONE, GONE),
        "Custom" to listOf(GONE, GONE, GONE, GONE, VISIBLE, VISIBLE)
    )

    private fun timeState(state: String) {
        val visibilityStates = checklistVisibilityMap[state] ?: return
        binding.ivChecklistAllTime.visibility = visibilityStates[0]
        binding.ivChecklistLatestWeek.visibility = visibilityStates[1]
        binding.ivChecklistLatestMonth.visibility = visibilityStates[2]
        binding.ivChecklistLatestThreeMonths.visibility = visibilityStates[3]
        binding.ivChecklistCustom.visibility = visibilityStates[4]
        binding.lyCalendarCustom.visibility = visibilityStates[5]
    }

}