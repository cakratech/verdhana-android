package com.bcasekuritas.mybest.app.domain.repositories

import com.bcasekuritas.mybest.app.domain.dto.request.ExerciseOrderListReq
import com.bcasekuritas.mybest.app.domain.dto.request.ExerciseSessionReq
import com.bcasekuritas.mybest.app.domain.dto.request.RightIssueInfoReq
import com.bcasekuritas.mybest.app.domain.dto.request.SendExerciseOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.WithdrawExerciseOrderRequest
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseInfoResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseOrderListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseSessionResponse
import kotlinx.coroutines.flow.Flow

interface RightIssueRepo {

    suspend fun getRightIssueInfo(rightIssueInfoReq: RightIssueInfoReq): Flow<Resource<ExerciseInfoResponse?>>

    suspend fun getExerciseOrderList(exerciseOrderListReq: ExerciseOrderListReq): Flow<Resource<ExerciseOrderListResponse?>>

    suspend fun sendExerciseOrder(exerciseOrderRequest: SendExerciseOrderRequest?)

    suspend fun sendWithdrawExerciseOrder(withdrawExerciseOrderRequest: WithdrawExerciseOrderRequest?)

    suspend fun getExerciseSession(exerciseSessionReq: ExerciseSessionReq): Flow<Resource<ExerciseSessionResponse?>>
}