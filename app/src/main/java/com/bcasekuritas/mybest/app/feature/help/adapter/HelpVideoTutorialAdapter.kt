package com.bcasekuritas.mybest.app.feature.help.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.domain.dto.response.FaqHelpData
import com.bcasekuritas.mybest.databinding.ItemVideoTutorialsBinding
import com.bcasekuritas.mybest.ext.common.base64ToByteArray
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import com.bcasekuritas.rabbitmq.proto.news.CMSNewsTutorialVideoDTO
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.coroutines.CoroutineStart
import kotlin.io.encoding.Base64

class HelpVideoTutorialAdapter(
    val onItemClickStr: OnClickStr
) : RecyclerView.Adapter<HelpVideoTutorialAdapter.ItemViewHolder>() {

    private val listData: ArrayList<CMSNewsTutorialVideoDTO> = arrayListOf()

    fun setData(list: List<CMSNewsTutorialVideoDTO>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemVideoTutorialsBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemVideoTutorialsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: CMSNewsTutorialVideoDTO) {
            binding.apply {
                tvTitle.text = data.tutorialTitle

                Glide.with(itemView.context)
                    .load(base64ToByteArray(data.thumbnailData))
                    .fitCenter()
                    .transform(RoundedCorners(16))
                    .placeholder(R.drawable.bg_layout_border_10)
                    .error(R.drawable.bg_layout_border_10)
                    .into(ivThumbnail)

                root.setOnClickListener {
                    onItemClickStr.onClickStr(data.link)
                }
            }
        }

    }
}