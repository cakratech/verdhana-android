package com.bcasekuritas.mybest.ext.event

import androidx.lifecycle.Observer
import timber.log.Timber

/**
 * An [Observer] for [Event]s, simplifying the pattern of checking if the [Event]'s content has
 * already been handled.
 *
 * [onEventUnhandledContent] is *only* called if the [Event]'s contents has not been handled.
 */
class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>) {
        Timber.d("Received event: ${event.peekContent()}")
        event.getContentIfNotHandled()?.let {
            Timber.d("Handling unhandled event content: $it")
            onEventUnhandledContent(it)
        } ?: Timber.d("Event already handled")
    }
}