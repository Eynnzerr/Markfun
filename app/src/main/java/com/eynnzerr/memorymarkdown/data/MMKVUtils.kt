package com.eynnzerr.memorymarkdown.data

import com.tencent.mmkv.MMKV
import javax.inject.Inject

class MMKVUtils @Inject constructor() {

    private val mv: MMKV by lazy {
        MMKV.defaultMMKV()
    }

    fun encodeString(key: String, value: String) = mv.encode(key, value)

    fun decodeString(key: String) = mv.decodeString(key) ?: ""

    fun removeCraft() = mv.removeValuesForKeys(arrayOf("craft_title", "craft_contents"))

    fun containsKey(key: String) = mv.containsKey(key)
}