package com.bcasekuritas.mybest.app.feature.dialog.bottom.fastorder

import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.domain.dto.request.CancelFastOrderReq
import com.bcasekuritas.mybest.app.domain.dto.response.QtyPriceItem
import com.bcasekuritas.mybest.app.feature.dialog.adapter.DialogWithdrawAllAdapter
import com.bcasekuritas.mybest.databinding.DialogWithdrawAllFastOrderBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class DialogWithdrawAllFastOrder(
    private val listWithdrawAll: List<QtyPriceItem>,
    private val cancelFastOrder: CancelFastOrderReq?,
) :
    BaseBottomSheet<DialogWithdrawAllFastOrderBinding>() {

    @FragmentScoped
    override val binding: DialogWithdrawAllFastOrderBinding by autoCleaned {
        (DialogWithdrawAllFastOrderBinding.inflate(
            layoutInflater
        ))
    }

    private val dialogWithdrawAllFastOrderAdapter: DialogWithdrawAllAdapter by autoCleaned { DialogWithdrawAllAdapter() }

    private var confirmBtnClickListener: ((CancelFastOrderReq) -> Unit)? = null
    private val cancelFastOrders = cancelFastOrder
    private var totalPrice = 0.0


    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            val totalOrder = listWithdrawAll.size
            tvTitle.text = "Withdraw All (${totalOrder}) Order"

            if (cancelFastOrder?.buySell == "B"){
                tvBuySell.text = "Buy"
            } else {
                tvBuySell.text =  "Sell"
                tvBuySell.setBackgroundResource(R.drawable.bg_e14343_16_status)
            }

            Glide.with(requireContext())
                .load(prefManager.urlIcon+ (cancelFastOrder?.stock ?: ""))
                .circleCrop()
                .placeholder(R.drawable.bg_circle)
                .error(R.drawable.bg_circle)
                .into(binding.ivLogo)

            tvDetailStockCode.text = cancelFastOrder?.stock ?: ""
            tvDetailCompanyName.text = cancelFastOrder?.stockName

            dialogWithdrawAllFastOrderAdapter.setData(listWithdrawAll)

            listWithdrawAll.map {
                totalPrice = totalPrice.plus(it.price.times(it.qty))
            }

            tvTotalVal.text = "Rp${totalPrice.formatPriceWithoutDecimal()}"
        }
    }

    override fun setupAdapter() {
        super.setupAdapter()

        binding.rcvTotalOrder.setHasFixedSize(true)
        binding.rcvTotalOrder.adapter = dialogWithdrawAllFastOrderAdapter
    }


    override fun initOnClick() {
        super.initOnClick()


        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()

        }

        binding.btnWithdraw.setOnClickListener {
            dismiss()
            confirmBtnClickListener?.invoke(cancelFastOrders ?: CancelFastOrderReq())

        }
    }

    fun setConfirmButtonClickListener(listener: (CancelFastOrderReq) -> Unit) {
        confirmBtnClickListener = listener
    }

}