package com.bcasekuritas.mybest.ext.event

import com.bcasekuritas.mybest.app.domain.ResultWrapper
import com.bcasekuritas.mybest.app.domain.safeApiCall
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.FailureData
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.constant.NetworkCodes
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline localFetch: () -> Flow<ResultType>,
    crossinline remoteFetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: () -> Boolean = { true },
    crossinline shouldFetchWithLocalData: () -> Boolean = { true },
    schedulerProvider: SchedulerProvider,
    errorCodesMapper: ErrorCodesMapper,
    coroutineDispatcher: CoroutineDispatcher
) = flow {

    // check for data in database
    val data = localFetch().firstOrNull()

    if (data != null) {
        // data is not null -> update loading status
        if (shouldFetchWithLocalData()) {
            emit(Resource.Success(data = data, DataSource.CACHE))
        }
    }

    if (shouldFetch()) {
        // Need to fetch data -> call backend
        val fetchResult = safeApiCall(
            dispatcher = schedulerProvider.io(),
            errorCodesMapper = errorCodesMapper
        ) {
            remoteFetch() // fetch the remote source provided
        }
        // got data from backend, store it in database
        when (fetchResult) {
            is ResultWrapper.Success -> {
                fetchResult.value?.let {
                    saveFetchResult(fetchResult.value)
                }
            }

            is ResultWrapper.GenericError -> {
                emit(Resource.Failure(
                    failureData = FailureData(fetchResult.code, fetchResult.message) )
                )
            }
        }
    }

    // load updated data from database (must not return null anymore)
    val updatedData = localFetch().first()

    // emit updated data
    emit(Resource.Success(data = updatedData, DataSource.REMOTE))

}.onStart {
    // emit(Resource.loading(null))

}.catch { exception ->
    emit(Resource.Failure(
        failureData = FailureData(NetworkCodes.GENERIC_ERROR, "An error occurred while fetching data! $exception") )
    )

}.flowOn(coroutineDispatcher)

abstract class NetworkBoundResource<ResultType, RequestType>(
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) {
    fun asFlow(): Flow<Resource<ResultType>> = flow {
        // check if should fetch data from remote or not
        if (shouldFetch()) {
            if (shouldFetchWithLocalData()) { // should emit cached date with loading state or not
                emit(Resource.Success(data = localFetch(), DataSource.CACHE))
            }

            val remoteResponse = safeApiCall(
                dispatcher = schedulerProvider.io(),
                errorCodesMapper = errorCodesMapper
            ) {
                remoteFetch() // fetch the remote source provided
            }

            when (remoteResponse) {
                is ResultWrapper.Success -> {
                    remoteResponse.value?.let {
                        saveFetchResult(remoteResponse.value)
                    }
                    emit(Resource.Success(data = localFetch(), DataSource.REMOTE))
                }

                is ResultWrapper.GenericError -> {
                    emit(Resource.Failure(
                        failureData = FailureData(remoteResponse.code, remoteResponse.message) )
                    )
                }
            }
        } else {
            // get from cache
            emit(Resource.Success(data = localFetch(), DataSource.CACHE))
        }
    }

    abstract suspend fun remoteFetch(): RequestType
    abstract suspend fun saveFetchResult(data: RequestType)
    abstract suspend fun localFetch(): ResultType
    open fun onFetchFailed(throwable: Throwable) = Unit
    open fun shouldFetch() = true
    open fun shouldFetchWithLocalData() = false
}

abstract class NetworkBoundResources<T>(
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) {
    fun asFlow(): Flow<Resource<T>> = flow {
        // check if should fetch data from remote or not
        if (shouldFetch()) {
            if (shouldFetchWithLocalData()) { // should emit cached date with loading state or not
                emit(Resource.Success(data = localFetch(), DataSource.CACHE))
            }

            val remoteResponse = safeApiCall(
                dispatcher = schedulerProvider.io(),
                errorCodesMapper = errorCodesMapper
            ) {
                remoteFetch() // fetch the remote source provided
            }

            when (remoteResponse) {
                is ResultWrapper.Success -> {
                    remoteResponse.value?.let {
                        saveFetchResult(remoteResponse.value)
                    }
                    emit(Resource.Success(data = remoteResponse.value, DataSource.REMOTE))
                }

                is ResultWrapper.GenericError -> {
                    emit(Resource.Failure(
                        failureData = FailureData(remoteResponse.code, remoteResponse.message) )
                    )
                }
            }
        } else {
            // get from cache
            emit(Resource.Success(data = localFetch(), DataSource.CACHE))
        }
    }

    abstract suspend fun remoteFetch(): T
    abstract suspend fun saveFetchResult(data: T)
    abstract suspend fun localFetch(): T
    open fun onFetchFailed(throwable: Throwable) = Unit
    open fun shouldFetch() = true
    open fun shouldFetchWithLocalData() = false
}

/*import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

abstract class NetworkBoundResource<ResultType, RequestType> @MainThread protected constructor() {

    private val asObservable: Observable<Resource<ResultType>>

    init {
        val source: Observable<Resource<ResultType>> = if (shouldFetch()) {

            createCall()
                .subscribeOn(Schedulers.io())
                .doOnNext {
                    saveCallResult(processResponse(it)!!) }

                .flatMap {
                    loadFromDb().toObservable()
                        .map { Resource.success(it) } }

                .doOnError { onFetchFailed() }

                .onErrorResumeNext { t : Throwable ->
                    loadFromDb().toObservable().map {
                        Resource.error(t.message!!, it) }
                }

                .observeOn(AndroidSchedulers.mainThread())

        } else {
            loadFromDb()
                .toObservable()
                .map { Resource.success(it) }
        }

        asObservable = Observable.concat(
            loadFromDb()
                .toObservable()
                .map { Resource.loading(it) }
                .take(1),
            source
        )
    }

    fun getAsObservable(): Observable<Resource<ResultType>> {
        return asObservable
    }

    private fun onFetchFailed() {}

    @WorkerThread
    protected fun processResponse(response: Resource<RequestType>): RequestType? {
        return response.data
    }

    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType)

    @MainThread
    protected abstract fun shouldFetch(): Boolean

    @MainThread
    protected abstract fun loadFromDb(): Flowable<ResultType>

    @MainThread
    protected abstract fun createCall(): Observable<Resource<RequestType>>
}*/