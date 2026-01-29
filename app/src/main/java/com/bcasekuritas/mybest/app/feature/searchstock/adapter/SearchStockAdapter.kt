package com.bcasekuritas.mybest.app.feature.searchstock.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.databinding.ItemSearchStockBinding
import com.bcasekuritas.mybest.ext.converter.GET_4_CHAR_STOCK_CODE
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bumptech.glide.Glide

class SearchStockAdapter(
    private val urlIcon: String,
    private val onClickAny: OnClickAny
) : RecyclerView.Adapter<BaseViewHolder>(){

    private val data: ArrayList<StockParamObject> = arrayListOf()
    private val stockCheckedList: ArrayList<StockParamObject> = arrayListOf()
    private var isCheckBox = false // For visibility of checkbox
    private var stockSelected = -1 // For item checked

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
    ): BaseViewHolder(binding.root){
        @SuppressLint("SuspiciousIndentation")
        override fun onBind(obj: Any) {
            val item = obj as StockParamObject
            binding.apply {
                Glide.with(itemView.context)
                    .load(urlIcon+ GET_4_CHAR_STOCK_CODE(item.stockCode))
                    .circleCrop()
                    .placeholder(R.drawable.bg_circle)
                    .error(R.drawable.bg_circle)
                    .into(ivLogo)

                cbStock.visibility = if (isCheckBox) View.VISIBLE else View.GONE

                tvStockCode.text = item.stockCode
                tvCompanyName.text = item.stockName

                item.isChecked?.let {
                    cbStock.isChecked = it
                }

                clSearchStock.setOnClickListener {
                    if (isCheckBox){

                        stockSelected = position // Get Item Position

                        if (stockSelected != RecyclerView.NO_POSITION) {

                            item.isChecked = !item.isChecked!! // Change isChecked Value
                            onClickAny.onClickAny(data[stockSelected])

                            if (item.isChecked == true){
                                stockCheckedList.add(item)
                            } else {
                                stockCheckedList.remove(item)
                            }

                        }

                        notifyDataSetChanged()
                    } else {
                        onClickAny.onClickAny(item.stockCode)
                    }
                }

                cbStock.setOnClickListener {
                    if (isCheckBox){

                        stockSelected = position // Get Item Position

                        if (stockSelected != RecyclerView.NO_POSITION) {

                            item.isChecked = !item.isChecked!! // Change isChecked Value
                            onClickAny.onClickAny(data[stockSelected])

                            if (item.isChecked == true){
                                stockCheckedList.add(item)
                            } else {
                                stockCheckedList.remove(item)
                            }

                        }

                        notifyDataSetChanged()
                    } else {
                        onClickAny.onClickAny(item.stockCode)
                    }
                }
            }

        }
    }

    fun setData(list: List<StockParamObject>, isCheckBox: Boolean) {
        this.isCheckBox = isCheckBox
        data.clear()
        data.addAll(list)
        checkSelectedStock(list)
        notifyDataSetChanged()
    }

    fun checkSelectedStock(stockParamList: List<StockParamObject>){
        for(checkedStock in stockCheckedList){
            for (listSearch in stockParamList){
                if (checkedStock == listSearch){
                    listSearch.isChecked = true
                }
            }
        }
    }
}