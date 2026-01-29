package com.bcasekuritas.mybest.app.feature.stockpick.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.databinding.ItemStockPickBinding
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.widget.textview.CustomTextView
import com.bcasekuritas.rabbitmq.proto.news.NewsStockPickDetil

class StockPickAdapter(val context: Context, val onClickStr: OnClickStr): RecyclerView.Adapter<StockPickAdapter.ItemViewHolder>() {

    private val listData: ArrayList<NewsStockPickDetil> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<NewsStockPickDetil>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemStockPickBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemStockPickBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: NewsStockPickDetil) {
            binding.apply {
                tvStockCode.text = data.stockCode
                setStockStrat(tvStockStrat, data.strategy)
                tvResistance.text = data.resistance.formatPriceWithoutDecimal()
                tvSupport.text = data.support.formatPriceWithoutDecimal()

                lyStockPick.setOnClickListener {
                    onClickStr.onClickStr(data.stockCode)
                }
            }
        }
    }

    private fun setStockStrat(tv: CustomTextView, strat: String){
        var strategy = ""
        var txtColor = 0
        when(strat){
            "TB" -> {
                strategy = "Trading Buy"
                txtColor = R.color.textUp
            }
            "BOW" ->{
                strategy = "Buy On Weakness"
                txtColor = R.color.textTeal
            }
            "HOLD" ->{
                strategy = "Hold"
                txtColor = R.color.textBCABlue
            }
            "SOS" ->{
                strategy = "Sell On Strength"
                txtColor = R.color.textDown
            }
            "BOB" ->{
                strategy = "Buy On Break"
                txtColor = R.color.calPubExp
            }
        }

        tv.text = strategy
        tv.setTextColor(ContextCompat.getColor(tv.context, txtColor))
    }
}