package com.bcasekuritas.mybest.app.feature.index.detail.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.domain.dto.response.TradeSummary
import com.bcasekuritas.mybest.databinding.ItemStockSectorBinding
import com.bcasekuritas.mybest.ext.converter.GET_4_CHAR_STOCK_CODE
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import com.bcasekuritas.mybest.ext.other.formatPercent
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimalWithoutMinus
import com.bumptech.glide.Glide

class IndexDetailAdapter(
    private val urlIcon: String,
    private val onClickItem: OnClickStr
) : RecyclerView.Adapter<IndexDetailAdapter.ItemViewHolder>(){

    private val listData: ArrayList<TradeSummary> = arrayListOf()

    fun setDataPaging(list: List<TradeSummary>?) {
        val startPosition = listData.size
        if (list == null) return
        listData.addAll(list)
        notifyItemRangeInserted(startPosition, list.size)
    }

    fun clearData() {
        listData.clear()
        notifyDataSetChanged()
    }

    fun getDataList(): List<TradeSummary> = listData

    fun updateData(index: Int, updateData: TradeSummary) {
        try {
            listData[index] = updateData
            notifyItemChanged(index)
        } catch (ignore: Exception) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemStockSectorBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(listData[position])

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemStockSectorBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: TradeSummary) {
            binding.apply {

                Glide.with(itemView.context)
                    .load(urlIcon+ GET_4_CHAR_STOCK_CODE(item.secCode))
                    .circleCrop()
                    .placeholder(R.drawable.bg_circle)
                    .error(R.drawable.bg_circle)
                    .into(ivLogo)

                tvStockCount.text = item.stockName
                tvPrice.text = "${item.last.formatPriceWithoutDecimal()}"
                tvStockName.text = item.secCode
                tvGainLoss.text = "${item.change.formatPriceWithoutDecimalWithoutMinus()} (${item.changePct.formatPercent()}%)"

                if (item.change > 0) {
                    tvGainLoss.setTextColor(ContextCompat.getColor(itemView.context, R.color.textUp))
                    tvGainLoss.text = "+${item.change.formatPriceWithoutDecimal()} (${item.changePct.formatPercent()}%)"

                } else if (item.change < 0) {
                    tvGainLoss.setTextColor(ContextCompat.getColor(itemView.context, R.color.textDown))
                    tvGainLoss.text = "-${item.change.formatPriceWithoutDecimalWithoutMinus()} (${item.changePct.formatPercent()}%)"

                } else {
                    tvGainLoss.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                }

                root.setOnClickListener {
                    onClickItem.onClickStr(item.secCode)
                }

            }

        }

    }
}