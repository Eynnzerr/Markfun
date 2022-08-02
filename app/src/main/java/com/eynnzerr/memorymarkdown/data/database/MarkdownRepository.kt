package com.eynnzerr.memorymarkdown.data.database

import android.util.Log
import com.eynnzerr.memorymarkdown.base.CPApplication
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MarkdownRepository @Inject constructor() {
    private val dao = MarkdownDatabase.getInstance(CPApplication.context).getDao()

    // retrieve different types of data
    fun getAllDataFlow() = dao.getAllMarkdown()
    fun getLocalCreatedFlow() = dao.getLocalMarkdown()
    fun getExternalFlow() = dao.getExternalMarkdown()
    fun getArchivedFlow() = dao.getArchivedMarkdown()
    fun getStarredFlow() = dao.getStarredMarkdown()
    fun getDataById(id: Int) = dao.getDataById(id)

    // CUD
    suspend fun updateMarkdown(vararg markdown: MarkdownData) = dao.updateMarkdown(*markdown)
    suspend fun insertMarkdown(vararg markdown: MarkdownData) = dao.insertMarkdown(*markdown)
    suspend fun deleteMarkdown(vararg markdown: MarkdownData) {
        val res = dao.deleteMarkdown(*markdown)
        Log.d(TAG, "deleteMarkdown: delete markdown result: $res")
    }

    suspend fun updateContentById(markDownContent: MarkDownContent) = dao.updateContentById(markDownContent)
}

private const val TAG = "MarkdownRepository"