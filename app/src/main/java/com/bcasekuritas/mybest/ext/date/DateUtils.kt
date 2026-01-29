package com.bcasekuritas.mybest.ext.date

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

object DateUtils {
    @SuppressLint("SimpleDateFormat")
    @JvmStatic
    fun toStringDate(date: Long, pattern: String) : String {
        val format = SimpleDateFormat(pattern)
        return format.format(date)
    }

    @JvmStatic
    fun toLongDate(date: String) : Long {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return format.parse(date)?.time ?: 0L
    }

    fun convertLongToDateTime(time: Long): String {
        val date   = Date(time)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
        return format.format(date)
    }

    fun currentTimeToLong(): Long {
        return System.currentTimeMillis()
    }

    fun convertDateToLong(date: String): Long {
        val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return df.parse(date)?.time ?: 0L
    }

    fun convertLongToDate(time: Long): String {
        return try {
            val date   = Date(time)
            val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            format.format(date)
        }catch (e: Exception){
            "-"
        }
    }

    fun convertLongToTime(time: Long): String {
        val date   = Date(time)
        val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return format.format(date)
    }

    fun convertLongToDate(time: Long, pattern: String): String {
        val date   = Date(time)
        val format = SimpleDateFormat(pattern, Locale.getDefault())
        return format.format(date)
    }
    
    fun convertLongToStringDate(time: Long): String {
        val date   = Date(time)
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return format.format(date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertStringToYear(date: String): String {
        val inputFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMM")
        val yearMonth: YearMonth = YearMonth.parse(date, inputFormat)

        val outputFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy")

        return yearMonth.format(outputFormat)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertStringToMonth(date: String, code: Int): String {
        val inputFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMM")
        val yearMonth: YearMonth = YearMonth.parse(date, inputFormat)
        var outputFormat: DateTimeFormatter? = null

        when(code){
            1 -> outputFormat = DateTimeFormatter.ofPattern("MM")
            2 -> outputFormat = DateTimeFormatter.ofPattern("MMM")
            3 -> outputFormat = DateTimeFormatter.ofPattern("MMMM")
        }

        return yearMonth.format(outputFormat)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertStringToDateWithLastDate(strDate: String): String {
        val inputFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMM")
        val yearMonth: YearMonth = YearMonth.parse(strDate, inputFormat)

        val outputFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
        val month = convertStringToMonth(strDate, 1).toInt()
        val year = convertStringToYear(strDate).toInt()
        val monthYear = yearMonth.format(outputFormat)
        var date: Int

        when (month) {
            1, 3, 5, 7, 8, 10, 12 -> date = 31
            4, 6, 9, 11 -> date = 30
            2 -> {
                // Cek apakah tahun kabisat atau tidak
                if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                    date =  29 // Tahun kabisat
                } else {
                    date = 28 // Bukan tahun kabisat
                }
            }
            else -> throw IllegalArgumentException("Month is not valid")
        }

        return "$date $monthYear"
    }

    fun getQuarter(period: String): String {
        var month: Int
        var year: String

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            month = convertStringToMonth(period, 1).toInt()
            year = convertStringToYear(period)
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        // Menentukan kuartal berdasarkan bulan
        return when (month) {
            in 1..3 -> "Q1 $year"
            in 4..6 -> "Q2 $year"
            in 7..9 -> "Q3 $year"
            in 10..12 -> "Q4 $year"
            else -> throw IllegalArgumentException("Bulan tidak valid")
        }
    }

    fun getYear(period: String): String {
        var month: Int
        var year: String

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            month = convertStringToMonth(period, 1).toInt()
            year = convertStringToYear(period)
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        // Menentukan kuartal berdasarkan bulan
        return year
    }

    fun formatDate(inputDate: String, formatInput: String, formatOutput: String): String {
        if (inputDate.isEmpty()) {
            return ""
        }

        val inputFormat = if (formatInput.isEmpty()) SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH) else SimpleDateFormat(formatInput, Locale.ENGLISH)
        val outputFormat =  if (formatOutput.isEmpty()) SimpleDateFormat("dd MMM", Locale.ENGLISH) else SimpleDateFormat(formatOutput, Locale.ENGLISH)
        return try {
            val date = inputFormat.parse(inputDate)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            ""
        }
    }

    fun isWaitForOffer(endBookDate: String, startOfferDate: String): Boolean {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentTime = System.currentTimeMillis()

        return try {
            val endBook = format.parse(endBookDate)!!.time
            val startOffer = format.parse(startOfferDate)!!.time

            if (currentTime > endBook && currentTime < startOffer){
                true
            } else {
                false
            }

        } catch (e: Exception) {
            false
        }
    }

    fun getTimeInMillisFromDateStringFormat(inputDate: String, inputFormat: String): Long {
        if (inputDate.isEmpty()) {
            return 0
        }

        val date = if (inputFormat.isEmpty()) SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH) else SimpleDateFormat(inputFormat, Locale.ENGLISH)
        val timeInMillis = date.parse(inputDate)
        val output = Calendar.getInstance().apply {
            if (timeInMillis != null) {
                time = timeInMillis
                set(Calendar.HOUR_OF_DAY, 0) // Set hour to 9 AM
                set(Calendar.MINUTE, 0)     // Set minutes to 0
                set(Calendar.SECOND, 0)     // Set seconds to 0
                set(Calendar.MILLISECOND, 1) // Set milliseconds to 0
            }
        }

        return output.timeInMillis

    }
}