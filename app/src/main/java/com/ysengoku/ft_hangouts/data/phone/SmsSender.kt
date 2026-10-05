package com.ysengoku.ft_hangouts.data.phone

import android.content.Context
import android.telephony.SmsManager
import java.lang.IllegalArgumentException
import java.lang.SecurityException

class SmsSender(context: Context) {
    private val smsManager = context.getSystemService(SmsManager::class.java)

    fun send(phone: String, content: String): Boolean {
        return try {
            // One SMS holds 160 characters, or 70 if the text contains an emoji
            // or Japanese characters. Longer texts must be sent in parts.
            val parts = smsManager.divideMessage(content)
            if (parts.size > 1) {
                smsManager.sendMultipartTextMessage(phone, null, parts, null, null) 
            } else {
                smsManager.sendTextMessage(phone, null, content, null, null)
            }
            true
        } catch(e: IllegalArgumentException) {
            false
        } catch(e: SecurityException) {
            false
        }
    }
}
