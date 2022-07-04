package com.eynnzerr.memorymarkdown.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.eynnzerr.memorymarkdown.ui.write.Markdown

@Entity(tableName = "markdown")
data class MarkdownData(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var title: String = "",
    var content: String = ""
) {
    constructor(
        title: String,
        content: String
    ): this() {
        this.title = title
        this.content = content
    }
}

fun Markdown.toData() = MarkdownData(title = title, content = content)
