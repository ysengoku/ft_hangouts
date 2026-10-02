package com.ysengoku.ft_hangouts.ui.contacts

import com.ysengoku.ft_hangouts.R
import com.ysengoku.ft_hangouts.data.DbExecutor
import com.ysengoku.ft_hangouts.data.model.Contact
import com.ysengoku.ft_hangouts.data.phone.toE164
import com.ysengoku.ft_hangouts.data.repository.ContactRepository
import java.time.LocalDate

data class ContactFormInput(
    val firstName: String,
    val lastName: String,
    val company: String,
    val phone: String,
    val phoneCountry: String,
    val address: String,
    val birthday: LocalDate?,
    val note: String,
    val picture: String?,
)

sealed class SaveResult {
    object Saved : SaveResult()
    data class Invalid(val firstNameError: Int?, val phoneError: Int?) : SaveResult()
    object Failed : SaveResult()
}

class ContactFormViewModel(private val repository: ContactRepository) {
    fun load(contactId: Long, onLoaded: (Contact?) -> Unit) {
        DbExecutor.execute {
            val contact = repository.getById(contactId)
            DbExecutor.main { onLoaded(contact) }
        }
    }

    fun save(input: ContactFormInput, contactId: Long?, onResult: (SaveResult) -> Unit) {
        DbExecutor.execute {
            val result = validate(input, contactId) ?: persist(input, contactId)
            DbExecutor.main { onResult(result) }
        }
    }

    private fun validate(input: ContactFormInput, contactId: Long?): SaveResult.Invalid? {
        val phone = toE164(input.phone, input.phoneCountry)

        val firstNameError = if (input.firstName.isBlank()) R.string.error_empty_first_name else null

        val phoneError = when {
            input.phone.isBlank() -> R.string.error_empty_phone
            phone == null -> R.string.error_invalid_phone
            isDuplicate(phone, contactId) -> R.string.error_duplicate_phone
            else -> null
        }
        return if (firstNameError == null && phoneError == null ) null else SaveResult.Invalid(firstNameError, phoneError)
    }

    private fun persist(input: ContactFormInput, contactId: Long?): SaveResult {
        val phone = toE164(input.phone, input.phoneCountry) ?: return SaveResult.Failed

        val contact = Contact(
            id = contactId ?: 0L,
            firstName = input.firstName.trim(),
            lastName = input.lastName.trimToNull(),
            company = input.company.trimToNull(),
            phone = phone,
            phoneCountry = input.phoneCountry,
            address = input.address.trimToNull(),
            birthday = input.birthday,
            note = input.note.trimToNull(),
            picture = input.picture
        )
        val saved = if (contactId == null) {
            repository.create(contact) != -1L
        } else {
            repository.update(contact) > 0
        }
        return if (saved) SaveResult.Saved else SaveResult.Failed
    }

    private fun isDuplicate(phone: String, contactId: Long?): Boolean {
        val existingId = repository.findIdByPhone(phone)
        return existingId != null && existingId != contactId
    }

    private fun String.trimToNull(): String? = trim().ifBlank { null }
}
