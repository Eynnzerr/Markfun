package com.eynnzerr.memorymarkdown.ui.write.markdown

import android.util.Log
import android.widget.EditText
import com.eynnzerr.memorymarkdown.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class MarkdownOption(val text: String) {
    IMAGE(""),
    HEADER("#"),
    BOLD("**"),
    ITALIC("*"),
    STRIKETHROUGH("~~"),
    CODEINLINE("`"),
    CODEBLOCK("\n```\n"),
    QUOTE("\n> "),
    DIVIDER("\n---\n"),
    HYPERLINK("[]()"),
    TASKLIST("\n- [ ] "),
    ORDEREDLIST(""),
    UNORDEREDLIST("\n- "),
    TABLE(""),
    LEFT(""),
    RIGHT(""),
    NONE("")
}

val optionList = listOf(
    R.drawable.option_header,
    R.drawable.option_bold,
    R.drawable.option_italic,
    R.drawable.option_delete_line,
    R.drawable.option_code_inline,
    R.drawable.option_code_block,
    R.drawable.option_quote,
    R.drawable.option_divider,
    R.drawable.option_hyperlink,
    R.drawable.option_task_list,
    // R.drawable.option_ordered_list,
    R.drawable.option_unordered_list,
    R.drawable.option_table,
    R.drawable.option_arrow_left,
    R.drawable.option_arrow_right,
    // R.drawable.option_undo
)

var picName = ""
var picUri = ""

private var currentOrder = 1  // TODO

fun EditText.addOption(option: MarkdownOption) {
    if (option == MarkdownOption.IMAGE) {
        Log.d(TAG, "addOption: Picture name: $picName, Picture uri: $picUri")
        text?.insert(selectionStart, "\n![$picName]($picUri)\n")
    }
    else if (option == MarkdownOption.LEFT) {
        if (selectionStart > 0) setSelection(selectionStart - 1)
    }
    else if (option == MarkdownOption.RIGHT) {
        if (selectionStart < text.length) setSelection(selectionStart + 1)
    }
    else if (option == MarkdownOption.ORDEREDLIST) {
        val optionText = "\n${currentOrder++}. "
        text?.insert(selectionStart, optionText)
    }
    else if (option != MarkdownOption.NONE) {
        text?.insert(selectionStart, option.text)
        // text?.insert(selectionEnd + option.text.length, option.text) 用户习惯：半符号或符号包裹
        // setSelection(selectionStart + option.text.length)
    }
}

private const val TAG = "MarkdownOption"