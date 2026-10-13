package com.ysengoku.ft_hangouts.ui.components

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.MotionEvent
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import com.ysengoku.ft_hangouts.R
import com.ysengoku.ft_hangouts.ui.format.countryLabel
import com.ysengoku.ft_hangouts.ui.format.countryName

private class CountryItem(val code: String, val label: String) {
    override fun toString() = label
}

class CountryPickerBottomSheet(
    context: Context,
    codes: List<String>,
    private val onSelected: (String) -> Unit
): Dialog(context) {
    private val items = codes.sortedBy { countryName(it) }
        .map { CountryItem(it, countryLabel(it)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottom_sheet_country_picker)
        window?.apply {
            setGravity(Gravity.BOTTOM)
            val height = (context.resources.displayMetrics.heightPixels * 0.7).toInt()
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height)
            setBackgroundDrawableResource(R.drawable.bg_bottom_sheet)
            setWindowAnimations(android.R.style.Animation_InputMethod)
        }

        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, items)

        val sheet = findViewById<View>(R.id.bottom_sheet_country_picker)
        val handle = findViewById<View>(R.id.drag_handle)
        var startY = 0f

        val search = findViewById<EditText>(R.id.country_search)
        val list = findViewById<ListView>(R.id.country_list)
        list.adapter = adapter

        handle.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> startY = event.rawY
                MotionEvent.ACTION_MOVE -> sheet.translationY = maxOf(0f, event.rawY - startY)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (sheet.translationY > sheet.height / 4) {
                        dismiss()
                    } else {
                        sheet.animate().translationY(0f).setDuration(150)
                    }
                    v.performClick()
                } 
            }
            true
        }

        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { adapter.filter.filter(s) }
        })

        list.setOnItemClickListener { _, _, position, _ ->
            val code = adapter.getItem(position)!!.code
            onSelected(code)
            dismiss()
        }
    }
}
