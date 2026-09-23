package com.ysengoku.ft_hangouts.ui.theme

import android.content.Context
import android.content.SharedPreferences

class ThemePreferences(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "app_theme_preferences"
        private const val KEY_SELECTED_THEME = "selected_theme"

        const val THEME_OCEAN = "OCEAN"
        const val THEME_AMBER = "AMBER"
        const val THEME_FOREST = "FOREST"
        const val THEME_ROSE = "ROSE"
        const val THEME_LAVENDER = "LAVENDER"

        val THEME_LIST: List<String> = listOf(THEME_OCEAN, THEME_AMBER, THEME_FOREST, THEME_ROSE, THEME_LAVENDER)!!
    }

    fun saveTheme(themeName: String) {
        preferences.edit()
            .putString(KEY_SELECTED_THEME, themeName)
            .apply()
    }

    fun getTheme(): String {
        return preferences.getString(KEY_SELECTED_THEME, THEME_OCEAN) ?: THEME_OCEAN
    }
}
