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
import com.bcasekuritas.mybest.databinding.DialogFilterHistoryPortfolioBinding
import com.bcasekuritas.mybest.databinding.DialogFilterPortfolioOrdersBinding
import com.bcasekuritas.mybest.databinding.DialogSortTabPortfolioBinding
import com.bcasekuritas.mybest.databinding.DialogWithdrawInfoBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl
import java.text.SimpleDateFormat
import java.util.Date

@FragmentScoped
@AndroidEntryPoint
class DialogFilterPortfolioHistory(private val data: UIDialogPortfolioHistoryModel, private val listStock: List<String>) : BaseBottomSheet<DialogFilterHistoryPortfolioBinding>(),
    ShowDropDown by ShowDropDownImpl() {

    @FragmentScoped
    override val binding: DialogFilterHistoryPortfolioBinding by autoCleaned { (DialogFilterHistoryPortfolioBinding.inflate(layoutInflater)) }

    private var stockCode: String = data.stockCode
    private var time: String = data.time
    private var dateFrom: Long = data.dateFrom
    private var dateTo: Long = data.dateTo

    private val datePattern = "dd/MM/yyyy"

    override fun setupComponent() {
        super.setupComponent()

        binding.tvFilterStock.text = if (!data.stockCode.equals("")) data.stockCode else "All Stocks"

        timeState(data.time.lowercase())

        if (time.equals("custom")) {
            binding.tvDateFrom.text = DateUtils.convertLongToDate(dateFrom, datePattern)
            binding.tvDateTo.text = DateUtils.convertLongToDate(dateTo, datePattern)
        }
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.apply {
            tvFilterStock.setOnClickListener {
                showDropDownStringSearchable(
                    requireContext(),
                    listStock,
                    binding.viewStockDropdownLine,
                    "Search stock code"
                ) { index, value ->
                    stockCode = value
                    tvFilterStock.text = value
                }
            }

            btnClose.setOnClickListener {
                dismiss()
            }

            tvLatestWeek.setOnClickListener {
                timeState("week")
                time = "week"
            }

            tvLatestMonth.setOnClickListener {
                timeState("1month")
                time = "1month"
            }

            tvLatestThreeMonths.setOnClickListener {
                timeState("3month")
                time = "3month"
            }

            tvCustom.setOnClickListener {
                timeState("custom")
                time = "custom"
            }

            btnApply.setOnClickListener {
                sendCallback()
                dismiss()
            }

            btnReset.setOnClickListener {
                resetFilter()
                sendCallback()
                dismiss()
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
                    binding.tvDateFrom.text = DateUtils.convertLongToDate(it, datePattern)
                }
                2 -> {
                    dateTo = it
                    binding.tvDateTo.text = DateUtils.convertLongToDate(it, datePattern)
                }
            }
        }

    }

    private fun resetFilter() {
        timeState("3month")
        time = "3month"
        stockCode = ""
        binding.tvFilterStock.text = "All Stocks"
    }

    private fun sendCallback(){
        val result = Bundle()
        result.putString(NavKeys.KEY_FM_TAB_PORTFOLIO_HISTORY, NavKeys.CONST_RES_TAB_PORTFOLIO_HISTORY_FILTER)
        result.putString(NavKeys.CONST_RES_TAB_PORTFOLIO_HISTORY_FILTER, "RESULT_OK")
        result.putString("stockCode", stockCode)
        result.putString("time", time)
        result.putLong("dateFrom", dateFrom)
        result.putLong("dateTo", dateTo)
        parentFragmentManager.setFragmentResult(NavKeys.KEY_FM_TAB_PORTFOLIO_HISTORY, result)
    }

    private val checklistVisibilityMap = mapOf(
        "week" to listOf(GONE, VISIBLE, GONE, GONE, GONE, GONE),
        "1month" to listOf(GONE, GONE, VISIBLE, GONE, GONE, GONE),
        "3month" to listOf(GONE, GONE, GONE, VISIBLE, GONE, GONE),
        "custom" to listOf(GONE, GONE, GONE, GONE, VISIBLE, VISIBLE),
    )

    private fun timeState(state: String) {
        val visibilityStates = checklistVisibilityMap[state] ?: return
        binding.ivChecklistLatestWeek.visibility = visibilityStates[1]
        binding.ivChecklistLatestMonth.visibility = visibilityStates[2]
        binding.ivChecklistLatestThreeMonths.visibility = visibilityStates[3]
        binding.ivChecklistCustom.visibility = visibilityStates[4]
        binding.lyCalendarCustom.visibility = visibilityStates[5]
    }

}