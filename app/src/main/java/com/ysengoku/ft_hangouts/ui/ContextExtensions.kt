package com.ysengoku.ft_hangouts.ui

import android.content.Context
import android.util.TypedValue

fun Context.themeColor(attr: Int): Int {
    val value = TypedValue()
    theme.resolveAttribute(attr, value, true)
    return value.data
}
