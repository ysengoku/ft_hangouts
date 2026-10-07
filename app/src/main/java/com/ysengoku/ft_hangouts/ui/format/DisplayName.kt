package com.ysengoku.ft_hangouts.ui.format

fun displayName(firstName: String, lastName: String?): String =
    listOfNotNull(firstName, lastName).joinToString(" ")
