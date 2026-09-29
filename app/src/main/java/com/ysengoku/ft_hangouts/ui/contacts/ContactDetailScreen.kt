package com.ysengoku.ft_hangouts.ui.contacts

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import com.ysengoku.ft_hangouts.R
import com.ysengoku.ft_hangouts.data.repository.ContactRepository
import com.ysengoku.ft_hangouts.navigation.Navigator
import com.ysengoku.ft_hangouts.navigation.Route
import com.ysengoku.ft_hangouts.ui.Action
import com.ysengoku.ft_hangouts.ui.NavigationIcon
import com.ysengoku.ft_hangouts.ui.Screen
import com.ysengoku.ft_hangouts.ui.themeColor
import com.ysengoku.ft_hangouts.ui.components.bindActionButton
import com.ysengoku.ft_hangouts.ui.components.bindAvatar
import com.ysengoku.ft_hangouts.ui.components.bindDetailField
import java.time.format.DateTimeFormatter.ofLocalizedDate
import java.time.format.FormatStyle

class ContactDetailScreen(
    inflater: LayoutInflater,
    container: ViewGroup,
    private val navigator: Navigator,
    repository: ContactRepository,
    private val contactId: Long
): Screen {
    override val view: View = inflater.inflate(R.layout.screen_contact_detail, container, false)
    override val title = ""
    override val navigationIcon = NavigationIcon.BACK
    override val action = Action(
        icon = R.drawable.ic_edit,
        label = R.string.edit_contact,
    ) { 
        navigator.navigate(Route.ContactForm(contactId))
    }

    private val viewModel = ContactDetailViewModel(repository)
    private val birthdayFormatter = ofLocalizedDate(FormatStyle.LONG)

    init {
        viewModel.load(contactId) { contact ->
            if (contact == null) {
                navigator.back()
                return@load
            }
            val avatarView = view.findViewById<FrameLayout>(R.id.contact_detail_avatar)
            bindAvatar(avatarView, contact.firstName, contact.lastName, contact.picture)

            view.findViewById<TextView>(R.id.contact_detail_name).text = "${contact.firstName} ${contact.lastName}"
            
            val companyView = view.findViewById<TextView>(R.id.contact_detail_company)
            if ((contact.company).isNullOrBlank()) {
                companyView.visibility = View.GONE
            } else {
                companyView.text = contact.company
                companyView.visibility = View.VISIBLE
            }

            val messageButton = view.findViewById<View>(R.id.contact_detail_message)
            bindActionButton(
                messageButton,
                R.drawable.ic_chat_filled,
                R.string.message,
                ::sendMessage
            )
            messageButton.setOnClickListener { sendMessage() }

            val callButton = view.findViewById<View>(R.id.contact_detail_call)
            bindActionButton(
                callButton,
                R.drawable.ic_call_filled,
                R.string.call,
                ::startCall
            )
            callButton.setOnClickListener { startCall() }

            bindDetailField(
                view.findViewById<View>(R.id.contact_detail_phone),
                R.drawable.ic_call,
                R.string.phone,
                contact.phone
            )
            bindDetailField(
                view.findViewById<View>(R.id.contact_detail_address),
                R.drawable.ic_location,
                R.string.address,
                contact.address
            )
            bindDetailField(
                view.findViewById<View>(R.id.contact_detail_birthday),
                R.drawable.ic_cake,
                R.string.birthday,
                contact.birthday?.format(birthdayFormatter)
            )
            bindDetailField(
                view.findViewById<View>(R.id.contact_detail_note),
                R.drawable.ic_description,
                R.string.note,
                contact.note
            )

            view.findViewById<Button>(R.id.contact_delete_button).setOnClickListener { confirmDelete() }
        }
    }

    private fun sendMessage() {
        navigator.navigate(Route.Conversation(contactId))
    }

    private fun startCall() {
        // TODO (Bonus)
    }

    private fun confirmDelete() {
        val dialog = AlertDialog.Builder(view.context)
            .setMessage(R.string.delete_contact_confirm)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.delete(contactId) { deleted ->
                    if (deleted) {
                        navigator.back()
                    }
                }
            }
            .show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(view.context.themeColor(android.R.attr.colorError))
    }
}
