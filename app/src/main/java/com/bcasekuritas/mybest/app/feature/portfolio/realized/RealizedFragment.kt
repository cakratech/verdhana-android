package com.bcasekuritas.mybest.app.feature.portfolio.realized

import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.data.layout.UIDialogPortfolioRealizedModel
import com.bcasekuritas.mybest.app.domain.dto.response.RealizedGainLossRes
import com.bcasekuritas.mybest.app.feature.activity.main.MainActivity
import com.bcasekuritas.mybest.app.feature.portfolio.realized.adapter.RealizedListMonthAdapter
import com.bcasekuritas.mybest.databinding.FragmentHistoryRealizedBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.getCurrentYearMonthDay
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl
import com.bcasekuritas.mybest.ext.listener.OnClickInts
import com.bcasekuritas.mybest.ext.other.formatPercent
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimalWithoutMinus

@FragmentScoped
@AndroidEntryPoint
class RealizedFragment: BaseFragment<FragmentHistoryRealizedBinding, RealizedViewModel>(),
    ShowDropDown by ShowDropDownImpl(), OnClickInts, ShowDialog by ShowDialogImpl() {

    override val viewModel: RealizedViewModel by viewModels()
    override val binding: FragmentHistoryRealizedBinding by autoCleaned { (FragmentHistoryRealizedBinding.inflate(layoutInflater)) }
    override val bindingVariable: Int = BR.vmRealized

    private val listMonthAdapter: RealizedListMonthAdapter by autoCleaned { RealizedListMonthAdapter(requireContext(),this) }

    private var userId = ""
    private var accNo = ""
    private var sessionId = ""

    private var stockCodeList = arrayListOf<String>()
    private var stockCodeFilter = "*"
    private var currentYear = 0
    private var selectedYear = 0

    private var isNotHaveRealize = false

    override fun setupArguments() {
        super.setupArguments()
        arguments?.let {
            isNotHaveRealize = it.getBoolean(Args.EXTRA_PARAM_BOOLEAN)
        }
    }

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvRealized.apply {
            adapter = listMonthAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            currentYear = getCurrentYearMonthDay("year").toInt()
            selectedYear = currentYear
            tvFilterYear.text = "$currentYear"

            lyToolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener {
                onBackPressed()
            }

        }

    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {

            tvRealizedInfo.setOnClickListener {
                showDialogRealizeGainLossInfoBottom(parentFragmentManager)
            }

            tvDiscoverStocks.setOnClickListener {
                MainActivity.startIntentParam(
                    requireActivity(),
                    NavKeys.KEY_MAIN_DISCOVER,
                    1,
                    ""
                )
            }

            tvFilterStock.setOnClickListener {
                val sortedStock = listOf("All Stocks") + stockCodeList.sorted()

                showDropDownStringSearchable(
                    requireContext(),
                    sortedStock,
                    binding.viewStockDropdownLine,
                    "Search stock code"
                ) { _, value ->
                    stockCodeFilter = if (value.equals("All Stocks")) "*" else value
                    tvFilterStock.text = value

                    listMonthAdapter.clearData()
                    viewModel.getRealizedByYear(userId, accNo, sessionId, selectedYear, stockCodeFilter)
                }
            }

            tvFilterYear.setOnClickListener {
                showDialogNumberPicker(
                    parentFragmentManager,
                    "Select Year",
                    2025,
                    currentYear,
                    selectedYear
                ){ value ->
                    selectedYear = value

                    val pickedYear = if(value != 0) value else getCurrentYearMonthDay("year").toInt()
                    binding.tvFilterYear.text = "$pickedYear"
                    listMonthAdapter.clearData()
                    viewModel.getRealizedByYear(userId, accNo, sessionId, selectedYear, stockCodeFilter)
                }
            }
        }

    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        accNo = prefManager.accno
        sessionId = prefManager.sessionId

        viewModel.getAllStockParam()
        if (!isNotHaveRealize) {
            viewModel.getRealizedByYear(userId, accNo, sessionId, selectedYear, stockCodeFilter)
        } else {
            binding.apply {
                lyContent.visibility = View.GONE
                rcvRealized.visibility = View.GONE
                lyHistoryEmpty.visibility = View.VISIBLE
            }
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.getAllStockParamResult.observe(viewLifecycleOwner) {
            it?.map { data -> data?.let { stockCodeList.add(data.stockCode) } }

        }

        viewModel.getRealizedGainLossByYear.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                val totalCost = data.profitLoss

                binding.tvRealizedGainLoss.text = when {
                    totalCost > 0 -> "+" + data.profitLoss.formatPriceWithoutDecimal()
                    totalCost < 0 -> data.profitLoss.formatPriceWithoutDecimal()
                    else -> "0"
                }
                binding.tvRealizedGainLoss.setTextColor(
                    when {
                        totalCost > 0 -> ContextCompat.getColor(requireContext(), R.color.textUp)
                        totalCost < 0 -> ContextCompat.getColor(requireContext(), R.color.textDown)
                        else -> ContextCompat.getColor(requireContext(), R.color.textSecondaryGrey)
                    }
                )

                if (!data.realizeGainLossList.isNullOrEmpty()) {
                    val listData = data.realizeGainLossList.sortedBy { it.month }.map { item ->
                        RealizedGainLossRes(
                            stockCode = item.stockCode,
                            date = item.date,
                            year = item.year,
                            month = item.month,
                            profitLoss = item.profitLoss,
                            totalCost = item.totalCost,
                            profitLossPct = item.profitLossPct
                        )
                    }
                    listMonthAdapter.setData(listData)
                }

                binding.rcvRealized.visibility = if (data.realizeGainLossList.isNullOrEmpty()) View.GONE else View.VISIBLE
                binding.lyHistoryEmpty.visibility = if (data.realizeGainLossList.isNullOrEmpty()) View.VISIBLE else View.GONE
            }
        }

        viewModel.getRealizedGainLossListStock.observe(viewLifecycleOwner) { listItems ->
            if (listItems.isNotEmpty()) {
                val positionItem = listMonthAdapter.getPositionExpanded()
                listMonthAdapter.updateListStock(positionItem, listItems.filterNotNull())
            }
        }
    }

    override fun onClickInts(valueInt: Int?, valueInts: Int?) {
        val year = valueInt?:0
        val month = valueInts?:0

        if (month != 0 && year != 0) {
            viewModel.getRealizedStock(userId, accNo, sessionId, year, month, stockCodeFilter)
        }
    }
}