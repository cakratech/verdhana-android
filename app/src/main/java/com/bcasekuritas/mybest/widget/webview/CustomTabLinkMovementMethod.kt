package com.bcasekuritas.mybest.widget.webview

import android.content.Context
import android.net.Uri
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.MotionEvent
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent

class CustomTabLinkMovementMethod(private val context: Context) : LinkMovementMethod() {

    override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val x = event.x.toInt()
            val y = event.y.toInt()

            val layout = widget.layout
            val line = layout.getLineForVertical(y)
            val offset = layout.getOffsetForHorizontal(line, x.toFloat())

            val link = buffer.getSpans(offset, offset, URLSpan::class.java).firstOrNull()
            link?.let {
                openCustomTab(context, it.url)
                return true // Prevent default behavior
            }
        }
        return super.onTouchEvent(widget, buffer, event)
    }

    private fun openCustomTab(context: Context, url: String) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }
}