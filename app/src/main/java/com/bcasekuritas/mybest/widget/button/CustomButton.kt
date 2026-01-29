package com.bcasekuritas.mybest.widget.button

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.bcasekuritas.mybest.R
import com.google.android.material.button.MaterialButton

class CustomButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet?
) : MaterialButton(context, attrs, R.attr.buttonStyle) {

    init {
        // Read custom attributes
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomButton)
        val btnStyle = typedArray.getString(R.styleable.CustomButton_btnStyle)
        val iconResId = typedArray.getResourceId(R.styleable.CustomButton_icon, 0)
        typedArray.recycle()

        backgroundTintList = null
        stateListAnimator = null
        elevation = 0f
        isAllCaps = false

        when (btnStyle) {
            "default" -> {
                setBackgroundResource(R.drawable.btn_0154fa_8)
                setTextColor(ContextCompat.getColor(context, R.color.txtWhiteBlack))
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            }
            "primary" -> {
                setBackgroundResource(R.drawable.btn_0154fa_8)
                setTextColor(ContextCompat.getColor(context, R.color.txtWhiteBlack))
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            }

            "outline" -> {
                setBackgroundResource(R.drawable.bg_ffffff_8_stroke_015fcd)
                setTextColor(ContextCompat.getColor(context, R.color.textBCABlue))
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            }

            "red" -> {
                setBackgroundResource(R.drawable.bg_e14343_8)
                setTextColor(ContextCompat.getColor(context, R.color.textWhite))
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            }

            "green" -> {
                setBackgroundResource(R.drawable.bg_27ae60_8)
                setTextColor(ContextCompat.getColor(context, R.color.textWhite))
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            }

            "orange" -> {
                setBackgroundResource(R.drawable.bg_ff7a00_8)
                setTextColor(ContextCompat.getColor(context, R.color.textWhite))
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            }

            "white" -> {
                setBackgroundResource(R.drawable.bg_ffffff_8)
                setTextColor(ContextCompat.getColor(context, R.color.textPrimary))
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            }

            "outlineGrey" -> {
                setBackgroundResource(R.drawable.bg_ffffff_stroke_dae8f6_0154fa)
                setTextColor(ContextCompat.getColor(context, R.color.txtBlackWhite))
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            }

            "transBlack" -> {
                background = null
                setIconResource(iconResId)
                setTextColor(ContextCompat.getColor(context, R.color.txtBlackWhite))
                setIconTintResource(R.color.bgBlack)
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            }

            "transBlue" -> {
                background = null
                setIconResource(iconResId)
                setTextColor(ContextCompat.getColor(context, R.color.textBCABlue))
                setIconTintResource(R.color.buttonPrimary)
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            }

            "transOutlineWhite" -> {
                setIconResource(iconResId)
                setBackgroundResource(R.drawable.bg_button_trans_stroke_ffffff)
                setTextColor(ContextCompat.getColor(context, R.color.textWhite))
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            }

            "action" -> {
                setBackgroundResource(R.drawable.bg_00aeb9_8)
                setTextColor(ContextCompat.getColor(context, R.color.textWhite))
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            }

            "redOrange" -> {
                setBackgroundResource(R.drawable.bg_f25532_8)
                setTextColor(ContextCompat.getColor(context, R.color.textWhite))
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            }

            "outlineCyan" -> {
                setBackgroundResource(R.drawable.bg_button_ffffff_stroke_02b9cb)
                setTextColor(ContextCompat.getColor(context, R.color.action_cyan))
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            }

            "outlineRed" -> {
                setBackgroundResource(R.drawable.bg_button_ffffff_stroke_e14343)
                setTextColor(ContextCompat.getColor(context, R.color.textDown))
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            }

            "dashboard" -> {
                setBackgroundResource(R.drawable.bg_button_dashboard)
                setTextColor(ContextCompat.getColor(context, R.color.textPrimaryDashboard))
            }

            "cancel" -> {}

            "warning" -> {}
        }
    }
}