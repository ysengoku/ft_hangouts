package com.ysengoku.ft_hangouts.ui.components

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.ysengoku.ft_hangouts.R
import com.ysengoku.ft_hangouts.ui.Screen

class TopAppBar(root: View) {
    private val title: TextView = root.findViewById(R.id.top_app_bar_title)
    private val navigation: ImageButton = root.findViewById(R.id.top_app_bar_navigation)

    fun update(screen: Screen) {
        title.text = screen.title
        navigation.visibility = if (screen.showNavigation) View.VISIBLE else View.GONE
    }

    fun setOnNavigationClick(listener: () -> Unit) {
        navigation.setOnClickListener { listener() }
    }
}
