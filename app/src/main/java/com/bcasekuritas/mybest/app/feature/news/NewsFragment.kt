package com.bcasekuritas.mybest.app.feature.news

import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.feature.news.adapter.NewsPagerAdapter
import com.bcasekuritas.mybest.databinding.FragmentNewsBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.Args

@FragmentScoped
@AndroidEntryPoint
class NewsFragment : BaseFragment<FragmentNewsBinding, NewsViewModel>() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmNews
    override val viewModel: NewsViewModel by viewModels()
    override val binding: FragmentNewsBinding by autoCleaned {
        (FragmentNewsBinding.inflate(
            layoutInflater
        ))
    }

    private val sharedViewModel: NewsShareViewModel by lazy {
        ViewModelProvider(requireActivity()).get(NewsShareViewModel::class.java)
    }

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private var layoutPosition = 0

    override fun setupArguments() {
        super.setupArguments()
        arguments.let {
            if (it != null) {
                layoutPosition = it.getInt(Args.EXTRA_PARAM_INT_ONE)
            }
        }
    }

    override fun setupComponent() {
        super.setupComponent()

        binding.searchbar.setHint("Search keyword")
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

    override fun setupViewPager() {
        super.setupViewPager()
        viewPager = binding.viewPager
        tabLayout = binding.tabLayout

        val adapter = NewsPagerAdapter(this)
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "News"
                1 -> "Research"
                else -> ""
            }
        }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.searchbar.setText("")
                sharedViewModel.setOnChangeTab(true)
            }
        })

        if (layoutPosition != 0) {
            viewPager.setCurrentItem(layoutPosition, false)
        }
    }
}
