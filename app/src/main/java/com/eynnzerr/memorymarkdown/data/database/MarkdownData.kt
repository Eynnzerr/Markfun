package com.eynnzerr.memorymarkdown.data.database

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "markdown")
@TypeConverters(UriConverter::class)
data class MarkdownData(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var title: String = "",
    var content: String = "",
    var uri: Uri? = null,
    @ColumnInfo(defaultValue = "0")
    var status: Int = STATUS_INTERNAL,
    @ColumnInfo(defaultValue = "0")
    var isStarred: Int = NOT_STARRED,
    @ColumnInfo(defaultValue = "empty")
    var createdDate: String = LocalDateTime.now().format(formatter),
    @ColumnInfo(defaultValue = "empty")
    var modifiedDate: String = LocalDateTime.now().format(formatter)
) {
    constructor(
        title: String,
        content: String,
        uri: Uri?,
        status: Int,
        isStarred: Int
    ): this() {
        this.title = title
        this.content = content
        this.uri = uri
        this.status = status
        this.isStarred = isStarred
    }

    companion object {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")!!
        const val STATUS_INTERNAL = 0
        const val STATUS_EXTERNAL = 1
        const val STATUS_ARCHIVED = 2
        const val NOT_STARRED = 0
        const val IS_STARRED = 1

        fun currentTime(): String = LocalDateTime.now().format(formatter)
    }

    override fun toString(): String {
        return "markdownData(id=$id, title=$title, status=$status, isStarred=$isStarred)"
    }
}

// Only used for updating file contents in database
data class MarkDownContent(
    val id: Int,
    val title: String,
    val content: String,
    val modifiedDate: String
)

// Only used for updating file uri in database
@TypeConverters(UriConverter::class)
data class MarkDownUri(
    val id: Int,
    val uri: Uri? = null
)