package com.bcasekuritas.mybest.app.feature.stockdetail.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.databinding.ItemStockDetailDailyTwoBinding
import com.bcasekuritas.mybest.ext.other.formatPercent
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.rabbitmq.proto.chart.Cf.CFMessage.TradeSummary

class StockDetailDailyTwoAdapter : RecyclerView.Adapter<BaseViewHolder>(){
    private val data: ArrayList<TradeSummary> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemStockDetailDailyTwoBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(data[position])

    inner class ItemViewHolder(
        val binding: ItemStockDetailDailyTwoBinding
    ): BaseViewHolder(binding.root){
        override fun onBind(obj: Any) {
            val item = obj as TradeSummary

            binding.tvItemLastPrice.text = item.last.formatPriceWithoutDecimal()
            binding.tvItemChange.text = item.change.formatPriceWithoutDecimal()
            binding.tvItemPercent.text = "${item.changePct.formatPercent()}%"
            binding.tvItemOpen.text = item.open.formatPriceWithoutDecimal()
            binding.tvItemHigh.text = item.high.formatPriceWithoutDecimal()
            binding.tvItemLow.text = item.low.formatPriceWithoutDecimal()
            binding.tvItemValue.text = item.tradeValue.formatPriceWithoutDecimal()
            binding.tvItemVolume.text = item.tradeVolume.formatPriceWithoutDecimal()
            binding.tvItemFreq.text = item.tradeFreq.formatPriceWithoutDecimal()

            val colorLast = if (item.colorLast.isNotEmpty()) item.colorLast else "#000000"
            val colorOpen = if (item.colorOpen.isNotEmpty()) item.colorOpen else "#000000"
            val colorHigh = if (item.colorHigh.isNotEmpty()) item.colorHigh else "#000000"
            val colorLow = if (item.colorLow.isNotEmpty()) item.colorLow else "#000000"

            binding.tvItemLastPrice.setTextColor(Color.parseColor(colorLast))
            binding.tvItemOpen.setTextColor(Color.parseColor(colorOpen))
            binding.tvItemHigh.setTextColor(Color.parseColor(colorHigh))
            binding.tvItemLow.setTextColor(Color.parseColor(colorLow))

            if (item.change > 0) {
                binding.tvItemChange.setTextColor(ContextCompat.getColor(itemView.context, R.color.textUp))
                binding.tvItemPercent.setTextColor(ContextCompat.getColor(itemView.context, R.color.textUp))
            } else if (item.change < 0) {
                binding.tvItemChange.setTextColor(ContextCompat.getColor(itemView.context, R.color.textDown))
                binding.tvItemPercent.setTextColor(ContextCompat.getColor(itemView.context, R.color.textDown))
            } else {
                binding.tvItemChange.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                binding.tvItemPercent.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
            }

        }
    }

    fun setData(list: List<TradeSummary>?) {
        if (list == null) return
        val startPosition = data.size
        data.addAll(list) // Modify data only once
        notifyItemRangeInserted(startPosition, list.size)
    }

    fun clearData(){
        data.clear()
        notifyDataSetChanged()
    }
}