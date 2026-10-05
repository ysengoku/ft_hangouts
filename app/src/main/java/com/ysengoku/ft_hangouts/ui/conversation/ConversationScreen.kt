package com.ysengoku.ft_hangouts.ui.conversation

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import com.ysengoku.ft_hangouts.R
import com.ysengoku.ft_hangouts.data.phone.SmsSender
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
    override val action = Action(icon = R.drawable.ic_call, label = R.string.call) { /*TODO: call()*/ }

    companion object {
        private const val REQUEST_SEND_SMS = 2
    }

    private val viewModel = ConversationViewModel(
        contactRepository,
        messageRepository,
        SmsSender(view.context)
    )
    private val adapter = ConversationAdapter(inflater)
    private val listView = view.findViewById<ListView>(R.id.message_list)
    private val input = view.findViewById<EditText>(R.id.message_input)
    private val sendButton = view.findViewById<ImageButton>(R.id.message_send)

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
        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
            override fun onScroll(view: AbsListView, first: Int, visible: Int, total: Int) {
                if (total > 0 && first <= 3) {
                    loadMore()
                }
            }
        })
        loadMore()

        input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                sendButton.visibility = if (s.isNullOrBlank()) View.GONE else View.VISIBLE
            }
        })

        sendButton.setOnClickListener {
            if (view.context.checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                (view.context as Activity).requestPermissions(arrayOf(Manifest.permission.SEND_SMS), REQUEST_SEND_SMS)
                return@setOnClickListener
            }

            viewModel.send(contactId, input.text.toString().trim()) { message ->
                if (message == null) {
                    Toast.makeText(view.context, R.string.error_send_failed, Toast.LENGTH_SHORT).show()
                    return@send
                }
                adapter.addNewer(message)
                input.text = null
                listView.setSelection(adapter.count - 1)
            }
        }
    }
}
