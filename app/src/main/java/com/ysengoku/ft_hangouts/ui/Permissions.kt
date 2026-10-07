package com.ysengoku.ft_hangouts.ui

import android.app.Activity
import android.content.pm.PackageManager

class Permissions(private val activity: Activity) {
    private val pendingPermissions = mutableMapOf<Int, (Boolean) -> Unit>()
    private var nextCode = 100

    fun request(vararg permissions: String, onResult: (Boolean) -> Unit) {
        val missing = permissions.filter { activity.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        if (missing.isEmpty()) {
            return onResult(true)
        }
        val code = ++nextCode
        pendingPermissions[code] = onResult
        activity.requestPermissions(missing.toTypedArray(), code)
    }

    fun onResult(requestCode: Int, grantResults: IntArray) {
        val callback = pendingPermissions.remove(requestCode) ?: return
        callback(grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED })
    }
}
