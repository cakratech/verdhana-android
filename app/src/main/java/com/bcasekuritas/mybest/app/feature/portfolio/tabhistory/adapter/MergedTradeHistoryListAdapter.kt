package com.bcasekuritas.mybest.app.feature.portfolio.tabhistory.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.domain.dto.response.TradeListInfo
import com.bcasekuritas.mybest.databinding.ItemMergeTradeHistoryListBinding
import com.bcasekuritas.mybest.ext.converter.GET_STATUS_BUY_SELL
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import java.text.SimpleDateFormat
import java.util.Date

class MergedTradeHistoryListAdapter(
    val context: Context,
    val onClickItem: OnClickAny,
) : RecyclerView.Adapter<MergedTradeHistoryListAdapter.ItemViewHolder>() {

    private val listData: ArrayList<TradeListInfo> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<TradeListInfo>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun cleardata() {
        listData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemMergeTradeHistoryListBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemMergeTradeHistoryListBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: TradeListInfo) {
            binding.apply {
                val lot = data.orderQty / 100
                val amout = data.price.times(data.orderQty)
                val date: Date = data.time.toLong().let { Date(it) }

                tvStockCode.text = data.stockCode
                tvBuySell.text = data.buySell.GET_STATUS_BUY_SELL()
                tvAmount.text = "Rp" + amout.formatPriceWithoutDecimal()
                tvQty.text = lot.formatPriceWithoutDecimal() + " Lot"
                tvStatus.text = "Matched"
                tvPrice.text = data.price.formatPriceWithoutDecimal()
                tvDate.text = date.let { SimpleDateFormat("dd MMM yyyy, HH:mm").format(it) }

                if (data.buySell == "B") {
                    tvBuySell.setTextColor(ContextCompat.getColor(context, R.color.textUp))
                } else {
                    tvBuySell.setTextColor(ContextCompat.getColor(context, R.color.textDown))
                }

                lyItem.setOnClickListener {
                    onClickItem.onClickAny(data)
                }
            }

        }

    }
}