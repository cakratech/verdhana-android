package com.bcasekuritas.mybest.app.feature.discover.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.response.IndexSectorDetailData
import com.bcasekuritas.mybest.databinding.ItemSectorsDiscoverBinding
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.mybest.ext.other.formatPercent

class SectorsDiscoverAdapter(private val context: Context, private val onItemClick: OnClickAny) : RecyclerView.Adapter<BaseViewHolder>() {

    private val listData: ArrayList<IndexSectorDetailData> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemSectorsDiscoverBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(listData[position])
    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemSectorsDiscoverBinding
    ) : BaseViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        override fun onBind(obj: Any) {
            val data = obj as IndexSectorDetailData
            binding.ivLogo.setImageResource(data.idImg)
            binding.tvSectorName.text = data.indiceCode

            binding.tvChange.text = data.chgPercent.formatPercent() + "%"
            binding.tvTotalStocks.text = data.stockCount.toString() + " Stocks"

            if (data.chgPercent > 0) {
                binding.tvChange.setTextColor(ContextCompat.getColor(context, R.color.textUp))
                binding.icUpDown.setImageResource(R.drawable.ic_caret_up)
            } else if (data.chgPercent < 0) {
                binding.tvChange.setTextColor(ContextCompat.getColor(context, R.color.textDown))
                binding.icUpDown.setImageResource(R.drawable.ic_caret_down)
            } else {
                binding.tvChange.setTextColor(ContextCompat.getColor(context, R.color.noChanges))
                binding.icUpDown.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                onItemClick.onClickAny(data)
            }
        }
    }

    fun setData(list: List<IndexSectorDetailData>) {
        if (list == null) return
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

}