package com.bcasekuritas.mybest.ext.interceptor

import com.bcasekuritas.mybest.ext.exeption.ResourceNotFoundException
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.HttpURLConnection

class ResourceNotFoundInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Chain): Response {
        val request: Request = chain.request()
        val response: Response = chain.proceed(request)
        if (response.code == HttpURLConnection.HTTP_NOT_FOUND) {
            throw ResourceNotFoundException(response)
        }
        return response
    }
}