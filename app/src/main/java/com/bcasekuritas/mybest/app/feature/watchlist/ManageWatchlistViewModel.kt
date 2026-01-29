package com.bcasekuritas.mybest.app.feature.watchlist

//import com.bcasekuritas.mybest.app.domain.interactors.GetWatchListUseCase
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.app.data.entity.StockWithNotationObject
import com.bcasekuritas.mybest.app.domain.dto.request.AllUserWatchListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.ManageWatchlistItem
import com.bcasekuritas.mybest.app.domain.dto.request.UserWatchListRequest
import com.bcasekuritas.mybest.app.domain.dto.response.TradeSummary
import com.bcasekuritas.mybest.app.domain.interactors.AddUserWatchlistUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetListStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSimpleAllWatchlistUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSimpleWatchlistUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.RemoveItemCategoryUseCase
import com.bcasekuritas.mybest.app.domain.interactors.RemoveWatchListCategoryUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.proto.bcas.AddUserWatchListGroupResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RemoveUserWatchListGroupResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RemoveUserWatchListItemResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleAllUserWatchListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleUserWatchListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ManageWatchlistViewModel @Inject constructor(
    private val getSimpleWatchlistUseCase: GetSimpleWatchlistUseCase,
    private val removeWatchListCategoryUseCase: RemoveWatchListCategoryUseCase,
    private val removeItemCategoryUseCase: RemoveItemCategoryUseCase,
    private val addUserWatchlistUseCase: AddUserWatchlistUseCase,
    private val getSimpleAllWatchlistUseCase: GetSimpleAllWatchlistUseCase,
    private val getStockParamDaoUseCase: GetListStockParamDaoUseCase,
    val imqConnectionListener: IMQConnectionListener
) : BaseViewModel() {

    val getSimpleWatchlistResult = MutableLiveData<Resource<SimpleUserWatchListResponse?>>()
    val removeWatchListCategoryResult =
        MutableLiveData<Resource<RemoveUserWatchListGroupResponse?>>()
    val removeItemCategoryResult = MutableLiveData<Resource<RemoveUserWatchListItemResponse?>>()
    val getAddUserWatchlistGroupResult = MutableLiveData<AddUserWatchListGroupResponse?>()
    val getSimpleAllWatchlistResult = MutableLiveData<SimpleAllUserWatchListResponse?>()
    var getStockParamResult = MutableLiveData<List<StockWithNotationObject?>>()


    fun getSimpleWatchlist(userId: String, sessionId: String, wlgCode: String? = "") {
        viewModelScope.launch(Dispatchers.IO) {
            val watchListRequest =
                UserWatchListRequest(userId, sessionId = sessionId, wlgCode) //if wlg empty = get all watchlist

            getSimpleWatchlistUseCase.invoke(watchListRequest).collect() { resource ->
                resource.let {
                    withContext(Dispatchers.Main) {
                        getSimpleWatchlistResult.postValue(it)
                    }
                }
            }
        }
    }

    fun removeWatchListCategory(userId: String, wlCode: String, sessionId: String) {

        viewModelScope.launch(Dispatchers.IO) {
            val watchListRequest =
                UserWatchListRequest(userId, wlCode = wlCode, sessionId = sessionId)

            removeWatchListCategoryUseCase.invoke(watchListRequest).collect() { resource ->
                resource.let {
                    withContext(Dispatchers.Main) {
                        removeWatchListCategoryResult.postValue(it)
                    }
                }
            }
        }
    }

    fun removeItemCategory(
        userId: String,
        wlCode: String,
        itemCode: String,
        itemSeq: Int,
        sessionId: String
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            val watchListRequest = UserWatchListRequest(
                userId,
                wlCode = wlCode,
                itemCode = itemCode,
                itemSeq = itemSeq,
                sessionId = sessionId
            )

            removeItemCategoryUseCase.invoke(watchListRequest).collect() { resource ->
                resource.let {
                    withContext(Dispatchers.Main) {
                        removeItemCategoryResult.postValue(it)
                    }
                }
            }
        }
    }

    fun addUserWatchlist(userId: String, wlCode: String? = "", itemList: List<String>, sessionId: String, newWlCode: String) {

        viewModelScope.launch(Dispatchers.IO) {
            val watchListRequest = UserWatchListRequest(userId, userWlListItem = itemList, wlCode = wlCode, sessionId = sessionId, newWlCode = newWlCode)

            addUserWatchlistUseCase.invoke(watchListRequest).collect() { resource ->
                resource.let {
                    when (resource) {
                        is Resource.Success -> {
                            withContext(Dispatchers.Main) {
                                getAddUserWatchlistGroupResult.postValue(resource.data)
                            }
                        }
                        else -> {}
                    }

                }
            }
        }
    }

    fun getAllWatchlist(userId: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val watchlistRequest = AllUserWatchListRequest(userId, sessionId)

            getSimpleAllWatchlistUseCase.invoke(watchlistRequest).collect() {resource ->
                resource.let {
                    when (resource) {
                        is Resource.Success -> {
                            withContext(Dispatchers.Main) {
                                getSimpleAllWatchlistResult.postValue(resource.data)
                            }
                        } else -> {}
                    }
                }
            }
        }
    }

     fun getStockParam(value: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            getStockParamDaoUseCase(value).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { data ->
                            withContext(Dispatchers.Main) {
                                getStockParamResult.postValue(data)
                            }
                        }
                    }

                    else -> {
                        // Handle other cases if needed
                    }
                }
            }
        }
    }
}