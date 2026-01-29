package com.bcasekuritas.mybest.app.feature.stockdetail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.response.TechnicalIndicator
import com.bcasekuritas.mybest.databinding.ItemTechIndicatorBinding

class StockDetailAnalysisAdapter(
    val context: Context
): RecyclerView.Adapter<BaseViewHolder>(){
    private val data: ArrayList<TechnicalIndicator?> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemTechIndicatorBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(data[position]!!)

    inner class ItemViewHolder(
        val binding: ItemTechIndicatorBinding
    ): BaseViewHolder(binding.root){
        override fun onBind(obj: Any) {
            val item = obj as TechnicalIndicator
            binding.apply {
                tvStockName.text = item.stockName
                tvValue.text = item.value.toString()

                if (item.stockName == "B"){
                    tvAction.text = "Buy"
                    tvAction.setTextColor(ContextCompat.getColor(context, R.color.textUp))
                } else {
                    tvAction.text = "Sell"
                    tvAction.setTextColor(ContextCompat.getColor(context, R.color.textDown))
                }
            }
        }
    }

    fun setData(list: List<TechnicalIndicator?>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }
}