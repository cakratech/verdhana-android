package com.bcasekuritas.mybest.widget.banner

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.databinding.CustomWidgetBannerLeftBinding

class BannerLeft  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {


    private val binding = CustomWidgetBannerLeftBinding.inflate(LayoutInflater.from(context), this, true)

    private val webView = CustomTabsIntent.Builder().build()

    init {

    }

    // Initialize the ViewPager2 with an adapter and set up the dots
    fun initSlider(items: List<BannerUtil.BannerItem>) {
        val adapter = BannerUtil.BannerAdapterLogin(items) { ctaLink ->
            if (ctaLink.isBlank() || !(ctaLink.startsWith("http://") || ctaLink.startsWith("https://"))) {
                Toast.makeText(context, "Unavailable url file", Toast.LENGTH_SHORT).show()
                return@BannerAdapterLogin
            }
            try {
                webView.launchUrl(context, Uri.parse(ctaLink))
            } catch (ignore: Exception) {}
        }
        binding.viewpager.adapter = adapter

        setupDots(items.size)

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
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
            layoutParams.setMargins(8, 0, 8, 0) // Set margins if needed

            dot.layoutParams = layoutParams
            dot.setImageResource(R.drawable.ic_dot_unselected)
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
                dot.setImageResource(R.drawable.ic_dot_selected)
            } else {
                dot.setImageResource(R.drawable.ic_dot_unselected)
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