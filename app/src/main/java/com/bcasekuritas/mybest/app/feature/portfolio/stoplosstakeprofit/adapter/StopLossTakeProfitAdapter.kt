package com.bcasekuritas.mybest.app.feature.portfolio.stoplosstakeprofit.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.databinding.ItemAutoOrdersTabOrdersPortfolioBinding
import com.bcasekuritas.mybest.ext.common.oprConvert
import com.bcasekuritas.mybest.ext.converter.GET_ADVANCE_TYPE
import com.bcasekuritas.mybest.ext.converter.GET_BRACKET_STATUS
import com.bcasekuritas.mybest.ext.converter.GET_STATUS_ADVANCE_ORDER
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.mybest.ext.listener.OnClickAnyInt
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.rabbitmq.proto.bcas.AdvancedOrderInfo

class StopLossTakeProfitAdapter(val context: Context, val onClickBtnOption: OnClickAnyInt, val onClickItem: OnClickAny): RecyclerView.Adapter<StopLossTakeProfitAdapter.ItemViewHolder>(), ShowDropDown by ShowDropDownImpl() {

    private val listData: ArrayList<AdvancedOrderInfo> = arrayListOf()
    private val listMenuOption = arrayListOf("Withdraw")

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<AdvancedOrderInfo>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemAutoOrdersTabOrdersPortfolioBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemAutoOrdersTabOrdersPortfolioBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: AdvancedOrderInfo) {
            binding.apply {

                tvStockCode.text = data.stockCode
                tvOrderId.text = "Order# ${data.orderID}"
                tvBuyType.text = when {
                    data.buySell == "B" && data.advType == 9 -> "BUY & SELL"
                    data.buySell == "B" -> "BUY"
                    else -> "SELL"
                }
                tvOrderStatus.text = data.status.GET_STATUS_ADVANCE_ORDER()?.uppercase()
                btnOption.visibility = if (data.status == "M" || data.status == "C" || data.status == "S" || data.status == "stop") View.GONE else View.VISIBLE

                tvCondition.text = data.advType.GET_ADVANCE_TYPE()
                tvQuantityAndPrice.text = data.ordQty.div(100).formatPriceWithoutDecimal() + " Lot, "+ data.ordPrice.formatPriceWithoutDecimal()

                if (data.buySell == "B") {
                    tvBuyType.setTextColor(ContextCompat.getColor(context, R.color.textUp))
                } else {
                    tvBuyType.setTextColor(ContextCompat.getColor(context, R.color.textDown))
                }

                when (data.advType) {
                    2 -> {
                        if (data.timeInForce.equals("0")) {
                            tvStopLoss.visibility = View.GONE
                            tvTakeProfit.visibility = View.GONE
                            tvNoOfSlice.visibility = View.GONE
                            tvBlockQty.visibility = View.GONE
                            tvValidUntil.visibility = View.GONE

                            tvComparePrice.visibility = View.VISIBLE
                            tvComparePrice.text = "Compare Price ${oprConvert(data.opr)} ${data.triggerPrice.formatPriceWithoutDecimal()}"
                        } else {
                            tvStopLoss.visibility = View.GONE
                            tvTakeProfit.visibility = View.GONE
                            tvNoOfSlice.visibility = View.GONE
                            tvBlockQty.visibility = View.GONE
                            tvComparePrice.visibility - View.GONE

                            tvCondition.text = "GTC"
                            tvValidUntil.visibility = View.VISIBLE
                            val date = DateUtils.toStringDate(data.validUntil, "dd MMM yyyy")
                            tvValidUntil.text = "Valid until : $date"
                        }
                    }
                    0 -> {
                        tvStopLoss.visibility = View.GONE
                        tvTakeProfit.visibility = View.GONE
                        tvValidUntil.visibility = View.GONE
                        tvComparePrice.visibility = View.GONE

                        tvNoOfSlice.visibility = View.VISIBLE
                        tvBlockQty.visibility = View.GONE
                        tvNoOfSlice.text = "No. of Split : ${data.splitNumber}"

                        tvOrderStatus.text = when (data.status) {
                            "PB", "B2" -> "PENDING"
                            else -> data.status.GET_STATUS_ADVANCE_ORDER()?.uppercase()
                        }
                    }

                    10 -> {
                        tvStopLoss.visibility = View.GONE
                        tvTakeProfit.visibility = View.GONE
                        tvValidUntil.visibility = View.GONE
                        tvComparePrice.visibility = View.GONE

                        tvNoOfSlice.visibility = View.GONE
                        tvBlockQty.visibility = View.VISIBLE
                        tvBlockQty.text = "No. of Repeat : ${data.splitNumber}"

                        tvOrderStatus.text = when (data.status) {
                            "PB", "B2" -> "PENDING"
                            else -> data.status.GET_STATUS_ADVANCE_ORDER()?.uppercase()
                        }
                    }
                    6 -> {
                        tvStopLoss.visibility = View.GONE
                        tvTakeProfit.visibility = View.GONE
                        tvNoOfSlice.visibility = View.GONE
                        tvBlockQty.visibility = View.GONE
                        tvComparePrice.visibility = View.GONE

                        tvOrderStatus.text = if (data.status == "PB") "PENDING TIMER" else data.status.GET_STATUS_ADVANCE_ORDER()?.uppercase()
                        tvValidUntil.visibility = View.VISIBLE
                        val date = DateUtils.toStringDate(data.validUntil, "dd MMM yyyy")
                        tvValidUntil.text = "Valid until : $date"
                    }
                    9 -> {
                        tvStopLoss.visibility = View.VISIBLE
                        tvTakeProfit.visibility = View.VISIBLE
                        tvNoOfSlice.visibility = View.GONE
                        tvBlockQty.visibility = View.GONE
                        tvValidUntil.visibility = View.GONE
                        tvComparePrice.visibility = View.GONE

                        tvOrderStatus.text = data.bracketStatus.GET_BRACKET_STATUS().uppercase()
                        btnOption.visibility = if (data.bracketStatus == 11) View.GONE else View.VISIBLE

                        if (data.stopLossCriteria.triggerVal != 0L) {
                            val price = data.stopLossCriteria.triggerVal.toDouble().formatPriceWithoutDecimal()
                            val opr = if (data.stopLossCriteria.opr.equals(2)) "≥" else "≤"

                            tvStopLoss.text = "Stop Loss $opr $price"
                        } else {
                            tvStopLoss.visibility = View.GONE
                        }

                        if (data.takeProfitCriteria.triggerVal != 0L) {
                            val price = data.takeProfitCriteria.triggerVal.toDouble().formatPriceWithoutDecimal()
                            val opr = if (data.takeProfitCriteria.opr.equals(2)) "≥" else "≤"

                            tvTakeProfit.text = "Take Profit $opr $price"
                        } else {
                            tvTakeProfit.visibility = View.GONE
                        }

                        if (data.takeProfitCriteria.triggerOrder.ordPrice != 0L){
                            tvQuantityAndPrice.text = data.takeProfitCriteria.triggerOrder.ordQty.div(100).toDouble().formatPriceWithoutDecimal() + " Lot"
                        }

                        if (data.stopLossCriteria.triggerOrder.ordPrice != 0L){
                            tvQuantityAndPrice.text = data.stopLossCriteria.triggerOrder.ordQty.div(100).toDouble().formatPriceWithoutDecimal() + " Lot"
                        }


                    }
                }

                root.rootView.setOnClickListener {
                    onClickItem.onClickAny(data)
                }

                btnOption.setOnClickListener {
                    showSimpleDropDownWidth80(
                        context,
                        listMenuOption,
                        lyDropDown
                        ) { index, value ->
                        onClickBtnOption.onClickAnyInt(data, index)
                    }
                }


            }
        }

    }
}