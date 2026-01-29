package com.bcasekuritas.mybest.app.feature.disclaimer

import androidx.fragment.app.viewModels
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.databinding.FragmentDisclaimerBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DisclaimerFragment: BaseFragment<FragmentDisclaimerBinding, DisclaimerViewModel>() {

    override val viewModel: DisclaimerViewModel by viewModels()
    override val binding: FragmentDisclaimerBinding by autoCleaned { (FragmentDisclaimerBinding.inflate(layoutInflater)) }
    override val bindingVariable: Int = BR.vmDisclaimer

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            ivBack.setOnClickListener {
                onBackPressed()
            }
        }
    }
}