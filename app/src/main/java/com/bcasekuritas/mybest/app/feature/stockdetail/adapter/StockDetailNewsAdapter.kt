package com.bcasekuritas.mybest.app.feature.stockdetail.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.databinding.ItemListNewsBinding
import com.bcasekuritas.mybest.ext.constant.ConstKeys
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import com.bcasekuritas.mybest.ext.listener.OnClickStrInt
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoFeed
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoFeedTagList
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup

class StockDetailNewsAdapter(private val onNewsClick: OnClickStrInt) : RecyclerView.Adapter<BaseViewHolder>(){
    private val data: ArrayList<NewsInfoFeed> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemListNewsBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(data[position])

    inner class ItemViewHolder(
        val binding: ItemListNewsBinding
    ): BaseViewHolder(binding.root){
        override fun onBind(obj: Any) {
            val item = obj as NewsInfoFeed

            binding.tvDateAuthorNews.text = DateUtils.convertLongToDate(item.datePublish, "dd MMM yyyy") + " | " + item.source
            binding.tvTitleNews.text = item.newsTitle

            Glide.with(itemView.context)
                .load(ConstKeys.PROMO_BANNER_URL + item.pathUrl)
                .into(binding.ivContent)

            binding.root.setOnClickListener {
                onNewsClick.onClickStrInt(item.url, 0)
            }

            binding.chipGroup.removeAllViews()
            createTagNews(item.listTagDataList, binding.chipGroup)

        }

        private fun createTagNews(listItem: List<NewsInfoFeedTagList>, chipGroup: ChipGroup) {
            if (listItem.isNotEmpty()) {

                for (tag in listItem) {
                    if (tag.stockName.isNotEmpty()) {
                        val context = itemView.context
                        val chip = LayoutInflater.from(context).inflate(
                            R.layout.layout_chip_tag_news,
                            chipGroup,
                            false
                        ) as Chip

                        val drawable = ChipDrawable.createFromAttributes(
                            context,
                            null,
                            0,
                            R.style.ChipTagNews
                        )

                        chip.text = tag.stockName
                        chip.setChipDrawable(drawable)
                        chip.setEnsureMinTouchTargetSize(false)
                        chip.setOnClickListener {
                            onNewsClick.onClickStrInt(tag.stockName, 1)
                        }
                        chipGroup.addView(chip)
                    }
                }
            }
        }
    }

    fun setData(list: List<NewsInfoFeed>?) {
        if (list == null) return
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addData(list: List<NewsInfoFeed>) {
        data.addAll(list)
        notifyDataSetChanged()
    }

    fun clearData() {
        data.clear()
        notifyDataSetChanged()
    }
}