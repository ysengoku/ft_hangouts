package com.ysengoku.ft_hangouts.ui.contacts

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ysengoku.ft_hangouts.R
import com.ysengoku.ft_hangouts.data.model.ContactSummary

class ContactSummaryAdapter(private val inflater: LayoutInflater) : BaseAdapter() {
    private val items = mutableListOf<ContactSummary>()

    fun addAll(newItems: List<ContactSummary>) {
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getCount() = items.size
    override fun getItem(position: Int) = items[position]
    override fun getItemId(position: Int) = items[position].id

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.item_contact_summary, parent, false).also {
            it.findViewById<ImageView>(R.id.contact_picture).clipToOutline = true
        }
        val contact = items[position]

        view.findViewById<TextView>(R.id.contact_name).text = "${contact.firstName} ${contact.lastName}"

        val picture = view.findViewById<ImageView>(R.id.contact_picture)
        val initials = view.findViewById<TextView>(R.id.contact_initials)

        if (contact.picture != null) {
            picture.setImageURI(Uri.parse(contact.picture))
            picture.visibility = View.VISIBLE
            initials.visibility = View.GONE
        } else {
            picture.setImageDrawable(null)
            picture.visibility = View.GONE
            initials.text = (contact.firstName.take(1) + contact.lastName.take(1)).uppercase()
            initials.visibility = View.VISIBLE
        }
        return view
    }
}
