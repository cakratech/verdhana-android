package com.bcasekuritas.mybest.app.domain

import com.bcasekuritas.mybest.ext.constant.NetworkCodes
import com.bcasekuritas.mybest.ext.exeption.ForbiddenException
import com.bcasekuritas.mybest.ext.exeption.NoNetworkException
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class GenericError(val code: Int, val message: String? = null) : ResultWrapper<Nothing>()
}

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    errorCodesMapper: ErrorCodesMapper,
    apiCall: suspend () -> T?
): ResultWrapper<T?> {
    return withContext(dispatcher) {
        try {
            val call = apiCall.invoke()
            ResultWrapper.Success(call)
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            when (throwable) {
                is NoNetworkException -> {
                    ResultWrapper.GenericError(
                        code = NetworkCodes.NO_CONNECTION,
                        message = errorCodesMapper.getMessage(NetworkCodes.NO_CONNECTION)
                    )
                }
                is SocketTimeoutException -> {
                    ResultWrapper.GenericError(
                        code = NetworkCodes.TIMEOUT_ERROR,
                        message = errorCodesMapper.getMessage(NetworkCodes.CONNECTION_ERROR)
                    )
                }
                is ConnectException -> {
                    ResultWrapper.GenericError(
                        code = NetworkCodes.TIMEOUT_ERROR,
                        message = errorCodesMapper.getMessage(NetworkCodes.CONNECTION_ERROR)
                    )
                }
                is TimeoutCancellationException -> {
                    ResultWrapper.GenericError(
                        code = NetworkCodes.TIMEOUT_ERROR,
                        message = errorCodesMapper.getMessage(NetworkCodes.CONNECTION_ERROR)
                    )
                }
                is IOException -> {
                    ResultWrapper.GenericError(
                        code = NetworkCodes.CONNECTION_ERROR,
                        message = errorCodesMapper.getMessage(NetworkCodes.CONNECTION_ERROR)
                    )
                }
                is ForbiddenException -> {
                    ResultWrapper.GenericError(
                        code = NetworkCodes.FORBIDDEN,
                        message = errorCodesMapper.getMessage(NetworkCodes.FORBIDDEN)
                    )
                }
                is HttpException -> {
                    val code = throwable.code()
                    ResultWrapper.GenericError(
                        code = code, message = convertErrorBody(throwable)
                    )
                }
                is NullPointerException -> {
                    val code = NetworkCodes.HTTP_NO_CONTENT
                    ResultWrapper.GenericError(
                        code = code, message = errorCodesMapper.getMessage(NetworkCodes.GENERIC_ERROR)
                    )
                }
                else -> {
                    ResultWrapper.GenericError(
                        code = NetworkCodes.GENERIC_ERROR,
                        message = errorCodesMapper.getMessage(NetworkCodes.GENERIC_ERROR)
                    )
                }
            }
        }
    }
}

// for custom error body
private fun convertErrorBody(throwable: HttpException): String? {
    try {
        val json = JSONTokener(throwable.response()?.errorBody()?.string()).nextValue()
        if (json is JSONObject || json is JSONArray) {
            val moshi = Moshi.Builder().build()
            val typeCustom = Types.newParameterizedType(ErrorResponse::class.java)
            val errorResponse = moshi.adapter<ErrorResponse>(typeCustom).fromJson(json.toString())

            errorResponse?.let { return it.message }
        }
        return null
    } catch (exception: Exception) {
        return null
    }
}

class ErrorResponse(val message: String? = "")