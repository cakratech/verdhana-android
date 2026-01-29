package com.bcasekuritas.mybest.app.feature.calendar.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.response.CalDividenSaham
import com.bcasekuritas.mybest.app.domain.dto.response.CalEvent
import com.bcasekuritas.mybest.app.domain.dto.response.CalIpo
import com.bcasekuritas.mybest.app.domain.dto.response.CalPubExp
import com.bcasekuritas.mybest.app.domain.dto.response.CalReverseStock
import com.bcasekuritas.mybest.app.domain.dto.response.CalRightIssue
import com.bcasekuritas.mybest.app.domain.dto.response.CalRups
import com.bcasekuritas.mybest.app.domain.dto.response.CalSahamBonus
import com.bcasekuritas.mybest.app.domain.dto.response.CalStockSplit
import com.bcasekuritas.mybest.app.domain.dto.response.CalWarrant
import com.bcasekuritas.mybest.databinding.ItemEventListCalendarBinding
import com.bcasekuritas.mybest.databinding.ItemEventListCalendarTypeBinding
import com.bcasekuritas.mybest.ext.listener.OnClickAnyStr
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal

class CalendarEventAdapter(
    val context: Context, val onClickAnyStr: OnClickAnyStr,
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val listData: ArrayList<Any> = arrayListOf()

    companion object {
        private const val VIEW_TYPE_TYPE = 1
        private const val VIEW_TYPE_DETAILS = 2
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<Any>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (listData[position]) {
            is String -> VIEW_TYPE_TYPE
            is CalWarrant, is CalRightIssue, is CalStockSplit, is CalSahamBonus, is CalDividenSaham,
            is CalPubExp, is CalRups, is CalIpo, is CalReverseStock,
            -> VIEW_TYPE_DETAILS

            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_TYPE -> {
                TypeViewHolder(ItemEventListCalendarTypeBinding.inflate(inflater, parent, false))
            }

            VIEW_TYPE_DETAILS -> {
                DetailsViewHolder(ItemEventListCalendarBinding.inflate(inflater, parent, false))
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = listData[position]
        when (holder) {
            is TypeViewHolder -> holder.onBind(item)
            is DetailsViewHolder -> holder.onBind(item)
        }
    }

    inner class TypeViewHolder(val binding: ItemEventListCalendarTypeBinding) :
        BaseViewHolder(binding.root) {
        override fun onBind(obj: Any) {
            super.onBind(obj)
            val item = obj as String
            binding.apply {
                tvCalendarType.text = item
                tvCalendarType.setTextColor(ContextCompat.getColor(context, getColor(item)))
                tvCalendarType.background = ContextCompat.getDrawable(context, getBackground(item))
            }

        }
    }

    inner class DetailsViewHolder(val binding: ItemEventListCalendarBinding) :
        BaseViewHolder(binding.root) {
        override fun onBind(obj: Any) {
            super.onBind(obj)
//            val item = obj as CalendarItem

            when (obj) {
                is CalWarrant -> {
                    binding.tvStock.text = obj.stockCode
                    binding.tvDesc.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                    binding.tvDesc.text = "Rp${obj.excercisePrice.formatPriceWithoutDecimal()}"
                    binding.tvDesc.setTextStyle("bold")
                    binding.clCalendarEvent.setOnClickListener {
                        val warrantData = CalEvent(
                            stockCode = obj.stockCode,
                            excercisePrice = obj.excercisePrice,
                            excerciseEnd = obj.excerciseEnd,
                            excerciseStart = obj.excerciseStart,
                            tradingEnd = obj.tradingEnd,
                            tradingStart = obj.tradingStart,
                            type = obj.type
                        )
                        onClickAnyStr.onClickAnyStr(warrantData, obj.type)
                    }
                }

                is CalRightIssue -> {
                    binding.tvStock.text = obj.stockCode
                    binding.tvDesc.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                    binding.tvDesc.text = "${obj.oldRatio.toInt()} : ${obj.newRatio.toInt()}"
                    binding.tvDesc.setTextStyle("bold")
                    binding.clCalendarEvent.setOnClickListener {
                        val rightIssueData = CalEvent(
                            stockCode = obj.stockCode,
                            oldRatio = obj.oldRatio,
                            newRatio = obj.newRatio,
                            factor = obj.factor,
                            price = obj.price,
                            cumulativeDate = obj.cumulativeDate,
                            exDate = obj.exDate,
                            recordingDate = obj.recordingDate,
                            tradingStart = obj.tradingStart,
                            tradingEnd = obj.tradingEnd,
                            type = obj.type
                        )
                        onClickAnyStr.onClickAnyStr(rightIssueData, obj.type)
                    }
                }

                is CalStockSplit -> {
                    binding.tvStock.text = obj.stockCode
                    binding.tvDesc.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                    binding.tvDesc.text = "${obj.oldRatio.toInt()} : ${obj.newRatio.toInt()}"
                    binding.tvDesc.setTextStyle("bold")
                    binding.clCalendarEvent.setOnClickListener {
                        val stockSplitData = CalEvent(
                            stockCode = obj.stockCode,
                            oldRatio = obj.oldRatio,
                            newRatio = obj.newRatio,
                            cumulativeDate = obj.cumulativeDate,
                            exDate = obj.exDate,
                            recordingDate = obj.recordingDate,
                            type = obj.type,
                            splitFactor = obj.splitFactor
                        )
                        onClickAnyStr.onClickAnyStr(stockSplitData, obj.type)
                    }
                }

                is CalReverseStock -> {
                    binding.tvStock.text = obj.stockCode
                    binding.tvDesc.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                    binding.tvDesc.text = "${obj.oldRatio.toInt()} : ${obj.newRatio.toInt()}"
                    binding.tvDesc.setTextStyle("bold")
                    binding.clCalendarEvent.setOnClickListener {
                        val stockSplitData = CalEvent(
                            stockCode = obj.stockCode,
                            oldRatio = obj.oldRatio,
                            newRatio = obj.newRatio,
                            factor = obj.factor,
                            paymentDate = obj.paymentDate,
                            cumulativeDate = obj.cumulativeDate,
                            exDate = obj.exDate,
                            type = obj.type,
                        )
                        onClickAnyStr.onClickAnyStr(stockSplitData, obj.type)
                    }
                }

                is CalSahamBonus -> {
                    binding.tvStock.text = obj.stockCode
                    binding.tvDesc.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                    binding.tvDesc.text = "${obj.oldRatio.toInt()} : ${obj.newRatio.toInt()}"
                    binding.tvDesc.setTextStyle("bold")
                    binding.clCalendarEvent.setOnClickListener {
                        val bonusData = CalEvent(
                            stockCode = obj.stockCode,
                            oldRatio = obj.oldRatio,
                            newRatio = obj.newRatio,
                            factor = obj.factor,
                            cumulativeDate = obj.cumulativeDate,
                            exDate = obj.exDate,
                            recordingDate = obj.recordingDate,
                            payDate = obj.payDate,
                            type = obj.type,
                        )
                        onClickAnyStr.onClickAnyStr(bonusData, obj.type)
                    }
                }

                is CalDividenSaham -> {
                    binding.tvStock.text = obj.stockCode
                    binding.tvDesc.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                    binding.tvDesc.text = "Rp${obj.cashDividend.formatPriceWithoutDecimal()}"
                    binding.tvDesc.setTextStyle("bold")
                    binding.clCalendarEvent.setOnClickListener {
                        val dividenData = CalEvent(
                            stockCode = obj.stockCode,
                            cashDividend = obj.cashDividend,
                            cumulativeDate = obj.cumulativeDate,
                            exDate = obj.exDate,
                            recordingDate = obj.recordingDate,
                            paymentDate = obj.paymentDate,
                            type = obj.type,
                        )
                        onClickAnyStr.onClickAnyStr(dividenData, obj.type)
                    }
                }

                is CalPubExp -> {
                    binding.tvStock.text = obj.stockCode
                    binding.tvDesc.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                    binding.tvDesc.text = obj.location
                    binding.tvDesc.setTextStyle("default")
                    binding.clCalendarEvent.setOnClickListener {
                        val pubExData = CalEvent(
                            stockCode = obj.stockCode,
                            date = obj.date,
                            time = obj.time,
                            location = obj.location,
                            type = obj.type,
                        )
                        onClickAnyStr.onClickAnyStr(pubExData, obj.type)
                    }
                }

                is CalRups -> {
                    binding.tvStock.text = obj.stockCode
                    binding.tvDesc.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                    binding.tvDesc.text = "at ${obj.location} "
                    binding.tvDesc.setTextStyle("default")
                    binding.clCalendarEvent.setOnClickListener {
                        val rupsData = CalEvent(
                            stockCode = obj.stockCode,
                            date = obj.date,
                            time = obj.time,
                            location = obj.location,
                            type = obj.type,
                        )
                        onClickAnyStr.onClickAnyStr(rupsData, obj.type)
                    }
                }

                is CalIpo -> {
                    binding.tvStock.text = obj.companyName
                    binding.tvDesc.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                    binding.tvDesc.text = obj.totalShareListed.formatPriceWithoutDecimal()
                    binding.tvDesc.setTextStyle("bold")
                    binding.clCalendarEvent.setOnClickListener {
                        val ipoData = CalEvent(
                            stockCode = obj.stockCode,
                            companyName = obj.companyName,
                            totalShareListed = obj.totalShareListed,
                            listingDate = obj.listingDate,
                            type = obj.type,
                        )
                        onClickAnyStr.onClickAnyStr(ipoData, obj.type)
                    }
                }
            }
        }
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

    fun getBackground(type: String): Int {
        var bgId = 0
        when (type) {
            "Warrant" -> bgId = R.drawable.bg_ffffff_stroke_27ae60_4
            "Right Issue" -> bgId = R.drawable.bg_ffffff_stroke_192f86_4
            "Reverse Split" -> bgId = R.drawable.bg_ffffff_stroke_689d11_4
            "Stock Split" -> bgId = R.drawable.bg_ffffff_stroke_0154fa_4
            "Bonus" -> bgId = R.drawable.bg_ffffff_stroke_e14343_4
            "Dividend" -> bgId = R.drawable.bg_ffffff_stroke_02b9cb_4
            "Public Expose" -> bgId = R.drawable.bg_ffffff_stroke_ff9900_4
            "RUPS" -> bgId = R.drawable.bg_ffffff_stroke_ed6cb1_4
            "E-IPO" -> bgId = R.drawable.bg_ffffff_stroke_6a44d9_4
        }
        return bgId
    }
}