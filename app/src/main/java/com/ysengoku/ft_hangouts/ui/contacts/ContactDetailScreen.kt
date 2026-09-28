package com.ysengoku.ft_hangouts.ui.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import com.ysengoku.ft_hangouts.R
import com.ysengoku.ft_hangouts.data.repository.ContactRepository
import com.ysengoku.ft_hangouts.navigation.Navigator
import com.ysengoku.ft_hangouts.navigation.Route
import com.ysengoku.ft_hangouts.ui.Action
import com.ysengoku.ft_hangouts.ui.NavigationIcon
import com.ysengoku.ft_hangouts.ui.Screen
import com.ysengoku.ft_hangouts.ui.components.bindAvatar

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

    init {
        viewModel.load(contactId) { contact ->
            if (contact == null) {
                navigator.back()
                return@load
            }
            val avatarView = view.findViewById<FrameLayout>(R.id.contact_detail_avatar)
            bindAvatar(avatarView, contact.firstName, contact.lastName, contact.picture)

            view.findViewById<TextView>(R.id.contact_detail_name).text = "${contact.firstName} ${contact.lastName}"
        }
    }
}
