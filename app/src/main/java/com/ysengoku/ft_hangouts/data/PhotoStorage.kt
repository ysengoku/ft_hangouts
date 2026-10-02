package com.ysengoku.ft_hangouts.data

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.InputStream
import java.io.IOException

fun copyImageToAppCache(uri: Uri, context: Context): String? {
    val dir = File(context.cacheDir, "photos").apply { mkdirs() }
    val file = File(dir, "${System.currentTimeMillis()}.jpg")
    return try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { input.copyTo(it) }
        } ?: return null
        file.absolutePath
    } catch (e: IOException) {
        null
    }
}

fun moveImageToStorage(path: String, context: Context): String? {
    val source = File(path)
    val dir = File(context.filesDir, "photos").apply { mkdirs() }
    val target = File(dir, source.name)
    return if (source.renameTo(target)) target.absolutePath else null
}

fun deleteOldImage(path: String?) {
    if (path == null) return
    File(path).delete()
}
