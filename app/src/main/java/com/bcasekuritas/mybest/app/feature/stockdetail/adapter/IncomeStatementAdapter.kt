package com.bcasekuritas.mybest.app.feature.stockdetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.databinding.ItemListIncomeStatementBinding
import com.bcasekuritas.mybest.ext.common.formatLastNumberWithNegatifValue
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.mybest.ext.other.formatPercent
import com.bcasekuritas.mybest.ext.other.formatPercentWithoutMinus
import com.bcasekuritas.rabbitmq.proto.news.FinancialIncomeStatement

class IncomeStatementAdapter : RecyclerView.Adapter<BaseViewHolder>(){
    private val data: ArrayList<FinancialIncomeStatement?> = arrayListOf()
    private var format = 0 // 0: Annual, 1: Quaterly
    private var isPercentage = false

    fun setDataFormat(format: Int) {
        this.format = format
    }

    fun setIsPercentage(state: Boolean) {
        isPercentage = state
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemListIncomeStatementBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(data[position]!!)

    inner class ItemViewHolder(
        val binding: ItemListIncomeStatementBinding
    ): BaseViewHolder(binding.root){
        override fun onBind(obj: Any) {
            val item = obj as FinancialIncomeStatement

            when (format) {
                0 -> {
                    binding.tvDataPeriod.text = DateUtils.getYear(item.period)
                }
                1 -> {
                    binding.tvDataPeriod.text = DateUtils.getQuarter(item.period)
                }
            }
            if (isPercentage) {
                binding.tvDataRevenue.text = formatNegativePercentage(item.revenuePercentage)
                binding.tvDataCogs.text = formatNegativePercentage(item.costOfGoodsPercentage)
                binding.tvDataGrossProfit.text = formatNegativePercentage(item.grossProfitPercentage)
                binding.tvDataOperatingExpenses.text = formatNegativePercentage(item.operatingExpensesPercentage)
                binding.tvDataEbitda.text = formatNegativePercentage(item.ebitdaPercentage)
                binding.tvDataOperatingProfit.text = formatNegativePercentage(item.operatingProfitEbitPercentage)
                binding.tvDataIncomeBeforeTax.text = formatNegativePercentage(item.incomeBeforeTaxtPercentage)
                binding.tvDataNetIncome.text = formatNegativePercentage(item.netIncomePercentage)
            } else {
                binding.tvDataRevenue.text = formatLastNumberWithNegatifValue(item.revenue)
                binding.tvDataCogs.text = formatLastNumberWithNegatifValue(item.costOfGoodsSold)
                binding.tvDataGrossProfit.text = formatLastNumberWithNegatifValue(item.grossProfit)
                binding.tvDataOperatingExpenses.text = formatLastNumberWithNegatifValue(item.operatingExpenses)
                binding.tvDataEbitda.text = formatLastNumberWithNegatifValue(item.ebitda)
                binding.tvDataOperatingProfit.text = formatLastNumberWithNegatifValue(item.operatingProfitEbit)
                binding.tvDataIncomeBeforeTax.text = formatLastNumberWithNegatifValue(item.incomeBeforeTax)
                binding.tvDataNetIncome.text = formatLastNumberWithNegatifValue(item.netIncome)
            }
        }
    }

    fun setData(list: List<FinancialIncomeStatement?>) {
        if (list == null) return
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    fun clearData(){
        data.clear()
        notifyDataSetChanged()
    }

    fun formatNegativePercentage(data: Double): String {
        return if (data >= 0) data.formatPercent() +"%" else "(${data.formatPercentWithoutMinus()}%)"
    }
}