package com.ysengoku.ft_hangouts.ui.components

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ysengoku.ft_hangouts.R

private const val INITIALS_PADDING_RATIO = 0.18f

fun bindAvatar(avatar: View, firstName: String, lastName: String?, picture: String?) {
    avatar.clipToOutline = true
    val initialsView = avatar.findViewById<TextView>(R.id.avatar_initials)
    val imageView = avatar.findViewById<ImageView>(R.id.avatar_picture)
    
    
    if (picture != null) {
        imageView.setImageURI(Uri.parse(picture))
        imageView.visibility = View.VISIBLE
        initialsView.visibility = View.GONE
    } else {
        val padding = (avatar.layoutParams.width * INITIALS_PADDING_RATIO).toInt()
        initialsView.setPadding(padding, padding, padding, padding)
        imageView.setImageDrawable(null)
        imageView.visibility = View.GONE
        initialsView.text = (firstName.take(1) + lastName.orEmpty().take(1)).uppercase()
        initialsView.visibility = View.VISIBLE
    }
}
