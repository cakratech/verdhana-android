package com.bcasekuritas.mybest.app.feature.stockdetail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.response.BuyOrderBookRes
import com.bcasekuritas.mybest.databinding.ItemOrderbookBuyBinding
import com.bcasekuritas.mybest.ext.common.initFormatThousandSeparator
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import kotlin.math.min

class OrderBookBuyAdapter(private val onClickStr: OnClickStr, private val context: Context) : RecyclerView.Adapter<BaseViewHolder>(){
    private val data: ArrayList<BuyOrderBookRes> = arrayListOf()
    private var totalBid: Long = 0
    private var count = 0
    private val limit = 10

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemOrderbookBuyBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = min(data.size, limit)

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(data[position])

    inner class ItemViewHolder(
        val binding: ItemOrderbookBuyBinding
    ): BaseViewHolder(binding.root){
        override fun onBind(obj: Any) {
            val item = obj as BuyOrderBookRes
            binding.tvOrderbookLotBid.text = initFormatThousandSeparator(item.quantityL)
            binding.tvOrderbookBid.text = initFormatThousandSeparator(item.price)

            val progressVal = kotlin.runCatching { item.quantityL.toInt() }.getOrElse { 0 }
            binding.progressIndicatorBid.max = item.totQuantityL
            binding.progressIndicatorBid.progress = progressVal

            binding.tvOrderbookBid.setOnClickListener {
                onClickStr.onClickStr(item.price.toString())
            }

            if (item.price > item.prevPrice) {
                binding.tvOrderbookBid.setTextColor(ContextCompat.getColor(itemView.context, R.color.textUp))
            } else if (item.price < item.prevPrice) {
                binding.tvOrderbookBid.setTextColor(ContextCompat.getColor(itemView.context, R.color.textDown))
            }
            else {
                binding.tvOrderbookBid.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
            }
        }
    }

    fun setData(list: List<BuyOrderBookRes>?) {
        if (list != null){
            clearData()
            data.addAll(list)
            notifyDataSetChanged()
        }
    }

    fun clearData(){
        totalBid = 0
        count = 0
        data.clear()
        notifyDataSetChanged()
    }
}