package com.eynnzerr.memorymarkdown.data

import com.eynnzerr.memorymarkdown.ui.theme.DEFAULT_COLOR
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object PreferenceKeys {
    const val CRAFT_TITLE = "craft_title"
    const val CRAFT_CONTENTS = "craft_contents"
    const val AUTOMATED_BACKUP = "preference_backup"
    const val ABBREVIATION = "preference_abbreviation"
    const val APP_THEME_COLOR = "app_theme"
    const val APP_THEME_INDEX = "app_theme_index"
    const val MD_THEME_COLOR = "md_theme"
    const val LIST_DISPLAY = "list_display"
    const val LIST_ORDER = "list_order"
}

object ListDisplayMode {
    const val IN_LIST = 0
    const val IN_GRID = 1
}

object ListOrder {
    const val TITLE_ASCEND = 0
    const val TITLE_DESCEND = 1
    const val CREATED_DATE_ASCEND = 2
    const val CREATED_DATE_DESCEND = 3
    const val MODIFIED_DATE_ASCEND = 4
    const val MODIFIED_DATE_DESCEND = 5
}

// TODO markdown syntax that provides diy shortcut
enum class ShortCuts {
    HEADER,
    BOLD,
    ITALIC,
    STRIKETHROUGH,
    CODEINLINE,
    CODEBLOCK,
    QUOTE,
    DIVIDER,
    HYPERLINK,
    TASKLIST,
    UNORDEREDLIST,
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
    fun decodeInt(key: String, defaultValue: Int) = mv.decodeInt(key, defaultValue)

    fun updateAppTheme(color: Int, index: Int) {
        encodeInt(PreferenceKeys.APP_THEME_COLOR, color)
        encodeInt(PreferenceKeys.APP_THEME_INDEX, index)
        _themeState.update { it.copy(appTheme = color) }
    }

    // boolean
    fun encodeBoolean(key: String, value: Boolean) = mv.encode(key, value)
    fun decodeBoolean(key: String) = mv.decodeBool(key, false)

    // String
    fun encodeString(key: String, value: String) = mv.encode(key, value)
    fun decodeString(key: String) = mv.decodeString(key) ?: ""

    fun removeCraft() = mv.removeValuesForKeys(arrayOf(
        PreferenceKeys.CRAFT_TITLE, PreferenceKeys.CRAFT_CONTENTS))

    fun containsKey(key: String) = mv.containsKey(key)
}