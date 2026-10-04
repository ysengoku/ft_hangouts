package com.ysengoku.ft_hangouts.data

import android.content.Context
import android.content.SharedPreferences

class BackgroundTimeStore(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "app_background_time"
        private const val KEY_TIME = "background_time"
    }

    fun save() {
        preferences.edit()
            .putLong(KEY_TIME, System.currentTimeMillis())
            .apply()
    }

    fun consume(): Long? {
        if (!preferences.contains(KEY_TIME)) {
            return null
        }
        val time = preferences.getLong(KEY_TIME, 0L)
        preferences.edit()
            .remove(KEY_TIME)
            .apply()
        return time
    }
}
