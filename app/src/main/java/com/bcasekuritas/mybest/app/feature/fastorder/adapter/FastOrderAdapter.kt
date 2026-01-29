package com.bcasekuritas.mybest.app.feature.fastorder.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.request.AmendFastOrderReq
import com.bcasekuritas.mybest.app.domain.dto.request.CancelFastOrderReq
import com.bcasekuritas.mybest.app.domain.dto.request.SendOrderReq
import com.bcasekuritas.mybest.app.domain.dto.response.FastOrderBook
import com.bcasekuritas.mybest.databinding.ItemTradingOrderBinding
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.prefs.SharedPreferenceManager
import com.bcasekuritas.mybest.widget.textview.CustomTextView

class FastOrderAdapter(
    private val onClickAny: OnClickAny,
    private val context: Context,
    private val prefManager: SharedPreferenceManager,
) : RecyclerView.Adapter<FastOrderAdapter.ItemViewHolder>(),
    ShowDropDown by ShowDropDownImpl() {

    private val data: ArrayList<FastOrderBook> = arrayListOf()
    private var totalBid: Long = 0
    private var count = 0

    private val buyClickList = listOf("Buy", "Amend", "Withdraw")
    private val sellClickList = listOf("Sell", "Amend", "Withdraw")
    private var isOrderCounts = false
    private var buySell = ""
    private var amendPrice = 0.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemTradingOrderBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(data[position])
    }

    override fun getItemCount(): Int = data.size
    inner class ItemViewHolder(
        val binding: ItemTradingOrderBinding,
    ) : BaseViewHolder(binding.root) {
        fun onBind(data: FastOrderBook) {

            val colorRed = ContextCompat.getColor(context, R.color.textDown)
            val colorGreen = ContextCompat.getColor(context, R.color.textUp)

            binding.apply {
                // Always set both bid and offer values
                tvPriceValue.text = data.price.formatPriceWithoutDecimal()
                tvBidValue.text = data.quantity.formatPriceWithoutDecimal()
                tvOfferValue.text = data.quantity.formatPriceWithoutDecimal()

                progressBarBid.max = data.totQuantityL
                progressBarBid.progress = data.quantityL
                progressBarOffer.max = data.totQuantityL
                progressBarOffer.progress = data.quantityL

                if (data.isBid) {
                    getPriceColor(tvPriceValue, data.price, data.closePrice)
                    tvBidValue.setTextColor(colorGreen)

                    tvOfferValue.isGone = true
                    progressBarOffer.isGone = true

                    tvBidValue.isGone = false
                    progressBarBid.isGone = false

                } else {
                    getPriceColor(tvPriceValue, data.price, data.closePrice)
                    tvOfferValue.setTextColor(colorRed)

                    tvBidValue.isGone = true
                    progressBarBid.isGone = true

                    tvOfferValue.isGone = false
                    progressBarOffer.isGone = false
                }
            }

            if (prefManager.isOnAmendFO && buySell == "B") {
                if (data.price == amendPrice){

                    binding.tvBuyValue.setBackgroundResource(R.drawable.bg_27ae60_8)

                    binding.tvBuyValue.text = if (isOrderCounts) {
                        "${data.totOrdQtyBid.div(100)} (${data.totOrdBid})"
                    } else {
                        "${data.totOrdQtyBid.div(100)}"
                    }
                } else {
                    binding.tvBuyValue.text = ""
                    binding.tvBuyValue.setBackgroundResource(R.drawable.bg_cbe7d7_8)

                    val drawable = ContextCompat.getDrawable(
                        binding.tvBuyValue.context,
                        R.drawable.ic_plus_circular
                    )?.mutate()
                    drawable?.let {
                        DrawableCompat.setTint(it, ContextCompat.getColor(context, R.color.bgWhite))
                        binding.tvBuyValue.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            null,
                            null,
                            it
                        )  // Set the icon
                    }

                    binding.tvBuyValue.gravity = Gravity.CENTER
                }
            } else {
                if (data.totOrdQtyBid != 0L) {
                    binding.tvBuyValue.setBackgroundResource(R.drawable.bg_27ae60_8)

                    binding.tvBuyValue.text = if (isOrderCounts) {
                        "${data.totOrdQtyBid.div(100)} (${data.totOrdBid})"
                    } else {
                        "${data.totOrdQtyBid.div(100)}"
                    }
                } else {
                    binding.tvBuyValue.text = ""
                    binding.tvBuyValue.setBackgroundResource(R.drawable.bg_cbe7d7_8)
                }
                binding.tvBuyValue.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    null,
                    null
                )  // Remove any icons
            }

            if (prefManager.isOnAmendFO && buySell == "S") {
                if (data.price == amendPrice){

                    binding.tvSellValue.setBackgroundResource(R.drawable.bg_e14343_8)

                    binding.tvSellValue.text = if (isOrderCounts) {
                        "${data.totOrdQtyOffer.div(100)} (${data.totOrdOffer})"
                    } else {
                        "${data.totOrdQtyOffer.div(100)}"
                    }

                } else {
                    binding.tvSellValue.text = ""
                    binding.tvSellValue.setBackgroundResource(R.drawable.bg_fed9d9_8)

                    val drawable = ContextCompat.getDrawable(
                        binding.tvSellValue.context,
                        R.drawable.ic_plus_circular
                    )?.mutate()
                    drawable?.let {
                        DrawableCompat.setTint(it, ContextCompat.getColor(context, R.color.bgWhite))
                        binding.tvSellValue.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            null,
                            null,
                            it
                        )  // Set the icon
                    }

                    binding.tvSellValue.gravity = Gravity.CENTER
                }
            } else {
                if (data.totOrdQtyOffer != 0L) {
                    binding.tvSellValue.setBackgroundResource(R.drawable.bg_e14343_8)

                    binding.tvSellValue.text = if (isOrderCounts) {
                        "${data.totOrdQtyOffer.div(100)} (${data.totOrdOffer})"
                    } else {
                        "${data.totOrdQtyOffer.div(100)}"
                    }
                } else {
                    binding.tvSellValue.text = ""
                    binding.tvSellValue.setBackgroundResource(R.drawable.bg_fed9d9_8)
                }
                binding.tvSellValue.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    null,
                    null
                )  // Remove any icons
            }

            /** LONG CLICK */

            binding.tvBuyValue.setOnLongClickListener {
                if (data.totOrdBid != 0) {

                    showSimpleDropDownWidth80(
                        itemView.context,
                        buyClickList,
                        binding.tvBuyValue
                    ) { index, value ->
                        // Index 0 = buy, 1= Amend, 2 = withdraw
                        when (index) {
                            0 -> {
                                val fastOrder = SendOrderReq(
                                    clOrderRef = "",
                                    board = "",
                                    orderTime = 0L,
                                    buySell = "B",
                                    stockCode = "",
                                    orderType = "",
                                    timeInForce = "",
                                    orderPeriod = null,
                                    accType = "", //
                                    ordQty = 0.0,
                                    ordPrice = data.price,
                                    accNo = "",
                                    inputBy = "",
                                    clientCode = "",
                                    sessionId = "",
                                    investType = "",
                                    status = "",
                                    ip = "",
                                    sameOrderList = data.sameOrderList
                                )
                                onClickAny.onClickAny(fastOrder)
                            }

                            1 -> {
                                prefManager.isOnAmendFO = !prefManager.isOnAmendFO

                                val sendAmend = AmendFastOrderReq(
                                    stock = "",
                                    board = "RG",
                                    buySell = "B",
                                    oldPrice = data.price,
                                    newPrice = 0.0,
                                    inputBy = "",
                                    ipAddress = "",
                                    channel = 0,
                                    volume = data.totOrdQtyBid.div(100).toString(),
                                    accNo = "",
                                    sameOrderList = data.sameOrderList
                                )

                                amendPrice = data.price
                                buySell = "B"

                                onClickAny.onClickAny(sendAmend)
                                notifyDataSetChanged()

                            }

                            2 -> {
                                val withdrawFastOrder = CancelFastOrderReq(
                                    stock = "",
                                    board = "",
                                    buySell = "B",
                                    price = data.price,
                                    inputBy = "",
                                    ipAddress = "",
                                    qty = data.totOrdQtyBid.div(100).toDouble(),
                                    channel = 0,
                                    accNo = "",
                                    sameOrderList = data.sameOrderList
                                )
                                onClickAny.onClickAny(withdrawFastOrder)
                            }
                        }

                    }
                    true
                } else {
                    false
                }
            }

            binding.tvSellValue.setOnLongClickListener {
                if (data.totOrdOffer != 0) {

                    // Index 0 = sell, 1= Amend, 2 = withdraw
                    showSimpleDropDownWidth80(
                        itemView.context,
                        sellClickList,
                        binding.tvSellValue
                    ) { index, value ->
                        when (index) {
                            0 -> {
                                val fastOrder = SendOrderReq(
                                    clOrderRef = "",
                                    board = "",
                                    orderTime = 0L,
                                    buySell = "S",
                                    stockCode = "",
                                    orderType = "",
                                    timeInForce = "",
                                    orderPeriod = null,
                                    accType = "", //
                                    ordQty = 0.0,
                                    ordPrice = data.price,
                                    accNo = "",
                                    inputBy = "",
                                    clientCode = "",
                                    sessionId = "",
                                    investType = "",
                                    status = "",
                                    ip = "",
                                    sameOrderList = data.sameOrderList
                                )
                                onClickAny.onClickAny(fastOrder)
                            }

                            1 -> {
                                prefManager.isOnAmendFO = !prefManager.isOnAmendFO

                                val sendAmend = AmendFastOrderReq(
                                    stock = "",
                                    board = "RG",
                                    buySell = "S",
                                    oldPrice = data.price,
                                    newPrice = 0.0,
                                    inputBy = "",
                                    ipAddress = "",
                                    channel = 0,
                                    volume = data.totOrdQtyOffer.div(100).toString(),
                                    accNo = "",
                                    sameOrderList = data.sameOrderList
                                )

                                amendPrice = data.price
                                buySell = "S"

                                onClickAny.onClickAny(sendAmend)
                                notifyDataSetChanged()

                            }

                            2 -> {
                                val withdrawFastOrder = CancelFastOrderReq(
                                    stock = "",
                                    board = "",
                                    buySell = "S",
                                    price = data.price,
                                    inputBy = "",
                                    ipAddress = "",
                                    qty = data.totOrdQtyOffer.div(100).toDouble(),
                                    channel = 0,
                                    accNo = "",
                                    sameOrderList = data.sameOrderList
                                )
                                onClickAny.onClickAny(withdrawFastOrder)
                            }
                        }
                    }
                    true
                } else {
                    false
                }

            }

            /** TAP */
            binding.tvBuyValue.setOnClickListener {

                if (prefManager.isOnAmendFO) {
                    prefManager.isOnAmendFO = !prefManager.isOnAmendFO
                    amendPrice = 0.0

                    val sendAmend = AmendFastOrderReq(
                        stock = "",
                        board = "RG",
                        buySell = "B",
                        oldPrice = null,
                        newPrice = data.price,
                        inputBy = "",
                        ipAddress = "",
                        channel = 0,
                        volume = data.totOrdQtyBid.div(100).toString(),
                        accNo = "",
                        sameOrderList = data.sameOrderList
                    )

                    onClickAny.onClickAny(sendAmend)
                    notifyDataSetChanged()
                } else {

                    val fastOrder = SendOrderReq(
                        clOrderRef = "",
                        board = "",
                        orderTime = 0L,
                        buySell = "B",
                        stockCode = "",
                        orderType = "",
                        timeInForce = "",
                        orderPeriod = null,
                        accType = "", //
                        ordQty = 0.0,
                        ordPrice = data.price,
                        accNo = "",
                        inputBy = "",
                        clientCode = "",
                        sessionId = "",
                        investType = "",
                        status = "",
                        ip = "",
                        sameOrderList = data.sameOrderList
                    )
                    onClickAny.onClickAny(fastOrder)
                }
            }

            binding.tvSellValue.setOnClickListener {

                if (prefManager.isOnAmendFO) {
                    amendPrice = 0.0
                    prefManager.isOnAmendFO = !prefManager.isOnAmendFO

                    val sendAmend = AmendFastOrderReq(
                        stock = "",
                        board = "RG",
                        buySell = "S",
                        oldPrice = null,
                        newPrice = data.price,
                        inputBy = "",
                        ipAddress = "",
                        channel = 0,
                        volume = data.totOrdQtyBid.div(100).toString(),
                        accNo = "",
                        sameOrderList = data.sameOrderList
                    )

                    onClickAny.onClickAny(sendAmend)
                    notifyDataSetChanged()
                } else {

                    val fastOrder = SendOrderReq(
                        clOrderRef = "",
                        board = "",
                        orderTime = 0L,
                        buySell = "S",
                        stockCode = "",
                        orderType = "",
                        timeInForce = "",
                        orderPeriod = null,
                        accType = "", //
                        ordQty = 0.0,
                        ordPrice = data.price,
                        accNo = "",
                        inputBy = "",
                        clientCode = "",
                        sessionId = "",
                        investType = "",
                        status = "",
                        ip = "",
                        sameOrderList = data.sameOrderList
                    )
                    onClickAny.onClickAny(fastOrder)
                }
            }

        }
    }

    fun setData(list: List<FastOrderBook>?, isOrderCount: Boolean) {
        if (list != null) {
            isOrderCounts = isOrderCount

            if (data.size != 0) {
                updateData(list)
            } else {
                val sortedList = list
                    .filter { it.price != 0.0 }
                    .sortedByDescending { it.price }
                    .mapIndexed { index, fastOrderBook ->
                        FastOrderBook(
                            index = index,
                            isBid = fastOrderBook.isBid,
                            price = fastOrderBook.price,
                            closePrice = fastOrderBook.closePrice,
                            quantity = fastOrderBook.quantity,
                            quantityL = fastOrderBook.quantityL,
                            totQuantityL = fastOrderBook.totQuantityL,
                            totOrdBid = fastOrderBook.totOrdBid,
                            totOrdQtyBid = fastOrderBook.totOrdQtyBid,
                            totOrdOffer = fastOrderBook.totOrdOffer,
                            totOrdQtyOffer = fastOrderBook.totOrdQtyOffer,
                            progress = fastOrderBook.progress,
                            sameOrderList = fastOrderBook.sameOrderList
                        )
                    }

                clearData()
                data.addAll(sortedList)
                notifyDataSetChanged()
            }
        }
    }

    private fun updateData(updatedData: List<FastOrderBook>) {
        updatedData.forEach { uData ->
            if (uData.price != 0.0) {
                val findData = data.find { it.price == uData.price }
                findData?.let { result ->
                    uData.index = result.index
                    data[result.index] = uData
                    notifyItemChanged(result.index)
                }
            }
        }
    }

    fun updateBuySell(updatedData: List<FastOrderBook>) {
        updatedData.forEach { uData ->
            if (uData.price != 0.0) {
                val findData = data.find { it.price == uData.price }
                findData?.let { result ->
                    uData.index = result.index
                    data[result.index] = uData
                    notifyItemChanged(result.index)
                }
            }
        }
    }

    fun updateOrderCounts(isOrderCount: Boolean) {
        isOrderCounts = isOrderCount
        notifyDataSetChanged()
    }

    fun clearData() {
        totalBid = 0
        count = 0
        data.clear()
        notifyDataSetChanged()

    }

    private fun getPriceColor(tvPrice: CustomTextView, currentPrice: Double, closePrice: Double) {


        if (currentPrice > closePrice) {
            tvPrice.setTextColor(ContextCompat.getColor(context, R.color.textUp))
        } else if (currentPrice < closePrice) {
            tvPrice.setTextColor(ContextCompat.getColor(context, R.color.textDown))
        } else {
            tvPrice.setTextColor(ContextCompat.getColor(context, R.color.black))
        }
    }
}