package com.bcasekuritas.mybest.ext.biometric

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.fragment.app.Fragment
import com.bcasekuritas.mybest.ext.biometric.new.CryptographyManager
import com.bcasekuritas.mybest.ext.constant.Const
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class BiometricPromptManager(
    private val fragment: Fragment,
    private val context: Context
) {
    private val resultChannel = Channel<BiometricResult>()
    val promptResults = resultChannel.receiveAsFlow()

    private val cryptographyManager = CryptographyManager()
    private val ciphertextWrapper
        get() = cryptographyManager.getCipherTextWrapperFromSharedPrefs(
            context,
            Const.SHARED_PREFS_FILENAME,
            Context.MODE_PRIVATE,
            Const.CIPHERTEXT_WRAPPER
        )

    fun showBiometricPrompt(
        title: String,
        description: String,
        onSuccess: ((Boolean) -> Unit)
    ) {
        val manager = BiometricManager.from(context)
        val authenticators = BIOMETRIC_STRONG

        val promptInfo = PromptInfo.Builder()
            .setTitle(title)
            .setDescription(description)
            .setAllowedAuthenticators(authenticators)
            .setNegativeButtonText("Cancel")

        if(Build.VERSION.SDK_INT < 30) {
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
                promptUserToEnrollBiometrics()
                return
            }
            else -> Unit
        }

        val prompt = BiometricPrompt(
            fragment,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    resultChannel.trySend(BiometricResult.AuthenticationError(errString.toString()))
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    result.cryptoObject?.cipher?.let {
                        resultChannel.trySend(BiometricResult.AuthenticationSuccess)
                    }
                    onSuccess(true)
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
        object  AuthenticationSuccess: BiometricResult
        object AuthenticationNotSet: BiometricResult
    }

    private fun encryptAndStoreToken(authenticationResult: BiometricPrompt.AuthenticationResult) {
        authenticationResult.cryptoObject?.cipher?.apply {
            SampleUser.token?.let { token->
                Log.d(TAG,"token: $token")
                val encryptServerTokenWrapper = cryptographyManager.encryptData(token, this)
                cryptographyManager.persistCipherTextWrapperToSharedPrefs(
                    encryptServerTokenWrapper,
                    context,
                    Const.SHARED_PREFS_FILENAME,
                    Context.MODE_PRIVATE,
                    Const.CIPHERTEXT_WRAPPER
                )
            }
        }
    }

    object SampleUser {
        var token:String?=null
        var username:String?=null
    }

    private fun promptUserToEnrollBiometrics() {
        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BiometricManager.Authenticators.BIOMETRIC_STRONG)
        }
        if (enrollIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(enrollIntent)
        } else {
            // Handle case where the device doesn't support enrollment activity
            resultChannel.trySend(BiometricResult.FeatureUnavailable)
        }
    }
}