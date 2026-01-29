package com.bcasekuritas.mybest.app.feature.dialog.searchrecyclerview.searchstock

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.base.dialog.withvm.BaseDialogFullFragment
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.app.feature.searchstock.adapter.SearchStockAdapter
import com.bcasekuritas.mybest.databinding.FragmentSearchStockBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarInterface
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import java.util.TreeMap

@FragmentScoped
@AndroidEntryPoint
class SearchStockDialog(private val selectedStock: List<String>) : BaseDialogFullFragment<FragmentSearchStockBinding, SearchStockDialogViewModel>(),
    OnClickAny, ShowSnackBarInterface by ShowSnackBarImpl(), ShowDialog by ShowDialogImpl() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmSelectAccount
    override val viewModel: SearchStockDialogViewModel by viewModels()
    override val binding: FragmentSearchStockBinding by autoCleaned {
        (FragmentSearchStockBinding.inflate(
            layoutInflater
        ))
    }

    private val searchStockAdapter: SearchStockAdapter by autoCleaned {
        SearchStockAdapter(prefManager.urlIcon, this)
    }

    private var onSelectedStock: ((Boolean, String) -> Unit)? = null

    fun setOnSelectedStock(listener: (Boolean, String) -> Unit) {
        onSelectedStock = listener
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onSelectedStock?.invoke(false, "")
    }

    override fun initAPI() {
        super.initAPI()

        viewModel.searchStockParam("")
    }

    override fun setupObserver() {
        super.setupObserver()

        viewModel.searchStockParamResult.observe(viewLifecycleOwner){
            val searchStockList = arrayListOf<StockParamObject>()
            it?.map { data -> data?.let{searchStockList.add(data) }}
            searchStockAdapter.setData(searchStockList.sortedBy { it.stockCode }, false )
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

    override fun onClickAny(valueAny: Any?) {
        if (valueAny != null) {
            val isDuplicate = selectedStock.contains(valueAny)
            if (isDuplicate) {
                showSnackBarBottom(requireContext(), binding.root, "error", R.drawable.ic_success, "Cannot add selected stock", "", requireActivity(), "")
            } else {
                if (selectedStock.size >= 3) {
                    showSnackBarBottom(requireContext(), binding.root, "error", R.drawable.ic_success, "Cannot add more than 3 stocks", "", requireActivity(), "")
                } else {
                    onSelectedStock?.invoke(true, valueAny.toString())
                    dismiss()
                }
            }
        }
    }
}