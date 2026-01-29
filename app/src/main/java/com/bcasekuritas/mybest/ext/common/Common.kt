package com.bcasekuritas.mybest.ext.common

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.DatePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.format.Formatter
import android.util.Base64
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.widget.DatePicker
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.data.layout.UIDialogModel
import com.bcasekuritas.mybest.app.domain.subscribers.FailureData
import com.bcasekuritas.mybest.ext.constant.Const
import com.bcasekuritas.mybest.ext.constant.ConstKeys
import com.bcasekuritas.mybest.ext.constant.NetworkCodes
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import timber.log.Timber
import java.math.BigDecimal
import java.net.Inet4Address
import java.net.NetworkInterface
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.TimeZone
import kotlin.math.abs
import kotlin.random.Random
import kotlin.system.measureTimeMillis


inline fun <T> T.applyIf(predicate: Boolean, block: T.() -> Unit): T = apply {
    if (predicate) block(this)
}

fun launchDelayedFunction(timeMillis: Long = 500, action: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({ action() }, timeMillis)
}

fun forceClose() {
    android.os.Process.killProcess(android.os.Process.myPid())
}

fun isNetworkConnected(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
    } else {
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            return true
        }
    }
    return false
}

@SuppressLint("HardwareIds")
fun getDeviceId(ctx: Context): String {
    return Settings.Secure.getString(
        ctx.applicationContext.contentResolver,
        Settings.Secure.ANDROID_ID
    )
}

@SuppressLint("HardwareIds")
fun getIpAddress(mContext: Context): String {
    try {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        for (intf in interfaces) {
            val addresses = intf.inetAddresses
            for (addr in addresses) {
                if (!addr.isLoopbackAddress && addr is Inet4Address) {
                    return addr.hostAddress
                }
            }
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return ""
}

@SuppressLint("HardwareIds")
fun getDeviceUUID(context: Context): String {
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}

@RequiresApi(Build.VERSION_CODES.O)
fun getDayAndMonth(millis: Long): Pair<Int, Int> {
    val zonedDateTime: ZonedDateTime = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
    val day = zonedDateTime.dayOfMonth
    val month = zonedDateTime.monthValue
    return Pair(day, month)
}

@RequiresApi(Build.VERSION_CODES.O)
fun millisToDateOnly(millis: Long): Long {
    val instant = Instant.ofEpochMilli(millis)
    val date = instant.atZone(ZoneId.systemDefault()).toLocalDate()
    return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

@RequiresApi(Build.VERSION_CODES.O)
fun getMonthAndYear(millis: Long): Pair<Int, Int> {
    val zonedDateTime: ZonedDateTime = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
    val month = zonedDateTime.monthValue
    val year = zonedDateTime.year
    return Pair(month, year)
}

@RequiresApi(Build.VERSION_CODES.O)
fun compareMonthAndYear(millis1: Long, millis2: Long): Boolean {
    val (month1, year1) = getMonthAndYear(millis1)
    val (month2, year2) = getMonthAndYear(millis2)
    return month1 == month2 && year1 == year2
}

@RequiresApi(Build.VERSION_CODES.O)
fun compareDayAndMonth(millis1: Long, millis2: Long): Boolean {
    val (day1, month1) = getDayAndMonth(millis1)
    val (day2, month2) = getDayAndMonth(millis2)
    return day1 == day2 && month1 == month2
}

fun getDeviceName(): String {
    val manufacturer = Build.MANUFACTURER
    val model = Build.MODEL
    return if (model.startsWith(manufacturer)) {
        model.uppercase()
    } else manufacturer.uppercase() + " " + model
}

fun showMessageErrorConnection(view: View) {
    val snackBar = Snackbar.make(view, "Connection error occurred", Snackbar.LENGTH_INDEFINITE)
    snackBar.setAction("Please try again") { snackBar.dismiss() }
    snackBar.show()
}

fun checkPermission(
    context: Fragment, vararg permissions: String,
    resLauncher: ActivityResultLauncher<Any>,
): Boolean {
    var allPermitted = false
    for (permission in permissions) {
        allPermitted = (ContextCompat.checkSelfPermission(context.requireContext(), permission)
                == PackageManager.PERMISSION_GRANTED)
        if (!allPermitted) break
    }
    if (allPermitted) return true
    resLauncher.launch(permissions)
    return false
}

@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentDate(): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    val current = LocalDateTime.now().format(formatter)

    return current
}

@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentDate(dateFormat: String): String {
    val formatter = DateTimeFormatter.ofPattern(dateFormat)

    return LocalDateTime.now().format(formatter)
}

fun getErrorDetail(failureData: FailureData): UIDialogModel {
    when (failureData.code) {
        NetworkCodes.NO_CONNECTION -> {
            return UIDialogModel(
                R.drawable.ic_error_no_internet, R.string.error_network_title,
                titleStr = "", R.string.error_network_description, descriptionStr = "",
                descriptionTwo = null, specialWarning = null, bgPositive = null,
                bgNegative = null, btnPositive = R.string.error_network_button, btnNegative = null
            )
        }
        NetworkCodes.CONNECTION_ERROR, NetworkCodes.TIMEOUT_ERROR, NetworkCodes.HTTP_CONFLICT -> {
            return UIDialogModel(
                R.drawable.ic_error_internet_interupted, R.string.error_network_interupted_title,
                titleStr = "", R.string.error_network_interupted_description, descriptionStr = "",
                descriptionTwo = null, specialWarning = null, bgPositive = null,
                bgNegative = null, btnPositive = R.string.error_network_button, btnNegative = null
            )
        }
        NetworkCodes.FORBIDDEN -> {
            return UIDialogModel(
                R.drawable.ic_error_403, R.string.error_forbidden_title,
                titleStr = "", R.string.error_forbidden_description, descriptionStr = "",
                descriptionTwo = null, specialWarning = null, bgPositive = null,
                bgNegative = null, btnPositive = R.string.error_forbidden_button, btnNegative = null
            )
        }
        NetworkCodes.GENERIC_ERROR -> {
            return UIDialogModel(
                R.drawable.ic_error_general, R.string.error_general_title,
                titleStr = "", R.string.error_general_description, descriptionStr = "",
                descriptionTwo = null, specialWarning = null, bgPositive = null,
                bgNegative = null, btnPositive = R.string.error_network_button, btnNegative = null
            )
        }
        else -> {
            return UIDialogModel(
                R.drawable.ic_error_general, title = null,
                titleStr = failureData.code.toString(), description = null,
                descriptionStr = failureData.message ?: "Pesan Error Kosong",
                descriptionTwo = null, specialWarning = null, bgPositive = null,
                bgNegative = null, btnPositive = R.string.error_network_button, btnNegative = null
            )
        }

    }
}

fun isAutomaticDate(activity: Activity, fragmentManager: FragmentManager) {
    if (Settings.Global.getInt(activity.contentResolver, Settings.Global.AUTO_TIME, 0) == 0
        && activity.javaClass.canonicalName != "SplashActivity"
    ) {
        val dialog = UIDialogModel(
            R.drawable.ic_error_automatic_time, R.string.automatic_time_title,
            titleStr = "", R.string.automatic_time_description, descriptionStr = "",
            descriptionTwo = null, specialWarning = null, bgPositive = null,
            bgNegative = null, btnPositive = R.string.automatic_time_button, btnNegative = null
        )

//        showDialogInfoBottom(
//            isCallback = false, isCancelable = false,
//            fragmentManager = fragmentManager, codeFromLayout = null,
//            reqTargetCode = null, resTargetCode = null, uiDialogModel = dialog
//        )

//        dialog.setOnButtonClicked {
//            startActivityForResult(
//                Intent(Settings.ACTION_DATE_SETTINGS),
//                BaseActivity.REQ_CODE_SETTINGS_DATE
//            )
//            dialog.dismissDialog()
//        }
    }
}

fun setTransparentNotificationBar(activity: AppCompatActivity) {
    val window = activity.window
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = Color.TRANSPARENT
    window.decorView.systemUiVisibility =
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
}

fun initOnBackPress(activity: FragmentActivity): Boolean {
    val result: Boolean
    val countStack = activity.supportFragmentManager.backStackEntryCount - 1
    result = if (countStack != 0) {
        false
    } else {
        activity.finish()
        true
    }
    return result
}

/*fun saveImage(mContext: Context, image: Bitmap, fileName: String) {
    val imageFile = ConstFile.getImage(mContext, fileName)
    try {
        val fOut: OutputStream = FileOutputStream(imageFile)
        image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
        fOut.close()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}*/


// Inject CSS method: read style.css from assets folder
// Append stylesheet to document head
fun injectCSS(context: Context, webView: WebView) {
    try {
        val inputStream = context.assets.open(Const.STYLE_CSS)
        val buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
        inputStream.close()
        val encoded = Base64.encodeToString(buffer, Base64.NO_WRAP)
        webView.loadUrl(
            "javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +  // Tell the browser to BASE64-decode the string into your script !!!
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()"
        )
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}

fun copyToClipboardFromString(activity: FragmentActivity, value: String, message: String?) {
    val clipboardManager = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("text", value)
    clipboardManager.setPrimaryClip(clipData)
    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
}

@RequiresApi(Build.VERSION_CODES.O)
fun getPrevious90DaysInMillis(): Long {
    // Get the current date and time
    val currentDateTime = LocalDateTime.now()

    // Subtract 90 days from the current date
    val previous90DaysDateTime = currentDateTime.minus(90, ChronoUnit.DAYS)

    // Convert the result to milliseconds in Jakarta's time zone
    return previous90DaysDateTime
        .atZone(ZoneId.of("Asia/Jakarta")) // Use Jakarta's time zone
        .toInstant()
        .toEpochMilli()
}

@SuppressLint("SimpleDateFormat")
fun initCalendarDialogForRange(
    context: Context,
    textView: TextView?,
    startDate: Long?,
    endDate: Long?,
    minDate: Long,
    pickedDate: Long,
    isStart: Boolean
): Long {
    var timeMillis: Long = 0
    val calendar = Calendar.getInstance()

    // Set initial calendar to start or end date if available
//    if (isStart && startDate != null) {
//        calendar.timeInMillis = startDate
//    } else if (!isStart && endDate != null) {
//        calendar.timeInMillis = endDate
//    }

    calendar.timeInMillis = pickedDate

    val datePickerDialog = DatePickerDialog(
        Objects.requireNonNull(context), R.style.CustomDatePickerDialog,
        { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, monthOfYear, dayOfMonth)

            // Validate the date selection based on start or end
            if (isStart) {
                if (endDate != null && selectedDate.timeInMillis > endDate) {
                    return@DatePickerDialog
                }
            } else {
                // For the end date, only restrict it to be earlier than or equal to the current date
                if (selectedDate.timeInMillis > System.currentTimeMillis()) {
                    return@DatePickerDialog
                }
            }

            // Format the selected date
            val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val formattedDate = dateFormatter.format(selectedDate.time)

            // Debug the formatted date
            Timber.d("Formatted date: $formattedDate")

            // Make sure textView is not null before setting the text
            textView?.let {
                it.text = formattedDate
                it.error = null
            } ?: Timber.e("TextView is null")

            timeMillis = selectedDate.timeInMillis
            Timber.d("timeMillis: $timeMillis")
        },
        calendar[Calendar.YEAR],
        calendar[Calendar.MONTH],
        calendar[Calendar.DAY_OF_MONTH]
    )

    datePickerDialog.datePicker.minDate = minDate

    // Set min/max date depending on whether it's for start or end
    if (isStart && endDate != null) {
        datePickerDialog.datePicker.maxDate = endDate
    } else if (!isStart) {
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis() // Allow past dates up to today
        // No minDate restriction for the end date, so it can be earlier than startDate
    }

    datePickerDialog.show()
    datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
        .setTextColor(ContextCompat.getColor(context, R.color.textSecondaryBlack))
    datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
        .setTextColor(ContextCompat.getColor(context, R.color.textBCABlue))

    return timeMillis
}




@SuppressLint("SimpleDateFormat")
fun initCalenderDialog(context: Context, textView: TextView, code: Int): Long {
    var timeMillis: Long = 0
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        Objects.requireNonNull<Context>(context), R.style.CustomDatePickerDialog,
        { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val newDate = Calendar.getInstance()
            newDate[year, monthOfYear] = dayOfMonth
            var dateFormatter = SimpleDateFormat()

            when(code){
                1 -> dateFormatter = SimpleDateFormat("dd MMM yyyy")
                2 -> dateFormatter = SimpleDateFormat("dd/MM/yyyy")
            }
            textView.text = dateFormatter.format(newDate.time)
            textView.error = null

            timeMillis = newDate.timeInMillis
            Timber.d("timemilis : $timeMillis")
        },
        calendar[Calendar.YEAR],
        calendar[Calendar.MONTH],
        calendar[Calendar.DAY_OF_MONTH]
    )

    datePickerDialog.show()
    datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.textSecondaryBlack))
    datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.textBCABlue))

    return timeMillis
}

fun initCalenderDialog(context: Context, textView: TextView, code: Int, minDate: Long? = null, selectedDate: String, callback: (Long) -> Unit) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance()

    try {
        // Parse the selectedDate string and set it to the calendar
        val date = dateFormat.parse(selectedDate)
        if (date != null) {
            calendar.time = date
        }
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    val datePickerDialog = DatePickerDialog(
        Objects.requireNonNull<Context>(context), R.style.CustomDatePickerDialog,
        { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val newDate = Calendar.getInstance()
            newDate[year, monthOfYear] = dayOfMonth
            var dateFormatter = SimpleDateFormat()

            when(code){
                1 -> dateFormatter = SimpleDateFormat("dd MMM yyyy")
                2 -> dateFormatter = SimpleDateFormat("dd/MM/yyyy")
            }
            textView.text = dateFormatter.format(newDate.time)
            textView.error = null

            val timeMillis = newDate.timeInMillis
            Timber.d("timemilis : $timeMillis")

            // Invoke the callback with the timeMillis value
            callback(timeMillis)
        },
        calendar[Calendar.YEAR],
        calendar[Calendar.MONTH],
        calendar[Calendar.DAY_OF_MONTH]
    )

    minDate?.let{
        datePickerDialog.datePicker.minDate = it
    }

    datePickerDialog.show()
    datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.textSecondaryBlack))
    datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.textBCABlue))
}

fun initCalenderDialogPlus1(context: Context, selectedDate: String, textView: TextView, minDate: Long? = null, callback: (Long) -> Unit) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance()

    try {
        // Parse the selectedDate string and set it to the calendar
        val date = dateFormat.parse(selectedDate)
        if (date != null) {
            calendar.time = date
        }
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    val datePickerDialog = DatePickerDialog(
        Objects.requireNonNull<Context>(context), R.style.CustomDatePickerDialog,
        { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val newDate = Calendar.getInstance()
            newDate.set(year, monthOfYear, dayOfMonth)

            textView.text = dateFormat.format(newDate.time)
            textView.error = null

            val timeMillis = newDate.timeInMillis
            Timber.d("timemilis : $timeMillis")

            // Invoke the callback with the timeMillis value
            callback(timeMillis)
        },
        calendar[Calendar.YEAR],
        calendar[Calendar.MONTH],
        calendar[Calendar.DAY_OF_MONTH]
    )

    minDate?.let {
        datePickerDialog.datePicker.minDate = it
    }

    datePickerDialog.show()
    datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.btnCalendarCancel))
    datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.blueCalendar))
}

fun convertToMillis(dateString: String, datePattern: String): Long {
    val dateFormat = SimpleDateFormat(datePattern, Locale.getDefault())
    val date = dateFormat.parse(dateString)
    return date?.time ?: 0L
}


fun initOpenTimePicker(fragmentManager: FragmentManager, textView: TextView, title: String, callback: (Long) -> Unit) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minutes = calendar.get(Calendar.MINUTE)

    val picker =
        MaterialTimePicker.Builder()
            .setTheme(R.style.CustomTimePickerDialog)
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(hour)
            .setMinute(minutes)
            .setTitleText(title)
            .setInputMode(INPUT_MODE_KEYBOARD)
            .build()

    picker.show(fragmentManager, "")

    picker.addOnPositiveButtonClickListener {
        val minuteString = if (picker.minute < 10) "0${picker.minute}" else "${picker.minute}"
        val hourString = if (picker.hour < 10) "0${picker.hour}" else "${picker.hour}"
        textView.text = "${hourString}:${minuteString}"

        calendar.set(Calendar.HOUR_OF_DAY, picker.hour)
        calendar.set(Calendar.MINUTE, picker.minute)
        val timeMillis = calendar.timeInMillis
        callback(timeMillis)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getDayOfMonthFromMillis(millis: Long): String {
    val instant = Instant.ofEpochMilli(millis)
    val zoneId = ZoneId.systemDefault()
    val localDate = instant.atZone(zoneId).toLocalDate()
    return "${localDate.dayOfMonth}"
}

@RequiresApi(Build.VERSION_CODES.O)
fun getMonthFromMillis(millis: Long): Int {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = millis
    }
    return calendar.get(Calendar.MONTH) + 1
}

fun getDateMillis(day: Int, month: Int, year: Int): Long {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.MONTH, month - 1) // Calendar.MONTH is zero-based
        set(Calendar.YEAR, year)
    }
    return calendar.timeInMillis
}


fun getCurrentHourAndMinutes(callback: (String, String, Long) -> Unit) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minutes = calendar.get(Calendar.MINUTE)

    val minuteString = if (minutes < 10) "0${minutes}" else "${minutes}"
    val hourString = if (hour < 10) "0${hour}" else "${hour}"

    callback(hourString, minuteString, calendar.timeInMillis)
}

fun getEndOfTheDayDateMillis(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    calendar.set(Calendar.DAY_OF_YEAR, 1)
    return calendar.timeInMillis
}

fun getStartOfTheDayDateMillis(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 100)
    return calendar.timeInMillis
}

fun initFormatThousandSeparator(value: Double): String{
    val formatter = DecimalFormat("#,###,###.##", DecimalFormatSymbols.getInstance(Locale.US))
    formatter.negativePrefix = ""

    return formatter.format(value)
}

fun initFormatThousandSeparator(value: BigDecimal): String {
    val formatter = DecimalFormat("#,###,###.##", DecimalFormatSymbols.getInstance(Locale.US))
    formatter.negativePrefix = "" // This removes the negative sign, use with caution

    return formatter.format(value)
}

fun initPercentFormatNumber(number: Double): String{
    val percentFormatter = DecimalFormat("#,##0.00'%'", DecimalFormatSymbols.getInstance(Locale.US))

    return percentFormatter.format(number)
}

fun getRandomString(): String {
    val saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890"
    val salt = StringBuilder()
    val rnd = Random

    repeat(14) {
        val index = rnd.nextInt(saltChars.length)
        salt.append(saltChars[index])
    }

    // Uncomment the line below if you have a Constant.SYSTEM_DATE() function
    // val dateTime = Constant.SYSTEM_DATE()

    return "${salt.toString()}-2" // -2 for mobile, -1 for desktop
}

fun validateSessionPin(curSessionPin: Long): Boolean {
    val currentTime = System.currentTimeMillis() / 1000
    return currentTime < curSessionPin
}

fun checkSessionPin(curSessionPin: Long): Boolean{
    val currentTime = System.currentTimeMillis() / 1000
    return (curSessionPin - currentTime) <= 60
}

fun setListViewHeightBasedOnItems(listView: ListView): Boolean {
    val listAdapter: ListAdapter? = listView.adapter
    return if (listAdapter != null) {
        val numberOfItems: Int = listAdapter.getCount()

        // Get total height of all items.
        var totalItemsHeight = 0
        for (itemPos in 0 until numberOfItems) {
            val item: View = listAdapter.getView(itemPos, null, listView)
            item.measure(0, 0)
            totalItemsHeight += item.measuredHeight
        }

        // Get total height of all item dividers.
//        val totalDividersHeight = listView.dividerHeight * (numberOfItems - 1)

        // Set list height.
        val params = listView.layoutParams
        params.height = totalItemsHeight + 100
        listView.layoutParams = params
        listView.requestLayout()
        true
    } else {
        false
    }
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun formatLastNumber(value: Float): String{
    val numberFormatter = NumberFormat.getInstance()
    numberFormatter.minimumFractionDigits = 0
    numberFormatter.maximumFractionDigits = 1

    return when (value) {
        in 0.0..1_000.0 -> numberFormatter.format(value.toDouble())
        in 1_000.0..1_000_000.0 -> "${numberFormatter.format(value / 1_000)}K"
        in 1_000_000.0..1_000_000_000.0 -> "${numberFormatter.format(value / 1_000_000)}M"
        in 1_000_000_000.0..1_000_000_000_000.0 -> "${numberFormatter.format(value / 1_000_000_000)}B"
        else -> "${numberFormatter.format(value / 1_000_000_000_000)}T"
    }
}

fun formatLastNumberStartFromMillion(value: Float): String{
    val numberFormatter = NumberFormat.getInstance()
    numberFormatter.minimumFractionDigits = 0
    numberFormatter.maximumFractionDigits = 2

    return when (value) {
        in 0.0..1_000_000.0 -> numberFormatter.format(value.toDouble())
        in 1_000_000.0..1_000_000_000.0 -> "${numberFormatter.format(value / 1_000_000)}M"
        in 1_000_000_000.0..1_000_000_000_000.0 -> "${numberFormatter.format(value / 1_000_000_000)}B"
        else -> "${numberFormatter.format(value / 1_000_000_000_000)}T"
    }
}


fun formatLastNumberStartFromMillionWithNegatif(value: Float): String{
    val numberFormatter = NumberFormat.getInstance()
    numberFormatter.minimumFractionDigits = 0
    numberFormatter.maximumFractionDigits = 1

    return when (value) {
        in 0.0..1_000_000.0 -> numberFormatter.format(value.toDouble())
        in 1_000_000.0..1_000_000_000.0 -> "${numberFormatter.format(value / 1_000_000)}M"
        in 1_000_000_000.0..1_000_000_000_000.0 -> "${numberFormatter.format(value / 1_000_000_000)}B"
        else -> "${numberFormatter.format(value / 1_000_000_000_000)}T"
    }
}

fun formatLastNumberWithNegatifValue(number: Double): String {
    val suffix = charArrayOf(' ', 'K', 'M', 'B', 'T')
    var numValue = abs(number)
    var value = abs(number)
    var index = 0

    while (value >= 1000) {
        value /= 1000
        numValue /= 1000
        index++
    }

    val formattedNumber = if (index > 0) {
        val indexSuffix = if (index > 4) 4 else index
        String.format("%.2f%s", numValue, suffix[indexSuffix])
    } else {
        String.format("%.0f", number)
    }

    // Menambahkan tanda kurung jika angka negatif
    return if (number < 0) {
        "($formattedNumber)"
    } else {
        formattedNumber
    }
}

fun CONVERT_NUMBER_MBT(number: Double): String {
    val formatter = NumberFormat.getInstance()
    formatter.minimumFractionDigits = 2
    formatter.maximumFractionDigits = 2
    formatter.isGroupingUsed = true

    val absoluteValue = abs(number)

    return when {
        absoluteValue >= 1_000_000_000_000.00 -> {
            val formattedString = formatter.format(absoluteValue / 1_000_000_000_000.00)
            "${if (number < 0) "-" else ""}$formattedString T"
        }
        absoluteValue >= 1_000_000_000.00 -> {
            val formattedString = formatter.format(absoluteValue / 1_000_000_000.00)
            "${if (number < 0) "-" else ""}$formattedString B"
        }
        absoluteValue >= 1_000_000.00 -> {
            val formattedString = formatter.format(absoluteValue / 1_000_000.00)
            "${if (number < 0) "-" else ""}$formattedString M"
        }
        else -> {
            val formattedString = formatter.format(absoluteValue)
            "${if (number < 0) "-" else ""}$formattedString"
        }
    }
}

fun formatLastNumberStartFromBillion(value: Float): String{
    val numberFormatter = NumberFormat.getInstance()
    val rupiahFormatter = DecimalFormat("#,###,###", DecimalFormatSymbols.getInstance(Locale.US))
    rupiahFormatter.minimumFractionDigits = 0
    rupiahFormatter.maximumFractionDigits = 1

    return when (value) {
        in 0.0..1_000_000_000.0 -> rupiahFormatter.format(value.toDouble())
        in 1_000_000_000.0..1_000_000_000_000.0 -> "${rupiahFormatter.format(value / 1_000_000_000)}B"
        else -> "${rupiahFormatter.format(value / 1_000_000_000_000)}T"
    }
}

fun getCurrentYearMonthDay(value: String): String{ // Input Year or Month or Day
    val stringValue: String = value

    val calendar = Calendar.getInstance()
    var returnValue = ""

    when(stringValue){
        "year" -> returnValue = calendar.get(Calendar.YEAR).toString()
        "month" -> returnValue = (calendar.get(Calendar.MONTH) + 1).toString()
        "day" -> returnValue =  calendar.get(Calendar.DAY_OF_MONTH).toString()
    }

    return returnValue
}

fun getCategoryName(codeCat: String): String{
    return when(codeCat){
        "ACC" -> "Akun Nasabah"
        "RDN" -> "Rekening Dana Nasabah"
        "BTR" -> "Biaya Transaksi"
        "TYP" -> "Tipe Order"
        "LIM" -> "Limit Order"
        "AOT" -> "Automatic Order"
        "MAR" -> "Market Order"
        "FAS" -> "Fast Order"
        "SLT" -> "Stop Loss/Take Profit"
        "SLC" -> "Split/Repeat Order"
        "GTC" -> "Good Till Cancelled"
        else -> ""
    }
}


fun boardType(board: String): String = when (board) {
    "Regular" -> "RG"
    "Cash" -> "TN"
    "Nego" -> "NG"
    else -> ""
}

fun orderType(type: String): String = when (type) {
    "0" ->  "Limit Order"
    "1", "5" ->  "Market Order"
    "2" -> "Best Bid"
    "3" -> "Best Offer"
    "4" -> "Best Opposite"
    else -> ""
}

fun oprConvert(value: Int): String = when (value) {
    0 -> "="
    1 -> ">"
    2 -> ">="
    3 -> "<"
    4 -> "<="
    else -> ""
}

fun timeInForce(timeInForce: String): String= when (timeInForce) {
    "0", "3", "4" -> "Today"
    "1" -> "GTD"
    "2" -> "GTC"
    "S" -> "Session"
    else -> ""
}

fun isValidPassword(password: String): Boolean {
    val capitalLetterPattern = Regex("[A-Z]")
    val numberPattern = Regex("[0-9]")
    val alphabet = Regex("[a-zA-Z]+")

    val hasCapitalLetter = capitalLetterPattern.containsMatchIn(password)
    val hasNumber = numberPattern.containsMatchIn(password)
    val hasAlphanumeric = alphabet.containsMatchIn(password)

    return hasAlphanumeric && hasNumber && password.length >= 8
}


fun capitalizeWords(text: String): String {
    return text.split(" ").map { it.capitalize(Locale.ROOT) }.toTypedArray().joinToString(" ")
}

fun getUrlTradingView(
    feature: Int, theme: String, board: String,
    stockCode: String, interval: String, userId: String,
): String {
    return "${ConstKeys.TRADING_VIEW_URL}$feature/$theme/$board/$stockCode/$interval/$userId"
}

fun compareTimeToCurrentTime(time: Long): Boolean {
    val currentTime = Calendar.getInstance().timeInMillis
    return time > currentTime
}

fun compareDateToToday(date: Long): Boolean {
    val today = getStartOfTheDayDateMillis()
    return date >= today
}

fun convertMillisToDate(millis: Long, datePattern: String): String {
    return if (millis != 0L) {
        val dateFormat = SimpleDateFormat(datePattern, Locale.getDefault())
        val date = Date(millis)
        dateFormat.format(date)
    } else "-"

}

fun validDateInMonth(date: Long): Boolean {
    val monthLater = Calendar.getInstance()
    monthLater.add(Calendar.DAY_OF_YEAR, 30)
    val validDate = monthLater.timeInMillis
    return date <= validDate
}

fun isValidTimeForTomorrow(timeInMillis: Long): Boolean {
    val currentCalendar = Calendar.getInstance()
    currentCalendar.set(Calendar.HOUR_OF_DAY, 0) // Set to midnight
    currentCalendar.set(Calendar.MINUTE, 0)
    currentCalendar.set(Calendar.SECOND, 0)
    currentCalendar.set(Calendar.MILLISECOND, 0)

    val tomorrow = currentCalendar.clone() as Calendar
    tomorrow.add(Calendar.DAY_OF_YEAR, 1) // Add 1 day

    return timeInMillis >= tomorrow.timeInMillis
}

fun isToday(millisSecond: Long): Boolean {
    val startDay = Calendar.getInstance()
    startDay.set(Calendar.HOUR_OF_DAY, 0) // Set to midnight
    startDay.set(Calendar.MINUTE, 0)
    startDay.set(Calendar.SECOND, 0)
    startDay.set(Calendar.MILLISECOND, 0)

    val endDay = Calendar.getInstance()
    endDay.set(Calendar.HOUR_OF_DAY, 23) // Set to midnight
    endDay.set(Calendar.MINUTE, 59)
    endDay.set(Calendar.SECOND, 59)
    endDay.set(Calendar.MILLISECOND, 999)



    return millisSecond in startDay.timeInMillis..endDay.timeInMillis
}

fun getTomorrowTimeInMillis(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)

    val tomorrow = calendar.clone() as Calendar
    tomorrow.add(Calendar.DAY_OF_YEAR, 1) // Add 1 day

    return tomorrow.timeInMillis
}

fun generateTextImageProfile(text: String): String {
    val array = text.trim().split(" ")

    return if (array.size > 1) array.first()[0].toString() + array.last()[0].toString() else array.first()[0].toString()
}

fun checkFractionPrice(price: Int, isEth: Boolean): Boolean {
    val modVal = when {
        isEth -> 1
        price < 200 -> 1
        price < 500 -> 2
        price < 2000 -> 5
        price < 5000 -> 10
        else -> 25
    }

    return if (modVal == 1) {
        true
    } else {
        price % modVal == 0
    }
}

fun getFractionNumber(price: Int): Int {
    return when {
        price < 200 -> 1
        price < 500 -> 2
        price < 2000 -> 5
        price < 5000 -> 10
        else -> 25
    }
}

fun getFractionPrice(price: Int): Int {
    val modVal = when {
        price <= 200 -> 1
        price <= 500 -> 2
        price <= 2000 -> 5
        price <= 5000 -> 10
        else -> 25
    }
    val roundedPrice = (price + modVal - 1) / modVal * modVal

    return roundedPrice
}

fun adjustFractionPrice(price: Int, mode: String, isEtf: Boolean): String {
    val modVal = when {
        isEtf -> 1
        price <= 200 -> 1
        price <= 500 -> 2
        price <= 2000 -> 5
        price <= 5000 -> 10
        else -> 25
    }

    val previousModVal = if (isEtf) 1 else when (price) {
        200 -> 1
        500 -> 2
        2000 -> 5
        5000 -> 10
        else -> modVal
    }
    val roundedPrice = (price + modVal - 1) / modVal * modVal

    val total = when (mode) {
        "-" -> if (roundedPrice > 0) roundedPrice - previousModVal else roundedPrice - modVal
        "+" -> if (checkFractionPrice(price, isEtf)) roundedPrice + modVal else roundedPrice
        else -> roundedPrice // Default to no change
    }

    return total.formatPriceWithoutDecimal()
}

fun adjustFractionPrice(price: Int, fractionNumber: Int, mode: String): String {

    val roundedPrice = (price + fractionNumber - 1) / fractionNumber * fractionNumber

    val total = when (mode) {
        "-" -> roundedPrice - fractionNumber
        "+" -> if (price == roundedPrice) roundedPrice + fractionNumber else roundedPrice
        else -> roundedPrice // Default to no change
    }

    return total.formatPriceWithoutDecimal()
}

fun convertMillisToTimeAgo(millis: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - millis

    return when {
        diff < 60 * 1000 -> "Just now"
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} minutes ago"
        diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)} hours ago"
        else -> "${diff / (24 * 60 * 60 * 1000)} days ago"
    }
}

fun getYesterdayTimeInMillis(): Long {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -1)
    return calendar.timeInMillis
}

fun getCurrentTimeInMillis(): Long {
    return Calendar.getInstance().timeInMillis
}

fun getEndTimeExercise(endDate: Long): Long {
    return if (endDate != 0L) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = endDate

        calendar.add(Calendar.DAY_OF_YEAR, -1)
        calendar.set(Calendar.HOUR_OF_DAY, 12)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        calendar.timeInMillis
    } else {
        0L
    }
}

fun getStartTimeExercise(startTime: Long, endTime: Long): Boolean {
    return if (startTime != 0L && endTime != 0L) {
        val tz = TimeZone.getTimeZone("Asia/Jakarta")
        val calendar = Calendar.getInstance(tz)
        val currentTime = calendar.timeInMillis

        currentTime in startTime..<endTime
    } else {
        true
    }

}

fun getOpenDate(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 9)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    return calendar.timeInMillis
}

fun base64ToByteArray(base64String: String): ByteArray {
    return Base64.decode(base64String, Base64.DEFAULT)
}

fun measureTime(block: () -> Unit){
    val millSecond = measureTimeMillis(block)
    Timber.i("It takes $millSecond ms to run the block of code")
}

fun setDateTo7PM(timeInMillis: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeInMillis

    calendar.set(Calendar.HOUR_OF_DAY, 19) // Set to 7 PM
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    return calendar.timeInMillis
}

fun setDateToMidnight(timeInMillis: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeInMillis

    calendar.set(Calendar.HOUR_OF_DAY, 0) // Set to midnight
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)


    return calendar.timeInMillis
}

fun ellipsizePercentage(input: String, maxLength: Int, minLength: Int): String {
    if (!input.endsWith("%")) return input

    // Extract numeric part (excluding %, +, -)
    val numericPart = when {
        input.startsWith("+") || input.startsWith("-") -> input.drop(1).dropLast(1)
        else -> input.dropLast(1)
    }

    // Rule 4: If numeric part is shorter than minLength, return as-is
    if (numericPart.length < minLength) {
        return input
    }

    // Rule 3: If numeric part >= minLength but < maxLength, remove decimal
    if (numericPart.length < maxLength) {
        val dotIndex = numericPart.indexOf('.')
        if (dotIndex != -1) {
            val wholeNumberPart = numericPart.substring(0, dotIndex)
            return "${input.take(1).takeIf { it == "+" || it == "-" } ?: ""}$wholeNumberPart%"
        }
        return input
    }

    // Rule 2: If numeric part >= maxLength, add ...% and truncate
    val ellipsis = "..."
    val allowedPrefixLength = maxLength - ellipsis.length - 1 // 1 for %

    val prefix = if (input.startsWith("+") || input.startsWith("-")) {
        val sign = input.take(1)
        val remainingLength = allowedPrefixLength - 1
        val numberPrefix = numericPart.take(remainingLength)
        "$sign$numberPrefix"
    } else {
        numericPart.take(allowedPrefixLength)
    }

    return "$prefix$ellipsis%"
}

fun Context.isAppInForeground(): Boolean {
    val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val running = am.runningAppProcesses ?: return false

    return running.any {
        it.processName == packageName &&
                it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
    }
}
