package ru.qdev.kudashov.jokes.view

import ru.qdev.kudashov.jokes.utils.AlertMessage

interface AlertMessageSubscriber {
    fun alertMessage (alertMessage : AlertMessage)
}