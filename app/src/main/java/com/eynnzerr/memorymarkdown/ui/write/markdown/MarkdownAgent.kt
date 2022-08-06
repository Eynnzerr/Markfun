package com.eynnzerr.memorymarkdown.ui.write.markdown

import android.widget.TextView
import com.eynnzerr.memorymarkdown.base.CPApplication
import com.eynnzerr.memorymarkdown.ui.write.markdown.edithandler.CodeInlineHandler
import com.eynnzerr.memorymarkdown.ui.write.markdown.edithandler.HeaderHandler
import com.eynnzerr.memorymarkdown.ui.write.markdown.edithandler.StrikethroughHandler
import io.noties.markwon.Markwon
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.handler.EmphasisEditHandler
import io.noties.markwon.editor.handler.StrongEmphasisEditHandler
import io.noties.markwon.ext.latex.JLatexMathPlugin
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin
import org.qosp.notes.ui.editor.markdown.QuoteHandler
import org.qosp.notes.ui.editor.markdown.CodeBlockHandler
import javax.inject.Inject


class MarkdownAgent @Inject constructor() {
    val markwon by lazy {
        Markwon.builder(CPApplication.context)
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(HtmlPlugin.create())
            .usePlugin(GlideImagesPlugin.create(CPApplication.context))
            .usePlugin(TaskListPlugin.create(CPApplication.context))
            .usePlugin(MarkwonInlineParserPlugin.create())
            .usePlugin(JLatexMathPlugin.create(80f) {
                    builder -> builder.inlinesEnabled(true)
            })
            .build()
    }

    val editor by lazy {
        MarkwonEditor.builder(markwon)
            .useEditHandler(EmphasisEditHandler())
            .useEditHandler(StrongEmphasisEditHandler())
            .useEditHandler(CodeInlineHandler())
            .useEditHandler(CodeBlockHandler())
            .useEditHandler(QuoteHandler())
            .useEditHandler(StrikethroughHandler())
            .useEditHandler(HeaderHandler())
            .build()
    }

    private val defaultBuilder by lazy {
        Markwon.builder(CPApplication.context)
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(HtmlPlugin.create())
            .usePlugin(GlideImagesPlugin.create(CPApplication.context))
            .usePlugin(TaskListPlugin.create(CPApplication.context))

    }

//    fun enableLatexMarkwon(textView: TextView): Markwon {
//        return defaultBuilder
//            .usePlugin(MarkwonInlineParserPlugin.create())
//            .usePlugin(JLatexMathPlugin.create(textView.textSize) {
//                    builder -> builder.inlinesEnabled(true)
//            })
//            .build()
//    }
}