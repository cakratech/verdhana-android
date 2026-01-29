package com.bcasekuritas.mybest.app.feature.dialog.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.response.QtyPriceItem
import com.bcasekuritas.mybest.databinding.ItemOrderFastOrderBinding
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal

class DialogListOrderFastOrderAdapter () :
    RecyclerView.Adapter<BaseViewHolder>() {

    private val data: ArrayList<QtyPriceItem> = arrayListOf()

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
            val item = obj as QtyPriceItem
            binding.apply {
                val lot = item.qty?.div(100)
                val total = item.qty.times(item.price)

                tvLotPrice.text = "${lot?.formatPriceWithoutDecimal()} Lot, ${item.price.formatPriceWithoutDecimal()}"
                tvSubTotal.text = "${total.formatPriceWithoutDecimal()}"
            }

        }
    }

    fun setData(list: List<QtyPriceItem>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

}