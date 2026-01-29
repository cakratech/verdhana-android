package com.bcasekuritas.mybest.app.feature.stockdetail.analysis

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
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
import com.bcasekuritas.mybest.app.feature.stockdetail.adapter.StockDetailAnalysisAdapter
import com.bcasekuritas.mybest.databinding.FragmentStockDetailAnalysisBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.getCurrentDate
import com.bcasekuritas.mybest.ext.common.initFormatThousandSeparator
import com.bcasekuritas.mybest.ext.other.CapitalizeFirstLetter
import com.bcasekuritas.mybest.ext.other.RoundedHalfDown
import com.bcasekuritas.mybest.ext.other.formatWithoutDecimalHalfUp
import com.bcasekuritas.mybest.ext.other.formatWithoutDecimalRoundingUp
import timber.log.Timber
import java.util.Collections
import java.util.Locale
import kotlin.math.roundToInt

@FragmentScoped
@AndroidEntryPoint
class StockDetailAnalysisFragment : BaseFragment<FragmentStockDetailAnalysisBinding, StockDetailAnalysisViewModel>() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmStockDetailAnalysis
    override val viewModel: StockDetailAnalysisViewModel by viewModels()
    override val binding: FragmentStockDetailAnalysisBinding by autoCleaned {
        FragmentStockDetailAnalysisBinding.inflate(
            layoutInflater
        )
    }

    private val stockDetailAnalysisAdapter: StockDetailAnalysisAdapter by autoCleaned{ StockDetailAnalysisAdapter(requireContext())}

    lateinit var sharedViewModel: StockDetailSharedViewModel

    private var stockCode = ""
    private var periodPerBand = 3
    private var periodPbvBand = 3
    private var countHideLoading = 0

    private var perForward = ""
    private var perPlus1Forward = ""
    private var perMin1Forward = ""
    private var perMeanForward = ""
    private var perPlus2Forward = ""
    private var perMin2Forward = ""

    private var pbvForward = ""
    private var pbvPlus1Forward = ""
    private var pbvMin1Forward = ""
    private var pbvMeanForward = ""
    private var pbvPlus2Forward = ""
    private var pbvMin2Forward = ""

    private var tvTargetPriceWidth = 0f
    private var tvCurrentPriceWidth = 0f
    private var progressTarget = "0"
    private var progressMax = "0"
    private var progressMin = "0"

    @RequiresApi(Build.VERSION_CODES.O)
    private var date = getCurrentDate()

    var userId = ""
    var sessionId = ""
    var lastPrice = 0.0
//    val codeStock = prefManager.stockDetailCode

    companion object {
        fun newInstance() = StockDetailAnalysisFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(StockDetailSharedViewModel::class.java)
    }

    override fun setupComponent() {
        super.setupComponent()

        showLoading()
        binding.chipGroupPvBand.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                when (checkedIds[0]) {
                    R.id.chip_pbv1 -> periodPbvBand = 3
                    R.id.chip_pbv2 -> periodPbvBand = 5
                    R.id.chip_pbv3 -> periodPbvBand = 10
                }
                viewModel.getPbvBand(userId, sessionId, stockCode, periodPbvBand)
                viewModel.getPbvData(userId, sessionId, stockCode, periodPbvBand)
            }
        }

        binding.chipGroupPeBand.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()){
                when(checkedIds[0]){
                    R.id.chip_pe1 -> periodPerBand = 3
                    R.id.chip_pe2 -> periodPerBand = 5
                    R.id.chip_pe3 -> periodPerBand = 10
                }
                viewModel.getPerBand(userId, sessionId, stockCode, periodPerBand)
                viewModel.getPerData(userId, sessionId, stockCode, periodPerBand)
            }
        }
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            btnFsdaPeBandInfo.setOnClickListener {
                showDialogPortfolioSummaryInfoBottom(
                    "PE Band (TTM)",
                    "PE Band (TTM) is a valuation tool used to determine the fair price of a stock by analyzing its historical Price to Earnings (P/E) Ratio. This method relies on earnings calculated on a trailing twelve months basis.",
                    "",
                    parentFragmentManager
                    )
            }

            btnFsdaPvBandInfo.setOnClickListener {
                showDialogPortfolioSummaryInfoBottom(
                    "PBV Band",
                    "PBV Band is a valuation tool used to determine the fair price of a stock by analyzing its historical Price to Book Value (PBV) Ratio. This method relies on historical book values, providing insight into the stock's valuation relative to its assets.",
                    "",
                    parentFragmentManager
                )
            }

            btnFsdaPivotInfo.setOnClickListener {
                showDialogFibonacci(parentFragmentManager)
            }
        }
    }

    override fun setupAdapter() {
        super.setupAdapter()

        binding.rcvTechIndicator.adapter = stockDetailAnalysisAdapter
    }

    override fun initAPI() {
        super.initAPI()
        countHideLoading = 0
        this.userId = prefManager.userId
        this.sessionId = prefManager.sessionId
        this.stockCode = prefManager.stockDetailCode
//        this.lastPrice = prefManager.lastPrice

        viewModel.getStockAnalysisRating(userId, sessionId, stockCode)
        viewModel.getPerBand(userId, sessionId, stockCode, periodPerBand)
        viewModel.getPbvBand(userId, sessionId, stockCode, periodPbvBand)
        viewModel.getPerData(userId, sessionId, stockCode, periodPerBand)
        viewModel.getPbvData(userId, sessionId, stockCode, periodPbvBand)
        viewModel.getFibonacciPivotPoint(userId, sessionId, stockCode)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
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

        sharedViewModel.getStockDetailSummary.observe(viewLifecycleOwner){
            if (it != null){
                lastPrice = it.last
            }
        }

        viewModel.getPerBandResult.observe(viewLifecycleOwner) {
            if (it != null){
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        when (it.data?.status) {
                            0.toString() -> {
                                if (it.data != null && it.data.dataCount != 0) {
                                    if (it.data.dataList.isNullOrEmpty()){
                                        emptyPerBand()
                                    }else{
                                        it.data.let { res ->
                                            binding.apply {
                                                perForward = res.dataList[0].stdDevPer.RoundedHalfDown()
                                                perPlus1Forward = res.dataList[0].stdDevPlus1Per.RoundedHalfDown()
                                                perMin1Forward = res.dataList[0].stdDevMin1Per.RoundedHalfDown()
                                                perMeanForward = res.dataList[0].meanPer.RoundedHalfDown()
                                                perPlus2Forward = res.dataList[0].stdDevPlus2Per.RoundedHalfDown()
                                                perMin2Forward = res.dataList[0].stdDevMin2Per.RoundedHalfDown()

                                                tvFsdaPeBandForwardPeRatioDate.text = date
                                                tvFsdaPeBandForwardPeRatioValue.text = perForward
                                                tvFsdaPeBandPlus1ForwardPeRatioDate.text = date
                                                tvFsdaPeBandPlus1ForwardPeRatioValue.text = perPlus1Forward
                                                tvFsdaPeBandMin1ForwardPeRatioDate.text = date
                                                tvFsdaPeBandMin1ForwardPeRatioValue.text = perMin1Forward
                                                tvFsdaPeBandMeanForwardPeRatioDate.text = date
                                                tvFsdaPeBandMeanForwardPeRatioValue.text = perMeanForward
                                                tvFsdaPeBandPlus2ForwardPeRatioDate.text = date
                                                tvFsdaPeBandPlus2ForwardPeRatioValue.text = perPlus2Forward
                                                tvFsdaPeBandMin2ForwardPeRatioDate.text = date
                                                tvFsdaPeBandMin2ForwardPeRatioValue.text = perMin2Forward
                                            }
                                        }
                                        countToHideLoading()
                                    }
                                }else{
                                    emptyPerBand()
                                }
                            }

                            else -> emptyPerBand()
                        }
                    }

                    is Resource.Failure -> {
                        emptyPerBand()
                        Toast.makeText(requireContext(), it.failureData.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {
                        emptyPerBand()
                    }
                }
            }else{
                emptyPerBand()
            }
        }

        viewModel.getPerDataResult.observe(viewLifecycleOwner) {
            if (it != null){
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        when (it.data?.status) {
                            0.toString() -> {
                                if (it.data != null && it.data.dataCount != 0) {
                                    if (it.data.dataList.isNullOrEmpty()){
                                        noChart(binding.lineChartPeBand)
                                    }else{
                                        it.data.let { res ->
                                            val dataItem = ArrayList<Double>()
                                            val dataXLabel = ArrayList<String>()
                                            val countData = res.dataCount - 1
                                            val sortingData = res.dataList.sortedBy { it.valueDate }

                                            for (i in 0..countData) {
                                                dataItem.add(sortingData[i].per)

                                                var year = sortingData[i].valueDate.split("-")
                                                dataXLabel.add((year[0].toInt() + 1).toString())
                                            }

                                            generateLineData(binding.lineChartPeBand, dataItem, dataXLabel, periodPerBand)
                                        }
                                        countToHideLoading()
                                    }
                                }else{
                                    noChart(binding.lineChartPeBand)
                                }
                            }

                            else -> noChart(binding.lineChartPeBand)
                        }
                    }

                    is Resource.Failure -> {
                        noChart(binding.lineChartPeBand)
                        Toast.makeText(requireContext(), it.failureData.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {
                        noChart(binding.lineChartPeBand)
                    }
                }
            }else{
                noChart(binding.lineChartPeBand)
            }
        }

        viewModel.getPbvBandResult.observe(viewLifecycleOwner) {
            if (it != null){
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        when (it.data?.status) {
                            0.toString() -> {
                                if (it.data != null && it.data.dataCount != 0) {
                                    if (it.data.dataList.isNullOrEmpty()){
                                        emptyPbvBand()
                                    }else{
                                        it.data.let { res ->
                                            pbvForward = res.dataList[0].stdDevPbv.RoundedHalfDown()
                                            pbvPlus1Forward = res.dataList[0].stdDevPlus1Pbv.RoundedHalfDown()
                                            pbvMin1Forward = res.dataList[0].stdDevMin1Pbv.RoundedHalfDown()
                                            pbvMeanForward = res.dataList[0].meanPbv.RoundedHalfDown()
                                            pbvPlus2Forward = res.dataList[0].stdDevPlus2Pbv.RoundedHalfDown()
                                            pbvMin2Forward = res.dataList[0].stdDevMin2Pbv.RoundedHalfDown()

                                            binding.apply {
                                                tvFsdaPvBandForwardPeRatioDate.text = date
                                                tvFsdaPvBandForwardPeRatioValue.text = pbvForward
                                                tvFsdaPvBandPlus1ForwardPeRatioDate.text = date
                                                tvFsdaPvBandPlus1ForwardPeRatioValue.text = pbvPlus1Forward
                                                tvFsdaPvBandMin1ForwardPeRatioDate.text = date
                                                tvFsdaPvBandMin1ForwardPeRatioValue.text = pbvMin1Forward
                                                tvFsdaPvBandMeanForwardPeRatioDate.text = date
                                                tvFsdaPvBandMeanForwardPeRatioValue.text = pbvMeanForward
                                                tvFsdaPvBandPlus2ForwardPeRatioDate.text = date
                                                tvFsdaPvBandPlus2ForwardPeRatioValue.text = pbvPlus2Forward
                                                tvFsdaPvBandMin2ForwardPeRatioDate.text = date
                                                tvFsdaPvBandMin2ForwardPeRatioValue.text = pbvMin2Forward
                                            }
                                        }
                                        countToHideLoading()
                                    }
                                }else{
                                    emptyPbvBand()
                                }
                            }

                            else -> emptyPbvBand()
                        }
                    }

                    is Resource.Failure -> {
                        emptyPbvBand()
                        Toast.makeText(requireContext(), it.failureData.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {
                        emptyPbvBand()
                    }
                }
            }else{
                emptyPbvBand()
            }
        }

        viewModel.getPbvDataResult.observe(viewLifecycleOwner) {
            if (it != null){
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        when (it.data?.status) {
                            0.toString() -> {
                                if (it.data != null && it.data.dataCount != 0) {
                                    if (it.data.dataList.isNullOrEmpty()){
                                        noChart(binding.lineChartPvBand)
                                    }else{
                                        it.data.let { res ->
                                            val dataItem = ArrayList<Double>()
                                            val dataXLabel = ArrayList<String>()
                                            val countData = res.dataCount - 1
                                            val sortingData = res.dataList.sortedBy { it.valueDate }

                                            for (i in 0..countData) {
                                                dataItem.add(sortingData[i].pbv)

                                                var year = sortingData[i].valueDate.split("-")
                                                dataXLabel.add((year[0].toInt() + 1).toString())
                                            }

                                            generateLineData(binding.lineChartPvBand, dataItem, dataXLabel, periodPbvBand)
                                        }
                                        countToHideLoading()
                                    }
                                }else{
                                    noChart(binding.lineChartPvBand)
                                }
                            }

                            else -> noChart(binding.lineChartPvBand)
                        }
                    }

                    is Resource.Failure -> {
                        noChart(binding.lineChartPvBand)
                        Toast.makeText(requireContext(), it.failureData.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {
                        noChart(binding.lineChartPvBand)
                    }
                }
            }else{
                noChart(binding.lineChartPvBand)
            }
        }

        viewModel.getStockAnalysisRatingResult.observe(viewLifecycleOwner) {
            if (it != null){
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        when (it.data?.status) {
                            0.toString() -> {
                                if (it.data != null){
                                    it.data.let { res ->
                                        binding.apply {
                                            val resTotalBuy = res.data.totalBuyRec?.toDoubleOrNull()?: 0.0
                                            val resTotalSell = res.data.totalSellRec?.toDoubleOrNull()?: 0.0
                                            val resTotalHold = res.data.totalHoldRec?.toDoubleOrNull()?: 0.0

                                            val totalBuy = resTotalBuy.roundToInt()
                                            val totalSell = resTotalSell.roundToInt()
                                            val totalHold = resTotalHold.roundToInt()
                                            val totalAll = totalBuy + totalHold + totalSell
                                            val recSumName = res.data.recommendedSummaryName ?: ""

                                            /** Analyst Rating */
                                            val resRecommendedSummary = res.data.recommendedSummary?.toDoubleOrNull() ?: 0.0
                                            tvFsdaAnalysisRatingPercent.text = "${(resRecommendedSummary * 100).roundToInt()}%"
                                            tvFsdaAnalysisRatingPercentName.text = recSumName.CapitalizeFirstLetter()

                                            if (totalAll == 0){
                                                clFsdaAnalystRating.visibility = View.GONE
                                            }else{
                                                clFsdaAnalystRating.visibility = View.VISIBLE

                                                tvFsdaProgressBarAnalysisRatingBuy.progress = totalBuy
                                                tvFsdaProgressBarAnalysisRatingBuy.max = totalAll
                                                tvFsdaAnalysisRatingBuy.text = "$totalBuy"

                                                tvFsdaProgressBarAnalysisRatingHold.progress = totalHold
                                                tvFsdaProgressBarAnalysisRatingHold.max = totalAll
                                                tvFsdaAnalysisRatingHold.text = "$totalHold"

                                                tvFsdaProgressBarAnalysisRatingSell.progress = totalSell
                                                tvFsdaProgressBarAnalysisRatingSell.max = totalAll
                                                tvFsdaAnalysisRatingSell.text = "$totalSell"

                                                val total = totalBuy + totalHold +totalSell
                                                tvFsdaDescAnalysisRating.text = "Analysis from $total securities in Indonesia"
                                            }

                                            /** Target Price */
                                            val priceTargetLow = res.data.minTargetPrice?.toDoubleOrNull() ?: 0.0
                                            val priceTargetHigh = res.data.maxTargetPrice?.toDoubleOrNull() ?: 0.0
                                            val bestTargetPrice = res.data.bestTargetPrice?.toDoubleOrNull() ?: 0.0

                                            tvFsdaTitleTargetPrice.text = "Target Price for $stockCode"

                                            if (lastPrice == 0.0 || bestTargetPrice == 0.0){
                                                clFsdaTargetPrice.visibility = View.GONE
                                            }else{
                                                clFsdaTargetPrice.visibility = View.VISIBLE

                                                tvFsdaTargetPriceLow.text = "Low ${initFormatThousandSeparator(priceTargetLow)}"
                                                tvFsdaTargetPriceHigh.text = "High ${initFormatThousandSeparator(priceTargetHigh)}"
                                                tvTargetPrice.text = "Target \nRp${initFormatThousandSeparator(bestTargetPrice)}"
                                                tvCurrentPrice.text = "Current \nRp${initFormatThousandSeparator(lastPrice)}"

                                                tvTargetPriceWidth = tvTargetPrice.width.toFloat() / 2
                                                tvCurrentPriceWidth = tvCurrentPrice.width.toFloat() / 2
                                                val llTargetPriceWidth = binding.llTargetPriceTarget.width.toFloat() / 2
                                                val llCurrentPriceWidth = binding.llTargetPriceCurrent.width.toFloat() / 2
                                                progressTarget = res.data.bestTargetPrice ?: "0"
                                                progressMax = res.data.maxTargetPrice ?: "0"
                                                progressMin = res.data.minTargetPrice ?: "0"

                                                val isBuy = recSumName.lowercase(Locale.getDefault()) == "buy"
                                                val isSell = recSumName.lowercase(Locale.getDefault()) == "sell"

                                                val currentPriceProgress = when {
                                                    isBuy && lastPrice < priceTargetLow -> 0.toFloat()
                                                    isSell && lastPrice > priceTargetHigh -> progressMax.toFloat()
                                                    else -> lastPrice.toFloat()
                                                }

                                                llTargetPriceCurrent.visibility = when {
                                                    (isBuy && lastPrice > priceTargetHigh) || (isSell && lastPrice < priceTargetLow) -> View.GONE
                                                    else -> View.VISIBLE
                                                }

                                                movingComponentProgress(progressIndicatorTargetPrice, llTargetPriceTarget, lineTargetCenter, llTargetPriceWidth, tvTargetPriceWidth, progressTarget.toFloat(), progressMax.toFloat())
                                                movingComponentProgress(progressIndicatorTargetPrice, llTargetPriceCurrent, lineCurrentCenter, llCurrentPriceWidth, tvCurrentPriceWidth, currentPriceProgress, progressMax.toFloat())

                                            }
                                        }
                                    }
                                    countToHideLoading()
                                }else{
                                    emptyDataAnalysisRating()
                                }
                            }

                            else -> {
                                Timber.e("Error ${it.data?.remarks}! Stock Detail Analyst Status : ${it.data?.status}")
                                emptyDataAnalysisRating()
                            }
                        }
                    }

                    is Resource.Failure -> {
                        emptyDataAnalysisRating()
                        Toast.makeText(requireContext(), it.failureData.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {
                        emptyDataAnalysisRating()
                    }
                }
            }else{
                emptyDataAnalysisRating()
            }
        }

        viewModel.getFibonacciPivotPointResult.observe(viewLifecycleOwner) {
            if (it != null){
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        when (it.data?.status) {
                            0.toString() -> {
                                if (it.data.data != null) {
                                    it.data.data.let { res ->
                                        binding.apply {
                                            tvFsdaFibonacciS3.text = (res.s3 ?: 0.0).formatWithoutDecimalHalfUp()
                                            tvFsdaFibonacciS2.text = (res.s2 ?: 0.0).formatWithoutDecimalHalfUp()
                                            tvFsdaFibonacciS1.text = (res.s1 ?: 0.0).formatWithoutDecimalHalfUp()
                                            tvFsdaFibonacciPivotPoint.text = (res.pivotPoint ?: 0.0).formatWithoutDecimalHalfUp()
                                            tvFsdaFibonacciR3.text = (res.r3 ?: 0.0).formatWithoutDecimalHalfUp()
                                            tvFsdaFibonacciR2.text = (res.r2 ?: 0.0).formatWithoutDecimalHalfUp()
                                            tvFsdaFibonacciR1.text = (res.r1 ?: 0.0).formatWithoutDecimalHalfUp()
                                        }
                                    }
                                }

                            }
                        }
                        countToHideLoading()
                    }

                    is Resource.Failure -> {
                        countToHideLoading()
                        Toast.makeText(requireContext(), it.failureData.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {
                        countToHideLoading()
                    }
                }
            }else{
                countToHideLoading()
            }
        }
    }

    private fun movingComponentTargetProgress(progressBar: ProgressBar, layout: LinearLayout, tvWidth: Float, progress: Float, progressMax: Float, progressMin: Float){
        val progressBarWidth = progressBar.width.toFloat()    // Ambil nilai progress dan lebar ProgressBar setelah layout diukur
        val newX = progress / progressMax * progressBarWidth  // Hitung posisi baru berdasarkan progress

        val totHeightMax = tvWidth + newX
        val totHeightMin = newX - tvWidth
        var marginStart = 0

        if (totHeightMax > progressBarWidth){
            marginStart = (newX - (totHeightMax - progressBarWidth) - tvWidth).toInt()
        }else if (totHeightMin < 0){
            marginStart = 0
        }else{
            marginStart = (newX - tvWidth).toInt()
        }

        progressBar.post {
            // Set posisi ImageView sesuai dengan newX
            val params = layout.layoutParams as LayoutParams
            params.marginStart = marginStart
            layout.layoutParams = params
        }
    }

//    private fun movingComponentProgress(progressBar: ProgressBar, layout: ConstraintLayout, tvWidth: Float, progress: Float, progressMax: Float, progressMin: Float){
//        val progressBarWidth = progressBar.width.toFloat()    // Ambil nilai progress dan lebar ProgressBar setelah layout diukur
//        val newX = progress / progressMax * progressBarWidth  // Hitung posisi baru berdasarkan progress
//
//        val totHeightMax = tvWidth + newX
//        val totHeightMin = newX - tvWidth
//        var marginStart = 0
//
//        if (progressBar == binding.llTargetPriceCurrent){
//            if (progress <= 0.toFloat()){
//                binding.lineCurrentStart.visibility = View.VISIBLE
//                binding.lineCurrentCenter.visibility = View.INVISIBLE
//                binding.lineCurrentEnd.visibility = View.INVISIBLE
//            }else if (progress >= progressMax){
//                binding.lineCurrentStart.visibility = View.INVISIBLE
//                binding.lineCurrentCenter.visibility = View.INVISIBLE
//                binding.lineCurrentEnd.visibility = View.VISIBLE
//            }else{
//                binding.lineCurrentStart.visibility = View.INVISIBLE
//                binding.lineCurrentCenter.visibility = View.VISIBLE
//                binding.lineCurrentEnd.visibility = View.INVISIBLE
//            }
//        }else{
////            if (progress <= 0.toFloat()){
////                binding.lineTargetStart.visibility = View.VISIBLE
////                binding.lineTargetCenter.visibility = View.INVISIBLE
////                binding.lineTargetEnd.visibility = View.INVISIBLE
////            }else if (progress >= progressMax){
////                binding.lineTargetStart.visibility = View.INVISIBLE
////                binding.lineTargetCenter.visibility = View.INVISIBLE
////                binding.lineTargetEnd.visibility = View.VISIBLE
////            }else{
////                binding.lineTargetStart.visibility = View.INVISIBLE
////                binding.lineTargetCenter.visibility = View.VISIBLE
////                binding.lineTargetEnd.visibility = View.INVISIBLE
////            }
//        }
//
//        if (totHeightMax > progressBarWidth){
//            marginStart = (newX - (totHeightMax - progressBarWidth) - tvWidth - 1).toInt()
//        }else if (totHeightMin <= 0){
//            marginStart = 0
//        }else{
//            marginStart = (newX - tvWidth).toInt()
//        }
//
//        progressBar.post {
//            // Set posisi ImageView sesuai dengan newX
//            val params = layout.layoutParams as LayoutParams
//            params.marginStart = marginStart
//            layout.layoutParams = params
//        }
//    }

    private fun movingComponentProgress(progressBar: ProgressBar, layout: RelativeLayout, lineLayout: View, llWidth: Float, tvWidth: Float, progressPrice: Float, progressMax: Float){
        val progressBarWidth = progressBar.width.toFloat()    // Ambil nilai progress dan lebar ProgressBar setelah layout diukur
        val newX = progressPrice / progressMax * progressBarWidth

        val totHeightMax = llWidth + newX
        val totHeightMin = newX - llWidth
        var marginStart = 0
        var marginStartPoint = 0f

        if (totHeightMax >= progressBarWidth){
            marginStart = (progressBarWidth - (llWidth * 2)).roundToInt()
            if (progressPrice >= progressMax){
                marginStartPoint = (tvWidth * 1.9).toFloat()
            }else{
                marginStartPoint = newX - marginStart
            }
        }else if (totHeightMin <= 0){
            marginStart = 0
            marginStartPoint = 0f
        }else{
            marginStart = (newX - llWidth).toInt()
            marginStartPoint = tvWidth
        }

        progressBar.post {
            // Set posisi ImageView sesuai dengan newX
            val params = layout.layoutParams as LayoutParams
            params.marginStart = marginStart
            layout.layoutParams = params
        }

        val layoutParamsLine = lineLayout.layoutParams as? ViewGroup.MarginLayoutParams ?: return
        layoutParamsLine.marginStart = marginStartPoint.toInt()
        lineLayout.layoutParams = layoutParamsLine
    }

    private fun generateLineData(chartView: LineChart, itemList: ArrayList<Double>, itemLabel: List<String>, period: Int) {
        chartView.description.isEnabled = false
        chartView.setDrawGridBackground(false)
        chartView.setPinchZoom(false)
        chartView.setTouchEnabled(false)
        chartView.isDragEnabled = false
        chartView.setScaleEnabled(false)
        chartView.setNoDataText("No data yet!")
        chartView.setExtraOffsets(1f, 0f, 1f, 15f)
        chartView.legend.isEnabled = false

        // Setting Value Right of Chart
        val rightAxis: YAxis = chartView.axisRight
        rightAxis.setDrawAxisLine(false)
        rightAxis.setDrawGridLines(false)
        rightAxis.setDrawZeroLine(false)
        rightAxis.setStartAtZero(false)
        rightAxis.setDrawLimitLinesBehindData(false)
        rightAxis.textColor = ContextCompat.getColor(requireContext(), R.color.textGray)
//        rightAxis.valueFormatter = PercentFormatterChart()

        // Setting Value Left of Chart
        val leftAxis: YAxis = chartView.axisLeft
        leftAxis.isEnabled = false

        // Setting Value Bottom of Chart
        val xAxis: XAxis = chartView.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(true)
        xAxis.axisLineColor = ContextCompat.getColor(requireContext(), R.color.textGray)
        xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.textGray)
        xAxis.setCenterAxisLabels(true)
        xAxis.setDrawLabels(true)
//        xAxis.isGranularityEnabled = true
//        xAxis.setLabelCount(itemLabel.size, false)
        xAxis.valueFormatter = IndexAxisValueFormatter(itemLabel)

        if(period == 3){
            xAxis.granularity = (itemList.size / 3).toFloat()
        }else if(period == 5) {
            xAxis.granularity = (itemList.size / 4).toFloat()
        }else if(period == 10) {
            xAxis.granularity = (itemList.size / 5).toFloat()
        }

        val data = LineData()
        val entries = ArrayList<Entry>()
        val arrItemY = ArrayList<Float>()

        entries.clear()
        data.clearValues()
        rightAxis.removeAllLimitLines()

        for (index in 0 until itemList.size) {
            entries.add(Entry(index + 0.25f, itemList[index].toFloat()))
            arrItemY.add(itemList[index].toFloat())
        }

        val set = LineDataSet(entries, "")
        set.mode = LineDataSet.Mode.LINEAR
        set.lineWidth = 1.5f
        set.setDrawCircles(false)
        set.setDrawValues(false)
        set.color = ContextCompat.getColor(requireContext(), R.color.chartBlue)
        set.fillColor = ContextCompat.getColor(requireContext(), R.color.chartBlue)
        set.axisDependency = YAxis.AxisDependency.RIGHT
        data.addDataSet(set)

        var ratioForward = "0"
        var ratioPlus1Forward = "0"
        var ratioMin1Forward = "0"
        var ratioMeanForward = "0"
        var ratioPlus2Forward = "0"
        var ratioMin2Forward = "0"

        if (chartView == binding.lineChartPeBand){
            ratioForward = perForward ?: "0"
            ratioPlus1Forward = perPlus1Forward ?: "0"
            ratioMin1Forward = perMin1Forward ?: "0"
            ratioMeanForward = perMeanForward ?: "0"
            ratioPlus2Forward = perPlus2Forward ?: "0"
            ratioMin2Forward = perMin2Forward ?: "0"
        } else{
            ratioForward = pbvForward ?: "0"
            ratioPlus1Forward = pbvPlus1Forward ?: "0"
            ratioMin1Forward = pbvMin1Forward ?: "0"
            ratioMeanForward = pbvMeanForward ?: "0"
            ratioPlus2Forward = pbvPlus2Forward ?: "0"
            ratioMin2Forward = pbvMin2Forward ?: "0"
        }

        // FORWARD PE RATIO
        if (ratioForward != "0" && ratioForward.isNotEmpty()){
            try {
                val peRatioLine = LimitLine(ratioForward.toFloat(), ratioForward)
                peRatioLine.lineColor = ContextCompat.getColor(requireContext(), R.color.chartBlue)
                peRatioLine.textColor = ContextCompat.getColor(requireContext(), R.color.chartBlue)
                peRatioLine.lineWidth = 0.8f
                peRatioLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP // Adjust label position as needed
                peRatioLine.yOffset = 2.0f
                peRatioLine.xOffset = 0f
                peRatioLine.textSize = 10.0f
                chartView.axisRight.addLimitLine(peRatioLine)

                arrItemY.add(ratioForward.toFloat())
            }catch (e: Exception){
                Timber.e(e)
            }

        }

        // FORWARD +1 PE RATIO
        if (ratioPlus1Forward != "0" && ratioPlus1Forward.isNotEmpty()){
            try {
                val pePlus1RatioLine = LimitLine(ratioPlus1Forward.toFloat(), ratioPlus1Forward)
                pePlus1RatioLine.lineColor = ContextCompat.getColor(requireContext(), R.color.chartPurple)
                pePlus1RatioLine.textColor = ContextCompat.getColor(requireContext(), R.color.chartPurple)
                pePlus1RatioLine.lineWidth = 0.8f
                pePlus1RatioLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP // Adjust label position as needed
                pePlus1RatioLine.yOffset = 2.0f
                pePlus1RatioLine.xOffset = 0f
                pePlus1RatioLine.textSize = 10.0f
                chartView.axisRight.addLimitLine(pePlus1RatioLine)

                arrItemY.add(ratioPlus1Forward.toFloat())
            }catch (e: Exception){
                Timber.e(e)
            }
        }

        // FORWARD -1 PE RATIO
        if (ratioMin1Forward != "0" && ratioMin1Forward.isNotEmpty()){
            try {
                val peMin1RatioLine = LimitLine(ratioMin1Forward.toFloat(), ratioMin1Forward)
                peMin1RatioLine.lineColor = ContextCompat.getColor(requireContext(), R.color.chartOrange)
                peMin1RatioLine.textColor = ContextCompat.getColor(requireContext(), R.color.chartOrange)
                peMin1RatioLine.lineWidth = 0.8f
                peMin1RatioLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP // Adjust label position as needed
                peMin1RatioLine.yOffset = 2.0f
                peMin1RatioLine.xOffset = 0f
                peMin1RatioLine.textSize = 10.0f
                chartView.axisRight.addLimitLine(peMin1RatioLine)

                arrItemY.add(ratioMin1Forward.toFloat())
            }catch (e: Exception){
                Timber.e(e)
            }
        }

        // FORWARD MEAN PE RATIO
        if (ratioMeanForward != "0" && ratioMeanForward.isNotEmpty()){
            try {
                val peMeanRatioLine = LimitLine(ratioMeanForward.toFloat(), ratioMeanForward)
                peMeanRatioLine.lineColor = ContextCompat.getColor(requireContext(), R.color.chartGreenTwo)
                peMeanRatioLine.textColor = ContextCompat.getColor(requireContext(), R.color.chartGreenTwo)
                peMeanRatioLine.lineWidth = 0.8f
                peMeanRatioLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP // Adjust label position as needed
                peMeanRatioLine.yOffset = 2.0f
                peMeanRatioLine.xOffset = 0f
                peMeanRatioLine.textSize = 10.0f
                chartView.axisRight.addLimitLine(peMeanRatioLine)

                arrItemY.add(ratioMeanForward.toFloat())
            }catch (e: Exception){
                Timber.e(e)
            }
        }

        // FORWARD +2 PE RATIO
        if (ratioPlus2Forward != "0" && ratioPlus2Forward.isNotEmpty()){
            try {
                val pePlus2RatioLine = LimitLine(ratioPlus2Forward.toFloat(), ratioPlus2Forward)
                pePlus2RatioLine.lineColor = ContextCompat.getColor(requireContext(), R.color.chartRed)
                pePlus2RatioLine.textColor = ContextCompat.getColor(requireContext(), R.color.chartRed)
                pePlus2RatioLine.lineWidth = 0.8f
                pePlus2RatioLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP // Adjust label position as needed
                pePlus2RatioLine.yOffset = 2.0f
                pePlus2RatioLine.xOffset = 0f
                pePlus2RatioLine.textSize = 10.0f
                chartView.axisRight.addLimitLine(pePlus2RatioLine)

                arrItemY.add(ratioPlus2Forward.toFloat())
            }catch (e: Exception){
                Timber.e(e)
            }
        }

        // FORWARD -2 PE RATIO
        if (ratioMin2Forward != "0" && ratioMin2Forward.isNotEmpty()){
            try {
                val peMin2RatioLine = LimitLine(ratioMin2Forward.toFloat(), ratioMin2Forward)
                peMin2RatioLine.lineColor = ContextCompat.getColor(requireContext(), R.color.chartBlueTwo)
                peMin2RatioLine.textColor = ContextCompat.getColor(requireContext(), R.color.chartBlueTwo)
                peMin2RatioLine.lineWidth = 0.8f
                peMin2RatioLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP // Adjust label position as needed
                peMin2RatioLine.yOffset = 2.0f
                peMin2RatioLine.xOffset = 0f
                peMin2RatioLine.textSize = 10.0f
                chartView.axisRight.addLimitLine(peMin2RatioLine)

                arrItemY.add(ratioMin2Forward.toFloat())
            }catch (e: Exception){
                Timber.e(e)
            }
        }

        chartView.data = data
        chartView.animateXY(2000, 2000)
        chartView.invalidate()
    }

    class MyXAxisValueFormatter internal constructor(private val mValues: ArrayList<String>) : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            var index = Math.round(value)
            if (index >= mValues.size) {
                index = mValues.size - 1
            }
            return mValues[index]
        }
    }


    private fun countToHideLoading(){
        countHideLoading++
        if (countHideLoading == 6){
            hideLoading()
        }
    }

    private fun emptyPerBand(){
        countToHideLoading()
        binding.apply {
            tvFsdaPeBandForwardPeRatioDate.text = "-"
            tvFsdaPeBandForwardPeRatioValue.text = "-"
            tvFsdaPeBandPlus1ForwardPeRatioDate.text = "-"
            tvFsdaPeBandPlus1ForwardPeRatioValue.text = "-"
            tvFsdaPeBandMin1ForwardPeRatioDate.text = "-"
            tvFsdaPeBandMin1ForwardPeRatioValue.text = "-"
            tvFsdaPeBandMeanForwardPeRatioDate.text = "-"
            tvFsdaPeBandMeanForwardPeRatioValue.text = "-"
            tvFsdaPeBandPlus2ForwardPeRatioDate.text = "-"
            tvFsdaPeBandPlus2ForwardPeRatioValue.text = "-"
            tvFsdaPeBandMin2ForwardPeRatioDate.text = "-"
            tvFsdaPeBandMin2ForwardPeRatioValue.text = "-"
        }
    }

    private fun emptyPbvBand(){
        countToHideLoading()
        binding.apply {
            tvFsdaPvBandForwardPeRatioDate.text = "-"
            tvFsdaPvBandForwardPeRatioValue.text = "-"
            tvFsdaPvBandPlus1ForwardPeRatioDate.text = "-"
            tvFsdaPvBandPlus1ForwardPeRatioValue.text = "-"
            tvFsdaPvBandMin1ForwardPeRatioDate.text = "-"
            tvFsdaPvBandMin1ForwardPeRatioValue.text = "-"
            tvFsdaPvBandMeanForwardPeRatioDate.text = "-"
            tvFsdaPvBandMeanForwardPeRatioValue.text = "-"
            tvFsdaPvBandPlus2ForwardPeRatioDate.text = "-"
            tvFsdaPvBandPlus2ForwardPeRatioValue.text = "-"
            tvFsdaPvBandMin2ForwardPeRatioDate.text = "-"
            tvFsdaPvBandMin2ForwardPeRatioValue.text = "-"
        }
    }

    private fun noChart(chartView: LineChart){
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

    private fun emptyDataAnalysisRating(){
        countToHideLoading()
        binding.apply {
            clFsdaAnalystRating.visibility = View.GONE
            clFsdaTargetPrice.visibility = View.GONE

            tvFsdaTitleTargetPrice.text = "Target Price for $stockCode"
            tvFsdaAnalysisRatingPercent.text = "0%"
            tvFsdaAnalysisRatingPercentName.text = "-"

            tvFsdaProgressBarAnalysisRatingBuy.progress = 0
            tvFsdaProgressBarAnalysisRatingBuy.max = 100
            tvFsdaAnalysisRatingBuy.text = "0"

            tvFsdaProgressBarAnalysisRatingHold.progress = 0
            tvFsdaProgressBarAnalysisRatingHold.max = 100
            tvFsdaAnalysisRatingHold.text = "0"

            tvFsdaProgressBarAnalysisRatingSell.progress = 0
            tvFsdaProgressBarAnalysisRatingSell.max = 0
            tvFsdaAnalysisRatingSell.text = "0"

            tvFsdaTargetPriceLow.text = "Low 0"
            tvFsdaTargetPriceHigh.text = "High 0"
            tvTargetPrice.text = "Target \nRp0"
            tvCurrentPrice.text = "Current \nRp0"

            tvTargetPriceWidth = tvTargetPrice.width.toFloat() / 2
            tvCurrentPriceWidth = tvCurrentPrice.width.toFloat() / 2
            progressTarget = "0"
            progressMax = "0"

            val llTargetPriceWidth = binding.llTargetPriceTarget.width.toFloat() / 2
            val llCurrentPriceWidth = binding.llTargetPriceCurrent.width.toFloat() / 2

            movingComponentProgress(progressIndicatorTargetPrice, llTargetPriceTarget, lineTargetCenter, llTargetPriceWidth, tvTargetPriceWidth, 50.toFloat(), 100.toFloat())
            movingComponentProgress(progressIndicatorTargetPrice, llTargetPriceCurrent, lineCurrentCenter, llCurrentPriceWidth, tvCurrentPriceWidth, 50.toFloat(), 100.toFloat())

        }
    }

}