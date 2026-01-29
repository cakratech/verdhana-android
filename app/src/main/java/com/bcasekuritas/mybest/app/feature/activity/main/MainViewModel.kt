package com.bcasekuritas.mybest.app.feature.activity.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.entity.SessionObject
import com.bcasekuritas.mybest.app.domain.dto.request.LogoutReq
import com.bcasekuritas.mybest.app.domain.dto.request.OrderListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.SaveDeviceTokenReq
import com.bcasekuritas.mybest.app.domain.dto.request.ValidateSessionReq
import com.bcasekuritas.mybest.app.domain.dto.response.OltOrder
import com.bcasekuritas.mybest.app.domain.dto.response.PortfolioOrderItem
import com.bcasekuritas.mybest.app.domain.interactors.DeleteSessionsDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetLogoutUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetOrderListUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSessionPinDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetValidateSessionByPinUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetValidateSessionUseCase
import com.bcasekuritas.mybest.app.domain.interactors.InsertSessionDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SaveDeviceTokenUsecase
import com.bcasekuritas.mybest.app.domain.interactors.StartAllConsumerUseCase
import com.bcasekuritas.mybest.app.domain.interactors.StopAppNotificationUseCase
import com.bcasekuritas.mybest.app.domain.interactors.StopOrderReplyUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SubscribeAppNotificationUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SubscribeOrderReplyUseCase
import com.bcasekuritas.mybest.app.domain.interactors.UnsubscribeAppNotificationUseCase
import com.bcasekuritas.mybest.app.domain.interactors.UnsubscribeOrderReplyUseCase
import com.bcasekuritas.mybest.app.domain.interactors.UpdateSessionPinDaoUseCase
import com.bcasekuritas.mybest.app.domain.repositories.AuthRepo
import com.bcasekuritas.mybest.app.domain.repositories.OrderRepo
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.event.SingleLiveEvent
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.proto.bcas.AppNotification
import com.bcasekuritas.rabbitmq.proto.bcas.LogoutResponse
import com.bcasekuritas.rabbitmq.proto.bcas.OrderListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SaveDeviceTokenResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ValidateSessionResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val subscribeAppNotificationUseCase: SubscribeAppNotificationUseCase,
    private val unsubscribeAppNotificationUseCase: UnsubscribeAppNotificationUseCase,
    private val subscribeOrderReplyUseCase: SubscribeOrderReplyUseCase,
    private val unsubscribeOrderReplyUseCase: UnsubscribeOrderReplyUseCase,
    private val stopAppNotificationUseCase: StopAppNotificationUseCase,
    private val insertSessionDaoUseCase: InsertSessionDaoUseCase,
    private val saveDeviceTokenUsecase: SaveDeviceTokenUsecase,
    private val updateSessionPinDaoUseCase: UpdateSessionPinDaoUseCase,
    private val stopOrderReplyUseCase: StopOrderReplyUseCase,
    private val getOrderListUseCase: GetOrderListUseCase,
    private val authRepo: AuthRepo,
    private val orderRepo: OrderRepo,
    private val logoutUseCase: GetLogoutUseCase,
    val imqConnectionListener: IMQConnectionListener,
    private val validateSessionUseCase: GetValidateSessionUseCase,
    private val validateSessionByPinUseCase: GetValidateSessionByPinUseCase,
    private val getSessionPin: GetSessionPinDaoUseCase,
    private val deleteSessions: DeleteSessionsDaoUseCase,
    private val startAllConsumerUseCase: StartAllConsumerUseCase,
) : BaseViewModel() {

    val getOrderListResult = MutableLiveData<OrderListResponse?>()
    val getLogoutResult = MutableLiveData<LogoutResponse?>()
    val getLogoutForceUpdateResult = MutableLiveData<LogoutResponse?>()
    val getValidateSessionResult = MutableLiveData<ValidateSessionResponse?>()
    val getValidateSessionByPinResult = MutableLiveData<ValidateSessionResponse?>()
    val saveTokenDeviceResult = MutableLiveData<Resource<SaveDeviceTokenResponse?>>()
    val getSessionPinResult = MutableLiveData<Long?>()
    val startAllConsumerResult = MutableLiveData<Boolean?>()

    // InsertSessionPin
    fun insertSession(sessionObject: SessionObject) {
        viewModelScope.launch(Dispatchers.IO) {
            insertSessionDaoUseCase.insertSessionDao(sessionObject)
        }
    }

    // Shared with orerList to update
    private val _orderListData = MutableLiveData<List<PortfolioOrderItem>>()
    val orderListData: LiveData<List<PortfolioOrderItem>> get() = _orderListData

    fun updateData(newData: List<PortfolioOrderItem>) {
        viewModelScope.launch(Dispatchers.Main) {
            _orderListData.value = newData
        }
    }

    fun saveFCMToken(userId: String, sessionId: String, fcmToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val saveDeviceTokenReq = SaveDeviceTokenReq(userId, sessionId, fcmToken)
            saveDeviceTokenUsecase.invoke(saveDeviceTokenReq).collect() {resource ->
                resource.let {
                    saveTokenDeviceResult.postValue(it)
                }
            }
        }

    }

    fun getOrderList(userId: String, accNo: String, sessionId: String, includeTrade: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val orderListRequest = OrderListRequest(userId, accNo, sessionId, includeTrade)

            getOrderListUseCase.invoke(orderListRequest).collect() { resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            if (it.data != null) {
                                val orderListItemMapper = ArrayList<PortfolioOrderItem>()
                                it.data.ordersList?.map { data ->
                                    orderListItemMapper.add(
                                        PortfolioOrderItem(
                                            data.odId,
                                            data.exordid,
                                            data.odTime,
                                            data.ostatus,
                                            data.bs,
                                            data.ordertype,
                                            data.stockcode,
                                            data.remark,
                                            data.oprice,
                                            data.oqty,
                                            data.mqty,
                                            timeInForce = data.timeInForce,
                                            ordPeriod = data.ordperiod.toLong()
                                        )
                                    )
                                }
                                orderListItemMapper.sortByDescending { item -> item.time }
                                updateData(orderListItemMapper)
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }


    // NEW METHOD FOR LIVE DATA

    val appNotificationLiveData: LiveData<AppNotification>
        get() = authRepo.appNotificationLiveData

    val orderReplyLiveData: LiveData<OltOrder>
        get() = orderRepo.orderReplyLiveData

    val newOrderNotifLiveData: SingleLiveEvent<Boolean>
        get() = orderRepo.newOrderNotifLiveData

    /** Subscribe */
    fun subscribeAppNotification(sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            subscribeAppNotificationUseCase.subscribeAppNotification(sessionId)
        }
    }
    fun subscribeOrderReply(accNo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            subscribeOrderReplyUseCase.subscribeOrderReply(accNo)
        }
    }

    /** Unsubscribe */
    fun unSubscribeAppNotification(sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            unsubscribeAppNotificationUseCase.unsubscribeAppNotification(sessionId)
        }
    }
    fun unsubscribeOrderReply(accNo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            unsubscribeOrderReplyUseCase.unsubscribeOrderReply(accNo)
        }
    }

    /** Stop Consume */
    fun stopAppNotification() {
        viewModelScope.launch(Dispatchers.IO) {
            stopAppNotificationUseCase.stopAppNotification()
        }
    }
    fun stopOrderReply() {
        viewModelScope.launch(Dispatchers.IO) {
            stopOrderReplyUseCase.stopAppNotification()
        }
    }

    /** Clear Live Data*/
    fun clearAppNotification() {
        (appNotificationLiveData as MutableLiveData).value = null
    }

    fun clearOrderReply() {
        (orderReplyLiveData as MutableLiveData).value = null
    }

    fun getLogout(userId: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val logoutRequest = LogoutReq(userId, sessionId)

            logoutUseCase.invoke(logoutRequest).collect() {resource ->
                when (resource) {
                    is Resource.Success -> {
                        getLogoutResult.postValue(resource.data)
                    }
                    else -> {}
                }
            }
        }
    }

    fun getLogoutForceUpdate(userId: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val logoutRequest = LogoutReq(userId, sessionId)

            logoutUseCase.invoke(logoutRequest).collect() {resource ->
                when (resource) {
                    is Resource.Success -> {
                        getLogoutForceUpdateResult.postValue(resource.data)
                    }
                    else -> {}
                }
            }
        }
    }

    fun validateSession(userId: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val validateSessionRequest = ValidateSessionReq(userId, sessionId)

            validateSessionUseCase.invoke(validateSessionRequest).collect() {resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            getValidateSessionResult.postValue(it.data)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun validateSessionByPin(userId: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val validateSessionRequest = ValidateSessionReq(userId, sessionId)

            validateSessionByPinUseCase.invoke(validateSessionRequest).collect() {resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            getValidateSessionByPinResult.postValue(it.data)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun getSessionPin(userId: String){
        CoroutineScope(Dispatchers.IO).launch {
            getSessionPin.invoke(userId).collect() {resource ->
                when (resource){
                    is Resource.Success -> {
                        getSessionPinResult.postValue(resource.data)
                    }

                    else -> {}
                }
            }
        }
    }

    fun deleteSessionPin(){
        CoroutineScope(Dispatchers.IO).launch {
            deleteSessions.deleteSessionsDao()
        }
    }

    fun startSubsRecovered() {
        viewModelScope.launch(Dispatchers.IO) {
            startAllConsumerUseCase.invoke(false).collect() {resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            startAllConsumerResult.postValue(it.data)
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}