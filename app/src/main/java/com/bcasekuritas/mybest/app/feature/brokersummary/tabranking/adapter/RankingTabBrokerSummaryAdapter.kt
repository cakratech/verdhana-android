package com.bcasekuritas.mybest.app.feature.brokersummary.tabranking.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.databinding.ItemActivityTabBrokerSummaryBinding
import com.bcasekuritas.mybest.databinding.ItemRankingTabBrokerSummaryBinding
import com.bcasekuritas.mybest.databinding.ItemStockDetailTabBrokerSummaryBinding
import com.bcasekuritas.mybest.ext.common.initFormatThousandSeparator
import com.bcasekuritas.mybest.ext.other.formatPriceWithTwoDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankActivityDiscover
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankByStockDiscover
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankingDiscover

class RankingTabBrokerSummaryAdapter: RecyclerView.Adapter<BaseViewHolder>(){
    private val data: ArrayList<BrokerRankingDiscover> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemRankingTabBrokerSummaryBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(data[position])

    inner class ItemViewHolder(
        val binding: ItemRankingTabBrokerSummaryBinding
    ): BaseViewHolder(binding.root){
        override fun onBind(obj: Any) {
            val item = obj as BrokerRankingDiscover

            binding.tvValue.text = (item.`val` / 1000).formatPriceWithoutDecimal()
            binding.tvVolume.text = (item.vol / 100).formatPriceWithoutDecimal()
            binding.tvFreq.text = item.freq.formatPriceWithoutDecimal()
            binding.tvBrokerName.text = item.brokerName

        }
    }

    fun setData(list: List<BrokerRankingDiscover>?) {
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