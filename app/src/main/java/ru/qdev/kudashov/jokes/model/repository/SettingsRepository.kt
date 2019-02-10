package ru.qdev.kudashov.jokes.model.repository

import android.content.Context
import com.github.pwittchen.prefser.library.rx2.Prefser
import ru.qdev.kudashov.jokes.model.preference.SettingsPreference

class SettingsRepository (context: Context) {
    companion object {
        val settingsKey = "settings"
    }
    var prefser: Prefser = Prefser(context)

    fun getSettingsPreference(): SettingsPreference {
       return prefser.get(settingsKey, SettingsPreference::class.java, null) ?: SettingsPreference()
    }

    fun saveSettingsPreference(settingsPreference: SettingsPreference) {
        prefser.put(settingsKey, settingsPreference)
    }
}