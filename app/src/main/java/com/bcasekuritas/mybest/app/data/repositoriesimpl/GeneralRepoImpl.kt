package com.bcasekuritas.mybest.app.data.repositoriesimpl

import com.bcasekuritas.mybest.app.domain.datasource.remote.GeneralDataSource
import com.bcasekuritas.mybest.app.domain.repositories.GeneralRepo
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
import javax.inject.Inject

class GeneralRepoImpl @Inject constructor(
    private val remoteSource: GeneralDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) : GeneralRepo {

}