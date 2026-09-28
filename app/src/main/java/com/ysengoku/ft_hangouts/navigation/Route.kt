package com.ysengoku.ft_hangouts.navigation

sealed class Route {
    object ContactList : Route()
    data class ContactDetail(val contactId: Long) : Route()
    data class Conversation(val contactId: Long) : Route()
    data class ContactForm(val contactId: Long?) : Route()
}
