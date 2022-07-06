package com.eynnzerr.memorymarkdown.ui.write

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eynnzerr.memorymarkdown.R
import com.eynnzerr.memorymarkdown.base.CPApplication
import com.eynnzerr.memorymarkdown.data.MMKVUtils
import com.eynnzerr.memorymarkdown.data.database.MarkdownRepository
import com.eynnzerr.memorymarkdown.data.database.toData
import com.eynnzerr.memorymarkdown.ui.write.markdown.MarkdownAgent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
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
            // read in pre-loaded view contents from MMKV when initializing
            mvUtils.decodeString("craft_title"),
            mvUtils.decodeString("craft_contents"))
    )
    val uiState: StateFlow<Markdown> = _uiState

    fun updateTitle(title: String) = _uiState.update { it.copy(title = title) }
    fun updateContent(content: String) = _uiState.update { it.copy(content = content) }

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

    fun saveFileAs(uri: Uri) {
        val file = uri.toFile().apply {
            if (exists()) delete()
        }
        Log.d(TAG, "saveFileAs: Save ${file.name} to ${file.path}")
        writeContent(file)
    }

    fun stashFile() {
        val basePath = CPApplication.context.getExternalFilesDir(null)?.absolutePath + "/files/markdown"
        val fileName = if (_uiState.value.title == "") "new.md" else _uiState.value.title + ".md"
        val filePath = basePath + fileName
        val mdFile = File(filePath).apply {
            if (exists()) delete()
        }
        writeContent(mdFile)
    }

    private fun writeContent(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "writeContent: Save ${file.name} to ${file.path}")
            with(file.writer()) {
                write(_uiState.value.content)
                flush()
                close()
            }
        }
        Toast.makeText(CPApplication.context, "Stash ${file.name} to ${file.path}", Toast.LENGTH_SHORT).show()
    }

    fun getEditor() = markdownAgent.editor

    inner class MarkdownOption(
        val iconResource: Int,
        val action: () -> Unit
    )

    val optionList = mutableListOf(
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
        MarkdownOption(R.drawable.option_divider) {
            _uiState.update { it.copy(content = it.content.plus("\n---\n")) }
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

    fun addOption(iconResource: Int, action: () -> Unit) = optionList.add(MarkdownOption(iconResource, action))
}

private const val TAG = "WriteViewModel"