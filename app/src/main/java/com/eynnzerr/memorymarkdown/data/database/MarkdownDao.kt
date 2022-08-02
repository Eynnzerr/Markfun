package com.eynnzerr.memorymarkdown.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MarkdownDao {

    @Query("SELECT * FROM markdown")
    fun getAllMarkdown(): Flow<List<MarkdownData>>

    @Query("SELECT * FROM markdown WHERE status = 0")
    fun getLocalMarkdown(): Flow<List<MarkdownData>>

    @Query("SELECT * FROM markdown WHERE status = 1")
    fun getExternalMarkdown(): Flow<List<MarkdownData>>

    @Query("SELECT * FROM markdown WHERE status = 2")
    fun getArchivedMarkdown(): Flow<List<MarkdownData>>

    @Query("SELECT * FROM markdown WHERE isStarred = 1")
    fun getStarredMarkdown(): Flow<List<MarkdownData>>

    @Query("SELECT * FROM markdown WHERE id = :id")
    fun getDataById(id: Int): Flow<MarkdownData>

    @Update
    suspend fun updateMarkdown(vararg markdown: MarkdownData)

    @Update(entity = MarkdownData::class)
    suspend fun updateContentById(markDownContent: MarkDownContent)

    @Insert
    suspend fun insertMarkdown(vararg markdown: MarkdownData)

    @Delete
    suspend fun deleteMarkdown(vararg markdown: MarkdownData)
}
