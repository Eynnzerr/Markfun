package com.eynnzerr.memorymarkdown.ui.home

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

data class HomeUiState(
    val homeType: HomeType,
    val homeList: List<MarkdownData>
)

enum class HomeType {
    CREATED,
    VIEWED,
    STARRED,
    ARCHIVED
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MarkdownRepository,
    markdownAgent: MarkdownAgent
): ViewModel() {
    private val _uiState = MutableStateFlow(
        HomeUiState(
            homeType = HomeType.CREATED,
            homeList = listOf()
        )
    )
    val uiState = _uiState.asStateFlow()

    val markwon = markdownAgent.markwon

    private var homeList = emptyList<MarkdownData>()
    lateinit var tempData: MarkdownData

//    init {
//        // register collector
//        registerCollector()
//    }

    fun registerCollector() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllDataFlow().collect { newList ->
                homeList = newList
                when (_uiState.value.homeType) {
                    HomeType.CREATED -> _uiState.update { it.copy(homeList = homeList.filter { data -> data.status == MarkdownData.STATUS_INTERNAL }) }
                    HomeType.VIEWED -> _uiState.update { it.copy(homeList = homeList.filter { data -> data.status == MarkdownData.STATUS_EXTERNAL }) }
                    HomeType.STARRED -> _uiState.update { it.copy(homeList = homeList.filter { data -> (data.isStarred == MarkdownData.IS_STARRED) and (data.status != MarkdownData.STATUS_ARCHIVED) }) }
                    HomeType.ARCHIVED -> _uiState.update { it.copy(homeList = homeList.filter { data -> data.status == MarkdownData.STATUS_ARCHIVED }) }
                }
            }
        }
    }

    fun switchType(type: HomeType) {
        if (_uiState.value.homeType != type) {
            when (type) {
                HomeType.CREATED -> {
                    _uiState.update { HomeUiState(HomeType.CREATED, homeList.filter { data -> data.status == MarkdownData.STATUS_INTERNAL }) }
                }
                HomeType.VIEWED -> {
                    _uiState.update { HomeUiState(HomeType.VIEWED, homeList.filter { data -> data.status == MarkdownData.STATUS_EXTERNAL }) }
                }
                HomeType.STARRED -> {
                    _uiState.update { HomeUiState(HomeType.STARRED, homeList.filter { data -> (data.isStarred == MarkdownData.IS_STARRED) and (data.status != MarkdownData.STATUS_ARCHIVED) }) }
                }
                HomeType.ARCHIVED -> {
                    _uiState.update { HomeUiState(HomeType.ARCHIVED, homeList.filter { data -> data.status == MarkdownData.STATUS_ARCHIVED }) }
                }
            }
        }
    }

    fun updateMarkdown(markdownData: MarkdownData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMarkdown(markdownData)
        }
    }

    fun deleteMarkdown(markdownData: MarkdownData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMarkdown(markdownData)
        }
    }

}

private const val TAG = "HomeViewModel"