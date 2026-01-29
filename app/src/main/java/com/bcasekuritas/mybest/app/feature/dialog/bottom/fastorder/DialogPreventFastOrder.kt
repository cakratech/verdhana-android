package com.bcasekuritas.mybest.app.feature.dialog.bottom.fastorder

import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.domain.dto.request.SendOrderReq
import com.bcasekuritas.mybest.app.domain.dto.response.QtyPriceItem
import com.bcasekuritas.mybest.app.feature.dialog.adapter.DialogListOrderFastOrderAdapter
import com.bcasekuritas.mybest.databinding.DialogPreventSameFastOrderBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class DialogPreventFastOrder(
    private val sendOrderReq: SendOrderReq
): BaseBottomSheet<DialogPreventSameFastOrderBinding>() {

    @FragmentScoped
    override val binding: DialogPreventSameFastOrderBinding by autoCleaned { (DialogPreventSameFastOrderBinding.inflate(layoutInflater)) }

    private var confirmBtnClickListener: ((SendOrderReq, Boolean) -> Unit)? = null
    private val adapterPreventFastOrder: DialogListOrderFastOrderAdapter by autoCleaned { DialogListOrderFastOrderAdapter() }

    private var totalPrice = 0.0
    private var totalQty = 0.0
    private var isCbChecked = false


    override fun setupComponent() {
        super.setupComponent()
        binding.apply {

            if (sendOrderReq.buySell == "B"){
                tvBuySell.text = "Buy"
            } else {
                tvBuySell.text =  "Sell"
                tvBuySell.setBackgroundResource(R.drawable.bg_e14343_16_status)
            }


            totalQty = sendOrderReq.sameOrderList.sum()
            totalPrice = totalQty.times(sendOrderReq.ordPrice ?: 0.0)
            tvTotalVal.text = totalPrice.formatPriceWithoutDecimal()

            Glide.with(requireContext())
                .load(prefManager.urlIcon+sendOrderReq.stockCode)
                .circleCrop()
                .placeholder(R.drawable.bg_circle)
                .error(R.drawable.bg_circle)
                .into(binding.ivLogo)

            tvDetailStockCode.text = sendOrderReq.stockCode
            tvDetailCompanyName.text = sendOrderReq.stockName
        }
    }

    override fun setupAdapter() {
        super.setupAdapter()

        binding.rcvTotalOrder.setHasFixedSize(true)
        binding.rcvTotalOrder.adapter = adapterPreventFastOrder

        val qtyPriceInfoList = sendOrderReq.sameOrderList.map { QtyPriceItem(it, sendOrderReq.ordPrice ?: 0.0) }

        adapterPreventFastOrder.setData(qtyPriceInfoList)
    }

    override fun initOnClick() {
        super.initOnClick()


        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.cbDontPreventOrder.setOnCheckedChangeListener { _, isChecked ->
            isCbChecked = isChecked
        }

        binding.btnAddOrder.setOnClickListener {
            dismiss()
            confirmBtnClickListener?.invoke(sendOrderReq, isCbChecked)

        }
    }

    fun setConfirmButtonClickListener(listener: (SendOrderReq, Boolean) -> Unit) {
        confirmBtnClickListener = listener
    }
}