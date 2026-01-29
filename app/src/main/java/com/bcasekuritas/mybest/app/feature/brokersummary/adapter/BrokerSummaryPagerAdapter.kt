package com.bcasekuritas.mybest.app.feature.brokersummary.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bcasekuritas.mybest.app.feature.brokersummary.tabactivity.ActivityTabBrokerSummaryFragment
import com.bcasekuritas.mybest.app.feature.brokersummary.tabbystock.ByStockTabBrokerSummaryFragment
import com.bcasekuritas.mybest.app.feature.brokersummary.tabranking.RankingTabBrokerSummaryFragment

class BrokerSummaryPagerAdapter(fragment:Fragment): FragmentStateAdapter(fragment) {

    private val items = listOf(
        ByStockTabBrokerSummaryFragment::class.java,
        ActivityTabBrokerSummaryFragment::class.java,
        RankingTabBrokerSummaryFragment::class.java
    )
    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment = items[position].newInstance() as Fragment
}