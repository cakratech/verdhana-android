package com.bcasekuritas.mybest.app.feature.portfolio.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bcasekuritas.mybest.app.feature.portfolio.tabhistory.HistoryTabPortfolioFragment
import com.bcasekuritas.mybest.app.feature.portfolio.taborders.OrdersTabPortfolioFragment
import com.bcasekuritas.mybest.app.feature.portfolio.tabportfolio.PortfolioTabFragment

class PortfolioPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val items = listOf(
        PortfolioTabFragment::class.java,
        OrdersTabPortfolioFragment::class.java,
        HistoryTabPortfolioFragment::class.java
    )

    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment = items[position].newInstance() as Fragment


}