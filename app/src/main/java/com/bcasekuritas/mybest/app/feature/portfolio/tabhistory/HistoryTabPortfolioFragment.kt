package com.bcasekuritas.mybest.app.feature.portfolio.tabhistory

import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.data.layout.UIDialogPortfolioHistoryModel
import com.bcasekuritas.mybest.app.domain.dto.response.PortfolioOrderItem
import com.bcasekuritas.mybest.app.domain.dto.response.TradeListInfo
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.portfolio.PortfolioShareViewModel
import com.bcasekuritas.mybest.app.feature.portfolio.tabhistory.adapter.PortfolioHistoryOrderTabAdapter
import com.bcasekuritas.mybest.databinding.FragmentTabHistoryPortfolioBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.mybest.ext.other.formatPercent
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimalWithoutMinus
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class HistoryTabPortfolioFragment : BaseFragment<FragmentTabHistoryPortfolioBinding, HistoryTabPortfolioViewModel>(),
    OnClickAny, ShowDialog by ShowDialogImpl() {

    override val bindingVariable: Int = BR.vmTabHistory
    override val viewModel: HistoryTabPortfolioViewModel by viewModels()
    override val binding: FragmentTabHistoryPortfolioBinding by autoCleaned { (FragmentTabHistoryPortfolioBinding.inflate(layoutInflater)) }

    private lateinit var linearLayoutManager: LinearLayoutManager

    private val mAdapter: PortfolioHistoryOrderTabAdapter by autoCleaned { PortfolioHistoryOrderTabAdapter(requireContext(), this) }

    lateinit var sharedViewModel: PortfolioShareViewModel

    private var accNo = ""
    private var userId = ""
    private var sessionId = ""
    private var startDate = 0L
    private var endDate = 0L
    private var filter = UIDialogPortfolioHistoryModel()
    private var stockCodeList = arrayListOf<String>()

    private var page = 0
    private var isPageLoading = false

    companion object {
        fun newInstance() = HistoryTabPortfolioFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(PortfolioShareViewModel::class.java)
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        accNo = prefManager.accno
        sessionId = prefManager.sessionId

        viewModel.getAllStockParam("")
    }

    override fun onResume() {
        super.onResume()
        page = 0
        defaultDate()
        // set filter first time
        filter = UIDialogPortfolioHistoryModel(
            "", "3month", startDate, endDate
        )

        viewModel.getTradeListHistoryGroup(userId, accNo, sessionId, startDate, endDate, page, "*")
    }

    override fun setupAdapter() {
        super.setupAdapter()
        linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvHistory.apply {
            adapter = mAdapter
            layoutManager = linearLayoutManager
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.swipeRefresh.setOnRefreshListener {
            defaultDate()
            page = 0
            viewModel.getTradeListHistoryGroup(userId, accNo, sessionId, startDate, endDate, page, "*")
            filter = UIDialogPortfolioHistoryModel(
                "", "3month", startDate, endDate
            )

        }
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            btnFilter.setOnClickListener {
                showDialogFilterPortfolioHistoryBottom(filter, stockCodeList.sorted(), childFragmentManager)
            }

            lyRealizedGainLoss.setOnClickListener {
                MiddleActivity.startIntent(requireActivity(), NavKeys.KEY_FM_REALIZED)
            }
        }
    }

    override fun setupListener() {
        super.setupListener()
        binding.rcvHistory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount = linearLayoutManager.childCount
                val pastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition()
                val total  = mAdapter.itemCount

                if (!isPageLoading) {
                    if (visibleItemCount + pastVisibleItem>= total){
                        page += 1
                        isPageLoading = true
                        val stockCode = filter.stockCode.ifEmpty { "*" }
                        viewModel.getTradeListHistoryGroup(userId, accNo, sessionId, startDate, endDate,  page, stockCode)
                    }
                }


                super.onScrolled(recyclerView, dx, dy)
            }
        })

        childFragmentManager.setFragmentResultListener(NavKeys.KEY_FM_TAB_PORTFOLIO_HISTORY, viewLifecycleOwner) {_, result ->
            //filter dialog
            val confirmResultFilter = result.getString(NavKeys.CONST_RES_TAB_PORTFOLIO_HISTORY_FILTER)
            if (confirmResultFilter == "RESULT_OK") {
                filter = UIDialogPortfolioHistoryModel(
                    result.getString("stockCode")!!,
                    result.getString("time")!!,
                    result.getLong("dateFrom"),
                    result.getLong("dateTo")
                )

                setupFilter(filter)
            }
        }
    }

    override fun setupObserver() {
        super.setupObserver()

        viewModel.getRealizeGainLossResults.observe(viewLifecycleOwner) { value ->
            if (value != null) {
                binding.tvRealizedGainLoss.text = when {
                    value > 0 -> "+Rp" + value.formatPriceWithoutDecimal()
                    value < 0 -> "-Rp" + value.formatPriceWithoutDecimalWithoutMinus()
                    else -> "Rp0"
                }
                binding.tvRealizedGainLoss.setTextColor(
                    when {
                        value > 0 -> ContextCompat.getColor(requireContext(), R.color.textUp)
                        value < 0 -> ContextCompat.getColor(requireContext(), R.color.textDown)
                        else -> ContextCompat.getColor(requireContext(), R.color.textSecondaryGrey)
                    }
                )
            }
        }

        viewModel.getOrderHistoryResults.observe(viewLifecycleOwner) {historyList ->
            if (!historyList.isNullOrEmpty()) {
                if (page > 0) {
                    mAdapter.addData(historyList)
                } else {
                    mAdapter.setData(historyList)
                }

                binding.groupContent.visibility = View.VISIBLE
                binding.lyHistoryEmpty.visibility = View.GONE
            } else {
                if (page == 0) {
                    binding.groupContent.visibility = View.GONE
                    binding.lyHistoryEmpty.visibility = View.VISIBLE
                }
            }

            isPageLoading = false
            binding.swipeRefresh.isRefreshing = false
        }

        viewModel.getAllStockParamResult.observe(viewLifecycleOwner) {
            it?.map { data -> data?.let { stockCodeList.add(data.stockCode) } }

        }

        sharedViewModel.isPinSuccess.observe(viewLifecycleOwner) {isSuccess ->
            if (isSuccess) {
                defaultDate()
                page = 0
                viewModel.getTradeListHistoryGroup(userId, accNo, sessionId, startDate, endDate,  page, "*")
                filter = UIDialogPortfolioHistoryModel(
                    "", "3month", startDate, endDate
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.clearOrderHistory()
    }

    override fun onClickAny(valueAny: Any?) {
        if (valueAny is TradeListInfo) {
            val data = PortfolioOrderItem(
                valueAny.orderId,
                valueAny.idxOrderId,
                valueAny.time,
                "M",
                valueAny.buySell,
                "",
                valueAny.stockCode,
                "",
                valueAny.price,
                valueAny.orderQty,
                valueAny.orderQty,
                fee = valueAny.fee,
                isHistory = true
            )

            val bundle = Bundle().apply {
                putParcelable(Args.EXTRA_PARAM_OBJECT, data)
            }

            findNavController().navigate(R.id.order_detail_fragment, bundle)
        }
    }

    private fun setupFilter(filter: UIDialogPortfolioHistoryModel) {
        val date = Calendar.getInstance()
        when (filter.time) {
            "week" -> {
                date.add(Calendar.DAY_OF_YEAR, -7)
            }
            "1month" -> {
                date.add(Calendar.MONTH, -1)
            }
            "3month" -> {
                date.add(Calendar.MONTH, -3)
            }
            "custom" -> {
                startDate = filter.dateFrom
                endDate = filter.dateTo
            }
        }

        if (filter.time != "custom") {
            startDate =  date.timeInMillis
            endDate = Calendar.getInstance().timeInMillis
        }
        val stockCode = filter.stockCode.ifEmpty { "*" }
        page = 0
        viewModel.getTradeListHistoryGroup(userId, accNo, sessionId, startDate, endDate,  page, stockCode)
    }

    private fun defaultDate() {
        val calendar = Calendar.getInstance()
        endDate = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_YEAR, -100)
        startDate = calendar.timeInMillis
    }

}