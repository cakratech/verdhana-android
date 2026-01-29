package com.bcasekuritas.mybest.app.domain.repositories

import com.bcasekuritas.mybest.app.domain.dto.request.CaCalendarbyCaDateInRangeReq
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.news.CorporateActionCalendarGetResponse
import kotlinx.coroutines.flow.Flow

interface CalendarRepo {
    suspend fun getCalendarByDateInRangeRpc(caCalendarbyCaDateInRangeReq: CaCalendarbyCaDateInRangeReq):  Flow<Resource<CorporateActionCalendarGetResponse?>>
}