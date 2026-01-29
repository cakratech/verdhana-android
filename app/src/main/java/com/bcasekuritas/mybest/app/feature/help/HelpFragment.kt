package com.bcasekuritas.mybest.app.feature.help

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.URLUtil
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.feature.help.adapter.HelpFaqAdapter
import com.bcasekuritas.mybest.app.feature.help.adapter.HelpVideoTutorialAdapter
import com.bcasekuritas.mybest.databinding.FragmentHelpBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.getCategoryName
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class HelpFragment : BaseFragment<FragmentHelpBinding, HelpViewModel>(), OnClickStr {

    override val bindingVariable: Int = BR.vmHelp
    override val viewModel: HelpViewModel by viewModels()
    override val binding: FragmentHelpBinding by autoCleaned { (FragmentHelpBinding.inflate(layoutInflater)) }

    private val faqHelpAdapter: HelpFaqAdapter by autoCleaned { HelpFaqAdapter(requireContext()) }
    private val videoTutorialHelpAdapter: HelpVideoTutorialAdapter by autoCleaned { HelpVideoTutorialAdapter(this) }

    private val webView = CustomTabsIntent.Builder().build()

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvHelpQuestions.apply {
            adapter = faqHelpAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        binding.rcvVideoTutorials.apply {
            adapter = videoTutorialHelpAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        webView.intent.setPackage("com.android.chrome")
        binding.apply {
            tvToolbar.text = "Help"
        }
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            ivBack.setOnClickListener {
                onBackPressed()
            }

            etSearchHelp.setOnClickListener {
                findNavController().navigate(R.id.search_questions_fragment)
            }

            menuAkun.setOnClickListener {
                viewModel.getFaqByCategory(prefManager.userId, prefManager.sessionId, "ACC")
            }

            menuRekening.setOnClickListener {
                viewModel.getFaqByCategory(prefManager.userId, prefManager.sessionId, "RDN")

            }

            menuBiaya.setOnClickListener {
                viewModel.getFaqByCategory(prefManager.userId, prefManager.sessionId, "BTR")

            }

            menuTipe.setOnClickListener {
                viewModel.getFaqByCategory(prefManager.userId, prefManager.sessionId, "TYP")

            }

            menuLimit.setOnClickListener {
                viewModel.getFaqByCategory(prefManager.userId, prefManager.sessionId, "LIM")

            }

            menuAuto.setOnClickListener {
                viewModel.getFaqByCategory(prefManager.userId, prefManager.sessionId, "AOT")

            }

            menuMarket.setOnClickListener {
                viewModel.getFaqByCategory(prefManager.userId, prefManager.sessionId, "MAR")

            }

            menuFast.setOnClickListener {
                viewModel.getFaqByCategory(prefManager.userId, prefManager.sessionId, "FAS")

            }

            menuSltp.setOnClickListener {
                viewModel.getFaqByCategory(prefManager.userId, prefManager.sessionId, "SLT")

            }

            menuSplit.setOnClickListener {
                viewModel.getFaqByCategory(prefManager.userId, prefManager.sessionId, "SLC")

            }

            menuGtc.setOnClickListener {
                viewModel.getFaqByCategory(prefManager.userId, prefManager.sessionId, "GTC")

            }
        }
    }

    override fun initAPI() {
        super.initAPI()
        val userId = prefManager.userId
        val sessionId = prefManager.sessionId
        viewModel.getTopFiveFaq(userId, sessionId)
        viewModel.getVideoTutorial(userId, sessionId)
    }

    override fun setupObserver() {
        super.setupObserver()

        viewModel.getTopFiveFaqResult.observe(viewLifecycleOwner) {listItem ->
            if (!listItem.isNullOrEmpty()) {
                faqHelpAdapter.setData(listItem)
            }
        }
        viewModel.getFaqByCategoryResult.observe(viewLifecycleOwner) {listItem ->
            if (!listItem.isNullOrEmpty()) {
                faqHelpAdapter.setData(listItem)
                binding.tvFaqTitle.text = getCategoryName(listItem[0].category)
            }
        }

        viewModel.getVideoTutorialResult.observe(viewLifecycleOwner) {listVideo ->
            if (!listVideo.isNullOrEmpty()) {
                videoTutorialHelpAdapter.setData(listVideo)
            }
        }
    }

    override fun onClickStr(value: String?) {
        // click video tutorial
        if (!value.isNullOrEmpty()) {
            val url = if (URLUtil.isValidUrl(value)) value else URLUtil.guessUrl(value)
            if (url.isBlank() || !(url.startsWith("http://") || url.startsWith("https://"))) {
                Toast.makeText(context, "Unavailable url", Toast.LENGTH_SHORT).show()
                return
            }
            try {
                // Attempt to launch Custom Tab
                webView.intent.setPackage("com.android.chrome") // Optional
                webView.launchUrl(requireContext(), Uri.parse(url))
            } catch (e: Exception) {
                // Fallback to default browser if Chrome is unavailable
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(browserIntent)
            }

        }
    }


}