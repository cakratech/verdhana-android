package com.bcasekuritas.mybest.app.feature.dialog.center

import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseDialogFragment
import com.bcasekuritas.mybest.databinding.CustomNumberPickerBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class DialogMonthPicker(
    private val title: String,
    private val currMonth: Int // Pass the numeric value of the current month (1-12)
) : BaseDialogFragment<CustomNumberPickerBinding>() {

    private var okButtonClickListener: ((Int) -> Unit)? = null
    private var pickedMonth = currMonth // Initialize with the current month

    @FragmentScoped
    override val binding: CustomNumberPickerBinding by autoCleaned { CustomNumberPickerBinding.inflate(layoutInflater) }

    private val monthNames = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    override fun setupComponent() {
        super.setupComponent()

        binding.tvTitle.text = title

        binding.npNumber.apply {
            minValue = 1
            maxValue = monthNames.size
            value = currMonth
            displayedValues = monthNames

            setOnValueChangedListener { _, _, newVal ->
                pickedMonth = newVal // Store the numeric value of the selected month
            }
        }
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnOk.setOnClickListener {
            okButtonClickListener?.invoke(pickedMonth)
            dismiss()
        }
    }

    fun setOkButtonClickListener(listener: (Int) -> Unit) {
        okButtonClickListener = listener
    }
}
