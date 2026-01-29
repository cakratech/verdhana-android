package com.bcasekuritas.mybest.widget.button

import android.content.Context
import androidx.appcompat.widget.AppCompatButton
import com.bcasekuritas.mybest.R
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import androidx.appcompat.content.res.AppCompatResources

class ButtonDrawable : AppCompatButton {
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
            val attributeArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.ButtonDrawable
            )
            var drawableLeft: Drawable? = null
            var drawableRight: Drawable? = null
            var drawableBottom: Drawable? = null
            var drawableTop: Drawable? = null
            var drawableBackground: Drawable? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawableLeft = attributeArray.getDrawable(R.styleable.ButtonDrawable_btn_drawableLeftCompat)
                drawableRight = attributeArray.getDrawable(R.styleable.ButtonDrawable_btn_drawableRightCompat)
                drawableBottom = attributeArray.getDrawable(R.styleable.ButtonDrawable_btn_drawableBottomCompat)
                drawableTop = attributeArray.getDrawable(R.styleable.ButtonDrawable_btn_drawableTopCompat)
                drawableBackground = attributeArray.getDrawable(R.styleable.ButtonDrawable_btn_drawableBackground)
            } else {
                val drawableLeftId = attributeArray.getResourceId(
                    R.styleable.ButtonDrawable_btn_drawableLeftCompat, -1)
                val drawableRightId = attributeArray.getResourceId(
                    R.styleable.ButtonDrawable_btn_drawableRightCompat, -1)
                val drawableBottomId = attributeArray.getResourceId(
                    R.styleable.ButtonDrawable_btn_drawableBottomCompat, -1)
                val drawableTopId = attributeArray.getResourceId(
                    R.styleable.ButtonDrawable_btn_drawableTopCompat, -1)
                val drawableBackgroundId = attributeArray.getResourceId(
                    R.styleable.ButtonDrawable_btn_drawableBackground, -1)

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
}