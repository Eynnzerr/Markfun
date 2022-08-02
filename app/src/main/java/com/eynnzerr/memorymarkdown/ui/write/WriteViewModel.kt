package com.eynnzerr.memorymarkdown.ui.write

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eynnzerr.memorymarkdown.base.CPApplication
import com.eynnzerr.memorymarkdown.data.MMKVUtils
import com.eynnzerr.memorymarkdown.data.PreferenceKeys
import com.eynnzerr.memorymarkdown.data.database.MarkdownData
import com.eynnzerr.memorymarkdown.data.database.MarkdownRepository
import com.eynnzerr.memorymarkdown.ui.write.markdown.MarkdownAgent
import com.eynnzerr.memorymarkdown.utils.UriUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class WriteUiState(
    val title: String = "",
    val content: String = "",
    var isReadOnly: Boolean = true
)

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val markdownAgent: MarkdownAgent,
    private val repository: MarkdownRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(
        WriteUiState(
                // read in pre-loaded view contents from MMKV when initializing
                title = MMKVUtils.decodeString(PreferenceKeys.CRAFT_TITLE),
                content = MMKVUtils.decodeString(PreferenceKeys.CRAFT_CONTENTS)
            )
        )
    val uiState = _uiState.asStateFlow()

    fun updateTitle(title: String) = _uiState.update { it.copy(title = title) }

    fun updateContent(content: String) = _uiState.update { it.copy(content = content) }

    fun updateMode() = _uiState.update { it.copy(isReadOnly = !it.isReadOnly) }

    // load content from uri, set readOnly mode
    fun loadMarkdown(uri: Uri?) {
        uri?.let {
            val title = DocumentFile.fromSingleUri(CPApplication.context, it)?.name?:""
            viewModelScope.launch(Dispatchers.IO) {
                val reader = CPApplication.context.contentResolver.openInputStream(it)?.reader()
                val text = reader?.readText()?:""
                _uiState.update { WriteUiState(title, text, true) } // If uri is invalid, then there will be no content displayed but empty screen.

            }
        }
    }

    // load content from database, set readOnly mode
    fun loadMarkdown(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = repository.getDataById(id).first()
            _uiState.update { WriteUiState(data.title, data.content, true) }
        }
    }

    // load content from MMKV craft, set write mode
    fun loadCraft() {
        _uiState.update {
            WriteUiState(
                MMKVUtils.decodeString(PreferenceKeys.CRAFT_TITLE),
                MMKVUtils.decodeString(PreferenceKeys.CRAFT_CONTENTS),
                false
            )
        }
    }

    fun saveMarkdown() {
        Log.d(TAG, "saveMarkdown")
        viewModelScope.launch {
            repository.insertMarkdown(
                MarkdownData(
                title = _uiState.value.title,
                content = _uiState.value.content,
                uri = UriUtils.uri,
                status = if (UriUtils.isUriValid) MarkdownData.STATUS_EXTERNAL else MarkdownData.STATUS_INTERNAL,
                isStarred = MarkdownData.NOT_STARRED
                )
            )
        }
        emptyCraft()
    }

    fun saveCraft() {
        MMKVUtils.encodeString(PreferenceKeys.CRAFT_TITLE, _uiState.value.title)
        MMKVUtils.encodeString(PreferenceKeys.CRAFT_CONTENTS, _uiState.value.content)
    }

    fun emptyCraft() {
        MMKVUtils.removeCraft()
        _uiState.update { WriteUiState() }
    }

    fun saveFileAs(uri: Uri) {
        if (MMKVUtils.decodeBoolean(PreferenceKeys.AUTOMATED_BACKUP)) {
            val content = _uiState.value.content // shallow copy
            viewModelScope.launch(Dispatchers.IO) {
                CPApplication.context.contentResolver.openOutputStream(uri)?.writer()?.run {
                    Log.d(TAG, "saveFileAs: saved content after entering coroutine: $content")
                    write(content)
                    flush()
                    close()
                }
            }
            Log.d(TAG, "saveFileAs: Done.")
            Toast.makeText(CPApplication.context, "Successfully saved.", Toast.LENGTH_SHORT).show()
        }
    }

    fun stashFile() {
        if (MMKVUtils.decodeBoolean(PreferenceKeys.AUTOMATED_BACKUP)) {
            val basePath = CPApplication.context.getExternalFilesDir(null)?.absolutePath
            val fileName = if (_uiState.value.title == "") "new.md" else _uiState.value.title + ".md"
            val filePath = basePath + fileName
            val mdFile = File(filePath).apply {
                if (exists()) delete()
            }
            writeContent(mdFile)
        }
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

    fun getMarkwon() = markdownAgent.markwon

}

private const val TAG = "WriteViewModel"