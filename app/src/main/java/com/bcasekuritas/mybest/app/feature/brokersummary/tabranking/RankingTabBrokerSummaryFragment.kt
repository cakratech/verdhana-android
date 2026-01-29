package com.bcasekuritas.mybest.app.feature.brokersummary.tabranking

import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.feature.brokersummary.tabranking.adapter.RankingBrokerCodeTabBrokerSummaryAdapter
import com.bcasekuritas.mybest.app.feature.brokersummary.tabranking.adapter.RankingTabBrokerSummaryAdapter
import com.bcasekuritas.mybest.databinding.FragmentTabRankingBrokerSummaryBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.getCurrentTimeInMillis
import com.bcasekuritas.mybest.ext.common.getYesterdayTimeInMillis
import com.bcasekuritas.mybest.ext.common.initCalenderDialog
import com.bcasekuritas.mybest.ext.common.setDateTo7PM
import com.bcasekuritas.mybest.ext.common.setDateToMidnight
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class RankingTabBrokerSummaryFragment : BaseFragment<FragmentTabRankingBrokerSummaryBinding, RankingTabBrokerSummaryViewModel>(), ShowDropDown by ShowDropDownImpl() {

    override val bindingVariable: Int = BR.vmBrokerSummary
    override val viewModel: RankingTabBrokerSummaryViewModel by viewModels()
    override val binding: FragmentTabRankingBrokerSummaryBinding by autoCleaned { (FragmentTabRankingBrokerSummaryBinding.inflate(layoutInflater)) }

    private val brokerSummaryAdapter: RankingTabBrokerSummaryAdapter by autoCleaned { RankingTabBrokerSummaryAdapter() }
    private val brokerSummaryBrokerCodeAdapter: RankingBrokerCodeTabBrokerSummaryAdapter by autoCleaned { RankingBrokerCodeTabBrokerSummaryAdapter() }

    private val listSortType = arrayListOf("Value")

    private var userId = ""
    private var sessionId = ""
    private var startDate: Long = 0
    private var endDate: Long = 0
    private var sortType: Int = 0

    private var datePattern = "dd/MM/yyyy"

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvBrokerSummary.apply {
            adapter = brokerSummaryAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            isNestedScrollingEnabled = false
        }

        binding.rcvBrokerSummaryCode.apply {
            adapter = brokerSummaryBrokerCodeAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            isNestedScrollingEnabled = false
        }
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            horizontalScrollRow.setOnScrollChangeListener { v, scrollX, oldScrollX, scrollY, oldScrollY ->
                horizontalScrollRcv.scrollTo(scrollX, 0)
            }

            horizontalScrollRcv.setOnScrollChangeListener { v, scrollX, oldScrollX, scrollY, oldScrollY ->
                horizontalScrollRow.scrollTo(scrollX, 0)
            }

            binding.scrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                val totalScrollY = binding.scrollView.getChildAt(0).measuredHeight - binding.scrollView.height
                val isAtBottom = binding.scrollView.scrollY >= totalScrollY

                if (isAtBottom) {
                    // Logic to execute when the bottom is reached for the first time
                    viewModel.loadNextPage()
                }
            }
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        endDate = setDateTo7PM(getCurrentTimeInMillis())
        startDate = setDateToMidnight(getYesterdayTimeInMillis())

        binding.apply {
            tvFromVal.text = DateUtils.convertLongToDate(startDate, datePattern)
            tvToVal.text = DateUtils.convertLongToDate(endDate, datePattern)
        }
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.tvFilter.setOnClickListener {
            val icFilter = ContextCompat.getDrawable(requireContext(), R.drawable.ic_filter)
            val icArrowUp = ContextCompat.getDrawable(requireContext(), R.drawable.ic_row_up)
            val icArrowDown = ContextCompat.getDrawable(requireContext(), R.drawable.ic_bot_row)
            if (binding.expandBrokerSummaryFilter.isExpanded){
                binding.expandBrokerSummaryFilter.collapse()
                binding.tvFilter.setCompoundDrawablesRelativeWithIntrinsicBounds(icFilter, null, icArrowUp, null)
            }else {
                binding.expandBrokerSummaryFilter.expand()
                binding.tvFilter.setCompoundDrawablesRelativeWithIntrinsicBounds(icFilter, null, icArrowDown, null)
            }
        }

        binding.tvFromVal.setOnClickListener {
            initCalenderDialog(requireContext(), binding.tvFromVal, 2, null, binding.tvFromVal.text.toString()){
                startDate = setDateToMidnight(it)
                binding.tvFromVal.error = null
                applyFilter()
            }
        }

        binding.tvToVal.setOnClickListener {
            initCalenderDialog(requireContext(), binding.tvToVal, 2, startDate, binding.tvToVal.text.toString()){
                endDate = setDateTo7PM(it)
                binding.tvToVal.error = null
                applyFilter()
            }
        }

        binding.tvSortVal.setOnClickListener {
            showSimpleDropDownWidth80(requireContext(), listSortType, binding.tvSortVal){ index, value->
                binding.tvSortVal.text = value
//                sortType = when (value){
//                    "Value Ascending" -> 0
//                    "Value Descending" -> 4
//                    "Volume Ascending" -> 1
//                    "Volume Descending" -> 5
//                    "Freq Ascending" -> 2
//                    "Freq Descending" -> 6
//                    else -> 0
//                }
                applyFilter()
            }
        }

        binding.tvApplyFilter.setOnClickListener {
            resetFilter()
        }

    }

    private fun resetFilter() {
        endDate = getCurrentTimeInMillis()
        startDate = getYesterdayTimeInMillis()
        sortType = 0

        binding.apply {
            tvFromVal.text = DateUtils.convertLongToDate(startDate, datePattern)
            tvToVal.text = DateUtils.convertLongToDate(endDate, datePattern)
            binding.tvSortVal.text = "Value"
        }

        applyFilter()
    }

    private fun applyFilter() {
        brokerSummaryBrokerCodeAdapter.clearData()
        brokerSummaryAdapter.clearData()
        viewModel.getBrokerSummaryRanking(userId, sessionId, startDate, endDate, sortType)
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        sessionId = prefManager.sessionId
        applyFilter()
    }

    override fun setupObserver() {
        super.setupObserver()

        viewModel.getBrokerSummaryRankingResult.observe(viewLifecycleOwner){
            if (!it.isNullOrEmpty()) {
                val listBrokerCode = arrayListOf<String>()
                it.map {item ->
                    listBrokerCode.add(item.brokerCode)
                }

                brokerSummaryBrokerCodeAdapter.setData(listBrokerCode)
                brokerSummaryAdapter.setData(it)
            } else {
                brokerSummaryBrokerCodeAdapter.clearData()
                brokerSummaryAdapter.clearData()
            }
        }

        viewModel.getDataPage.observe(viewLifecycleOwner) {page ->
            if (page == 0) {
                brokerSummaryBrokerCodeAdapter.clearData()
                brokerSummaryAdapter.clearData()
            }
        }
    }

}