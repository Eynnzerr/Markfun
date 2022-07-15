package com.eynnzerr.memorymarkdown.ui.read

import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eynnzerr.memorymarkdown.base.CPApplication
import com.eynnzerr.memorymarkdown.data.MMKVUtils
import com.eynnzerr.memorymarkdown.ui.write.Markdown
import com.eynnzerr.memorymarkdown.ui.write.markdown.MarkdownAgent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadViewModel @Inject constructor(
    private val markdownAgent: MarkdownAgent
): ViewModel() {

    private val _uiState = MutableStateFlow(Markdown())
    val uiState: StateFlow<Markdown> = _uiState

    fun getMarkwon() = markdownAgent.markwon

    // 从Uri读取待阅读md文件
    fun loadMarkdown(uri: Uri?) {
        uri?.let {
            val title = DocumentFile.fromSingleUri(CPApplication.context, it)?.name?:""
            viewModelScope.launch(Dispatchers.IO) {
                val reader = CPApplication.context.contentResolver.openInputStream(it)?.reader()
                val text = reader?.readText()?:""
                _uiState.update { Markdown(title, text) } // If uri is invalid, then there will be no content displayed but empty screen.
            }
        }
    }
}

private const val TAG = "ReadViewModel"