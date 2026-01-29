package com.bcasekuritas.mybest.ext.biometric.new

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.google.gson.Gson
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

interface CryptographyManager {

    fun getInitializedCipherForEncryption(keyName:String):Cipher

    fun getInitializedCipherForDecryption(keyName: String, initializationVector:ByteArray) : Cipher

    fun encryptData(text:String, cipher: Cipher): CipherTextWrapper

    fun decryptData(cipherText:ByteArray, cipher:Cipher):String

    fun encryptBiometric(values: String, keyName: String): String
    fun  decryptBiometric(encryptedData: String, keyName: String): String

    fun persistCipherTextWrapperToSharedPrefs(
        cipherTextWrapper: CipherTextWrapper,
        context: Context,
        filename:String,
        mode:Int,
        prefKey:String
    )

    fun getCipherTextWrapperFromSharedPrefs(
        context: Context,
        filename:String,
        mode: Int,
        prefKey: String
    ): CipherTextWrapper?

}

fun CryptographyManager(): CryptographyManager = CryptographyManagerImpl()

private class CryptographyManagerImpl: CryptographyManager {

    private  val KEY_SIZE = 128
    private  val ANDROID_KEY_STORE = "AndroidKeyStore"
    private  val ENCRYPTED_BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
    private  val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
    private  val ENCRYPTION_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private  val GCM_TAG_LENGTH = 128
    private  val GCM_IV_LENGTH = 12

    private fun generateSecretKey(keyName: String): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)
        keyStore.getKey(keyName, null)?.let { return it as SecretKey }

        val paramsBuilder = KeyGenParameterSpec.Builder(
            keyName,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).apply {
            setBlockModes(ENCRYPTED_BLOCK_MODE)
            setEncryptionPaddings(ENCRYPTION_PADDING)
            setKeySize(KEY_SIZE)
            setUserAuthenticationRequired(true)
        }

        val keyGenParams = paramsBuilder.build()
        val keyGenerator = KeyGenerator.getInstance(
            ENCRYPTION_ALGORITHM,
            ANDROID_KEY_STORE
        )
        keyGenerator.init(keyGenParams)
        return keyGenerator.generateKey()
    }

    override fun encryptBiometric(values: String, keyName: String): String {
        val cipher = getCipher()
        val secretKey = generateSecretKey(keyName)

        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(values.toByteArray())
        val ivAndEncryptedData = ByteArray(iv.size + encryptedBytes.size)
        System.arraycopy(iv, 0, ivAndEncryptedData, 0, iv.size)
        System.arraycopy(encryptedBytes, 0, ivAndEncryptedData, iv.size, encryptedBytes.size)
        return Base64.encodeToString(ivAndEncryptedData, Base64.DEFAULT)
    }

    override fun decryptBiometric(encryptedData: String, keyName: String): String {
        val decodedBytes = Base64.decode(encryptedData, Base64.DEFAULT)
        val iv = decodedBytes.copyOfRange(0, GCM_IV_LENGTH)
        val encryptedBytes = decodedBytes.copyOfRange(GCM_IV_LENGTH, decodedBytes.size)

        val cipher = getCipher()
        val secretKey = generateSecretKey(keyName)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }

    private fun getCipher(): Cipher {
        val transformation = "$ENCRYPTION_ALGORITHM/$ENCRYPTED_BLOCK_MODE/$ENCRYPTION_PADDING"
        return Cipher.getInstance(transformation)
    }

    override fun getInitializedCipherForEncryption(keyName: String): Cipher {
        val cipher = getCipher()
        val secretKey = generateSecretKey(keyName)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return cipher
    }

    override fun getInitializedCipherForDecryption(
        keyName: String,
        initializationVector: ByteArray
    ): Cipher {
        val cipher = getCipher()
        val secretKey = generateSecretKey(keyName)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, initializationVector))
        return cipher
    }

    override fun encryptData(text: String, cipher: Cipher): CipherTextWrapper {
        val cipherText = cipher.doFinal(text.toByteArray(Charset.forName("UTF-8")))
        return CipherTextWrapper(cipherText,cipher.iv)
    }

    override fun decryptData(cipherText: ByteArray, cipher: Cipher): String {
        val text = cipher.doFinal(cipherText)
        return String(text, Charset.forName("UTF-8"))
    }

    override fun persistCipherTextWrapperToSharedPrefs(
        cipherTextWrapper: CipherTextWrapper,
        context: Context,
        filename: String,
        mode: Int,
        prefKey: String
    ) {
        val json = Gson().toJson(cipherTextWrapper)
        context.getSharedPreferences(filename,mode).edit().putString(prefKey,json).apply()
    }

    override fun getCipherTextWrapperFromSharedPrefs(
        context: Context,
        filename: String,
        mode: Int,
        prefKey: String
    ): CipherTextWrapper? {
        val json = context.getSharedPreferences(filename,mode).getString(prefKey, null)
        return Gson().fromJson(json, CipherTextWrapper::class.java)
    }
}

