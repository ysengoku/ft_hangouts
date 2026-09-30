package com.ysengoku.ft_hangouts.navigation

sealed class Route {
    object ContactList : Route()
    data class ContactDetail(val contactId: Long) : Route()
    data class Conversation(val contactId: Long) : Route()
    data class ContactForm(val contactId: Long?) : Route()
}

fun Route.toPair(): Pair<String, Long> =
    when (this) {
        Route.ContactList -> "list" to -1L
        is Route.ContactDetail -> "detail" to contactId
        is Route.Conversation -> "conversation" to contactId
        is Route.ContactForm -> "form" to (contactId ?: -1L)
}

fun routeOf(kind: String, id: Long): Route =
    when (kind) {
        "detail" -> Route.ContactDetail(id)
        "conversation" -> Route.Conversation(id)
        "form" -> Route.ContactForm(id.takeIf { it != -1L })
        else -> Route.ContactList
    }
