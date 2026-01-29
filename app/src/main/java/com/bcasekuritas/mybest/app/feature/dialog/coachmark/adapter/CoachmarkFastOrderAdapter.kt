package com.bcasekuritas.mybest.app.feature.dialog.coachmark.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.request.AmendFastOrderReq
import com.bcasekuritas.mybest.app.domain.dto.request.CancelFastOrderReq
import com.bcasekuritas.mybest.app.domain.dto.request.SendOrderReq
import com.bcasekuritas.mybest.app.domain.dto.response.CoachMarkFastOrder
import com.bcasekuritas.mybest.app.domain.dto.response.FastOrderBook
import com.bcasekuritas.mybest.databinding.ItemTradingOrderBinding
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.prefs.SharedPreferenceManager
import com.bcasekuritas.mybest.widget.textview.CustomTextView

class CoachmarkFastOrderAdapter(
    private val context: Context
) : RecyclerView.Adapter<CoachmarkFastOrderAdapter.ItemViewHolder>(),
    ShowDropDown by ShowDropDownImpl() {

    private val data: ArrayList<CoachMarkFastOrder> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemTradingOrderBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(data[position])
    }

    override fun getItemCount(): Int = data.size
    inner class ItemViewHolder(
        val binding: ItemTradingOrderBinding,
    ) : BaseViewHolder(binding.root) {
        fun onBind(data: CoachMarkFastOrder) {
            binding.apply {
                tvBuyValue.text = data.buyValue.ifEmpty { "" }
                tvBidValue.text = if (data.bid != 0.0) data.bid.formatPriceWithoutDecimal() else ""
                tvPriceValue.text = data.price.formatPriceWithoutDecimal()
                tvOfferValue.text = data.offer.formatPriceWithoutDecimal()
                tvSellValue.text = ""

                if (layoutPosition == itemCount) {
                    tvBidValue.setTextColor(ContextCompat.getColor(context, R.color.textUp))
                    tvPriceValue.setTextColor(ContextCompat.getColor(context, R.color.textUp))
                }
            }
        }
    }

    fun setData(list: List<CoachMarkFastOrder>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    private fun getPriceColor(tvPrice: CustomTextView, currentPrice: Double, closePrice: Double) {
        if (currentPrice > closePrice) {
            tvPrice.setTextColor(ContextCompat.getColor(context, R.color.textUp))
        } else if (currentPrice < closePrice) {
            tvPrice.setTextColor(ContextCompat.getColor(context, R.color.textDown))
        } else {
            tvPrice.setTextColor(ContextCompat.getColor(context, R.color.black))
        }
    }
}