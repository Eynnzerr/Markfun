package com.eynnzerr.memorymarkdown.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MarkdownDao {

    @Query("SELECT * FROM markdown")
    fun getLocalMarkdown(): Flow<List<MarkdownData>>

    @Query("SELECT content FROM markdown WHERE title = :title")
    fun getContentByTitle(title: String): Flow<String>

    @Insert
    suspend fun insertMarkdown(vararg markdown: MarkdownData)

    @Delete
    suspend fun deleteMarkdown(vararg markdown: MarkdownData)
}
