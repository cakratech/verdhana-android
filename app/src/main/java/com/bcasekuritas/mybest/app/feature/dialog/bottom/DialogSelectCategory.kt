package com.bcasekuritas.mybest.app.feature.dialog.bottom

import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.domain.dto.request.WatchListCategory
import com.bcasekuritas.mybest.app.feature.dialog.adapter.DialogSelectCategoryAdapter
import com.bcasekuritas.mybest.databinding.DialogSelectWatchlistBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.listener.OnClickStr

@FragmentScoped
@AndroidEntryPoint
class DialogSelectCategory (
    private val listCategory: List<WatchListCategory>,
) : BaseBottomSheet<DialogSelectWatchlistBinding>(), OnClickStr{

    private var okButtonClickListener: ((String, Boolean) -> Unit)? = null

    @FragmentScoped
    override val binding: DialogSelectWatchlistBinding by autoCleaned {
        (DialogSelectWatchlistBinding.inflate(
            layoutInflater
        ))
    }

    private val dialogSelectCategoryAdapter: DialogSelectCategoryAdapter by autoCleaned { DialogSelectCategoryAdapter(this) }
    private var categoryName = ""

    override fun setupAdapter() {
        super.setupAdapter()

        binding.rcvWatchlistCategory.setHasFixedSize(true)
        binding.rcvWatchlistCategory.adapter = dialogSelectCategoryAdapter

    }

    override fun setupComponent() {
        super.setupComponent()

//        listCategory.find { it.category == currentCategory }?.isChecked = true

        dialogSelectCategoryAdapter.setData(listCategory)
        binding.btnOk.text = "Create New Watchlist"
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnOk.setOnClickListener {
            okButtonClickListener?.invoke(categoryName, true)
            dismiss()
        }
    }

    fun setOkButtonClickListener(listener: (String, Boolean) -> Unit) {
        okButtonClickListener = listener
    }

    override fun onClickStr(value: String?) {
        categoryName = value ?: ""
        okButtonClickListener?.invoke(categoryName, false)
        dismiss()
    }
}