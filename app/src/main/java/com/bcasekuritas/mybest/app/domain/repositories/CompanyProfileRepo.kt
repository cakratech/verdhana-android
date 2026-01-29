package com.bcasekuritas.mybest.app.domain.repositories

import com.bcasekuritas.mybest.app.domain.dto.request.CompanyProfileRequest
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.news.StockDetilCompanyProfileResponse
import kotlinx.coroutines.flow.Flow

interface CompanyProfileRepo {
    suspend fun getCompanyProfile(companyProfRequest: CompanyProfileRequest?): Flow<Resource<StockDetilCompanyProfileResponse?>>
}