package com.bcasekuritas.mybest.app.feature.brokersummary

import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.feature.brokersummary.adapter.BrokerSummaryPagerAdapter
import com.bcasekuritas.mybest.app.feature.portfolio.adapter.PortfolioPagerAdapter
import com.bcasekuritas.mybest.databinding.FragmentBrokerSummaryBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned

@FragmentScoped
@AndroidEntryPoint
class BrokerSummaryFragment: BaseFragment<FragmentBrokerSummaryBinding, BrokerSummaryViewModel>() {

    override val bindingVariable: Int = BR.vmBrokerSummary
    override val viewModel: BrokerSummaryViewModel by viewModels()
    override val binding: FragmentBrokerSummaryBinding by autoCleaned { (FragmentBrokerSummaryBinding.inflate(layoutInflater)) }

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            toolbar.tvLayoutToolbarMasterTitle.text = "Broker Summary"
            toolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener{
                onBackPressed()
            }
        }
    }

    override fun setupViewPager() {
        super.setupViewPager()
        viewPager = binding.viewPager
        tabLayout = binding.tabLayout

        val adapter = BrokerSummaryPagerAdapter(this)
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position+1) {
                1 -> "By Stock"
                2 -> "Activity"
                3 -> "Ranking"
                else -> {""}
            }
        }.attach()
    }

}