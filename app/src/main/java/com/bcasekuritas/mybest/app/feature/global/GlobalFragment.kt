package com.bcasekuritas.mybest.app.feature.global

import android.text.Editable
import android.text.TextWatcher
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.feature.global.adapter.GlobalMarketViewPagerAdapter
import com.bcasekuritas.mybest.databinding.FragmentGlobalBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
@FragmentScoped
@AndroidEntryPoint
class GlobalFragment : BaseFragment<FragmentGlobalBinding, GlobalViewModel>() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmGlobal
    override val viewModel: GlobalViewModel by viewModels()
    override val binding: FragmentGlobalBinding by autoCleaned { (FragmentGlobalBinding.inflate(layoutInflater)) }

    private val sharedViewModel: GlobalSharedViewModel by lazy {
        ViewModelProvider(requireActivity()).get(GlobalSharedViewModel::class.java)
    }

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private var layoutPosition = 0

    companion object {
        fun newInstance() = GlobalFragment()
    }

    override fun setupViewPager() {
        super.setupViewPager()
        viewPager = binding.vpTabGlobalMarket
        viewPager.setOffscreenPageLimit(4);

        tabLayout = binding.tabLayoutGlobalMarket

        val adapter = GlobalMarketViewPagerAdapter(this)
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false
        TabLayoutMediator(tabLayout, viewPager, true, false) { tab, position ->
            tab.text = when (position+1) {
                1 -> "Index"
                2 -> "Commodities"
                3 -> "Currencies"
                4 -> "Rank"
                else -> {""}
            }
        }.attach()
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.lyToolbarProfileGlobal.ivLayoutToolbarMasterIconLeft.isGone = false
        binding.lyToolbarProfileGlobal.ivLayoutToolbarMasterIconLeft.setImageResource(R.drawable.ic_back)
        binding.lyToolbarProfileGlobal.tvLayoutToolbarMasterTitle.text = "Global"

        binding.searchbar.setHint("Search code or name")
    }

    override fun initOnClick() {
        super.initOnClick()


        binding.lyToolbarProfileGlobal.ivLayoutToolbarMasterIconLeft.setOnClickListener {
            onBackPressed()
        }

    }

    override fun setupListener() {
        super.setupListener()
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // Do something before text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do something when text changes
            }

            override fun afterTextChanged(s: Editable?) {
                sharedViewModel.setQuery(binding.searchbar.getText())
            }
        }

        binding.searchbar.setTextWatcher(searchTextWatcher)
    }


}