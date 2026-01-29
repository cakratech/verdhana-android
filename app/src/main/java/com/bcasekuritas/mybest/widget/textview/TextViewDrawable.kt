package com.bcasekuritas.mybest.widget.textview

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import android.graphics.drawable.Drawable
import com.bcasekuritas.mybest.R
import android.os.Build
import android.util.AttributeSet
import androidx.appcompat.content.res.AppCompatResources

class TextViewDrawable : AppCompatTextView {
    var attrs: AttributeSet? = null
    var drawLeft: Drawable? = null

    constructor(context: Context?) : super(context!!)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttrs(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        initAttrs(context, attrs)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            this.attrs = attrs
            val attributeArray = context.obtainStyledAttributes(
                attrs, R.styleable.TextViewDrawable
            )
            var drawableLeft: Drawable? = null
            var drawableRight: Drawable? = null
            var drawableBottom: Drawable? = null
            var drawableTop: Drawable? = null
            var drawableBackground: Drawable? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawableLeft = if (drawLeft == null) {
                    attributeArray.getDrawable(R.styleable.TextViewDrawable_tv_drawableLeftCompat)
                } else {
                    drawLeft
                }
                drawableRight = attributeArray.getDrawable(R.styleable.TextViewDrawable_tv_drawableRightCompat)
                drawableBottom = attributeArray.getDrawable(R.styleable.TextViewDrawable_tv_drawableBottomCompat)
                drawableTop = attributeArray.getDrawable(R.styleable.TextViewDrawable_tv_drawableTopCompat)
                drawableBackground = attributeArray.getDrawable(R.styleable.TextViewDrawable_tv_drawableBackground)
            } else {
                val drawableLeftId = attributeArray.getResourceId(
                    R.styleable.TextViewDrawable_tv_drawableLeftCompat, -1)
                val drawableRightId = attributeArray.getResourceId(
                    R.styleable.TextViewDrawable_tv_drawableRightCompat, -1)
                val drawableBottomId = attributeArray.getResourceId(
                    R.styleable.TextViewDrawable_tv_drawableBottomCompat, -1)
                val drawableTopId = attributeArray.getResourceId(
                    R.styleable.TextViewDrawable_tv_drawableTopCompat, -1)
                val drawableBackgroundId = attributeArray.getResourceId(
                    R.styleable.TextViewDrawable_tv_drawableBackground, -1)

                if (drawableLeftId != -1) drawableLeft = AppCompatResources.getDrawable(context, drawableLeftId)
                if (drawableRightId != -1) drawableRight = AppCompatResources.getDrawable(context, drawableRightId)
                if (drawableBottomId != -1) drawableBottom = AppCompatResources.getDrawable(context, drawableBottomId)
                if (drawableTopId != -1) drawableTop = AppCompatResources.getDrawable(context, drawableTopId)
                if (drawableBackgroundId != -1) drawableBackground = AppCompatResources.getDrawable(context, drawableBackgroundId)
            }
            setCompoundDrawablesWithIntrinsicBounds(
                drawableLeft, drawableTop, drawableRight, drawableBottom
            )
            background = drawableBackground
            attributeArray.recycle()
        }
    }

    fun setBackgrounds(image: Int) {
        val draw = AppCompatResources.getDrawable(context, image)
        drawLeft = draw
        initAttrs(context, attrs)
    }
}