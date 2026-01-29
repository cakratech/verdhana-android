package com.bcasekuritas.mybest.app.feature.stockdetail.financial.cashflow


import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.stockdetail.StockDetailSharedViewModel
import com.bcasekuritas.mybest.app.feature.stockdetail.adapter.CashFlowAdapter
import com.bcasekuritas.mybest.databinding.FragmentCashflowBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl

@FragmentScoped
@AndroidEntryPoint
class CashflowFragment :
    BaseFragment<FragmentCashflowBinding, CashflowViewModel>(),
    ShowDropDown by ShowDropDownImpl() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmFinancialBalanceSheet
    override val viewModel: CashflowViewModel by viewModels()
    override val binding: FragmentCashflowBinding by autoCleaned {
        FragmentCashflowBinding.inflate(
            layoutInflater
        )
    }

    private val listPeriodType = arrayListOf("Annual", "Quarterly")
    private var periodType = 1
    lateinit var sharedViewModel: StockDetailSharedViewModel

    private val cashFlowAdapter: CashFlowAdapter by autoCleaned { CashFlowAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(StockDetailSharedViewModel::class.java)
    }

    companion object {
        fun newInstance() = CashflowFragment()
    }

    override fun setupComponent() {
        super.setupComponent()
        showLoading()
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.apply {
            radioGroupFcf.setOnCheckedChangeListener { radioGroup, i ->
                when(i){
                    R.id.rb_fcf_dollar -> {
                        binding.rbFcfDollar.isChecked = true
                        binding.rbFcfPercent.isChecked = false
                    }
                    R.id.rb_fcf_percent -> {
                        binding.rbFcfDollar.isChecked = false
                        binding.rbFcfPercent.isChecked = true
                    }
                }
            }

            clTypePeriod.setOnClickListener {
                showSimpleDropDownWidth80(requireContext(), listPeriodType, clTypePeriod) { index, value ->
                    tvTypePeriodCode.text = value
                    periodType = index + 1
                    initAPI()
                }
            }
        }
    }

    override fun initAPI() {
        super.initAPI()

        val userId = prefManager.userId
        val stockCode = prefManager.stockDetailCode
        val sessionId = prefManager.sessionId

        viewModel.getDetailCashFlow(userId, sessionId, stockCode, 5, periodType)
    }

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvCashFlow.setHasFixedSize(false)
        binding.rcvCashFlow.adapter = cashFlowAdapter
        binding.rcvCashFlow.smoothScrollToPosition(binding.rcvCashFlow.adapter!!.itemCount)
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

        viewModel.getDetailCashFlowResult.observe(viewLifecycleOwner) {
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
                                            binding.rcvCashFlow.visibility = View.VISIBLE
                                            cashFlowAdapter.setPeriodType(periodType)
                                            cashFlowAdapter.setData(res.dataList)
                                        }
                                        hideLoading()
                                    }
                                }else{
                                    noData()
                                }
                            }

                            else -> noData()
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
        binding.rcvCashFlow.visibility = View.GONE
        cashFlowAdapter.clearData()
    }

}