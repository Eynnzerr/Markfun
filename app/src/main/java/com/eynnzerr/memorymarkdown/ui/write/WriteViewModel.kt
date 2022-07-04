package com.eynnzerr.memorymarkdown.ui.write

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eynnzerr.memorymarkdown.R
import com.eynnzerr.memorymarkdown.data.MMKVUtils
import com.eynnzerr.memorymarkdown.data.database.MarkdownRepository
import com.eynnzerr.memorymarkdown.data.database.toData
import com.eynnzerr.memorymarkdown.ui.write.markdown.MarkdownAgent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Markdown(
    val title: String = "",
    val content: String = ""
)

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val markdownAgent: MarkdownAgent,
    private val mvUtils: MMKVUtils,
    private val repository: MarkdownRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(
        Markdown(
            mvUtils.decodeString("craft_title"),
            mvUtils.decodeString("craft_contents"))
    )
    val uiState: StateFlow<Markdown> = _uiState

    fun updateUiState(uiState: Markdown) {
        if (uiState.content == "") _uiState.update { it.copy(title = uiState.title) }
        else if (uiState.title == "") _uiState.update { it.copy(content = uiState.content) }
        else _uiState.update { uiState }
    }

    fun saveMarkdown() {
        viewModelScope.launch {
            repository.insertMarkdown(_uiState.value.toData())
        }
        emptyCraft()
    }

    fun saveCraft() {
        mvUtils.encodeString("craft_title", _uiState.value.title)
        mvUtils.encodeString("craft_contents", _uiState.value.content)
    }

    fun emptyCraft() {
        mvUtils.removeCraft()
        _uiState.update { Markdown() }
    }

    fun getEditor() = markdownAgent.editor

    inner class MarkdownOption(
        val iconResource: Int,
        val action: () -> Unit
    )

    val optionList = listOf(
        MarkdownOption(R.drawable.option_header) {
            _uiState.update { it.copy(content = it.content.plus("#")) }
        },
        MarkdownOption(R.drawable.option_bold) {
            _uiState.update { it.copy(content = it.content.plus("**")) }
        },
        MarkdownOption(R.drawable.option_italic) {
            _uiState.update { it.copy(content = it.content.plus("*")) }
        },
        MarkdownOption(R.drawable.option_delete_line) {
            _uiState.update { it.copy(content = it.content.plus("~~")) }
        },
        MarkdownOption(R.drawable.option_code_inline) {
            _uiState.update { it.copy(content = it.content.plus("`")) }
        },
        MarkdownOption(R.drawable.option_code_block) {
            _uiState.update { it.copy(content = it.content.plus("\n```")) }
        },
        MarkdownOption(R.drawable.option_quote) {
            _uiState.update { it.copy(content = it.content.plus("> ")) }
        },
        MarkdownOption(R.drawable.option_image) {

        },
        MarkdownOption(R.drawable.option_hyperlink) {

        },
        MarkdownOption(R.drawable.option_table) {

        },
        MarkdownOption(R.drawable.option_arrow_left) {

        },
        MarkdownOption(R.drawable.option_arrow_right) {

        },
        MarkdownOption(R.drawable.option_undo) {

        }
    )
}
