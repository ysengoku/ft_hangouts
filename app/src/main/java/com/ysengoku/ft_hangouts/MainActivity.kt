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
import android.window.OnBackInvokedDispatcher
import com.ysengoku.ft_hangouts.data.BackgroundTimeStore
import com.ysengoku.ft_hangouts.navigation.Navigator
import com.ysengoku.ft_hangouts.navigation.RequestCodes
import com.ysengoku.ft_hangouts.ui.components.TopAppBar
import com.ysengoku.ft_hangouts.ui.components.Snackbar
import com.ysengoku.ft_hangouts.ui.format.timeLabel
import com.ysengoku.ft_hangouts.ui.Permissions
import com.ysengoku.ft_hangouts.ui.theme.ThemePreferences

class MainActivity : Activity() {
    private lateinit var navigator: Navigator
    private lateinit var themePreferences: ThemePreferences
    private lateinit var backgroundTimeStore: BackgroundTimeStore
    private lateinit var permissionRequests: Permissions
    private lateinit var themeSpinner: Spinner

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

        val snackbar = Snackbar(findViewById<View>(R.id.snackbar))

        permissionRequests = Permissions(this)
        navigator = Navigator(
            findViewById(R.id.screen_container),
            topAppBar,
            snackbar,
            permissionRequests,
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

        permissionRequests.request(Manifest.permission.RECEIVE_SMS) {}
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
        val formatted = timeLabel(this, time)
        window.decorView.postDelayed({ navigator.showToast(getString(R.string.last_background_time, formatted), 2500) }, 400)
    }

    override fun onDestroy() {
        navigator.onDestroy()
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionRequests.onResult(requestCode, grantResults)
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
        val container = findViewById<View>(R.id.screen_body)

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
