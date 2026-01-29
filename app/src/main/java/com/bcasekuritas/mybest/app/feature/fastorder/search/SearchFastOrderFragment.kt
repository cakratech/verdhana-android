package com.bcasekuritas.mybest.app.feature.fastorder.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.app.feature.fastorder.search.adapter.SearchFastOrderAdapter
import com.bcasekuritas.mybest.databinding.FragmentSearchStockBinding
import com.bcasekuritas.mybest.databinding.FragmentSearchStockFastOrderBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.listener.OnClickStrs
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class SearchFastOrderFragment : BaseFragment<FragmentSearchStockFastOrderBinding, SearchFastOrderViewModel>(),
    OnClickStrs {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmSelectAccount
    override val viewModel: SearchFastOrderViewModel by viewModels()
    override val binding: FragmentSearchStockFastOrderBinding by autoCleaned {
        (FragmentSearchStockFastOrderBinding.inflate(
            layoutInflater
        ))
    }

    private val searchStockAdapter: SearchFastOrderAdapter by autoCleaned {
        SearchFastOrderAdapter(prefManager.urlIcon, this)
    }

    private var isCheckbox = false
    private var stockCheckList = mutableListOf<StockParamObject>()
    private lateinit var checkedTemp : List<String>

    override fun initAPI() {
        super.initAPI()

        viewModel.searchStockParam("")
    }

    override fun setupArguments() {
        super.setupArguments()
        arguments?.let { if(it.getInt(Args.EXTRA_PARAM_INT_ONE) == 0) isCheckbox = true }
    }

    override fun setupObserver() {
        super.setupObserver()

        viewModel.searchStockParamResult.observe(viewLifecycleOwner){
            val searchStockList = arrayListOf<StockParamObject>()
            it?.map { data -> data?.let{searchStockList.add(data) }}
            if (isCheckbox){
                if (checkedTemp.isNotEmpty()){
                    for (checkedStock in checkedTemp) {
                        val searchStock = searchStockList.firstOrNull { searchStock -> searchStock.stockCode == checkedStock }
                        searchStock?.isChecked = true
                    }

                } else {
                    stockCheckList = mutableListOf()
                    for (checkedStock in stockCheckList) {
                        val searchStock = searchStockList.firstOrNull { searchStock -> searchStock.stockCode == checkedStock.stockCode }
                        searchStock?.isChecked = true
                    }
                }
            }
            searchStockAdapter.setData(searchStockList.sortedBy { it.stockCode } )
        }
    }

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvSearchStock.setHasFixedSize(true)
        binding.rcvSearchStock.adapter = searchStockAdapter
    }

    override fun setupComponent() {
        super.setupComponent()

        binding.apply {
            lyToolbar.llMasterToolbarContainer.visibility = View.VISIBLE
            lyToolbar.tvLayoutToolbarMasterTitle.text = "Search Stock"
            lyToolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener {
                onBackPressed()
            }

            val searchTextWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // Do something before text changes
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Do something when text changes
                }

                override fun afterTextChanged(s: Editable?) {
                    viewModel.searchStockParam(binding.searchBar.getText())
                }
            }

            binding.searchBar.setTextWatcher(searchTextWatcher)

            searchBar.setHint("Search code or name")
        }
    }

    override fun onClickStrs(stockCode: String?, stockName: String?) {
        val bundleBuy = Bundle().apply {
            putString(Args.EXTRA_PARAM_STR_ONE, stockCode)
            putString(Args.EXTRA_PARAM_STR_TWO, stockName)
        }

        val navOptions = NavOptions.Builder()
            .setPopUpTo(findNavController().graph.id, false)
            .build()

        findNavController().navigate(R.id.fast_order_fragment, bundleBuy, navOptions)
    }
}