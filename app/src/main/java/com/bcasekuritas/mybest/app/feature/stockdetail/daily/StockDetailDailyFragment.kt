package com.bcasekuritas.mybest.app.feature.stockdetail.daily

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.feature.stockdetail.StockDetailSharedViewModel
import com.bcasekuritas.mybest.app.feature.stockdetail.adapter.StockDetailDailyOneAdapter
import com.bcasekuritas.mybest.app.feature.stockdetail.adapter.StockDetailDailyTwoAdapter
import com.bcasekuritas.mybest.databinding.FragmentStockDetailDailyBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.date.DateUtils.convertLongToDate
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import timber.log.Timber
import java.util.Calendar

@FragmentScoped
@AndroidEntryPoint
class StockDetailDailyFragment : BaseFragment<FragmentStockDetailDailyBinding, StockDetailDailyViewModel>() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmStockDetailDaily
    override val viewModel: StockDetailDailyViewModel by viewModels()
    override val binding: FragmentStockDetailDailyBinding by autoCleaned {
        FragmentStockDetailDailyBinding.inflate( layoutInflater )
    }

    lateinit var sharedViewModel: StockDetailSharedViewModel
    private var stockCode = ""

    private lateinit var linearLayoutManagerOne: LinearLayoutManager
    private lateinit var linearLayoutManagerTwo: LinearLayoutManager
    private val stockDetailDailyOneAdapter: StockDetailDailyOneAdapter by autoCleaned { StockDetailDailyOneAdapter() }
    private val stockDetailDailyTwoAdapter: StockDetailDailyTwoAdapter by autoCleaned { StockDetailDailyTwoAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(StockDetailSharedViewModel::class.java)
    }

    companion object {
        fun newInstance() = StockDetailDailyFragment()
    }

    override fun setupComponent() {
        super.setupComponent()
        showLoading()

        binding.apply {
            includeLyTableDailyOne.rcvTableDailyOne.viewTreeObserver.addOnGlobalLayoutListener {
                val maxHeight = 1500
                if (includeLyTableDailyOne.rcvTableDailyOne.height > maxHeight) {
                    includeLyTableDailyOne.rcvTableDailyOne.layoutParams.height = maxHeight
                    includeLyTableDailyOne.rcvTableDailyOne.requestLayout()
                }
            }
        }

        binding.apply {
            includeLyTableDailyTwo.rcvTableDailyTwo.viewTreeObserver.addOnGlobalLayoutListener {
                val maxHeight = 1500
                if (includeLyTableDailyTwo.rcvTableDailyTwo.height > maxHeight) {
                    includeLyTableDailyTwo.rcvTableDailyTwo.layoutParams.height = maxHeight
                    includeLyTableDailyTwo.rcvTableDailyTwo.requestLayout()
                }
            }

        }
    }

    override fun setupListener() {
        super.setupListener()

        var isSyncingScroll = false

        val leftScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!isSyncingScroll) {
                    isSyncingScroll = true
                    binding.includeLyTableDailyTwo.rcvTableDailyTwo.scrollBy(dx, dy) // Scroll the right RecyclerView

                    val visibleItemCount = linearLayoutManagerOne.childCount
                    val pastVisibleItem = linearLayoutManagerOne.findFirstVisibleItemPosition()
                    val total  = stockDetailDailyOneAdapter.itemCount

                    if (visibleItemCount + pastVisibleItem >= total) {
                        Timber.d("Trigger paging daily")
                        // Logic to execute when the bottom is reached for the first time
                        viewModel.loadNextPage()
                    }
                    isSyncingScroll = false
                }
            }
        }

        val rightScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!isSyncingScroll) {
                    isSyncingScroll = true
                    binding.includeLyTableDailyOne.rcvTableDailyOne.scrollBy(dx, dy) // Scroll the left RecyclerView

                    val visibleItemCount = linearLayoutManagerTwo.childCount
                    val pastVisibleItem = linearLayoutManagerTwo.findFirstVisibleItemPosition()
                    val total  = stockDetailDailyTwoAdapter.itemCount

                    if (visibleItemCount + pastVisibleItem >= total) {
                        Timber.d("Trigger paging daily")
                        // Logic to execute when the bottom is reached for the first time
                        viewModel.loadNextPage()
                    }
                    isSyncingScroll = false
                }
            }
        }

        // Add scroll listeners to both RecyclerViews
        binding.includeLyTableDailyOne.rcvTableDailyOne.addOnScrollListener(leftScrollListener)
        binding.includeLyTableDailyTwo.rcvTableDailyTwo.addOnScrollListener(rightScrollListener)
    }

    override fun setupAdapter() {
        super.setupAdapter()
        linearLayoutManagerOne = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.includeLyTableDailyOne.rcvTableDailyOne.setHasFixedSize(true)
        binding.includeLyTableDailyOne.rcvTableDailyOne.adapter = stockDetailDailyOneAdapter
        binding.includeLyTableDailyOne.rcvTableDailyOne.layoutManager = linearLayoutManagerOne

        linearLayoutManagerTwo = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.includeLyTableDailyTwo.rcvTableDailyTwo.setHasFixedSize(true)
        binding.includeLyTableDailyTwo.rcvTableDailyTwo.adapter = stockDetailDailyTwoAdapter
        binding.includeLyTableDailyTwo.rcvTableDailyTwo.layoutManager = linearLayoutManagerTwo
    }

    override fun initAPI() {
        super.initAPI()
        val cal = Calendar.getInstance()
        val endDate = cal.time
        cal.add(Calendar.YEAR, -1)
        val startDate = cal.time

        val userId = prefManager.userId
        val sessionId = prefManager.sessionId
        stockCode = prefManager.stockDetailCode

        viewModel.getTradeSummary(userId, sessionId, stockCode, startDate.time, endDate.time)

    }

    override fun setupObserver() {
        super.setupObserver()
        sharedViewModel.getStockCodeChangeResult.observe(viewLifecycleOwner) {
            if (it == true) {
                showLoading()
                initAPI()
            }
        }

        sharedViewModel.getRefreshFragmentResult.observe(viewLifecycleOwner) {
            if (it == true) {
                initAPI()
            }
        }

        viewModel.hideLoading.observe(viewLifecycleOwner) {
            hideLoading()
        }

        viewModel.getTradeSummaryResult.observe(viewLifecycleOwner) {
            hideLoading()
            if (!it.isNullOrEmpty()) {
                val listDate = it.map { item ->
                    convertLongToDate(item.tradeDate, "yyyy-MM-dd")
                }.toList()

                stockDetailDailyOneAdapter.setData(listDate) // Use copy
                stockDetailDailyTwoAdapter.setData(ArrayList(it)) // Use copy
            } else {
                stockDetailDailyOneAdapter.clearData()
                stockDetailDailyTwoAdapter.clearData()
            }
        }

        viewModel.getDataPage.observe(viewLifecycleOwner) { page ->
            if (page == 0) {
                stockDetailDailyOneAdapter.clearData()
                stockDetailDailyTwoAdapter.clearData()
            }
        }

    }
}