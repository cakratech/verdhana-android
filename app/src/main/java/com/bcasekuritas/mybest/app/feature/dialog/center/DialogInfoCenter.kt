package com.bcasekuritas.mybest.app.feature.dialog.center

import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseDialogFragment
import com.bcasekuritas.mybest.app.data.layout.UIDialogModel
import com.bcasekuritas.mybest.databinding.DialogInfoBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned

@FragmentScoped
@AndroidEntryPoint
class DialogInfoCenter(
    private val uiDialogModel: UIDialogModel
) : BaseDialogFragment<DialogInfoBinding>() {

    private var okButtonClickListener: ((Boolean) -> Unit)? = null

    @FragmentScoped
    override val binding: DialogInfoBinding by autoCleaned { (DialogInfoBinding.inflate(layoutInflater)) }


    override fun setupComponent() {
        super.setupComponent()

        // if title is not null use it, when null use titleStr. when null again the default is ""
        binding.title.text = uiDialogModel.title?.let { getString(it) } ?: uiDialogModel.titleStr ?: ""
        binding.description.text = uiDialogModel.description?.let { getString(it) } ?: uiDialogModel.descriptionStr ?: ""
        binding.buttonOk.text = uiDialogModel.btnPositive?.let { getString(it) }
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.buttonClose.setOnClickListener {
            dismiss()
        }

        binding.buttonOk.setOnClickListener {
//            if (!reqTargetCode.isNullOrBlank() && !reqTargetCode.isNullOrEmpty()) {
////                sendCallback()
//            }
            okButtonClickListener?.invoke(true)
            dismiss()
        }
    }

    fun setOkButtonClickListener(listener: (Boolean) -> Unit) {
        okButtonClickListener = listener
    }
}