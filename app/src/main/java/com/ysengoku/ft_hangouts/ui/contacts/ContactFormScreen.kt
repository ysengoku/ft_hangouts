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

    override val view: View = inflater.inflate(R.layout.screen_contact_form, container, false)
    override val title = if (contactId == null) container.context.getString(R.string.new_contact) else container.context.getString(R.string.edit_contact)
    override val navigationIcon = NavigationIcon.CLOSE
    override val action = Action(
        icon = R.drawable.ic_check,
        label = R.string.save
    ) { save() }

    override fun saveState(outState: Bundle) {
        outState.putString(KEY_PICTURE, picture)
        outState.putString(KEY_COUNTRY, country)
        outState.putString(KEY_BIRTHDAY, birthday?.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != REQUEST_PICK_PHOTO || resultCode != Activity.RESULT_OK) return
        val uri = data?.data ?: return
        viewModel.importPhoto(uri, view.context.applicationContext) { path ->
            if (path != null) setPicture(path)
        }
    }

    override fun onBack(): Boolean {
        if (currentInput() == initialInput) {
            return false
        }
        val dialog = AlertDialog.Builder(view.context)
            .setMessage(R.string.discard_changes_confirm)
            .setNegativeButton(R.string.keep_editing, null)
            .setPositiveButton(R.string.discard) { _, _ -> navigator.pop() }
            .show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
        return true
    }

    companion object {
        private const val REQUEST_PICK_PHOTO = 1
        private const val KEY_PICTURE = "picture"
        private const val KEY_COUNTRY = "country"
        private const val KEY_BIRTHDAY = "birthday"
    }

    private val callingCodes = CountryCallingCodes(view.resources)
    private val phoneCharsFilter = InputFilter { source, start, end, _, _, _ ->
        val filtered = source.subSequence(start, end)
            .filter { it.isDigit() || it == ' ' || it == '-' || it == '(' || it == ')' }
        if (filtered.length == end - start) null else filtered
    }
    private fun defaultCountry(): String {
        val region = Locale.getDefault().country
        return if (callingCodes.callingCodeOf(region) != null) region else "FR"
    }
    private val birthdayFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)

    private var initialInput: ContactFormInput? = null
    private var picture: String? = null
    private var country: String = defaultCountry()
    private var birthday: LocalDate? = null

    private val avatarField: View = view.findViewById<View>(R.id.contact_form_avatar)
    private val photoEditButton: Button = view.findViewById<Button>(R.id.contact_form_photo_edit_button)
    private val photoRemoveButton: ImageButton = view.findViewById<ImageButton>(R.id.contact_form_remove_photo)

    private val firstNameField: View = view.findViewById(R.id.contact_form_first_name)
    private val firstNameInput: EditText = bindFormField(
        field = firstNameField,
        inputId = R.id.form_first_name,
        label = R.string.first_name,
        inputType = InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_VARIATION_PERSON_NAME or
            InputType.TYPE_TEXT_FLAG_CAP_WORDS,
        required = true,
    )

    private val lastNameInput: EditText = bindFormField(
        view.findViewById<View>(R.id.contact_form_last_name),
        R.id.form_last_name,
        R.string.last_name,
        InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_VARIATION_PERSON_NAME or
            InputType.TYPE_TEXT_FLAG_CAP_WORDS,
    )

    private val companyInput: EditText = bindFormField(
        view.findViewById<View>(R.id.contact_form_company),
        R.id.form_company,
        R.string.company,
        InputType.TYPE_CLASS_TEXT
    )

    private val phoneField: View = view.findViewById<View>(R.id.contact_form_phone)
    private val phoneInput: EditText = bindFormField(
        field = phoneField,
        inputId = R.id.form_phone,
        label = R.string.phone,
        inputType = InputType.TYPE_CLASS_PHONE,
        required = true
    )

    private val countryField: View = view.findViewById<View>(R.id.contact_form_phone_country)
    private val countryInput: EditText = bindFormField(
        field = countryField,
        inputId = R.id.form_phone_country,
        label = R.string.country,
        inputType = InputType.TYPE_NULL,
        required = true
    )

    private val addressInput: EditText = bindFormField(
        view.findViewById<View>(R.id.contact_form_address),
        R.id.form_address,
        R.string.address,
        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS,
        view.context.resources.getInteger(R.integer.max_length_address),
    )

    private val birthdayInput: EditText = bindFormField(
        view.findViewById<View>(R.id.contact_form_birthday),
        R.id.form_birthday,
        R.string.birthday,
        InputType.TYPE_NULL
    )
    private val noteInput: EditText = bindFormField(
        view.findViewById<View>(R.id.contact_form_note),
        R.id.form_note,
        R.string.note,
        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE,
        view.context.resources.getInteger(R.integer.max_length_note),
    )

    private fun setPicture(path: String?) {
        picture = path
        photoEditButton.setText(if (path == null) R.string.add_photo else R.string.change_photo)
        photoRemoveButton.visibility = if (path == null) View.GONE else View.VISIBLE
        updateAvatar()
    }

    private fun updateAvatar() {
        bindAvatar(avatarField, firstNameInput.text.toString(), lastNameInput.text.toString(), picture)
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
                setBirthday(selected)
            },
            initial.year,
            initial.monthValue - 1,
            initial.dayOfMonth,
        )
        dialog.datePicker.maxDate = System.currentTimeMillis()
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, view.context.getString(R.string.clear)) { _, _ ->
            setBirthday(null)
        }
        dialog.show()
        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).isAllCaps = false
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).isAllCaps = false
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).isAllCaps = false
    }

    private fun currentInput() = ContactFormInput(
        firstName = firstNameInput.text.toString(),
        lastName = lastNameInput.text.toString(),
        company = companyInput.text.toString(),
        phone = phoneInput.text.toString(),
        phoneCountry = country,
        address = addressInput.text.toString(),
        birthday = birthday,
        note = noteInput.text.toString(),
        picture = picture,
    )

    private fun save() {
        viewModel.save(currentInput(), contactId, view.context.applicationContext) { result ->
            when (result) {
                SaveResult.Saved -> navigator.pop()
                is SaveResult.Invalid -> {
                    setFieldError(firstNameField, firstNameInput, result.firstNameError)
                    setFieldError(phoneField, phoneInput, result.phoneError)
                }
                SaveResult.Failed -> {
                    navigator.showToast(R.string.error_save_failed)
                }
            }
        }
    }

    init {
        view.findViewById<TextView>(R.id.contact_form_required).setText(
            R.string.required)

        setPicture(null)
        photoEditButton.setOnClickListener { showPhotoPicker() }
        photoRemoveButton.setOnClickListener { setPicture(null) }

        firstNameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                setFieldError(firstNameField, firstNameInput, null)
                updateAvatar()
            }
        })

        lastNameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { updateAvatar() }
        })

        phoneInput.filters = arrayOf(
            InputFilter.LengthFilter(view.context.resources.getInteger(R.integer.max_length_phone)),
            phoneCharsFilter,
        )
        phoneInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { setFieldError(phoneField, phoneInput, null) }
        })

        val countryLabel = countryField.findViewById<TextView>(R.id.form_field_label)
        countryLabel.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val labelStart = view.resources.getDimensionPixelSize(R.dimen.text_field_label_start)
        countryInput.minWidth = countryLabel.measuredWidth + labelStart * 2
        countryInput.gravity = Gravity.CENTER
        setCountry(country)
        countryInput.isFocusable = false
        countryInput.setOnClickListener { showCountryPicker() }

        birthdayInput.isFocusable = false
        birthdayInput.setOnClickListener { showBirthdayPicker() }

        if (contactId != null) {
            viewModel.load(contactId) { contact ->
                if (contact == null) {
                    navigator.back()
                    return@load
                }
                if (savedState != null) return@load
                setPicture(contact.picture)
                firstNameInput.setText(contact.firstName)
                lastNameInput.setText(contact.lastName)
                companyInput.setText(contact.company)
                setCountry(contact.phoneCountry)
                phoneInput.setText(callingCodes.split(contact.phone)?.second ?: contact.phone)
                addressInput.setText(contact.address)
                setBirthday(contact.birthday)
                noteInput.setText(contact.note)
                initialInput = currentInput()
            }
        }

        if (savedState != null) {
            setPicture(savedState.getString(KEY_PICTURE))
            setCountry(savedState.getString(KEY_COUNTRY))
            setBirthday(savedState.getString(KEY_BIRTHDAY)?.let { LocalDate.parse(it) })
        }

        if (contactId == null) {
            initialInput = currentInput()
        }
    }
}
