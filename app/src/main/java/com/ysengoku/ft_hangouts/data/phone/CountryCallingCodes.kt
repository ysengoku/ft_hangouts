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

fun toE164(number: String, country: String): String? {
    val trimmed = number.trim()

    // Allow emulator port numbers or emulator test numbers for local dev/testing
    if (trimmed.length == 4) {
        val port = trimmed.toIntOrNull()
        if (port != null && port in 5554..5584 && port % 2 == 0) {
            return "+1555521$trimmed"
        }
    }
    if (trimmed.matches(Regex("^1?55552155\\d{2}$"))) {
        return if (trimmed.startsWith("+")) trimmed else "+1$trimmed"
    }

    return PhoneNumberUtils.formatNumberToE164(number, country)
}
