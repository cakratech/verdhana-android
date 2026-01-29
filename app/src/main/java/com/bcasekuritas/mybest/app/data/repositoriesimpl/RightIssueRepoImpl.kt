package com.bcasekuritas.mybest.app.data.repositoriesimpl

import com.bcasekuritas.mybest.app.domain.datasource.remote.RightIssueDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.ExerciseOrderListReq
import com.bcasekuritas.mybest.app.domain.dto.request.ExerciseSessionReq
import com.bcasekuritas.mybest.app.domain.dto.request.RightIssueInfoReq
import com.bcasekuritas.mybest.app.domain.dto.request.SendExerciseOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.WithdrawExerciseOrderRequest
import com.bcasekuritas.mybest.app.domain.repositories.RightIssueRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseInfoResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseOrderListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ExerciseSessionResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RightIssueRepoImpl @Inject constructor(
    private val remoteSource: RightIssueDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) : RightIssueRepo {

    override suspend fun getRightIssueInfo(rightIssueInfoReq: RightIssueInfoReq): Flow<Resource<ExerciseInfoResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getExerciseInfo(rightIssueInfoReq), DataSource.REMOTE))
    }

    override suspend fun getExerciseOrderList(exerciseOrderListReq: ExerciseOrderListReq): Flow<Resource<ExerciseOrderListResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getExerciseOrderList(exerciseOrderListReq), DataSource.REMOTE))
    }

    override suspend fun sendExerciseOrder(exerciseOrderRequest: SendExerciseOrderRequest?) {
        remoteSource.sendExerciseOrder(exerciseOrderRequest)
    }

    override suspend fun sendWithdrawExerciseOrder(withdrawExerciseOrderRequest: WithdrawExerciseOrderRequest?) {
        remoteSource.sendWithdrawExerciseOrder(withdrawExerciseOrderRequest)
    }

    override suspend fun getExerciseSession(exerciseSessionReq: ExerciseSessionReq): Flow<Resource<ExerciseSessionResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getExerciseSession(exerciseSessionReq), DataSource.REMOTE))
    }
}