package com.eynnzerr.memorymarkdown.ui.search

import androidx.lifecycle.ViewModel
import com.eynnzerr.memorymarkdown.data.database.MarkdownRepository
import com.eynnzerr.memorymarkdown.ui.write.markdown.MarkdownAgent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MarkdownRepository,
    private val markdownAgent: MarkdownAgent
): ViewModel() {

}