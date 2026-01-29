package com.bcasekuritas.mybest.app.feature.global.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.response.IndCommCurrHelper
import com.bcasekuritas.mybest.databinding.ItemDiscoverGlobalBinding
import com.bcasekuritas.mybest.ext.converter.GET_IMAGE_INDEX
import com.bcasekuritas.mybest.ext.other.formatPercent
import com.bcasekuritas.mybest.ext.other.formatPrice
import com.bcasekuritas.mybest.ext.other.formatPriceWithDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithTwoDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutMinus

class IndCommCurrAdapter(private val urlIcon: String) :
    RecyclerView.Adapter<IndCommCurrAdapter.ItemViewHolder>() {

    private val data: ArrayList<IndCommCurrHelper> = arrayListOf()
    private var isCurrencies = false
    private var isCommodity = false
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): IndCommCurrAdapter.ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemDiscoverGlobalBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: IndCommCurrAdapter.ItemViewHolder, position: Int) {
        holder.onBind(data[position])
    }

    inner class ItemViewHolder(
        val binding: ItemDiscoverGlobalBinding,
    ) : BaseViewHolder(binding.root) {
        fun onBind(data: IndCommCurrHelper) {
            binding.apply {

                if (isCurrencies){
                    tvImage.isGone = true
                    Glide.with(itemView.context)
                        .load(urlIcon+ data.code)
                        .circleCrop()
                        .placeholder(R.drawable.bg_circle)
                        .error(R.drawable.bg_circle)
                        .into(ivGlobalImage)
                } else {
                    val backgroundImage = GET_IMAGE_INDEX(data.idImg)
                    ivGlobalImage.setImageResource(backgroundImage)
                }

                tvName.visibility = if (isCommodity) View.GONE else View.VISIBLE

                val textImage = data.code.replace(Regex("[^A-Za-z]"), "")
                tvImage.text = if (textImage.length >=2 ) textImage.substring(0,2) else textImage
                tvCode.text = data.code
                tvName.text = data.name
                tvValue.text = "${data.value.formatPriceWithoutDecimal()}"

                val changePct = data.changePct.times(100)

                if (data.change > 0) {
                    tvChange.setTextColor(ContextCompat.getColor(itemView.context, R.color.textUp))
                    tvChange.text = "+${data.change.formatPriceWithTwoDecimal()} (${changePct.formatPercent()}%)"

                } else if (data.change < 0) {
                    tvChange.setTextColor(ContextCompat.getColor(itemView.context, R.color.textDown))
                    tvChange.text = "${data.change.formatPriceWithTwoDecimal()} (${changePct.formatPercent()}%)"
                } else {
                    tvChange.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                    tvChange.text = "${data.change.formatPriceWithTwoDecimal()} (${changePct.formatPercent()}%)"
                }


                /** OPTION FOR VALIDATE TEXT AND COLOR*/
//                val (textColor, changeText) = when {
//                    data.change > 0 -> {
//                        R.color.textUp to "+${data.change.formatPrice()} (${data.changePct.formatPriceWithDecimal()}%)"
//                    }
//                    data.change < 0 -> {
//                        R.color.textDown to "-${data.change.formatPriceWithoutMinus()} (${data.changePct.formatPriceWithDecimal()}%)"
//                    }
//                    else -> {
//                        R.color.black to "${data.change.formatPriceWithoutMinus()} (${data.changePct.formatPriceWithDecimal()}%)"
//                    }
//                }
//
//                tvChange.setTextColor(ContextCompat.getColor(itemView.context, textColor))
//                tvChange.text = changeText
            }
        }
    }

    override fun getItemCount(): Int = data.size

    fun setData(listData: List<IndCommCurrHelper>, isCurrency: Boolean, isCommodities: Boolean) {
        clearData()
        isCurrencies = isCurrency
        isCommodity = isCommodities
        data.addAll(listData)
        notifyDataSetChanged()
    }

    fun clearData(){
        data.clear()
        notifyDataSetChanged()
    }

}