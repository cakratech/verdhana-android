package com.bcasekuritas.mybest.app.feature.dialog.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.request.SendOrderReq
import com.bcasekuritas.mybest.databinding.ItemOrderFastOrderBinding
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal

class DialogOrderFastOrderAdapter () :
    RecyclerView.Adapter<BaseViewHolder>() {

    private val data: ArrayList<SendOrderReq> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemOrderFastOrderBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(data[position])

    }

    inner class ItemViewHolder(
        val binding: ItemOrderFastOrderBinding
    ) : BaseViewHolder(binding.root) {
        @SuppressLint("SuspiciousIndentation")
        override fun onBind(obj: Any) {
            val item = obj as SendOrderReq
            binding.apply {
                val lot = item.ordQty?.div(100)
                val subTotal = item.ordPrice?.times(item.ordQty ?: 0.0)

                tvLotPrice.text = "${lot?.formatPriceWithoutDecimal()} Lot, ${item.ordPrice}"
                tvSubTotal.text = "${subTotal?.formatPriceWithoutDecimal()}"
            }

        }
    }

    fun setData(list: List<SendOrderReq>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

}