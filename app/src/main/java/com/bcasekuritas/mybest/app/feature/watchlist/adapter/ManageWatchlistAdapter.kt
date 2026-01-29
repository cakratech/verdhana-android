package com.bcasekuritas.mybest.app.feature.watchlist.adapter

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.request.ManageWatchlistItem
import com.bcasekuritas.mybest.databinding.ItemStockWatchlistBinding
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.converter.GET_4_CHAR_STOCK_CODE
import com.bcasekuritas.mybest.ext.converter.GET_BACKGROUND_IMAGE_RANDOM_ROUNDED
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.mybest.ext.listener.OnClickBoolean
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import java.util.Collections

class ManageWatchlistAdapter(
    private val urlIcon: String,
    private val onClickAny: OnClickAny,
    private val onClickBoolean: OnClickBoolean,
    private val onItemClick: OnClickStr
) : RecyclerView.Adapter<BaseViewHolder>(), ItemMoveCallback.ItemTouchHelperContract{

    private val data: ArrayList<ManageWatchlistItem> = arrayListOf()
    private var isAllWl = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemStockWatchlistBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(data[position])

    }

    inner class ItemViewHolder(
        val binding: ItemStockWatchlistBinding
    ): BaseViewHolder(binding.root){
        @SuppressLint("SuspiciousIndentation")
        override fun onBind(obj: Any) {
            val item = obj as ManageWatchlistItem
            binding.apply {
                Glide.with(itemView.context)
                    .load(urlIcon+ GET_4_CHAR_STOCK_CODE(item.stockCode))
                    .circleCrop()
                    .placeholder(R.drawable.bg_circle)
                    .error(R.drawable.bg_circle)
                    .into(ivLogo)

                tvStockCode.text = item.stockCode
                tvCompanyName.text = item.stockName

                btnDelete.visibility = if (isAllWl) View.VISIBLE else View.GONE
                ivDot.visibility = if (isAllWl) View.VISIBLE else View.GONE

                btnDelete.setOnClickListener {
                    val bundle = Bundle().apply {
                        putString(Args.EXTRA_PARAM_STR_ONE, item.stockCode)
                        putInt(Args.EXTRA_PARAM_INT_ONE, position+1)
                    }
                    onClickAny.onClickAny(bundle)
                }
                root.setOnClickListener {
                    onItemClick.onClickStr(item.stockCode)
                }
            }

        }
    }

    fun setData(list: List<ManageWatchlistItem>, isAllWatchlist: Boolean) {
        data.clear()
        data.addAll(list)
        isAllWl = isAllWatchlist
        notifyDataSetChanged()
    }

    fun clearData() {
        data.clear()
        notifyDataSetChanged()
    }

    fun getListData(): List<ManageWatchlistItem> {
        return data
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (isAllWl) {
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(data, i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap(data, i, i - 1)
                }
            }
            notifyItemMoved(fromPosition, toPosition)
            onClickBoolean.onClickBoolean(true)
        }
    }

    override fun onRowSelected(myViewHolder: ItemViewHolder) {
//        val viewToAnimate: View = myViewHolder.itemView
//        val bgColor = ContextCompat.getColor(viewToAnimate.context, R.color.bgPrimary)
//        viewToAnimate.setBackgroundColor(bgColor)
    }

    override fun onRowClear(myViewHolder: ItemViewHolder) {
        val viewToAnimate: View = myViewHolder.itemView

        // ObjectAnimator to animate the alpha property from 0.5 to 1.0 over 1000 milliseconds (1 second)
        val animator = ObjectAnimator.ofFloat(viewToAnimate, View.ALPHA, 0.5f, 1.0f)
        animator.duration = 1000 // 1 second in milliseconds

        // Start the animation
        animator.start()
    }

}