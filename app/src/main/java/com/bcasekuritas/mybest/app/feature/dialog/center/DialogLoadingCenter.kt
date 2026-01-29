package com.bcasekuritas.mybest.app.feature.dialog.center

import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseDialogFragment
import com.bcasekuritas.mybest.app.data.layout.UIDialogModel
import com.bcasekuritas.mybest.databinding.DialogLoadingBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned

@FragmentScoped
@AndroidEntryPoint
class DialogLoadingCenter(
    private val uiDialogModel: UIDialogModel
) : BaseDialogFragment<DialogLoadingBinding>() {

    @FragmentScoped
    override val binding: DialogLoadingBinding by autoCleaned { (DialogLoadingBinding.inflate(layoutInflater)) }


    override fun setupComponent() {
        super.setupComponent()

        // if title is not null use it, when null use titleStr. when null again the default is ""
        binding.title.text = uiDialogModel.title?.let { getString(it) } ?: uiDialogModel.titleStr ?: ""
        binding.description.text = uiDialogModel.description?.let { getString(it) } ?: uiDialogModel.descriptionStr ?: ""
    }
}