package com.bcasekuritas.mybest.app.feature.portfolio.portfoliodetail.tabhistory

import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.PortfolioOrderItem
import com.bcasekuritas.mybest.app.feature.portfolio.portfoliodetail.PortfolioDetailSharedViewModel
import com.bcasekuritas.mybest.app.feature.portfolio.portfoliodetail.adapter.PortfolioDetailHistoryOrderTabAdapter
import com.bcasekuritas.mybest.databinding.FragmentTabHistoryPortfolioDetailBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class HistoryTabPortfolioDetailFragment : BaseFragment<FragmentTabHistoryPortfolioDetailBinding, HistoryTabPortfolioDetailViewModel>(),
    OnClickAny {

    override val viewModel: HistoryTabPortfolioDetailViewModel by viewModels()
    override val binding: FragmentTabHistoryPortfolioDetailBinding by autoCleaned { (FragmentTabHistoryPortfolioDetailBinding.inflate(layoutInflater)) }
    override val bindingVariable: Int = BR.vmHistoryPortfolioDetail

    private val mAdapter: PortfolioDetailHistoryOrderTabAdapter by autoCleaned { PortfolioDetailHistoryOrderTabAdapter(requireContext(), this) }
    private lateinit var sharedViewModel: PortfolioDetailSharedViewModel

    private var accNo = ""
    private var userId = ""
    private var sessionId = ""
    private var startDate = 0L
    private var endDate = 0L
    private var stockCode = ""
    private var page = 1

    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(PortfolioDetailSharedViewModel::class.java)
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        accNo = prefManager.accno
        sessionId = prefManager.sessionId
        val calendar = Calendar.getInstance()
        endDate = calendar.timeInMillis

        calendar.add(Calendar.YEAR, -1)
        startDate = calendar.timeInMillis
    }

    override fun onResume() {
        super.onResume()

        page = 1
        viewModel.getOrderHistory(userId, accNo, sessionId, startDate, endDate, stockCode, page)
    }

    override fun setupAdapter() {
        super.setupAdapter()
        linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvHistoryPortfolio.apply {
            adapter = mAdapter
            layoutManager = linearLayoutManager
        }
    }

    override fun setupListener() {
        super.setupListener()
        binding.rcvHistoryPortfolio.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount = linearLayoutManager.childCount
                val pastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition()
                val total  = mAdapter.itemCount

                if (visibleItemCount + pastVisibleItem>= total){
                    page += 1
                    viewModel.getOrderHistory(userId, accNo, sessionId, startDate, endDate, stockCode, page)
                }

                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }


    override fun setupObserver() {
        super.setupObserver()
        sharedViewModel.portfolioDetailData.observe(viewLifecycleOwner) {
            if (it.stockcode != "") {
                stockCode = it.stockcode
//                viewModel.getOrderHistory(userId!!, accNo!!, sessionId!!, startDate!!, endDate!!, stockCode, page)
            }
        }

        viewModel.getOrderHistoryResult.observe(viewLifecycleOwner) {
            when (it?.status) {
                0 -> {
                    if (it.tradeInfoList.isNotEmpty()) {
                        val orderListItemMapper = ArrayList<PortfolioOrderItem>()
                        it.tradeInfoList.map {data ->
                            orderListItemMapper.add(
                                PortfolioOrderItem(
                                    data.tdId,
                                    data.exordid,
                                    data.mtime,
                                    "M",
                                    data.bs,
                                    "",
                                    data.stockcode,
                                    "",
                                    data.mprice,
                                    data.mqty,
                                    data.mqty,
                                    isHistory = true
                                )
                            )
                        }

                        if (page > 1) {
                            mAdapter.addData(orderListItemMapper)
                        } else {
                            mAdapter.setData(orderListItemMapper)
                        }

                        binding.rcvHistoryPortfolio.visibility = View.VISIBLE
                        binding.lyHistoryEmpty.visibility = View.GONE
                    } else {
                        if (page == 1) {
                            binding.rcvHistoryPortfolio.visibility = View.GONE
                            binding.lyHistoryEmpty.visibility = View.VISIBLE
                        }
                    }
                }
            }

        }
    }

    override fun onClickAny(valueAny: Any?) {
        if (valueAny is PortfolioOrderItem) {

            val bundle = Bundle().apply {
                putParcelable(Args.EXTRA_PARAM_OBJECT, valueAny)
            }

            findNavController().navigate(R.id.order_detail_fragment, bundle)
        }
    }
}