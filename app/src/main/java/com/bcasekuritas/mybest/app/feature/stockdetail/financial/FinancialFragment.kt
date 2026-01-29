package com.bcasekuritas.mybest.app.feature.stockdetail.financial

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.base.navigation.NavigationFinancial
import com.bcasekuritas.mybest.databinding.FragmentFinancialBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import javax.inject.Inject

@FragmentScoped
@AndroidEntryPoint
class FinancialFragment : BaseFragment<FragmentFinancialBinding, FinancialViewModel>() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmFinancial
    override val viewModel: FinancialViewModel by viewModels()
    override val binding: FragmentFinancialBinding by autoCleaned {
        FragmentFinancialBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var navigationFinancial: NavigationFinancial

    private var chipPosition = 1

    companion object {
        fun newInstance() = FinancialFragment()
    }

    override fun setupComponent() {
        super.setupComponent()
        initLayout(chipPosition)
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.chipGroupFinancial.setOnCheckedChangeListener { group, checkedId ->
            if (binding.chipFinancialOverview.isChecked){
                chipPosition = 1
            }

            if (binding.chipFinancialIncomeStatements.isChecked){
                chipPosition = 2
            }

            if (binding.chipFinancialBalanceSheet.isChecked){
                chipPosition = 3
            }

            if (binding.chipFinancialCashflow.isChecked){
                chipPosition = 4
            }

            initLayout(chipPosition)
        }
    }

    private fun initLayout(position: Int){
        if (position == 1){
            navigationFinancial.navigateFinancialOverview()
        }

        if (position == 2){
            navigationFinancial.navigateFinancialIncomeStatement()
        }

        if (position == 3){
            navigationFinancial.navigateFinancialBalanceSheet()
        }

        if (position == 4){
            navigationFinancial.navigateFinancialCashflow()
        }
    }


}