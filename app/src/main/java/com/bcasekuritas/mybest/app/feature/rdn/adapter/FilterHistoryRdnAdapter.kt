package com.bcasekuritas.mybest.app.feature.rdn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.databinding.ItemMultipleSelectionResultBinding
import com.bcasekuritas.mybest.ext.listener.OnClickStrInt

class FilterHistoryRdnAdapter(private val itemClickInts: OnClickStrInt): RecyclerView.Adapter<BaseViewHolder>() {
    private val listData: ArrayList<String> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemMultipleSelectionResultBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(listData[position])

    inner class ItemViewHolder(
        val binding: ItemMultipleSelectionResultBinding
    ) : BaseViewHolder(binding.root){
        override fun onBind(obj: Any) {
            val data = obj as String

            val type = when(data) {
                "*" -> "All"
                "V" -> "Dividend"
                "C" -> "Top Up"
                "W" -> "Withdrawal"
                else -> data
            }
            
            binding.tvMultipleSelectionResult.text = type

            binding.ivMultipleSelectionResultDelete.setOnClickListener {
                itemClickInts.onClickStrInt(data, position)
            }
        }
    }

    fun setData(list: List<String>?) {
        if (list == null) return
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }
}