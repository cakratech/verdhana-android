package com.bcasekuritas.mybest.app.feature.index.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.domain.dto.response.IndexSectorDetailData
import com.bcasekuritas.mybest.databinding.ItemStockIndexBinding
import com.bcasekuritas.mybest.ext.converter.GET_IMAGE_INDEX
import com.bcasekuritas.mybest.ext.listener.OnClickStrInt
import com.bcasekuritas.mybest.ext.other.formatPercent
import com.bcasekuritas.mybest.ext.other.formatPrice
import com.bcasekuritas.mybest.ext.other.formatPriceWithDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithTwoDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal

class IndexAdapter(private val context: Context, private val onClickItem: OnClickStrInt) : RecyclerView.Adapter<IndexAdapter.ItemViewHolder>(){

    private val listData: ArrayList<IndexSectorDetailData> = arrayListOf()

    fun setData(list: List<IndexSectorDetailData>) {
        if (list == null) return
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemStockIndexBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(listData[position])

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemStockIndexBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: IndexSectorDetailData) {
            binding.apply {

                val backgroundImage = GET_IMAGE_INDEX(item.idImg)
                ivLogo.setImageResource(backgroundImage)
                tvImage.text = item.indiceCode.replace("-", "").substring(0,2)

                tvStockName.text = item.indiceCode
                tvPrice.text = "${item.indiceVal.formatPriceWithoutDecimal()}"
                tvStockCount.text = item.indexName

                if (item.change > 0) {
                    tvGainLoss.setTextColor(ContextCompat.getColor(context, R.color.textUp))
                    tvGainLoss.text = "+${item.change.formatPriceWithTwoDecimal()} (+${item.chgPercent.formatPercent()}%)"

                } else if (item.change < 0) {
                    tvGainLoss.setTextColor(ContextCompat.getColor(context, R.color.textDown))
                    tvGainLoss.text = "${item.change.formatPriceWithTwoDecimal()} (${item.chgPercent.formatPercent()}%)"

                } else {
                    tvGainLoss.setTextColor(ContextCompat.getColor(context, R.color.noChanges))
                    tvGainLoss.text = "0.00 (${item.chgPercent.formatPercent()}%)"
                }

                root.setOnClickListener {
                    onClickItem.onClickStrInt(item.indiceCode, item.id.toInt())
                }


            }

        }

    }
}