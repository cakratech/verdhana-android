package com.bcasekuritas.mybest.app.feature.global.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bcasekuritas.mybest.app.feature.global.rank.RankGlobalFragment
import com.bcasekuritas.mybest.app.feature.global.tabcommodities.TabCommoditiesFragment
import com.bcasekuritas.mybest.app.feature.global.tabcurrencies.TabCurrenciesFragment
import com.bcasekuritas.mybest.app.feature.global.tabindex.TabIndexFragment

class GlobalMarketViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val items = listOf(
        TabIndexFragment::class.java,
        TabCommoditiesFragment::class.java,
        TabCurrenciesFragment::class.java,
        RankGlobalFragment::class.java
    )

    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment = items[position].newInstance() as Fragment


}