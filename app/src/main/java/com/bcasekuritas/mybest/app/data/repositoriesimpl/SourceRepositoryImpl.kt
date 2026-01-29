package com.bcasekuritas.mybest.app.data.repositoriesimpl

/*
class SourceRepositoryImpl @Inject constructor(
    private val remoteSource: SourceDataSource,
    private val stubDataSource: SourceStubDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) : SourceRepository {

    private var isUsingDummyDataSource = true
    override suspend fun getSourceByCategory(): Flow<Resource<SourceRes>> =
        flow {
            if (isUsingDummyDataSource) {
                emit(Resource.Success(data = stubDataSource.getSourceByCategory(), DataSource.REMOTE))
            } else {
                emit(Resource.Success(data = remoteSource.getSourceByCategory(), DataSource.REMOTE))
            }
        }

}*/
