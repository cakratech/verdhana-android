package com.bcasekuritas.mybest.app.feature.global.rank.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.databinding.ItemGlobalRankBinding
import com.bcasekuritas.mybest.ext.other.formatPercent
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankGlobalActivityDiscover

class GlobalRankAdapter: RecyclerView.Adapter<BaseViewHolder>(){
    private val data: ArrayList<BrokerRankGlobalActivityDiscover> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemGlobalRankBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(data[position])

    inner class ItemViewHolder(
        val binding: ItemGlobalRankBinding
    ): BaseViewHolder(binding.root){
        override fun onBind(obj: Any) {
            val item = obj as BrokerRankGlobalActivityDiscover

            binding.apply {
                val netValue  = if(item.netValBuy != 0.0) item.netValBuy else item.netValSell
                tvBoard.text = item.board
                tvValue.text = item.value.div(1000).formatPriceWithoutDecimal()
                tvVolume.text = item.volume.formatPriceWithoutDecimal()
                tvFreq.text = item.freq.formatPriceWithoutDecimal()
                tvValueBuy.text = item.valBuy.div(1000).formatPriceWithoutDecimal()
                tvValueSell.text = item.valSell.div(1000).formatPriceWithoutDecimal()
                tvNetValue.text = netValue.div(1000).formatPriceWithoutDecimal()
                tvLastPrice.text = item.lastPrice.formatPriceWithoutDecimal()
                tvChange.text = item.change.formatPriceWithoutDecimal()

                val changePct = item.changePct.times(100)
                tvChangePct.text = changePct.formatPercent()
            }
        }
    }

    fun setData(list: List<BrokerRankGlobalActivityDiscover>?) {
        val startPosition = data.size
        if (list == null) return
        data.addAll(list)
        notifyItemRangeInserted(startPosition, list.size)
    }

    fun clearData(){
        if (data.isNotEmpty()){
            data.clear()
            notifyDataSetChanged()
        }
    }
}