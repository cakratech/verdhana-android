package com.bcasekuritas.mybest.ext.context

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AnimRes
import androidx.core.content.ContextCompat
import com.bcasekuritas.mybest.ext.property.orFalse
import java.io.File


fun Context?.loadAnimation(@AnimRes resId: Int): Animation = AnimationUtils.loadAnimation(this, resId)

inline val Context.inputManager: InputMethodManager?
    get() {
        return ContextCompat.getSystemService(this, InputMethodManager::class.java)
    }

@Suppress("DEPRECATION")
inline val Context.isNetworkAvailable: Boolean
    get() {
        val manager = ContextCompat.getSystemService(this, ConnectivityManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities = manager?.getNetworkCapabilities(manager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
                }
            }
        } else {
            return manager?.activeNetworkInfo?.isConnected.orFalse()
        }
        return false
    }

fun Context.copyToClipboard(data: String?) {
    ContextCompat.getSystemService(this, ClipboardManager::class.java)?.apply {
        if (data.isNullOrEmpty()) return
        setPrimaryClip(ClipData.newPlainText("Share", data.orEmpty()))
        //showToast("Copied!")
    }
}

fun Context.getFileName(uri: Uri): String {
    var filename = "image"

    if (uri.toString().startsWith("content://")) {
        var cursor: Cursor? = null

        try {
            cursor = contentResolver.query(uri, null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                filename = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        } finally {
            cursor?.close()
        }
    } else if (uri.toString().startsWith("file://")) {
        filename = File(uri.toString()).name
    }

    return filename.apply {
        replace(Regex("[^a-zA-Z]+"), "")
    }
}