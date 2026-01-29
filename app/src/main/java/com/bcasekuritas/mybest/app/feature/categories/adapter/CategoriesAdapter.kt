package com.bcasekuritas.mybest.app.feature.categories.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.domain.dto.response.CategoriesItem
import com.bcasekuritas.mybest.app.domain.dto.response.TradeSummary
import com.bcasekuritas.mybest.databinding.ItemStockSectorBinding
import com.bcasekuritas.mybest.ext.converter.GET_4_CHAR_STOCK_CODE
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import com.bcasekuritas.mybest.ext.other.formatPercent
import com.bcasekuritas.mybest.ext.other.formatPrice
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutMinus

class CategoriesAdapter(
    private val urlIcon: String,
    private val onClickItem: OnClickStr
) : RecyclerView.Adapter<CategoriesAdapter.ItemViewHolder>(){

    private val listData: ArrayList<CategoriesItem> = arrayListOf()

    fun setDataPaging(list: List<CategoriesItem>?) {
        val startPosition = listData.size
        if (list == null) return
        listData.addAll(list)
        notifyItemRangeInserted(startPosition, list.size)
    }

    fun clearData() {
        listData.clear()
        notifyDataSetChanged()
    }

    fun getDataList(): List<CategoriesItem> = listData

    fun updateData(index: Int, updateData: CategoriesItem) {
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
        fun bind(item: CategoriesItem) {
            binding.apply {
                val stockCode = GET_4_CHAR_STOCK_CODE(item.stockCode)
                Glide.with(itemView.context)
                    .load(urlIcon+stockCode)
                    .circleCrop()
                    .placeholder(R.drawable.bg_circle)
                    .error(R.drawable.bg_circle)
                    .into(ivLogo)

                tvStockCount.text = item.stockName
                tvPrice.text = "${item.lastPrice.formatPriceWithoutDecimal()}"
                tvStockName.text = item.stockCode

                if (item.change > 0) {
                    tvGainLoss.setTextColor(ContextCompat.getColor(itemView.context, R.color.textUp))
                    tvGainLoss.text = "+${item.change.formatPrice()} (${item.changePct.formatPercent()}%)"

                } else if (item.change < 0) {
                    tvGainLoss.setTextColor(ContextCompat.getColor(itemView.context, R.color.textDown))
                    tvGainLoss.text = "-${item.change.formatPriceWithoutMinus()} (${item.changePct.formatPercent()}%)"
                } else {
                    tvGainLoss.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                    tvGainLoss.text = "${item.change.formatPriceWithoutMinus()} (${item.changePct.formatPercent()}%)"
                }

                root.setOnClickListener {
                    onClickItem.onClickStr(item.stockCode)
                }

            }

        }

    }
}