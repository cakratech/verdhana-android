package com.bcasekuritas.mybest.ext.other

import android.content.res.Resources
import android.os.Build
import android.util.Base64
import com.bcasekuritas.mybest.ext.constant.Const
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.nio.charset.StandardCharsets
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.TimeZone
import kotlin.math.ceil
import kotlin.math.floor

fun Int.dpToPx(): Int {
    return this * Resources.getSystem().displayMetrics.density.toInt()
}

fun Float.getDistanceMerchant(): String {
    return DecimalFormat("#.##").format(this)
}

fun Int?.orZero():Int = this ?: 0

fun Int?.plus(value :Int?):Int {
    if(this == null) return 0
    if(value == null) return this
    return this + value
}

fun String.hexToByte(): ByteArray {
    return BigInteger(this, 16).toByteArray()
}

fun String.getDecodedJWT(): String {
    return try {
        val pieces = this.split("\\.".toRegex()).toTypedArray()
        val b64payload = pieces[1]
        String(
            Base64.decode(b64payload, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING),
            StandardCharsets.UTF_8
        )
    } catch (e: Exception) {
        ""
    }
}

fun String.decodeBase64(): String {
    val decoded: String = try {
        val data = Base64.decode(this, Base64.DEFAULT)
        String(data, StandardCharsets.UTF_8)
    } catch (e: Exception) {
        ""
    }
    return decoded
}

fun Double.formatPriceWithDecimal(): String {
    val rupiahFormatter = DecimalFormat("#,###,##0.00", DecimalFormatSymbols.getInstance(Locale.US))
    rupiahFormatter.toLocalizedPattern()
//    rupiahFormatter.negativePrefix = ""
    return if (this % 1 == 0.0) {
        this.formatPriceWithoutDecimal()
    } else {
        rupiahFormatter.format(this)

    }
}

fun Double.formatPriceWithTwoDecimal(): String {
    val rupiahFormatter = DecimalFormat("#,###,##0.00", DecimalFormatSymbols.getInstance(Locale.US))
    rupiahFormatter.toLocalizedPattern()
//    rupiahFormatter.negativePrefix = ""
    return rupiahFormatter.format(this)
}

fun Int.formatPrice(): String {
    val rupiahFormatter = DecimalFormat("#,###,###", DecimalFormatSymbols.getInstance(Locale.US))
    return rupiahFormatter.format(this)
}

fun Double.formatPrice(): String {
    val rupiahFormatter = DecimalFormat("#,###,###.##", DecimalFormatSymbols.getInstance(Locale.US))
    rupiahFormatter.toLocalizedPattern()
//    rupiahFormatter.negativePrefix = ""
    return rupiahFormatter.format(this)
}

fun Double.formatPriceWithoutMinus(): String {
    val rupiahFormatter = DecimalFormat("#,###,###.##", DecimalFormatSymbols.getInstance(Locale.US))
    rupiahFormatter.negativePrefix = ""
    return rupiahFormatter.format(this)
}

fun Double.formatPercent(): String {
    val percentFormatter = DecimalFormat("##0.00", DecimalFormatSymbols.getInstance(Locale.US))
    return percentFormatter.format(this)
}

fun Double.formatPercentThousand(): String {
    val percentFormatter = DecimalFormat("###,##0.00", DecimalFormatSymbols.getInstance(Locale.US))
    return percentFormatter.format(this)
}

fun Double.formatPercentWithoutMinus(): String {
    val percentFormatter = DecimalFormat("##0.00", DecimalFormatSymbols.getInstance(Locale.US))
    percentFormatter.negativePrefix = ""
    return percentFormatter.format(this)
}

fun Float.formatDecimal(): String {
    val rupiahFormatter = DecimalFormat("#,###,###.##", DecimalFormatSymbols.getInstance(Locale.US))
    rupiahFormatter.negativePrefix = ""
    return rupiahFormatter.format(this)
}

fun Long.getDateFromTimestamp(): Date? {
    val sdf = SimpleDateFormat(Const.LOCAL_DATE_TIME_FORMAT)
    sdf.timeZone = TimeZone.getTimeZone("GMT+7")
    return sdf.format(this / 1000).getDateFromString(Const.LOCAL_DATE_TIME_FORMAT)
}


fun String.getDateFromString(format: String?): Date? {
    return try {
        val locale = Locale("in", "ID")
        val sdf = SimpleDateFormat(format, locale)
        sdf.parse(this)
    } catch (e: ParseException) {
        e.printStackTrace()
        null
    }
}

fun String.getDateFromString(): Date? {
    return this.getDateFromString(Const.SERVER_TIME_FORMAT)
}

fun String.convertDateFromServer(format: String): String {
    return this.getDateFromString()!!.getStringFormatDate(format)
}

fun String.convertDateStringToString(format: String, newFormat: String): String {
    return this.getDateFromString(format)!!.getStringFormatDate(newFormat)
}

fun Date.getStringFormatDate(format: String): String {
    val locale = Locale("in", "ID")
    val sdf = SimpleDateFormat(format, locale)
    sdf.timeZone = TimeZone.getTimeZone("GMT+07:00")
    return sdf.format(this)
}

fun Long.getStringFormatDate(format: String?): String {
    val c = Calendar.getInstance()
    c.timeInMillis = this * 1000L
    val d = c.time
    // SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");
    val sdf = SimpleDateFormat(format)
    return sdf.format(d)
}

fun Date.getTimeFormat(): String {
    return this.getStringFormatDate("HH:mm:dd")
}

fun Date.getTimeFormatHM(): String {
    return this.getStringFormatDate("HH:mm")
}

fun Date.toCalendar(): Calendar? {
    val cal = Calendar.getInstance()
    cal.time = this
    return cal
}

fun String.toGmt7HM(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmmz")
        val inputDate = inputFormat.parse(this)
        val outputFormat: DateFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        outputFormat.timeZone = TimeZone.getTimeZone("GMT+07:00")
        outputFormat.format(inputDate)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

fun String.fromHMStoHM(): String {
    return this.substring(0, this.length - 3)
}

fun Int.getMonthName(): String{
        val monthNames = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        return if (this in 1..12) {
            monthNames[this - 1] // Convert 1-based index to 0-based array index
        } else {
            "Invalid month" // Handle invalid input
        }

}

fun Long.getTimeFormatHM(): String {
    return Date(this).getStringFormatDate( "HH:mm")
}

fun getStartDateTime(startDate: Date?, startTime: String): Long {
    val startDateTime: String = if (startDate == null) {
        Date().getStringFormatDate(Const.SERVER_TIME_FORMAT) + " " + startTime
    } else {
        startDate.getStringFormatDate(Const.SERVER_TIME_FORMAT) + " " + startTime
    }
    return Objects.requireNonNull<Date>(
        startDateTime.getDateFromString(Const.TIMESTAMP_FORMAT)
    ).time
}

fun Int.getMinutesFormatFromSecond(): String {
    val hours = this / 3600
    val remains = this - hours * 3600
    val mins = remains / 60
    val seconds = remains - mins * 60
    val sum = mins + hours * 60
    return "$sum menit $seconds detik"
}

fun Int.getHoursFormatFromSecond(): String {
    val hours = this / 3600
    val remains = this - hours * 3600
    val mins = remains / 60
    val seconds = remains - mins * 60
    return String.format(Locale.US, "%d:%02d:%02d", hours, mins, seconds)
}

fun Long.getHoursFormatFromSecond(): String {
    val hours = this / 3600
    val remains = this - hours * 3600
    val mins = remains / 60
    val seconds = remains - mins * 60
    return String.format(Locale.US, "%d:%02d:%02d", hours, mins, seconds)
}

fun Int.getHoursLongFormatFromSecond(): String {
    val hours = this / 3600
    val remains = this - hours * 3600
    val mins = remains / 60
    val seconds = remains - mins * 60
    return String.format(Locale.US, "%1\$d jam %2$02d menit %3$02d detik", hours, mins, seconds)
}

fun Long.getHoursLongFormatFromMillis(): String {
    val second = this / 1000
    val hours = second / 3600
    val remains = second - hours * 3600
    val mins = remains / 60
    val seconds = remains - mins * 60
    return String.format(Locale.US, "%1\$d jam %2$02d menit %3$02d detik", hours, mins, seconds)
}

fun String.getDateFormatFromString(): Date? {
    return try {
        val inputFormat: SimpleDateFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        } else {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        }
        inputFormat.parse(this)
    } catch (e: Exception) {
        null
    }
}

fun String.getDateFormatFromString(formatDate: String): Date? {
    return try {
        val inputFormat = SimpleDateFormat(formatDate)
        inputFormat.parse(this)
    } catch (e: Exception) {
        null
    }
}

fun Double.formatLotRoundingDown(): String {
    val rupiahFormatter = DecimalFormat("#,###,###", DecimalFormatSymbols.getInstance(Locale.US))
    rupiahFormatter.roundingMode = RoundingMode.DOWN
    return rupiahFormatter.format(this)
}

fun Double.formatWithoutDecimalRoundingUp(): String {
    val rupiahFormatter = DecimalFormat("#,###,###", DecimalFormatSymbols.getInstance(Locale.US))
    rupiahFormatter.roundingMode = RoundingMode.UP
    return rupiahFormatter.format(this)
}

fun Double.formatWithoutDecimalHalfUp(): String {
    val rupiahFormatter = DecimalFormat("#,###,###", DecimalFormatSymbols.getInstance(Locale.US))
    rupiahFormatter.roundingMode = RoundingMode.HALF_UP
    return rupiahFormatter.format(this)
}

fun Double.formatPriceWithoutDecimal(): String {
    val rupiahFormatter = DecimalFormat("#,###,###", DecimalFormatSymbols.getInstance(Locale.US))
    return rupiahFormatter.format(this)
}

fun Double.formatPriceWithoutDecimalOptional(): String {
    val rupiahFormatter = DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.US))
    rupiahFormatter.minimumFractionDigits = 0
    rupiahFormatter.maximumFractionDigits = 2
    return rupiahFormatter.format(this)
}

fun Long.formatPriceWithoutDecimal(): String {
    val rupiahFormatter = DecimalFormat("#,###,###", DecimalFormatSymbols.getInstance(Locale.US))
    return rupiahFormatter.format(this)
}

fun Double.formatPriceWithoutDecimalWithoutMinus(): String {
    val rupiahFormatter = DecimalFormat("#,###,###", DecimalFormatSymbols.getInstance(Locale.US))
    rupiahFormatter.negativePrefix = ""
    return rupiahFormatter.format(this)
}

fun String.unformatPriceToDouble(): Double {
    val lot = this.replace(" Lot", "")
    return lot.replace(",", "").toDouble()
}

fun Int.formatPriceWithoutDecimal(): String {
    val rupiahFormatter = DecimalFormat("#,###,###", DecimalFormatSymbols.getInstance(Locale.US))
    rupiahFormatter.negativePrefix = ""
    return rupiahFormatter.format(this)
}

fun Double.RoundedHalfDown(): String {
    val df = DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.US))
    df.roundingMode = RoundingMode.HALF_DOWN

    return df.format(this)
}

fun Double.roundedHalfDown(): String {
    val roundedValue = BigDecimal(this).setScale(2, RoundingMode.HALF_DOWN)
    return roundedValue.toString()
}

fun Double.roundedHalfUp(): Double {
    return if (this - floor(this) >= 0.5) {
        ceil(this)
    } else {
        floor(this)
    }
}

fun String.CapitalizeFirstLetter(): String {
    return if (isNotEmpty()) {
        this[0].toUpperCase() + substring(1)
    } else {
        this
    }
}

