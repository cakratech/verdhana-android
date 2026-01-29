package com.bcasekuritas.mybest.app.feature.about

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.BuildConfig
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.databinding.FragmentAboutBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned

@FragmentScoped
@AndroidEntryPoint
class AboutFragment : BaseFragment<FragmentAboutBinding, AboutViewModel>() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmAbout
    override val viewModel: AboutViewModel by viewModels()
    override val binding: FragmentAboutBinding by autoCleaned {(FragmentAboutBinding.inflate(layoutInflater))}

    companion object {
        fun newInstance() = AboutFragment()
    }

    @SuppressLint("ResourceAsColor")
    override fun setupComponent() {
        binding.lyToolbarAbout.tvLayoutToolbarMasterTitle.text = ""
        binding.lyToolbarAbout.ivLayoutToolbarMasterIconLeft.visibility = View.VISIBLE

        binding.swipeRefreshLayoutAbout.isRefreshing = false
        binding.swipeRefreshLayoutAbout.isEnabled = false

        val versionName = BuildConfig.VERSION_NAME
        binding.tvVersionAbout.text = "Version $versionName"
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.lyToolbarAbout.ivLayoutToolbarMasterIconLeft.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.ivInstagramAbout.setOnClickListener{
            //
        }

        binding.ivYoutubeAbout.setOnClickListener{
            //
        }

        binding.ivTwitterAbout.setOnClickListener{
            //
        }

        binding.ivWhatsappAbout.setOnClickListener{
            //
        }

    }
}