package com.bcasekuritas.mybest.app.feature.dialog.bottom

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.databinding.DialogWithdrawGtcOrderBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl

@FragmentScoped
@AndroidEntryPoint
class DialogWithdrawGtcOrder(private val fromLayout: String) : BaseBottomSheet<DialogWithdrawGtcOrderBinding>(), ShowDropDown by ShowDropDownImpl() {

    @FragmentScoped
    override val binding: DialogWithdrawGtcOrderBinding by autoCleaned { (DialogWithdrawGtcOrderBinding.inflate(layoutInflater)) }

    private var listSelection = arrayListOf("Withdraw For Today", "Withdraw Order")
    private var selection = 0

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            btnWithdraw.setOnClickListener {
                sendCallback()
                dismiss()
            }

            btnCancel.setOnClickListener {
                dismiss()
            }

            btnClose.setOnClickListener {
                dismiss()
            }

            boxSelection.setOnClickListener {
                showSimpleDropDownWidth80(
                    requireContext(),
                    listSelection,
                    binding.viewDropdownSelection
                ){ index, value->
                    boxSelection.text = value
                    selection = index
                }
            }

        }
    }


    private fun sendCallback(){
        val result = Bundle()
        result.putString(fromLayout, NavKeys.CONST_RES_WITHDRAW_ORDER_GTC_CONFIRM)
        result.putString(NavKeys.CONST_RES_WITHDRAW_ORDER_GTC_CONFIRM, "RESULT_OK")
        result.putBoolean("confirm", true)
        result.putInt("selection", selection)
        parentFragmentManager.setFragmentResult(fromLayout, result)

    }

}