package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.domain.datasource.remote.CompanyProfileDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.CompanyProfileRequest
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.proto.news.StockDetilCompanyProfileRequest
import com.bcasekuritas.rabbitmq.proto.news.StockDetilCompanyProfileResponse
import javax.inject.Inject

class CompanyProfileDataSourceImpl @Inject constructor(
    private val oltService: OLTService
) : CompanyProfileDataSource {

    override suspend fun getCompanyProfile(companyProfileRequest: CompanyProfileRequest?): StockDetilCompanyProfileResponse? {
        val stockDetilCompanyProfileRequest = StockDetilCompanyProfileRequest
            .newBuilder()
            .setUserId(companyProfileRequest?.userId)
            .setSessionId(companyProfileRequest?.sessionId)
            .setStockCode(companyProfileRequest?.stockCode)
            .build()

        return oltService.getCompanyProfile(stockDetilCompanyProfileRequest)
    }
}