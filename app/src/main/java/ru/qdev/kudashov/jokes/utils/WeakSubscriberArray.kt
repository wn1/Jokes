package ru.qdev.kudashov.jokes.utils

import java.lang.ref.WeakReference

// Vladimir Kudashov.
// 15.01.2019
// WeakSubscriberArray
// For view subscribe to ViewModel message support
// (C) Vladimir Kudashov. 2016. MIT License

open class WeakSubscriberArray <T> : ArrayList<WeakReference<T>>() {
    fun addSubscriber(subscriber: T) {
        val iterator = iterator()
        while (iterator.hasNext()) {
            val reference = iterator.next()
            when (reference.get()) {
                null -> iterator.remove()
                subscriber -> return
            }
        }
        add(WeakReference(subscriber))
    }

    fun removeSubscriber(subscriber: T) {
        val iterator = iterator()
        while (iterator.hasNext()) {
            val reference = iterator.next()
            when (reference.get()) {
                subscriber, null -> iterator.remove()
            }
        }
    }

    fun clearEmptySubscribers() {
        val iterator = iterator()
        while (iterator.hasNext()) {
            val reference = iterator.next()
            if (reference.get() == null) {
                iterator.remove()
            }
        }
    }

    fun forEachSubscribers(action: (T) -> Unit) {
        val iterator = iterator()
        while (iterator.hasNext()) {
            val reference = iterator.next()
            val subscriber = reference.get()
            if (subscriber == null) {
                iterator.remove()
            }
            else
            {
                action(subscriber)
            }
        }
    }
}