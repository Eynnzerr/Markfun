package com.eynnzerr.memorymarkdown.ui.setting

import androidx.lifecycle.ViewModel
import com.eynnzerr.memorymarkdown.data.MMKVUtils
import com.eynnzerr.memorymarkdown.data.PreferenceKeys
import com.eynnzerr.memorymarkdown.ui.write.markdown.MarkdownAgent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SettingUiState(
    val isAutomaticallySaveEnabled: Boolean = true,
    val isAbbreviationEnabled: Boolean = true,
    val initColorIndex: Int = 0
)

@HiltViewModel
class SettingViewModel @Inject constructor(
    markdownAgent: MarkdownAgent
): ViewModel() {
    val markwon = markdownAgent.markwon

    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState: StateFlow<SettingUiState> = _uiState

    init {
        _uiState.update {
            it.copy(
                isAutomaticallySaveEnabled = MMKVUtils.decodeBoolean(PreferenceKeys.AUTOMATED_BACKUP),
                isAbbreviationEnabled = MMKVUtils.decodeBoolean(PreferenceKeys.ABBREVIATION),
                initColorIndex = MMKVUtils.decodeInt(PreferenceKeys.APP_THEME_INDEX, 0)
            )
        }
    }

    fun updateBackupPreference(selection: Boolean) {
        MMKVUtils.encodeBoolean(PreferenceKeys.AUTOMATED_BACKUP, selection)
        _uiState.update {
            it.copy(
                isAutomaticallySaveEnabled = selection
            )
        }
    }

    fun updateAbbreviationPreference(selection: Boolean) {
        MMKVUtils.encodeBoolean(PreferenceKeys.ABBREVIATION, selection)
        _uiState.update {
            it.copy(
                isAbbreviationEnabled = selection
            )
        }
    }

    fun updateAppTheme(color: Int, index: Int) = MMKVUtils.updateAppTheme(color, index)

}