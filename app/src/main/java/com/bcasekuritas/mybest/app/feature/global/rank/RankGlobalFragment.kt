package com.bcasekuritas.mybest.app.feature.global.rank

import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.feature.global.rank.adapter.GlobalRankAdapter
import com.bcasekuritas.mybest.app.feature.global.rank.adapter.GlobalRankCodeAdapter
import com.bcasekuritas.mybest.databinding.FragmentRankGlobalBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.getCurrentTimeInMillis
import com.bcasekuritas.mybest.ext.common.getYesterdayTimeInMillis
import com.bcasekuritas.mybest.ext.common.initCalenderDialog
import com.bcasekuritas.mybest.ext.common.setDateTo7PM
import com.bcasekuritas.mybest.ext.common.setDateToMidnight
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl
import com.bcasekuritas.mybest.ext.view.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class RankGlobalFragment : BaseFragment<FragmentRankGlobalBinding, RankGlobalViewModel>(), ShowDropDown by ShowDropDownImpl() {

    override val binding: FragmentRankGlobalBinding by autoCleaned { (FragmentRankGlobalBinding.inflate(layoutInflater)) }
    override val bindingVariable: Int = BR.vmRankGlobal
    override val viewModel: RankGlobalViewModel by viewModels()

    private val globalRankAdapter: GlobalRankAdapter by autoCleaned { GlobalRankAdapter() }
    private val globalRankCodeAdapter: GlobalRankCodeAdapter by autoCleaned { GlobalRankCodeAdapter() }

    private var userId = ""
    private var sessionId = ""

    private var boardCode: String = "RG"
    private var startDate: Long = 0
    private var endDate: Long = 0
    private var sortType: Int = 0
    private var sortField: Int = 0
    private var activity = 0

    private val listBoard = arrayListOf("RG", "NG", "TN")
    private val listActivity = arrayListOf("Buy", "Sell")
    private val listSortType = arrayListOf(
        "Value"
    )

    private var datePattern = "dd/MM/yyyy"

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvGlobalCode.apply {
            adapter = globalRankCodeAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            isNestedScrollingEnabled = false
        }

        binding.rcvGlobalRank.apply {
            adapter = globalRankAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            isNestedScrollingEnabled = false
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        endDate = setDateTo7PM(getCurrentTimeInMillis())
        startDate = setDateToMidnight(getYesterdayTimeInMillis())

        binding.apply {
            tvDateFromVal.text = DateUtils.convertLongToDate(startDate, datePattern)
            tvDateToVal.text = DateUtils.convertLongToDate(endDate, datePattern)
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

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            binding.tvFilter.setOnClickListener {
                val icFilter = ContextCompat.getDrawable(requireContext(), R.drawable.ic_filter)
                val icArrowUp = ContextCompat.getDrawable(requireContext(), R.drawable.ic_row_up)
                val icArrowDown = ContextCompat.getDrawable(requireContext(), R.drawable.ic_bot_row)

                if (binding.expandRankFilter.isExpanded){
                    binding.expandRankFilter.collapse()
                    binding.tvFilter.setCompoundDrawablesRelativeWithIntrinsicBounds(icFilter, null, icArrowDown, null)
                }else {
                    binding.expandRankFilter.expand()
                    binding.tvFilter.setCompoundDrawablesRelativeWithIntrinsicBounds(icFilter, null, icArrowUp, null)
                }
            }

            tvDateFromVal.setOnClickListener {
                initCalenderDialog(requireContext(), binding.tvDateFromVal, 2, null, binding.tvDateFromVal.text.toString()){
                    startDate = setDateToMidnight(it)
                    tvDateFromVal.error = null
                    applyFilter()
                }
            }

            tvDateToVal.setOnClickListener {
                initCalenderDialog(requireContext(), tvDateToVal, 2, startDate, binding.tvDateToVal.text.toString()){
                    endDate = setDateTo7PM(it)
                    tvDateToVal.error = null
                    applyFilter()
                }
            }

            tvTradeVal.setOnClickListener {
                showSimpleDropDownWidth80(requireContext(), listBoard, tvTradeVal){ _, value->
                    tvTradeVal.text = value
                    boardCode = value
                    applyFilter()
                }
            }

            tvSortVal.setOnClickListener {
                showSimpleDropDownWidth80(requireContext(), listSortType, tvSortVal){ index, value->
                    tvSortVal.text = value
                }
            }

            tvActivityVal.setOnClickListener {
                showSimpleDropDownWidth80(requireContext(), listActivity, tvActivityVal) { index, value ->
                    tvActivityVal.text = value
                    activity = index
                    applyFilter()
                }
            }

            tvResetFilter.setSafeOnClickListener {
                resetFilter()
            }
        }
    }

    private fun resetFilter() {
        endDate = getCurrentTimeInMillis()
        startDate = getYesterdayTimeInMillis()
        activity = 0
        boardCode = "RG"
        sortType = 0

        binding.apply {
            tvDateFromVal.text = DateUtils.convertLongToDate(startDate, datePattern)
            tvDateToVal.text = DateUtils.convertLongToDate(endDate, datePattern)
            tvActivityVal.text = "Buy"
            tvTradeVal.text = boardCode
            tvTradeVal.text = "RG"
            tvSortVal.text = "Value"
        }

        applyFilter()
    }

    private fun applyFilter() {
        globalRankCodeAdapter.clearData()
        globalRankAdapter.clearData()
        viewModel.getGlobalRank(userId, sessionId, sortType, boardCode, activity, startDate, endDate)
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        sessionId = prefManager.sessionId

        applyFilter()
    }

    override fun setupObserver() {
        super.setupObserver()

        viewModel.getGlobalRankResult.observe(viewLifecycleOwner){
            if (!it.isNullOrEmpty()) {
                val listBrokerCode = arrayListOf<String>()
                it.map {item ->
                    listBrokerCode.add(item.stockCode)
                }

                globalRankCodeAdapter.setData(listBrokerCode)
                globalRankAdapter.setData(it)
            } else {
                globalRankCodeAdapter.clearData()
                globalRankAdapter.clearData()
            }
        }
    }



}