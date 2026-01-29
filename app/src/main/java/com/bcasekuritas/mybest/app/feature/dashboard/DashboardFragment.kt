package com.bcasekuritas.mybest.app.feature.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.feature.activity.fullscreen.FullScreenActivity
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.databinding.FragmentDashboardBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.ConstKeys
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.widget.banner.BannerUtil

@FragmentScoped
@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding, DashboardViewmodel>() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmDashboard
    override val viewModel: DashboardViewmodel by viewModels()
    override val binding: FragmentDashboardBinding by autoCleaned {(FragmentDashboardBinding.inflate(layoutInflater))}

    private val webView = CustomTabsIntent.Builder().build()

    companion object {
        fun newInstance() = DashboardFragment()
    }

    override fun setupComponent() {
        super.setupComponent()

        binding.btnRegister.text =  spanString("No Account Yet? Register")

        val itemBanner = ArrayList<BannerUtil.BannerItemSpan>()
        itemBanner.add(BannerUtil.BannerItemSpan(R.drawable.onboarding1, R.string.on_board_title_1, "Investing just got better with the right tools and resources to help you succeed"))
        itemBanner.add(BannerUtil.BannerItemSpan(R.drawable.onboarding2, R.string.on_board_title_2, "Peace of mind comes with knowing your  data and transaction are well protected"))
        itemBanner.add(BannerUtil.BannerItemSpan(R.drawable.onboarding3, R.string.on_board_title_3, "Equipped with advanced tools and resources to  help you invest in the right market"))
        binding.banner.initSlider(itemBanner)

        binding.btnLogin.setOnClickListener {
            prefManager.onBoardingState = true
            MiddleActivity.startIntentWithFinish(requireActivity(), NavKeys.KEY_FM_LOGIN, "")
        }

        binding.btnRegister.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ConstKeys.PRE_LOGIN_URL))
            startActivity(browserIntent)
        }
    }

    fun spanString(text: String): SpannableStringBuilder{
        val spannableStringBuilder = SpannableStringBuilder(text)
        val startIndex = text.indexOf("Register")
        val endIndex = startIndex + "Register".length
        spannableStringBuilder.setSpan(
            StyleSpan(android.graphics.Typeface.BOLD),
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannableStringBuilder
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as FullScreenActivity).transparentStatusBar()
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}