package com.eynnzerr.memorymarkdown.ui.write.markdown

import com.eynnzerr.memorymarkdown.base.CPApplication
import com.eynnzerr.memorymarkdown.ui.write.markdown.edithandler.HeaderHandler
import io.noties.markwon.Markwon
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.handler.EmphasisEditHandler
import io.noties.markwon.editor.handler.StrongEmphasisEditHandler
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import javax.inject.Inject


class MarkdownAgent @Inject constructor() {
    val markwon by lazy {
        Markwon.builder(CPApplication.context)
            .usePlugin(StrikethroughPlugin.create())
            .build()
    }

    val editor by lazy {
        MarkwonEditor.builder(markwon)
            .useEditHandler(EmphasisEditHandler())
            .useEditHandler(StrongEmphasisEditHandler())
//        .useEditHandler(CodeHandler())
//        .useEditHandler(CodeBlockHandler())
//        .useEditHandler(BlockQuoteHandler())
//        .useEditHandler(StrikethroughHandler())
            .useEditHandler(HeaderHandler())
            .build()
    }
}