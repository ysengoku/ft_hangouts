package com.ysengoku.ft_hangouts.ui.conversation

import android.content.Context
import android.text.format.DateFormat
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
import com.ysengoku.ft_hangouts.ui.format.dateLabel
import com.ysengoku.ft_hangouts.ui.format.timeLabel
import com.ysengoku.ft_hangouts.ui.themeColor

class ConversationAdapter(private val inflater: LayoutInflater) : BaseAdapter() {
    private val items = mutableListOf<Message>()
    private var shownTimeId: Long? = null

    var contactName: String = ""
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
        val view = (convertView ?: inflater.inflate(R.layout.item_message, parent, false)) as LinearLayout

        val message = items[position]
        val content = view.findViewById<TextView>(R.id.message_content)
        content.text = message.content
        val date = dateLabel(parent.context, message.createdAt)
        val time = timeLabel(parent.context, message.createdAt)

        if (parent.width > 0) {
            content.maxWidth = (parent.width * 0.75f).toInt()
        }
        if (message.isIncoming) {
            view.gravity = Gravity.START
            content.setBackgroundResource(R.drawable.bg_message_receiver)
            content.setTextColor(parent.context.themeColor(R.attr.colorOnSurface))
        } else {
            view.gravity = Gravity.END
            content.setBackgroundResource(R.drawable.bg_message_sender)
            content.setTextColor(parent.context.themeColor(R.attr.colorOnPrimary))
        }

        val timeView = view.findViewById<TextView>(R.id.message_time)
        val name = if (message.isIncoming) contactName else "You"
        timeView.text = listOfNotNull(name, listOfNotNull(date, time).joinToString(" ")).joinToString(" · ")
        timeView.visibility = if (message.id == shownTimeId) View.VISIBLE else View.GONE
        
        content.setOnClickListener {
            shownTimeId = if (shownTimeId == message.id) null else message.id
            notifyDataSetChanged()
        }

        val divider = view.findViewById<TextView>(R.id.message_divider)
        val previous = items.getOrNull(position - 1)
        if (previous == null || message.createdAt - previous.createdAt > DateUtils.HOUR_IN_MILLIS) {
            divider.text = listOfNotNull(date, time).joinToString(" · ")
            divider.visibility = View.VISIBLE
        } else {
            divider.visibility = View.GONE
        }
        
        return view
    }
}
