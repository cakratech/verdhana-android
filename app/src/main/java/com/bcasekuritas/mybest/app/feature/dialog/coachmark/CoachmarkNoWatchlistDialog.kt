package com.bcasekuritas.mybest.app.feature.dialog.coachmark

import android.view.View
import androidx.fragment.app.viewModels
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.dialog.withvm.BaseDialogFullFragment
import com.bcasekuritas.mybest.databinding.DialogCoachmarkNoWatchlistBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned

class CoachmarkNoWatchlistDialog: BaseDialogFullFragment<DialogCoachmarkNoWatchlistBinding, CoachmarkViewModel>() {
    override val viewModel: CoachmarkViewModel by viewModels()
    override val binding: DialogCoachmarkNoWatchlistBinding by autoCleaned { (DialogCoachmarkNoWatchlistBinding.inflate(layoutInflater)) }
    override val bindingVariable: Int = BR.vmCoachmark

    private var currentStep = 1

    override fun setupComponent() {
        super.setupComponent()
        binding.bottomNavigation.itemIconTintList = null
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

    fun nextStep() {
        if (currentStep > 2) {
            dismiss()
        } else {
            binding.apply {
                tvCoachmarkStep.text = "2 of 2"
                tvCoachmarkTitle.text = "Add Watchlist"
                tvCoachmarkDesc.text = "Add stocks to watchlist by tapping on Add Stocks. You can also add categories to your stocks."
                bgCoachmarkBot.visibility = View.VISIBLE
            }
        }
    }
}