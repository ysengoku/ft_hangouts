package com.ysengoku.ft_hangouts.ui.components

import android.content.res.ColorStateList
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ysengoku.ft_hangouts.R
import com.ysengoku.ft_hangouts.ui.themeColor

private const val INITIALS_PADDING_RATIO = 0.18f

fun bindAvatar(avatar: View, firstName: String, lastName: String?, picture: String?) {
    avatar.clipToOutline = true
    val initialsView = avatar.findViewById<TextView>(R.id.avatar_initials)
    val imageView = avatar.findViewById<ImageView>(R.id.avatar_picture)
    
    if (picture != null) {
        imageView.setImageURI(Uri.parse(picture))
        imageView.imageTintList = null
        imageView.setPadding(0, 0, 0, 0)
        imageView.visibility = View.VISIBLE
        initialsView.visibility = View.GONE
        return
    }
    val firstNameInitial = firstName.firstOrNull()
    if (firstNameInitial == null || !firstNameInitial.isLetter()) {
        imageView.setImageResource(R.drawable.ic_person)
        val iconPadding = (avatar.layoutParams.width * 0.25f).toInt()
        imageView.setPadding(iconPadding, iconPadding, iconPadding, iconPadding)
        imageView.imageTintList = ColorStateList.valueOf(
            avatar.context.themeColor(R.attr.colorOnTertiaryContainer)
        )
        imageView.visibility = View.VISIBLE
        initialsView.visibility = View.GONE
        return
    }
    val padding = (avatar.layoutParams.width * INITIALS_PADDING_RATIO).toInt()
    initialsView.setPadding(padding, padding, padding, padding)
    imageView.setImageDrawable(null)
    imageView.visibility = View.GONE
    initialsView.text = (firstName.take(1) + lastName.orEmpty().take(1)).uppercase()
    initialsView.visibility = View.VISIBLE
}
