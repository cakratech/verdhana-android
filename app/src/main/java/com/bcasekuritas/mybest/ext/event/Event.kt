package com.bcasekuritas.mybest.ext.event

import timber.log.Timber

class Event<out T>(private val content: T) {

    @Suppress("MemberVisibilityCanBePrivate")
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        Timber.d("Checking hasBeenHandled: $hasBeenHandled")
        return if (hasBeenHandled) {
            Timber.d("Content already handled, returning null")
            null
        } else {
            Timber.d("Content is now marked as handled")
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content

}