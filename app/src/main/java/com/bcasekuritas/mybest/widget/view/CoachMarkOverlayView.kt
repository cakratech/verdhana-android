package com.bcasekuritas.mybest.widget.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.toColorInt

class CoachMarkOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : View(context, attrs) {

    private val dimPaint = Paint().apply {
        color = "#B3000000".toColorInt()
        isAntiAlias = true
    }

    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        isAntiAlias = true
    }

    private val focusRects = mutableListOf<RectF>()
    private val overlayLocation = IntArray(2)
    private val path = Path()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Offscreen buffer
        val saved = canvas.saveLayer(null, null)

        // Draw dimmed overlay
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), dimPaint)

        // Cut out each hole
        path.reset()
        focusRects.forEach { rect ->
            path.addRoundRect(rect, 16f, 16f, Path.Direction.CW)
        }

        canvas.drawPath(path, clearPaint)

        canvas.restoreToCount(saved)
    }

    fun setFocusAreas(vararg targets: View) {
        focusRects.clear()

        // Get overlay location
        getLocationInWindow(overlayLocation)

        targets.forEach { target ->
            val targetLocation = IntArray(2)
            target.getLocationInWindow(targetLocation)

            val left = targetLocation[0] - overlayLocation[0]
            val top = targetLocation[1] - overlayLocation[1]

            val rect = RectF(
                left.toFloat(),
                top.toFloat(),
                left + target.width.toFloat(),
                top + target.height.toFloat()
            )

            focusRects.add(rect)
        }

        visibility = VISIBLE
        invalidate()
    }

    fun clearFocusAreas() {
        focusRects.clear()
        visibility = GONE
        invalidate()
    }
}
