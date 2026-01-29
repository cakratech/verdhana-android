package com.bcasekuritas.mybest.app.feature.dialog.coachmark

import android.content.res.Resources
import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowInsets
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.dialog.withvm.BaseDialogFullFragment
import com.bcasekuritas.mybest.app.domain.dto.response.TradeSummary
import com.bcasekuritas.mybest.app.feature.dialog.coachmark.adapter.CoachmarkWatchlistAdapter
import com.bcasekuritas.mybest.databinding.DialogCoachmarkNoWatchlistBinding
import com.bcasekuritas.mybest.databinding.DialogCoachmarkWatchlistBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CoachmarkWatchlistDialog: BaseDialogFullFragment<DialogCoachmarkWatchlistBinding, CoachmarkViewModel>() {
    override val viewModel: CoachmarkViewModel by viewModels()
    override val binding: DialogCoachmarkWatchlistBinding by autoCleaned { (DialogCoachmarkWatchlistBinding.inflate(layoutInflater)) }
    override val bindingVariable: Int = BR.vmCoachmark

    private val mAdapter: CoachmarkWatchlistAdapter by autoCleaned { CoachmarkWatchlistAdapter() }

    private var currentStep = 1

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvCoachmark.apply {
            adapter = mAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

    }

    override fun setupComponent() {
        super.setupComponent()
        binding.bottomNavigation.itemIconTintList = null
        mAdapter.setData(watchlistData(), prefManager.urlIcon)
        setHeightCoachmark()
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
        }
    }

    private fun nextStep() {
        binding.apply {
            when (currentStep) {
                2 -> {
                    tvCoachmarkStep.text = "2 of 3"
                    tvCoachmarkTitle.text = "Add Watchlist"
                    tvCoachmarkDesc.text = "Add stocks to watchlist by tapping on Manage. You can also add categories to your stocks."
                    bgCoachmarkBot.visibility = View.VISIBLE

                }
                3 -> {
                    tvCoachmarkStep.text = "3 of 3"
                    tvCoachmarkTitle.text = "Easy Access To Your Actions"
                    tvCoachmarkDesc.text = "Swipe right to buy stocks or swipe left to remove irrelevant stocks from your list."
                }
                else -> dismiss()
            }
            setHeightCoachmark()
        }
    }

    private fun setHeightCoachmark() {
        binding.apply {
            when (currentStep) {
                1 -> {
                    lyWatchlist.post {
                        val height = binding.lyWatchlist.top
                        val layoutParams = bgCoachmarkTop.layoutParams
                        layoutParams.height = height
                        bgCoachmarkTop.layoutParams = layoutParams
                    }
                }
                2 -> {
                    val containerHeight = binding.container.height
                    val topRcv = binding.rcvCoachmark.top + binding.lyWatchlist.top

                    val layoutParams = bgCoachmarkBot.layoutParams
                    layoutParams.height = containerHeight - topRcv
                    bgCoachmarkBot.layoutParams = layoutParams
                }
                3 -> {
                    val containerHeight = binding.container.height
                    val rcvTop = binding.rcvCoachmark.top + binding.lyWatchlist.top
                    val viewHolderTop = binding.rcvCoachmark.findViewHolderForAdapterPosition(2)
                    val viewHolderBot = binding.rcvCoachmark.findViewHolderForAdapterPosition(3)
                    val itemViewTop = viewHolderTop?.itemView
                    val itemViewBot = viewHolderBot?.itemView

                    if (itemViewTop != null&& itemViewBot != null) {
                        val itemTop = itemViewTop.top
                        val itemBot = itemViewBot.bottom
                        val topInParent = itemTop + rcvTop
                        val botInParent = itemBot + rcvTop

                        val layoutParamsTop = binding.bgCoachmarkTop.layoutParams
                        layoutParamsTop.height = topInParent
                        binding.bgCoachmarkTop.layoutParams = layoutParamsTop

                        val layoutParamsBot = binding.bgCoachmarkBot.layoutParams
                        layoutParamsBot.height = containerHeight - botInParent
                        binding.bgCoachmarkBot.layoutParams = layoutParamsBot

                        mAdapter.showSwipeButton()
                    }
                }
            }

        }

    }

    fun watchlistData(): ArrayList<TradeSummary> {
        return arrayListOf<TradeSummary>(
            TradeSummary(secCode = "BBCA", stockName = "Bank Central Asia Tbk", last = 2780.0, change = 30.0, changePct = 0.55),
            TradeSummary(secCode = "BBNI", stockName = "Bank Negara Indonesia", last = 2780.0, change = -30.0, changePct = -0.55),
            TradeSummary(secCode = "BMRI", stockName = "Bank Mandiri (Persero)", last = 2780.0, change = 3000.0, changePct = 0.55),
            TradeSummary(secCode = "MIDI", stockName = "Midi Utama Indonesia", last = 2780.0, change = 0.0, changePct = 0.0),
            TradeSummary(secCode = "UNVR", stockName = "Unilever Indonesia Tbk", last = 2780.0, change = 0.0, changePct = 0.0),
            TradeSummary(secCode = "AMRT", stockName = "Sumber Alfa Trijaya", last = 2780.0, change = 0.0, changePct = 0.0),
        )
    }
}