package com.bcasekuritas.mybest.app.feature.pin

import android.app.Dialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withvm.BaseDialogFullFragment
import com.bcasekuritas.mybest.app.data.entity.SessionObject
import com.bcasekuritas.mybest.app.data.layout.UIDialogModel
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.activity.main.SharedMainViewModel
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.databinding.FragmentPinBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.RabbitMQForegroundService
import com.bcasekuritas.mybest.ext.common.showToast
import com.bcasekuritas.mybest.ext.constant.ConstKeys
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.constant.NetworkCodes
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarInterface
import com.bcasekuritas.mybest.widget.button.CustomButton
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.launch
import timber.log.Timber


@FragmentScoped
@AndroidEntryPoint
class PinFragment : BaseDialogFullFragment<FragmentPinBinding, PinViewModel>(),
    ShowDialog by ShowDialogImpl(), ShowSnackBarInterface by ShowSnackBarImpl() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmPin
    override val viewModel: PinViewModel by viewModels()
    override val binding: FragmentPinBinding by autoCleaned {
        (FragmentPinBinding.inflate(
            layoutInflater
        ))
    }

    private lateinit var sharedViewModel: SharedMainViewModel

    private var listNumber: ArrayList<String> = ArrayList()
    private lateinit var userId: String
    private lateinit var sessionId: String

    private val webView = CustomTabsIntent.Builder().build()
    private var onPinSuccess: ((Boolean, Boolean) -> Unit)? = null

    private var isLoadingSubmit = false

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        sessionId = prefManager.sessionId
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedMainViewModel::class.java)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        return dialog
    }

    fun setOnPinSuccess(listener: (Boolean, Boolean) -> Unit) {
        onPinSuccess = listener
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onPinSuccess?.invoke(false, false)
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
                        try {
                            binding.edtPin.setText(list.toList().joinToString(separator = ""))
                        } catch (e: Exception){
                            Timber.tag("PinFragment").d("Concurrent Modification")
                        }
                        if (!isLoadingSubmit) {
                            binding.btnSubmit.isEnabled = list.size >= 8
                        }
//                        binding.btnSubmit.isEnabled = list.size >= 8
//                        binding.edtPin.cursorsPosition(list.joinToString(separator = "").length)
//                        binding.edtPin.hideKeyboard()
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
                    Timber.e(it.remarks)
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

        viewModel.validatePinResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if (it.data != null) {
                        when (it.data.status) {
                            0 -> {
                                Timber.d("App notif pin PINFRAG")
                                binding.edtPin.hideError()

                                val timeSecond = System.currentTimeMillis() / 1000
                                val remain = timeSecond + (it.data.remain.div(1000))

                                lifecycleScope.launch {
                                    viewModel.insertSession(
                                        SessionObject(
                                            prefManager.userId,
                                            prefManager.sessionId,
                                            remain
                                        )
                                    )

                                    Timber.d("Pin close Dialog")
                                    onPinSuccess?.invoke(true, false)
                                    dismiss()
                                }
                            }

                            1 -> {
                                if (it.data.remarks.lowercase().contains("blocked")) {
                                    onPinSuccess?.invoke(false, true)
                                    dismiss()
                                } else {
                                    binding.edtPin.setText("")
                                    listNumber.clear()
                                    binding.edtPin.showError(it.data.remarks)
                                }
                            }

                            2 -> {
                                viewModel.deleteSessionPin()
                                showDialogInfoCenterCallBack(false,
                                    childFragmentManager, UIDialogModel(
                                        titleStr = "Session Expired",
                                        descriptionStr = "Please re-login to renew your session",
                                        btnPositive = R.string.logout_button_positive
                                    ),
                                    onOkClicked = {
                                        it.let {
                                            MiddleActivity.startIntentWithFinish(
                                                requireActivity(),
                                                NavKeys.KEY_FM_LOGIN, ""
                                            )
                                        }
                                    })
                            }

                            3 -> {
                                showToast(requireContext(), "Need Change PIN")
                            }

                            4 -> {
                                binding.edtPin.showError("PIN Locked")
                            }

                            else -> {
                                binding.edtPin.showError("Other Error ${it.data.status}")
                            }
                        }
                    }

                }
                is Resource.Failure -> {
                    if (it.failureData.code == NetworkCodes.GENERIC_ERROR && it.failureData.message?.contains("Timeout") == true) {
                        showSnackBarTop(
                            requireContext(), binding.root,
                            "error",
                            R.drawable.ic_error,
                            "ValidatePinRequest",
                            "Code: 408 - Message: Request time out.",
                            requireActivity(), ""
                        )
                    }
                }

                else -> {}

            }
            hideButtonLoading()
        }
    }

    override fun setupComponent() {
        super.setupComponent()

        binding.lyToolbarPin.tvLayoutToolbarMasterTitle.text = "Enter PIN"
        binding.lyToolbarPin.ivLayoutToolbarMasterIconLeft.visibility = View.VISIBLE
        binding.lyToolbarPin.ivLayoutToolbarMasterIconLeft.setImageResource(R.drawable.ic_back)

        binding.progressSubmit.indeterminateDrawable?.setTint(ContextCompat.getColor(requireContext(), R.color.white))

        val buttonClick = View.OnClickListener {
            binding.edtPin.hideError()
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
            binding.edtPin.hideError()
            viewModel.inputPin(listNumber, "")
        }


        binding.edtPin.disableFocus()
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.lyToolbarPin.ivLayoutToolbarMasterIconLeft.setOnClickListener {
            dismiss()
            onPinSuccess?.invoke(false, false)
        }

        binding.btnSubmit.setOnClickListener {
            if (!isLoadingSubmit) {
                showButtonLoading()
                viewModel.validatePin(userId, binding.edtPin.getText(), sessionId)
            }
        }
        binding.btnForgotPin.setOnClickListener {
            viewModel.getLogout(userId, sessionId)
        }
    }

    private fun showButtonLoading() {
        isLoadingSubmit = true
        binding.btnSubmit.apply {
            isEnabled = false
            text = ""
        }
        binding.progressSubmit.visibility = View.VISIBLE
    }

    private fun hideButtonLoading() {
        isLoadingSubmit = false
        binding.btnSubmit.apply {
            isEnabled = binding.edtPin.getText() != ""
            text = "Submit"
        }
        binding.progressSubmit.visibility = View.GONE
    }

//    private fun setupUi (listPin: List<String>){
//        clearInput()
//        for (i in listPin.indices) {
//            val dotImageView = when (i) {
//                0 -> binding.pinField1
//                1 -> binding.pinField2
//                2 -> binding.pinField3
//                3 -> binding.pinField4
//                4 -> binding.pinField5
//                5 -> binding.pinField6
//                6 -> binding.pinField7
//                7 -> binding.pinField8
//                else -> null
//            }
//
//            dotImageView?.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)
//            dotImageView?.setImageResource(R.drawable.ic_dot_selected)
//        }
//    }

//    private fun clearInput(){
//
//        for (i in 0..7) {
//            val dotImageView = when (i) {
//                0 -> binding.pinField1
//                1 -> binding.pinField2
//                2 -> binding.pinField3
//                3 -> binding.pinField4
//                4 -> binding.pinField5
//                5 -> binding.pinField6
//                6 -> binding.pinField7
//                7 -> binding.pinField8
//                else -> null
//            }
//
//            dotImageView?.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)
//            dotImageView?.setImageResource(R.drawable.ic_outline_dot)
//        }
//    }


//    private fun invalidPIN (){
//        for (i in 0..7) {
//            val dotImageView = when (i) {
//                0 -> binding.pinField1
//                1 -> binding.pinField2
//                2 -> binding.pinField3
//                3 -> binding.pinField4
//                4 -> binding.pinField5
//                5 -> binding.pinField6
//                6 -> binding.pinField7
//                7 -> binding.pinField8
//                else -> null
//            }
//
//            dotImageView?.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
//            dotImageView?.setImageResource(R.drawable.ic_dot_selected)
//        }
//    }
}