package com.ysengoku.ft_hangouts.ui.conversation

import android.text.format.DateUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.ysengoku.ft_hangouts.R
import com.ysengoku.ft_hangouts.data.model.Message
import com.ysengoku.ft_hangouts.ui.components.bindAvatar
import com.ysengoku.ft_hangouts.ui.format.dateTimeLabel
import com.ysengoku.ft_hangouts.ui.themeColor

class ConversationAdapter(private val inflater: LayoutInflater) : BaseAdapter() {
    private val items = mutableListOf<Message>()
    private var shownTimeId: Long? = null

    var contactName: String = ""
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var contactLastName: String? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var contactPicture: String? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /** Adds older messages (newest first from the DB) at the top of the list. */
    fun addOlder(page: List<Message>) {
        items.addAll(0, page.reversed())
        notifyDataSetChanged()
    }

    fun addNewer(message: Message) {
        items.add(message)
        notifyDataSetChanged()
    }

    override fun getCount() = items.size
    override fun getItem(position: Int) = items[position]
    override fun getItemId(position: Int) = items[position].id

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = (
            convertView ?: inflater.inflate(
                R.layout.item_message,
                parent,
                false
            )
            ) as LinearLayout

        val message = items[position]
        val content = view.findViewById<TextView>(R.id.message_content)
        content.text = message.content

        if (parent.width > 0) {
            content.maxWidth = (parent.width * 0.75f).toInt()
        }
        val avatarView: View = view.findViewById(R.id.message_avatar)
        if (message.isIncoming) {
            view.gravity = Gravity.START
            content.setBackgroundResource(R.drawable.bg_message_receiver)
            content.setTextColor(parent.context.themeColor(R.attr.colorOnSurface))
            bindAvatar(avatarView, contactName, contactLastName, contactPicture)
            avatarView.visibility = View.VISIBLE
        } else {
            view.gravity = Gravity.END
            content.setBackgroundResource(R.drawable.bg_message_sender)
            content.setTextColor(parent.context.themeColor(R.attr.colorOnPrimary))
            avatarView.visibility = View.GONE
        }

        val timeView = view.findViewById<TextView>(R.id.message_time)
        val name = if (message.isIncoming) contactName else parent.context.getString(R.string.you)
        timeView.text =
            listOfNotNull(
                name,
                dateTimeLabel(parent.context, message.createdAt)
            ).joinToString(" · ")
        timeView.visibility = if (message.id == shownTimeId) View.VISIBLE else View.GONE

        content.setOnClickListener {
            shownTimeId = if (shownTimeId == message.id) null else message.id
            notifyDataSetChanged()
        }

        val divider = view.findViewById<TextView>(R.id.message_divider)
        val previous = items.getOrNull(position - 1)
        if (previous == null || message.createdAt - previous.createdAt > DateUtils.HOUR_IN_MILLIS) {
            divider.text = dateTimeLabel(parent.context, message.createdAt, " · ")
            divider.visibility = View.VISIBLE
        } else {
            divider.visibility = View.GONE
        }

        return view
    }
}
