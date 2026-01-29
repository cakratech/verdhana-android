package com.bcasekuritas.mybest.app.feature.tradingview

import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.databinding.FragmentTradingViewBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.getUrlTradingView
import com.bcasekuritas.mybest.ext.constant.Args

@FragmentScoped
@AndroidEntryPoint
class TradingViewFragment : BaseFragment<FragmentTradingViewBinding, TradingViewViewModel>() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmSelectAccount
    override val viewModel: TradingViewViewModel by viewModels()
    override val binding: FragmentTradingViewBinding by autoCleaned {
        (FragmentTradingViewBinding.inflate(layoutInflater))
    }

    private var stockCode = "BBCA"
    private var mode = "LIGHT"

    override fun setupArguments() {
        super.setupArguments()
        arguments?.let {
            stockCode = it.getString(Args.EXTRA_PARAM_STR_ONE).toString()
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            webviewTradingView.clearCache(true)
            webviewTradingView.settings.loadsImagesAutomatically = true
            webviewTradingView.settings.javaScriptEnabled = true
            webviewTradingView.settings.domStorageEnabled = true
            webviewTradingView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            val thm = if (mode == "DARK") {
                "D"
            } else {
                "L"
            }
//            webviewTradingView.loadUrl(
//                getUrlTradingView(
//                    3, thm, "RG",
//                    stockCode, "D", prefManager.userId
//                )
//            )

            webviewTradingView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    return false
                }
            }

            webviewTradingView.loadUrl(
                getUrlTradingView(
                    3, thm, "RG",
                    stockCode, "D", prefManager.userId
                )
            )

        }
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

}