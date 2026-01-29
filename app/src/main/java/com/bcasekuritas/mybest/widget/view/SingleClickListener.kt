package com.bcasekuritas.mybest.widget.view

import android.os.SystemClock
import android.view.View
import com.bcasekuritas.mybest.ext.constant.Const

class SingleClickListener(
    private var defaultInterval: Long = Const.COUNTDOWN_INTERVAL,
    private val onSafeCLick: (View) -> Unit
) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick(v)
    }
}