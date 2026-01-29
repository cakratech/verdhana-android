package com.bcasekuritas.mybest.app.feature.dialog.bottom.fastorder

import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.domain.dto.request.SendOrderReq
import com.bcasekuritas.mybest.databinding.DialogOrderFastOrderBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned

@FragmentScoped
@AndroidEntryPoint
class DialogOrderFastOrder(private val data: SendOrderReq) :
    BaseBottomSheet<DialogOrderFastOrderBinding>() {

    private var addOrderBtnClickListener: ((SendOrderReq) -> Unit)? = null

    @FragmentScoped
    override val binding: DialogOrderFastOrderBinding by autoCleaned{(
            DialogOrderFastOrderBinding.inflate(layoutInflater)
            )}
    override fun initOnClick() {
        super.initOnClick()

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()

        }

        binding.btnAddOrder.setOnClickListener {
            dismiss()
            addOrderBtnClickListener?.invoke(data)

        }
    }
    fun setAddOrderBtnClickListener(listener: (SendOrderReq) -> Unit) {
        addOrderBtnClickListener = listener
    }

}