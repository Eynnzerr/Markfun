package com.eynnzerr.memorymarkdown.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eynnzerr.memorymarkdown.data.ListDisplayMode
import com.eynnzerr.memorymarkdown.data.ListOrder
import com.eynnzerr.memorymarkdown.data.MMKVUtils
import com.eynnzerr.memorymarkdown.data.PreferenceKeys
import com.eynnzerr.memorymarkdown.data.database.MarkdownData
import com.eynnzerr.memorymarkdown.data.database.MarkdownRepository
import com.eynnzerr.memorymarkdown.ui.mainActivityReady
import com.eynnzerr.memorymarkdown.ui.write.markdown.MarkdownAgent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.absoluteValue

data class HomeUiState(
    val homeType: HomeType,
    val homeList: List<MarkdownData>,
    val listDisplay: Int,
    val listOrder: Int
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
    private val markdownAgent: MarkdownAgent
): ViewModel() {
    private val _uiState = MutableStateFlow(
        HomeUiState(
            homeType = HomeType.CREATED,
            homeList = listOf(),
            listDisplay = MMKVUtils.decodeInt(PreferenceKeys.LIST_DISPLAY, ListDisplayMode.IN_LIST),
            listOrder = MMKVUtils.decodeInt(PreferenceKeys.LIST_ORDER, ListOrder.TITLE_ASCEND)
        )
    )
    val uiState = _uiState.asStateFlow()

    private var homeList = emptyList<MarkdownData>()
    lateinit var tempData: MarkdownData

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
                Log.d(TAG, "registerCollector: data is ready.")
                mainActivityReady = true
            }
        }
    }

    fun getMarkwon() = if (MMKVUtils.decodeBoolean(PreferenceKeys.ABBREVIATION)) markdownAgent.markwon else null

    fun switchType(type: HomeType) {
        if (_uiState.value.homeType != type) {
            when (type) {
                HomeType.CREATED -> {
                    _uiState.update { it.copy(
                        homeType = HomeType.CREATED,
                        homeList = homeList.filter { data -> data.status == MarkdownData.STATUS_INTERNAL }) }
                }
                HomeType.VIEWED -> {
                    _uiState.update { it.copy(
                        homeType = HomeType.VIEWED,
                        homeList = homeList.filter { data -> data.status == MarkdownData.STATUS_EXTERNAL }) }
                }
                HomeType.STARRED -> {
                    _uiState.update { it.copy(
                        homeType = HomeType.STARRED,
                        homeList = homeList.filter { data -> (data.isStarred == MarkdownData.IS_STARRED) and (data.status != MarkdownData.STATUS_ARCHIVED) }) }
                }
                HomeType.ARCHIVED -> {

                    _uiState.update { it.copy(
                        homeType = HomeType.ARCHIVED,
                        homeList = homeList.filter { data -> data.status == MarkdownData.STATUS_ARCHIVED }) }
                }
            }
        }
    }

    fun updateDisplayMode(mode: Int) {
        _uiState.update { it.copy(listDisplay = mode) }
        MMKVUtils.encodeInt(PreferenceKeys.LIST_DISPLAY, mode)
    }

    fun updateDisplayMode() {
        val newMode = (_uiState.value.listDisplay - 1).absoluteValue
        updateDisplayMode(newMode)
    }

    fun updateDisplayOrder(order: Int) {
        _uiState.update { it.copy(listOrder = order) }
        MMKVUtils.encodeInt(PreferenceKeys.LIST_ORDER, order)
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