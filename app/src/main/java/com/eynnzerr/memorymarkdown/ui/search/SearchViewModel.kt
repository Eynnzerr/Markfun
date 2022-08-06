package com.eynnzerr.memorymarkdown.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eynnzerr.memorymarkdown.data.database.MarkdownData
import com.eynnzerr.memorymarkdown.data.database.MarkdownRepository
import com.eynnzerr.memorymarkdown.ui.write.markdown.MarkdownAgent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MarkdownRepository,
    private val markdownAgent: MarkdownAgent
): ViewModel() {

    private val _keywordState = MutableStateFlow("")
    val keywordState = _keywordState.asStateFlow()

    private  val _resultState = MutableStateFlow(emptyList<MarkdownData>())
    val resultState = _resultState.asStateFlow()

    init {
        // collect values emitted by flow of keyword, and update the value of result list
        viewModelScope.launch(Dispatchers.IO) {
            _keywordState.collect { keyword ->
//                repository.searchDataByKeyword(keyword).collect { result ->
//                    _resultState.update { result }
//                }
                val result = repository.searchDataByKeyword(keyword)
                _resultState.update { result }
            }
        }
    }

    val markwon = markdownAgent.markwon

    fun updateKeyWord(s: String) {
        _keywordState.update { s }
    }
}