package com.bcasekuritas.mybest.app.feature.portfolio.taborders.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.domain.dto.response.TradeListInfo
import com.bcasekuritas.mybest.databinding.ItemMergeTradeListBinding
import com.bcasekuritas.mybest.databinding.ItemTradeListBinding
import com.bcasekuritas.mybest.ext.common.timeInForce
import com.bcasekuritas.mybest.ext.converter.GET_STATUS_BUY_SELL
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.rabbitmq.proto.bcas.TradeInfo

class MergedTradeListAdapter(
    val context: Context,
    val onClickItem: OnClickAny,
) : RecyclerView.Adapter<MergedTradeListAdapter.ItemViewHolder>() {

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
        return ItemViewHolder(ItemMergeTradeListBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemMergeTradeListBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: TradeListInfo) {
            binding.apply {
                val lot = data.orderQty / 100
                val amout = data.price.times(data.orderQty)

                tvStockCode.text = data.stockCode
                tvBuySell.text = data.buySell.GET_STATUS_BUY_SELL()
                tvAmount.text = "Rp" + amout.formatPriceWithoutDecimal()
                tvQty.text = lot.formatPriceWithoutDecimal()
                tvStatus.text = "Matched"
                tvPrice.text = data.price.formatPriceWithoutDecimal()
                tvDate.text = timeInForce("0")

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