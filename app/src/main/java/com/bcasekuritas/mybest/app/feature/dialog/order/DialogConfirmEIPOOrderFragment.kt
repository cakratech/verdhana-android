package com.bcasekuritas.mybest.app.feature.dialog.order

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.data.layout.UIDialogExerciseOrderModel
import com.bcasekuritas.mybest.databinding.CustomStickyButtonBinding
import com.bcasekuritas.mybest.databinding.DialogConfrimEipoOrderBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.converter.GET_4_CHAR_STOCK_CODE
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

@FragmentScoped
@AndroidEntryPoint
class DialogConfirmEIPOOrderFragment(private val model: UIDialogExerciseOrderModel): BaseBottomSheet<DialogConfrimEipoOrderBinding>(), ShowDialog by ShowDialogImpl() {

    @FragmentScoped
    override val binding: DialogConfrimEipoOrderBinding by autoCleaned { (DialogConfrimEipoOrderBinding.inflate(layoutInflater)) }

    lateinit var stickyLayoutBinding: CustomStickyButtonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stickyLayoutBinding = CustomStickyButtonBinding.inflate(
            LayoutInflater.from(requireContext())
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener{
            (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            (dialog as BottomSheetDialog).behavior.isFitToContents = true

            val containerLayout = dialog?.findViewById(
                com.google.android.material.R.id.container
            ) as? FrameLayout

            val layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            )

            containerLayout?.addView(
                stickyLayoutBinding.root,
                layoutParams
            )

            stickyLayoutBinding.root.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    stickyLayoutBinding.root.viewTreeObserver?.removeOnGlobalLayoutListener(this)

                    val height = stickyLayoutBinding.root?.measuredHeight
                    binding.root.setPadding(0, 0, 0, height ?: 0)
                }

            })

            stickyLayoutBinding.buttonOk.setOnClickListener {
                sendCallback()
                dismiss()
            }

            stickyLayoutBinding.tvDialogOrderTerms.setOnClickListener {
                showDialogTncEipoOrderBottom(childFragmentManager)
            }
        }

        return dialog
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            val stockCode = GET_4_CHAR_STOCK_CODE(model.stockCode?:"")
            val url = if (!model.logoLink.isNullOrEmpty()) prefManager.urlIcon + model.logoLink else prefManager.urlIcon + stockCode
            Glide.with(requireContext())
                .load(url)
                .circleCrop()
                .placeholder(R.drawable.bg_circle)
                .error(R.drawable.bg_circle)
                .into(imageLogo)

            tvStockCode.text = model.stockCode
            tvCompanyName.text = model.stockName
            tvPriceVal.text = model.price
            tvLot.text = model.quantity
            tvDialogOrderTotal.text = model.total
        }
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.buttonDialogOrderClose.setOnClickListener {
            dismiss()
        }
    }

    private fun sendCallback() {
        val result = Bundle()
        result.putString(NavKeys.KEY_FM_EIPO_ORDER, NavKeys.CONST_RES_EIPO_ORDER)
        result.putString(NavKeys.CONST_RES_EIPO_ORDER, "RESULT_OK")
        parentFragmentManager.setFragmentResult(NavKeys.KEY_FM_EIPO_ORDER, result)
    }
}