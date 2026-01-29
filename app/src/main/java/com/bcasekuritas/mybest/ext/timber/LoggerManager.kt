/*
package com.bcasekuritas.mybest.ext.timber

import android.content.Context
import android.os.Build
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.CustomKeysAndValues
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.bcasekuritas.mybest.ext.exeption.ResourceNotFoundException
import retrofit2.HttpException
import java.util.*
import javax.inject.Inject

class LoggerManager @Inject constructor(context: Context) {
    private val builder = CustomKeysAndValues.Builder()
    private val mFirebaseCrashlytics = FirebaseCrashlytics.getInstance()
    private val mFirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    fun logService(
        nameService: String,
        throwable: Throwable,
        userId: String
    ) {
        if (throwable is HttpException || throwable is ResourceNotFoundException) {
            when (throwable) {
                is HttpException -> {
                    builder.putString("service_link",
                        throwable.response()!!.raw().request.url.toString()) }
                is ResourceNotFoundException -> {
                    builder.putString("service_link",
                        throwable.message) }
                else -> {
                    builder.putString("service_link", "no_url") }
            }
            builder.putString("service_name", nameService)
            mFirebaseCrashlytics.setUserId(userId)
            mFirebaseCrashlytics.setCustomKeys(builder.build())
            mFirebaseCrashlytics.log(throwable.toString())
            mFirebaseCrashlytics.recordException(RuntimeException(nameService))
        }
    }

    fun logServiceInt(
        nameService: String,
        intVal: Int,
        throwable: Throwable, userId: String
    ) {
        if (throwable is HttpException || throwable is ResourceNotFoundException) {
            when (throwable) {
                is HttpException -> {
                    builder.putString("service_link",
                        throwable.response()!!.raw().request.url.toString()) }
                is ResourceNotFoundException -> {
                    builder.putString("service_link",
                        throwable.message) }
                else -> {
                    builder.putString("service_link", "no_url") }
            }
            builder.putString("service_name", nameService)
            builder.putString("param_int", intVal.toString())
            mFirebaseCrashlytics.setUserId(userId)
            mFirebaseCrashlytics.setCustomKeys(builder.build())
            mFirebaseCrashlytics.log(throwable.toString())
            mFirebaseCrashlytics.recordException(RuntimeException(nameService))
        }
    }

    fun logServiceInts(
        nameService: String,
        intOne: Int,
        intTwo: Int,
        throwable: Throwable, userId: String
    ) {
        if (throwable is HttpException || throwable is ResourceNotFoundException) {
            when (throwable) {
                is HttpException -> {
                    builder.putString("service_link",
                        throwable.response()!!.raw().request.url.toString()) }
                is ResourceNotFoundException -> {
                    builder.putString("service_link",
                        throwable.message) }
                else -> {
                    builder.putString("service_link", "no_url") }
            }
            builder.putString("service_name", nameService)
            builder.putString("param_int_one", intOne.toString())
            builder.putString("param_int_two", intTwo.toString())
            mFirebaseCrashlytics.setUserId(userId)
            mFirebaseCrashlytics.setCustomKeys(builder.build())
            mFirebaseCrashlytics.log(throwable.toString())
            mFirebaseCrashlytics.recordException(RuntimeException(nameService))
        }
    }

    fun logServiceStr(
        nameService: String,
        strVal: String,
        throwable: Throwable, userId: String
    ) {
        if (throwable is HttpException || throwable is ResourceNotFoundException) {
            when (throwable) {
                is HttpException -> {
                    builder.putString("service_link",
                        throwable.response()!!.raw().request.url.toString()) }
                is ResourceNotFoundException -> {
                    builder.putString("service_link",
                        throwable.message) }
                else -> {
                    builder.putString("service_link", "no_url") }
            }
            builder.putString("service_name", nameService)
            builder.putString("param_str", strVal)
            mFirebaseCrashlytics.setUserId(userId)
            mFirebaseCrashlytics.setCustomKeys(builder.build())
            mFirebaseCrashlytics.log(throwable.toString())
            mFirebaseCrashlytics.recordException(RuntimeException(nameService))
        }
    }

    fun logServiceStrs(
        nameService: String,
        strOne: String,
        strTwo: String,
        throwable: Throwable, userId: String
    ) {
        if (throwable is HttpException || throwable is ResourceNotFoundException) {
            when (throwable) {
                is HttpException -> {
                    builder.putString("service_link",
                        throwable.response()!!.raw().request.url.toString()) }
                is ResourceNotFoundException -> {
                    builder.putString("service_link",
                        throwable.message) }
                else -> {
                    builder.putString("service_link", "no_url") }
            }
            builder.putString("service_name", nameService)
            builder.putString("param_str_one", strOne)
            builder.putString("param_str_two", strTwo)
            mFirebaseCrashlytics.setUserId(userId)
            mFirebaseCrashlytics.setCustomKeys(builder.build())
            mFirebaseCrashlytics.log(throwable.toString())
            mFirebaseCrashlytics.recordException(RuntimeException(nameService))
        }
    }

    fun logServiceIntStr(
        nameService: String,
        intVal: Int,
        strVal: String,
        throwable: Throwable, userId: String
    ) {
        if (throwable is HttpException || throwable is ResourceNotFoundException) {
            when (throwable) {
                is HttpException -> {
                    builder.putString("service_link",
                        throwable.response()!!.raw().request.url.toString()) }
                is ResourceNotFoundException -> {
                    builder.putString("service_link",
                        throwable.message) }
                else -> {
                    builder.putString("service_link", "no_url") }
            }
            builder.putString("service_name", nameService)
            builder.putString("param_int", intVal.toString())
            builder.putString("param_str", strVal)
            mFirebaseCrashlytics.setUserId(userId)
            mFirebaseCrashlytics.setCustomKeys(builder.build())
            mFirebaseCrashlytics.log(throwable.toString())
            mFirebaseCrashlytics.recordException(RuntimeException(nameService))
        }
    }

    fun logServiceAny(
        nameService: String,
        anyVal: Any,
        throwable: Throwable, userId: String
    ) {
        if (throwable is HttpException || throwable is ResourceNotFoundException) {
            when (throwable) {
                is HttpException -> {
                    builder.putString("service_link",
                        throwable.response()!!.raw().request.url.toString()) }
                is ResourceNotFoundException -> {
                    builder.putString("service_link",
                        throwable.message) }
                else -> {
                    builder.putString("service_link", "no_url") }
            }
            builder.putString("service_name", nameService)
            builder.putString("param_any", anyVal.toString())
            mFirebaseCrashlytics.setUserId(userId)
            mFirebaseCrashlytics.setCustomKeys(builder.build())
            mFirebaseCrashlytics.log(throwable.toString())
            mFirebaseCrashlytics.recordException(RuntimeException(nameService))
        }
    }

    fun logServiceIntAny(
        nameService: String,
        intVal: Int,
        anyVal: Any,
        throwable: Throwable,
        userId: String
    ) {
        if (throwable is HttpException || throwable is ResourceNotFoundException) {
            when (throwable) {
                is HttpException -> {
                    builder.putString("service_link",
                        throwable.response()!!.raw().request.url.toString()) }
                is ResourceNotFoundException -> {
                    builder.putString("service_link",
                        throwable.message) }
                else -> {
                    builder.putString("service_link", "no_url") }
            }
            builder.putString("service_name", nameService)
            builder.putString("param_int", intVal.toString())
            builder.putString("param_any", anyVal.toString())
            mFirebaseCrashlytics.setUserId(userId)
            mFirebaseCrashlytics.setCustomKeys(builder.build())
            mFirebaseCrashlytics.log(throwable.toString())
            mFirebaseCrashlytics.recordException(RuntimeException(nameService))
        }
    }

    fun logScreen(screenName: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    init {
        builder.putString("abi", Build.SUPPORTED_ABIS[0])
        builder.putString("locale", if (getLocale(context) != null) getLocale(context).toString() else "null")
        builder.putBoolean("isGooglePlayServicesAvailable", isGooglePlayServicesAvailable(context))
        getInstallSource(context)?.let {
            builder.putString("installSource", it)
        }
    }

    */
/**
     * Retrieve the locale information for the app.
     *//*

    @Suppress("DEPRECATION")
    private fun getLocale(context: Context): Locale? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.resources.configuration.locales[0]
            } else {
                context.resources.configuration.locale
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun isGooglePlayServicesAvailable(context: Context): Boolean {
        return GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
    }

    private fun getInstallSource(context: Context): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return context.packageManager.getInstallSourceInfo(context.packageName).initiatingPackageName
        }
        return "null"
    }
}*/
