package com.ysengoku.ft_hangouts.ui.components

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ysengoku.ft_hangouts.R

fun bindDetailField(field: View, icon: Int, label: Int, value: String?) {
    if (value.isNullOrBlank()) {
        field.visibility = View.GONE
        return
    }
    field.findViewById<ImageView>(R.id.field_icon).setImageResource(icon)
    field.findViewById<TextView>(R.id.field_label).setText(label)
    field.findViewById<TextView>(R.id.field_value).text = value
    field.visibility = View.VISIBLE
}
