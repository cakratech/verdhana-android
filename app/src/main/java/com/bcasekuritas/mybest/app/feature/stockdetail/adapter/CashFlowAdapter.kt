package com.bcasekuritas.mybest.app.feature.stockdetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.databinding.ItemListBalanceSheetBinding
import com.bcasekuritas.mybest.databinding.ItemListCashFlowBinding
import com.bcasekuritas.mybest.ext.common.formatLastNumberWithNegatifValue
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.rabbitmq.proto.news.FinancialCashFlow

class CashFlowAdapter : RecyclerView.Adapter<BaseViewHolder>(){
    private val data: ArrayList<FinancialCashFlow?> = arrayListOf()
    private var periodType = 1

    fun setPeriodType(periodType: Int) {
        this.periodType = periodType
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemListCashFlowBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(data[position]!!)

    inner class ItemViewHolder(
        val binding: ItemListCashFlowBinding
    ): BaseViewHolder(binding.root){
        override fun onBind(obj: Any) {
            val item = obj as FinancialCashFlow

            if (periodType.equals(1)) {
                binding.tvDataPeriod.text = DateUtils.getYear(item.period)
            } else {
                binding.tvDataPeriod.text = DateUtils.getQuarter(item.period)
            }
            binding.tvDataOperating.text = formatLastNumberWithNegatifValue(item.cashFromOperatingAct)
            binding.tvDataInvesting.text = formatLastNumberWithNegatifValue(item.cashFromInvestingAct)
            binding.tvDataFinancing.text = formatLastNumberWithNegatifValue(item.cashFromFinancingAct)
            binding.tvDataNetCashFlow.text = formatLastNumberWithNegatifValue(item.netCashFlowActivities)
            binding.tvDataCapitalExpenditure.text = formatLastNumberWithNegatifValue(item.capitalExpenditure)
            binding.tvDataFreeCashFlow.text = formatLastNumberWithNegatifValue(item.freeCashFlow)
        }
    }

    fun setData(list: List<FinancialCashFlow?>) {
        if (list == null) return
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    fun clearData(){
        data.clear()
        notifyDataSetChanged()
    }
}