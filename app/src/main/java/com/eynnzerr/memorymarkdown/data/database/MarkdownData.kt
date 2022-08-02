package com.eynnzerr.memorymarkdown.data.database

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

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
    var isStarred: Int = NOT_STARRED
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
        const val STATUS_INTERNAL = 0
        const val STATUS_EXTERNAL = 1
        const val STATUS_ARCHIVED = 2
        const val NOT_STARRED = 0
        const val IS_STARRED = 1
    }

    override fun toString(): String {
        return "markdownData(id=$id, title=$title, status=$status, isStarred=$isStarred)"
    }
}
