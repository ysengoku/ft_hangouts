package com.ysengoku.ft_hangouts.data.model

import java.time.LocalDate

data class Contact(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val company: String?,
    val phone: String,
    val address: String?,
    val birthday: LocalDate?, // In SQLite: TEXT in ISO format: "1995-04-12"
    val note: String?,
    val picture: String?
)

data class ContactSummary(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val picture: String?,
)
