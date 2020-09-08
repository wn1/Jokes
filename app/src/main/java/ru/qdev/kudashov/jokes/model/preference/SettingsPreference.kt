package ru.qdev.kudashov.jokes.model.preference

import java.io.Serializable

class SettingsPreference (var notificationEnable: Boolean = false, var notificationCount: Int = 3) : Serializable