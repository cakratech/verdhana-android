package com.bcasekuritas.mybest.widget.banner

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.databinding.CustomWidgetBannerCenterBinding

class BannerCenter  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {


    private val binding = CustomWidgetBannerCenterBinding.inflate(LayoutInflater.from(context), this, true)

    init {

    }

    // Initialize the ViewPager2 with an adapter and set up the dots
    fun initSlider(items: List<BannerUtil.BannerItemSpan>) {
        val adapter = BannerUtil.BannerAdapter(items)
        binding.viewpager.adapter = adapter

        setupDots(items.size)

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tvTitle.setText(items.map { it.title }[position])
                binding.tvDesc.text = items.map { it.desc }[position]
                updateDots(position)
            }
        })
    }
    // Set up the dots based on the number of slides
    private fun setupDots(numDots: Int) {
        binding.lyDotLayout.removeAllViews()

        for (i in 0 until numDots) {
            val dot = ImageView(context)

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(16, 0, 16, 0) // Set margins if needed

            dot.layoutParams = layoutParams

            val unselectedDrawable = ContextCompat.getDrawable(context, R.drawable.ic_dot_unselected)
            val color = ContextCompat.getColor(context, R.color.unselectedDotDashboard)
            unselectedDrawable?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            dot.setImageDrawable(unselectedDrawable)
            binding.lyDotLayout.addView(dot)
        }

        // Highlight the first dot initially
        updateDots(0)
    }

    private fun updateDots(selectedDotPosition: Int) {
        val dotCount = binding.lyDotLayout.childCount
        for (i in 0 until dotCount) {
            val dot = binding.lyDotLayout.getChildAt(i) as ImageView
            if (i == selectedDotPosition) {
                val selectedDrawable = ContextCompat.getDrawable(context, R.drawable.ic_dot_selected)
                val color = ContextCompat.getColor(context, R.color.selectedDotDashboard)
                selectedDrawable?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                dot.setImageDrawable(selectedDrawable)
            } else {
                val unselectedDrawable = ContextCompat.getDrawable(context, R.drawable.ic_dot_unselected)
                val color = ContextCompat.getColor(context, R.color.unselectedDotDashboard)
                unselectedDrawable?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                dot.setImageDrawable(unselectedDrawable)
            }
        }
    }

    // Set a callback for when a user clicks an image
    fun setImageClickListener(callback: (position: Int) -> Unit) {
        binding.viewpager.setOnClickListener {
            callback(binding.viewpager.currentItem)
        }
    }
}