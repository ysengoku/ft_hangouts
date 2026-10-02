package com.ysengoku.ft_hangouts.data.phone

import android.content.res.Resources
import android.telephony.PhoneNumberUtils
import com.ysengoku.ft_hangouts.R

class CountryCallingCodes(resources: Resources) {
    private val entries: List<Pair<String, String>> =
        resources.getStringArray(R.array.country_calling_codes).map { item ->
            val (region, code) = item.split(":")
            region to code
        }

    private val codeByRegion: Map<String, String> = entries.toMap()
    private val regionByCode: Map<String, List<String>> =
        entries.groupBy({ it.second }, { it.first} )

    fun regions(): List<String> = entries.map { it.first }

    fun callingCodeOf(region: String): String? = codeByRegion[region]

    fun regionsOf(callingCode: String): List<String> = regionByCode[callingCode].orEmpty()

    fun split(e164: String): Pair<String, String>? {
        val digits = e164.removePrefix("+")
        for (length in 3 downTo 1) {
            val code = digits.take(length)
            if (code in regionByCode) {
                return code to digits.drop(length)
            }
        }
        return null
    }
}

fun flagEmoji(isoCode: String): String {
    val code = isoCode.uppercase()
    if (code.length != 2 || !code.all { it in 'A'..'Z' }) return ""
    return code.map { Character.toChars(0x1F1E6 + (it - 'A')).concatToString() }.joinToString("")
}

fun toE164(number: String, country: String): String? {
    return PhoneNumberUtils.formatNumberToE164(number, country)
}
