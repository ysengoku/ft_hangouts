package com.ysengoku.ft_hangouts.ui.contacts

import com.ysengoku.ft_hangouts.util.BackgroundExecutor
import com.ysengoku.ft_hangouts.data.deleteOldImage
import com.ysengoku.ft_hangouts.data.model.Contact
import com.ysengoku.ft_hangouts.data.repository.ContactRepository

class ContactDetailViewModel(private val repository: ContactRepository) {
    fun load(contactId: Long, onLoaded: (Contact?) -> Unit) {
        BackgroundExecutor.execute {
            val contact = repository.getById(contactId)
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
}
