package com.eynnzerr.memorymarkdown.ui.home

import androidx.lifecycle.ViewModel
import com.eynnzerr.memorymarkdown.data.MMKVUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mvUtils: MMKVUtils
): ViewModel() {
    fun loadMarkdown(title: String, content: String) {
        mvUtils.encodeString("craft_title", title)
        mvUtils.encodeString("craft_contents", content)
    }
}