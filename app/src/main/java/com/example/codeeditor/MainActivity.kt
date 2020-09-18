package com.example.codeeditor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.core.widget.doOnTextChanged

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val textEditor = findViewById<EditText>(R.id.text)

        // Auto-indent
        textEditor.doOnTextChanged { text, start, _, count ->
            if (text == null) return@doOnTextChanged

            val insertedText = text
                .slice(start until start+count)
                .toString()

            insertedText.withIndex().find {
                it.value == '\n'
            }?.run {
                val insertedLineBreakIndex = this.index + start

                // If the inserted linebreak is already indented, leave it as it is.
                if (text.length > insertedLineBreakIndex + 1 && "\t ".contains(text[insertedLineBreakIndex + 1])) return@run

                val previousLineBreakIndex = text
                    .withIndex()
                    .take(insertedLineBreakIndex)
                    .reversed()
                    .find {
                        it.value == '\n'
                    }?.index ?: -1

                // Get the spaces from the previous line.
                val spaces = text.drop(previousLineBreakIndex + 1).takeWhile {
                    "\t ".contains(it)
                }.toString()

                if (spaces.isEmpty()) return@run

                val newText =
                    text.take(insertedLineBreakIndex+1).toString() +
                    spaces +
                    text.drop(insertedLineBreakIndex+1)

                textEditor.setText(newText)
                textEditor.setSelection(insertedLineBreakIndex+1+spaces.length)
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
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}