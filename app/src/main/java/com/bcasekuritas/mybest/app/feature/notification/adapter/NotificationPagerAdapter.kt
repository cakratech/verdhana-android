package com.bcasekuritas.mybest.app.feature.notification.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bcasekuritas.mybest.app.feature.news.tabnews.TabNewsFragment
import com.bcasekuritas.mybest.app.feature.news.tabresearch.TabResearchFragment
import com.bcasekuritas.mybest.app.feature.notification.generalnotification.GeneralNotificationFragment
import com.bcasekuritas.mybest.app.feature.notification.transactionnotification.TransactionNotificationFragment
import com.bcasekuritas.mybest.app.feature.portfolio.tabhistory.HistoryTabPortfolioFragment
import com.bcasekuritas.mybest.app.feature.portfolio.taborders.OrdersTabPortfolioFragment
import com.bcasekuritas.mybest.app.feature.portfolio.tabportfolio.PortfolioTabFragment

class NotificationPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val items = listOf(
        TransactionNotificationFragment::class.java,
        GeneralNotificationFragment::class.java
    )

    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment = items[position].newInstance() as Fragment


}