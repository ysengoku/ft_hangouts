package com.ysengoku.ft_hangouts

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.view.WindowInsets
import android.widget.Spinner
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import com.ysengoku.ft_hangouts.data.BackgroundTimeStore
import com.ysengoku.ft_hangouts.ui.components.TopAppBar
import com.ysengoku.ft_hangouts.ui.theme.ThemePreferences
import com.ysengoku.ft_hangouts.navigation.Navigator
import java.util.Date

class MainActivity : Activity() {
    private lateinit var navigator: Navigator
    private lateinit var themePreferences: ThemePreferences
    private lateinit var backgroundTimeStore: BackgroundTimeStore
    private lateinit var themeSpinner: Spinner

    companion object {
        private const val REQUEST_SMS = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        themePreferences = ThemePreferences(this)
        backgroundTimeStore = BackgroundTimeStore(this) 
        val savedTheme = themePreferences.getTheme()
        setTheme(getThemeStyleResId(savedTheme))

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        applySystemBarInsets()

        val topAppBar = TopAppBar(findViewById(R.id.top_app_bar))
        topAppBar.setOnNavigationClick { navigator.back() }

        navigator = Navigator(
            findViewById(R.id.screen_container),
            topAppBar,
            onThemeSelected = { theme ->
                themePreferences.saveTheme(theme)
                recreate()
            },
        )
        onBackInvokedDispatcher.registerOnBackInvokedCallback(
            OnBackInvokedDispatcher.PRIORITY_DEFAULT
        ) {
            if (!navigator.back()) finish()
        }

        if (savedInstanceState == null || !navigator.restoreState(savedInstanceState)) {
            navigator.start()
        }

        val missingPermissions = arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS)
            .filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        if (missingPermissions.isNotEmpty()) requestPermissions(missingPermissions.toTypedArray(), REQUEST_SMS)
    }

    override fun onStop() {
        super.onStop()
        if (isChangingConfigurations) {
            return
        }
        backgroundTimeStore.save()
    }

    override fun onStart() {
        super.onStart()
        val time = backgroundTimeStore.consume()
        if (time == null) {
            return
        }
        val formatted = DateFormat.getTimeFormat(this).format(Date(time))
        Toast.makeText(
            this,
            getString(R.string.last_background_time, formatted),
            Toast.LENGTH_LONG
        ).show()
    }

    /**
     * Called by Android before the Activity is recreated (rotation, theme change).
     * The saved history is restored in onCreate through navigator.restoreState.
    */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        navigator.saveState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        navigator.onActivityResult(requestCode, resultCode, data)
    }

    private fun enableEdgeToEdge() {
        window.setDecorFitsSystemWindows(false)
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

        // Pads the screen container so that its content stays above the navigation bar
        // and above the keyboard when it is open.
        container.setOnApplyWindowInsetsListener { v, insets ->
            val bars = insets.systemBarsRect()
            val bottom = maxOf(bars.bottom, insets.imeBottom())
            v.setPadding(bars.left, 0, bars.right, bottom)
            insets
        }
    }

    // Returns the height of the keyboard, or 0 when it is closed.
    private fun WindowInsets.imeBottom(): Int =
        getInsets(WindowInsets.Type.ime()).bottom

    private fun WindowInsets.systemBarsRect(): Rect {
        val i = getInsets(WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout())
        return Rect(i.left, i.top, i.right, i.bottom)
    }
}
