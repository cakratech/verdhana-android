package com.bcasekuritas.mybest.app.feature.dialog.bottom.fastorder

import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.databinding.DialogFastOrderSettingBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned

@FragmentScoped
@AndroidEntryPoint
class DialogFastOrderSetting(
    private val isCheckShowOrder: Boolean,
    private val isCheckOrderCount: Boolean,
    private val isCheckPreventOrder: Boolean
) :
    BaseBottomSheet<DialogFastOrderSettingBinding>() {

    private var showOrder = isCheckShowOrder
    private var orderCount = isCheckOrderCount
    private var preventOrder = isCheckPreventOrder

    private var saveBtnClickListener: ((Boolean, Boolean, Boolean) -> Unit)? = null

    @FragmentScoped
    override val binding: DialogFastOrderSettingBinding by autoCleaned{(
            DialogFastOrderSettingBinding.inflate(layoutInflater)
            )}

    override fun setupComponent() {
        super.setupComponent()


        binding.swtchShowOrder.isChecked = showOrder

        binding.swtchOrderCount.isChecked = orderCount

        binding.swtchPreventOrder.isChecked = preventOrder

    }
    override fun initOnClick() {
        super.initOnClick()

        binding.swtchShowOrder.setOnCheckedChangeListener { _, isChecked ->
            showOrder = isChecked
        }

        binding.swtchOrderCount.setOnCheckedChangeListener { _, isChecked ->
            orderCount = isChecked
        }

        binding.swtchPreventOrder.setOnCheckedChangeListener { _, isChecked ->
            preventOrder = isChecked
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()

        }

        binding.btnSave.setOnClickListener {
            dismiss()
                saveBtnClickListener?.invoke(showOrder, orderCount, preventOrder)

        }
    }
    fun setSaveButtonClickListener(listener: (Boolean, Boolean, Boolean) -> Unit) {
        saveBtnClickListener = listener
    }

}