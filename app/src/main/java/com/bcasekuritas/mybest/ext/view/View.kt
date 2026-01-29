package com.bcasekuritas.mybest.ext.view

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AnimRes
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.ext.context.inputManager
import com.bcasekuritas.mybest.ext.context.loadAnimation
import com.bcasekuritas.mybest.widget.view.SingleClickListener

fun View.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()

fun View.slideDown() {
    animate().setDuration(600).translationY(dip(height) * 1f).start()
}

fun View.slideUp() {
    animate().setDuration(600).translationY(16f).start()
}

fun View.showKeyboard() {
    clearFocus()
    requestFocus()
    context.inputManager?.showSoftInput(this, InputMethodManager.SHOW_FORCED)
}

fun View.closeKeyboard() {
    clearFocus()
    context.inputManager?.hideSoftInputFromWindow(windowToken, InputMethodManager.RESULT_UNCHANGED_SHOWN)
}

fun View?.backgroundColor(hexCode: String) {
    this?.run { setBackgroundColor(Color.parseColor(hexCode)) }
}

fun View?.backgroundColor(resId: Int) {
    this?.run { setBackgroundColor(ContextCompat.getColor(context, resId)) }
}

//fun View?.onItemClicked() {
//    this?.let {
//        setOnClickListener { _ ->
//            isEnabled = false
//            postDelayed({ isEnabled = true }, 1000)
//        }
//    }
//}

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SingleClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

fun View.visible(animate: Boolean = true) {
    if (!animate) {
        visibility = View.VISIBLE
        alpha = 1.0f
        return
    }

    alpha = 0f
    visibility = View.VISIBLE
    animate().alpha(1.0f).duration = 100
}

fun View.invisible(delayMillis: Long = 100) {
    animate().alpha(0f).duration = delayMillis

    Handler(Looper.getMainLooper()).postDelayed({
        visibility = View.INVISIBLE
    }, delayMillis)
}

fun View.gone(delayMillis: Long = 100) {
    animate().alpha(0f).duration = delayMillis

    Handler(Looper.getMainLooper()).postDelayed({
        visibility = View.GONE
    }, delayMillis)
}

fun View?.toggleView(isTrue: Boolean, isGone: Boolean = true) {
    this?.let {
        if (isTrue) {
            visible()
        } else {
            if (isGone) {
                gone()
            } else {
                invisible()
            }
        }
    }
}

fun View.revealWithAnimation(@AnimRes resId: Int = R.anim.fade_in) {
    if (!isVisible) {
        isVisible = true
        startAnimation(context.loadAnimation(resId))
    }
}

fun View.hideWithAnimation(@AnimRes resId: Int = R.anim.fade_out, invisible: Boolean = false) {
    if (isVisible) {
        startAnimation(context.loadAnimation(resId))

        if (invisible) {
            isInvisible = true
        } else {
            isVisible = false
        }
    }
}

fun ViewPager2.wrapContentHeight() {
    this.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            requestLayout()
        }
    })

    this.adapter?.let {
        val currentChild: View? = this.getChildAt(0)
        val height = currentChild?.height ?: 0
        this.layoutParams = this.layoutParams.apply {
            this.height = height
        }
    }
}