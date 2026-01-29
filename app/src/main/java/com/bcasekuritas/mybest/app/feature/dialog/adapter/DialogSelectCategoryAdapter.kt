package com.bcasekuritas.mybest.app.feature.dialog.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.request.WatchListCategory
import com.bcasekuritas.mybest.databinding.ListWatchlistCategoryBinding
import com.bcasekuritas.mybest.ext.listener.OnClickStr

class DialogSelectCategoryAdapter(private val onClickStr: OnClickStr) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private val data: ArrayList<WatchListCategory> = arrayListOf()
    private var selectedItem: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ListWatchlistCategoryBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(data[position])

    }

    inner class ItemViewHolder(
        val binding: ListWatchlistCategoryBinding
    ) : BaseViewHolder(binding.root) {
        @SuppressLint("SuspiciousIndentation")
        override fun onBind(obj: Any) {
            val item = obj as WatchListCategory
            binding.apply {

                tvCategory.text = item.category

                item.isChecked = selectedItem == position
                ivCheck.visibility = if (selectedItem == position) View.VISIBLE else View.GONE

                clWatchlistCategory.setOnClickListener {
                    selectedItem = position
                    onClickStr.onClickStr(item.category)
                    notifyDataSetChanged()
                }
            }

        }
    }

    fun setData(list: List<WatchListCategory>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

}