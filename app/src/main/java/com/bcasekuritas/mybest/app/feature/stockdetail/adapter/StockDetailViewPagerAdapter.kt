package com.bcasekuritas.mybest.app.feature.stockdetail.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bcasekuritas.mybest.app.feature.stockdetail.about.StockDetailAboutFragment
import com.bcasekuritas.mybest.app.feature.stockdetail.analysis.StockDetailAnalysisFragment
import com.bcasekuritas.mybest.app.feature.stockdetail.brokersummary.StockDetailBrokerSummaryFragment
import com.bcasekuritas.mybest.app.feature.stockdetail.corporateaction.StockDetailCorporateActionFragment
import com.bcasekuritas.mybest.app.feature.stockdetail.daily.StockDetailDailyFragment
import com.bcasekuritas.mybest.app.feature.stockdetail.financial.FinancialFragment
import com.bcasekuritas.mybest.app.feature.stockdetail.keystats.KeyStatsFragment
import com.bcasekuritas.mybest.app.feature.stockdetail.newsandreports.NewsAndReportsFragment
import com.bcasekuritas.mybest.app.feature.stockdetail.orderbook.OrderBookFragment
import com.bcasekuritas.mybest.app.feature.stockdetail.runningtrade.StockDetailRunningTradeFragment
import com.bcasekuritas.mybest.app.feature.stockdetail.trade.TradeFragment


private const val NUM_TABS = 11

class StockDetailViewPagerAdapter(fragment: Fragment, stockCode: String) : FragmentStateAdapter(fragment) {

    val secCode = stockCode

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return OrderBookFragment()
            1 -> return KeyStatsFragment()
            2 -> return TradeFragment()
            3 -> return FinancialFragment()
            4 -> return StockDetailAnalysisFragment()
            5 -> return StockDetailBrokerSummaryFragment()
            6 -> return StockDetailDailyFragment()
            7 -> return StockDetailRunningTradeFragment()
            8 -> return NewsAndReportsFragment()
            9 -> return StockDetailCorporateActionFragment()
            10 -> return StockDetailAboutFragment()
        }
        return OrderBookFragment()
    }
}