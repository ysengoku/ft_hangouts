package com.ysengoku.ft_hangouts

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.ysengoku.ft_hangouts.ui.theme.ThemePreferences


class MainActivity : Activity() {
    private lateinit var themePreferences: ThemePreferences
    private lateinit var themeSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        themePreferences = ThemePreferences(this)
        val savedTheme = themePreferences.getTheme()
        setTheme(getThemeStyleResId(savedTheme))

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        themeSpinner = findViewById(R.id.theme_spinner)
        setupThemeSpinner(savedTheme)
    }

    private fun enableEdgeToEdge() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    )
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
        }
    }

    private fun getThemeStyleResId(themeName: String): Int {
        return when (themeName) {
            ThemePreferences.THEME_AMBER -> R.style.Theme_FtHangouts_Amber
            ThemePreferences.THEME_FOREST -> R.style.Theme_FtHangouts_Forest
            ThemePreferences.THEME_ROSE -> R.style.Theme_FtHangouts_Rose
            ThemePreferences.THEME_LAVENDER -> R.style.Theme_FtHangouts_Lavender
            else -> R.style.Theme_FtHangouts_Ocean
        }
    }

    private fun setupThemeSpinner(currentTheme: String) {
        val options = ThemePreferences.THEME_LIST
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
        themeSpinner.adapter = adapter

        val currentPosition = options.indexOf(currentTheme).coerceAtLeast(0)
        themeSpinner.setSelection(currentPosition)

        themeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedTheme = options[position]

                if (selectedTheme != themePreferences.getTheme()) {
                    themePreferences.saveTheme(selectedTheme)
                    recreate() // Restarts Activity to apply new XML theme everywhere
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}
