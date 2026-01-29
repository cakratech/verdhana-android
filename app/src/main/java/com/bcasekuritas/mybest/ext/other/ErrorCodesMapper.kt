package com.bcasekuritas.mybest.ext.other

import android.content.Context
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.ext.constant.NetworkCodes
import javax.inject.Inject

class ErrorCodesMapper @Inject constructor(
    val appContext: Context
) {

    fun getMessage(errorCode: Int) = when (errorCode) {
        NetworkCodes.NO_CONNECTION -> appContext.getString(R.string.error_network_description)
        NetworkCodes.CONNECTION_ERROR,
        NetworkCodes.TIMEOUT_ERROR -> appContext.getString(R.string.error_network_interupted_description)
        NetworkCodes.FORBIDDEN -> appContext.getString(R.string.error_forbidden_description)
        NetworkCodes.HTTP_CONFLICT, -> appContext.getString(R.string.error_network_interupted_description)
        NetworkCodes.HTTP_NO_CONTENT, -> appContext.getString(R.string.error_network_null_description)
        else -> appContext.getString(R.string.error_network_500)
    }
}
