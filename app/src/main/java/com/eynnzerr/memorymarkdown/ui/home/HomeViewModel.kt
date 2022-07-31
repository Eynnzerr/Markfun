package com.eynnzerr.memorymarkdown.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eynnzerr.memorymarkdown.data.MMKVUtils
import com.eynnzerr.memorymarkdown.data.PreferenceKeys
import com.eynnzerr.memorymarkdown.data.database.MarkdownData
import com.eynnzerr.memorymarkdown.data.database.MarkdownRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
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
    private val repository: MarkdownRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(
        HomeUiState(
            homeType = HomeType.CREATED,
            homeList = listOf()
        )
    )
    val uiState = _uiState as StateFlow<HomeUiState>

    init {
        viewModelScope.launch {
            repository.getAllMdFlow().collect { created ->
                _uiState.update { it.copy(homeList = created) }
            }
        }
    }

    fun switchType(type: HomeType) {
        if (_uiState.value.homeType != type) {
            when (type) {
                HomeType.CREATED -> {
                    viewModelScope.launch {
                        repository.getAllMdFlow().collect { created ->
                            _uiState.update { HomeUiState(HomeType.CREATED, created) }
                        }
                    }
                }
                HomeType.VIEWED -> {
                    // TODO Fetch viewed data
                    _uiState.update { it.copy(homeType = HomeType.VIEWED) }
                }
                HomeType.STARRED -> {
                    // TODO Fetch starred data
                    _uiState.update { it.copy(homeType = HomeType.STARRED) }
                }
                HomeType.ARCHIVED -> {
                    // TODO Fetch archived data
                    _uiState.update { it.copy(homeType = HomeType.ARCHIVED) }
                }
            }
        }
    }
}