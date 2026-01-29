package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.domain.datasource.remote.CalendarDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.CaCalendarbyCaDateInRangeReq
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.proto.news.CorporateActionCalendarGetRequest
import com.bcasekuritas.rabbitmq.proto.news.CorporateActionCalendarGetResponse
import javax.inject.Inject

class CalendarDataSourceImpl @Inject constructor(
    private val oltService: OLTService
) : CalendarDataSource {
    override suspend fun getCalendarByDateInRangeRpc(caCalendarbyCaDateInRangeReq: CaCalendarbyCaDateInRangeReq): CorporateActionCalendarGetResponse? {

        val calendarReq = CorporateActionCalendarGetRequest.newBuilder()
            .setUserId(caCalendarbyCaDateInRangeReq.userId)
            .setSessionId(caCalendarbyCaDateInRangeReq.sessionId)
            .setYear(caCalendarbyCaDateInRangeReq.year.toInt())
            .setMonth(caCalendarbyCaDateInRangeReq.month.toInt())
            .build()

    return oltService.getCalendarByDateInRangeRpc(calendarReq)
    }
}