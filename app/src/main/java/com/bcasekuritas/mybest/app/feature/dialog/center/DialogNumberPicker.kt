package com.bcasekuritas.mybest.app.feature.dialog.center
import androidx.core.view.isGone
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseDialogFragment
import com.bcasekuritas.mybest.app.data.layout.UIDialogModel
import com.bcasekuritas.mybest.databinding.CustomNumberPickerBinding
import com.bcasekuritas.mybest.databinding.DialogConfirmationBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class DialogNumberPicker (
    private val title: String,
    private val minVal: Int,
    private val maxVal: Int,
    private val currVal: Int,
): BaseDialogFragment<CustomNumberPickerBinding>() {

    private var okButtonClickListener: ((Int) -> Unit)? = null
    private var pickedValue = 0

    @FragmentScoped
    override val binding: CustomNumberPickerBinding by autoCleaned { (CustomNumberPickerBinding.inflate(layoutInflater)) }


    override fun setupComponent() {
        super.setupComponent()

        binding.tvTitle.text = title
        pickedValue = currVal

        binding.npNumber.apply {
            minValue = minVal
            maxValue = maxVal
            value = currVal
            wrapSelectorWheel = false

            setOnValueChangedListener { _, _, newVal ->
                pickedValue = newVal
            }
        }
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnOk.setOnClickListener {
            okButtonClickListener?.invoke(pickedValue)
            dismiss()
        }
    }

    fun setOkButtonClickListener(listener: (Int) -> Unit) {
        okButtonClickListener = listener
    }
}