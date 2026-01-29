package com.bcasekuritas.mybest.ext.exeption

import okhttp3.Response

class ResourceNotFoundException(var response: Response) : RuntimeException() {
    override val message: String
        get() = try {
            if ("?Expires" in response.request.url.toString()) {
                response.request.url.toString().substringBefore("?Expires")
            } else {
                response.request.url.toString()
            }
        } catch (e: Exception) {
            "no_url"
        }
}