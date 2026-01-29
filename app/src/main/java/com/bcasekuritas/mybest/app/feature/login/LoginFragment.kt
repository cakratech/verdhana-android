package com.bcasekuritas.mybest.app.feature.login

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.BuildConfig
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.data.entity.BiometricObject
import com.bcasekuritas.mybest.app.data.layout.UIDialogModel
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.activity.main.MainActivity
import com.bcasekuritas.mybest.databinding.FragmentLoginBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.biometric.BiometricPromptManager
import com.bcasekuritas.mybest.ext.common.RabbitMQForegroundService
import com.bcasekuritas.mybest.ext.common.getDeviceUUID
import com.bcasekuritas.mybest.ext.common.getIpAddress
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.ConstKeys
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.delegate.BiometricImpl
import com.bcasekuritas.mybest.ext.delegate.ShowBiometric
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl
import com.bcasekuritas.mybest.widget.banner.BannerUtil
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.launch
import timber.log.Timber

@FragmentScoped
@AndroidEntryPoint
    class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>(),
    ShowBiometric by BiometricImpl(),
    ShowDialog by ShowDialogImpl(),
    ShowDropDown by ShowDropDownImpl() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmLogin
    override val viewModel: LoginViewModel by viewModels()
    override val binding: FragmentLoginBinding by autoCleaned {
        (FragmentLoginBinding.inflate(layoutInflater))
    }
    private val promptManager: BiometricPromptManager by lazy {
        BiometricPromptManager(this, requireContext())
    }

    private var isBiometricLogin = false
    private val webView = CustomTabsIntent.Builder().build()
    private var isOpenPinBlocked = false
    private var ipAddress = ""

    private var deviceType = ""
    private var deviceModel = ""
    private var deviceManufacture = ""
    private var appVersion = ""

    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun setupArguments() {
        super.setupArguments()
        arguments.let {
            if (it != null) {
                isOpenPinBlocked = it.getBoolean(Args.EXTRA_PARAM_BOOLEAN)
            }
        }
        deviceType = Build.DEVICE
        deviceModel = Build.MODEL
        deviceManufacture = Build.MANUFACTURER
        appVersion = BuildConfig.VERSION_NAME
    }

    override fun setupComponent() {
        super.setupComponent()
        viewModel.closeChannel()
        if (isOpenPinBlocked) {
            showDialogAccountDisable(parentFragmentManager)
        }
    }

    override fun setupObserver() {
        super.setupObserver()

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
                                prefManager.userId = binding.edtUserId.getText()
                                userId = binding.edtUserId.getText()
                            }
                            prefManager.cifCode = it.data.cifCode
                            prefManager.sessionId = it.data.sessionId
                            prefManager.urlIcon = it.data.appConfInfo.assetIconUrl
                            prefManager.urlLogo = it.data.appConfInfo.assetLogoUrl
                            Timber.d("sessionId: ${it.data.sessionId}")

                            viewModel.getSimpleAccInfo(userId, it.data.sessionId, it.data.cifCode)
                            viewModel.saveFCMToken(userId, it.data.sessionId, prefManager.fcmToken)

                            // start foreground service
                            RabbitMQForegroundService.startService(requireContext())
                        }

                        1 -> {
                            binding.edtPassword.showError(it.data.remarks)
                            binding.edtPassword.requestFocus()
                            hideLoading()
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

                        13 -> {
                            binding.edtUserId.showError(it.data.remarks)
                            binding.edtUserId.requestFocus()
                            hideLoading()
                        }

//                        14 -> {
//                            val bundle = Bundle().apply {
//                                val userId = if (prefManager.userId != "") prefManager.userId else binding.edtUserId.getText()
//                                putString(Args.EXTRA_PARAM_STR_ONE, userId)
//                                putString(Args.EXTRA_PARAM_STR_TWO, binding.edtPassword.getText())
//                            }
//                            // clear value
//                            binding.edtUserId.setEdt("")
//                            binding.edtPassword.setEdt("")
//                            viewModel.getLoginResult.value = null
//
//                            findNavController().navigate(R.id.device_otp_fragment, bundle)
//                        }
//
//                        15 -> {
//                            showDialogInfoManyDevicesOtp(parentFragmentManager)
//                            hideLoading()
//                        }

                        else -> {
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

        viewModel.getBannerLoginResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    it.data?.let { res ->
                        if (res.loginBannerDataCount != 0) {
                            binding.lyNoData.isGone = true
                            val itemBanner = ArrayList<BannerUtil.BannerItem>()
                            val sortedList = res.loginBannerDataList.sortedBy { it.priority }
                            sortedList.forEach { item ->
                                itemBanner.add(
                                    BannerUtil.BannerItem(
                                        item.bannerPathUrl,
                                        item.title,
                                        item.desc,
                                        item.ctaText,
                                        item.cta
                                    )
                                )
                            }
                            binding.banner.initSlider(itemBanner)
                        } else {
                            binding.lyNoData.isGone = false
                        }
                    }
                }
                else -> {}
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

                            if (!isBiometricLogin) {
                                viewModel.getToken(prefManager.userId) // check dao
                            }

                            prefManager.isDialogForceUpdateShow = false
                            viewModel.startSubsAll()
                        }
                    }
                }

                else -> {}
            }
        }

        viewModel.getTokenResult.observe(viewLifecycleOwner) {
            if (it == null) {
                viewModel.insertToken(
                    BiometricObject(
                        prefManager.userId,
                        binding.edtPassword.getText()
                    )
                )
            } else if(it.userId != "" && it.pw == ""){
                    viewModel.insertToken(
                        BiometricObject(
                            prefManager.userId,
                            binding.edtPassword.getText()
                        )
                    )

            }else if (it.token != null) {
                binding.btnBiometric.isGone = false
                if (isBiometricLogin) {
                    viewModel.getLogin(it.userId, it.pw!!, ipAddress, getDeviceUUID(requireContext()), deviceModel, deviceManufacture, appVersion)
                }
                binding.btnBiometric.visibility = View.VISIBLE

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
                                false, ""
                            )
                        }, 1000)
                } catch (e:Exception){
                    Timber.tag("LoginFragment").d("Potentially Activity Not Attached")
                }

                }
            }
        }

//        viewModel.getStockParamListResult.observe(viewLifecycleOwner) {
//            when (it) {
//                is Resource.Loading -> {
//                    showLoading()
//                }
//
//                is Resource.Success -> {
//                    when (it.data?.status) {
//                        0 -> {
//                            it.data.stockParamInfoList?.map { res ->
//                                val stockNotasi = StockParamInfo.newBuilder().setStockcode(res.stockcode).addAllStockNotasi(res.stockNotasiList)
//                                val stockNotasiByte = stockNotasi.build().toByteArray()
//                                viewModel.insertStockParamDao(
//                                    StockParamRes(
//                                    stockCode = res.stockcode,
//                                    stockName = res.stockname,
//                                    idxTrdBoard = res.idxTrdboard,
//                                    stockNotasi = stockNotasiByte
//                                )
//                                )
//                            }
//                        }
//                    }
//
//                    hideLoading()
//
//                    MainActivity.startIntentWithFinish(
//                        requireActivity(),
//                        NavKeys.KEY_MAIN_HOME
//                    )
//                }
//
//                else -> {}
//            }
//        }

    }

    override fun onStart() {
        super.onStart()

        try {
            viewModel.getToken(prefManager.userId)
            viewModel.deleteSession()
            viewModel.deleteDBStockNotation()
        } catch (e: Exception) {
            Timber.d("deleteSession no Data in sessionDb")
        }
    }

    override fun initAPI() {
        super.initAPI()
        viewModel.getBannerLogin()

        ipAddress = getIpAddress(requireContext())
        viewModel.getIpAddress()

        //
        viewModel.stopRunningTrade()
        viewModel.stopOrderBook()
        viewModel.stopIndiceData()
        viewModel.stopTradeSummary()
        viewModel.stopCIFStockPos()
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.btnLogin.setOnClickListener {
            if (prefManager.userId != "") {
                if (binding.edtPassword.getText() != "") {
                    viewModel.getLogin(
                        prefManager.userId,
                        binding.edtPassword.getText(),
                        ipAddress,
                        getDeviceUUID(requireContext()),
                        deviceModel,
                        deviceManufacture,
                        appVersion
                    )
                } else {
                    binding.edtPassword.showError("Please input password")
                    binding.edtPassword.requestFocus()
                }
            } else {
                val userId = binding.edtUserId.getText()
                val password = binding.edtPassword.getText()
                when {
                    userId == "" -> {
                        binding.edtUserId.showError("Please input Login ID")
                        binding.edtUserId.requestFocus()
                    }
                    password == "" -> {
                        binding.edtPassword.showError("Please input password")
                        binding.edtPassword.requestFocus()
                    }
                    else -> {
                        viewModel.getLogin(
                            userId,
                            password,
                            ipAddress,
                            getDeviceUUID(requireContext()),
                            deviceModel,
                            deviceManufacture,
                            appVersion
                        )
                    }
                }
            }

        }

        lifecycleScope.launch {
            promptManager.promptResults.collect { biometricResult ->
                handleBiometricResult(biometricResult)
            }
        }


        binding.btnBiometric.setOnClickListener {
            promptManager.showBiometricPrompt("Login With Biometric", "") { success ->
                if (success) {
                    if (prefManager.userId != "") {
                        viewModel.getToken(prefManager.userId)
                        isBiometricLogin = true
                    }
                }
            }
        }

        val twUserId = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int,
            ) {
                // Do something before text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                binding.tvError.text = ""
            }

            override fun afterTextChanged(s: Editable?) {
                binding.edtUserId.hideError()
                binding.edtPassword.hideError()

            }
        }

        binding.edtUserId.setTextWatcher(twUserId)

        val twPassword = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int,
            ) {
                // Do something before text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                binding.tvError.text = ""
            }

            override fun afterTextChanged(s: Editable?) {
                binding.edtUserId.hideError()
                binding.edtPassword.hideError()

            }
        }

        binding.edtPassword.setTextWatcher(twPassword)

        binding.btnLoginDifferentAccount.setOnClickListener {
            binding.edtUserId.setEdt("")
            prefManager.clearPreferencesUserId()
            binding.edtUserId.enable()
            binding.edtUserId.requestFocus()
            viewModel.getToken(prefManager.userId)
            binding.btnLoginDifferentAccount.isGone = true
            binding.lyRegister.visibility = View.VISIBLE
            viewModel.deleteToken()
            prefManager.isBiometricActive = false
            binding.btnBiometric.visibility = View.GONE
        }

        binding.btnForgotPassword.setOnClickListener {
            val url = ConstKeys.FORGOT_PASS_URL
            if (url.isBlank() || !(url.startsWith("http://") || url.startsWith("https://"))) {
                Toast.makeText(context, "Unavailable url", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {
                webView.launchUrl(requireContext(), Uri.parse(url))
            } catch (ignore: Exception) {}
        }

        binding.lyRegister.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ConstKeys.PRE_LOGIN_URL))
            startActivity(browserIntent)
        }
    }

    private fun handleBiometricResult(result: BiometricPromptManager.BiometricResult) {
        when (result) {
            is BiometricPromptManager.BiometricResult.HardwareUnavailable -> {
                "Hardware unavailable"
            }

            is BiometricPromptManager.BiometricResult.FeatureUnavailable -> {
                "Feature unavailable"
            }

            is BiometricPromptManager.BiometricResult.AuthenticationError -> {
                result.error
            }

            is BiometricPromptManager.BiometricResult.AuthenticationFailed -> {
                "Authentication failed"
            }

            is BiometricPromptManager.BiometricResult.AuthenticationSuccess -> {
                "Authentication success"
            }

            is BiometricPromptManager.BiometricResult.AuthenticationNotSet -> {
                "Authentication not set"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //
        if (prefManager.userId != "") {
            binding.edtUserId.setEdt(maskString(prefManager.userId))
            binding.edtUserId.disable()
            binding.btnLoginDifferentAccount.isGone = false
            binding.lyRegister.visibility = View.GONE
        } else {
            binding.edtUserId.enable()
            binding.lyRegister.visibility = View.VISIBLE
        }
    }

    fun maskString(input: String): String {

        val firstChar = input.take(1)
        val lastTwoChars = input.takeLast(2)
        val middleAsterisks = "*".repeat(input.length - 3)

        return firstChar + middleAsterisks + lastTwoChars
    }
}