package com.bcasekuritas.mybest.app.feature.rightissue.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.domain.dto.response.RightIssueItem
import com.bcasekuritas.mybest.databinding.ItemRightIssuesBinding
import com.bcasekuritas.mybest.databinding.ItemStockPickBinding
import com.bcasekuritas.mybest.ext.converter.GET_4_CHAR_STOCK_CODE
import com.bcasekuritas.mybest.ext.converter.GET_BACKGROUND_IMAGE_RANDOM_ROUNDED
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseInfo
import com.bcasekuritas.rabbitmq.proto.news.StockPick

class RightIssueAdapter(val urlIcon: String, val onItemClick: OnClickAny): RecyclerView.Adapter<RightIssueAdapter.ItemViewHolder>() {

    private val listData: ArrayList<RightIssueItem> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<RightIssueItem>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemRightIssuesBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemRightIssuesBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: RightIssueItem) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(urlIcon+ GET_4_CHAR_STOCK_CODE(data.stockCode))
                    .circleCrop()
                    .error(GET_BACKGROUND_IMAGE_RANDOM_ROUNDED())
                    .placeholder(R.drawable.bg_circle)
                    .error(R.drawable.bg_circle)
                    .into(ivStockRightIssues)

                tvStockCodeRightIssues.text = data.instrumentCode
                tvStockNameRightIssues.text = data.stockName
                tvPriceRightIssues.text = data.price.formatPriceWithoutDecimal()

                itemView.rootView.setOnClickListener {
                    onItemClick.onClickAny(data)
                }
            }
        }
    }
}