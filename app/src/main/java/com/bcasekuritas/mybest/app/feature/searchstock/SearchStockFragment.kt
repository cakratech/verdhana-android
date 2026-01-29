package com.bcasekuritas.mybest.app.feature.searchstock

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.app.feature.searchstock.adapter.SearchStockAdapter
import com.bcasekuritas.mybest.databinding.FragmentSearchStockBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import java.util.TreeMap

@FragmentScoped
@AndroidEntryPoint
class SearchStockFragment : BaseFragment<FragmentSearchStockBinding, SearchStockViewModel>(),
    OnClickAny {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmSelectAccount
    override val viewModel: SearchStockViewModel by viewModels()
    override val binding: FragmentSearchStockBinding by autoCleaned {
        (FragmentSearchStockBinding.inflate(
            layoutInflater
        ))
    }

    private lateinit var sharedViewModel: SearchStockSharedViewmodel

    private val searchStockAdapter: SearchStockAdapter by autoCleaned {
        SearchStockAdapter(prefManager.urlIcon, this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SearchStockSharedViewmodel::class.java)
    }

    private var isCheckbox = false
    private var stockCheckList = mutableListOf<StockParamObject>()
    private lateinit var checkedTemp : List<String>
    private var hintText = ""

    override fun initAPI() {
        super.initAPI()

        viewModel.searchStockParam("")
    }

    override fun setupArguments() {
        super.setupArguments()
        arguments?.let {
            if(it.getInt(Args.EXTRA_PARAM_INT_ONE) == 0) isCheckbox = true
            val hint = it.getString(Args.EXTRA_PARAM_STR_ONE)
            hintText = hint ?: ""
            if (hintText != "") {
                binding.searchBar.setHint(hintText)
            }
        }
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
            searchStockAdapter.setData(searchStockList.sortedBy { it.stockCode }, isCheckbox )
        }

        sharedViewModel.getCheckedStockParam.observe(viewLifecycleOwner){
            it?.let {
                checkedTemp = it
                if (stockCheckList.isEmpty() && it.isNotEmpty()) {
                    it.map { stockCheckList.add(StockParamObject(it, "","", byteArrayOf(), true)) }
                }
            }
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

        }
    }

    private fun sendCheckedList(list: List<StockParamObject>){
        val checkedStockMapList: MutableList<TreeMap<String, String>> = mutableListOf()

        list.map {
            val checkedStockMap = TreeMap<String, String>()
            checkedStockMap[it.stockCode] = it.stockCode
            checkedStockMapList.add(checkedStockMap)
        }

        var stockList: List<String> = emptyList()

        stockList = checkedStockMapList.flatMap { it.values }.toList()

        sharedViewModel.setCheckedStockParam(stockList)
    }

    override fun onClickAny(valueAny: Any?) {
        if (isCheckbox){
            valueAny as StockParamObject
            if (stockCheckList.any { it.stockCode == valueAny.stockCode }){
                stockCheckList.removeIf {it.stockCode == valueAny.stockCode}
            } else {
                stockCheckList.add(valueAny)
            }

            sendCheckedList(stockCheckList)
        }else{
            valueAny as String
            val bundle = Bundle().apply {
                putString(Args.EXTRA_PARAM_STR_ONE, valueAny)
            }
            findNavController().popBackStack()
            findNavController().navigate(R.id.stock_detail_fragment, bundle)
        }
    }
}