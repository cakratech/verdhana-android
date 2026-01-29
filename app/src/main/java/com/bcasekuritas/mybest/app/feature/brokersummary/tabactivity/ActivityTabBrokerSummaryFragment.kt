package com.bcasekuritas.mybest.app.feature.brokersummary.tabactivity

import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.feature.brokersummary.tabactivity.adapter.ActivityStockCodeTabBrokerSummaryAdapter
import com.bcasekuritas.mybest.app.feature.brokersummary.tabactivity.adapter.ActivityTabBrokerSummaryAdapter
import com.bcasekuritas.mybest.databinding.FragmentTabActivityBrokerSummaryBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.getCurrentTimeInMillis
import com.bcasekuritas.mybest.ext.common.getYesterdayTimeInMillis
import com.bcasekuritas.mybest.ext.common.initCalenderDialog
import com.bcasekuritas.mybest.ext.common.setDateTo7PM
import com.bcasekuritas.mybest.ext.common.setDateToMidnight
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl

@FragmentScoped
@AndroidEntryPoint
class ActivityTabBrokerSummaryFragment : BaseFragment<FragmentTabActivityBrokerSummaryBinding, ActivityTabBrokerSummaryViewModel>(), ShowDropDown by ShowDropDownImpl() {
    override val bindingVariable: Int = BR.vmActivityTabBrokerSummary
    override val viewModel: ActivityTabBrokerSummaryViewModel by viewModels()
    override val binding: FragmentTabActivityBrokerSummaryBinding by autoCleaned { (FragmentTabActivityBrokerSummaryBinding.inflate(layoutInflater)) }

    private val brokerSummaryAdapter: ActivityTabBrokerSummaryAdapter by autoCleaned { ActivityTabBrokerSummaryAdapter() }
    private val brokerSummaryStockCodeAdapter: ActivityStockCodeTabBrokerSummaryAdapter by autoCleaned { ActivityStockCodeTabBrokerSummaryAdapter() }

    private val listBoard = arrayListOf( "All", "RG", "NG", "TN")
    private var brokerList = arrayListOf<String>()

    private var startDate: Long = 0
    private var endDate: Long = 0
    private var boardCode: String = "*"
    private var brokerCode: String = "SQ"
    private var userId = ""
    private var sessionId = ""

    private var datePattern = "dd/MM/yyyy"

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvBrokerSummary.apply {
            adapter = brokerSummaryAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            isNestedScrollingEnabled = false
        }

        binding.rcvBrokerSummaryCode.apply {
            adapter = brokerSummaryStockCodeAdapter
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

        binding.tvBrokerVal.setOnClickListener {
            showDropDownStringSearchable(
                requireContext(),
                brokerList.sorted(),
                binding.viewDropdownStock,
                "Search Broker Code"
            ) { index, value ->
                brokerCode = value
                binding.tvBrokerVal.text = value
                binding.tvBrokerVal.error = null
                applyFilter()
            }
        }

        binding.tvTradeVal.setOnClickListener {
            showSimpleDropDownWidth80(requireContext(), listBoard, binding.tvTradeVal){ _, value->
                binding.tvTradeVal.text = value
                boardCode = if (value == "All") "*" else value
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
        brokerCode = "SQ"
        boardCode = "*"

        binding.apply {
            tvFromVal.text = DateUtils.convertLongToDate(startDate, datePattern)
            tvToVal.text = DateUtils.convertLongToDate(endDate, datePattern)
            tvTradeVal.text = "All"
            tvBrokerVal.text = brokerCode
        }

        applyFilter()
    }

    private fun applyFilter() {
        brokerSummaryStockCodeAdapter.clearData()
        brokerSummaryAdapter.clearData()

        viewModel.getBrokerRankActivity(userId, sessionId, brokerCode, boardCode, startDate, endDate)
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        sessionId = prefManager.sessionId

        viewModel.getBrokerList(userId)
        applyFilter()
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.getBrokerListResult.observe(viewLifecycleOwner){
            if (!it.isNullOrEmpty()) {
                brokerList.addAll(it)
            }
        }

        viewModel.getBrokerRankActivityResult.observe(viewLifecycleOwner){
            if (!it.isNullOrEmpty()) {
                val listStock = arrayListOf<String>()
                it.map {item ->
                    listStock.add( item.stockCode)
                }

                brokerSummaryStockCodeAdapter.setData(listStock)
                brokerSummaryAdapter.setData(it)
            } else {
                brokerSummaryStockCodeAdapter.clearData()
                brokerSummaryAdapter.clearData()
            }
        }

        viewModel.getDataPage.observe(viewLifecycleOwner) {page ->
            if (page == 0) {
                brokerSummaryAdapter.clearData()
                brokerSummaryStockCodeAdapter.clearData()
            }
        }
    }


}