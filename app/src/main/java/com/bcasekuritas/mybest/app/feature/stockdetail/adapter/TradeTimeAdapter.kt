package com.bcasekuritas.mybest.app.feature.stockdetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.databinding.ItemTradeTimeBinding
import com.bcasekuritas.mybest.ext.common.initFormatThousandSeparator
import com.bcasekuritas.mybest.ext.date.DateUtils.convertLongToTime
import com.bcasekuritas.mybest.ext.other.formatPercent
import com.bcasekuritas.mybest.ext.other.formatPrice
import com.bcasekuritas.rabbitmq.proto.datafeed.TradeBookTime
import kotlin.math.min

class TradeTimeAdapter : RecyclerView.Adapter<BaseViewHolder>(){
    private var data: ArrayList<TradeBookTime> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemTradeTimeBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(data[position])

    inner class ItemViewHolder(
        val binding: ItemTradeTimeBinding
    ): BaseViewHolder(binding.root){
        override fun onBind(obj: Any) {
            val item = obj as TradeBookTime

            val buyPercent = item.buyLot.toDouble() / item.totalLot.toDouble() * 100
            val sellPercent = item.sellLot.toDouble() / item.totalLot.toDouble() * 100

            binding.tvTradeTableTime.text = convertLongToTime(item.tbtTime)
            binding.tvTradeTableBuyLot.text = initFormatThousandSeparator(item.buyLot.toDouble())
            binding.tvTradeTableSellLot.text = initFormatThousandSeparator(item.sellLot.toDouble())
            binding.tvTradeTablePercentBuy.text = "${buyPercent.formatPercent()}%"
            binding.tvTradeTablePercentSell.text = "${sellPercent.formatPercent()}%"
            binding.lpTradeTableChart.trackColor = if (buyPercent == 0.0 && sellPercent == 0.0) ContextCompat.getColor(binding.root.context, R.color.textTertiaryDashboard) else ContextCompat.getColor(binding.root.context, R.color.textDown)
            binding.lpTradeTableChart.progress = buyPercent.toInt()
        }
    }

    fun setData(list: List<TradeBookTime>) {
        if (list == null) return
        clearData()
        data.addAll(list)
        notifyDataSetChanged()
    }

    fun clearData() {
        data.clear()
        notifyDataSetChanged()
    }
}