package com.ysengoku.ft_hangouts.ui.conversation

import com.ysengoku.ft_hangouts.data.model.Message
import com.ysengoku.ft_hangouts.data.repository.ContactRepository
import com.ysengoku.ft_hangouts.data.repository.MessageRepository
import com.ysengoku.ft_hangouts.util.BackgroundExecutor

class ConversationViewModel(
    private val contactRepository: ContactRepository,
    private val messageRepository: MessageRepository
) {
    private val pageSize = 10
    private var offset = 0
    private var isLoading = false
    private var hasMore = true

    fun loadContactName(contactId: Long, onLoaded: (String?) -> Unit) {
        BackgroundExecutor.execute {
            val contact = contactRepository.getById(contactId)
            val name = contact?.let { "${it.firstName} ${it.lastName.orEmpty()}".trim() }
            BackgroundExecutor.main { onLoaded(name) }
        }
    }

    fun loadNextPage(contactId: Long, onLoaded: (List<Message>) -> Unit) {
        if (isLoading || !hasMore) {
            return
        }
        isLoading = true
        BackgroundExecutor.execute {
            val page = messageRepository.getPageByContactId(contactId, pageSize, offset)
            BackgroundExecutor.main {
                offset += page.size
                hasMore = page.size == pageSize
                isLoading = false
                onLoaded(page)
            }
        }
    }
}