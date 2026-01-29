package com.bcasekuritas.mybest.app.feature.discover

import android.annotation.SuppressLint
import android.icu.text.DecimalFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.DataChart
import com.bcasekuritas.mybest.app.domain.dto.response.IndexSectorDetailData
import com.bcasekuritas.mybest.app.feature.activity.main.MainActivity
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.discover.adapter.CategoriesDiscoverAdapter
import com.bcasekuritas.mybest.app.feature.discover.adapter.EIPODiscoverAdapter
import com.bcasekuritas.mybest.app.feature.discover.adapter.IndexDiscoverAdapter
import com.bcasekuritas.mybest.app.feature.discover.adapter.SectorsDiscoverAdapter
import com.bcasekuritas.mybest.databinding.FragmentDiscoverBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.formatLastNumberStartFromBillion
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import com.bcasekuritas.mybest.ext.listener.OnClickStrIntBoolean
import com.bcasekuritas.mybest.ext.listener.OnClickStrs
import com.bcasekuritas.mybest.ext.other.formatPercent
import com.bcasekuritas.mybest.ext.other.formatPriceWithTwoDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.other.roundedHalfUp
import com.bcasekuritas.mybest.ext.view.setSafeOnClickListener
import com.bcasekuritas.mybest.widget.view.CustomMarkerView
import com.bcasekuritas.rabbitmq.proto.news.ViewIndexSector
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class DiscoverFragment : BaseFragment<FragmentDiscoverBinding, DiscoverViewModel>(), OnClickAny, OnClickStr, OnClickStrIntBoolean, OnClickStrs, ShowDropDown by ShowDropDownImpl() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmDiscover
    override val viewModel: DiscoverViewModel by viewModels()
    override val binding: FragmentDiscoverBinding by autoCleaned { (FragmentDiscoverBinding.inflate(layoutInflater)) }

    // adapter
    private val eipoAdapter: EIPODiscoverAdapter by autoCleaned { EIPODiscoverAdapter(prefManager.urlIcon, this) }
    private val categoriesAdapter: CategoriesDiscoverAdapter by autoCleaned { CategoriesDiscoverAdapter(requireContext(), this, prefManager.urlIcon) }
    private val indexAdapter: IndexDiscoverAdapter by autoCleaned { IndexDiscoverAdapter(requireContext(), this) }
    private val sectorsAdapter: SectorsDiscoverAdapter by autoCleaned { SectorsDiscoverAdapter( requireContext(), this) }
    private var indexList = mutableMapOf<String, ViewIndexSector>()


    private var userId = ""
    private var sessionId = ""

    private var endDate = 0L
    private var indiceCode = "COMPOSITE"

    private var idViewAllStockIndex = 0

    private var sortAscCategories = 1
    private var sortTypeCategories = 1

    // for datamarker chart
    private var isHoursFormat = true

    companion object {
        fun newInstance() = DiscoverFragment()
    }


    override fun setupAdapter() {
        super.setupAdapter()

        binding.apply {
            // eipo
            rcvEipo.adapter = eipoAdapter
            rcvEipo.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            // categories
            rcvCategories.adapter = categoriesAdapter
            rcvCategories.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            // index
            rcvIndex.adapter = indexAdapter
            rcvIndex.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            // sectors
            rcvSectors.adapter = sectorsAdapter
            rcvSectors.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        }
    }

    override fun setupComponent() {
        super.setupComponent()
//        lineChart()

    }

    override fun initOnClick() {
        super.initOnClick()

        binding.apply {

            btnViewAllIndex.setOnClickListener {
//                MiddleActivity.startIntent(requireActivity(), NavKeys.KEY_FM_INDEX)
                findNavController().navigate(R.id.index_fragment)
            }

            btnViewAllSector.setOnClickListener {
//                MiddleActivity.startIntent(requireActivity(), NavKeys.KEY_FM_SECTOR)
                findNavController().navigate(R.id.sector_fragment)
            }

            btnViewAllCategories.setOnClickListener {
//                MiddleActivity.startIntent(requireActivity(), NavKeys.KEY_FM_CATEGORIES)
                findNavController().navigate(R.id.categories_fragment)
            }

            menuBrokerSummary.setOnClickListener {
//                MiddleActivity.startIntent(requireActivity(), NavKeys.KEY_FM_BROKER_SUMMARY)
                findNavController().navigate(R.id.broker_summary_fragment)
            }

            menuIndex.setOnClickListener {
                findNavController().navigate(R.id.index_fragment)
            }

            menuSector.setOnClickListener {
                findNavController().navigate(R.id.sector_fragment)
            }

            menuGlobalMarket.setOnClickListener {
                findNavController().navigate(R.id.global_market_fragment)
            }

            menuRightIssues.setOnClickListener {
                MiddleActivity.startIntent(requireActivity(), NavKeys.KEY_FM_RIGHT_ISSUE)
            }

            menuFastOrder.setOnClickListener {
                MiddleActivity.startIntentParam(
                    requireActivity(),
                    NavKeys.KEY_FM_FAST_ORDER,
                    "BBCA",
                    "Bank Central Asia Tbk"
                )
            }

            etSearchDiscover.setOnClickListener {
                MiddleActivity.startIntentParam(requireActivity(), NavKeys.KEY_FM_SEARCH_STOCK, "Search code or name", "")
            }

            btnDropdown.setOnClickListener {
                showDropDownStringSearchable(
                    requireContext(),
                    indexList.keys.toList(),
                    binding.viewDropdownStock,
                    "Search index code"
                ) { index, value ->
                    val previousValue = if (tvStockCode.text.toString().equals("IHSG")) "COMPOSITE" else tvStockCode.text.toString()
                    tvStockCode.text = value

                    dividerCard.isGone = tvStockCode.text.toString() != "IHSG"
                    lyOpenHighLow.isGone = tvStockCode.text.toString() != "IHSG"

                    tvStockName.text = indexList[value]?.indexName.toString()
                    val routingKey = if (value.equals("IHSG")) "COMPOSITE" else value
                    viewModel.setIndiceSummary(routingKey)

                    // for chart
                    indiceCode =  if (value.equals("IHSG")) "COMPOSITE" else indexList[value]?.indexCode.toString()
                    // for button view all stocks
                    idViewAllStockIndex = indexList[value]?.id!!.toInt()

                    viewModel.unSubscribeIndiceSummary(previousValue)
                    viewModel.getIndiceData(userId, sessionId, routingKey)
                    viewModel.getChartIntraday(userId, sessionId, indiceCode, getStartDate("1D"), endDate, 0)
                    filterChartOnClick(1)
                }
            }

            btnViewAllStocks.setOnClickListener {
                val bundle = Bundle().apply {
                    putString(Args.EXTRA_PARAM_STR_ONE, tvStockCode.text.toString())
                    putInt(Args.EXTRA_PARAM_INT_ONE, idViewAllStockIndex)
                }
                findNavController().navigate(R.id.index_detail_fragment, bundle)
            }

            btnExpandTradingView.setSafeOnClickListener {
//                LandscapeActivity.startIntentParam(requireActivity(), NavKeys.KEY_FM_TRADING_VIEW, tvStockCode.text, "")
            }

            btnViewAllIpo.setOnClickListener {
                findNavController().navigate(R.id.eipo_fragment)
            }

            menuOnCLick()
            chipCategoriesClick()
            filterChartOnClick()
        }
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        sessionId = prefManager.sessionId

        endDate = System.currentTimeMillis()
        viewModel.getChartIntraday(userId, sessionId, indiceCode, getStartDate("1D"), endDate, 0)
    }

    override fun onResume() {
        super.onResume()
        viewModel.setListenerIndiceAndTradeSum()
        viewModel.setIndiceSummary(indiceCode)
        viewModel.getIndiceData(userId, sessionId, indiceCode)
        viewModel.getIndexSectorData(userId, sessionId)
        viewModel.getCategoriesData(userId,sessionId, sortAscCategories, sortTypeCategories)
        viewModel.getIpoList(userId, sessionId)
    }

    override fun onPause() {
        super.onPause()
        viewModel.unSubscribeTradeSummary()
        val txtStockCode = binding.tvStockCode.text.toString().trim()
        val previousValue = if (txtStockCode == "IHSG") "COMPOSITE" else txtStockCode
        viewModel.unSubscribeAllIndiceSummary(previousValue)
    }

    @SuppressLint("SetTextI18n")
    override fun setupObserver() {
        super.setupObserver()

        viewModel.getIndiceDataResult.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.apply {
                    tvOpenVal.text = it.open.formatPriceWithoutDecimal()
                    tvHighVal.text = it.high.formatPriceWithoutDecimal()
                    tvLowVal.text = it.low.formatPriceWithoutDecimal()
                    tvIndiceVal.text = it.indiceVal.formatPriceWithTwoDecimal()
                    tvLotVal.text = formatLastNumberStartFromBillion(it.marketVol.toFloat())
                    tvValueVal.text = formatLastNumberStartFromBillion(it.marketVal.toFloat())
                    tvFreqVal.text = it.marketFreq?.formatPriceWithoutDecimal()

                    val change = it.change
                    val chgPercent = it.chgPercent

                    if (change > 0 ) {
                        tvChangeVal.text = "+${change.formatPriceWithTwoDecimal()} (+${chgPercent.formatPercent()}%)"
                        binding.tvChangeVal.setTextColor(ContextCompat.getColor(requireContext(), R.color.textUpHeader))
                    } else if (change < 0) {
                        tvChangeVal.text = "${change.formatPriceWithTwoDecimal()} (${chgPercent.formatPercent()}%)"
                        binding.tvChangeVal.setTextColor(ContextCompat.getColor(requireContext(), R.color.textDownHeader))
                    } else {
                        tvChangeVal.text = "${change.formatPriceWithTwoDecimal()} (${chgPercent.formatPercent()}%)"
                        binding.tvChangeVal.setTextColor(ContextCompat.getColor(requireContext(), R.color.textWhite))
                    }

                }
            }
        }

        viewModel.getSummaryIsNull.observe(viewLifecycleOwner) {
            if (it) {
                binding.apply {
                    tvOpenVal.text = "0"
                    tvHighVal.text = "0"
                    tvLowVal.text = "0"
                    tvIndiceVal.text = "0.00"
                    tvLotVal.text = "0"
                    tvValueVal.text = "0"
                    tvFreqVal.text = "0"
                    tvChangeVal.text = "0.00 (0.00%)"
                    binding.tvChangeVal.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
            }
        }

        viewModel.getListIndexForSummary.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isNotEmpty()) {
                    it.map {item ->
                        indexList[item.indexCode] =  item
                        if (item.indexCode == "IHSG") {
                            idViewAllStockIndex = item.id.toInt()
                        }
                    }
                }

            }
        }

        viewModel.getIndexDetailDataResult.observe(viewLifecycleOwner) {
            if (it != null) {
                indexAdapter.setData(it.filterNotNull())
            }
        }

        viewModel.getSectorDetailDataResult.observe(viewLifecycleOwner) {
            if (it != null) {
                sectorsAdapter.setData(it.filterNotNull())
            }
        }

        viewModel.getCategoriesDataResult.observe(viewLifecycleOwner) {
            if (it != null) {
                categoriesAdapter.setData(it)
            }
        }

        viewModel.showSessionExpired.observe(viewLifecycleOwner) {
            if (it) {
                (activity as MainActivity).showDialogSessionExpired()
            }
        }

        viewModel.getChartIntradayResult.observe(viewLifecycleOwner) {
            if (it != null) {
                val listData = it.map {item ->
                    DataChart(item.price, item.axisDate, isHoursFormat)
                }
                binding.chartDiscover.setNoDataTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                setupChart(listData)
            } else {
                binding.chartDiscover.clear()
            }
        }

        viewModel.getIpoListResult.observe(viewLifecycleOwner) {listData ->
            if (!listData.isNullOrEmpty()) {
                val data = if (listData.size > 10) listData.take(10) else listData
                eipoAdapter.setData(data)
                binding.rcvEipo.visibility = View.VISIBLE
                binding.groupNoEipo.visibility = View.GONE
            } else {
                binding.rcvEipo.visibility = View.GONE
                binding.groupNoEipo.visibility = View.VISIBLE
            }
        }
    }

    // on click item index
    override fun onClickStrIntBoolean(valueStr: String?, valueInt: Int?, valueBoolean: Boolean?) {
        if (!valueStr.equals("") && valueInt != 0) {
            val bundle = Bundle().apply {
                putString(Args.EXTRA_PARAM_STR_ONE, valueStr.toString())
                if (valueInt != null) {
                    putInt(Args.EXTRA_PARAM_INT_ONE, valueInt)
                }
            }
            findNavController().navigate(R.id.index_detail_fragment, bundle)
        }
    }

    // on click item sector
    override fun onClickAny(valueAny: Any?) {
        val data = valueAny as IndexSectorDetailData
        if (data.indiceCode != "" && data.id.toInt() != 0 && data.stockCount != 0) {
            val bundle = Bundle().apply {
                putString(Args.EXTRA_PARAM_STR_ONE, data.indiceCode )
                putInt(Args.EXTRA_PARAM_INT_ONE, data.id.toInt())
                putInt(Args.EXTRA_PARAM_INT_TWO, data.stockCount)
            }
            findNavController().navigate(R.id.sector_detail_fragment, bundle)
        }
    }

    // Categories item on click
    override fun onClickStr(value: String?) {
        if (value != null) {
            MiddleActivity.startIntentParam(requireActivity(), NavKeys.KEY_FM_STOCK_DETAIL, value, "")
        }
    }

    // eipo item on click
    override fun onClickStrs(valueStr1: String?, valueStr2: String?) {
        if (valueStr1 != null) {
            MiddleActivity.startIntentParam(requireActivity(), NavKeys.KEY_FM_EIPO_DETAIL, valueStr1, "")
        }
    }

    private fun filterChartOnClick(state: Int) {
        binding.apply {
            // 1 = day, 2 = week, 3 = month, 4 = three month, 5 = year,6 = five years, 7 = all
            isHoursFormat = state == 1 || state == 2
            for (i in 1..7) {
                val textView = when (i) {
                    1 -> tvFilterChartDay
                    2 -> tvFilterChartWeek
                    3 -> tvFilterChartMonth
                    4 -> tvFilterChartThreeMonth
                    5 -> tvFilterChartYear
                    6 -> tvFilterChartFiveYears
                    7 -> tvFilterChartAll
                    else -> throw IllegalArgumentException("Invalid parameter: $i")
                }
                textView.setBackgroundResource(if (i == state) R.drawable.rounded_02b9cb_56 else 0)
                textView.setTextColor(if (i == state) ContextCompat.getColor(requireContext(), R.color.textChart) else ContextCompat.getColor(requireContext(), R.color.textSecondary))
            }
        }
    }

    private fun chipCategoriesClick() {
        binding.apply {
            chipCategoriesTopGainers.setOnClickListener {
                sortAscCategories = 1
                sortTypeCategories = 1
                viewModel.getCategoriesData(userId,sessionId, sortAscCategories, sortTypeCategories)
            }

            chipCategoriesTopLosers.setOnClickListener {
                sortAscCategories = 0
                sortTypeCategories = 1
                viewModel.getCategoriesData(userId,sessionId, sortAscCategories, sortTypeCategories)
            }

            chipCategoriesTopVolume.setOnClickListener {
                sortAscCategories = 1
                sortTypeCategories = 4
                viewModel.getCategoriesData(userId,sessionId,sortAscCategories,sortTypeCategories)
            }

            chipCategoriesTopFreq.setOnClickListener {
                sortAscCategories = 1
                sortTypeCategories = 5
                viewModel.getCategoriesData(userId,sessionId,sortAscCategories,sortTypeCategories)
            }

            chipCategoriesTopGainerPct.setOnClickListener {
                sortAscCategories = 1
                sortTypeCategories = 2
                viewModel.getCategoriesData(userId,sessionId,sortAscCategories,sortTypeCategories)
            }

            chipCategoriesTopLosersPct.setOnClickListener {
                sortAscCategories = 0
                sortTypeCategories = 2
                viewModel.getCategoriesData(userId,sessionId,sortAscCategories,sortTypeCategories)
            }

            chipCategoriesTopValue.setOnClickListener {
                sortAscCategories = 1
                sortTypeCategories = 3
                viewModel.getCategoriesData(userId,sessionId, sortAscCategories, sortTypeCategories)
            }


        }
    }

    private fun menuOnCLick() {
        binding.apply {
            menuOrderBook.setOnClickListener {
                MiddleActivity.startIntentParam(requireActivity(), NavKeys.KEY_FM_STOCK_DETAIL, "BBCA", "")
            }

            menuRunningTrade.setOnClickListener {
//                MiddleActivity.startIntent(requireActivity(), NavKeys.KEY_FM_RUNNING_TRADE)
                findNavController().navigate(R.id.running_trade_fragment)
            }

            menuStockPicks.setOnClickListener {
//                MiddleActivity.startIntent(requireActivity(), NavKeys.KEY_FM_STOCK_PICK)
                findNavController().navigate(R.id.stock_pick_fragment)
            }

            menuCalendar.setOnClickListener {
                findNavController().navigate(R.id.calendar_fragment)
            }
        }
    }

    private fun filterChartOnClick() {
        binding.apply {
            tvFilterChartDay.setOnClickListener {
                filterChartOnClick(1)
                viewModel.getChartIntraday(userId, sessionId, indiceCode, getStartDate("1D"), endDate, 0)
            }

            tvFilterChartWeek.setOnClickListener {
                filterChartOnClick(2)
                val startDay = Calendar.getInstance()
                startDay.add(Calendar.DAY_OF_YEAR, -7)
                startDay.set(Calendar.HOUR_OF_DAY, 0) // Set to midnight
                startDay.set(Calendar.MINUTE, 0)
                startDay.set(Calendar.SECOND, 0)
                startDay.set(Calendar.MILLISECOND, 0)

                val endDay = Calendar.getInstance()
                endDay.set(Calendar.HOUR_OF_DAY, 0) // Set to midnight
                endDay.set(Calendar.MINUTE, 0)
                endDay.set(Calendar.SECOND, 0)
                endDay.set(Calendar.MILLISECOND, 0)
                viewModel.getChartIntraday(userId, sessionId, indiceCode, startDay.timeInMillis, endDay.timeInMillis, 1)
            }

            tvFilterChartMonth.setOnClickListener {
                filterChartOnClick(3)
                viewModel.getChartIntraday(userId, sessionId, indiceCode, getStartDate("1M"), endDate, 2)
            }

            tvFilterChartThreeMonth.setOnClickListener {
                filterChartOnClick(4)
                viewModel.getChartIntraday(userId, sessionId, indiceCode, getStartDate("3M"), endDate, 2)
            }

            tvFilterChartYear.setOnClickListener {
                filterChartOnClick(5)
                viewModel.getChartIntraday(userId, sessionId, indiceCode, getStartDate("1Y"), endDate, 2)
            }

            tvFilterChartFiveYears.setOnClickListener {
                filterChartOnClick(6)
                viewModel.getChartIntraday(userId, sessionId, indiceCode, getStartDate("5Y"), endDate, 2)
            }

            tvFilterChartAll.setOnClickListener {
                filterChartOnClick(7)
                viewModel.getChartIntraday(userId, sessionId, indiceCode, getStartDate("10Y"), endDate, 2)
            }
        }
    }

    private fun getStartDate(state: String): Long {
        val calendar = Calendar.getInstance()
        when (state) {
            "1D" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            }
            "1W" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
            }
            "1M" -> {
                calendar.add(Calendar.MONTH, -1)
            }
            "3M" -> {
                calendar.add(Calendar.MONTH, -3)
            }
            "1Y" -> {
                calendar.add(Calendar.YEAR, -1)
            }
            "5Y" -> {
                calendar.add(Calendar.YEAR, -5)
            }
            "10Y" -> {
                calendar.add(Calendar.YEAR, -10)
            }
        }
        return calendar.timeInMillis
    }

    private fun setupChart(data: List<DataChart>) {
        //Part1
        val entries = ArrayList<Entry>()
        var index = 0f

        //Part2
        if (data.size != 0) {
            data.map {
                index++
                entries.add(Entry(index, it.price.roundedHalfUp().toFloat()))
            }
            val lineDataSet = LineDataSet(entries, "")
            val drawable =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_gradient_10c00d_ffffff)

            lineDataSet.fillDrawable = drawable
            lineDataSet.setDrawValues(false)
            lineDataSet.setDrawFilled(true)
            lineDataSet.lineWidth = 2f
            lineDataSet.color = ContextCompat.getColor(requireContext(), R.color.textUp)
            lineDataSet.setDrawCircles(false)

            val dataMarker = data.toMutableList()
            val marker = CustomMarkerView(requireContext(), R.layout.custom_market_view, dataMarker)
            marker.chartView = binding.chartDiscover

            binding.chartDiscover.marker = marker
            binding.chartDiscover.data = LineData(lineDataSet)
            binding.chartDiscover.xAxis.setDrawGridLines(false)
            binding.chartDiscover.axisLeft.setDrawGridLines(false)
            binding.chartDiscover.axisRight.setDrawGridLines(false)
            binding.chartDiscover.axisRight.textColor = ContextCompat.getColor(requireContext(), R.color.textPrimary)
            binding.chartDiscover.description.text = ""
            binding.chartDiscover.legend.isEnabled = false
            binding.chartDiscover.axisLeft.isEnabled = false
            binding.chartDiscover.xAxis.isEnabled = false
            binding.chartDiscover.axisRight.axisLineColor = ContextCompat.getColor(requireContext(), R.color.white)
            binding.chartDiscover.axisRight.isGranularityEnabled = true
            binding.chartDiscover.isDoubleTapToZoomEnabled = false
            binding.chartDiscover.setTouchEnabled(true)
            binding.chartDiscover.setScaleEnabled(false)
            binding.chartDiscover.invalidate()
            binding.chartDiscover.axisRight.valueFormatter = object : ValueFormatter() {
                private val decimalFormat = DecimalFormat("#,###,###")
                override fun getPointLabel(entry: Entry?): String {
                    return decimalFormat.format(entry)
                }

                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return decimalFormat.format(value)
                }
            }
        } else {
            binding.chartDiscover.clear()
            binding.chartDiscover.setNoDataTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }


    }


}