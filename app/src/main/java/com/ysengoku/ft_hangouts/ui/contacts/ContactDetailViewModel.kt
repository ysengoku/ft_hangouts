package com.ysengoku.ft_hangouts.ui.contacts

import android.content.Context
import com.ysengoku.ft_hangouts.data.deleteOldImage
import com.ysengoku.ft_hangouts.data.model.Contact
import com.ysengoku.ft_hangouts.data.repository.ContactRepository
import com.ysengoku.ft_hangouts.telephony.startCall
import com.ysengoku.ft_hangouts.util.BackgroundExecutor

class ContactDetailViewModel(private val repository: ContactRepository) {
    private var contact: Contact? = null

    fun load(contactId: Long, onLoaded: (Contact?) -> Unit) {
        BackgroundExecutor.execute {
            val loaded = repository.getById(contactId)
            contact = loaded
            BackgroundExecutor.main { onLoaded(contact) }
        }
    }

    fun delete(contactId: Long, onDeleted: (Boolean) -> Unit) {
        BackgroundExecutor.execute {
            val picture = repository.getById(contactId)?.picture
            val deleted = repository.delete(contactId)
            if (deleted) deleteOldImage(picture)
            BackgroundExecutor.main { onDeleted(deleted) }
        }
    }

    fun startCall(context: Context) {
        contact?.let { startCall(context, it.phone) }
    }
}
