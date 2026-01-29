package com.bcasekuritas.mybest.app.domain.datasource.remote

import com.bcasekuritas.mybest.app.domain.dto.request.CompanyProfileRequest
import com.bcasekuritas.rabbitmq.proto.news.StockDetilCompanyProfileResponse

interface CompanyProfileDataSource {
    suspend fun getCompanyProfile(companyProfileRequest: CompanyProfileRequest?): StockDetilCompanyProfileResponse?
}