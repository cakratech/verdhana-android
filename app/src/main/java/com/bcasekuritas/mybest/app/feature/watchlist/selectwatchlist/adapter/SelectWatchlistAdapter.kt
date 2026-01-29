package com.bcasekuritas.mybest.app.feature.watchlist.selectwatchlist.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.request.WatchListCategory
import com.bcasekuritas.mybest.databinding.ItemWatchlistCategoryBinding
import com.bcasekuritas.mybest.ext.listener.OnClickAny

class SelectWatchlistAdapter(private val onClickAny: OnClickAny) :
    RecyclerView.Adapter<BaseViewHolder>() {
    private val data: ArrayList<WatchListCategory> = arrayListOf()
    private val wlgCheckedList: ArrayList<WatchListCategory> = arrayListOf()
    private var wlgPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemWatchlistCategoryBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(data[position])

    }

    inner class ItemViewHolder(
        val binding: ItemWatchlistCategoryBinding
    ) : BaseViewHolder(binding.root) {
        @SuppressLint("SuspiciousIndentation")
        override fun onBind(obj: Any) {
            val item = obj as WatchListCategory
            binding.apply {

                tvCategoryName.text = item.category
                tvCategoryQty.text = "${item.stockListString.size} Stocks"

                item.isChecked?.let {
                    cbStock.isChecked = it
                }

                clWatchlistCategory.setOnClickListener {
                    cbStock.isChecked = !item.isChecked
                    item.isChecked = cbStock.isChecked

//                    for (wlgList in wlgCheckedList) {
//                        data.filter { it.isChecked }.find() { it.category != wlgList.category }
//                            ?.let { wlgCheckedList.add(it) }
//                    }
                    val dataIsCheck = data.filter { it.isChecked }
                    wlgCheckedList.clear()
                    dataIsCheck.map { wlgCheckedList.add(it) }
//                    data.filter { it.isChecked }.map { wlgCheckedList.add(it) }
                    onClickAny.onClickAny(wlgCheckedList)
                    notifyDataSetChanged()
                }

                cbStock.setOnClickListener {
                    cbStock.isChecked = !item.isChecked
                    item.isChecked = cbStock.isChecked

//                    for (wlgList in wlgCheckedList) {
//                        data.filter { it.isChecked }.find() { it.category != wlgList.category }
//                            ?.let { wlgCheckedList.add(it) }
//                    }
                    val dataIsCheck = data.filter { it.isChecked }
                    wlgCheckedList.clear()
                    dataIsCheck.map { wlgCheckedList.add(it) }
//                    data.filter { it.isChecked }.map { wlgCheckedList.add(it) }
                    onClickAny.onClickAny(wlgCheckedList)
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