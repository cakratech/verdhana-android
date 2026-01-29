package com.bcasekuritas.mybest.app.feature.e_ipo.orderlist.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.databinding.ItemEipoOrderListBinding
import com.bcasekuritas.mybest.ext.converter.GET_STATUS_IPO_ORDER_LIST
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.rabbitmq.proto.bcas.IpoOrderListData

class EipoOrderListAdapter(): RecyclerView.Adapter<EipoOrderListAdapter.ItemViewHolder>() {

    private val listData: ArrayList<IpoOrderListData> = arrayListOf()

    fun setData(list: List<IpoOrderListData>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addData(list: List<IpoOrderListData>) {
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun clearData() {
        listData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemEipoOrderListBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemEipoOrderListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: IpoOrderListData) {
            binding.apply {
                tvCode.text = data.pipeline
                tvLot.text = data.orderQty.formatPriceWithoutDecimal()
                tvPrice.text = data.orderPrice.formatPriceWithoutDecimal()
                tvAllot.text = if (data.allotmentQty > 0.0) data.allotmentQty.formatPriceWithoutDecimal() else "-"
                tvAmount.text = "Rp"+ data.orderTotal.formatPriceWithoutDecimal()
                tvStatus.text = data.statusId.GET_STATUS_IPO_ORDER_LIST().uppercase()

                val statusColor = when (data.statusId) {
                    "0" -> R.color.brandSecondaryBlue
                    "1" -> R.color.textUp
                    "4" -> R.color.textDown
                    else -> R.color.black
                }
                tvStatus.setTextColor(ContextCompat.getColor(root.context, statusColor))
            }


        }
    }
}