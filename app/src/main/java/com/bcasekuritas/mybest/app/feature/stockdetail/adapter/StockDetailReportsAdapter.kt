package com.bcasekuritas.mybest.app.feature.stockdetail.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.response.source.StockDetReportsRes
import com.bcasekuritas.mybest.app.feature.stockdetail.adapter.StockDetailReportsAdapter.ItemViewHolder
import com.bcasekuritas.mybest.databinding.ItemListResearchBinding
import com.bcasekuritas.mybest.databinding.ItemListStockDetailReportsBinding
import com.bcasekuritas.mybest.databinding.ItemReportsBinding
import com.bcasekuritas.mybest.ext.constant.ConstKeys
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.mybest.ext.listener.OnClickAnyStr
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import com.bcasekuritas.rabbitmq.proto.news.NewsResearchContent

class StockDetailReportsAdapter(private val onClickAnyStr: OnClickAnyStr) : RecyclerView.Adapter<ItemViewHolder>(){
    private val listData: ArrayList<NewsResearchContent> = arrayListOf()
    var stockCode = ""

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<NewsResearchContent>, code: String) {
        stockCode = code
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addData(list: List<NewsResearchContent>) {
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun clearData() {
        listData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemReportsBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemReportsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: NewsResearchContent) {
            binding.apply {

                tvDate.text = DateUtils.convertLongToDate(data.publishDate, "dd MMM yyyy")
                tvTitle.text = data.title
                tvStockCode.text = stockCode

                root.setOnClickListener {
                    if (data.title.contains(".pdf")) {
                        onClickAnyStr.onClickAnyStr(true, data.docUrl)
                    } else {
                        onClickAnyStr.onClickAnyStr(false, data.docUrl)
                    }
                }
            }

        }
    }
}