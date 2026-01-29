package com.bcasekuritas.mybest.app.feature.order.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bcasekuritas.mybest.app.feature.order.tabbuy.OrderBuyFragment
import com.bcasekuritas.mybest.app.feature.rdn.topup.atm.TransferAtmFragment
import com.bcasekuritas.mybest.app.feature.rdn.topup.branch.TransferBranchFragment
import com.bcasekuritas.mybest.app.feature.rdn.topup.internetbanking.TransferInternetBankingFragment
import com.bcasekuritas.mybest.app.feature.rdn.topup.mbanking.TransferMBankingFragment
import com.bcasekuritas.mybest.app.feature.order.tabsell.OrderSellFragment


private const val NUM_TABS = 2

class OrderViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {

        when (position) {
            0 -> return OrderBuyFragment()
            1 -> return OrderSellFragment()
        }
        return OrderBuyFragment()
    }
}