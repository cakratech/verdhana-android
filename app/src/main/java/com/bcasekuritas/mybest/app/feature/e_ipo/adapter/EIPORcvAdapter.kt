package com.bcasekuritas.mybest.app.feature.e_ipo.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.domain.dto.response.IpoData
import com.bcasekuritas.mybest.databinding.ItemStockEipoBinding
import com.bcasekuritas.mybest.ext.common.getCurrentTimeInMillis
import com.bcasekuritas.mybest.ext.converter.GET_4_CHAR_STOCK_CODE
import com.bcasekuritas.mybest.ext.converter.GET_STATUS_STAGE_EIPO
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bumptech.glide.Glide

class EIPORcvAdapter(val urlIcon: String, val onItemClick: OnClickStr): RecyclerView.Adapter<EIPORcvAdapter.ItemViewHolder>() {

    private val listData: ArrayList<IpoData> = arrayListOf()

    fun setData(list: List<IpoData>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addData(list: List<IpoData>) {
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun clearData() {
        listData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemStockEipoBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemStockEipoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: IpoData) {
            binding.apply {

                tvStockCode.text = data.code
                tvCompany.text = data.companyName
                val stockCode = GET_4_CHAR_STOCK_CODE(data.code)

                val url = if (data.logoLink.isNotEmpty()) urlIcon + data.logoLink else urlIcon + stockCode
                Glide.with(itemView.context)
                    .load(url)
                    .circleCrop()
                    .placeholder(R.drawable.bg_circle)
                    .error(R.drawable.bg_circle)
                    .into(ivLogo)

                val price = if (data.statusId == "2") "${data.bookPriceFrom.formatPriceWithoutDecimal()} - ${data.bookPriceTo.formatPriceWithoutDecimal()}"
                else "${data.offeringPrice.formatPriceWithoutDecimal()}"
                tvPrice.text = price

                tvStatus.text = GET_STATUS_STAGE_EIPO(data.statusId)
                if (data.statusId == "2" || data.statusId == "3") {
                    tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.textSecondaryBluey))
                } else {
                    tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.black))
                }

                tvDate.text = when (data.statusId) {
                    "2" -> "${DateUtils.formatDate(data.bookPeriodStart, "yyyy-MM-dd", "dd MMM")} - ${DateUtils.formatDate(data.bookPeriodEnd, "yyyy-MM-dd", "dd MMM")}"
                    "3" -> "${DateUtils.formatDate(data.offeringPeriodStart, "yyyy-MM-dd", "dd MMM")} - ${DateUtils.formatDate(data.offeringPeriodEnd, "yyyy-MM-dd", "dd MMM")}"
                    "4" -> DateUtils.formatDate(data.allotmentDate, "yyyy-MM-dd", "dd MMM")
                    "Distribution" -> DateUtils.formatDate(data.distDate, "yyyy-MM-dd", "dd MMM")
                    "IPO" -> DateUtils.formatDate(data.listingDate, "yyyy-MM-dd", "dd MMM")
                    else  -> ""
                }

                root.setOnClickListener {
                    onItemClick.onClickStr(data.code)
                }
            }


        }
    }
}