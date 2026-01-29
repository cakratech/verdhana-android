package com.bcasekuritas.mybest.app.feature.stockdetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.databinding.ItemStockDetailDailyOneBinding

class StockDetailDailyOneAdapter : RecyclerView.Adapter<BaseViewHolder>(){
    private val data: ArrayList<String> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemStockDetailDailyOneBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(data[position])

    inner class ItemViewHolder(
        val binding: ItemStockDetailDailyOneBinding
    ): BaseViewHolder(binding.root){
        override fun onBind(obj: Any) {
            val item = obj as String

            binding.tvItemDate.text = item
        }
    }

    fun setData(list: List<String>?) {
        if (list == null) return
        val startPosition = data.size
        data.addAll(list) // Modify data only once
        notifyItemRangeInserted(startPosition, list.size)
    }

    fun clearData(){
        data.clear()
        notifyDataSetChanged()
    }
}