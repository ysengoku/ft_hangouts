package com.ysengoku.ft_hangouts.ui.contacts

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.ysengoku.ft_hangouts.R
import com.ysengoku.ft_hangouts.data.model.Contact
import com.ysengoku.ft_hangouts.data.repository.ContactRepository
import com.ysengoku.ft_hangouts.navigation.Navigator
import com.ysengoku.ft_hangouts.ui.Action
import com.ysengoku.ft_hangouts.ui.NavigationIcon
import com.ysengoku.ft_hangouts.ui.Screen
import com.ysengoku.ft_hangouts.ui.components.bindAvatar
import com.ysengoku.ft_hangouts.ui.components.bindFormField
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ContactFormScreen(
    inflater: LayoutInflater,
    container: ViewGroup,
    private val navigator: Navigator,
    repository: ContactRepository,
    private val contactId: Long?
): Screen {
    override val view: View = inflater.inflate(R.layout.screen_contact_form, container, false)
    override val title = if (contactId == null) container.context.getString(R.string.new_contact) else container.context.getString(R.string.edit_contact)
    override val navigationIcon = NavigationIcon.CLOSE
    override val action: Action? = null

    private val viewModel = ContactFormViewModel(repository)
    private val birthdayFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)

    private val avatar: View = view.findViewById<View>(R.id.contact_form_avatar)
    private val photoEditButton: Button = view.findViewById<Button>(R.id.contact_form_photo_edit_button)
    private val photoRemoveButton: ImageButton = view.findViewById<ImageButton>(R.id.contact_form_remove_photo)

    private var original: Contact? = null

    private var picture: String? = null
    private var firstNameInput: EditText
    private var lastNameInput: EditText
    private var companyInput: EditText
    private var addressInput: EditText
    private var birthdayInput: EditText
    private var noteInput: EditText
    private var birthday: LocalDate? = null

    private fun setPicture(path: String?, firstName: String?, lastName: String?) {
        picture = path
        photoEditButton.setText(if (path == null) R.string.add_photo else R.string.change_photo)
        photoRemoveButton.visibility = if (path == null) View.GONE else View.VISIBLE
        if (firstName != null && lastName != null) {
            bindAvatar(avatar, firstName, lastName, path)
        }
    }

    private fun setBirthday(date: LocalDate?) {
        birthday = date
        birthdayInput.setText(date?.format(birthdayFormatter))
    }


    private fun showBirthdayPicker(input: EditText) {
        val initial = birthday ?: LocalDate.now().minusYears(18)
        val dialog = DatePickerDialog(
            view.context,
            { _, year, month, day ->
                val selected = LocalDate.of(year, month + 1, day)
                birthday = selected
                input.setText(selected.format(birthdayFormatter))
            },
            initial.year,
            initial.monthValue - 1,
            initial.dayOfMonth,
        )
        dialog.datePicker.maxDate = System.currentTimeMillis()
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, view.context.getString(R.string.clear)) { _, _ ->
            birthday = null
            input.text = null
        }
        dialog.show()
        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).isAllCaps = false
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).isAllCaps = false
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).isAllCaps = false
    }

    init {
        if (contactId != null) {
            viewModel.load(contactId) { contact ->
                if (contact == null) {
                    navigator.back()
                    return@load
                }
                original = contact
                setPicture(contact.picture, contact.firstName, contact.lastName)
                firstNameInput.setText(contact.firstName)
                lastNameInput.setText(contact.lastName)
                companyInput.setText(contact.company)
                addressInput.setText(contact.address)
                setBirthday(contact.birthday)
                noteInput.setText(contact.note)
            }
        }

        setPicture(null, null, null)

        view.findViewById<TextView>(R.id.contact_form_required).setText(
            R.string.required)

        val firstNameField = view.findViewById<View>(R.id.contact_form_first_name)
        firstNameInput = bindFormField(
            firstNameField,
            R.id.form_first_name,
            R.string.first_name,
            InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_VARIATION_PERSON_NAME or
                InputType.TYPE_TEXT_FLAG_CAP_WORDS,
            true
        )

        val lastNameField = view.findViewById<View>(R.id.contact_form_last_name)
        lastNameInput = bindFormField(
            lastNameField,
            R.id.form_last_name,
            R.string.last_name,
            InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_VARIATION_PERSON_NAME or
                InputType.TYPE_TEXT_FLAG_CAP_WORDS,
            true
        )

        val companyField = view.findViewById<View>(R.id.contact_form_company)
        companyInput = bindFormField(
            companyField,
            R.id.form_company,
            R.string.company,
            InputType.TYPE_CLASS_TEXT
        )

        val addressField = view.findViewById<View>(R.id.contact_form_address)
        addressInput = bindFormField(
            addressField,
            R.id.form_address,
            R.string.address,
            InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS
        )

        val birthdayField =  view.findViewById<View>(R.id.contact_form_birthday)
        birthdayInput = bindFormField(
            birthdayField,
            R.id.form_birthday,
            R.string.birthday,
            InputType.TYPE_NULL
        )
        birthdayInput.isFocusable = false
        birthdayInput.setOnClickListener { showBirthdayPicker(birthdayInput) }

        val noteField = view.findViewById<View>(R.id.contact_form_note)
        noteInput = bindFormField(
            noteField,
            R.id.form_note,
            R.string.note,
            InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_FLAG_MULTI_LINE
        )
    }
}
