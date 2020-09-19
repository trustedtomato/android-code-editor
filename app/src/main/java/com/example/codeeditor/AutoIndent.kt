package com.example.codeeditor

fun autoIndent(text: CharSequence?, start: Int, count: Int): EditTextState? {
    if (text == null) return null

    val insertedText = text
        .slice(start until start+count)
        .toString()

    return insertedText.withIndex().find {
        it.value == '\n'
    }?.run {
        val insertedLineBreakIndex = this.index + start

        // If the inserted linebreak is already indented, leave it as it is.
        if (text.length > insertedLineBreakIndex + 1 && "\t ".contains(text[insertedLineBreakIndex + 1])) return@run null

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

        if (spaces.isEmpty()) return@run null

        // Add spaces to the existing text
        val newText = text.take(insertedLineBreakIndex+1).toString() +
                spaces +
                text.drop(insertedLineBreakIndex+1)

        object : EditTextState {
            override val text = newText
            override val cursorPosition = insertedLineBreakIndex+1+spaces.length
        }
    }
}