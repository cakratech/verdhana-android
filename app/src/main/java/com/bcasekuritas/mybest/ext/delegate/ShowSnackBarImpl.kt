package com.bcasekuritas.mybest.ext.delegate

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.fragment.app.FragmentActivity
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.feature.activity.main.MainActivity
import com.bcasekuritas.mybest.databinding.CustomSnackbarOrderBinding
import com.bcasekuritas.mybest.databinding.CustomSnackbarTopBinding
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.converter.GET_STATUS_ORDER
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.google.android.material.snackbar.Snackbar

interface ShowSnackBarInterface {
    fun showSnackBarTop(
        context: Context,
        component: View,
        status: String,
        imageStatus: Int,
        title: String,
        desc: String,
        activity: FragmentActivity,
        fromLayout: String
    )

    fun showSnackBarOrder(
        context: Context,
        component: View,
        buySell: String,
        stockCode: String,
        status: String,
        lotSize: String,
        price: Double
    )

    fun showSnackBarBottom(
        context: Context,
        component: View,
        status: String,
        imageStatus: Int,
        title: String,
        desc: String,
        activity: FragmentActivity,
        fromLayout: String
    )
}

class ShowSnackBarImpl : ShowSnackBarInterface {
    @SuppressLint("RestrictedApi")
    override fun showSnackBarTop(
        context: Context,
        component: View,
        status: String,
        imageStatus: Int,
        title: String,
        desc: String,
        activity: FragmentActivity,
        fromLayout: String
    ) {
        val binding = CustomSnackbarTopBinding.inflate(LayoutInflater.from(context))
        val snackbar = Snackbar.make(component, "", Snackbar.LENGTH_LONG)
        ViewCompat.setOnApplyWindowInsetsListener(snackbar.view) { view, insets ->
            val topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            view.setPadding(
                view.paddingLeft,
                topInset,
                view.paddingRight,
                view.paddingBottom
            )
            insets
        }

        // Set SnackBar From Top Of The Screen
        if (snackbar.view.layoutParams is FrameLayout.LayoutParams) {
            val params = snackbar.view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            snackbar.view.layoutParams = params
        } else if (snackbar.view.layoutParams is CoordinatorLayout.LayoutParams) {
            val params = snackbar.view.layoutParams as CoordinatorLayout.LayoutParams
            params.gravity = Gravity.TOP
            snackbar.view.layoutParams = params
        }

        binding.tvTitle.text = title

        if (desc != null && desc != "") {
            binding.tvDesc.visibility = View.VISIBLE
            binding.tvDesc.text = desc
        } else {
            binding.tvDesc.visibility = View.GONE
        }

        binding.ivStatus.setImageResource(imageStatus)

        when (status) {
            "success" -> {
                binding.clCustomSnackbarTop.setBackgroundResource(R.drawable.bg_light_green_border_green)
                val tintColor = ContextCompat.getColor(context, R.color.green)
                binding.ivStatus.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
                binding.btnClose.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
                binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.green))
                binding.tvDesc.setTextColor(ContextCompat.getColor(context, R.color.textPrimary))
            }

            "info" -> {
                binding.clCustomSnackbarTop.setBackgroundResource(R.drawable.bg_light_blue_border_blue)
                val tintColor = ContextCompat.getColor(context, R.color.blue)
                binding.ivStatus.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
                binding.btnClose.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
                binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.blue))
                binding.tvDesc.setTextColor(ContextCompat.getColor(context, R.color.textPrimary))
            }

            "warning" -> {
                binding.clCustomSnackbarTop.setBackgroundResource(R.drawable.bg_light_yellow_border_yellow)
                val tintColor = ContextCompat.getColor(context, R.color.yellow)
                binding.ivStatus.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
                binding.btnClose.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
                binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.yellow))
                binding.tvDesc.setTextColor(ContextCompat.getColor(context, R.color.textPrimary))
            }

            "error" -> {
                if (title.isEmpty()) {
                    binding.tvTitle.isGone = true
                    binding.tvDesc.setTextColor(ContextCompat.getColor(context, R.color.textDown))
                }
                binding.clCustomSnackbarTop.setBackgroundResource(R.drawable.bg_light_red_border_red)
                val tintColor = ContextCompat.getColor(context, R.color.textDown)
                binding.ivStatus.setColorFilter(ContextCompat.getColor(context, R.color.textDown))
                binding.btnClose.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
                binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.textDown))
                binding.tvDesc.setTextColor(ContextCompat.getColor(context, R.color.textPrimary))

            }
        }



        binding.btnClose.setOnClickListener {
            snackbar.dismiss()
        }

        binding.clCustomSnackbarTop.setOnClickListener {
//            if (activity is MiddleActivity) {
//                MainActivity.startIntentParam(activity, NavKeys.KEY_FM_TAB_PORTFOLIO, 1,"")
//                snackbar.dismiss()
//            }
            when (fromLayout) {
                NavKeys.KEY_FM_EXERCISE_ORDER -> {
                    //activity.findNavController(R.id.f_middle_container).navigate(R.id.exercise_order_list_fragment)
                    snackbar.dismiss()
                }
                NavKeys.KEY_FM_ORDER -> {
                    MainActivity.startIntentParam(activity, NavKeys.KEY_FM_TAB_PORTFOLIO, 1,"")
                    snackbar.dismiss()
                }
                else -> snackbar.dismiss()
            }
//            val bundle = Bundle().apply {
//                putInt(Args.EXTRA_PARAM_INT_ONE, 1)
//            }
//            navController.navigate(R.id.fragment_portofolio, bundle)

        }

        snackbar.view.setBackgroundResource(R.color.transparent)

        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
        snackbarLayout.addView(binding.root, 0)
        snackbar.show()
    }

    @SuppressLint("RestrictedApi")
    override fun showSnackBarOrder(
        context: Context,
        component: View,
        buySell: String,
        stockCode: String,
        status: String,
        lotSize: String,
        price: Double
    ) {
        val binding = CustomSnackbarOrderBinding.inflate(LayoutInflater.from(context))
        val snackbar = Snackbar.make(component, "", Snackbar.LENGTH_LONG)

        // Set SnackBar From Top Of The Screen
        if (snackbar.view.layoutParams is FrameLayout.LayoutParams) {
            val params = snackbar.view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.BOTTOM
            snackbar.view.layoutParams = params
        } else if (snackbar.view.layoutParams is CoordinatorLayout.LayoutParams) {
            val params = snackbar.view.layoutParams as CoordinatorLayout.LayoutParams
            params.gravity = Gravity.BOTTOM
            snackbar.view.layoutParams = params
        }

        val bs = if (buySell == "B") "Buy" else "Sell"

        binding.tvNotif1.text = "$bs $stockCode - ${status.GET_STATUS_ORDER()}"
        binding.tvNotif2.text = "$lotSize (lot) ${price.formatPriceWithoutDecimal()}"

        binding.btnClose.setOnClickListener {
            snackbar.dismiss()
        }

        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
        snackbarLayout.addView(binding.root, 0)
        snackbar.show()
    }

    @SuppressLint("RestrictedApi")
    override fun showSnackBarBottom(
        context: Context,
        component: View,
        status: String,
        imageStatus: Int,
        title: String,
        desc: String,
        activity: FragmentActivity,
        fromLayout: String
    ) {
        val binding = CustomSnackbarTopBinding.inflate(LayoutInflater.from(context))
        val snackbar = Snackbar.make(component, "", Snackbar.LENGTH_LONG)

        // Set SnackBar From Top Of The Screen
        if (snackbar.view.layoutParams is FrameLayout.LayoutParams) {
            val params = snackbar.view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.BOTTOM
            snackbar.view.layoutParams = params
        } else if (snackbar.view.layoutParams is CoordinatorLayout.LayoutParams) {
            val params = snackbar.view.layoutParams as CoordinatorLayout.LayoutParams
            params.gravity = Gravity.BOTTOM
            snackbar.view.layoutParams = params
        }

        binding.tvTitle.text = title

        if (desc != null && desc != "") {
            binding.tvDesc.visibility = View.VISIBLE
            binding.tvDesc.text = desc
        } else {
            binding.tvDesc.visibility = View.GONE
        }

        binding.ivStatus.setImageResource(imageStatus)

        when (status) {
            "success" -> {
                binding.clCustomSnackbarTop.setBackgroundResource(R.drawable.bg_light_green_border_green)
                val tintColor = ContextCompat.getColor(context, R.color.green)
                binding.ivStatus.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
                binding.btnClose.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
                binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.green))
            }

            "info" -> {
                binding.clCustomSnackbarTop.setBackgroundResource(R.drawable.bg_light_blue_border_blue)
                val tintColor = ContextCompat.getColor(context, R.color.blue)
                binding.ivStatus.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
                binding.btnClose.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
                binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.blue))
            }

            "warning" -> {
                binding.clCustomSnackbarTop.setBackgroundResource(R.drawable.bg_light_yellow_border_yellow)
                val tintColor = ContextCompat.getColor(context, R.color.yellow)
                binding.ivStatus.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
                binding.btnClose.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
                binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.yellow))
            }

            "error" -> {
                if (title.isEmpty()) {
                    binding.tvTitle.isGone = true
                    binding.tvDesc.setTextColor(ContextCompat.getColor(context, R.color.textDown))
                }
                binding.clCustomSnackbarTop.setBackgroundResource(R.drawable.bg_light_red_border_red)
                binding.ivStatus.setColorFilter(ContextCompat.getColor(context, R.color.textDown))
                binding.ivStatus.setImageResource(R.drawable.ic_alert)
                binding.btnClose.visibility = View.GONE
                binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.textDown))
            }
        }



        binding.btnClose.setOnClickListener {
            snackbar.dismiss()
        }

        binding.clCustomSnackbarTop.setOnClickListener {
//            if (activity is MiddleActivity) {
//                MainActivity.startIntentParam(activity, NavKeys.KEY_FM_TAB_PORTFOLIO, 1,"")
//                snackbar.dismiss()
//            }
            when (fromLayout) {
                NavKeys.KEY_FM_EXERCISE_ORDER -> {
                    //activity.findNavController(R.id.f_middle_container).navigate(R.id.exercise_order_list_fragment)
                    snackbar.dismiss()
                }
                NavKeys.KEY_FM_ORDER -> {
                    MainActivity.startIntentParam(activity, NavKeys.KEY_FM_TAB_PORTFOLIO, 1,"")
                    snackbar.dismiss()
                }
                else -> snackbar.dismiss()
            }
//            val bundle = Bundle().apply {
//                putInt(Args.EXTRA_PARAM_INT_ONE, 1)
//            }
//            navController.navigate(R.id.fragment_portofolio, bundle)

        }

        snackbar.view.setBackgroundResource(R.color.transparent)

        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
        snackbarLayout.addView(binding.root, 0)
        snackbar.show()
    }
}