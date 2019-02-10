package ru.qdev.kudashov.jokes.view.joke.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.qdev.kudashov.jokes.model.repository.SettingsRepository

class SettingsViewModel (application: Application) : AndroidViewModel(application) {
    private val settingsRepository = SettingsRepository(application)
    private val settingsPreference = settingsRepository.getSettingsPreference()

    val notificationEnable
        get() = settingsPreference.notificationEnable

    val notificationCount
        get() = settingsPreference.notificationCount.toString()

    fun saveSettings() {
        settingsRepository.saveSettingsPreference(settingsPreference)
    }
}
