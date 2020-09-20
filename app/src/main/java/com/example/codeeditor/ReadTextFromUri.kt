package com.example.codeeditor

import android.content.Context
import android.net.Uri
import java.io.BufferedReader
import java.io.InputStreamReader

fun readTextFromUri(context: Context, uri: Uri): String? {
    return context.contentResolver.openInputStream(uri)?.let {
        val reader = BufferedReader(InputStreamReader(it))
        val text = StringBuilder()
        readLines@ while (true) {
            val line = reader.readLine() ?: break@readLines
            text.append(line).append('\n')
        }
        text.toString().dropLast(1)
    }
}
