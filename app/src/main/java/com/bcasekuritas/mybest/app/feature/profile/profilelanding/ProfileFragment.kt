package com.bcasekuritas.mybest.app.feature.profile.profilelanding

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.activity.main.MainActivity
import com.bcasekuritas.mybest.app.feature.activity.main.SharedMainViewModel
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.databinding.FragmentProfileBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.biometric.BiometricPromptManager
import com.bcasekuritas.mybest.ext.biometric.new.CryptographyManager
import com.bcasekuritas.mybest.ext.common.RabbitMQForegroundService
import com.bcasekuritas.mybest.ext.common.generateTextImageProfile
import com.bcasekuritas.mybest.ext.common.getDeviceId
import com.bcasekuritas.mybest.ext.common.validateSessionPin
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.other.ifNotNull
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import timber.log.Timber

@FragmentScoped
@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileViewModel>() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmProfile
    override val viewModel: ProfileViewModel by viewModels()
    override val binding: FragmentProfileBinding by autoCleaned {
        (FragmentProfileBinding.inflate(layoutInflater))
    }
    private val cryptographyManager = CryptographyManager()

    private lateinit var sharedViewModel: SharedMainViewModel
    private var userId = ""
    private var sessionId = ""
    private var accNo = ""
    private var sessionPin: Long? = null
    private var isPinBlocked = false

    private var isBiometricActive = false

    private val promptManager: BiometricPromptManager by lazy {
        BiometricPromptManager(this, requireContext())
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedMainViewModel::class.java)
    }

    override fun onPause() {
        super.onPause()
        viewModel.clearSessionPinResult()
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.lyToolbarProfile.ivLayoutToolbarMasterIconRightOne.setOnClickListener {
            MiddleActivity.startIntent(requireActivity(), NavKeys.KEY_FM_SEARCH_STOCK)
        }

        binding.constraintLayoutSmartLogin.setOnClickListener {
            viewModel.getToken(prefManager.userId)
        }

        binding.tvProfileName.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_profile_to_account_fragment)
        }

        binding.constraintLayoutAbout.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_profile_to_about_fragment)
        }

        binding.lyTopUp.setOnClickListener {
            MiddleActivity.startIntent(requireActivity(), NavKeys.KEY_FM_TOP_UP)
        }

        binding.lyWithdraw.setOnClickListener {
            MiddleActivity.startIntent(requireActivity(), NavKeys.KEY_FM_WITHDRAW)
        }

        binding.lyHistory.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_profile_to_fragment_rdn_history)
        }

        binding.constraintLayoutSecurity.setOnClickListener {
            MiddleActivity.startIntent(requireActivity(), NavKeys.KEY_FM_PROFILE_SECURITY)
        }

        binding.constraintLayoutEstatements.setOnClickListener {
            MiddleActivity.startIntent(requireActivity(), NavKeys.KEY_FM_E_STATEMENT)
        }

        binding.constraintLayoutPriceAlert.setOnClickListener {

        }

        binding.constraintLayoutNotifications.setOnClickListener {
            MiddleActivity.startIntent(requireActivity(), NavKeys.KEY_FM_NOTIFICATION_SETTINGS)
        }

        binding.constraintLayoutDisclaimer.setOnClickListener {
            MiddleActivity.startIntent(requireActivity(), NavKeys.KEY_FM_DISCLAIMER)
        }

        binding.lyHistory.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_profile_to_fragment_rdn_history)
        }

        binding.constraintLayoutHelp.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_profile_to_fragment_help)
        }

        binding.constraintLayoutAbout.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_profile_to_about_fragment)
        }

        binding.constraintLayoutLogout.setOnClickListener {
            viewModel.getLogout(userId, sessionId)
        }

        binding.constraintLayoutLineSetting.setOnClickListener {
            MiddleActivity.startIntent(requireActivity(), NavKeys.KEY_FM_LINE_SETTING)
        }

        binding.constraintLayoutDarkMode.setOnClickListener {
//            isDarkMode(prefManager.isDarkMode)
        }

        binding.constraintLayoutDarkMode.setOnClickListener {
//            isDarkMode(prefManager.isDarkMode)
        }

        binding.constraintLayoutManageDevice.setOnClickListener {
            MiddleActivity.startIntent(requireActivity(), NavKeys.KEY_FM_MANAGE_DEVICE)
        }
    }

    private fun isDarkMode(isDarkMode: Boolean) {
        var darkModeToggled = 1

        if (isDarkMode) {
            prefManager.isDarkMode = false
            binding.switchDarkMode.isChecked = false
            darkModeToggled = AppCompatDelegate.MODE_NIGHT_NO
        } else {
            prefManager.isDarkMode = true
            binding.switchDarkMode.isChecked = true
            darkModeToggled = AppCompatDelegate.MODE_NIGHT_YES
        }
        AppCompatDelegate.setDefaultNightMode(darkModeToggled)
    }

    @SuppressLint("ResourceAsColor")
    override fun setupComponent() {
        binding.lyToolbarProfile.tvLayoutToolbarMasterTitle.text = getString(R.string.text_profile)
        binding.lyToolbarProfile.ivLayoutToolbarMasterIconLeft.visibility = View.GONE
        binding.lyToolbarProfile.ivLayoutToolbarMasterIconRightOne.visibility = View.VISIBLE
        binding.lyToolbarProfile.ivLayoutToolbarMasterIconRightOne.setImageResource(R.drawable.ic_search)

        binding.switchDarkMode.isEnabled = false
        binding.switchEnglish.isEnabled = false
        binding.swipeRefreshLayoutProfile.isRefreshing = false
        binding.swipeRefreshLayoutProfile.isEnabled = false


        binding.switchDarkMode.isChecked = prefManager.isDarkMode

    }

    override fun onResume() {
        super.onResume()
        userId = prefManager.userId
        sessionId = prefManager.sessionId
        accNo = prefManager.accno

        viewModel.getSessionPin(userId)
        viewModel.getAccNameDao(accNo)
        setSwitchBiometric(prefManager.isBiometricActive)
    }

    @SuppressLint("SetTextI18n")
    override fun setupObserver() {
        super.setupObserver()
        viewModel.imqConnectionListener.connListenerLiveData.observe(this) { value ->
            when (value) {
                "Recovered" -> {
                    viewModel.imqConnectionListener.onListener("")
                    viewModel.getSessionPin(userId)
                }

                else -> {}
            }
        }

        viewModel.getAccountInfoResult.observe(this) {
            binding.tvProfileName.text = it.accName
            binding.tvImageProfile.text = generateTextImageProfile(it.accName)
            binding.tvProfileNoAccount.text = it.accNo
        }

        // Get Session PIN
        viewModel.getSessionPinResult.observe(viewLifecycleOwner) {
            sessionPin = it
            if (it != null) {
                if (sessionPin != null) {
                    if (validateSessionPin(sessionPin!!)) {
                        binding.clProfile.visibility = View.VISIBLE
                        viewModel.getSimplePortfolio(userId, sessionId, accNo)
                    } else {
                        showDialogPin()
                    }
                } else {
                    showDialogPin()
                }
            }
        }

        // Get Trading Balance
        viewModel.getSimplePortfolioResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }

                is Resource.Success -> {
                    if (it.data != null) {
                        when (it.data?.status) {
                            0 -> {
                                it.data?.let { res ->
                                    binding.apply {
                                        tvTradingBalance.text =
                                            "Rp${res.simplePortofolio.realCashBalance.formatPriceWithoutDecimal()}"
                                    }
                                }
                            }

                            2 -> {
                                hideLoading()
                                (activity as MainActivity).showDialogSessionExpired()
                            }
                        }
                    }
                    hideLoading()

                }

                is Resource.Failure -> {
                    Toast.makeText(requireContext(), it.failureData.message, Toast.LENGTH_SHORT)
                        .show()
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
                    if (isPinBlocked) {
                        MiddleActivity.startIntentWithFinish(requireActivity(), NavKeys.KEY_FM_LOGIN, true)
                    } else {
                        MiddleActivity.startIntentWithFinish(requireActivity(), NavKeys.KEY_FM_LOGIN, "")
                    }
                    Timber.e(it?.remarks)
                }
                else -> {
                    Timber.e("${it?.status} : ${it?.remarks}" )
                }

            }
        }

        viewModel.getTokenResult.observe(viewLifecycleOwner) {
            it.ifNotNull {
                if (it?.token != null) {
                    viewModel.deleteToken(prefManager.userId)
                    binding.switchSmartLogin.isChecked = false
                    prefManager.isBiometricActive = false
                } else {
                    promptManager.showBiometricPrompt("Activate Biometric", "") { success ->
                        if (success) {
//                        val token = cryptographyManager.encryptBiometric(getDeviceId(requireContext()) + prefManager.userId, prefManager.userId)
                            val token = getDeviceId(requireContext()) + prefManager.userId
                            viewModel.updateToken(prefManager.userId, token)
                            viewModel.clearGetToken()
                            prefManager.isBiometricActive = true
                            binding.switchSmartLogin.isChecked = true
                        }
                    }
                }
            }
        }
    }

    private fun showDialogPin() {
        showDialogPin(parentFragmentManager, onSuccess = { isSuccess, isBlocked ->
            if (isSuccess) {
                if (isAdded) {
                    binding.clProfile.visibility = View.VISIBLE
                    viewModel.getSimplePortfolio(userId, sessionId, accNo)
                }
            } else {
                if (isBlocked) {
                    isPinBlocked = true
                    viewModel.getLogout(prefManager.userId, prefManager.sessionId)
                } else {
                    if (isAdded) {
                        onBackPressed()
                    }
                }

            }
        })
    }

    private fun setSwitchBiometric(isBiometric: Boolean){

        binding.switchSmartLogin.isChecked = isBiometric
    }
}