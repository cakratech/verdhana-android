package com.bcasekuritas.mybest.app.feature.stockdetail.specialnotes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.entity.StockNotationObject
import com.bcasekuritas.mybest.app.domain.interactors.GetNotationByStockCodeDaoUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SpecialNotesViewModel @Inject constructor(
    private val getNotationByStockCodeDaoUseCase: GetNotationByStockCodeDaoUseCase
): BaseViewModel(){
    var getStockNotationResult = MutableLiveData<List<StockNotationObject?>>()

    fun getStockNotation(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getNotationByStockCodeDaoUseCase(value).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { data ->
                            if(data != null && data.isNotEmpty()){
                                getStockNotationResult.postValue(data)
                            }
                            Timber.d("notasi2 : $data")
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