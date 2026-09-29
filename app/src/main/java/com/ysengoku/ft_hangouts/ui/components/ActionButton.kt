package com.ysengoku.ft_hangouts.ui.components

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.ysengoku.ft_hangouts.R

fun bindActionButton(wrapper: View, icon: Int, label: Int, onClick: () -> Unit) {
    val iconButton = wrapper.findViewById<ImageButton>(R.id.action_button_icon)
    iconButton.setImageResource(icon)
    iconButton.contentDescription = wrapper.context.getString(label)
    iconButton.setOnClickListener { onClick() }
    wrapper.findViewById<TextView>(R.id.action_button_label).setText(label)
}
