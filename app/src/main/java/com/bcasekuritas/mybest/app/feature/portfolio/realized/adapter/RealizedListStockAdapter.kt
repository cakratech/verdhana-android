package com.bcasekuritas.mybest.app.feature.portfolio.realized.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.response.RealizedGainLossRes
import com.bcasekuritas.mybest.databinding.ItemHistoryRealizedBinding
import com.bcasekuritas.mybest.databinding.ItemHistoryRealizedDateBinding
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.mybest.ext.other.formatPercent
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal

class RealizedListStockAdapter(
    val context: Context
): RecyclerView.Adapter<BaseViewHolder>() {

    private val listData: ArrayList<RealizedGainLossRes> = arrayListOf()

    fun setData(list: List<RealizedGainLossRes>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData[position].isDateDivider) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 0) {
            DateViewHolder(ItemHistoryRealizedDateBinding.inflate(inflater, parent, false))
        } else {
            StockViewHolder(ItemHistoryRealizedBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

    inner class DateViewHolder(
        val binding: ItemHistoryRealizedDateBinding
    ): BaseViewHolder(binding.root) {

        override fun onBind(obj: Any) {
            val data = obj as RealizedGainLossRes
            binding.tvDate.text = DateUtils.convertLongToDate(data.date, "dd MMM yyyy")
        }
    }

    inner class StockViewHolder(
        val binding: ItemHistoryRealizedBinding,
    ): BaseViewHolder(binding.root) {

        override fun onBind(obj: Any) {
            val data = obj as RealizedGainLossRes
            binding.apply {

                tvStockCode.text = data.stockCode

                val profitLoss = data.profitLoss
                if (profitLoss > 0) {
                    tvRealizedGainLoss.setTextColor(ContextCompat.getColor(context, R.color.textUp))
                    tvRealizedGainLoss.text = "+${profitLoss.formatPriceWithoutDecimal()} (+${data.profitLossPct.formatPercent()}%)"
                } else if (profitLoss < 0) {
                    tvRealizedGainLoss.setTextColor(ContextCompat.getColor(context, R.color.textDown))
                    tvRealizedGainLoss.text = "${profitLoss.formatPriceWithoutDecimal()} (${data.profitLossPct.formatPercent()}%)"
                } else {
                    tvRealizedGainLoss.setTextColor(ContextCompat.getColor(context, R.color.noChanges))
                    tvRealizedGainLoss.text = "+0 (+0.00%)"
                }
            }
        }
    }
}