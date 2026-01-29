package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.domain.datasource.remote.GeneralDataSource
import com.bcasekuritas.rabbitmq.network.OLTService
import javax.inject.Inject

class GeneralDataSourceImpl @Inject constructor(
    private val oltService: OLTService
) : GeneralDataSource {

}