package com.bcasekuritas.mybest.app.feature.brokersummary.tabactivity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.databinding.ItemActivityCodeTabBrokerSummaryBinding
import com.bcasekuritas.mybest.databinding.ItemActivityTabBrokerSummaryBinding
import com.bcasekuritas.mybest.databinding.ItemStockDetailTabBrokerSummaryBinding
import com.bcasekuritas.mybest.ext.common.initFormatThousandSeparator
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankActivityDiscover
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankByStockDiscover

class ActivityStockCodeTabBrokerSummaryAdapter: RecyclerView.Adapter<BaseViewHolder>(){
    private val data: ArrayList<String> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemActivityCodeTabBrokerSummaryBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(data[position])

    inner class ItemViewHolder(
        val binding: ItemActivityCodeTabBrokerSummaryBinding
    ): BaseViewHolder(binding.root){
        override fun onBind(obj: Any) {
            val item = obj as String

            binding.tvCode.text = item

        }
    }

    fun setData(list: List<String>?) {
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