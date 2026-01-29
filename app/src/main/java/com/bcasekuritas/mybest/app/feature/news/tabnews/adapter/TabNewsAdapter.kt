package com.bcasekuritas.mybest.app.feature.news.tabnews.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.databinding.ItemListNewsBinding
import com.bcasekuritas.mybest.ext.constant.ConstKeys
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.mybest.ext.listener.OnClickStrInt
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoFeed
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoFeedTagList
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup

class TabNewsAdapter(private val onClickStr: OnClickStrInt): RecyclerView.Adapter<TabNewsAdapter.ItemViewHolder>() {

    private val listData: ArrayList<NewsInfoFeed> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<NewsInfoFeed>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addData(list: List<NewsInfoFeed>) {
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun clearData() {
        listData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemListNewsBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemListNewsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: NewsInfoFeed) {
            binding.apply {

                tvDateAuthorNews.text = DateUtils.convertLongToDate(
                    data.datePublish,
                    "dd MMM yyyy"
                ) + " | " + data.source
                tvTitleNews.text = data.newsTitle

                Glide.with(itemView.context)
                    .load(ConstKeys.PROMO_BANNER_URL + data.pathUrl)
                    .into(ivContent)

                root.setOnClickListener {
                    onClickStr.onClickStrInt(data.url, 0)
                }

                chipGroup.removeAllViews() // Clear old chips to avoid duplicates
                createTagNews(data.listTagDataList, chipGroup)
            }

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
                        chip.isCheckable = true
                        chip.isClickable = true
                        chip.isCloseIconVisible = true
                        chip.closeIcon = ContextCompat.getDrawable(context, R.drawable.ic_arrow_right_black)
                        chip.closeIconTint = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
                        val scale = context.resources.displayMetrics.density
                        chip.closeIconSize = 12*scale + 0.5f
                        chip.setEnsureMinTouchTargetSize(false)

                        chip.setOnClickListener {
                            onClickStr.onClickStrInt(tag.stockName, 1)
                        }

                        chipGroup.addView(chip)
                    }
                }
            }
        }
    }
}