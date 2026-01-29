package com.bcasekuritas.mybest.app.feature.calendar.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.response.CalDividenSaham
import com.bcasekuritas.mybest.app.domain.dto.response.CalIpo
import com.bcasekuritas.mybest.app.domain.dto.response.CalPubExp
import com.bcasekuritas.mybest.app.domain.dto.response.CalReverseStock
import com.bcasekuritas.mybest.app.domain.dto.response.CalRightIssue
import com.bcasekuritas.mybest.app.domain.dto.response.CalRups
import com.bcasekuritas.mybest.app.domain.dto.response.CalSahamBonus
import com.bcasekuritas.mybest.app.domain.dto.response.CalStockSplit
import com.bcasekuritas.mybest.app.domain.dto.response.CalWarrant
import com.bcasekuritas.mybest.app.domain.dto.response.CalendarData
import com.bcasekuritas.mybest.databinding.ItemCalendarBinding
import com.bcasekuritas.mybest.ext.common.compareDayAndMonth
import com.bcasekuritas.mybest.ext.common.compareMonthAndYear
import com.bcasekuritas.mybest.ext.common.getMonthFromMillis
import com.bcasekuritas.mybest.ext.listener.OnClickAny

class CalendarAdapter(
    val context: Context, val onClickItem: OnClickAny, val recyclerView: RecyclerView,
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val listData = arrayListOf<CalendarData>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<CalendarData>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemCalendarBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemCalendarBinding,
    ) : BaseViewHolder(binding.root) {


        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBind(obj: Any) {
            val data = obj as CalendarData
            val dotData = mutableMapOf<String, String>()

            data.calItem.forEach {
                when (it) {
                    is CalWarrant -> dotData[it.type] = it.type
                    is CalRightIssue -> dotData[it.type] = it.type
                    is CalStockSplit -> dotData[it.type] = it.type
                    is CalSahamBonus -> dotData[it.type] = it.type
                    is CalDividenSaham -> dotData[it.type] = it.type
                    is CalPubExp -> dotData[it.type] = it.type
                    is CalRups -> dotData[it.type] = it.type
                    is CalIpo ->  dotData[it.type] = it.type
                    is CalReverseStock ->  dotData[it.type] = it.type
                }
            }

            val currentDate = System.currentTimeMillis()

            binding.apply {
                tvCalDate.text = data.calDate?.first.toString()

                if (listData.all { it.isSelected == false }) {
                    if (compareMonthAndYear(data.calDateData ?: 0L,  currentDate)) {
                        if (compareDayAndMonth(data.calDateData ?: 0L, currentDate)) {
                            safeUpdateSelection(bindingAdapterPosition)
                            return
                        }
                    }
                }

                val sortedByKey = dotData.toList().sortedBy { it.first.lowercase() }
                sortedByKey.forEach { (key, _) ->
                    setupDots(key, binding.lyDot)
                }

                clDate.setOnClickListener {
                    if (data.calDate?.second == getMonthFromMillis(data.selectedMonth ?: 0)){
                        updateSelection(bindingAdapterPosition)
                        onClickItem.onClickAny(data)
                    }
                }

                if (data.isSelected == true) {
                    vwSelected.isGone = false
                    tvCalDate.setTextColor(ContextCompat.getColor(context, R.color.txtWhiteBlack))
                    Handler(Looper.getMainLooper()).postDelayed({
                        onClickItem.onClickAny(data)
                    }, 200)
                } else {
                    vwSelected.isGone = true

                    if (data.calDate?.second != getMonthFromMillis(data.selectedMonth ?: 0)){
                        tvCalDate.setTextColor(ContextCompat.getColor(context, R.color.textSecondary))
                    } else {
                        tvCalDate.setTextColor(ContextCompat.getColor(context, R.color.txtBlackWhite))
                    }
                }

                lyDot.isVisible = dotData.isNotEmpty() && data.isSelected == false && data.calDate?.second == getMonthFromMillis(data.selectedMonth ?: 0)
            }
        }

    }

    private fun safeUpdateSelection(position: Int) {
        recyclerView.post {
            updateSelection(position)
        }
    }

    fun updateSelection(position: Int) {
        for (i in listData.indices) {
            listData[i].isSelected = (i == position)
        }
        notifyDataSetChanged()
    }

    fun getColor(type: String): Int {
        var colorId = 0
        when (type) {
            "Warrant" -> colorId = R.color.calWarrant
            "Right Issue" -> colorId = R.color.calRightIssue
            "Reverse Split" -> colorId = R.color.calReverseSplit
            "Stock Split" -> colorId = R.color.calStockSplit
            "Bonus" -> colorId = R.color.calBonus
            "Dividend" -> colorId = R.color.calDividen
            "Public Expose" -> colorId = R.color.calPubExp
            "RUPS" -> colorId = R.color.calRUPS
            "E-IPO" -> colorId = R.color.calEIPO
        }
        return colorId
    }


    private fun setupDots(type: String, dotLayout: ViewGroup) {
        val dot = ImageView(context)

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(2, 0, 2, 0) // Set margins if needed

        dot.layoutParams = layoutParams

        val unselectedDrawable = ContextCompat.getDrawable(context, R.drawable.ic_dot_unselected)
        val color = ContextCompat.getColor(context, getColor(type))
        unselectedDrawable?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        dot.setImageDrawable(unselectedDrawable)
        dotLayout.addView(dot)
    }
}