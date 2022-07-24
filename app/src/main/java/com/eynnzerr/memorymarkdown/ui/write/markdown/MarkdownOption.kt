package com.eynnzerr.memorymarkdown.ui.write.markdown

import android.widget.EditText
import com.eynnzerr.memorymarkdown.R

enum class MarkdownOption(val text: String) {
    HEADER("#"),
    BOLD("**"),
    ITALIC("*"),
    STRIKETHROUGH("~~"),
    CODEINLINE("`"),
    CODEBLOCK("\n```\n"),
    QUOTE("\n> "),
    DIVIDER("\n---\n"),
    HYPERLINK("()[]"),
    TASKLIST("\n- [ ] "),
    ORDEREDLIST(""),
    UNORDEREDLIST(""),
    TABLE(""),
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
    R.drawable.option_ordered_list,
    R.drawable.option_unordered_list,
    R.drawable.option_table,
    R.drawable.option_arrow_left,
    R.drawable.option_arrow_right,
    R.drawable.option_undo
)

fun EditText.addOption(option: MarkdownOption) {
    if (option != MarkdownOption.NONE) {
        text?.insert(selectionStart, option.text)
        // text?.insert(selectionEnd + option.text.length, option.text) 用户习惯：半符号或符号包裹
        // setSelection(selectionStart + option.text.length)
    }
}