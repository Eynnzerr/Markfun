package com.eynnzerr.memorymarkdown.utils

import android.net.Uri

object UriUtils {
    /*
    Fuck SAF
     */
    var uri: Uri? = null
    var isUriValid = false

    fun prepareUri(nextUri: Uri) {
        uri = nextUri
        isUriValid = true
    }

    fun clearUri() {
        if (isUriValid) {
            uri = null
            isUriValid = false
        }
    }
}