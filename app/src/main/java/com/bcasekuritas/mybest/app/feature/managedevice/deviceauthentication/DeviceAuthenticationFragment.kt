package com.bcasekuritas.mybest.app.feature.managedevice.deviceauthentication

import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.BuildConfig
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.data.layout.UIDialogModel
import com.bcasekuritas.mybest.app.domain.dto.request.SendOtpTrustedDeviceRequest
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.activity.main.MainActivity
import com.bcasekuritas.mybest.databinding.FragmentDeviceAuthenticationBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.getDeviceUUID
import com.bcasekuritas.mybest.ext.common.getIpAddress
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.ConstKeys
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.view.setSafeOnClickListener
import com.bcasekuritas.mybest.widget.textview.CustomTextView
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

@FragmentScoped
@AndroidEntryPoint
class DeviceAuthenticationFragment: BaseFragment<FragmentDeviceAuthenticationBinding, DeviceAuthenticationViewModel>() {

    override val viewModel: DeviceAuthenticationViewModel by viewModels()
    override val binding: FragmentDeviceAuthenticationBinding by autoCleaned { (FragmentDeviceAuthenticationBinding.inflate(layoutInflater)) }
    override val bindingVariable: Int = BR.vmDeviceAuth

    private var countDownTimer: CountDownTimer? = null
    private var endTime: Long = 0L

    private var userId = ""
    private var deviceUUID = ""
    private var deviceManufacture = ""
    private var deviceModel = ""
    private var appVersion = ""
    private var pw = ""
    private var ipAddress = ""
    private var channel = "email"

    private var countdownErrorJob: Job? = null

    private val webView = CustomTabsIntent.Builder().build()

    override fun setupArguments() {
        super.setupArguments()
        arguments.let {
            if (it != null) {
                userId = it.getString(Args.EXTRA_PARAM_STR_ONE)?: ""
                pw = it.getString(Args.EXTRA_PARAM_STR_TWO)?: ""
            }
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            deviceModel = Build.MODEL
            deviceManufacture = Build.MANUFACTURER
            appVersion = BuildConfig.VERSION_NAME
            deviceUUID = getDeviceUUID(requireContext())

            lyToolbar.tvLayoutToolbarMasterTitle.text = "Device Authentication"

        }
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            lyToolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener {
                onBackPressed()
            }

            tvUpdateData.setOnClickListener {
                val url = ConstKeys.FORGOT_PASS_URL
                if (url.isBlank() || !(url.startsWith("http://") || url.startsWith("https://"))) {
                    Toast.makeText(context, "Unavailable url", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                try {
                    webView.launchUrl(requireContext(), Uri.parse(url))
                } catch (ignore: Exception) {}
            }

            btnResend.setSafeOnClickListener {
                channel = "email"
                edtOtp.setEdt("")
                changeInfoUpdateData(isEmail = true)
                sendOtp()
            }

            btnResendWa.setSafeOnClickListener {
                channel = "whatsapp"
                edtOtp.setEdt("")
                changeInfoUpdateData(isEmail = false)
                sendOtp()
            }

            btnContinue.setOnClickListener {
                val otp = edtOtp.getText()
                viewModel.verifyOtp(userId, otp, deviceUUID)
                btnContinue.isEnabled = false
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )
    }

    override fun initAPI() {
        super.initAPI()
        ipAddress = getIpAddress(requireContext())
        viewModel.getIpAddress()

        sendOtp()
    }

    private fun sendOtp() {
        val request = SendOtpTrustedDeviceRequest(
            userId,
            channel,
            deviceUUID,
            deviceModel,
            deviceManufacture,
            appVersion
        )
        viewModel.sendOtp(request)
    }

    override fun onResume() {
        super.onResume()
        runCountdown()
    }

    override fun onPause() {
        super.onPause()
        countDownTimer?.cancel()
        countdownErrorJob?.cancel()
    }

    override fun setupListener() {
        super.setupListener()
        val searchTextWatcher = object : TextWatcher {
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
                binding.edtOtp.setLetterSpacing(s?.isNotEmpty() == true)
                binding.btnContinue.isEnabled = s?.length == 6

                binding.edtOtp.hideError()
                binding.tvError.visibility = View.GONE
            }
        }

        binding.edtOtp.setTextWatcher(searchTextWatcher)
    }

    private fun startResendCountdown(duration: String) {
        val dur = duration.toLongOrNull()?.times(1000L) ?: 30_000L
        endTime = System.currentTimeMillis() + dur
        runCountdown()
    }

    private fun runCountdown() {
        countDownTimer?.cancel()
        showCountdownText(true)
        val millisLeft = endTime - System.currentTimeMillis()
        if (millisLeft <= 0) {
            showCountdownText(false)
            return
        }

        countDownTimer = object : CountDownTimer(millisLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60
                val timeFormatted = String.format("%02d:%02d", minutes, remainingSeconds)
                binding.tvResendCountdown.text = "Didn’t receive the code? Resend after 00:$timeFormatted"
            }

            override fun onFinish() {
                showCountdownText(false)
            }
        }.start()
    }

    private fun showCountdownText(isShow: Boolean) {
        binding.apply {
            tvResendCountdown.visibility = if (isShow) View.VISIBLE else View.GONE
            groupResend.visibility = if (isShow) View.GONE else View.VISIBLE
        }
    }

    private fun showErrorText(text: String, outlineRed: Boolean) {
        binding.tvError.text = text
        binding.tvError.visibility = View.VISIBLE
        if (outlineRed) {
            binding.edtOtp.showError("")
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.sendOtpResult.observe(viewLifecycleOwner) {res ->
            if (res != null) {
                when (res.status) {
                    0 ->  {
                        startResendCountdown(res.timeout)
                        val email = if (!res.source.isNullOrEmpty()) res.source else ""
                        setInfoEmail(binding.tvEmailInfo, binding.tvEmailInfoBhs, email)
                    }
                    1 -> showErrorText(res.remarks, false)
                    2 -> {
                        showErrorText("Still valid OTP", false)
                    }
                    3 -> {
                        if (res.remarks.lowercase().contains("too many")) {
                            onBackPressed()
                        } else {
                            showErrorText(res.remarks, false)
                            countdownErrorJob = viewLifecycleOwner.lifecycleScope.launch {
                                delay(5000)
                                if (!isActive) return@launch
                                onBackPressed()
                            }
                        }
                    }
                    4 -> {
                        prefManager.isShowDialogManyRequestOtp = true
                        onBackPressed()
                    }
                }
            }

        }

        viewModel.verifyOtpResult.observe(viewLifecycleOwner) {res ->
            if (res != null) {
                when (res.status) {
                    0 -> {
                        viewModel.getLogin(
                            userId,
                            pw,
                            ipAddress,
                            deviceUUID,
                            deviceModel,
                            deviceManufacture,
                            appVersion
                        )
                    }
                    1 -> {
                        showErrorText(res.remarks, true)
                    }
                    3 -> {
                        onBackPressed()
                    }
                }
            }
            binding.btnContinue.isEnabled = true
        }

        viewModel.getIpAddressResult.observe(viewLifecycleOwner) {value ->
            ipAddress = value.ifEmpty { getIpAddress(requireContext()) }
        }

        viewModel.getLoginResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }

                is Resource.Success -> {
                    when (it.data?.status) {
                        0 -> {
                            Timber.d("App Info & Order Reply Start")
                            viewModel.startNewAppNotification()
                            viewModel.startNewOrderReply()

                            // save userid
                            var userId = prefManager.userId
                            if (userId == "") {
                                prefManager.userId = this.userId
                                userId = this.userId
                            }
                            prefManager.cifCode = it.data.cifCode
                            prefManager.sessionId = it.data.sessionId
                            prefManager.urlIcon = it.data.appConfInfo.assetIconUrl
                            prefManager.urlLogo = it.data.appConfInfo.assetLogoUrl
                            Timber.d("sessionId: ${it.data.sessionId}")

                            viewModel.getSimpleAccInfo(userId, it.data.sessionId, it.data.cifCode)
                            viewModel.saveFCMToken(userId, it.data.sessionId, prefManager.fcmToken)
                        }

                        2 -> {
                            showDialogInfoCenter(
                                false,
                                UIDialogModel(
                                    titleStr = "User Locked",
                                    descriptionStr = "Your account is Locked, Please contact admin.",
                                    btnPositive = R.string.session_expired_button_positive
                                ), parentFragmentManager
                            )
                            hideLoading()
                        }


                        else -> {
                            showErrorText(it.data?.remarks?:"", false)
                            hideLoading()
                        }
                    }
                }

                is Resource.Failure -> {
                    hideLoading()
                    Toast.makeText(requireContext(), it.failureData.message, Toast.LENGTH_SHORT)
                        .show()

                    hideLoading()
                }

                else -> {
                    hideLoading()
                }
            }
        }

        viewModel.getSimpleAccountResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }

                is Resource.Success -> {
                    when (it.data?.status) {
                        0 -> {
                            it.data.let { res ->
                                Timber.d("accInfo Size: ${res.simpleAccountInfoList.size}")
                                val accNo = res.simpleAccountInfoList[0]?.accno
                                prefManager.accno = accNo ?: ""
                                Timber.d("accNo: $accNo")

                                if (res.simpleAccountInfoList.first()?.channelCommList?.isNotEmpty() == true) {
                                    res.simpleAccountInfoList.first()?.channelCommList?.forEach { channelCom ->
                                        if (channelCom.channelCode.equals("OLT")) {
                                            prefManager.buyCommission =  channelCom.buyComm.toString()
                                            prefManager.sellCommission = channelCom.sellComm.toString()
                                        }

                                        if (channelCom.channelCode.equals("OMS")) {
                                            prefManager.buyCommissionOms = channelCom.buyComm.toString()
                                            prefManager.buyCommissionOms = channelCom.sellComm.toString()
                                        }
                                    }
                                } else {
                                    prefManager.buyCommission =  "0.00"
                                    prefManager.sellCommission = "0.00"
                                    prefManager.buyCommissionOms =  "0.00"
                                    prefManager.buyCommissionOms = "0.00"
                                }
                            }
                            prefManager.isDialogForceUpdateShow = false
                            viewModel.startSubsAll()
                        }
                        else -> {
                            showErrorText(it.data?.remarks?:"", false)
                            hideLoading()
                        }
                    }
                }

                else -> {
                    hideLoading()
                }
            }
        }

        viewModel.startAllConsumerResult.observe(this) { data ->
            data?.let {
                if (data) {
                    try {
                        Handler(Looper.getMainLooper()).postDelayed({
                            MainActivity.startIntentParam(
                                requireActivity(),
                                NavKeys.KEY_MAIN_HOME,
                                true, ""
                            )
                        }, 1000)
                    } catch (e:Exception){
                        Timber.tag("LoginFragment").d("Potentially Activity Not Attached")
                    }

                }
            }
        }
    }

    private fun changeInfoUpdateData(isEmail: Boolean) {
        val platform = if (isEmail) "email address" else "phone number"
        val text = "If your $platform is no longer valid, please "
        val highlight = "update your data"

        val spannable = SpannableString(text + highlight)

        spannable.setSpan(StyleSpan(Typeface.BOLD), text.length, text.length + highlight.length, 0)
        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#0154FA")), text.length, text.length + highlight.length, 0)

        binding.tvUpdateData.text = spannable
    }

    private fun setInfoEmail(
        binding: CustomTextView,
        bindingBahasa: CustomTextView,
        email: String
    ) {

        val staticText = "Please enter the OTP code that has been sent to $email to continue the verification process."
        val staticTextBahasa = "Silakan masukkan kode OTP yang telah dikirim ke $email untuk melanjutkan proses verifikasi."

        val spannableStringBuilder = SpannableStringBuilder(staticText)
        val spannableStringBuilderBahasa = SpannableStringBuilder(staticTextBahasa)

        val spanStart = staticText.indexOf(email)
        val spanEnd = spanStart + email.length

        spannableStringBuilder.setSpan(
            StyleSpan(Typeface.BOLD),
            spanStart,
            spanEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableStringBuilderBahasa.setSpan(
            StyleSpan(Typeface.BOLD),
            spanStart,
            spanEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.text = spannableStringBuilder
        bindingBahasa.text = spannableStringBuilderBahasa
    }
}