package com.bcasekuritas.mybest.app.feature.e_ipo.eipodetail.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bcasekuritas.mybest.app.feature.e_ipo.eipodetail.about.EIPOAboutFragment
import com.bcasekuritas.mybest.app.feature.e_ipo.eipodetail.stages.EIPOStagesFragment

class EIPODetailPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val items = listOf(
        EIPOStagesFragment::class.java,
        EIPOAboutFragment::class.java
    )

    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment = items[position].newInstance() as Fragment


}