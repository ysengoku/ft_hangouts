package com.ysengoku.ft_hangouts.data.phone

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.TelephonyManager
import com.ysengoku.ft_hangouts.data.DatabaseHelper
import com.ysengoku.ft_hangouts.data.model.Contact
import com.ysengoku.ft_hangouts.data.model.Message
import com.ysengoku.ft_hangouts.data.repository.ContactRepository
import com.ysengoku.ft_hangouts.data.repository.MessageRepository
import com.ysengoku.ft_hangouts.R
import com.ysengoku.ft_hangouts.util.BackgroundExecutor

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val dbHelper = DatabaseHelper.getInstance(context)
        val contactRepository = ContactRepository(dbHelper)
        val messageRepository = MessageRepository(dbHelper)

        val parts = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (parts.isEmpty()) return

        val from = parts[0].originatingAddress ?: return
        val content = parts.joinToString("") { it. messageBody }
        val time = parts[0].timestampMillis

        val pending = goAsync()
        BackgroundExecutor.execute {
            try {
                val deviceCountry = context.getSystemService(TelephonyManager::class.java)
                    .networkCountryIso.uppercase().ifEmpty { "FR" }
                val number = toE164(from, deviceCountry) ?: from
                val contactId = contactRepository.findIdByPhone(number) ?: contactRepository.create(
                    Contact(
                        id = 0L,
                        firstName = number,
                        lastName = null,
                        company = null,
                        phone = number,
                        phoneCountry = deviceCountry,
                        address = null,
                        birthday = null,
                        note = null,
                        picture = null
                    ))
                if (contactId == -1L) return@execute
                val draft = Message(0, contactId, true, time, content)
                val saved = draft.copy(id = messageRepository.create(draft))
                val name = contactRepository.getById(contactId)?.firstName ?: number
                MessageEvent.notifyReceived(saved, name)
            } finally {
                pending.finish()
            }
        }
    }
}
