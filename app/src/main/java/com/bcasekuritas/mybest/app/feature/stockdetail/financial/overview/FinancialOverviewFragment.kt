package com.bcasekuritas.mybest.app.feature.stockdetail.financial.overview


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.stockdetail.StockDetailSharedViewModel
import com.bcasekuritas.mybest.databinding.FragmentFinancialOverviewBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.formatLastNumber
import com.bcasekuritas.mybest.ext.date.DateUtils.getQuarter
import com.bcasekuritas.mybest.ext.other.RoundedHalfDown
import com.bcasekuritas.mybest.ext.other.formatPercent


@FragmentScoped
@AndroidEntryPoint
class FinancialOverviewFragment :
    BaseFragment<FragmentFinancialOverviewBinding, FinancialOverviewViewModel>() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmFinancialOverview
    override val viewModel: FinancialOverviewViewModel by viewModels()
    override val binding: FragmentFinancialOverviewBinding by autoCleaned {
        FragmentFinancialOverviewBinding.inflate(
            layoutInflater
        )
    }

    lateinit var sharedViewModel: StockDetailSharedViewModel
    private var stockCode = ""
    private var countHideLoading = 0

    companion object {
        fun newInstance() = FinancialOverviewFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(StockDetailSharedViewModel::class.java)
    }

    override fun setupComponent() {
        super.setupComponent()
        showLoading()
    }

    override fun initAPI() {
        super.initAPI()

        countHideLoading = 0

        val userId = prefManager.userId
        stockCode = prefManager.stockDetailCode
        val sessionId = prefManager.sessionId

        viewModel.getIncomeStatementChart(userId, sessionId, stockCode)
        viewModel.getBalanceSheetChart(userId, sessionId, stockCode)
        viewModel.getCashFlowChart(userId, sessionId, stockCode)
    }

    override fun setupObserver() {
        super.setupObserver()
        sharedViewModel.getStockCodeChangeResult.observe(viewLifecycleOwner){
            if (it == true){
                showLoading()
                initAPI()
            }
        }

        sharedViewModel.getRefreshFragmentResult.observe(viewLifecycleOwner){
            if (it == true){
                initAPI()
            }
        }

        viewModel.getIncomeStatementResult.observe(viewLifecycleOwner) {
            if (it != null){
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        when (it.data?.status) {
                            0.toString() -> {
                                if (it.data != null) {
                                    if (it.data.incomeStatementDataList.isNullOrEmpty()){
                                        noChart(binding.chartFoInvoiceStatement)
                                    }else{
                                        it.data.let { res ->
                                            val dataprofitMargin = ArrayList<Double>()
                                            val dataNetIncome = ArrayList<Double>()
                                            val dataRevenue = ArrayList<Double>()
                                            val dataXLabel = ArrayList<String>()
                                            val countData = res.incomeStatementDataList.size - 1

                                            for (i in countData downTo 0) {
                                                dataprofitMargin.add(res.incomeStatementDataList[i].netProfitMargin)
                                                dataNetIncome.add(res.incomeStatementDataList[i].netIncome)
                                                dataRevenue.add(res.incomeStatementDataList[i].revenue)
                                                dataXLabel.add(getQuarter(res.incomeStatementDataList[i].period))
                                            }

                                            generateLineBarChart(
                                                1,
                                                binding.chartFoInvoiceStatement,
                                                dataprofitMargin,
                                                dataRevenue,
                                                dataNetIncome,
                                                dataXLabel
                                            )
                                        }
                                        countToHideLoading()
                                    }
                                }else{
                                    noChart(binding.chartFoInvoiceStatement)
                                }
                            }
                            else -> {
                                noChart(binding.chartFoInvoiceStatement)
                            }
                        }

                    }

                    is Resource.Failure -> {
                        noChart(binding.chartFoInvoiceStatement)
                        Toast.makeText(requireContext(), it.failureData.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {
                        noChart(binding.chartFoInvoiceStatement)
                    }
                }
            }else{
                noChart(binding.chartFoInvoiceStatement)
            }
        }

        viewModel.getBalanceSheetResult.observe(viewLifecycleOwner) {
            if (it != null){
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        when (it.data?.status) {
                            0.toString() -> {
                                if (it.data != null) {
                                    if (it.data.balanceSheetDataList.isNullOrEmpty()){
                                        noChart(binding.chartFoBalanceSheet)
                                    }else{
                                        it.data.let { res ->
                                            val dataLiabilitiesTot = ArrayList<Double>()
                                            val dataAssetTot = ArrayList<Double>()
                                            val dataDebtEquityRatio = ArrayList<Double>()
                                            val dataXLabel = ArrayList<String>()
                                            val countData = res.balanceSheetDataList.size - 1

                                            for (i in countData downTo 0) {
                                                dataLiabilitiesTot.add(res.balanceSheetDataList[i].liabilities)
                                                dataAssetTot.add(res.balanceSheetDataList[i].assets)
                                                dataDebtEquityRatio.add(res.balanceSheetDataList[i].der)
                                                dataXLabel.add(getQuarter(res.balanceSheetDataList[i].period))
                                            }

                                            generateLineBarChart(
                                                2,
                                                binding.chartFoBalanceSheet,
                                                dataDebtEquityRatio,
                                                dataAssetTot,
                                                dataLiabilitiesTot,
                                                dataXLabel
                                            )
                                        }
                                        countToHideLoading()
                                    }
                                } else{
                                    noChart(binding.chartFoBalanceSheet)
                                }
                            }

                            else -> {
                                noChart(binding.chartFoBalanceSheet)
                            }
                        }
                    }

                    is Resource.Failure -> {
                        noChart(binding.chartFoBalanceSheet)
                        Toast.makeText(requireContext(), it.failureData.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {
                        noChart(binding.chartFoBalanceSheet)
                    }
                }
            }else{
                noChart(binding.chartFoBalanceSheet)
            }
        }

        viewModel.getCashFlowResult.observe(viewLifecycleOwner) {
            if (it != null){
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        when (it.data?.status) {
                            0.toString() -> {
                                if (it.data != null) {
                                    if (it.data.cashFlowDataList.isNullOrEmpty()){
                                        noChart(binding.chartFoCashflow)
                                    }else{
                                        it.data.let { res ->
                                            val dataFinancing = ArrayList<Double>()
                                            val dataInvesting = ArrayList<Double>()
                                            val dataOperating = ArrayList<Double>()
                                            val dataXLabel = ArrayList<String>()
                                            val countData = res.cashFlowDataList.size - 1

                                            for (i in countData downTo 0) {
                                                dataFinancing.add(res.cashFlowDataList[i].financing)
                                                dataInvesting.add(res.cashFlowDataList[i].investing)
                                                dataOperating.add(res.cashFlowDataList[i].operating)
                                                dataXLabel.add(getQuarter(res.cashFlowDataList[i].period))
                                            }
                                            generateBarChart(
                                                binding.chartFoCashflow,
                                                dataInvesting,
                                                dataOperating,
                                                dataFinancing,
                                                dataXLabel
                                            )
                                        }
                                        countToHideLoading()
                                    }
                                }else{
                                    noChart(binding.chartFoCashflow)
                                }
                            }

                            else -> {
                                noChart(binding.chartFoCashflow)
                            }
                        }
                    }

                    is Resource.Failure -> {
                        noChart(binding.chartFoCashflow)
                        Toast.makeText(requireContext(), it.failureData.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {
                        noChart(binding.chartFoCashflow)
                    }
                }
            }else{
                noChart(binding.chartFoCashflow)
            }
        }
    }

    private fun countToHideLoading(){
        countHideLoading++
        if (countHideLoading == 3){
            hideLoading()
        }
    }

    private fun generateLineBarChart(
        idChart: Int,
        chartView: CombinedChart,
        listDataOne: ArrayList<Double>,
        listDataTwo: ArrayList<Double>,
        listDataThree: ArrayList<Double>?,
        listDataLabel: ArrayList<String>
    ) {
        setGeneralChart(chartView)

        // Setting Value Right of Chart
        val rightAxis: YAxis = chartView.axisRight
        val minValue = listDataOne.min()
        val maxValue = listDataOne.max()
        val range = (maxValue - minValue).toFloat()

        rightAxis.axisMinimum = (minValue - 0.03).toFloat()
        rightAxis.axisMaximum = (maxValue + 0.02).toFloat()
        rightAxis.setDrawAxisLine(false)
        rightAxis.setDrawGridLines(false)
        rightAxis.setLabelCount(5, true)
        rightAxis.granularity = range / 5
        rightAxis.valueFormatter = CustomPercentValueFormatter()

        Log.d("chart", "${rightAxis.axisMinimum}")
        Log.d("chart", "${rightAxis.granularity}")

        // Setting Value Left of Chart
        val leftAxis: YAxis = chartView.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)
        leftAxis.setStartAtZero(false)
        leftAxis.valueFormatter = CustomLargeValueFormatter()


        // Setting Value Bottom of Chart
        val xAxis: XAxis = chartView.xAxis
        xAxis.position = XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.setCenterAxisLabels(true)
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
        xAxis.valueFormatter = IndexAxisValueFormatter(listDataLabel)

        // Set Data to Chart
        val data = CombinedData()
        data.clearValues()

        data.setData(generateBarData(idChart, listDataTwo, listDataThree!!, null))
        data.setData(generateLineData(idChart, listDataOne))

        xAxis.axisMaximum = data.xMax + 0.5f

        chartView.data = data
        chartView.invalidate()
    }

    private fun generateBarChart(
        chartView: CombinedChart, listDataOne: ArrayList<Double>, listDataTwo: ArrayList<Double>,
        listDataThree: ArrayList<Double>?, listDataLabel: ArrayList<String>
    ) {
        setGeneralChart(chartView)

        // Setting Value Right of Chart
        val rightAxis: YAxis = chartView.axisRight
        rightAxis.setDrawAxisLine(false)
        rightAxis.setDrawGridLines(false)
        rightAxis.valueFormatter = CustomLargeValueFormatter()

        // Setting Value Left of Chart
        val leftAxis: YAxis = chartView.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)
        leftAxis.setDrawZeroLine(true)
        leftAxis.zeroLineColor = Color.GRAY
        leftAxis.zeroLineWidth = 0.5f
        leftAxis.valueFormatter = CustomLargeValueFormatter()

        // Setting Value Bottom of Chart
        val xAxis: XAxis = chartView.xAxis
        xAxis.position = XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.setCenterAxisLabels(true)
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
        xAxis.valueFormatter = IndexAxisValueFormatter(listDataLabel)

        // Set Data to Chart
        val data = CombinedData()
        data.clearValues()

        data.setData(generateBarData(3, listDataOne, listDataTwo, listDataThree))
        data.setData(generateLineData(3, listDataOne))

        xAxis.axisMaximum = data.xMax + 0.5f

        chartView.data = data
        chartView.invalidate()
    }

    private fun setGeneralChart(chartView: CombinedChart) {
        chartView.description.isEnabled = false
        chartView.isHighlightFullBarEnabled = false
        chartView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bgWhite))
        chartView.setDrawGridBackground(false)
        chartView.setDrawBarShadow(false)
        chartView.setPinchZoom(false)
        chartView.setTouchEnabled(false)
        chartView.setDrawValueAboveBar(false)
        chartView.setNoDataText("No data yet!")
        chartView.setExtraOffsets(1f, 5f, 1f, 15f)

        chartView.drawOrder = arrayOf<DrawOrder>(
            DrawOrder.BAR,
            DrawOrder.LINE
        )

        // Setting Legend Chart
        val legend: Legend = chartView.legend
        legend.isWordWrapEnabled = true
        legend.textColor = ContextCompat.getColor(requireContext(), R.color.textPrimary)
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.form = Legend.LegendForm.CIRCLE
        legend.xEntrySpace = 10f
        legend.setDrawInside(false)
    }

    private fun generateLineData(idChart: Int, itemList: ArrayList<Double>): LineData {
        val data = LineData()
        val entries = ArrayList<Entry>()
        var label = ""

        entries.clear()
        data.clearValues()

        for (index in 0 until itemList.size) {
            entries.add(Entry(index + 0.5f, itemList[index].toFloat()))
        }

        when (idChart) {
            1 -> label = "Profit Margin"
            2 -> label = "Debt Equity Ratio"
        }

        val set = LineDataSet(entries, label)
        set.mode = LineDataSet.Mode.LINEAR
        set.lineWidth = 2f
        set.setDrawCircles(false)
        set.setDrawValues(false)

        if (idChart == 3) {
            set.color = Color.TRANSPARENT
            set.fillColor = Color.TRANSPARENT
            set.axisDependency = YAxis.AxisDependency.LEFT
        } else {
            set.color = ContextCompat.getColor(requireContext(), R.color.chartBlue)
            set.fillColor = ContextCompat.getColor(requireContext(), R.color.chartBlue)
            set.axisDependency = YAxis.AxisDependency.RIGHT
        }

        data.addDataSet(set)

        return data
    }

    private fun generateBarData(
        idChart: Int,
        listDataOne: ArrayList<Double>,
        listDataTwo: ArrayList<Double>,
        listDataThree: ArrayList<Double>?
    ): BarData {
        val barOne = ArrayList<BarEntry>()
        val barTwo = ArrayList<BarEntry>()
        val barThree = ArrayList<BarEntry>()
        var labelOne = ""
        var labelTwo = ""
        var labelThree = ""
        val setDataBarOne: BarDataSet
        val setDataBarTwo: BarDataSet
        val setDataBarThree: BarDataSet
        var barData = BarData()
        var groupSpace = 0f
        val barSpace = 0.02f
        val barWidth = 0.2f

        barOne.clear()
        barTwo.clear()
        barThree.clear()
        barData.clearValues()

        when (idChart) {
            1 -> {
                labelOne = "Revenue"
                labelTwo = "Net Income"
            }

            2 -> {
                labelOne = "Total Assets"
                labelTwo = "Total Liabilities"

            }

            3 -> {
                labelOne = "Investing"
                labelTwo = "Operating"
                labelThree = "Financing"
            }
        }


        for (i in 0 until listDataOne.size) {
            barOne.add(BarEntry(0f, listDataOne[i].toFloat()))
        }

        for (i in 0 until listDataTwo.size) {
            barTwo.add(BarEntry(0f, listDataTwo[i].toFloat()))
        }

        setDataBarOne = BarDataSet(barOne, labelOne)
        setDataBarOne.color = ContextCompat.getColor(requireContext(), R.color.chartGreen)
        setDataBarOne.axisDependency = YAxis.AxisDependency.LEFT
        setDataBarOne.setDrawValues(false)

        setDataBarTwo = BarDataSet(barTwo, labelTwo)
        setDataBarTwo.color = ContextCompat.getColor(requireContext(), R.color.chartYellow)
        setDataBarTwo.axisDependency = YAxis.AxisDependency.LEFT
        setDataBarTwo.setDrawValues(false)

        if (idChart == 3) {
            groupSpace = 0.34f

            for (i in 0 until listDataThree!!.size) {
                barThree.add(BarEntry(0f, listDataThree[i].toFloat()))
            }

            setDataBarThree = BarDataSet(barThree, labelThree)
            setDataBarThree.color = ContextCompat.getColor(requireContext(), R.color.chartBlue)
            setDataBarThree.axisDependency = YAxis.AxisDependency.LEFT
            setDataBarThree.setDrawValues(false)

            barData = BarData(setDataBarOne, setDataBarTwo, setDataBarThree)
        } else {
            groupSpace = 0.56f
            barData = BarData(setDataBarOne, setDataBarTwo)
        }

        barData.barWidth = barWidth
        barData.setValueFormatter(CustomLargeValueFormatter())
        barData.groupBars(0f, groupSpace, barSpace) // start at x = 0

        return barData
    }

    private fun noChart(chartView: CombinedChart){
        countToHideLoading()
        chartView.setNoDataText("No Date Yet!")
        if (chartView.data != null){
            chartView.data.clearValues()
        }
        chartView.xAxis.valueFormatter = null
        chartView.axisLeft.valueFormatter = null
        chartView.axisRight.valueFormatter = null
        chartView.notifyDataSetChanged()
        chartView.clear()
        chartView.invalidate()
    }

    class CustomLargeValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return formatLastNumber(value)
        }
    }

    class CustomPercentValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val percent = value.toDouble().formatPercent()

            return "$percent%"
        }
    }
}