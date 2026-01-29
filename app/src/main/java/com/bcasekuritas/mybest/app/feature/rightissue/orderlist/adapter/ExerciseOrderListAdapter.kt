package com.bcasekuritas.mybest.app.feature.rightissue.orderlist.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.domain.dto.response.ExerciseOrderListItem
import com.bcasekuritas.mybest.databinding.ItemExerciseOrderListSwipeBinding
import com.bcasekuritas.mybest.ext.converter.GET_STATUS_EXERCISE
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal

class ExerciseOrderListAdapter(val context: Context): RecyclerView.Adapter<ExerciseOrderListAdapter.ItemViewHolder>() {

    private val listData: ArrayList<ExerciseOrderListItem> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<ExerciseOrderListItem>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemExerciseOrderListSwipeBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemExerciseOrderListSwipeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: ExerciseOrderListItem) {
            binding.apply {

                tvStockCode.text = data.stockCode
                tvQty.text = data.orderQty.formatPriceWithoutDecimal()
                tvPrice.text = data.orderPrice.formatPriceWithoutDecimal()
                tvAmount.text = "Rp" + data.amount.formatPriceWithoutDecimal()

                tvStatus.text = data.status.GET_STATUS_EXERCISE()?.uppercase()

                when (data.status) {
                    "0" -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.textDown))
                    }
                    "1" -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.alwaysBlue))
                    }
                    "9" -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.textUp))
                    }
                    else -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.black))
                    }
                }
//
//                btnWithdraw.setOnClickListener {
//                    withdrawClick.onClickStr(data.transCode)
//                }
//
//                lyItem.setOnClickListener {
//                    itemClick.onClickAny(data)
//                }

            }
        }
    }
}