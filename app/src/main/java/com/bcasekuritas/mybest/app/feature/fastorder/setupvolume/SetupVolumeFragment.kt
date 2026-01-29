package com.bcasekuritas.mybest.app.feature.fastorder.setupvolume

import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withvm.BaseDialogFullFragment
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.databinding.FragmentSetupVolumeBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.converter.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.converter.removeSeparator
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarInterface
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class SetupVolumeFragment(private val maxLimit: String, private val maxCash: String, private val stockCode: String, private val lastPrice: Double) :
    BaseDialogFullFragment<FragmentSetupVolumeBinding, SetupVolumeViewModel>(),  ShowSnackBarInterface by ShowSnackBarImpl() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmPin
    override val viewModel: SetupVolumeViewModel by viewModels()
    override val binding: FragmentSetupVolumeBinding by autoCleaned {
        (FragmentSetupVolumeBinding.inflate(
            layoutInflater
        ))
    }

    private var onConfirm: ((String) -> Unit)? = null

    private var stockCodes = stockCode
    private var maxOrderResCount = 0
    private var stringBuilderVol = StringBuilder()

    override fun setupArguments() {
        super.setupArguments()

        arguments?.let {

            stockCodes = it.getString(Args.EXTRA_PARAM_STR_ONE).toString()
//            lastPrice = it.getDouble(Args.EXTRA_PARAM_DOUBLE_ONE)
        }

    }

    override fun initAPI() {
        super.initAPI()

        viewModel.getMaxOrder(
            prefManager.userId,
            prefManager.accno,
            "B",
            prefManager.sessionId,
            stockCodes,
            lastPrice,
            "C",
            "RG")

        maxOrderResCount++
    }

    override fun setupListener() {
        super.setupListener()

        val textWatcherAmendPrice = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // Do something before text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do something when text changes
            }

            override fun afterTextChanged(s: Editable?) {
                val volume = s.toString().removeSeparator()
                if (volume.toInt() > 50000){
                    binding.adjusterVolume.setEdt("50000".formatPriceWithoutDecimal())
                    stringBuilderVol.clear()
                    stringBuilderVol.append(50000)

                    showSnackBarTop(
                        requireContext(),
                        binding.root,
                        "error",
                        R.drawable.ic_error,
                        "Can't buy or sell more than 50,000 lot",
                        "", requireActivity(), ""
                    )
                }
            }
        }
        binding.adjusterVolume.setTextWatcher(textWatcherAmendPrice)
    }

    override fun setupComponent() {
        super.setupComponent()

        binding.tvStockCode.text = stockCodes
        binding.tvLastPrice.text = "Last Rp ${lastPrice.formatPriceWithoutDecimal()}"

        binding.lyToolbar.tvMaxCash.text = "Max Cash $maxCash Lot"
        binding.lyToolbar.tvMaxLimit.text = "Max Limit $maxLimit Lot"


        Glide.with(requireContext())
            .load(prefManager.urlIcon + stockCodes)
            .circleCrop()
            .placeholder(R.drawable.bg_circle)
            .error(R.drawable.bg_circle)
            .into(binding.ivLogo)
    }

    override fun setupObserver() {
        super.setupObserver()



        viewModel.getMaxOrderByStockResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {
                    when (it.data?.status) {
                        0 -> {
                            val maxLot = it.data.maxLot
                            if (maxOrderResCount == 1){
//                                binding.tvMaxCashVal.text = maxLot.formatPriceWithoutDecimal()
                                maxOrderResCount++

                                viewModel.getMaxOrder(
                                    prefManager.userId,
                                    prefManager.accno,
                                    "B",
                                    prefManager.sessionId,
                                    stockCodes,
                                    lastPrice,
                                    "L",
                                    "RG")
                            } else if(maxOrderResCount == 2){
//                                binding.tvMaxLimitVal.text = maxLot.formatPriceWithoutDecimal()
                                maxOrderResCount = 0
                            }
                        }
                    }

                }

                is Resource.Failure -> {
                    Toast.makeText(requireContext(), it.failureData.message, Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {}
            }
        }
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.lyToolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener {
            dismiss()
        }

        binding.inputLayout.btn00.setOnClickListener {
            appendVolume("00")
        }
        binding.inputLayout.btn0.setOnClickListener {
            appendVolume("0")
        }
        binding.inputLayout.btn1.setOnClickListener {
            appendVolume("1")
        }
        binding.inputLayout.btn2.setOnClickListener {
            appendVolume("2")
        }
        binding.inputLayout.btn3.setOnClickListener {
            appendVolume("3")
        }
        binding.inputLayout.btn4.setOnClickListener {
            appendVolume("4")
        }
        binding.inputLayout.btn5.setOnClickListener {
            appendVolume("5")
        }
        binding.inputLayout.btn6.setOnClickListener {
            appendVolume("6")
        }
        binding.inputLayout.btn7.setOnClickListener {
            appendVolume("7")
        }
        binding.inputLayout.btn8.setOnClickListener {
            appendVolume("8")
        }
        binding.inputLayout.btn9.setOnClickListener {
            appendVolume("9")
        }

        binding.inputLayout.btnBackspace.setOnClickListener {
            if (stringBuilderVol.isNotEmpty()) {
                stringBuilderVol.deleteCharAt(stringBuilderVol.length - 1)

                if (stringBuilderVol.isNotEmpty()) {
                    binding.adjusterVolume.setEdt(stringBuilderVol.toString())
                } else {
                    binding.adjusterVolume.setEdt("")
                }
            }
        }

        binding.inputLayout.btnAdd10.setOnClickListener {
            if (stringBuilderVol.isNotEmpty()){
                val value = stringBuilderVol.toString().removeSeparator().toInt()
                val inputVol = value.plus(10)

                stringBuilderVol.clear()
                stringBuilderVol.append(inputVol.toString())

                binding.adjusterVolume.setEdt(stringBuilderVol.toString())
            } else {
                stringBuilderVol.append(10)
                binding.adjusterVolume.setEdt(stringBuilderVol.toString())
            }

        }
        binding.inputLayout.btnAdd50.setOnClickListener {
            if (stringBuilderVol.isNotEmpty()) {
                val value = stringBuilderVol.toString().removeSeparator().toInt()
                val inputVol = value.plus(50)

                stringBuilderVol.clear()
                stringBuilderVol.append(inputVol.toString())

                binding.adjusterVolume.setEdt(stringBuilderVol.toString())
            } else {
                stringBuilderVol.append(50)
                binding.adjusterVolume.setEdt(stringBuilderVol.toString())
            }
        }

        binding.inputLayout.btnAdd100.setOnClickListener {
            if (stringBuilderVol.isNotEmpty()) {
                val value = stringBuilderVol.toString().removeSeparator().toInt()
                val inputVol = value.plus(100)

                stringBuilderVol.clear()
                stringBuilderVol.append(inputVol.toString())

                binding.adjusterVolume.setEdt(stringBuilderVol.toString())
            } else {
                stringBuilderVol.append(100)
                binding.adjusterVolume.setEdt(stringBuilderVol.toString())
            }
        }

        binding.inputLayout.btnAddHalf.setOnClickListener {
            if (stringBuilderVol.isNotEmpty()){
                val value = stringBuilderVol.toString().removeSeparator().toInt()
                val valueDiv = value.div(2)

                stringBuilderVol.clear()
                stringBuilderVol.append(valueDiv.toString())

                binding.adjusterVolume.setEdt(stringBuilderVol.toString())
            }
        }

        binding.inputLayout.btnAddThird.setOnClickListener {
            if (stringBuilderVol.isNotEmpty()) {
                val value = stringBuilderVol.toString().removeSeparator().toInt()
                val valueDiv = value.div(3)

                stringBuilderVol.clear()
                stringBuilderVol.append(valueDiv.toString())

                binding.adjusterVolume.setEdt(stringBuilderVol.toString())
            }
        }

        binding.inputLayout.btnAddQuarter.setOnClickListener {
            if (stringBuilderVol.isNotEmpty()) {
                val value = stringBuilderVol.toString().removeSeparator().toInt()
                val valueDiv = value.div(4)

                stringBuilderVol.clear()
                stringBuilderVol.append(valueDiv.toString())

                binding.adjusterVolume.setEdt(stringBuilderVol.toString())
            }
        }

        binding.inputLayout.btnAddFifth.setOnClickListener {
            if (stringBuilderVol.isNotEmpty()) {
                val value = stringBuilderVol.toString().removeSeparator().toInt()
                val valueDiv = value.div(5)

                stringBuilderVol.clear()
                stringBuilderVol.append(valueDiv.toString())

                binding.adjusterVolume.setEdt(stringBuilderVol.toString())
            }
        }

        binding.inputLayout.btnAddLimit.setOnClickListener {
            stringBuilderVol.clear()
            stringBuilderVol.append(maxLimit)

            binding.adjusterVolume.setEdt(stringBuilderVol.toString())
        }

        binding.inputLayout.btnAddCash.setOnClickListener {
            stringBuilderVol.clear()
            stringBuilderVol.append(maxCash)

            binding.adjusterVolume.setEdt(stringBuilderVol.toString())
        }

        binding.btnConfirm.setOnClickListener {
            val volume = binding.adjusterVolume.getInt()

            if (volume <= 50000){
                onConfirm?.invoke(volume.formatPriceWithoutDecimal())
                dismiss()
            }
        }
    }

    fun setOnPinSuccess(listener: (String) -> Unit) {
        onConfirm = listener
    }

    fun appendVolume(value: String) {
        stringBuilderVol.append(value)

        if (stringBuilderVol.startsWith("0")) {
            stringBuilderVol.deleteCharAt(0)
        }

        binding.adjusterVolume.setEdt(stringBuilderVol.toString())
    }

}
