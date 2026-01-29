package com.bcasekuritas.mybest.app.feature.stockdetail.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.response.TradeDetail
import com.bcasekuritas.mybest.databinding.ItemRunningTradeBinding
import com.bcasekuritas.mybest.ext.other.formatPercent
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutMinus
import timber.log.Timber

class StockDetailRunningTradeAdapter() : RecyclerView.Adapter<BaseViewHolder>(){
    private var data: ArrayList<TradeDetail> = arrayListOf()
    private var latestUniqueId: Long = 0
    private var prevHighlightPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemRunningTradeBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        try {
            holder.onBind(data[position])
        }catch (e: Exception){
            Timber.tag("StockDetailRunningTrade").d("get is empty/out of bound")
        }

    }

    inner class ItemViewHolder(
        val binding: ItemRunningTradeBinding
    ): BaseViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        override fun onBind(obj: Any) {
            val item = obj as TradeDetail

            binding.apply {
                val hours = item.tradeTime.substring(0,2)
                val minutes = item.tradeTime.substring(2,4)
                val seconds = item.tradeTime.substring(4,6)
                val changePct = (item.change/item.closePrice) * 100

                tvTime.text = String.format("%s:%s:%s", hours, minutes, seconds)
                tvCode.text = item.secCode
                tvLot.text = item.volume.formatPriceWithoutDecimal()
                tvPrice.text = item.price?.formatPriceWithoutDecimal()
                tvChange.text = item.change.formatPriceWithoutDecimal() + " (${changePct.formatPercent()}%)"

                if (item.change > 0) {
                    tvChange.text = item.change.formatPriceWithoutMinus() + " (+${changePct.formatPercent()}%)"
                    tvChange.setTextColor(ContextCompat.getColor(binding.root.context, R.color.textUp))
                    tvPrice.setTextColor(ContextCompat.getColor(binding.root.context, R.color.textUp))
                } else if (item.change < 0) {
                    tvChange.setTextColor(ContextCompat.getColor(binding.root.context, R.color.textDown))
                    tvPrice.setTextColor(ContextCompat.getColor(binding.root.context, R.color.textDown))
                } else {
                    tvChange.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                    tvPrice.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
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
            data.clear()
            data.addAll(newData)
            notifyDataSetChanged()
        }
    }

    fun setData(newData: List<TradeDetail>?) {
        setData(newData as List<TradeDetail>, -1)
    }

    fun clearData(){
        data.clear()
        notifyDataSetChanged()
    }


    internal class PostDiffCallback(
        private val oldPosts: List<TradeDetail>,
        private val newPosts: List<TradeDetail>
    ) :
        DiffUtil.Callback() {
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