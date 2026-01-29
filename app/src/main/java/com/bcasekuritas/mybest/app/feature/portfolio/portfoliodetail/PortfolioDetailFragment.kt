package com.bcasekuritas.mybest.app.feature.portfolio.portfoliodetail

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.PortfolioStockDataItem
import com.bcasekuritas.mybest.app.feature.portfolio.portfoliodetail.adapter.PortfolioDetailPagerAdapter
import com.bcasekuritas.mybest.databinding.FragmentPortfolioDetailBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.converter.GET_4_CHAR_STOCK_CODE
import com.bcasekuritas.mybest.ext.converter.GET_IDX_BOARD
import com.bcasekuritas.mybest.ext.other.formatPercentThousand
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.property.orZero
import com.bcasekuritas.mybest.ext.view.setSafeOnClickListener
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class PortfolioDetailFragment : BaseFragment<FragmentPortfolioDetailBinding, PortfolioDetailViewModel>() {

    override val viewModel: PortfolioDetailViewModel by viewModels()
    override val binding: FragmentPortfolioDetailBinding by autoCleaned { (FragmentPortfolioDetailBinding.inflate(layoutInflater)) }
    override val bindingVariable: Int = BR.vmPortfolioDetail

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var sharedViewModel: PortfolioDetailSharedViewModel

    private var data: PortfolioStockDataItem? = PortfolioStockDataItem()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(PortfolioDetailSharedViewModel::class.java)
    }

    override fun setupArguments() {
        super.setupArguments()
        arguments?.let {
            data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(Args.EXTRA_PARAM_OBJECT, PortfolioStockDataItem::class.java)
            } else {
                it.getParcelable(Args.EXTRA_PARAM_OBJECT)
            }
        }

        data?.let { sharedViewModel.setDataPortfolioDetail(it) }

    }

    override fun initAPI() {
        super.initAPI()
        viewModel.getStockParam(data!!.stockcode)
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.getStockParamResult.observe(viewLifecycleOwner) {
            binding.tvStockName.text = it?.stockName
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {

            binding.toolbar.ivLayoutToolbarMasterIconRightOne.visibility = View.VISIBLE
            binding.toolbar.ivLayoutToolbarMasterIconRightOne.setImageResource(R.drawable.ic_share_outline)

            toolbar.tvLayoutToolbarMasterTitle.text = "Portfolio Detail"
            tvStockCode.text = data?.stockcode
            tvStockName.text = "-"

            tvNotation.visibility = if (data?.notation?.isNotEmpty() == true) View.VISIBLE else View.GONE
            tvNotation.text = data?.notation
            tvInfoSpecialNotesAcceleration.text = data?.idxBoard.GET_IDX_BOARD()
            tvInfoSpecialNotesAcceleration.visibility = if (binding.tvInfoSpecialNotesAcceleration.text != "") View.VISIBLE else View.GONE

            val url = prefManager.urlIcon+ GET_4_CHAR_STOCK_CODE(data?.stockcode?:"")

            Glide.with(requireContext())
                .load(url)
                .circleCrop()
                .placeholder(R.drawable.bg_circle)
                .error(R.drawable.bg_circle)
                .into(ivStock)
        }
    }

    override fun setupAdapter() {
        super.setupAdapter()
        viewPager = binding.viewpager
        tabLayout = binding.tabLayoutPortfolioDetail

        val adapter = PortfolioDetailPagerAdapter(this)
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position+1) {
                1 -> "Portfolio"
                2 -> "History"
                else -> {""}
            }
        }.attach()


        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.toolbar.ivLayoutToolbarMasterIconRightOne.isGone = position != 0
            }
        })
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.apply {
            toolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener {
                requireActivity().finish()
            }

            toolbar.ivLayoutToolbarMasterIconRightOne.setOnClickListener {
                if (data?.profitLoss.orZero() > 0.0) {

                    showDialogSharePortfolioDetail(
                        data?.stockcode.orEmpty(),
                        data?.reffprice?.formatPriceWithoutDecimal().orEmpty(),
                        data?.avgprice?.formatPriceWithoutDecimal().orEmpty(),
                        "+${data?.pct?.formatPercentThousand()}%",
                        true,
                        parentFragmentManager )

                } else if (data?.profitLoss.orZero() < 0) {

                    showDialogSharePortfolioDetail(
                        data?.stockcode.orEmpty(),
                        data?.reffprice?.formatPriceWithoutDecimal().orEmpty(),
                        data?.avgprice?.formatPriceWithoutDecimal().orEmpty(),
                        "${data?.pct?.formatPercentThousand()}%",
                        false,
                        parentFragmentManager )

                } else {

                    showDialogSharePortfolioDetail(
                        data?.stockcode.orEmpty(),
                        data?.reffprice?.formatPriceWithoutDecimal().orEmpty(),
                        data?.avgprice?.formatPriceWithoutDecimal().orEmpty(),
                        "+${data?.pct?.formatPercentThousand()}%",
                        true,
                        parentFragmentManager )
                }
            }

            btnFast.setOnClickListener {
                val bundleBuy = Bundle().apply {
                    putString(Args.EXTRA_PARAM_STR_ONE, data?.stockcode)
                    putString(Args.EXTRA_PARAM_STR_TWO, binding.tvStockName.text.toString())
                }
                findNavController().navigate(R.id.fast_order_fragment, bundleBuy)
            }

            btnSell.setOnClickListener {
                val bundleSell = Bundle().apply {
                    putInt(Args.EXTRA_PARAM_INT_ONE, 1) // tab sell = 1
                    putString(Args.EXTRA_PARAM_STR_ONE, data?.stockcode)
                }
                findNavController().navigate(R.id.order_fragment, bundleSell)
            }

            btnBuy.setOnClickListener {
                val bundleBuy = Bundle().apply {
                    putInt(Args.EXTRA_PARAM_INT_ONE, 0)  // tab buy = 0
                    putString(Args.EXTRA_PARAM_STR_ONE, data?.stockcode)
                }
                findNavController().navigate(R.id.order_fragment, bundleBuy)

            }

            lyStockInfo.setSafeOnClickListener {
                val bundle = Bundle().apply {
                    putString(Args.EXTRA_PARAM_STR_ONE, data?.stockcode)
                }
                findNavController().navigate(R.id.stock_detail_fragment, bundle)
            }
        }

    }


}