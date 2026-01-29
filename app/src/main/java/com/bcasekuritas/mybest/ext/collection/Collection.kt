package com.bcasekuritas.mybest.ext.collection

import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}

operator fun <T> MutableLiveData<List<T>>.plusAssign(item: T){
    val value  = this.value ?: emptyList()
    this.value = value + listOf(item)
}