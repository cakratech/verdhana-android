package com.bcasekuritas.mybest.app.feature.portfolio.portfoliodetail.tabportfolio

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.PortfolioStockDataItem
import com.bcasekuritas.mybest.app.feature.portfolio.portfoliodetail.PortfolioDetailSharedViewModel
import com.bcasekuritas.mybest.databinding.FragmentTabPortfolioDetailBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.other.formatLotRoundingDown
import com.bcasekuritas.mybest.ext.other.formatPercent
import com.bcasekuritas.mybest.ext.other.formatPercentThousand
import com.bcasekuritas.mybest.ext.other.formatPrice
import com.bcasekuritas.mybest.ext.other.formatPriceWithDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class PortfolioTabPortfolioDetailFragment : BaseFragment<FragmentTabPortfolioDetailBinding, PortfolioTabPortfolioDetailViewModel>() {

    override val viewModel: PortfolioTabPortfolioDetailViewModel by viewModels()
    override val binding: FragmentTabPortfolioDetailBinding by autoCleaned { (FragmentTabPortfolioDetailBinding.inflate(layoutInflater)) }
    override val bindingVariable: Int = BR.vmPortfolioTabPortfolioDetail

    private lateinit var sharedViewModel: PortfolioDetailSharedViewModel

    private var data: PortfolioStockDataItem? = PortfolioStockDataItem()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(PortfolioDetailSharedViewModel::class.java)
    }

    override fun setupComponent() {
        super.setupComponent()
        var bool = false
        binding.apply {
            tvBalanceLot.setOnClickListener {
                if (!bool) {
                    lyWithCondition.visibility = View.VISIBLE
                    cardStopLossNoActive.visibility = View.GONE
                    bool = true
                } else {
                    bool = false
                    cardStopLossNoActive.visibility = View.VISIBLE
                    lyWithCondition.visibility = View.GONE
                }
            }
        }
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            cardStopLossNoActive.setOnClickListener {
                val bundle = Bundle().apply {
                    putParcelable(Args.EXTRA_PARAM_OBJECT, data)
                }
                findNavController().navigate(R.id.condition_advanced_fragment, bundle)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPortfolioDetail(prefManager.userId, prefManager.sessionId, prefManager.accno, data?.stockcode?: "")
    }

    override fun setupObserver() {
        super.setupObserver()
        sharedViewModel.portfolioDetailData.observe(viewLifecycleOwner) {
            if (it.stockcode != "") {
                data = it
            }
        }

        viewModel.getPortfolioDetailResult.observe(viewLifecycleOwner) { res ->
            if (res != null) {
                data = res
                setUIData(res)
            } else {
                data?.let { setUIData(it) }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUIData(data: PortfolioStockDataItem) {
        binding.apply {
            val shares = data.qtyStock * 100
            val blockedLot = if (data.blockedLot != 0.0) {
                data.blockedLot.formatLotRoundingDown()
            } else "0"

            tvBalanceLot.text = data.qtyStock.formatLotRoundingDown()
            tvAmountOfShares.text = shares.formatPriceWithoutDecimal()
            tvAveragePrice.text = data.avgprice.formatPriceWithDecimal()
            tvAvailableLot.text = data.potentialLot.formatLotRoundingDown()
            tvCurrnetPrice.text = data.reffprice.formatPriceWithoutDecimal()
            tvInvested.text = data.totalAsset.formatPriceWithoutDecimal()
            tvMarketValue.text = data.value.formatPriceWithoutDecimal()
            tvHaircut.text = data.haircut.formatPriceWithoutDecimal() + "%"
            tvBlockedLot.text = blockedLot
            lyBlockedLot.visibility = if (data.blockedLot != 0.0) View.VISIBLE else View.GONE

            if (data.profitLoss > 0) {
                tvPotential.setTextColor(ContextCompat.getColor(requireContext(), R.color.textUp))
                tvPercentage.setTextColor(ContextCompat.getColor(requireContext(), R.color.textUp))
                tvPotential.text = "+${data.profitLoss.formatPriceWithoutDecimal()}"
                tvPercentage.text = "+${data.pct.formatPercentThousand()}%"
            } else if (data.profitLoss < 0) {
                tvPotential.setTextColor(ContextCompat.getColor(requireContext(), R.color.textDown))
                tvPercentage.setTextColor(ContextCompat.getColor(requireContext(), R.color.textDown))
                tvPotential.text = "${data.profitLoss.formatPriceWithoutDecimal()}"
                tvPercentage.text = "${data.pct.formatPercentThousand()}%"
            } else {
                tvPotential.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                tvPercentage.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                tvPotential.text = "${data.profitLoss.formatPriceWithoutDecimal()}"
                tvPercentage.text = "${data.pct.formatPercentThousand()}%"
            }

        }
    }

}