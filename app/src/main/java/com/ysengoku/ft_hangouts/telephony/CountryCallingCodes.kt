package com.ysengoku.ft_hangouts.telephony

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
        entries.groupBy({ it.second }, { it.first })

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

/**
 * Emulator numbers:
 * the modem port (6520..6527, see docs/SMS_TESTING.md) or
 * the console port (5554..5584, even) as 4 digits,
 * or any 1555xxxxxxx / +1555xxxxxxx number.
 */
private val EMULATOR_PORT = Regex("^(555[468]|55[67][02468]|558[024]|652[0-7])$")
private val EMULATOR_NUMBER = Regex("^\\+?(?:1?555)\\d{7}$")

fun toE164(number: String, country: String): String? {
    val trimmed = number.trim()
    val compact = trimmed.replace(Regex("[\\s()-]"), "")

    // Allow emulator port numbers or emulator test numbers for local dev/testing
    EMULATOR_PORT.find(compact)?.let { return "+1555521${it.value}" }
    if (EMULATOR_NUMBER.matches(compact)) return "+1${compact.removePrefix("+1")}"

    return PhoneNumberUtils.formatNumberToE164(number, country)
}

// The emulator's modem rejects a call to "+1555521xxxx" (DIAL error 44).
fun toDialable(phone: String): String =
    if (EMULATOR_NUMBER.matches(phone)) phone.removePrefix("+") else phone
