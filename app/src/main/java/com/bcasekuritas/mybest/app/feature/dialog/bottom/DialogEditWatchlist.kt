package com.bcasekuritas.mybest.app.feature.dialog.bottom

import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.databinding.DialogEditWatchlistBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.listener.OnClickStr

@FragmentScoped
@AndroidEntryPoint
class DialogEditWatchlist (
) : BaseBottomSheet<DialogEditWatchlistBinding>(), OnClickStr {

    private var okButtonClickListener: ((Boolean) -> Unit)? = null

    @FragmentScoped
    override val binding: DialogEditWatchlistBinding by autoCleaned {
        (DialogEditWatchlistBinding.inflate(
            layoutInflater
        ))
    }

    private var categoryName = ""

    override fun initOnClick() {
        super.initOnClick()

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.tvRename.setOnClickListener {
            okButtonClickListener?.invoke(false)
            dismiss()
        }

        binding.tvDelete.setOnClickListener {
            okButtonClickListener?.invoke(true)
            dismiss()
        }
    }

    fun setOkButtonClickListener(listener: (Boolean) -> Unit) {
        okButtonClickListener = listener
    }

    override fun onClickStr(value: String?) {
        categoryName = value ?: ""
    }
}