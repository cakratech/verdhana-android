package com.bcasekuritas.mybest.app.feature.stockdetail.trade

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.stockdetail.StockDetailSharedViewModel
import com.bcasekuritas.mybest.app.feature.stockdetail.adapter.TradePriceAdapter
import com.bcasekuritas.mybest.app.feature.stockdetail.adapter.TradeTimeAdapter
import com.bcasekuritas.mybest.databinding.FragmentTradeBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.date.DateUtils
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class TradeFragment : BaseFragment<FragmentTradeBinding, TradeViewModel>() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmTrade
    override val viewModel: TradeViewModel by viewModels()
    override val binding: FragmentTradeBinding by autoCleaned { FragmentTradeBinding.inflate(layoutInflater) }

    lateinit var sharedViewModel: StockDetailSharedViewModel
    private val tradePriceAdapter: TradePriceAdapter by autoCleaned { TradePriceAdapter() }
    private val tradeTimeAdapter: TradeTimeAdapter by autoCleaned { TradeTimeAdapter() }

    private var isPrice = true
    private var stockCode = ""
    private var countHideLoading = 0

    private var userId = ""
    private var sessionId = ""

    companion object {
        fun newInstance() = TradeFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(StockDetailSharedViewModel::class.java)
    }

    override fun setupComponent() {
        super.setupComponent()
//        showLoading()
        initLayout(isPrice)

        binding.rcvTradePrice.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.rcvTradePrice.viewTreeObserver.removeOnGlobalLayoutListener(this)
                if (isAdded) {
                    try {
                        val maxHeight = 600
                        if (isAdded && !isDetached) {
                            if (binding.rcvTradePrice.height > maxHeight) {
                                binding.rcvTradePrice.layoutParams.height = maxHeight
                                binding.rcvTradePrice.requestLayout()
                            }
                        }
                    } catch (ignore: Exception) {}
                }
            }
        })

        binding.rcvTradeTime.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.rcvTradePrice.viewTreeObserver.removeOnGlobalLayoutListener(this)
                if (isAdded) {
                    try {
                        val maxHeight = 600
                        if (isAdded && !isDetached) {
                            if (binding.rcvTradeTime.height > maxHeight) {
                                binding.rcvTradeTime.layoutParams.height = maxHeight
                                binding.rcvTradeTime.requestLayout()
                            }
                        }
                    } catch (ignore: Exception) {}
                }
            }
        })

        binding.chipGroupTrade.setOnCheckedChangeListener { _, _ ->
            isPrice = binding.chipTradePrice.isChecked
            initLayout(isPrice)
        }
    }

    override fun setupAdapter() {
        super.setupAdapter()

        // Trade Price
        binding.rcvTradePrice.setHasFixedSize(false)
        binding.rcvTradePrice.adapter = tradePriceAdapter
        binding.rcvTradePrice.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // Trade Time
        binding.rcvTradeTime.setHasFixedSize(false)
        binding.rcvTradeTime.adapter = tradeTimeAdapter
        binding.rcvTradeTime.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun initLayout(isTradePrice: Boolean){
        if (isTradePrice) {
            viewModel.getStockTrade(userId, sessionId, stockCode)
            binding.llTradeTablePrice.visibility = View.VISIBLE
            binding.llTradeTableTime.visibility = View.GONE
        } else {
            viewModel.unSubscribeStockTrade(stockCode)
            viewModel.getTradeBookTime(userId,sessionId, stockCode)
            binding.llTradeTableTime.visibility = View.VISIBLE
            binding.llTradeTablePrice.visibility = View.GONE
        }
    }

    override fun initAPI() {
        super.initAPI()
        countHideLoading = 0

        userId = prefManager.userId
        sessionId = prefManager.sessionId
        stockCode = prefManager.stockDetailCode

        getTradeData()
    }

    override fun setupObserver() {
        super.setupObserver()
        sharedViewModel.getStockCodeChangeResult.observe(viewLifecycleOwner){
            if (it == true){
                tradePriceAdapter.clearData()
                tradeTimeAdapter.clearData()
                viewModel.unSubscribeStockTrade(stockCode)
                stockCode = prefManager.stockDetailCode
                getTradeData()
            }
        }

        sharedViewModel.getRefreshFragmentResult.observe(viewLifecycleOwner){
            if (it == true){
                getTradeData()
            }
        }

        viewModel.getStockTradPriceeResult.observe(viewLifecycleOwner){listData ->
            if (listData.isNotEmpty()){
                tradePriceAdapter.setData(listData)
            }
        }

        viewModel.getTradeBookTimeResult.observe(viewLifecycleOwner){
            when(it){
                is Resource.Success -> {
                    when(it.data?.status){
                        0 -> {
                            val listData = it.data.tradeBookTimeList
                            val filteredList = listData.filter { item ->
                                val time = DateUtils.convertLongToDate(item.tbtTime, "HH:mm")
                                time != "08:30"
                            }
                            tradeTimeAdapter.setData(filteredList)
                        }

                        1 -> {

                        }

                        2 -> {

                        }
                    }
                }

                else -> {}
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.setListenerStockTrade()
    }

    override fun onStop() {
        super.onStop()
        viewModel.unSubscribeStockTrade(stockCode)
    }

    private fun getTradeData() {
        if (isPrice) {
            viewModel.getStockTrade(userId, sessionId, stockCode)
        } else {
            viewModel.getTradeBookTime(userId, sessionId, stockCode)
        }
    }

    private fun countToHideLoading(){
        countHideLoading++
        if (countHideLoading == 2){
            hideLoading()
        }
    }
}