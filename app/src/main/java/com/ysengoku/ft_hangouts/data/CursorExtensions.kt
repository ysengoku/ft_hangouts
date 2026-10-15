package com.ysengoku.ft_hangouts.data

import android.database.Cursor

internal fun Cursor.getStringOrNull(index: Int): String? =
    if (isNull(index)) null else getString(index)
