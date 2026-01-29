package com.bcasekuritas.mybest.app.feature.stockdetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.response.TradeDetail
import com.bcasekuritas.mybest.app.domain.dto.response.TradeDetailData
import com.bcasekuritas.mybest.app.domain.dto.response.source.TradeDetailDataRes
import com.bcasekuritas.mybest.databinding.ItemTradePriceBinding
import com.bcasekuritas.mybest.ext.common.initFormatThousandSeparator
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import java.util.Collections
import kotlin.math.min

class TradePriceAdapter : RecyclerView.Adapter<BaseViewHolder>(){
    private var data: ArrayList<TradeDetailData> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemTradePriceBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(data[position])

    inner class ItemViewHolder(
        val binding: ItemTradePriceBinding
    ): BaseViewHolder(binding.root){
        override fun onBind(obj: Any) {
            val item = obj as TradeDetailData

            val value = (item.price.times(item.totalLot)) / 10
            binding.tvTradeTablePrice.text = item.price.formatPriceWithoutDecimal()
            binding.tvVolume.text = item.totalLot.formatPriceWithoutDecimal()
            binding.tvFreq.text = item.totalFreq.formatPriceWithoutDecimal()
            binding.tvValue.text = value.formatPriceWithoutDecimal()
        }
    }

    fun setData(newData: List<TradeDetailData>) {
        if (newData.size != 0) {
            data.clear()
            data.addAll(newData)
            notifyDataSetChanged()
        }
    }

    fun clearData() {
        data.clear()
        notifyDataSetChanged()
    }


}