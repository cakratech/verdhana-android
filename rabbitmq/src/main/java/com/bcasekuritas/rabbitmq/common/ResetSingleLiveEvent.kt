package com.bcasekuritas.rabbitmq.common

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ResetSingleLiveEvent<T> {

    private val _value = MutableStateFlow<T?>(null)
    val value: StateFlow<T?> = _value

    fun postValue(value: T?) {
        _value.value = value
        CoroutineScope(Dispatchers.Main).launch {
            // Delay to ensure the value is observed before resetting
            delay(1000)
            _value.value = null
        }
    }

    fun observe(owner: LifecycleOwner, observer: (T?) -> Unit) {
        owner.lifecycleScope.launch {
            value.collect {
                if (it != null) {
                    observer(it)
                }
            }
        }
    }
}