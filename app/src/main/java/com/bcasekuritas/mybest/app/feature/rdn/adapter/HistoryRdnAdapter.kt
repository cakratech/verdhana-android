package com.bcasekuritas.mybest.app.feature.rdn.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.response.FaqHelpData
import com.bcasekuritas.mybest.app.domain.dto.response.RdnHistoryItem
import com.bcasekuritas.mybest.databinding.ItemRdnHistoryBinding
import com.bcasekuritas.mybest.ext.converter.GET_COLOR_STATUS_CASH_WITHDRAW
import com.bcasekuritas.mybest.ext.converter.GET_RDN_HISTORY_TYPE
import com.bcasekuritas.mybest.ext.converter.GET_STATUS_CASH_WITHDRAW
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import java.text.SimpleDateFormat

class HistoryRdnAdapter(val context: Context, private val itemClick: OnClickAny) : RecyclerView.Adapter<BaseViewHolder>(){

    private val listData: ArrayList<RdnHistoryItem> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemRdnHistoryBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(listData[position])

    inner class ItemViewHolder(
        val binding: ItemRdnHistoryBinding
    ) : BaseViewHolder(binding.root){
        override fun onBind(obj: Any) {
            val data = obj as RdnHistoryItem

            binding.tvItemDate.text = data.createdDate.let { SimpleDateFormat("dd MMM yyyy, HH:mm").format(it) }
            binding.tvItemName.text = data.transType.GET_RDN_HISTORY_TYPE()

            if (data.status.isNotEmpty()) {
                binding.tvItemStatus.text = data.status.GET_STATUS_CASH_WITHDRAW()
                val textColor = data.status.GET_COLOR_STATUS_CASH_WITHDRAW()
                binding.tvItemStatus.setTextColor(ContextCompat.getColor(context, textColor))
                binding.tvItemStatus.visibility = View.VISIBLE
            } else {
                binding.tvItemStatus.visibility = View.GONE
            }

            when (data.transType) {
                "C" -> {
                    binding.tvItemAmount.text = "+ Rp${data.transAmount.formatPriceWithoutDecimal()}"
                    binding.tvItemAmount.setTextColor(ContextCompat.getColor(context, R.color.textUp))
                }
                "W", "D" -> {
                    binding.tvItemAmount.text = "- Rp${data.transAmount.formatPriceWithoutDecimal()}"
                    binding.tvItemAmount.setTextColor(ContextCompat.getColor(context, R.color.textDown))
                }
            }

            itemView.setOnClickListener {
                itemClick.onClickAny(data)
            }
        }
    }

    fun setData(list: List<RdnHistoryItem>?) {
        if (list == null) return
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }



    fun addData(list: List<RdnHistoryItem>){
        if (list.isNotEmpty()){
            listData.addAll(list)
            notifyDataSetChanged()
        }
    }
}