package com.eynnzerr.memorymarkdown.data

import com.eynnzerr.memorymarkdown.ui.theme.DEFAULT_COLOR
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

object PreferenceKeys {
    const val CRAFT_TITLE = "craft_title"
    const val CRAFT_CONTENTS = "craft_contents"
    const val AUTOMATED_BACKUP = "preference_backup"
    const val APP_THEME_COLOR = "preference_app_theme"
    const val APP_THEME_INDEX = "app_theme_index"
    const val MD_THEME_COLOR = "preference_md_theme"
}

data class ThemeState(
    val appTheme: Int = DEFAULT_COLOR,
    val markdownTheme: Int = 0
)

object MMKVUtils {

    private val mv = MMKV.defaultMMKV()

    private val _themeState = MutableStateFlow(ThemeState(appTheme = mv.decodeInt(PreferenceKeys.APP_THEME_COLOR, DEFAULT_COLOR)))
    val themeState = _themeState.asStateFlow()

    // int
    fun encodeInt(key: String, value: Int) = mv.encode(key, value)
    fun decodeInt(key: String, value: Int) = mv.decodeInt(key)

    fun updateAppTheme(color: Int, index: Int) {
        encodeInt(PreferenceKeys.APP_THEME_COLOR, color)
        encodeInt(PreferenceKeys.APP_THEME_INDEX, index)
        _themeState.update { it.copy(appTheme = color) }
    }

    // boolean
    fun encodeBoolean(key: String, value: Boolean) = mv.encode(key, value)
    fun decodeBoolean(key: String) = mv.decodeBool(key)

    // value
    fun encodeString(key: String, value: String) = mv.encode(key, value)
    fun decodeString(key: String) = mv.decodeString(key) ?: ""

    fun removeCraft() = mv.removeValuesForKeys(arrayOf(
        PreferenceKeys.CRAFT_TITLE, PreferenceKeys.CRAFT_CONTENTS))

    fun containsKey(key: String) = mv.containsKey(key)
}