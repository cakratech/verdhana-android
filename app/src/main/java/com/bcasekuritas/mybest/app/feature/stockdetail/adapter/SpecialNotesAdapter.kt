package com.bcasekuritas.mybest.app.feature.stockdetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.data.entity.StockNotationObject
import com.bcasekuritas.mybest.databinding.ItemSpecialNotesBinding

class SpecialNotesAdapter : RecyclerView.Adapter<BaseViewHolder>(){
    private val data: ArrayList<StockNotationObject?> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemSpecialNotesBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(data[position]!!)

    inner class ItemViewHolder(
        val binding: ItemSpecialNotesBinding
    ): BaseViewHolder(binding.root){
        override fun onBind(obj: Any) {
            val item = obj as StockNotationObject

            binding.tvSpecialNotesCode.text = item.notation
            binding.tvSpecialNotesDesc.text = item.description
        }
    }

    fun setData(list: List<StockNotationObject?>) {
        if (list == null) return
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }
}