package com.bcasekuritas.mybest.app.feature.dialog.bottom

import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.databinding.DialogCreateRenameCategoryBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned

@FragmentScoped
@AndroidEntryPoint
class DialogCreateRenameCategory (
    val listCategory: List<String>,
    val isRename: Boolean,
    val oldCategoryName: String
) : BaseBottomSheet<DialogCreateRenameCategoryBinding>() {

    private var okButtonClickListener: ((String) -> Unit)? = null

    @FragmentScoped
    override val binding: DialogCreateRenameCategoryBinding by autoCleaned {
        (DialogCreateRenameCategoryBinding.inflate(
            layoutInflater
        ))
    }

    override fun setupComponent() {
        super.setupComponent()
        if (isRename) {
            binding.tvTitle.text = "Rename Watchlist"
            binding.edtCategory.setText(oldCategoryName)
            val textCount = binding.edtCategory.getText().length
            binding.tvCount.text =  "$textCount/20"
        }
        binding.edtCategory.requestFocus()

    }

    private fun checkInput(wlCode: String): Boolean {
        val listCategory = listCategory.map { it.lowercase() }
        return listCategory.contains(wlCode.lowercase())
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnOk.setOnClickListener {
            val wlCode = binding.edtCategory.getText().trim()
            if (wlCode.length != 0) {
                if (checkInput(wlCode)) {
                    binding.edtCategory.showError("Name can't be the same with existing watchlist.")
                    return@setOnClickListener
                }
                if (wlCode.lowercase() == "all" || wlCode.lowercase() == "all watchlist" || wlCode.lowercase() == "allwatchlist" || wlCode.lowercase() == "portfolio") {
                    binding.edtCategory.showError("Name can't be the same with existing watchlist.")
                    return@setOnClickListener
                }
                if (!checkInput(wlCode)) {
                    okButtonClickListener?.invoke(wlCode)
                    dismiss()
                }
            } else {
                binding.edtCategory.showError("Name can’t be empty")
            }

        }
    }

    override fun setupListener() {
        super.setupListener()
        val textWatcherCategory = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // Do something before text changes
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                binding.btnOk.isEnabled = binding.edtCategory.getText().isNotEmpty()
                val totalLength = binding.edtCategory.getText().length
                binding.tvCount.text = "$totalLength/20"
                if (totalLength == 20) {
                    binding.tvCount.setTextColor(ContextCompat.getColor(requireContext(), R.color.textDown))
                } else {
                    binding.tvCount.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        binding.edtCategory.setTextWatcher(textWatcherCategory)
    }

    fun setOkButtonClickListener(listener: (String) -> Unit) {
        okButtonClickListener = listener
    }
}