package com.bcasekuritas.mybest.app.feature.stockdetail.brokersummary


import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerRankByStockReq
import com.bcasekuritas.mybest.app.feature.stockdetail.StockDetailSharedViewModel
import com.bcasekuritas.mybest.app.feature.stockdetail.adapter.StockDetailBrokerSummaryAdapter
import com.bcasekuritas.mybest.databinding.FragmentStockDetailBrokerSummaryBinding
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
class StockDetailBrokerSummaryFragment : BaseFragment<FragmentStockDetailBrokerSummaryBinding, StockDetailBrokerSummaryViewModel>(), ShowDropDown by ShowDropDownImpl() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmStockDetailBrokerSummary
    override val viewModel: StockDetailBrokerSummaryViewModel by viewModels()
    override val binding: FragmentStockDetailBrokerSummaryBinding by autoCleaned {
        FragmentStockDetailBrokerSummaryBinding.inflate(
            layoutInflater
        )
    }

    lateinit var sharedViewModel: StockDetailSharedViewModel
    private val brokerSummaryAdapter: StockDetailBrokerSummaryAdapter by autoCleaned { StockDetailBrokerSummaryAdapter() }
    private var startDate: Long = 0
    private var endDate: Long = 0
    private var boardCode: String = "*"
    private var brokerType: Int = 2
    private var sortType: Int = 0
    private var userId = ""
    private var stockCode = ""
    private var sessionId = ""

    private val listBroker = arrayListOf("Domestic", "Foreign", "All")
    private val listBoard = arrayListOf("All", "RG", "NG", "TN")
    private val listSortType = arrayListOf("Value")

    private var datePattern = "dd/MM/yyyy"
    private var isNetValue = false


    companion object {
        fun newInstance() = StockDetailBrokerSummaryFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(StockDetailSharedViewModel::class.java)
    }

    override fun setupComponent() {
        super.setupComponent()

        endDate = setDateTo7PM(getCurrentTimeInMillis())
        startDate = setDateToMidnight(getYesterdayTimeInMillis())

        binding.tvFromVal.text = DateUtils.convertLongToDate(startDate, datePattern)
        binding.tvToVal.text = DateUtils.convertLongToDate(endDate, datePattern)

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
               applyFilter(isNetValue)
           }
        }

        binding.tvToVal.setOnClickListener {
            initCalenderDialog(requireContext(), binding.tvToVal, 2, startDate, binding.tvToVal.text.toString()){
                endDate = setDateTo7PM(it)
                applyFilter(isNetValue)
            }
        }

        binding.tvBrokerSummaryBroker.setOnClickListener {
            showSimpleDropDownWidth80(requireContext(), listBroker, binding.tvBrokerSummaryBroker){ index, value->
                binding.tvBrokerSummaryBroker.text = value
                brokerType = index
                applyFilter(isNetValue)
            }
        }

        binding.tvBrokerSummaryTrade.setOnClickListener {
            showSimpleDropDownWidth80(requireContext(), listBoard, binding.tvBrokerSummaryTrade){ _, value->
                binding.tvBrokerSummaryTrade.text = value
                boardCode = if (value == "All") "*" else value
                applyFilter(isNetValue)
            }
        }

        binding.tvBrokerSummarySortBy.setOnClickListener {
            showSimpleDropDownWidth80(requireContext(), listSortType, binding.tvBrokerSummarySortBy){ index, value->
                binding.tvBrokerSummarySortBy.text = value
                sortType = index
                applyFilter(isNetValue)
            }
        }

        binding.tvResetFilter.setOnClickListener {
            resetFilter()
        }
    }

    private fun resetFilter() {
        endDate = getCurrentTimeInMillis()
        startDate = getYesterdayTimeInMillis()
        boardCode = "*"
        brokerType = 2
        sortType = 0
        isNetValue = false
        binding.checkboxNetValue.isChecked = false

        binding.apply {
            tvFromVal.text = DateUtils.convertLongToDate(startDate, datePattern)
            tvToVal.text = DateUtils.convertLongToDate(endDate, datePattern)
            tvBrokerSummaryTrade.text = "All"
            tvBrokerSummaryBroker.text = "All"
            tvBrokerSummarySortBy.text = "Value"
        }

        applyFilter(isNetValue)
    }

    private fun applyFilter(isNet: Boolean){
        brokerSummaryAdapter.clearData()
        val brokerRankByStockReq = BrokerRankByStockReq(userId, startDate, endDate, boardCode, stockCode, brokerType, sessionId)
        if (isNet) {
            viewModel.getBrokerStockNet(brokerRankByStockReq)
        } else {
            viewModel.getBrokerStockSum(brokerRankByStockReq)
        }
    }

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvSdBrokerSummary.adapter = brokerSummaryAdapter
    }

    override fun setupListener() {
        super.setupListener()
        binding.checkboxNetValue.setOnCheckedChangeListener { _, isChecked ->
            isNetValue = isChecked
            applyFilter(isNetValue)
        }

        binding.rcvSdBrokerSummary.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    override fun initAPI() {
        super.initAPI()

        userId = prefManager.userId
        sessionId = prefManager.sessionId
        stockCode = prefManager.stockDetailCode

        applyFilter(isNetValue)
    }

    override fun setupObserver() {
        super.setupObserver()

        sharedViewModel.getStockCodeChangeResult.observe(viewLifecycleOwner){
            if (it == true){
                stockCode = prefManager.stockDetailCode
                resetFilter()
                hideLoading()
            }
        }

        sharedViewModel.getRefreshFragmentResult.observe(viewLifecycleOwner){
            if (it == true){
                applyFilter(isNetValue)
            }
        }

        viewModel.getBrokerSumResult.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()){
                brokerSummaryAdapter.setData(it)
            } else {
                brokerSummaryAdapter.clearData()
            }
        }
    }
}