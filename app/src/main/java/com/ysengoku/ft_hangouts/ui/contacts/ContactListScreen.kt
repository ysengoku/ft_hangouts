package com.ysengoku.ft_hangouts.ui.contacts

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListPopupWindow
import android.widget.ListView
import com.ysengoku.ft_hangouts.R
import com.ysengoku.ft_hangouts.data.model.ContactSummary
import com.ysengoku.ft_hangouts.navigation.Navigator
import com.ysengoku.ft_hangouts.navigation.Route
import com.ysengoku.ft_hangouts.ui.Action
import com.ysengoku.ft_hangouts.ui.NavigationIcon
import com.ysengoku.ft_hangouts.ui.Screen
import com.ysengoku.ft_hangouts.ui.theme.ThemeMenuAdapter
import com.ysengoku.ft_hangouts.ui.theme.ThemePreferences

class ContactListScreen(
    inflater: LayoutInflater,
    container: ViewGroup,
    private val navigator: Navigator,
    private val onThemeSelected: (String) -> Unit
): Screen {
    private val context = container.context
    override val view: View = inflater.inflate(R.layout.screen_contact_list, container, false)
    override val title = container.context.getString(R.string.home_title)
    override val navigationIcon = NavigationIcon.NONE
    override val action = Action(R.drawable.ic_palette, R.string.change_color_theme) { anchor -> 
        showThemeMenu(anchor)
    }

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

        listView.setOnItemClickListener { _, _, _, id ->
            navigator.navigate(Route.ContactDetail(id))
        }
        view.findViewById<ImageButton>(R.id.fab_add_contact).setOnClickListener {
            navigator.navigate(Route.ContactForm(null))
        }
    }

    private fun showThemeMenu(anchor: View) {
        val current = ThemePreferences(context).getTheme()
        val popup = ListPopupWindow(context)
        popup.anchorView = anchor
        popup.setAdapter(ThemeMenuAdapter(context, current))
        popup.width = context.resources.getDimensionPixelSize(R.dimen.menu_width)
        popup.setDropDownGravity(Gravity.END)
        popup.horizontalOffset = -context.resources.getDimensionPixelSize(R.dimen.spacing_small)
        popup.setBackgroundDrawable(context.getDrawable(R.drawable.bg_menu))
        popup.isModal = true

        popup.setOnItemClickListener {_, _, position, _ ->
            popup.dismiss()
            val selected = ThemePreferences.THEME_LIST[position]
            if (selected != current) {
                onThemeSelected(selected)
            }
        }
        popup.show()
    }
}
