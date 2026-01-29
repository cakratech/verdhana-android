package com.bcasekuritas.mybest.app.feature.security.changepin

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withvm.BaseDialogFullFragment
import com.bcasekuritas.mybest.app.data.entity.SessionObject
import com.bcasekuritas.mybest.app.data.layout.UIDialogModel
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.activity.main.MainActivity
import com.bcasekuritas.mybest.app.feature.activity.main.SharedMainViewModel
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.pin.PinViewModel
import com.bcasekuritas.mybest.databinding.FragmentPinBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.RabbitMQForegroundService
import com.bcasekuritas.mybest.ext.common.showToast
import com.bcasekuritas.mybest.ext.constant.ConstKeys
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import timber.log.Timber

@FragmentScoped
@AndroidEntryPoint
class ChangePinFragment : BaseDialogFullFragment<FragmentPinBinding, PinViewModel>(),
    ShowDialog by ShowDialogImpl() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmPin
    override val viewModel: PinViewModel by viewModels()
    override val binding: FragmentPinBinding by autoCleaned {
        (FragmentPinBinding.inflate(
            layoutInflater
        ))
    }

    private lateinit var sharedViewModel: SharedMainViewModel

    private val webView = CustomTabsIntent.Builder().build()

    private var listNumber: ArrayList<String> = ArrayList()
    private lateinit var userId: String
    private lateinit var sessionId: String

    private var onPinSuccess: ((Boolean) -> Unit)? = null
    private var countPage = 0
    private var pinValue = ""
    private var oldPin = ""
    private var newPin = ""
    private var confirmPin = ""

    private var stepState = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedMainViewModel::class.java)
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        sessionId = prefManager.sessionId
    }

    fun setOnPinSuccess(listener: (Boolean) -> Unit) {
        onPinSuccess = listener
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onPinSuccess?.invoke(false)
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.inputPinResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    it.data?.let { list ->
//                        setupUi(list)
//                        if (list.size == 8){
//                            viewModel.validatePin(userId, list.joinToString(separator = "") , sessionId)
//                        }
                        pinValue = list.joinToString(separator = "")
                        binding.edtPin.setText(pinValue)
                        binding.btnSubmit.isEnabled = list.size >= 8
//                        binding.edtPin.cursorsPosition(list.joinToString(separator = "").length)
//                        binding.edtPin.hideKeyboard()
                    }
                }

                else -> {}
            }
        }

        viewModel.getChangePinResult.observe(viewLifecycleOwner){
            when(it){
                is Resource.Success -> {
                    when(it.data?.status){
                        0 -> {
                            onPinSuccess?.invoke(true)
                            dismiss()
                        }
                        1 -> {
                            binding.edtPin.setText("")
                            binding.edtPin.showErrorForPin("PIN doesn’t not match")
                            listNumber = arrayListOf<String>()
                            countPage = 2
                            binding.btnSubmit.isEnabled = false

                        }
                    }
                }

                is Resource.Failure -> {

                }

                else -> {

                }
            }
        }

        viewModel.validatePinResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    when (it.data?.status) {
                        0 -> {
                            binding.btnForgotPin.visibility = View.GONE
                            val resetPin = ArrayList<String>()
                            countPage++
                            viewModel.inputPin(ArrayList<String>(), "")
                            listNumber = resetPin
                            binding.tvInstruction.text = "Please enter new security PIN"
                            binding.lyToolbarPin.tvLayoutToolbarMasterTitle.text = "Enter New PIN"
                            oldPin = pinValue
                        }
                        1 -> {
                            listNumber = arrayListOf<String>()
                            binding.edtPin.setText("")
                            binding.edtPin.showErrorForPin(it.data.remarks)
                            binding.btnSubmit.isEnabled = false
                        }
                    }
                }

                else -> {}
            }
        }

        viewModel.getLogoutResult.observe(viewLifecycleOwner) {
            when (it?.status){
                0 -> {
                    RabbitMQForegroundService.stopService(requireContext())
                    sharedViewModel.setStopSubs(true)
                    viewModel.deleteSession()
                    prefManager.clearPreferences()
                    MiddleActivity.startIntentWithFinish(requireActivity(), NavKeys.KEY_FM_LOGIN, "")
                    Timber.e(it?.remarks)
                    val url = ConstKeys.FORGOT_PASS_URL
                    if (url.isBlank() || !(url.startsWith("http://") || url.startsWith("https://"))) {
                        Toast.makeText(context, "Unavailable url", Toast.LENGTH_SHORT).show()
                        return@observe
                    }
                    try {
                        webView.launchUrl(requireContext(), Uri.parse(url))
                    } catch (ignore: Exception) {}
                }
                else -> {
                    Timber.e("${it?.status} : ${it?.remarks}" )
                }
            }
        }
    }

    override fun setupComponent() {
        super.setupComponent()

        binding.lyToolbarPin.tvLayoutToolbarMasterTitle.text = "Enter PIN"
        binding.lyToolbarPin.ivLayoutToolbarMasterIconLeft.visibility = View.VISIBLE
        binding.lyToolbarPin.ivLayoutToolbarMasterIconLeft.setImageResource(R.drawable.ic_back)

        binding.tvInstruction.text = "Please enter security PIN to continue."

        val buttonClick = View.OnClickListener {
            val buttonText = (it as TextView).text.toString()
            viewModel.inputPin(listNumber, buttonText)
        }

        binding.btn0.setOnClickListener(buttonClick)
        binding.btn1.setOnClickListener(buttonClick)
        binding.btn2.setOnClickListener(buttonClick)
        binding.btn3.setOnClickListener(buttonClick)
        binding.btn4.setOnClickListener(buttonClick)
        binding.btn5.setOnClickListener(buttonClick)
        binding.btn6.setOnClickListener(buttonClick)
        binding.btn7.setOnClickListener(buttonClick)
        binding.btn8.setOnClickListener(buttonClick)
        binding.btn9.setOnClickListener(buttonClick)
        binding.btn0.setOnClickListener(buttonClick)

        binding.btnErase.setOnClickListener {
            viewModel.inputPin(listNumber, "")
        }


        binding.edtPin.disableFocus()
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.lyToolbarPin.ivLayoutToolbarMasterIconLeft.setOnClickListener {
            dismiss()
            onPinSuccess?.invoke(false)
        }

        binding.btnForgotPin.setOnClickListener {
            viewModel.getLogout(userId, sessionId)
        }

        binding.btnSubmit.setOnClickListener {
            binding.edtPin.hideErrorForPin()
            val resetPin = ArrayList<String>()
            if (countPage == 0) {
                // input old pin
                viewModel.validatePin(userId, binding.edtPin.getText() , sessionId)
            } else if (countPage == 1) {
                // input new pin
                listNumber = resetPin
                viewModel.inputPin(ArrayList<String>(), "")
                newPin = pinValue
                if (newPin != oldPin) {
                    binding.tvInstruction.text = "Please confirm new security PIN"
                    binding.lyToolbarPin.tvLayoutToolbarMasterTitle.text = "Confirm New PIN"
                    countPage++
                } else {
                    binding.edtPin.showErrorForPin("The new PIN must be different from the old one.")
                }
            } else if (countPage == 2) {
                // confirm new pin
                confirmPin = pinValue
                viewModel.getChangePin(prefManager.userId, oldPin, newPin, confirmPin)
            }
        }
    }
}