package com.bcasekuritas.mybest.app.feature.portfolio.portfoliodetail.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.domain.dto.response.PortfolioOrderItem
import com.bcasekuritas.mybest.databinding.ItemTabHistoryPortfolioDetailBinding
import com.bcasekuritas.mybest.ext.converter.GET_STATUS_BUY_SELL
import com.bcasekuritas.mybest.ext.converter.GET_STATUS_ORDER
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import java.text.SimpleDateFormat
import java.util.Date

class PortfolioDetailHistoryOrderTabAdapter(val context: Context, val onClickItem: OnClickAny): RecyclerView.Adapter<PortfolioDetailHistoryOrderTabAdapter.ItemViewHolder>() {

    private val listData: ArrayList<PortfolioOrderItem> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<PortfolioOrderItem>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addData(list: List<PortfolioOrderItem>) {
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemTabHistoryPortfolioDetailBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(listData[position])
    }
    inner class ItemViewHolder(
        val binding: ItemTabHistoryPortfolioDetailBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: PortfolioOrderItem) {
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

                itemView.setOnClickListener {
                    onClickItem.onClickAny(data)
                }


            }
        }

    }


}