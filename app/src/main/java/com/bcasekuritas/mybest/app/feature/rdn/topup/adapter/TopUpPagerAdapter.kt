package com.bcasekuritas.mybest.app.feature.rdn.topup.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bcasekuritas.mybest.app.feature.news.tabnews.TabNewsFragment
import com.bcasekuritas.mybest.app.feature.news.tabresearch.TabResearchFragment
import com.bcasekuritas.mybest.app.feature.rdn.topup.atm.TransferAtmFragment
import com.bcasekuritas.mybest.app.feature.rdn.topup.mbanking.TransferMBankingFragment

class TopUpPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val items = listOf(
        TransferMBankingFragment::class.java,
        TransferAtmFragment::class.java
    )

    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment = items[position].newInstance() as Fragment


}