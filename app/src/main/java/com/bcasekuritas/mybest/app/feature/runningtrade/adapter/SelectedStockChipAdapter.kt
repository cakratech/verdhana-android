package com.bcasekuritas.mybest.app.feature.runningtrade.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.databinding.ItemMultipleSelectionResultBinding
import com.bcasekuritas.mybest.ext.listener.OnClickStr

class SelectedStockChipAdapter(private val onChipClick: OnClickStr): RecyclerView.Adapter<BaseViewHolder>() {
    private val listData: ArrayList<String> = arrayListOf()
    fun setData(stocks: List<String>) {
        listData.clear()
        listData.addAll(stocks)
        notifyDataSetChanged()
    }

    fun clearData() {
        listData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemMultipleSelectionResultBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(listData[position])

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemMultipleSelectionResultBinding
    ) : BaseViewHolder(binding.root){
        override fun onBind(obj: Any) {
            val data = obj as String

            binding.tvMultipleSelectionResult.text = data

            binding.ivMultipleSelectionResultDelete.setOnClickListener {
                onChipClick.onClickStr(data)
            }
        }
    }
}