package com.ysengoku.ft_hangouts.ui

import android.content.Intent
import android.os.Bundle
import android.view.View

interface Screen {
    val view: View
    val title: String
    val navigationIcon: NavigationIcon // NONE / BACK / CLOSE
    val action: Action?
    val snackbarAnchor: View? get() = null

    fun onShow() {}
    fun onHide() {}
    fun onDestroy() {}
    fun saveState(outState: Bundle) {} 
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {}
    fun onBack(): Boolean = false
}

data class Action(
    val icon: Int,
    val label: Int,
    val onClick: (View) -> Unit
)

enum class NavigationIcon {
    NONE,
    BACK,
    CLOSE,
}
