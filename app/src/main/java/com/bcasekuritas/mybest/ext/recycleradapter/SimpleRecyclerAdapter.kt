package com.bcasekuritas.mybest.ext.recycleradapter

import android.animation.ObjectAnimator
import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections

class SimpleRecyclerAdapter<RecyclerData : Any>(
    data: List<RecyclerData>, @LayoutRes layoutID: Int,
    private val onBindView: BaseViewHolder<RecyclerData>.(data: RecyclerData) -> Unit
) : BaseRecyclerAdapter<RecyclerData>(data), ItemMoveCallback.ItemTouchHelperContract {

    override val layoutItemId: Int = layoutID

    override fun onBindViewHolder(holder: BaseViewHolder<RecyclerData>, position: Int) {
        holder.onBindView(dataList[position])
    }

    fun setData(newData: List<RecyclerData>) {
        dataList = newData
        notifyDataSetChanged()
    }

    fun clearData() {
        dataList = emptyList()
        notifyDataSetChanged()
    }
    fun addData(addData: List<RecyclerData>) {
        dataList = dataList + addData
        notifyDataSetChanged()
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(dataList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(dataList, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onRowClear(viewHolder: RecyclerView.ViewHolder) {
        val viewToAnimate: View = viewHolder.itemView

        // ObjectAnimator to animate the alpha property from 0.5 to 1.0 over 1000 milliseconds (1 second)
        val animator = ObjectAnimator.ofFloat(viewToAnimate, View.ALPHA, 0.5f, 1.0f)
        animator.duration = 500

        // Start the animation
        animator.start()
    }
}