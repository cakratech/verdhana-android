package com.bcasekuritas.mybest.widget.banner

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.feature.activity.main.MainActivity
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.profile.profilelanding.ProfileFragmentDirections
import com.bcasekuritas.mybest.databinding.CustomWidgetBannerHomeBinding
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.ConstKeys
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.listener.OnClickStr

class BannerHome  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), OnClickStr {


    private val binding = CustomWidgetBannerHomeBinding.inflate(LayoutInflater.from(context), this, true)

    private var activity: Activity? = null // Store the Activity reference

    fun setActivity(activity: Activity) {
        this.activity = activity
    }

    init {

    }

    // Initialize the ViewPager2 with an adapter and set up the dots
    fun initSlider(items: ArrayList<BannerUtil.BannerItemPromo>) {
        val adapter = BannerUtil.BannerHomeAdapter(items, this)
        binding.viewpager.adapter = adapter

        setupDots(items.size)

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
//                binding.tvTitle.text = items.map { it.title }[position]
//                binding.tvDesc.text = items.map { it.desc }[position]
                updateDots(position)
            }
        })
    }

    // Set up the dots based on the number of slides
    private fun setupDots(numDots: Int) {
//        binding.lyDotLayout.removeAllViews()
        binding.lyDotLayout.removeAllViews()

        for (i in 0 until numDots) {
            val dot = ImageView(context)

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(8, 0, 8, 0) // Set margins if needed

            dot.layoutParams = layoutParams

            dot.setImageResource(R.drawable.rectangle_unselected)
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
                dot.setImageResource(R.drawable.rectangle_selected)
            } else {
                dot.setImageResource(R.drawable.rectangle_unselected)
            }
        }
    }

    // Set a callback for when a user clicks an image
    fun setImageClickListener(callback: (position: Int) -> Unit) {
        binding.viewpager.setOnClickListener {
            callback(binding.viewpager.currentItem)
        }
    }

    override fun onClickStr(value: String?) {
        if (value?.isNotEmpty() == true) {
            navigateDeepLink(value)
        }
    }

    private fun openWebView(value: String?) {
        val webView = CustomTabsIntent.Builder().build()
        if (!value.isNullOrEmpty()) {
            var url = value
            if (!value.contains("https://")) {
                url = "https://$value"
            }
            if (url.isBlank() || !(url.startsWith("http://") || url.startsWith("https://"))) {
                Toast.makeText(context, "Unavailable url file", Toast.LENGTH_SHORT).show()
                return
            }

            try {
                webView.launchUrl(context, Uri.parse(url))
            } catch (ignore: Exception) {}
        }
    }

    private fun navigateDeepLink(link: String) {
        val url = Uri.parse(link)
        val segment = url.pathSegments
        if (segment.isNotEmpty()) {
            val firstSegment = segment[0]
            val twoSegment = if (segment.size > 1) segment[1] else ""

            if (activity != null) {
                when (firstSegment) {
                    "home" -> {
                        Log.d("deeplink", "home")
                    }
                    "order" -> {
                        MiddleActivity.startIntentParam(
                            activity!!,
                            NavKeys.KEY_FM_ORDER,
                            "BBCA",
                            0
                        )
                    }
                    "discover" -> findNavController().navigate(R.id.discover_fragment)
                    "portofolio" -> {
                        val layoutPosition = if (twoSegment == "orderlist") 1 else 0
                        val bundle = Bundle().apply {
                            putInt(Args.EXTRA_PARAM_INT_ONE, layoutPosition)
                        }
                        findNavController().navigate(R.id.portfolio_fragment, bundle)
                    }
                    "news" -> {
                        val layoutPosition = if (twoSegment == "research") 1 else 0
                        val bundle = Bundle().apply {
                            putInt(Args.EXTRA_PARAM_INT_ONE, layoutPosition)
                        }
                        findNavController().navigate(R.id.news_fragment, bundle)
                    }
                    "profile" -> findNavController().navigate(R.id.profile_fragment)
                    "stock-pick" -> findNavController().navigate(R.id.stock_pick_fragment)
                    "calendar" -> findNavController().navigate(R.id.calendar_fragment)
                    "fast-order" -> MiddleActivity.startIntentParam(
                        activity!!,
                        NavKeys.KEY_FM_FAST_ORDER,
                        "BBCA",
                        "Bank Central Asia Tbk"
                    )
                    "price-alert" -> MiddleActivity.startIntent(activity!!, NavKeys.KEY_FM_PRICE_ALERT)
                    "broker-sum" -> findNavController().navigate(R.id.broker_summary_fragment)
                    "run-trade" -> findNavController().navigate(R.id.running_trade_fragment)
                    "global-market" -> findNavController().navigate(R.id.global_market_fragment)
                    "right-issue" -> MiddleActivity.startIntent(activity!!, NavKeys.KEY_FM_RIGHT_ISSUE)
                    "stock-detail" -> {
                        MiddleActivity.startIntentParam(
                            activity!!,
                            NavKeys.KEY_FM_STOCK_DETAIL,
                            twoSegment, false
                        )
                    }
                    else -> {
                        openWebView(link)
                    }
                }
            }
        } else {
            openWebView(link)
        }
    }
}