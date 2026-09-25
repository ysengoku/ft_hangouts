package com.ysengoku.ft_hangouts

import android.app.Activity
import android.graphics.Color
import android.graphics.Rect
import android.view.WindowInsets
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.ysengoku.ft_hangouts.ui.theme.ThemePreferences
import com.ysengoku.ft_hangouts.data.DatabaseHelper
import com.ysengoku.ft_hangouts.navigation.Navigator
import com.ysengoku.ft_hangouts.ui.components.TopAppBar

class MainActivity : Activity() {
    private lateinit var navigator: Navigator
    private lateinit var themePreferences: ThemePreferences
    private lateinit var themeSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        themePreferences = ThemePreferences(this)
        val savedTheme = themePreferences.getTheme()
        setTheme(getThemeStyleResId(savedTheme))

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        applySystemBarInsets()

        // themeSpinner = findViewById(R.id.theme_spinner)
        // setupThemeSpinner(savedTheme)

        // For testing only
        // DatabaseHelper.getInstance(this).writableDatabase
        navigator = Navigator(
            findViewById(R.id.screen_container),
            TopAppBar(findViewById(R.id.top_app_bar))
        )
        navigator.showContactList()
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

    private fun applySystemBarInsets() {
        val topAppBar = findViewById<View>(R.id.top_app_bar)
        val container = findViewById<View>(R.id.screen_container)

        val barHeight = resources.getDimensionPixelSize(R.dimen.top_app_bar_height)
        val barPaddingLeft = topAppBar.paddingLeft
        val barPaddingRight = topAppBar.paddingRight

        topAppBar.setOnApplyWindowInsetsListener { v, insets ->
            val bars = insets.systemBarsRect()
            v.setPadding(barPaddingLeft + bars.left, bars.top, barPaddingRight + bars.right, 0)
            v.layoutParams.height = barHeight + bars.top
            insets
        }

        container.setOnApplyWindowInsetsListener { v, insets ->
            val bars = insets.systemBarsRect()
            v.setPadding(bars.left, 0, bars.right, bars.bottom)
            insets
        }
    }

    private fun WindowInsets.systemBarsRect(): Rect =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val i = getInsets(WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout())
            Rect(i.left, i.top, i.right, i.bottom)
        } else {
            @Suppress("DEPRECATION")
            Rect(systemWindowInsetLeft, systemWindowInsetTop, systemWindowInsetRight, systemWindowInsetBottom)
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
