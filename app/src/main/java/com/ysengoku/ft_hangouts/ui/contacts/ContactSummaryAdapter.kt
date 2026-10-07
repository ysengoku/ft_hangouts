package com.ysengoku.ft_hangouts.ui.contacts

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.ysengoku.ft_hangouts.R
import com.ysengoku.ft_hangouts.data.model.ContactSummary
import com.ysengoku.ft_hangouts.ui.components.bindAvatar

class ContactSummaryAdapter(private val inflater: LayoutInflater) : BaseAdapter() {
    private val items = mutableListOf<ContactSummary>()

    fun addAll(newItems: List<ContactSummary>) {
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    override fun getCount() = items.size
    override fun getItem(position: Int) = items[position]
    override fun getItemId(position: Int) = items[position].id

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.item_contact_summary, parent, false)
        val contact = items[position]

        view.findViewById<TextView>(R.id.contact_name).text = listOfNotNull(contact.firstName, contact.lastName).joinToString(" ")

        bindAvatar(
            view.findViewById<FrameLayout>(R.id.contact_avatar),
            contact.firstName,
            contact.lastName,
            contact.picture
        )
        return view
    }
}
