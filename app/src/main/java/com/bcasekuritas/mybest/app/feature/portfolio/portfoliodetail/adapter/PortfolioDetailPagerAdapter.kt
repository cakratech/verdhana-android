package com.bcasekuritas.mybest.app.feature.portfolio.portfoliodetail.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bcasekuritas.mybest.app.feature.portfolio.portfoliodetail.tabhistory.HistoryTabPortfolioDetailFragment
import com.bcasekuritas.mybest.app.feature.portfolio.portfoliodetail.tabportfolio.PortfolioTabPortfolioDetailFragment

class PortfolioDetailPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val items = listOf(
        PortfolioTabPortfolioDetailFragment::class.java,
        HistoryTabPortfolioDetailFragment::class.java
    )

    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment = items[position].newInstance()


}