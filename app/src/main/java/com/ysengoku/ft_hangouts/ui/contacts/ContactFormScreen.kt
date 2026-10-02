package com.ysengoku.ft_hangouts.ui.contacts

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.ysengoku.ft_hangouts.R
import com.ysengoku.ft_hangouts.data.model.Contact
import com.ysengoku.ft_hangouts.data.copyImageToAppCache
import com.ysengoku.ft_hangouts.data.phone.CountryCallingCodes
import com.ysengoku.ft_hangouts.data.phone.flagEmoji
import com.ysengoku.ft_hangouts.data.repository.ContactRepository
import com.ysengoku.ft_hangouts.navigation.Navigator
import com.ysengoku.ft_hangouts.ui.Action
import com.ysengoku.ft_hangouts.ui.NavigationIcon
import com.ysengoku.ft_hangouts.ui.Screen
import com.ysengoku.ft_hangouts.ui.components.bindAvatar
import com.ysengoku.ft_hangouts.ui.components.bindFormField
import com.ysengoku.ft_hangouts.ui.components.setFieldError
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

class ContactFormScreen(
    inflater: LayoutInflater,
    container: ViewGroup,
    private val navigator: Navigator,
    repository: ContactRepository,
    private val contactId: Long?,
    savedState: Bundle?
): Screen {
    private val viewModel = ContactFormViewModel(repository)
    private val birthdayFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)

    override val view: View = inflater.inflate(R.layout.screen_contact_form, container, false)
    override val title = if (contactId == null) container.context.getString(R.string.new_contact) else container.context.getString(R.string.edit_contact)
    override val navigationIcon = NavigationIcon.CLOSE
    override val action = Action(
        icon = R.drawable.ic_check,
        label = R.string.save
    ) { save() }

    private val avatar: View = view.findViewById<View>(R.id.contact_form_avatar)
    private val photoEditButton: Button = view.findViewById<Button>(R.id.contact_form_photo_edit_button)
    private val photoRemoveButton: ImageButton = view.findViewById<ImageButton>(R.id.contact_form_remove_photo)

    private val callingCodes = CountryCallingCodes(view.resources)
    private fun defaultCountry(): String {
        val region = Locale.getDefault().country
        return if (callingCodes.callingCodeOf(region) != null) region else "FR"
    }

    private var original: Contact? = null

    private var firstNameField: View
    private var phoneField: View
    private var picture: String? = null
    private var firstNameInput: EditText
    private var lastNameInput: EditText
    private var companyInput: EditText
    private var countryInput: EditText
    private var phoneInput: EditText
    private var addressInput: EditText
    private var birthdayInput: EditText
    private var noteInput: EditText


    private var country: String = defaultCountry()
    private var birthday: LocalDate? = null

    companion object {
        private const val REQUEST_PICK_PHOTO = 1
        // private const val DEFAULT_COUNTRY = Locale.getDefault().country
        private const val KEY_PICTURE = "picture"
        private const val KEY_COUNTRY = "country"
        private const val KEY_BIRTHDAY = "birthday"
    }

    override fun saveState(outState: Bundle) {
        outState.putString(KEY_PICTURE, picture)
        outState.putString(KEY_COUNTRY, country)
        outState.putString(KEY_BIRTHDAY, birthday?.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != REQUEST_PICK_PHOTO || resultCode != Activity.RESULT_OK) return
        val uri = data?.data ?: return
        val path = copyImageToAppCache(uri, view.context) ?: return
        setPicture(path, firstNameInput.text.toString(), lastNameInput.text.toString())
    }

    private fun setPicture(path: String?, firstName: String?, lastName: String?) {
        picture = path
        photoEditButton.setText(if (path == null) R.string.add_photo else R.string.change_photo)
        photoRemoveButton.visibility = if (path == null) View.GONE else View.VISIBLE
        if (firstName != null && lastName != null) {
            bindAvatar(avatar, firstName, lastName, path)
        }
    }

    private fun showPhotoPicker() {
        val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
        (view.context as Activity).startActivityForResult(intent, REQUEST_PICK_PHOTO)
    }

    private fun setCountry(isoCode: String?) {
        country = isoCode ?: defaultCountry()
        countryInput.setText("${flagEmoji(country)} +${callingCodes.callingCodeOf(country)}")
    }

    private fun showCountryPicker() {
        val codes = callingCodes.regions()
            .sortedBy { Locale("", it).displayCountry }
        val labels = codes.map { "${flagEmoji(it)}  ${Locale("", it).displayCountry}" }
            .toTypedArray()

        AlertDialog.Builder(view.context)
            .setTitle(R.string.country)
            .setItems(labels) { _, which ->
                if (codes[which] != country) {
                    setCountry(codes[which]) 
                }}
            .show()
    }

    private val phoneCharsFilter = InputFilter { source, start, end, _, _, _ ->
        val filtered = source.subSequence(start, end)
            .filter { it.isDigit() || it == ' ' || it == '-' || it == '(' || it == ')' }
        if (filtered.length == end - start) null else filtered
    }

    private fun setBirthday(date: LocalDate?) {
        birthday = date
        birthdayInput.setText(date?.format(birthdayFormatter))
    }
    
    private fun showBirthdayPicker() {
        val initial = birthday ?: LocalDate.now().minusYears(18)
        val dialog = DatePickerDialog(
            view.context,
            { _, year, month, day ->
                val selected = LocalDate.of(year, month + 1, day)
                birthday = selected
                setBirthday(selected)
            },
            initial.year,
            initial.monthValue - 1,
            initial.dayOfMonth,
        )
        dialog.datePicker.maxDate = System.currentTimeMillis()
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, view.context.getString(R.string.clear)) { _, _ ->
            birthday = null
            setBirthday(null)
        }
        dialog.show()
        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).isAllCaps = false
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).isAllCaps = false
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).isAllCaps = false
    }

    private fun save() {
        viewModel.save(
            ContactFormInput(
                firstName = firstNameInput.text.toString(),
                lastName = lastNameInput.text.toString(),
                company = companyInput.text.toString(),
                phone = phoneInput.text.toString(),
                phoneCountry = country,
                address = addressInput.text.toString(),
                birthday = birthday,
                note = noteInput.text.toString(),
                picture = picture
            ),
            contactId,
            view.context.applicationContext
        ) { result ->
            when (result) {
                SaveResult.Saved -> navigator.back()
                is SaveResult.Invalid -> {
                    setFieldError(firstNameField, firstNameInput, result.firstNameError)
                    setFieldError(phoneField, phoneInput, result.phoneError)
                }
                SaveResult.Failed -> { /* TODO: Show error */ }
            }
        }
    }

    init {
        setPicture(null, "", "")
        photoEditButton.setOnClickListener { showPhotoPicker() }

        view.findViewById<TextView>(R.id.contact_form_required).setText(
            R.string.required)

        firstNameField = view.findViewById<View>(R.id.contact_form_first_name)
        firstNameInput = bindFormField(
            field = firstNameField,
            inputId = R.id.form_first_name,
            label = R.string.first_name,
            inputType = InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_VARIATION_PERSON_NAME or
                InputType.TYPE_TEXT_FLAG_CAP_WORDS,
            required = true
        )
        firstNameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { setFieldError(firstNameField, firstNameInput, null) }
        })

        lastNameInput = bindFormField(
            view.findViewById<View>(R.id.contact_form_last_name),
            R.id.form_last_name,
            R.string.last_name,
            InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_VARIATION_PERSON_NAME or
                InputType.TYPE_TEXT_FLAG_CAP_WORDS,
        )

        companyInput = bindFormField(
            view.findViewById<View>(R.id.contact_form_company),
            R.id.form_company,
            R.string.company,
            InputType.TYPE_CLASS_TEXT
        )

        phoneField = view.findViewById<View>(R.id.contact_form_phone)
        phoneInput = bindFormField(
            field = phoneField,
            inputId = R.id.form_phone,
            label = R.string.phone,
            inputType = InputType.TYPE_CLASS_PHONE,
            required = true
        )
        phoneInput.filters = arrayOf(
            InputFilter.LengthFilter(view.context.resources.getInteger(R.integer.max_length_phone)),
            phoneCharsFilter,
        )
        phoneInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { setFieldError(phoneField, phoneInput, null) }
        })

        val countryField = view.findViewById<View>(R.id.contact_form_phone_country)
        countryInput = bindFormField(
            field = countryField,
            inputId = R.id.form_phone_country,
            label = R.string.country,
            inputType = InputType.TYPE_NULL,
            required = true
        )
        val countryLabel = countryField.findViewById<TextView>(R.id.form_field_label)
        countryLabel.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val labelStart = view.resources.getDimensionPixelSize(R.dimen.text_field_label_start)
        countryInput.minWidth = countryLabel.measuredWidth + labelStart * 2
        countryInput.gravity = Gravity.CENTER
        setCountry(country)
        countryInput.isFocusable = false
        countryInput.setOnClickListener { showCountryPicker() }

        addressInput = bindFormField(
            view.findViewById<View>(R.id.contact_form_address),
            R.id.form_address,
            R.string.address,
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS,
            view.context.resources.getInteger(R.integer.max_length_address),
        )

        birthdayInput = bindFormField(
            view.findViewById<View>(R.id.contact_form_birthday),
            R.id.form_birthday,
            R.string.birthday,
            InputType.TYPE_NULL
        )
        birthdayInput.isFocusable = false
        birthdayInput.setOnClickListener { showBirthdayPicker() }

        noteInput = bindFormField(
            view.findViewById<View>(R.id.contact_form_note),
            R.id.form_note,
            R.string.note,
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE,
            view.context.resources.getInteger(R.integer.max_length_note),
        )

        if (contactId != null) {
            viewModel.load(contactId) { contact ->
                if (contact == null) {
                    navigator.back()
                    return@load
                }
                original = contact
                if (savedState != null) return@load
                setPicture(contact.picture, contact.firstName, contact.lastName)
                firstNameInput.setText(contact.firstName)
                lastNameInput.setText(contact.lastName)
                companyInput.setText(contact.company)
                setCountry(contact.phoneCountry)
                phoneInput.setText(callingCodes.split(contact.phone)?.second ?: contact.phone)
                addressInput.setText(contact.address)
                setBirthday(contact.birthday)
                noteInput.setText(contact.note)
            }
        }

        if (savedState != null) {
            setPicture(savedState.getString(KEY_PICTURE), "", "")
            setCountry(savedState.getString(KEY_COUNTRY))
            setBirthday(savedState.getString(KEY_BIRTHDAY)?.let { LocalDate.parse(it) })
        }
    }
}

// TODO: Handle remove picture button
// TODO: Confirmation dialog if the user closes without saving
// TODO: Resize picture
// TODO: Use another thread to copy pic to chache