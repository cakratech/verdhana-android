package com.bcasekuritas.mybest.app.feature.news.tabresearch.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.databinding.ItemListResearchBinding
import com.bcasekuritas.mybest.ext.constant.ConstKeys
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.mybest.ext.listener.OnClickStrInt
import com.bcasekuritas.rabbitmq.proto.news.NewsResearchContent
import com.bumptech.glide.Glide

class TabResearchAdapter(private val onClickStrInt: OnClickStrInt): RecyclerView.Adapter<TabResearchAdapter.ItemViewHolder>() {

    private val listData: ArrayList<NewsResearchContent> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<NewsResearchContent>) {
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
        return ItemViewHolder(ItemListResearchBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemListResearchBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: NewsResearchContent) {
            binding.apply {

                Glide.with(itemView.context)
                    .load(ConstKeys.PROMO_BANNER_URL + data.thumbMLink)
                    .into(ivContent)

                tvDateResearch.text = DateUtils.convertLongToDate(data.publishDate, "E, dd MMM yyyy")
                tvTitleResearch.text = data.title

                root.setOnClickListener {
                    if (data.title.contains(".pdf")) {
                        onClickStrInt.onClickStrInt(data.docUrl, 0)
                    } else {
                        onClickStrInt.onClickStrInt(data.docUrl, 1)
                    }
                }
            }

        }
    }
}