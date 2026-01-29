package com.bcasekuritas.mybest.app.domain.datasource.stub

import com.bcasekuritas.mybest.app.domain.dto.response.source.SourceRes

interface SourceStubDataSource {

    suspend fun getSourceByCategory(): SourceRes
}