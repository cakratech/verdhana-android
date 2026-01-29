package com.bcasekuritas.mybest.app.feature.portfolio.tabportfolio.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.domain.dto.response.PortfolioStockDataItem
import com.bcasekuritas.mybest.databinding.ItemPortfolioStocksBinding
import com.bcasekuritas.mybest.ext.converter.GET_4_CHAR_STOCK_CODE
import com.bcasekuritas.mybest.ext.converter.GET_BACKGROUND_IMAGE_RANDOM_ROUNDED
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.mybest.ext.listener.OnClickStrInt
import com.bcasekuritas.mybest.ext.other.formatPercent
import com.bcasekuritas.mybest.ext.other.formatPercentThousand
import com.bcasekuritas.mybest.ext.other.formatPrice
import com.bcasekuritas.mybest.ext.other.formatPriceWithDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal

class PortfolioTabAdapter(
    val urlIcon: String,
    val onClickSwipeBtn: OnClickStrInt,
    val onClickItem: OnClickAny
) : RecyclerView.Adapter<PortfolioTabAdapter.ItemViewHolder>() {

    private val listData: ArrayList<PortfolioStockDataItem> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<PortfolioStockDataItem>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemPortfolioStocksBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ItemViewHolder(
        val binding: ItemPortfolioStocksBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: PortfolioStockDataItem) {
            binding.apply {

                val stockCode = GET_4_CHAR_STOCK_CODE(data.stockcode)
                Glide.with(itemView.context)
                    .load(urlIcon+stockCode)
                    .circleCrop()
                    .placeholder(R.drawable.bg_circle)
                    .error(R.drawable.bg_circle)
                    .into(ivStock)

                tvStockCode.text = data.stockcode
                tvAvg.text = "Avg. " + data.avgprice.formatPriceWithDecimal()
                tvLast.text = "Last. " + data.reffprice.formatPriceWithoutDecimal()
                tvPrice.text = data.value.formatPriceWithoutDecimal()
                tvLot.text = data.qtyStock.formatPrice() + " Lot"
                tvProfitLoss.text = "${data.profitLoss.formatPriceWithoutDecimal()} (${data.pct.formatPercentThousand()}%)"

                if (data.profitLoss > 0) {
                    tvProfitLoss.text = "+${data.profitLoss.formatPriceWithoutDecimal()} (+${data.pct.formatPercentThousand()}%)"
                    tvProfitLoss.setTextColor(ContextCompat.getColor(itemView.context, R.color.textUp))
                } else if (data.profitLoss < 0) {
                    tvProfitLoss.setTextColor(ContextCompat.getColor(itemView.context, R.color.textDown))
                    tvProfitLoss.text = "${data.profitLoss.formatPriceWithoutDecimal()} (${data.pct.formatPercentThousand()}%)"
                } else {
                    tvProfitLoss.text = "0 (0.00%)"
                    tvProfitLoss.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                }

//                tvNotation.visibility = if (data.notation.isNotEmpty()) View.VISIBLE else View.GONE
//                tvNotation.text = data.notation

                btnBuy.setOnClickListener {
                    onClickSwipeBtn.onClickStrInt(data.stockcode, 0) // 0 for buy
                }

                itemView.setOnClickListener {
                    onClickItem.onClickAny(data)
                }

                btnSell.setOnClickListener {
                    onClickSwipeBtn.onClickStrInt(data.stockcode, 1) // 1 for sell
                }
            }
        }

    }
}