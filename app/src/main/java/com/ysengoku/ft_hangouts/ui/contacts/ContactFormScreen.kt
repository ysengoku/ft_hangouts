package com.ysengoku.ft_hangouts.ui.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ysengoku.ft_hangouts.R
import com.ysengoku.ft_hangouts.navigation.Navigator
import com.ysengoku.ft_hangouts.ui.Action
import com.ysengoku.ft_hangouts.ui.NavigationIcon
import com.ysengoku.ft_hangouts.ui.Screen

class ContactFormScreen(
    inflater: LayoutInflater,
    container: ViewGroup,
    private val navigator: Navigator,
    private val contactId: Long?
): Screen {
    override val view: View = inflater.inflate(R.layout.screen_contact_form, container, false)
    override val title = if (contactId == null) container.context.getString(R.string.new_contact) else container.context.getString(R.string.edit_contact)
    override val navigationIcon = NavigationIcon.CLOSE
    override val action: Action? = null

    init {
        view.findViewById<TextView>(R.id.contact_form_placeholder).text = "Conversation : id=$contactId"
    }
}
