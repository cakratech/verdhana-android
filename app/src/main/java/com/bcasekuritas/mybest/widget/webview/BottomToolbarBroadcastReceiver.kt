package com.bcasekuritas.mybest.widget.webview
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsIntent.EXTRA_REMOTEVIEWS_CLICKED_ID
import androidx.core.content.ContextCompat
import com.bcasekuritas.mybest.R


class BottomToolbarBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val intentBuilder = CustomTabsIntent.Builder()
        val url = intent.dataString
        val remoteViewId = intent.getIntExtra(EXTRA_REMOTEVIEWS_CLICKED_ID, -1)
        when (remoteViewId) {
            R.id.ct_toolbar_previous -> {}
            R.id.ct_toolbar_next -> {
//                intentBuilder.set
            }
            R.id.ct_toolbar_share -> {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, url)
                sendIntent.type = "text/plain"
                val shareIntent = Intent.createChooser(sendIntent, null)
                context.startActivity(shareIntent)
            }
        }
    }
}
