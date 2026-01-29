package com.bcasekuritas.mybest.app.feature.pricealert.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.domain.dto.response.PriceAlertItem
import com.bcasekuritas.mybest.app.feature.portfolio.stoplosstakeprofit.adapter.StopLossTakeProfitAdapter
import com.bcasekuritas.mybest.databinding.ItemAutoOrdersTabOrdersPortfolioBinding
import com.bcasekuritas.mybest.databinding.ItemPriceAlertBinding
import com.bcasekuritas.mybest.databinding.ItemPriceAlertSentBinding
import com.bcasekuritas.mybest.ext.common.oprConvert
import com.bcasekuritas.mybest.ext.converter.GET_4_CHAR_STOCK_CODE
import com.bcasekuritas.mybest.ext.converter.GET_ADVANCE_TYPE
import com.bcasekuritas.mybest.ext.converter.GET_OPERATOR_COMPARE_STR
import com.bcasekuritas.mybest.ext.converter.GET_STATUS_ADVANCE_ORDER
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.mybest.ext.listener.OnClickAnyInt
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.rabbitmq.proto.bcas.AdvancedOrderInfo
import com.bcasekuritas.rabbitmq.proto.bcas.PriceAlert

class PriceAlertSentAdapter (
    val context: Context,
    val urlIcon: String
): RecyclerView.Adapter<PriceAlertSentAdapter.ItemViewHolder>(), ShowDropDown by ShowDropDownImpl() {

    private val listData: ArrayList<PriceAlertItem> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<PriceAlertItem>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun clearData() {
        listData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemPriceAlertSentBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemPriceAlertSentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: PriceAlertItem) {
            binding.apply {

                val stockCode = GET_4_CHAR_STOCK_CODE(data.stockCode)
                Glide.with(itemView.context)
                    .load(urlIcon+stockCode)
                    .circleCrop()
                    .placeholder(R.drawable.bg_circle)
                    .error(R.drawable.bg_circle)
                    .into(ivLogo)

                tvStockName.text = data.stockCode
                tvCompanyName.text = data.stockName
                tvGainLoss.text = data.operation + " " + data.price.formatPriceWithoutDecimal()

                val date = DateUtils.convertLongToDate(data.triggerAt, "dd-MMM")
                tvStockStatus.text = "Sent at ${date}"


            }
        }

    }
}