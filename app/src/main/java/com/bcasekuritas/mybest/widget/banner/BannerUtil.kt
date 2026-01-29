package com.bcasekuritas.mybest.widget.banner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.ext.constant.ConstKeys
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import com.bcasekuritas.mybest.widget.textview.CustomTextView
import com.bumptech.glide.Glide

class BannerUtil {


    class BannerAdapter(private val items: List<BannerItemSpan>) : RecyclerView.Adapter<ImageViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_image_banner, parent, false)
            return ImageViewHolder(view)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int {
            return items.size
        }
    }

     class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: BannerItemSpan) {
            val imageView: ImageView = itemView.findViewById(R.id.imageView)
            imageView.setImageResource(item.imageResId)
        }
    }


    class BannerAdapterLogin(private val items: List<BannerItem>, private val onItemClick: (String) -> Unit) : RecyclerView.Adapter<ImageLoginViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageLoginViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_image_banner_login, parent, false)
            return ImageLoginViewHolder(view)
        }

        override fun onBindViewHolder(holder: ImageLoginViewHolder, position: Int) {
            holder.bind(items[position], onItemClick)
        }

        override fun getItemCount(): Int {
            return items.size
        }
    }

     class ImageLoginViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: BannerItem, onItemClick: (String) -> Unit) {
            val imageView: ImageView = itemView.findViewById(R.id.imageView)
            val title: CustomTextView = itemView.findViewById(R.id.tv_title_banner_left)
            val desc: CustomTextView = itemView.findViewById(R.id.tv_desc_banner_left)
            val readMore: CustomTextView = itemView.findViewById(R.id.tv_read_more)
            val clBanner: ConstraintLayout = itemView.findViewById(R.id.cl_banner_left)

            title.text = item.title
            desc.text = item.desc
            readMore.text = item.ctaText
            Glide.with(itemView.context).load(ConstKeys.PROMO_BANNER_URL + item.imageRes).into(imageView)

            clBanner.setOnClickListener {
                onItemClick(item.ctaLink)
            }

        }
    }

    class BannerHomeAdapter(private val items: ArrayList<BannerItemPromo>, private val onClickStr: OnClickStr) : RecyclerView.Adapter<ImageBannerHomeViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageBannerHomeViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_image_banner_home, parent, false)
            return ImageBannerHomeViewHolder(view)
        }

        override fun onBindViewHolder(holder: ImageBannerHomeViewHolder, position: Int) {
            holder.bind(items[position], onClickStr)

        }

        override fun getItemCount(): Int {
            return items.size
        }
    }

    class ImageBannerHomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: BannerItemPromo, onClickStr: OnClickStr) {
            val imageView: ImageView = itemView.findViewById(R.id.imageView)
            Glide.with(itemView.context).load(ConstKeys.PROMO_BANNER_URL + item.imageRes).into(imageView)

            itemView.setOnClickListener {
                onClickStr.onClickStr(item.link)
            }

        }
    }

    data class BannerItem(val imageRes: String, val title: String, val desc: String, val ctaText: String, val ctaLink: String)
    data class BannerItemPromo(val imageRes: String, val link: String)
    data class BannerItemSpan(val imageResId: Int, @StringRes val title: Int, val desc: String)
}