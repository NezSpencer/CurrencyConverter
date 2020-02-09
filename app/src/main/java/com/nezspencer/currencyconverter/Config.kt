package com.nezspencer.currencyconverter

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Config @Inject constructor(private val application: Application) {
    private val preferences: SharedPreferences
        get() = application.getSharedPreferences("converterConfig", Context.MODE_PRIVATE)

    var lastCached: Long
        set(value) = preferences.edit().putLong(KEY_LAST_SAVE_TIME, value).apply()
        get() = preferences.getLong(KEY_LAST_SAVE_TIME, 0L)

    companion object {
        private const val KEY_LAST_SAVE_TIME = "key_last_save_time"
    }
}