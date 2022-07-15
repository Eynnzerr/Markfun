package com.eynnzerr.memorymarkdown.data.database

import com.eynnzerr.memorymarkdown.base.CPApplication
import javax.inject.Inject

class MarkdownRepository @Inject constructor() {
    private val dao = MarkdownDatabase.getInstance(CPApplication.context).getDao()

    fun getAllMdFlow() = dao.getLocalMarkdown()

    fun getContentByTitle(title: String) = dao.getContentByTitle(title)

    suspend fun insertMarkdown(vararg markdown: MarkdownData) = dao.insertMarkdown(*markdown)

    suspend fun deleteMarkdown(vararg markdown: MarkdownData) = dao.deleteMarkdown(*markdown)
}