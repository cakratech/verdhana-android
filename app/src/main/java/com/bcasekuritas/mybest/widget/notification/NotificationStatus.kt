package com.bcasekuritas.mybest.widget.notification

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.databinding.CustomWidgetNotificationStatusBinding

class NotificationStatus@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = CustomWidgetNotificationStatusBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.NotificationStatus )
        val statusStyle = typedArray.getString(R.styleable.NotificationStatus_statusStyle)
        typedArray.recycle()

        when (statusStyle){
            "default" -> {

            }
            "icon" -> {
                binding.ivIcon.visibility = VISIBLE
            }
        }
    }

}