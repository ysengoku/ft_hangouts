package com.ysengoku.ft_hangouts.ui.contacts

import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListPopupWindow
import android.widget.ListView
import com.ysengoku.ft_hangouts.R
import com.ysengoku.ft_hangouts.data.model.Message
import com.ysengoku.ft_hangouts.data.repository.ContactRepository
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
    repository: ContactRepository,
    private val onThemeSelected: (String) -> Unit
): Screen {
    private val context = container.context
    override val view: View = inflater.inflate(R.layout.screen_contact_list, container, false)
    override val title = container.context.getString(R.string.home_title)
    override val navigationIcon = NavigationIcon.NONE
    override val action = Action(R.drawable.ic_palette, R.string.change_color_theme) { anchor -> 
        showThemeMenu(anchor)
    }
    override val snackbarAnchor: View = view.findViewById(R.id.fab_add_contact)

    override fun onMessageReceived(message: Message): Boolean {
        viewModel.resetPaging()
        adapter.clear()
        loadMore()
        return false
    }

    private val viewModel = ContactListViewModel(repository)
    private val adapter = ContactSummaryAdapter(inflater)
    private val listView: ListView = view.findViewById(R.id.contact_list)
    private val searchInputView: EditText = view.findViewById(R.id.search_input)
    private val searchClearButton: ImageButton = view.findViewById<ImageButton>(R.id.search_clear)

    init {
        view.findViewById<ImageButton>(R.id.fab_add_contact).setOnClickListener {
            navigator.navigate(Route.ContactForm(null))
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

        searchInputView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchClearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                viewModel.setQuery(s?.toString())
                adapter.clear()
                loadMore()
            }
        })

        searchClearButton.setOnClickListener { searchInputView.text.clear() }

        listView.setOnItemClickListener { _, _, _, id ->
            navigator.navigate(Route.ContactDetail(id))
        }
    }

    private fun loadMore() = viewModel.loadNextPage { adapter.addAll(it) }

    private fun showThemeMenu(anchor: View) {
        val current = ThemePreferences(context).getTheme()
        val popup = ListPopupWindow(context)
        popup.anchorView = anchor
        popup.setAdapter(ThemeMenuAdapter(context, current))
        popup.width = context.resources.getDimensionPixelSize(R.dimen.menu_item_width)
        popup.setDropDownGravity(Gravity.END)
        popup.horizontalOffset = -context.resources.getDimensionPixelSize(R.dimen.spacing_extra_small)
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
