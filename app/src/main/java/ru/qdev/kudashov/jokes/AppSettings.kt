package ru.qdev.kudashov.jokes

class AppSettings {
    companion object {
        val jokeCountInRequest = 20
        val requestTimeoutInHourse = 10
        val minHourForNotifycation = 10
        val maxHourForNotifycation = 20
        val notificationCountOnDay = 4
    }
}