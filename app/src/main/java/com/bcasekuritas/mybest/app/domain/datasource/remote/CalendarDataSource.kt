package com.bcasekuritas.mybest.app.domain.datasource.remote

import com.bcasekuritas.mybest.app.domain.dto.request.CaCalendarbyCaDateInRangeReq
import com.bcasekuritas.rabbitmq.proto.news.CorporateActionCalendarGetResponse

interface CalendarDataSource {

        suspend fun getCalendarByDateInRangeRpc(caCalendarbyCaDateInRangeReq: CaCalendarbyCaDateInRangeReq): CorporateActionCalendarGetResponse?
}