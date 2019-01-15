package ru.qdev.kudashov.jokes.utils

import java.lang.ref.WeakReference

// Vladimir Kudashov.
// 15.01.2019
// WeakSubscriberArray
// For view subscribe to ViewModel message support
// (C) Vladimir Kudashov. 2016. MIT License

open class WeakSubscriberArray <T> : ArrayList<WeakReference<T>>() {
    fun addSubscriber(subscriber: T) {
        //TODO test
        val iterator = iterator()
        while (iterator.hasNext()) {
            val observableObject = iterator.next()
            when (observableObject.get()) {
                null -> iterator.remove()
                subscriber -> return
            }
        }
        add(WeakReference(subscriber))
    }

    fun removeSubscriber(subscriber: T) {
        //TODO test
        val iterator = iterator()
        while (iterator.hasNext()) {
            val observableObject = iterator.next()
            when (observableObject.get()) {
                subscriber, null -> iterator.remove()
            }
        }
    }

    fun clearEmptySubscribers() {
        //TODO test
        val iterator = iterator()
        while (iterator.hasNext()) {
            val observableObject = iterator.next()
            if (observableObject.get() == null) {
                iterator.remove()
            }
        }
    }
}