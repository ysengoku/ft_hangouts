package com.ysengoku.ft_hangouts.ui.contacts

import com.ysengoku.ft_hangouts.data.model.ContactSummary
import com.ysengoku.ft_hangouts.data.repository.ContactRepository
import com.ysengoku.ft_hangouts.util.BackgroundExecutor

class ContactListViewModel(private val repository: ContactRepository) {
    private val pageSize = 10
    private var offset = 0
    private var query: String? = null
    private var isLoading = false
    private var hasMore = true

    fun loadNextPage(onLoaded: (List<ContactSummary>) -> Unit) {
        if (isLoading || !hasMore) {
            return
        }
        isLoading = true
        BackgroundExecutor.execute {
            val requested = query
            val page = repository.getPage(pageSize, offset, requested)
            BackgroundExecutor.main {
                isLoading = false
                if (requested != query) {
                    loadNextPage(onLoaded)
                    return@main
                }
                offset += page.size
                hasMore = page.size == pageSize
                onLoaded(page)
            }
        }
    }

    fun setQuery(text: String?) {
        query = text
        resetPaging()
    }

    fun resetPaging() {
        offset = 0
        hasMore = true
    }
}
