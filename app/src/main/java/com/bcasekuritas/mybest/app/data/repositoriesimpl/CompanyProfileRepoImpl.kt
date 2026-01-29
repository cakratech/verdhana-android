package com.bcasekuritas.mybest.app.data.repositoriesimpl

import com.bcasekuritas.mybest.app.domain.datasource.remote.CompanyProfileDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.CompanyProfileRequest
import com.bcasekuritas.mybest.app.domain.repositories.CompanyProfileRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
import com.bcasekuritas.rabbitmq.proto.news.StockDetilCompanyProfileResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CompanyProfileRepoImpl @Inject constructor(
    private val remoteSource: CompanyProfileDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) : CompanyProfileRepo {

    override suspend fun getCompanyProfile(companyProfRequest: CompanyProfileRequest?): Flow<Resource<StockDetilCompanyProfileResponse?>> = flow{
        emit(
            Resource.Success(
                data = remoteSource.getCompanyProfile(companyProfRequest),
                DataSource.REMOTE
            )
        )
    }
}