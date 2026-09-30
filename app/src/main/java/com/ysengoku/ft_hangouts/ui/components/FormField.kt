package com.ysengoku.ft_hangouts.ui.components

import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.ysengoku.ft_hangouts.R

fun bindFormField(
    field: View,
    inputId: Int,
    label: Int,
    inputType: Int,
    required: Boolean = false
): EditText {
    val labelText = field.context.getString(label)
    field.findViewById<TextView>(R.id.form_field_label).text =
        if (required) "$labelText *" else labelText
    val input = field.findViewById<EditText>(R.id.form_field_input)
    input.id = inputId
    input.inputType = inputType
    return input
}
