package com.bcasekuritas.mybest.app.feature.stockdetail.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.databinding.ItemListBalanceSheetBinding
import com.bcasekuritas.mybest.databinding.ItemListIncomeStatementBinding
import com.bcasekuritas.mybest.ext.common.CONVERT_NUMBER_MBT
import com.bcasekuritas.mybest.ext.common.formatLastNumberWithNegatifValue
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.mybest.ext.other.formatPercent
import com.bcasekuritas.mybest.ext.other.formatPercentWithoutMinus
import com.bcasekuritas.rabbitmq.proto.news.FinancialBalanceSheet

class BalanceSheetAdapter : RecyclerView.Adapter<BaseViewHolder>(){
    private val data: ArrayList<FinancialBalanceSheet?> = arrayListOf()
    private var format = 0 // 0: Annual, 1: Quaterly
    private var isPercentage = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemListBalanceSheetBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(data[position]!!)

    inner class ItemViewHolder(
        val binding: ItemListBalanceSheetBinding
    ): BaseViewHolder(binding.root){
        override fun onBind(obj: Any) {
            val item = obj as FinancialBalanceSheet

            when (format) {
                0 -> {
                    binding.tvDataPeriod.text = DateUtils.getYear(item.period)
                }
                1 -> {
                    binding.tvDataPeriod.text = DateUtils.getQuarter(item.period)
                }
            }
            
            if (isPercentage){
                binding.tvDataCash.text = formatNegativePercentage(item.cashPct)
                binding.tvDataTotalAssets.text = formatNegativePercentage(item.totalAssetsPct)
                binding.tvDataCurrentAsset.text = formatNegativePercentage(item.currentAssetsPct)
                binding.tvDataNonCurrentAsset.text = formatNegativePercentage(item.nonCurrentAssetsPct)
                binding.tvDataLiabilities.text = formatNegativePercentage(item.totalLiabilitiesPct)
                binding.tvDataCurrentLiabilities.text = formatNegativePercentage(item.currentLiabilitiesPct)
                binding.tvDataNonCurrentLiabilities.text = formatNegativePercentage(item.nonCurrentLiabilitiesPct)
                binding.tvDataTotalEquity.text = formatNegativePercentage(item.totalEquityPct)
            } else {
                binding.tvDataCash.text = CONVERT_NUMBER_MBT(item.cash)
                binding.tvDataTotalAssets.text = CONVERT_NUMBER_MBT(item.totalAssets)
                binding.tvDataCurrentAsset.text = CONVERT_NUMBER_MBT(item.currentAssets)
                binding.tvDataNonCurrentAsset.text = CONVERT_NUMBER_MBT(item.nonCurrentAssets)
                binding.tvDataLiabilities.text = CONVERT_NUMBER_MBT(item.totalLiabilities)
                binding.tvDataCurrentLiabilities.text = CONVERT_NUMBER_MBT(item.currentLiabilities)
                binding.tvDataNonCurrentLiabilities.text = CONVERT_NUMBER_MBT(item.nonCurrentLiabilities)
                binding.tvDataTotalEquity.text = CONVERT_NUMBER_MBT(item.totalEquity)
            }
        }
    }

    fun setData(list: List<FinancialBalanceSheet?>) {
        if (list == null) return
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    fun clearData(){
        data.clear()
        notifyDataSetChanged()
    }

    fun setDataFormat(format: Int) {
        this.format = format
    }

    fun setisisPercentage(state: Boolean) {
        isPercentage = state
        notifyDataSetChanged()
    }

    fun formatNegativePercentage(data: Double): String {
        return if (data >= 0) data.formatPercent() +"%" else "(${data.formatPercentWithoutMinus()}%)"
    }
}