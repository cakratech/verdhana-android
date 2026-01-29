package com.bcasekuritas.mybest.app.feature.stockdetail.financial.balancesheet

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.stockdetail.StockDetailSharedViewModel
import com.bcasekuritas.mybest.app.feature.stockdetail.adapter.BalanceSheetAdapter
import com.bcasekuritas.mybest.databinding.FragmentBalanceSheetBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl

@FragmentScoped
@AndroidEntryPoint
class BalanceSheetFragment :
    BaseFragment<FragmentBalanceSheetBinding, BalanceSheetViewModel>(), ShowDropDown by ShowDropDownImpl() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmFinancialBalanceSheet
    override val viewModel: BalanceSheetViewModel by viewModels()
    override val binding: FragmentBalanceSheetBinding by autoCleaned {
        FragmentBalanceSheetBinding.inflate(
            layoutInflater
        )
    }

    private val listPeriodType = arrayListOf("Annual", "Quarterly")
    private var periodType = 1
    lateinit var sharedViewModel: StockDetailSharedViewModel
    private var format = 0
    private var isDataPercent = false

    private val balanceSheetAdapter: BalanceSheetAdapter by autoCleaned { BalanceSheetAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(StockDetailSharedViewModel::class.java)
    }

    companion object {
        fun newInstance() = BalanceSheetFragment()
    }

    override fun setupComponent() {
        super.setupComponent()
        showLoading()
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.apply {
            radioGroupFbs.setOnCheckedChangeListener { radioGroup, i ->
                when(i){
                    R.id.rb_fbs_rupiah -> {
                        binding.rbFbsRupiah.isChecked = true
                        binding.rbFbsPercent.isChecked = false

                        isDataPercent = false
                        balanceSheetAdapter.setisisPercentage(isDataPercent)
                    }
                    R.id.rb_fbs_percent -> {
                        binding.rbFbsRupiah.isChecked = false
                        binding.rbFbsPercent.isChecked = true

                        isDataPercent = true
                        balanceSheetAdapter.setisisPercentage(isDataPercent)
                    }
                }
            }

            clStockDetailInfo.setOnClickListener {
                showSimpleDropDownWidth80(requireContext(), listPeriodType, clStockDetailInfo) { index, value ->
                    format = if (index != 0) 1 else 0
                    tvPeriodType.text = value
                    periodType = index + 1
                    initAPI()
                }
            }
        }
    }

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvBalanceSheet.setHasFixedSize(false)
        binding.rcvBalanceSheet.adapter = balanceSheetAdapter
        binding.rcvBalanceSheet.smoothScrollToPosition(binding.rcvBalanceSheet.adapter!!.itemCount)
    }

    override fun initAPI() {
        super.initAPI()

        val userId = prefManager.userId
        val stockCode = prefManager.stockDetailCode
        val sessionId = prefManager.sessionId

        balanceSheetAdapter.clearData()
        viewModel.getDetailBalanceSheet(userId, sessionId, stockCode, 5, periodType)
    }

    override fun setupObserver() {
        super.setupObserver()
        sharedViewModel.getStockCodeChangeResult.observe(viewLifecycleOwner){
            if (it == true){
                showLoading()
                initAPI()
            }
        }

        sharedViewModel.getRefreshFragmentResult.observe(viewLifecycleOwner){
            if (it == true){
                initAPI()
            }
        }

        viewModel.getDetailBalanceSheetResult.observe(viewLifecycleOwner) {
            if (it != null){
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        when (it.data?.status) {
                            0.toString() -> {
                                if (it.data != null) {
                                    if (it.data.dataList.isNullOrEmpty()){
                                        noData()
                                    }else{
                                        it.data.let { res ->
                                            binding.lyNoData.lyDataItem.visibility = View.GONE
                                            binding.rcvBalanceSheet.visibility = View.VISIBLE

                                            balanceSheetAdapter.setDataFormat(format)
                                            balanceSheetAdapter.setData(res.dataList)
                                        }
                                        hideLoading()
                                    }
                                }else{
                                    noData()
                                }
                            }

                            else -> {
                                noData()
                            }
                        }
                    }

                    is Resource.Failure -> {
                        noData()
                        Toast.makeText(requireContext(), it.failureData.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {
                        noData()
                    }
                }
            }else{
                noData()
            }
        }
    }

    private fun noData(){
        hideLoading()
        binding.lyNoData.lyDataItem.visibility = View.VISIBLE
        binding.rcvBalanceSheet.visibility = View.GONE
        balanceSheetAdapter.clearData()
    }

}