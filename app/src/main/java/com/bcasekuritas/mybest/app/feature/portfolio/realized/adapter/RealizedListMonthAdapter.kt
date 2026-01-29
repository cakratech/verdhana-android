package com.bcasekuritas.mybest.app.feature.portfolio.realized.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.domain.dto.response.RealizedGainLossRes
import com.bcasekuritas.mybest.databinding.ItemHistoryRealizedMonthBinding
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.mybest.ext.listener.OnClickInts
import com.bcasekuritas.mybest.ext.other.formatPercent
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.other.getMonthName

class RealizedListMonthAdapter(
    val context: Context,
    val onClickItem: OnClickInts
):RecyclerView.Adapter<RealizedListMonthAdapter.ItemViewHolder>() {
    private val listData: ArrayList<RealizedGainLossRes> = arrayListOf()
    private var expandedItemPosition: Int = RecyclerView.NO_POSITION

    fun setData(list: List<RealizedGainLossRes>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun clearData() {
        listData.clear()
        expandedItemPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }

    fun getPositionExpanded(): Int = expandedItemPosition

    fun updateListStock(position: Int, listStock: List<RealizedGainLossRes>) {
        if (position == RecyclerView.NO_POSITION || position < 0 || position >= listData.size) {
            return
        }
        listData[position].listStock = listStock
        expandedItemPosition = position
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemHistoryRealizedMonthBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(listData[position],position == expandedItemPosition)
    }

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemHistoryRealizedMonthBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: RealizedGainLossRes, isExpanded: Boolean) {
            binding.apply {

                tvMonth.text = if (data.date != 0L) DateUtils.convertLongToDate(data.date, "MMM yyyy") else data.month.getMonthName().substring(0, 3) + " " + data.year

                val profitLoss = data.profitLoss
                if (profitLoss > 0) {
                    tvTotal.setTextColor(ContextCompat.getColor(context, R.color.textUp))
                    tvTotal.text = "+${profitLoss.formatPriceWithoutDecimal()}"

                } else if (profitLoss < 0) {
                    tvTotal.setTextColor(ContextCompat.getColor(context, R.color.textDown))
                    tvTotal.text = profitLoss.formatPriceWithoutDecimal()
                } else {
                    tvTotal.setTextColor(ContextCompat.getColor(context, R.color.noChanges))
                    tvTotal.text = "+0"
                }

                // toggle visibility
                rcvListStock.visibility = if (isExpanded) View.VISIBLE else View.GONE
                val listStockAdapter = RealizedListStockAdapter(context)
                rcvListStock.layoutManager = LinearLayoutManager(context)
                rcvListStock.adapter = listStockAdapter

                if (isExpanded) {
                    listStockAdapter.setData(data.listStock)
                }

                itemView.setOnClickListener {
                    val previousExpanded = expandedItemPosition
                    if (layoutPosition == expandedItemPosition) {
                        // Collapse if already expanded
                        expandedItemPosition = RecyclerView.NO_POSITION
                    } else {
                        // Expand new one
                        expandedItemPosition = layoutPosition
                        onClickItem.onClickInts(data.year, data.month)

                    }

                    // Notify both previous and current to redraw
                    notifyItemChanged(previousExpanded)
                }

                icExpand.setImageResource(if (isExpanded) R.drawable.ic_row_expand_up_blue_large else R.drawable.ic_row_expand_down_blue_large)
            }

        }

    }
}