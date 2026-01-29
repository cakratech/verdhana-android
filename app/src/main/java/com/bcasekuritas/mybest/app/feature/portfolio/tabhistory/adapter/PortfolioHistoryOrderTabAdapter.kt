package com.bcasekuritas.mybest.app.feature.portfolio.tabhistory.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.domain.dto.response.TradeListInfo
import com.bcasekuritas.mybest.databinding.ItemTabHistoryPortfolioDetailBinding
import com.bcasekuritas.mybest.ext.converter.GET_STATUS_BUY_SELL
import com.bcasekuritas.mybest.ext.converter.GET_STATUS_ORDER
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import java.text.SimpleDateFormat
import java.util.Date

class PortfolioHistoryOrderTabAdapter(val context: Context, val onClickItem: OnClickAny): RecyclerView.Adapter<PortfolioHistoryOrderTabAdapter.ItemViewHolder>() {

    private val listData: ArrayList<TradeListInfo> = arrayListOf()
    private var expandedItemPosition: Int = RecyclerView.NO_POSITION

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<TradeListInfo>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addData(list: List<TradeListInfo>) {
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemTabHistoryPortfolioDetailBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(listData[position],position == expandedItemPosition)
    }
    inner class ItemViewHolder(
        val binding: ItemTabHistoryPortfolioDetailBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: TradeListInfo, isExpanded: Boolean) {
            binding.apply {

                val lot = data.orderQty / 100
                val amount = data.price * data.orderQty
                val date: Date = data.time.toLong().let { Date(it) }

                tvStockCode.text = data.stockCode
                tvBuySell.text = data.buySell.GET_STATUS_BUY_SELL()
                tvAmount.text = "Rp" + amount.formatPriceWithoutDecimal()
                tvQty.text = lot.formatPriceWithoutDecimal() +" Lot"
                tvPrice.text = data.price.formatPriceWithoutDecimal()
                tvDate.text = date.let { SimpleDateFormat("dd MMM yyyy, HH:mm").format(it) }

                tvStatus.text = data.status.GET_STATUS_ORDER()
                tvStatus.setTextColor(ContextCompat.getColor(context, R.color.textUp))

                if (data.buySell == "B") {
                    tvBuySell.setTextColor(ContextCompat.getColor(context, R.color.textUp))
                } else {
                    tvBuySell.setTextColor(ContextCompat.getColor(context, R.color.textDown))
                }

                // toggle visibility
                rcvMergedTrade.visibility = if (isExpanded) View.VISIBLE else View.GONE
                if (isExpanded) {
                    val mergeTradeHistoryListAdapter = MergedTradeHistoryListAdapter(context, onClickItem)
                    rcvMergedTrade.layoutManager = LinearLayoutManager(context)
                    rcvMergedTrade.adapter = mergeTradeHistoryListAdapter
                    mergeTradeHistoryListAdapter.setData(data.listTradeInfo)
                }

                itemView.setOnClickListener {
                    if (data.listTradeInfo.size > 1) {
                        val previousExpanded = expandedItemPosition
                        if (layoutPosition == expandedItemPosition) {
                            // Collapse if already expanded
                            expandedItemPosition = RecyclerView.NO_POSITION
                        } else {
                            // Expand new one
                            expandedItemPosition = layoutPosition
                        }

                        // Notify both previous and current to redraw
                        notifyItemChanged(previousExpanded)
                        notifyItemChanged(layoutPosition)

                    } else {
                        onClickItem.onClickAny(data)
                    }
                }

                // set icon expand beside status text
                tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    null,
                    if (data.listTradeInfo.size > 1) {
                        ContextCompat.getDrawable(
                            context,
                            if (isExpanded) R.drawable.ic_row_expand_up_blue else R.drawable.ic_row_expand_down_blue
                        )
                    } else {
                        null
                    },
                    null
                )


            }
        }

    }


}