package com.bcasekuritas.mybest.app.feature.discover.adapter

import android.annotation.SuppressLint
import android.content.Context
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
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.response.CategoriesItem
import com.bcasekuritas.mybest.databinding.ItemCategoriesDiscoverBinding
import com.bcasekuritas.mybest.ext.converter.GET_4_CHAR_STOCK_CODE
import com.bcasekuritas.mybest.ext.converter.GET_BACKGROUND_IMAGE_RANDOM_ROUNDED
import com.bcasekuritas.mybest.ext.converter.GET_IMAGE_INDEX
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import com.bcasekuritas.mybest.ext.other.formatPercent

class CategoriesDiscoverAdapter(private val context: Context, private val onItemClick: OnClickStr, private val urlImage: String) : RecyclerView.Adapter<BaseViewHolder>() {

    private val listData: ArrayList<CategoriesItem> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemCategoriesDiscoverBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(listData[position])
    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemCategoriesDiscoverBinding
    ) : BaseViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        override fun onBind(obj: Any) {
            val data = obj as CategoriesItem

            Glide.with(itemView.context)
                .load(urlImage+ GET_4_CHAR_STOCK_CODE(data.stockCode))
                .circleCrop()
                .placeholder(R.drawable.bg_circle)
                .error(R.drawable.bg_circle)
                .into(binding.ivLogo)

            binding.tvStockCode.text = data.stockCode
            binding.tvChange.text = data.changePct.formatPercent() + "%"
            binding.icUpDown.visibility= View.VISIBLE

            if (data.changePct > 0) {
                binding.tvChange.setTextColor(ContextCompat.getColor(context, R.color.textUp))
                binding.icUpDown.setImageResource(R.drawable.ic_caret_up)
            } else if (data.changePct < 0) {
                binding.tvChange.setTextColor(ContextCompat.getColor(context, R.color.textDown))
                binding.icUpDown.setImageResource(R.drawable.ic_caret_down)
            } else {
                binding.icUpDown.visibility= View.GONE
                binding.tvChange.setTextColor(ContextCompat.getColor(context, R.color.noChanges))
            }

            binding.root.setOnClickListener {
                onItemClick.onClickStr(data.stockCode)
//                notifyItemChanged(layoutPosition)
            }
        }
    }

    fun setData(list: List<CategoriesItem>?) {
        if (list == null) return
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }
}