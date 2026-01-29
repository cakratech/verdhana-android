package com.bcasekuritas.mybest.app.feature.brokersummary.tabactivity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.databinding.ItemActivityTabBrokerSummaryBinding
import com.bcasekuritas.mybest.ext.other.formatPriceWithTwoDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankActivityDiscover

class ActivityTabBrokerSummaryAdapter: RecyclerView.Adapter<BaseViewHolder>(){
    private val data: ArrayList<BrokerRankActivityDiscover> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemActivityTabBrokerSummaryBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(data[position])

    inner class ItemViewHolder(
        val binding: ItemActivityTabBrokerSummaryBinding
    ): BaseViewHolder(binding.root){
        override fun onBind(obj: Any) {
            val item = obj as BrokerRankActivityDiscover

            binding.apply {
                tvBoard.text = item.board
                tvItemBuyAvg.text = item.buyActivity.avg.formatPriceWithTwoDecimal()
                tvItemBuyVal.text = (item.buyActivity.`val` / 1000).formatPriceWithoutDecimal()
                tvItemBuyVol.text = (item.buyActivity.vol / 100).formatPriceWithoutDecimal()

                tvItemSellVol.text = (item.sellActivity.vol / 100).formatPriceWithoutDecimal()
                tvItemSellVal.text = (item.sellActivity.`val` / 1000).formatPriceWithoutDecimal()
                tvItemSellAvg.text = item.sellActivity.avg.formatPriceWithTwoDecimal()

                tvItemNetVal.text = (item.netValue / 1000).formatPriceWithoutDecimal()
                tvItemNetVol.text = (item.netVolume / 100).formatPriceWithoutDecimal()

                tvItemTotalVal.text = (item.totalValue / 1000).formatPriceWithoutDecimal()
                tvItemTotalVol.text = (item.totalVolume / 100).formatPriceWithoutDecimal()
                tvItemTotalFreq.text = item.totalFreq.formatPriceWithoutDecimal()
            }

        }
    }

    fun setData(list: List<BrokerRankActivityDiscover>?) {
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