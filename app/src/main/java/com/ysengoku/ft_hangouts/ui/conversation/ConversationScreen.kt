package com.ysengoku.ft_hangouts.ui.conversation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ysengoku.ft_hangouts.R
import com.ysengoku.ft_hangouts.navigation.Navigator
import com.ysengoku.ft_hangouts.ui.Action
import com.ysengoku.ft_hangouts.ui.NavigationIcon
import com.ysengoku.ft_hangouts.ui.Screen

class ConversationScreen(
    inflater: LayoutInflater,
    container: ViewGroup,
    private val navigator: Navigator,
    private val contactId: Long
): Screen {
    override val view: View = inflater.inflate(R.layout.screen_conversation, container, false)
    override val title = container.context.getString(R.string.home_title)
    override val navigationIcon = NavigationIcon.BACK
    override val action: Action? = null

    init {
        view.findViewById<TextView>(R.id.conversation_placeholder).text = "Contact Form : id=$contactId"
    }
}