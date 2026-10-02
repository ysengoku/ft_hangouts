package com.ysengoku.ft_hangouts.ui.contacts

import com.ysengoku.ft_hangouts.data.DbExecutor
import com.ysengoku.ft_hangouts.data.deleteOldImage
import com.ysengoku.ft_hangouts.data.model.Contact
import com.ysengoku.ft_hangouts.data.repository.ContactRepository

class ContactDetailViewModel(private val repository: ContactRepository) {
    fun load(contactId: Long, onLoaded: (Contact?) -> Unit) {
        DbExecutor.execute {
            val contact = repository.getById(contactId)
            DbExecutor.main { onLoaded(contact) }
        }
    }

    fun delete(contactId: Long, onDeleted: (Boolean) -> Unit) {
        DbExecutor.execute {
            val picture = repository.getById(contactId)?.picture
            val deleted = repository.delete(contactId)
            if (deleted) deleteOldImage(picture)
            DbExecutor.main { onDeleted(deleted) }
        }
    }
}
