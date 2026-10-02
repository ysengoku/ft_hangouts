package com.ysengoku.ft_hangouts.ui.components

import android.text.InputFilter
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.ysengoku.ft_hangouts.R
import com.ysengoku.ft_hangouts.ui.themeColor

fun bindFormField(
    field: View,
    inputId: Int,
    label: Int,
    inputType: Int,
    maxLength: Int = 50,
    required: Boolean = false
): EditText {
    val labelText = field.context.getString(label)
    field.findViewById<TextView>(R.id.form_field_label).text =
        if (required) "$labelText *" else labelText
    val input = field.findViewById<EditText>(R.id.form_field_input)
    input.id = inputId
    input.inputType = inputType
    input.filters = arrayOf(InputFilter.LengthFilter(maxLength))
    return input
}

/**
 * Shows <message> as an error below the field and draws the outline and label
 * in the error color. Pass `null` as <message> to clear the error.
 */
fun setFieldError(field: View, input: EditText, message: Int?) {
    val feedback = field.findViewById<TextView>(R.id.form_field_feedback)
    val label = field.findViewById<TextView>(R.id.form_field_label)
    if (message == null) {
        feedback.visibility = View.GONE
        label.setTextColor(field.context.themeColor(R.attr.colorOnSurfaceVariant))
        input.isActivated = false
        return
    }
    feedback.setText(message)
    feedback.visibility = View.VISIBLE
    label.setTextColor(field.context.themeColor(android.R.attr.colorError))
    input.isActivated = true
}
