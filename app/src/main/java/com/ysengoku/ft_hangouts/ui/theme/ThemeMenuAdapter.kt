package com.ysengoku.ft_hangouts.ui.theme

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ysengoku.ft_hangouts.R
import com.ysengoku.ft_hangouts.ui.themeColor

class ThemeMenuAdapter(
    private val context: Context,
    private val current: String
): BaseAdapter() {
    private val themes = ThemePreferences.THEME_LIST
    private val inflater = LayoutInflater.from(context)
    private val horizontalPadding = context.resources.getDimensionPixelSize(R.dimen.spacing_medium)

    override fun getCount() = themes.size
    override fun getItem(position: Int) = themes[position]
    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = (convertView ?: inflater.inflate(R.layout.item_theme_menu, parent, false)) as TextView
        val theme = themes[position]
        label.setText(ThemePreferences.labelResId(theme))

        if (theme == current) {
            label.background = context.getDrawable(R.drawable.bg_menu_item_selected)
            label.setTextColor(context.themeColor(R.attr.colorOnPrimaryContainer))
        } else {
            label.background = null
            label.setTextColor(context.getColor(ThemePreferences.primaryColorRes(theme)))
        }
        label.setPaddingRelative(horizontalPadding, 0, horizontalPadding, 0)
        return label
    }
}
