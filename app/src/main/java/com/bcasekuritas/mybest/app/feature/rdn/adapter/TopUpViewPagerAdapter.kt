package com.bcasekuritas.mybest.app.feature.rdn.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bcasekuritas.mybest.app.feature.rdn.topup.atm.TransferAtmFragment
import com.bcasekuritas.mybest.app.feature.rdn.topup.branch.TransferBranchFragment
import com.bcasekuritas.mybest.app.feature.rdn.topup.internetbanking.TransferInternetBankingFragment
import com.bcasekuritas.mybest.app.feature.rdn.topup.mbanking.TransferMBankingFragment

private const val NUM_TABS = 4

class TopUpViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return TransferMBankingFragment.newInstance()
            1 -> return TransferInternetBankingFragment()
            2 -> return TransferAtmFragment()
            3 -> return TransferBranchFragment()
        }
        return TransferMBankingFragment()
    }
}