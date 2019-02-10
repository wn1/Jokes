package ru.qdev.kudashov.jokes.view.joke.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.qdev.kudashov.jokes.model.repository.SettingsRepository

class SettingsViewModel (application: Application) : AndroidViewModel(application) {
    private val settingsRepository = SettingsRepository(application)
    val settingsPreference = MutableLiveData(settingsRepository.getSettingsPreference())

    fun saveSettings() {
        settingsRepository.saveSettingsPreference(settingsPreference.value!!)
    }
}
