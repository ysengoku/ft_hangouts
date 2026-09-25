package com.ysengoku.ft_hangouts.navigation

import android.view.LayoutInflater
import android.view.ViewGroup
import com.ysengoku.ft_hangouts.ui.Screen
import com.ysengoku.ft_hangouts.ui.components.TopAppBar
import com.ysengoku.ft_hangouts.ui.contacts.ContactListScreen

class Navigator(
    private val container: ViewGroup,
    private val topAppBar: TopAppBar
) {
    private val inflater = LayoutInflater.from(container.context)

    fun show(screen: Screen) {
        container.removeAllViews()
        container.addView(screen.view)
        topAppBar.update(screen)
    }

    fun showContactList() = show(ContactListScreen(inflater, container))
}
