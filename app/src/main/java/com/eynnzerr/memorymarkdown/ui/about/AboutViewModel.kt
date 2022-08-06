package com.eynnzerr.memorymarkdown.ui.about

import androidx.lifecycle.ViewModel
import com.eynnzerr.memorymarkdown.ui.write.markdown.MarkdownAgent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val markdownAgent: MarkdownAgent
): ViewModel() {
    val markwon = markdownAgent.markwon

    // for feature extensions in future.
}