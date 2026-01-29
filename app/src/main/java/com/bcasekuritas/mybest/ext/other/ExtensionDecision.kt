package com.bcasekuritas.mybest.ext.other

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.fragment.app.FragmentActivity

fun <T> T.returnIf(condition: (T.() -> Boolean) = { false }, replaceValue: T): T {
    return if (condition.invoke(this)) replaceValue
    else this
}

fun <T> T.returnIf(condition: Boolean = false, replaceValue: T): T {
    return if (condition) replaceValue
    else this
}

fun String?.ifNotNullOrEmpty(func: (String.() -> Unit)? = null) {
    if (!this.isNullOrBlank()) {
        func?.invoke(this)
    }
}

fun String?.ifNullOrEmpty(defaultValue: String? = this, func: (String?.() -> Unit)? = null): String {
    return if (this == null || this.isEmpty()) {
        func?.invoke(this)
        defaultValue.toString()
    } else this
}

fun <T> List<T>?.ifNotNullOrEmpty(func: List<T>.() -> Unit) {
    if (this != null && this.isNotEmpty()) {
        func()
    }
}

fun <T> List<T>?.ifNullOrEmpty(defaultValue: List<T>? = this, func: (List<T>?.() -> Unit)? = null): List<T> {
    return if (this == null || this.isEmpty()) {
        func?.invoke(this)
        if (defaultValue == null) return listOf()
        defaultValue
    } else this
}

fun <T> T?.ifNull(defaultValue: T, func: (T?.() -> Unit)? = null): T {
    return if (this == null) {
        func?.invoke(this)
        defaultValue
    } else this
}

fun <T> T?.ifNotNull(func: (T.() -> Unit)? = null){
    if (this != null) {
        func?.invoke(this)
    }
}


fun <T> withNotNull(value : T?, func: T.()-> Unit){
    if(value!= null) func.invoke(value)
}

fun firstNotNullOrEmpty(vararg datas: String?) : String{
    for (data in datas ){
        if(!data.isNullOrEmpty()) return data
    }
    return ""
}

suspend fun <T> withNotNullSuspend(value : T?, func: suspend T.()-> Unit){
    if(value!= null) func.invoke(value)
}

//fun isProduction()= BuildConfig.FLAVOR == "production"

fun Context?.isAvailable(): Boolean {
    if (this == null) {
        return false
    } else if (this !is Application) {
        if (this is FragmentActivity) {
            return !this.isDestroyed
        } else if (this is Activity) {
            return !this.isDestroyed
        }
    }
    return true
}