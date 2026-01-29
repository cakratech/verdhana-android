package com.bcasekuritas.mybest.app.feature.stockdetail.corporateaction.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
import com.bcasekuritas.mybest.databinding.ItemCorporateBonusBinding
import com.bcasekuritas.mybest.databinding.ItemCorporateDividendBinding
import com.bcasekuritas.mybest.databinding.ItemCorporateIpoBinding
import com.bcasekuritas.mybest.databinding.ItemCorporateReverseSplitBinding
import com.bcasekuritas.mybest.databinding.ItemCorporateRightIssueBinding
import com.bcasekuritas.mybest.databinding.ItemCorporateRupsPublicBinding
import com.bcasekuritas.mybest.databinding.ItemCorporateStockSplitBinding
import com.bcasekuritas.mybest.databinding.ItemCorporateWarrantBinding
import com.bcasekuritas.mybest.ext.common.convertMillisToDate
import com.bcasekuritas.mybest.ext.converter.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.other.formatPrice
import com.bcasekuritas.mybest.ext.other.formatPriceWithDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithTwoDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimalOptional

class CorporateActionAdapter: RecyclerView.Adapter<BaseViewHolder>() {
    companion object {
        private const val VIEW_DIVIDEND = 1
        private const val VIEW_RUPS = 2
        private const val VIEW_PUBLIC_EXPOSE = 3
        private const val VIEW_BONUS = 4
        private const val VIEW_STOCK_SPLIT = 5
        private const val VIEW_REVERSE_SPLIT = 6
        private const val VIEW_RIGHT_ISSUE = 7
        private const val VIEW_IPO = 8
        private const val VIEW_WARRANT = 9
    }

    private val listData: ArrayList<Any> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<Any>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        listData.clear()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (listData[position]) {
            is CalDividenSaham -> VIEW_DIVIDEND
            is CalRups -> VIEW_RUPS
            is CalIpo -> VIEW_IPO
            is CalWarrant -> VIEW_WARRANT
            is CalPubExp -> VIEW_PUBLIC_EXPOSE
            is CalReverseStock -> VIEW_REVERSE_SPLIT
            is CalStockSplit -> VIEW_STOCK_SPLIT
            is CalSahamBonus -> VIEW_BONUS
            is CalRightIssue -> VIEW_RIGHT_ISSUE
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_DIVIDEND -> {
                DividendViewHolder(ItemCorporateDividendBinding.inflate(inflater, parent, false))
            }
            VIEW_WARRANT -> {
                WarrantViewHolder(ItemCorporateWarrantBinding.inflate(inflater, parent, false))
            }
            VIEW_IPO -> {
                IpoViewHolder(ItemCorporateIpoBinding.inflate(inflater, parent, false))
            }
            VIEW_RUPS -> {
                RupsViewHolder(ItemCorporateRupsPublicBinding.inflate(inflater, parent, false))
            }
            VIEW_BONUS -> {
                BonusViewHolder(ItemCorporateBonusBinding.inflate(inflater, parent, false))
            }
            VIEW_STOCK_SPLIT -> {
                StockSplitViewHolder(ItemCorporateStockSplitBinding.inflate(inflater, parent, false))
            }
            VIEW_REVERSE_SPLIT -> {
                ReverseSplitViewHolder(ItemCorporateReverseSplitBinding.inflate(inflater, parent, false))
            }
            VIEW_RIGHT_ISSUE -> {
                RightIssueViewHolder(ItemCorporateRightIssueBinding.inflate(inflater, parent, false))
            }
            VIEW_PUBLIC_EXPOSE -> {
                PublicExposeViewHolder(ItemCorporateRupsPublicBinding.inflate(inflater, parent, false))
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = listData[position]
        when (holder) {
            is DividendViewHolder -> holder.onBind(item)
            is WarrantViewHolder -> holder.onBind(item)
            is BonusViewHolder -> holder.onBind(item)
            is RupsViewHolder -> holder.onBind(item)
            is RightIssueViewHolder -> holder.onBind(item)
            is ReverseSplitViewHolder -> holder.onBind(item)
            is StockSplitViewHolder -> holder.onBind(item)
            is PublicExposeViewHolder -> holder.onBind(item)
            is IpoViewHolder -> holder.onBind(item)
        }
    }

    override fun getItemCount(): Int = listData.size

    inner class DividendViewHolder(val binding: ItemCorporateDividendBinding): BaseViewHolder(binding.root) {
        override fun onBind(obj: Any) {
            super.onBind(obj)
            if (obj is CalDividenSaham) {
                binding.apply {
                    tvDividendCashVal.text = "Rp${obj.cashDividend.formatPrice()}"
                    tvDividendCumulDateVal.text = convertMillisToDate(obj.cumulativeDate, "dd MMM yyyy")
                    tvDividendExDateVal.text = convertMillisToDate(obj.exDate, "dd MMM yyyy")
                    tvDividendReceiveDateVal.text = convertMillisToDate(obj.recordingDate, "dd MMM yyyy")
                    tvDividendPayDateVal.text = convertMillisToDate(obj.paymentDate, "dd MMM yyyy")
                }
            }
        }
    }

    inner class RupsViewHolder(val binding: ItemCorporateRupsPublicBinding): BaseViewHolder(binding.root) {
        override fun onBind(obj: Any) {
            super.onBind(obj)
            if (obj is CalRups) {
                binding.apply {
                    tvDate.text = convertMillisToDate(obj.date, "EEE, dd MMM yyyy")
                    tvTime.text = obj.time
                    tvAddress.text = obj.location
                }
            }
        }
    }

    inner class IpoViewHolder(val binding: ItemCorporateIpoBinding): BaseViewHolder(binding.root) {
        override fun onBind(obj: Any) {
            super.onBind(obj)
            if (obj is CalIpo) {
                binding.apply {
                    tvIpoCompany.text = obj.companyName
                    tvDateVal.text = convertMillisToDate(obj.listingDate, "EEE, dd MMM yyyy")
                    tvIpoQty.text = obj.totalShareListed.formatPriceWithoutDecimal()
                }
            }
        }
    }

    inner class RightIssueViewHolder(val binding: ItemCorporateRightIssueBinding): BaseViewHolder(binding.root) {
        override fun onBind(obj: Any) {
            super.onBind(obj)
            if (obj is CalRightIssue) {
                binding.apply {
                    tvRatioVal.text = "${obj.oldRatio.toInt()} : ${obj.newRatio.toInt()}"
                    tvPriceVal.text = "Rp${obj.price.formatPriceWithoutDecimal()}"
                    tvFactorVal.text = obj.factor.formatPriceWithDecimal()
                    tvCumulativeDateVal.text = convertMillisToDate(obj.cumulativeDate, "dd MMM yyyy")
                    tvExDateVal.text = convertMillisToDate(obj.exDate, "dd MMM yyyy")
                    tvReceivingDateVal.text = convertMillisToDate(obj.recordingDate, "dd MMM yyyy")
                    tvTradingStartVal.text = convertMillisToDate(obj.tradingStart, "dd MMM yyyy")
                    tvTradingEndVal.text = convertMillisToDate(obj.tradingEnd, "dd MMM yyyy")
                }
            }
        }
    }

    inner class BonusViewHolder(val binding: ItemCorporateBonusBinding): BaseViewHolder(binding.root) {
        override fun onBind(obj: Any) {
            super.onBind(obj)
            if (obj is CalSahamBonus) {
                binding.apply {
                    tvRatioVal.text = "${obj.oldRatio.toInt()} : ${obj.newRatio.toInt()}"
                    tvFactorVal.text = obj.factor.toString()
                    tvCumulativeDateVal.text = convertMillisToDate(obj.cumulativeDate, "dd MMM yyyy")
                    tvExDateVal.text = convertMillisToDate(obj.exDate, "dd MMM yyyy")
                    tvRecordingDateVal.text = convertMillisToDate(obj.recordingDate, "dd MMM yyyy")
                    tvPaydateVal.text = convertMillisToDate(obj.payDate, "dd MMM yyyy")
                }
            }
        }
    }

    inner class PublicExposeViewHolder(val binding: ItemCorporateRupsPublicBinding): BaseViewHolder(binding.root) {
        override fun onBind(obj: Any) {
            super.onBind(obj)
            if (obj is CalPubExp) {
                binding.apply {
                    tvDate.text = convertMillisToDate(obj.date, "EEE, dd MMM yyyy")
                    tvTime.text = obj.time
                    tvAddress.text = obj.location
                }
            }
        }
    }

    inner class StockSplitViewHolder(val binding: ItemCorporateStockSplitBinding): BaseViewHolder(binding.root) {
        override fun onBind(obj: Any) {
            super.onBind(obj)
            if (obj is CalStockSplit) {
                binding.apply {
                    tvRatioVal.text = "${obj.oldRatio.toInt()} : ${obj.newRatio.toInt()}"
                    tvDividendFactorVal.text = obj.splitFactor.toString().formatPriceWithoutDecimal()
                    tvCumulDateVal.text = convertMillisToDate(obj.cumulativeDate, "dd MMM yyyy")
                    tvDividendExDateVal.text = convertMillisToDate(obj.exDate, "dd MMM yyyy")
                    tvDividendReceiveDateVal.text = convertMillisToDate(obj.recordingDate, "dd MMM yyyy")
                }
            }
        }
    }

    inner class WarrantViewHolder(val binding: ItemCorporateWarrantBinding): BaseViewHolder(binding.root) {
        override fun onBind(obj: Any) {
            super.onBind(obj)
            if (obj is CalWarrant) {
                binding.apply {
                    tvExercisePriceVal.text = "Rp${obj.excercisePrice.formatPriceWithoutDecimal()}"
                    tvTradingStartVal.text = convertMillisToDate(obj.tradingStart, "dd MMM yyyy")
                    tvTradingEndVal.text = convertMillisToDate(obj.tradingEnd, "dd MMM yyyy")
                    tvExerciseStartVal.text = convertMillisToDate(obj.excerciseStart, "dd MMM yyyy")
                    tvExerciseEndVal.text = convertMillisToDate(obj.excerciseEnd, "dd MMM yyyy")
                }
            }
        }
    }

    inner class ReverseSplitViewHolder(val binding: ItemCorporateReverseSplitBinding): BaseViewHolder(binding.root) {
        override fun onBind(obj: Any) {
            super.onBind(obj)
            if (obj is CalReverseStock) {
                binding.apply {
                    tvRatioVal.text = "${obj.oldRatio.toInt()} : ${obj.newRatio.toInt()}"
                    tvFactorVal.text = obj.factor.formatPriceWithoutDecimalOptional()
                    tvCumulativeDateVal.text = convertMillisToDate(obj.cumulativeDate, "dd MMM yyyy")
                    tvExDateVal.text = convertMillisToDate(obj.exDate, "dd MMM yyyy")
                    tvPayDateVal.text = convertMillisToDate(obj.paymentDate, "dd MMM yyyy")
                }
            }
        }
    }

}