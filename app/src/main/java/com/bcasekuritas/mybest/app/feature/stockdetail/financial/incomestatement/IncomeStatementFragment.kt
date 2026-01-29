package com.bcasekuritas.mybest.app.feature.stockdetail.financial.incomestatement


import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.stockdetail.StockDetailSharedViewModel
import com.bcasekuritas.mybest.app.feature.stockdetail.adapter.IncomeStatementAdapter
import com.bcasekuritas.mybest.databinding.FragmentIncomeStatementBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl

@FragmentScoped
@AndroidEntryPoint
class IncomeStatementFragment :
    BaseFragment<FragmentIncomeStatementBinding, IncomeStatementViewModel>(),
    ShowDropDown by ShowDropDownImpl() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmFinancialBalanceSheet
    override val viewModel: IncomeStatementViewModel by viewModels()
    override val binding: FragmentIncomeStatementBinding by autoCleaned {
        FragmentIncomeStatementBinding.inflate(
            layoutInflater
        )
    }

    lateinit var sharedViewModel: StockDetailSharedViewModel
    private val listPeriodType = arrayListOf("Annual", "Quarterly")
    private var periodType = 1
    private val incomeStatementAdapter: IncomeStatementAdapter by autoCleaned { IncomeStatementAdapter() }

    private var format = 0
    private var isPercentage = false

    companion object {
        fun newInstance() = IncomeStatementFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(StockDetailSharedViewModel::class.java)
    }

    override fun setupComponent() {
        super.setupComponent()
        showLoading()
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.apply {
            radioGroupFis.setOnCheckedChangeListener { radioGroup, i ->
                when (i) {
                    R.id.rb_fis_dollar -> {
                        binding.rbFisDollar.isChecked = true
                        binding.rbFisPercent.isChecked = false

                        isPercentage = false
                        incomeStatementAdapter.setIsPercentage(isPercentage)
                    }

                    R.id.rb_fis_percent -> {
                        binding.rbFisDollar.isChecked = false
                        binding.rbFisPercent.isChecked = true

                        isPercentage = true
                        incomeStatementAdapter.setIsPercentage(isPercentage)
                    }
                }
            }

            clPeriodType.setOnClickListener {
                showSimpleDropDownWidth80(requireContext(), listPeriodType, clPeriodType) { index, value ->
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
        binding.rcvIncomeStatement.setHasFixedSize(false)
        binding.rcvIncomeStatement.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcvIncomeStatement.adapter = incomeStatementAdapter
    }

    override fun initAPI() {
        super.initAPI()

        val userId = prefManager.userId
        val stockCode = prefManager.stockDetailCode
        val sessionId = prefManager.sessionId
        incomeStatementAdapter.clearData()

        viewModel.getDetailIncomeStatement(userId, sessionId, stockCode, 5, periodType)
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

        viewModel.getDetailncomeStatementResult.observe(viewLifecycleOwner) {
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
                                    } else{
                                        it.data.let { res ->
                                            binding.lyNoData.lyDataItem.visibility = View.GONE
                                            binding.rcvIncomeStatement.visibility = View.VISIBLE

                                            incomeStatementAdapter.setDataFormat(format)
                                            incomeStatementAdapter.setData(res.dataList)
                                        }
                                        hideLoading()
                                    }
                                } else{
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
        binding.rcvIncomeStatement.visibility = View.GONE

        incomeStatementAdapter.clearData()
    }
}