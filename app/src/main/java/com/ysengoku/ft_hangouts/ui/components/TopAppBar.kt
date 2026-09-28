package com.ysengoku.ft_hangouts.ui.components

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.ysengoku.ft_hangouts.R
import com.ysengoku.ft_hangouts.ui.NavigationIcon
import com.ysengoku.ft_hangouts.ui.Screen

class TopAppBar(root: View) {
    private val title: TextView = root.findViewById(R.id.top_app_bar_title)
    private val navigationButton: ImageButton = root.findViewById(R.id.top_app_bar_navigation)
    private val actionButton: ImageButton = root.findViewById(R.id.top_app_bar_action)

    fun update(screen: Screen) {
        title.text = screen.title

        when (screen.navigationIcon) {
            NavigationIcon.NONE -> navigationButton.visibility = View.GONE
            NavigationIcon.BACK -> {
                navigationButton.setImageResource(R.drawable.ic_arrow_back)
                navigationButton.contentDescription = navigationButton.context.getString(R.string.back)
                navigationButton.visibility = View.VISIBLE
            }
            NavigationIcon.CLOSE -> {
                navigationButton.setImageResource(R.drawable.ic_close)
                navigationButton.contentDescription = navigationButton.context.getString(R.string.close)
                navigationButton.visibility = View.VISIBLE
            }
        }

        val action = screen.action
        if (action == null) {
            actionButton.visibility = View.GONE
        } else {
            actionButton.setImageResource(action.icon)
            actionButton.contentDescription = actionButton.context.getString(action.label)
            actionButton.tooltipText = actionButton.context.getString(action.label)
            actionButton.setOnClickListener { action.onClick(it) }
            actionButton.visibility = View.VISIBLE
        }
    }

    fun setTitle(text: String) {
        title.text = text
    }

    fun setOnNavigationClick(listener: () -> Unit) {
        navigationButton.setOnClickListener { listener() }
    }

    fun setOnActionClick(listener: (View) -> Unit) {
        actionButton.setOnClickListener { listener(it) }
    }
}
