package com.bcasekuritas.mybest.app.feature.discover.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.domain.dto.response.IpoData
import com.bcasekuritas.mybest.databinding.ItemEipoBinding
import com.bcasekuritas.mybest.ext.converter.GET_4_CHAR_STOCK_CODE
import com.bcasekuritas.mybest.ext.listener.OnClickStrs

class EIPODiscoverAdapter(val urlIcon: String, val onItemClick: OnClickStrs) : RecyclerView.Adapter<EIPODiscoverAdapter.ItemViewHolder>() {

    private val listData: ArrayList<IpoData> = arrayListOf()

    fun setData(list: List<IpoData>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun clearData() {
        listData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemEipoBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemEipoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: IpoData) {
            binding.apply {
                val stockCode = GET_4_CHAR_STOCK_CODE(data.code)
                val urlImage = if (data.logoLink.isNotEmpty()) urlIcon+data.logoLink else urlIcon+stockCode
                Glide.with(itemView.context)
                    .load(urlImage)
                    .circleCrop()
                    .placeholder(R.drawable.bg_circle)
                    .error(R.drawable.bg_circle)
                    .into(ivLogo)

                tvStockCode.text = data.code

                root.setOnClickListener {
                    onItemClick.onClickStrs(data.code, "")
                }
            }


        }
    }

}