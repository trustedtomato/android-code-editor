package com.example.codeeditor

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

fun getFileName(uri: Uri, context: Context): String? {
    if (uri.scheme == "content") {
        context.contentResolver.query(uri, null, null, null, null)?.use {
            it.moveToFirst()
            return@getFileName it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
    }
    return uri.path?.let { path ->
        val cut = path.lastIndexOf('/')
        if (cut != -1) {
            path.substring(cut + 1)
        } else {
            null
        }
    }
}