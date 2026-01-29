package com.bcasekuritas.mybest.app.feature.e_ipo.eipodetail.stages

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.IpoStatusOrder
import com.bcasekuritas.mybest.app.feature.e_ipo.eipodetail.EIPODetailSharedViewModel
import com.bcasekuritas.mybest.databinding.FragmentEIPOStagesBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.converter.GET_STATUS_IPO_ORDER_LIST
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import timber.log.Timber

@FragmentScoped
@AndroidEntryPoint
class EIPOStagesFragment : BaseFragment<FragmentEIPOStagesBinding, EIPOStagesViewModel>() {
    override val viewModel: EIPOStagesViewModel by viewModels()
    override val binding: FragmentEIPOStagesBinding by autoCleaned { (FragmentEIPOStagesBinding.inflate(layoutInflater)) }
    override val bindingVariable: Int = BR.vmEIPOStages

    private lateinit var eipoSharedViewModel: EIPODetailSharedViewModel

    private var stages = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eipoSharedViewModel = ViewModelProvider(requireActivity()).get(EIPODetailSharedViewModel::class.java)
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {

        }
    }

    @SuppressLint("SetTextI18n")
    override fun setupObserver() {
        super.setupObserver()
        eipoSharedViewModel.getIpoData.observe(viewLifecycleOwner) {ipoData ->
            if (ipoData != null) {
                if (ipoData.code.isNotEmpty()) {
                    viewModel.getEipoOrderList(prefManager.userId, prefManager.sessionId, prefManager.accno, ipoData.code, 1, 10)
                }
                stages = ipoData.status

                if (ipoData.status != 0) {
                    setStep(if (stages == 8) 1 else ipoData.status)
                }
                if (stages == 8) {
                    binding.tvInfoIpo.text = "Please wait for the offering time to place an order"
                    eipoSharedViewModel.setIsHasOrder(IpoStatusOrder(true, stages, "Waiting"))
                    binding.tvInfoIpo.isGone = false
                } else {
                    eipoSharedViewModel.setIsHasOrder(IpoStatusOrder(false, stages, ""))
                    binding.tvInfoIpo.isGone = stages < 3
                }
                binding.tvOfferingPrice.isGone = stages < 3

                binding.apply {
                    val formatDateInput = "yyyy-MM-dd"
                    val formatDateBook = "dd MMM"
                    val formatDateAlloc = "dd MMM yyyy"

                    tvBookPrice.text = "${ipoData.bookPriceFrom.formatPriceWithoutDecimal()} - ${ipoData.bookPriceTo.formatPriceWithoutDecimal()}"
                    tvBookDate.text = "${DateUtils.formatDate(ipoData.bookPeriodStart, formatDateInput, formatDateBook)} - " +
                            DateUtils.formatDate(ipoData.bookPeriodEnd, formatDateInput, formatDateBook)

                    tvOfferingPrice.text = ipoData.offeringPrice.formatPriceWithoutDecimal()
                    tvOfferingDate.text = "${DateUtils.formatDate(ipoData.offeringPeriodStart, formatDateInput, formatDateBook)} - " +
                            DateUtils.formatDate(ipoData.offeringPeriodEnd, formatDateInput, formatDateBook)

                    tvAllocationDate.text = DateUtils.formatDate(ipoData.allotmentDate, formatDateInput, formatDateAlloc)
                    tvDistributionDate.text = DateUtils.formatDate(ipoData.distDate, formatDateInput, formatDateAlloc)
                    tvEipoDate.text = DateUtils.formatDate(ipoData.listingDate, formatDateInput, formatDateAlloc)
                }
            }
        }

        viewModel.getEipoOrderListResult.observe(viewLifecycleOwner) { orderList ->
            if (orderList.isNotEmpty()) {
                binding.apply {
                    val sortData = orderList.sortedByDescending { it.createdAt }
                    val isHasApproveOrder = sortData.filter { it.statusId == "1" || it.statusId == "6" || it.statusId == "4" || it.statusId == "9" }
                    //Timber.tag("getEipoOrderListResult").d("countFilter: ${isHasApproveOrder.size}")
                    val data = if (isHasApproveOrder.isNotEmpty()) isHasApproveOrder[0] else orderList[0]
                    //Timber.tag("getEipoOrderListResult").d("data: ${data.toString()}")
                    val price = data.orderPrice
                    val qty = data.orderQty.times(100)
                    val lot = qty.div(100)
                    val total = price.times(qty)

                    tvTotalIpoVal.text = "Rp ${total.formatPriceWithoutDecimal()}"
                    tvPriceIpoVal.text = price.formatPriceWithoutDecimal()
                    tvLotIpoVal.text = lot.formatPriceWithoutDecimal()
                    tvStatusIpo.text = data.statusId.GET_STATUS_IPO_ORDER_LIST().uppercase()
                    var remarks = ""
                    when (data.statusId) {
                        "0" -> {
                            tvStatusIpo.setTextColor(ContextCompat.getColor(root.context, R.color.brandSecondaryBlue))
                            ivOrderInfoEipo.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.brandSecondaryBlue))
                        }
                        "1" -> {
                            tvStatusIpo.setTextColor(ContextCompat.getColor(root.context, R.color.textUp))
                            tvOrderInfoEipo.setTextColor(ContextCompat.getColor(root.context, R.color.brandSecondaryBlue))
                            ivOrderInfoEipo.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.brandSecondaryBlue))
                            remarks = getString(R.string.info_eipo_status_approved)
                        }
                        "3", "2" -> {
                            tvStatusIpo.setTextColor(ContextCompat.getColor(root.context, R.color.txtBlackWhite))
                            ivOrderInfoEipo.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.textPrimary))
                            tvOrderInfoEipo.setTextColor(ContextCompat.getColor(root.context, R.color.txtBlackWhite))
                            remarks = data.dropReason
                        }
                        "4" -> {
                            tvStatusIpo.setTextColor(ContextCompat.getColor(root.context, R.color.textDown))
                            ivOrderInfoEipo.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.textDown))
                            tvOrderInfoEipo.setTextColor(ContextCompat.getColor(root.context, R.color.textDown))
                            remarks = data.dropReason
                        }
                        else -> {
                            tvStatusIpo.setTextColor(ContextCompat.getColor(root.context, R.color.txtBlackWhite))
                            ivOrderInfoEipo.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.txtBlackWhite))
                            remarks = data.dropReason
                        }
                    }
                    lyMyIpoInfo.isGone = true
                    tvOrderInfoEipo.text = remarks

                    if (stages > 2) {
                        if (data.statusId == "1" || data.statusId == "6" || data.statusId == "4" || data.statusId == "9") {
                            binding.tvInfoIpo.isGone = true
                            binding.groupMyIpo.isGone = false

                            binding.tvTotalIpo.text = "Lot Allocation"
                            binding.tvTotalIpoVal.text = if (data.allotmentQty > 0.0) data.allotmentQty.formatPriceWithoutDecimal() else "-"
                        } else {
                            binding.tvInfoIpo.isGone = false
                            binding.groupMyIpo.isGone = true
                        }
                    } else {
                        binding.tvInfoIpo.isGone = true
                        binding.groupMyIpo.isGone = false
                    }

                    eipoSharedViewModel.setIsHasOrder(IpoStatusOrder(true, stages, data.statusId))
                }
            }
        }
    }

    private fun setStep(step: Int) {
        val black = ContextCompat.getColor(requireContext(), R.color.txtBlackWhite)
        val green = ContextCompat.getColor(requireContext(), R.color.textUp)
        binding.apply {
            val titles = listOf(
                tvBookTitle,
                tvOfferingTitle,
                tvAllocationTitle,
                tvDistributionTitle,
                tvIpoTitle
            )

            val dots = listOf(
                ivDotBookBuilding,
                ivDotOffering,
                ivDotAllocation,
                ivDotDistribution,
                ivDotIpo
            )

            val connectors = listOf(
                vwBookToOffer,
                vwOfferToAllocation,
                vwAllocToDistrib,
                vwDistribToIpo
            )

            dots.forEachIndexed { index, imageView ->
                if (index < step) {
                    imageView.setColorFilter(green)
                    titles[index].setTextColor(black)

                    // Set connector color if it's not the first or last step
                    if (index > 0 && index - 1 < connectors.size) {
                        connectors[index - 1].setBackgroundColor(green)
                    }
                }
            }

        }

    }
}