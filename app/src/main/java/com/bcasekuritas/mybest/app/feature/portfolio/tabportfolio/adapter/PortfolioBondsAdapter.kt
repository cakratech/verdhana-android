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
import com.bcasekuritas.mybest.databinding.ItemPortfolioBondsBinding
import com.bcasekuritas.mybest.databinding.ItemPortfolioStocksBinding
import com.bcasekuritas.mybest.ext.converter.GET_4_CHAR_STOCK_CODE
import com.bcasekuritas.mybest.ext.converter.GET_BACKGROUND_IMAGE_RANDOM_ROUNDED
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.mybest.ext.listener.OnClickStrInt
import com.bcasekuritas.mybest.ext.other.formatPercent
import com.bcasekuritas.mybest.ext.other.formatPrice
import com.bcasekuritas.mybest.ext.other.formatPriceWithDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleAccBondPos

class PortfolioBondsAdapter() : RecyclerView.Adapter<PortfolioBondsAdapter.ItemViewHolder>() {

    private val listData: ArrayList<SimpleAccBondPos> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<SimpleAccBondPos>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemPortfolioBondsBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ItemViewHolder(
        val binding: ItemPortfolioBondsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: SimpleAccBondPos) {
            binding.apply {
                val percent = data.couponRate.times(100)
                tvId.text = data.bondCode
                tvDate.text = "Due: ${DateUtils.toStringDate(data.maturityDate, "dd-MMM-yyyy")}"
                tvTotal.text = data.bondOnHand.formatPriceWithoutDecimal()
                tvPercent.text = "${percent.formatPercent()}% p.a"
            }
        }

    }
}