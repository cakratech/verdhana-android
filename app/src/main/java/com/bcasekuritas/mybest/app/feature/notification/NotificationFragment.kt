package com.bcasekuritas.mybest.app.feature.notification

import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.feature.notification.adapter.NotificationPagerAdapter
import com.bcasekuritas.mybest.databinding.FragmentNotificationBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class NotificationFragment : BaseFragment<FragmentNotificationBinding, NotificationViewModel>() {

    override val bindingVariable: Int = BR.vmNotification
    override val viewModel: NotificationViewModel by viewModels()
    override val binding: FragmentNotificationBinding by autoCleaned { (FragmentNotificationBinding.inflate(layoutInflater)) }

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun setupViewPager() {
        super.setupViewPager()
        viewPager = binding.viewPagerNotification
        tabLayout = binding.tabLayoutNotification

        val adapter = NotificationPagerAdapter(this)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Transaction"
                1 -> "General"
                else -> ""
            }
        }.attach()
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            toolbarNotification.tvLayoutToolbarMasterTitle.text = "Notification"
            toolbarNotification.ivLayoutToolbarMasterIconLeft.setOnClickListener {
                onBackPressed()
            }
        }
    }

}