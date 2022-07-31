package com.eynnzerr.memorymarkdown.utils

import android.net.Uri

object UriUtils {
    /*
    忍不住想要吐槽分区存储和SAF，逼迫我只能采取最后一个考虑的坏办法
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