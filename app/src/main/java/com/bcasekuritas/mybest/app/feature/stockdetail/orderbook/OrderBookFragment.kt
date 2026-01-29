package com.bcasekuritas.mybest.app.feature.stockdetail.orderbook


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.BuyOrderBookRes
import com.bcasekuritas.mybest.app.domain.dto.response.SellOrderBookRes
import com.bcasekuritas.mybest.app.feature.stockdetail.StockDetailSharedViewModel
import com.bcasekuritas.mybest.app.feature.stockdetail.adapter.OrderBookBuyAdapter
import com.bcasekuritas.mybest.app.feature.stockdetail.adapter.OrderBookSellAdapter
import com.bcasekuritas.mybest.databinding.FragmentOrderBookBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import java.math.BigDecimal

@FragmentScoped
@AndroidEntryPoint
class OrderBookFragment() : BaseFragment<FragmentOrderBookBinding, OrderBookViewModel>(), OnClickStr {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmOrderBook
    override val viewModel: OrderBookViewModel by viewModels()
    override val binding: FragmentOrderBookBinding by autoCleaned {
        FragmentOrderBookBinding.inflate(layoutInflater)
    }

    lateinit var sharedViewModel: StockDetailSharedViewModel

    private val orderBookBuyAdapter: OrderBookBuyAdapter by autoCleaned { OrderBookBuyAdapter(this, requireContext()) }
    private val orderBookSellAdapter: OrderBookSellAdapter by autoCleaned { OrderBookSellAdapter(this) }

    companion object {
        fun newInstance() = OrderBookFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(StockDetailSharedViewModel::class.java)
    }

    override fun setupComponent() {
        super.setupComponent()

        showLoading()
    }
    override fun setupAdapter() {
        super.setupAdapter()

        binding.rcvOrderBookTableBuy.setHasFixedSize(true)
        binding.rcvOrderBookTableBuy.adapter = orderBookBuyAdapter
        binding.rcvOrderBookTableBuy.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.rcvOrderBookTableSell.setHasFixedSize(true)
        binding.rcvOrderBookTableSell.adapter = orderBookSellAdapter
        binding.rcvOrderBookTableSell.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

//        binding.rcvOrderBookTableBuy.withSimpleAdapter(it[0]?.buyOrderBookList as List<BuyOrderBook>, R.layout.item_orderbook_buy){data ->
//
//        }
    }

    override fun setupObserver() {
        super.setupObserver()
        sharedViewModel.getStockCodeChangeResult.observe(viewLifecycleOwner){
            if (it == true){
                onResume()
            }
        }

        sharedViewModel.getStockOrderbookResult.observe(viewLifecycleOwner){
            if (it != null){
                val buyOrderbook = mutableListOf<BuyOrderBookRes>()
                val sellOrderbook = mutableListOf<SellOrderBookRes>()
                val totBidQtyM = it.totalBidQtyM.toDouble()
                val totOfferQtyM = it.totalOfferQtyM.toDouble()
                val prevPrice = it.close

                if (it.buyOrderBookList != null && it.buyOrderBookList.isNotEmpty()) {
                    it.buyOrderBookList.take(10).forEachIndexed { i, item ->
                        val itemQtyL = item.quantityL
                        val buyProgress = totBidQtyM / itemQtyL

                        val buyOrderBookItem = BuyOrderBookRes(
                            id = i,
                            price = item.price,
                            quantity = item.quantity.toDouble(),
                            quantityL = BigDecimal(item.quantityL),
                            totQuantityL = it.totalBidQtyM.toInt(),
                            progress = buyProgress.toInt(),
                            prevPrice = prevPrice
                        )
                        buyOrderbook.add(buyOrderBookItem)
                    }
                }

                if (it.sellOrderBookList != null && it.sellOrderBookList.isNotEmpty()) {
                    it.sellOrderBookList.take(10).forEachIndexed { i, item ->
                        val itemQtyL = item.quantityL
                        val sellProgress = totOfferQtyM / itemQtyL

                        val sellOrderBookItem = SellOrderBookRes(
                            id = i,
                            price = item.price,
                            quantity = item.quantity.toDouble(),
                            quantityL = BigDecimal(item.quantityL),
                            totQuantityL = it.totalOfferQtyM.toInt(),
                            progress = sellProgress.toInt(),
                            prevPrice = prevPrice
                        )
                        sellOrderbook.add(sellOrderBookItem)
                    }
                }

                orderBookBuyAdapter.setData(buyOrderbook)
                orderBookSellAdapter.setData(sellOrderbook)
            }
            hideLoading()
        }

        sharedViewModel.getRefreshFragmentResult.observe(viewLifecycleOwner){
            if (it == true){
                onResume()
            }
        }

        sharedViewModel.getHideLoadingResult.observe(viewLifecycleOwner){
            if (it == true){
                hideLoading()
            }
        }
    }

    override fun onClickStr(value: String?) {
        Log.d("OrderBook Fragment", "onClickStr: click")
    }
}
