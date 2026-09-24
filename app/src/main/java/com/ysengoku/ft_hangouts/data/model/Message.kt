package com.ysengoku.ft_hangouts.data.model

data class Message(
    val id: Long,
    val contactId: Long,
    val isIncoming: Boolean,
    val createdAt: Long,
    val content: String
)
