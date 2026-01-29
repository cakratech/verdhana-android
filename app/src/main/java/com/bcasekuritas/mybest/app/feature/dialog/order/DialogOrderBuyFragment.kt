package com.bcasekuritas.mybest.app.feature.dialog.order


import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.data.layout.UIDialogOrderConfirmationModel
import com.bcasekuritas.mybest.app.feature.dialog.order.viewmodel.DialogOrderBuyViewModel
import com.bcasekuritas.mybest.databinding.CustomButtonDialogBinding
import com.bcasekuritas.mybest.databinding.DialogOrderBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.oprConvert
import com.bcasekuritas.mybest.ext.common.orderType
import com.bcasekuritas.mybest.ext.common.timeInForce
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.converter.GET_4_CHAR_STOCK_CODE
import com.bcasekuritas.mybest.ext.converter.LOT_DIVBIDED_BY_100
import com.bcasekuritas.mybest.ext.converter.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.mybest.ext.view.visible
import java.text.SimpleDateFormat

@FragmentScoped
@AndroidEntryPoint
class DialogOrderBuyFragment(private val model: UIDialogOrderConfirmationModel) :
    BaseBottomSheet<DialogOrderBinding>() {

    @FragmentScoped
    override val binding: DialogOrderBinding by autoCleaned {
        (DialogOrderBinding.inflate(layoutInflater))
    }

    lateinit var mViewModel: DialogOrderBuyViewModel
    lateinit var stickyLayoutBinding: CustomButtonDialogBinding

    private var buttonLayoutParams: ConstraintLayout.LayoutParams? = null
    private var collapsedMargin = 0
    private var buttonHeight = 0
    private var expandedHeight = 0

    companion object {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(requireActivity()).get(DialogOrderBuyViewModel::class.java)
        stickyLayoutBinding = CustomButtonDialogBinding.inflate(
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
                stickyLayoutBinding?.root,
                layoutParams
            )

            stickyLayoutBinding?.root?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    stickyLayoutBinding?.root?.viewTreeObserver?.removeOnGlobalLayoutListener(this)

                    val height = stickyLayoutBinding?.root?.measuredHeight
                    binding.root.setPadding(0, 0, 0, height ?: 0)
                }

            })

            stickyLayoutBinding?.buttonOk?.setOnClickListener {
                sendCallback()
                dismiss()
            }
        }

        return dialog
    }


    override fun initAPI() {
        super.initAPI()
        mViewModel.getMarketSession(prefManager.userId)
    }

    override fun setupObserver() {
        super.setupObserver()
        mViewModel.getMarketSessionResult.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                val nowSession = data.marketSessionName
                val isOpen = nowSession.contains("SESS_1") || nowSession.contains("SESS_2") || nowSession.contains("SESS_PRE_CLOSE")

                binding.clDialogOrderInfo.visibility = if (isOpen) View.GONE else View.VISIBLE
            }
        }
    }

    override fun setupComponent() {
        super.setupComponent()

        binding.apply {
            tvTitleDialogOrderConfirm.text =
                if (model.isAmend) "Confirm Amend Order" else "Confirm Buy Order"
            tvStockCode.text = model.stockCode
            tvCompanyName.text = model.companyName ?: ""
            tvDialogOrderPrice.text = model.buyPrice ?: ""
            tvDialogOrderLot.text = model.lot?.formatPriceWithoutDecimal() ?: ""
            stickyLayoutBinding.tvDialogOrderTotal.text = "Rp" + model.total
            tvDialogProceedAmount.text = model.proceedAmount
            tvDialogDialogCommissionFee.text = model.commissionFee

            tvNotation.visibility = if (model.notation.isNotEmpty()) View.VISIBLE else View.GONE
            tvNotation.text = model.notation
            tvInfoSpecialNotesAcceleration.visibility =
                if (model.idxBoard.isNotEmpty()) View.VISIBLE else View.GONE
            tvInfoSpecialNotesAcceleration.text = model.idxBoard

            tvDialogOrderExpiry.text = timeInForce(model.orderExpiry.toString())
            tvDialogOrderType.text = if (!model.isAutoOrder) orderType(model.orderType.toString()) else "Automatic Order"

            if (model.orderPeriod != null && model.orderExpiry.equals("2")) {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy").format(model.orderPeriod)
                tvDialogOrderExpiry.text =
                    timeInForce(model.orderExpiry.toString()) + " ($dateFormat)"
            }

            // for auto order
            groupAutoOrder.visibility = if (model.isAutoOrder) View.VISIBLE else View.GONE
            val opr = oprConvert(model.autoOrderOpr ?: 2)
            tvTitleDialogOrderComparePriceAutoOrderVal.text =
                "Last Price $opr ${model.autoOrderComparePrice}"

        }

        val tvImageError = model.stockCode?.replace("-", "")?.substring(0,2)
        val stockCode = GET_4_CHAR_STOCK_CODE(model.stockCode?:"")
        Glide.with(requireContext())
            .load(prefManager.urlIcon+stockCode)
            .circleCrop()
            .placeholder(R.drawable.bg_circle)
            .error(R.drawable.bg_circle)
            .into(binding.imageLogo)

        if (model.isAmendGtc) {
            stickyLayoutBinding.tvInfoFooter.visibility = View.VISIBLE
        }

        setupSliceOrderComponent()
        setupAdvOrderComponent()
        setupMarketOrder()
    }

    private fun setupMarketOrder() {
        if (model.isMarketOrder) {
            binding.apply {
                tvInfoMarketOrder.visibility = View.VISIBLE
                stickyLayoutBinding.tvDialogOrderTotal.text  = "Rp0"
                tvDialogMarketOrderType.text = model.marketOrderType

                groupMarketOrder.visibility = View.VISIBLE

                tvDialogOrderPrice.visibility = View.GONE
                tvTitleDialogOrderPrice.visibility = View.GONE
                tvDialogProceedAmount.visibility = View.GONE
                tvTitleDialogProceedAmount.visibility = View.GONE
                tvDialogDialogCommissionFee.visibility = View.GONE
                tvTitleDialogCommissionFee.visibility = View.GONE
            }
        }
    }

    private fun setupSliceOrderComponent() {
        binding.apply {
            if (model.isSliceOrder) {
                clDialogOrderSlice.visible(true)
            } else {
                clDialogOrderSlice.visibility = View.GONE
            }

            if (model.slicingType == 0) {
                tvTitleDialogOrderSliceOrderEndTimee.visibility = View.GONE
                tvDialogOrderSliceOrderEndTimee.visibility = View.GONE
            } else {
                tvTitleDialogOrderSliceOrderEndTimee.visible(true)
                tvDialogOrderSliceOrderEndTimee.visible(true)

                if (model.endTimeSliceOrder != 0L) {
                    tvDialogOrderSliceOrderEndTimee.text =
                        DateUtils.toStringDate(model.endTimeSliceOrder!!, "dd/MM/yyyy, HH:mm")
                }
            }

            when {
                model.splitNumber != 0 -> {
                    tvTitleDialogOrderSliceOrderBlockSize.visibility = View.GONE
                    tvDialogOrderSliceOrderBlockSize.visibility = View.GONE

                    tvTitleDialogOrderSliceOrderNoOfSplit.visibility = View.VISIBLE
                    tvDialogOrderSliceOrderNoOfSplit.visibility = View.VISIBLE
                    tvDialogOrderSliceOrderNoOfSplit.text = model.splitNumber.toString()
                }

                model.blockSize != 0 -> {
                    tvTitleDialogOrderSliceOrderNoOfSplit.visibility = View.GONE
                    tvDialogOrderSliceOrderNoOfSplit.visibility = View.GONE

                    tvTitleDialogOrderSliceOrderBlockSize.visibility = View.VISIBLE
                    tvDialogOrderSliceOrderBlockSize.visibility = View.VISIBLE
                    tvDialogOrderSliceOrderBlockSize.text = model.blockSize?.toString()
                }
            }
            tvDialogOrderSliceSlicingType.text =
                if (model.slicingType != 0) "Excecution Time" else "At Once"
        }
    }

    private fun setupAdvOrderComponent() {
        binding.apply {
            if (model.isAdvOrder) {
                clDialogOrderTakeProfit.visibility =
                    if (model.takeProfitSellPrice.equals("0")) View.GONE else View.VISIBLE
                clDialogOrderStopLoss.visibility =
                    if (model.stopLossSellPrice.equals("0")) View.GONE else View.VISIBLE

                /** Take Profit*/
                tvDialogOrderTakeProfitCompare.text = model.takeProfitCompare
                tvDialogOrderTakeProfitTriggerPrice.text =
                    "Rp${model.takeProfitTriggerPrice ?: "0"}"
                tvDialogOrderTakeProfitSellPrice.text = "Rp${model.takeProfitSellPrice ?: "0"}"
                tvDialogOrderTakeProfitSellLot.text =
                    model.takeProfirSellLot?.LOT_DIVBIDED_BY_100()?.formatPriceWithoutDecimal()
                        ?: "0"

                /** Stop Loss*/
                tvDialogOrderStopLossCompare.text = model.stopLossCompare
                tvDialogOrderStopLossTriggerPrice.text = "Rp${model.stopLossTriggerPrice ?: "0"}"
                tvDialogOrderStopLossSellPrice.text = "Rp${model.stopLossSellPrice ?: "0"}"
                tvDialogOrderStopLossSellLot.text =
                    model.stopLossSellLot?.LOT_DIVBIDED_BY_100()?.formatPriceWithoutDecimal() ?: "0"
            } else {
                clDialogOrderTakeProfit.visibility = View.GONE
                clDialogOrderStopLoss.visibility = View.GONE
            }
        }
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.buttonOk.setOnClickListener {
            sendCallback()
            dismiss()
        }

        binding.buttonDialogOrderClose.setOnClickListener {
            dismiss()
        }
    }

    private fun sendCallback() {
        val result = Bundle()
        result.putString(NavKeys.KEY_FM_ORDER, NavKeys.CONST_RES_ORDER_BUY)
        result.putString(NavKeys.CONST_RES_ORDER_BUY, "RESULT_OK")
        parentFragmentManager.setFragmentResult(NavKeys.KEY_FM_ORDER, result)
    }

}