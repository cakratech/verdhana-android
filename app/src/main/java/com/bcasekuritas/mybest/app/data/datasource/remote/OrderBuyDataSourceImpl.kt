package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.domain.datasource.remote.OrderBuyDataSource
import com.bcasekuritas.rabbitmq.network.OLTService
import javax.inject.Inject

class OrderBuyDataSourceImpl @Inject constructor(
    private val oltService: OLTService
) : OrderBuyDataSource {

}