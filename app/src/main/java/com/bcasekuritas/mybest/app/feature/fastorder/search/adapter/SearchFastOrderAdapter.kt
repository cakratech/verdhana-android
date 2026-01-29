package com.bcasekuritas.mybest.app.feature.fastorder.search.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.databinding.ItemSearchStockBinding
import com.bcasekuritas.mybest.ext.listener.OnClickStrs
import com.bumptech.glide.Glide

class SearchFastOrderAdapter(
    private val urlIcon: String,
    private val onClickStrs: OnClickStrs
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val data: ArrayList<StockParamObject> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemSearchStockBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(data[position])

    }

    inner class ItemViewHolder(
        val binding: ItemSearchStockBinding
    ) : BaseViewHolder(binding.root) {
        @SuppressLint("SuspiciousIndentation")
        override fun onBind(obj: Any) {
            val item = obj as StockParamObject
            binding.apply {
                Glide.with(itemView.context)
                    .load(urlIcon + item.stockCode.take(4))
                    .circleCrop()
                    .placeholder(R.drawable.bg_circle)
                    .error(R.drawable.bg_circle)
                    .into(ivLogo)

                cbStock.isGone = true
                ivChevron.isGone = true

                tvStockCode.text = item.stockCode
                tvCompanyName.text = item.stockName

                clSearchStock.setOnClickListener {
                    onClickStrs.onClickStrs(item.stockCode, item.stockName)
                }
            }

        }
    }

    fun setData(list: List<StockParamObject>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }
}