package com.ysengoku.ft_hangouts.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import java.io.File
import java.io.InputStream
import java.io.IOException

private const val MAX_SHORT_SIDE = 512
private const val JPEG_QUALITY = 85

fun copyImageToAppCache(uri: Uri, context: Context): String? {
    val dir = File(context.cacheDir, "photos").apply { mkdirs() }
    val file = File(dir, "${System.currentTimeMillis()}.jpg")
    return try {
        val bitmap = decodeResized(uri, context)
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, it) }
        file.absolutePath
    } catch (e: IOException) {
        null
    }
}

/** Decodes the image at [uri], scaled down so that its shorter side is at most MAX_SHORT_SIDE. */
private fun decodeResized(uri: Uri, context: Context): Bitmap {
    val source = ImageDecoder.createSource(context.contentResolver, uri)
    return ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
        val width = info.size.width
        val height = info.size.height
        val scale = MAX_SHORT_SIDE.toFloat() / minOf(width, height)
        if (scale < 1f) {
            decoder.setTargetSize((width * scale).toInt(), (height * scale).toInt())
        }
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
