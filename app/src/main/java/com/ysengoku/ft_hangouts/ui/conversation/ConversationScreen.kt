package com.ysengoku.ft_hangouts.ui.conversation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ListView
import android.widget.TextView
import com.ysengoku.ft_hangouts.R
import com.ysengoku.ft_hangouts.data.repository.ContactRepository
import com.ysengoku.ft_hangouts.data.repository.MessageRepository
import com.ysengoku.ft_hangouts.navigation.Navigator
import com.ysengoku.ft_hangouts.ui.Action
import com.ysengoku.ft_hangouts.ui.NavigationIcon
import com.ysengoku.ft_hangouts.ui.Screen

class ConversationScreen(
    inflater: LayoutInflater,
    container: ViewGroup,
    private val navigator: Navigator,
    contactRepository: ContactRepository,
    messageRepository: MessageRepository,
    private val contactId: Long,
    savedState: Bundle?
): Screen {
    override val view: View = inflater.inflate(R.layout.screen_conversation, container, false)
    override val title = container.context.getString(R.string.home_title)
    override val navigationIcon = NavigationIcon.BACK
    override val action: Action? = null

    private val listView: ListView = view.findViewById(R.id.message_list)
    private val viewModel = ConversationViewModel(contactRepository, messageRepository)
    private val adapter = ConversationAdapter(inflater)

    private fun loadMore() = viewModel.loadNextPage(contactId) { page ->
        val isFirstPage = adapter.count == 0
        val first = listView.firstVisiblePosition
        val top = listView.getChildAt(0)?.top ?: 0
        adapter.addOlder(page)
        if (isFirstPage) {
            listView.setSelection(adapter.count - 1)
        } else {
            listView.setSelectionFromTop(first + page.size, top)
        }
    }

    init {
        viewModel.loadContactName(contactId) { name ->
            if (name == null) navigator.back() else navigator.setTitle(name)
        }

        listView.adapter = adapter
        listView.setOnScrollListener(object: AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
            override fun onScroll(view: AbsListView, first: Int, visible: Int, total: Int) {
                if (total > 0 && first + visible >= total - 3) {
                    loadMore()
                }
            }
        })
        loadMore()
    }
}
