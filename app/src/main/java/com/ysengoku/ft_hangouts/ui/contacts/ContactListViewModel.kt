package com.ysengoku.ft_hangouts.ui.contacts

import com.ysengoku.ft_hangouts.util.BackgroundExecutor
import com.ysengoku.ft_hangouts.data.model.ContactSummary
import com.ysengoku.ft_hangouts.data.repository.ContactRepository

class ContactListViewModel(private val repository: ContactRepository) {
    private val pageSize = 10
    private var offset = 0
    private var isLoading = false
    private var hasMore = true

    fun loadNextPage(onLoaded: (List<ContactSummary>) -> Unit) {
        if (isLoading || !hasMore) {
            return
        }
        isLoading = true
        BackgroundExecutor.execute {
            val page = repository.getPage(pageSize, offset)
            BackgroundExecutor.main {
                offset += page.size
                hasMore = page.size == pageSize
                isLoading = false
                onLoaded(page)
            }
        }
    }
}
