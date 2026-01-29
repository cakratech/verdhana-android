package com.bcasekuritas.mybest.widget.snackbar

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.databinding.CustomSnackbarTopBinding

class CustomSnackBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = CustomSnackbarTopBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomSnackBar )
        val snackbarFor = typedArray.getString(R.styleable.CustomSnackBar_snackbarFor)
        typedArray.recycle()

        when(snackbarFor){
            "success" -> {
                binding.clCustomSnackbarTop.setBackgroundResource(R.drawable.bg_light_green_border_green)
                val tintColor = ContextCompat.getColor(context, R.color.green)
                binding.ivStatus.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
                binding.btnClose.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
                binding.tvTitle.setTextColor(getResources().getColor(R.color.green))
            }
            "info" -> {
                binding.clCustomSnackbarTop.setBackgroundResource(R.drawable.bg_light_blue_border_blue)
                val tintColor = ContextCompat.getColor(context, R.color.blue)
                binding.ivStatus.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
                binding.btnClose.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
                binding.tvTitle.setTextColor(getResources().getColor(R.color.blue))
            }
            "warning" -> {
                binding.clCustomSnackbarTop.setBackgroundResource(R.drawable.bg_light_yellow_border_yellow)
                val tintColor = ContextCompat.getColor(context, R.color.yellow)
                binding.ivStatus.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
                binding.btnClose.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
                binding.tvTitle.setTextColor(getResources().getColor(R.color.yellow))
            }
            "error" -> {
                binding.clCustomSnackbarTop.setBackgroundResource(R.drawable.bg_light_red_border_red)
                val tintColor = ContextCompat.getColor(context, R.color.red)
                binding.ivStatus.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
                binding.btnClose.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
                binding.tvTitle.setTextColor(getResources().getColor(R.color.red))
            }

        }
    }
}