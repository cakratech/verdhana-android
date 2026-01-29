package com.bcasekuritas.mybest.ext.activity

import android.app.Activity
import android.content.res.Configuration
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.bcasekuritas.mybest.ext.property.orZero

fun Activity.getStringExtra(key: String): String {
    return intent?.getStringExtra(key).orEmpty()
}

fun Activity.getIntegerExtra(key: String): Int {
    return intent?.getIntExtra(key, 0).orZero()
}

@Suppress("DEPRECATION")
fun Activity.adjustFontScale(configuration: Configuration) {
    configuration.fontScale = 1.0f

    val metrics = resources.displayMetrics
    val manager = ContextCompat.getSystemService(this, WindowManager::class.java)

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        val display = display
        display?.getRealMetrics(metrics)
    } else {
        val display = windowManager.defaultDisplay
        display.getMetrics(metrics)
    }

    manager?.defaultDisplay?.getMetrics(metrics)
    metrics.scaledDensity = configuration.fontScale * metrics.density

    baseContext.resources.displayMetrics.setTo(metrics)
}