package com.ysengoku.ft_hangouts.ui.format

import java.util.Locale

fun countryName(code: String): String = Locale("", code).displayCountry

fun countryLabel(code: String) = "${flagEmoji(code)}  ${countryName(code)}"

fun flagEmoji(isoCode: String): String {
    val code = isoCode.uppercase()
    if (code.length != 2 || !code.all { it in 'A'..'Z' }) return ""
    return code.map { Character.toChars(0x1F1E6 + (it - 'A')).concatToString() }.joinToString("")
}
