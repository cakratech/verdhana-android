package com.bcasekuritas.mybest.app.feature.e_ipo.eipodetail.about

import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.viewModels
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.feature.e_ipo.eipodetail.EIPODetailSharedViewModel
import com.bcasekuritas.mybest.databinding.FragmentEIPOAboutBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class EIPOAboutFragment : BaseFragment<FragmentEIPOAboutBinding, EIPOAboutViewModel>() {
    override val viewModel: EIPOAboutViewModel by viewModels()
    override val binding: FragmentEIPOAboutBinding by autoCleaned { (FragmentEIPOAboutBinding.inflate(layoutInflater)) }
    override val bindingVariable: Int = BR.vmEIPOAbout

    private lateinit var eipoSharedViewModel: EIPODetailSharedViewModel

    private val listWriters: MutableList<String> = mutableListOf()
    private var overview = Html.fromHtml("")
    private var desc: CharSequence = ""
    private var webLink = ""
    private var isLearnMore = true

    private val webView = CustomTabsIntent.Builder().build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eipoSharedViewModel = ViewModelProvider(requireActivity()).get(EIPODetailSharedViewModel::class.java)
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            tvLearnMore.setOnClickListener {
                tvAboutDesc.text = if (isLearnMore) overview else desc
                tvLearnMore.text = if (isLearnMore) "Show Less" else "Learn More"

                isLearnMore = !isLearnMore
            }

            tvWebsiteVal.setOnClickListener {
                if (webLink.isBlank() || !(webLink.startsWith("http://") || webLink.startsWith("https://"))) {
                    Toast.makeText(context, "Unavailable url file", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                try {
                    webView.launchUrl(requireContext(), Uri.parse(webLink))
                } catch (ignore: Exception) {}
            }
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        eipoSharedViewModel.getIpoData.observe(viewLifecycleOwner) {ipoData ->
            if (ipoData != null) {
                binding.apply {
                    overview = Html.fromHtml(ipoData.overview, Html.FROM_HTML_MODE_LEGACY)
                    tvLearnMore.visibility = if (overview.length > 250) View.VISIBLE else View.GONE
                    desc = if (overview.length > 250)  overview.substring(0,200) + "..." else overview
                    tvAboutDesc.text = desc
                    webLink = ipoData.website

                    tvCodeVal.text = ipoData.code.ifEmpty { "-" }
                    tvSectorVal.text = ipoData.sector.ifEmpty { "-" }
                    tvSubSectorVal.text = ipoData.subSector.ifEmpty { "-" }
                    tvBusinessFieldVal.text = ipoData.businessLine.ifEmpty { "-" }
                    tvAddressVal.text = ipoData.address.ifEmpty { "-" }
                    tvWebsiteVal.text = ipoData.website.ifEmpty { "-" }
                    tvSharesOfferedVal.text = ipoData.targetLots.formatPriceWithoutDecimal() +" Lot"
                    tvTotalShareVal.text = ipoData.sharePercent.formatPriceWithoutDecimal() +"%"
                    tvParticipantAdminVal.text = ipoData.participantAdmin.ifEmpty { "-" }
                    tvUnderwriterVal.text = ipoData.underWriters.ifEmpty { "-" }
                }
            }

        }
    }
}