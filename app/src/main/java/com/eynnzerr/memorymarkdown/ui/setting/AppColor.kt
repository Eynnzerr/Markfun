package com.eynnzerr.memorymarkdown.ui.setting

import com.eynnzerr.memorymarkdown.ui.theme.DEFAULT_COLOR

class AppColor(
    val colorArgb: Int = DEFAULT_COLOR,
    val name: String = "default"
)

val defaultColors = listOf(
    // Material
    AppColor(0XFFE6E6FA.toInt(), "lavender"),
    AppColor(0XFFFFE4E1.toInt(), "mistyRose"),
    AppColor(0XFFFFF0F5.toInt(), "lavenderBlush"),
    AppColor(0XFFF0F8FF.toInt(), "aliceBlue"),
    AppColor(0XFFF0FFFF.toInt(), "azure"),
    AppColor(0XFFF5FFFA.toInt(), "mintCream"),
    AppColor(0XFFF0FFF0.toInt(), "honeyDew"),
    AppColor(0XFFFFF5EE.toInt(), "seaShell"),
    AppColor(0XFFFFFACD.toInt(), "lemonChiffon"),
    AppColor(0XFFFFFFF0.toInt(), "ivory"),
    AppColor(0XFFFFF8DC.toInt(), "cornSilk"),
    // Red
    AppColor(0xFFFF0000.toInt(), "red"),
    AppColor(0XFFFF4500.toInt(), "orangeRed"),
    AppColor(0XFFFF6347.toInt(), "tomato"),
    AppColor(0XFFFF7256.toInt(), "coral"),
    // Orange
    AppColor(0XFFFFA500.toInt(), "orange"),
    AppColor(0XFFFF7F00.toInt(), "darkOrange"),
    AppColor(0XFFFF7F24.toInt(), "chocolate"),
    AppColor(0XFFFF8247.toInt(), "sienna"),
    // Yellow
    AppColor(0XFFFFFF00.toInt(), "yellow"),
    AppColor(0XFFFFD700.toInt(), "gold"),
    AppColor(0XFFFFFFE0.toInt(), "lightYellow"),
    AppColor(0XFFFFF68F.toInt(), "khaki"),
    // Green
    AppColor(0XFF00FF00.toInt(), "green"),
    AppColor(0XFF228B22.toInt(), "forest"),
    AppColor(0XFF98FB98.toInt(), "paleGreen"),
    AppColor(0XFF7FFFD4.toInt(), "aquamarine"),
    // Cyan
    AppColor(0XFF00FFFF.toInt(), "cyan"),
    AppColor(0XFF40E0D0.toInt(), "turquoise"),
    AppColor(0XFF5F9EA0.toInt(), "cadetBlue"),
    AppColor(0XFFE0FFFF.toInt(), "lightCyan"),
    // Blue
    AppColor(0XFF0000FF.toInt(), "blue"),
    AppColor(0XFF1E90FF.toInt(), "dodgerBlue"),
    AppColor(0XFF87CEFA.toInt(), "lightBlue"),
    AppColor(0XFF00BFFF.toInt(), "deepSky"),
    // Purple
    AppColor(0XFFA020F0.toInt(), "purple"),
    AppColor(0XFFDA70D6.toInt(), "orchid"),
    AppColor(0XFFEE82EE.toInt(), "violet"),
    AppColor(0XFFDDA0DD.toInt(), "plum"),
)