package com.bcasekuritas.mybest.app.feature.watchlist.selectwatchlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.UserWatchListRequest
import com.bcasekuritas.mybest.app.domain.interactors.AddItemCategoryUseCase
import com.bcasekuritas.mybest.app.domain.interactors.AddUserWatchlistUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSimpleWatchlistUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.AddUserWatchListGroupResponse
import com.bcasekuritas.rabbitmq.proto.bcas.AddUserWatchListItemResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleUserWatchListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectWatchlistViewModel@Inject constructor(
    private val getSimpleWatchlistUseCase: GetSimpleWatchlistUseCase,
    private val addUserWatchlistUseCase: AddUserWatchlistUseCase,
    private val addItemCategoryUseCase: AddItemCategoryUseCase
): BaseViewModel(){

    val getSimpleWatchlistResult = MutableLiveData<Resource<SimpleUserWatchListResponse?>>()
    val addUserWatchlistResult = MutableLiveData<Resource<AddUserWatchListGroupResponse?>>()
    val addItemCategoryResult = MutableLiveData<Resource<AddUserWatchListItemResponse?>>()
    
    fun getSimpleWatchlist(userId: String, wlgCode: String? = "", sessionId: String) {

        viewModelScope.launch(Dispatchers.IO) {
            val watchListRequest = UserWatchListRequest(userId, sessionId = sessionId, wlgCode) //if wlg empty = get all watchlist

            getSimpleWatchlistUseCase.invoke(watchListRequest).collect() { resource ->
                resource.let {
                    getSimpleWatchlistResult.postValue(it)

                }
            }
        }
    }

    fun addUserWatchList(userId: String, wlCode: String? = "", itemList: List<String>, sessionId: String) {

        viewModelScope.launch(Dispatchers.IO) {
            val watchListRequest = UserWatchListRequest(userId, userWlListItem = itemList, wlCode = wlCode, sessionId = sessionId)

            addUserWatchlistUseCase.invoke(watchListRequest).collect() { resource ->
                resource.let {
                    addUserWatchlistResult.postValue(it)
                }
            }
        }
    }

    fun addItemToCategory(userId: String, catGroupList: List<String>, stockList: List<String>, sessionId: String) {

        viewModelScope.launch(Dispatchers.IO) {
            val watchListRequest = UserWatchListRequest(userId, userWlListItem = stockList, userWlListCat = catGroupList, sessionId = sessionId)

            addItemCategoryUseCase.invoke(watchListRequest).collect() { resource ->
                resource.let {
                    addItemCategoryResult.postValue(it)
                }
            }
        }
    }
}