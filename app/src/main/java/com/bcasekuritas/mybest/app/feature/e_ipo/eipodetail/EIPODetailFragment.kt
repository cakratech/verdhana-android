package com.bcasekuritas.mybest.app.feature.e_ipo.eipodetail

import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.data.layout.UIDialogModel
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.e_ipo.eipodetail.adapter.EIPODetailPagerAdapter
import com.bcasekuritas.mybest.databinding.FragmentEIPODetailBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.converter.GET_4_CHAR_STOCK_CODE
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarInterface
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class EIPODetailFragment : BaseFragment<FragmentEIPODetailBinding, EIPODetailViewModel>(),
    ShowSnackBarInterface by ShowSnackBarImpl() {
    override val viewModel: EIPODetailViewModel by viewModels()
    override val binding: FragmentEIPODetailBinding by autoCleaned { (FragmentEIPODetailBinding.inflate(layoutInflater)) }
    override val bindingVariable: Int = BR.vmEIPODetail

    private lateinit var eipoSharedViewModel: EIPODetailSharedViewModel

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private var layoutPosition = 0

    private var stockCode = ""
    private var prospectusLink = ""
    private var summaryLink = ""
    private var successOrder = false
    private val webView = CustomTabsIntent.Builder().build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eipoSharedViewModel = ViewModelProvider(requireActivity()).get(EIPODetailSharedViewModel::class.java)
    }

    override fun setupArguments() {
        super.setupArguments()
        arguments?.let {
            stockCode = it.getString(Args.EXTRA_PARAM_STR_ONE).toString()
            successOrder = it.getBoolean(Args.EXTRA_PARAM_BOOLEAN)
        }
    }

    override fun setupViewPager() {
        super.setupViewPager()
        viewPager = binding.viewPager
        tabLayout = binding.tabLayout

        val adapter = EIPODetailPagerAdapter(this)
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Stages"
                1 -> "About"
                else -> ""
            }
        }.attach()

        if (layoutPosition != 0) {
            viewPager.setCurrentItem(layoutPosition, false)
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            lyToolbar.ivLayoutToolbarMasterIconRightOne.visibility = View.VISIBLE
            lyToolbar.ivLayoutToolbarMasterIconRightOne.setImageResource(R.drawable.ic_file)

            showDialogSuccessOrder(successOrder)
        }
    }

    private fun showDialogSuccessOrder(isSuccess: Boolean) {
        if (isSuccess) {
            showSnackBarTop(
                requireContext(),
                binding.root,
                "success",
                R.drawable.ic_success,
                "Order for $stockCode is placed",
                getString(R.string.desc_snackbar_eipo_order_success), requireActivity(), NavKeys.KEY_FM_EIPO_DETAIL
            )
        }
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            lyToolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener {
                onBackPressed()
            }

            lyToolbar.ivLayoutToolbarMasterIconRightOne.setOnClickListener {
                findNavController().navigate(R.id.eipo_order_list_fragment)
            }

            buttonOrder.setOnClickListener {
                viewModel.getIpoInfo(prefManager.userId, prefManager.sessionId, stockCode, true)
            }

            btnProspectus.setOnClickListener {
                if (prospectusLink.isNotEmpty()){
                    showDialogConfirmationCenterCallBack(
                        parentFragmentManager,
                        UIDialogModel(titleStr = "Do you want to download Prospectus?", btnPositiveStr = "OK", btnNegativeStr = "Cancel"),
                        true,
                        onOkClicked = {
                            if (prospectusLink.isBlank() || !(prospectusLink.startsWith("http://") || prospectusLink.startsWith("https://"))) {
                                Toast.makeText(context, "Unavailable url file", Toast.LENGTH_SHORT).show()
                                return@showDialogConfirmationCenterCallBack
                            }
                            try {
                                webView.launchUrl(requireContext(), Uri.parse(prospectusLink))
                            } catch (ignore: Exception) {}
                        })
                } else {
                    showDialogConfirmationCenterCallBack(
                        parentFragmentManager,
                        UIDialogModel(titleStr = "Prospectus does not exist", btnPositiveStr = "Confirm", btnNegativeStr = "Cancel"),
                        false,
                        onOkClicked = { })
                }
            }

            btnSummary.setOnClickListener {
                if (summaryLink.isNotEmpty()){
                    showDialogConfirmationCenterCallBack(
                        parentFragmentManager,
                        UIDialogModel(titleStr = "Do you want to download Summary?", btnPositiveStr = "OK", btnNegativeStr = "Cancel"),
                        true,
                        onOkClicked = {
                            if (summaryLink.isBlank() || !(summaryLink.startsWith("http://") || summaryLink.startsWith("https://"))) {
                                Toast.makeText(context, "Unavailable url file", Toast.LENGTH_SHORT).show()
                                return@showDialogConfirmationCenterCallBack
                            }
                            try {
                                webView.launchUrl(requireContext(), Uri.parse(summaryLink))
                            } catch (ignore: Exception) {}
                        })
                } else {
                    showDialogConfirmationCenterCallBack(
                        parentFragmentManager,
                        UIDialogModel(titleStr = "Summary does not exist", btnPositiveStr = "Confirm", btnNegativeStr = "Cancel"),
                        false,
                        onOkClicked = { })
                }
            }

        }
    }

    override fun initAPI() {
        super.initAPI()
        viewModel.getIpoInfo(prefManager.userId, prefManager.sessionId, stockCode, false)
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.orderEipoLiveData.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                if (stockCode == it) {
                    viewModel.getIpoInfo(prefManager.userId, prefManager.sessionId, stockCode, false)
                }
            }
        }

        viewModel.getIpoInfoResult.observe(viewLifecycleOwner) {ipoData ->
            if (ipoData != null) {
                eipoSharedViewModel.setIpoData(ipoData)

                prospectusLink = ipoData.prospectusLink
                summaryLink = ipoData.summaryProspectusLink

                val stockCode = GET_4_CHAR_STOCK_CODE(ipoData.code)

                val url = if (ipoData.logoLink.isNotEmpty()) prefManager.urlIcon + ipoData.logoLink else prefManager.urlIcon + stockCode

                Glide.with(requireActivity())
                    .load(url)
                    .override(300, 200)
                    .circleCrop()
                    .placeholder(R.drawable.bg_circle)
                    .error(R.drawable.bg_circle)
                    .into(binding.ivLogo)

                binding.tvStockName.text = ipoData.companyName
                binding.tvStockCode.text = ipoData.code

                val price = if (ipoData.status == 1) "${ipoData.bookPriceFrom.formatPriceWithoutDecimal()} - ${ipoData.bookPriceTo.formatPriceWithoutDecimal()}"
                else ipoData.offeringPrice.formatPriceWithoutDecimal()
                binding.tvFinalPrice.text = price
            }
        }

        viewModel.getIpoInfoForOrderResult.observe(viewLifecycleOwner) { ipoData ->
            if (ipoData != null) {
                eipoSharedViewModel.setIpoData(ipoData)

                prospectusLink = ipoData.prospectusLink
                summaryLink = ipoData.summaryProspectusLink

                val stockCode = GET_4_CHAR_STOCK_CODE(ipoData.code)

                val url = if (ipoData.logoLink.isNotEmpty()) prefManager.urlIcon + ipoData.logoLink else prefManager.urlIcon + stockCode

                Glide.with(requireActivity())
                    .load(url)
                    .override(300, 200)
                    .circleCrop()
                    .placeholder(R.drawable.bg_circle)
                    .error(R.drawable.bg_circle)
                    .into(binding.ivLogo)

                binding.tvStockName.text = ipoData.companyName
                binding.tvStockCode.text = ipoData.code

                val price = if (ipoData.status == 1) "${ipoData.bookPriceFrom.formatPriceWithoutDecimal()} - ${ipoData.bookPriceTo.formatPriceWithoutDecimal()}"
                else ipoData.offeringPrice.formatPriceWithoutDecimal()
                binding.tvFinalPrice.text = price

                if (binding.buttonOrder.isEnabled) {
                    val bundle = Bundle().apply {
                        putString(Args.EXTRA_PARAM_STR_ONE, stockCode)
                        putString(Args.EXTRA_PARAM_STR_TWO, binding.tvStockName.text.toString())
                    }
                    findNavController().navigate(R.id.eipo_order_fragment, bundle)
                    viewModel.getIpoInfoForOrderResult.postValue(null)
                }
            }
        }

        eipoSharedViewModel.isHasOrder.observe(viewLifecycleOwner) { stagesOrderStatus ->
            binding.lyButtonStockDetail.isGone = stagesOrderStatus.status == "1" || stagesOrderStatus.status == "6" || stagesOrderStatus.status == "4" || stagesOrderStatus.status == "9"
            if (stagesOrderStatus.status == "Waiting") {
                binding.buttonOrder.isEnabled = false
            } else {
                binding.buttonOrder.isEnabled = stagesOrderStatus.stages <= 2
            }
        }
    }

}
