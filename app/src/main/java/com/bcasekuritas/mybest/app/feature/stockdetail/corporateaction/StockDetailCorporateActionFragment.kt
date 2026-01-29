package com.bcasekuritas.mybest.app.feature.stockdetail.corporateaction

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.CalDividenSaham
import com.bcasekuritas.mybest.app.domain.dto.response.CalIpo
import com.bcasekuritas.mybest.app.domain.dto.response.CalPubExp
import com.bcasekuritas.mybest.app.domain.dto.response.CalReverseStock
import com.bcasekuritas.mybest.app.domain.dto.response.CalRightIssue
import com.bcasekuritas.mybest.app.domain.dto.response.CalRups
import com.bcasekuritas.mybest.app.domain.dto.response.CalSahamBonus
import com.bcasekuritas.mybest.app.domain.dto.response.CalStockSplit
import com.bcasekuritas.mybest.app.domain.dto.response.CalWarrant
import com.bcasekuritas.mybest.app.feature.stockdetail.StockDetailSharedViewModel
import com.bcasekuritas.mybest.app.feature.stockdetail.corporateaction.adapter.CorporateActionAdapter
import com.bcasekuritas.mybest.databinding.FragmentStockDetailCorporateActionBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class StockDetailCorporateActionFragment: BaseFragment<FragmentStockDetailCorporateActionBinding, StockDetailCorporateActionViewModel>() {
    override val viewModel: StockDetailCorporateActionViewModel by viewModels()
    override val binding: FragmentStockDetailCorporateActionBinding by autoCleaned { (FragmentStockDetailCorporateActionBinding.inflate(layoutInflater)) }
    override val bindingVariable: Int = BR.vmCorporateAction

    lateinit var sharedViewModel: StockDetailSharedViewModel

    private val corporateActionAdapter: CorporateActionAdapter by autoCleaned { CorporateActionAdapter() }

    private var chipPosition = 1
    private var userId = ""
    private var sessionId = ""
    private var stockCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(StockDetailSharedViewModel::class.java)
    }

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvCorporateAction.apply {
            adapter = corporateActionAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            val chipDataMap = mapOf(
                chipDividend.id to 1,
                chipRups.id to 2,
                chipEipo.id to 3,
                chipPublicExpose.id to 4,
                chipBonus.id to 5,
                chipStockSplit.id to 6,
                chipReverseSplit.id to 7,
                chipRightIssue.id to 8,
                chipWarrant.id to 9
            )

            chipGroup.setOnCheckedStateChangeListener { _, checkedIds  ->
                val checkedId = checkedIds.firstOrNull()
                chipPosition = chipDataMap[checkedId] ?: 1

                getData(chipPosition)
            }
        }
    }

    override fun initAPI() {
        super.initAPI()
        stockCode = prefManager.stockDetailCode
        userId = prefManager.userId
        sessionId = prefManager.sessionId

        viewModel.getCorporateActionByStockCode(userId, sessionId, stockCode, 5)
    }

    override fun setupObserver() {
        super.setupObserver()
        sharedViewModel.getStockCodeChangeResult.observe(viewLifecycleOwner){
            if (it == true){
                stockCode = prefManager.stockDetailCode
                corporateActionAdapter.clearData()
                getData(chipPosition)
            }
        }

        sharedViewModel.getRefreshFragmentResult.observe(viewLifecycleOwner){
            if (it == true){
                corporateActionAdapter.clearData()
                getData(chipPosition)
            }
        }

        viewModel.getDividendResult.observe(viewLifecycleOwner) {data ->
            if (!data.isNullOrEmpty() && chipPosition == 1) {
                corporateActionAdapter.setData(data)
            } else {
                corporateActionAdapter.clearData()
            }

            binding.groupEmpty.visibility = if (data.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.getRupsResult.observe(viewLifecycleOwner) {data ->
            if (!data.isNullOrEmpty() && chipPosition == 2) {
                corporateActionAdapter.setData(data)
            } else {
                corporateActionAdapter.clearData()
            }
            binding.groupEmpty.visibility = if (data.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.getIpoResult.observe(viewLifecycleOwner) {data ->
            if (!data.isNullOrEmpty() && chipPosition == 3) {
                corporateActionAdapter.setData(data)
            } else {
                corporateActionAdapter.clearData()
            }
            binding.groupEmpty.visibility = if (data.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.getPublicExposeResult.observe(viewLifecycleOwner) {data ->
            if (!data.isNullOrEmpty() && chipPosition == 4) {
                corporateActionAdapter.setData(data)
            } else {
                corporateActionAdapter.clearData()
            }
            binding.groupEmpty.visibility = if (data.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.getBonusResult.observe(viewLifecycleOwner) {data ->
            if (!data.isNullOrEmpty() && chipPosition == 5) {
                corporateActionAdapter.setData(data)
            } else {
                corporateActionAdapter.clearData()
            }
            binding.groupEmpty.visibility = if (data.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.getStockSplitResult.observe(viewLifecycleOwner) {data ->
            if (!data.isNullOrEmpty() && chipPosition == 6) {
                corporateActionAdapter.setData(data)
            } else {
                corporateActionAdapter.clearData()
            }
            binding.groupEmpty.visibility = if (data.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.getReverseStockResult.observe(viewLifecycleOwner) {data ->
            if (!data.isNullOrEmpty() && chipPosition == 7) {
                corporateActionAdapter.setData(data)
            } else {
                corporateActionAdapter.clearData()
            }
            binding.groupEmpty.visibility = if (data.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.getRightIssueResult.observe(viewLifecycleOwner) {data ->
            if (!data.isNullOrEmpty() && chipPosition == 8) {
                corporateActionAdapter.setData(data)
            } else {
                corporateActionAdapter.clearData()
            }
            binding.groupEmpty.visibility = if (data.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.getWarrantResult.observe(viewLifecycleOwner) {data ->
            if (!data.isNullOrEmpty() && chipPosition == 9) {
                corporateActionAdapter.setData(data)
            } else {
                corporateActionAdapter.clearData()
            }
            binding.groupEmpty.visibility = if (data.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun getData(position: Int) {
        val calType = when (position) {
            1 -> 5
            2 -> 7
            3 -> 8
            4 -> 6
            5 -> 4
            6 -> 3
            7 -> 9
            8 -> 2
            9 -> 1
            else -> 5
        }

        viewModel.getCorporateActionByStockCode(userId, sessionId, stockCode, calType)
    }

}