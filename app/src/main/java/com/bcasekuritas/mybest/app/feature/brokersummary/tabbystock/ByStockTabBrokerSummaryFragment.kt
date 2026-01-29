package com.bcasekuritas.mybest.app.feature.brokersummary.tabbystock

import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerRankByStockReq
import com.bcasekuritas.mybest.app.feature.brokersummary.tabbystock.adapter.ByStockTabBrokerSummaryAdapter
import com.bcasekuritas.mybest.databinding.FragmentTabByStockBrokerSummaryBinding
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
class ByStockTabBrokerSummaryFragment : BaseFragment<FragmentTabByStockBrokerSummaryBinding, ByStockTabBrokerSummaryViewModel>(), ShowDropDown by ShowDropDownImpl() {
    override val bindingVariable: Int = BR.vmActivityTabBrokerSummary
    override val viewModel: ByStockTabBrokerSummaryViewModel by viewModels()
    override val binding: FragmentTabByStockBrokerSummaryBinding by autoCleaned { (FragmentTabByStockBrokerSummaryBinding.inflate(layoutInflater)) }

    private val brokerSummaryAdapter: ByStockTabBrokerSummaryAdapter by autoCleaned { ByStockTabBrokerSummaryAdapter() }

    private val listBroker = arrayListOf("Domestic", "Foreign", "All")
    private val listBoard = arrayListOf("All", "RG", "NG", "TN")
    private val listSortType = arrayListOf("Total Value")
    private var stockCodeList = arrayListOf<String>()

    private var startDate: Long = 0
    private var endDate: Long = 0
    private var boardCode: String = "*"
    private var brokerType: Int = 2
    private var sortType: Int = 0
    private var userId = ""
    private var stockCode = "BBCA"
    private var sessionId = ""

    private var datePattern = "dd/MM/yyyy"
    private var isNetValue = false

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvBrokerSummary.apply {
            adapter = brokerSummaryAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            isNestedScrollingEnabled = false
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
                applyFilter(isNetValue)
            }
        }

        binding.tvToVal.setOnClickListener {
            initCalenderDialog(requireContext(), binding.tvToVal, 2, startDate, binding.tvToVal.text.toString()){
                endDate = setDateTo7PM(it)
                binding.tvToVal.error = null
                applyFilter(isNetValue)
            }
        }

        binding.tvStockVal.setOnClickListener {
            showDropDownStringSearchable(
                requireContext(),
                stockCodeList.sorted(),
                binding.viewDropdownStock,
                "Search Stock Code"
            ) { index, value ->
                stockCode = value
                binding.tvStockVal.text = value
                binding.tvStockVal.error = null
                applyFilter(isNetValue)
            }
        }

        binding.tvBrokerVal.setOnClickListener {
            showSimpleDropDownWidth80(requireContext(), listBroker, binding.tvBrokerVal){ index, value->
                binding.tvBrokerVal.text = value
                brokerType = index
                applyFilter(isNetValue)
            }
        }

        binding.tvTradeVal.setOnClickListener {
            showSimpleDropDownWidth80(requireContext(), listBoard, binding.tvTradeVal){ _, value->
                binding.tvTradeVal.text = value
                boardCode = if (value == "All") "*" else value
                applyFilter(isNetValue)
            }
        }

        binding.tvSortByVal.setOnClickListener {
            showSimpleDropDownWidth80(requireContext(), listSortType, binding.tvSortByVal){ index, value->
                binding.tvSortByVal.text = value
                sortType = index
                applyFilter(isNetValue)
            }
        }

        binding.checkboxNetValue.setOnCheckedChangeListener { _, isChecked ->
            isNetValue = isChecked
            applyFilter(isNetValue)
        }

        binding.tvApplyFilter.setOnClickListener {
            resetFilter()
        }
    }

    override fun setupListener() {
        super.setupListener()

        binding.rcvBrokerSummary.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                (recyclerView.layoutManager as? LinearLayoutManager)?.let { layoutManager ->
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                    if (visibleItemCount + pastVisibleItems >= totalItemCount && totalItemCount > 0) {
                        viewModel.loadNextPage()
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    private fun resetFilter() {
        brokerSummaryAdapter.clearData()
        endDate = getCurrentTimeInMillis()
        startDate = getYesterdayTimeInMillis()
        stockCode = "BBCA"
        boardCode = "*"
        brokerType = 2
        sortType = 0
        isNetValue = false
        binding.checkboxNetValue.isChecked = false

        binding.apply {
            tvFromVal.text = DateUtils.convertLongToDate(startDate, datePattern)
            tvToVal.text = DateUtils.convertLongToDate(endDate, datePattern)
            tvStockVal.text = stockCode
            tvTradeVal.text = "All"
            tvBrokerVal.text = "All"
            tvSortByVal.text = "Total Value"
        }

        applyFilter(isNetValue)
    }

    private fun applyFilter(isNet: Boolean) {
        brokerSummaryAdapter.clearData()
        val brokerRankByStockReq = BrokerRankByStockReq(userId, startDate, endDate, boardCode, stockCode, brokerType, sessionId, isNet)
        if (isNet) {
            viewModel.getBrokerStockNet(brokerRankByStockReq)
        } else {
            viewModel.getBrokerStockSum(brokerRankByStockReq)
        }
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        sessionId = prefManager.sessionId

        viewModel.getAllStockParam("")
        applyFilter(isNetValue)
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.getAllStockParamResult.observe(viewLifecycleOwner) {
            it?.map { data -> data?.let { stockCodeList.add(data.stockCode) } }
        }

        viewModel.getBrokerSumResult.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()){
                brokerSummaryAdapter.setData(it)
            } else {
                brokerSummaryAdapter.clearData()
            }
        }

        viewModel.getDataPage.observe(viewLifecycleOwner) {page ->
            if (page == 0) {
                brokerSummaryAdapter.clearData()
            }
        }
    }


}