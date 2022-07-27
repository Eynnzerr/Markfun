package com.eynnzerr.memorymarkdown.ui.setting

import androidx.lifecycle.ViewModel
import com.eynnzerr.memorymarkdown.data.MMKVUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class SettingUiState(
    val isAutomaticallySaveEnabled: Boolean = true
)

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val mvUtils: MMKVUtils
): ViewModel() {

}