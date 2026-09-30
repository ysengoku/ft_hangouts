package com.ysengoku.ft_hangouts.ui.contacts

import com.ysengoku.ft_hangouts.data.DbExecutor
import com.ysengoku.ft_hangouts.data.model.Contact
import com.ysengoku.ft_hangouts.data.repository.ContactRepository

class ContactFormViewModel(private val repository: ContactRepository) {
    fun load(contactId: Long, onLoaded: (Contact?) -> Unit) {
        DbExecutor.execute {
            val contact = repository.getById(contactId)
            DbExecutor.main { onLoaded(contact) }
        }
    }

    fun delete(contactId: Long, onDeleted: (Boolean) -> Unit) {
        DbExecutor.execute {
            val deleted = repository.delete(contactId)
            DbExecutor.main { onDeleted(deleted) }
        }
    }
}
