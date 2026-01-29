package com.bcasekuritas.mybest.app.feature.dialog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.response.source.SelectionCheckRes
import com.bcasekuritas.mybest.databinding.ItemSelectionCheckedBinding

class DialogSelectionSingleAdapter() : RecyclerView.Adapter<BaseViewHolder>() {

    private var data: ArrayList<SelectionCheckRes> = arrayListOf()
    private var positionSelection = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemSelectionCheckedBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(data[position])

    inner class ItemViewHolder(
        val binding: ItemSelectionCheckedBinding
    ) : BaseViewHolder(binding.root) {
        override fun onBind(obj: Any) {
            val item = obj as SelectionCheckRes

            binding.ctvText.text = item.name

            if (item.check){
                binding.ctvText.setCheckMarkDrawable(R.drawable.ic_check)
                binding.ctvText.isChecked = true
            }else{
                binding.ctvText.checkMarkDrawable = null
                binding.ctvText.isChecked = false
            }

            itemView.setOnClickListener {
                if (item.check){
                    positionSelection = -1
                    removeSelected()
                }else{
                    removeSelected()
                    positionSelection = position
                    item.check = true
                    notifyDataSetChanged()
                }
            }
        }

    }

    fun setData(newData: ArrayList<SelectionCheckRes>) {
        if (data != null) {
            data.clear()
            data.addAll(newData)
            notifyDataSetChanged()
        } else {
            data = newData
        }
    }

    fun getSelected() : String{
        var result = ""
        for (items in data) {
            if (items.check) {
                result = items.name
            }
        }
        return result
    }

    private fun removeSelected() {
        for (items in data) {
            items.check = false
        }
        notifyDataSetChanged()
    }

}