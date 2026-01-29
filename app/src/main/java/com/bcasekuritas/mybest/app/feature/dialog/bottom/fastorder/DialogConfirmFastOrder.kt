package com.bcasekuritas.mybest.app.feature.dialog.bottom.fastorder

import androidx.core.view.isGone
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.domain.dto.request.CancelFastOrderReq
import com.bcasekuritas.mybest.app.domain.dto.request.SendOrderReq
import com.bcasekuritas.mybest.databinding.DialogConfirmFastOrderBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class DialogConfirmFastOrder(
    private val isWithdrawAll: Boolean,
    private val title: String,
    private val sendOrder: SendOrderReq?,
    private val cancelFastOrder: CancelFastOrderReq?,
    private val commFee: Double,
) :
    BaseBottomSheet<DialogConfirmFastOrderBinding>() {

    @FragmentScoped
    override val binding: DialogConfirmFastOrderBinding by autoCleaned { (DialogConfirmFastOrderBinding.inflate(layoutInflater)) }

    private var confirmBtnClickListener: ((SendOrderReq, CancelFastOrderReq) -> Unit)? = null
    private val sendOrders = sendOrder
    private val cancelFastOrders = cancelFastOrder
    private val data: Any = sendOrders ?: (cancelFastOrders ?: CancelFastOrderReq()) // Providing a default value if data2 is null


    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            if (isWithdrawAll) {
                groupRegularOrder.isGone = true
                groupWithdrawAllOrder.isGone = false
            }

            when(data) {
                is SendOrderReq ->{
                    val total = data.ordPrice?.times(data.ordQty?.times(100) ?: 0.0)
                    binding.tvPrice.text =  if (data.buySell == "B"){
                        "Buy Price"
                    } else {
                        "Sell Price"
                    }
                    setUpStockInfo(data.stockCode ?: "", data.stockName ?: "", data.ordPrice?.formatPriceWithoutDecimal() ?: "", data.ordQty?.formatPriceWithoutDecimal() ?: "", total?.plus(commFee)?.formatPriceWithoutDecimal() ?: "", commFee )
                }
                is CancelFastOrderReq ->{
                    val total = data.price?.times(data.qty?.times(100) ?: 0.0)
                    binding.tvPrice.text =  if (data.buySell == "B"){
                        "Buy Price"
                    } else {
                        "Sell Price"
                    }
                    setUpStockInfo(data.stock ?: "", data.stockName ?: "", data.price?.formatPriceWithoutDecimal() ?: "", data.qty?.formatPriceWithoutDecimal() ?: "", total?.formatPriceWithoutDecimal() ?: "", null )
                }
            }

            tvTitle.text = title
        }
    }

    fun setUpStockInfo(stockCode: String, companyName: String, price: String, qty: String, total: String, commFee: Double?){
        binding.apply {
            tvStockCode.text = stockCode
            tvCompanyName.text = companyName

            Glide.with(requireContext())
                .load(prefManager.urlIcon+stockCode)
                .circleCrop()
                .placeholder(R.drawable.bg_circle)
                .error(R.drawable.bg_circle)
                .into(binding.ivLogo)

            tvCommFeeVal.text = commFee?.formatPriceWithoutDecimal()
            tvPriceVal.text = price
            tvLotVal.text = qty
            tvTotalVal.text = "Rp$total"
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

        binding.btnConfirm.setOnClickListener {
            dismiss()
            confirmBtnClickListener?.invoke(sendOrder ?: SendOrderReq(), cancelFastOrder ?: CancelFastOrderReq())

        }
    }
    fun setConfirmButtonClickListener(listener: (SendOrderReq, CancelFastOrderReq) -> Unit) {
        confirmBtnClickListener = listener
    }

}