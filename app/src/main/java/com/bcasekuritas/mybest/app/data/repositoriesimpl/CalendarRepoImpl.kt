package com.bcasekuritas.mybest.app.data.repositoriesimpl

import com.bcasekuritas.mybest.app.domain.datasource.remote.CalendarDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.CaCalendarbyCaDateInRangeReq
import com.bcasekuritas.mybest.app.domain.repositories.CalendarRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
import com.bcasekuritas.rabbitmq.proto.news.CorporateActionCalendarGetResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CalendarRepoImpl @Inject constructor(
    private val remoteSource: CalendarDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) : CalendarRepo{
    override suspend fun getCalendarByDateInRangeRpc(caCalendarbyCaDateInRangeReq: CaCalendarbyCaDateInRangeReq): Flow<Resource<CorporateActionCalendarGetResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getCalendarByDateInRangeRpc(caCalendarbyCaDateInRangeReq), DataSource.REMOTE))
    }
}