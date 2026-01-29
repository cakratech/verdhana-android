package com.bcasekuritas.mybest.ext.delegate

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.widget.webview.BottomToolbarBroadcastReceiver

private interface ShowWebview{

    fun showWebview(context: Context, url: String)
}

class ShowWebviewImpl: ShowWebview {
    override fun showWebview(context: Context, url: String) {
        val actionIntent = Intent(
            context, BottomToolbarBroadcastReceiver::class.java
        )

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0 /* request code */,
            actionIntent,
            PendingIntent.FLAG_MUTABLE
        )

        val secondaryToolbarViews = RemoteViews(context.packageName, R.layout.custom_webview_bottom)
        val clickableIds = intArrayOf(R.id.ct_toolbar_next, R.id.ct_toolbar_previous, R.id.ct_toolbar_share)

        //TODO RK: Change Color To Primary light and dark
        @ColorInt
        val colorPrimaryLight =
            ContextCompat.getColor(context, R.color.blue_pale)
        @ColorInt
        val colorPrimaryDark =
            ContextCompat.getColor(context, R.color.dark_blue)

        val intent = CustomTabsIntent.Builder() // set the default color scheme
            .setDefaultColorSchemeParams(
                CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(colorPrimaryLight)
                    .build()
            ) // set the alternative dark color scheme
            .setColorSchemeParams(
                CustomTabsIntent.COLOR_SCHEME_DARK, CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(colorPrimaryDark)
                    .build()
            )
            .setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left)
            .setExitAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .setSecondaryToolbarViews(secondaryToolbarViews, clickableIds, pendingIntent)
            .setUrlBarHidingEnabled(true) // auto-hide bar
            .setShowTitle(true)
            .build()

        intent.launchUrl(context, Uri.parse(url))
    }
}