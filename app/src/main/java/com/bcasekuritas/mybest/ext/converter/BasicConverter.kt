package com.bcasekuritas.mybest.ext.converter

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.ViewTreeObserver
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.ext.common.getCurrentTimeInMillis
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.mybest.widget.edittext.EditTextDrawable
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TreeMap

class BasicConverter {
    private val map = TreeMap<Int, String>()

    fun toRoman(number: Int): String? {
        val l = map.floorKey(number)
        return if (number == l) {
            map[number]
        } else map[l!!]
            .toString() + toRoman(number - l)
    }

    init {
        map[1000] = "M"
        map[900] = "CM"
        map[500] = "D"
        map[400] = "CD"
        map[100] = "C"
        map[90] = "XC"
        map[50] = "L"
        map[40] = "XL"
        map[10] = "X"
        map[9] = "IX"
        map[5] = "V"
        map[4] = "IV"
        map[1] = "I"
    }


}

fun String?.GET_STATUS_ORDER(): String? {
    return if (this != null) {
        when (this) {
            "PN", "PA", "PS", "PJ" , "PT", "PB" -> "Pending"
            "O" -> "Open"
            "R" -> "Rejected"
            "P" -> "Partial Matched"
            "M" -> "Matched"
            "RA" -> "Request Amend"
            "RW" -> "Request Cancel"
            "RM" -> "Request Approval"
            "A" -> "Amended"
            "C" -> "Withdrawn"
            "CM" -> "Partial Withdrawn"
            "WC" -> "Waiting To Confirm"
            else -> this
        }
    } else this
}

fun String?.GET_STATUS_EXERCISE(): String? {
    return if (this != null) {
        when (this) {
            "0" -> "Rejected"
            "1" -> "Accepted"
            "9" -> "Completed"
            else -> this
        }
    } else this
}

fun String?.GET_IDX_BOARD(): String {
   return when (this) {
        "3" -> "Acceleration"
        "4" -> "Watch-List"
        else -> ""
    }
}

fun String?.GET_STATUS_ADVANCE_ORDER(): String? {
    return if (this != null) {
        when (this) {
            "PN" -> "Pending"
            "PA" -> "Pending"
            "PS" -> "Pending"
            "PJ" -> "Pending"
            "PT" -> "Pending"
            "O", "O2" -> "Open"
            "R" -> "Rejected"
            "P" -> "Partial Matched"
            "M" -> "Matched"
            "RA" -> "Request Amend"
            "RW" -> "Request Cancel"
            "RM" -> "Request Approval"
            "A" -> "Amended"
            "C" -> "Withdrawn"
            "CM" -> "Partial Withdrawn"
            "PB", "B2" -> "Pending Compare"
            "S", "stop" -> "Stopped"
            else -> this
        }
    } else this
}

fun Int?.GET_ADVANCE_TYPE(): String? {
    return if (this != null) {
        when (this) {
            0,10 -> "Slicing"
            2 -> "Automatic Order"
            6 -> "GTC"
            9 -> "Stop Loss/Take Profit"
            else -> this.toString()
        }
    } else this
}

fun Int?.GET_BRACKET_STATUS(): String {
    return when (this) {
        0 -> "Pending Compare Buy"
        1 -> "Open Buy"
        2 -> "Partial Buy"
        3 -> "Pending Compare Take Profit"
        4 -> "Pending Compare Stop Loss"
        5 -> "Pending Compare SL/TP"
        6 -> "Open Take Profit"
        7 -> "Open Stop Loss"
        8 -> "Partial Take Profit"
        9 -> "Partial Stop Loss"
        10 -> "Matched"
        11 -> "Stopped"
        else -> "None"
        }

}

fun String?.GET_STATUS_ORDER_TO_INT(): Int{
    return when (this) {
            "O", "P", "PT", "PB" -> 0
            else -> 1
        }
}

fun String?.GET_TIME_INFORCE(data: Long): String? {
    return if (this != null) {
        when (this) {
            "0" -> "Day"
            "1" -> {
                val date = Date(data)
                date.let { SimpleDateFormat("dd MMM yyyy, HH:mm").format(it) }
            }

            else -> "GTD"
        }
    } else this
}

fun Int?.GET_COMPARE_SLTP(): String {
    return when (this) {
        0 -> "0"
        1 -> "3"
        2 -> "4"
        else -> "0"
    }
}

fun Int.GET_COMPARE_VALUE(): String {
    return when (this) {
        0 -> "Last Price"
        3 -> "Best Offer"
        4 -> "Best Bid"
        else -> "Last Price"
    }
}

fun String.GET_STATUS_BUY_SELL(): String {
    return if (this == "B") "Buy" else "Sell"
}

fun String.LOT_DIVBIDED_BY_100(): String {
    val lot = this.toInt()
    val div = lot.div(100)
    return div.toString()
}

fun String?.GET_ORDER_TYPE(): String {
    return if (this != null) {
        when (this) {
            "1" -> "Day"
            "2" -> "Slice"
            "3" -> "GTC"
            else -> "-"
        }
    } else "-"
}

fun EditTextDrawable.edtToDouble(): Double {
    val stringValue = this.text.toString().replace(",", "")
    return try {
        stringValue.toDoubleOrNull() ?: 0.0
    } catch (e: NumberFormatException) {
        // Handle the case where text is not a valid Double
        0.0
    }
}

fun String.formatPriceWithoutDecimal(): String {
    val doubleValue = this.toDoubleOrNull() ?: 0.0
    val rupiahFormatter = DecimalFormat("#,###,###", DecimalFormatSymbols.getInstance(Locale.US))
    return rupiahFormatter.format(doubleValue)
}

fun String.removeSeparator(): String {
    return if (this.isNotEmpty()){
        replace(",", "")
    } else {
        return "0"
    }
}

fun EditTextDrawable.removeSeparator(): String {
    return this.text.toString().replace(",", "")
}

fun String.GET_IMAGE_SECTOR(): Int {
    return when (this) {
        "IDXBASIC"-> R.drawable.raw_goods
        "IDXINFRA"-> R.drawable.infrastructure
        "IDXHEALTH"-> R.drawable.heath
        "IDXINDUST"-> R.drawable.industry
        "IDXCYCLIC"-> R.drawable.primary
        "IDXENERGY"-> R.drawable.energy
        "IDXTRANS"-> R.drawable.transportation
        "IDXNONCYC"-> R.drawable.non_primary
        "IDXPROPERT"-> R.drawable.property
        "IDXTECHNO"-> R.drawable.technology
        "IDXFINANCE"-> R.drawable.financial
        else -> R.drawable.industry
    }
}

fun String?.GET_STATUS_CASH_WITHDRAW(): String? {
    return if (this != null) {
        when (this) {
            "S", "0" -> "Success"
            "P", "N" -> "Sent"
            "F", "C", "B", "O", "R", "T" -> "Rejected"
            else -> this
        }
    } else this
}

fun String?.GET_COLOR_STATUS_CASH_WITHDRAW(): Int {
    return if (this != null) {
        when (this) {
            "S", "0" -> R.color.textUp
            "P", "N" -> R.color.brandAccent
            "F", "C", "B", "O", "R", "T" -> R.color.textDown
            else -> R.color.txtBlackWhite
        }
    }
    else R.color.txtBlackWhite
}

fun String?.GET_RDN_HISTORY_TYPE(): String? {
    return if (this != null) {
        when (this) {
            "C" -> "Deposit RDN"
            "W", "D" -> "Withdrawal"
            else -> "Withdrawal"
        }
    } else this
}

fun GET_IMAGE_INDEX(idImg: Int): Int {
    return when (idImg) {
        0 -> R.drawable.bg_circle_0154fa
        1 -> R.drawable.bg_circle_27ae60
        2 -> R.drawable.bg_circle_e14343
        3 -> R.drawable.bg_circle_139fcb
        4 -> R.drawable.bg_circle_ff9900
        5 -> R.drawable.bg_circle_f25532
        6 -> R.drawable.bg_circle_6a44d9
        7 -> R.drawable.bg_circle_ed6cb1
        else -> R.drawable.bg_circle_0154fa
    }
}

fun GET_BACKGROUND_IMAGE_RANDOM_ROUNDED(): Int {
    val randomInt = (0..7).random()
    return GET_IMAGE_INDEX(randomInt)
}

fun getRandomColorBrokerSum(tag: Int): Int {
    val colorResources = listOf(
        R.color.brokerPurple,
        R.color.brokerCyan,
        R.color.calRightIssue,
        R.color.yellowIndex,
        R.color.brokerRed,
        R.color.calWarrant
    )

    val colorIndex = tag % colorResources.size
    return colorResources[colorIndex]
}

fun GET_4_CHAR_STOCK_CODE(stockCode: String):String {
    return if (stockCode.length > 4) stockCode.substring(0,4) else stockCode
}

fun GET_OPERATOR_COMPARE_STR(type: Int): String {
    return when (type) {
        0 -> "="
        1 -> ">"
        2 -> "≥"
        3 -> "<"
        4 -> "≤"
        else -> ""
    }
}

fun GET_STATUS_TRIGGER_CATEGORY(value: Int): String {
    return when (value) {
        0 -> "Last Price"
        3 -> "Best Offer"
        4 -> "Best Bid"
        else -> "Last Price"
    }
}

fun GET_ESTATEMENT_TYPE(value: String): String {
    return when(value) {
        "Statement of Account" -> "SOA"
        "Client Sell Activity" -> "ACT"
        "Client Buy Activity" -> "ACTBUY"
        "Cash Dividend" -> "CD"
        "Bond Coupon" -> "BOND"
        "Transaction Report" -> "TAX"
        else -> ""
    }
}

fun View.toBitmap(): Bitmap {
    // Measure if not already measured
    Timber.d("width : ${this.width}, height : ${this.height}")
    // Create bitmap with the measured width and height
    val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    this.draw(canvas)
    return bitmap
}

fun View.toBitmap(callback: (Bitmap) -> Unit) {
    val viewTreeObserver = this.viewTreeObserver
    if (viewTreeObserver.isAlive) {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Remove the listener to prevent multiple calls
                this@toBitmap.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Now the view has been laid out, so we can get its width and height
                Timber.d("width : ${this@toBitmap.width}, height : ${this@toBitmap.height}")

                // Create bitmap with the measured width and height
                val bitmap = Bitmap.createBitmap(this@toBitmap.width, this@toBitmap.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                this@toBitmap.draw(canvas)

                // Pass the bitmap to the callback
                callback(bitmap)
            }
        })
    }
}


fun GET_PIPELINE_STATUS_EIPO(
    inputBookDate: String,
    deadlineBookBuilding: Long,
    inputOfferDateStart: String,
    deadlineOffering: Long,
    inputAllocDate: String,
    inputDistDate: String,
    inputListingDate: String
): Int{
    val bookingDate = DateUtils.getTimeInMillisFromDateStringFormat(inputBookDate, "")
    val offeringDateStart = DateUtils.getTimeInMillisFromDateStringFormat(inputOfferDateStart, "")
    val allotmentDate = DateUtils.getTimeInMillisFromDateStringFormat(inputAllocDate, "")
    val distributionDate = DateUtils.getTimeInMillisFromDateStringFormat(inputDistDate, "")
    val listingDate = DateUtils.getTimeInMillisFromDateStringFormat(inputListingDate, "")
    val currentDate = getCurrentTimeInMillis()

    // 1: Book building 2: Offering 3: Allocation 4: Distribution 5: IPO
    var result = when {
        listingDate != 0L && currentDate >= listingDate -> 5
        distributionDate != 0L && currentDate >= distributionDate -> 4
        allotmentDate != 0L && currentDate >= allotmentDate -> {
           if (deadlineOffering != 0L && currentDate < deadlineOffering) 2 else 3
        }
        offeringDateStart != 0L && currentDate >= offeringDateStart -> {
            if (deadlineBookBuilding != 0L && currentDate > deadlineBookBuilding && currentDate < offeringDateStart) 8 else 2
        }
        deadlineBookBuilding != 0L && currentDate > deadlineBookBuilding -> 8
        bookingDate != 0L && currentDate >= bookingDate -> 1
        else -> 0
    }
    return result
}

fun GET_STATUS_STAGE_EIPO(statusId: String) : String {
    return when (statusId) {
        "0" -> "Initiation"
        "1" -> "Pre Effective"
        "2" -> "Book Building"
        "3" -> "Offering"
        "4" -> "Allocation"
        "5" -> "Closed"
        "6" -> "Postpone"
        "8" -> "Waiting For Offering"
        "Distribution" -> "Distribution"
        "IPO" -> "IPO"
        else -> ""
    }
}

fun CONVERT_PIPELINE_STATUS_NAME_EIPO(inputStatus: Int): String {
    // 1: Book building 2: Offering 3: Allocation 4: Distribution 5: IPO
    return when (inputStatus) {
        1 -> "Book Building"
        2 -> "Offering"
        3 -> "Allocation"
        4 -> "Distribution"
        5 -> "IPO"
        else -> ""
    }
}

fun String?.GET_STATUS_IPO_ORDER_LIST(): String {
    return if (this != null) {
        when (this) {
            "0" -> "Submitted"
            "1" -> "Approved"
            "2" -> "Reject"
            "3" -> "Dropped"
            "4" -> "Not Valid"
            "5" -> "Proposed"
            "6" -> "Sent"
            "9" -> "Canceled"
            else -> "-"
        }
    } else "-"
}

inline fun <reified T> ByteArray.tryDeserialize(): T? {
    return try {
        val clazz = T::class.java
        val iStream = ByteArrayInputStream(this)

        val method = clazz.getMethod("newBuilder")
        val builder = method.invoke(null)

        builder.javaClass.getMethod("mergeFrom", InputStream::class.java).invoke(builder, iStream)
        builder.javaClass.getMethod("build").invoke(builder) as T
    } catch (e: Exception) {
        Timber.w("ByteArray.tryDeserialize", "Failed to deserialize to ${T::class.java.simpleName}: ${e.message}")
        null
    }
}