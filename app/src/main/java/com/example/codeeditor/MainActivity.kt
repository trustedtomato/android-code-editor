package com.example.codeeditor

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import kotlin.properties.Delegates.observable

class MainActivity : RequesterActivity() {

    private var textEditor: EditText? = null
    private var textMetadata: TextMetadata by observable(TextMetadata(null, true)) { _, _, textMetadata ->
        var title = textMetadata.uri?.let { uri -> getFileName(uri, this) } ?: getString(R.string.unsaved_file)
        if (!textMetadata.saved) title += '*'
        supportActionBar?.title = title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        textEditor = findViewById(R.id.text_editor)
        // Trigger setting actionBar title
        textMetadata = textMetadata

        textEditor!!.doOnTextChanged { text, start, _, count ->
            if (textMetadata.saved) {
                textMetadata = TextMetadata(textMetadata.uri, false)
            }
            autoIndent(text, start, count)?.let { newEditTextState ->
                textEditor!!.setText(newEditTextState.text)
                textEditor!!.setSelection(newEditTextState.cursorPosition)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_open_file -> {
                requestPermissions(arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )) { results ->
                    val granted = results.all { (_, granted) ->
                        granted
                    }
                    if (!granted) return@requestPermissions
                    requestIntent(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        type = "*/*"
                        putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("text/*", "application/*"))
                        addCategory(Intent.CATEGORY_OPENABLE)
                    }) { (resultCode, data) ->
                        if (resultCode != Activity.RESULT_OK || data?.data == null) return@requestIntent

                        val textFileUri = data.data
                        contentResolver.takePersistableUriPermission(textFileUri!!,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        )

                        val text = readTextFromUri(this, textFileUri)
                        textEditor!!.setText(text)

                        textMetadata = TextMetadata(textFileUri, true)
                    }
                }
                true
            }
            R.id.action_save -> {
                // Don't save if the file is already saved.
                if (textMetadata.saved) return true

                if (textMetadata.uri == null) {
                    saveAs()
                } else {
                    requestPermissions(arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )) { results ->
                        val granted = results.all { (_, granted) ->
                            granted
                        }
                        if (!granted) return@requestPermissions

                        saveTextToUri(textMetadata.uri!!, textEditor?.text.toString(), this)
                        textMetadata = TextMetadata(textMetadata.uri, true)
                    }
                }
                true
            }
            R.id.action_save_as -> {
                saveAs()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveAs() {
        requestIntent(Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/octet-stream"
        }) { (resultCode, data) ->
            if (resultCode != Activity.RESULT_OK || data?.data == null) return@requestIntent

            val uri = data.data
            saveTextToUri(uri!!, textEditor?.text.toString(), this)
            textMetadata = TextMetadata(uri, true)
        }
    }
}