package com.ysengoku.ft_hangouts.ui.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.ysengoku.ft_hangouts.R
import com.ysengoku.ft_hangouts.data.model.ContactSummary
import com.ysengoku.ft_hangouts.ui.Screen

class ContactListScreen(inflater: LayoutInflater, container: ViewGroup): Screen {
    override val view: View = inflater.inflate(R.layout.screen_contact_list, container, false)
    override val title = container.context.getString(R.string.app_name)
    override val showNavigation = false

    private val listView: ListView = view.findViewById(R.id.contact_list)
    private val adapter = ContactSummaryAdapter(inflater)

    init {
        listView.adapter = adapter
        adapter.addAll(
            listOf(
                ContactSummary(1, "Colette", "Martin", null),
                ContactSummary(2, "Bob", "Durand", null),
                ContactSummary(3, "Naomi", "Sato", null),
            )
        )
    }
}
