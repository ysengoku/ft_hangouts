package com.ysengoku.ft_hangouts.ui.conversation

import com.ysengoku.ft_hangouts.data.model.Message
import com.ysengoku.ft_hangouts.data.phone.SmsSender
import com.ysengoku.ft_hangouts.data.repository.ContactRepository
import com.ysengoku.ft_hangouts.data.repository.MessageRepository
import com.ysengoku.ft_hangouts.util.BackgroundExecutor

class ConversationViewModel(
    private val contactRepository: ContactRepository,
    private val messageRepository: MessageRepository,
    private val smsSender: SmsSender,
) {
    private val pageSize = 10
    private var offset = 0
    private var isLoading = false
    private var hasMore = true

    private var phone: String? = null

    fun loadContactName(contactId: Long, onLoaded: (String?) -> Unit) {
        BackgroundExecutor.execute {
            val contact = contactRepository.getById(contactId)
            val name = contact?.let { "${it.firstName} ${it.lastName.orEmpty()}".trim() }
            phone = contact?.phone
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

    fun send(contactId: Long, content: String, onSent: (Message?) -> Unit) {
        BackgroundExecutor.execute {
            val sent = phone?.let { smsSender.send(it, content) } ?: false
            val message = if (sent) {
                val draft = Message(0, contactId, false, System.currentTimeMillis(), content)
                draft.copy(id = messageRepository.create(draft))
            } else {
                null
            }
            BackgroundExecutor.main {
                if (message != null) offset += 1
                onSent(message)
            }
        }
    }
}
