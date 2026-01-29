package com.bcasekuritas.mybest.app.data.datasource.stub

import android.content.Context
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.domain.datasource.stub.SourceStubDataSource
import com.bcasekuritas.mybest.app.domain.dto.response.source.Source
import com.bcasekuritas.mybest.app.domain.dto.response.source.SourceRes
import com.bcasekuritas.mybest.app.domain.entities.SourceModel
import com.bcasekuritas.mybest.ext.stub.StubUtil
import javax.inject.Inject

class SourceStubDataSourceImpl @Inject constructor(
    private val context: Context,
    private val stubUtil: StubUtil
): SourceStubDataSource {

    override suspend fun getSourceByCategory(): SourceRes =
        stubUtil.parseInto(
            jsonString = stubUtil.getJsonFromRaw(context, R.raw.stub_login),
            classOfT = SourceRes::class.java,
            defaultObject = SourceRes(
                "Default Dummy Data", listOf<Source>()
            )
        )!!

}