package com.bcasekuritas.mybest.app.feature.dialog.coachmark

import android.content.res.Resources
import android.graphics.PorterDuff
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withvm.BaseDialogFullFragment
import com.bcasekuritas.mybest.app.domain.dto.response.CoachMarkFastOrder
import com.bcasekuritas.mybest.app.feature.dialog.coachmark.adapter.CoachmarkFastOrderAdapter
import com.bcasekuritas.mybest.databinding.DialogCoachmarkFastOrderBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned

class CoachmarkFastOrderDialog: BaseDialogFullFragment<DialogCoachmarkFastOrderBinding, CoachmarkViewModel>() {

    override val viewModel: CoachmarkViewModel by viewModels()
    override val binding: DialogCoachmarkFastOrderBinding by autoCleaned { (DialogCoachmarkFastOrderBinding.inflate(layoutInflater)) }
    override val bindingVariable: Int = BR.vmCoachmark

    private val fastOrderAdapter: CoachmarkFastOrderAdapter by autoCleaned {
        CoachmarkFastOrderAdapter(
            requireContext()
        )
    }

    private var currentStep = 1
    private var maxItem = 0

    var statusBarHeight = 0

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvFastOrderBook.adapter = fastOrderAdapter
    }

    private fun getStatusBarHeight() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top

            // penting! teruskan agar fitsSystemWindows tetap jalan
            ViewCompat.onApplyWindowInsets(view, insets)
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            getStatusBarHeight()
            setMaxData()
            lyToolbar.tvLayoutToolbarMasterTitle.text = "BBCA"
            lyToolbar.ivLayoutToolbarMasterIconRightOne.visibility = View.VISIBLE
            lyToolbar.ivLayoutToolbarMasterIconRightTwo.visibility = View.VISIBLE

            lyToolbar.ivLayoutToolbarMasterIconRightOne.setImageResource(R.drawable.ic_search)
            lyToolbar.ivLayoutToolbarMasterIconRightTwo.setImageResource(R.drawable.ic_setting)

            val tintColor = ContextCompat.getColor(requireContext(), R.color.black)
            lyToolbar.ivLayoutToolbarMasterIconRightOne.setColorFilter(
                tintColor,
                PorterDuff.Mode.SRC_IN
            )
            lyToolbar.ivLayoutToolbarMasterIconRightTwo.setColorFilter(
                tintColor,
                PorterDuff.Mode.SRC_IN
            )

        }
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            btnCoachmarkSkip.setOnClickListener {
                dismiss()
            }

            btnCoachmarkNext.setOnClickListener {
                currentStep += 1
                nextStep()
            }

            btnCoachmarkSkip4.setOnClickListener {
                dismiss()
            }

            btnCoachmarkNext4.setOnClickListener {
                currentStep += 1
                nextStep()
            }

            tvBuyValue.setOnLongClickListener {
                lyDropdownBuy.visibility = View.VISIBLE
                true
            }
        }
    }

    private fun nextStep() {
        binding.apply {
            when (currentStep) {
                2 -> {
                    tvCoachmarkStep.text = "2 of 4"
                    tvCoachmarkTitle.text = "Change Stock"
                    tvCoachmarkDesc.text = "Tap code name or search icon to change name"

                }
                3 -> {
                    step3A.visibility = View.VISIBLE
                    step3B.visibility = View.VISIBLE
                    step3C.visibility = View.VISIBLE

                    tvCoachmarkStep.text = "3 of 4"
                    tvCoachmarkTitle.text = "Set up fast order"
                    tvCoachmarkDesc.text = "Set up volume and setting for 1-tap order"
                }
                4 -> {
                }
                else -> dismiss()
            }
            setHeightCoachmark()
        }
    }

    private fun setHeightCoachmark() {
        binding.apply {
            when (currentStep) {
                2 -> {
                    changeMarginTopCoachmark()
                    val bottomToolbar = container.height - (tvSetUp.top + statusBarHeight)

                    val layoutParams = bgCoachmarkBot.layoutParams
                    layoutParams.height = bottomToolbar
                    bgCoachmarkBot.layoutParams = layoutParams
                }
                3 -> {
                    bgCoachmarkTop.visibility = View.VISIBLE
                    val containerHeight = container.height
                    val top = tvSetUp.top - statusBarHeight
                    val bot = viewDividerTrading.top + statusBarHeight

                    val layoutParamsTop = binding.bgCoachmarkTop.layoutParams
                    layoutParamsTop.height = top
                    binding.bgCoachmarkTop.layoutParams = layoutParamsTop

                    val layoutParamsBot = binding.bgCoachmarkBot.layoutParams
                    layoutParamsBot.height = containerHeight - bot
                    binding.bgCoachmarkBot.layoutParams = layoutParamsBot
                }
                4 -> {
                    bgCoachmarkTop.visibility = View.GONE
                    lyCoachmark.visibility = View.GONE
                    highlightBuy.visibility = View.VISIBLE
                    highlightSell.visibility = View.VISIBLE
                    lyCoachmarkStep4.visibility = View.VISIBLE

                    val layoutParams = bgCoachmarkBot.layoutParams
                    layoutParams.height = container.height
                    bgCoachmarkBot.layoutParams = layoutParams

                    Handler(Looper.getMainLooper()).postDelayed({
                        tvBuyValue.performLongClick()
                    }, 2000)
                }
            }

        }

    }

    private fun changeMarginTopCoachmark() {
        val marginTopInDp = 30
        val marginTopInPx = (marginTopInDp * Resources.getSystem().displayMetrics.density).toInt()

        val layoutParams = binding.lyCoachmark.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topMargin = marginTopInPx
        binding.lyCoachmark.layoutParams = layoutParams
    }

    private fun getDataList(): List<CoachMarkFastOrder> {
        val basePrice = 9325.0
        val volume = 127919.0

        val list = mutableListOf<CoachMarkFastOrder>()

        for (i in 0 until maxItem) {
            val price = basePrice - (i * 25)
            val buy = if (i == 0) "1(1)" else ""
            val bid = if (i==maxItem-1) 127919.0 else 0.0
            list.add(CoachMarkFastOrder(buy, "",bid , price, volume))
        }

        return list
    }

    private fun setMaxData() {
        binding.rcvFastOrderBook.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.rcvFastOrderBook.viewTreeObserver.removeOnGlobalLayoutListener(this)
                if (isAdded) {
                    val recyclerViewHeight = binding.rcvFastOrderBook.height
                    val density = resources.displayMetrics.density
                    val heightInDP = recyclerViewHeight / density

                    val totalItem = heightInDP.toInt()/50
                    if (totalItem > 0) {
                        maxItem = totalItem
                        fastOrderAdapter.setData(getDataList())
                    }
                }
            }
        })
    }
}