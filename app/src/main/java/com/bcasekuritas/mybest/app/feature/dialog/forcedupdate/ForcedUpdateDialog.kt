package com.bcasekuritas.mybest.app.feature.dialog.forcedupdate

import android.view.View
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.databinding.DialogForcedUpdateBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned

class ForcedUpdateDialog(val isForceUpdate: Boolean): BaseBottomSheet<DialogForcedUpdateBinding>() {

    override val binding: DialogForcedUpdateBinding by autoCleaned { (DialogForcedUpdateBinding.inflate(layoutInflater)) }
    private var onClickUpdate: ((Boolean) -> Unit)? = null

    fun setOnClickUpdated(listener: (Boolean) -> Unit) {
        onClickUpdate = listener
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            btnClose.visibility = if (isForceUpdate) View.GONE else View.VISIBLE
            btnNotNow.visibility = if (isForceUpdate) View.GONE else View.VISIBLE
            tvTitle.text = if (isForceUpdate) "Update is Required" else "New Update Available"
            tvDesc.text = if (isForceUpdate) "Please update to the latest version to continue using BCA Sekuritas app." else "Please update to the latest version for better experience."
        }
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            btnClose.visibility = if (isForceUpdate) View.GONE else View.VISIBLE
            btnClose.setOnClickListener {
                onClickUpdate?.invoke(false)
                dismiss()
            }

            btnNotNow.setOnClickListener {
                onClickUpdate?.invoke(false)
                dismiss()
            }

            btnUpgrade.setOnClickListener {
                onClickUpdate?.invoke(true)
            }
        }
    }


}