package com.bcasekuritas.mybest.app.feature.runningtrade.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.domain.dto.response.TradeDetail
import com.bcasekuritas.mybest.databinding.ItemRunningTradeBinding
import com.bcasekuritas.mybest.ext.other.formatPercent
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutMinus

class RunningTradeAdapter(val context: Context) : RecyclerView.Adapter<RunningTradeAdapter.ItemViewHolder>() {

    private var listData: ArrayList<TradeDetail> = arrayListOf()
    private var latestUniqueId: Long = 0
    private var prevHighlightPosition = -1

    fun getHighlightStock(): String {
        val item = listData.getOrNull(prevHighlightPosition)
        return item?.secCode?:""
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemRunningTradeBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemRunningTradeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: TradeDetail) {
            binding.apply {
                val hours = item.tradeTime.substring(0,2)
                val minutes = item.tradeTime.substring(2,4)
                val seconds = item.tradeTime.substring(4,6)
                tvTime.text = String.format("%s:%s:%s", hours, minutes, seconds)

                val changePct = item.change.div(item.closePrice).times(100)

                tvPrice.text = item.price?.formatPriceWithoutDecimal()
                tvCode.text = item.secCode
                tvLot.text = item.volume.formatPriceWithoutDecimal()
                tvChange.text = item.change.formatPriceWithoutMinus() + " (${changePct.formatPercent()}%)"

                if (item.change > 0) {
                    tvChange.text = item.change.formatPriceWithoutMinus() + " (+${changePct.formatPercent()}%)"
                    tvChange.setTextColor(ContextCompat.getColor(context, R.color.textUp))
                    tvPrice.setTextColor(ContextCompat.getColor(context, R.color.textUp))
                } else if (item.change < 0) {
                    tvChange.setTextColor(ContextCompat.getColor(context, R.color.textDown))
                    tvPrice.setTextColor(ContextCompat.getColor(context, R.color.textDown))
                } else {
                    tvChange.setTextColor(ContextCompat.getColor(context, R.color.black))
                    tvPrice.setTextColor(ContextCompat.getColor(context, R.color.black))
                }

                if (item.uniqueId == latestUniqueId) {
                    root.rootView.setBackgroundResource(R.color.bgPrimary)
                    prevHighlightPosition =  layoutPosition
                } else {
                    root.rootView.setBackgroundResource(R.color.white)
                }

            }
        }

    }

    fun setData(newData: List<TradeDetail>, latestUniqueId: Long) {
        this.latestUniqueId = latestUniqueId

        if (prevHighlightPosition >= 0) {
            notifyItemChanged(prevHighlightPosition)
        }

        if (newData.size != 0) {
            listData.clear()
            listData.addAll(newData)
            notifyDataSetChanged()
        }
    }

    fun setData(newData: List<TradeDetail>?) {
        setData(newData as List<TradeDetail>, -1)
    }

    internal class PostDiffCallback(
        private val oldPosts: List<TradeDetail>,
        private val newPosts: List<TradeDetail>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldPosts.size
        }

        override fun getNewListSize(): Int {
            return newPosts.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldPosts[oldItemPosition].rowId === newPosts[newItemPosition].rowId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldPosts[oldItemPosition].uniqueId === newPosts[newItemPosition].uniqueId
        }
    }

}