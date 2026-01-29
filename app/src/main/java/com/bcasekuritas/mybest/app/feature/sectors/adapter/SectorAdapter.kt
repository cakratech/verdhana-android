package com.bcasekuritas.mybest.app.feature.sectors.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.domain.dto.response.IndexSectorDetailData
import com.bcasekuritas.mybest.databinding.ItemStockSectorBinding
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.mybest.ext.other.formatPercent
import com.bcasekuritas.mybest.ext.other.formatPercentWithoutMinus
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimalWithoutMinus

class SectorAdapter(private val context: Context, private val onClickItem: OnClickAny) : RecyclerView.Adapter<SectorAdapter.ItemViewHolder>(){

    private val listData: ArrayList<IndexSectorDetailData> = arrayListOf()

    fun setData(list: List<IndexSectorDetailData>) {
        if (list == null) return
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemStockSectorBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(listData[position])

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemStockSectorBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: IndexSectorDetailData) {
            binding.apply {

                ivLogo.setImageResource(item.idImg)
                tvStockName.text = item.indiceCode
                tvPrice.text = "${item.indiceVal.formatPriceWithoutDecimal()}"
                tvStockCount.text = "${item.stockCount} Stocks"

                if (item.change > 0) {
                    tvGainLoss.setTextColor(ContextCompat.getColor(context, R.color.textUp))
                    tvGainLoss.text = "+${item.change.formatPriceWithoutDecimal()} (${item.chgPercent.formatPercentWithoutMinus()}%)"

                } else if (item.change < 0) {
                    tvGainLoss.setTextColor(ContextCompat.getColor(context, R.color.textDown))
                    tvGainLoss.text = "-${item.change.formatPriceWithoutDecimalWithoutMinus()} (${item.chgPercent.formatPercentWithoutMinus()}%)"

                } else {
                    tvGainLoss.setTextColor(ContextCompat.getColor(context, R.color.txtBlackWhite))
                    tvGainLoss.text = "${item.change.formatPriceWithoutDecimalWithoutMinus()} (${item.chgPercent.formatPercent()}%)"
                }



                root.setOnClickListener {
//                    onClickItem.onClickAny(item.indiceCode, item.id.toInt())
                    onClickItem.onClickAny(item)
                }

            }

        }

    }
}