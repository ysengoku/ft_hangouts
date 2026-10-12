package com.ysengoku.ft_hangouts.data.phone

import android.content.Context
import android.content.Intent
import android.net.Uri

fun startCall(context: Context, phone: String) {
    val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${toDialable(phone)}"))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}
