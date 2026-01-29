package com.bcasekuritas.mybest.app.feature.categories

import android.view.View
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.categories.adapter.CategoriesAdapter
import com.bcasekuritas.mybest.databinding.FragmentCategoriesBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.listener.OnClickStr

@FragmentScoped
@AndroidEntryPoint
class CategoriesFragment : BaseFragment<FragmentCategoriesBinding, CategoriesViewModel>(), OnClickStr {

    override val viewModel: CategoriesViewModel by viewModels()
    override val binding: FragmentCategoriesBinding by autoCleaned { (FragmentCategoriesBinding.inflate(layoutInflater)) }
    override val bindingVariable: Int = BR.vmCategories

    private val categoriesAdapter: CategoriesAdapter by autoCleaned { CategoriesAdapter(prefManager.urlIcon,this) }

    private lateinit var linearLayoutManager: LinearLayoutManager

    private var userId = ""
    private var sessionId = ""
    private var isFilterExpanded = false
    private var chipSelection = 0

    private var sortAscending = 1
    private var sortType = 1

    private var isPageLoading = false

    override fun setupAdapter() {
        super.setupAdapter()
        linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvCategories.apply {
            adapter = categoriesAdapter
            layoutManager = linearLayoutManager
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }

    override fun setupComponent() {
        super.setupComponent()

        binding.lyToolbar.ivLayoutToolbarMasterIconLeft.isGone = false
        binding.lyToolbar.ivLayoutToolbarMasterIconLeft.setImageResource(R.drawable.ic_back)
        binding.lyToolbar.tvLayoutToolbarMasterTitle.text = "Categories"
    }

    override fun setupListener() {
        super.setupListener()
        binding.rcvCategories.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount = linearLayoutManager.childCount
                val pastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition()
                val total  = categoriesAdapter.itemCount

                if (!isPageLoading) {
                    if ((visibleItemCount + pastVisibleItem) >= total) {
                        viewModel.loadNextPage()
                        isPageLoading = true
                    }
                }


                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    override fun initOnClick() {
        super.initOnClick()
        chipOnClick()
        binding.apply {
            btnExpanded.setOnClickListener {
                isFilterExpanded = false
                filterExpanded.visibility = View.GONE
                filterNotExpand.visibility = View.VISIBLE
                chipSelection()
            }

            btnNotExpanded.setOnClickListener {
                isFilterExpanded = true
                filterNotExpand.visibility = View.GONE
                filterExpanded.visibility = View.VISIBLE
                chipSelection()
            }

            lyToolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener {
                onBackPressed()
            }
        }
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        sessionId = prefManager.sessionId

        viewModel.getCategoriesData(userId, sessionId, sortAscending, sortType)
    }

    override fun onResume() {
        super.onResume()
        viewModel.setListenerTradeSummary()
        viewModel.resumeSubscribe()
    }

    override fun onPause() {
        super.onPause()
        viewModel.unSubscribeTradeSummary(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.unSubscribeTradeSummary(true)
    }

    override fun setupObserver() {
        super.setupObserver()

        viewModel.getCategoriesDataResult.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                categoriesAdapter.setDataPaging(it)
            }
            isPageLoading = false
        }

        viewModel.getCategoriesDataSubscribeResult.observe(viewLifecycleOwner) { item ->
            if (item != null) {
                val itemIndex = categoriesAdapter.getDataList().indexOfFirst { it.stockCode == item.stockCode }

                if (itemIndex != -1) {
                    if (!isPageLoading) {
                        categoriesAdapter.updateData(itemIndex, item)
                    }
                }
            }
        }
    }

    override fun onClickStr(value: String?) {
        if (value != null) {
            MiddleActivity.startIntentParam(requireActivity(), NavKeys.KEY_FM_STOCK_DETAIL, value, "")
        }
    }

    private fun chipSelection() {
        binding.apply {
            when (chipSelection) {
                2 -> {
                    chipExpandTopGainer.isChecked = true
                    chipNotExpandedTopGainer.isChecked = true
                }
                3 -> {
                    chipExpandTopLosers.isChecked = true
                    chipNotExpandedTopLosers.isChecked = true

                }
                4 -> {
                    chipExpandTopGainerPct.isChecked = true
                    chipNotExpandedTopGainerPct.isChecked = true

                }
                5 -> {
                    chipExpandTopLosersPct.isChecked = true
                    chipNotExpandedTopLosersPct.isChecked = true

                }
                6 -> {
                    chipExpandTopVolume.isChecked = true
                    chipNotExpandedTopVolume.isChecked = true

                }
                7 -> {
                    chipExpandTopValue.isChecked = true
                    chipNotExpandedTopValue.isChecked = true

                }
                8 -> {
                    chipExpandTopFreq.isChecked = true
                    chipNotExpandedTopFreq.isChecked = true

                }
            }
        }
    }

    private fun resetPaging() {
        categoriesAdapter.clearData()
        viewModel.unSubscribeTradeSummary(false)
    }

    private fun chipOnClick() {
        binding.apply {
            chipExpandTopGainer.setOnClickListener {
                chipSelection = 2
                resetPaging()
                sortAscending = 1
                sortType = 1
                viewModel.getCategoriesData(userId, sessionId, 1, 1)
            }

            chipExpandTopLosers.setOnClickListener {
                chipSelection = 3
                resetPaging()
                sortAscending = 0
                sortType = 1
                viewModel.getCategoriesData(userId, sessionId, 0, 1)
            }

            chipExpandTopGainerPct.setOnClickListener {
                chipSelection = 4
                resetPaging()
                sortAscending = 1
                sortType = 2
                viewModel.getCategoriesData(userId, sessionId, 1, 2)
            }

            chipExpandTopLosersPct.setOnClickListener {
                chipSelection = 5
                resetPaging()
                sortAscending = 0
                sortType = 2
                viewModel.getCategoriesData(userId, sessionId, 0, 2)
            }

            chipExpandTopVolume.setOnClickListener {
                chipSelection = 6
                resetPaging()
                sortAscending = 1
                sortType = 4
                viewModel.getCategoriesData(userId, sessionId, 1, 4)
            }

            chipExpandTopValue.setOnClickListener {
                chipSelection = 7
                resetPaging()
                sortAscending = 1
                sortType = 3
                viewModel.getCategoriesData(userId, sessionId, 1, 3)
            }

            chipExpandTopFreq.setOnClickListener {
                chipSelection = 8
                resetPaging()
                sortAscending = 1
                sortType = 5
                viewModel.getCategoriesData(userId, sessionId, 1, 5)
            }

            chipNotExpandedTopGainer.setOnClickListener {
                chipSelection = 2
                resetPaging()
                sortAscending = 1
                sortType = 1
                viewModel.getCategoriesData(userId, sessionId, 1, 1)
            }

            chipNotExpandedTopLosers.setOnClickListener {
                chipSelection = 3
                resetPaging()
                sortAscending = 0
                sortType = 1
                viewModel.getCategoriesData(userId, sessionId, 0, 1)
            }

            chipNotExpandedTopGainerPct.setOnClickListener {
                chipSelection = 4
                resetPaging()
                sortAscending = 1
                sortType = 2
                viewModel.getCategoriesData(userId, sessionId, 1, 2)
            }

            chipNotExpandedTopLosersPct.setOnClickListener {
                chipSelection = 5
                resetPaging()
                sortAscending = 0
                sortType = 2
                viewModel.getCategoriesData(userId, sessionId, 0, 2)
            }

            chipNotExpandedTopVolume.setOnClickListener {
                chipSelection = 6
                resetPaging()
                sortAscending = 1
                sortType = 4
                viewModel.getCategoriesData(userId, sessionId, 1, 4)
            }

            chipNotExpandedTopValue.setOnClickListener {
                chipSelection = 7
                resetPaging()
                sortAscending = 1
                sortType = 3
                viewModel.getCategoriesData(userId, sessionId, 1, 3)
            }
            chipNotExpandedTopFreq.setOnClickListener {
                chipSelection = 8
                resetPaging()
                sortAscending = 1
                sortType = 5
                viewModel.getCategoriesData(userId, sessionId, 1, 5)
            }

        }
    }
}