package com.bcasekuritas.mybest.app.feature.stockdetail.runningtrade

import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.feature.stockdetail.StockDetailSharedViewModel
import com.bcasekuritas.mybest.app.feature.stockdetail.adapter.StockDetailRunningTradeAdapter
import com.bcasekuritas.mybest.databinding.FragmentStockDetailRunningTradeBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import java.util.Collections

@FragmentScoped
@AndroidEntryPoint
class StockDetailRunningTradeFragment :
    BaseFragment<FragmentStockDetailRunningTradeBinding, StockDetailRunningTradeViewModel>(){

    @FragmentScoped
    override val bindingVariable: Int = BR.vmStockDetailRunningTrade
    override val viewModel: StockDetailRunningTradeViewModel by viewModels()
    override val binding: FragmentStockDetailRunningTradeBinding by autoCleaned {
        FragmentStockDetailRunningTradeBinding.inflate(layoutInflater)
    }

    lateinit var sharedViewModel: StockDetailSharedViewModel
    private val stockDetailRunningTradeAdapter: StockDetailRunningTradeAdapter by autoCleaned { StockDetailRunningTradeAdapter() }

    private var stockCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(StockDetailSharedViewModel::class.java)
    }

    companion object {
        fun newInstance() = StockDetailRunningTradeFragment()
    }

    override fun setupComponent() {
        super.setupComponent()

        binding.rcvSdRunningTrade.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewModel.setMaxRunningTrade(15)
                binding.rcvSdRunningTrade.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvSdRunningTrade.setHasFixedSize(false)
        binding.rcvSdRunningTrade.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvSdRunningTrade.adapter = stockDetailRunningTradeAdapter
    }

    override fun initAPI() {
        super.initAPI()
//        showLoading()

        val userId = prefManager.userId
        val sessionId = prefManager.sessionId
        stockCode = prefManager.stockDetailCode
        stockDetailRunningTradeAdapter.clearData()

        viewModel.getRunningTrade(userId, sessionId, stockCode)
//        viewModel.startRunningTrade(stockCode)
    }

    override fun setupObserver() {
        super.setupObserver()
        sharedViewModel.getStockCodeChangeResult.observe(viewLifecycleOwner){
            if (it == true){
                viewModel.unSubscribeRunningTrade(stockCode)
                initAPI()
                viewModel.subscribeRunningTrade(stockCode)
            }
        }

        sharedViewModel.getRefreshFragmentResult.observe(viewLifecycleOwner){
            if (it == true) {
                initAPI()
            }
        }

        viewModel.getRunningTradeData.observe(viewLifecycleOwner) {listResource ->
            if (listResource.isNotEmpty()) {
                stockDetailRunningTradeAdapter.setData(listResource, viewModel.getLatestUniqueId())
            } else {
                stockDetailRunningTradeAdapter.setData(Collections.emptyList());
            }
            hideLoading()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.setListenerRunningTrade()
        viewModel.subscribeRunningTrade(stockCode)
    }

    override fun onPause() {
        super.onPause()
        viewModel.unSubscribeRunningTrade(stockCode)
    }
}