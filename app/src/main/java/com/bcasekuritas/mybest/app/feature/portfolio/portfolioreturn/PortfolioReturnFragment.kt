package com.bcasekuritas.mybest.app.feature.portfolio.portfolioreturn

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.databinding.FragmentPortfolioReturnBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned

@FragmentScoped
@AndroidEntryPoint
class PortfolioReturnFragment: BaseFragment<FragmentPortfolioReturnBinding, PortfolioReturnViewModel>() {

    override val bindingVariable: Int = BR.vmPortfolioReturn
    override val viewModel: PortfolioReturnViewModel by viewModels()
    override val binding: FragmentPortfolioReturnBinding by autoCleaned { (FragmentPortfolioReturnBinding.inflate(layoutInflater)) }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            ivBack.setOnClickListener {
                onBackPressed()
            }
        }
    }


}