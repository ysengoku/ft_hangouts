package com.ysengoku.ft_hangouts.ui

import android.view.View

interface Screen {
    val view: View
    val title: String
    val navigationIcon: NavigationIcon // NONE / BACK / CLOSE
    val action: Action?

    fun onShow() {}
    fun onHide() {}
    fun onDestroy() {}
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
