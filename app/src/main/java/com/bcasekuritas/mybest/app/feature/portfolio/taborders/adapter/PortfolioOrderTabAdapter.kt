package com.bcasekuritas.mybest.app.feature.portfolio.taborders.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.response.PortfolioOrderItem
import com.bcasekuritas.mybest.databinding.ItemTabActiveOrderBinding
import com.bcasekuritas.mybest.databinding.ItemTabActiveOrderSwipeBinding
import com.bcasekuritas.mybest.ext.common.timeInForce
import com.bcasekuritas.mybest.ext.converter.GET_STATUS_BUY_SELL
import com.bcasekuritas.mybest.ext.converter.GET_STATUS_ORDER
import com.bcasekuritas.mybest.ext.converter.GET_STATUS_ORDER_TO_INT
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.mybest.ext.listener.OnClickAnyInt
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal

class PortfolioOrderTabAdapter(
        val context: Context, val onClickSwipeBtn: OnClickAnyInt, val onClickItem: OnClickAny
): RecyclerView.Adapter<BaseViewHolder>() {

    private val listData: ArrayList<PortfolioOrderItem> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<PortfolioOrderItem>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun clearData() {
        listData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 0) {
            ItemViewHolderSwipe(ItemTabActiveOrderSwipeBinding.inflate(inflater, parent, false))
        } else {
            ItemViewHolder(ItemTabActiveOrderBinding.inflate(inflater, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return listData[position].status.GET_STATUS_ORDER_TO_INT()
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolderSwipe(
        val binding: ItemTabActiveOrderSwipeBinding
    ) : BaseViewHolder(binding.root) {


        override fun onBind(obj: Any) {
            val data = obj as PortfolioOrderItem

            binding.apply {

                val lot = data.orderQty / 100
                val price = if (data.status == "M" || data.status == "CM") data.matchPrice else data.price
                val amount = price * data.orderQty

                tvStockCode.text = data.stockCode
                tvBuySell.text = data.buySell.GET_STATUS_BUY_SELL()
                tvAmount.text = "Rp" + amount.formatPriceWithoutDecimal()
                tvQty.text = lot.formatPriceWithoutDecimal()
                tvStatus.text = data.status.GET_STATUS_ORDER()
                tvPrice.text = price.formatPriceWithoutDecimal()
                if (data.timeInForce.equals("2")) {
                    tvDate.text = "Until " + DateUtils.convertLongToDate(data.ordPeriod, "dd MMM yyyy")
                } else {
                    tvDate.text = timeInForce(data.timeInForce)
                }

                if (data.buySell == "B") {
                    tvBuySell.setTextColor(ContextCompat.getColor(context, R.color.textUp))
                } else {
                    tvBuySell.setTextColor(ContextCompat.getColor(context, R.color.textDown))
                }

                tvNotation.visibility = if (data.notation.isNotEmpty()) View.VISIBLE else View.GONE
                tvNotation.text = data.notation

                when (data.status) {
                    "M" -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.textUp))
                    }
                    "O" -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.alwaysBlue))
                    }
                    "A" -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.action_cyan))
                    }
                    "PT" -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.black))
                    }
                    "C" -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.textDown))
                    }
                    "R" -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.black))
                    }
                    else -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.black))
                    }
                }


                // valueint: 1 = withdraw, 2 = amend
                btnWithdraw.setOnClickListener {
                    onClickSwipeBtn.onClickAnyInt(data, 1)
                }

                btnAmend.setOnClickListener {
                    data.isAmend = true
                    onClickSwipeBtn.onClickAnyInt(data, 2)
                }

                lyItem.setOnClickListener {
                    onClickItem.onClickAny(data)
                }


            }
        }

    }

    inner class ItemViewHolder(
        val binding: ItemTabActiveOrderBinding
    ) : BaseViewHolder(binding.root) {


        override fun onBind(obj: Any) {
            val data = obj as PortfolioOrderItem

            binding.apply {

                val lot = data.orderQty / 100
                val price = if (data.status == "M" || data.status == "CM") data.matchPrice else data.price
                val amount = price * data.orderQty

                tvStockCode.text = data.stockCode
                tvBuySell.text = data.buySell.GET_STATUS_BUY_SELL()
                tvAmount.text = "Rp" + amount.formatPriceWithoutDecimal()
                tvQty.text = lot.formatPriceWithoutDecimal()
                tvStatus.text = data.status.GET_STATUS_ORDER()
                tvPrice.text = price.formatPriceWithoutDecimal()
                if (data.timeInForce.equals("2")) {
                    tvDate.text = "Until " + DateUtils.convertLongToDate(data.ordPeriod, "dd MMM yyyy")
                } else {
                    tvDate.text = timeInForce(data.timeInForce)
                }

                if (data.buySell == "B") {
                    tvBuySell.setTextColor(ContextCompat.getColor(context, R.color.textUp))
                } else {
                    tvBuySell.setTextColor(ContextCompat.getColor(context, R.color.textDown))
                }

                tvNotation.visibility = if (data.notation.isNotEmpty()) View.VISIBLE else View.GONE
                tvNotation.text = data.notation


                when (data.status) {
                    "M" -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.textUp))
                    }
                    "O" -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.alwaysBlue))
                    }
                    "A" -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.action_cyan))
                    }
                    "C" -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.textDown))
                    }
                    "R" -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.black))
                    }
                    else -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.black))
                    }
                }

                lyItem.setOnClickListener {
                    onClickItem.onClickAny(data)
                }


            }
        }

    }
}