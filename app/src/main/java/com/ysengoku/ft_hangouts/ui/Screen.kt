package com.ysengoku.ft_hangouts.ui

import android.view.View

interface Screen {
    val view: View
    val title: String
    val showNavigation: Boolean
}
