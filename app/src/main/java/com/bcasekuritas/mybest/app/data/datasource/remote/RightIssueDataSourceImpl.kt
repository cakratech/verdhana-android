package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.domain.datasource.remote.RightIssueDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.ExerciseOrderListReq
import com.bcasekuritas.mybest.app.domain.dto.request.ExerciseSessionReq
import com.bcasekuritas.mybest.app.domain.dto.request.RightIssueInfoReq
import com.bcasekuritas.mybest.app.domain.dto.request.SendExerciseOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.SendOrderReq
import com.bcasekuritas.mybest.app.domain.dto.request.WithdrawExerciseOrderRequest
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.proto.bcas.CakraMessage
import com.bcasekuritas.rabbitmq.proto.bcas.CancelExerciseOrder
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseInfoRequest
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseInfoResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseOrder
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseOrderListRequest
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseOrderListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseSessionRequest
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseSessionResponse
import java.util.Date
import javax.inject.Inject

class RightIssueDataSourceImpl @Inject constructor(
    private val oltService: OLTService
) : RightIssueDataSource {

    override suspend fun getExerciseInfo(rightIssueInfoReq: RightIssueInfoReq): ExerciseInfoResponse? {
        val exerciseRequest = ExerciseInfoRequest.newBuilder()
            .setUserId(rightIssueInfoReq.userId)
            .setAccno(rightIssueInfoReq.accNo)
            .setSessionId(rightIssueInfoReq.sessionId)
            .build()

        return oltService.getRightIssueInfo(exerciseRequest)
    }

    override suspend fun getExerciseOrderList(exerciseOrderListReq: ExerciseOrderListReq): ExerciseOrderListResponse? {
        val exerciseOrderListRequest = ExerciseOrderListRequest.newBuilder()
            .setReqType(exerciseOrderListReq.reqType)
            .addAllReqInfo(exerciseOrderListReq.reqInfo)
            .setUserID(exerciseOrderListReq.userId)
            .setSessionId(exerciseOrderListReq.sessionId)
            .build()

        return oltService.getExerciseOrderList(exerciseOrderListRequest)

    }

    override suspend fun sendExerciseOrder(exerciseOrder: SendExerciseOrderRequest?) {
        val newExerciseOrder = ExerciseOrder.newBuilder()
            .setTrxCode(exerciseOrder?.trxCode)
            .setTrxDate(exerciseOrder?.trxDate ?: 0L)
            .setTrxType(exerciseOrder?.trxType ?: 0)
            .setAccNo(exerciseOrder?.accNo)
            .setStockCode(exerciseOrder?.stockCode)
            .setQty(exerciseOrder?.qty?: 0.0)
            .setPrice(exerciseOrder?.price?: 0.0)
            .setIpAddress(exerciseOrder?.ipAddress)
            .setInputBy(exerciseOrder?.inputBy)
            .setChannel(exerciseOrder?.channel?: 0)
            .setMediaSource(0)
            .setAmount(exerciseOrder?.amount?: 0.0)

        val cakraMessage = CakraMessage.newBuilder()
            .setType(CakraMessage.Type.EXERCISE_ORDER)
            .setExerciseOrder(newExerciseOrder)
            .setUserId(exerciseOrder?.inputBy)
            .setSendingTime(Date().time)
            .setSessionId(exerciseOrder?.sessionId)
            .build()

        oltService.sendOrder(cakraMessage)
    }

    override suspend fun sendWithdrawExerciseOrder(withdrawExerciseOrderRequest: WithdrawExerciseOrderRequest?) {
        val cancelExerciseOrder =  CancelExerciseOrder.newBuilder()
            .setTrxCode(withdrawExerciseOrderRequest?.trxCode)
            .setOrderID(withdrawExerciseOrderRequest?.orderId)
            .setInputBy(withdrawExerciseOrderRequest?.inputBy)
            .setIpAddress(withdrawExerciseOrderRequest?.ipAddress)
            .setAccNo(withdrawExerciseOrderRequest?.accNo)

        val cakraMessage = CakraMessage.newBuilder()
            .setType(CakraMessage.Type.CANCEL_EXERCISE_ORDER)
            .setCancelExerciseOrder(cancelExerciseOrder)
            .setUserId(withdrawExerciseOrderRequest?.inputBy)
            .setSessionId(withdrawExerciseOrderRequest?.sessionId)
            .setSendingTime(Date().time)
            .build()

        oltService.sendOrder(cakraMessage)
    }

    override suspend fun getExerciseSession(exerciseSessionReq: ExerciseSessionReq): ExerciseSessionResponse? {
        val request = ExerciseSessionRequest.newBuilder()
            .setUserId(exerciseSessionReq.userId)
            .setSessionId(exerciseSessionReq.sessionId)
            .build()

        return oltService.getExerciseSession(request)
    }
}