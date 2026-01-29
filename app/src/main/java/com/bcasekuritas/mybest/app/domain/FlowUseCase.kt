package com.bcasekuritas.mybest.app.domain

import com.bcasekuritas.mybest.app.domain.subscribers.FailureData
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.constant.NetworkCodes
import com.bcasekuritas.mybest.ext.exeption.ForbiddenException
import com.bcasekuritas.mybest.ext.exeption.NoNetworkException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * Executes business logic in its execute method and keep posting updates to the resulst a
 * [Result<R>].
 */
abstract class FlowUseCase<in P, R>() {
    suspend operator fun invoke(parameters: P? = null): Flow<Resource<R>> = execute(parameters)
        .catch { e -> emit(Resource.Failure(handleException(e))) }

    protected abstract suspend fun execute(parameters: P? = null): Flow<Resource<R>>

    private fun handleException(throwable: Throwable): FailureData {
        return when (throwable) {
            is NoNetworkException -> {
                FailureData(
                    code = NetworkCodes.NO_CONNECTION,
                    message = throwable.localizedMessage
                )
            }
            is SocketTimeoutException -> {
                FailureData(
                    code = NetworkCodes.TIMEOUT_ERROR,
                    message = throwable.localizedMessage
                )
            }
            is ConnectException -> {
                FailureData(
                    code = NetworkCodes.TIMEOUT_ERROR,
                    message = throwable.localizedMessage
                )
            }
            is TimeoutCancellationException -> {
                FailureData(
                    code = NetworkCodes.TIMEOUT_ERROR,
                    message = throwable.localizedMessage
                )
            }
            is IOException -> {
                FailureData(
                    code = NetworkCodes.CONNECTION_ERROR,
                    message = throwable.localizedMessage
                )
            }
            is ForbiddenException -> {
                FailureData(
                    code = NetworkCodes.FORBIDDEN,
                    message = throwable.localizedMessage
                )
            }
            is HttpException -> {
                FailureData(
                    code = throwable.code(),
                    message = throwable.localizedMessage
                )
            }
            is NullPointerException -> {
                FailureData(
                    code = NetworkCodes.HTTP_NO_CONTENT,
                    message = throwable.localizedMessage
                )
            }
            else -> {
                FailureData(
                    code = NetworkCodes.GENERIC_ERROR,
                    message = throwable.localizedMessage
                )
            }
        }
    }
}