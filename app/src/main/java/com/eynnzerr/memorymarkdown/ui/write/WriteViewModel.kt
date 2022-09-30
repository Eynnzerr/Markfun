package com.eynnzerr.memorymarkdown.ui.write

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eynnzerr.memorymarkdown.base.CPApplication
import com.eynnzerr.memorymarkdown.data.MMKVUtils
import com.eynnzerr.memorymarkdown.data.PreferenceKeys
import com.eynnzerr.memorymarkdown.data.database.MarkDownContent
import com.eynnzerr.memorymarkdown.data.database.MarkDownUri
import com.eynnzerr.memorymarkdown.data.database.MarkdownData
import com.eynnzerr.memorymarkdown.data.database.MarkdownRepository
import com.eynnzerr.memorymarkdown.ui.mainActivityReady
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

/**
 * WriteViewModel is responsible for:
 * 1. managing data in [WriteScreen], i.e. title and content of current file
 * 2. manipulating IO for room, MMKV and local files
 */
@HiltViewModel
class WriteViewModel @Inject constructor(
    private val markdownAgent: MarkdownAgent,
    private val repository: MarkdownRepository
): ViewModel() {

    // Implies the id of file on editing.
    // -2 means viewModel is not validly initialized, -1 means importing external file, others mean file in database.
    var targetId: Int = -2

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
    @SuppressLint("Recycle")
    fun loadMarkdown(uri: Uri?) {
        if (targetId == -2) {
            targetId = -1
            uri?.let {
                val title = DocumentFile.fromSingleUri(CPApplication.context, it)?.name?:""
                viewModelScope.launch(Dispatchers.IO) {
                    val reader = CPApplication.context.contentResolver.openInputStream(it)?.reader()
                    val text = reader?.readText()?:""

                    _uiState.update { WriteUiState(title, text, true) }

                    // Main activity not ready indicates app is cold launched.
                    if (!mainActivityReady) mainActivityReady = true
                }
            }
        }
    }

    // load content from database, set readOnly mode
    fun loadMarkdown(id: Int) {
        if (targetId == -2) {
            targetId = id
            viewModelScope.launch(Dispatchers.IO) {
                val data = repository.getDataById(id).first()
                _uiState.update { WriteUiState(data.title, data.content, true) }
            }
        }
    }

    // load content from MMKV craft, set write mode
    fun loadCraft() {
        if (targetId == -2) {
            targetId = -1
            _uiState.update {
                WriteUiState(
                    MMKVUtils.decodeString(PreferenceKeys.CRAFT_TITLE),
                    MMKVUtils.decodeString(PreferenceKeys.CRAFT_CONTENTS),
                    false
                )
            }
        }
    }

    private suspend fun importMarkdown(title: String, text: String, uri: Uri) {
        // import external file for further operation, since WRITE permission is not requested
        repository.insertMarkdown(
            MarkdownData(
                title = title,
                content = text,
                uri = uri,
                status = MarkdownData.STATUS_EXTERNAL,
                isStarred = MarkdownData.NOT_STARRED
            )
        )
    }

    fun saveMarkdown() {
        // Must use main dispatcher to ensure that homeScreen collect data only after new data has been inserted.
        viewModelScope.launch {
            if (targetId != -1) {
                // If already from database, just modify the data content via primary key in database.
                repository.updateContentById(MarkDownContent(
                    id = targetId,
                    title = _uiState.value.title,
                    content = _uiState.value.content,
                    modifiedDate = MarkdownData.currentTime()
                ))
            }
            else {
                // If from craft or uri(first enter), save the new markdown file into database.
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

    @SuppressLint("Recycle")
    fun saveFileAs(uri: Uri) {
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

    // If automated backup is allowed by user, export markdown file to app external file directory.
    fun stashFile() {
        if (MMKVUtils.decodeBoolean(PreferenceKeys.AUTOMATED_BACKUP)) {
            val fileName = if (_uiState.value.title == "") "new.md" else _uiState.value.title + ".md"
            val mdDirectory = File(CPApplication.context.getExternalFilesDir(null), "my_files")
            if (!mdDirectory.exists()) mdDirectory.mkdir()
            val mdFile = File(mdDirectory, fileName).apply {
                if (exists()) delete()
            }
            // update uri via targetId
            viewModelScope.launch(Dispatchers.IO) {
                // For external files this will overwrite their original uri
                val contentUri = FileProvider.getUriForFile(CPApplication.context, "com.eynnzerr.memorymarkdown.file_provider", mdFile)
                repository.updateUriById(MarkDownUri(id = targetId, uri = contentUri))
                writeContent(mdFile)
            }
        }
    }

    // Create temporal file in cache directory to share the file with external apps
    fun cacheFile(): Uri? {
        // Create file
        val fileName = if (_uiState.value.title == "") "new.md" else _uiState.value.title + ".md"
        val mdDirectory = File(CPApplication.context.cacheDir, "cache_files")
        if (!mdDirectory.exists()) mdDirectory.mkdir()
        val cachedFile = File(mdDirectory, fileName).apply {
            if (exists()) delete()
        }

        // Write file content
        writeContent(cachedFile)

        return FileProvider.getUriForFile(
            CPApplication.context,
            "com.eynnzerr.memorymarkdown.file_provider",
            cachedFile
        )
    }

    // IO method which should only run in coroutines
    private fun writeContent(file: File) {
        Log.d(TAG, "writeContent: Save ${file.name} to ${file.path}")
        with(file.writer()) {
            write(_uiState.value.content)
            flush()
            close()
        }
        Toast.makeText(CPApplication.context, "Stash ${file.name} successfully", Toast.LENGTH_SHORT).show()
    }

    suspend fun getUri(): Uri? = repository.getDataById(targetId).first().uri

    fun getEditor() = markdownAgent.editor

    fun getMarkwon() = markdownAgent.markwon

}

private const val TAG = "WriteViewModel"