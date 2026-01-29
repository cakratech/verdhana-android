package com.bcasekuritas.mybest.ext.delegate

import android.content.Context
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import timber.log.Timber

interface ShowBiometric {
    fun showBiometric(context: Context, button: MaterialButton, fragment: Fragment)
    fun showBiometric(context: Context, title: String, description: String,  button: MaterialButton, fragment: Fragment)
}

class BiometricImpl : ShowBiometric {

    private lateinit var authenticationCallback: BiometricPrompt.AuthenticationCallback
    private lateinit var biometricPrompt: BiometricPrompt

    private val resultChannel = Channel<BiometricResult>()
    val promptResults = resultChannel.receiveAsFlow()

    override fun showBiometric(context: Context, button: MaterialButton, fragment: Fragment) {

        authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                // Biometric authentication succeeded
                // You can proceed with your secure operation
                // For demonstration, we'll display a success message
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                // Biometric authentication error
                // Handle errors as needed
            }

            override fun onAuthenticationFailed() {
                // Biometric authentication failed
                // Handle failures as needed
            }
        }

        biometricPrompt = BiometricPrompt(
            fragment,
            ContextCompat.getMainExecutor(context),
            authenticationCallback
        )


        var promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Otentikasi Biometrik Untuk Melanjutkan")
            .setSubtitle("Anda bisa Autentikasi menggunakan fingerprint atau face")
            .setAllowedAuthenticators(BIOMETRIC_STRONG) // Biometric Strong untuk fingerprint atau face id
            .setNegativeButtonText("Batal")
            .setConfirmationRequired(false)
            .build()



            val biometricManager = BiometricManager.from(context)
            when (biometricManager.canAuthenticate()) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    // Biometric authentication is available
                    biometricPrompt.authenticate(promptInfo)
                    Timber.d("Biometric eligible")
                }

                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    // hardware biometrik tidak tersedia

                    Timber.d("Biometric tidak tersedia")
                }

                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                    // hardware biometrik tidak dapat digunakan

                    Timber.d("Biometric error")
                }

                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    // user belum mendaftarkan biometrik pada ponsel
                    Timber.d("not enrolled")
                }
            }


    }

    override fun showBiometric(context: Context,title: String, description: String,  button: MaterialButton, fragment: Fragment) {
        val manager = BiometricManager.from(context)
        val authenticators = BIOMETRIC_STRONG

        val promptInfo = PromptInfo.Builder()
            .setTitle(title)
            .setDescription(description)
            .setAllowedAuthenticators(authenticators)
            .setConfirmationRequired(false)

        if (Build.VERSION.SDK_INT < 30){
            promptInfo.setNegativeButtonText("Cancel")
        }

        when(manager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                resultChannel.trySend(BiometricResult.HardwareUnavailable)
                return
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                resultChannel.trySend(BiometricResult.FeatureUnavailable)
                return
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                resultChannel.trySend(BiometricResult.AuthenticationNotSet)
                return
            }
            else -> Unit
        }

        val prompt = BiometricPrompt(
            fragment,
            object : BiometricPrompt.AuthenticationCallback(){
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    resultChannel.trySend(BiometricResult.AuthenticationError(errString.toString()))
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    resultChannel.trySend(BiometricResult.AuthenticationSuccess)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    resultChannel.trySend(BiometricResult.AuthenticationFailed)
                }
            }
        )

        prompt.authenticate(promptInfo.build())
    }

    sealed interface BiometricResult {
        object HardwareUnavailable: BiometricResult
        object FeatureUnavailable: BiometricResult
        data class AuthenticationError(val error: String): BiometricResult
        object AuthenticationFailed: BiometricResult
        object AuthenticationSuccess: BiometricResult
        object AuthenticationNotSet: BiometricResult
    }
}