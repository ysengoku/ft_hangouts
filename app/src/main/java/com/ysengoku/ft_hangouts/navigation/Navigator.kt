package com.ysengoku.ft_hangouts.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ysengoku.ft_hangouts.data.DatabaseHelper
import com.ysengoku.ft_hangouts.data.phone.MessageEvent
import com.ysengoku.ft_hangouts.data.model.Message
import com.ysengoku.ft_hangouts.data.repository.ContactRepository
import com.ysengoku.ft_hangouts.data.repository.MessageRepository
import com.ysengoku.ft_hangouts.R
import com.ysengoku.ft_hangouts.ui.Screen
import com.ysengoku.ft_hangouts.ui.components.TopAppBar
import com.ysengoku.ft_hangouts.ui.components.Snackbar
import com.ysengoku.ft_hangouts.ui.contacts.ContactDetailScreen
import com.ysengoku.ft_hangouts.ui.contacts.ContactFormScreen
import com.ysengoku.ft_hangouts.ui.contacts.ContactListScreen
import com.ysengoku.ft_hangouts.ui.conversation.ConversationScreen
import com.ysengoku.ft_hangouts.ui.Permissions

class Navigator(
    private val container: ViewGroup,
    private val topAppBar: TopAppBar,
    private val snackbar: Snackbar,
    private val permissionRequests: Permissions,
    val onThemeSelected: (String) -> Unit
) {
    private val inflater = LayoutInflater.from(container.context)
    private val history = ArrayDeque<Route>()
    private var current: Screen? = null

    companion object {
        private const val KEY_KINDS = "navigator_kinds"
        private const val KEY_IDS = "navigator_ids"
        private const val KEY_SCREEN = "navigator_screen"
    }

    fun start() = navigate(Route.ContactList)

    fun navigate(route: Route) {
        history.addLast(route)
        render(route)
    }

    fun back(): Boolean {
        if (current?.onBack() == true) {
            return true
        }
        return pop()
    }

    fun pop(): Boolean {
        if (history.size <= 1) {
            return false
        }
        history.removeLast()
        render(history.last())
        return true
    }

    fun setTitle(text: String) {
        topAppBar.setTitle(text)
    }

    // fun onPause() {

    // }

    // fun onResume() {
        
    // }

    private fun render(route: Route, savedState: Bundle? = null) {
        val screen = createScreen(route, savedState)
        current = screen
        container.removeAllViews()
        container.addView(screen.view)
        topAppBar.update(screen)
    }

    private val contactRepository = ContactRepository(DatabaseHelper.getInstance(container.context))
    private val messageRepository = MessageRepository(DatabaseHelper.getInstance(container.context))

    private val onMessageReceived: (Message, String) -> Unit = { message, name ->
        if (current?.onMessageReceived(message) != true) {
            showToast(container.context.getString(R.string.new_message, name))
        }
    }

    private fun createScreen(route: Route, savedState: Bundle? = null): Screen =
        when (route) {
            Route.ContactList -> ContactListScreen(inflater, container, this, contactRepository, onThemeSelected)
            is Route.ContactDetail -> ContactDetailScreen(inflater, container, this, contactRepository, route.contactId)
            is Route.Conversation -> ConversationScreen(inflater, container, this, contactRepository, messageRepository, route.contactId, savedState)
            is Route.ContactForm -> ContactFormScreen(inflater, container, this, contactRepository, route.contactId, savedState)
        }

    fun saveState(outState: Bundle) {
        val pairs = history.map { it.toPair() }
        outState.putStringArray(KEY_KINDS, pairs.map { it.first }.toTypedArray())
        outState.putLongArray(KEY_IDS, pairs.map { it.second }.toLongArray())

        val screenState = Bundle()
        current?.saveState(screenState)
        outState.putBundle(KEY_SCREEN, screenState)
    }

    fun restoreState(state: Bundle): Boolean {
        val kinds = state.getStringArray(KEY_KINDS) ?: return false
        val ids = state.getLongArray(KEY_IDS) ?: return false
        history.clear()
        kinds.indices.forEach {
            history.addLast(routeOf(kinds[it], ids[it]))
        }
        render(history.last(), state.getBundle(KEY_SCREEN))
        return true
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        current?.onActivityResult(requestCode, resultCode, data)
    }

    fun showToast(content: CharSequence, durationMs: Long = 3500) {
        snackbar.show(content, durationMs, current?.snackbarAnchor)
    }

    fun showToast(content: Int, durationMs: Long = 3500) {
        snackbar.show(container.context.getText(content), durationMs, current?.snackbarAnchor)
    }

    init { MessageEvent.register(onMessageReceived) }
    fun onDestroy() = MessageEvent.unregister(onMessageReceived)
}
