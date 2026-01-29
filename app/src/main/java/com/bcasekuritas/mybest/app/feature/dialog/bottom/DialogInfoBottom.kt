package com.bcasekuritas.mybest.app.feature.dialog.bottom

import androidx.core.view.isGone
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.data.layout.UIDialogModel
import com.bcasekuritas.mybest.databinding.DialogInfoBottomBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class DialogInfoBottom (
    private val uiDialogModel: UIDialogModel
) : BaseBottomSheet<DialogInfoBottomBinding>() {

    @FragmentScoped
    override val binding: DialogInfoBottomBinding by autoCleaned { (DialogInfoBottomBinding.inflate(layoutInflater)) }

    private var okButtonClickListener: ((Boolean) -> Unit)? = null

    override fun setupComponent() {
        super.setupComponent()

        // if title is not null use it, when null use titleStr. when null again the default is ""

        if (uiDialogModel.icon != null && uiDialogModel.descriptionStr == null){
            binding.ivIcon.setImageResource(uiDialogModel.icon!!)
            binding.tvTitleCenter.text = uiDialogModel.title?.let { getString(it) } ?: uiDialogModel.titleStr ?: ""

            binding.ivIcon.isGone = false
            binding.tvTitleCenter.isGone = false

            binding.tvTitle.isGone = true
            binding.tvDescription.isGone = true
            binding.btnClose.isGone = true
        }

        binding.tvTitle.text = uiDialogModel.title?.let { getString(it) } ?: uiDialogModel.titleStr ?: ""
        binding.tvDescription.text = uiDialogModel.description?.let { getString(it) } ?: uiDialogModel.descriptionStr ?: ""
        binding.buttonOk.text = uiDialogModel.btnPositiveStr
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.buttonOk.setOnClickListener {
            okButtonClickListener?.invoke(true)
            dismiss()
        }
    }

    fun setOkButtonClickListener(listener: (Boolean) -> Unit) {
        okButtonClickListener = listener
    }
}