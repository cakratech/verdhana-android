package com.bcasekuritas.mybest.app.domain.datasource.remote

import com.bcasekuritas.mybest.app.domain.dto.request.ExerciseOrderListReq
import com.bcasekuritas.mybest.app.domain.dto.request.ExerciseSessionReq
import com.bcasekuritas.mybest.app.domain.dto.request.RightIssueInfoReq
import com.bcasekuritas.mybest.app.domain.dto.request.SendExerciseOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.WithdrawExerciseOrderRequest
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseInfoResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseOrderListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseSessionRequest
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseSessionResponse

interface RightIssueDataSource {

    suspend fun getExerciseInfo(rightIssueInfoReq: RightIssueInfoReq): ExerciseInfoResponse?

    suspend fun getExerciseOrderList(exerciseOrderListReq: ExerciseOrderListReq): ExerciseOrderListResponse?

    suspend fun sendExerciseOrder(exerciseOrder: SendExerciseOrderRequest?)

    suspend fun sendWithdrawExerciseOrder(withdrawExerciseOrderRequest: WithdrawExerciseOrderRequest?)

    suspend fun getExerciseSession(exerciseSessionReq: ExerciseSessionReq): ExerciseSessionResponse?
}