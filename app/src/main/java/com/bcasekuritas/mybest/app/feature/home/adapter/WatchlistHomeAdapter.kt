package com.bcasekuritas.mybest.app.feature.home.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.response.TradeSummary
import com.bcasekuritas.mybest.app.domain.dto.response.TradeSummaryItem
import com.bcasekuritas.mybest.databinding.ItemSectorsBinding
import com.bcasekuritas.mybest.databinding.ItemSectorsTwoBinding
import com.bcasekuritas.mybest.ext.converter.GET_4_CHAR_STOCK_CODE
import com.bcasekuritas.mybest.ext.converter.GET_BACKGROUND_IMAGE_RANDOM_ROUNDED
import com.bcasekuritas.mybest.ext.converter.GET_IMAGE_INDEX
import com.bcasekuritas.mybest.ext.listener.OnClickAnyInt
import com.bcasekuritas.mybest.ext.listener.OnClickStrInt
import com.bcasekuritas.mybest.ext.other.formatPercent
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal

class WatchlistHomeAdapter(
    val urlIcon: String,
    val itemclick: OnClickStrInt,
    val btnDeleteClick: OnClickAnyInt
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val listData: ArrayList<TradeSummary> = arrayListOf()
    var isPortfolioCategory = false

    fun setIsPortfolioCategory(state: Boolean) {
        isPortfolioCategory = state
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == TradeSummaryItem.TYPE_ALL) {
            ItemTwoViewHolder(ItemSectorsTwoBinding.inflate(inflater, parent, false))
        } else {
            ItemViewHolder(ItemSectorsBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemSectorsBinding
    ) : BaseViewHolder(binding.root) {

        override fun onBind(obj: Any) {
            val item = obj as TradeSummary
            binding.apply {

                val stockCode = GET_4_CHAR_STOCK_CODE(item.secCode)
                Glide.with(itemView.context)
                    .load(urlIcon+stockCode)
                    .circleCrop()
                    .placeholder(R.drawable.bg_circle)
                    .error(R.drawable.bg_circle)
                    .into(ivStock)

                val price = if (item.last != 0.0) item.last else item.close

                tvCompanyName.text = item.stockName
                tvPrice.text = "${price.formatPriceWithoutDecimal()}"
                tvStockCode.text = item.secCode
                tvReturn.text = "${item.change.formatPriceWithoutDecimal()} (${item.changePct.formatPercent()}%)"

                if (item.change > 0) {
                    tvReturn.setTextColor(ContextCompat.getColor(itemView.context, R.color.textUp))
                    tvReturn.text = "+${item.change.formatPriceWithoutDecimal()} (+${item.changePct.formatPercent()}%)"

                } else if (item.change < 0) {
                    tvReturn.setTextColor(ContextCompat.getColor(itemView.context, R.color.textDown))
                    tvReturn.text = "${item.change.formatPriceWithoutDecimal()} (${item.changePct.formatPercent()}%)"
                } else {
                    tvReturn.setTextColor(ContextCompat.getColor(itemView.context, R.color.noChanges))
                    tvReturn.text = "0 (0.00%)"
                }

                tvNotation.visibility = if (item.notation.equals("")) View.GONE else View.VISIBLE
                tvNotation.text = item.notation

//                 on click value int: 1 = itemview, 2 = btn buy
                clWatchlist.setOnClickListener {
                    itemclick.onClickStrInt(item.secCode, 1)
                }
                btnBuy.setOnClickListener {
                    itemclick.onClickStrInt(item.secCode, 2)
                }

                btnDelete.text = if (isPortfolioCategory) "SELL" else "DELETE"
                btnDelete.setOnClickListener {
                    btnDeleteClick.onClickAnyInt(item.secCode, layoutPosition+1)
                }
            }
        }
    }

    inner class ItemTwoViewHolder(
        val binding: ItemSectorsTwoBinding
    ) : BaseViewHolder(binding.root) {

        override fun onBind(obj: Any) {
            val item = obj as TradeSummary
            binding.apply {
                Glide.with(itemView.context)
                    .load(urlIcon+ GET_4_CHAR_STOCK_CODE(item.secCode))
                    .circleCrop()
                    .placeholder(R.drawable.bg_circle)
                    .error(R.drawable.bg_circle)
                    .into(ivStock)

                tvCompanyName.text = item.stockName
                tvPrice.text = "${item.last.formatPriceWithoutDecimal()}"
                tvStockCode.text = item.secCode
                tvReturn.text = "${item.change.formatPriceWithoutDecimal()} (${item.changePct.formatPercent()}%)"

                if (item.change > 0) {
                    tvReturn.setTextColor(ContextCompat.getColor(itemView.context, R.color.textUp))
                    tvReturn.text = "+${item.change.formatPriceWithoutDecimal()} (+${item.changePct.formatPercent()}%)"

                } else if (item.change < 0) {
                    tvReturn.setTextColor(ContextCompat.getColor(itemView.context, R.color.textDown))
                    tvReturn.text = "${item.change.formatPriceWithoutDecimal()} (${item.changePct.formatPercent()}%)"

                } else {
                    tvReturn.setTextColor(ContextCompat.getColor(itemView.context, R.color.noChanges))
                    tvReturn.text = "0 (0.00%)"
                }

                tvNotation.visibility = if (item.notation.isNotEmpty()) View.VISIBLE else View.GONE
                tvNotation.text = item.notation

//                 on click value int: 1 = itemview, 2 = btn buy
                clWatchlist.setOnClickListener {
                    itemclick.onClickStrInt(item.secCode, 1)
                }
                btnBuy.setOnClickListener {
                    itemclick.onClickStrInt(item.secCode, 2)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<TradeSummary>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun updateData(index: Int, updateData: TradeSummary) {
        try {
            listData[index] = updateData
            notifyItemChanged(index)
        } catch (ignore: Exception) {

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        listData.clear()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return listData[position].type
    }

}