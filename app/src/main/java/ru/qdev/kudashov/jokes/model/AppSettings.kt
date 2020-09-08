package ru.qdev.kudashov.jokes.model

class AppSettings {
    companion object {
        val jokeCountInRequest = 20
        val requestTimeoutInHourse = 10
        val minHourForNotifycation = 10
        val maxHourForNotifycation = 20
        val notificationCountOnDay = 4
        val removeJokeAfterMonthCount = 2
        val fullAgeWord = "" //TODO
    }
}