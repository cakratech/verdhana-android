package com.bcasekuritas.mybest.app.feature.managedevice

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.DeleteTrustedDeviceRequest
import com.bcasekuritas.mybest.app.domain.dto.request.TrustedDeviceReq
import com.bcasekuritas.mybest.app.domain.dto.response.TrustedDeviceItem
import com.bcasekuritas.mybest.app.domain.interactors.DeleteTrustedDeviceUsecase
import com.bcasekuritas.mybest.app.domain.interactors.GetTrustedDeviceUsecase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.common.getDeviceName
import com.bcasekuritas.rabbitmq.proto.bcas.DeleteDeviceResponse
import com.bcasekuritas.rabbitmq.proto.bcas.TrustedDeviceResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageDeviceViewModel @Inject constructor(
    private val getTrustedDeviceUsecase: GetTrustedDeviceUsecase,
    private val deleteTrustedDeviceUsecase: DeleteTrustedDeviceUsecase
): BaseViewModel() {

    val getTrustedDeviceResult = MutableLiveData<List<TrustedDeviceItem>>()
    val deleteDeviceResult = MutableLiveData<DeleteDeviceResponse?>()

    fun getTrustedDevice(userId: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = TrustedDeviceReq(userId, sessionId)

            getTrustedDeviceUsecase.invoke(request).collect() {resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            val data = arrayListOf<TrustedDeviceItem>()
                            it.data?.trustedDevicesList?.forEach {item ->
                                val deviceName = getDeviceName(item.deviceName)
                                val date = item.lastLogin.split(".").getOrNull(0) ?: ""
                                data.add(
                                    TrustedDeviceItem(
                                        item.deviceId,
                                        deviceName,
                                        date,
                                        item.lastIp,
                                        item.platform
                                    )
                                )
                            }
                            getTrustedDeviceResult.postValue(data)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun deleteDevice(userId: String, sessionId: String, deviceId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = DeleteTrustedDeviceRequest(userId, sessionId, deviceId)

            deleteTrustedDeviceUsecase.invoke(request).collect() {resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            deleteDeviceResult.postValue(it.data)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun getDeviceName(deviceNameRaw: String): String {
        return if (deviceNameRaw.contains("model:", ignoreCase = true)) {
            deviceNameRaw.replace("model:", "", ignoreCase = true).trim()
        } else {
            deviceNameRaw
        }
    }
}