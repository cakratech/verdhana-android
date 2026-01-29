package com.bcasekuritas.mybest.app.feature.stockdetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.response.BrokerSummaryByStock
import com.bcasekuritas.mybest.databinding.ItemStockDetailTabBrokerSummaryBinding
import com.bcasekuritas.mybest.ext.common.initFormatThousandSeparator
import com.bcasekuritas.mybest.ext.converter.getRandomColorBrokerSum
import com.bcasekuritas.mybest.ext.other.formatPriceWithTwoDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimalWithoutMinus
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankByStockDiscover
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerStockSummary

class StockDetailBrokerSummaryAdapter : RecyclerView.Adapter<BaseViewHolder>(){
    private val data: ArrayList<BrokerSummaryByStock> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemStockDetailTabBrokerSummaryBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(data[position])

    inner class ItemViewHolder(
        val binding: ItemStockDetailTabBrokerSummaryBinding
    ): BaseViewHolder(binding.root){
        override fun onBind(obj: Any) {
            val item = obj as BrokerSummaryByStock
            val tag = layoutPosition+1

            binding.tvItemBuy.setTextColor(ContextCompat.getColor(binding.root.context, getRandomColorBrokerSum(tag)))
            binding.tvItemSell.setTextColor(ContextCompat.getColor(binding.root.context, getRandomColorBrokerSum(tag+3)))

            binding.tvItemBuy.text = item.brokerCodeBuy
            binding.tvItemBuyLot.text = (item.buyLot / 100).formatPriceWithoutDecimal()
            binding.tvItemBuyVal.text = (item.buyVal / 1000000).formatPriceWithoutDecimal()
            binding.tvItemBuyAvg.text = item.buyAvg.formatPriceWithTwoDecimal()
            binding.tvItemTag.text = tag.toString()
            binding.tvItemSell.text = item.brokerCodeSell
            binding.tvItemSellLot.text = (item.sellLot / 100).formatPriceWithoutDecimal()
            binding.tvItemSellVal.text = (item.sellVal / 1000000).formatPriceWithoutDecimal()
            binding.tvItemSellAvg.text = item.sellAvg.formatPriceWithTwoDecimal()

        }
    }

    fun setData(list: List<BrokerSummaryByStock>?) {
        val startPosition = data.size
        if (list == null) return
        data.addAll(list)
        notifyItemRangeInserted(startPosition, list.size)
    }

    fun clearData(){
        data.clear()
        notifyDataSetChanged()
    }
}