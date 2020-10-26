package com.example.codeeditor

import android.content.Context
import android.net.Uri
import java.io.FileOutputStream

fun saveTextToUri(uri: Uri, text: String, context: Context) {
    context.contentResolver.openFileDescriptor(uri, "w")?.use { file ->
        FileOutputStream(file.fileDescriptor).use { stream ->
            stream.write(text.toByteArray())
        }
    }
}