package com.bcasekuritas.mybest.app.feature.stockdetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.response.SellOrderBookRes
import com.bcasekuritas.mybest.databinding.ItemOrderbookSellBinding
import com.bcasekuritas.mybest.ext.common.initFormatThousandSeparator
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import com.bcasekuritas.rabbitmq.proto.datafeed.OrderbookSummary
import timber.log.Timber
import kotlin.math.min
import kotlin.math.roundToInt

class OrderBookSellAdapter(private val onClickStr: OnClickStr) : RecyclerView.Adapter<BaseViewHolder>(){
    private val data: ArrayList<SellOrderBookRes> = arrayListOf()
    private var totalOffer: Long = 1
    private val limit = 10
    private var count = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemOrderbookSellBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = min(data.size, limit)

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(data[position])

    inner class ItemViewHolder(
        val binding: ItemOrderbookSellBinding
    ): BaseViewHolder(binding.root){
        override fun onBind(obj: Any) {
            val item = obj as SellOrderBookRes
            binding.tvOrderbookLotAsk.text = initFormatThousandSeparator(item.quantityL)
            binding.tvOrderbookAsk.text = initFormatThousandSeparator(item.price)

            val progressVal = kotlin.runCatching { item.quantityL.toInt() }.getOrElse { 0 }
            binding.progressIndicatorAsk.max = item.totQuantityL
            binding.progressIndicatorAsk.progress = progressVal

            binding.tvOrderbookAsk.setOnClickListener {
                onClickStr.onClickStr(item.price.toString())
            }

            if (item.price > item.prevPrice) {
                binding.tvOrderbookAsk.setTextColor(ContextCompat.getColor(itemView.context, R.color.textUp))
            } else if (item.price < item.prevPrice) {
                binding.tvOrderbookAsk.setTextColor(ContextCompat.getColor(itemView.context, R.color.textDown))
            }
            else {
                binding.tvOrderbookAsk.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
            }
        }
    }

    fun setData(list: List<SellOrderBookRes>?) {
        if (list == null) return
        totalOffer = 0
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }
}