package com.bcasekuritas.mybest.app.feature.dialog.center

import androidx.core.view.isGone
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseDialogFragment
import com.bcasekuritas.mybest.app.data.layout.UIDialogModel
import com.bcasekuritas.mybest.databinding.DialogConfirmationBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class DialogConfirmationCenter (
    private val isSelection: Boolean,
    private val uiDialogModel: UIDialogModel
): BaseDialogFragment<DialogConfirmationBinding>() {

    private var okButtonClickListener: ((Boolean) -> Unit)? = null

    @FragmentScoped
    override val binding: DialogConfirmationBinding by autoCleaned { (DialogConfirmationBinding.inflate(layoutInflater)) }


    override fun setupComponent() {
        super.setupComponent()

        if (!isSelection){
            binding.btnCancel.isGone = true
            binding.dividerButton.isGone = true
        }

        binding.tvTitle.text = uiDialogModel.titleStr
        binding.btnOk.text = uiDialogModel.btnPositiveStr
        binding.btnCancel.text = uiDialogModel.btnNegativeStr
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnOk.setOnClickListener {
            okButtonClickListener?.invoke(true)
            dismiss()
        }
    }

    fun setOkButtonClickListener(listener: (Boolean) -> Unit) {
        okButtonClickListener = listener
    }
}