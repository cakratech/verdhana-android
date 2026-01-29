package com.bcasekuritas.mybest.app.feature.dialog.bottom.fastorder

import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.domain.dto.request.CancelFastOrderReq
import com.bcasekuritas.mybest.app.domain.dto.response.QtyPriceItem
import com.bcasekuritas.mybest.app.feature.dialog.adapter.DialogListOrderFastOrderAdapter
import com.bcasekuritas.mybest.databinding.DialogWithdrawFastOrderBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class DialogWithdrawFastOrder(
    private val data: CancelFastOrderReq?,
) :
    BaseBottomSheet<DialogWithdrawFastOrderBinding>() {

    @FragmentScoped
    override val binding: DialogWithdrawFastOrderBinding by autoCleaned {
        (DialogWithdrawFastOrderBinding.inflate(
            layoutInflater
        ))
    }

    private val adapterWithdrawFastOrder: DialogListOrderFastOrderAdapter by autoCleaned { DialogListOrderFastOrderAdapter() }

    private var confirmBtnClickListener: ((CancelFastOrderReq) -> Unit)? = null

    private var totalPrice = 0.0
    private var totalQty = 0.0


    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            val totalOrder = data?.sameOrderList?.size
            tvTitle.text = "Withdraw (${totalOrder}) Order"

            if (data?.buySell == "B"){
                tvBuySell.text = "Buy"
            } else {
                tvBuySell.text =  "Sell"
                tvBuySell.setBackgroundResource(R.drawable.bg_e14343_16_status)
            }

            totalQty = data?.sameOrderList?.sum() ?: 0.0
            totalPrice = totalQty.times(data?.price ?: 0.0)
            tvTotalVal.text = "Rp${totalPrice.formatPriceWithoutDecimal()}"

            Glide.with(requireContext())
                .load(prefManager.urlIcon+ (data?.stock ?: ""))
                .circleCrop()
                .placeholder(R.drawable.bg_circle)
                .error(R.drawable.bg_circle)
                .into(binding.ivLogo)

            tvDetailStockCode.text = data?.stock ?: ""
            tvDetailCompanyName.text = data?.stockName
        }
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
            confirmBtnClickListener?.invoke(data ?: CancelFastOrderReq())

        }
    }

    override fun setupAdapter() {
        super.setupAdapter()

        binding.rcvTotalOrder.setHasFixedSize(true)
        binding.rcvTotalOrder.adapter = adapterWithdrawFastOrder

        val qtyPriceInfoList = data?.sameOrderList?.map { QtyPriceItem(it, data.price ?: 0.0) } ?: listOf()

        adapterWithdrawFastOrder.setData(qtyPriceInfoList)
    }

    fun setConfirmButtonClickListener(listener: (CancelFastOrderReq) -> Unit) {
        confirmBtnClickListener = listener
    }
}