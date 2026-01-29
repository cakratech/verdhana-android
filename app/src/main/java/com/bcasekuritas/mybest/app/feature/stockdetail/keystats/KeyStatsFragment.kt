package com.bcasekuritas.mybest.app.feature.stockdetail.keystats


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.stockdetail.StockDetailSharedViewModel
import com.bcasekuritas.mybest.databinding.FragmentKeyStatsBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.formatLastNumberStartFromMillion
import com.bcasekuritas.mybest.ext.common.initFormatThousandSeparator
import com.bcasekuritas.mybest.ext.date.DateUtils.convertStringToDateWithLastDate
import com.bcasekuritas.mybest.ext.date.DateUtils.convertStringToMonth
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl
import com.bcasekuritas.mybest.ext.other.RoundedHalfDown
import com.bcasekuritas.mybest.ext.other.formatPrice
import java.util.Locale

@FragmentScoped
@AndroidEntryPoint
class KeyStatsFragment : BaseFragment<FragmentKeyStatsBinding, KeyStatsViewModel>(), ShowDropDown by ShowDropDownImpl() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmKeyStats
    override val viewModel: KeyStatsViewModel by viewModels()
    override val binding: FragmentKeyStatsBinding by autoCleaned {
        FragmentKeyStatsBinding.inflate(
            layoutInflater
        )
    }

    lateinit var sharedViewModel: StockDetailSharedViewModel
//    private val listEarningPerShare = arrayListOf("Earning Per Share", "Cash Per Share", "Revenue Per Share")
    private val listEarningPerShare = arrayListOf("Earning Per Share")
    private var stockCode = ""
    private var countHideLoading = 0

    companion object {
        fun newInstance() = KeyStatsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(StockDetailSharedViewModel::class.java)
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.rlKeyStatesSelection.setOnClickListener {
            showDropDownEarningPerShare(requireContext(), listEarningPerShare, binding.rlKeyStatesSelection){index, value ->
                binding.tvKeyStatesSelection.text = value
            }
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        showLoading()
    }

    override fun initAPI() {
        super.initAPI()
        countHideLoading = 0

        val userId = prefManager.userId
        val sessionId = prefManager.sessionId
        stockCode = prefManager.stockDetailCode

//        viewModel.getKeyStat(userId, "", stockCode)
        viewModel.getKeyStatRti(userId, sessionId, stockCode)
        viewModel.getEarningPerShare(userId, "", stockCode)
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

        // KEY STATS
//        viewModel.getKeyStatResult.observe(viewLifecycleOwner){
//            when (it) {
//                is Resource.Loading -> {
//                    showLoading()
//                }
//
//                is Resource.Success -> {
//                    when (it.data?.status) {
//                        0.toString() -> {
//                            if (it.data != null) {
//                                it.data.let { res ->
//                                    binding.apply {
//                                        tvKeyStatesValuationCperAnnualised.text = initFormatThousandSeparator(res.data.currentPeRatioAnnualised)
//                                        tvKeyStatesValuationCperTtm.text = initFormatThousandSeparator(res.data.currentPeRatioTtm)
//                                        tvKeyStatesValuationFper.text = initFormatThousandSeparator(res.data.forwardPeRatio)
//                                        tvKeyStatesValuationCpsTtm.text = initFormatThousandSeparator(res.data.currentPriceToSalesTtm)
//                                        tvKeyStatesValuationCpbv.text = initFormatThousandSeparator(res.data.currentPriceToBookValue)
//                                        tvKeyStatesValuationCpcTtm.text = initFormatThousandSeparator(res.data.currentPriceToCashflowTtm)
//                                        tvKeyStatesValuationEvEbitdaTtm.text = initFormatThousandSeparator(res.data.evToEbitdaTtm)
//
//                                        tvKeyStatesPerShareEpsTtm.text = initFormatThousandSeparator(res.data.currentEpsTtm)
//                                        tvKeyStatesPerShareEpsAnnualised.text = initFormatThousandSeparator(res.data.currentEpsAnnualised)
//                                        tvKeyStatesPerShareRevenueTtm.text = initFormatThousandSeparator(res.data.revenuePerShareTtm)
//                                        tvKeyStatesPerShareCprQuarter.text = initFormatThousandSeparator(res.data.cashPerShareQuarter)
//                                        tvKeyStatesPerShareCbv.text = initFormatThousandSeparator(res.data.currentBookValuePerShare)
//                                        tvKeyStatesPerShareFcpsTtm.text = initFormatThousandSeparator(res.data.freeCashflowPershareTtm)
//
//                                        tvKeyStatesProfitabilityRoa.text = res.data.returnOnAssetsTtm.RoundedHalfDown()
//                                        tvKeyStatesProfitabilityRoe.text = "${(res.data.returnOnEquityTtm * 100).RoundedHalfDown()}%"
//                                        tvKeyStatesProfitabilityGpm.text = "${(res.data.grossProfitMarginQuarter * 100).RoundedHalfDown()}%"
//                                        tvKeyStatesProfitabilityOpm.text = "${(res.data.operatingProfitMarginQuarter * 100).RoundedHalfDown()}%"
//                                        tvKeyStatesProfitabilityNpm.text = "${(res.data.netProfitMarginQuarter * 100).RoundedHalfDown()}%"
//
//                                        tvKeyStatesDividendTtm.text = res.data.dividendTtm.RoundedHalfDown()
//                                        tvKeyStatesDividendPayoutRatio.text = "${(res.data.dividendPayoutRatio * 100).RoundedHalfDown()}%"
//                                        tvKeyStatesDividendYield.text = "${(res.data.dividendYield * 100).RoundedHalfDown()}%"
//                                        tvKeyStatesDividendExDate.text = convertLongToDate(res.data.latestDividendExDate)
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    countToHideLoading()
//                }
//
//                is Resource.Failure -> {
//                    countToHideLoading()
//                    Toast.makeText(requireContext(), it.failureData.message, Toast.LENGTH_SHORT).show()
//                }
//
//                else -> {
//                    countToHideLoading()
//                }
//            }
//        }

        // KEY STATS RTI
        viewModel.getKeyStatRtiResult.observe(viewLifecycleOwner){
            if (it != null){
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        when (it.data?.status) {
                            0.toString() -> {
                                if (it.data != null) {
                                    when (it.data.status) {
                                        "0" -> {
                                            it.data.let { res ->
                                                binding.apply {
                                                    val year = convertStringToDateWithLastDate(res.data.period).takeLast(4).toInt()
                                                    tvKeyStatesTablePeriod4.text = "$year (Rp)"
                                                    tvKeyStatesTablePeriod3.text = "${year - 1} (Rp)"
                                                    tvKeyStatesTablePeriod2.text = "${year - 2} (Rp)"
                                                    tvKeyStatesTablePeriod1.text = "${year - 3} (Rp)"

                                                    tvKsRtiMostRecentq.text = "Most Recent Quarter : ${convertStringToDateWithLastDate(res.data.period)}"
                                                    tvKsRtiMostRecentqFinancialYearEnd.text = convertStringToMonth(res.data.period, 3)
                                                    tvKsRtiMostRecentqIssuedShares.text = formatLastNumberStartFromMillion(res.data.issuesShares.toFloat()) //res.data.issuesShares.toString()
                                                    tvKsRtiMostRecentqMarketCap.text = formatLastNumberStartFromMillion(res.data.marketCap.toFloat()) //res.data.marketCap.toString()
                                                    tvKsRtiMostRecentqStockIndex.text = ""

                                                    tvKsRtiFundamentalSales.text = formatLastNumberStartFromMillion(res.data.sales.toFloat()) //"Rp${initRupiahFormatNumber(res.data.sales)}"
                                                    tvKsRtiFundamentalAssets.text = formatLastNumberStartFromMillion(res.data.assets.toFloat()) //"Rp${initRupiahFormatNumber(res.data.assets)}"
                                                    tvKsRtiFundamentalLiability.text = formatLastNumberStartFromMillion(res.data.liabilities.toFloat()) //"Rp${initRupiahFormatNumber(res.data.liabilities)}"
                                                    tvKsRtiFundamentalEquity.text = formatLastNumberStartFromMillion(res.data.equity.toFloat()) //"Rp${initRupiahFormatNumber(res.data.equity)}"
                                                    tvKsRtiFundamentalCashFlow.text = formatLastNumberStartFromMillion(res.data.cashFlow.toFloat()) //"Rp${initRupiahFormatNumber(res.data.cashFlow)}"
                                                    tvKsRtiFundamentalOperatingProfit.text = formatLastNumberStartFromMillion(res.data.operatingProfit.toFloat()) //"Rp${initRupiahFormatNumber(res.data.operatingProfit)}"
                                                    tvKsRtiFundamentalNetProfit.text = formatLastNumberStartFromMillion(res.data.netProfit.toFloat()) // "Rp${initRupiahFormatNumber(res.data.netProfit)}"

                                                    tvKsRtiEarningsDps.text = initFormatThousandSeparator(res.data.dividendPerShare)
                                                    tvKsRtiEarningsEps.text = initFormatThousandSeparator(res.data.earningPerShare)
                                                    tvKsRtiEarningsRps.text = initFormatThousandSeparator(res.data.revenuePerShareTtm)
                                                    tvKsRtiEarningsBvps.text = initFormatThousandSeparator(res.data.bookValuePerShare)
                                                    tvKsRtiEarningsCfps.text = initFormatThousandSeparator(res.data.cashFlowPerShare)
                                                    tvKsRtiEarningsCeps.text = initFormatThousandSeparator(res.data.cashEquivalentPerShare)
                                                    tvKsRtiEarningsNavs.text = initFormatThousandSeparator(res.data.netAssetsPerShare)

                                                    tvKsRtiValuationDividendYield.text = "${(res.data.dividendYield * 100).RoundedHalfDown()}%"
                                                    tvKsRtiValuationPer.text = "${res.data.priceEarningRatio.RoundedHalfDown()}"
                                                    tvKsRtiValuationPsr.text = "${res.data.priceSalesRatio.RoundedHalfDown()}"
                                                    tvKsRtiValuationPbvr.text = "${res.data.priceBookValueRatio.RoundedHalfDown()}"
                                                    tvKsRtiValuationPcfr.text = "${res.data.priceCashFlowRatio.RoundedHalfDown()}"

                                                    tvKsRtiProfitabilityDpr.text = "${(res.data.dividendPayoutRatio * 100).RoundedHalfDown()}%"
                                                    tvKsRtiProfitabilityGpm.text = "${(res.data.grossProfitMargin * 100).RoundedHalfDown()}%"
                                                    tvKsRtiProfitabilityOpm.text = "${(res.data.operatingProfitMargin * 100).RoundedHalfDown()}%"
                                                    tvKsRtiProfitabilityNpm.text = "${(res.data.netProfitMargin * 100).RoundedHalfDown()}%"
                                                    tvKsRtiProfitabilityEbitm.text = "${(res.data.earningsBeforeInterestAndTaxMargin * 100).RoundedHalfDown()}%"
                                                    tvKsRtiProfitabilityRoe.text = "${(res.data.returnOnEquity * 100).RoundedHalfDown()}%"
                                                    tvKsRtiProfitabilityRoa.text = "${(res.data.returnOnAssets * 100).RoundedHalfDown()}%"

                                                    tvKsRtiLiquidityDer.text = "${(res.data.debtEquityRatio * 100).RoundedHalfDown()}%"
                                                    tvKsRtiLiquidityCr.text = "${(res.data.cashRatio * 100).RoundedHalfDown()}%"
                                                    tvKsRtiLiquidityQr.text = "${(res.data.quickRatio * 100).RoundedHalfDown()}%"
                                                    tvKsRtiLiquidityCrr.text = "${(res.data.currentRatio * 100).RoundedHalfDown()}%"
                                                }
                                            }
                                            countToHideLoading()
                                        }
                                        "2" -> {
                                            (activity as MiddleActivity).showDialogSessionExpired()
                                            countToHideLoading()
                                        }
                                    }

                                }else{
                                    emptyDataKeyStatsRTI()
                                }
                            }
                            else -> emptyDataKeyStatsRTI()
                        }
                    }

                    is Resource.Failure -> {
                        emptyDataKeyStatsRTI()
                        Toast.makeText(requireContext(), it.failureData.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        emptyDataKeyStatsRTI()
                    }
                }
            }else{
                emptyDataKeyStatsRTI()
            }
        }

        viewModel.getEarningPerShareResult.observe(viewLifecycleOwner){
            if (it != null){
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        when (it.data?.status) {
                            0.toString() -> {
                                if (it.data != null) {
                                    it.data.let { res ->
                                        emptyDataTable()
                                        if (!res.dataList.isNullOrEmpty()) {
                                            res.dataList.forEach { i ->
                                                when(i.period.toLowerCase(Locale.ROOT)){
                                                    "q1(mar)" -> {
                                                        binding.tvKeyStatesTableQ1Col1.text = i.year1.formatPrice()
                                                        binding.tvKeyStatesTableQ1Col2.text = i.year2.formatPrice()
                                                        binding.tvKeyStatesTableQ1Col3.text = i.year3.formatPrice()
                                                        binding.tvKeyStatesTableQ1Col4.text = i.year4.formatPrice()
                                                    }

                                                    "q2(jun)" -> {
                                                        binding.tvKeyStatesTableQ2Col1.text = i.year1.formatPrice()
                                                        binding.tvKeyStatesTableQ2Col2.text = i.year2.formatPrice()
                                                        binding.tvKeyStatesTableQ2Col3.text = i.year3.formatPrice()
                                                        binding.tvKeyStatesTableQ2Col4.text = i.year4.formatPrice()
                                                    }

                                                    "q3(sep)" -> {
                                                        binding.tvKeyStatesTableQ3Col1.text = i.year1.formatPrice()
                                                        binding.tvKeyStatesTableQ3Col2.text = i.year2.formatPrice()
                                                        binding.tvKeyStatesTableQ3Col3.text = i.year3.formatPrice()
                                                        binding.tvKeyStatesTableQ3Col4.text = i.year4.formatPrice()
                                                    }

                                                    "q4(dec)" -> {
                                                        binding.tvKeyStatesTableQ4Col1.text = i.year1.formatPrice()
                                                        binding.tvKeyStatesTableQ4Col2.text = i.year2.formatPrice()
                                                        binding.tvKeyStatesTableQ4Col3.text = i.year3.formatPrice()
                                                        binding.tvKeyStatesTableQ4Col4.text = i.year4.formatPrice()
                                                    }

                                                    "eps" -> {
                                                        binding.tvKeyStatesTableEpsCol1.text = i.year1.formatPrice()
                                                        binding.tvKeyStatesTableEpsCol2.text = i.year2.formatPrice()
                                                        binding.tvKeyStatesTableEpsCol3.text = i.year3.formatPrice()
                                                        binding.tvKeyStatesTableEpsCol4.text = i.year4.formatPrice()
                                                    }

                                                    "dps" -> {
                                                        binding.tvKeyStatesTableDpsCol1.text = i.year1.formatPrice()
                                                        binding.tvKeyStatesTableDpsCol2.text = i.year2.formatPrice()
                                                        binding.tvKeyStatesTableDpsCol3.text = i.year3.formatPrice()
                                                        binding.tvKeyStatesTableDpsCol4.text = i.year4.formatPrice()
                                                    }

                                                    "dpr" -> {
                                                        binding.tvKeyStatesTableDprCol1.text = "${(i.year1 * 100).formatPrice()} %"
                                                        binding.tvKeyStatesTableDprCol2.text = "${(i.year2 * 100).formatPrice()} %"
                                                        binding.tvKeyStatesTableDprCol3.text = "${(i.year3 * 100).formatPrice()} %"
                                                        binding.tvKeyStatesTableDprCol4.text = "${(i.year4 * 100).formatPrice()} %"
                                                    }
                                                }
                                            }
                                            countToHideLoading()
                                        }
                                    }
                                }else{
                                    emptyDataTable()
                                }
                            }
                            else -> emptyDataTable()
                        }
                    }

                    is Resource.Failure -> {
                        emptyDataTable()
                        Toast.makeText(requireContext(), it.failureData.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        emptyDataTable()
                    }
                }
            }else{
                emptyDataTable()
            }
        }
    }

    private fun countToHideLoading(){
        countHideLoading++
        if (countHideLoading == 2){
            hideLoading()
        }
    }

    private fun emptyDataKeyStatsRTI(){
        countToHideLoading()
        binding.apply {
            tvKsRtiMostRecentq.text = "Most Recent Quarter :"
            tvKsRtiMostRecentqFinancialYearEnd.text = "-"
            tvKsRtiMostRecentqIssuedShares.text = "-"
            tvKsRtiMostRecentqMarketCap.text = "-"
            tvKsRtiMostRecentqStockIndex.text = "-"

            tvKsRtiFundamentalSales.text = "-"
            tvKsRtiFundamentalAssets.text = "-"
            tvKsRtiFundamentalLiability.text = "-"
            tvKsRtiFundamentalEquity.text = "-"
            tvKsRtiFundamentalCashFlow.text = "-"
            tvKsRtiFundamentalOperatingProfit.text = "-"
            tvKsRtiFundamentalNetProfit.text = "-"

            tvKsRtiEarningsDps.text = "-"
            tvKsRtiEarningsEps.text = "-"
            tvKsRtiEarningsRps.text = "-"
            tvKsRtiEarningsBvps.text = "-"
            tvKsRtiEarningsCfps.text = "-"
            tvKsRtiEarningsCeps.text = "-"
            tvKsRtiEarningsNavs.text = "-"

            tvKsRtiValuationDividendYield.text = "-"
            tvKsRtiValuationPer.text = "-"
            tvKsRtiValuationPsr.text = "-"
            tvKsRtiValuationPbvr.text = "-"
            tvKsRtiValuationPcfr.text = "-"

            tvKsRtiProfitabilityDpr.text = "-"
            tvKsRtiProfitabilityGpm.text = "-"
            tvKsRtiProfitabilityOpm.text = "-"
            tvKsRtiProfitabilityNpm.text = "-"
            tvKsRtiProfitabilityEbitm.text = "-"
            tvKsRtiProfitabilityRoe.text = "-"
            tvKsRtiProfitabilityRoa.text = "-"

            tvKsRtiLiquidityDer.text = "-"
            tvKsRtiLiquidityCr.text = "-"
            tvKsRtiLiquidityQr.text = "-"
            tvKsRtiLiquidityCrr.text = "-"
        }
    }

    private fun emptyDataTable(){
        countToHideLoading()
        binding.apply {
            binding.tvKeyStatesTableQ1Col1.text = "-"
            binding.tvKeyStatesTableQ1Col2.text = "-"
            binding.tvKeyStatesTableQ1Col3.text = "-"
            binding.tvKeyStatesTableQ1Col4.text = "-"

            binding.tvKeyStatesTableQ2Col1.text = "-"
            binding.tvKeyStatesTableQ2Col2.text = "-"
            binding.tvKeyStatesTableQ2Col3.text = "-"
            binding.tvKeyStatesTableQ2Col4.text = "-"

            binding.tvKeyStatesTableQ3Col1.text = "-"
            binding.tvKeyStatesTableQ3Col2.text = "-"
            binding.tvKeyStatesTableQ3Col3.text = "-"
            binding.tvKeyStatesTableQ3Col4.text = "-"

            binding.tvKeyStatesTableQ4Col1.text = "-"
            binding.tvKeyStatesTableQ4Col2.text = "-"
            binding.tvKeyStatesTableQ4Col3.text = "-"
            binding.tvKeyStatesTableQ4Col4.text = "-"

            binding.tvKeyStatesTableEpsCol1.text = "-"
            binding.tvKeyStatesTableEpsCol2.text = "-"
            binding.tvKeyStatesTableEpsCol3.text = "-"
            binding.tvKeyStatesTableEpsCol4.text = "-"

            binding.tvKeyStatesTableDpsCol1.text = "-"
            binding.tvKeyStatesTableDpsCol2.text = "-"
            binding.tvKeyStatesTableDpsCol3.text = "-"
            binding.tvKeyStatesTableDpsCol4.text = "-"

            binding.tvKeyStatesTableDprCol1.text = "-"
            binding.tvKeyStatesTableDprCol2.text = "-"
            binding.tvKeyStatesTableDprCol3.text = "-"
            binding.tvKeyStatesTableDprCol4.text = "-"
        }
    }

}
