package com.ysengoku.ft_hangouts.data.phone

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.ysengoku.ft_hangouts.navigation.RequestCodes
import com.ysengoku.ft_hangouts.ui.Permissions

class CallLauncher(private val activity: Activity) {
    private var pendingPhoneCall: String? = null

    fun call(phone: String) {
        activity.startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$phone")))
    }
}
