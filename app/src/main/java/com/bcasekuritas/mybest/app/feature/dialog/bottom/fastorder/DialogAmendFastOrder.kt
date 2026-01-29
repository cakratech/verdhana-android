package com.bcasekuritas.mybest.app.feature.dialog.bottom.fastorder

import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.domain.dto.request.AmendFastOrderReq
import com.bcasekuritas.mybest.app.domain.dto.response.QtyPriceItem
import com.bcasekuritas.mybest.app.feature.dialog.adapter.DialogListOrderFastOrderAdapter
import com.bcasekuritas.mybest.databinding.DialogAmendFastOrderBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class DialogAmendFastOrder(
    private val amendFastOrderReq: AmendFastOrderReq,
) :
    BaseBottomSheet<DialogAmendFastOrderBinding>() {

    @FragmentScoped
    override val binding: DialogAmendFastOrderBinding by autoCleaned { (DialogAmendFastOrderBinding.inflate(layoutInflater)) }

    private val adapterPreventFastOrder: DialogListOrderFastOrderAdapter by autoCleaned { DialogListOrderFastOrderAdapter() }

    private var confirmBtnClickListener: ((AmendFastOrderReq) -> Unit)? = null
    private val data: Any = amendFastOrderReq

    private var totalPrice = 0.0
    private var totalQty = 0.0

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            data as AmendFastOrderReq

            val totalOrder = data.sameOrderList.size
            tvTitle.text = "Amend (${totalOrder}) Order"

            if (data.buySell == "B"){
                tvBuySell.text = "Buy"
            } else {
                tvBuySell.text =  "Sell"
                tvBuySell.setBackgroundResource(R.drawable.bg_e14343_16_status)
            }

            totalQty = amendFastOrderReq.sameOrderList.sum()
            totalPrice = totalQty.times(amendFastOrderReq.oldPrice ?: 0.0)
            tvTotalVal.text = "Rp${totalPrice.formatPriceWithoutDecimal()}"

            Glide.with(requireContext())
                .load(prefManager.urlIcon+amendFastOrderReq.stock)
                .circleCrop()
                .placeholder(R.drawable.bg_circle)
                .error(R.drawable.bg_circle)
                .into(binding.ivLogo)

            tvDetailStockCode.text = amendFastOrderReq.stock
            tvDetailCompanyName.text = amendFastOrderReq.stockName

        }
    }



    override fun setupAdapter() {
        super.setupAdapter()

        binding.rcvTotalOrder.setHasFixedSize(true)
        binding.rcvTotalOrder.adapter = adapterPreventFastOrder

        val qtyPriceInfoList = amendFastOrderReq.sameOrderList.map { QtyPriceItem(it, amendFastOrderReq.oldPrice ?: 0.0) }

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

        binding.btnAddOrder.setOnClickListener {
            confirmBtnClickListener?.invoke(amendFastOrderReq)
            dismiss()

        }
    }
    fun setConfirmButtonClickListener(listener: (AmendFastOrderReq) -> Unit) {
        confirmBtnClickListener = listener
    }

}