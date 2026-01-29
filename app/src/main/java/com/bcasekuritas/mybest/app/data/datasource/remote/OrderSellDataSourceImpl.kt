package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.domain.datasource.remote.OrderSellDataSource
import com.bcasekuritas.rabbitmq.network.OLTService
import javax.inject.Inject

class OrderSellDataSourceImpl @Inject constructor(
    private val oltService: OLTService
) : OrderSellDataSource {

}