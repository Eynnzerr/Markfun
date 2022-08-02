package com.eynnzerr.memorymarkdown.data.database

import android.net.Uri
import androidx.room.TypeConverter

class UriConverter {
    @TypeConverter
    fun uriToStr(uri: Uri?) = uri.toString()

    @TypeConverter
    fun strToUri(uriString: String?): Uri? {
        return if (uriString == null) null else Uri.parse(uriString)
    }
}